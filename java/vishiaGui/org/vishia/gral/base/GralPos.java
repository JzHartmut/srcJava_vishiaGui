package org.vishia.gral.base;

import java.io.IOException;
import java.text.ParseException;

import org.vishia.bridgeC.IllegalArgumentExceptionJc;
import org.vishia.gral.ifc.GralPanel_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.Assert;
import org.vishia.util.Debugutil;
import org.vishia.util.StringPart;
import org.vishia.util.StringPartScan;


/*Test with Jbat: call Jbat with this java file with its full path:
D:/vishia/Java/srcJava_vishiaGui/org/vishia/gral/base/GralPos.java
==JZcmd==
java org.vishia.gral.base.GralPos.testScanSize();
==endJZcmd==
*/


/**This class describes a position in a gral panel. 
   * <br><br>
   * <b>Concept of positioning</b>:<br>
   * The position are never given as pixel positions. They are user-oriented positions. The calculation
   * of pixel units are done from the implementing graphic layer depending on the graphic device properties
   * and the requested size appearance. The same graphic elements can be presented in several sizes
   * on the display.
   * <br><br>
   * A normal text with a font in the standard proper read-able size is presented by 2 units of the Gral-position
   * in vertical direction (line) and approximately 1 unit per character in horizontal direction.
   * Of course the horizontal character size depends on the font properties.
   * A text can be presented in a smaller or larger font.  
   * The text height depends on the height given in the position of the text. A very small font is presented by 1 vertical gral-unit.
   * Such a text can be used as short title for text input fields (prompt) or adequate.
   * <br>
   * A button is able to proper present with 3 or 2 vertical gral units. A small check box may be presented 
   * with 1 x 1 gral unit.
   * <br><br>
   * A gral unit should be have the same distance in vertical as in horizontal direction. It depends on the 
   * graphical implementation. One gral unit may have approximately 6 to 30 pixel, 
   * depending on the requested size of appearance in comparison with the given display pixel size. 
   * Any graphic can be shown in several sizes of appearance, given with a start parameter of the application
   * (see {@link org.vishia.gral.area9.GuiCallingArgs#sSize}) respectively the parameter size of {@link GralGridProperties#GralGridProperties(char size)}.
   * <br><br>
   * <b>Fine positions</b>:<br>
   * Either the positions are given with 2 integer values as 'fundamental positions'. 
   * That are the position described above.
   * Or they can be given with a float value or a second int named 'fractional part'. From the float value
   * only the first digit after point is used, a fractional part can be given
   * with a value from 0 to 9. 
   * <br><br>
   * The fine position divides one gral position into 5 or into 6 fine positions. 
   * The odd numbers divide into 6 positions. In this kind a gral position is able to divide by 2, 3 and 6:
   * <ul>
   * <li>1: 1/6 = 0.1333
   * <li>3: 1/3 = 0.3333
   * <li>5: 1/2 = 0.5
   * <li>7: 2/3 = 0.6667
   * <li>9: 5/6 = 0.8667
   * </ul>
   * The even numbers divide into 5 positions: 
   * <ul>
   * <li>2: 1/5 = 0.2
   * <li>4: 2/5 = 0.4
   * <li>6: 3/5 = 0.6
   * <li>8: 4/5 = 0.8
   * </ul>
   * The fine positioning enables a fine positioning of widgets in respect to the fundamental positions.
   * <br><br>
   * <b>Positions as user arguments</b>:<br>
   * Positions may be given with absolute values of grid units regarded to the actual panel or in related to the 
   * last or parent position. In that cases the constant values are added to the number, see the following list.
   * The position is given in form 'from..to' for the line and column or in form 'from' and 'size'. 
   * The size may be given positive or negative. A positive size is counted from top or left to bottom or right.
   * It means that the 'from' position is top or left. But a negative size is counted from right or bottom
   * and the 'from' position is the right or bottom column and line.
   * <br><br>
   * <b>Position writing style in a gral script:</b><br>
   * The appearance of a graphic can be given with a script using {@link org.vishia.gral.cfg.GralCfgZbnf}.
   * The writing style of positions in the script regards a stinting short style to give positions,
   * because a hand written script should not cause a lot of calculations for positions by the writer.
   * It should be simple. The syntax of a position in the gral script is given in the variable {@link #syntaxZbnf}.
   * The method {@link #setPosition(CharSequence, GralPos)} uses that syntax.
   * <ul>
   * <li>@myPanel, 5..7, 8..18: This is a full given absolute position. The element should be placed from line 5 to line 7 
   *   and from column 8 to 18. The vertical position 7 is the bottom line, the column 18 is the right column exclusive. 
   *   The horizontal size is 10, the vertical size is 2.
   * <li>@myPanel, 5..7, 8..-1: This is a full given absolute position. The end column is given with a negative value.
   *   It means that the end column is related to the right border of the panel.
   * <li>@myPanel, 5+2, 8+10: This is a full given position using the size. The element should be placed from line 5 
   *   to line 7 and from column 8 to 18. The range is given as size. 
   * <li>@7-2, 18-10: The panel isn't given, so the panel of the last position, in the script in order of text,
   *   is used. The positions are exactly the same, from 5 to 7 and from 10 to 18. But because the size is given
   *   as negative value, the position value is the bottom line and the right column. A user may place elements
   *   with a common bottom line. The this form can be used.
   * <li>@7-2, 10+18++1: This is the same position too. The '++' operator after size means, that the next element
   *   is positioning right side after the current in distance of 1 unit (distance feature TODO).
   * <li>@-3,+4: The position isn't given yet. It means, the position of the last element is used. Because
   *   the size is negative, the bottom position of the last element is used. Because the last position is
   *   designated with '++' for column, the column value of position is the right value and the current element 
   *   is placed right hand from the last one. This is an example of a button right from a text. The button is
   *   some times greater (3 units) in relation to the text (2 units), but they have the same button line.
   * <li>@,+5: Here the line isn't specified. It is taken from the last position: bottom line 7
   *   and height 3. The column isn't given, it is taken form the last: Because the column position is cumulated,
   *   it is the 22 yet.
   * <li>If no position is given, the position is the next position. In this example @7-3,27+5.
   * <li>@,&2+10: The ampersand determines a relative position related to the last one. In this example
   *   the new column is 24 to 34. There is 2 units space.
   * <li>@ $2-2,$+20: The dollar determines a relative position related to the last absolute given position. 
   *   In this example it is line 7 and column 10. The new position is calculated with that values to line 9. 
   *   The column 10 is the same column related to the last given absolute.
   *   This kind of specification allows determining some positions in lines and columns, 
   *   whereby the absolute position is given only one time. (feature TODO)
   * <li>@ %50-2,%10..%90: The positions are calculated from the size of the panel in percent. (feature TODO)  
   * </ul>
   * <br><br>
   * <b>Ranges and Designation of position parameter</b>:
   * <ul>
   * <li>Positive number in range 0...about 100..200 up to 1000: Grid Unit from left or top.
   * <li>Negative number in range 0, -1...about -200..-200 up to 1000: Gral Unit from right or bottom.
   *   0 for lineEnd or columnEnd means the right or bottom.
   * <li>{@link #same} or {@link #refer} added with a number in range of -1000..1000: This given position 
   *   refers to the parent position with the given distance. same and refer is equate, the difference 
   *   is in semantic only. Use {@link #same} without distance, use {@link #refer} +/- distance.
   *   If {@link #same} or {@link #refer} is used for the line or column, and the second position is given
   *   with '{@link #size} - size' then the bottom or right value of the parent is referred.
   *  
   * <li>{@link #size} + number applied at lineEnd or columnEnd: 
   *   The size is given instead from..to. Because the size value is positive, the line and column value is left or top. 
   * <li>{@link #size} - number applied at lineEnd or columnEnd: The size is given negative.  
   *   The absolute value is the size. Because the size is negative it is measured from right to left
   *   respectively bottom to top. It means the given line and column is the right or the bottom line. 
   *   If the position is given using {@link #same} or {@link #refer}, the related end position
   *   is used. 
   * <li> {@link GralPos#next} and {@link GralPos#nextBlock}   
   * <li>as width or height or as percent value from the panel size.
   * </ul>
   * Fine positions are given always from left or top of the fundamental positions. 
   * For example a value -1.3 means, the widget is placed 1 unit from right, and then 1/3 inside this unit.
   * This is 2/3 unit from right. A value for example -0.3 is not admissible, because -0 is not defined. 
   * <br><br>
   * <b>The position values in this class</b>:<br>
   * Though the position parameters may be given as size, with bottom line etc the position values are stored
   * as absolute positions anyway. That are the elements {@link #x}, and {@link #y} with its values in
   * {@link Coordinate}.
   * The x and y is the lesser value, left and top, and the xEnd and yEnd is the greater value, right or bottom.
   * This form of storing is better to calculate the pixel position while building the graphic and it is better
   * to calculate related position too.    
   * <br><br>
   * 
 * @author Hartmut Schorrig
 *
 */
public class GralPos implements Cloneable
{
  /**Version, history and license.
   * <ul>
   * <li>2022-08 refactored. Now also for textual configured Widgets the {@link #setPosition(CharSequence, GralPos)} is used.
   *   It means, should support all features. It is refactored and yet in test. Some bugfixes including, and more features.
   * <li>2013-05-24 Hartmut bugfix fine position can be greater 20 if positions are add and sizes are add too. 
   * <li>2011-10-01 Hartmut corr: Calculation of next position or refer + value if the size was negative and sameSize is selected.
   *                Then the new input value should calculate from the bottom or left value because the size is negative furthermore.
   * <li>2011-10-01 Hartmut bugfix: if(qf >= 10)... instead >10 
   * <li>2011-09-23 Hartmut chg: The methods {@link #setPosition(GralPos, float, float, float, float, int, char)} etc
   *     are moved from the GralGridMngBase to this. It are methods of this class functionally. The GralGridMngBase wrappes it
   *     because that methods should be able to call there.
   * <li>2011-08-31 Hartmut new: constants {@link #same} etc. as adding values for setPosition-methods.
   *     It prevents the necessity of a lot of special set methods. The parameter for positions may be relative, referred etc.
   *     to the previous position or to a frame.
   * <li>2011-08-31 Hartmut new: method {@link #toString()} to see values of instance in debug
   * <li>2011-08-14 Hartmut new: creation of this class. Beforehand this values are stored inside the GralGridMngBase as main position.
   *     But a position in this kind is necessary in other contexts too, and the position values should be pooled in one class.                       
   * </ul>
   * <ul>
   * <li>2011-06-00 Hartmut created
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final int version = 20120317;

  
  /**This adding value applied at any coordinate parameter of any setPosition- method means, that the value is 
   * referred to the position of the previous or given position. The referred value may be given as positive or negative number.
   * Adding this constant a value in range 0x7000...0x8000 to 0x8fff results for positions -4096...4095 (usual -100..100)
   * Hint: If only the fractional part is changed, the non-fractional part should be given as refer.
   */
  public final static int refer = 0x8000;
  
  /**This value applied at any coordinate parameter of any setPosition- method means, that the value is 
   * the same as the previous or given position.
   * Hint: The constant is equal to {@link #refer}. This constant regards the semantic.
   */
  public final static int same = 0x8000;
  
  /**Use the next value for the coordinate in relation to the last one for the given direction
   * but the same value for the other coordinate. It is the typical value for both coordinates
   * if a quasi-float layout is desired. 
   * This value can be applied to x, xEnd, y, yEnd or to the float given arguments of any setPosition- method. 
   * It uses xEnd for x and yEnd for y with the adequate fractional parts. 
   * The new value for xEnd or yEnd is calculated using the size if this value is applied to them. 
   */
  public final static int next = 0xdffe;
  
  /**Use the next value for the coordinate in relation to the last one. 
   * This value can be applied to x, xEnd, y, yEnd or to the float given arguments of any setPosition- method. 
   * It uses xEnd for x and yEnd for y with the adequate fractional parts. 
   * The new value for xEnd or yEnd is calculated using the size if this value is applied to them. 
   */
  public final static int nextBlock = 0xdffd;
  
  /**Marker for invalid, to check*/
  public final static int invalidPos = 0xdfff;
  
  /**This value at xEnd or yEnd means, that the native size of a widget should be used.
   * It is especially to draw images with its native size.
   * This is a bit mask. The nature size is stored in the bits 12..0, that is maximal 8191 pixel 
   */
  public final static int useNatSize = 0xc000;
  
  
  /**This bit at all coordinates means, that the value is given as ratio from the size.
   * The bit {@link useNatSize} have to be 0. The ratio is stored as a value from 0 to 999
   * in the bits 9..0. The other bits should be 0.
   */
  public final static int ratio = 0xa000;
  
  
  /**This adding value at xEnd or yEnd or the float presentations of the calling argument of any
   * setPosition- method means, that the value is the size, not the position.
   * A size can be positive or negative. A negative size determines, that the origin point for
   * further elements or inner elements is on bottom line or right line of the current widget.
   */
  public final static int size = 0x4000;  //Note: Bit is contained in useNatSize, (pos = useNatSize | size) == useNatSize
  
  
  /**Use the same size.
   * 
   */
  public final static int samesize = 0x6000;
  

  /**Bits in an integer position value for range and size.
   * of relative position values for size, percent and refer. The mask for the value is 0x1fff.
   * The range of any value is from 0x0 to 0x0fff for positive values and from 0x1fff downto 0x1000
   * for negative values. The value == 0 is designated with 0x0 after mask. */
  private final static int mValueRange_ = 0x1fff, mValueNegative = 0x1000;
  
  /**Bits in an integer position value for the type. 
   * If the type should be tested, the mTypwAdd_ should be added before masking. That is because
   * the negative values for a specific type are disposed type - value */
  private final static int mType_ = 0xe000, kTypAdd_ = 0x1000;
  
  /**Bit masks for the special types {@link #next}, {@link #nextBlock}, {@link #useNatSize}. */
  private final static int  mSpecialType = 0xff00, kSpecialType=0xdf00; 
  
  /**Mask to check ratio. This bits should be mask and the ratio value should compare with them. */
  private final static int XXXmCheckRatio_ = 0xf000;
  
  /**Mask for value of natural size. The maximum size is 16383 pixel. */
  private final static int XXXmNatSize_ = 0xf001;
  
  /**Mask bits to designate related position, related end position and size.
   * 
   */
  //private final static int mBitRel = 0x1, mBitSize = 0x2, mBitRelEnd = 0x4, mBitSizeNeg = 8;
  
  /**Position and mask of bits to designate kind of parameter. See {@link #parameterDesignation} */ 
  //private final static int kBitParamDesignator_x = 0, kBitParamDesignator_y = 8, mParamDesignator = 0xff; 
  
  
  /**Syntax of a position. It is
   * <pre>
  position::= @ [<$?panel> ,]
       [<?yPosRelative> &+] [<#?yPos>[\\.<#?yPosFrac>]]
     [ [+] <#-?ySizeDown>[\\.<#?ySizeFrac>]| +* <?yOwnSize> |] 
   [ , [<?xPosRelative> &+] [<#?xPos>[\\.<#?xPosFrac>]]
     [ [+] <#-?xWidth>[\\.<#?xSizeFrac>]| +* <?xOwnSize> |] [ <?xIncr> ++]
   ].
   * </pre>
   * The semantic identifier match to the elements in {@link org.vishia.gral.cfg.GralCfgPosition}.
   * */
  public final static String syntaxZbnf = null; //not used because the syntax is evaluated by hard code.
  /*
  "position::= @ [<$?panel> ,]"
  + "     [<?yPosRelative> &+] [<#?yPos>[\\.<#?yPosFrac>]]"
  + "   [ [+] <#-?ySizeDown>[\\.<#?ySizeFrac>]| +* <?yOwnSize> |] ##| - <#?ySizeUp>|][ <?yIncr> ++]"
  + " [ , [<?xPosRelative> &+] [<#?xPos>[\\.<#?xPosFrac>]]"
  + "   [ [+] <#-?xWidth>[\\.<#?xSizeFrac>]| +* <?xOwnSize> |] [ <?xIncr> ++]"
  + " ] :";
  */
  
  
  
  /**The Property for the input parameter to use same, next etc. 
   * This value is used to generate an adequate config file from given input values.
   * The coordinates don't carry the information about that input values.
   */
  //public int parameterDesignation;
  
  /**Position of any widget.
   * Generally: There are coordinates in a grid, not in pixel. 
   * Positive value is from top or left, negative value is from right or bottom.
   * {@link GralGridPos#useNatSize} on xEnd, yEnd means, that the natural size of the object should be used.
   * 
   */
  //public int x, xEnd, y, yEnd;

  
  
  /**Fractional part of position.
   * Generally: It is a number from 0 to 9 as part of 1 grid unit.
   */
  //public int xFrac, xEndFrac, yFrac, yEndFrac;
  
  /**The border to the next element. */
  //public int xyBorder, xyBorderFrac;
  
  /**Origin of widget, use l, m, r for xOrigin and t, m, b for yOrigin. */
  //public char xOrigin, yOrigin;
  
  /**direction of the next element. Use r, d, l, u. */
  //public char dirNext;
  
  /**Relation of x and y left and top to any separation line. 0 - relation to left and top border. */
  //public int xSepLine, ySepLine;
  
  public GralWidget_ifc parent;


  /**The values for x and y positions. Note: should be private! Don't use in application furthermore. */
  public final Coordinate x = new Coordinate(), y = new Coordinate();
  
  /**Creates an position with all values 0.
   * The position can be changed after them with {@link #setPosition(float, float)} etc.
   */
  public GralPos(){}
  
  
  /**Set a initial GralPos for a given PanelContent (also a Window).
   * Afterwards any absolute position should be set. Without set, this GralPos describes the whole panel size.
   * Because also the end values (right, bottom) are 0, it is the whole left to right and top to bottom position.
   * @param panel The given panel
   */
  public GralPos(GralWidget parent) {
    this.parent = parent;
  }
  
  /**A GralPos should never create as instance from the application. It is created only from the GralMng which accesses package private.
   * It is copied inside the GralMng for any widget. The position can be changed after them with {@link #setPosition(float, float)} etc. 
   * @param src the src position to copy all values.
   */
  public GralPos(GralPos src){ set(src); }
  
  public GralPos(String pos) throws ParseException {
    this();
    this.setPosition(pos, this);
  }
  
  /**The border to the next element. */
  //public int xyBorder, xyBorderFrac;

  /**Origin of widget, use l, m, r for xOrigin and t, m, b for yOrigin. */
  //public char xOrigin, yOrigin;
  
  /**direction of the next element. Use r, d, l, u. */
  //public char dirNext;
  
  /**Sets all values of this with the values of pos (copy values)
   * @param pos The src pos
   */
  public void set(GralPos pos)
  { this.x.p1 = pos.x.p1; this.x.p2 = pos.x.p2; 
    this.y.p1 = pos.y.p1; this.y.p2 = pos.y.p2;
    this.x.p1Frac = pos.x.p1Frac; this.x.p2Frac = pos.x.p2Frac; 
    this.y.p1Frac = pos.y.p1Frac; this.y.p2Frac = pos.y.p2Frac;
    this.x.origin = pos.x.origin; this.y.origin = pos.y.origin; 
    this.x.dirNext = pos.x.dirNext; this.y.dirNext = pos.y.dirNext;
    this.parent = pos.parent;
  }
  
  
  
  public void setPosition(float line, float column)
  {
    setPosition(this, line, size + same, column, size + same, 0, '.');
  }
  
  
  
  
  /* (non-Javadoc)
   * @see org.vishia.gral.gridPanel.GuiPanelMngBuildIfc#setPosition(int, int, int, int, char)
   */
  public void setPositionSize(int line, int column, int height, int width, char direction, GralPos posFrame)
  { setFinePosition(line, 0, height + GralPos.size, 0, column, 0, width + GralPos.size, 0, 1, direction, 0, 0, posFrame);
  }

  
  /**Sets the position with the given string representation.
 <pre>
 position::= [@] [<$?panel> ,] 
                [<?yPosRelative> &+]
                [<#?yPos>[\.<#?yPosFrac>]] 
                [ [+] <#-?ySizeDown>[\.<#?ySizeFrac>]| +* <?yOwnSize> |] ##| - <#?ySizeUp>|] 
                [ <?yIncr> ++] 
                [ ,
                  [<?xPosRelative> &+] 
                  [<#?xPos>[\.<#?xPosFrac>]] 
                  [ [+] <#-?xWidth>[\.<#?xSizeFrac>]| +* <?xOwnSize> |]
                  [ <?xIncr> ++]
                ] :
 ] 
</pre>
   * @param sPos The syntax of the string see description of this class, starting with "@panel, ..." etc.
   *   for example "@windowA, 3..5, 16+20" for absolute line 3 and 4 (exclusive 5) and from absolute column 16, size-x=20. 
   *   The position can be given without panel designation or relative, then the posParent argument is necessary.
   * @param posParent necessary to build the absolute position from relative given sPos, maybe null if not necessary.
   * @throws ParseException on errors of sPos or missing posParent if necessary.
   */
  public void setPosition(CharSequence sPos, GralPos posParent) throws ParseException {
    GralPos posParent1;
    //int line =0, yPosFrac =0, ye =0, yef =0, column =0, xPosFrac =0, xe =0, xef =0, origin =0, border =0, borderFrac =0;
    Coordinate line = new Coordinate(), col = new Coordinate();
    int  origin =1;                                        // default left top
    int border =0, borderFrac =0;
    if(posParent !=null) {
      origin = 0;  // do not change, get from parent.
    }
    char direction = 'r';
    if(sPos ==null) {
      //position text not given, use refer and same size
      line.p1 = next;
      line.p2 = samesize;
      //all other values of line and col remain 0. It is default.
      col.p1 = next;
      col.p2 = samesize;
      posParent1 = posParent; 
    } else {
      //position given as text
      StringPartScan spPos = new StringPartScan(sPos);
      spPos.setIgnoreWhitespaces(true);
      try {
        spPos.scan("@").scanStart();  //skip over a first @
        if(spPos.scanIdentifier().scan(",").scanOk()) {  //ckeck if a panel is given:
          String sPanel = spPos.getLastScannedString().toString();
          GralMng mng = GralMng.get();  //singleton.
          GralPanel_ifc panel = mng.getPanel(sPanel);
          if(panel == null) {
            spPos.close();
            throw new IllegalArgumentException("GralPos.setPosition - unknown panel, " + sPanel);
          }
          if(posParent !=null && panel == posParent.parent) {
            posParent1 = posParent;
          } else {
            //only if it is another panel, remove the given parent.
            posParent1 = null; //new GralPos();                    // should exist formally
            this.parent = panel;
          }
        } else {
          posParent1 = posParent;  //the parent is valid. Because no other panel. Use the current panel.
        }
        //======>>>>>>
        scanPosition(spPos, line);
        if(spPos.scan(",").scanOk()) {
          //======>>>>>>
          scanPosition(spPos, col);
        } else {
          col.p1 = line.p1 == next ? next : refer;         // line.next is set if nothing is scanned on line.
          col.p2 = samesize;
        }
      } finally {
        spPos.close();
      }
    }
    setFinePosition(line.p1, line.p1Frac, line.p2, line.p2Frac, col.p1, col.p1Frac, col.p2, col.p2Frac,  origin, direction, border, borderFrac, posParent1);
  }
  
  
  
  /**Scans one coordinate.
   * <pre>
   * [[<?refer>+|<?percent>%]<#?p1>[.<#p2Frac>] [..|<?size>]] <#?p2>[.<#p2Frac>]
   * </pre>
   * If only the right number is given, p1 is {@link #refer}
   * @param spPos
   * @param co
   * @throws ParseException
   */
  private void scanPosition(StringPartScan spPos, Coordinate co) throws ParseException {
    char sign = spPos.seekNoWhitespace().getCurrentChar();
    int size1; //maybe size for end element, converted to refer for start element.
    co.p1 = invalidPos;                                          // default, next line or next column depending on parent.
    co.p1Frac = invalidPos;
    co.p2 = invalidPos;                                      // default, same size for heigth or width
    co.p2Frac = invalidPos;
    int rel_p1 = 0;
    final int[] rel_select = { refer, ratio};
    //
    int typeFirstChar = "+%".indexOf(sign);
    if(typeFirstChar >=0) {
      rel_p1 = rel_select[typeFirstChar];                  // additional value to p1
      spPos.seekPos(1);  //skip over first char
    }
    if(spPos.scanInteger().scanOk()) {                     // value maybe negative scanned  10 or -10
      co.p1 = rel_p1 + (int)spPos.getLastScannedIntegerNumber();  //higher bits determines rel_p1, lower bits are scanned number
      if(spPos.scan(".").scanInteger().scanOk()) {
        co.p1Frac = (int)(spPos.getLastScannedIntegerNumber() % 10);  //should be 0..9
      } else {
        co.p1Frac = 0;
      }
    } else {
      co.p1 = next;                                        // nothing given, then the next position should be used
      co.p1Frac = 0;                                       // respectively the same in the unchanged direction
    }
    int size2;
    int rel_p2;   //relative to p1
    if(spPos.scan("..").scanOk()) {                        // absolute position for end is given
      rel_p2 = 0;
    } else {
      rel_p2 = size;                                       // not .. then size as 2th number
      spPos.scan("+").scanStart();                         //skip over '+', it is the separator, 
    }
    if(spPos.scanInteger().scanOk()) {                     //can start with '-' but not with '+'
      co.p2 = rel_p2 + (int)spPos.getLastScannedIntegerNumber();    // maybe negative, means from right
      if(spPos.scan(".").scanInteger().scanOk()) {
        co.p2Frac = (int)(spPos.getLastScannedIntegerNumber() % 10);  //should be 0..9
      } else {
        co.p2Frac = 0;                                     // frac default 0 if p2 is determined.
      }
    } else {
      co.p2 = samesize;                                    // not an integer following, nothing given, then samesize as default
      co.p2Frac = 0;
    }
    assert(co.p1 != invalidPos && co.p2 != invalidPos && co.p1Frac >=0 && co.p1Frac <=9 && co.p2Frac >=0 && co.p2Frac <=9 );
  }
  
  
  
  /**Sets the position absolutely or also relative to the given position with given values.
   * @param parent The panel which is used as container. If null then left the existing panel unchanged.
   * @param line The line. If the parameter lineEndOrSize is designated with {@link #size} with a negative value,
   *   it is the bottom line for the position. 
   *   If it is designated with {@link #same} without offset and the lineEndOrSize is designated with {@link #size} 
   *   with a negative value, the framePos {@link GralPos#y.p2} is used. If it is designated
   *   with {@link #same} but with any offset, the {@link GralPos#y} is used as refer position, it is the top line position.
   *   Elsewhere it is the top position.
   * 
   * @param lineEndOrSize Maybe designated with {@link #size} or {@link #samesize}
   * @param column
   * @param columnEndOrSize
   * @param origin
   * @param direction
   */
  public void setPosition(GralWidget_ifc parent, float line, float lineEndOrSize, float column, float columnEndOrSize
      , int origin, char direction, float border)
  {
    if(parent !=null) { this.parent = parent; }               // change the parent only if given
    assert(this.parent !=null);                             // should always have a parent information. 
    int[] pos = new int[10];
    frac(line, pos, 0);
    frac(lineEndOrSize, pos, 2);
    frac(column, pos, 4);
    frac(columnEndOrSize, pos, 6);
    frac(border, pos, 8);
    setFinePosition(pos[0], pos[1], pos[2], pos[3], pos[4], pos[5], pos[6], pos[7], origin, direction, pos[8], pos[9], parent.pos());
    /*
    int y1 = (int)(line);
    int y1f = frac(y1, line);
    int y2 = (int)(lineEndOrSize);
    int y2f = frac(y2, lineEndOrSize);
    int x1 = (int)(column);
    int x1f = frac(x1, column);  
    int x2 = (int)(columnEndOrSize);
    int x2f = frac(x2, columnEndOrSize); 
    setFinePosition(y1, y1f, y2, y2f, x1, x1f, x2, x2f, origin, direction, framePos);
    */
  }

  
  
  
  /**Sets the position
   * @param refPos The frame or last pos for relative positions.
   * @param line The line. If the parameter lineEndOrSize is designated with {@link #size} with a negative value,
   *   it is the bottom line for the position. 
   *   If it is designated with {@link #same} without offset and the lineEndOrSize is designated with {@link #size} 
   *   with a negative value, the framePos {@link GralPos#y.p2} is used. If it is designated
   *   with {@link #same} but with any offset, the {@link GralPos#y} is used as refer position, it is the top line position.
   *   Elsewhere it is the top position.
   * 
   * @param lineEndOrSize Maybe designated with {@link #size} or {@link #samesize}
   * @param column
   * @param columnEndOrSize
   * @param origin
   * @param direction
   */
  public void setPosition(GralPos refPos, float line, float lineEndOrSize, float column, float columnEndOrSize
      , int origin, char direction, float border)
  {
    int[] pos = new int[10];
    frac(line, pos, 0);
    frac(lineEndOrSize, pos, 2);
    frac(column, pos, 4);
    frac(columnEndOrSize, pos, 6);
    frac(border, pos, 8);
    setFinePosition(pos[0], pos[1], pos[2], pos[3], pos[4], pos[5], pos[6], pos[7], origin, direction, pos[8], pos[9], refPos);
    /*
    int y1 = (int)(line);
    int y1f = frac(y1, line);
    int y2 = (int)(lineEndOrSize);
    int y2f = frac(y2, lineEndOrSize);
    int x1 = (int)(column);
    int x1f = frac(x1, column);  
    int x2 = (int)(columnEndOrSize);
    int x2f = frac(x2, columnEndOrSize); 
    setFinePosition(y1, y1f, y2, y2f, x1, x1f, x2, x2f, origin, direction, framePos);
    */
  }
  

  /**Sets the position
   * @param framePos The frame or last pos for relative positions.
   * @param line The line. If the parameter lineEndOrSize is designated with {@link #size} with a negative value,
   *   it is the bottom line for the position. 
   *   If it is designated with {@link #same} without offset and the lineEndOrSize is designated with {@link #size} 
   *   with a negative value, the framePos {@link GralPos#y.p2} is used. If it is designated
   *   with {@link #same} but with any offset, the {@link GralPos#y} is used as refer position, it is the top line position.
   *   Elsewhere it is the top position.
   * 
   * @param lineEndOrSize Maybe designated with {@link #size} or {@link #samesize}
   * @param column
   * @param columnEndOrSize
   * @param origin
   * @param direction
   */
  public void setPosition ( GralPos framePos, float line, float lineEndOrSize, float column, float columnEndOrSize ) {
    int[] pos = new int[10];
    frac(line, pos, 0);
    frac(lineEndOrSize, pos, 2);
    frac(column, pos, 4);
    frac(columnEndOrSize, pos, 6);
    frac(framePos.x.pb, pos, 8);
    setFinePosition(pos[0], pos[1], pos[2], pos[3], pos[4], pos[5], pos[6], pos[7], 0, 'r', pos[8], pos[9], framePos);
  }
  
  
  
  /**Returns true if this position is from right or bottom, so that a resize of the parent needs new positions for this widget.
   */
  public boolean toResize(){ return x.p1 < 0 || x.p2 <= 0 || y.p1< 0 || y.p2 <=0; }
  
  private void frac(float v, int[] pos, int ix){
    int i, f;
    i = (int)v;
    if((i & mValueNegative) !=0){
      f = (int)((v-i)*10 + 0.5f);
      if( f == 10){ f = 0; }
    } 
    else {
      f = (int)((v - i)*10 + 0.5f);
    }
    if(f < 0){
      i -=1; f +=10;
    }
    assert(f >=0 && f <= 9);
    pos[ix] = i; pos[ix+1]= f;
    //return f;
  }


  
  
  
  public void setPosition(GralPos framePos, float line, float lineEndOrSize, float column, float columnEndOrSize
    , int origin, char direction)
  {
    setPosition(framePos, line, lineEndOrSize, column, columnEndOrSize, origin, direction, 0);
  }
  
  
  /**Sets the position for the next widget to add in the container.
   * Implementation note: This is the core function to calculate positions. It is called from all other ones.
   * @param yPos y-Position in y-Units, count from top of the box. It is the bottom line of the widget.
   *              If <0, then it counts from bottom of the parent.
   * @param xPos x-Position in x-Units, count from left of the box. 
   *              If <0, then the previous position is valid still.
   *              It < 0 then line = 0 is not a proper value. To show a text in the first line, use line=2.
   * @param heigth: The height of the line. If <0, then the param line is the bottom line of the widget, 
   *                and (line-height) is the top line. If 0 then the last value of height is not changed. 
   * @param length: The number of columns. If <0, then the param column is the right column, 
   *                and column-length is the left column. If 0 then the last value of length is not changed.
   * @param direction: direction for a next widget, use 'r', 'l', 'u', 'd' for right, left, up, down
   * @param refPos if given, the parent is the base for calculation. If null, this itself is the base.
   *
   * @param yPos yPos 
   * @param yPosf fractional part of yPos, 0, 1..9
   * @param ye
   * @param yef
   * @param xPos
   * @param xPosf
   * @param xe
   * @param xef
   * @param origin 0 then get from parent or left as is, 1 2 3 on top-left, -mid, -right 4 5 6 mid, 7-8-9 bottom left to right 
   * @param direction
   * @param border
   * @param borderFrac
   * @param refPos The reference position for relative coordinates 
   */
  public void setFinePosition(int yPos, int yPosf, int ye, int yef
      , int xPos, int xPosf, int xe, int xef, int origin, char direction
      , int border, int borderFrac
      , GralPos refPos)
  {
    //
    if(yPosf < 0 || yPosf >9){
      throw new IllegalArgumentExceptionJc("GralPos - yPosFrac error", yPosf);
    }
    if(xPosf < 0 || xPosf >9) {
      throw new IllegalArgumentExceptionJc("GralPos - xPosFrac error", xPosf);
    }
    //
    if(ye == (size -1) && yef == 5)
      stop();
    if(ye == useNatSize)
      stop();
    //
    final GralPos refUse = refPos == null ? this : refPos;
    //
    if(this.parent ==null) { this.parent = refPos.parent; }
    if(origin >0 && origin <=9) {                          // newly given
      int yOrigin = (origin-1) /3;
      int xOrigin = origin - yOrigin -1; //0..2
      this.x.origin = "lmr".charAt(xOrigin);
      this.y.origin = "tmb".charAt(yOrigin);
    } else {                                               // origin == 0: take from parent or from this.
      this.x.origin = refUse.x.origin;                  // from this means, left it as is.
      this.y.origin = refUse.y.origin;
    }
    
    this.y.set(yPos, yPosf, ye, yef, "uUdD", refUse.y);      // core of positioning for line and column
    this.x.set(xPos, xPosf, xe, xef, "lLrR", refUse.x);
    
    if("rlRL".indexOf(direction)>=0 ){
      this.x.dirNext = Character.toLowerCase(direction);
      this.y.dirNext = '.';
      this.x.pb = border;
      this.x.pbf = borderFrac;
      this.y.pb = this.y.pbf = 0;
    } else if("udUD".indexOf(direction)>=0 ){
      this.y.dirNext = Character.toLowerCase(direction);
      this.x.dirNext = '.';
      this.y.pb = border;
      this.y.pbf = borderFrac;
      this.x.pb = this.x.pbf = 0;
    } else {
      this.x.dirNext = Character.toLowerCase(refUse.x.dirNext);
      this.y.dirNext = Character.toLowerCase(refUse.y.dirNext);
      this.x.pb = refUse.x.pb; this.x.pbf = refUse.x.pbf;
      this.y.pb = refUse.y.pb; this.y.pbf = refUse.y.pbf;
    }
    assert(".\0lr".indexOf(this.x.dirNext) >=0);
    assert(".\0ud".indexOf(this.y.dirNext) >=0);
    assert(this.x.p1Frac >=0 && this.x.p1Frac < 10 && this.y.p1Frac >=0 && this.y.p1Frac < 10 );
    assert(this.x.p2Frac >=0 && this.x.p2Frac < 10 && this.y.p2Frac >=0 && this.y.p2Frac < 10 );
  
    
  }
  
  
  
  
  public void setSize(int height, int ySizeFrac, int width, int xSizeFrac)
  {
    if(height !=0){
      //ySize = height >0 ? height : -height;
      //this.ySizeFrac = ySizeFrac;
    }
    if(width !=0){
      //xSize = width >0 ? width: -width;
      //this.xSizeFrac = xSizeFrac;
    }
    if(height >0){ this.y.origin = 't'; }
    else if(height < 0){ this.y.origin = 'b'; }
    else; //let it unchanged if height == 0
    if(width >0){ this.x.origin = 'l'; }
    else if(width < 0){ this.x.origin = 'r'; }
    else; //let it unchanged if width == 0
  }
  
  
  public void setSize(float height, float width, GralPos frame)
  { 
    int y2 = (int)(height);
    int y2f = y2 >=0 ? (int)((height - y2)* 10.001F) : (int)((height - y2)* -10.001F);  
    int x2 = (int)(width);
    int x2f = x2 >=0 ? (int)((width - x2)* 10.001F) : (int)((width - x2)* -10.001F); 
    setFinePosition(GralPos.next, 0,  y2 + GralPos.size, y2f, GralPos.next, 0, x2 + GralPos.size, x2f, 0, '.', 0, 0, frame);
  }
  
  
  /**Sets the position to the next adequate the {@link #pos.dirNext}. */
  public void setNextPosition()
  {
    setPosition(this, next, samesize, next, samesize, 0, '.', 0 );
    /*
    float dx3 = this.width();
    float dy3 = this.height();
    int dx = (int)dx3;
    int dxf = (int)((dx3 - dx) * 10.001F) + this.x.p2Frac;
    if(dxf >= 10){ dxf -=10; dx +=1; }
    int dy = (int)dy3;
    int dyf = (int)((dy3 - dy) * 10.001F) + this.y.p2Frac;
    if(dyf >= 10){ dyf -=10; dy +=1; }
    switch(this.dirNext){
    case 'r': this.x = this.x.p2; this.x.p1Frac = this.x.p2Frac; this.x.p2 = this.x + dx; this.x.p2Frac = dxf; break;
    case 'd': this.y = this.y.p2; this.y.p1Frac = this.y.p2Frac; this.y.p2 = this.y + dy; this.y.p2Frac = dyf; break;
    }
    */
  }
  

  
  /**This operation should be called before usage the position.
   * If the dirNext is in upper case, {@link #setNextPosition()} is called.
   * This is the state where no setPosition was called after last usage. 
   * The Position#dirNext of the x coord is set to upper case after them
   * to mark it for the next call of {@link #checkSetNext()}.
   */
  public void checkSetNext ( ) {
    if("LR".indexOf(this.x.dirNext) >=0 || "UD".indexOf(this.y.dirNext) >=0  ) {
      setNextPosition();
    } 
    boolean bNext = false;
    switch(this.x.dirNext) {                 // check and set to upper case
    case '\0': break;
    case '.': break;
    case 'l': this.x.dirNext = 'L'; break;
    case 'r': this.x.dirNext = 'R'; break;
    default: throw new IllegalArgumentException("faulty dirnext" + toString());
    }
    switch(this.y.dirNext) {                 // check and set to upper case
    case 'u': this.y.dirNext = 'U'; break;
    case 'd': this.y.dirNext = 'D'; break;
    case '.': break;
    case '\0': break;
    default: throw new IllegalArgumentException("faulty dirnext" + toString());
    }
  }
  
  
  
  
  
  public float height()
  { float height;
    if(y.p1 * y.p2 >= 0){
      height = y.p2 - y.p1;
    }
    else{ 
      height = 2.0F;  //not able to determine, use default.
    }
    height += (y.p2Frac - y.p1Frac) * 0.1F;
    return height;
  }
  
  
  public float width()
  { float width;
    if(y.p1 > 0 && x.p2 > 0){ width = x.p2 - y.p1 + (x.p2Frac - x.p1Frac) * 0.1F; }
    else if(x.p1 < 0 && x.p2 < 0){ width = x.p2 - x.p1 + (x.p2Frac - x.p1Frac) * 0.1F; }
    else { width = 0.0F; } //not able to determine, use default.
    return width;
  }
  
  
  
  
  
  
  @Override public GralPos clone(){
    //Hint: Object.clone() can't be used because it clones the references of x and y and not its values. 
    GralPos newObj = new GralPos();
    newObj.x.set(x);
    newObj.y.set(y);
    newObj.parent = parent;
    //try{ newObj = (GralGridPos)super.clone(); 
    //} catch(CloneNotSupportedException exc){ assert(false); }
    return newObj; 
  }
  
  
  
  
  /**Sets the given position with the given posString.
   * @param posString "@parent,line, col:" syntax see description on class level.
   * @return new calculated position equal to this.
   *   The clone is done because the result is intend to use as position for the current Gral Widget
   *   during building the Gui. This instance is furthermore used as currently calculated,
   *   the widget aggregates a
   * @throws ParseException
   */
  public GralPos calcNextPos(String posString) throws ParseException {
    if(posString.equals("!")) {  //new window, initialize the position without parent because it is top.
      parent = null;
      setFinePosition(0,0,0,0,0,0,0,0,0,'d', 0,0, null);
    } else {
      setPosition(posString, this);
      //TODO change pos:
    }
    return this; 
  }
  
  
  
  
  

  
  
  /**Calculates the position and size of a widget from this given Pos.
   * @param propertiesGui The properties for presentation.
   * @param widthParentPixel width of the container. This value will be used if the position is given 
   *   from right with negative numbers.
   * @param heightParentPixel height of the container. This value will be used if the position is given 
   *   from bottom with negative numbers.
   * @param widthWidgetNat natural width of the component which will be positioning. 
   *   This value is used only if the pos parameter contains {@link GralPos#useNatSize} for the xe-value
   * @param heightWidgetNat natural height of the component which will be positioning. 
   *   This value is used only if the pos parameter contains {@link GralPos#useNatSize} for the ye-value
   * @return A rectangle for setBounds.
   */
  public GralRectangle calcWidgetPosAndSize(GralGridProperties propertiesGui,
      int widthParentPixel, int heightParentPixel,
      int widthWidgetNat, int heightWidgetNat
  )
  {
    int xPixelUnit = propertiesGui.xPixelUnit();
    int yPixelUnit = propertiesGui.yPixelUnit();
    //calculate pixel
    final int x1,y1, x2, y2;
    ///
    
    x1 = xPixelUnit * this.x.p1 + propertiesGui.xPixelFrac(this.x.p1Frac)  //negative if from right
       + (this.x.p1 < 0 ? widthParentPixel : 0);  //from right
    y1 = yPixelUnit * this.y.p1 + propertiesGui.yPixelFrac(this.y.p1Frac)  //negative if from right
       + (this.y.p1 < 0 ? heightParentPixel : 0);  //from right
    if(this.x.p2 == GralPos.useNatSize){
      x2 = x1 + widthWidgetNat; 
    } else {
      x2 = xPixelUnit * this.x.p2 + propertiesGui.xPixelFrac(this.x.p2Frac)  //negative if from right
         + (this.x.p2 < 0 || this.x.p2 == 0 && this.x.p2Frac == 0 ? widthParentPixel : 0);  //from right
    }
    if(this.x.p2 == GralPos.useNatSize){
      y2 = y1 + heightWidgetNat; 
    } else {
      y2 = yPixelUnit * this.y.p2 + propertiesGui.yPixelFrac(this.y.p2Frac)  //negative if from right
         + (this.y.p2 < 0  || this.y.p2 == 0 && this.y.p2Frac == 0 ? heightParentPixel : 0);  //from right
    }
    GralRectangle rectangle = new GralRectangle(x1, y1, x2-x1-1, y2-y1-1);
    return rectangle;
  }




  private static void appendPos(Appendable b, int p, int pFrac) throws IOException
  {
    if(pFrac >0) {
      if(p < 0) { b.append(Integer.toString(p-1)); } else { b.append(Integer.toString(p)); }
      b.append('.').append(Integer.toString(pFrac));
    } else {
      b.append(Integer.toString(p));
    }
  }


  
  
  /**Returns the position in a syntax which is able to parse 
   * Useful to write back to a configuration file.
   */
  public String posString ( ) { 
    StringBuilder b = new StringBuilder(16);
    try {
      b.append('@');
      if(this.parent != null) {
        b.append(this.parent.getName()).append(", ");
      }
      appendPos(b, this.y.p1, this.y.p1Frac);
      b.append("..");
      appendPos(b, this.y.p2, this.y.p2Frac);
      b.append(", ");
      appendPos(b, this.x.p1, this.x.p1Frac);
      b.append("..");
      appendPos(b, this.x.p2, this.x.p2Frac);
    } catch(IOException exc) { throw new RuntimeException("unecpected", exc); }
    return b.toString();
  }
  
  
  
  /**Append position to given Appendable, for toString of using widgets.
   * @param b append here
   * @return b for concatenate.
   * @throws IOException can be force RuntimeException if a StringBuilder is given as Appendable
   */
  public Appendable toString(Appendable b, boolean bAppendPanel) throws IOException 
  { b.append('@');
    if(bAppendPanel && this.parent != null) {
      b.append(this.parent.getName()).append(", ");
    }
    appendPos(b, this.y.p1, this.y.p1Frac);
    b.append("..");
    appendPos(b, this.y.p2, this.y.p2Frac);
    b.append(", ");
    appendPos(b, this.x.p1, this.x.p1Frac);
    b.append("..");
    appendPos(b, this.x.p2, this.x.p2Frac);
    return b;
  }
  
  /**Use especially for debug.
   * @see java.lang.Object#toString()
   */
  @Override public String toString()
  { return "panel=" + (this.parent == null ? "?" : this.parent.getName()) + ", "
    +"line=" + this.y.toString() + " col=" + this.x.toString() + " " + this.y.dirNext + this.x.dirNext + this.y.origin + this.x.origin;
  }

  
  
  void stop(){}

  
  /**Class holds values for either x or y. Both values are calculated with adequate algorithms,
   * so that algorithm is written only one time but called 2 times for x and for y.
   * 
   *
   */
  public static class Coordinate
  {
    /**The start position for the spread. 
     * If there are positive numbers, they count from left to right respectively top to bottom. 
     * If the value is negative, the absolute is the distance from right respectively bottom. 
     */
    public int p1;
    
    /**The end position for the spread. 
     * If there are positive numbers, they count from left to right respectively top to bottom. 
     * If the value is negative or 0, the absolute is the distance from right respectively bottom. 
     */
    public int p2;
    
    /**Start Position in percent. If -1 then not used.
     * 
     */
    int n1 = -1;
    ///
    /**End Position in percent. */
    int n2;
    
    /**Fractional parts of position. Use 0..9 only. 
     * The fractional part counts from left to right respectively top to bottom 
     * independent of the sign of p1, p2.
     */
    public int p1Frac, p2Frac;
    
    /**Additional border value for {@link GralPos#next}. */
    public int pb, pbf;
    
    /**Attributes of this coordinate. */
    //public int attr;
    
    /**Origin point of the pos in the widget.
     * Use l m, r, t, m, b for x and y (left, mid, right, top, bottom)
     */
    char origin;
    
    /**direction of the next element. Use r, d, l, u for right, down, left, up.
     * If this characters are in upper case R D L U, then the position is not set newly since usage. 
     * It means it should be set forward in the proper direction by next usage. */
    public char dirNext;

    

    /**Sets the new position for this coordinate.
     * Special functions:
     * <table>
     * <th><td>z1       </td><td>z2         </td><td>...</td></th>
     * <tr><td>0        </td><td>sameSize 0x6... </td><td>absolute start coord, same size  </td></tr>
     * <tr><td>0        </td><td>                </td><td>absolute start coord, same size  </td></tr>
     * <tr><td>0        </td><td>sameSize   </td><td>absolute start coord, same size  </td></tr>
     * <tr><td>refer    </td><td>0          </td><td>relative start coord, abs. end coord </td></tr>
     * <tr><td>refer    </td><td>0          </td><td>relative start coord, abs. end coord </td></tr>
     * <tr><td>refer    </td><td>0          </td><td>relative start coord, abs. end coord </td></tr>
     * </table>
     * @param z
     * @param zFrac
     * @param ze
     * @param zeFrac
     * @param refCoord The refer position. Note that parent may be == this because the new position based on the current.
     */
    public Coordinate set(final int z, final int zFrac, final int ze, final int zeFrac, String nextPosChars, final Coordinate refCoord)
    { /**User final local variable to set p, pf, pe, pef to check whether all variants are regarded. */
      int q1 = invalidPos, q1f =0, q2 = invalidPos, q2f =0;

      //check input parameter ze of size and negative size
      
      //The type of input parameter.
      final boolean zNeg =  (z & mValueNegative) !=0;     // zNeg true for 7000..7fff, 9000...9fff etc.
      //zType is either 0x2000, 0x4000, 0x6000, 0x8000, 0xa000, 0xc000 for one of the type designations
      final int zType = (z & mSpecialType) == kSpecialType ? z // 0xdfxx for the specialType. 
                      : (z + kTypAdd_) & mType_;          // Adding kTypeAdd_ brings forex 7000..8fff to 8000..9fff, mType_ results is 0x8000
      final boolean zeNeg =  (ze & mValueNegative) !=0;    // same for end coord.
      final int zeType = (ze & mSpecialType) == kSpecialType ? ze : (ze + kTypAdd_) & mType_;
      final int testCase;                                  // testcase for debug
      final int testType = (zType<<16) + zeType;           // combination of both coord start ...end
      if(refCoord !=this){
        pb = refCoord.pb; pbf = refCoord.pbf;
      }
      boolean bSetEnd = false;                             // true then z determines qe
      switch(zType) {
      case 0: q1 = z; q1f = zFrac; break;                  // no specific designation: absloute pos
      case refer:  //same                                  // relative to the reference position
        q1 = refCoord.p1 + (z - refer); 
        q1f = refCoord.p1Frac + zFrac;      //q = p + refer 
        break;
      case next: {
        int direction = nextPosChars.indexOf(refCoord.dirNext);
        switch(direction) {
        case 0: case 1:                                    // left or up
          q1 = refCoord.p1 - refCoord.pb; q1f = refCoord.p1Frac + refCoord.pbf;
          break;
        case 2: case 3:                                    // right or down
          q1 = refCoord.p2 + refCoord.pb; q1f = refCoord.p2Frac + refCoord.pbf;  //add the border to second (right, bottom) to get the next first pos
          break;
        default:                                           // left same position for this coord (the other may be changed)
          q1 = refCoord.p1; q1f = refCoord.p1Frac;             // don't change this coordinate. It may be the other one.              
        }//switch direction     
      } break;
      default:
          throw new IllegalArgumentException("GralPos coord set, start value case missing: 0x" + Integer.toHexString(zType));  
      } //switch for z coord
      //
      switch(zeType) {
      case 0: q2 = ze; q2f = zeFrac; break;                // no specific designation: absloute pos
      case size:
        int ze2 = (ze - size);                             // relative end position to start pos
        if(zeNeg) { 
          if(zType == same) {
            q2 = refCoord.p2;                              // use the bottom / right position 
            q2f = refCoord.p2Frac;      
            q1 = q2 + ze2;                                 // calc the top/left position via size
            q1f = q2f - zeFrac;    //TODO test
          } else {
            q2 = q1; q2f = q1f;                              // "10-2": negative size: q2 is the end point, 
            q1f = q1f -zeFrac; q1 = q1 + ze2;                   // q1 
            if(q1f <0) { q1f +=10; q1 -=1; }                 // q1 1 gridunit left/top because frac increased the distance
          }
        } else { 
          q2 = q1 + ze2; q2f = q1f + zeFrac;               // "10+2" positive size, end is caculate with size
          if(q2f >=10) { q2f -=10; q2 +=1; }
        }
        break;
      case same:                                           //"" not textual
        q2 = refCoord.p2 + (ze - refer); 
        q2f = refCoord.p2Frac + zeFrac;      //q = p + refer 
        break;
      case samesize:
        q2 = q1 + (refCoord.p2 - refCoord.p1);                 // use size of parent to calculate q2 from q1
        q2f = q1f + (refCoord.p2Frac - refCoord.p1Frac);       // same frac, note: the coord ready to use are absolutely.
        break;
      default:                                              
        throw new IllegalArgumentException("GralPos coord set, end value case missing: 0x" + Integer.toHexString(zeType));  
      }
      assert(q1 != invalidPos && q2 != invalidPos);      
      
//      switch(testType){
//        //
//        case 0: {                                          // both are absolute coord
//          testCase = 1;
//          q = z; qFrac = zFrac;                        
//          qe = ze; qeFrac = zeFrac;                        
//        } break;
//        //
//        case 0 + refer: {                                  //start absolute, end relative to parent end
//          testCase = 2;                                      
//          q = z; qFrac = zFrac;                            // it is a special case, not textual possible
//          qe = parent.p2 + (ze - refer); qeFrac = parent.p2Frac + zeFrac;
//        } break;
//        //
//        case 0 + size: {                                   //set start, size to end, typical case
//          testCase = 3;
//          if(zeNeg){ 
//            qe = z; qeFrac = zFrac;                        // if negative size, start is bottom or right point
//            q = qe + (ze - size); qFrac = qeFrac + zeFrac; //              and q calculate by size
//          } else {
//            q = z; qFrac = zFrac;                          // positive size, start is left or top
//            qe = q + (ze - size); qeFrac = qFrac +zeFrac;  // end is calculate by size
//          }
//        } break;
//        //
//        case 0 + samesize: {
//          testCase = 4;
//          if( (attr & mBitSizeNeg) !=0){     //was the last size negative? the qe is base
//            qe = z; qeFrac = zFrac;                     //qe = z
//            q = qe - (parent.p2 - parent.p1) + (ze - samesize);  //q = qe - lastsize + sizediff 
//            qFrac = qeFrac + (parent.p2Frac - parent.p1Frac) + zeFrac;
//          } else {
//            q = z; qFrac = zFrac;                       //q = z 
//            qe = q + (parent.p2 - parent.p1) + (ze - samesize);  //qe = q + lastsize + sizediff 
//            qeFrac = qFrac + (parent.p2Frac - parent.p1Frac) + zeFrac;
//          }
//        } break;
//        //
//        case 0 + useNatSize: {
//          testCase = 11;
//          q = z; qFrac = zFrac;
//          qe = ze;  qeFrac = 0; //store useNatSize
//        } break;
//        //
//        case (refer<<16) + 0: {                            // start is relative to parent, end is given.
//          testCase = 5;
//          q = parent.p1 + (z - refer); qFrac = parent.p1Frac + zFrac;      //q = p + refer 
//          qe = ze; qeFrac = zeFrac;                     //qe = ze
//        } break;
//        //
//        case (refer<<16) + refer: {                        // start and end are relative to parent.
//          testCase = 1;
//          q = parent.p1 + (z - refer); qFrac = parent.p1Frac + zFrac;        //q = parent.p + refer
//          qe = parent.p2 + (ze - refer); qeFrac = parent.p2Frac + zeFrac;  //qe = parent.pe + refer
//        } break;
//        //
//        case (refer<<16) + size: {                         // start relative to parent, end is start + size
//          testCase = 6;
//          if(zeNeg){ 
//            qe = parent.p2 + (z - refer); qeFrac = parent.p2Frac + zFrac; //qe = parent.pe + refer, z is the bottom/right pos 
//            q = qe + (ze - size); qFrac = qeFrac + zeFrac;     //q = qe - size
//          } else {
//            q = parent.p1 + z - refer; qFrac = parent.p1Frac + zFrac;        //q = parent.p + refer
//            qe = q + (ze - size); qeFrac = qFrac + zeFrac;   //qe = q + size
//          }
//        } break;
//        //
//        case (refer<<16) + samesize: {
//          testCase = 7;
//          if( (attr & mBitSizeNeg) !=0){       
//            q = z - (parent.p2 - parent.p1) + ze - samesize; qFrac = zFrac - (parent.p2Frac - parent.p1Frac);
//            qe = parent.p2 + z - refer; qeFrac = zeFrac; 
//          } else {
//            q = parent.p1 + (z - refer); qFrac = parent.p1Frac + zFrac;      //q = parent.p + refer
//            qe = q + (parent.p2 - parent.p1) + (ze - samesize);    //qe = q + lastsize + sizediff 
//            qeFrac = qFrac + (parent.p2Frac - parent.p1Frac) + zeFrac;
//          }
//        } break;
//        //
//        case (next<<16) + refer: {
//          testCase = 8;
//          q = parent.p2 + parent.pb; qFrac = parent.p2Frac + parent.pbf;              //q = parent.pe + parent.pb  the next right/down
//          qe = q + (parent.p2 - parent.p1) + (ze - refer); qeFrac = qFrac + (parent.p2Frac - parent.p1Frac) + zeFrac;     //qe = q + (parent.pe - parent.p) + refer  
//        } break;
//        //
//        case (next<<16) + size: {
//          testCase = 10;
//          //switch(dirNext){
//          switch(parent.dirNext){
//              case 'r': case 'd': {
//              if( (attr & mBitSizeNeg) !=0){ 
//                qe = parent.p2 + parent.pb; qeFrac = parent.p2Frac + parent.pbf; 
//                q = qe - (parent.p2 - parent.p1) + (ze - size); qFrac = qeFrac - (parent.p2Frac - parent.p1Frac) + zeFrac;
//              } else {                               //same as next, refer
//                q = parent.p2 + parent.pb; qFrac = parent.p2Frac + parent.pbf;         //q = parent.pe + parent.pb the next right/down
//                qe = q + ze - size; qeFrac = qFrac + zeFrac; 
//              }
//            } break;
//            //
//            default: {
//              q = parent.p1; qFrac = parent.p1Frac; qe = parent.p2; qeFrac = parent.p2Frac;  //don't change this coordinate. It may be the other one.              
//            }
//          }
//        } break;
//        //
//        case (next<<16) + samesize: {  //z1 is next, ze is samesize, means typical next
//          testCase = 9;
//          switch(parent.dirNext){
//            case 'r': case 'd': {
//              if( (attr & mBitSizeNeg) !=0){ 
//                qe = parent.p2 + (parent.p2 - parent.p1) + parent.pb; qeFrac = parent.p2Frac + (parent.p2Frac - parent.p1Frac) + parent.pbf; 
//                q = qe - (parent.p2 - parent.p1) + (ze - samesize); qFrac = qeFrac - (parent.p2Frac - parent.p1Frac) + zeFrac;
//              } else {                               //same as next, refer
//                q = parent.p2 + parent.pb; qFrac = parent.p2Frac + parent.pbf;         //q = parent.pe + parent.pb the next right/down
//                qe = q + (parent.p2 - parent.p1) + ze - samesize; qeFrac = qFrac + (parent.p2Frac - parent.p1Frac) + zeFrac; 
//              }
//            } break;
//            //
//            default: {
//              q = parent.p1; qFrac = parent.p1Frac; qe = parent.p2; qeFrac = parent.p2Frac;  //don't change this coordinate. It may be the other one.              
//            }
//          }
//        } break;
//        default: 
//          assert(false);
//          testCase = 12;
//          q = z; qFrac = zFrac; 
//          qe = ze; qeFrac = zeFrac;
//      }
      
      if(!(q1 <= q2 || q2 <=0)){
        throw new IllegalArgumentException("start > end " + q1 + " > " + q2);
      }
      if(!(q1 > -1000 && q1 < 1000 && ((q2 > -1000 && q2 < 1000) || ((q2 - useNatSize) >=0 && (q2 - useNatSize) < 8192)))){
        throw new IllegalArgumentException("positions out of range" + q1 + ", " + q2);
      }
      if(q1f >= 20){  //can be on adding distance
        this.p1 = q1 +2; this.p1Frac = q1f -20;
      } else if(q1f >= 10){
          this.p1 = q1 +1; this.p1Frac = q1f -10;
      } else if(q1f < 0){
        this.p1 = q1 - 1; this.p1Frac = q1f +10;
      } else {
        this.p1 = q1; this.p1Frac = q1f;   
      }
      if(q2f >= 20){
        this.p2 = q2 +2; this.p2Frac = q2f -20;
      } else if(q2f >= 10){
        this.p2 = q2 +1; this.p2Frac = q2f -10;
      } else if(q2f < 0){
        this.p2 = q2 - 1; this.p2Frac = q2f +10;
      } else {
        this.p2 = q2; this.p2Frac = q2f;   
      }
      if(!(p1Frac >=0 && p1Frac <=9 && p2Frac >=0 && p2Frac <=9 )){
        throw new IllegalArgumentException("Fractional position failure: " + p1Frac + ", " + p2Frac);
      }
      return this;
    }//set


    /**Copies all values. Hint: clone isn't able to use because the instance in parent is final.
     * @param src
     */
    void set(Coordinate src){
      this.p1 = src.p1; this.p1Frac = src.p1Frac; this.p2 = src.p2; this.p2Frac = src.p2Frac;
      this.pb = src.pb; this.pbf = src.pbf;  
      this.origin = src.origin; this.dirNext = Character.toLowerCase(src.dirNext);
    }
    
    
    ///
    /**Calculate from size to pixel. 
     * 
     */
    void calc(int[] dst, int dparent, int dnat, int xPixelUnit, int[] xPixelFrac){
      int x1, x2;  //begin, end
      int min, max;
      
      //maximum of width
      x1 = xPixelUnit * p1 + xPixelFrac[p1Frac]  //negative if from right
      + (p1 < 0 ? dparent : 0);  //from right
      if(p2 == GralPos.useNatSize){
        x2 = x1 + dnat; 
      } else {
       x2 = xPixelUnit * p2 + xPixelFrac[p2Frac]  //negative if from right
          + (p2 < 0 || p2 == 0 && p2Frac == 0 ? dparent : 0);  //from right
      }
     
      
      if(n1 >=0 && ((1000 * (x2 - x1))/dparent) > (n2 - n1) ){
        //The percent size is less then the maximum size, use it.
        x1 = n1 * dparent; 
        x2 = n2 * dparent;
        max = xPixelUnit * p2;
        min = xPixelUnit * p1;
      }
    }
    
    
    @Override public String toString() {
      return this.p1 + "." + this.p1Frac + ".." + this.p2 + "." + this.p2Frac;
    }
  }
  
  
  public static void testScanSize(){
    GralPos posParent = new GralPos();
    GralPos posTest = new GralPos();
    try{
      posParent.setPosition("@3+2, 10+10", null);  //line 3, column 10 + 10
      posTest.setPosition(",+12", posParent);       //line 3, column 20 + 12
      Assert.checkMsg(posParent.y.p1 == 3 && posParent.y.p2 == 5 && posParent.x.p1 == 10 && posParent.x.p2 == 20 , "posParent failure");
      Assert.checkMsg(posTest.y.p1 == 3   && posTest.y.p2 == 5   && posTest.x.p1 == 20   && posTest.x.p2 == 32 , "posTest failure");  //next, size=12
    } catch(ParseException exc) {
      System.err.println("GralPos.testScanSize - error, " + exc.getMessage());
    }
    
  }
}

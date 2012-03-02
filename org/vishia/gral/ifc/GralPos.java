package org.vishia.gral.ifc;

import org.vishia.gral.area9.GuiCallingArgs;
import org.vishia.gral.base.GralGridProperties;
import org.vishia.gral.base.GralPanelContent;

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
   * (see {@link GuiCallingArgs#sSize}) respectively the parameter size of {@link GralGridProperties#GralGridProperties(char size)}.
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
  /**Version and history:
   * <ul>
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
  /**Version, history and license.
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
  public static final int version = 20111001;

  
  /**This adding value applied at any coordinate parameter of any setPosition- method means, that the value is 
   * referred to the position of the previous or given position. The referred value may be given as positive or negative number.
   * Adding this constant a value in range 0x2000 to 0x2fff results.
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
  public final static int size = 0x4000;
  
  
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
  private final static int mBitRel = 0x1, mBitSize = 0x2, mBitRelEnd = 0x4, mBitSizeNeg = 8;
  
  /**Position and mask of bits to designate kind of parameter. See {@link #parameterDesignation} */ 
  private final static int kBitParamDesignator_x = 0, kBitParamDesignator_y = 8, mParamDesignator = 0xff; 
  
  
  /**Syntax of a position. It is
   * <pre>
    "position::= @ [<$?panel> ,]"
  + "     [<?yPosRelative> &+] [<#?yPos>[\\.<#?yPosFrac>]]"
  + "   [ [+] <#-?ySizeDown>[\\.<#?ySizeFrac>]| +* <?yOwnSize> |] "
  + " [ , [<?xPosRelative> &+] [<#?xPos>[\\.<#?xPosFrac>]]"
  + "   [ [+] <#-?xWidth>[\\.<#?xSizeFrac>]| +* <?xOwnSize> |] [ <?xIncr> ++]"
  + " ] :";
   * </pre>
   * The semantic identifier match to the elements in {@link org.vishia.gral.cfg.GralCfgPosition}.
   * */
  public final static String syntaxZbnf = 
    "position::= @ [<$?panel> ,]"
  + "     [<?yPosRelative> &+] [<#?yPos>[\\.<#?yPosFrac>]]"
  + "   [ [+] <#-?ySizeDown>[\\.<#?ySizeFrac>]| +* <?yOwnSize> |] ##| - <#?ySizeUp>|][ <?yIncr> ++]"
  + " [ , [<?xPosRelative> &+] [<#?xPos>[\\.<#?xPosFrac>]]"
  + "   [ [+] <#-?xWidth>[\\.<#?xSizeFrac>]| +* <?xOwnSize> |] [ <?xIncr> ++]"
  + " ] :";
  
  
  
  
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
  
  /**The values for x and y positions. */
  public final Coordinate x = new Coordinate(), y = new Coordinate();
  
  /**The border to the next element. */
  //public int xyBorder, xyBorderFrac;

  /**Origin of widget, use l, m, r for xOrigin and t, m, b for yOrigin. */
  //public char xOrigin, yOrigin;
  
  /**direction of the next element. Use r, d, l, u. */
  //public char dirNext;
  
  /**Relation of x and y left and top to any separation line. 0 - relation to left and top border. */
  //public int xSepLine, ySepLine;
  
  public GralPanelContent panel;
  
  /**Sets all values of this with the values of pos (copy values)
   * @param pos The src pos
   */
  public void set(GralPos pos)
  { x.attr = pos.x.attr; y.attr = pos.y.attr; 
    x.p1 = pos.x.p1; x.p2 = pos.x.p2; y.p1 = pos.y.p1; y.p2 = pos.y.p2;
    x.p1Frac = pos.x.p1Frac; x.p2Frac = pos.x.p2Frac; y.p1Frac = pos.y.p1Frac; y.p2Frac = pos.y.p2Frac;
    x.origin = pos.x.origin; y.origin = pos.y.origin; x.sepLine = pos.x.sepLine; y.sepLine = pos.y.sepLine;
    x.dirNext = pos.x.dirNext; y.dirNext = pos.y.dirNext;
    panel = pos.panel;
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
  public void setPosition(GralPos framePos, float line, float lineEndOrSize, float column, float columnEndOrSize
      , int origin, char direction, float border)
  {
    int[] pos = new int[10];
    frac(line, pos, 0);
    frac(lineEndOrSize, pos, 2);
    frac(column, pos, 4);
    frac(columnEndOrSize, pos, 6);
    frac(border, pos, 8);
    setFinePosition(pos[0], pos[1], pos[2], pos[3], pos[4], pos[5], pos[6], pos[7], origin, direction, pos[8], pos[9], framePos);
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
  
  private void frac(float v, int[] pos, int ix){
    int i, f;
    i = (int)v;
    if((i & mValueNegative) !=0){
      f = (int)((v-i)*10 + 0.5f);
      if( f == 10){ f = 0; }
    } else {
      f = (int)((v - i)*10 + 0.5f);
    }
    if(f < 0){
      i -=1; f +=10;
    }
    assert(f >=0 && f < 9);
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
   * @param line y-Position in y-Units, count from top of the box. It is the bottom line of the widget.
   *              It means ypos = 0 is not a proper value. To show a text in the first line, use y=2.
   *              If <0, then the previous position is valid still.
   * @param column x-Position in x-Units, count from left of the box. 
   *              If <0, then the previous position is valid still.
   * @param heigth: The height of the line. If <0, then the param line is the buttom line, 
   *                and (line-height) is the top line. If 0 then the last value of height is not changed. 
   * @param length: The number of columns. If <0, then the param column is the right column, 
   *                and column-length is the left column. If 0 then the last value of length is not changed.
   * @param direction: direction for a next widget, use 'r', 'l', 'u', 'd' for right, left, up, down
   * @param parent 
   */
  public void setFinePosition(int line, int yPosFrac, int ye, int yef
      , int column, int xPosFrac, int xe, int xef, int origin, char direction
      , int border, int borderFrac
      , GralPos parent)
  {
    //
    //
    if(ye == (size -1) && yef == 5)
      stop();
    if(ye == useNatSize)
      stop();
    //
    if(parent == null){ parent = this; }
    if(origin >0 && origin <=9){
      int yOrigin = (origin-1) /3;
      int xOrigin = origin - yOrigin -1; //0..2
      this.x.origin = "lmr".charAt(xOrigin);
      this.y.origin = "tmb".charAt(yOrigin);
    }
    
    y.set(line, yPosFrac, ye, yef, parent.y);
    x.set(column, xPosFrac, xe, xef, parent.x);
    
    if("rl".indexOf(direction)>=0 ){
      this.x.dirNext = direction;
      this.y.dirNext = '.';
      this.x.pb = border;
      this.x.pbf = borderFrac;
      this.y.pb = this.y.pbf = 0;
    } else if("ud".indexOf(direction)>=0 ){
      this.y.dirNext = direction;
      this.x.dirNext = '.';
      this.y.pb = border;
      this.y.pbf = borderFrac;
      this.x.pb = this.x.pbf = 0;
    } else {
      this.x.dirNext = parent.x.dirNext;
      this.y.dirNext = parent.y.dirNext;
      this.x.pb = parent.x.pb; this.x.pbf = parent.x.pbf;
      this.y.pb = parent.y.pb; this.y.pbf = parent.y.pbf;
    }
    assert(x.p1Frac >=0 && x.p1Frac < 10 && y.p1Frac >=0 && y.p1Frac < 10 );
    assert(x.p2Frac >=0 && x.p2Frac < 10 && y.p2Frac >=0 && y.p2Frac < 10 );
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
  

  
  
  
  
  
  public float height()
  { float height;
    if(y.p1 * y.p2 >= 0){
      height = y.p2 - y.p1;
      if((y.attr & mBitSizeNeg)!=0) {
        height = -height;
      }
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
  
  
  
  
  
  
  public GralPos clone(){
    //Hint: Object.clone() can't be used because it clones the references of x and y and not its values. 
    GralPos newObj = new GralPos();
    newObj.x.set(x);
    newObj.y.set(y);
    newObj.panel = panel;
    //try{ newObj = (GralGridPos)super.clone(); 
    //} catch(CloneNotSupportedException exc){ assert(false); }
    return newObj; 
  }
  
  
  
  
  

  
  
  /**Calculates the position and size of a widget from given {@link #posWidget}.
   * @param posWidget The position.
   * @param widthParentPixel The size of the panel, where the widget is member of
   * @param heightParentPixel The size of the panel, where the widget is member of
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

  
  
  
  
  
  /**Use especially for debug.
   * @see java.lang.Object#toString()
   */
  @Override public String toString()
  { return "panel=" + (panel == null ? "?" : panel.toString()) + ", "
    +"line=" + y.p1 + "." + y.p1Frac + ".." + y.p2 + "." + y.p2Frac + " col=" + x.p1 + "." + x.p1Frac + ".." + x.p2 + "." + x.p2Frac + " " + x.dirNext + y.dirNext + y.origin + x.origin;
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
    public int attr;
    
    char origin;
    
    /**direction of the next element. Use r, d, l, u. */
    public char dirNext;

    
    /**Relation of x and y left and top to any separation line. 0 - relation to left and top border. 
     * TODO the sepLine is planned but not used yet. 2012-01-31 */
    public int sepLine;
    /**Relation of xEnd and yEnd right and bottom to any separation line. 
     * 0 - relation to left and top border. 
     * positive Index: separation line with this index is left or top. Typical it may be the same index
     * then for left top position.
     * negative Index: separation line with negate value as index is right or bottom. */
    public int endSepLine;

    /**Sets the new position for this coordinate.
     * @param z1
     * @param z1Frac
     * @param z2
     * @param z2Frac
     * @param parent The refer position. Note that parent may be == this because the new position based on the current.
     */
    public Coordinate set(final int z1, final int z1Frac, final int z2, final int z2Frac, final Coordinate parent)
    {
      /**User final local variable to set p, pf, pe, pef to check whether all variants are regarded. */
      final int q1, q1Frac, q2, q2Frac;

      //check input parameter ze of size and negative size
      
      //The type of input parameter.
      final boolean zNeg =  (z1 & mValueNegative) !=0;
      final int zType = (z1 & mSpecialType) == kSpecialType ? z1 : (z1 + kTypAdd_) & mType_;
      final boolean zeNeg =  (z2 & mValueNegative) !=0;
      final int zeType = (z2 & mSpecialType) == kSpecialType ? z2 : (z2 + kTypAdd_) & mType_;
      final int testCase;
      final int testType = (zType<<16) + zeType;
      if(parent !=this){
        pb = parent.pb; pbf = parent.pbf;
      }
      switch(testType){
        //
        case 0: {
          testCase = 1;
          q1 = z1; q1Frac = z1Frac;                         //q = z
          q2 = z2; q2Frac = z2Frac;                     //qe = ze
        } break;
        //
        case 0 + refer: {
          testCase = 2;                           //q = z
          q1 = z1; q1Frac = z1Frac;                         //qe = pe + refer
          q2 = parent.p2 + (z2 - refer); q2Frac = parent.p2Frac + z2Frac;
        } break;
        //
        case 0 + size: {
          testCase = 3;
          if(zeNeg){ 
            q2 = z1; q2Frac = z1Frac;                    //qe = z
            q1 = q2 + (z2 - size); q1Frac = q2Frac + z2Frac;   //q = qe + size
          } else {
            q1 = z1; q1Frac = z1Frac;                       //q = z
            q2 = q1 + (z2 - size); q2Frac = q1Frac +z2Frac;  //qe = q + size
          }
        } break;
        //
        case 0 + samesize: {
          testCase = 4;
          if( (attr & mBitSizeNeg) !=0){     //was the last size negative? the qe is base
            q2 = z1; q2Frac = z1Frac;                     //qe = z
            q1 = q2 - (parent.p2 - parent.p1) + (z2 - samesize);  //q = qe - lastsize + sizediff 
            q1Frac = q2Frac + (parent.p2Frac - parent.p1Frac) + z2Frac;
          } else {
            q1 = z1; q1Frac = z1Frac;                       //q = z 
            q2 = q1 + (parent.p2 - parent.p1) + (z2 - samesize);  //qe = q + lastsize + sizediff 
            q2Frac = q1Frac + (parent.p2Frac - parent.p1Frac) + z2Frac;
          }
        } break;
        //
        case 0 + useNatSize: {
          testCase = 11;
          q1 = z1; q1Frac = z1Frac;
          q2 = z2;  q2Frac = 0; //store useNatSize
        } break;
        //
        case (refer<<16) + 0: {
          testCase = 5;
          q1 = parent.p1 + (z1 - refer); q1Frac = parent.p1Frac + z1Frac;      //q = p + refer 
          q2 = z2; q2Frac = z2Frac;                     //qe = ze
        } break;
        //
        case (refer<<16) + refer: {
          testCase = 1;
          q1 = parent.p1 + (z1 - refer); q1Frac = parent.p1Frac + z1Frac;        //q = parent.p + refer
          q2 = parent.p2 + (z2 - refer); q2Frac = parent.p2Frac + z2Frac;  //qe = parent.pe + refer
        } break;
        //
        case (refer<<16) + size: {
          testCase = 6;
          if(zeNeg){ 
            q2 = parent.p2 + (z1 - refer); q2Frac = parent.p2Frac + z1Frac; //qe = parent.pe + refer, z is the bottom/right pos 
            q1 = q2 + (z2 - size); q1Frac = q2Frac + z2Frac;     //q = qe - size
          } else {
            q1 = parent.p1 + z1 - refer; q1Frac = parent.p1Frac + z1Frac;        //q = parent.p + refer
            q2 = q1 + (z2 - size); q2Frac = q1Frac + z2Frac;   //qe = q + size
          }
        } break;
        //
        case (refer<<16) + samesize: {
          testCase = 7;
          if( (attr & mBitSizeNeg) !=0){       
            q1 = z1 - (parent.p2 - parent.p1) + z2 - samesize; q1Frac = z1Frac - (parent.p2Frac - parent.p1Frac);
            q2 = parent.p2 + z1 - refer; q2Frac = z2Frac; 
          } else {
            q1 = parent.p1 + (z1 - refer); q1Frac = parent.p1Frac + z1Frac;      //q = parent.p + refer
            q2 = q1 + (parent.p2 - parent.p1) + (z2 - samesize);    //qe = q + lastsize + sizediff 
            q2Frac = q1Frac + (parent.p2Frac - parent.p1Frac) + z2Frac;
          }
        } break;
        //
        case (next<<16) + refer: {
          testCase = 8;
          q1 = parent.p2 + parent.pb; q1Frac = parent.p2Frac + parent.pbf;              //q = parent.pe + parent.pb  the next right/down
          q2 = q1 + (parent.p2 - parent.p1) + (z2 - refer); q2Frac = q1Frac + (parent.p2Frac - parent.p1Frac) + z2Frac;     //qe = q + (parent.pe - parent.p) + refer  
        } break;
        //
        case (next<<16) + size: {
          testCase = 10;
          switch(dirNext){
            case 'r': case 'd': {
              if( (attr & mBitSizeNeg) !=0){ 
                q2 = parent.p2 + parent.pb; q2Frac = parent.p2Frac + parent.pbf; 
                q1 = q2 - (parent.p2 - parent.p1) + (z2 - size); q1Frac = q2Frac - (parent.p2Frac - parent.p1Frac) + z2Frac;
              } else {                               //same as next, refer
                q1 = parent.p2 + parent.pb; q1Frac = parent.p2Frac + parent.pbf;         //q = parent.pe + parent.pb the next right/down
                q2 = q1 + z2 - size; q2Frac = q1Frac + z2Frac; 
              }
            } break;
            //
            default: {
              q1 = parent.p1; q1Frac = parent.p1Frac; q2 = parent.p2; q2Frac = parent.p2Frac;  //don't change this coordinate. It may be the other one.              
            }
          }
        } break;
        //
        case (next<<16) + samesize: {
          testCase = 9;
          switch(dirNext){
            case 'r': case 'd': {
              if( (attr & mBitSizeNeg) !=0){ 
                q2 = parent.p2 + (parent.p2 - parent.p1) + parent.pb; q2Frac = parent.p2Frac + (parent.p2Frac - parent.p1Frac) + parent.pbf; 
                q1 = q2 - (parent.p2 - parent.p1) + (z2 - samesize); q1Frac = q2Frac - (parent.p2Frac - parent.p1Frac) + z2Frac;
              } else {                               //same as next, refer
                q1 = parent.p2 + parent.pb; q1Frac = parent.p2Frac + parent.pbf;         //q = parent.pe + parent.pb the next right/down
                q2 = q1 + (parent.p2 - parent.p1) + z2 - samesize; q2Frac = q1Frac + (parent.p2Frac - parent.p1Frac) + z2Frac; 
              }
            } break;
            //
            default: {
              q1 = parent.p1; q1Frac = parent.p1Frac; q2 = parent.p2; q2Frac = parent.p2Frac;  //don't change this coordinate. It may be the other one.              
            }
          }
        } break;
        default: 
          assert(false);
          testCase = 12;
          q1 = z1; q1Frac = z1Frac; 
          q2 = z2; q2Frac = z2Frac;
      }
      
      assert(q1 < q2 || q2 <=0);
      assert(q1 > -1000 && q1 < 1000 && ((q2 > -1000 && q2 < 1000) || ((q2 - useNatSize) >=0 && (q2 - useNatSize) < 8192)));
      if(q1Frac >= 10){
        this.p1 = q1 +1; this.p1Frac = q1Frac -10;
      } else if(q1Frac < 0){
        this.p1 = q1 - 1; this.p1Frac = q1Frac +10;
      } else {
        this.p1 = q1; this.p1Frac = q1Frac;   
      }
      if(q2Frac >= 10){
        this.p2 = q2 +1; this.p2Frac = q2Frac -10;
      } else if(q2Frac < 0){
        this.p2 = q2 - 1; this.p2Frac = q2Frac +10;
      } else {
        this.p2 = q2; this.p2Frac = q2Frac;   
      }
      assert(p1Frac >=0 && p1Frac <=9 && p2Frac >=0 && p2Frac <=9 );
      return this;
    }//set


    /**Copies all values. Hint: clone isn't able to use because the instance in parent is final.
     * @param src
     */
    private void set(Coordinate src){
      p1 = src.p1; p1Frac = src.p1Frac; p2 = src.p2; p2Frac = src.p2Frac;
      pb = src.pb; pbf = src.pbf; attr = src.attr; origin = src.origin; dirNext = src.dirNext;
      sepLine = src.sepLine; endSepLine = src.endSepLine;
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
    
  }
  
}

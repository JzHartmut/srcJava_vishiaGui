package org.vishia.gral.base;

import org.vishia.gral.ifc.GralRectangle;

/**This is a special panel, enhances the {@link GralPanelContent}, with up to 9 areas for other panels or widgets.
 * <pre>
 * +=======+===============+===========+
 * |  A1   |       A2      |    A3     | 
 * +-------+---------------+-----------+ 
 * |  B1   |       B2      |    B3     | 
 * |       |               |           | 
 * +-------+---------------+-----------+ 
 * |  C1   |       C2      |    C3     | 
 * |       |               |           | 
 * +=======+===============+===========+
 * </pre>
 * The areas can be combined for one widget (usual a {@link GralTextBox} or a sub panel) for example in the form:
 * <pre>
 * +=======+===========================+
 * |       |                           | 
 * | tree  |     edit area,            | 
 * | view  |     tables, etc           | 
 * |       |                           | 
 * +-------+---------------------------+ 
 * |    output console text            | 
 * |                                   | 
 * +=======+===============+===========+
 * </pre>
 * The separation lines between the areas are movable with mouse.
 * <br>
 * To place widgets the {@link GralPos} for contents should be set with <code>"@area9,A1A2"</code> for example for the tree view area.
 * or alternatively (here for the edit area) via <code>pos.setPositionSize(0, 1, 2, 2, 'r', pos)</code> 
 * with numeric values 0..2 for the areas.
 * @author Hartmut Schorrig
 *
 */
public class GralArea9Panel  extends GralPanelContent //implements GralPanel_ifc, GralWidget_ifc, GralVisibleWidgets_ifc{
{

  
  /**Version, history and license.
   * <ul>
   * <li>2022-11-12 created following the concept idea of the older {@link org.vishia.gral.area9.GralArea9MainCmd}.
   *   The last one should be removed in future. <br>
   *   Why this is better: <ul>
   *   <li>This class can used whenever a panel with areas are necessary also as sub panel, not only for the whole window.
   *   <li>The GralArea9MainCmd is over-engineered, contains to much, especially the older {@link org.vishia.mainCmd.MainCmd}
   *     which was one of the first ideas in my Java programming.
   *   </ul>
   *   This GralArea9Panel is more simple and lightweight. 
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
  @SuppressWarnings("hiding") public static final int version = 20221112;

  
  
  /**All main components of the Display in each FrameArea. */
  //protected GralWidget[][] componentFrameArea = new GralPanelContent[3][3];

  
  /**A little control to capture the mouse position for movement of area borders. */
  //private GralWidgetImplWrapper[] yAreaMover = new GralWidgetImplWrapper[2];
  

  
  /**Position of the FrameArea borders in percent. 
   * [0] is always 0, [1] and [2] are the given borders, [3] is always 100.
   * It is because lower and higher bound should be accessed always without tests.
   * Use area +1, because it is a Off-by-one problem */
  protected short xpFrameArea[] = new short[4],
               ypFrameArea[] = new short[4];
  
  /**spread of each frame area in x direction.
   * It it is -1, than the area is occupied by another area.
   * If it is 0, the area is free.
   * 1..3 are the number of areas in horizontal direction.
   */
  //protected byte[][][] dxyFrameArea = new byte[3][3][2]; 
  
  /**requested minimal size of each window area in GralPos units. If the minimal value is 0, 
   * the size is calculated from percent always. If the minimal value of percent calculation is lesser
   * than this value, this value is used. Note that a given minimal value of all areas cannot required
   * if the window's size is lesser. Then the middle area will 
   */
  private final short[] xMinGralSize = new short[3], yMinGralSize = new short[3]; 
  
  /**requested maximal size of each window area in GralPos units. If the maximal value is greater than 
   * the size calculated from percent always, the percent value is used. 
   * Note that a given maximal value of all areas cannot regarded
   * if the window's size is greater. Then the middle area will be presented  
   */
  private final short[] xMaxGralSize = new short[3], yMaxGralSize = new short[3];

  public GralArea9Panel ( GralPos currPos, String posName, GralMng gralMng ) {
    super(currPos, posName, gralMng);
    for(int ix = 0; ix < 3; ++ix) for(int iy = 0; iy < 3; ++iy){
      this.xMinGralSize[ix] = 4;
      this.yMinGralSize[iy] = 4;
      this.xMaxGralSize[ix] = Short.MAX_VALUE;
      this.yMaxGralSize[iy] = Short.MAX_VALUE;
    }
    setFrameAreaBorders(30,70,100, 30, 70, 100);
  }

  
  /**Sets the divisions of the frame. The frame is divide into 9 parts,
   * where two horizontal and two vertical lines built them:
   * <pre>
   * +=======+===============+===========+
   * |  A1   |       B1      |    C1     | 
   * +-------+---------------+-----------+ 
   * |  A1   |       B2      |    C2     | 
   * |       |               |           | 
   * +-------+---------------+-----------+ 
   * |  A3   |       B3      |    C3     | 
   * |       |               |           | 
   * +=======+===============+===========+
   * </pre>
   * 
   * @param x1p percent from left for first vertical divide line.
   * @param x2p percent from left for second vertical divide line.
   * @param y1p percent from left for first horizontal divide line.
   * @param y2p percent from left for first horizontal divide line.
   */
  public void setFrameAreaBorders(int x1p, int x2p, int xRange, int y1p, int y2p, int yRange)
  { this.xpFrameArea[0] = 0;
    this.xpFrameArea[1] = (short)x1p;
    this.xpFrameArea[2] = (short)x2p;
    this.xpFrameArea[3] = (short)xRange;
    this.ypFrameArea[0] = 0;
    this.ypFrameArea[1] = (short)y1p;
    this.ypFrameArea[2] = (short)y2p;
    this.ypFrameArea[3] = (short)yRange;
    validateFrameAreas();
  }
  
  

  
  public void XXXaddFrameArea(String sArea, GralWidget component) {
    
  }
//  throws IndexOutOfBoundsException {
//    GralRectangle r = convertArea(sArea);
//    addFrameArea(r.x, r.y, r.dx, r.dy, component);
//  }
 
  
  /**Sets a Component into a defined area. See {@link #setFrameAreaBorders(int, int, int, int)}.
   * It should be called only in the GUI-Thread.
   * @param xArea 1 to 3 for left, middle, right
   * @param yArea 1 to 3 for top, middle, bottom
   * @param dxArea 1 to 3 for 1 field to 3 fields to right.
   * @param dyArea 1 to 3 for 1 field to 3 field to bottom
   * @param component The component.
   * @throws IndexOutOfBoundsException if the arguments are false or the area is occupied already.
   */
//  public final void addFrameArea(int xArea, int yArea, int dxArea, int dyArea, GralWidget component)
//  throws IndexOutOfBoundsException
//  { //int idxArea = (x -1) + 3 * (y -1);
//    //Composite component = new Composite(swtWindow.graphicThreadSwt, SWT.NONE);
//    if(  xArea <1 || xArea > componentFrameArea[0].length
//      || dxArea < 1
//      || xArea+dxArea-2 > componentFrameArea[0].length
//      || yArea <1 || yArea > componentFrameArea.length
//      || dyArea < 1
//      || yArea+dyArea-1 > componentFrameArea.length
//      ) 
//      throw new IndexOutOfBoundsException("failed argument");
//    for(int idxArea = xArea-1; idxArea <= xArea + dxArea -2; idxArea++)
//    { for(int idyArea = yArea-1; idyArea <= yArea + dyArea -2; idyArea++)
//      { if(dxyFrameArea[idyArea][idxArea][0] != 0) throw new IndexOutOfBoundsException("area occupied already");
//      }
//    }
//    for(int idxArea = xArea-1; idxArea <= xArea + dxArea -2; idxArea++)
//    { for(int idyArea = yArea-1; idyArea <= yArea + dyArea -2; idyArea++)
//      { dxyFrameArea[idyArea][idxArea][0] = -1; //ocuupy it.
//      }
//    }
//    dxyFrameArea[yArea-1][xArea-1][0] = (byte)dxArea;
//    dxyFrameArea[yArea-1][xArea-1][1] = (byte)dyArea;
//    //JScrollPane scrollPane = new JScrollPane(component);
//    //scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//    //scrollPane.setViewportView(component);
//    componentFrameArea[yArea-1][xArea-1] = component; //scrollPane;
//    //setBoundsForFrameArea(xArea-1, yArea-1);
//    //scrollPane.validate();
//    //swtWindow.graphicThreadSwt.add(component); //scrollPane);
//    /*TODO
//      ((Control)component.getWidgetImplementation()).addMouseListener(mouseListener);
//      if(yAreaMover[1] == null){
//        yAreaMover[1] = new Canvas(swtWindow.graphicThreadSwt.windowSwt, SWT.None);
//        yAreaMover[1].setSize(10,10);
//        yAreaMover[1].setBackground(swtWindow.graphicThreadSwt.displaySwt.getSystemColor(SWT.COLOR_GREEN));
//      }
//    */
//    validateFrameAreas();
//    //return component;
//  }

  
  
  public void setMinMaxSizeArea(String sArea, int yMinUnit, int yMaxUnit, int xMinUnit, int xMaxUnit)
  throws IndexOutOfBoundsException {
    GralRectangle r = convertArea(sArea);
    this.yMinGralSize[r.y -1] = (short)yMinUnit;
    this.yMaxGralSize[r.y -1] = yMaxUnit <=0 ? Short.MAX_VALUE : (short)yMaxUnit;
    this.xMinGralSize[r.x -1] = (short)xMinUnit;
    this.xMaxGralSize[r.x -1] = xMaxUnit <=0 ? Short.MAX_VALUE : (short)xMaxUnit;
    
    validateFrameAreas();
  }

  
  /**Set the bounds of all areas.
   * Calls {@link GralWindow#getPixelPositionSize()} to get the size of working area of the whole window
   * 
   */
  protected void validateFrameAreas()
  {
    
  }

  
  /**Converts a string given are designation to indices.
   * @param area A, B , C for column, 1, 2, 3 for row.
   *   Write "A1" or "1A" for a one-area rectangle, or "A1C2" or also "1A2C" for a comprehensive area.
   * @return Rectangle describes the usage of the area, it contains indices from 1..3.
   */
  protected GralRectangle convertArea(String area)
  { int x1,x2,y1,y2;
    
    x1 = "ABC".indexOf(area.charAt(0));
    if(x1 < 0){x1 = "ABC".indexOf(area.charAt(1));}
    y1 = "123".indexOf(area.charAt(0));
    if(y1 < 0){y1 = "123".indexOf(area.charAt(1));}
    if(area.length() >=2){
      x2 = "ABC".indexOf(area.charAt(2));
      if(x2 < 0){x2 = "ABC".indexOf(area.charAt(3));}
      y2 = "123".indexOf(area.charAt(2));
      if(y2 < 0){y2 = "123".indexOf(area.charAt(3));}
    } else {
      x2 = x1; y2 = y1;
    }
    GralRectangle ret = new GralRectangle(x1+1, y1+1, x2-x1+1, y2-y1+1);
    return ret;
  }



  
}

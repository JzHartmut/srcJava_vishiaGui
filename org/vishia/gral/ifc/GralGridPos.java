package org.vishia.gral.ifc;

import org.vishia.gral.base.GralPanelContent;

/**This class describes a position in a grid panel. 
 * @author Hartmut Schorrig
 *
 */
public class GralGridPos implements Cloneable
{
  /**Version and history:
   * <ul>
   * <li>2011-10-01 Hartmut corr: Calculation of next position or refer + value if the size was negative and sameSize is selected.
   *                Then the new input value should calculate from the bottom or left value because the size is negative furthermore.
   * <li>2011-10-01 Hartmut bugfix: if(qf >= 10)... instead >10 
   * <li>2011-09-23 Hartmut chg: The methods {@link #setPosition(GralGridPos, float, float, float, float, int, char)} etc
   *     are moved from the GralGridMngBase to this. It are methods of this class functionally. The GralGridMngBase wrappes it
   *     because that methods should be able to call there.
   * <li>2011-08-31 Hartmut new: constants {@link #same} etc. as adding values for setPosition-methods.
   *     It prevents the necessity of a lot of special set methods. The parameter for positions may be relative, referred etc.
   *     to the previous position or to a frame.
   * <li>2011-08-31 Hartmut new: method {@link #toString()} to see values of instance in debug
   * <li>2011-08-14 Hartmut new: creation of this class. Beforehand this values are stored inside the GralGridMngBase as main position.
   *     But a position in this kind is necessary in other contexts too, and the position values should be pooled in one class.                       
   * </ul>
   */
  public final static int version = 0x20111001;
  
  /**This adding value applied at any coordinate parameter of any setPosition- method means, that the value is 
   * referred to the position of the previous or given position. The referred value may be given as positive or negative number.
   * Adding this constant a value in range 0x2000 to 0x2fff results.
   * Hint: If only the fractional part is changed, the non-fractional part should be given as refer.
   */
  public final static int refer = 0x2800;
  
  /**This value applied at any coordinate parameter of any setPosition- method means, that the value is 
   * the same as the previous or given position.
   * Hint: The constant is equal to {@link #refer}. This constant regards the semantic.
   */
  public final static int same = 0x2800;
  
  /**Use the next value for the coordinate in relation to the last one for the given direction
   * but the same value for the other coordinate. It is the typical value for both coordinates
   * if a quasi-float layout is desired. 
   * This value can be applied to x, xEnd, y, yEnd or to the float given arguments of any setPosition- method. 
   * It uses xEnd for x and yEnd for y with the adequate fractional parts. 
   * The new value for xEnd or yEnd is calculated using the size if this value is applied to them. 
   */
  public final static int next = 0x7ffe;
  
  /**Use the next value for the coordinate in relation to the last one. 
   * This value can be applied to x, xEnd, y, yEnd or to the float given arguments of any setPosition- method. 
   * It uses xEnd for x and yEnd for y with the adequate fractional parts. 
   * The new value for xEnd or yEnd is calculated using the size if this value is applied to them. 
   */
  public final static int nextBlock = 0x7ffd;
  
  
  /**This bit at xEnd or yEnd means, that the native size of a widget should be used.
   * It is especially to draw images with its native size.
   * This is a bit mask. The nature size is stored in the bits 14..0, 
   */
  public final static int useNatSize = 0x7fff;
  
  
  /**This bit at all coordinates means, that the value is given as ratio from the size.
   * The bit {@link useNatSize} have to be 0. The ratio is stored as a value from 0 to 999
   * in the bits 9..0. The other bits should be 0.
   */
  public final static int ratio = 0x5000;
  
  
  /**This adding value at xEnd or yEnd or the float presentations of the calling argument of any
   * setPosition- method means, that the value is the size, not the position.
   * A size can be positive or negative. A negative size determines, that the origin point for
   * further elements or inner elements is on bottom line or right line of the current widget.
   */
  public final static int size = 0x1800;
  
  
  /**Use the same size.
   * 
   */
  public final static int samesize = 0x4000;
  
  /**Range of size. The size range of any size value is from 0x1001 to 0x1fff for negative and positive size values.
   * The user should add only + GralgridPosition.size to a positive or negative value in range of 
   * -2000 to 2000. This value is used only iternally to test. */
  private final static int mValueRange_ = 0x7ff;
  
  /**Mask to check ratio. This bits should be mask and the ratio value should compare with them. */
  private final static int mCheckRatio_ = 0xf000;
  
  /**Mask for value of natural size. The maximum size is 16383 pixel. */
  private final static int mNatSize_ = 0x3fff;
  
  /**Mask bits to designate related position, related end position and size.
   * 
   */
  private final static int mBitRel = 0x1, mBitSize = 0x2, mBitRelEnd = 0x4, mBitSizeNeg = 8;
  
  /**Position and mask of bits to designate kind of parameter. See {@link #parameterDesignation} */ 
  private final static int kBitParamDesignator_x = 0, kBitParamDesignator_y = 8, mParamDesignator = 0xff; 
  
  
  /**The Property for the input parameter to use same, next etc. 
   * This value is used to generate an adequate config file from given input values.
   * The coordinates don't carry the information about that input values.
   */
  public int parameterDesignation;
  
  /**Position of any widget.
   * Generally: There are coordinates in a grid, not in pixel. 
   * Positive value is from top or left, negative value is from right or bottom.
   * {@link GralGridPos#useNatSize} on xEnd, yEnd means, that the natural size of the object should be used.
   * 
   */
  public int x, xEnd, y, yEnd;

  
  
  /**Fractional part of position.
   * Generally: It is a number from 0 to 9 as part of 1 grid unit.
   */
  public int xFrac, xEndFrac, yFrac, yEndFrac;

  /**Origin of widget, use l, m, r for xOrigin and t, m, b for yOrigin. */
  public char xOrigin, yOrigin;
  
  /**direction of the next element. Use r, d, l, u. */
  public char dirNext;
  
  /**Relation of x and y left and top to any separation line. 0 - relation to left and top border. */
  public int xSepLine, ySepLine;
  /**Relation of xEnd and yEnd right and bottom to any separation line. 
   * 0 - relation to left and top border. 
   * positive Index: separation line with this index is left or top. Typical it may be the same index
   * then for left top position.
   * negative Index: separation line with negate value as index is right or bottom. */
  public int xEndSepLine, yEndSepLine;

  public GralPanelContent panel;
  
  /**Sets all values of this with the values of pos (copy values)
   * @param pos The src pos
   */
  public void set(GralGridPos pos)
  { parameterDesignation = pos.parameterDesignation;
    x = pos.x; xEnd = pos.xEnd; y = pos.y; yEnd = pos.yEnd;
    xFrac = pos.xFrac; xEndFrac = pos.xEndFrac; yFrac = pos.yFrac; yEndFrac = pos.yEndFrac;
    xOrigin = pos.xOrigin; yOrigin = pos.yOrigin; xSepLine = pos.xSepLine; ySepLine = pos.ySepLine;
    dirNext = pos.dirNext;
    panel = pos.panel;
  }
  
  
  
  public void setPosition(float line, float column)
  {
    setPosition(this, line, size + same, column, size + same, 0, '.');
  }
  
  
  
  
  /* (non-Javadoc)
   * @see org.vishia.gral.gridPanel.GuiPanelMngBuildIfc#setPosition(int, int, int, int, char)
   */
  public void setPositionSize(int line, int column, int height, int width, char direction, GralGridPos posFrame)
  { setFinePosition(line, 0, height + GralGridPos.size, 0, column, 0, width + GralGridPos.size, 0, 1, direction, posFrame);
  }

  

  
  
  
  public void setPosition(GralGridPos framePos, float line, float lineEndOrSize, float column, float columnEndOrSize
      , int origin, char direction)
  {
    int y1 = (int)(line);
    float f = (lineEndOrSize - y1)* 10 + 0.5f;
    int y1f = y1 >=0 ? (int)((line - y1)* 10.001F) : (int)((line - y1)* -10.001F);  
    int y2 = (int)(lineEndOrSize);
    f = (lineEndOrSize - y2)* 10 + 0.5f;
    int y2f = y2 >=0 ? (int)(f) : (int)(-f);  
    int x1 = (int)(column);
    int x1f = x1 >=0 ? (int)((column - x1)* 10.001F) : (int)((column - x1)* -10.001F);  
    int x2 = (int)(columnEndOrSize);
    int x2f = x2 >=0 ? (int)((columnEndOrSize - x2)* 10.001F) : (int)((columnEndOrSize - x2)* -10.001F); 
    setFinePosition(y1, y1f, y2, y2f, x1, x1f, x2, x2f, origin, direction, framePos);
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
   * @param frame 
   */
  public void setFinePosition(int line, int yPosFrac, int ye, int yef
      , int column, int xPosFrac, int xe, int xef, int origin, char direction, GralGridPos frame)
  {
    //
    //
    if(ye == (size -1) && yef == 5)
      stop();
    if(ye == useNatSize)
      stop();
    //
    if(frame == null){ frame = this; }
    Calc calc = new Calc();
    if(origin >0 && origin <=9){
      int yOrigin = (origin-1) /3;
      int xOrigin = origin - yOrigin -1; //0..2
      this.xOrigin = "lmr".charAt(xOrigin);
      this.yOrigin = "tmb".charAt(yOrigin);
    }
    //calculate y
    int paramDesg1;
    calc.p = frame.y; calc.pe = frame.yEnd; calc.pf = frame.yFrac; calc.pef = frame.yEndFrac;
    calc.pd = frame.height(); calc.pDir = "ud".indexOf(frame.dirNext); calc.pOrigin = "tmb".indexOf(frame.yOrigin);
    calc.paramDesg = (parameterDesignation >> kBitParamDesignator_y) & mParamDesignator;
    calc.calc(line, yPosFrac, ye, yef);
    paramDesg1 = calc.paramDesg << kBitParamDesignator_y;
    this.y = calc.p; this.yEnd = calc.pe; this.yFrac = calc.pf; this.yEndFrac = calc.pef;
    this.yOrigin = "tmb".charAt(calc.pOrigin); if(calc.pDir >=0){ this.dirNext = calc.pDir == 0 ? 'u': 'd'; }
    //calculate x
    calc.p = frame.x; calc.pe = frame.xEnd; calc.pf = frame.xFrac; calc.pef = frame.xEndFrac;
    calc.pd = frame.width(); calc.pDir = "lr".indexOf(frame.dirNext); calc.pOrigin = "lmr".indexOf(frame.xOrigin);
    calc.paramDesg = (parameterDesignation >> kBitParamDesignator_x) & mParamDesignator;
    calc.calc(column, xPosFrac, xe, xef);
    this.parameterDesignation = calc.paramDesg << kBitParamDesignator_x | paramDesg1;
    this.x = calc.p; this.xEnd = calc.pe; this.xFrac = calc.pf; this.xEndFrac = calc.pef;
    this.xOrigin = "lmr".charAt(calc.pOrigin); if(calc.pDir >=0){ this.dirNext = calc.pDir == 0 ? 'l': 'r'; }
    
    if("rlud".indexOf(direction)>=0 ){
      this.dirNext = direction;
    }
    assert(xFrac >=0 && xFrac < 10 && yFrac >=0 && yFrac < 10 );
    assert(xEndFrac >=0 && xEndFrac < 10 && yEndFrac >=0 && yEndFrac < 10 );
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
    if(height >0){ this.yOrigin = 't'; }
    else if(height < 0){ this.yOrigin = 'b'; }
    else; //let it unchanged if height == 0
    if(width >0){ this.xOrigin = 'l'; }
    else if(width < 0){ this.xOrigin = 'r'; }
    else; //let it unchanged if width == 0
  }
  
  
  public void setSize(float height, float width, GralGridPos frame)
  { 
    int y2 = (int)(height);
    int y2f = y2 >=0 ? (int)((height - y2)* 10.001F) : (int)((height - y2)* -10.001F);  
    int x2 = (int)(width);
    int x2f = x2 >=0 ? (int)((width - x2)* 10.001F) : (int)((width - x2)* -10.001F); 
    setFinePosition(GralGridPos.next, 0,  y2 + GralGridPos.size, y2f, GralGridPos.next, 0, x2 + GralGridPos.size, x2f, 0, this.dirNext, frame);
  }
  
  
  /**Sets the position to the next adequate the {@link #pos.dirNext}. */
  public void setNextPosition()
  {
    float dx3 = this.width();
    float dy3 = this.height();
    int dx = (int)dx3;
    int dxf = (int)((dx3 - dx) * 10.001F) + this.xEndFrac;
    if(dxf >= 10){ dxf -=10; dx +=1; }
    int dy = (int)dy3;
    int dyf = (int)((dy3 - dy) * 10.001F) + this.yEndFrac;
    if(dyf >= 10){ dyf -=10; dy +=1; }
    switch(this.dirNext){
    case 'r': this.x = this.xEnd; this.xFrac = this.xEndFrac; this.xEnd = this.x + dx; this.xEndFrac = dxf; break;
    case 'd': this.y = this.yEnd; this.yFrac = this.yEndFrac; this.yEnd = this.y + dy; this.yEndFrac = dyf; break;
    }
  }
  

  
  
  
  
  
  public float height()
  { float height;
    if(y * yEnd >= 0){
      height = yEnd - y;
      if((parameterDesignation & mBitSizeNeg)!=0) {
        height = -height;
      }
    }
    else{ 
      height = 2.0F;  //not able to determine, use default.
    }
    height += (yEndFrac - yFrac) * 0.1F;
    return height;
  }
  
  
  public float width()
  { float width;
    if(x > 0 && xEnd > 0){ width = xEnd - x + (xEndFrac - xFrac) * 0.1F; }
    else if(x < 0 && xEnd < 0){ width = xEnd - x + (xEndFrac - xFrac) * 0.1F; }
    else { width = 0.0F; } //not able to determine, use default.
    return width;
  }
  
  
  public GralGridPos clone(){ 
    GralGridPos newObj = null;
    try{ newObj = (GralGridPos)super.clone(); 
    } catch(CloneNotSupportedException exc){ assert(false); }
    return newObj; 
  }
  
  /**Use especially for debug.
   * @see java.lang.Object#toString()
   */
  @Override public String toString()
  { return "panel=" + (panel == null ? "?" : panel.toString()) + ", "
    +"line=" + y + "." + yFrac + ".." + yEnd + "." + yEndFrac + " col=" + x + "." + xFrac + ".." + xEnd + "." + xEndFrac + " " + dirNext + yOrigin + xOrigin;
  }

  
  //Inner class to calculate for x and y.   
  class Calc{
    /**Values from parent or frame. */
    int p, pf, pe, pef;
    int paramDesg;
    float pd;
    int pDir;  //0 for up or left, 1 for down or right, -1 else 
    int pOrigin;
    
    void calc(int z, int zf, final int ze, final int zef)
    {
      /**User final local variable to set p, pf, pe, pef to check whether all variants are regarded. */
      final int q, qf, qe, qef;
      int qParamDesg = 0;
      final boolean bxSizeNeg;
      final int pSize = p >=0 && pe >=0 ? pe - p:
                        p <0 && pe <0 ? p - pe:
                        -1;
      //check the ranges of input parameter. There are added constants, see parameter of setPosition.
      //check z: set z to the absolute value, positive or negative.
      int pRefer, pfRefer;
      if((paramDesg & mBitSizeNeg)!=0){ 
        pRefer = pe;  pfRefer = pef;    //size was negative: refer from end if size is given.
      } else { pRefer = p;  pfRefer = pf; }
      
      if(  z > (GralGridPos.same - GralGridPos.mValueRange_)
        && z < (GralGridPos.same + GralGridPos.mValueRange_)
        ){
        //related value to frame or previous position
        qParamDesg |= mBitRel;
        //add parent position:
        z -= same;
        //NOTE: q, qe will be set after ze is checked.
      } else if( z == next || z == nextBlock){
        if(this.pDir == 1){
          z = pRefer + pSize;
        } else { z = pRefer; }
      }
      //check ze: set the final positions q...
      if(  ze > (GralGridPos.same - GralGridPos.mValueRange_)
        && ze < (GralGridPos.same + GralGridPos.mValueRange_)
        ){ 
        //related end value to frame or previous position
        qParamDesg |= mBitRelEnd;
        //regard maybe related position in z,zf for start. 
        if((qParamDesg & mBitRel)!=0){
          q = z + p; qf = zf + pf;
        } else {
          q = z; qf = zf;
        }
        qe = ze-same + pe; qef = zef + pf; 
        /*
        if((paramDesg & mBitSizeNeg) != 0){ 
          qe = z; qef = zf;   //use the left/top position like given.
          q = p + ze; qf = pf + zef;  //use the end position like given.
        } else {
          q = z; qf = zf;   //use the left/top position like given.
          qe = pe + ze; qef = pef + zef;  //use the end position like given.
        }
        */
      } else if(  ze > (GralGridPos.size - GralGridPos.mValueRange_)
               && ze < (GralGridPos.size + GralGridPos.mValueRange_)
               ){
        //size value
        qParamDesg |= mBitSize;
        if((qParamDesg & mBitRel)!=0){  //The z parameter was detected as refer (same)
          z = pRefer + z;     //use the pRefer instead p or pe 
          zf = pfRefer + zf;  //frac part always positive.
        } 
        if(bxSizeNeg = (ze < size)){
          qParamDesg |= mBitSizeNeg;
          ///
          //ze = -ze;  //positive
          q = z + ze -size; qf = zf + zef;  //the left/top is the given position - size
          qe = z; qef = zf;           //the end position is the given position.
        } else {
          q = z; qf = zf;
          qe = z + ze-size; qef = zf + zef;
        }
        //
      } else if(  ze > (GralGridPos.size + GralGridPos.same- GralGridPos.mValueRange_)
               && ze < (GralGridPos.size + GralGridPos.same + GralGridPos.mValueRange_)
               ){
        //size value related to frame or previous size
        if((qParamDesg & mBitRel)!=0){
          z = pRefer + z;
          zf = pfRefer + zf;  //frac part always positive.
        }
        qParamDesg |= mBitSize + mBitRelEnd;
        bxSizeNeg = (this.paramDesg & mBitSizeNeg) !=0;  //the referred pos has negative size
        if(bxSizeNeg){
          qParamDesg |= mBitSizeNeg;
          ///
          q = z - ze -(size+same) + pSize; qf = zf - zef;  //the left/top is the given position - size
          qe = z; qef = zf;           //the end position is the given position.
        } else {
          q = z; qf = zf;
          qe = z + ze; qef = zf + zef;
        }
        
      } else {
        //without special designatin, use the position like given
        q = z; qf = zf;
        qe = ze; qef = zef;
      }
      
      if(qf >= 10){
        p = q +1; pf = qf -10;
      } else if(qf < 0){
        p = q - 1; pf = qf +10;
      } else {
        p = q; pf = qf;   
      }
      if(qef >= 10){
        pe = qe +1; pef = qef -10;
      } else if(qef < 0){
        pe = qe - 1; pef = qef +10;
      } else {
        pe = qe; pef = qef;   
      }
      this.paramDesg = qParamDesg;
      if(pOrigin <0){ pOrigin = 0; } //set default if not determined. 
    }//calc
  }//class Calc method-local

  
  
  void stop(){}

  
  
  
}

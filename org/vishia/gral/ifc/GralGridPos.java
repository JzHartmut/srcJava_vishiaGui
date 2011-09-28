package org.vishia.gral.ifc;

import org.vishia.gral.base.GralPanelContent;

/**This class describes a position in a grid panel. 
 * @author Hartmut Schorrig
 *
 */
public class GralGridPos implements Cloneable
{
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
  private final int mBitRel = 0x1, mBitSize = 0x2, mBitRelEnd = 0x4, mBitSizeNeg = 8;
  
  /**Position and mask of bits to designate kind of parameter. See {@link #parameterDesignation} */ 
  private final int kBitParamDesignator_x = 0, kBitParamDesignator_y = 8, mParamDesignator = 0xff; 
  
  
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
  
  public void set(GralGridPos pos)
  {
    x = pos.x; xEnd = pos.xEnd; y = pos.y; yEnd = pos.yEnd;
    xFrac = pos.xFrac; xEndFrac = pos.xEndFrac; yFrac = pos.yFrac; yEndFrac = pos.yEndFrac;
    xOrigin = pos.xOrigin; yOrigin = pos.yOrigin; xSepLine = pos.xSepLine; ySepLine = pos.ySepLine;
    dirNext = pos.dirNext;
    
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
    int y1f = y1 >=0 ? (int)((line - y1)* 10.001F) : (int)((line - y1)* -10.001F);  
    int y2 = (int)(lineEndOrSize);
    int y2f = y2 >=0 ? (int)((lineEndOrSize - y2)* 10.001F) : (int)((lineEndOrSize - y2)* -10.001F);  
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
    if(y >= 0 && yEnd > 0){ height = yEnd - y + (yEndFrac - yFrac) * 0.1F; }
    else if(y < 0 && yEnd <= 0){ height = yEnd - y + (yEndFrac - yFrac) * 0.1F; }
    else { height = 2.0F; } //not able to determine, use default.
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
  { return "line:" + y + "." + yFrac + ".." + yEnd + "." + yEndFrac + " col:" + x + "." + xFrac + ".." + xEnd + "." + xEndFrac + " " + dirNext + yOrigin + xOrigin;
  }

  
  //Inner class to calculate for x and y.   
  class Calc{
    /**Values from parent or frame. */
    int p, pf, pe, pef;
    int paramDesg;
    float pd;
    int pDir;  //0 for up or left, 1 for down or right, -1 else 
    int pOrigin;
    
    void calc(int z, int zf, int ze, int zef)
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
      if(  z > (GralGridPos.same - GralGridPos.mValueRange_)
        && z < (GralGridPos.same + GralGridPos.mValueRange_)
        ){
        //related value to frame or previous position
        qParamDesg |= mBitRel;
        //add parent position:
        z -= same;
        //NOTE: q, qe will be set after ze is checked.
        if(z >=0){
          z += p;
        } else {
          z = pe + z;  //negative: refer from end.  
        }
        zf += pf;  //frac part always positive.
      } else if( z == next || z == nextBlock){
        if(this.pDir == 1){
          z = p + pSize;
        } else {
          z = p;
        }
      }
      //check ze: set the final positions q...
      if(  ze > (GralGridPos.same - GralGridPos.mValueRange_)
        && ze < (GralGridPos.same + GralGridPos.mValueRange_)
        ){ 
        //related end value to frame or previous position
        qParamDesg |= mBitRelEnd;
        ze -= same;
        q = z; qf = zf;   //use the left/top position like given.
        qe = pe + ze; qef = pef + zef;  //use the end position like given.
      } else if(  ze > (GralGridPos.size - GralGridPos.mValueRange_)
               && ze < (GralGridPos.size + GralGridPos.mValueRange_)
               ){
        //size value
        qParamDesg |= mBitSize;
        ze -= size;
        if(bxSizeNeg = (ze <0)){
          qParamDesg |= mBitSizeNeg;
          ///
          ze = -ze;  //positive
          q = z - ze; qf = zf - zef;  //the left/top is the given position - size
          qe = z; qef = zf;           //the end position is the given position.
        } else {
          q = z; qf = zf;
          qe = z + ze; qef = zf + zef;
        }
        //
      } else if(  ze > (GralGridPos.size + GralGridPos.same- GralGridPos.mValueRange_)
               && ze < (GralGridPos.size + GralGridPos.same + GralGridPos.mValueRange_)
               ){
        //size value related to frame or previous size
        qParamDesg |= mBitSize + mBitRelEnd;
        ze -= size + same;
        ze += pSize;  //the size
        bxSizeNeg = (this.paramDesg & mBitSizeNeg) !=0;  //the referred pos has negative size
        if(bxSizeNeg){
          qParamDesg |= mBitSizeNeg;
          ///
          q = z - ze; qf = zf - zef;  //the left/top is the given position - size
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
      /*
      if(bxSame){ 
        z -= GralGridPos.same; //may be negative!
      }
      if(bSameSize){
        qSameNextSizeProperty = same + size;
        ze -= GralGridPos.size +  GralGridPos.same; //may be negative!
        bxSizeNeg = pe < 0;  //use negative size form parent or frame.
        ze = pe + ze;        //but increment the size with given relative size.
        zef = pef + zef;
        if(bxSizeNeg){ 
          if(pOrigin <0){ pOrigin = 2; }
        } else {
          if(pOrigin <0){ pOrigin = 0; }
        }
      } else if(bxSize){
          qSameNextSizeProperty = size;
          ze -= GralGridPos.size; //may be negative!
          bxSizeNeg = ze < 0;
          if(bxSizeNeg){ 
            ze =  -ze;
            if(pOrigin <0){ pOrigin = 2; }
          } else {
            if(pOrigin <0){ pOrigin = 0; }
          }
      } else if(bxSameEnd){ 
          ze -= GralGridPos.same; //may be negative!
        bxSizeNeg = false;
      } else {
        bxSizeNeg = false;
      }
      
      final boolean bColumnFromRight = z < 0;
      if(bColumnFromRight){ z = - z; } //use positive values anytime
      if(bxSame){
        //don't change column
        if(bxSameEnd){
          q = p + z; qf = pf + zf; 
          qe = pe + ze; qef = pef + zef;
          //if(qef >=10){ qef -=10; qe+=1; }
        } else if(bxSize){
          //The size is changed but the same column
          if(bxSizeNeg){
            if(pef >= zef){
              q = pe - ze; qf = pef - zef;
            } else {
              q = pe - ze -1; qf = 10 + pef - zef;
            }
            qe = pe; qef = pef;
            //if(pf <0){ p -=1; pf +=10; }
          } else {
            q = p + z; qf = pf + zf; 
            qe = p + ze; qef = pf + zef;
            //if(pef >=10){ pe +=1; pef -=10; }
          }
        } else { //an end position is given
          q = p + z; qf = pf + zf; 
          qe = ze; qef = zef;
        }
        //if(pf >=10){ pf -=10; p+=1; }
      } else if(z == GralGridPos.next && pDir==1 || z == GralGridPos.nextBlock){
        //calculate next x
        if(bxSameEnd || ze == GralGridPos.next || ze == GralGridPos.nextBlock){ 
          //calculate next position, don't change end column
          int xd = (int)pd;
          zef = (int)((pd - xd)* 10.001F) + pef;
          
          q = pe; qf = pef;
          if(zef >= 10){ zef -=10; xd +=1; }
          qe = p + xd;
          qef = zef;
        } else if(bxSize){
          //calculate next position, size is given:
          q = pe; qf = pef;  //set start to previous end.
          zef += pef; if(zef >=10){ zef -=10; ze +=1; }
          qe = p + ze; qef = zef;  //set end to newstart + size
        } else { 
          //calculate next position, end position is given:
          q = pe; qf = pef;
          qe = ze; qef = zef;
        }
      } else { //position z is given:
        if(z == GralGridPos.next){  //next is given, but not for this coordinate: 
          if(bxSizeNeg){
            z = pe; zf = pef;     //use the actual value.
          } else {
            z = p; zf = pf;       //use the actual value.
          }
        }
        //position is given or next is set but not in this direction:
        if(bxSameEnd || ze == GralGridPos.next || ze == GralGridPos.nextBlock){ 
          //don't change end position
          q = z; qf = zf;
          qe = pe; qef = pef;
        } else if(bxSize){
          if(bxSizeNeg){ //the given position is the right or button one:
            if(zf >= zef){
              q = z - ze; qf = zf - zef;
            } else {
              q = z - ze -1; qf = zf - zef + 10;
            }
            //if(pf < 0){ pf +=10; p -=1; }
            qe = z; qef = zf;  //the end position is the given one.
          } else { //the given position is the left one:
            q = z; qf = zf; 
            qe = z + ze; qef = zf + zef;
            //if(pef >=10){ pef -=10; pe +=1; }
          } 
        } else if(bColumnFromRight){
          q = -z; qf = zf;      //Note: values may be negative then calculate pixel from right or bottom border. 
          qe = ze; qef = zef;
          
        } else { //column and end column is given:
          q = z; qf = zf;      //Note: values may be negative then calculate pixel from right or bottom border. 
          qe = ze; qef = zef;
        }
      }
      */
      
      if(qf > 10){
        p = q +1; pf = qf -10;
      } else if(qf < 0){
        p = q - 1; pf = qf +10;
      } else {
        p = q; pf = qf;   
      }
      if(qef > 10){
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

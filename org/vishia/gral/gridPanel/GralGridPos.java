package org.vishia.gral.gridPanel;

/**This class describes a position in a grid panel. 
 * @author Hartmut Schorrig
 *
 */
public class GralGridPos implements Cloneable
{
  /**This adding value applied at any coordinate of any setPosition- method means, that the value is 
   * relative to the current position. The relative value may be given as positive or negative number.
   * Adding this constant a value in range 0x2000 to 0x2fff results.
   * Hint: If only the fractional part is changed, the non-fractional part should be given too with its correct value.
   */
  public final static int same = 0x2800;
  
  /**Use the next value for the coordinate in relation to the last one for the given direction
   * but the same value for the other coordinate. It is the typical value for both coordinates
   * if a quasi-float layout is desired. 
   * This value can be applied to x, xEnd, y, yEnd or to the float given arguments of any setPosition- method. 
   * It uses xEnd for x and yEnd for y with the adequate fractional parts. 
   * The new value for xEnd or yEnd is calculated using the size if this value is applied to them. 
   */
  public final static int next = 0x3ffe;
  
  /**Use the next value for the coordinate in relation to the last one. 
   * This value can be applied to x, xEnd, y, yEnd or to the float given arguments of any setPosition- method. 
   * It uses xEnd for x and yEnd for y with the adequate fractional parts. 
   * The new value for xEnd or yEnd is calculated using the size if this value is applied to them. 
   */
  public final static int nextBlock = 0x3ffd;
  
  
  /**This bit at xEnd or yEnd means, that the native size of a widget should be used.
   * It is especially to draw images with its native size.
   * This is a bit mask. The nature size is stored in the bits 14..0, 
   */
  public final static int useNatSize = 0x4000;
  
  
  /**This bit at all coordinates means, that the value is given as ratio from the size.
   * The bit {@link useNatSize} have to be 0. The ratio is stored as a value from 0 to 999
   * in the bits 9..0. The other bits should be 0.
   */
  public final static int ratio = 0x3000;
  
  
  /**This adding value at xEnd or yEnd or the float presentations of the calling argument of any
   * setPosition- method means, that the value is the size, not the position.
   * A size can be positive or negative. A negative size determines, that the origin point for
   * further elements or inner elements is on bottom line or right line of the current widget.
   */
  public final static int size = 0x1800;
  
  /**Range of size. The size range of any size value is from 0x1001 to 0x1fff for negative and positive size values.
   * The user should add only + GralgridPosition.size to a positive or negative value in range of 
   * -2000 to 2000. This value is used only iternally to test. */
  public final static int sizeRange_ = 0x7ff;
  
  /**Mask to check ratio. This bits should be mask and the ratio value should compare with them. */
  public final static int mCheckRatio_ = 0xf000;
  
  /**Mask for value of natural size. The maximum size is 16383 pixel. */
  final static int mNatSize_ = 0x3fff;
  
  
  /**Position of any widget.
   * Generally: There are coordinates in a grid, not in pixel. 
   * Positive value is from top or left, negative value is from right or bottom.
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

 
  
  public void set(GralGridPos pos)
  {
    x = pos.x; xEnd = pos.xEnd; //etc TODO
  }
  
  
  
  
  
  
  public float height()
  { float height;
    if(y > 0 && yEnd > 0){ height = yEnd - y + (yEndFrac - yFrac) * 0.1F; }
    else if(y < 0 && yEnd < 0){ height = yEnd - y + (yEndFrac - yFrac) * 0.1F; }
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
  
  
}

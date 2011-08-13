package org.vishia.gral.gridPanel;

/**This class describes a position in a grid panel. 
 * @author Hartmut Schorrig
 *
 */
public class GralGridPosition implements Cloneable
{
  /**Position of any widget.
   * Generally: There are coordinates in a grid, not in pixel. 
   * Positive value is from top or left, negative value is from right or bottom.
   */
  public int x, xEnd, y, yEnd;

  /**Fractional part of position.
   * Generally: It is a number from 0 to 9 as part of 1 grid unit.
   */
  public int xFrac, xEndFrac, yFrac, yEndFrac;

  /**Relation of x and y left and top to any separation line. 0 - relation to left and top border. */
  public int xSepLine, ySepLine;
  /**Relation of xEnd and yEnd right and bottom to any separation line. 
   * 0 - relation to left and top border. 
   * positive Index: separation line with this index is left or top. Typical it may be the same index
   * then for left top position.
   * negative Index: separation line with negate value as index is right or bottom. */
  public int xEndSepLine, yEndSepLine;
  
  public void set(GralGridPosition pos)
  {
    x = pos.x; xEnd = pos.xEnd; //etc TODO
  }
  
  
  public GralGridPosition clone(){ 
    GralGridPosition newObj = null;
    try{ newObj = (GralGridPosition)super.clone(); 
    } catch(CloneNotSupportedException exc){ assert(false); }
    return newObj; 
  }
  
}

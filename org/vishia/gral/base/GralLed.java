package org.vishia.gral.base;

import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.util.ObjectValue;

public abstract class GralLed extends GralWidget
{
  /**Version and history
   * <ul>
   * <li>2012-04-01 new: {@link #colorBorder}, {@link #colorBorderSelectable}, {@link #setValue(Object[])}
   * <li>2011-12-03 new Baseclass for LED visualization.
   *   It is the concept of specialized {@link GralWidget}.
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL ist not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   * 
   */
  @SuppressWarnings("hiding")
  static final public int version = 0x20111203;
  
  /**Some given colors which can be selected by value calling {@link #setValue(Object[])}
   * or {@link #setValue(float)}.
   * 
   */
  private GralColor[] colorBorderSelectable, colorInnerSelectable;
  
  
  /**The colors which are used for repaint (if not null). */
  protected GralColor colorBorder, colorInner;
  
  protected GralLed(String name, GralWidgetMng mng)
  { super(name, 'D', mng);
    colorBorderSelectable = new GralColor[2];
    colorBorderSelectable[0] = GralColor.getColor("ye");
    colorBorderSelectable[1] = GralColor.getColor("gn");
    colorInnerSelectable = new GralColor[2];
    colorInnerSelectable[0] = GralColor.getColor("wh");
    colorInnerSelectable[1] = GralColor.getColor("gn");
  }

  public abstract void XXXsetColor(int nBorderColor, int nInnerColor);

  @Override public void setValue(Object[] values){
    if(values.length >=2){
      int val1 = ObjectValue.getInt(values[0]);
      int val2 = ObjectValue.getInt(values[1]);
      if(val1 >= colorBorderSelectable.length){ val1 = colorBorderSelectable.length -1; }
      if(val2 >= colorInnerSelectable.length){ val2 = colorInnerSelectable.length -1; }
      colorBorder = colorBorderSelectable[val1];
      colorInner = colorInnerSelectable[val1];
      repaint(repaintDelay, repaintDelayMax);
    }
  }
}

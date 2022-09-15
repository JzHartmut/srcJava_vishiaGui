package org.vishia.gral.base;

import org.vishia.gral.ifc.GralColor;
import org.vishia.util.ObjectValue;

public class GralLed extends GralWidget
{
  /**Version and history
   * <ul>
   * <li>2016-11-22 bugfix: {@link #setValue(Object[])} with negative input causes an exception. Now a 3. color magenta was shown to see that failure. 
   * <li>2014-10-12 chg: now instantiable. New concept. 
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
  private final GralColor[] colorBorderSelectable, colorInnerSelectable;
  
  
  /**The colors which are used for repaint (if not null). */
  //protected GralColor colorBorder, colorInner;
  
  /**
   * @param name
   * @param gralMng
   */
  public GralLed(GralPos currPos, String sPosName)
  { super(currPos, sPosName, 'D');
    colorBorderSelectable = new GralColor[3];
    colorBorderSelectable[0] = GralColor.getColor("ye");
    colorBorderSelectable[1] = GralColor.getColor("gn");
    colorBorderSelectable[2] = GralColor.getColor("ma");  //faulty showing color
    colorInnerSelectable = new GralColor[3];
    colorInnerSelectable[0] = GralColor.getColor("wh");
    colorInnerSelectable[1] = GralColor.getColor("gn");
    colorInnerSelectable[2] = GralColor.getColor("ma");   //faulty showing color
    setValue(0);  //initializes dyda.colors
  }

  public GralLed(GralPos currPos, String sPosName, GralMng mng) {
    this(currPos, sPosName);
  }
  
  public GralLed(String name)
  { this(null, name);
  }
  
  
  
  /**Sets the LED's color. The border can be another than the inner color.
   * The effect of {@link #setValue(Object[])} is independent from this method.
   * @param colorBorder assign to {@link GralWidget#setLineColor(GralColor, int)} or {@link GralWidget.DynamicData#lineColor}. 
   * @param colorInner assign to {@link GralWidget#setBackColor(GralColor, int)} {@link GralWidget.DynamicData#backColor}.
   */
  public void setColor(GralColor colorBorder, GralColor colorInner){
    dyda.lineColor = colorBorder;
    dyda.backColor = colorInner;
    repaint(repaintDelay, repaintDelayMax);
  }

  /**Sets the color both inner and border with the given value
   * using the {@link #colorInnerSelectable}
   * @see org.vishia.gral.base.GralWidget#setValue(float)
   */
  @Override public void setValue(float value){
    int ival = (int)value;
    if(ival < 0){ ival = 0; }
    else if(ival >= colorInnerSelectable.length){ ival = colorInnerSelectable.length -1;}
    dyda.lineColor = dyda.backColor = colorInnerSelectable[ival];
    dyda.setChanged(ImplAccess.chgColorBack | ImplAccess.chgColorLine);
    super.setValue(value);  //stores and calls repaint.
  }
  
  
  /**This method is invoked if more as one variable is assigned to the widget.
   * It sets the line and the back color which is the inner color (back) and border (line).
   * The values should be integer-readable. 
   * @see org.vishia.gral.base.GralWidget#setValue(java.lang.Object[])
   */
  @Override public void setValue(Object[] values){
    if(values.length >=2){
      int val1 = ObjectValue.getInt(values[0]); //The index to the color
      int val2 = ObjectValue.getInt(values[1]);
      if(val1 >= colorBorderSelectable.length || val1 <0){ val1 = colorBorderSelectable.length -1; } //faulty index, use faulty showing color.
      if(val2 >= colorInnerSelectable.length || val2 <0){ val2 = colorInnerSelectable.length -1; }
      dyda.lineColor = colorBorderSelectable[val1];
      dyda.backColor = colorInnerSelectable[val2];
      dyda.setChanged(ImplAccess.chgColorBack | ImplAccess.chgColorLine);
      super.setValue(values);  //stores and calls repaint.
      //repaint(repaintDelay, repaintDelayMax);
    }
  }
  
  
  
  public abstract class GraphicImplAccess extends GralWidget.ImplAccess
  implements GralWidgImpl_ifc
  {

    
    protected GraphicImplAccess(GralWidget widgg, GralMng mng)
    {
      super(widgg, mng);
    }
    
    protected GralWidget.DynamicData dyda(){ return dyda; }
  }    

}

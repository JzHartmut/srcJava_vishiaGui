package org.vishia.gral.widget;

import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWidgImpl_ifc;
import org.vishia.gral.base.GralWidget;

public class GralLabel extends GralWidget
{
  /**Use 1..9 for
   * 
   * 
   */
  int origin;

  public GralLabel(String sName)
  {
    super(sName, 'S');
  }
  
  public GralLabel(String sName, String sText, int origin)
  {
    super(sName, 'S');
    this.origin = origin;
    super.setText(sText);
  }
  
  
  public GralLabel(String pos, String sName, String sText, int origin)
  {
    super(pos,sName, 'S');
    this.origin = origin;
    super.setText(sText);
  }
  
  
  public abstract class GraphicImplAccess extends GralWidget.ImplAccess
  implements GralWidgImpl_ifc
  {
    protected GraphicImplAccess(GralWidget widgg, GralMng mng)
    {
      super(widgg, mng);
    }
    
    protected int origin(){ return origin; }

  }
  
}

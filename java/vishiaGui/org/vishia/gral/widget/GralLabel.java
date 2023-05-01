package org.vishia.gral.widget;

import java.io.IOException;

import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidgImplAccess_ifc;
import org.vishia.gral.base.GralWidget;

public class GralLabel extends GralWidget
{
  /**Use 1..9 for
   * 
   * 
   */
  int origin;

  public GralLabel(GralPos currPos, String sText)
  {
    this(currPos, null, sText, 0);
  }

//  public GralLabel(String sName) {
//    this(null, sName);
//  }
  
  public GralLabel(GralPos currPos, String sPosName, String sText, int origin)
  {
    super(currPos, sPosName, 'S');
    this.origin = origin;
    super.setText(sText);
  }
  
//  public GralLabel(String sName, String sText, int origin) {
//    this(null, sText, sName, sText, origin);
//  }
  
//  public GralLabel(GralPos currPos, String poName, String sText, int origin)
//  {
//    super(currPos, posName, 'S');
//    this.origin = origin;
//    super.setText(sText);
//  }
  


//  public GralLabel(String posName, String sText, int origin) {
//    this(null, pos, sName, sText, origin);
//  }

  
  @Override public String toString() {
    StringBuilder b = new StringBuilder();
    super._wdgPos.toString(b, "p").append(" Text:").append(getText());
    return b.toString();
  }
  
  
  public abstract class GraphicImplAccess extends GralWidget.ImplAccess
  implements GralWidgImplAccess_ifc
  {
    protected GraphicImplAccess(GralWidget widgg, GralMng mng)
    {
      super(widgg, mng);
    }
    
    protected int origin(){ return origin; }

  }
  
}

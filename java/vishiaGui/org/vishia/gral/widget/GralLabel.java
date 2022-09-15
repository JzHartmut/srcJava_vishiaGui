package org.vishia.gral.widget;

import java.io.IOException;

import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidgImpl_ifc;
import org.vishia.gral.base.GralWidget;

public class GralLabel extends GralWidget
{
  /**Use 1..9 for
   * 
   * 
   */
  int origin;

  public GralLabel(GralPos currPos, String sName)
  {
    super(currPos, sName, 'S');
  }

  public GralLabel(String sName) {
    this(null, sName);
  }
  
  public GralLabel(GralPos currPos, String sName, String sText, int origin)
  {
    super(currPos, sName, 'S');
    this.origin = origin;
    super.setText(sText);
  }
  
  public GralLabel(String sName, String sText, int origin) {
    this(null, sText, sName, sText, origin);
  }
  
  public GralLabel(GralPos currPos, String pos, String sName, String sText, int origin)
  {
    super(currPos, pos,sName, 'S');
    this.origin = origin;
    super.setText(sText);
  }
  

  public GralLabel(String pos, String sName, String sText, int origin) {
    this(null, pos, sName, sText, origin);
  }

  
  @Override public String toString() {
    StringBuilder b = new StringBuilder();
    try { super._wdgPos.toString(b, true).append(" Text:").append(getText());
    } catch(IOException exc) { throw new RuntimeException("unexpected: ", exc); };
    return b.toString();
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

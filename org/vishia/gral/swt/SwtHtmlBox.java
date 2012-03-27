package org.vishia.gral.swt;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.vishia.gral.base.GralHtmlBox;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;

public class SwtHtmlBox extends GralHtmlBox
{
  
  Browser boxSwt;
  
  String lastUrl, lastUrlOk;
  
  public boolean bActiv;
  
  public SwtHtmlBox(String name, GralWidgetMng mng)
  {
    super(name, mng);
    SwtMng mngSwt = (SwtMng)mng;
    Composite parent = (Composite)(mng.pos.panel.getPanelImpl());
    try {
      boxSwt = new Browser(parent,0);
      mngSwt.setPosAndSizeSwt(boxSwt, 0, 0);
    } catch(Throwable exc){
      System.err.println("can't create SWT-Browser");
      exc.printStackTrace(System.err);
    }
    bActiv = false;
  }

  @Override public void setUrl(String url){
    //this.w
    lastUrl = url;
    if(bActiv){
      boolean bOk = boxSwt.setUrl(url);
      if(bOk){
        lastUrlOk = url;
      } else {
        boxSwt.setUrl(lastUrlOk);
      }
    }
  }

  
  
  @Override public void activate(){
    if(boxSwt !=null){
      bActiv = true;
      setUrl(lastUrl);
    }
  }
  

  
  
  @Override
  protected void removeWidgetImplementation()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean setFocus()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override protected void repaintGthread(){
    if(boxSwt !=null){ boxSwt.redraw(); }
  }


  
  @Override
  public Object getWidgetImplementation()
  {
    // TODO Auto-generated method stub
    return boxSwt;
  }

  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBoundsPixel(int x, int y, int dx, int dy)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public GralWidgetGthreadSet_ifc getGthreadSetifc(){ return gThreadSet; }

  /**Implementation of the graphic thread widget set interface. */
  GralWidgetGthreadSet_ifc gThreadSet = new GralWidgetGthreadSet_ifc(){

    @Override public void clearGthread()
    { // TODO Auto-generated method stub
    }

    @Override public void insertGthread(int pos, Object visibleInfo, Object data)
    { // TODO Auto-generated method stub
    }

    @Override public void redrawGthread()
    { // TODO Auto-generated method stub
    }

    @Override public void setBackGroundColorGthread(GralColor color)
    { // TODO Auto-generated method stub
    }

    @Override public void setForeGroundColorGthread(GralColor color)
    { // TODO Auto-generated method stub
    }

    @Override public void setTextGthread(String text, Object data)
    { // TODO Auto-generated method stub
    }
  };
  
  
  

}

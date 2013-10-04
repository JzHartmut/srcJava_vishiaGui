package org.vishia.gral.awt;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;

import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;

public class AwtSubWindow extends GralWindow
{
  
  protected final Frame window;

  public AwtSubWindow(String name, Frame window, String title, int windStyle, GralMng gralMng)
  {
    super(name, title, windStyle, gralMng, window);
    this.window = window;
    // TODO Auto-generated constructor stub
  }

  @Override public void setTitle(String sTitle){
    window.setTitle(sTitle);
  }


  
  @Override
  public void setMouseAction(GralUserAction action)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setResizeAction(GralUserAction action)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Object getPanelImpl()
  {
    // TODO Auto-generated method stub
    return window;
  }

  @Override
  public void closeWindow()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public GralRectangle getPixelPositionSize()
  {
    Rectangle r = window.getBounds();
    // TODO Auto-generated method stub
    return new GralRectangle(r.x, r.y, r.height, r.width);
  }


  @Override public GralRectangle getPixelSize(){
    Dimension r = ((Component)panelComposite).getSize();
    GralRectangle posSize = new GralRectangle(0, 0, r.width, r.height);
    return posSize;
  }



  
  
  @Override
  public boolean isWindowsVisible()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void repaint()
  {
    // TODO Auto-generated method stub
    
  }

  
  

  @Override
  public void setWindowVisible(boolean visible)
  {
    // TODO Auto-generated method stub
    
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
  
  
  @Override public void setFullScreen(boolean full){ } //TODO window.setFullScreen(full); }


  @Override
  public boolean setFocusGThread()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void removeWidgetImplementation()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void addMenuItemGThread(String nameWidg, String sMenuPath, GralUserAction action)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void repaintGthread() {
    // TODO Auto-generated method stub
    
  }
  
  
  

  
}

package org.vishia.gral.widget;

import java.io.IOException;

import org.vishia.gral.base.GralSubWindow;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.ifc.GralTextBox_ifc;

/**This class presents a sub window which is used as info box for any messages.
 * @author Hartmut Schorrig
 *
 */
public class InfoBox implements GralTextBox_ifc, GralWindow_ifc
{

  /**The window is created invoking the {@link GralGridBuild_ifc#createWindow(String, boolean)}. 
   * It has its implementation in the underlying graphic system.  */
  private final GralSubWindow window;
  
  
  /**The widget which holds the text in the {@link #window}. */
  private final GralTextBox textBox;
  
  private final GralWidget buttonOk;
  
  public InfoBox(GralSubWindow window, GralTextBox textBox, GralWidget buttonOk)
  {
    this.window = window;
    this.textBox = textBox;
    this.buttonOk = buttonOk;
  }
  
  public static InfoBox create(GralGridBuild_ifc mng, String name, String title)
  {
    GralSubWindow window = mng.createWindow(title, false);
    mng.setPosition(0, -4, 0, 0, 0, '.');
    GralTextBox text = mng.addTextBox(name, false, null, '.');
    mng.setPosition(-4, -1, -6, 0, 0, '.');
    GralWidget buttonOk = mng.addButton(name + "-Info-ok", null, "", null, null, "OK");
    InfoBox box = new InfoBox(window, text, buttonOk);
    box.buttonOk.setActionChange(box.actionOk);
    return box; 

  }
  
  @Override public Appendable append(CharSequence text) throws IOException{ return textBox.append(text); }

  @Override
  public int getNrofLines()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void setText(String text)
  {
    // TODO Auto-generated method stub
  }

  @Override public String getText()
  { return null;
  
  }
  
  @Override public void redraw(){  window.redraw(); }


  
  @Override
  public void viewTrail()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Object getWidgetImplementation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean setFocus()
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
  public boolean isWindowsVisible()
  {
    // TODO Auto-generated method stub
    return false;
  }



  @Override
  public void setWindowVisible(boolean visible)
  {
    window.setWindowVisible(visible);
    // TODO Auto-generated method stub
    
  }

  @Override
  public Appendable append(char arg0) throws IOException
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Appendable append(CharSequence arg0, int arg1, int arg2)
      throws IOException
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  
  @Override public void setResizeAction(GralUserAction action){
    window.setResizeAction(action);
  }
  

  
  GralUserAction actionOk = new GralUserAction(){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params) {
      window.setWindowVisible(false);
      return true;
    }
  };

  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { window.setBoundsPixel(x,y,dx,dy);
  }
  
  
  
  @Override public GralRectangle getPixelPositionSize(){ return window.getPixelPositionSize(); }
    
  @Override public void closeWindow()
  { 
    window.closeWindow();
  }


}

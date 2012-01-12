package org.vishia.gral.widget;

import java.io.IOException;

import org.vishia.gral.base.GralHtmlBox;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
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
  private final GralWindow window;
  
  
  /**The widget which holds the text in the {@link #window}. */
  private final GralTextBox textBox;
  
  private final GralHtmlBox htmlBox;
  
  private final GralWidget buttonOk;
  
  public InfoBox(GralWindow window, GralTextBox textBox, GralWidget buttonOk)
  {
    this.window = window;
    this.textBox = textBox;
    this.htmlBox = null;
    this.buttonOk = buttonOk;
  }
  
  public InfoBox(GralWindow window, GralHtmlBox htmlBox, GralWidget buttonOk)
  {
    this.window = window;
    this.textBox = null;
    this.htmlBox = htmlBox;
    this.buttonOk = buttonOk;
  }
  
  public static InfoBox createTextInfoBox(GralGridBuild_ifc mng, String name, String title)
  {
    GralWindow window = mng.createWindow(name, title, GralWindow.windConcurrently);
    //TODO the position frame (size) regards the title bar, it should not do so!
    mng.setPosition(0, -3, 0, 0, 0, '.');
    GralTextBox text = mng.addTextBox(name, false, null, '.');
    mng.setPosition(-3, 0, -6, 0, 0, '.');
    GralWidget buttonOk = mng.addButton(name + "-Info-ok", null, "", null, null, "OK");
    InfoBox box = new InfoBox(window, text, buttonOk);
    box.buttonOk.setActionChange(box.actionOk);
    return box; 

  }
  
  public static InfoBox createHtmlInfoBox(GralGridBuild_ifc mng, String name, String title)
  {
    GralWindow window = mng.createWindow(name, title, GralWindow.windConcurrently);
    //TODO the position frame (size) regards the title bar, it should not do so!
    mng.setPosition(0, -3, 0, 0, 0, '.');
    GralHtmlBox text = mng.addHtmlBox(name);
    mng.setPosition(-3, 0, -6, 0, 0, '.');
    GralWidget buttonOk = mng.addButton(name + "-Info-ok", null, "", null, null, "OK");
    InfoBox box = new InfoBox(window, text, buttonOk);
    box.buttonOk.setActionChange(box.actionOk);
    return box; 

  }
  
  @Override public void setTextStyle(GralColor color, GralFont font)
  { textBox.setTextStyle(color, font);
  }


  
  @Override public Appendable append(CharSequence text) throws IOException{ return textBox.append(text); }

  @Override
  public int getNrofLines()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void setText(CharSequence text)
  { textBox.setText(text);
  }
  
  public void setUrl(String url){
    if(htmlBox !=null){ 
      htmlBox.setUrl(url); 
    }
    else throw new IllegalArgumentException("it is not a html box."); 
  }
  

  @Override public String getText(){ return textBox.getText(); }
  
  @Override public int getCursorPos(){ return textBox.getCursorPos(); }


  
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
  
  

  
  GralUserAction actionOk = new GralUserAction(){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params) {
      if(textBox !=null) {textBox.setText(""); }  //'I have seen it, therefore delete.
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

  @Override
  public boolean remove()
  {
    // TODO Auto-generated method stub
    return false;
  }


}

package org.vishia.gral.widget;

import java.io.IOException;

import org.vishia.gral.base.GralHtmlBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWindow_setifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWindow_getifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.ifc.GralTextBox_ifc;

/**This class presents a sub window which is used as info box for any messages.
 * @author Hartmut Schorrig
 *
 */
public final class GralInfoBox implements GralTextBox_ifc, GralWindow_setifc, GralWindow_getifc
{

  /**The window is created invoking the {@link GralMngBuild_ifc#createWindow(String, boolean)}. 
   * It has its implementation in the underlying graphic system.  */
  private final GralWindow window;
  
  
  /**The widget which holds the text in the {@link #window}. */
  private final GralTextBox textBox;
  
  private final GralHtmlBox htmlBox;
  
  private final GralWidget buttonOk;
  
  protected final GralTextField infoLine;
  
  public GralInfoBox(GralWindow window, GralTextBox textBox, GralTextField infoLine, GralWidget buttonOk)
  {
    this.window = window;
    this.textBox = textBox;
    this.htmlBox = null;
    this.infoLine = infoLine;
    this.buttonOk = buttonOk;
  }
  
  public GralInfoBox(GralWindow window, GralHtmlBox htmlBox, GralTextField infoLine, GralWidget buttonOk)
  {
    this.window = window;
    this.textBox = null;
    this.htmlBox = htmlBox;
    this.infoLine = infoLine;
    this.buttonOk = buttonOk;
  }
  
  public static GralInfoBox createTextInfoBox(GralMngBuild_ifc mng, String name, String title)
  {
    GralWindow window = mng.createWindow(name, title, GralWindow.windConcurrently);
    //TODO the position frame (size) regards the title bar, it should not do so!
    mng.setPosition(0, -3, 0, 0, 0, '.');
    GralTextBox text = mng.addTextBox(name, false, null, '.');
    mng.setPosition(0, -4, -4, -2, 0, '.');
    GralTextField infoLine = mng.addTextField("info", false, null, null);
    mng.setPosition(-3, 0, -6, 0, 0, '.');
    GralWidget buttonOk = mng.addButton(name + "-Info-ok", null, "", null, null, "OK");
    GralInfoBox box = new GralInfoBox(window, text, infoLine, buttonOk);
    box.buttonOk.setActionChange(box.actionOk);
    return box; 

  }
  
  public static GralInfoBox createHtmlInfoBox(GralMngBuild_ifc mng, String name, String title)
  {
    GralWindow window = mng.createWindow(name, title, GralWindow.windConcurrently);
    //TODO the position frame (size) regards the title bar, it should not do so!
    mng.setPosition(0, -3, 0, 0, 0, '.');
    GralHtmlBox text = mng.addHtmlBox(name);
    mng.setPosition(-2.5f, -0.5f, 0, -7, 0, '.');
    GralTextField infoLine = mng.addTextField("info", false, null, null);
    mng.setPosition(-3, 0, -6, 0, 0, '.');
    GralWidget buttonOk = mng.addButton(name + "-Info-ok", null, "", null, null, "OK");
    GralInfoBox box = new GralInfoBox(window, text, infoLine, buttonOk);
    box.buttonOk.setActionChange(box.actionOk);
    return box; 

  }
  
  
  public void activate(){
    if(htmlBox !=null){ htmlBox.activate(); }
  }
  
  @Override public String getName(){ return textBox.getName(); }
  

  
  
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

  @Override public void setText(CharSequence text)
  { textBox.setText(text);
  }
  
  @Override public void setText(CharSequence text, int caretPos)
  { textBox.setText(text, caretPos);
  }
  
  //@Override public void setSelection(String how){ textBox.setSelection(how); }
  
  
  public void setUrl(String url){
    infoLine.setText(url);
    if(htmlBox !=null){ 
      htmlBox.setUrl(url); 
    }
    else throw new IllegalArgumentException("it is not a html box."); 
  }
  

  @Override public String getText(){ return textBox.getText(); }
  
  @Override public int getCursorPos(){ return textBox.getCursorPos(); }


  
  @Override public void repaintGthread(){  window.repaintGthread(); }


  @Override public void repaint(){ repaint(0,0); }

  @Override public void repaint(int delay, int latest){
    window.repaint(delay, latest);
  }
  
  

  
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
      //if(textBox !=null) {textBox.setText(""); }  //'I have seen it, therefore delete.
      window.setWindowVisible(false);
      return true;
    }
  };

  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { window.setBoundsPixel(x,y,dx,dy);
  }
  
  @Override public GralWidgetGthreadSet_ifc getGthreadSetifc()
  { return textBox.getGthreadSetifc(); }

  

  
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

  @Override
  public void setHtmlHelp(String url)
  {
    if(textBox !=null){ textBox.setHtmlHelp(url); }
    if(htmlBox !=null){ htmlBox.setHtmlHelp(url); }
    infoLine.setText(url);
    //buttonOk.setHtmlHelp(url);
  }


}

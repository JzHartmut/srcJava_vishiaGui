package org.vishia.gral.widget;

import java.io.IOException;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralHtmlBox;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWindow_setifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindow_getifc;
import org.vishia.gral.ifc.GralTextBox_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.KeyCode;

/**This class presents a sub window which is used as info box for any messages.
 * A Text info box and a html info box is supported. The html info box is used especially for help.
 * The HTML text is navigable with internal links, maybe with external too.
 * The HTML text 
 * @author Hartmut Schorrig
 *
 */
public final class GralInfoBox implements GralTextBox_ifc, GralWindow_setifc, GralWindow_getifc
{

  /**Version, history and license.
   * <ul>
   * <li>2013-03-24 Hartmut chg: {@link #createHtmlInfoBox(GralMngBuild_ifc, String, String, boolean)}
   *   with Parameter onTop: Especially a help window should able to stay on top.
   * <li>2011-10-00 Hartmut created
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final int version = 20130324;

  /**The window is created invoking the {@link GralMngBuild_ifc#createWindow(String, boolean)}. 
   * It has its implementation in the underlying graphic system.  */
  private final GralWindow window;
  
  
  /**The widget which holds the text in the {@link #window}. */
  private final GralTextBox textBox;
  
  private final GralHtmlBox htmlBox;
  
  private final GralWidget buttonOk;
  
  private GralButton buttonLock;
  
  Object[] implWidgets = new Object[3];

  
  protected final GralTextField infoLine;
  
  private GralUserAction actionOk;
  
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
    GralWindow window = mng.createWindow(name, title, GralWindow_ifc.windConcurrently);
    //TODO the position frame (size) regards the title bar, it should not do so!
    mng.setPosition(0, -3, 0, 0, 0, '.');
    GralTextBox text = mng.addTextBox(name, false, null, '.');
    mng.setPosition(0, -4, -4, -2, 0, '.');
    GralTextField infoLine = mng.addTextField("info", false, null, null);
    mng.setPosition(-3, 0, -6, 0, 0, '.');
    GralWidget buttonOk = mng.addButton(name + "-Info-ok", null, "OK");
    GralInfoBox box = new GralInfoBox(window, text, infoLine, buttonOk);
    box.buttonOk.setActionChange(box.actionOkButton);
    return box; 

  }
  
  public static GralInfoBox createHtmlInfoBox(GralMngBuild_ifc mng, String name, String title, boolean onTop)
  {
    int props = GralWindow_ifc.windConcurrently | GralWindow_ifc.windResizeable;
    if(onTop){ props |= GralWindow_ifc.windOnTop; }
    GralWindow window = mng.createWindow(name, title, props);
    //TODO the position frame (size) regards the title bar, it should not do so!
    mng.setPosition(0, -3, 0, 0, 0, '.');
    GralHtmlBox text = mng.addHtmlBox(name);
    mng.setPosition(-2.5f, -0.5f, 0, -14, 0, '.');
    GralTextField infoLine = mng.addTextField("info", false, null, null);
    mng.setPosition(-3, GralPos.size+3, -13, GralPos.size+6, 0, 'r', 0.5f);
    GralButton buttonLock = mng.addSwitchButton(name + "-Info-ok", "follow", "lock", GralColor.getColor("wh"), GralColor.getColor("gn"));
    mng.setPosition(-3, GralPos.size+3, -6, GralPos.size+6, 0, 'r', 0.5f);
    GralWidget buttonOk = mng.addButton(name + "-Info-ok", null, "OK");
    GralInfoBox box = new GralInfoBox(window, text, infoLine, buttonOk);
    box.buttonLock = buttonLock;
    box.implWidgets[0] = text.getWidgetImplementation();
    box.implWidgets[1] = infoLine.getWidgetImplementation();
    box.implWidgets[2] = buttonOk.getWidgetImplementation();
    box.buttonOk.setActionChange(box.actionOkButton);
    //mng.registerWidget(box);
    return box; 

  }
  
  public void setActionOk(GralUserAction action){ this.actionOk = action; }
  
  
  public void activate(){
    if(htmlBox !=null){ htmlBox.activate(); }
  }
  
  @Override public String getName(){ return textBox.getName(); }
  

  
  
  @Override public void setTextStyle(GralColor color, GralFont font)
  { textBox.setTextStyle(color, font);
  }

  
  @Override public void setEditable(boolean editable){
    textBox.setEditable(editable);
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
  
  
  @Override public int setCursorPos(int pos){
    if(textBox !=null){
      return textBox.setCursorPos(pos);
    } else {
      return 0;
    }
  }


  
  //@Override public void setSelection(String how){ textBox.setSelection(how); }
  
  
  public void setUrl(String url){
    if(buttonLock == null || !buttonLock.isOn()){
      infoLine.setText(url);
      if(htmlBox !=null){ 
        htmlBox.setUrl(url); 
      }
      else throw new IllegalArgumentException("it is not a html box.");
    }
  }
  
  
  /**Query whether the table line is able to edit: Return from the whole table.
   * @see org.vishia.gral.ifc.GralWidget_ifc#isEditable()
   */
  @Override public boolean isEditable(){ return textBox.isEditable(); }
  
  @Override public boolean isNotEditableOrShouldInitialize(){ return textBox.isNotEditableOrShouldInitialize(); }
  

  

  @Override public boolean isChanged(boolean setUnchanged){ return textBox.isChanged(setUnchanged); }
  
  @Override public String getText(){ return textBox.getText(); }
  
  
  @Override public boolean isVisible(){ return textBox.isVisible(); }
  
  @Override public int getCursorPos(){ return textBox.getCursorPos(); }

  @Override public long setContentIdent(long date){ return textBox.setContentIdent(date); }
  
  @Override public long getContentIdent(){ return textBox.getContentIdent(); }



  
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

  //@Override public Object[] getWidgetMultiImplementations(){ return implWidgets; }
  
  
  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public void setFocus()
  { if(textBox !=null){ textBox.setFocus(); }
    else if(htmlBox !=null){ htmlBox.setFocus();}
  }

  @Override public void setFocus(int delay, int latest)
  { if(textBox !=null){ textBox.setFocus(delay, latest); }
    else if(htmlBox !=null){ htmlBox.setFocus(delay, latest);}
  }

  @Override public void setFullScreen(boolean full){ window.setFullScreen(full); }

  
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
  
  

  
  GralUserAction actionOkButton = new GralUserAction("actionOkButton"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params) {
      //if(textBox !=null) {textBox.setText(""); }  //'I have seen it, therefore delete.
      if(actionOk !=null){ actionOk.exec(KeyCode.enter, widgd); }
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

  
  @Override public void refreshFromVariable(VariableContainer_ifc container){
    if(textBox !=null){ textBox.refreshFromVariable(container); }
    if(htmlBox !=null){ htmlBox.refreshFromVariable(container); }
    
  }

  @Override public void setDataPath(String sDataPath)
  { 
    if(textBox !=null){ textBox.setDataPath(sDataPath); }
    if(htmlBox !=null){ htmlBox.setDataPath(sDataPath); }
  }

  @Override public void setBackColor(GralColor color, int ix)
  { 
    if(textBox !=null){ textBox.setBackColor(color, ix); }
    if(htmlBox !=null){ htmlBox.setBackColor(color, ix); }
  }

  @Override public void setLineColor(GralColor color, int ix)
  { 
    if(textBox !=null){ textBox.setLineColor(color, ix); }
    if(htmlBox !=null){ htmlBox.setLineColor(color, ix); }
  }

  @Override public void setTextColor(GralColor color)
  { 
    if(textBox !=null){ textBox.setTextColor(color); }
    if(htmlBox !=null){ htmlBox.setTextColor(color); }
  }

  @Override public Object getContentInfo(){ return null; }
  
}

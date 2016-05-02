package org.vishia.gral.awt;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.swt.SwtMng;
import org.vishia.util.KeyCode;

/**Implementation of the {@link GralTextField} in awt graphics.
 * The original {@link java.awt.TextField} has a considerable border for a smart outfit. That is not proper for some applications
 * which needs an slim and simple outfit without borders because the smart outfit properties are given outside of the textfield itself.
 * Therefore the functionality of a text field is implemented with a simple canvas. All functions to edit the content with keyboard
 * are implemented here in an independent way of the awt.TextField. The {@link #paint(Graphics)} shows the simple outfit in a special kind.
 * 
 * @author Hartmut Schorrig
 *
 */
public class AwtTextField extends GralTextField.GraphicImplAccess
{
  
    /**Version, history and license.
   * <ul>
   * <li>2015-10-30 Hartmut improved, own implementation with canvas 
   * <li>2011-10-31 Hartmut created.
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
   * <li> But the LPGL is not appropriate for a whole software product,
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
   */
  public final static String sVersion = "2015-10-30";

  
  /**The AWT widget. A TextField is not used because a TextField has a border which is not proper in any case.
   * Therefore the capability of text output and edit is implemented with an own algorithm.
   */
  final TextCanvas widgetAwt;
  
  /**A possible prompt for the text field or null. */
  /*packagePrivate*/ Label promptSwt;
  
  /**It contains the association to the swt widget (Control) and the {@link SwtMng}
   * and implements some methods of {@link GralWidgImpl_ifc} which are delegate from this.
   */
  private final AwtWidgetHelper widgHelper;
  
  StringBuilder editBuffer;
  
  
  public AwtTextField(GralTextField widgg, AwtWidgetMng mng)
  { widgg.super(widgg, mng.mng); //NOTE: superclass is a non static inner class of GralTextField. 
    if(widgg.isEditable()){
      editBuffer = new StringBuilder(20); 
      editBuffer.setLength(0); editBuffer.append(dyda().displayedText);
    }
    GralPos pos = widgg.pos();
    Container panelAwt = (Container)pos.panel.getWidgetImplementation();
    
    widgetAwt = new TextCanvas(); //TextField();
    widgetAwt.setData(widgg);
    widgetAwt.addFocusListener(mng.focusListener);
    widgetAwt.addMouseListener(mng.mouseStdAction);
    widgetAwt.addKeyListener(keyListener);
    //widgetAwt.set
    //widgetAwt.addHierarchyBoundsListener(hierarchyBoundsListener);
    mng.setPosAndSize_(posField, widgetAwt);
    //widgetAwt.setForeground(mng.propertiesGuiAwt.colorAwt(GralColor.getColor("rd")));
    //widgetAwt.setBackground(mng.propertiesGuiAwt.colorAwt(GralColor.getColor("gn")));
    panelAwt.add(widgetAwt);
    widgHelper = new AwtWidgetHelper(widgetAwt, mng);
  }

  @Override
  public void removeWidgetImplementation()
  {
      // TODO Auto-generated method stub
    
  }

  //@Override public int getCursorPos(){ return widgetAwt.getCaretPosition(); }


  @Override
  public Object getWidgetImplementation()
  {
    return widgetAwt;
  }


  
  @Override public boolean setFocusGThread()
  { return AwtWidgetHelper.setFocusOfTabSwt(widgetAwt);
  }


  
  public int setCursorPos(int pos){
    //int oldPos = widgetAwt.getCaretPosition();
    ///widgetAwt.setCaretPosition(pos);
    return 0; //oldPos;
  }


  



  @Override
  public void setBoundsPixel(int x, int y, int dx, int dy){ widgHelper.setBoundsPixel(x, y, dx, dy); }

  
  
  
  
  public void setEditable(boolean editable){
    //widgetAwt.setEditable(editable);
  }



  @Override public void repaintGthread(){
    GralWidget.DynamicData dyda = dyda();
    int chg = dyda.getChanged();
    if((chg & chgText) !=0  && dyda.displayedText !=null){ 
      if(widgg.isEditable()){
        editBuffer.setLength(0); editBuffer.append(dyda().displayedText);
      }
    }
    if((chg & chgEditable) !=0) {
      if(widgg.isEditable()){
        if(editBuffer == null) { editBuffer = new StringBuilder(20); }
        editBuffer.setLength(0); editBuffer.append(dyda().displayedText);
      } else { //not editable
        editBuffer = null;  //garbage it.
      }
    }
    dyda.acknChanged(chg);
    if((chg & chgColorText) !=0){ widgetAwt.setForeground(widgHelper.mng.getColorImpl(dyda.textColor)); }
    if((chg & chgColorBack) !=0){ widgetAwt.setBackground(widgHelper.mng.getColorImpl(dyda.backColor)); }
    widgetAwt.repaint();
  }

  
  
  @Override public GralRectangle getPixelPositionSize()
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  
  
  
  private void processKey(int key){
    int caretPos = caretPos();
    int caretPos1 = caretPos;
    if(KeyCode.isWritingKey(key) && widgg.isEditable()){
      switch(key){
        case KeyCode.del: {
          if(caretPos < editBuffer.length()) {
            editBuffer.deleteCharAt(caretPos);
            caretPos1 = -1; //force repaint
          }
        } break;
        case KeyCode.back: {
          if(caretPos >=1){
            caretPos -=1;
            editBuffer.deleteCharAt(caretPos);
          }
        } break;
        default: {
        }  
        editBuffer.insert(caretPos, (char)key);
        caretPos +=1;
      } //switch
      dyda().displayedText = editBuffer.toString(); //it will be shown, a String is necessary.
    } else {
      switch(key){
        case KeyCode.left: if(caretPos >=1){ caretPos -=1; } break;
        case KeyCode.right: if(caretPos < editBuffer.length()){ caretPos +=1; } break;
      }
    }
    if(caretPos != caretPos1) {
      caretPos(caretPos);  //set new pos.
      widgetAwt.repaint();
    }
  }
  
  
  private void paint(Graphics gc) {
    String text = dyda().displayedText;
    Rectangle r = widgetAwt.getBounds();
    if(text != null) {
      gc.drawString(text, 1, r.height -2);
      if(widgg.isEditable() && widgg.isInFocus()) {
        int caretPos = caretPos();
        int x = 7*caretPos;
        gc.drawLine(x, 1, x, r.height-2);
      }
    }
  }

  HierarchyBoundsListener hierarchyBoundsListener = new HierarchyBoundsListener()
  {

    @Override public void ancestorMoved(HierarchyEvent e)
    {
      System.out.println("AwtField: hierarchy-anchestorMoved");
      
    }

    @Override public void ancestorResized(HierarchyEvent e)
    {
      System.out.println("AwtField: hierarchy-anchestorResized");
      
    }
    
  };


  @SuppressWarnings("serial") 
  class TextCanvas extends Canvas implements AwtWidget {

    Object data;
    
    @SuppressWarnings("synthetic-access")  
    @Override public void paint(Graphics gc) {
      AwtTextField.this.paint(gc);
    }

    @Override public Object getData() { return data; }
  
    @Override public void setData(Object data){ this.data = data; }
    
  }
  
  
 
  KeyListener keyListener = new KeyListener() {

    /**Invoked on character keys
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    @SuppressWarnings("synthetic-access") 
    @Override public void keyTyped(KeyEvent e)
    {
      int key = AwtGralKey.convertFromAwt(e.getKeyCode(), e.getModifiersEx(), e.getKeyChar());
      //processKey(key);
      
    }

    /**Invoked on function keys and on character keys.
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    @SuppressWarnings("synthetic-access") 
    @Override public void keyPressed(KeyEvent e)
    {
      int key = AwtGralKey.convertFromAwt(e.getKeyCode(), e.getModifiersEx(), e.getKeyChar());
      processKey(key);
    }

    @Override public void keyReleased(KeyEvent e)
    {
      // TODO Auto-generated method stub
      
    }
    
  };
  
  
  
  
  
  
  
  
}
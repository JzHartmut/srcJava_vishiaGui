package org.vishia.gral.base;

import java.util.concurrent.atomic.AtomicInteger;

import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindowMng_ifc;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralWidget;

/**This is the base class for all widgets which represents a simple text.
 * @author Hartmut Schorrig
 *
 */
public abstract class GralTextField extends GralWidget implements GralTextField_ifc
{
  /**Version and history
   * <ul>
   * <li>2011-11-18 Hartmut new {@link #setMouseAction(GralUserAction)}. This method should be 
   * an abstract method of all {@link GralWidget} but it is used yet only here.
   * <li>2011-09-00 Hartmut Creation to build a platform-indenpenden representation of text field. 
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
   * <li> But the LPGL ist not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are indent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   * 
   */
  @SuppressWarnings("hiding")
  public final static int version = 0x20111118;
  
  protected String text = "";
  
  protected int caretPos;
  
  protected GralColor colorBack = GralColor.getColor("wh"), colorText = GralColor.getColor("bk");
  
  protected GralFont fontText;
  
  
  protected AtomicInteger whatIsChanged = new AtomicInteger();
  
  protected static final int chgText = 1, chgColorBack=2, chgColorText=4, chgFont = 8;
  
  /**It is used for some operations. */
  protected final GralGraphicThread windowMng;
  
  
  
  public GralTextField(String name, char whatis, GralWidgetMng mng){
    super(name, whatis, mng);
    this.windowMng = mng.gralDevice;
  }
  
  
  @Override public void setText(CharSequence arg)
  { setText(arg, 0);
  }
  
  @Override public void setText(CharSequence arg, int caretPos)
  {
    text = arg.toString();
    this.caretPos = caretPos;
    int yet = whatIsChanged.get();
    int catastrophicCount = 0;
    while( !whatIsChanged.compareAndSet(yet, yet | chgText)){ 
      if(++catastrophicCount > 10000) throw new RuntimeException("");
    }
    if(Thread.currentThread().getId() == windowMng.getThreadIdGui()){
      repaintGthread();
    } else {
      repaint(100,0);
    }
  }
  
  

  
  /**Sets the action which is invoked while a mouse button is pressed or release on this widget.
   * Implementation hint: It installs a mouse listener.
   * TODO: use GralMouseWidgetAction_ifc instead GralUserAction, use another action for mouse than change.
   */
  abstract public void setMouseAction(GralUserAction action);
  
  
  @Override public String getText(){ return text; }
   


  
  
  /**Returns the Label for a prompt or null if there isn't used a prompt
   */
  abstract public String getPromptLabelImpl();
  
}

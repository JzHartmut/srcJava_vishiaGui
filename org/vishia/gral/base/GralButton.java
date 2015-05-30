package org.vishia.gral.base;

import java.lang.ref.Reference;

import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralImageBase;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.KeyCode;

public class GralButton extends GralWidget
{
  /**Version, history and license.
   * <ul>
   * <li>2015-05-31 Hartmut new: Now a key listener and a traverse listener was added for the SWT implementation.
   *   This base class provides the {@link #activate()} method which should be invoked on ENTER-key.
   * <li>2013-12-22 Hartmut chg: Now {@link GralButton} uses the new concept of instantiation: It is not
   *   the super class of the implementation class. But it provides {@link GralButton.GraphicImplAccess}
   *   as the super class. 
   * <li>2013-05-23 Hartmut new color of text able to change (not only black)
   * <li>2013-05-23 Hartmut chg: {@link #setState(State)} with an enum, not int, used for showing active/inactive
   * <li>2012-08-22 Hartmut new implements {@link #setBackColor(GralColor, int)}
   * <li>2012-03-09 Hartmut new Some functionality from SwtButton moved to this, some enhancement of 
   *   functionality: Three states, 
   * <li>2011 Hartmut created
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
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   * 
   */
  public final static int version = 0x20120303;

  
  //final GralWindowMng_ifc mainWindow;
  
  protected boolean pressed;
  
  /**The state of switch and check button
   * 1=on 2=off 3=disabled. 0=initial, see {@link #kOn} etc.
   */
  //protected int switchState;
  //protected boolean switchedOn;

  
  public enum State{ On, Off, Disabled}
  
  protected State switchState;
  
  //public static final int kOn = 1, kOff=2, kDisabled=3;
  
  protected String sButtonTextOff, sButtonTextOn, sButtonTextDisabled;
  
  protected GralImageBase imageOff, imageOn, imageDisabled;
  
  /**Color of button. If colorOff is null, then it is not a switch button. */
  protected GralColor colorBackOn, colorBackDisabled = GralColor.getColor("gr");
  
  /**Color of button. If colorOff is null, then it is not a switch button. */
  protected GralColor colorLineOff = GralColor.getColor("bk"), colorLineOn = GralColor.getColor("bk"), colorLineDisabled = GralColor.getColor("lgr");
  
  /**Currently pressed, show it in graphic. */
  protected boolean isPressed;
  
  
  /**True if it is a switch or a check button. */
  protected boolean shouldSwitched;
  
  /**True if the switch is in disable state. Show it in graphic. */
  //protected boolean isDisabled;
  
  /**True if the button has three states: on, off, disabled. */
  protected boolean bThreeStateSwitch;
  
  public GralButton(String sName)
  {
    super(sName, 'B');  //GralWidget
  }
  
  public GralButton(String sName, String sText, GralUserAction action)
  {
    super(sName, 'B');  //GralWidget
    setText(sText);
    setActionChange(action);
  }
  
  /**Declares the button to a switch button (switching on/off) with the given texts.
   * Different colors for on and off may be set with {@link #setSwitchMode(GralColor, GralColor)}.
   * See {@link #setSwitchMode(GralImageBase, GralImageBase)}.
   * @param sButtonTextOff
   * @param sButtonTextOn
   */
  public void setSwitchMode(String sButtonTextOff, String sButtonTextOn){ 
    this.sButtonTextOn = sButtonTextOn;
    this.sButtonTextOff = sButtonTextOff;
    shouldSwitched = true;
    bThreeStateSwitch = false;
  }
  
  
  /**Declares the button to a check button (switching on/off/disable) with the given texts.
   * Different colors for on, off and disable may be set with {@link #setSwitchMode(GralColor, GralColor, GralColor)}.
   * See {@link #setSwitchMode(GralImageBase, GralImageBase, GralImageBase)}.
   * @param sButtonTextOff
   * @param sButtonTextOn
   * @param sDisabled
   */
  public void setSwitchMode(String sButtonTextOff, String sButtonTextOn, String sDisabled){ 
    this.sButtonTextOn = sButtonTextOn;
    this.sButtonTextOff = sButtonTextOff;
    this.sButtonTextDisabled = sDisabled;
    shouldSwitched = true; 
    bThreeStateSwitch = true;
  }
  
  
  /**Declares the button to a switch button (switching on/off) with the given colors.
   * Different texts for on and off may be set with {@link #setSwitchMode(String, String)}.
   * See {@link #setSwitchMode(GralImageBase, GralImageBase)}.
   * @param colorOff
   * @param colorOn
   */
  public void setSwitchMode(GralColor colorOff, GralColor colorOn){ 
    this.colorBackOn = colorOn;
    dyda.backColor = colorOff;
    bThreeStateSwitch = false;
    shouldSwitched = true; 
  }
  
  
  /**Declares the button to a check button (switching on/off/disable) with the given texts.
   * Different texts for on and off may be set with {@link #setSwitchMode(String, String, String)}.
   * See {@link #setSwitchMode(GralImageBase, GralImageBase, GralImageBase)}.
   * @param colorOff
   * @param colorOn
   */
  public void setSwitchMode(GralColor colorOff, GralColor colorOn, GralColor colorDisabled){ 
    this.colorBackOn = colorOn;
    dyda.backColor = colorOff;
    this.colorBackDisabled = colorDisabled;
    bThreeStateSwitch = true;
    shouldSwitched = true; 
  }
  
  
  /**Declares the button to a switch button (switching on/off) with the given images.
   * see {@link #setSwitchMode(String, String)}.
   * @param imageOff
   * @param imageOn
   */
  public void setSwitchMode(GralImageBase imageOff, GralImageBase imageOn){ 
    this.imageOn = imageOn;
    this.imageOff = imageOff;
    bThreeStateSwitch = false;
    shouldSwitched = true; 
  }
  
  
  /**Declares the button to a check button (switching on/off/disable) with the given texts.
   * see {@link #setSwitchMode(String, String, String)}.
   * @param imageOff
   * @param imageOn
   */
  public void setSwitchMode(GralImageBase imageOff, GralImageBase imageOn, GralImageBase imageDisabled){ 
    this.imageOn = imageOn;
    this.imageOff = imageOff;
    this.imageDisabled = imageDisabled;
    bThreeStateSwitch = true;
    shouldSwitched = true; 
  }
  
  
  
  
  /**Implementing for 
   * @see org.vishia.gral.base.GralWidget#setBackColor(org.vishia.gral.ifc.GralColor, int)
   * @param ix=0: off-color ix=1: on-color, ix=2 or ix=-1: disable color.
   */
  @Override public void setBackColor(GralColor color, int ix){ 
    GralColor[] ref = new GralColor[1];
    boolean bChanged;
    switch(ix){
      case 0:          bChanged = !color.equals(dyda.backColor);     if(bChanged){ dyda.backColor = color; } break;
      case 1:          bChanged = !color.equals(colorBackOn);        if(bChanged){ colorBackOn = color; } break;
      case -1: case 2: bChanged = !color.equals(colorBackDisabled);  if(bChanged){ colorBackDisabled = color; } break;
      default: throw new IllegalArgumentException("fault ix, allowed -1, 0, 1, 2, ix=" + ix);
    }//switch
    if(bChanged){
      dyda.setChanged(ImplAccess.chgColorBack); 
      repaint();
    }
  }

  
  /**Sets the disable text and color. A Button can be shown disabled anytime, independent of its role
   * of switch button or check button.
   * @param color
   * @param text
   */
  public void setDisableColorText(GralColor color, String text){
    this.colorBackDisabled = color;
    this.sButtonTextDisabled = text;
  }
  
  
  /**Sets the text of the button.
   * The text can be changed in any thread any time.
   * A repaint is forced automatically after a short time (100 ms).
   * @param sButtonText
   */
  public void setText(String sButtonText){ 
    this.sButtonTextOff = sButtonText;
    sButtonTextDisabled = sButtonText;
    sButtonTextOff = sButtonText;
    
    if(sButtonTextDisabled == null){ sButtonTextDisabled = sButtonText; }
    if(sButtonTextOn == null){ sButtonTextOn = sButtonText; }
    repaint(100, 100);
  }
  
  /**Show the button in an activated state. This method is called especially 
   * in its mouse press and release events. 
   * In the activated state the button looks like pressed.*/
  protected void setActivated(boolean value){ 
    isPressed = value;
    repaint(100, 100);
  }
  
  
  /**This is the same action like press and release the left mouse button. It can be called from the application
   * to activate the function of the button. Especially it is used for [Enter] on the focused button.
   * 
   */
  public void activate() {
    if(shouldSwitched){
      if( switchState == State.On) { switchState = State.Off; }
      else if(bThreeStateSwitch && switchState == State.Off){ switchState = State.Disabled; }
      else { switchState = State.On; }
    }
    if(actionChanging !=null){
      actionChanging.exec(KeyCode.enter, this);
    }
    repaint();
  }
  
  
  
  @Override public void setValue(int cmd, int ident, Object visibleInfo, Object userData){
    if(visibleInfo instanceof Integer){
      int value = ((Integer)visibleInfo).intValue();
      if(shouldSwitched){
        switch(value){
          case 0: switchState = State.Off; break;
          case 1: switchState = State.On; break;
          default: switchState = State.Disabled; break;
        }
      }
    }
  }

  
  
  public void XXXsetDisabled(boolean value){
    this.switchState = value ? State.Disabled: State.On; //kDisabled : kOn;
    repaint(100, 100);
  }
  
  
  public boolean isDisabled(){ return switchState == State.Disabled; }
  
  
  public boolean isOn(){ return switchState == State.On; }

  /**Sets the appearance of the button
   * <ul> 
   * <li>val instance of GralColor, change the color appropriate to the current state.
   * <li>val instance of Text, changes the switch state.
   *   <ul>
   *   <li>"1", "true", "on"; switches to on
   *   <li>"0", "false", "off"; switches to off
   *   </ul>    
   * </ul>
   * <li>val instance of Integer:
   *   <ul>
   *   <li>0 switches off.
   *   <li>not 0 switches on
   *   </ul>
   * </ul> 
   *   instance
   * @param val 
   */
  public void setState(Object val)
  {
    if(val instanceof GralColor){
      if(switchState == State.On){
        colorBackOn = (GralColor)val;
      } else {
        dyda.backColor = (GralColor)val;
      }
    } else {
      String sVal = (val instanceof String) ? (String)val : null;
      int nVal = val instanceof Integer ? ((Integer)val).intValue(): -1;
      if(sVal !=null && (sVal.equals("1") || sVal.equals("true") || sVal.equals("on"))
        || sVal == null && nVal !=0){
        switchState = State.On;
      } else if(sVal !=null && (sVal.equals("0") || sVal.equals("false") || sVal.equals("off"))
          || nVal == 0){
        switchState = State.Off;
      }
    }
    repaint(100,100);
  }
  

  
  /**Sets the state of the button
   * @param state one of {@link #kOn}, {@value #kOff}, {@value #kDisabled}.
   * @throws IllegalArgumentException if the state is faulty.
   */
  public void setState(State state){
    //if(state == kOn || state == kOff || state ==kDisabled){
      switchState = state;
      repaint(100,100);
    //} else {
    //  throw new IllegalArgumentException("faulty state: " + state);
    //}
  }
  
  
  public State getState(){ return switchState; }
  
  
  /**Inner class to implement the mouse actions called from the gral implementing layer.
   * If the mouse was moved from the widget's area while a button is pressed, the mouse action on release
   * is not invoked.
   */
  class MouseActionButton implements GralMouseWidgetAction_ifc
  {

    @Override public boolean mouseMoved(int xMousePixel, int yMousePixel, int sizex, int sizey){
      if(  xMousePixel < 0 || xMousePixel > sizex
          || yMousePixel < 0 || yMousePixel > sizey
          ){ //the mouse cursor has left the area of the widget:
        setActivated(false);
        return false;
      }
      else return true;
    }


    @Override public void mouse1Down(int keyCode, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel, GralWidget widgg)
    {
      setActivated(true);
      if(widgg.actionChanging !=null){
        widgg.actionChanging.exec(keyCode, widgg, new Integer(xMousePixel), new Integer(yMousePixel));
      }
    }


    /**It will be called only if the mouse up is pressed inside the button's area.
     * @see org.vishia.gral.base.GralMouseWidgetAction_ifc#mouse1Up(int, int, int, int, int, org.vishia.gral.base.GralWidget)
     */
    @Override public void mouse1Up(int keyCode, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel, GralWidget widgg)
    {
      setActivated(false);
      //On -> Off -> Disabled -> On
      if(shouldSwitched){
        if( switchState == State.On) { switchState = State.Off; }
        else if(bThreeStateSwitch && switchState == State.Off){ switchState = State.Disabled; }
        else { switchState = State.On; }
      }
      if(widgg.actionChanging !=null){
        widgg.actionChanging.exec(keyCode, widgg, new Integer(xMousePixel), new Integer(yMousePixel));
      }
      widgg.repaint(100, 200);
    }


    @Override public void mouse2Down(int keyCode, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel, GralWidget widgg)
    {
      if(widgg.actionChanging !=null){
        widgg.actionChanging.exec(keyCode, widgg, new Integer(xMousePixel), new Integer(yMousePixel));
      }
    }


    @Override public void mouse2Up(int keyCode, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel, GralWidget widgg)
    {
      if(widgg.actionChanging !=null){
        widgg.actionChanging.exec(keyCode, widgg, new Integer(xMousePixel), new Integer(yMousePixel));
      }
    }

    @Override public void mouse1Double(int keyCode, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel, GralWidget widgg)
    {
      if(widgg.actionChanging !=null){
        widgg.actionChanging.exec(keyCode, widgg, new Integer(xMousePixel), new Integer(yMousePixel));
      }
    }
    
    
    
  }
  
  
  
  public abstract class GraphicImplAccess extends GralWidget.ImplAccess
  implements GralWidgImpl_ifc
  {

    /**That action effects that the internal variable {@link #switchedOn} is set for a switching button.
     * The user action will be called by the standard mouse action of the implementation graphic
     * AWT: {@link org.vishia.gral.awt.AwtGralMouseListener.MouseListenerUserAction}.
     */
    final protected GralMouseWidgetAction_ifc mouseWidgetAction = new MouseActionButton();
    

    /**Set in {@link #paint1()}, used for paint routine. */
    protected String sButtonText;
    /**Set in {@link #paint1()}, used for paint routine. */
    protected GralColor colorgback, colorgline;

    /**Covers the GralWidget#widgg, but it is the same reference. This is the necessary type. */
    protected GralButton widgg;
    
    protected GraphicImplAccess(GralButton widgg, GralMng mng)
    { super(widgg);
      this.widgg = widgg;
    }
    
    
    protected boolean isPressed(){ return GralButton.this.isPressed; }
    
    
    protected void prepareWidget(){
      int catastrophicalCount = 0;
      int chg, chgAckn=0;
      int state1 = -1;
      do{
        chg = dyda().whatIsChanged.get() & ~chgAckn;  //don't handle a bit twice if re-read.
        if(++catastrophicalCount > 10000) 
          throw new RuntimeException("atomic failed");
        if((chg & chgVisibleInfo) !=0 && dyda().visibleInfo !=null){ 
          chgAckn |= chgVisibleInfo;
          if(dyda().visibleInfo instanceof Integer){
            state1 = ((Integer)dyda().visibleInfo).intValue();
          }
        }
        if((chg & chgIntg) !=0 ){ 
          chgAckn |= chgIntg;
          state1 = 0; //TODO dyda.intValue
        }
      } while(!dyda().whatIsChanged.compareAndSet(chg, 0));  //repeat if chg is changed in that time.

      switch(state1){
        case 0: switchState = State.Off; break;
        case 1: switchState = State.On; break;
        case 2: switchState = State.Disabled; break;
        default: ; //don't change state.
      }
    }

    
    
    protected void paint1(){
      if(switchState == State.On){ 
        sButtonText = sButtonTextOn != null ? sButtonTextOn : sButtonTextOff;
        colorgback = colorBackOn !=null ? colorBackOn: dyda().backColor;
        colorgline = colorLineOn !=null ? colorLineOn: colorLineOff;
      } else if(switchState == State.Disabled){ 
        sButtonText = sButtonTextDisabled;
        colorgback = colorBackDisabled;
        colorgline = colorLineDisabled;
      } else { 
        sButtonText = sButtonTextOff;
        colorgback = dyda().backColor;
        colorgline = colorLineOff;
      }

    }
    
    
  }
  
  

  
}

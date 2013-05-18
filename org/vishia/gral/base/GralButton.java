package org.vishia.gral.base;

import org.vishia.bridgeC.IllegalArgumentExceptionJc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralImageBase;
import org.vishia.gral.ifc.GralWindowMng_ifc;
import org.vishia.gral.swt.SwtGralMouseListener;

public abstract class GralButton extends GralWidget
{
  /**Version, history and license
   * <ul>
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
  
  /**That action effects that the internal variable {@link #switchedOn} is set for a switching button.
   * The user action will be called by the standard mouse action of the implementation graphic
   * AWT: {@link org.vishia.gral.awt.AwtGralMouseListener.MouseListenerUserAction}.
   */
  final protected GralMouseWidgetAction_ifc mouseWidgetAction = new MouseActionButton();
  

  protected boolean pressed;
  
  /**The state of switch and check button
   * 1=on 2=off 3=disabled. 0=initial, see {@link #kOn} etc.
   */
  protected int switchState;
  //protected boolean switchedOn;

  
  public static final int kOn = 1, kOff=2, kDisabled=3;
  
  protected String sButtonTextOff, sButtonTextOn, sButtonTextDisabled = "-";
  
  protected GralImageBase imageOff, imageOn, imageDisabled;
  
  /**Color of button. If colorOff is null, then it is not a switch button. */
  protected GralColor colorOff, colorOn, colorDisabled = GralColor.getColor("gr");
  
  /**Currently pressed, show it in graphic. */
  protected boolean isPressed;
  
  
  /**True if it is a switch or a check button. */
  protected boolean shouldSwitched;
  
  /**True if the switch is in disable state. Show it in graphic. */
  protected boolean isDisabled;
  
  /**True if the button has three states: on, off, disabled. */
  protected boolean bThreeStateSwitch;
  
  //public GralButton(String sName, GralWindowMng_ifc mainWindow, GralGridMngBase mng)
  public GralButton(String sName, GralMng mng)
  {
    super(sName, 'B', mng);  //GralWidget
    //this.mng = mng;
    //this.mainWindow = mainWindow;
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
    this.colorOn = colorOn;
    this.colorOff = colorOff;
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
    this.colorOn = colorOn;
    this.colorOff = colorOff;
    this.colorDisabled = colorDisabled;
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
    switch(ix){
    case 0: colorOff = color; dyda.backColor = color; break;
    case 1: colorOn = color; break;
    case -1: case2: colorDisabled = color; break;
    default: throw new IllegalArgumentException("fault ix, allowed -1, 0, 1, 2, ix=" + ix);
    }//switch
     
    repaint(100, 100); 
  }

  
  /**Sets the disable text and color. A Button can be shown disabled anytime, independent of its role
   * of switch button or check button.
   * @param color
   * @param text
   */
  public void setDisableColorText(GralColor color, String text){
    this.colorDisabled = color;
    this.sButtonTextDisabled = text;
  }
  
  
  /**Sets the text of the button.
   * The text can be changed in any thread any time.
   * A repaint is forced automatically after a short time (100 ms).
   * @param sButtonText
   */
  public void setText(String sButtonText){ 
    this.sButtonTextOff = sButtonText; 
    repaint(100, 100);
  }
  
  /**Show the button in an activated state. This method is called especially 
   * in its mouse press and release events. 
   * In the activated state the button looks like pressed.*/
  protected void setActivated(boolean value){ 
    isPressed = value;
    repaint(100, 100);
  }
  
  
  
  public void setDisabled(boolean value){
    this.isDisabled = value;
    repaint(100, 100);
  }
  
  
  
  public boolean isOn(){ return switchState == kOn; }

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
      if(switchState == kOn){
        colorOn = (GralColor)val;
      } else {
        colorOff = (GralColor)val;
      }
    } else {
      String sVal = (val instanceof String) ? (String)val : null;
      int nVal = val instanceof Integer ? ((Integer)val).intValue(): -1;
      if(sVal !=null && (sVal.equals("1") || sVal.equals("true") || sVal.equals("on"))
        || sVal == null && nVal !=0){
        switchState = kOn;
      } else if(sVal !=null && (sVal.equals("0") || sVal.equals("false") || sVal.equals("off"))
          || nVal == 0){
        switchState = kOff;
      }
    }
    repaint(100,100);
  }
  

  
  /**Sets the state of the button
   * @param state one of {@link #kOn}, {@value #kOff}, {@value #kDisabled}.
   * @throws IllegalArgumentException if the state is faulty.
   */
  public void setState(int state){
    if(state == kOn || state == kOff || state ==kDisabled){
      switchState = state;
      repaint(100,100);
    } else {
      throw new IllegalArgumentException("faulty state: " + state);
    }
  }
  
  
  public int getState(){ return switchState; }
  
  
  /**Inner class to implement the mouse actions called from the gral implementing layer.
   */
  private class MouseActionButton implements GralMouseWidgetAction_ifc
  {
    /**Constructor.
     */
    public MouseActionButton()
    { //super(this);
    }

    
    @Override public void mouseAction(int keyCode, int xMousePixel, int yMousePixel, GralWidget widgg)
    {
      
    }
    

    @Override public boolean mouseMoved(int xMousePixel, int yMousePixel, int sizex, int sizey){
      if(  xMousePixel < 0 || xMousePixel > sizex
          || yMousePixel < 0 || yMousePixel > sizey
          ){ //the mouse cursor has left the area of the widget:
        setActivated(false);
        return false;
      }
      else return true;
    }

    
    

    @Override
    public void mouse1Down(int keyCode, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel, GralWidget widgg)
    {
      setActivated(true);
    }


    @Override public void mouse1Up(int keyCode, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel, GralWidget widgg)
    {
      setActivated(false);
      //On -> Off -> Disabled -> On
      if(shouldSwitched){
        if( switchState == kOn) { switchState = kOff; }
        else if(bThreeStateSwitch && switchState == kOff){ switchState = kDisabled; }
        else { switchState = kOn; }
      }
      widgg.repaint(100, 200);
    }


    @Override
    public void mouse2Down(int keyCode, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel, GralWidget widgg)
    {
      // TODO Auto-generated method stub
      
    }


    @Override
    public void mouse2Up(int keyCode, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel, GralWidget widgg)
    {
      // TODO Auto-generated method stub
      
    }

    
    
    
    
  }
  
  
  
}

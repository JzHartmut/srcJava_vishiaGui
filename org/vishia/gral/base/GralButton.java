package org.vishia.gral.base;

import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralImageBase;
import org.vishia.gral.ifc.GralWindowMng_ifc;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.swt.SwtGralMouseListener;

public abstract class GralButton extends GralWidget
{
  /**Version, history and licence
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
  public final static int version = 0x20120303;

  
  //final GralWindowMng_ifc mainWindow;
  
  /**That action effects that the internal variable {@link #switchedOn} is set for a switching button.
   * The user action will be called by the standard mouse action of the implementation graphic
   * AWT: {@link org.vishia.gral.awt.AwtGralMouseListener.MouseListenerUserAction}.
   */
  final protected GralMouseWidgetAction_ifc mouseWidgetAction = new MouseActionButton();
  

  protected boolean pressed;
  protected boolean switchedOn;

  protected String sButtonTextOff, sButtonTextOn;
  
  protected GralImageBase imageOff, imageOn;
  
  /**Color of button. If colorOff is null, then it is not a switch button. */
  protected GralColor colorOff, colorOn;
  
  //final GralGridMngBase mng;
  protected boolean isActivated;
  
  
  protected boolean shouldSwitched;
  
  //public GralButton(String sName, GralWindowMng_ifc mainWindow, GralGridMngBase mng)
  public GralButton(String sName, GralWidgetMng mng)
  {
    super(sName, 'B', mng);  //GralWidget
    //this.mng = mng;
    //this.mainWindow = mainWindow;
  }
  
  public void setSwitchMode(String sButtonTextOff, String sButtonTextOn){ 
    this.sButtonTextOn = sButtonTextOn;
    this.sButtonTextOff = sButtonTextOff;
    shouldSwitched = true; 
  }
  
  
  public void setSwitchMode(GralColor colorOff, GralColor colorOn){ 
    this.colorOn = colorOn;
    this.colorOff = colorOff;
    shouldSwitched = true; 
  }
  
  
  public void setSwitchMode(GralImageBase imageOff, GralImageBase imageOn){ 
    this.imageOn = imageOn;
    this.imageOff = imageOff;
    shouldSwitched = true; 
  }
  
  
  public void setText(String sButtonText){ 
    this.sButtonTextOff = sButtonText; 
    repaint(100, 100);
  }
  
  /**Show the button in an activated state. This method is called especially 
   * in its mouse press and release events. 
   * In the activated state the button looks like pressed.*/
  public void setActivated(boolean value){ 
    isActivated = value;
    repaint();
  }
  
  
  public boolean isOn(){ return switchedOn; }

  public void setState(Object val)
  {
    if(val instanceof GralColor){
      if(switchedOn){
        colorOn = (GralColor)val;
      } else {
        colorOff = (GralColor)val;
      }
    } else {
      String sVal = (val instanceof String) ? (String)val : null;
      int nVal = val instanceof Integer ? ((Integer)val).intValue(): -1;
      if(sVal !=null && (sVal.equals("1") || sVal.equals("true") || sVal.equals("on"))
        || sVal == null && nVal !=0){
        switchedOn = true;
      } else if(sVal !=null && (sVal.equals("0") || sVal.equals("false") || sVal.equals("off"))
          || nVal == 0){
        switchedOn = false;
      }
    }
    repaint();
  }
  

  
  
  /**Inner class to implement the mouse actions called from the gral implementing layer.
   */
  private class MouseActionButton implements GralMouseWidgetAction_ifc
  {
    /**Constructor.
     */
    public MouseActionButton()
    { //super(this);
    }

    
    @Override public void mouseAction(int key, GralWidget widgg)
    {
      
    }
    
    @Override
    public void removeMouseCursorFromWidgetWhilePressed()
    {
      setActivated(false);
      
    }


    @Override
    public void mouse1Down()
    {
      setActivated(true);
    }


    @Override public void mouse1Up()
    {
      setActivated(false);
      if(shouldSwitched){
        switchedOn = ! switchedOn;
      }
    }


    @Override
    public void mouse2Down()
    {
      // TODO Auto-generated method stub
      
    }


    @Override
    public void mouse2Up()
    {
      // TODO Auto-generated method stub
      
    }

    
    
    
    
  }
  
  
  
}

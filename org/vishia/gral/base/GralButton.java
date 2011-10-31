package org.vishia.gral.base;

import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralImageBase;
import org.vishia.gral.ifc.GralWindowMng_ifc;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.mainGuiSwt.SwtGralMouseListener;

public abstract class GralButton extends GralWidget
{
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
  
  
  public void setText(String sButtonText){ this.sButtonTextOff = sButtonText; }
  
  /**Show the button in an activated state. This method is called especially 
   * in its mouse press and release events. 
   * In the activated state the button looks like pressed.*/
  public void setActivated(boolean value){ 
    isActivated = value;
    redraw();
  }
  
  
  public boolean isOn(){ return switchedOn; }

  public void setState(Object val)
  {
    String sVal = (val instanceof String) ? (String)val : null;
    int nVal = val instanceof Integer ? ((Integer)val).intValue(): -1;
    if(sVal !=null && (sVal.equals("1") || sVal.equals("true") || sVal.equals("on"))
      || sVal == null && nVal !=0){
      switchedOn = true;
    } else if(sVal !=null && (sVal.equals("0") || sVal.equals("false") || sVal.equals("off"))
        || nVal == 0){
      switchedOn = false;
    }
    redraw();
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

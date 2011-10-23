package org.vishia.gral.base;

import org.vishia.gral.gridPanel.GralGridMngBase;
import org.vishia.gral.ifc.GralWindowMng_ifc;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.mainGuiSwt.SwtGralMouseListener;

public abstract class GralButton extends GralWidget
{
  //final GralWindowMng_ifc mainWindow;
  
  final protected GralMouseWidgetAction_ifc mouseWidgetAction = new MouseActionButton();
  

  protected boolean pressed;
  protected boolean switchedOn;

  protected String sButtonText;
  
  //final GralGridMngBase mng;
  protected boolean isActivated;
  
  
  protected boolean shouldSwitched;
  
  //public GralButton(String sName, GralWindowMng_ifc mainWindow, GralGridMngBase mng)
  public GralButton(String sName, GralGridMngBase mng)
  {
    super(sName, 'B', mng);  //GralWidget
    //this.mng = mng;
    //this.mainWindow = mainWindow;
  }
  
  
  public void setText(String sButtonText){ this.sButtonText = sButtonText; }
  
  /**Show the button in an activated state. This method is called especially 
   * in its mouse press and release events. 
   * In the activated state the button looks like pressed.*/
  public void setActivated(boolean value){ 
    isActivated = value;
    redraw();
  }
  
  


  
  
  private class MouseActionButton implements GralMouseWidgetAction_ifc
  {
    /**Constructor.
     * @param guiMng The Gui-manager
     * @param userCmdGui The users method for the action. 
     * @param sCmdPress command string provided as first parameter on mouse button press.
     * @param sCmdRelease
     * @param sCmdDoubleClick
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


    @Override
    public void mouse1Up()
    {
      setActivated(false);
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

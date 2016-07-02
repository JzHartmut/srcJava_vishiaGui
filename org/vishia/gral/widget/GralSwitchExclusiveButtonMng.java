package org.vishia.gral.widget;

import java.util.LinkedList;
import java.util.List;

import org.vishia.byteData.VariableAccess_ifc;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;

public class GralSwitchExclusiveButtonMng implements VariableAccess_ifc
{
  
  //final GuiPanelMngWorkingIfc mng;

  private final List<GralWidget> listSwitchButtons = new LinkedList<GralWidget>();
  
  String currentButtonText;
  
  public void add(GralWidget widgd){ 
    listSwitchButtons.add(widgd);
    if(widgd.getActionChange(GralWidget_ifc.ActionChangeWhen.onEnter) == null){
      widgd.setActionChange(null, switchAction, null, GralWidget_ifc.ActionChangeWhen.onEnter);
    }
  }
  
  public void remove(GralWidget widgd)
  {
    listSwitchButtons.remove(widgd);
  }
  
  
  public GralSwitchExclusiveButtonMng() //GuiPanelMngWorkingIfc mng)
  { //this.mng = mng;
  }
  
  public GralUserAction switchAction = new GralUserAction()
  {
    
    @Override
    public boolean userActionGui(int key, GralWidget infos, Object... params)
    {
      // TODO Auto-generated method stub
      currentButtonText = infos.sCmd;
      for(GralWidget item: listSwitchButtons){
        if(item != infos){
          item.setValue(GralMng_ifc.cmdSet, 0, 0);
        }
      }
      return true;
    }
  };

  @Override public int getInt(){ return 0; }
  @Override public int setInt(int value){ return 0; }

  @Override public long getLong(){ return 0; }
  @Override public long setLong(long value){ return 0; }

  @Override
  public float getFloat()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public float setFloat(float value)
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getDouble()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double setDouble(double value)
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getString()
  { return currentButtonText;
  }

  @Override
  public String setString(String value)
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override public char getType(){ return 'I'; } 
  

  @Override public void setRefreshed(long time){ }
  
  @Override
  public long getLastRefreshTime()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  
  @Override public boolean isRefreshed(){ return true; }

  @Override
  public void requestValue(long timeRequested) { }
  
  @Override public void requestValue(){ requestValue(System.currentTimeMillis()); }

  
  @Override
  public void requestValue(long timeRequested, Runnable run) { }
  
  @Override public boolean isRequestedValue(long timeEarlyRequested, boolean retryFaultyVariables){ return false; }
  


}

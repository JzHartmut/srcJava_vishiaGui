package org.vishia.mainGui;

import java.util.LinkedList;
import java.util.List;

import org.vishia.byteData.VariableAccess_ifc;

public class SwitchExclusiveButtonMng implements VariableAccess_ifc
{
  
  //final GuiPanelMngWorkingIfc mng;

  private List<WidgetDescriptor> listSwitchButtons = new LinkedList<WidgetDescriptor>();
  
  String currentButtonText;
  
  public void add(WidgetDescriptor widgd){ 
    listSwitchButtons.add(widgd);
    if(widgd.action == null){
      widgd.action = switchAction;
    }
  }
  
  public void remove(WidgetDescriptor widgd)
  {
    listSwitchButtons.remove(widgd);
  }
  
  
  public SwitchExclusiveButtonMng() //GuiPanelMngWorkingIfc mng)
  { //this.mng = mng;
  }
  
  public UserActionGui switchAction = new UserActionGui()
  {
    
    @Override
    public void userActionGui(String sIntension, WidgetDescriptor<?> infos, Object... params)
    {
      // TODO Auto-generated method stub
      currentButtonText = infos.sCmd;
      for(WidgetDescriptor item: listSwitchButtons){
        if(item != infos){
          item.setValue(GuiPanelMngWorkingIfc.cmdSet, 0, 0);
        }
      }
    }
  };

  @Override public int getInt(int... ixArray){ return 0; }
  @Override public int setInt(int value, int... ixArray){ return 0; }

  @Override
  public float getFloat(int... ixArray)
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public float setFloat(float value, int... ixArray)
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getDouble(int... ixArray)
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double setDouble(double value, int... ixArray)
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getString(int ixArray)
  { return currentButtonText;
  }

  @Override
  public String setString(String value, int ixArray)
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  
}

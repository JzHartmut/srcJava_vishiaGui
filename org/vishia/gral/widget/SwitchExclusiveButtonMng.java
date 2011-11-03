package org.vishia.gral.widget;

import java.util.LinkedList;
import java.util.List;

import org.vishia.byteData.VariableAccess_ifc;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;

public class SwitchExclusiveButtonMng implements VariableAccess_ifc
{
  
  //final GuiPanelMngWorkingIfc mng;

  private List<GralWidget> listSwitchButtons = new LinkedList<GralWidget>();
  
  String currentButtonText;
  
  public void add(GralWidget widgd){ 
    listSwitchButtons.add(widgd);
    if(widgd.getActionChange() == null){
      widgd.setActionChange(switchAction);
    }
  }
  
  public void remove(GralWidget widgd)
  {
    listSwitchButtons.remove(widgd);
  }
  
  
  public SwitchExclusiveButtonMng() //GuiPanelMngWorkingIfc mng)
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
          item.setValue(GralPanelMngWorking_ifc.cmdSet, 0, 0);
        }
      }
      return true;
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

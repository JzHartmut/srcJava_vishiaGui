package org.vishia.mainGui.cfg;

import org.vishia.mainGui.GuiPanelMngBase;
import org.vishia.mainGui.GuiPanelMngWorkingIfc;
import org.vishia.mainGui.GuiRectangle;
import org.vishia.mainGui.GuiShellMngBuildIfc;
import org.vishia.mainGui.UserActionGui;
import org.vishia.mainGui.WidgetDescriptor;
import org.vishia.msgDispatch.LogMessage;

public class GuiCfgDesigner
{
  
  
  protected final LogMessage log;

  private final GuiPanelMngBase mng;
  
  private final GuiCfgBuilder cfgBuilder;

  /**A Panel which contains the table to select some projectPaths. */
  private GuiShellMngBuildIfc dialogWindowProps;

  /**Some dialog widget elements. */
  private WidgetDescriptor dialogFieldName, dialogFieldDatapath, dialogFieldText;
  
  private WidgetDescriptor dialogButtonOk, dialogButtonEsc;
  
  /**Coordinates while left mouse pressed. */
  private int xMouse0, yMouse0;
  
  /**Coordinates while mouse moved, capture on release button. */
  private int xMouse, yMouse;
  
  private boolean bWidgetMoving = false;
  
  WidgetDescriptor widgdInDialog = null;
  
  public GuiCfgDesigner(GuiCfgBuilder cfgBuilder, GuiPanelMngBase mng, LogMessage log)
  { this.cfgBuilder = cfgBuilder;
    this.log = log;
    this.mng = mng;
  }

  
  /**Initializes the graphic, especially the context menu of right mouse button 
   * It will be called in the GUI-Thread.
   */
  public void initGui()
  {
    assert(dialogWindowProps == null); //check call only one time.
    mng.setPosition(2, 60, 22, 40, 'r');
    dialogWindowProps = mng.createWindow("Widget Properties", false);
    dialogWindowProps.setPosition(0, 0, 2, 39, 'd');
    dialogFieldName = dialogWindowProps.addTextField("name", true, "name", 'r');
    dialogFieldDatapath = dialogWindowProps.addTextField("dataPath", true, "dataPath", 'r');
    dialogFieldText = dialogWindowProps.addTextField("text", true, "text", 'r');
    dialogWindowProps.setPosition(16, 2, 3, 10, 'r');
    dialogButtonOk = dialogWindowProps.addButton("OK", actionOk, null, null, null, "OK");
    dialogButtonEsc = dialogWindowProps.addButton("esc", actionOk, null, null, null, "esc");
    
  }
  
  

  public void pressedLeftMouseDownForDesign(WidgetDescriptor widgd, GuiRectangle xy)
  {
    xMouse0 = xy.x;
    yMouse0 = xy.y;
    bWidgetMoving = true;
  }
  
  
  public void releaseLeftMouseForDesign(WidgetDescriptor widgd, GuiRectangle xy, boolean bCopy)
  {
    if(bWidgetMoving){
      bWidgetMoving = false;
      int dxPixel = xy.x - xMouse0;
      int dyPixel = xy.y - yMouse0;
      
      float dxGridf = (float)dxPixel / mng.propertiesGui.xPixelUnit();
      float dyGridf = (float)dyPixel / mng.propertiesGui.yPixelUnit();
      
      int dxGrid = (int)(dxGridf >0 ? dxGridf + 0.5f : dxGridf - 0.5f);
      int dyGrid = (int)(dyGridf >0 ? dyGridf + 0.5f : dyGridf - 0.5f);
      
      GuiCfgData.GuiCfgElement cfge = widgd.getCfgElement();
      if(cfge !=null){
        String sPanel = cfge.position.panel;  //Note: The cloned Object maybe empty here before buildWidget() is called
        int xPosAct = cfge.position.xPos;
        int yPosAct = cfge.position.yPos;
        if(bCopy){
          GuiCfgData.GuiCfgElement cfgn = cfge.clone(); //cfgBuilder.newCfgElement(cfge);
          cfge = cfgn;
        }
        cfge.positionInput.xPos = xPosAct + dxGrid; 
        cfge.positionInput.yPos = yPosAct + dyGrid; 
        if(!bCopy){
          mng.remove(widgd);  //remove the widget.
        }
        mng.selectPanel(sPanel);
        cfgBuilder.buildWidget(cfge);
      }
      
    }
    
  }

  
  public void pressedRightMouseDownForDesign(WidgetDescriptor widgd, GuiRectangle xy)
  { if(widgdInDialog == null){
      widgdInDialog = widgd;
      GuiCfgData.GuiCfgElement cfge = widgd.getCfgElement();
      String sName, sDataPath, sText;
      if(cfge !=null){
        sName = cfge.widgetType.name;
        sDataPath = cfge.widgetType.info;
        sText = cfge.widgetType.text;
        if(sName != null){ dialogFieldName.setValue(GuiPanelMngWorkingIfc.cmdSet, 0, sName); }
        if(sDataPath != null){ dialogFieldDatapath.setValue(GuiPanelMngWorkingIfc.cmdSet, 0, sDataPath); }
        if(sText != null){ dialogFieldText.setValue(GuiPanelMngWorkingIfc.cmdSet, 0, sText); }
      } else {
        dialogFieldName.setValue(GuiPanelMngWorkingIfc.cmdSet, 0, "error cfge");
      }
    }
    dialogWindowProps.setWindowVisible(true);
  }
  
  private UserActionGui actionOk = new UserActionGui()
  { @Override public void userActionGui(String sIntension, WidgetDescriptor widgd, Object... params)
    { if(widgdInDialog !=null){
        String sName = dialogFieldName.getValue();
        String sDataPath = dialogFieldDatapath.getValue();
        String sText = dialogFieldText.getValue();
        GuiCfgData.GuiCfgElement cfge = widgdInDialog.getCfgElement();
        if(cfge !=null){
          if(sName.trim().length() >0) { cfge.widgetType.name = sName; }
          if(sDataPath.trim().length() >0) { cfge.widgetType.info = sDataPath; }
          if(sText.trim().length() >0) { cfge.widgetType.text = sText; }
        }
        dialogWindowProps.setWindowVisible(false);
        widgdInDialog = null;
      }
    }
    
  };

  
  void stop(){}

}

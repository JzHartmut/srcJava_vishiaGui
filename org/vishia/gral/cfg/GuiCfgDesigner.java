package org.vishia.gral.cfg;

import org.vishia.gral.gridPanel.GuiPanelMngBase;
import org.vishia.gral.gridPanel.GuiShellMngBuildIfc;
import org.vishia.gral.ifc.GuiPanelMngWorkingIfc;
import org.vishia.gral.ifc.GuiRectangle;
import org.vishia.gral.ifc.UserActionGui;
import org.vishia.gral.ifc.WidgetDescriptor;
import org.vishia.msgDispatch.LogMessage;

public class GuiCfgDesigner
{
  
  
  protected final LogMessage log;

  private final GuiPanelMngBase mng;
  
  private final GuiCfgBuilder cfgBuilder;

  /**A Panel which contains the table to select some projectPaths. */
  private GuiShellMngBuildIfc dialogWindowProps;
  //private GuiWindowMng_ifc dialogWindowProps;
  
  /**Some dialog widget elements. */
  private WidgetDescriptor dialogFieldName, dialogFieldDatapath, dialogFieldText, dialogFieldFormat
    , dialogFieldShow, dialogFieldAction
    , dialogFieldLine, dialogFieldColumn, dialogFieldHeight, dialogFieldWidth;
  
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
    mng.setPosition(2, 60, 30, 40, 'r');
    dialogWindowProps = mng.createWindow("Widget Properties", false);
    dialogWindowProps.setPosition(0, 0, 3, 34, 'd');
    dialogFieldName = dialogWindowProps.addTextField("name", true, "name", 't');
    dialogFieldDatapath = dialogWindowProps.addTextField("dataPath", true, "data", 't');
    dialogFieldText = dialogWindowProps.addTextField("text", true, "text", 't');
    dialogFieldFormat = dialogWindowProps.addTextField("format", true, "format", 't');
    dialogFieldShow = dialogWindowProps.addTextField("show", true, "show method", 't');
    dialogFieldAction = dialogWindowProps.addTextField("action", true, "action method", 't');
    dialogWindowProps.setPosition(19, 2, 3, 5, 'r');
    dialogFieldLine = dialogWindowProps.addTextField("line", true, "pos-y", 't');
    //dialogWindowProps.addText(", ", 'B', 0);
    dialogFieldColumn = dialogWindowProps.addTextField("column", true, "pos-x", 't');
    //dialogWindowProps.addText("   ", 'B', 0);
    dialogFieldHeight = dialogWindowProps.addTextField("height", true, "size-y", 't');
    //dialogWindowProps.addText(" x ", 'B', 0);
    dialogFieldWidth = dialogWindowProps.addTextField("width", true, "size-x", 't');
    dialogWindowProps.setPosition(23, 2, 3, 8, 'r');
    dialogButtonEsc = dialogWindowProps.addButton("esc", actionEsc, null, null, null, "esc");
    dialogButtonOk = dialogWindowProps.addButton("del", actionDel, null, null, null, "del");
    dialogButtonOk = dialogWindowProps.addButton("OK", actionOk, null, null, null, "OK");
    
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
      
      GuiCfgData.GuiCfgElement cfge = (GuiCfgData.GuiCfgElement)widgd.getCfgElement();
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
  { //if(widgdInDialog == null){
      widgdInDialog = widgd;
      GuiCfgData.GuiCfgElement cfge = (GuiCfgData.GuiCfgElement)widgd.getCfgElement();
      String sName, sDataPath, sText, sFormat, sShowMethod,  sActionMethod;
      String sLine, sColumn, sWidth, sHeight;
      if(cfge !=null){
        sName = cfge.widgetType.name;
        sDataPath = cfge.widgetType.info;
        sText = cfge.widgetType.text;
        sFormat = cfge.widgetType.format;
        sShowMethod = cfge.widgetType.showMethod;
        sActionMethod = cfge.widgetType.userAction;
        sLine = (cfge.positionInput.yPosRelative ? "&" : "") + cfge.positionInput.yPos + (cfge.positionInput.yPosFrac !=0 ? "." + cfge.positionInput.yPosFrac : "");
        sColumn = (cfge.positionInput.xPosRelative ? "&" : "") + cfge.positionInput.xPos + (cfge.positionInput.xPosFrac !=0 ? "." + cfge.positionInput.xPosFrac : "");
        sHeight = "" + cfge.positionInput.ySizeDown + (cfge.positionInput.ySizeFrac !=0 ? "." + cfge.positionInput.ySizeFrac : "");
        sWidth = "" + cfge.positionInput.xWidth + (cfge.positionInput.xSizeFrac !=0 ? "." + cfge.positionInput.xSizeFrac : "");
        dialogFieldName.setValue(GuiPanelMngWorkingIfc.cmdSet, 0, sName ==null ? "" : sName);
        dialogFieldDatapath.setValue(GuiPanelMngWorkingIfc.cmdSet, 0, sDataPath == null ? "" : sDataPath);
        dialogFieldText.setValue(GuiPanelMngWorkingIfc.cmdSet, 0, sText ==null ? "" : sText);
        dialogFieldFormat.setValue(GuiPanelMngWorkingIfc.cmdSet, 0, sFormat ==null ? "" : sFormat);
        dialogFieldShow.setValue(GuiPanelMngWorkingIfc.cmdSet, 0, sShowMethod ==null ? "" : sShowMethod);
        dialogFieldAction.setValue(GuiPanelMngWorkingIfc.cmdSet, 0, sActionMethod ==null ? "" : sActionMethod);
        dialogFieldLine.setValue(GuiPanelMngWorkingIfc.cmdSet, 0, sLine);
        dialogFieldColumn.setValue(GuiPanelMngWorkingIfc.cmdSet, 0, sColumn);
        dialogFieldHeight.setValue(GuiPanelMngWorkingIfc.cmdSet, 0, sHeight);
        dialogFieldWidth.setValue(GuiPanelMngWorkingIfc.cmdSet, 0, sWidth);
      } else {
        dialogFieldName.setValue(GuiPanelMngWorkingIfc.cmdSet, 0, "ERROR cfge");
      }
    //}
    dialogWindowProps.setWindowVisible(true);
  }
  
  private UserActionGui actionOk = new UserActionGui()
  { @Override public void userActionGui(String sIntension, WidgetDescriptor widgd, Object... params)
    { //note widgd is the OK-button!
      if(widgdInDialog !=null){
        String sName = dialogFieldName.getValue();
        String sDataPath = dialogFieldDatapath.getValue();
        String sText = dialogFieldText.getValue();
        String sFormat = dialogFieldFormat.getValue();
        String sLine = dialogFieldLine.getValue();
        String sColumn = dialogFieldColumn.getValue();
        String sWidth = dialogFieldWidth.getValue();
        String sHeight = dialogFieldHeight.getValue();
        
        GuiCfgData.GuiCfgElement cfge = (GuiCfgData.GuiCfgElement)widgdInDialog.getCfgElement();
        if(cfge !=null){
          String sPanel = cfge.position.panel;  //Note: The cloned Object maybe empty here before buildWidget() is called
            /*if(sName.trim().length() >0) { cfge.widgetType.name = sName; }
          if(sDataPath.trim().length() >0) { cfge.widgetType.info = sDataPath; }
          if(sText.trim().length() >0) { cfge.widgetType.text = sText; }
          if(sFormat.trim().length() >0) { cfge.widgetType.format = sFormat; }
          */
          cfge.widgetType.name = sName.trim().length() >0 ? sName : null;
          cfge.widgetType.info = sDataPath.trim().length() >0 ? sDataPath : null;
          cfge.widgetType.text = sText.trim().length() >0 ? sText : null;
          cfge.widgetType.format = sFormat.trim().length() >0 ? sFormat : null;
          boolean bOk;
          bOk = cfge.positionInput.setPosElement('y', sLine.trim());          
          bOk = bOk && cfge.positionInput.setPosElement('x', sColumn.trim());          
          bOk = bOk && cfge.positionInput.setPosElement('h', sHeight.trim());          
          bOk = bOk && cfge.positionInput.setPosElement('w', sWidth.trim());
          if(!bOk)
            stop();
          mng.remove(widgdInDialog);  //remove the widget.
          mng.selectPanel(sPanel);
          cfgBuilder.buildWidget(cfge);
        }
        dialogWindowProps.setWindowVisible(false);
        widgdInDialog = null;
      }
    }
    
  };

  
  
  
  private UserActionGui actionDel = new UserActionGui()
  { @Override public void userActionGui(String sIntension, WidgetDescriptor widgd, Object... params)
    { //note widgd is the OK-button!
      if(widgdInDialog !=null){
        mng.remove(widgdInDialog);  //remove the widget.
        dialogWindowProps.setWindowVisible(false);
        widgdInDialog = null;
      }
    }
    
  };

  
  
  
  
  
  
  private UserActionGui actionEsc = new UserActionGui()
  { @Override public void userActionGui(String sIntension, WidgetDescriptor widgd, Object... params)
    { dialogWindowProps.setWindowVisible(false);
      widgdInDialog = null;
    }
    
  };

  
  void stop(){}

}

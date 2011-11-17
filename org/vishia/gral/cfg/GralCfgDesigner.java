package org.vishia.gral.cfg;

import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralGridPos;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.msgDispatch.LogMessage;

public class GralCfgDesigner
{
  
  /**The version.
   * <ul>
   * <li>2011-09-30 Hartmut chg: rename pressedRightMouseDownForDesign(...) to {@link #editFieldProperties(GralWidget, GralRectangle)}.
   *     because it isn't called as mouse action.
   * <li>2011-09-23 Hartmut corr: Use the new windows concept with {@link GralWindow}.
   * <li>2011-07-07 Hartmut new: Improve dialog, edit all fields of the {@link GralWidget} inclusive positions.
   * <li>2011-05-24 Created. The configuration of fields of a GUI are edit-able now in the GUI itself without any other tool.
   * </ul>
   */
  public final int version = 0x20110930;
  
  protected final LogMessage log;

  private final GralWidgetMng mng;
  
  private final GralCfgBuilder cfgBuilder;

  /**A Panel which contains the table to select some projectPaths. */
  //private GuiShellMngBuildIfc dialogWindowProps;
  private GralWindow dialogWindowProps;

  /**Some dialog widget elements. */
  private GralWidget dialogFieldName, dialogFieldDatapath, dialogFieldText, dialogFieldFormat
    , dialogFieldShow, dialogFieldAction
    , dialogFieldLine, dialogFieldColumn, dialogFieldHeight, dialogFieldWidth;
  
  private GralWidget dialogButtonOk, dialogButtonEsc;
  
  /**Coordinates while left mouse pressed. */
  private int xMouse0, yMouse0;
  
  /**Coordinates while mouse moved, capture on release button. */
  private int xMouse, yMouse;
  
  private boolean bWidgetMoving = false;
  
  GralWidget widgdInDialog = null;
  
  public GralCfgDesigner(GralCfgBuilder cfgBuilder, GralWidgetMng mng, LogMessage log)
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
    mng.setPosition(-30, GralGridPos.size +30, -40, GralGridPos.size +40, 1, 'r');
    dialogWindowProps = mng.createWindow("widgetEdit", "Widget Properties", GralWindow.windConcurrently);
    mng.setPositionSize(0, 0, 3, 34, 'd');
    dialogFieldName = mng.addTextField("name", true, "name", 't');
    dialogFieldDatapath = mng.addTextField("dataPath", true, "data", 't');
    dialogFieldText = mng.addTextField("text", true, "text", 't');
    dialogFieldFormat = mng.addTextField("format", true, "format", 't');
    dialogFieldShow = mng.addTextField("show", true, "show method", 't');
    dialogFieldAction = mng.addTextField("action", true, "action method", 't');
    mng.setPositionSize(19, 2, 3, 5, 'r');
    dialogFieldLine = mng.addTextField("line", true, "pos-y", 't');
    //mng.addText(", ", 'B', 0);
    dialogFieldColumn = mng.addTextField("column", true, "pos-x", 't');
    //mng.addText("   ", 'B', 0);
    dialogFieldHeight = mng.addTextField("height", true, "size-y", 't');
    //mng.addText(" x ", 'B', 0);
    dialogFieldWidth = mng.addTextField("width", true, "size-x", 't');
    mng.setPositionSize(23, 2, 3, 8, 'r');
    dialogButtonEsc = mng.addButton("esc", actionEsc, null, null, null, "esc");
    dialogButtonOk = mng.addButton("del", actionDel, null, null, null, "del");
    dialogButtonOk = mng.addButton("OK", actionOk, null, null, null, "OK");
  }
  
  

  public void pressedLeftMouseDownForDesign(GralWidget widgd, GralRectangle xy)
  {
    xMouse0 = xy.x;
    yMouse0 = xy.y;
    bWidgetMoving = true;
  }
  
  
  public void releaseLeftMouseForDesign(GralWidget widgd, GralRectangle xy, boolean bCopy)
  {
    if(bWidgetMoving){
      bWidgetMoving = false;
      int dxPixel = xy.x - xMouse0;
      int dyPixel = xy.y - yMouse0;
      
      float dxGridf = (float)dxPixel / mng.propertiesGui.xPixelUnit();
      float dyGridf = (float)dyPixel / mng.propertiesGui.yPixelUnit();
      
      int dxGrid = (int)(dxGridf >0 ? dxGridf + 0.5f : dxGridf - 0.5f);
      int dyGrid = (int)(dyGridf >0 ? dyGridf + 0.5f : dyGridf - 0.5f);
      
      GralCfgElement cfge = (GralCfgElement)widgd.getCfgElement();
      if(cfge !=null){
        String sPanel = cfge.position.panel;  //Note: The cloned Object maybe empty here before buildWidget() is called
        int xPosAct = cfge.position.xPos;
        int yPosAct = cfge.position.yPos;
        if(bCopy){
          GralCfgElement cfgn = cfge.clone(); //cfgBuilder.newCfgElement(cfge);
          cfge = cfgn;
        }
        cfge.positionInput.xPos = xPosAct + dxGrid; 
        cfge.positionInput.yPos = yPosAct + dyGrid; 
        if(!bCopy){
          widgd.remove();
          //mng.remove(widgd);  //remove the widget.
        }
        mng.selectPanel(sPanel);
        cfgBuilder.buildWidget(cfge);
      }
      
    }
    
  }

  
  public void editFieldProperties(GralWidget widgd, GralRectangle xy)
  { //if(widgdInDialog == null){
      widgdInDialog = widgd;
      GralCfgElement cfge = (GralCfgElement)widgd.getCfgElement();
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
        dialogFieldName.setValue(GralPanelMngWorking_ifc.cmdSet, 0, sName ==null ? "" : sName);
        dialogFieldDatapath.setValue(GralPanelMngWorking_ifc.cmdSet, 0, sDataPath == null ? "" : sDataPath);
        dialogFieldText.setValue(GralPanelMngWorking_ifc.cmdSet, 0, sText ==null ? "" : sText);
        dialogFieldFormat.setValue(GralPanelMngWorking_ifc.cmdSet, 0, sFormat ==null ? "" : sFormat);
        dialogFieldShow.setValue(GralPanelMngWorking_ifc.cmdSet, 0, sShowMethod ==null ? "" : sShowMethod);
        dialogFieldAction.setValue(GralPanelMngWorking_ifc.cmdSet, 0, sActionMethod ==null ? "" : sActionMethod);
        dialogFieldLine.setValue(GralPanelMngWorking_ifc.cmdSet, 0, sLine);
        dialogFieldColumn.setValue(GralPanelMngWorking_ifc.cmdSet, 0, sColumn);
        dialogFieldHeight.setValue(GralPanelMngWorking_ifc.cmdSet, 0, sHeight);
        dialogFieldWidth.setValue(GralPanelMngWorking_ifc.cmdSet, 0, sWidth);
      } else {
        dialogFieldName.setValue(GralPanelMngWorking_ifc.cmdSet, 0, "ERROR cfge");
      }
    //}
    //dialogWindowProps.posWindow.setPosition(widgd.pos, widgd.pos.y +2, GralGridPos.size+30, widgd.pos.x, GralGridPos.size+40, 1, 'r' );
    //dialogWindowProps.setWindowVisible(true);
    //use manager to position.
    mng.setWindowsVisible(dialogWindowProps, dialogWindowProps.pos);
  }
  
  private GralUserAction actionOk = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget widgd, Object... params)
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
        
        GralCfgElement cfge = (GralCfgElement)widgdInDialog.getCfgElement();
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
      return true;
    }
    
  };

  
  
  
  private GralUserAction actionDel = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget widgd, Object... params)
    { //note widgd is the OK-button!
      if(widgdInDialog !=null){
        //widgdInDialog.remove();
        mng.remove(widgdInDialog);  //remove the widget.
        dialogWindowProps.setWindowVisible(false);
        widgdInDialog = null;
        return true;
      } else {
        return false;
      }
    }
    
  };

  
  
  
  
  
  
  private GralUserAction actionEsc = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget widgd, Object... params)
    { dialogWindowProps.setWindowVisible(false);
      widgdInDialog = null;
      return true;
    }
    
  };

  
  void stop(){}

}

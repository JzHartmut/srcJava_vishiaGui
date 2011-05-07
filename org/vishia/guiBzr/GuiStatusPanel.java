package org.vishia.guiBzr;

import org.vishia.mainGui.GuiPanelMngBuildIfc;
import org.vishia.mainGui.GuiPanelMngWorkingIfc;
import org.vishia.mainGui.GuiShellMngBuildIfc;
import org.vishia.mainGui.SwitchExclusiveButtonMng;
import org.vishia.mainGui.UserActionGui;
import org.vishia.mainGui.WidgetDescriptor;
import org.vishia.mainGuiSwt.InfoBox;

/**This class contains all data and methods of the status (select) panel.
 * 
 * @author Hartmut Schorrig
 *
 */
public class GuiStatusPanel
{

  /**Aggregation to the main data of the GUI. */
  final MainData mainData;

  /**Aggregation to the build interface of the manager where the panel is member of. */
  final GuiPanelMngBuildIfc panelBuildifc;
  
  
  /**Widget to select the project path. 
   * The project is a user-project which contains one or more source archive-sandboxes.
   * 
   * */
  final WidgetDescriptor widgdProjektpath = new WidgetDescriptor("projectPath", 'E'); 
  
  
  /**A Panel which contains the table to select some projectPaths. */
  private GuiShellMngBuildIfc selectorProjectPath;
  
  /**The table (list) which contains the selectable project paths. */
  private WidgetDescriptor selectorProjectPathTable;
  
  
  private GuiPanelMngBuildIfc[] bzrComponentBox = new GuiPanelMngBuildIfc[10]; 

  private InfoBox testDialogBox;

  
  /**Instance for some buttons to exclude switch on only for one button.
   * GUI-concept: This is an alternative to TODO
   * 
   */
  private SwitchExclusiveButtonMng switchExcluder = new SwitchExclusiveButtonMng();
  

  public GuiStatusPanel(MainData mainData, GuiPanelMngBuildIfc panelBuildifc)
  {
    this.panelBuildifc = panelBuildifc;
    this.mainData = mainData;
  }
  
  /**Initializes the graphic. 
   * It will be called in the GUI-Thread.
   */
  void initGui()
  { int xposProjectPath = 0, yposProjectPath=5; 
  
    panelBuildifc.selectPanel("Select");
    panelBuildifc.setPosition(yposProjectPath, xposProjectPath, -2, 70, 'r');
    panelBuildifc.addTextField(widgdProjektpath, true, "Project path", 't');
    //Test only in one Project
    String sPrjPath = "/home/hartmut/vishia/Java2C/sf/Java2C";
    widgdProjektpath.setValue(GuiPanelMngWorkingIfc.cmdInsert, 0,sPrjPath);
    mainData.currPrj = new DataProject(sPrjPath);
    
    panelBuildifc.setPosition(-1, -1, -2, 2, 'r');
    panelBuildifc.addButton("selectProjectPath", selectProjectPath, "c", "s", "d", "?");
    ///
    //WidgetDescriptor widgdRefresh = new WidgetDescriptor("refresh", 'B');
    //widgdRefresh.setAction(refreshProjectBzrComponents);
    panelBuildifc.setPosition(-1, -1, -3, 10, 'r');
    panelBuildifc.addButton("Brefresh", refreshProjectBzrComponents, "c", "s", "d", "Get/Refresh");

    String[] lines = {"1", "2"};
    
    testDialogBox = new InfoBox(mainData.guifc.getitsGraphicFrame(), "Title", lines, true);

    panelBuildifc.setPosition(yposProjectPath, xposProjectPath, 20, 60, 'r');
    selectorProjectPath = panelBuildifc.createWindow(null, false);
    int[] columnWidths = {40, 10};
    selectorProjectPath.setPosition(0, 0, 10, 60, 'd');
    selectorProjectPathTable = selectorProjectPath.addTable("selectProjectPath", 20, columnWidths);
    selectorProjectPathTable.setAction(actionSelectorProjectPathTable);
    selectorProjectPath.setPosition(20, 0, -3, 10, 'r');
    selectorProjectPath.addButton("closeProjectBzrComponents", actionCloseProjectBzrComponents, "","","","ok");
    
    mainData.panelAccess.setInfo(selectorProjectPathTable, GuiPanelMngWorkingIfc.cmdInsert, 0,"/home/hartmut/vishia/GUI");
    mainData.panelAccess.setInfo(selectorProjectPathTable, GuiPanelMngWorkingIfc.cmdInsert, Integer.MAX_VALUE,"line2");
    
  }
  
  
  private final UserActionGui selectProjectPath = new UserActionGui()
  { 
    public void userActionGui(String sCmd, WidgetDescriptor<?> widgetInfos, Object... values)
    {
      //testDialogBox.open();
      selectorProjectPath.setWindowVisible(true);
    }
  };
  
  
  
  private final UserActionGui actionSelectorProjectPathTable = new UserActionGui()
  { 
    public void userActionGui(String sCmd, WidgetDescriptor<?> widgetInfos, Object... values)
    {
      if(sCmd.equals("ok")){
        String sPath = (String)values[0];
        widgdProjektpath.setValue(GuiPanelMngWorkingIfc.cmdInsert, 0, sPath);
      }
      selectorProjectPath.setWindowVisible(false);
    }
  };
  
  
  private final UserActionGui actionCloseProjectBzrComponents = new UserActionGui()
  { 
    public void userActionGui(String sCmd, WidgetDescriptor<?> widgetInfos, Object... values)
    {
      selectorProjectPath.setWindowVisible(false);
    }
  };

  
  private final UserActionGui refreshProjectBzrComponents = new UserActionGui()
  { 
    public void userActionGui(String sCmd, WidgetDescriptor<?> widgetInfos, Object... values)
    {
      //String sProjectPath = dlgAccess.getValue("projectPath");
      String sProjectPath = widgdProjektpath.getValue();
      panelBuildifc.selectPanel("Select");
      for(int ii=0; ii< bzrComponentBox.length; ++ii){
        GuiPanelMngBuildIfc item = bzrComponentBox[ii]; 
        if(item !=null){ 
          bzrComponentBox[ii] = null;
          panelBuildifc.remove(item); 
        }
      }
      mainData.getterStatus.getBzrLocations(sProjectPath);
      int yPosComponents = 10;
      int iComponent = 0;
      //Only one of the switch buttons are checked. If another button is pressed, it should be deselect.
      //The switchExcluder helps to do so. 
      //It contains a special method, which captures the text of the last pressed switch button. 
      WidgetDescriptor switchButton;
      for(DataCmpn data: mainData.currPrj.data){
        String sName = data.getBzrLocationDir().getName();
        panelBuildifc.selectPanel("Select");
        panelBuildifc.setPosition(yPosComponents, 1, 2, 70, 'r');
        GuiPanelMngBuildIfc box;
        bzrComponentBox[iComponent] = box = panelBuildifc.createCompositeBox();
        box.selectPanel("$");
        box.setPosition(0, 0, 2, 2, 'r');
        switchButton = box.addSwitchButton("selectMain", actionSelectCmpn, "", null, data.sNameCmpn, "", "wh", "rd");
        switchExcluder.add(switchButton);
        box.setPosition(0, 6, 2, 15, 'r');
        box.addText(sName, 'B', 0);
        String sRev = "Rev. ";
        if(data.nrTopRev == data.nrSboxRev){
          sRev = "Rev. " + data.nrSboxRev + " uptodate ";
        } else {
          sRev = "Rev. " + data.nrSboxRev + " / "+ data.nrTopRev;
        }
        box.addText(sRev, 'B', 0x0);
        String sBzrStatus = data.uBzrStatusOutput.toString();
        boolean isModified = sBzrStatus.indexOf("modified:") >=0;
        boolean hasNew = sBzrStatus.indexOf("non-versioned:") >=0;
        if(isModified){
          //bzrComponentBox[iComponent].setPosition(0, 40, 2, 10, 'r');
          box.addText("- modified", 'B', 0xff0000);
        } else if(hasNew){
          //bzrComponentBox[iComponent].setPosition(0, 40, 2, 10, 'r');
          box.addText("- new Files", 'B', 0xff0000);
        } else {
          box.addText("- no changes", 'B', 0x00ff00);
        }
        yPosComponents +=2;
        iComponent +=1;
      }
    }
  };

  private final UserActionGui actionSelectCmpn = new UserActionGui()
  { 
    public void userActionGui(String sCmd, WidgetDescriptor<?> widgd, Object... values)
    {
      mainData.currCmpn = mainData.currPrj.selectComponent(widgd.sDataPath);
      stop();
      //call the exclusion of the other button:
      switchExcluder.switchAction.userActionGui(sCmd, widgd, values);
    }
  };
  
  
  void stop(){}
}

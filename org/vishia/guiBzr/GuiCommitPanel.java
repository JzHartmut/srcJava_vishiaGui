package org.vishia.guiBzr;

import java.io.File;

import org.vishia.mainCmd.Report;
import org.vishia.mainGui.GuiPanelMngBuildIfc;
import org.vishia.mainGui.UserActionGui;
import org.vishia.mainGui.WidgetDescriptor;
import org.vishia.util.FileSystem;

public class GuiCommitPanel
{

  /**Aggregation to the main data of the GUI. */
  final MainData mainData;

  /**Aggregation to the build interface of the manager where the panel is member of. */
  final GuiPanelMngBuildIfc panelBuildifc;
  
  final StringBuilder uCommitOut =  new StringBuilder();
  
  final WidgetDescriptor widgdCommitText = new WidgetDescriptor("commitText", 'T');
  
  public GuiCommitPanel(MainData mainData, GuiPanelMngBuildIfc panelBuildifc)
  {
    this.panelBuildifc = panelBuildifc;
    this.mainData = mainData;
  }

  /**Initializes the graphic. 
   * It will be called in the GUI-Thread.
   */
  void initGui()
  { panelBuildifc.selectPanel("Commit");
    panelBuildifc.setPosition(2,0, 30, 70, 'r');
    panelBuildifc.addTextBox(widgdCommitText, true, null, ' '); // "commit Text", 't');
    
    panelBuildifc.setPosition(33,0, 3, 10, 'r');
    panelBuildifc.addButton("commit", actionCommit, "commit", null, null, "commit");
    
  }
    
  
  
  
  private final UserActionGui actionCommit = new UserActionGui()
  { 
    public void userActionGui(String sCmdGui, WidgetDescriptor widgetInfos, Object... values)
    {
      File fileCommitText = mainData.mainAction.getContentofCommitText();
      String sCmd = "bzr commit -F " + fileCommitText.getAbsolutePath();
      mainData.cmdMng.directory(mainData.currCmpn.fileBzrLocation);
      mainData.mainCmdifc.executeCmdLine(mainData.cmdMng, "bzr status", null, Report.info, uCommitOut, uCommitOut);
      uCommitOut.setLength(0);
      mainData.mainCmdifc.executeCmdLine(mainData.cmdMng, sCmd, null, Report.info, uCommitOut, uCommitOut);
      stop();
    }
  };


  void stop(){}
  
}

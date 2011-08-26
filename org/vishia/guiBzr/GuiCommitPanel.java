package org.vishia.guiBzr;

import java.io.File;

import org.vishia.gral.gridPanel.GuiPanelMngBuildIfc;
import org.vishia.gral.ifc.UserActionGui;
import org.vishia.gral.ifc.WidgetDescriptor;
import org.vishia.mainCmd.Report;
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
      StringBuilder sCmdCommit = new StringBuilder(mainData.cfg.indexCmds.get("commit"));
      int posFile = sCmdCommit.indexOf("$CommitDescrFile");
      if(posFile >=0){
      	sCmdCommit.replace(posFile, 16, fileCommitText.getAbsolutePath());
      } else {
      	//what todo
      }
      //String sCmd = "bzr commit -F " + fileCommitText.getAbsolutePath();
      mainData.cmdExec.setCurrentDir(mainData.currCmpn.fileBzrLocation);
      String sCmdStatus = mainData.cfg.indexCmds.get("status");
      mainData.cmdExec.execWait(sCmdStatus, null, uCommitOut, uCommitOut);
      uCommitOut.setLength(0);
      mainData.cmdExec.execWait(sCmdCommit.toString(), null, uCommitOut, uCommitOut);
      stop();
    }
  };


  void stop(){}
  
}

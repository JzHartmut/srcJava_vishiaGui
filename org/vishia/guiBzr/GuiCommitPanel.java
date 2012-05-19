package org.vishia.guiBzr;

import java.io.File;

import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.mainCmd.Report;
import org.vishia.util.FileSystem;

public class GuiCommitPanel
{

  /**Aggregation to the main data of the GUI. */
  final MainData mainData;

  /**Aggregation to the build interface of the manager where the panel is member of. */
  final GralMngBuild_ifc panelBuildifc;
  
  final StringBuilder uCommitOut =  new StringBuilder();
  
  GralWidget widgdCommitText; // = new GralWidget("commitText", 'T');
  
  public GuiCommitPanel(MainData mainData, GralMngBuild_ifc panelBuildifc)
  {
    this.panelBuildifc = panelBuildifc;
    this.mainData = mainData;
  }

  /**Initializes the graphic. 
   * It will be called in the GUI-Thread.
   */
  void initGui()
  { panelBuildifc.selectPanel("Commit");
    panelBuildifc.setPositionSize(2,0, 30, 70, 'r');
    widgdCommitText = panelBuildifc.addTextBox("commitText", true, null, ' '); // "commit Text", 't');
    
    panelBuildifc.setPositionSize(33,0, 3, 10, 'r');
    panelBuildifc.addButton("commit", actionCommit, "commit", null, null, "commit");
    
  }
    
  
  
  
  private final GralUserAction actionCommit = new GralUserAction()
  { 
    public boolean userActionGui(String sCmdGui, GralWidget widgetInfos, Object... values)
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
      mainData.cmdExec.execute(sCmdStatus, null, uCommitOut, uCommitOut);
      uCommitOut.setLength(0);
      mainData.cmdExec.execute(sCmdCommit.toString(), null, uCommitOut, uCommitOut);
      stop();
      return true;
    }
  };


  void stop(){}
  
}

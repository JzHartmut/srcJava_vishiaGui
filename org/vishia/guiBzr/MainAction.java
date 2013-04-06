package org.vishia.guiBzr;

import java.io.File;

import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;

public class MainAction
{
  final MainData mainData;
  
  final GuiStatusPanel guiStatusPanel;

  final GuiCommitPanel guiCommitPanel;
  
  final GuiFilesDiffPanel guiFilesDiffPanel;
  
  final PanelOutput panelOutput;
  
  MainAction(MainData mainData, GuiStatusPanel guiStatusPanel
      , final GuiCommitPanel guiCommitPanel, 
      final GuiFilesDiffPanel guiFilesDiffPanel
      , PanelOutput panelOutput
      )
  {
    this.mainData = mainData;
    this.guiStatusPanel = guiStatusPanel;
    this.guiCommitPanel = guiCommitPanel;
    this.guiFilesDiffPanel = guiFilesDiffPanel;
    this.panelOutput = panelOutput;  
  }
  
  Runnable initNewComponent = new Runnable()
  {
    @Override public void run()
    { mainData.getterStatus.initListFiles();
      guiFilesDiffPanel.fillFileTable(mainData.currCmpn);
    }
  };
  
  
  /**Stores the commit text in a file and returns this File-object.
   * Reads the text of the commit text box in the commit panel
   * and writes it to the file "_bzrCommit.txt" in the current source component folder.
   * @return null if the text in the box is empty, else the File where the commit text is stored.
   */
  File getContentofCommitText()
  { final File fileCommitText;
    String sCommitText = guiCommitPanel.widgdCommitText.getValue();
    if(sCommitText.trim().length()>0 ){
      fileCommitText = new File(mainData.currCmpn.dirWorkingtree, "_bzrCommit.txt");
      FileSystem.writeFile(sCommitText, fileCommitText);
    } else {
      fileCommitText = null;
    }
    return fileCommitText;
  }
  

  
  String findBazaarExe(){
    String sBzrExe = "D:/Progs/Bazaar/bzr.exe";
    File bzrExe = new File(sBzrExe);
    if(bzrExe.exists()) return sBzrExe;
    sBzrExe = "D:/Programme/Bazaar/bzr.exe";
    bzrExe = new File(sBzrExe);
    if(bzrExe.exists()) return sBzrExe;
    throw new IllegalArgumentException("BzrGui.MainAction - findBazaarExe; don't find bazaar.exe");
  }
  
  
  
  
  void revertWithTimestamps(){
    String sBazaarExe = findBazaarExe();
    try{
      File dirWorkingTree = mainData.currCmpn.dirWorkingtree;
      File fileBzr = new File(dirWorkingTree, ".bzr");
      //search whether a .bzr or .bzr.bat exists and change to parent dir till it is found.
      if(!fileBzr.exists()) throw new IllegalArgumentException(
          "BzrRevertAllWithTimestamp - arg1 does not refer to a .bzr;");
      //
      File dirParent = dirWorkingTree.getParentFile();
      mainData.cmdExec.setCurrentDir(dirParent);
      File tempBzrDir = new File(dirParent, "tmpBzr");
      if(tempBzrDir.exists()){
        if(!FileSystem.cleandir(tempBzrDir)) throw new IllegalArgumentException(
            "BzrRevertAllWithTimestamp - temp bzr dir can't remove -try it manually; " + tempBzrDir.getAbsolutePath());
      }
      File fileBzrTmp = new File(tempBzrDir, ".bzr");
      FileSystem.mkDirPath(fileBzrTmp);   //creates the tempBzrDir if not exists
      //Now move cmpn/.bzr to tmpBzr/.bzr
      if(!fileBzr.renameTo(fileBzrTmp))  throw new IllegalArgumentException(
          "BzrRevertAllWithTimestamp - .bzr can't move -check manually; " + fileBzrTmp.getAbsolutePath());
      if(!FileSystem.rmdir(dirWorkingTree)){
        boolean bMovedBack = fileBzrTmp.renameTo(fileBzr);
        String text = "BzrRevertAllWithTimestamp - can't delete working tree -check manually; " + dirWorkingTree.getAbsolutePath();
        if(!bMovedBack){
          text += "; Note: The .bzr is yet temporary moved to " + fileBzrTmp.getAbsolutePath();  
        }
        throw new IllegalArgumentException(text);
      }
      //call the bazaar mainData.cmdExec:
      String[] cmdarg = new String[5];
      cmdarg[0] = sBazaarExe;
      cmdarg[1] = "export";
      cmdarg[2] = dirWorkingTree.getAbsolutePath();
      cmdarg[3] = tempBzrDir.getAbsolutePath();
      cmdarg[4] = "--per-file-timestamps";
      mainData.cmdExec.execute(cmdarg, null, System.err, System.err, false);
      //it is reverted.
      boolean bMovedBack = fileBzrTmp.renameTo(fileBzr);
      if(!bMovedBack){
        throw new IllegalArgumentException("BzrRevertAllWithTimestamp - The .bzr is yet temporary moved to;" + fileBzrTmp.getAbsolutePath());
      }
      //Not moves the .bzr return
      //assemble the bzrGetCmpn text.
      StringBuilder uVersion = new StringBuilder();
      String sCurrDirName = dirParent.getName();
      //main.mainData.cmdExec.execute("D:/Progs/Bazaar/bzr.exe log -l 1", null, uVersion, null);
      /*
       * I don't know, it does not work:
      String[] mainData.cmdExec = {"D:/Progs/Bazaar/bzr.exe", "version-info --custom --template=\"_bzrGetCmpn " + sCurrDirName + " {revision_id}\\n\""};
      main.mainData.cmdExec.execute(mainData.cmdExec, null, uVersion, null, false);
      main.mainData.cmdExec.execute("D:/Progs/Bazaar/bzr.exe version-info --custom --template=\"_bzrGetCmpn " + sCurrDirName + " {revision_id}\"", null, uVersion, null);
      */
      //instead:
      mainData.cmdExec.setCurrentDir(dirWorkingTree);
      mainData.cmdExec.execute("D:/Progs/Bazaar/bzr.exe version-info", null, uVersion, null);
      //uVersion contains the output of bzr version-info. The second line is the revision id!
      int pos1 = uVersion.indexOf("revision-id:");
      int pos2 = uVersion.indexOf("\n", pos1+12);
      String sRevisionId = uVersion.substring(pos1+13, pos2).trim();
      String sGetCmpn = "bzrGetCmpn " + sCurrDirName + " " + sRevisionId + "\n"; 
      System.out.println("jbatch/BazaarExplorer.java - write " + sGetCmpn);
      File fileVersion = new File(dirParent, "_bzrGetCmpn-" + sCurrDirName + ".bat");
      FileSystem.writeFile(sGetCmpn, fileVersion);
      //Assert.stop();
    } catch(Exception exc){
      mainData.mainCmdifc.writeError(exc.getMessage());
    }
  }
  
  
  /**Gets the status for the Component of the actual line, refresh the line. */
  GralUserAction actionRevertWithTimestamp = new GralUserAction("actionRevertWithTimestamp"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgi, Object... oArgs){
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        if(mainData.currTableline !=null){
          revertWithTimestamps();
          //mainData.currTableline.setCellText(formatter.getContent(), 1);
        }
        return true;
      } else return false;
    }
  };

  
  

  
}

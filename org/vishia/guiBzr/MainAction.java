package org.vishia.guiBzr;

import java.io.File;

import org.vishia.util.FileSystem;

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
      fileCommitText = new File(mainData.currCmpn.fileBzrLocation, "_bzrCommit.txt");
      FileSystem.writeFile(sCommitText, fileCommitText);
    } else {
      fileCommitText = null;
    }
    return fileCommitText;
  }
  

  
}

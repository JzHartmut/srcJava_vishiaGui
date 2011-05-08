package org.vishia.guiBzr;

public class MainAction
{
  final MainData mainData;
  
  final GuiStatusPanel guiStatusPanel;

  final GuiCommitPanel guiCommitPanel;
  
  final GuiFilesDiffPanel guiFilesDiffPanel;
  

  
  MainAction(MainData mainData, GuiStatusPanel guiStatusPanel
      , final GuiCommitPanel guiCommitPanel, 
      final GuiFilesDiffPanel guiFilesDiffPanel
      )
  {
    this.mainData = mainData;
    this.guiStatusPanel = guiStatusPanel;
    this.guiCommitPanel = guiCommitPanel;
    this.guiFilesDiffPanel = guiFilesDiffPanel;
      
  }
  
  Runnable initNewComponent = new Runnable()
  {
    @Override public void run()
    { mainData.getterStatus.initListFiles();
      guiFilesDiffPanel.fillFileTable(mainData.currCmpn);
    }
  };
  
  

  

  
}

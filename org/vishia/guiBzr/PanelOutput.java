package org.vishia.guiBzr;

import java.io.File;

import org.vishia.gral.GuiPanelMngBuildIfc;
import org.vishia.gral.UserActionGui;
import org.vishia.gral.WidgetDescriptor;
import org.vishia.mainCmd.Report;

public class PanelOutput
{
  /**Version, able to read as hex yyyymmdd.
   * Changes:
   * <ul>
   * <li>2011-06-17 Hartmut: Created. It is the output of some commands.
   * </ul>
   */
  public final static int version = 0x20110617;

  /**Aggregation to the main data of the GUI. */
  final MainData mainData;

  /**Aggregation to the build interface of the manager where the panel is member of. */
  final GuiPanelMngBuildIfc panelBuildifc;
  
  final StringBuilder uCommitOut =  new StringBuilder();
  
  final WidgetDescriptor widgdOutputText = new WidgetDescriptor("OutputText", 'T');
  
  public PanelOutput(MainData mainData, GuiPanelMngBuildIfc panelBuildifc)
  {
    this.panelBuildifc = panelBuildifc;
    this.mainData = mainData;
  }

  /**Initializes the graphic. 
   * It will be called in the GUI-Thread.
   */
  void initGui()
  { panelBuildifc.selectPanel("Output");
    panelBuildifc.setPosition(2,0, 30, 70, 'r');
    panelBuildifc.addTextBox(widgdOutputText, true, null, ' '); // "commit Text", 't');
  }
    
  
  
  

  void stop(){}
  
  
}

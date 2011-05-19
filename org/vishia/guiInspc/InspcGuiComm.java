package org.vishia.guiInspc;

import java.util.LinkedList;
import java.util.List;

import org.vishia.inspectorAccessor.InspcAccessor;
import org.vishia.mainCmd.Report;

/**The communication manager. */
public class InspcGuiComm
{
 
  /**To Output log informations. The ouput will be done in the output area of the graphic. */
  private final Report console;

  private final InspcAccessor inspcAccessor;
  
  /**List of all Panels, which have values to show repeating.
   * Any Panel can be an independent window. Any panel may have other values to show.
   * But any panel can select more as one tabs (tabPanel). Then it will be select which values to show.
   */
  private final List<InspcGuiPanelContent> listPanels = new LinkedList<InspcGuiPanelContent>();
  
  
  
  InspcGuiComm(Report console)
  {
    this.console = console;
    this.inspcAccessor = new InspcAccessor();
  }
  
  
  void addPanel(InspcGuiPanelContent panel)
  {
    listPanels.add(panel);
  }
  
  
  void openComm()
  {
    inspcAccessor.open(null)
  }
  
}

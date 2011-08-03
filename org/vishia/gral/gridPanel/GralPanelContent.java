package org.vishia.gral.gridPanel;

import java.util.Queue;

import org.vishia.gral.ifc.GuiPlugUser_ifc;
import org.vishia.gral.ifc.WidgetDescriptor;

/**This class contains the info about the values to show in one panel.
 * 
 * @author Hartmut Schorrig
 *
 */
public class GralPanelContent
{
  /**The actual widgets in that panel. It may a sub-panel or changed content. The list can be changed. */
  public Queue<WidgetDescriptor> widgets;
  
  /**A new list of actual widgets, set while select another tab etc. The reference may be set 
   * in the GUI-Thread (GUI-listener). The communication-manager thread reads whether it isn't null,
   * processes it and sets this reference to null if it is processed. */
  public Queue<WidgetDescriptor> newWidgets;
  

  /**Some actions may be processed by a user implementation. */
  private final GuiPlugUser_ifc user;
  

  
  
  
  public GralPanelContent(GuiPlugUser_ifc user)
  { this.user = user;
  }


  /**Changes the communication data base because another tab was activated on the TabPanel. 
   * @param newWidgets The new widgets.
   */
  void changeWidgets(Queue<WidgetDescriptor> newWidgetsP)
  {
    this.newWidgets = newWidgetsP; //signal for other thread, there are new one.
    user.changedView("unknown yet", 0);
  }
  
  
  public PanelActivatedGui actionPanelActivate = new PanelActivatedGui()
  { @Override public void panelActivatedGui(Queue<WidgetDescriptor> widgetsP)
    {  changeWidgets(widgetsP);
    }
  };

  
}



package org.vishia.guiInspc;

import java.util.List;
import java.util.Queue;

import org.vishia.gral.PanelActivatedGui;
import org.vishia.gral.WidgetDescriptor;

/**This class contains the info about the values to show in one panel.
 * 
 * @author Hartmut Schorrig
 *
 */
public class InspcGuiPanelContent
{
  /**The actual widgets in that panel. It may a sub-panel or changed content. The list can be changed. */
  Queue<WidgetDescriptor> widgets;
  
  /**A new list of actual widgets, set while select another tab etc. The reference may be set 
   * in the GUI-Thread (GUI-listener). The communication-manager thread reads whether it isn't null,
   * processes it and sets this reference to null if it is processed. */
  Queue<WidgetDescriptor> newWidgets;
  

  /**Some actions may be processed by a user implementation. */
  final InspcPlugUser_ifc user;
  

  
  
  
  public InspcGuiPanelContent(InspcPlugUser_ifc user)
  { this.user = user;
  }


  /**Changes the communication data base because another tab was activated on the TabPanel. 
   * @param widgets The new widgets.
   */
  void changeWidgets(Queue<WidgetDescriptor> widgets)
  {
    newWidgets = widgets; //signal for other thread, there are new one.
    user.changedView("unknown yet", 0);
  }
  
  
  PanelActivatedGui actionPanelActivate = new PanelActivatedGui()
  { @Override public void panelActivatedGui(Queue<WidgetDescriptor> widgets)
    {  changeWidgets(widgets);
    }
  };

  
}



package org.vishia.guiInspc;

import java.util.List;

import org.vishia.mainGui.PanelActivatedGui;
import org.vishia.mainGui.WidgetDescriptor;

/**This class contains the info about the values to show in one panel.
 * 
 * @author Hartmut Schorrig
 *
 */
public class InspcGuiPanelContent
{
  /**The actual widgets in that panel. It may a sub-panel or changed content. The list can be changed. */
  List<WidgetDescriptor> widgets;
  
  /**A new list of actual widgets, set while select another tab etc. The reference may be set 
   * in the GUI-Thread (GUI-listener). The communication-manager thread reads whether it isn't null,
   * processes it and sets this reference to null if it is processed. */
  List<WidgetDescriptor> newWidgets;
  

  
  /**Changes the communication data base because another tab was activated on the TabPanel. 
   * @param widgets The new widgets.
   */
  void changeWidgets(List<WidgetDescriptor> widgets)
  {
    newWidgets = widgets; //signal for other thread, there are new one.
  }
  
  
  PanelActivatedGui actionPanelActivate = new PanelActivatedGui()
  { @Override public void panelActivatedGui(List<WidgetDescriptor> widgets)
    {  changeWidgets(widgets);
    }
  };

  
}

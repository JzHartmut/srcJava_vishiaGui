package org.vishia.gral.ifc;

import java.util.List;

import org.vishia.gral.base.GralCanvasStorage;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.impl_ifc.GralWidgetImpl_ifc;

/**Abstraction of an {@link GralPanelContent} which may also the whole display
 * implemented with {@link org.vishia.gral.base.GralScreen}.
 * @author hartmut Schorrig
 * @since 2022-08
 *
 */
public interface GralPanel_ifc extends GralWidget_ifc {

  
  void setPrimaryWidget(GralWidget widg);
  
  GralWidget getPanelWidget();
  
  List<GralWidget> getWidgetList();
  
  GralPos pos();
  
  GralCanvasStorage canvas();
  
  void addWidget(GralWidget widg, boolean toResize);

  void removeWidget(GralWidget widg);
  
}

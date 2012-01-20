package org.vishia.gral.base;

import org.vishia.gral.ifc.GralColor;

/**This class defines the methods which are able to invoke from a widget change request. 
 * It is used only internally, it should not be invoked by a user's application. 
 * The class is public because it should be known by the graphical implementation level,
 * which may be resident in another package. The methods must not invoke in another thread than
 * the graphic thread. The implementation of this interface will be done in the graphical implementation layer.
 * <br><br>
 * The ObjectModelDiagram may shown the relations:
 * <img src="../../../../img/GralWidgetGthreadSet_ifc_gral.png"><br>
 * This interface should be implemented in any widget deployment. It is provide by the 
 * {@link org.vishia.gral.ifc.GralWidget_ifc#getGthreadSetifc()} method.
 * It is used in the overridden method 
 * {@link GralWidgetMng#setInfoGthread(GralWidget_ifc, int, int, Object, Object)} of the graphical
 * implementation layer. Any change request is determined by its {@link #cmd} code, which is defined
 * in {@link org.vishia.gral.ifc.GralPanelMngWorking_ifc}. A switch-case calls the proper methods
 * from this interface.
 * <br> 
 * @author Hartmut Schorrig
 *
 */
public interface GralWidgetGthreadSet_ifc {
  
  void clearGthread();
  
  void redrawGthread();
  
  void setTextGthread(String text, Object data);
  
  void insertGthread(int pos, Object visibleInfo, Object data);
  
  void setBackGroundColorGthread(GralColor color);

  void setForeGroundColorGthread(GralColor color);
}

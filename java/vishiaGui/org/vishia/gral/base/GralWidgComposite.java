package org.vishia.gral.base;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralWidgetBase_ifc;
import org.vishia.util.Debugutil;

public final class GralWidgComposite {
  /**Version, history and license.
   * <ul>
   * <li>2023-04-15 Hartmut new It was now obviously, that comprehensive widgets are necessary,
   *   they need the a widget List.
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final String sVersion = "2022-09-13";

  protected final GralWidgetBase widg; 
  
  /**List of all widgets which are contained in this panel.
   * This list is used in the communication thread to update the content of all widgets in the panel.
   */
  protected List<GralWidgetBase> widgetList = new LinkedList<GralWidgetBase>();

  public List<GralWidgetBase> widgetsToResize = new LinkedList<GralWidgetBase>();

  /**The widget which should be focused if the panel is focused.
   * It is possible to set any actual widget to store the focus situation,
   * It is possible too to have only one widget to focus. if the panel gets the focus. */
  protected GralWidgetBase_ifc primaryWidget;

  protected final Map<String, GralWidgetBase> idxWidgets = new TreeMap<String, GralWidgetBase>();


  
  public GralWidgComposite ( GralWidgetBase widg ) {
    this.widg = widg;
  }

  /*package private*/
  /**Adds a widget to its panel. This method will be called in {@link GralWidgetBase#initPosAndRegisterWidget(GralPos)}
   * either on creation the GralWidget with a given position String or on {@link GralWidget#setToPanel(GralMngBuild_ifc)}
   * with the given currently {@link GralMng#pos()}.
   * @param widg
   * @param toResize
   */
  void addWidget(GralWidgetBase widg, boolean toResize){
    String nameWidg = widg.name;
    if(widg instanceof GralWindow)
      Debugutil.stop();
    if(nameWidg !=null) {
      String nameGlobal;
      if(nameWidg.startsWith("@")) {
        nameWidg = nameWidg.substring(1);  //without @
        nameGlobal = widg.name + "." + nameWidg;  //panel.widget
      } else {
        nameGlobal = nameWidg;
      }
      widg.gralMng.registerWidget(nameGlobal, widg);
      this.idxWidgets.put(nameWidg, widg);
    }
    if(this.widgetList.remove(widg)){
      System.err.println("Widget added twice; " + nameWidg);
    }
    this.widgetList.add(widg);
    if(toResize) {
      if(widg instanceof GralWindow) {
        System.err.println("GralPanelContent.addWidget - A window itself should not be added to widgetsToResize, " + widg.name);
      } else {
        this.widgetsToResize.add(widg);
      }
    }
    if(this.primaryWidget ==null && !(widg instanceof GralPanelContent)) {  //register only a non-panel widget as primary - for the panel or window.
      this.primaryWidget = widg;
    }
  }


  /**Removes this widget from the lists in this panel. This method is not intent to invoke
   * by an application. It is only used in {@link GralWidgetBase#remove()}. Use the last one method
   * to remove a widget includint is disposition and remove from the panel.
   * @param widg The widget.
   */
  public void removeWidget(GralWidgetBase widg)
  { String nameWidg = widg.name;
    if(nameWidg !=null) {
      String nameGlobal;
      if(nameWidg.startsWith("@")) {
        nameWidg = nameWidg.substring(1);  //without @
        nameGlobal = widg.name + "." + nameWidg;  //panel.widget
      } else {
        nameGlobal = nameWidg;
      }
      widg.gralMng.removeWidget(nameGlobal);
      this.idxWidgets.remove(nameWidg);
    }

    this.widgetList.remove(widg);
    this.widgetsToResize.remove(widg);

  }


  public void resizeWidgets ( GralRectangle parentPix) {
    for(GralWidgetBase widgd: this.widgetsToResize) {
      if(widgd instanceof GralWindow) {
        System.err.println("GralWindow.ActionResizeOnePanel - A window itself should not be added to widgetsToResize");
      } else {
        GralRectangle pix = widgd.pos().calcWidgetPosAndSize(this.widg.gralMng.gralProps, parentPix,800,600);
        if(widgd instanceof GralWidget && ((GralWidget)widgd)._wdgImpl !=null) {
          ((GralWidget)widgd)._wdgImpl.setBoundsPixel(pix.x, pix.y, pix.dx, pix.dy);
        }
        if(widgd._compt !=null) {
          widgd._compt.resizeWidgets(pix);
        }
      }
    }
  }

}

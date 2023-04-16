package org.vishia.gral.base;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralWidgetBase_ifc;
import org.vishia.util.Debugutil;

/**This is the base class for a composite widget - consisting of some GralWidgets,
 * but not in any case a panel.
 * 
 * @author Hartmut Schorrig.
 *
 */
public abstract class GralWidgetComposite extends GralWidget {

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

  
  public static class _Composite {
    /**List of all widgets which are contained in this panel.
     * This list is used in the communication thread to update the content of all widgets in the panel.
     */
    protected List<GralWidget> widgetList = new LinkedList<GralWidget>();

    public List<GralWidget> widgetsToResize = new LinkedList<GralWidget>();

    /**The widget which should be focused if the panel is focused.
     * It is possible to set any actual widget to store the focus situation,
     * It is possible too to have only one widget to focus. if the panel gets the focus. */
    protected GralWidgetBase_ifc primaryWidget;

    protected final Map<String, GralWidget> idxWidgets = new TreeMap<String, GralWidget>();


  
  
  }
  
  public final _Composite _compt = new _Composite();
  
  
  public GralWidgetComposite(GralPos refPos, String sPosName, char whatIsIt) {
    super(refPos, sPosName, whatIsIt, null);
    assert(refPos !=null && refPos.parent !=null);
  }

  /*package private*/
  /**Adds a widget to its panel. This method will be called in {@link GralWidget#initPosAndRegisterWidget(GralPos)}
   * either on creation the GralWidget with a given position String or on {@link GralWidget#setToPanel(GralMngBuild_ifc)}
   * with the given currently {@link GralMng#pos()}.
   * @param widg
   * @param toResize
   */
  void addWidget(GralWidget widg, boolean toResize){
    String nameWidg = widg.name;
    if(widg instanceof GralWindow)
      Debugutil.stop();
    if(nameWidg !=null) {
      String nameGlobal;
      if(nameWidg.startsWith("@")) {
        nameWidg = nameWidg.substring(1);  //without @
        nameGlobal = super.name + "." + nameWidg;  //panel.widget
      } else {
        nameGlobal = nameWidg;
      }
      this.gralMng.registerWidget(nameGlobal, widg);
      this._compt.idxWidgets.put(nameWidg, widg);
    }
    if(this._compt.widgetList.remove(widg)){
      System.err.println("Widget added twice; " + nameWidg);
    }
    this._compt.widgetList.add(widg);
    if(toResize) {
      if(widg instanceof GralWindow) {
        System.err.println("GralPanelContent.addWidget - A window itself should not be added to widgetsToResize, " + widg.name);
      } else {
        this._compt.widgetsToResize.add(widg);
      }
    }
    if(this._compt.primaryWidget ==null && !(widg instanceof GralPanelContent)) {  //register only a non-panel widget as primary - for the panel or window.
      this._compt.primaryWidget = widg;
    }
  }


  /**Overridden form of {@link GralWidget#createImplWidget_Gthread()} to create also content of the panel.
   * First the Panel will be created if necessary.
   * Then all widgets in the panel will be created.
   * This operation does also regard new Gral Widgets without implementation, on dynamically creation on runtime.
   * On a tabed panel the widgets are the tabs of the panel.
   * If widgets are found, which are already created, nothing is done with it. All ok.
   *
   */
  @Override public boolean createImplWidget_Gthread() throws IllegalStateException {
    super.createImplWidget_Gthread();
    if(super._wdgImpl !=null) {
      for(GralWidget widg: this._compt.widgetList) {
        widg.createImplWidget_Gthread();                     // recursively call of same
      }
      return true;
    } else return false;
  }


  /**Removes the implementation widget, maybe to re-create with changed properties
   * or also if the GralWidget itself should be removed.
   * This is a internal operation not intent to use by an application.
   * It is called from the {@link GralMng#runGraphicThread()} and hence package private.
   */
  @Override public void removeImplWidget_Gthread() {
    for(GralWidget widg: this._compt.widgetList) {
      widg.removeImplWidget_Gthread();                     // recursively call of same
    }
    super.removeImplWidget_Gthread();
  }


  /**Removes this widget from the lists in this panel. This method is not intent to invoke
   * by an application. It is only used in {@link GralWidget#remove()}. Use the last one method
   * to remove a widget includint is disposition and remove from the panel.
   * @param widg The widget.
   */
  public void removeWidget(GralWidget widg)
  { String nameWidg = widg.name;
    if(nameWidg !=null) {
      String nameGlobal;
      if(nameWidg.startsWith("@")) {
        nameWidg = nameWidg.substring(1);  //without @
        nameGlobal = super.name + "." + nameWidg;  //panel.widget
      } else {
        nameGlobal = nameWidg;
      }
      this.gralMng.removeWidget(nameGlobal);
      this._compt.idxWidgets.remove(nameWidg);
    }

    this._compt.widgetList.remove(widg);
    this._compt.widgetsToResize.remove(widg);

  }


  public void resizeWidgets ( GralRectangle parentPix) {
    for(GralWidget widgd: this._compt.widgetsToResize) {
      if(widgd instanceof GralWidgetComposite) {
        Debugutil.stop();
      }
      if(widgd instanceof GralWindow) {
        System.err.println("GralWindow.ActionResizeOnePanel - A window itself should not be added to widgetsToResize");
      } else {
        GralRectangle pix = widgd.pos().calcWidgetPosAndSize(this.gralMng.gralProps, parentPix,800,600);
        if(widgd.whatIs != '&') {
          widgd._wdgImpl.setBoundsPixel(pix.x, pix.y, pix.dx, pix.dy);
        }
//        GralRectangle framePix = widgd.gralMng()._mngImpl.resizeWidget(widgd, parentPix);
        if(widgd instanceof GralWidgetComposite) {
          ((GralWidgetComposite)widgd).resizeWidgets(pix);
        }
      }
    }
  }

  
}

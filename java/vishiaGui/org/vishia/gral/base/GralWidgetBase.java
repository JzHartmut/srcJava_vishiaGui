package org.vishia.gral.base;

import java.io.IOException;
import java.text.ParseException;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.event.EventConsumer;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralWidgetBase_ifc;
import org.vishia.util.Debugutil;
import org.vishia.util.ObjectVishia;

/**It is the base calss for comprehensive widgets,
 * contains only the basically data for GralPos os the whole widget,
 * the GralMng and the  
 *
 */
public abstract class GralWidgetBase  extends ObjectVishia implements GralWidgetBase_ifc {



  /**Version, history and license.
   * <ul>
   * <li>2023-04-17 Hartmut new
   *   <ul><li> {@link GralWidgComposite} as inner class for composite widgets, also for {@link GralPanelContent} 
   *     as also for comprehensive widgets as container for all Gral sub widgets. 
   *   <li>Resizing is done for all children widgets in {@link GralWidgComposite#resizeWidgets(GralRectangle, int)}.
   *   <li>{@link #pixSize} is set while resizing
   *   <li>properties {@link #hasFocus()} ( {@link #bHasFocus} and {@link #bVisibleState} is stored and handled here.  
   *   </ul> 
   * <li>2022-12-17 Hartmut new {@link #createImplWidget()} able to call in any thread,
   *   to create the implementation widget afterwards. 
   * <li>2022-06-00 Hartmut created, It was now obviously, that comprehensive widgets are necessary,
   *   they need the 
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


  /**The widget manager from where the widget is organized. Most of methods need the information
   * stored in the panel manager. This reference is used to set values to other widgets. */
  public final GralMng gralMng;


  /**The position of the widget. 
   * The GralPos contains also the reference to the parent composite panel {@link GralPanel_ifc} which contains the widget. */
  protected final GralPos _wdgPos;



  /**Name of the widget in the panel. */
  public final String name;
  
  
  
  public final class GralWidgComposite {
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


    
    protected GralWidgComposite (  ) {}

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


    /**Resizes the current widget and all child widgets with the known {@link GralWidgetBase#_wdgPos} and the parent bounds.
     * This operation is called recursively for all children widgets which have composite children ( {@link GralWidgetBase#_compt} is given ).
     * The implementing graphic should only call this operation in the resize listener of the window. 
     * All others is done recursively. 
     * A resize listener of panels (swt.widget.Composite) is not necessary, because the widget structure is completely contained
     * in this class itself. Specific operations to determine the bounds of widgets are not necessary.
     * <br>
     * But for any widget the operation {@link GralWidgetBase#resizePostPreparation()} is called. This operation is empty per default
     * but can be overridden, for example to detect number of shown lines in the GralTable.
     * <br>
     * The user's application should never call this operation. It is only a Gral system operation to resize.
     * Thats why it is protected. 
     * 
     * @param parentPixelRectangle parent bounds, either the client area of a window by the first call
     *   or the bounds of the parent, which are set before, calculated from the size of the parent.
     * @param recursion as usual on recursively call, prevents too much erroneous recursions, 
     *   to prevent a stack overflow on faulty situations.    
     *   The limit is 50, it means 50 nested widgets, enough at all.
     */
    protected void resizeWidgets ( GralRectangle parentPixelRectangle, int recursion) {
      for(GralWidgetBase widgd: this.widgetsToResize) {
        if(widgd instanceof GralWindow) {
          System.err.println("GralWindow.ActionResizeOnePanel - A window itself should not be added to widgetsToResize");
        } else {
          GralRectangle pix = widgd.pos().calcWidgetPosAndSize(GralWidgetBase.this.gralMng.gralProps, parentPixelRectangle, 800, 600);
          widgd.pixSize.set(pix);
          if(widgd instanceof GralWidget && ((GralWidget)widgd)._wdgImpl !=null) {
            GralWidget widg = (GralWidget)widgd;
            widg._wdgImpl.setBoundsPixel(pix.x, pix.y, pix.dx, pix.dy);
          }
          widgd.resizePostPreparation();                // some additional things, _wdgImpl should have the porper size already.
          //
          //------------------------------- recursive for child widgets
          if(widgd._compt !=null && recursion < 50) {
            widgd._compt.resizeWidgets(pix, recursion+1); //recursively call of same
          }
        }
      }
    }

    
    
    void reportAllContent(Appendable out, int level) throws IOException {
      if(level < 20) {
        final String nl = "\n| | | | | | |                               ";
        if(level >0) {
          out.append(nl.substring(0, 2*level-1));
        }
        out.append(GralWidgetBase.this.isVisible() ? GralWidgetBase.this.hasFocus()? '*' : '+' : ':');
        out.append("-Panel: ").append(GralWidgetBase.this.name);
        if(GralWidgetBase.this instanceof GralPanelContent && ((GralPanelContent)GralWidgetBase.this)._panel.labelTab !=null) {
          out.append('(').append(((GralPanelContent)GralWidgetBase.this)._panel.labelTab).append(')');
        }
        out.append(" @").append(GralWidgetBase.this._wdgPos.toString());
        for(GralWidgetBase widg: this.widgetList) {
          if(widg._compt !=null) { // instanceof GralPanelContent) {
            widg._compt.reportAllContent(out, level+1);
          } else {                                   // simple widget without sub widgets
            out.append(nl.substring(0,2*level+1)).append(GralWidgetBase.this.isVisible() ? GralWidgetBase.this.hasFocus()? "*-" : "+-" : ":-");
            widg.toString(out);
          }
        }
      } else {
        out.append("\n .... more");
      }
    }

  }

  
  public final GralWidgComposite _compt;

  /**Set on focus gained, false on focus lost. */
  private boolean bHasFocus;
  
  public final GralRectangle pixSize = new GralRectangle(0,0,0,0);
  
  /**Set true if its shell, tab card etc is be activated. Set false if it is deactivated.
   * It is an estimation whether this widget is be shown yet. 
   */
  protected boolean bVisibleState = true;
  
  public GralWidgetBase(GralPos refPos, String sPosName, GralMng gralMng) {
    this(refPos, sPosName, gralMng, false);
  }

  
  
  public GralWidgetBase(GralPos refPos, String sPosName, boolean isComposite) {
    this(refPos, sPosName, null, isComposite);
  }
  

  /**Either the GralMng is given, or pos should be given, then the GralMng is gotten from {@link GralPos#parent}.
   * If both are null, the {@link GralMng#get()} is used as fallback for compatibility.
   * @param refPos
   * @param sPosName syntax "[@[@<?preventRefPos>] <posString> [= <$?name>] | <$?name>]. 
   * @param gralMng
   */
  public GralWidgetBase(GralPos refPos, String sPosName, GralMng gralMng, boolean isComposite) {
    if(gralMng !=null) {
      this.gralMng = gralMng;
    } else if(refPos !=null && refPos.parent !=null) {
      this.gralMng = refPos.parent.gralMng();
    } else {
      this.gralMng = gralMng; //deprecated approach with singleton.
    }
    this._compt = isComposite ? new GralWidgComposite() : null;
    final GralPos currPos1;
    final boolean bFramePos;
    if(sPosName !=null && sPosName.startsWith("@")) {
      int posName= sPosName.indexOf('=');
      if(posName >=0) {
        this.name = sPosName.substring(posName+1).trim();
      } else {
        this.name = null;
        posName = sPosName.length();
      }
      String posString1 = sPosName.substring(0, posName).trim();
      bFramePos = refPos.x.dirNext == 'f';       // it is a framePos
        currPos1 = bFramePos? refPos.clone()     // don't change the given refPos, instead clone it before new position calculation
                 : refPos !=null ? refPos        // or change the refPos with the position calculation.
                 : this.gralMng.pos().pos;           
      try{
        currPos1.calcNextPos(posString1);  // Note: setNextPos() returns a cloned position.
        //          }
      } catch(ParseException exc) {
        throw new IllegalArgumentException("GralWidget - position is syntactical faulty; " + posString1);
      }
    } else {
      this.name = sPosName;            // name maybe null
      currPos1 = refPos == null ? this.gralMng.pos().pos : refPos;
      bFramePos = false;
      currPos1.checkSetNext();                             // If the position was used and not set newly, calc setNext()
    }
    currPos1.assertCorrectness();
    currPos1.setUsed();           //mark "used" for the referred GralPos
    this._wdgPos = bFramePos ? currPos1 : currPos1.clone();   //use always a clone for the widget.
    //
    //============================================= Register the widget
    if(this._wdgPos.parent == this) {
      //don't register the panel itself!
    } else if(this._wdgPos.parent !=null && this._wdgPos.parent instanceof GralWidgetBase && ((GralWidgetBase)this._wdgPos.parent)._compt !=null){
      ((GralWidgetBase)this._wdgPos.parent)._compt.addWidget(this, this._wdgPos.toResize());
    } else if(this._wdgPos ==null) {
      this._wdgPos.parent = this.gralMng.getCurrentPanel();
      System.out.println("GralWidget.GralWidget - pos without panel");
    }
    //already done: this.gralMng.registerWidget(this);
  }

  
  
  
  
  /**Access to the name of the widget. But you can also access the final public {@link org.vishia.gral.base.GralWidgetBase#name} immediately.
   * Defined in {@link GralWidgetBase_ifc#getName()}
   * @return the name
   */
  @Override public String getName ( ) { return this.name; }

  /**Position of the widget, which refers also the {@link GralPos#parent}. 
   * The parent in a GralPos has usual the reference to the {@link org.vishia.gral.base.GralMng}.
   * 
   * @return the position, always available, never null.
   */
  @Override public final GralPos pos ( ) { return this._wdgPos; } 

  /**Returns the associated GralMng. The GralMng is associated by construction,
   */
  @Override public final GralMng gralMng() { return this.gralMng; }

  

  public abstract boolean setVisible(boolean visible);
  
  /**This operation should be called from the implementation graphic 
   * if the focus was changed for an implementation widget by user handling.
   * It does not change the focus state in the implementation widget,
   * it denotes only that the GralWidget has the focus or not.
   * <br>
   * It is also called for all parents of a widget if a child gets the focus.
   * The specific widget implementation can override this operation to do specifics.
   * It should call super.focused(bGained) to forward the focus to parents.
   * @param bGained
   */
  @Override public void setFocused(boolean bGained) {
    if (this instanceof GralTextBox)
      Debugutil.stop();
    this.bHasFocus = bGained;
    if(bGained == false && this instanceof GralWidget) { ((GralWidget)this).dyda.bTouchedField = false; }
    this._wdgPos.parent.setFocused(bGained);
  }

  public boolean hasFocus () { return this.bHasFocus; }
  
  public boolean isVisible () { return this.bVisibleState; }
  
  // can be specific implemented
  /**This operation is called after the widget itself was resized but not just redrawn.
   * It is called from {@link GralWidgComposite#resizeWidgets(GralRectangle, int)}
   * If some specific resize operations should be done for redraw (for example calculate which implementation widgets are visible)
   * Then this operation should be overridden.
   * It is done for example in {@link GralTable#resizePostPreparation()}.
   */
  protected void resizePostPreparation () {}
  

  
//  /**Standard implementation. Override only if necessary for sepcial handling.
//   * @see org.vishia.gral.ifc.GralWidget_ifc#setFocus()
//   */
//  public void setFocus(){ setFocus(0,0); }
//
//  
//  
//  /**Sets the focus to this widget. This method is possible to call in any thread.
//   * If it is called in the graphic thread and the delay = 0, then it is executed immediately.
//   * Elsewhere the request is stored in the graphic thread execution queue and invoked later.
//   * If the widget is inside a tab of a tabbed panel, the tab is designated as currently therewith.
//   * That is done in the calling thread because it is a thread safe operation.
//   * 
//   * @param delay Delay in ms for invoking the focus request 
//   * @param latest 
//   */
//  public void setFocus(int delay, int latest){
//    if(delay >0 || !gralMng.currThreadIsGraphic() || _wdgImpl == null) {
//      dyda.setChanged(ImplAccess.chgFocus | ImplAccess.chgVisible);
//      repaint(delay, latest);
//    } else {
//      //action in the graphic thread.
//      setFocusGthread();
//    }
//  }
//
//  
//  
//  /**This operation should be overridden by comprehensive widgets and GralPanelContent
//   * because the focus should be set to one of the specific widget in that containers.
//   * This non overridden operation does nothing then. 
//   */
//  protected void setFocusGthread ( ) {
//    GralWidget.ImplAccess wdgi = getImplAccess();
//    if(wdgi == null) return;
//    else {
//      if(!this.bHasFocus) {
//        GralWidgetBase_ifc child = this;
//        if(! (child instanceof GralWindow)) {
//          GralWidgetBase_ifc parent = _wdgPos.parent;
//          int catastrophicalCount = 100;
//          //set the visible state and the focus of the parents.
//          while(parent !=null && parent.pos() !=null  //a panel is knwon, it has a parent inside its pos() 
//              && !parent.isInFocus()
//              && parent.getImplAccess() !=null
//              && --catastrophicalCount >=0){
//            parent.getImplAccess().setFocusGThread();
//            if(parent instanceof GralPanelContent) {
//              ((GralPanelContent)parent).setVisibleStateWidget(true);   // for example a tab panel activate it. 
//            }
//            if((parent instanceof GralPanelContent)){ // && ((GralPanelContent)parent).isTabbed()) {
//              //TabbedPanel: The tab where the widget is member of have to be set as active one.
//              GralPanelContent panel = (GralPanelContent)parent;
//              panel.setPrimaryWidget((GralWidget)this);    // casting possible because it is overridden on comprehensive widgets.
////              if(child instanceof GralPanelContent) {
////                panelTabbed.selectTab(child.getName());
////              }
//              //String name = panel1.getName();
//              //panelTabbed.selectTab(name);  //why with name, use GralPanel inside GralTabbedPanel immediately!
//            }
//            if(parent instanceof GralWindow) {       //This is not the window itself
//              parent = null;
//            } else {
//              child = parent;
//              parent = parent.pos().parent; //
//            }
//          }
//        }
//        wdgi.setFocusGThread();  //sets the focus to the
//        bVisibleState = true;
//      } 
//      GralWidgetBase_ifc parent = this;
//      GralWidget child;
//      GralPanelContent panel;
//      while(parent instanceof GralPanelContent
//        && (child = (panel = (GralPanelContent)parent)._panel.primaryWidget) !=null
//        && child._wdgImpl !=null
//        && !child.bHasFocus
//        ) {
//        child.setFocus();
//        child._wdgImpl.setFocusGThread();
//        child.bVisibleState = true;
//        child._wdgImpl.repaintGthread();
//        List<GralWidget> listWidg = panel.getWidgetList();
//        for(GralWidget widgChild : listWidg) {
//          widgChild._wdgImpl.setVisibleGThread(true);
//          widgChild.bVisibleState = true;
//        }
//        parent = child;  //loop if more as one GralPanelContent
//      }
//      
//    }
//  }
  


  /**This operation is implemented in the {@link GralWidget} by the default behavior.
   * Or it should be overridden in instances which are not derived from GralWidget itself,
   * on instances of comprehensive widgets.
   * This implementation is only called in {@link GralWidget#createImplWidget_Gthread()} for composite widgets.
   * @return true if created.
   * @throws IllegalStateException if called though the GralMng is not implemented. 
   */
  public boolean createImplWidget_Gthread() throws IllegalStateException {
    if(this._compt !=null) {                           // is it a composite widget? a Panel or comprehensive widget?
      for(GralWidgetBase widg: this._compt.widgetList) {
        if(widg instanceof org.vishia.gral.widget.GralFileSelector)
          Debugutil.stop();
        widg.createImplWidget_Gthread();               // recursively call of same for all members.
      }
      return true;
    }
    else return false;
  }

  /**Checks whether the creation of the implementation widget is admissible
   * @param implAccess use this._widgImpl for a normal widget, or use one of the (the primary) widgets
   *   of a comprehensive widget to check whether the implementation is also done. 
   * @return true if ok, false if widget exists already
   * @throws IllegalStateException if this function is called and the implementation manager is not given.
   *   It is before call {@link GralMng#createGraphic(String, char, org.vishia.msgDispatch.LogMessage)}.
   */
  public final boolean checkImplWidgetCreation ( GralWidget.ImplAccess wdgImpl ) {
    if(wdgImpl ==null){ // throw new IllegalStateException("setToPanel faulty call - GralTable;");
      if(this.gralMng._mngImpl ==null) {
        throw new IllegalStateException("must not call createImplWidget_Gthread if the graphic is not initialized. itsMng._mngImpl ==null");
      }
      return true;
    } else {
      return false;                                        // implementation exists.
    }
  }

  
  /**Should create the implementation widget. 
   * This operation should be called on new created Gral Widgets if it is able to suppose
   * that the parent is created already. This is definitely on new widgets in runtime.
   * If the conditions to create are given, either immediately the {@link #createImplWidget_Gthread()} is called
   * if it is in the graphic thread, or a GralGraphicTimeOrder is created to do so in the graphic thread.
   * It means this operation can be called in any thread.
   * Calls the overridden {@link GralWidget#createImplWidget_Gthread()}
   * 
   * @return true if created or the time order for creation is started.
   *   false if creation is not possible yet.
   */
  public final boolean createImplWidget ( ) {
    if(this.gralMng._mngImpl !=null && this._wdgPos.parent.getImplAccess() !=null ) {
      //assume the widget is not implemented, elsewhere this operation may not be called,
      //but the conditions for implementation are given: parent is implemented.
      if(this.gralMng.currThreadIsGraphic()){
        return createImplWidget_Gthread();      // call immediately the graphic thread operation, overridden in GralWidget.
      } else {
        GralGraphicOrder order = new TimeOrderCreateImplWidget(this);
        this.gralMng.addDispatchOrder(order);
        return true;
      }
    }
    else return false; // no gralMng implementation.
  }


  /**Removes the implementation widget, maybe to re-create with changed properties
   * or also if the GralWidget itself should be removed.
   * This is a internal operation not intent to use by an application. 
   * It is called from the {@link GralMng#runGraphicThread()} and hence package private.
   */
  public void removeImplWidget_Gthread () {
    if(this._compt !=null) {
      for(GralWidgetBase widg: this._compt.widgetList) {
        widg.removeImplWidget_Gthread();                     // recursively call of same
      }
    }
  }

  /**Removes the widget from the lists in its panel and from the graphical representation.
   * It calls the protected {@link #removeWidgetImplementation()} which is implemented in the adaption.
   */
  @Override public boolean remove()
  {
    if(this instanceof GralWidget && ((GralWidget)this)._wdgImpl !=null) {
      ((GralWidget)this)._wdgImpl.removeWidgetImplementation();
      ((GralWidget)this)._wdgImpl = null;
    }
    if(this._wdgPos.parent instanceof GralWidgetBase && ((GralWidgetBase)this._wdgPos.parent)._compt !=null) {
      ((GralWidgetBase)this._wdgPos.parent)._compt.removeWidget(this);
    }
    this.gralMng.deregisterWidgetName(this);
    return true;
  }


  
  
  @Override public GralWidget.ImplAccess getImplAccess() {
    return null;  //default, it is overridden if it is a Widget.
  }
  
  

  
  /**Especially for test and debug, short info about widget.
   * @see java.lang.Object#toString()
   */
  @Override public String toString ( ) { 
    StringBuilder u = new StringBuilder(240);
    toString(u);
    return u.toString();
  }

  
  
  @Override public Appendable toString(Appendable u, String ... cond) {
    try {
      if(this.name !=null) { u.append(":").append(this.name);}
      if(this._wdgPos !=null){
        this._wdgPos.toString(u, "p");
      } else {
        u.append("@?");
      }
    } catch(IOException exc) {
      throw new RuntimeException("unexpected", exc);
    }
    return u;
  }

  @SuppressWarnings("serial") 
  public static class TimeOrderCreateImplWidget extends GralGraphicOrder implements EventConsumer {

    final GralWidgetBase fromWdg;
    
    protected TimeOrderCreateImplWidget(GralWidgetBase fromWdg) {
      super("createImpl-" + fromWdg.name, fromWdg.gralMng);
      this.fromWdg = fromWdg;
    }

    @Override public int processEvent ( EventObject ev ) {
      this.fromWdg.createImplWidget_Gthread();
      return 0;
    }
  }

  
}

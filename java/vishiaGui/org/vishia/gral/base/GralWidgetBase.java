package org.vishia.gral.base;

import java.io.IOException;
import java.text.ParseException;
import java.util.EventObject;
import java.util.List;

import org.vishia.event.EventConsumer;
import org.vishia.gral.ifc.GralWidgetBase_ifc;
import org.vishia.util.ObjectVishia;

/**It is the base calss for comprehensive widgets,
 * contains only the basically data for GralPos os the whole widget,
 * the GralMng and the  
 *
 */
public abstract class GralWidgetBase  extends ObjectVishia implements GralWidgetBase_ifc {



  /**Version, history and license.
   * <ul>
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

//  /**Set on focus gained, false on focus lost. */
//  protected boolean bHasFocus;
//  
//  
//  /**Set true if its shell, tab card etc is be activated. Set false if it is deactivated.
//   * It is an estimation whether this widget is be shown yet. 
//   */
//  protected boolean bVisibleState = true;
//  
//
  

  /**Either the GralMng is given, or pos should be given, then the GralMng is gotten from {@link GralPos#parent}.
   * If both are null, the {@link GralMng#get()} is used as fallback for compatibility.
   * @param refPos
   * @param sPosName syntax "[@[@<?preventRefPos>] <posString> [= <$?name>] | <$?name>]. 
   * @param gralMng
   */
  public GralWidgetBase(GralPos refPos, String sPosName, GralMng gralMng) {
    if(gralMng !=null) {
      this.gralMng = gralMng;
    } else if(refPos !=null && refPos.parent !=null) {
      this.gralMng = refPos.parent.gralMng();
    } else {
      this.gralMng = gralMng; //deprecated approach with singleton.
    }
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
   * @return true if created.
   * @throws IllegalStateException if called though the GralMng is not implemented. 
   */
  public abstract boolean createImplWidget_Gthread() throws IllegalStateException;

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
   * 
   * @return true if created or the time order for creation is started.
   *   false if creation is not possible yet.
   */
  public final boolean createImplWidget ( ) {
    if(this.gralMng._mngImpl !=null && this._wdgPos.parent.getImplAccess() !=null ) {
      //assume the widget is not implemented, elsewhere this operation may not be called,
      //but the conditions for implementation are given: parent is implemented.
      if(this.gralMng.currThreadIsGraphic()){
        return createImplWidget_Gthread();      // call immediately the graphic thread operation.
      } else {
        GralGraphicEventTimeOrder order = new TimeOrderCreateImplWidget(this);
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
  abstract public void removeImplWidget_Gthread();

  
  
  
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
  public static class TimeOrderCreateImplWidget extends GralGraphicEventTimeOrder implements EventConsumer {

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

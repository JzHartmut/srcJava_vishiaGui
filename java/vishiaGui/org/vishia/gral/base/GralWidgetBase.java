package org.vishia.gral.base;

import java.io.IOException;
import java.text.ParseException;

import org.vishia.util.ObjectVishia;

/**It is the base calss for comprehensive widgets,
 * contains only the basically data for GralPos os the whole widget,
 * the GralMng and the  
 *
 */
public abstract class GralWidgetBase  extends ObjectVishia {



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
  protected final GralMng gralMng;


  /**The position of the widget. 
   * The GralPos contains also the reference to the parent composite panel {@link GralPanel_ifc} which contains the widget. */
  protected GralPos _wdgPos;



  /**Name of the widget in the panel. */
  public final String name;


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
    }
    currPos1.assertCorrectness();
    currPos1.checkSetNext();           //mark "used" for the referred GralPos
    this._wdgPos = bFramePos ? currPos1 : currPos1.clone();   //use always a clone for the widget.
  }


  public final GralPos pos(){ return _wdgPos; } 



  public abstract boolean setVisible(boolean visible);


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
      System.err.println("\nERROR: widget implementation already given. ");
      return false;                                        // implementation exists.
    }
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
        GralGraphicTimeOrder order = new TimeOrderCreateImplWidget(this) {
          @Override protected void executeOrder () {
            createImplWidget_Gthread();
          }
        };
        this.gralMng.addDispatchOrder(order);
        return true;
      }
    }
    else return false; // no gralMng implementation.
  }

  
  @SuppressWarnings("serial") 
  public static class TimeOrderCreateImplWidget extends GralGraphicTimeOrder {

    final GralWidgetBase fromWdg;
    
    protected TimeOrderCreateImplWidget(GralWidgetBase fromWdg) {
      super("createImpl-" + fromWdg.name, fromWdg.gralMng);
      this.fromWdg = fromWdg;
    }

    @Override protected void executeOrder () {
      fromWdg.createImplWidget_Gthread();
    }
  }

  
  
}

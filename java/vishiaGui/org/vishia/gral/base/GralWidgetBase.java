package org.vishia.gral.base;

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
   * <li>2011-06-00 Hartmut created, It was now obviously, that comprehensive widgets are necessary,
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
     * @param pos
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
      int posName;
      final GralPos currPos1;
      if(refPos == null) { currPos1 = this.gralMng.pos().pos; }
      else {currPos1 = refPos;}
      if(sPosName !=null && sPosName.startsWith("@") && (posName= sPosName.indexOf('='))>0) {
        String posString1 = sPosName.substring(0, posName).trim();
        this.name = sPosName.substring(posName+1).trim();
        try{
//          if(whatIs == 'w') {
//            //a window: don't change the GralMng.pos, create a new one.
//            currPos1.panel = this.itsMng.getPrimaryWindow();
//            currPos1.calcNextPos(posString1);  // Note: setNextPos() returns a cloned position.
//          } else {
            //a normal widget on a panel
            currPos1.calcNextPos(posString1);  // Note: setNextPos() returns a cloned position.
//          }
        } catch(ParseException exc) {
          throw new IllegalArgumentException("GralWidget - position is syntactical faulty; " + posString1);
        }
      } else {
        this.name = sPosName;
      }
      currPos1.assertCorrectness();
      currPos1.checkSetNext();           //mark "used" for the referred GralPos
      this._wdgPos = currPos1.clone();   //use a clone for the widget.

    }

    
    public final GralPos pos(){ return _wdgPos; } 
    

    
    public abstract boolean setVisible(boolean visible);
    

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
    

}

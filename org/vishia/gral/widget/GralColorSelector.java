//==JZcmd==
//JZcmd Obj a = java org.vishia.gral.widget.GralColorSelector.todo();
//==endJZcmd==

package org.vishia.gral.widget;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.KeyCode;

/**This class creates a window with buttons for selecting colors.
 * The window may be created permanently but hided. On calling {@link #openDialog(String, GralUserAction)}
 * the window is placed in foreground in a concurrently mode. 
 * The user have to implement a instance of {@link GralColorSelector.SetColorIfc}. This interface instance
 * is used as parameter for {@link #openDialog(String, SetColorIfc)} to receive the color. The user should show
 * its graphic with the selected color immediately to see the effect in the users context.
 * @author Hartmut Schorrig
 *
 */
public class GralColorSelector
{

  /**Version, history and license. The version number is a date written as yyyymmdd as decimal number.
   * Changes:
   * <ul>
   * <li>2013-04-15 Hartmut create new
   * </ul>
   * <br><br> 
   * 
   * <b>Copyright/Copyleft</b>:
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL ist not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  //@SuppressWarnings("hiding")
  public final static int version = 20130415;

  
  
  
  String[][] colors = {
      { "pma", "prd", "por", "pye", "pam",  "pgn", "pcy", "pbl", "pmv", "pgr", "lgr"}
    , { "lma", "lrd", "lor", "lye", "lam",  "lgn", "lcy", "lbl", "lmv", "gr", "dgr"}
    , { "Ma1", "Ma2", "Rd1", "Rd2", "Rd3", "Ye1", "Ye2", "Ye3", "Ye4", "Gn1", "Gn2", "Gn3", "Gn4", "Cy1", "Cy2", "Cy3", "Bl1", "Bl2", "Bl3"}     
    , { "sMa1", "sMa2", "sRd1", "sRd2", "sRd3", "sYe1", "sYe2", "sYe3", "sYe4", "sGn1", "sGn2", "sGn3", "sGn4", "sCy1", "sCy2", "sCy3", "sBl1", "sBl2", "sBl3"}     
    , { "ma", "rp", "rd", "or", "am", "ye", "ygn", "lm", "gn", "sgn", "cy", "bl", "vi", "gr", "pbk", "lbk"}
    , { "dma", "pu", "pk", "or", "am", "sye", "fgn", "sg2", "scy", "dbl", "vi", "dgr", "lbk"}
    , { "dma", "pu", "pk", "dye", "wh", "dgn", "dcy", "dbl", "vi", "dgr", "lbk"}
    , { "wh", "drd", "bn", "bk"}
  };
  
  
  private final GralWindow_ifc wind;

  SetColorIfc callback;
  
  /**Creates the dialog window to select colors. This window may be created only one time
   * for some functionality, if the application is attempt to open only one color selection dialog.
   * Use {@link #openDialog(String, SetColorIfc)} to open the window and specify the destination for selecting colors. 
   * @return The created window.
   */
   public GralColorSelector(String name, GralMngBuild_ifc mng){
     mng.selectPanel("primaryWindow");
     mng.setPosition(-24, 0, -67, 0, 1, 'r'); //right bottom, about half less display width and hight.
     wind = mng.createWindow("windSelectColor", "select file", GralWindow_ifc.windConcurrently | GralWindow_ifc.windResizeable );
     for(int irow=0; irow <= 5 /*colors.length*/; ++irow){
       //String[] colorRow = colors[irow];
       mng.setPosition(0.5f + 4*irow, GralPos.size + 2.5f, 0, GralPos.size + 3.5f, 0, 'r', 0.5f);
       for(int ix=0; ix < 19 /*colorRow.length*/; ++ix){
         //String sColor = colorRow[ix];
         GralColor color = GralColor.getStdColor(irow, ix);
         String sColor = color.getColorName();
         GralButton btn = mng.addButton("", actionSelect_, sColor);
         btn.setBackColor(color, 0);
         if(irow >=3){
           btn.setLineColor(GralColor.getColor("wh"), 0);
         }
       }
       mng.setPosition(0.5f + 4*irow + 2.6f, GralPos.size + 1.2f, 0, GralPos.size + 3.5f, 0, 'r', 0.5f);
       for(int ix=0; ix < 19 /*colorRow.length*/; ++ix){
         GralColor color = GralColor.getStdColor(irow, ix);
         int colorValue = color.getColorValue();
         String sColorValue = String.format("%06x", new Integer(colorValue));
         mng.addText(sColorValue);
       }
     }
    // mng.setPosition(0.5f + 3*irow, 3, 0, GralPos.size + 2.5f, 0, 'r', 0.5f);
     
   }
   

   /**Opens the window with the given title. The window is non-exclusive.
   * @param sTitle The title of the window
   * @param actionSelect called method on press any color button.
   */
  public void openDialog(String sTitle, SetColorIfc actionSelect){
     wind.setTitle(sTitle);
     callback = actionSelect;
     wind.setFocus(); //setWindowVisible(true);
  }

   
  /**Action for a color button. It calls {@link #callback}*/
  private final GralUserAction actionSelect_ = new GralUserAction("GralColorSelector-select"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        GralButton widg = (GralButton)widgd;
        if(callback !=null){
          GralColor color = widg.getBackColor(0);
          callback.setColor(color, SetColorFor.line);
        }
        return true;
      } else return false;
    }
  };
  
   
  /**Enumeration to specify what's color is changed.  */
  public enum SetColorFor{
     back, line, text
   }
   
  /**The user can build some instances with this interface.
   * A anonymous inner class is recommended in form:
   * <pre>
  public GralColorSelector.SetColorIfc actionColorSet = new GralColorSelector.SetColorIfc(){
    @Override public void setColor(GralColor color, SetColorFor what) { 
      switch(what){
        case SetColorFor.back: {
          //do something for show the line
        } break;
        case SetColorFor.line: {
          //do something for show the line
        } break;
        case SetColorFor.text: {
          //do something for show the line
        } break;
      }//switch
  } };
   * </pre>
   * In that routine the widget which should be changed may be chosen by the users algorithm maybe depending
   * by selecting any widget or other mode. So the GralColorSelector-window may remain open to change 
   * the colors of some parts one after another in a simple way.
   */
  public interface SetColorIfc{
     void setColor(GralColor color, SetColorFor what);
   }
   
  
}

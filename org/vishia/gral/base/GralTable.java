package org.vishia.gral.base;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralTable_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.util.KeyCode;



/**This interface can be used to work with a whole table.
 * It is an abstraction between SWT and swing table capabilities.
 * <br><br>
 * To work with lines of a table see {@link GralTableLine_ifc}.
 * @author Hartmut Schorrig
 *
 */
public abstract class GralTable extends GralWidget implements GralTable_ifc
{

  /**Version and history
   * <ul>
   * <li>2011-12-30 Hartmut chg {@link #procStandardKeys(int, GralTableLine_ifc, int)} returns true if standard keys are used. 
   * <li>2011-11-27 Hartmut new {@link #setActionOnLineSelected(GralUserAction)}: The user action is called
   *   anytime if a line is selected by user operation. It can be show any associated content anywhere
   *   additionally. It is used for example in "The.file.Commander" to show date, time and maybe content 
   *   while the user selects any files. The graphical implementation should be call {@link #selectLine(GralTableLine_ifc)}
   *   in its Selection listener. 
   * <li>2011-11-20 Hartmut new The capability of selection of lines is moved from the 
   *   {@link org.vishia.gral.widget.SelectList} to this class. It means any table has the capability
   *   of selection of multiple lines. This capability is supported with a extension of the
   *   {@link GralTableLine_ifc} with {@link org.vishia.util.SelectMask_ifc}. The selection of a line
   *   is notificated in the users data which are associated with the table line, not in the graphic
   *   representation of the table. This is differenced to SWT Table implementation. The capability
   *   of selection in an SWT table isn't used. The selection is done by default with shift-key up/down
   *   or with mouse selection ctrl-click, shift-click like usual.
   *   The selection is not missed if the up/down keys are pressed furthermore. More as that,
   *   more as one ranges of lines can be selected. That is a better capability than in SWT.
   * </ul>
   */
  public final static int version = 0x20111121;
  
  private int keyMarkUp = KeyCode.shift + KeyCode.up, keyMarkDn = KeyCode.shift + KeyCode.dn;
  
  
  protected final Map<String, GralTableLine_ifc> idxLine = new TreeMap<String, GralTableLine_ifc>();
  
  GralUserAction actionOnLineSelected;
  
  public GralTable(String name, GralWidgetMng mng)
  {
    super(name, 'L', mng);
  }
  
  
  /**Sets an action which is called any time when another line is selected.
   * @param actionOnLineSelected The action, null to switch off this functionality.
   */
  public void setActionOnLineSelected(GralUserAction actionOnLineSelected){
    this.actionOnLineSelected = actionOnLineSelected;
  }
  
  
  @Override public boolean setCurrentLine(String key){
    GralTableLine_ifc line = idxLine.get(key);
    if(line == null) return false;
    else {
      int nLine = line.getLineNr();
      return setCurrentCell(nLine, -1);
    }
  }
  
  
  protected boolean procStandardKeys(int keyCode, GralTableLine_ifc line, int ixLine){
    boolean bUsed = true;
    if(keyCode == keyMarkDn){
      if((line.getSelection() & 1)!=0){
        //it is selected yet
        line.setForegroundColor(GralColor.getColor("bk"));
        line.setDeselect(1);
      } else {
        line.setForegroundColor(GralColor.getColor("rd"));
        line.setSelect(1);
      }
    } else bUsed = false;
    return bUsed;
  }
  
  
  /**It is called whenever a line is changed. The user can override this method to get infos.
   * @param line
   */
  protected void selectLine(GralTableLine_ifc line){
    if(actionOnLineSelected !=null){
      actionOnLineSelected.userActionGui(KeyCode.tableLineSelect, this, line);
    }
  }
  
  
  
  
}

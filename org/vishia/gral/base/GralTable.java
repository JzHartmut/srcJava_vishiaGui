package org.vishia.gral.base;

import java.util.List;

import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralTable_ifc;
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
  
  public GralTable(String name, GralWidgetMng mng)
  {
    super(name, 'L', mng);
  }
  
  
  
  protected void procStandardKeys(int keyCode, GralTableLine_ifc line, int ixLine){
    if(keyCode == keyMarkDn){
      if((line.getSelection() & 1)!=0){
        //it is selected yet
        line.setForegroundColor(GralColor.getColor("bk"));
        line.setDeselect(1);
      } else {
        line.setForegroundColor(GralColor.getColor("rd"));
        line.setSelect(1);
      }
    }
  }
  
  
}

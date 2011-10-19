package org.vishia.gral.base;

import org.vishia.gral.gridPanel.GralGridMngBase;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralTable_ifc;
import org.vishia.gral.ifc.GralWidget;



/**This interface can be used to work with a whole table.
 * It is an abstraction between SWT and swing table capabilities.
 * <br><br>
 * To work with lines of a table see {@link GralTableLine_ifc}.
 * @author Hartmut Schorrig
 *
 */
public abstract class GralTable extends GralWidget implements GralTable_ifc
{

  public GralTable(String name, GralGridMngBase mng)
  {
    super(name, 'L', mng);
  }
  
  
}

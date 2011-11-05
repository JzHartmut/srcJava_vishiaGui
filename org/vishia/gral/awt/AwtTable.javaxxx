package org.vishia.gral.awt;

import java.awt.List;
import java.awt.Container;

import org.vishia.gral.base.GralTable;

public class AwtTable extends GralTable
{

  
  /**Version and History:
   * <ul>
   * <li>What to do: AWT provides only a simple awt.List with only one column and without header.
   *   Should used swing.JTable ?
   *   Or should build a table from a Canvas in the gral layer itself? 
  * </ul>
   */
  @SuppressWarnings("hiding")
  public static final int version = 0x20111001;


  
  //private JTable table;
  
  private final AwtWidgetMng mng;
  
  private int[] columnWidths;
  
  //private final int selectionColumn;
  //private final CharSequence selectionText;
  
  public AwtTable(AwtWidgetMng mng, String name, Container parent,  int height
      , int[] columnWidths) //, int selectionColumn, CharSequence selectionText)
  { super(name, mng);
    this.mng = mng;
    this.columnWidths = columnWidths;
  }

  
  void resizeTable()
  {
    
  }
  
  
  
  
  public static GralTable addTable(AwtWidgetMng mng, String sName, int height, int[] columnWidths
  //, int selectionColumn, CharSequence selectionText    
  )
  {
    
    boolean TEST = false;
    return null;
  }
  
  
  
  
  /**Inserts a line. it is called from {@link GralPanelMngWorking_ifc#setInfo(GralWidget, int, int, Object, Object)} 
   * and from {@link #insertLine(String, int)}.
   * @param ident
   * @param visibleInfo
   * @param userData
   * @return
   */
  GralTableLine_ifc changeTable(int ident, Object visibleInfo, Object userData)
  {
    return null;
  }
  
  
  
  
  void clearTable(int ident)
  {
  }
  
  
  

  

  
  
  
}

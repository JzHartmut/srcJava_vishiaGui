package org.vishia.gral.ifc;

import java.util.List;


public interface GralTable_ifc extends GralWidget_ifc
{
  /**Version and history:
   * <ul>
   * <ul>2011-11-20 Hartmut new {@link #getSelectedLines()}
   * <li>2011-10-01 Hartmut new: {@link #clearTable()}
   * <li>2011-09-03 Hartmut chg: method {@link #insertLine(String, int)} returns now the instance of {@link GralTableLine_ifc}
   *   The user doesn't create a line instance, an extra factory isn't necessary. The implementing instance
   *   can be determined by the implementer of this interface.
   * <li>2011-05-11 Hartmut new: creation. Tables are a relevant medium to present GUIs. The implementation of tables
   *   in SWT or swing are strong different. It needs a simple interface to work with tables.  
   * </ul>
   */
  public final static int version = 0x20111001;
  
  public abstract GralTableLine_ifc getCurrentLine();
  
  public abstract void setCurrentCell(int line, int column);
  
  public abstract GralTableLine_ifc getLine(int row);
  
  /**Get the line which is designated with the requested key.
   * Background: The lines of a table can be sorted in view. To get a line
   * which is sorted in another row a originally set a key is necessary.
   * The table data model contains a sorted map with keys and the associated lines.
   * The keys can, but may not shown as user visible data. 
   * @param key The key to find out the row.
   * @return null if such line isn't found.
   */
  public abstract GralTableLine_ifc getLine(String key);
  
  /**Inserts a line in the table.
   * @param key The key to get it.
   * @param line The line
   * @param row The row where the line should be inserted before. 0 - insert on top. 
   *        Integer.MAXINT or number greater as number of rows: append on end.
   * @return row where the line is inserted.
   */
  public abstract GralTableLine_ifc insertLine(String key, int row);
  
  
  /**Deletes a line in the table.
   * 
   */
  public abstract void deleteLine(  GralTableLine_ifc line);

  public abstract void clearTable();
  
  
  /**Search where this line is shown.
   * @param key
   * @return -1 if the key isn't found in the table. 0... row where this line is shown in table.
   */
  public abstract int searchLine(String key);
  
  
  
  /**Returns all selected lines.
   */
  List<GralTableLine_ifc> getSelectedLines();
  

  
}

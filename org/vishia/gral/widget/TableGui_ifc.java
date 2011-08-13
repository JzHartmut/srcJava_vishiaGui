package org.vishia.gral.widget;


/**This interface can be used to work with a whole table.
 * It is an abstraction between SWT and swing table capabilities.
 * <br><br>
 * To work with lines of a table see {@link TableLineGui_ifc}.
 * @author Hartmut Schorrig
 *
 */
public interface TableGui_ifc extends WidgetGui_ifc
{

  TableLineGui_ifc getCurrentLine();
  
  void setCurrentCell(int line, int column);
  
  TableLineGui_ifc getLine(int row);
  
  /**Get the line which is designated with the requested key.
   * Background: The lines of a table can be sorted in view. To get a line
   * which is sorted in another row a originally set a key is necessary.
   * The table data model contains a sorted map with keys and the associated lines.
   * The keys can, but may not shown as user visible data. 
   * @param key The key to find out the row.
   * @return null if such line isn't found.
   */
  TableLineGui_ifc getLine(String key);
  
  /**Inserts a line in the table.
   * @param key The key to get it.
   * @param line The line
   * @param row The row where the line should be inserted before. 0 - insert on top. 
   *        Integer.MAXINT or number greater as number of rows: append on end.
   * @return row where the line is inserted.
   */
  int insertLine(String key, TableLineGui_ifc line, int row);
  
  /**Search where this line is shown.
   * @param key
   * @return -1 if the key isn't found in the table. 0... row where this line is shown in table.
   */
  int searchLine(String key);
  
}

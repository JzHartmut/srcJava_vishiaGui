package org.vishia.gral.ifc;

import java.util.List;

import org.vishia.util.TreeNode_ifc;


/**Interface to a table widget.
 * @author hartmut
 *
 * @param <UserData> Type of the data which is associated with a table line.
 */
public interface GralTable_ifc<UserData> extends GralWidget_ifc
{
  /**Version and history:
   * <ul>
   * <li>2013-09-15 Hartmut new: Definition of {@link #setBackColor(GralColor, int)} parallel to the
   *   {@link GralWidget_ifc#getBackColor(int)} with special comments for usage of the int parameter.
   *   See {@link #kAllLines}, {@link #kEmptyArea}. Adequate {@link #getBackColor(int)}. 
   * <li>2013-09-05 Hartmut chg: {@link #getMarkedLines(int)} now has that 1 argument for mark mask.
   * <li>2013-06-11 Hartmut new Now the {@link GralTable}, the {@link GralTable.TableLineData} and this
   *   interface are marked with the generic type UserData.
   * <li>2012-08-22 Hartmut new {@link #setCurrentLine(int)} with int, it isn't new because it was able to set with
   *   {@link #setCurrentCell(int, int)} with -1 as second parameter.
   * <ul>2011-11-20 Hartmut new {@link #getMarkedLines()}
   * <li>2011-10-01 Hartmut new: {@link #clearTable()}
   * <li>2011-09-03 Hartmut chg: method {@link #insertLine(String, int)} returns now the instance of {@link GralTableLine_ifc}
   *   The user doesn't create a line instance, an extra factory isn't necessary. The implementing instance
   *   can be determined by the implementer of this interface.
   * <li>2011-05-11 Hartmut new: creation. Tables are a relevant medium to present GUIs. The implementation of tables
   *   in SWT or swing are strong different. It needs a simple interface to work with tables.  
   * </ul>
  /**Version, history and license.
   * <ul>
   * <li>2011-06-00 Hartmut created
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
  public static final int version = 20130528;

  
  /**Identify number for some methods which have a line number parameter.
   * Execute for all lines and the empty area.
   */
  public static final int kAllLines = -1;
  
  /**Identify number for some methods which have a line number parameter.
   * Execute only for the empty area (not existing lines)
   */
  public static final int kEmptyArea = -2;
  
  /**Sets the background color of the table or one line.
   * @param ix >=0 sets for one line only, 
   *   {@link #kAllLines}: Sets for all lines and the table background, 
   *   {@link #kEmptyArea}: Only for table background. 
   * @see org.vishia.gral.ifc.GralWidget_ifc#setBackColor(org.vishia.gral.ifc.GralColor, int)
   */
  @Override void setBackColor(GralColor color, int ix);
  
  
  @Override GralColor getBackColor(int ix);
  

  /**Returns the currently selected line or null if nothing is selected. */
  public abstract GralTableLine_ifc<UserData> getCurrentLine();
  
  
  /**Sets the line which is the current one. The current row is unchanged.
   * @param key The key of the line, given by {@link #insertLine(String, int, String[], Object)}.
   * @return true if found.
   */
  boolean setCurrentLine(String key);
  
  /**Sets the line which is the current one. The current row is unchanged.
   * @param line from 0 for the first (top) line
   *   A number greater than the number of lines, especially Integer.MAX_VALUE selects the last line.
   *   The value -1 let the current line in the table unchanged.
   * @return true if found.
   */
  //boolean setCurrentLine(int line);
  
  /**Sets the cell which is the current one in the given line.
   * @param line The line which should be selected.
   * @param ixline the index in the visible are of table where the line should be present. 
   *   If the index is negative, the lines from end of visible are are countered.
   *   if the index is outside of the area, it will be set inside.  
   * @param ixcolumn from 0 for the left column, if -1 then let the current row of table unchanged.
   */
  void setCurrentLine(GralTableLine_ifc<UserData> line, int ixline, int ixcolumn);
  //boolean setCurrentCell(int line, int column);
  
  /**Returns the line at row.
   * @param row 0 is the first row. Must not be negative.
   * @return null if the row isn't exists.
   */
  //public abstract GralTableLine_ifc<UserData> getLine(int row);
  
  /**Get the line which is designated with the requested key.
   * Background: The lines of a table can be sorted in view. To get a line
   * which is sorted in another row a originally set a key is necessary.
   * The table data model contains a sorted map with keys and the associated lines.
   * The keys can, but may not shown as user visible data. 
   * @param key The key to find out the row.
   * @return null if such line isn't found.
   */
  public abstract GralTableLine_ifc<UserData> getLine(String key);
  
  /**Inserts a line in the table.
   * @param key The key to get it.
   * @param row The row where the line should be inserted before. 0 - insert on top. 
   *        Integer.MAXINT or number greater as number of rows: append on end.
   * @return instance to add info.
   */
  //public abstract GralTableLine_ifc insertLine(String key, int row);
  
  
  /**Inserts a line in the table with given content.
   * @param key The key to get it.
   * @param row The row where the line should be inserted before. 0 - insert on top. 
   *        negative or Integer.MAXINT or number greater as number of rows: append on end.
   * @param cellTexts texts of the cells. May be null.
   * @param userData data assigned to the line, able to get with {@link GralTableLine_ifc#getUserData()}.
   * @return instance to add info.
   * @see {@link GralTableLine_ifc} to add content.
   */
  public abstract GralTableLine_ifc<UserData> insertLine(String key, int row, String[] cellTexts, UserData userData);
  
  /**Inserts a line in the table with given content.
   * @param key The key to get it.
   * @param row The row where the line should be inserted before. 0 - insert on top. 
   *        negative or Integer.MAXINT or number greater as number of rows: append on end.
   * @param cellTexts texts of the cells. May be null.
   * @param userData data assigned to the line, able to get with {@link GralTableLine_ifc#getUserData()}.
   * @return instance to add info.
   * @see {@link GralTableLine_ifc} to add content.
   */
  public abstract GralTableLine_ifc<UserData> addLine(String key, String[] cellTexts, UserData userData);
  

  
  /**Deletes a line in the table.
   * 
   */
  public abstract void deleteLine(  GralTableLine_ifc<UserData> line);

  
  /**Replaces the key associated to the given line.
   * It replaces the key stored in the line data and additional the map key-line
   * in the table's key-line-index {@link #idxLine}.
   * @param line The line, not a new line, an existing one.
   * @param keyNew The new key for this line.
   */
  //public void replaceLineKey(GralTableLine_ifc<UserData> line, String keyNew);
  
  /**Returns the number of active lines of the table.
   * @return
   */
  int size();
  
  public abstract void clearTable();
  
  
  /**Search where this line is shown.
   * @param key
   * @return -1 if the key isn't found in the table. 0... row where this line is shown in table.
   */
  //public abstract int searchLine(String key);
  
  
  
  /**Returns all marked lines. It returns an empty list if no line is marked. 
   */
  List<GralTableLine_ifc<UserData>> getMarkedLines(int mask);
  

  /**Returns all lines. Note: This returns the original container of lines without backup.
   * It means the list will be changed if any {@link #insertLine(String, int, String[], Object)}
   * or {@link #deleteLine(GralTableLine_ifc)} will be called. Do not call any modification of this list
   * for example {@link java.util.List#add(Object)} or {@link java.util.List#remove(int)}, use the methods
   * of this interface to change the content of the table. Elsewhere the graphical presentation of the data
   * may be corrupted.
   * 
   * See {@link #getListContent()}.
   */
  TreeNode_ifc<?, UserData> getAllLines();
  
  /**Returns all content of lines. This makes a snapshot of the lines in calling time. If the table 
   * will be changed in another thread while this routine runs, a {@link java.util.ConcurrentModificationException}
   * will be thrown. But after them the list is able to use independently. 
   */
  List<UserData> getListContent();
  

  /**Returns the first line from top of the table which is marked or null if no line is marked.   */
  GralTableLine_ifc<UserData> getFirstMarkedLine(int mask);
  

  
  
  
}

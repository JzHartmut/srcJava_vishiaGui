package org.vishia.gral.ifc;

import org.vishia.gral.base.GralTable;
import org.vishia.util.MarkMask_ifc;
import org.vishia.util.SortedTree;
import org.vishia.util.TreeNode_ifc;



public interface GralTableLine_ifc<UserData> extends GralWidget_ifc, MarkMask_ifc
, SortedTree<GralTableLine_ifc<UserData>>
//, TreeNode_ifc<GralTable.TableLineData<UserData>, UserData>
, TreeNode_ifc<GralTable<UserData>.TableLineData, UserData>
{

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
  public static final int version = 20120303;

  /**Returns the text which is assigned to the cell of the table.
   * @param column The column of table.
   * @return If the cell text, an empty text "" if the cell has not any text assigned.
   * @throws IndexOutOfBoundsException on fault column.
   */
  String getCellText(int column);
  
  String[] getCellTexts();
  
  String setCellText(String text, int column);
  
  //void setUserData(UserData data);
  
  UserData getUserData();
  
  int getSelectedColumn();
  
  /**Inserts a line as child of this. This method should be used if no child line exists yet.
   * It adds as last line of children. If child lines are existent, one can use {@link #addPrevLine(String, String[], Object)}
   * or {@link #addNextLine(String, String[], Object)} in respect to a given child line too.
   * @param lineKey The key for the line
   * @param lineTexts maybe null, elsewhere texts for the cells (column texts) of the line.
   * @param userDataP
   * @return the added line.
   */
  GralTableLine_ifc<UserData> addChildLine(String childKey, String[] childTexts, UserData data);

  
  /**Inserts a line behind the current line.
   * @param lineKey The key for the line
   * @param lineTexts maybe null, elsewhere texts for the cells (column texts) of the line.
   * @param userDataP
   * @return the added line.
   */
  GralTableLine_ifc<UserData> addNextLine(String childKey, String[] childTexts, UserData data);

  /**Inserts a line before the current line.
   * @param lineKey The key for the line
   * @param lineTexts maybe null, elsewhere texts for the cells (column texts) of the line.
   * @param userDataP
   * @return the added line.
   */
  GralTableLine_ifc<UserData> addPrevLine(String childKey, String[] childTexts, UserData data);

  
  void deleteLine();
  
  GralTableLine_ifc<UserData> parentNode();  
  
  
  /**Opens or closes showing children of this line. It is for tree view.
   * @param show true then show, false then close this bough.
   * @param bLeftGrandChildrenOpen if show==true, false then close sub children boughs of this, true left opened grand children open.
   *   This is an important behavior of operating with a tree view. Both variants are desirable in different situations. 
   */
  void showChildren(boolean show, boolean bLeftGrandChildrenOpen);
  
}

package org.vishia.curves;

import java.io.File;
import java.io.IOException;

import org.vishia.gral.ifc.GralCurveView_ifc;
import org.vishia.util.Timeshort;

/**This interface can be used for reading curves from files in any format.
 * 
 * @author Hartmut Schorrig
 *
 */
public interface ReadCurves_ifc
{
  /**Version, history and copyright/copyleft.
   * <ul>
   * <li>2012-10-12 created. Firstly used for {@link org.vishia.guiInspc.InspcCurveView} and {@link org.vishia.gral.base.GralCurveView}.
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
   *    You mustn't delete this copyright/copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final String sVersion = "2016-02-21";

  /**Checks whether the file has a proper format. If not returns false without other activity. 
   * If true than reads the file and writes the curve data into the given {@link GralCurveView_ifc} instance. 
   */
  boolean checkFile(File file, GralCurveView_ifc curve);
  
 }

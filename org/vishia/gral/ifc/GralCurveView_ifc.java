package org.vishia.gral.ifc;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.base.GetGralWidget_ifc;

/**This interface describes the capabilities of a curve view as application interface.
 * @author Hartmut Schorrig
 *
 */
public interface GralCurveView_ifc extends GralWidget_ifc, GralSetValue_ifc, GetGralWidget_ifc
{
  
  /**Version, history and license.
   * <ul>
   * <li>2012-04-26 Hartmut new extends GralWidget_ifc etc: A reference to a GralCurveView_ifc is a
   *   reference to a widget too.
   * <li>2012-04-26 Hartmut new capability for curve view: Set active or non active. It is a state
   *   whether values are stored, independent of their visualization.
   * <li>2012-03-25 Hartmut created as interface to {@link org.vishia.gral.base.GralCurveView}.
   *   Approach: The capabilities should be described by an interface. Yet only 1 method.
   * </ul>
   * <br><br>
   * <b>Copyright/Copyleft</b>:
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
   * 
   */
  public static final int version = 20120426;
  
  /**Adds a sampling value set.
   * <br><br> 
   * This method can be called in any thread. It updates only data,
   * a GUI-call isn't done. But the method is not thread-safe. 
   * If more as one threads writes data, an external synchronization should be done
   * which may encapsulate more as only this call.
   * <br><br> 
   * The implementation should force repaint to show the data in the graphic thread.
   * 
   * @param values The values.
   * @param timeshort relative time-stamp as currently wrapping time in milliseconds.
   */
  void setSample(float[] values, int timeshort);

  void activate(boolean activate);
  
  boolean isActiv();
  
}

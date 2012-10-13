package org.vishia.gral.ifc;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.curves.WriteCurve_ifc;
import org.vishia.gral.base.GetGralWidget_ifc;

/**This interface describes the capabilities of a curve view as application interface.
 * @author Hartmut Schorrig
 *
 */
public interface GralCurveView_ifc extends GralWidget_ifc, GralSetValue_ifc, GetGralWidget_ifc
{
  
  /**Version, history and license.
   * <ul>
   * <li>2012-06-08 Hartmut new: {@link #applySettings(String)} and {@link #writeSettings(Appendable)} for saving
   *   and getting the configuration of curve view from a file or another text.
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
  public static final int version = 20120608;
  
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

  
  
  /**Sets a point of absolute time. This routine should be called at least 2 times in an wrapping area of timeshort.
   * If timeshort are milliseconds, this is about 2000000 seconds. It means, it isn't often. You should call this routine 
   * cyclically in an slower cycle. Then the graphic can showt the
   * 
   * @param date The absolute time appropriate to the timeshort value.
   * @param timeshort The wrapping short time at this moment of data.
   * @param millisecPerTimeshort Number of milliseconds per step of timeshort.
   */
  void setTimePoint(long date, int timeshort, float millisecPerTimeshort);
  
  
  
  /**Activates or deactivates. Only if the curve view is activated, it writes in time. If it is not active,
   * than the curve can be watched, zoomed etc. 
   * @param activate true to activate, false to deactivate.
   */
  void activate(boolean activate);
  
  /**Returns true if the curve view is active. See {@link #activate(boolean)}. */
  boolean isActiv();
  
  /**Writes the curve to the given interface, it is an exporter class.
   * @param out
   */
  void writeCurve(WriteCurve_ifc out);
  
  
  /**The ZBNF-syntax of a setting for curve view. */
  static String syntaxSettings = "curveSettings::= { <Track> } \\e."
    + "Track::= track <*:?name> : { datapath = <* ,;?datapath> | color = <* ,;?color> | "
    + "scale = <#f?scale> | offset = <#f?offset> | 0-line-percent = <#?nullLine> "
    + "? , } ; .";

  
  /**Reads the settings from the given String (maybe read from a file)
   * and sets all tracks newly.
   * @param in Input, syntax see {@link #syntaxSettings}
   * @return true on success.
   */
  boolean applySettings(String in);

  /**Writes the settings of the curve view in an output, maybe a file. 
   * The syntax is matching to {@link #applySettings(String)}.
   * 
   * @param out any output stream.
   */
  void writeSettings(Appendable out);

  
}

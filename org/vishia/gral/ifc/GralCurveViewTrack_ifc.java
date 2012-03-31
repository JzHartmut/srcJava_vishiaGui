package org.vishia.gral.ifc;

/**This interface describes a track (one curve) of a curve view.
 * @see org.vishia.gral.widget.GralCurveView, 
 * @see GralCurveView_ifc
 * @author Hartmut Schorrig
 *
 */
public interface GralCurveViewTrack_ifc
{
  /**Version, history and license.
   * <ul>
   * <li>2012-04-01 Hartmut new: {@link #setDataPath(String)}
   * <li>2012-03-17 Hartmut created as interface to {@link org.vishia.gral.base.GralCurveView.Track}.
   *   Approach: Accessibility. The class Track is protected. Its details should only be used
   *   for implementation and inside {@link org.vishia.gral.base.GralCurveView}
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
  public static final int version = 20120325;

  /**Returns the scaling per division. One division is 1/10 of full y presentation, like usual
   *   on oscilloscopes.
   */
  float getScale7div();
  
  /**Returns the value which is shown at the 0-line. */
  float getOffset();
  
  /**Returns the position of the 0-line as value from 0 to 100 for this track. */
  int getLinePercent();

  /**Returns the set color for this line. */
  GralColor getLineColor();
  
  /**Change the scaling of a track.
   * @param trackNr Number of the track in order of creation, 0 ist the first.
   * @param scale7div value per division
   * @param offset value, which is shown at line0
   * @param line0 percent of 0-line in graphic.
   */
  void setTrackScale(float scale7div, float offset, int line0);
  
  /**Sets the color for this track.
   * @param color
   */
  void setLineColor(GralColor color);
  
  void setDataPath(String path);

}

package org.vishia.gral.ifc;

/**Interface for all widgets which can represent a float value maybe showing or regarding a minimal and maximal value.
 * It is applicable commonly.
 * @author Hartmut Schorrig
 *
 */
public interface GralSetValue_ifc
{
  
  
  /**Version and history
   * <ul>
   * <li>2012-04-01 Hartmut new: {@link #setValue(Object[])} as a possibility to set the appearance of a widget
   *   with more as one value, maybe float, integer etc. It is used for {@link org.vishia.gral.base.GralLed}
   *   to determine colors for border and inner.
   * <li>2012-02-22 Hartmut enhanced: now used for lines of a curve view. Now basic interface for GralWidget.
   * <li>2012-01-01 Hartmut Created, firstly only for the value bar.
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

  
  /**Sets a application specific info. 
   * It should help to present user data which are associated to this widget. 
   * This info can be set and changed anytime. */
  public void setContentInfo(Object content);
  
  /**Gets the application specific info. See {@link #setContentInfo(Object)}. */
  public Object getContentInfo();
  
  /**Sets the data path. It is a String in application context.
   * @param sDataPath
   */
  public void setDataPath(String sDataPath);
  
  /**Changes the data path
   * @param sDataPath the new one
   * @return the last one.
   */
  public String getDataPath();
  
  public int getDataIx();

  public void setDataIx(int dataIx);

  


	/**Sets a value to show.
	 * @param value
	 */
	void setValue(float value);
	
  /**Sets some values to show any content. Depending from the type of widget more as one value
   * can be used for several functionality.
   * @param value array of floats
   */
  void setValue(Object[] value);
  
  
	/**Sets the border of the value range for showing. 
	 * If it is a ValueBar, for example, it is the value for 0% and 100%
	 * @param minValue
	 * @param maxValue
	 */
	void setMinMax(float minValue, float maxValue);
}

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
   * <li>2012-02-22 Hartmut Enhanced, now used for lines of a curve view. Now basic interface for GralWidget.
   * <li>2012-01-01 Hartmut Created, firstly only for the value bar.
   * </ul>
   */
  public static final int version = 0x20120222;
  
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
	
	/**Sets the border of the value range for showing. 
	 * If it is a ValueBar, for exmaple, it is the value for 0% and 100%
	 * @param minValue
	 * @param maxValue
	 */
	void setMinMax(float minValue, float maxValue);
}

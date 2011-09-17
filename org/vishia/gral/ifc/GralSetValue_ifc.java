package org.vishia.gral.ifc;

/**Interface for all widgets which can represent a float value maybe showing or regarding a minimal and maximal value.
 * It is applicable commonly.
 * @author Hartmut Schorrig
 *
 */
public interface GralSetValue_ifc
{

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

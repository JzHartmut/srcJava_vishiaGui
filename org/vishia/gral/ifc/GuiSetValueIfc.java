package org.vishia.gral.ifc;

public interface GuiSetValueIfc
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

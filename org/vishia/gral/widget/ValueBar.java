package org.vishia.gral.widget;

import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralSetValue_ifc;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.swt.SwtWidgetMng;

/**This is a common base class for a ValueBar-widget. 
 * It is designed as a container for the widget. The widget itself is derived from the graphical base class,
 * for example from org.eclipse.swt.widgets.Canvas if the SWT-graphic is used,
 * or from java.awt.canvas if Swing is used.
 * @author Hartmut Schorrig
 *
 */
abstract public class ValueBar extends GralWidget implements GralSetValue_ifc
{
	final protected boolean horizontal;
	
	/**The maximal value, number of pixel */
	protected int valueMax;
	
	/**Values of borders */
	protected int[] valPixelBorder;
	
	
	/**The values in Pixel between the colored bar is shown. */
	protected int value1, value2 = 50;
	
	protected int ixColor;
	
	protected float minRange = 0.0F, maxRange = 100.0F;

	protected float[] floatBorder;
	
	protected String[] sColorBorder;
	
	
	
	protected ValueBar(String name, GralWidgetMng mng)
	{ super(name, 'U', mng);
		this.horizontal = false;
	}


	@Override
	public void setMinMax(float minValue, float maxValue)
	{
		minRange = minValue;
	  maxRange = maxValue;	
	}

	/**
	 * @param sParam The first and the last String is the min and max values.
	 *        All odd Strings (0, 2, ...) are values, the following String is the color.
	 */
	public void setBorderAndColors(String[] sParam)
	{
		int zParam = sParam.length;
		//the first and the last value are the border:
		minRange = Float.parseFloat(sParam[0]);
		maxRange = Float.parseFloat(sParam[zParam-1]);
		floatBorder = new float[zParam/2];
		sColorBorder = new String[zParam/2];
		int ixBorder = 0;
		for(int ix = 1; ix < zParam; ix +=2){
	    sColorBorder[ixBorder] = sParam[ix];
	    floatBorder[ixBorder] = Float.parseFloat(sParam[ix+1]);
	    ixBorder +=1;
		}
	}
	
	
	@Override
	public void setValue(float valueP)
	{
		int value1, value2;
		
		value1 = (int)(valueMax * ((0.0F - minRange)/ (maxRange - minRange)));  //the 0-value
		value2 = (int)(valueMax * ((valueP - minRange) / (maxRange - minRange)));
		if(value1 < 0){ value1 = 0;}
		if(value1 > valueMax){ value1 = valueMax; }
		if(value2 < 0){ value2 = 0;}
		if(value2 > valueMax){ value2 = valueMax; }
		if(false && value1 < value2){
			this.value2 = value1;
			this.value1 = value2;
		} else {
			this.value1 = value1;
			this.value2 = value2;
		}
		float border1 = minRange;
		//check in which range the value is assigned, set ixColor 
		if(floatBorder !=null)
		for(ixColor = 0; ixColor < floatBorder.length -1; ++ixColor){
			float border2 = floatBorder[ixColor];
			if(  border1 < border2 && border1 <= valueP && valueP < border2
				|| border1 > border2 && border2 <= valueP && valueP < border1
				)
			break; //found
			border1 = border2;
		}
		this.redraw();
	}
  
  /**This method should implement the redraw-capability. */
  abstract public void redraw();


	public static class ColorValues
	{
		float[] border;
		int[] color;
	}
	

}

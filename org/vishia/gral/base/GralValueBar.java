package org.vishia.gral.base;

import org.vishia.gral.ifc.GralSetValue_ifc;

/**This is a common base class for a ValueBar-widget. 
 * It is designed as a container for the widget. The widget itself is derived from the graphical base class,
 * for example from org.eclipse.swt.widgets.Canvas if the SWT-graphic is used,
 * or from java.awt.canvas if Swing is used.
 * @author Hartmut Schorrig
 *
 */
public class GralValueBar extends GralWidget implements GralSetValue_ifc
{
  
  /**Version, history and license.
   * <ul>
   * <li>2014-02-11 Hartmut new: new Widget concept
   * <li>2010-06-00 Hartmut created
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
  public static final int version = 20140211;

	protected float minRange = 0.0F, maxRange = 100.0F;

	protected float[] fLevels;
	
	protected String[] sColorLevels;
	
	
	
	public GralValueBar(String name)
	{ super(name, 'U', null);
	}


	@Override
	public void setMinMax(float minValue, float maxValue)
	{
		minRange = minValue;
	  maxRange = maxValue;	
	}

	/**Associates values to level and color.
   * <ul>
   * <li>The first and the last String is the min and max values.
   * <li>All odd Strings (0, 2, ...) are values, the following String is the color.
   * <li>For example {"-100","red","0","green","100","orange", "200"}
   * </ul>
   * The example shows a value bar from -100 to 200. A value < 0 is shown red, >100 is shown organge.
   * This form of parameter allows a simple preparation from a simple String,
   * for example <pre>
   * String param = "-100,red,0,green,100,orange,200";
   * String[] sParam = param.tokenize(",");
   * 
	 * @param sParam Even number of Strings.
	 *        
	 */
	public void setBorderAndColors(String[] sParam)
	{
		int zParam = sParam.length;
		//the first and the last value are the border:
		minRange = Float.parseFloat(sParam[0]);
		maxRange = Float.parseFloat(sParam[zParam-1]);
		fLevels = new float[zParam/2];
		sColorLevels = new String[zParam/2];
		int ixBorder = 0;
		for(int ix = 1; ix < zParam; ix +=2){
	    sColorLevels[ixBorder] = sParam[ix];
	    fLevels[ixBorder] = Float.parseFloat(sParam[ix+1]);
	    ixBorder +=1;
		}
		if(wdgImpl !=null){
		  ((GraphicImplAccess)wdgImpl).setBorderAndColorsImpl(sColorLevels);
		}
	}
	
	
	/**Sets the bar and its color. A refresh of graphic is only done if the value change forces 
	 * a change of pixel positions of the value bar. Less changes of value does not force refresh.
	 * @see org.vishia.gral.base.GralWidget#setValue(float)
	 */
	@Override public void setValue(float value)
	{
		if(wdgImpl !=null){
		  if(((GraphicImplAccess)wdgImpl).setValue(value)) {
        this.repaint();
		  }
		}
	}
  

	public static class ColorValues
	{
		float[] border;
		int[] color;
	}
	
	
  public abstract class GraphicImplAccess extends GralWidget.ImplAccess
  implements GralWidgImpl_ifc
  {
    
    
    /**Values of the level borders */
    protected int[] pixLevel;

    public boolean horizontal;

    /**The values in Pixel between the colored bar is shown. */
    protected int pix0line, pixvalue = 50;
    
    protected int ixColor;
    
    protected GraphicImplAccess(GralMng mng)
    {
      super(GralValueBar.this, mng);
      if(GralValueBar.this.sColorLevels !=null){
        setBorderAndColorsImpl(GralValueBar.this.sColorLevels); 
      }
    }
    
    
    /**Sets the pixel values
     * @param valueP
     * @return true if a pixel value is changed. Only then refresh is necessary.
     */
    protected boolean setValue(float valueP)
    {
      final boolean chg;
      int value1, value2;
      int pixMax = horizontal ? pixBounds.dx : pixBounds.dy;
      value1 = (int)(pixMax * ((0.0F - minRange)/ (maxRange - minRange)));  //the 0-value
      value2 = (int)(pixMax * ((valueP - minRange) / (maxRange - minRange)));
      if(value1 < 0){ value1 = 0;}
      if(value1 > pixMax){ value1 = pixMax; }
      if(value2 < 0){ value2 = 0;}
      if(value2 > pixMax){ value2 = pixMax; }
      chg = this.pix0line !=value1 || this.pixvalue !=value2;
      if(chg) {
        this.pix0line = value1;
        this.pixvalue = value2;
        float level1 = minRange;
        //check in which range the value is assigned, set ixColor 
        if(fLevels !=null)
        for(ixColor = 0; ixColor < fLevels.length -1; ++ixColor){
          float level2 = fLevels[ixColor];
          if(  level1 < level2 && level1 <= valueP && valueP < level2
            || level1 > level2 && level2 <= valueP && valueP < level1
            )
          break; //found
          level1 = level2;
        }
      }
      return chg;
    }

    
    abstract public void setBorderAndColorsImpl(String[] sColorLevels);
    
    protected boolean horizontal(){ return horizontal; }
    
    protected int ixColor(){ return ixColor; }
    
    protected int pix0line(){ return pix0line; }
    
    protected int pixvalue(){ return pixvalue; }
    
  }


}

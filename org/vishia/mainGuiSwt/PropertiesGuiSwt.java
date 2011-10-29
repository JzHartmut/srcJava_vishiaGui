package org.vishia.mainGuiSwt;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.vishia.gral.base.GralGridProperties;
import org.vishia.gral.ifc.GralColor;


public class PropertiesGuiSwt extends GralGridProperties
{
	private final Device guiDevice;

  public final Font smallPromptFont;
  
  public final Font[] textFont = new Font[10];
  
  public final Font stdInputFont;
  
  public final Font stdButtonFont;
  
  Map<Integer, Color> colorsSwt = new TreeMap<Integer, Color>();
  
  private final Color colorBlack;
  
  public final Color colorGrid, colorGridStrong;
  
  /**A common background color for all widgets which are paint at the background. */
  public Color colorBackground;
  
  /**Initializes a properties object.
   * <br><br>
   * @param size number between 1..5 to determine the size of the content (font size, pixel per cell)
   */
  public PropertiesGuiSwt(Device device, char sizeC)
  { super(sizeC);
  	this.guiDevice = device;
    this.colorBlack = new Color(guiDevice, 0,0,0);
    this.colorGrid = colorSwt(0xe0e0e0);
    this.colorGridStrong = colorSwt(0xc0c0c0);
    this.colorBackground = colorSwt(colorBackground_);
    this.smallPromptFont = new Font(device, "Arial", smallPromptFontSize[size], SWT.NORMAL);
    this.stdInputFont = new Font(device, "Arial", stdInputFontSize[size], SWT.NORMAL);
    this.stdButtonFont = new Font(device, "Arial", stdButtonFontSize[size], SWT.NORMAL);
    this.textFont[0] = new Font(device, "Arial", stdTextFontSize[0][size], SWT.NORMAL);
    this.textFont[1] = new Font(device, "Arial", stdTextFontSize[1][size], SWT.NORMAL);
    this.textFont[2] = new Font(device, "Arial", stdTextFontSize[2][size], SWT.NORMAL);
    this.textFont[3] = new Font(device, "Arial", stdTextFontSize[3][size], SWT.NORMAL);
    this.textFont[4] = new Font(device, "Arial", stdTextFontSize[4][size], SWT.NORMAL);
    this.textFont[5] = new Font(device, "Arial", stdTextFontSize[5][size], SWT.NORMAL);
    this.textFont[6] = new Font(device, "Arial", stdTextFontSize[6][size], SWT.NORMAL);
    this.textFont[7] = new Font(device, "Arial", stdTextFontSize[7][size], SWT.NORMAL);
    this.textFont[8] = new Font(device, "Arial", stdTextFontSize[8][size], SWT.NORMAL);
    this.textFont[9] = new Font(device, "Arial", stdTextFontSize[9][size], SWT.NORMAL);
  }
  
  /**Returns a color with given Gui-independent color.
   * The SWT-Color instance is taken from a pool if the color is used already.
   * Elsewhere it is created newly and put into the pool.
   * @param color The given color in system-indpending form.
   * @return An instance of SWT-color
   */
  public Color colorSwt(GralColor color)
  {
    if(color.colorGuimpl == null){
      int colorValue = color.getColorValue();
      color.colorGuimpl = colorSwt(colorValue);
    } else if(!(color.colorGuimpl instanceof Color)){
      throw new IllegalArgumentException("unauthorized color setting");
    }
    return (Color)color.colorGuimpl;
  }
  
  
  /**Returns a color with given numeric color value.
   * The SWT-Color instance is taken from a pool if the color is used already.
   * Elsewhere it is created newly and put into the pool.
   * @param colorValue red, green and blue
   * @return An instance of SWT-color
   */
  public Color colorSwt(int colorValue){
  	Color color;
  	if(colorValue >=0 && colorValue < 0x1000000){
  		color = colorsSwt.get(colorValue);
	  	if(color==null){
	  		color = new Color(guiDevice, (colorValue >>16)&0xff, (colorValue >>8)&0xff, (colorValue)&0xff );
	      colorsSwt.put(colorValue, color);  //store it to reuse.
	  	}
  	} else {
  		color = colorBlack;  //The values -1... may be used for palettes.
  	}
  	return color;
  }
  
  /**Returns a color with given Gui-independent color name.
   * The SWT-Color instance is taken from a pool if the color is used already.
   * Elsewhere it is created newly and put into the pool.
   * @param sColorName One of the registered color names.
   * @return An instance of SWT-color
   */
  
  public Color color(String sColorname)
  { int nColor = getColorValue(sColorname);
    return colorSwt(nColor);
  }
  
  public Color colorBackgroundSwt(){ return colorSwt(colorBackground_); }
  
  /**Creates an instance of a GUI-system independent color with given SWT color.
   * @param color The SWT-color
   * @return a new instance.
   */
  public static GralColor createColorGui(Color color)
  { RGB rgb = color.getRGB();
    GralColor ret = new GralColor(rgb.red, rgb.green, rgb.blue);
    return ret;
  }
  

  
  public Font getTextFont(float size)
  {
  	if(size <=1.2f) return textFont[0];  //1, 1.1, 1.2
  	if(size <=1.4f) return textFont[1];  // 1 1/3, 
  	if(size <=1.6f) return textFont[2];  //1.5
  	if(size <=1.8f) return textFont[3];  //1 2/3
  	if(size <=2.0f) return textFont[4];  //2
  	if(size <=2.4f) return textFont[5];   //2 1/3
  	if(size <=2.8f) return textFont[6];   //2.5, 2 2/3
  	if(size <=3.1f) return textFont[7];   //3
  	if(size <=3.9f) return textFont[8];   //3.5
    return textFont[9];                   //>=4
  	
  }
  
  
  /*
   * JLabel   Trace-GUI           56 x 16
   * FileInputField              237 x 34
   * JButton                      73 x 26
   */
  
}

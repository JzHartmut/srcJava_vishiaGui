package org.vishia.gral.swt;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.vishia.gral.base.GralGridProperties;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;


public class SwtProperties extends GralGridProperties
{
	private final Device guiDevice;

  public final Font smallPromptFont;
  
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
  public SwtProperties(Device device, char sizeC)
  { super(sizeC);
  	this.guiDevice = device;
    this.colorBlack = new Color(guiDevice, 0,0,0);
    this.colorGrid = colorSwt(0xe0e0e0);
    this.colorGridStrong = colorSwt(0xc0c0c0);
    this.colorBackground = colorSwt(colorBackground_);
    this.smallPromptFont = new Font(device, "Arial", smallPromptFontSize[size], SWT.NORMAL);
    this.stdInputFont = new Font(device, "Arial", stdInputFontSize[size], SWT.NORMAL);
    this.stdButtonFont = new Font(device, "Arial", stdButtonFontSize[size], SWT.NORMAL);
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
  
  public Font getSwtFont(float fontSize){ return fontSwt(super.getTextFont(fontSize)); }

  
  /*
   * JLabel   Trace-GUI           56 x 16
   * FileInputField              237 x 34
   * JButton                      73 x 26
   */

  
  
  /**Returns a color with given Gui-independent color.
   * The SWT-Color instance is taken from a pool if the color is used already.
   * Elsewhere it is created newly and put into the pool.
   * @param color The given color in system-indpending form.
   * @return An instance of SWT-color
   */
  public Font fontSwt(GralFont font)
  {
    if(font.fontImpl == null){
      int styleSwt = 0;
      switch(font.style){
        case 'b': styleSwt |= SWT.BOLD; break;
        case 'B': styleSwt |= SWT.BOLD; break;
        case 'i': styleSwt |= SWT.ITALIC; break;
        case 'I': styleSwt |= SWT.BOLD | SWT.ITALIC; break;
        default: styleSwt = SWT.NORMAL;
      }
      String fontName;
      //NOTE: on SWT there are not Java standardfonts, there are platform-depending.
      if(font.fontName.equals(GralFont.fontMonospacedSansSerif)){ fontName = "Courier"; }
      else if(font.fontName.equals(GralFont.fontMonospacedSmall)){ fontName = "Courier"; }
      else if(font.fontName.equals(GralFont.fontMonospacedSerif)){ fontName = "Courier"; }
      else if(font.fontName.equals(GralFont.fontSansSerif)){ fontName = "Arial"; }
      else if(font.fontName.equals(GralFont.fontSerif)){ fontName = "Serif"; }
      else {fontName = font.fontName; }
      font.fontImpl = new Font(guiDevice, fontName, font.size, styleSwt);
    } else if(!(font.fontImpl instanceof Font)){
      throw new IllegalArgumentException("unauthorized font setting");
    }
    return (Font)font.fontImpl;
  }
  
  

  
  
  
}

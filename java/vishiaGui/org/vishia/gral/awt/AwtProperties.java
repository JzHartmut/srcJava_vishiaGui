package org.vishia.gral.awt;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.base.GralGridProperties;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;

public class AwtProperties extends GralGridProperties.ImplAccess
{

  public final Font smallPromptFont;
  
  public final Font[] textFontAwt = new Font[10];
  
  public final Font stdInputFont;
  
  public final Font stdButtonFont;
  
  Map<Integer, Color> colorsSwt = new TreeMap<Integer, Color>();
  
  private final Color colorBlack;
  
  public final Color colorGrid, colorGridStrong;
  
  /**A common background color for all widgets which are paint at the background. */
  public Color colorBackground;
  

  
  
  public AwtProperties(GralGridProperties gralProps)
  {
    //super(sizeC);
    this.colorBlack = new Color(0,0,0);
    this.colorGrid = new Color(0xe0e0e0);
    this.colorGridStrong = new Color(0xc0c0c0);
    this.colorBackground = new Color(0xffffff);
    this.smallPromptFont = new Font("Arial", 0, smallPromptFontSize[gralProps.size()]);
    this.stdInputFont = new Font("Arial", 0, stdInputFontSize[gralProps.size()]);
    this.stdButtonFont = new Font("Arial", 0, stdButtonFontSize[gralProps.size()]);
//    this.textFontAwt[0] = new Font("Arial", 0, GralGridProperties.stdTextFontSize[0][gralProps.size()]);
//    this.textFontAwt[1] = new Font("Arial", 0, stdTextFontSize[1][size]);
//    this.textFontAwt[2] = new Font("Arial", 0, stdTextFontSize[2][size]);
//    this.textFontAwt[3] = new Font("Arial", 0, stdTextFontSize[3][size]);
//    this.textFontAwt[4] = new Font("Arial", 0, stdTextFontSize[4][size]);
//    this.textFontAwt[5] = new Font("Arial", 0, stdTextFontSize[5][size]);
//    this.textFontAwt[6] = new Font("Arial", 0, stdTextFontSize[6][size]);
//    this.textFontAwt[7] = new Font("Arial", 0, stdTextFontSize[7][size]);
//    this.textFontAwt[8] = new Font("Arial", 0, stdTextFontSize[8][size]);
//    this.textFontAwt[9] = new Font("Arial", 0, stdTextFontSize[9][size]);
  }
  
  
  /**Returns a color with given Gui-independent color.
   * The SWT-Color instance is taken from a pool if the color is used already.
   * Elsewhere it is created newly and put into the pool.
   * @param color The given color in system-indpending form.
   * @return An instance of SWT-color
   */
  public Color colorAwt(GralColor color)
  {
    if(color.colorGuimpl == null){
      int colorValue = color.getColorValue();
      color.colorGuimpl = new Color(colorValue);
    } else if(!(color.colorGuimpl instanceof Color)){
      throw new IllegalArgumentException("unauthorized color setting");
    }
    return (Color)color.colorGuimpl;
  }
  
  
  
  
  /**Returns a implementation font with given Gui-independent font.
   * The SWT-Font instance is taken from a pool if the font is used already.
   * Elsewhere it is created newly and put into the pool.
   * @param forn The given Gral font in system-independent form.
   * @return An instance of SWT-Font
   */
  public Font fontAwt(GralFont font)
  {
    if(font.fontImpl == null){
      int styleSwt = 0;
      switch(font.style){
        case 'b': styleSwt |= Font.BOLD; break;
        case 'B': styleSwt |= Font.BOLD; break;
        case 'i': styleSwt |= Font.ITALIC; break;
        case 'I': styleSwt |= Font.BOLD | Font.ITALIC; break;
        default: styleSwt = Font.PLAIN;
      }
      String fontName;
      //NOTE: on SWT there are not Java standardfonts, there are platform-depending.
      if(font.fontName.equals(GralFont.fontMonospacedSansSerif)){ fontName = "Courier"; }
      else if(font.fontName.equals(GralFont.fontMonospacedSmall)){ fontName = "Courier"; }
      else if(font.fontName.equals(GralFont.fontMonospacedSerif)){ fontName = "Courier"; }
      else if(font.fontName.equals(GralFont.fontSansSerif)){ fontName = "Arial"; }
      else if(font.fontName.equals(GralFont.fontSerif)){ fontName = "Serif"; }
      else {fontName = font.fontName; }
      font.fontImpl = new Font(fontName, font.size, styleSwt);
    } else if(!(font.fontImpl instanceof Font)){
      throw new IllegalArgumentException("unauthorized font setting");
    }
    return (Font)font.fontImpl;
  }
  

  
}

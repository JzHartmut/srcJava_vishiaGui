package org.vishia.gral.awt;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.base.GralGridProperties;
import org.vishia.gral.ifc.GralColor;

public class AwtProperties extends GralGridProperties
{

  public final Font smallPromptFont;
  
  public final Font[] textFont = new Font[10];
  
  public final Font stdInputFont;
  
  public final Font stdButtonFont;
  
  Map<Integer, Color> colorsSwt = new TreeMap<Integer, Color>();
  
  private final Color colorBlack;
  
  public final Color colorGrid, colorGridStrong;
  
  /**A common background color for all widgets which are paint at the background. */
  public Color colorBackground;
  

  
  
  public AwtProperties(char sizeC)
  {
    super(sizeC);
    this.colorBlack = new Color(0,0,0);
    this.colorGrid = new Color(0xe0e0e0);
    this.colorGridStrong = new Color(0xc0c0c0);
    this.colorBackground = new Color(0xffffff);
    this.smallPromptFont = new Font("Arial", 0, smallPromptFontSize[size]);
    this.stdInputFont = new Font("Arial", 0, stdInputFontSize[size]);
    this.stdButtonFont = new Font("Arial", 0, stdButtonFontSize[size]);
    this.textFont[0] = new Font("Arial", 0, stdTextFontSize[0][size]);
    this.textFont[1] = new Font("Arial", 0, stdTextFontSize[1][size]);
    this.textFont[2] = new Font("Arial", 0, stdTextFontSize[2][size]);
    this.textFont[3] = new Font("Arial", 0, stdTextFontSize[3][size]);
    this.textFont[4] = new Font("Arial", 0, stdTextFontSize[4][size]);
    this.textFont[5] = new Font("Arial", 0, stdTextFontSize[5][size]);
    this.textFont[6] = new Font("Arial", 0, stdTextFontSize[6][size]);
    this.textFont[7] = new Font("Arial", 0, stdTextFontSize[7][size]);
    this.textFont[8] = new Font("Arial", 0, stdTextFontSize[8][size]);
    this.textFont[9] = new Font("Arial", 0, stdTextFontSize[9][size]);
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
  

  
}

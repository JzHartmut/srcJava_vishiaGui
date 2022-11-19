package org.vishia.gral.base;

import java.util.Map;
import java.util.TreeMap;

import org.vishia.bridgeC.IllegalArgumentExceptionJc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;

public final class GralGridProperties
{
  /**Version, history and license.
   * <ul>
   * <li>2015-10-24 Hartmut chg: Size C is now 10 pixel per grid unit, size D is the older size C. 
   * <li>2015-10-11 Hartmut chg: The text font should be lesser because g, q, y are not proper able to read.
   *   TODO think about finer steps between A..C, but don't change existing sizes because some graphics are tuned with them. 
   * <li>2010-00-00 Hartmut created. The idea of a grid layout with different sizes as basic.     
   * </ul>
   * <b>Copyright/Copyleft</b>:
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
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are indent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   * 
   */
  public final static int version = 0x20120303;

  
  /**This are the font sizes for some heights of fields in the given sizes of presentation. 
   * First index is the height of field, second is the size of presentation.
   */
  protected final static int[][] stdTextFontSize =
  { {4, 5, 5, 7, 7, 8, 9}  ////1, 1.1, 1.2
  , {5, 6, 6, 8, 9, 9,10}  // 1 1/3,
  , {5, 7, 7, 9, 9,10,12}  //1.5
  , {6, 9, 8, 10,10,12,14}  //1 2/3
  , {7, 10, 9, 11,12,14,18}  //2
  , {7, 11, 10,11,12,14,18}  //2 1/3
  , {9,12, 14, 11,12,14,18}  //2.5, 2 2/3
  , {12,14, 16, 12, 12,14,18}  //3
  , {12,16,18,12, 12,14,18}  //3.5
  , {15,18,20,12, 12,14,18}  //>=4
  };
  
  /** Number of pixel for fractional part and for the grid size.
   * The array is organized in a 2-dimensional array <code>[frac][size]</code>.
   * The <code>[frac][10]</code> contains the number of pixel for 1 grid unit. 
   * <ul>
   * <li>The first column contains 0 for frac = 0.
   * <li>The last column: <code>pixelFrac[size] [10]</code> contains the pixel size of the grid.
   * <li>The fractional part is given in even numbers (1, 3, 5, 7, 9) to divide in part of 1/6:
   *     1 = 1/6, 3 = 1/3, 5 = 1/2, 7 = 2/3, 9 = 5/6. It is decimal 0.166, 0.333, 0.5, 0.666, 0.833. 
   * <li>The fractional part is given in odd numbers (2, 4, 6, 8) to divide in part of 1/5 or decimal 2/10:
   *     2 = 1/5 = 0.2, 4 = 2/5 = 0.4, 6 = 3/5 = 0.6, 8 = 4/5 = 0.8.
   * </ul>
   * Typical usage for grid units and associated pixel units in size C:
   * <table>
   * <tr><th>Type                      </td><td>units</td><td>pixel</td></tr>
   * <tr><td>Label normal text size    </td><td>2   </td><td>16   </td></tr>
   * <tr><td>Button normal text size   </td><td>3   </td><td>24   </td></tr>
   * <tr><td>TextField                 </td><td>2   </td><td>16   </td></tr>
   * <tr><td>Large InputField          </td><td>4   </td><td>32   </td></tr>
   * </table>
   * Number of Grid Units for full display sizes. An application which needs about 40 lines of text or text fields 
   * needs 80 grid unit in vertical, it is about 80x120. It runs in a small window with 640 x 480 pixel on size A only. 
   * In full display mode on a standard display it can use the size C or D.
   * <table>
   * <tr><th>size        </td><td>A        </td><td>B      </td><td>C      </td><td>D      </td><td>E      </td><td>F      </td></td><td>G     </td></tr>
   * <tr><td>pixel/grid  </td><td> 6       </td><td>  7      </td><td>  8    </td><td>  9    </td><td>  10   </td><td>12  </td><td>15  </td></tr>
   * <tr><td>640 x 480   </td><td><b>106 x  80</b></td><td>91  x  68</td><td> 80 x  60</td><td> todo </td><td> todo </td><td> todo </td></tr>
   * <tr><td>800 x 600   </td><td>133 x 100</td><td><b>114 x  85</b></td><td>100 x  75</td><td> todo </td><td> todo </td><td> todo </td></tr>
   * <tr><td>1024 x 768  </td><td>170 x 128</td><td>146 x 109</td><td><b>128 x  96</b></td><td>113 x 85</td><td>102 x 76</td><td>85 x 64</td><td>68 x 51</td></tr>
   * <tr><td>1200 x 800  </td><td>200 x 133</td><td>171 x 114</td><td>150 x 100</td><td><b>133 x 88</b></td><td>120 x 80</td><td>100 x 66</td><td>80 x 53</td></tr>
   * <tr><td>1680 x 1024 </td><td>280 x 170</td><td>240 x 146</td><td>210 x 128</td><td>186 x 113</td><td>168 x 102</td><td>140 x 85</td><td>112 x 68</td></tr>
   * </table>
   * <table>
   * <tr><th>Type                       </td><td>unit</td><td>pixel</td></tr>
   * <tr><td>JLabel normal text size    </td><td>2   </td><td>0+6   </td></tr>
   * <tr><td>JButton normal text size   </td><td>3   </td><td>26   </td></tr>
   * <tr><td>InputField                 </td><td>4   </td><td>34   </td></tr>
   * </table>
   */
  protected final static int[][] pixelFrac = 
 //    1/6   1/3   1/2   2/3   5/6   Divisions of 2, 3 and 6
 //       1/5   2/5   3/5   4/5      Divisions of 5
 //     1  2  3  4  5  6  7  8  9   0   the number given
  { {0, 1, 1, 2, 2, 3, 4, 4, 5, 5,  6 }  //pixel size A
  , {0, 1, 1, 2, 3, 3, 4, 5, 6, 6,  7 }  //pixel size B
  , {0, 1, 2, 3, 3, 4, 5, 6, 6, 7,  8 }  //pixel size C
  , {0, 1, 2, 3, 4, 4, 5, 6, 7, 8,  9 }  //pixel size D
  , {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }  //pixel size E
  , {0, 1, 2, 4, 5, 6, 7, 8,10,11, 12 }  //pixel size F
  , {0, 2, 3, 5, 6, 7, 9,10,12,13, 15 }  //pixel size G
  };
  
  //protected final static int[][] yPixelFrac = xPixelFrac;
  	  
  protected final int xPixelUnit;

  //Map<Integer, GralColor> colors = new TreeMap<Integer, GralColor>();

  /**A common background color for all widgets which are paint at the background. */
  public GralColor colorBackground_;

  /**Up to 10 font sizes for following hight of lettes:
   * <table>
   * <tr><td>..1.2</td><td>..1.4</td><td>..1.6</td><td>..1.8</td><td>..2.0</td><td>..2.4</td><td>..2.8</td><td>..3.1</td><td>..3.9</td><td>3.9...</td></tr>
   * <tr><td>  0  </td><td>  1  </td><td>  2  </td><td>  3  </td><td>  4  </td><td>  5  </td><td>  6  </td><td>  7  </td><td>  8  </td><td>  9  </td><td></tr>
   * </table>
   * This table will be filled depending of the Grid size, see {@link #stdTextFontSize}.
   * 
   */
  public final GralFont[] textFont = new GralFont[10];
  

  public final GralFont[] fontMonospacedSansSerif = new GralFont[10];  
  
  /**The size of this propety set.*/
  protected int size;

	public GralGridProperties()
	{
		this.xPixelUnit = pixelFrac[size][10];
    colorBackground_ = color(0xeeeeee);
    this.textFont[0] = new GralFont("Arial", stdTextFontSize[0][size], 'n');
    this.textFont[1] = new GralFont("Arial", stdTextFontSize[1][size], 'n');
    this.textFont[2] = new GralFont("Arial", stdTextFontSize[2][size], 'n');
    this.textFont[3] = new GralFont("Arial", stdTextFontSize[3][size], 'n');
    this.textFont[4] = new GralFont("Arial", stdTextFontSize[4][size], 'n');
    this.textFont[5] = new GralFont("Arial", stdTextFontSize[5][size], 'n');
    this.textFont[6] = new GralFont("Arial", stdTextFontSize[6][size], 'n');
    this.textFont[7] = new GralFont("Arial", stdTextFontSize[7][size], 'n');
    this.textFont[8] = new GralFont("Arial", stdTextFontSize[8][size], 'n');
    this.textFont[9] = new GralFont("Arial", stdTextFontSize[9][size], 'n');
    String sMonospaced = GralFont.fontMonospacedSansSerif;
    this.fontMonospacedSansSerif[0] = new GralFont(sMonospaced, stdTextFontSize[0][size], 'n');
    this.fontMonospacedSansSerif[1] = new GralFont(sMonospaced, stdTextFontSize[1][size], 'n');
    this.fontMonospacedSansSerif[2] = new GralFont(sMonospaced, stdTextFontSize[2][size], 'n');
    this.fontMonospacedSansSerif[3] = new GralFont(sMonospaced, stdTextFontSize[3][size], 'n');
    this.fontMonospacedSansSerif[4] = new GralFont(sMonospaced, stdTextFontSize[4][size], 'n');
    this.fontMonospacedSansSerif[5] = new GralFont(sMonospaced, stdTextFontSize[5][size], 'n');
    this.fontMonospacedSansSerif[6] = new GralFont(sMonospaced, stdTextFontSize[6][size], 'n');
    this.fontMonospacedSansSerif[7] = new GralFont(sMonospaced, stdTextFontSize[7][size], 'n');
    this.fontMonospacedSansSerif[8] = new GralFont(sMonospaced, stdTextFontSize[8][size], 'n');
    this.fontMonospacedSansSerif[9] = new GralFont(sMonospaced, stdTextFontSize[9][size], 'n');

	}
  
	
	public void setSizeGui(char sizeC) {
	  int size = (sizeC - 'A');
    if(size <0 || size >= pixelFrac.length) throw new IllegalArgumentException("parameter size should be A.." + (char)('A' + pixelFrac.length));
    this.size = size; 
	}
	
	public int size() { return size; }
	
	public int getColorValue(String sColorName)
	{ //Integer colorValue = 
	  GralColor color = GralColor.getColor(sColorName);
	  if(color == null) return 0x606060;
	  else return color.getColorValue();
	}
	
  
  public GralFont getTextFont(float size)
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
  
  /**The type:
   * <ul>
   * <li>m: monospaced small
   * </ul>
   * The style: not supported yet. TODO i, b for italic, bold,...
   * 
   * @param fontHeight height of the text line in GralPos. 2.0f is standard.
   * @param type 
   * @param style 
   * @return
   */
  public GralFont getTextFont(float fontHeight, char type, char style)
  {
    int ifontSize;
    if(fontHeight <=1.2f) ifontSize = 0;  //1, 1.1, 1.2
    else if(fontHeight <=1.4f) ifontSize = 1;  // 1 1/3, 
    else if(fontHeight <=1.6f) ifontSize = 2;  //1.5
    else if(fontHeight <=1.8f) ifontSize = 3;  //1 2/3
    else if(fontHeight <=2.0f) ifontSize = 4;  //2
    else if(fontHeight <=2.4f) ifontSize = 5;   //2 1/3
    else if(fontHeight <=2.8f) ifontSize = 6;   //2.5, 2 2/3
    else if(fontHeight <=3.1f) ifontSize = 7;   //3
    else if(fontHeight <=3.9f) ifontSize = 8;   //3.5
    else ifontSize = 9;                   //>=4
    final GralFont font;
    switch(type){
      case 'm': font = fontMonospacedSansSerif[ifontSize]; break;
      default: font = textFont[ifontSize];
    }
    return font;
  }
  
  

	
  /**Returns a color with given numeric color value.
   * The color instance is taken from a pool if the color is used already.
   * Elsewhere it is created newly and put into the pool.
   * @param colorValue red, green and blue
   * @return An instance of color
   */
  public GralColor color(int colorValue){
    GralColor color;
    if(colorValue >=0 && colorValue < 0x1000000){
      color = GralColor.getColor(colorValue);
    } else {
      throw new IllegalArgumentExceptionJc("color value fault", colorValue);
    }
    return color;
  }
  

	
	
  public int yPixelFrac(int frac){ return pixelFrac[size][frac]; }
  
  
  /**Gets the number of pixel for one unit of x-direction. It is approximately the half width of the letter 'm' 
   * for the standard input field font. To support visibility of a dedicated number of chars, you should use
   * 2 * nrofChars units for a text field. In opposite, the Constructor for the javax.swing.TextField(int) 
   * sets the size of the text-field to the given number of 'm'-width. Here you should use factor 2.
   *  
   * NOTE: A possibility to derive the xPixelUnit() from the font wasn't found. The cohesion between the font-widht for 'm'
   * and the returned number of pixels is tested manually. It may be different if another font is used.
   * @return Number of pixel for 1 unit in x-direction.
   */
  public int xPixelUnit(){ return pixelFrac[size][10]; }
  
  public int yPixelUnit(){ return pixelFrac[size][10]; }

  public int xPixelFrac(int frac){ return pixelFrac[size][frac]; }


  /**
   *
   */
  public static abstract class ImplAccess {
    
    protected final static int[] smallPromptFontSize = stdTextFontSize[0];
    protected final static int[] stdInputFontSize =    stdTextFontSize[4];
    protected final static int[] stdButtonFontSize =   stdTextFontSize[7];
    
  }
  
}

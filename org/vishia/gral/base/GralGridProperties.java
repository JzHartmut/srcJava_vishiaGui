package org.vishia.gral.base;

import java.util.Map;
import java.util.TreeMap;

import org.vishia.bridgeC.IllegalArgumentExceptionJc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;

public class GralGridProperties
{
  /**Version, history and licence
   * 
   * <b>Copyright/Copyleft</b>:
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL ist not appropriate for a whole software product,
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

  
  protected final static int[] smallPromptFontSize = {5,5,6,6,7,8};  //{5,6,8,9,11,12};
  protected final static int[] stdInputFontSize =    {8,9,11,12,14,18};
  protected final static int[] stdButtonFontSize =   {10,11,12,14,16,20};
  
  /**This are the font sizes for some heights of fields in the given sizes of presentation. 
   * First index is the height of field, second is the size of presentation.
   */
  protected final static int[][] stdTextFontSize =
  { {5, 6, 7, 7, 8, 9}  ////1, 1.1, 1.2
  , {6, 6, 8, 9, 9,10}  // 1 1/3,
  , {6, 8, 9, 9,10,12}  //1.5
  , {7, 9,10,10,12,14}  //1 2/3
  , {8,10,11,12,14,18}  //2
  , {8,10,11,12,14,18}  //2 1/3
  , {9,10,11,12,14,18}  //2.5, 2 2/3
  , {9,10,11,12,14,18}  //3
  , {10,10,11,12,14,18}  //3.5
  , {10,10,11,12,14,18}  //>=4
  };
  
  /**Pixel per Y-Unit. 
   * <table>
   * <tr><th>Type                       </td><td>units</td><td>pixel</td></tr>
   * <tr><td>JLabel normal text size    </td><td>2   </td><td>16   </td></tr>
   * <tr><td>JButton normal text size   </td><td>3   </td><td>26   </td></tr>
   * <tr><td>TextField                  </td><td>2   </td><td>34   </td></tr>
   * <tr><td>InputField                 </td><td>4   </td><td>34   </td></tr>
   * </table>
   * Number of Units
   * <table>
   * <tr><th>size        </td><td>A      </td><td>B      </td><td>C      </td><td>D      </td><td>E      </td><td>F      </td></tr>
   * <tr><td>640 x 480   </td><td>91 x 68</td><td>80 x 68</td><td>71 x 68</td><td>58 x 68</td><td>91 x 68</td><td>35 x 68</td></tr>
   * <tr><td>800 x 600   </td><td>91 x 68</td><td>91 x 68</td><td>88 x 68</td><td>72 x 68</td><td>91 x 68</td><td>44 x 68</td></tr>
   * <tr><td>1024 x 768  </td><td>91 x 68</td><td>91 x 68</td><td>91 x 68</td><td>93 x 68</td><td>73 x 68</td><td>56 x 68</td></tr>
   * <tr><td>1200 x 800  </td><td>91 x 68</td><td>91 x 68</td><td>91 x 68</td><td>91 x 68</td><td>85 x 68</td><td>66 x 68</td></tr>
   * <tr><td>1680 x 1024 </td><td>91 x 68</td><td>91 x 68</td><td>91 x 68</td><td>140x 68</td><td>120 x 68</td><td>88 x 68</td></tr>
   * </table>
   */
  protected final static int[] yPixelUnit_ = {6,7,9,11,14, 18};
  
  /**Pixel per X-Unit. 
   * <table>
   * <tr><th>Type                       </td><td>unit</td><td>pixel</td></tr>
   * <tr><td>JLabel normal text size    </td><td>2   </td><td>0+6   </td></tr>
   * <tr><td>JButton normal text size   </td><td>3   </td><td>26   </td></tr>
   * <tr><td>InputField                 </td><td>4   </td><td>34   </td></tr>
   * </table>
   */
  protected final static int[] xPixelUnit_ = {6,7,9,11,14, 18}; //{6,7,8,10,13,16};
  
  /**Number of pixel for fractional part of position and size.
   * The fractional part is given in even numbers (1, 3, 5, 7, 9) to divide in part of 1/6.
   * 1 = 1/6, 3 = 1/3, 5 = 1/2, 7 = 2/3, 9 = 5/6.
   * The even numbers are approximate a rounded presentation: 0.166, 0.333, 0.5, 0.666, 0.833
   * 
   */
  protected final static int[][] xPixelFrac = 
 // 1/6   1/3   1/2   2/3   5/6   Divisions of 2 and 3
 //    1/5   2/5   3/5   4/5      Divisions of 5
 //  1  2  3  4  5  6  7  8  9    the number given
  { {0, 1, 1, 2, 2, 3, 4, 4, 5, 5 }  //pixel size A
  , {0, 1, 1, 2, 3, 3, 4, 5, 6, 6 }
  , {0, 1, 2, 3, 4, 5, 6, 6, 7, 8 }
  , {0, 2, 3, 4, 5, 6, 7, 8, 9,10 }
  , {0, 2, 3, 4, 5, 7, 8, 9,10,12 }
  , {0, 3, 4, 6, 7, 9,11,12,14,15 }
  };
  
  protected final static int[][] yPixelFrac = xPixelFrac;
  	  
  protected final int xPixelUnit;

  //Map<Integer, GralColor> colors = new TreeMap<Integer, GralColor>();

  /**A common background color for all widgets which are paint at the background. */
  public GralColor colorBackground_;

  public final GralFont[] textFont = new GralFont[10];
  

  public final GralFont[] fontMonospacedSansSerif = new GralFont[10];  
  
  /**The size of this propety set.*/
  protected final int size;

	public GralGridProperties(char sizeC)
	{
		int size = (sizeC - 'A');
	  if(size <0 || size >= stdInputFontSize.length) throw new IllegalArgumentException("parameter size should be 1.." + stdInputFontSize.length);
  	this.size = size; 
    this.xPixelUnit = xPixelUnit_[size];
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
  

	
	
  public int yPixelUnit(){ return yPixelUnit_[size]; }

  public int yPixelFrac(int frac){ return yPixelFrac[size][frac]; }
  
  
  /**Gets the number of pixel for one unit of x-direction. It is approximately the half width of the letter 'm' 
   * for the standard input field font. To support visibility of a dedicated number of chars, you should use
   * 2 * nrofChars units for a text field. In opposite, the Constructor for the javax.swing.TextField(int) 
   * sets the size of the text-field to the given number of 'm'-width. Here you should use factor 2.
   *  
   * NOTE: A possibility to derive the xPixelUnit() from the font wasn't found. The cohesion between the font-widht for 'm'
   * and the returned number of pixels is tested manually. It may be different if another font is used.
   * @return Number of pixel for 1 unit in x-direction.
   */
  public int xPixelUnit(){ return xPixelUnit_[size]; }
  
  public int xPixelFrac(int frac){ return xPixelFrac[size][frac]; }


  
  
}

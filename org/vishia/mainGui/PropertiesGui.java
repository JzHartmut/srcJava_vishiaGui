package org.vishia.mainGui;

import java.util.Map;
import java.util.TreeMap;

public class PropertiesGui
{
  protected final static int[] smallPromptFontSize = {5,6,8,9,11,12};
  protected final static int[] stdInputFontSize =    {9,10,11,12,14,18};
  protected final static int[] stdButtonFontSize =   {10,11,12,14,16,20};
  
  protected final static int[][] stdTextFontSize =
  { {5, 6, 7, 7, 8, 9}  ////1, 1.1, 1.2
  , {6, 6, 8, 9, 9,10}  // 1 1/3,
  , {7, 8, 9, 9,10,12}  //1.5
  , {8, 9,10,10,12,14}  //1 2/3
  , {9,10,11,12,14,18}  //2
  , {9,10,11,12,14,18}  //2 1/3
  , {9,10,11,12,14,18}  //2.5, 2 2/3
  , {9,10,11,12,14,18}  //3
  , {9,10,11,12,14,18}  //3.5
  , {9,10,11,12,14,18}  //>=4
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

  private final Map<String, Integer> colorNames = new TreeMap<String, Integer>();
  
  /**The size of this propety set.*/
  protected final int size;

	public PropertiesGui(char sizeC)
	{
		int size = (sizeC - 'A');
	  if(size <0 || size >= stdInputFontSize.length) throw new IllegalArgumentException("parameter size should be 1.." + stdInputFontSize.length);
  	this.size = size; 
    this.xPixelUnit = xPixelUnit_[size];
    setColorNames();
	}
  

  private void setColorNames(){
  	colorNames.put("white",  0xffffff); 
  	colorNames.put("gray",   0x808080);
  	colorNames.put("black",  0x000000);
  	colorNames.put("red",    0xff0000);
  	colorNames.put("green",  0x00ff00);
  	colorNames.put("blue",   0x0000ff);
  	colorNames.put("yellow", 0xffff00);
  	colorNames.put("magenta",0xff00ff);
  	colorNames.put("cyan",   0x00ffff);
  	colorNames.put("brown",  0x600020);
  	colorNames.put("amber",  0xffe000);
  	colorNames.put("orange",  0xffa000);
  	colorNames.put("violet",  0x6000ff);
    
  	//saturated colors
  	colorNames.put("wh", 0xffffff);
  	colorNames.put("gr", 0x808080);
  	colorNames.put("bk", 0x000000);
  	colorNames.put("rd", 0xff0000);
  	colorNames.put("gn", 0x00ff00);
  	colorNames.put("bl", 0x0000ff);
  	colorNames.put("ye", 0xffff00);
  	colorNames.put("ma", 0xff00ff);
  	colorNames.put("cy", 0x00ffff);
  	colorNames.put("bn", 0x400000);
  	colorNames.put("am", 0xffe000);
  	colorNames.put("or", 0xffa000);
  	colorNames.put("vi", 0x6000ff);
    
  	//light colors
  	colorNames.put("lgr", 0xc0c0c0);
  	colorNames.put("lrd", 0xff8080);
  	colorNames.put("lgn", 0x80ff80);
  	colorNames.put("lbl", 0x8080ff);
  	colorNames.put("lye", 0xffff00);
  	colorNames.put("lma", 0xff00ff);
  	colorNames.put("lcy", 0x00ffff);
  	colorNames.put("bn", 0x400000);
    
  	//pastel colors, especially for background color
  	colorNames.put("pgr", 0xf0f0f0);
  	colorNames.put("prd", 0xffe0e0);
  	colorNames.put("pgn", 0xe0ffe0);
  	colorNames.put("pbl", 0xe0e0ff);
  	colorNames.put("pye", 0xffffa0);
  	colorNames.put("pma", 0xffa0ff);
  	colorNames.put("pcy", 0xa0ffff);

  	//dark colors for forground
  	colorNames.put("dgr", 0x404040);
  	colorNames.put("drd", 0x800000);
  	colorNames.put("dgn", 0x008000);
  	colorNames.put("dbl", 0x000080);
  	colorNames.put("dye", 0x606000);
  	colorNames.put("dma", 0x600060);
  	colorNames.put("dcy", 0x006060);
  }
	
	public int getColorValue(String sColorName)
	{ Integer colorValue = colorNames.get(sColorName);
	  if(colorValue == null) return 0x606060;
	  else return colorValue;
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

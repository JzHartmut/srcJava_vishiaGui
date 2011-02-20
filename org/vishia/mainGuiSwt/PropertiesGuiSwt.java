package org.vishia.mainGuiSwt;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.vishia.mainGui.PropertiesGui;


public class PropertiesGuiSwt extends PropertiesGui
{
	private final Device guiDevice;

  public final Font smallPromptFont;
  
  public final Font[] textFont = new Font[10];
  
  public final Font stdInputFont;
  
  public final Font stdButtonFont;
  
  Map<Integer, Color> colors = new TreeMap<Integer, Color>();
  
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
    this.colorGrid = color(0xe0e0e0);
    this.colorGridStrong = color(0xc0c0c0);
    this.colorBackground = color(0xeeeeee);
    this.smallPromptFont = new Font(device, "Arial", smallPromptFontSize[size], SWT.ITALIC);
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
  
  public Color color(int colorValue){
  	Color color;
  	if(colorValue >=0 && colorValue < 0x1000000){
  		color = colors.get(colorValue);
	  	if(color==null){
	  		color = new Color(guiDevice, (colorValue >>16)&0xff, (colorValue >>8)&0xff, (colorValue)&0xff );
	      colors.put(colorValue, color);  //store it to reuse.
	  	}
  	} else {
  		color = colorBlack;  //The values -1... may be used for palettes.
  	}
  	return color;
  }
  
  public Color color(String sColorname)
  { int nColor = getColorValue(sColorname);
    return color(nColor);
  }
  
  public Font getTextFont(int ySize, int ySizeFrac)
  {
  	float size = ySize + ySizeFrac/10.0F;
  	if(size <=1.25F) return textFont[0];  //1, 1.1, 1.2
  	if(size <=1.45F) return textFont[1];  // 1 1/3, 
  	if(size <=1.55F) return textFont[2];  //1.5
  	if(size <=1.95F) return textFont[3];  //1 2/3
  	if(size <=2.05F) return textFont[4];  //2
  	if(size <=2.4F) return textFont[5];   //2 1/3
  	if(size <=2.8F) return textFont[6];   //2.5, 2 2/3
  	if(size <=3.1F) return textFont[7];   //3
  	if(size <=3.9F) return textFont[8];   //3.5
    return textFont[9];                   //>=4
  	
  }
  
  
  /*
   * JLabel   Trace-GUI           56 x 16
   * FileInputField              237 x 34
   * JButton                      73 x 26
   */
  
}

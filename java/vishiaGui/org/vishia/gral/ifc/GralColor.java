package org.vishia.gral.ifc;

import java.util.Map;
import java.util.TreeMap;

/**This class defines a data structure to hold color values in a usual but system-independent form
 * and defines some standard colors by name.
 * 
 *  
 * @author Hartmut Schorrig
 *
 */
public class GralColor
{
  
  
  /**Version, history and license.
   * <ul>
   * <li>2012-10-11 Hartmut new concepts from new ColorChooser with usualNames, starting.
   * <li>2012-09-07 Hartmut  bugfix: toString: color with hexa value with leading 00 for example "0x00ff00".
   * <li>2012-06-09 Hartmut new: {@link #getColor(String)} now accepts a "0xhexa-Value" for a color name.
   *  If the color name does not match, a magenta color is returned. The method returns a color in any case.
   * <li>2011-10-01 Hartmut new: color lbk light black darker than dark gray. Change values for gray.
   * <li>2011-09-08 Hartmut new: some enhancements, new colors.
   * <li>2011-09-04 Hartmut chg: Rename from ColorGui to GralColor
   * <li>2011-09-04 Hartmut chg: Move the {@link #getColor(String)} and {@link #getColor(int)} with the colorContainer
   *     from gridPanel/PropertiesGUI to this.
   * <li>2011-05-14 Hartmut new: Reference to the {@link #colorGuimpl} to get the graphic base system color instance if need.
   * <li>2010-00-00 Hartmut created. It had contain only the 3 int red, green, blue     
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
  public final static String version = "2015-10-11";
  
  
  
  /**Values 0..255 for the base colors. Note: don't change by any application, read only! Changes are overridden on any usage. */
  public int red, green, blue;
  
  /**The rgb value, same value as red, green, blue. */
  protected int rgb;

  /**The unique short name for this color. */
  public final String name;
  
  /**String with one or more usual names for this color, especially HTML names, X11 names. 
   * If more as one name is given, it is separated by comma. 
   */
  protected String usualNames;
  
  /**The color-instance for the implementation can be set from the implementation with the necessary type
   * to minimize the effort in dynamic instances.  */
  public Object colorGuimpl;
  
  /**Deprecated
   * @param red
   * @param green
   * @param blue
   * @deprecated use {@link #getColor(int, int, int)} because more as one instances for the same color is prevented then
   *   and the color have a correct name if the value matches to a existent color.
   */
  public GralColor(int red, int green, int blue){
    this.red = red; this.green = green; this.blue = blue;
    this.rgb = ((red << 16) & 0xff0000) | ((green << 8) & 0xff00) | (blue & 0xff);
    name = String.format("0x%06X",new Integer(this.rgb));
  }
  
  /**
   * @param color
   * @deprecated only private using, because more as one instances for the same color is prevented then
   *   and the color have a correct name if the value matches to a existent color.
   */
  public GralColor(int color){
    this.rgb = color;
    this.red = (color >>16) & 0xff; this.green = (color >>8) & 0xff; this.blue = (color >>0) & 0xff;
    name = String.format("0x%06X", new Integer(color));
  }
  
  /**Deprecated, only private usage. Use 
   * @param color
   * @deprecated only private using, because more as one instances for the same color is prevented then
   *   and the color have a correct name if the value matches to a existent color.
   */
  public GralColor(String name, int color){
    this.rgb = color;
    this.red = (color >>16) & 0xff; this.green = (color >>8) & 0xff; this.blue = (color >>0) & 0xff;
    this.name = name;
  }
  
  public int getColorValue(){ return (red & 0xff)<<16 | (green & 0xff)<<8 | (blue & 0xff); } 
  
  public int rgb(){ return rgb; } 
  
  public String usualNames(){ return usualNames; }
  
  public String getColorName(){ return name; }
  
  
  /**The color is not used till yet or it is changed.
   * @return
   */
  public boolean notUsed(){ return colorGuimpl == null; }
  
  private static class GralColorContainer
  {
    Map<String, GralColor> colorsByName = new TreeMap<String, GralColor>();
    Map<Integer, GralColor> colorsByValue = new TreeMap<Integer, GralColor>();
    
    public GralColor[][] stdColors = new GralColor[6][19];
    
    
    GralColorContainer()
    {
      addColor("white",  0xffffff);
      addColor("gray",   0x808080);
      addColor("black",  0x000000);
      addColor("red",    0xff0000);
      addColor("lime",   0x00ff00);
      addColor("green",  0x00e000);
      addColor("blue",   0x0000ff);
      addColor("yellow", 0xffff00);
      addColor("magenta",0xff00ff);
      addColor("cyan",   0x00ffff);
      addColor("brown",  0x600020);
      addColor("amber",  0xffe000);
      addColor("orange", 0xffa000);
      addColor("violet", 0x6000ff);
      addColor("mauve",  0xd0d0ff);
      addColor("pink",   0xff0080);
      addColor("red-purple", 0xff0060);
      addColor("purple", 0x800080);
      
      addColor("ye", 0xffff00); //
      addColor("ygn", 0x9ACD32); //yellow green
      addColor("lm", 0x00ff00); //lime
      addColor("gn", 0x00e000);
      addColor("g2", 0x00e020);
      addColor("cy", 0x00ffff);
      
      //saturated colors
      addColor("wh", 0xffffff);
      addColor("gr", 0xa0a0a0);
      addColor("bk", 0x000000);
      addColor("ma", 0xff00ff);
      addColor("ma2", 0xff00c0);
      addColor("ma3", 0xff0080);
      addColor("ma4", 0xff0040);
      addColor("rpu", 0xff0040);  //red purple
      addColor("pu", 0x800080);  //purple html
      addColor("rd", 0xff2020);
      addColor("or", 0xffa000);  //html: ffa500
      addColor("cc", 0xd2691e);  //chocolate html
      addColor("am", 0xffe000);
      addColor("sye", 0xc0c000); //saturated yellow
      addColor("ol", 0x808000); //olive
      addColor("od", 0x6b8e23); //olive drab html
      addColor("fgn",  0x228b22);  //forest green html
      addColor("sgn", 0x2E8B57); //sea green html
      addColor("sg2", 0x00c020); //saturated green-blue
      addColor("scy", 0x00c0c0);
      addColor("tl", 0x008080);  //teal html
      addColor("bl", 0x0000ff);
      addColor("ubl", 0x000080); //navi html ultramarin blue
      addColor("vi", 0x6000ff);

      addColor("bn", 0x600020);
      addColor("mv", 0xd0d0ff);
      addColor("pk", 0xff0080);
      
      //light colors
      addColor("lgr", 0xd0d0d0);
      addColor("lrd", 0xff8080);
      addColor("lgn", 0x80ff80);
      addColor("lbl", 0xa0a0ff);
      addColor("lye", 0xffff80);
      addColor("lam", 0xfff080);
      addColor("lor", 0xffa060);
      addColor("lma", 0xff80ff);
      addColor("lmv", 0xe0e0ff);
      addColor("lcy", 0x00ffff);
      addColor("lbk", 0x404040);
      
      //pastel colors, especially for background color
      addColor("pgr", 0xf0f0f0);
      addColor("prd", 0xffe0e0);
      addColor("pgn", 0xe0ffe0);
      addColor("pbl", 0xe0e0ff);
      addColor("pye", 0xffffc0);
      addColor("pam", 0xfff0c0);
      addColor("por", 0xffc080);
      addColor("pmv", 0xfff0ff);
      addColor("pma", 0xffa0ff);
      addColor("pcy", 0xa0ffff);

      //dark colors for forground
      addColor("dgr", 0x606060);
      addColor("drd", 0x800000);
      addColor("dor", 0xff8c00);  //dark orange html
      addColor("dbn", 0x400010);
      addColor("dol", 0x556b2f); //dark olive, html
      addColor("dgn", 0x008000);
      addColor("dbl", 0x000080);
      addColor("dye", 0x606000);
      addColor("dma", 0x600060);
      addColor("dcy", 0x006060);
    
    
      int iRow = 0;
    
      addColor(iRow, 0, "pMa1", 0xffc0fF);
      addColor(iRow, 1, "pMa2", 0xffc0e0);
      addColor(iRow, 2, "pRd1", 0xffd0d0);
      addColor(iRow, 3, "pRd2", 0xffe0c0);
      addColor(iRow, 4, "pRd3", 0xfff0c0);
      addColor(iRow, 5, "pYe1", 0xffffd0);
      addColor(iRow, 6, "pYe2", 0xe0ffc0);
      addColor(iRow, 7, "pYe3", 0xe0ffc0);
      addColor(iRow, 8, "pYe4", 0xe0ffc0);
      addColor(iRow, 9, "pGn1", 0xd0ffd0);
      addColor(iRow,10, "pGn2", 0xc0fff0);
      addColor(iRow,11, "pGn3", 0xc0ffe0);
      addColor(iRow,12, "pGn4", 0xc0fff0);
      addColor(iRow,13, "pCy1", 0xc0ffff);
      addColor(iRow,14, "pCy2", 0xd0f0ff);
      addColor(iRow,15, "pCy3", 0xc0e0ff);
      addColor(iRow,16, "pBl1", 0xd0d0ff);
      addColor(iRow,17, "pBl2", 0xe0c0ff);
      addColor(iRow,18, "pBl3", 0xf0c0ff);

      iRow +=1;
      addColor(iRow, 0, "lMa1", 0xff80fF);
      addColor(iRow, 1, "lMa2", 0xff80c8);
      addColor(iRow, 2, "lRd1", 0xffb8b8);
      addColor(iRow, 3, "lRd2", 0xffc8a0);
      addColor(iRow, 4, "lRd3", 0xffe090);
      addColor(iRow, 5, "lYe1", 0xffff80);
      addColor(iRow, 6, "lYe2", 0xe0ff80);
      addColor(iRow, 7, "lYe3", 0xc0ff80);
      addColor(iRow, 8, "lYe4", 0xa0ff80);
      addColor(iRow, 9, "lGn1", 0x70ff70);
      addColor(iRow,10, "lGn2", 0x80ffa0);
      addColor(iRow,11, "lGn3", 0x80ffc0);
      addColor(iRow,12, "lGn4", 0x80ffe0);
      addColor(iRow,13, "lCy1", 0x80ffff);
      addColor(iRow,14, "lCy2", 0x98e8ff);
      addColor(iRow,15, "lCy3", 0xb0d0ff);
      addColor(iRow,16, "lBl1", 0xc0c0ff);
      addColor(iRow,17, "lBl2", 0xd0b0ff);
      addColor(iRow,18, "lBl3", 0xe0a0ff);

      iRow +=1;
      addColor(iRow, 0, "Ma1", 0xff00fF);
      addColor(iRow, 1, "Ma2", 0xff4090);
      addColor(iRow, 2, "Rd1", 0xff7070);
      addColor(iRow, 3, "Rd2", 0xff9040);
      addColor(iRow, 4,  "Am", 0xffe000);
      addColor(iRow, 5, "Ye1", 0xffff00);
      addColor(iRow, 6, "Ye2", 0xc0ff00);
      addColor(iRow, 7, "Ye3", 0x80ff00);
      addColor(iRow, 8, "Ye4", 0x40ff00);
      addColor(iRow, 9, "Gn1", 0x00ff00);
      addColor(iRow,10, "Gn2", 0x00ff40);
      addColor(iRow,11, "Gn3", 0x00ff80);
      addColor(iRow,12, "Gn4", 0x00ffc0);
      addColor(iRow,13, "Cy1", 0x00ffff);
      addColor(iRow,14, "Cy2", 0x30d0ff);
      addColor(iRow,15, "Cy3", 0x60a0ff);
      addColor(iRow,16, "Bl1", 0x8080ff);
      addColor(iRow,17, "Bl2", 0xa060ff);
      addColor(iRow,18, "Bl3", 0xc040ff);

      iRow +=1;
      addColor(iRow, 0, "gMa1", 0xd080d0);
      addColor(iRow, 1, "gMa2", 0xd080c8);
      addColor(iRow, 2, "gRd1", 0xd0b8b8);
      addColor(iRow, 3, "gRd2", 0xd0c8a0);
      addColor(iRow, 4, "gRd3", 0xd0e090);
      addColor(iRow, 5, "gYe1", 0xd0d080);
      addColor(iRow, 6, "gYe2", 0xe0d080);
      addColor(iRow, 7, "gYe3", 0xc0d080);
      addColor(iRow, 8, "gYe4", 0xa0d080);
      addColor(iRow, 9, "gGn1", 0x70d070);
      addColor(iRow,10, "gGn2", 0x80d0a0);
      addColor(iRow,11, "gGn3", 0x80d0c0);
      addColor(iRow,12, "gGn4", 0x80d0e0);
      addColor(iRow,13, "gCy1", 0x80d0d0);
      addColor(iRow,14, "gCy2", 0x98e8d0);
      addColor(iRow,15, "gCy3", 0xb0d0d0);
      addColor(iRow,16, "gBl1", 0xc0c0d0);
      addColor(iRow,17, "gBl2", 0xd0b0d0);
      addColor(iRow,18, "gBl3", 0xe0a0d0);

      iRow +=1;
      addColor(iRow, 0, "sMa1", 0xd000d0);
      addColor(iRow, 1, "sMa2", 0xe02080);
      addColor(iRow, 2, "sRd1", 0xff0000);
      addColor(iRow, 3, "sRd2", 0xffc000);
      addColor(iRow, 4, "sRd3", 0xff8000);
      addColor(iRow, 5, "sYe1", 0xe8e800);
      addColor(iRow, 6, "sYe2", 0xc0c000);
      addColor(iRow, 7, "sYe3", 0x80b000);
      addColor(iRow, 8, "sYe4", 0x40b000);
      addColor(iRow, 9, "sGn1", 0x00c000);
      addColor(iRow,10, "sGn2", 0x00b040);
      addColor(iRow,11, "sGn3", 0x00b080);
      addColor(iRow,12, "sGn4", 0x00b0c0);
      addColor(iRow,13, "sCy1", 0x00d0d0);
      addColor(iRow,14, "sCy2", 0x0080d0);
      addColor(iRow,15, "sCy3", 0x3030ff);
      addColor(iRow,16, "sBl1", 0x0000ff);
      addColor(iRow,17, "sBl2", 0x8030ff);
      addColor(iRow,18, "sBl3", 0xc000ff);

      iRow +=1;
      addColor(iRow, 0, "dMa1", 0x800080);
      addColor(iRow, 1, "dMa2", 0x800000);
      addColor(iRow, 2, "dRd1", 0xc00000);
      addColor(iRow, 3, "dRd2", 0x808000);
      addColor(iRow, 4, "dRd3", 0x800000);
      addColor(iRow, 5, "dYe1", 0xc0c000);
      addColor(iRow, 6, "dYe2", 0x606000);
      addColor(iRow, 7, "dYe3", 0x405000);
      addColor(iRow, 8, "dYe4", 0x20000);
      addColor(iRow, 9, "dGn1", 0x008000);
      addColor(iRow,10, "dGn2", 0x004000);
      addColor(iRow,11, "dGn3", 0x005840);
      addColor(iRow,12, "dGn4", 0x005860);
      addColor(iRow,13, "dCy1", 0x006868);
      addColor(iRow,14, "dCy2", 0x004068);
      addColor(iRow,15, "dCy3", 0x171780);
      addColor(iRow,16, "dBl1", 0x0000c0);
      addColor(iRow,17, "dBl2", 0x2000a0);
      addColor(iRow,18, "dBl3", 0x200080);

}
    
    private void addColor(int i1, int i2, String name, int value)
    { GralColor color = new GralColor(name, value);
      stdColors[i1][i2] = color;
      colorsByName.put(name, color);
      colorsByValue.put(new Integer(value), color);
      
    }

    private void addColor(String name, int value)
    { GralColor color = new GralColor(name, value);
      colorsByName.put(name, color);
      colorsByValue.put(new Integer(value), color);
      
    }
  }
  
  private static GralColorContainer container = new GralColorContainer();
  
  /**Gets a color by its name.
   * The following named colors are available:
   * <br>Full name simple colors:
   * <ul>
   * <li>"white",  0xffffff);
   * <li>"gray",   0x808080);
   * <li>"black",  0x000000);
   * <li>"red",    0xff0000);
   * <li>"green",  0x00ff00);
   * <li>"blue",   0x0000ff);
   * <li>"yellow", 0xffff00);
   * <li>"magenta",0xff00ff);
   * <li>"cyan",   0x00ffff);
   * <li>"brown",  0x600020);
   * <li>"amber",  0xffe000);
   * <li>"orange",  0xffa000);
   * <li>"pink",   0xff0080);
   * <li>"violet",  0x6000ff);
   * <li>"purple",  0xff00ff);
   * </ul>
      
   * <br>saturated colors: The same as full name colors. All colors are designated with 2 letters.
   * <ul>
   * <li>"wh", white, 0xffffff);
   * <li>"gr", gray, 0xa0a0a0);
   * <li>"bk", black, 0x000000);
   * <li>"rd", red, 0xff0000);
   * <li>"gn", green, 0x00e000);
   * <li>"bl", blue, 0x0000ff);
   * <li>"ye", yellow, 0xffff00);
   * <li>"ma", magenta, 0xff00ff);
   * <li>"cy", cyan, 0x00ffff);
   * <li>"bn", brown, 0x600020);
   * <li>"am", amber, 0xffe000);
   * <li>"or", orange, 0xffa000);
   * <li>"pk", orange, 0xff0080);
   * <li>"vi", violet 0x6000ff);
   * <li>"pu", purple, de: purpurfarben 0xff0060);
   * </ul>
      
   * <br>light colors: The more light version is designated with a "l" before the 2-letter color name.
   * <ul>
   * <li>"lgr", 0xd0d0d0);
   * <li>"lrd", 0xffb0b0);
   * <li>"lgn", 0xa0ffa0);
   * <li>"lbl", 0xa0a0ff);
   * <li>"lye", 0xffff80);
   * <li>"lma", 0xffc0ff); 
   * <li>"lcy", 0xa0ffff);
   * <li>"lbk", 0x404040);  a black which is lighter, but darker as dark gray
   * </ul>
      
   * <br>pastel colors, especially for background color. They are designates with "p" before the 2-letter color name.
   * <ul>
   * <li>"pgr", 0xf0f0f0);
   * <li>"prd", 0xffe0e0);
   * <li>"pgn", 0xe0ffe0);
   * <li>"pbl", 0xf0f0ff);
   * <li>"pye", 0xffffc0);
   * <li>"pma", 0xffe0ff);
   * <li>"pcy", 0xd0ffff);
   * </ul>

   * <br>dark colors for lines and fonts: They are designates with "p" before the 2-letter color name.
   * <ul>
   * <li>"dgr", 0x606060);
   * <li>"drd", 0x800000);
   * <li>"dbn", 0x400010);
   * <li>"dgn", 0x008000);
   * <li>"dbl", 0x000080);
   * <li>"dye", 0x606000);
   * <li>"dma", 0x600060);
   * <li>"dcy", 0x006060);
   * </ul>
   * Additional a hexa value can be given in form "0xffffff" as name. 
   * @param name The name or 0xhexa
   * @return The proper color from the color container. The color is not created as a new instance if it is contained
   *   in the container already.
   *   If the color name does not match, a magenta color is returned.
   */
  public static GralColor getColor(String name){ 
    GralColor color;
    if(name.startsWith("0x")){
      try{
        int colorValue = Integer.parseInt(name.substring(2).trim(), 16);
        color = getColor(colorValue);
      }catch(NumberFormatException exc){
        color = container.colorsByName.get("ma");
      }
    } else {
      color = container.colorsByName.get(name);
      if(color == null){
        color = container.colorsByName.get("ma");
      }
    }
    return color;
  }
  
  public static GralColor getStdColor(int color, int brightness){
    return container.stdColors[color][brightness];
  }

  
  public static GralColor getColor(int value)
  { GralColor color = container.colorsByValue.get(new Integer(value));
    if(color == null){
      color = new GralColor(value);
      container.colorsByValue.put(new Integer(value), color);
    }
    return color;
  }
  
  public static GralColor getColor(int red, int green, int blue)
  { int value = ((red << 16) & 0xff0000) | ((green << 8) & 0xff00) | (blue & 0xff);
    GralColor color = container.colorsByValue.get(new Integer(value));
    if(color == null){
      color = new GralColor(value);
      container.colorsByValue.put(new Integer(value), color);
    }
    return color;
  }
  
  @Override public String toString(){
    int color = ((red << 16) & 0xff0000) | ((green << 8) & 0xff00) | (blue & 0xff);
    // name = container.colorsByValue.get(color);
    //String name = null;
    return (name !=null ? name +":" : "") + String.format("0x%06X",new Integer(color));
  }
}

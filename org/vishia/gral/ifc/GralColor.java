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
  
  
  /**Version and history:
   * <ul>
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
  /**Version, history and license.
   * <ul>
   * <li>2011-06-00 Hartmut created
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
  public final static int version = 20111001;
  
  /**Values 0..255 for the base colors. */
  public final int red, green, blue;

  //public final String name;
  
  /**The color-instance for the implementation can be set from the implementation with the necessary type
   * to minimize the effort in dynamic instances.  */
  public Object colorGuimpl;
  
  public GralColor(int red, int green, int blue){
    this.red = red; this.green = green; this.blue = blue;
  }
  
  public GralColor(int color){
    this.red = (color >>16) & 0xff; this.green = (color >>8) & 0xff; this.blue = (color >>0) & 0xff;
  }
  
  public int getColorValue(){ return (red & 0xff)<<16 | (green & 0xff)<<8 | (blue & 0xff); } 
  
  
  private static class GralColorContainer
  {
    Map<String, GralColor> colorsByName = new TreeMap<String, GralColor>();
    Map<Integer, GralColor> colorsByValue = new TreeMap<Integer, GralColor>();
    
    GralColorContainer()
    {
      addColor("white",  0xffffff);
      addColor("gray",   0x808080);
      addColor("black",  0x000000);
      addColor("red",    0xff0000);
      addColor("green",  0x00ff00);
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
      addColor("purple", 0xff0060);
      
      //saturated colors
      addColor("wh", 0xffffff);
      addColor("gr", 0xa0a0a0);
      addColor("bk", 0x000000);
      addColor("rd", 0xff0000);
      addColor("gn", 0x00ff00);
      addColor("bl", 0x0000ff);
      addColor("ye", 0xffff00);
      addColor("am", 0xffe000);
      addColor("or", 0xffa000);
      addColor("ma", 0xff00ff);
      addColor("cy", 0x00ffff);
      addColor("bn", 0x600020);
      addColor("mv", 0xd0d0ff);
      addColor("vi", 0x6000ff);
      addColor("pk", 0xff0080);
      addColor("pu", 0xff0040);
      
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
      addColor("dbn", 0x400010);
      addColor("dgn", 0x008000);
      addColor("dbl", 0x000080);
      addColor("dye", 0x606000);
      addColor("dma", 0x600060);
      addColor("dcy", 0x006060);
    }
    
    private void addColor(String name, int value)
    { GralColor color = new GralColor(value);
      colorsByName.put(name, color);
      colorsByValue.put(value, color);
      
    }
  }
  
  static GralColorContainer container = new GralColorContainer();
  
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
   * </ul>
      
   * <br>saturated colors: The same as full name colors. All colors are designated with 2 letters.
   * <ul>
   * <li>"wh", white, 0xffffff);
   * <li>"gr", gray, 0xa0a0a0);
   * <li>"bk", black, 0x000000);
   * <li>"rd", red, 0xff0000);
   * <li>"gn", green, 0x00ff00);
   * <li>"bl", blue, 0x0000ff);
   * <li>"ye", yellow, 0xffff00);
   * <li>"ma", magenta, 0xff00ff);
   * <li>"cy", cyan, 0x00ffff);
   * <li>"bn", brown, 0x600020);
   * <li>"am", amber, 0xffe000);
   * <li>"or", orange, 0xffa000);
   * <li>"vi", violet 0x6000ff);
   * <li>"pu", purple, de: purpurfarben 0xff0060);
   * </ul>
      
   * <br>light colors: The more light version is designated with a "l" before the 2-letter color name.
   * <ul>
   * <li>"lgr", 0xd0d0d0);
   * <li>"lrd", 0xff8080);
   * <li>"lgn", 0x80ff80);
   * <li>"lbl", 0x8080ff);
   * <li>"lye", 0xffff00);
   * <li>"lma", 0xff00ff);
   * <li>"lcy", 0x00ffff);
   * <li>"lbk", 0x404040);  a black which is lighter, but darker as dark gray
   * </ul>
      
   * <br>pastel colors, especially for background color. They are designates with "p" before the 2-letter color name.
   * <ul>
   * <li>"pgr", 0xf0f0f0);
   * <li>"prd", 0xffe0e0);
   * <li>"pgn", 0xe0ffe0);
   * <li>"pbl", 0xe0e0ff);
   * <li>"pye", 0xffffa0);
   * <li>"pma", 0xffa0ff);
   * <li>"pcy", 0xa0ffff);
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
  
  public static GralColor getColor(int value)
  { GralColor color = container.colorsByValue.get(value);
    if(color == null){
      color = new GralColor(value);
      container.colorsByValue.put(value, color);
    }
    return color;
  }
  
  @Override public String toString(){
    int color = ((red << 16) & 0xff0000) | ((green << 8) & 0xff00) | (blue & 0xff);
    // name = container.colorsByValue.get(color);
    String name = null;
    if(name !=null) return name;
    else return String.format("0x%6X",color);
  }
}

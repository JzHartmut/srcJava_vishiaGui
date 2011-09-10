package org.vishia.gral.ifc;

import java.util.Map;
import java.util.TreeMap;

public class GralColor
{
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
      addColor("pink",   0xff0080);
      addColor("purple", 0xff0060);
      
      //saturated colors
      addColor("wh", 0xffffff);
      addColor("gr", 0x808080);
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
      addColor("vi", 0x6000ff);
      addColor("pk", 0xff0080);
      addColor("pu", 0xff0040);
      
      //light colors
      addColor("lgr", 0xc0c0c0);
      addColor("lrd", 0xff8080);
      addColor("lgn", 0x80ff80);
      addColor("lbl", 0x8080ff);
      addColor("lye", 0xffff80);
      addColor("lam", 0xfff080);
      addColor("lor", 0xffa060);
      addColor("lma", 0xff80ff);
      addColor("lcy", 0x00ffff);
      
      //pastel colors, especially for background color
      addColor("pgr", 0xf0f0f0);
      addColor("prd", 0xffe0e0);
      addColor("pgn", 0xe0ffe0);
      addColor("pbl", 0xe0e0ff);
      addColor("pye", 0xffffc0);
      addColor("pam", 0xfff0c0);
      addColor("por", 0xffc080);
      addColor("pma", 0xffa0ff);
      addColor("pcy", 0xa0ffff);

      //dark colors for forground
      addColor("dgr", 0x404040);
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
   * <li>"gr", gray, 0x808080);
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
   * <li>"lgr", 0xc0c0c0);
   * <li>"lrd", 0xff8080);
   * <li>"lgn", 0x80ff80);
   * <li>"lbl", 0x8080ff);
   * <li>"lye", 0xffff00);
   * <li>"lma", 0xff00ff);
   * <li>"lcy", 0x00ffff);
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
   * <li>"dgr", 0x404040);
   * <li>"drd", 0x800000);
   * <li>"dbn", 0x400010);
   * <li>"dgn", 0x008000);
   * <li>"dbl", 0x000080);
   * <li>"dye", 0x606000);
   * <li>"dma", 0x600060);
   * <li>"dcy", 0x006060);
   * </ul>
   * @param name
   * @return
   */
  public static GralColor getColor(String name){ return container.colorsByName.get(name); }
  
  public static GralColor getColor(int value)
  { GralColor color = container.colorsByValue.get(value);
    if(color == null){
      color = new GralColor(value);
      container.colorsByValue.put(value, color);
    }
    return color;
  }
  
}
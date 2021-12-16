//==JZcmd==
//JZcmd main(){
//JZcmd   if($1=="AWT") { java org.vishia.gral.test.GralColorShow.mainAWT();
//JZcmd   } else { java org.vishia.gral.test.GralColorShow.main(); } 
//JZcmd }
//==endJZcmd==
package org.vishia.gral.test;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.awt.AwtFactory;
import org.vishia.gral.base.GralColorConv;
import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralMouseWidgetAction_ifc;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc._GralChgColor;
import org.vishia.gral.swt.SwtFactory;
import org.vishia.gral.widget.GralLabel;
import org.vishia.math.CurveInterpolation;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.Debugutil;
import org.vishia.util.KeyCode;
import org.vishia.util.StringPartFromFileLines;
import org.vishia.util.StringPartScan;


/*Test with jzcmd: call jzcmd with this java file with its full path:
D:/vishia/Java/srcJava_vishiaGui/org/vishia/gral/test/GralColorShow.java
==JZcmd==
java org.vishia.gral.test.GralColorShow.main(null);                 
==endJZcmd==
 */

/**This class opens a window with some colors sorted horizontal with saturation and vertical with color value.
 * 
 * @author Hartmut Schorrig
 *
 */
public class GralColorShow
{
  
  /**The version, history and license.
   * <ul>
   * <li>2015-09-12 Hartmut now generates some colors via HSB values.
   * <li>2015-01-01 Hartmut created: .
   * </ul>
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
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   * 
   */
  public static final String version = "2015-09-12";

  
  class Index {
    int ixCol, ixLight;
    //int ixColNext, ixColPrev;
    //int ixLightNext, ixLightPrev;
    
  }
  
  
  /**All attributes should be stored in a GralColor TODO.
   */
  class XXXColorValue {
    GralColor color;
    int rgb;
    String shortname, name;
    public XXXColorValue(int rgb, String shortname, String name)
    { this.color = new GralColor(shortname, rgb);
      this.rgb = rgb;
      this.shortname = shortname;
      this.name = name;
    }
  }
  
  
  
  
  class ColorWithField {
    GralColor co;
    final Index index;
    float[] hsb = new float[3];
    float[] hls = new float[3];
    final GralTextField wdgColor;
    
    /**True if a graphic is initialized and opened . */
    boolean bActive;
    
    ColorWithField(GralColor co, String shortname, int ixCol, int ixBright, int colValue) {
      this.co = co;
      this.index = new Index();
      this.index.ixCol = ixCol; index.ixLight = ixBright;
      this.wdgColor = new GralTextField("" + ixCol + "," + ixBright); //, GralTextField.Type.editable);
      this.wdgColor.setBackColor(co, 0);
      this.wdgColor.setData(this);
      //this.wdgColor.setActionFocused(actionFocusColor);
      this.wdgColor.setActionChange(actionEditColor);
      this.wdgColor.setActionMouse(null, GralMouseWidgetAction_ifc.mUserAll);
      //this.wdgColor.setTextColor(colText);
    }
    
    @Override public String toString(){ return co.name + ": [" + index.ixLight + ", " + index.ixCol + "]"; }
  }
  
  GralMng gralMng = GralMng.get();
  
  GralFactory gralFactory;
  
  public static void main(String[] args){ main(); }

    
  public static void mainAWT(){
    GralColorShow main = new GralColorShow();
    main.gralFactory = new AwtFactory();
    //main.execute();
    main.execute1();
  }
  
  public static void main(){
    GralColorShow main = new GralColorShow();
    main.gralFactory = new SwtFactory();
    //main.execute();
    main.execute1();
  }
  
  private void execute(){
    genColorFrame();
    genDefaultConfig();
    /*
    try {
      readConfig();
    } catch (IOException e1) {
      System.err.println("GralColor - config file error, " + e1.getMessage());
    } catch(ParseException exc) {
      System.err.println("GralColor - cannot read config, " + exc.getMessage());
    }
    */
    LogMessage log = new LogMessageStream(System.out);
    GralWindow wind = gralFactory.createWindow(log, "Show Colors", 'B', 150, 10,1000, 800);
    gralMng = wind.gralMng();
    gralMng.gralDevice.addDispatchOrder(initGraphicFullColors);
    //initGraphic.awaitExecution(1, 0);
    while(gralMng.gralDevice.isRunning()){
      try{ Thread.sleep(100);} 
      catch (InterruptedException e)
      { //dialogZbnfConfigurator.terminate();
      }
    }
      
  }
  
  
  
  
  private void execute1(){
    genColorFrame();
    try {
      readConfig();
    } catch (IOException e1) {
      System.err.println("GralColor - config file error, " + e1.getMessage());
    } catch(ParseException exc) {
      System.err.println("GralColor - cannot read config, " + exc.getMessage());
    }
    GralFactory gralFactory = new SwtFactory();
    LogMessage log = new LogMessageStream(System.out);
    GralWindow wind = gralFactory.createWindow(log, "Show Colors", 'B', 150, 10,1000, 800);
    gralMng = wind.gralMng();
    gralMng.gralDevice.addDispatchOrder(initGraphicLessColors);
    //initGraphic.awaitExecution(1, 0);
    while(gralMng.gralDevice.isRunning()){
      try{ Thread.sleep(100);} 
      catch (InterruptedException e)
      { //dialogZbnfConfigurator.terminate();
      }
    }
      
  }
  
  
  
  
  class ColHue{
    final String colorName;
    final float colorValue;

    public ColHue(String colorName, float colorValue)
    { this.colorName = colorName;
      this.colorValue = colorValue;
    }
  }
  
  
  
  ColHue[] colHue =   ////
  { new ColHue("ma",  0.0f)  //pu
  , new ColHue("pk",  2.0f) 
  //)), new ColHue("rp",  2.5f) 
  , new ColHue("rd",  4.0f)  //rd
  , new ColHue("ro",  5.0f) 
  , new ColHue("or",  6.0f) 
  , new ColHue("yo",  7.0f) //or
  , new ColHue("am",  7.5f) //am
  , new ColHue("ye",  8.0f) //ye
  , new ColHue("gy",  8.4f) 
  , new ColHue("yg",  9.0f)                         
  , new ColHue("ng", 10.0f) 
  , new ColHue("gn", 12.0f)  //gn
  //, new ColHue("gc", 13.5f) 
  , new ColHue("sg", 14.0f)  //cy
  , new ColHue("cy", 16.0f) //cy
  , new ColHue("cb", 17.5f) 
  , new ColHue("nb", 19.0f) 
  , new ColHue("bl", 20.0f)   //bl
  , new ColHue("vb", 21.0f)   //bl                   
  , new ColHue("vi", 22.0f)   //vi
  , new ColHue("vm", 23.0f)   //vi
  };
  
  
  
  String[][] namesGray = 
  { {"gr", "mg", "rg", "yg", "gg", "cg", "bg"}, {"g2", "gm", "gd", "gy", "ge", "gc", "gb"} };

  
  /**Class to store a color with light-value and saturation
   * @author hartmut
   *
   */
  class LightSat{
    final String lName;
    final float light;
    final float sat, sat2;
    
    public LightSat(String lName, float light, float sat)
    { this.lName = lName;
      this.light = light;
      this.sat = sat;
      this.sat2 = sat;
    }
    
    public LightSat(String lName, float light, float sat, float sat2)
    { this.lName = lName;
      this.light = light;
      this.sat = sat;
      this.sat2 = sat2;
    }
    
  }
  
  
  /**The 2 tables of gray colors. Saturation for table 1 and table 2*/
  LightSat[] gray1Sat =  ////
  { new LightSat("p1", 1.90f, 0.02f, 0.05f )
  , new LightSat("p2", 1.80f, 0.03f, 0.07f )
  , new LightSat("l1", 1.70f, 0.04f, 0.10f  )
  , new LightSat("l2", 1.60f, 0.05f, 0.12f  )
  , new LightSat("l3", 1.50f, 0.06f, 0.14f  )
  , new LightSat("l4", 1.40f, 0.08f, 0.16f  )
  , new LightSat("l5", 1.30f, 0.10f, 0.18f  )
  , new LightSat("d1", 1.20f, 0.12f, 0.20f  )
  , new LightSat("d2", 1.10f, 0.12f, 0.20f  )
  , new LightSat("d3", 1.00f, 0.15f, 0.25f  )
  , new LightSat("d4", 0.80f, 0.18f, 0.30f  )
  , new LightSat("d5", 0.60f, 0.20f, 0.40f  )
  , new LightSat("d6", 0.40f, 0.40f, 0.60f  )
  , new LightSat("d7", 0.20f, 0.60f, 1.0f  )
  };
  
  
  /**Use this table to test the gray light in comparison with color lights
   * 
   */
  LightSat[] gray1Sat_test =  ////
  { new LightSat("p1", 1.90f, 1.0f, 0.5f )
  , new LightSat("p2", 1.80f, 1.0f, 0.5f )
  , new LightSat("l1", 1.70f, 1.0f, 0.5f  )
  , new LightSat("l2", 1.60f, 1.0f, 0.5f  )
  , new LightSat("l3", 1.50f, 1.0f, 0.5f  )
  , new LightSat("l4", 1.40f, 1.0f, 0.5f  )
  , new LightSat("l5", 1.30f, 1.0f, 0.5f  )
  , new LightSat("d1", 1.20f, 1.0f, 0.5f  )
  , new LightSat("d2", 1.10f, 1.0f, 0.5f  )
  , new LightSat("d3", 1.00f, 1.0f, 0.5f  )
  , new LightSat("d4", 0.80f, 1.0f, 0.5f  )
  , new LightSat("d5", 0.60f, 1.0f, 0.5f  )
  , new LightSat("d6", 0.40f, 1.0f, 0.5f  )
  , new LightSat("d7", 0.20f, 1.0f, 0.5f  )
  };
  
  
  /**Table which determines the rows of the color table.
   * It give light and saturation values.
   */
  LightSat[] lightSat_html =  ////
  { new LightSat("p1", 1.95f, 1.00f )
  , new LightSat("p2", 1.90f, 1.00f )
  , new LightSat("p3", 1.85f, 1.00f )
  , new LightSat("p4", 1.80f, 1.00f )
  , new LightSat("p5", 1.80f, 0.80f )
  , new LightSat("l1", 1.70f, 1.00f )
  , new LightSat("l2", 1.60f, 1.00f )
  , new LightSat("l3", 1.60f, 0.80f )
  , new LightSat("l4", 1.50f, 1.00f )
  , new LightSat("l5", 1.40f, 1.00f )
  , new LightSat("s1", 1.30f, 1.00f )
  , new LightSat("g1", 1.30f, 0.70f )
  , new LightSat("g2", 1.30f, 0.40f )
  , new LightSat("s2", 1.20f, 1.00f )
  , new LightSat("s3", 1.10f, 1.00f )
  , new LightSat("g3", 1.20f, 0.30f )                                                                 
  , new LightSat("s4", 1.10f, 0.50f )                                                                 
  , new LightSat("g4", 1.10f, 0.70f )                                                                 
  , new LightSat("s5", 1.00f, 1.00f )
  , new LightSat("s6", 0.90f, 1.00f )
  , new LightSat("g5", 0.90f, 0.70f )
  , new LightSat("g6", 0.90f, 0.50f )
  , new LightSat("g7", 0.80f, 0.40f )
  , new LightSat("g8", 0.80f, 0.60f )
  , new LightSat("s7", 0.80f, 1.00f )
  , new LightSat("d1", 0.65f, 1.00f )                        
  , new LightSat("d2", 0.60f, 0.70f )
  , new LightSat("g9", 0.60f, 0.50f )
  , new LightSat("d3", 0.50f, 0.60f )
  , new LightSat("d4", 0.45f, 1.00f )
  , new LightSat("d5", 0.40f, 0.70f )
  , new LightSat("d6", 0.30f, 1.00f )
  };
  
  
  LightSat[] lightSat_test =  //// test
  { new LightSat("p1", 1.95f, 1.00f )
  , new LightSat("p2", 1.90f, 1.00f )
  , new LightSat("p3", 1.85f, 1.00f )
  , new LightSat("p4", 1.80f, 1.00f )
  , new LightSat("p5", 1.75f, 1.00f )
  , new LightSat("l1", 1.70f, 1.00f )
  , new LightSat("l2", 1.60f, 1.00f )
  , new LightSat("l3", 1.50f, 1.00f )
  , new LightSat("l4", 1.50f, 1.00f )
  , new LightSat("l5", 1.50f, 1.00f )
  , new LightSat("s1", 1.40f, 1.00f )
  , new LightSat("g1", 1.30f, 1.00f )
  , new LightSat("s2", 1.20f, 1.00f )
  , new LightSat("s3", 1.10f, 1.00f )
  , new LightSat("g2", 1.10f, 0.00f )                                                                 
  , new LightSat("g3", 1.00f, 0.10f )                                                                 
  , new LightSat("g4", 1.00f, 0.20f )                                                                 
  , new LightSat("s4", 1.00f, 0.30f )
  , new LightSat("s5", 1.00f, 0.40f )
  , new LightSat("g5", 1.00f, 0.60f )
  , new LightSat("g6", 1.00f, 0.8f )
  , new LightSat("g7", 1.00f, 1.00f )
  , new LightSat("g8", 0.90f, 1.00f )
  , new LightSat("s6", 0.80f, 1.00f )
  , new LightSat("d1", 0.70f, 1.00f )                        
  , new LightSat("d2", 0.60f, 1.00f )
  , new LightSat("g9", 0.50f, 1.00f )
  , new LightSat("d3", 0.40f, 1.00f )
  , new LightSat("d4", 0.30f, 1.00f )
  , new LightSat("d5", 0.20f, 1.00f )
  , new LightSat("d6", 0.10f, 1.00f )
  };
  
  
  LightSat[] lightSat_test2 =  //// test
  { new LightSat("p1", 1.50f, 0.00f )
  , new LightSat("p2", 1.50f, 0.10f )
  , new LightSat("p3", 1.50f, 0.20f )
  , new LightSat("p4", 1.50f, 0.30f )
  , new LightSat("p5", 1.50f, 0.40f )
  , new LightSat("l1", 1.50f, 0.60f )
  , new LightSat("l2", 1.50f, 0.80f )
  , new LightSat("l3", 1.50f, 0.90f )
  , new LightSat("l4", 1.50f, 1.00f )
  , new LightSat("l5", 1.00f, 0.00f )
  , new LightSat("s1", 1.00f, 0.10f )
  , new LightSat("g1", 1.00f, 0.20f )
  , new LightSat("s2", 1.00f, 0.30f )
  , new LightSat("s3", 1.00f, 0.40f )
  , new LightSat("g2", 1.00f, 0.50f )                                                                 
  , new LightSat("g3", 1.00f, 0.60f )                                                                 
  , new LightSat("g4", 1.00f, 0.70f )                                                                 
  , new LightSat("s4", 1.00f, 0.80f )
  , new LightSat("s5", 1.00f, 0.90f )
  , new LightSat("g5", 1.00f, 1.00f )
  , new LightSat("g6", 0.65f, 0.00f )
  , new LightSat("g7", 0.65f, 0.10f )
  , new LightSat("g8", 0.65f, 0.20f )
  , new LightSat("s6", 0.65f, 0.30f )
  , new LightSat("d1", 0.65f, 0.40f )                        
  , new LightSat("d2", 0.65f, 0.50f )
  , new LightSat("g9", 0.65f, 0.60f )
  , new LightSat("d3", 0.65f, 0.70f )
  , new LightSat("d4", 0.65f, 0.80f )
  , new LightSat("d5", 0.65f, 0.90f )
  , new LightSat("d6", 0.65f, 1.00f )
  };
  
  
  
  LightSat[] lightSat = lightSat_html;
  
  String[] valTest = {"", ""};
  

  String[][] colorsLess = 
  { { "ma", ""}
  , { "", ""}
  };
  
  
  String[][] colorsAll = 
  { { "p1ma", "p1pk", "p1rd", "p1ro", "p1op", "p1yo", "p1am", "p1ye", "p1gy", "p1yg", "p1ng", "p1gn", "p1sg", "p1cy", "p1cb", "p1nb", "p1bl", "p1vb", "p1vi", "p1vm", "p1ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "p2ma", "p2pk", "p2rd", "p2ro", "p2op", "p2yo", "p2am", "p2ye", "p2gy", "p2yg", "p2ng", "p2gn", "p2sg", "p2cy", "p2cb", "p2nb", "p2bl", "p2vb", "p2vi", "p2vm", "p2ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "p3ma", "p3pk", "p3rd", "p3ro", "p3op", "p3yo", "p3am", "p3ye", "p3gy", "p3yg", "p3ng", "p3gn", "p3sg", "p3cy", "p3cb", "p3nb", "p3bl", "p3vb", "p3vi", "p3vm", "p3ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "p4ma", "p4pk", "p4rd", "p4ro", "p4op", "p4yo", "p4am", "p4ye", "p4gy", "p4yg", "p4ng", "p4gn", "p4sg", "p4cy", "p4cb", "p4nb", "p4bl", "p4vb", "p4vi", "p4vm", "p4ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "l1ma", "l1pk", "l1rd", "l1ro", "l1op", "l1yo", "l1am", "l1ye", "l1gy", "l1yg", "l1ng", "l1gn", "l1sg", "l1cy", "l1cb", "l1nb", "l1bl", "l1vb", "l1vi", "l1vm", "l1ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "l2ma", "l2pk", "l2rd", "l2ro", "l2op", "l2yo", "l2am", "l2ye", "l2gy", "l2yg", "l2ng", "l2gn", "l2sg", "l2cy", "l2cb", "l2nb", "l2bl", "l2vb", "l2vi", "l2vm", "l2ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "l3ma", "l3pk", "l3rd", "l3ro", "l3op", "l3yo", "l3am", "l3ye", "l3gy", "l3yg", "l3ng", "l3gn", "l3sg", "l3cy", "l3cb", "l3nb", "l3bl", "l3vb", "l3vi", "l3vm", "l3ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "l4ma", "l4pk", "l4rd", "l4ro", "l4op", "l4yo", "l4am", "l4ye", "l4gy", "l4yg", "l4ng", "l4gn", "l4sg", "l4cy", "l4cb", "l4nb", "l4bl", "l4vb", "l4vi", "l4vm", "l4ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "l5ma", "l5pk", "l5rd", "l5ro", "l5op", "l5yo", "l5am", "l5ye", "l5gy", "l5yg", "l5ng", "l5gn", "l5sg", "l5cy", "l5cb", "l5nb", "l5bl", "l5vb", "l5vi", "l5vm", "l5ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "s1ma", "s1pk", "s1rd", "s1ro", "s1op", "s1yo", "s1am", "s1ye", "s1gy", "s1yg", "s1ng", "s1gn", "s1sg", "s1cy", "s1cb", "s1nb", "s1bl", "s1vb", "s1vi", "s1vm", "s1ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "g1ma", "g1pk", "g1rd", "g1ro", "g1op", "g1yo", "g1am", "g1ye", "g1gy", "g1yg", "g1ng", "g1gn", "g1sg", "g1cy", "g1cb", "g1nb", "g1bl", "g1vb", "g1vi", "g1vm", "g1ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "s2ma", "s2pk", "s2rd", "s2ro", "s2op", "s2yo", "s2am", "s2ye", "s2gy", "s2yg", "s2ng", "s2gn", "s2sg", "s2cy", "s2cb", "s2nb", "s2bl", "s2vb", "s2vi", "s2vm", "s2ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "s3ma", "s3pk", "s3rd", "s3ro", "s3op", "s3yo", "s3am", "s3ye", "s3gy", "s3yg", "s3ng", "s3gn", "s3sg", "s3cy", "s3cb", "s3nb", "s3bl", "s3vb", "s3vi", "s3vm", "s3ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "g2ma", "g2pk", "g2rd", "g2ro", "g2op", "g2yo", "g2am", "g2ye", "g2gy", "g2yg", "g2ng", "g2gn", "g2sg", "g2cy", "g2cb", "g2nb", "g2bl", "g2vb", "g2vi", "g2vm", "g2ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "g3ma", "g3pk", "g3rd", "g3ro", "g3op", "g3yo", "g3am", "g3ye", "g3gy", "g3yg", "g3ng", "g3gn", "g3sg", "g3cy", "g3cb", "g3nb", "g3bl", "g3vb", "g3vi", "g3vm", "g3ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "g4ma", "g4pk", "g4rd", "g4ro", "g4op", "g4yo", "g4am", "g4ye", "g4gy", "g4yg", "g4ng", "g4gn", "g4sg", "g4cy", "g4cb", "g4nb", "g4bl", "g4vb", "g4vi", "g4vm", "g4ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "s4ma", "s4pk", "s4rd", "s4ro", "s4op", "s4yo", "s4am", "s4ye", "s4gy", "s4yg", "s4ng", "s4gn", "s4sg", "s4cy", "s4cb", "s4nb", "s4bl", "s4vb", "s4vi", "s4vm", "s4ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "s5ma", "s5pk", "s5rd", "s5ro", "s5op", "s5yo", "s5am", "s5ye", "s5gy", "s5yg", "s5ng", "s5gn", "s5sg", "s5cy", "s5cb", "s5nb", "s5bl", "s5vb", "s5vi", "s5vm", "s5ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "g5ma", "g5pk", "g5rd", "g5ro", "g5op", "g5yo", "g5am", "g5ye", "g5gy", "g5yg", "g5ng", "g5gn", "g5sg", "g5cy", "g5cb", "g5nb", "g5bl", "g5vb", "g5vi", "g5vm", "g5ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "g6ma", "g6pk", "g6rd", "g6ro", "g6op", "g6yo", "g6am", "g6ye", "g6gy", "g6yg", "g6ng", "g6gn", "g6sg", "g6cy", "g6cb", "g6nb", "g6bl", "g6vb", "g6vi", "g6vm", "g6ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "g7ma", "g7pk", "g7rd", "g7ro", "g7op", "g7yo", "g7am", "g7ye", "g7gy", "g7yg", "g7ng", "g7gn", "g7sg", "g7cy", "g7cb", "g7nb", "g7bl", "g7vb", "g7vi", "g7vm", "g7ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "g8ma", "g8pk", "g8rd", "g8ro", "g8op", "g8yo", "g8am", "g8ye", "g8gy", "g8yg", "g8ng", "g8gn", "g8sg", "g8cy", "g8cb", "g8nb", "g8bl", "g8vb", "g8vi", "g8vm", "g8ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "s6ma", "s6pk", "s6rd", "s6ro", "s6op", "s6yo", "s6am", "s6ye", "s6gy", "s6yg", "s6ng", "s6gn", "s6sg", "s6cy", "s6cb", "s6nb", "s6bl", "s6vb", "s6vi", "s6vm", "s6ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "d1ma", "d1pk", "d1rd", "d1ro", "d1op", "d1yo", "d1am", "d1ye", "d1gy", "d1yg", "d1ng", "d1gn", "d1sg", "d1cy", "d1cb", "d1nb", "d1bl", "d1vb", "d1vi", "d1vm", "d1ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "d2ma", "d2pk", "d2rd", "d2ro", "d2op", "d2yo", "d2am", "d2ye", "d2gy", "d2yg", "d2ng", "d2gn", "d2sg", "d2cy", "d2cb", "d2nb", "d2bl", "d2vb", "d2vi", "d2vm", "d2ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "g9ma", "g9pk", "g9rd", "g9ro", "g9op", "g9yo", "g9am", "g9ye", "g9gy", "g9yg", "g9ng", "g9gn", "g9sg", "g9cy", "g9cb", "g9nb", "g9bl", "g9vb", "g9vi", "g9vm", "g9ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "d3ma", "d3pk", "d3rd", "d3ro", "d3op", "d3yo", "d3am", "d3ye", "d3gy", "d3yg", "d3ng", "d3gn", "d3sg", "d3cy", "d3cb", "d3nb", "d3bl", "d3vb", "d3vi", "d3vm", "d3ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "d4ma", "d4pk", "d4rd", "d4ro", "d4op", "d4yo", "d4am", "d4ye", "d4gy", "d4yg", "d4ng", "d4gn", "d4sg", "d4cy", "d4cb", "d4nb", "d4bl", "d4vb", "d4vi", "d4vm", "d4ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "d5ma", "d5pk", "d5rd", "d5ro", "d5op", "d5yo", "d5am", "d5ye", "d5gy", "d5yg", "d5ng", "d5gn", "d5sg", "d5cy", "d5cb", "d5nb", "d5bl", "d5vb", "d5vi", "d5vm", "d5ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "d6ma", "d6pk", "d6rd", "d6ro", "d6op", "d6yo", "d6am", "d6ye", "d6gy", "d6yg", "d6ng", "d6gn", "d6sg", "d6cy", "d6cb", "d6nb", "d6bl", "d6vb", "d6vi", "d6vm", "d6ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  };
  
  
  String[][] colors4 = 
  { { "p1ma", "p1pk", "p1rd", "p1or", "p1yo", "p1ye", "p1yg", "p1gn", "p1sg", "p1cy", "p1bl", "p1vi", "p1w2" ,"p1mw" ,"p1rw", "whye", "p1gw", "p1cw", "whbl"}
  , { "p3ma", "p3pk", "p3rd", "p3or", "p3yo", "p3ye", "p3yg", "p3gn", "p3sg", "p3cy", "p3bl", "p3vi", "p2w2" ,"p2mw" ,"p2rw", "p2yw", "p2gw", "p2cw", "p2bw" }
  , { "l1ma", "l1pk", "l1rd", "l1or", "l1yo", "l1ye", "l1yg", "l1gn", "l1sg", "l1cy", "l1bl", "l1vi", "l1g2" ,"l1mg" ,"l1rg", "l1yg", "l1gg", "l1cg", "l1bg" }
  , { "l3ma", "l3pk", "l3rd", "l3or", "l3yo", "l3ye", "l3yg", "l3gn", "l3sg", "l3cy", "l3bl", "l3vi", "l2g2" ,"l2mg" ,"l2rg", "l2yg", "l2gg", "l2cg", "l2bg" }
  , { "l5ma", "l5pk", "l5rd", "l5or", "l5yo", "l5ye", "l5yg", "l5gn", "l5sg", "l5cy", "l5bl", "l5vi", "l3g2" ,"l3mg" ,"l3rg", "l3yg", "l3gg", "l3cg", "l3bg" }
  , { "g1ma", "g1pk", "g1rd", "g1or", "g1yo", "g1ye", "g1yg", "g1gn", "g1sg", "g1cy", "g1bl", "g1vi", "l4g2" ,"l4mg" ,"l4rg", "l4yg", "l4gg", "l4cg", "l4bg" }
  , { "s3ma", "s3pk", "s3rd", "s3or", "s3yo", "s3ye", "s3yg", "s3gn", "s3sg", "s3cy", "s3bl", "s3vi", "l5g2" ,"l5mg" ,"l5rg", "l5yg", "l5gg", "l5cg", "l5bg" }
  , { "g3ma", "g3pk", "g3rd", "g3or", "g3yo", "g3ye", "g3yg", "g3gn", "g3sg", "g3cy", "g3bl", "g3vi", "d1g2" ,"d1mg" ,"d1rg", "d1yg", "d1gg", "d1cg", "d1bg" }
  , { "s4ma", "s4pk", "s4rd", "s4or", "s4yo", "s4ye", "s4yg", "s4gn", "s4sg", "s4cy", "s4bl", "s4vi", "d2g2" ,"d2mg" ,"d2rg", "d2yg", "d2gg", "d2cg", "d2bg" }
  , { "g5ma", "g5pk", "g5rd", "g5or", "g5yo", "g5ye", "g5yg", "g5gn", "g5sg", "g5cy", "g5bl", "g5vi", "d3g2" ,"d3mg" ,"d3rg", "d3yg", "d3gg", "d3cg", "d3bg" }
  , { "g7ma", "g7pk", "g7rd", "g7or", "g7yo", "g7ye", "g7yg", "g7gn", "g7sg", "g7cy", "g7bl", "g7vi", "d4g2" ,"d4mg" ,"d4rg", "d4yg", "d4gg", "d4cg", "d4bg" }
  , { "s6ma", "s6pk", "s6rd", "s6or", "s6yo", "s6ye", "s6yg", "s6gn", "s6sg", "s6cy", "s6bl", "s6vi", "d5g2" ,"d5mg" ,"d5rg", "d5yg", "d5gg", "d5cg", "d5bg" }
  , { "d2ma", "d2pk", "d2rd", "d2or", "d2yo", "d2ye", "d2yg", "d2gn", "d2sg", "d2cy", "d2bl", "d2vi", "d6g2" ,"d6mg" ,"d6rg", "d6yg", "d6gg", "d6cg", "d6bg" }
  , { "g9ma", "g9pk", "g9rd", "g9or", "g9yo", "g9ye", "g9yg", "g9gn", "g9sg", "g9cy", "g9bl", "g9vi", "d7g2" ,"d7mg" ,"d7rg", "d7yg", "d7gg", "d7cg", "d7bg" }
  , { "d4ma", "d4pk", "d4rd", "d4or", "d4yo", "d4ye", "d4yg", "d4gn", "d4sg", "d4cy", "d4bl", "d4vi", "d8g2" ,"d8mg" ,"d8rg", "d8yg", "d8gg", "d8cg", "d8bg" }
  , { "d6ma", "d6pk", "d6rd", "d6or", "d6yo", "d6ye", "d6yg", "d6gn", "d6sg", "d6cy", "d6bl", "d6vi"} //, "p1w2" ,"p1mw" ,"p1rw", "whye", "p1gw", "p1cw", "whbl"}
  };
  
  
  
  

  
    
  Map<String, GralColor> idxColorsByShortname = new TreeMap<String, GralColor>();
  
  
  //GralTextField[][] wdgColorBack = new GralTextField[19][10];

  //String[][] shortname = new String[19][10];
  
  //String[][] name = new String[19][10];
  
  ColorWithField[][] colorF;
  
  
  //final GralColor[][] colorV = new GralColor[lightSat.length][colHue.length + 7];
  final GralColor[][] colorV = new GralColor[lightSat.length][];
  
  ColorWithField colorFocus, colorFocus2;
  
  /**Shows in the right field, else in left. */
  boolean bRightShow;
  
  /**Set for 1 time if the show field is switched till foucus. */
  boolean bNewSwitchShow;
  
  GralTextField wdgTest, wdgTest1, wdgTest2;
  
  GralTextField wdgHexValue, wdgHue, wdgSat, wdgBright, wdgHue2, wdgSat2, wdgLight2, wdgShortname, wdgName;
  
  boolean testText, testLine;
  
  GralColor colWh = GralColor.getColor("wh");  
  GralColor colBk = GralColor.getColor("bk");
  //GralFont fontText = GralFont.fontMonospacedSansSerif;
  
  
  /**If not null, edit mode, write possible. */
  File pathConfig;
  
  


  
  int HSBtoRGB(float hue, float saturation, float brightness) {
    float hue1 = (hue-4) / 24;
    if(hue1 < 0 ){ hue1 += 1.0f; }
    return java.awt.Color.HSBtoRGB(hue1, saturation, brightness);
  }
  
  void RGBtoHSB(int col2, float[] hsb){
    java.awt.Color.RGBtoHSB((col2>>16) & 0xff, (col2>>8) & 0xff, (col2) & 0xff, hsb);
    hsb[0] = hsb[0] * 24 +4;
    if(hsb[0] >= 24.0f){ hsb[0] -= 24.0f; }
  }
  
  
  ColorWithField createColorField(int ixSatB, int ixHue, GralColor colorV1 ){
    ColorWithField colorF1 = new ColorWithField(colorV1, colorV1.name, ixHue, ixSatB, colorV1.rgb());
    colorF[ixSatB][ixHue] = colorF1;
    setColorHSB_HLSvalues(colorF1);
    setColorT(colorF1);
    return colorF1;
  }
  
  void genColorFrame() { 
    int colorVal;
    for(int ixSatB = 0; ixSatB < lightSat.length; ++ixSatB){
      int len = colHue.length;
      if(ixSatB < gray1Sat.length || ixSatB >= gray1Sat.length+1 && ixSatB < gray1Sat.length+1 + gray1Sat.length){
        len += 7;
      }
      colorV[ixSatB] = new GralColor[len];
      for(int ixHue = 0; ixHue < colHue.length; ++ixHue){
        String shname = lightSat[ixSatB].lName + colHue[ixHue].colorName;
        GralColor colorV1 = new GralColor(shname, 0xfefefe);
        colorV[ixSatB][ixHue] = colorV1;
        idxColorsByShortname.put(shname, colorV1);
      }
    }
    for(int ixGrayTable = 0; ixGrayTable < 2; ++ixGrayTable){
      //float[][] clinesat = ixsat ==0 ? clineSatC1 : clineSatC2;
      for(int ixHue=0; ixHue < 7; ++ixHue){
        for(int ixSatB = 0; ixSatB < gray1Sat.length; ++ixSatB){
          final String shname = gray1Sat[ixSatB].lName + namesGray[ixGrayTable][ixHue];
          int ixline = ixSatB + gray1Sat.length * ixGrayTable + ixGrayTable;
          GralColor colorV1 = new GralColor(shname, 0xffffff);
          colorV[ixline][colHue.length + ixHue] = colorV1;
          idxColorsByShortname.put(shname, colorV1);
        }
      }
    }
  }
  
  
  
  
  /**
   * 
   */
  void genDefaultConfig() {
    int colorVal;
    for(int ixSatB = 0; ixSatB < lightSat.length; ++ixSatB){
      for(int ixHue = 0; ixHue < colHue.length; ++ixHue){
      //StringBuilder line = new StringBuilder(1000);
        float b = lightSat[ixSatB].light;
        float s = lightSat[ixSatB].sat; // * colHue[ixHue][1];
        //if(b > 1.0f){ s -= b -1.0f; b = 1.0f; }
        //colorVal = HSBtoRGB(colHue[ixHue][0], s, b) & 0xffffff;
        if(ixHue == 3 && ixSatB == 1)
          Debugutil.stop();
        GralColor color = colorV[ixSatB][ixHue];
        if(color.name.equals("l3rd"))   ////
          Debugutil.stop();
        colorVal = GralColorConv.HLStoRGB(colHue[ixHue].colorValue, b, s) & 0xffffff;
        //String shname = lightSat[ixSatB].lName + colHue[ixHue].colorName;
        _GralChgColor.setColorValue(color, colorVal);
        _GralChgColor.setColorUsualNames(color, null);
      }
    }
    for(int ixGrayTable = 0; ixGrayTable < 2; ++ixGrayTable){
      for(int ixHue=0; ixHue < 7; ++ixHue){
        for(int ixSatB = 0; ixSatB < gray1Sat.length; ++ixSatB){
          if(ixGrayTable == 0 && ixHue == 1 && ixSatB == 5)
            Debugutil.stop();
          int ixline = ixSatB + gray1Sat.length * ixGrayTable + ixGrayTable;
          float color, sat; 
          float light = gray1Sat[ixSatB].light; //(1.0f - ((float)ixSatB) / lightSat.length * 2);
          if(ixHue == 0) {
            color = 4; 
            sat = 0;
          } else {
            color = 4*(ixHue-1);
            if(ixHue == 5 && ixSatB == 10)
              Debugutil.stop();
            //sat = CurveInterpolation.linearInterpolation(light, color, clinesat, -1);
            if(ixGrayTable == 0) {
              sat = gray1Sat[ixSatB].sat;  //0.06f + 0.4f * (1-light);// * (1-light);
            } else {
              sat = gray1Sat[ixSatB].sat2;  // 0.02f + 0.25f * (1-light);// * (1-light);
            }
         }
          colorVal = GralColorConv.HLStoRGB(color, light, sat) & 0xffffff;
          _GralChgColor.setColorValue(colorV[ixline][colHue.length + ixHue], colorVal);
          _GralChgColor.setColorUsualNames(colorV[ixline][colHue.length + ixHue], null);
        }
      }
    }
  }
  
  
  
  
  
  void readConfig() throws FileNotFoundException, IOException, ParseException
  { 
    InputStream ins = ClassLoader.getSystemClassLoader().getResourceAsStream("org/vishia/gral/colordef.txt");
    //BufferedReader in = new BufferedReader(new InputStreamReader(ins));
    if(ins == null) return;
    StringPartFromFileLines spline = new StringPartFromFileLines(ins, "org/vishia/gral/colordef.txt", 100000, null, null);
    ins.close();
    spline.setIgnoreWhitespaces(true);
    if(spline.scan("@").scanOk()){
      //it is redirected to a file:
      spline.len0end();
      String path = spline.getCurrentPart().toString().trim();
      spline.close();
      File fileConfig = new File(path);
      if(fileConfig.exists()){
        this.pathConfig = fileConfig;
      } else {
        throw new FileNotFoundException("edit mode failed, " + fileConfig.getAbsolutePath());
      }
      spline = new StringPartFromFileLines(fileConfig);
    }
    //String[] val = valTest;
    //String[] val = valOk;
    
    //int zLine = Math.min(val.length, lightSat.length);
    //int ixBright = -1;
    boolean cont = true;
    do {
      spline.setIgnoreWhitespaces(true);  //for the next line.
      if(spline.scan("colorhex").scan("=").scanOk()){}
      else if(spline.scan("color").scan("=").scanOk()){
        boolean ok;
        String shname = null;
        int cvalue;
        GralColor colorV1 = null;
        ok = spline.scanIdentifier().scanOk();
        if(ok){
          shname = spline.getLastScannedString().toString();
          ok = spline.scan(":").scan("#").scanHex(6).scanOk();
        }
        if(ok) {
          cvalue = (int)spline.getLastScannedIntegerNumber();
          colorV1 = idxColorsByShortname.get(shname);
          if(colorV1 == null) {
            System.err.println("GralColor: faulty colorname = " + shname);
          } else {
            if(cvalue >=0){
              _GralChgColor.setColorValue(colorV1, cvalue);
            }
          }
        }
        int check = 0;
        while(ok && spline.scan(",").scanIdentifier(null, "-").scanOk()){
          String name = spline.getLastScannedString().toString();
          if(++check == 1){
            _GralChgColor.setColorUsualNames(colorV1, name);
          }
        }
      }
      else if( spline.scanHex(6).scanOk()) {
        //for(int ixCol=0; ixCol<val.length; ++ixCol) {
        GralColor colText;
        //if(ixBright < 11){ colText = colBk; }
        //else { colText = colWh; }
        int col2;
        spline.setIgnoreWhitespaces(true);
        spline.scanOk();
        try{ 
          //spline.scanHex(6); 
          col2 = (int) spline.getLastScannedIntegerNumber();
        } catch(ParseException exc){ col2 = 0xffffff; }
        GralColor col3 = GralColor.getColor(col2);
        spline.setIgnoreWhitespaces(false);
        spline.scanSkipSpace().scanOk();
        final String shortname;
        if(spline.scan(":").scanIdentifier().scanOk()){
          shortname = spline.getLastScannedString().toString();   
        } else {
          spline.scan(":").scanOk();  //read ':'
          shortname = "";
        }
        boolean marked = spline.scan("+").scanOk();
        GralColor colorV1 = idxColorsByShortname.get(shortname);
        if(colorV1 !=null) {
          if(marked){
            //marked.
            _GralChgColor.setColorValue(colorV1,col2);
          }
        } else {
          System.err.println("GralColor - color not found, " + shortname);
        }
        
      }
      else {
        cont = false;
      }
    } while(cont);
    spline.close();
  }
  
 
  
  void refreshColorFields()
  {
    for(int ixLine = 0; ixLine < colorF.length; ++ixLine){
      for(int col = 0; col < colorF[ixLine].length; ++col){
        ColorWithField field = colorF[ixLine][col];
        field.wdgColor.setBackColor(colorF[ixLine][col].co,0);
        String text = "";
        if(field.co.usualNames() !=null && field.co.usualNames().length() >0) {
          text = "-";
        }
        field.wdgColor.setText(text);
      }
    }
  }
  
  
  
  void outColors(){
    if(pathConfig ==null) return;
    try{ 
      Writer out = new FileWriter(pathConfig);
      for(int line=9990; line < colorF.length; ++line) {
        String spaces = "            ";
        out.append("colorhex = ");
        for(int col = 0; col < colorF[line].length; ++col){
          ColorWithField colorF1 = colorF[line][col];
          //String sVal = colorF1.wdgColor.getText();
          GralColor color = colorF1.wdgColor.getBackColor(0);
          int colValue = color.getColorValue();
          String sHex = String.format("%06X", colValue);
          out.append(sHex).append(':').append(colorF1.co.name);
          if(colorF1.co.usualNames() !=null && colorF1.co.usualNames().trim().length()>0){
            out.append('+');
          } else {
            out.append(' ');
          }
          int zspaces = 5 - colorF1.co.name.length();
          if(zspaces < 1){ zspaces = 1; }
          out.append(spaces.substring(0, zspaces));
        }
        out.append("\"\n");
      }
      out.append("\n\n");
    
      for(int line=0; line<colorF.length; ++line) {
        for(int col = 0; col < colorF[line].length; ++col){
          String usualNames = colorF[line][col].co.usualNames();
          if(usualNames !=null && usualNames.length()>0) {
            GralColor color = colorF[line][col].wdgColor.getBackColor(0);
            int colValue = color.getColorValue();
            String sHex = String.format("%06X", colValue);
            out.append("color = ")
            .append(colorF[line][col].co.name)
            .append(": #").append(sHex)
            .append(", ")
            .append(usualNames)
            .append("\n");
          }
        }
      }
      out.close();
    } catch(IOException exc){
      System.err.println(exc.getMessage());
    }
  }


  void setColorHSB_HLSvalues(ColorWithField colorF1){
    int col2 = colorF1.co.rgb();
    RGBtoHSB(col2, colorF1.hsb);
    GralColorConv.RGBtoHLS(col2, colorF1.hls);
    //colorF1.color = GralColor.getColor(colorF1.rgb);
    //colorF1.wdgColor.setBackColor(colorF1.color, 0);
  }
        
  
  void processHSBinput() {
    ColorWithField colorF2 = bRightShow ? colorFocus2 : colorFocus;
    int rgb = colorF2.co.rgb();
    int rgb2 = HSBtoRGB(colorF2.hsb[0], colorF2.hsb[1], colorF2.hsb[2]) & 0xffffff;
    if(rgb != rgb2){
      _GralChgColor.setColorValue(colorF2.co, rgb2);
      setColorHSB_HLSvalues(colorF2);
      setColorT(colorF2);
      setColorEditFields(bRightShow ? 2 : 1);
    }
  }

  
  /**Sets the color of the cell with the given HSB values, maybe changed in the cell.
   * @param colorF1 The cell.
   */
  void setColorFromHSB(ColorWithField colorF1) {
    _GralChgColor.setColorValue(colorF1.co, HSBtoRGB(colorF1.hsb[0], colorF1.hsb[1], colorF1.hsb[2]) & 0xffffff);
    GralColorConv.RGBtoHLS(colorF1.co.rgb(), colorF1.hls);
    /*    String sHex = String.format("%06X", colorF1.rgb);
    wdgHexValue.setText(sHex);
    colorF1.color = GralColor.getColor(colorF1.rgb);
    colorF1.wdgColor.setBackColor(colorF1.color, 0);
*/  }


  void setColorFromHSL(ColorWithField colorF1) {
    _GralChgColor.setColorValue(colorF1.co, GralColorConv.HLStoRGB(colorF1.hls[0], colorF1.hls[1], colorF1.hls[1]) & 0xffffff);
    RGBtoHSB(colorF1.co.rgb(), colorF1.hsb);
    /*   String sHex = String.format("%06X", colorF1.rgb);
    wdgHexValue.setText(sHex);
    colorF1.color = GralColor.getColor(colorF1.rgb);
    colorF1.wdgColor.setBackColor(colorF1.color, 0);
 */ }

  
  
  void setColorEditFields(int nr) {
    ColorWithField colorF1 = nr == 2 ? colorFocus2 : colorFocus;
    String sHex = String.format("%06X", colorF1.co.rgb());
    wdgHexValue.setText(sHex); //Integer.toHexString(colValue));
    wdgShortname.setText(colorF1.co.name);
    String usualNames = colorF1.co.usualNames();
    if(usualNames ==null) { usualNames = ""; }
    wdgName.setText(usualNames);
    if(colorF1.co.name.length()>0){
      //colorF1.wdgColor.setText(colorF1.name);
    }
    float hue1 = colorF1.hsb[0]; //24 * colorF1.hsb[0] +4;
    //if(hue1 >= 24.0f){ hue1 -= 24; }
    wdgHue.setText(""+hue1);
    wdgSat.setText(""+colorF1.hsb[1]);
    wdgBright.setText(""+colorF1.hsb[2]);
    float col2 = 24 * colorF1.hls[0] +4;
    if(col2 >=24.0f) col2 -= 24.0f;
    //wdgHue2.setText(""+colorF1.hls[0]);
    
    
    //int l2 = (int)((colorF1.co.red + colorF1.co.green / 1.6f + colorF1.co.blue * 1.0f) /3);
    int l2 = (int)((colorF1.co.red + colorF1.co.green * 1.5f + colorF1.co.blue / 1.2f) /3);
    String sl2 = String.format("%02X", l2);
    wdgHue2.setText(sl2);
    
    wdgSat2.setText(""+colorF1.hls[2]);
    wdgLight2.setText(""+colorF1.hls[1]);
    GralColor color = GralColor.getColor(colorF1.co.rgb());
    if(nr == 1){ wdgTest = wdgTest1; }
    else if(nr == 2){ wdgTest = wdgTest2; }
    if(testText) {
      wdgTest.setTextColor(color);
      wdgTest.setLineColor(color, 0);
    } else {
      wdgTest.setBackColor(color,0);
    }
        
  }      

  
  void setFocusColor(ColorWithField field)
  { colorFocus.wdgColor.setBorderWidth(0);
    colorFocus = field; 
    colorFocus.wdgColor.setBorderWidth(3);
    setColorHSB_HLSvalues(colorFocus);
    setColorEditFields(1);
 
  }
  
  
  void setFocusColor2(ColorWithField field)
  { colorFocus2.wdgColor.setBorderWidth(0);
    colorFocus2 = field; 
    colorFocus2.wdgColor.setBorderWidth(3);
    setColorHSB_HLSvalues(colorFocus2);
    setColorEditFields(2);
 
  }
  
  
  
  void setColorT(ColorWithField colorF1) {
    //colorF1.co.color = GralColor.getColor(colorF1.co.rgb());
    
    colorF1.wdgColor.setBackColor(colorF[0][0].co, 0);
    colorF1.wdgColor.setBackColor(colorF1.co, 0);
    String usualNames = colorF1.co.usualNames();
    if(usualNames !=null && usualNames.length()>0) {
      colorF1.wdgColor.setText("x");
    } else {
      colorF1.wdgColor.setText("");
    }
  }

  GralGraphicTimeOrder initGraphicFullColors = new GralGraphicTimeOrder("GralArea9Window.initGraphic"){
    @Override public void executeOrder()
    {
      //gralMng.addTextField();
      //colorF = new ColorWithField[lightSat.length][colHue.length + 7];
      colorF = new ColorWithField[lightSat.length][];
      ColorWithField colorF1 = null;
      for(int ixRow = 0; ixRow < lightSat.length; ++ixRow){ //cells in columns, vertical
        LightSat ls = lightSat[ixRow];
        String text = ls.lName; //String.format("%s %1.1f,%1.1f", ls.lName, ls.light, ls.sat);
        gralMng.setPosition(6+3*ixRow, GralPos.size -2, 0, GralPos.size+2, 0, 'd', 0);
        GralLabel label = new GralLabel(null, text,0);
        label.createImplWidget_Gthread();
      }
      for(int ixCol=0; ixCol<colHue.length; ++ixCol) { //the column, for horizontal
        String nameShow;
        nameShow = colHue[ixCol].colorName;
        gralMng.setPosition(2.5f, GralPos.size -2, 4*ixCol +3, GralPos.size+4, 0, 'd', 0);
        GralLabel label = new GralLabel(null, nameShow,0);
        label.createImplWidget_Gthread();
      }
      gralMng.setPosition(2.5f, GralPos.size -2, 4 + 4 * colHue.length, GralPos.size+4, 0, 'r', 0);
      for(int ixCol=0; ixCol<namesGray[0].length; ++ixCol) { //the column, for horizontal
        String nameShow;
        nameShow = namesGray[0][ixCol];
        GralLabel label = new GralLabel(null, nameShow,0);
        label.createImplWidget_Gthread();
      }
      gralMng.setPosition(5.5f + 3* gray1Sat.length , GralPos.size -2, 4 + 4 * colHue.length, GralPos.size+4, 0, 'r', 0);
      for(int ixCol=0; ixCol<namesGray[1].length; ++ixCol) { //the column, for horizontal
        String nameShow;
        nameShow = namesGray[1][ixCol];
        GralLabel label = new GralLabel(null, nameShow,0);
        label.createImplWidget_Gthread();
      }
      for(int ixRow = 0; ixRow < colorF.length; ++ixRow){ //cells in columns, vertical
        
        colorF[ixRow] = new ColorWithField[colorV[ixRow].length];
        gralMng.setPosition(6 + 3 * ixRow, GralPos.size -3, 2, GralPos.size+4, 0, 'r', 0);
        //gralMng.setPosition(3, GralPos.size -2, 9*col, GralPos.size+8, 0, 'd', 1);
        //out.append(", \"");
        //int pline = 0;
        for(int ixCol=0; ixCol < colorF[ixRow].length; ++ixCol) { //the column, for horizontal
          
          GralColor colText;
          if(ixRow < 11){ colText = colBk; }
          else { colText = colWh; }
          GralColor colorV2 = colorV[ixRow][ixCol];
          if(colorV2 !=null) {
            colorF1 = createColorField(ixRow, ixCol, colorV2);
            //colorF1 = colorF[ixRow][ixCol];
            if(colorF1 !=null) {
              colorF1.wdgColor.createImplWidget_Gthread();
              colorF1.bActive = true;
              colorF1.wdgColor.setTextColor(colText);
              if(colorF1.co.usualNames() !=null && colorF1.co.usualNames().length() >0) {
                colorF1.wdgColor.setText("x");
              }
            }
          }
        }
      }
      colorFocus = colorF[0][0];
      colorFocus2 = colorF[0][1];
      
      gralMng.setPosition(4 + 3 * colorV.length /*GralPos.refer+4*/, GralPos.size +10, 1, GralPos.size+15, 0, 'r');
      //
      wdgTest1 = new GralTextField("test1");
      wdgTest2 = new GralTextField("test2");
      wdgTest1.setEditable(true);
      wdgTest2.setEditable(true);
      wdgTest1.setActionFocused(actionFocusedTest);
      wdgTest2.setActionFocused(actionFocusedTest);
      wdgTest1.setToPanel(gralMng);
      wdgTest2.setToPanel(gralMng);
      wdgTest1.setText("ABC");
      wdgTest2.setText("XYZ");
      wdgTest = wdgTest1;
      
      gralMng.setPosition(GralPos.refer, GralPos.size +2, 40, GralPos.size+10, 0, 'r');
      wdgHexValue = new GralTextField("hex");
      wdgHexValue.setEditable(true);
      wdgHexValue.setActionChange(actionEnterHex);
      wdgHexValue.setToPanel(gralMng);
      wdgShortname = new GralTextField("sname");
      wdgShortname.setEditable(true);
      wdgShortname.setActionChange(actionEnterShortname);
      wdgShortname.setToPanel(gralMng);
      gralMng.setPosition(GralPos.refer +3, GralPos.size +2, 40, GralPos.size+20, 0, 'r');
      wdgName = new GralTextField("name");
      wdgName.setEditable(true);
      wdgName.setActionChange(actionEnterName);
      wdgName.setToPanel(gralMng);
      gralMng.setPosition(GralPos.refer +3, GralPos.size +2, 40, GralPos.size+12, 0, 'r',1);
      wdgHue = new GralTextField("name");
      wdgHue.setEditable(true);
      wdgHue.setActionChange(new ActionEnterHSB(0));
      wdgHue.setToPanel(gralMng);
      wdgBright = new GralTextField("name");
      wdgBright.setEditable(true);
      wdgBright.setActionChange(new ActionEnterHSB(2));
      wdgBright.setToPanel(gralMng);
      wdgSat = new GralTextField("name");
      wdgSat.setEditable(true);
      wdgSat.setActionChange(new ActionEnterHSB(1));
      wdgSat.setToPanel(gralMng);
      gralMng.setPosition(GralPos.refer +3, GralPos.size +2, 40, GralPos.size+12, 0, 'r',1);
      wdgHue2 = new GralTextField("name");
      wdgHue2.setEditable(true);
      wdgHue2.setActionChange(new ActionEnterHLS(0));
      wdgHue2.setToPanel(gralMng);
      wdgLight2 = new GralTextField("name");
      wdgLight2.setEditable(true);
      wdgLight2.setActionChange(new ActionEnterHLS(1));
      wdgLight2.setToPanel(gralMng);
      wdgSat2 = new GralTextField("name");
      wdgSat2.setEditable(true);
      wdgSat2.setActionChange(new ActionEnterHLS(2));
      wdgSat2.setToPanel(gralMng);
   } };
  
  
  
  GralGraphicTimeOrder initGraphicLessColors = new GralGraphicTimeOrder("initGraphicLessColors"){
    @Override public void executeOrder()
    {
      String[][] colors = colors4;
      colorF = new ColorWithField[colors.length][];
      gralMng.setPosition(4, -2, 2, -2, 0, 'd');
      for(int ixRow = 0; ixRow < colors.length; ++ixRow){ //fill cells of columns.
        colorF[ixRow] = new ColorWithField[colors[ixRow].length];
        gralMng.setPosition(4 + 3*ixRow, GralPos.size -3, 1, GralPos.size+4, 0, 'r', 0);
        //gralMng.setPosition(3, GralPos.size -2, 9*col, GralPos.size+8, 0, 'd', 1);
        ColorWithField colorF1 = null;
        for(int ixCol=0; ixCol<colors[ixRow].length; ++ixCol) { //create columns
          GralColor colText;
          String shname = colors[ixRow][ixCol];
          GralColor colorV1 = idxColorsByShortname.get(shname);
          if(colorV1 == null) { colorV1 = colorV[0][0]; }
          colorF1 = createColorField(ixRow, ixCol, colorV1);
          //colorF1 = colorF[ixRow][ixCol];
          if(colorF1 !=null) {
            colorF1.wdgColor.createImplWidget_Gthread();
            colorF1.bActive = true;
            if(colorF1.co.usualNames() !=null && colorF1.co.usualNames().length() >0) {
              colorF1.wdgColor.setText("x");
            }
          }
        }
      }
      colorFocus = colorF[0][0];
      colorFocus2 = colorF[0][1];
      
      gralMng.setPosition(GralPos.refer+4, GralPos.size +10, 1, GralPos.size+15, 0, 'r');
      //
      wdgTest1 = new GralTextField("test1");
      wdgTest2 = new GralTextField("test2");
      wdgTest1.setEditable(true);
      wdgTest2.setEditable(true);
      wdgTest1.setActionFocused(actionFocusedTest);
      wdgTest2.setActionFocused(actionFocusedTest);
      wdgTest1.setToPanel(gralMng);
      wdgTest2.setToPanel(gralMng);
      wdgTest1.setText("ABC");
      wdgTest2.setText("XYZ");
      wdgTest = wdgTest1;
      
      gralMng.setPosition(GralPos.refer, GralPos.size +2, 40, GralPos.size+10, 0, 'r');
      wdgHexValue = new GralTextField("hex");
      wdgHexValue.setEditable(true);
      wdgHexValue.setActionChange(actionEnterHex);
      wdgHexValue.setToPanel(gralMng);
      wdgShortname = new GralTextField("sname");
      wdgShortname.setEditable(true);
      wdgShortname.setActionChange(actionEnterShortname);
      wdgShortname.setToPanel(gralMng);
      gralMng.setPosition(GralPos.refer +3, GralPos.size +2, 40, GralPos.size+20, 0, 'r');
      wdgName = new GralTextField("name");
      wdgName.setEditable(true);
      wdgName.setActionChange(actionEnterName);
      wdgName.setToPanel(gralMng);
      gralMng.setPosition(GralPos.refer +3, GralPos.size +2, 40, GralPos.size+12, 0, 'r',1);
      wdgHue = new GralTextField("name");
      wdgHue.setEditable(true);
      wdgHue.setActionChange(new ActionEnterHSB(0));
      wdgHue.setToPanel(gralMng);
      wdgBright = new GralTextField("name");
      wdgBright.setEditable(true);
      wdgBright.setActionChange(new ActionEnterHSB(2));
      wdgBright.setToPanel(gralMng);
      wdgSat = new GralTextField("name");
      wdgSat.setEditable(true);
      wdgSat.setActionChange(new ActionEnterHSB(1));
      wdgSat.setToPanel(gralMng);
      gralMng.setPosition(GralPos.refer +3, GralPos.size +2, 40, GralPos.size+12, 0, 'r',1);
      wdgHue2 = new GralTextField("name");
      wdgHue2.setEditable(true);
      wdgHue2.setActionChange(new ActionEnterHLS(0));
      wdgHue2.setToPanel(gralMng);
      wdgLight2 = new GralTextField("name");
      wdgLight2.setEditable(true);
      wdgLight2.setActionChange(new ActionEnterHLS(1));
      wdgLight2.setToPanel(gralMng);
      wdgSat2 = new GralTextField("name");
      wdgSat2.setEditable(true);
      wdgSat2.setActionChange(new ActionEnterHLS(2));
      wdgSat2.setToPanel(gralMng);
   } };
  
  
  
  GralUserAction XXXactionFocusColor = new GralUserAction("focus color"){
    @Override public boolean exec(int key, GralWidget_ifc widgP, Object... params)
    {
      if(key == (KeyCode.focusGained)){
        GralTextField wdg = (GralTextField)widgP;
        if(colorFocus !=null) {
          //if(colorFocus != colorFocus1 || colorFocus != colorFocus2) {
          if(bNewSwitchShow){
            bNewSwitchShow = false; 
          } else { 
            colorFocus.wdgColor.setBorderWidth(0);
            colorFocus = (ColorWithField)widgP.getData();
            colorFocus.wdgColor.setBorderWidth(3);
            setColorHSB_HLSvalues(colorFocus);
            setColorEditFields(0);
          }
          System.out.println("focus");
        }
      }
      return true;
  } };
  
  
  
  GralUserAction XXXactionMouseColor = new GralUserAction("MouseColor"){
    @Override public boolean exec(int key, GralWidget_ifc widgP, Object... params)
    {
      if(key == (KeyCode.mouse2Up)){
        GralTextField wdg = (GralTextField)widgP;
        colorFocus.wdgColor.setBorderWidth(0);
        colorFocus = (ColorWithField)widgP.getData();
        colorFocus.wdgColor.setBorderWidth(3);
        //int colValue = color.getColorValue();
        setColorHSB_HLSvalues(colorFocus);
        setColorEditFields(0);
      }
      return true;
  } };
  
  
  
  GralUserAction actionEditColor = new GralUserAction("edit color"){
    @Override public boolean exec(int key, GralWidget_ifc widgP, Object... params)
    {
      ColorWithField colorFs = bRightShow ? colorFocus2 : colorFocus;
    
      GralTextField wdg = (GralTextField)widgP;
      if(key == KeyCode.right) {
        int ixRow = colorFocus.index.ixLight;
        int ixCol = colorFocus.index.ixCol;
        int ctw = 100;
        do { if(++ixCol >= colorF[ixRow].length){ ixCol = 0;}
        } while(--ctw >=0 && !colorF[ixRow][ixCol].bActive);
        ColorWithField newField = colorF[ixRow][ixCol];
        newField.wdgColor.setFocus();
        setFocusColor(newField);
      } else if(key == KeyCode.shift + KeyCode.right) {
        int ixRow = colorFocus2.index.ixLight;
        int ixCol = colorFocus2.index.ixCol;
        int ctw = 100;
        do { if(++ixCol >= colorF[0].length){ ixCol = 0;}
        } while(--ctw >=0 && !colorF[ixRow][ixCol].bActive);
        if(ixCol >= colorF[0].length) { ixCol = 0; } //first is active.
        ColorWithField newField = colorF[ixRow][ixCol];
        newField.wdgColor.setFocus();
        setFocusColor2(newField);
      } else if(key == KeyCode.left){ 
        int ixRow = colorFocus.index.ixLight;
        int ixCol = colorFocus.index.ixCol;
        int ctw = 100;
        if(--ixCol <0){  ixCol = colorF[ixRow].length -1; }
        //} while(--ctw >= 0 && !colorF[ixRow][ixCol].bActive);
        ColorWithField newField = colorF[ixRow][ixCol];
        newField.wdgColor.setFocus();
        setFocusColor(newField);
      } else if(key == KeyCode.shift + KeyCode.left){ 
        int ixRow = colorFocus2.index.ixLight;
        int ixCol = colorFocus2.index.ixCol;
        int ctw = 100;
        do { if(--ixCol <0){  ixCol = colorF[0].length -1; }
        } while(--ctw >= 0 && !colorF[ixRow][ixCol].bActive);
        ColorWithField newField = colorF[ixRow][ixCol];
        newField.wdgColor.setFocus();
        setFocusColor2(newField);
      } else if(key == KeyCode.up && colorFocus.index.ixLight > 0){ 
        int ixRow = colorFocus.index.ixLight;
        int ixCol = colorFocus.index.ixCol;
        ixRow -=1;
        if(ixCol >= colorF[ixRow].length) { ixRow -=1; } //skip over title on gray table
        ColorWithField newField = colorF[ixRow][ixCol];
        newField.wdgColor.setFocus();
        setFocusColor(newField);
      } else if(key == KeyCode.shift + KeyCode.up && colorFocus2.index.ixLight > 0){ 
        int ixRow = colorFocus2.index.ixLight;
        int ixCol = colorFocus2.index.ixCol;
        int ctw = 100;
        do { if(--ixRow <0){  ixRow = colorF.length -1; }
        } while(--ctw >= 0 && !colorF[ixRow][ixCol].bActive);
        ColorWithField newField = colorF[ixRow][ixCol];
        newField.wdgColor.setFocus();
        setFocusColor2(newField);
      } else if(key == KeyCode.dn && colorFocus.index.ixLight < colorF.length -1){ 
        int ixRow = colorFocus.index.ixLight;
        int ixCol = colorFocus.index.ixCol;
        ixRow +=1;
        while(ixRow < colorF.length && ixCol >= colorF[ixRow].length) { ixRow +=1; } //skip over title on gray table
        if(ixRow < colorF.length){ 
          ColorWithField newField = colorF[ixRow][ixCol];
          newField.wdgColor.setFocus();
          setFocusColor(newField);
        }
      } else if(key == KeyCode.shift + KeyCode.dn && colorFocus2.index.ixLight < colorF.length -1){ 
        int ixRow = colorFocus2.index.ixLight;
        int ixCol = colorFocus2.index.ixCol;
        int ctw = 100;
        do { if(++ixRow >=colorF.length){  ixRow = 0; }
        } while(--ctw >= 0 && !colorF[ixRow][ixCol].bActive);
        if(ctw >= 0){
          ColorWithField newField = colorF[ixRow][ixCol];
          newField.wdgColor.setFocus();
          setFocusColor2(newField);
        }
      } else if(key == KeyCode.mouse2Down) {
        ColorWithField colorF2 = (ColorWithField)widgP.getData();
        _GralChgColor.setColorValue(colorF2.co, colorFocus.co.rgb());  //copy from last focused.
        colorF2.wdgColor.setBackColor(colorFocus.wdgColor.getBackColor(0),0);
        setColorHSB_HLSvalues(colorF2);
        //setFocusColor(wdg);
      } else if(key == KeyCode.mouse1Down) {
        ColorWithField colorF2 = (ColorWithField)widgP.getData();
        setColorHSB_HLSvalues(colorF2);
        setColorT(colorFocus);
        setColorEditFields(1);
        bNewSwitchShow = true;
        System.out.println("mouse down");
        bRightShow = false;
        setFocusColor(colorF2);
      } else if(key == KeyCode.mouse1Down + KeyCode.shift) {
        ColorWithField colorF2 = (ColorWithField)widgP.getData();
        setColorHSB_HLSvalues(colorF2);
        setColorT(colorFocus);
        bNewSwitchShow = true;
        setColorEditFields(2);
        System.out.println("mouse sh down");
        bRightShow = true;
        setFocusColor2(colorF2);
      } else if(key == KeyCode.mouse1Down + KeyCode.shift + KeyCode.ctrl) {
        ColorWithField colorF2 = (ColorWithField)widgP.getData();
        int rgb2 = colorF2.co.rgb();
        String usualNames2 = colorF2.co.usualNames();
        String usualNames = colorFocus.co.usualNames();
        int rgb = colorFocus.co.rgb();
        _GralChgColor.setColorValue(colorF2.co, rgb);
        _GralChgColor.setColorUsualNames(colorF2.co, usualNames);
        _GralChgColor.setColorValue(colorFocus.co, rgb2);
        _GralChgColor.setColorUsualNames(colorFocus.co, usualNames2);
        setColorHSB_HLSvalues(colorF2);
        setColorT(colorF2);
        setColorHSB_HLSvalues(colorFocus);
        setColorT(colorFocus);
        bNewSwitchShow = true;
        setColorEditFields(2);
        bRightShow = true;
        setFocusColor2(colorF2);
      } else if(key == 'R'){
        int rd = (colorFocus.co.rgb() >>16) & 0xff;
        if(rd <255) rd +=1;
        _GralChgColor.setColorValue(colorFocus.co, (colorFocus.co.rgb() & 0x00ffff) | rd <<16);
        setColorHSB_HLSvalues(colorFocus);
        setColorT(colorFocus);
        setColorEditFields(0);
      } else if(key == ('r')){
        int rd = (colorFocus.co.rgb() >>16) & 0xff;
        if(rd >0) rd -=1;
        _GralChgColor.setColorValue(colorFocus.co, (colorFocus.co.rgb() & 0x00ffff) | rd <<16);
        setColorHSB_HLSvalues(colorFocus);
        setColorT(colorFocus);
        setColorEditFields(0);
      } else if(key == ('G')){
        int gn = (colorFocus.co.rgb() >>8) & 0xff;
        if(gn <255) gn +=1;
        _GralChgColor.setColorValue(colorFocus.co, (colorFocus.co.rgb() & 0xff00ff) | gn <<8);
        setColorHSB_HLSvalues(colorFocus);
        setColorT(colorFocus);
        setColorEditFields(0);
      } else if(key == ('g')){
        int gn = (colorFocus.co.rgb() >>8) & 0xff;
        if(gn >0) gn -=1;
        _GralChgColor.setColorValue(colorFocus.co, (colorFocus.co.rgb() & 0xff00ff) | gn <<8);
        setColorHSB_HLSvalues(colorFocus);
        setColorT(colorFocus);
        setColorEditFields(0);
      } else if(key == ('B')){  //key of a notebook: 
        int bl = (colorFocus.co.rgb()) & 0xff;
        if(bl <255) bl +=1;
        _GralChgColor.setColorValue(colorFocus.co, (colorFocus.co.rgb() & 0xffff00) | bl);
        setColorHSB_HLSvalues(colorFocus);
        setColorT(colorFocus);
        setColorEditFields(0);
      } else if(key == ('b')){
        int bl = (colorFocus.co.rgb()) & 0xff;
        if(bl >0) bl -=1;
        _GralChgColor.setColorValue(colorFocus.co, (colorFocus.co.rgb() & 0xffff00) | bl);
        setColorHSB_HLSvalues(colorFocus);
        setColorT(colorFocus);
        setColorEditFields(0);
      } else if(key == ('H')){
        float val = colorFs.hsb[0] + 0.125f;
        if(val >= 24){ val = 0.0f; }
        colorFs.hsb[0] = val;
        processHSBinput();
        colorFs.hsb[0] = val;
      } else if(key == ('h')){
        float val = colorFs.hsb[0] - 0.125f;
        if(val <0 ){ val += 24.0f; }
        colorFs.hsb[0] = val;
        processHSBinput();
        colorFs.hsb[0] = val;
      } else if(key == ('J')){
        float light = colorFs.hsb[2] + 0.005f;
        if(light > 1){ light = 1.0f; }
        colorFs.hsb[2] = light;
        processHSBinput();
      } else if(key == ('j')){
        float light = colorFs.hsb[2] - 0.005f;
        if(light < 0){ light = 0.0f; }
        colorFs.hsb[2] = light;
        processHSBinput();
      } else if(key == ('K')){
        float val = colorFs.hsb[1] + 0.005f;
        if(val > 1){ val = 1.0f; }
        colorFs.hsb[1] = val;
        processHSBinput();
        colorFs.hsb[1] = val;
      } else if(key == ('k')){
        float val = colorFs.hsb[1] - 0.005f;
        if(val > 1){ val = 1.0f; }
        colorFs.hsb[1] = val;
        processHSBinput();
        colorFs.hsb[1] = val;
      } else if(key == KeyCode.ctrl + ('x')){
        genDefaultConfig();
        refreshColorFields();
      } else if(key == KeyCode.ctrl +('y')){
        try {
          readConfig();
        } catch (IOException e1) {
          System.err.println("GralColor - config file error, " + e1.getMessage());
        } catch(ParseException exc) {
          System.err.println("GralColor - cannot read config, " + exc.getMessage());
        }
        refreshColorFields();
      } else if(key == KeyCode.enter){
        setColorHSB_HLSvalues(colorFocus);
      } else if(key == (KeyCode.focusGained)){ //only if it is an edit field
        //actionFocusColor.exec(key, widgP);
      } else if(key == (KeyCode.enter + KeyCode.ctrl) || key == ('s' + KeyCode.ctrl) || key == ('S' + KeyCode.ctrl)){
        outColors();
      }
      return true;
    }
  };
  

  
  
  
    GralUserAction actionEnterHex = new GralUserAction("actionEnterHex"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widg, Object... params) {
      if(actionCode == KeyCode.enter || actionCode == KeyCode.focusLost){
        GralTextField widgt = (GralTextField)widg;
        String text = widgt.getText();
        try{ 
          _GralChgColor.setColorValue(colorFocus.co, Integer.parseInt(text, 16));
          setColorHSB_HLSvalues(colorFocus);
          //GralColor colorBack = GralColor.getColor(colorFocus.co.rgb());
          //colorFocus.co.color = colorBack;
          colorFocus.wdgColor.setBackColor(colorFocus.co, 0); 
        } catch(NumberFormatException exc){
          System.out.println(exc.getMessage());
        }
      }
      return true;      
  } };

  GralUserAction actionEnterShortname = new GralUserAction("actionEnterShortname"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widg, Object... params) {
      if(actionCode == KeyCode.enter || actionCode == KeyCode.focusLost){
        GralTextField widgt = (GralTextField)widg;
        String text = widgt.getText().trim();
        if(!text.equals(colorFocus.co.name)) {
          GralColor oldColor = colorFocus.co;
          colorFocus.co = new GralColor(text, oldColor.rgb());
          idxColorsByShortname.remove(oldColor.name);
          String usualNames = oldColor.usualNames();
          _GralChgColor.setColorUsualNames(colorFocus.co, usualNames);
          if(usualNames!=null && usualNames.length() >0) {
            //remove given shortname 
            //idxColorsByShortname.remove(usualNames);
          }
          
          //_GralChgColor.setColorUsualNames(colorFocus.co, text.trim());
          idxColorsByShortname.put(colorFocus.co.name, colorFocus.co);
        }
      }
      return true;      
  } };

  GralUserAction actionEnterName = new GralUserAction("actionEnterName"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widg, Object... params) {
      if(actionCode == KeyCode.enter || actionCode == KeyCode.focusLost){
        GralTextField widgt = (GralTextField)widg;
        String text = widgt.getText();
        _GralChgColor.setColorUsualNames(colorFocus.co, text.trim());
      }
      return true;      
  } };

  
  GralUserAction XXXactionEnterHue = new GralUserAction("actionEnterHue") {
    @Override public boolean exec(int actionCode, GralWidget_ifc widg, Object... params) {
      if(actionCode == KeyCode.enter || actionCode == KeyCode.focusLost){
        GralTextField widgt = (GralTextField)widg;
        String text = widgt.getText();
        float hue;
        try{
          hue = Float.parseFloat(text.trim());
          if(hue > 1.0f){ hue = 1.0f; }
          if(hue < 0.0f){ hue = 0.0f; }
        } catch(NumberFormatException exc){ hue = 0; }  //red
        colorFocus.hsb[0] = hue;
        setColorFromHSB(colorFocus);
      }
      return true;      
  } };

  
  
  
  class ActionEnterHSB extends GralUserAction {
    final int indexHSB;
    ActionEnterHSB(int indexHSB){
      super("actionEnterHSB" + indexHSB);
      this.indexHSB = indexHSB;
    }
      
    @Override public boolean exec(int actionCode, GralWidget_ifc widg, Object... params) {
      if(actionCode == KeyCode.enter || actionCode == KeyCode.focusLost){
        GralTextField widgt = (GralTextField)widg;
        String text = widgt.getText();
        float value;
        try{
          value = Float.parseFloat(text.trim());
          if(indexHSB == 777){ value = (value -4) / 24; if(value < 0.0f){ value +=1.0f;} }
          //if(value > 1.0f){ value = 1.0f; }
          //if(value < 0.0f){ value = 0.0f; }
        } catch(NumberFormatException exc){ value = 0; }  //red
        colorFocus.hsb[indexHSB] = value;
        setColorFromHSB(colorFocus);
        setColorEditFields(0);
        setColorT(colorFocus);
      }
      return true;      
  } };

  
  
  class ActionEnterHLS extends GralUserAction {
    final int indexHLS;
    ActionEnterHLS(int indexHSL){
      super("actionEnterHLS" + indexHSL);
      this.indexHLS = indexHSL;
    }
      
    @Override public boolean exec(int actionCode, GralWidget_ifc widg, Object... params) {
      if(actionCode == KeyCode.enter || actionCode == KeyCode.focusLost){
        GralTextField widgt = (GralTextField)widg;
        String text = widgt.getText();
        float value;
        try{
          value = Float.parseFloat(text.trim());
          if(value > 1.0f){ value = 1.0f; }
          if(value < 0.0f){ value = 0.0f; }
        } catch(NumberFormatException exc){ value = 0; }  //red
        colorFocus.hls[indexHLS] = value;
        setColorFromHSL(colorFocus);
        setColorEditFields(0);
        setColorT(colorFocus);
      }
      return true;      
  } };

  
  
  
  GralUserAction actionFocusedTest = new GralUserAction("actionFocusedTest"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widg, Object... params) {
      if(actionCode == KeyCode.focusLost){
        GralTextField widgt = (GralTextField)widg;
        wdgTest = widgt;
        String text = widgt.getText();
        int cursor = widgt.getCursorPos();
        testText =  cursor > 0 && cursor < text.length();
      }
      return true;      
  } };

}

//==JZcmd==
//JZcmd main(){ java org.vishia.gral.test.GralColorShow.main(); }
//==endJZcmd==
package org.vishia.gral.test;
import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

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
import org.vishia.gral.swt.SwtFactory;
import org.vishia.math.CurveInterpolation;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.Debugutil;
import org.vishia.util.KeyCode;
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
  
  class ColorWithField {
    final Index index;
    GralColor color;
    int rgb;
    float[] hsb = new float[3];
    float[] hls = new float[3];
    String shortname, name;
    final GralTextField wdgColor;
    
    /**True if a graphic is initialized and opened . */
    boolean bActive;
    
    ColorWithField(String shortname, int ixCol, int ixBright, int colValue) {
      this.rgb = colValue;
      this.index = new Index();
      this.index.ixCol = ixCol; index.ixLight = ixBright;
      this.color = GralColor.getColor(colValue);
      this.shortname = shortname;
      this.name = "";
      this.wdgColor = new GralTextField("" + ixCol + "," + ixBright); //, GralTextField.Type.editable);
      this.wdgColor.setBackColor(this.color, 0);
      this.wdgColor.setData(this);
      //this.wdgColor.setActionFocused(actionFocusColor);
      this.wdgColor.setActionChange(actionEditColor);
      this.wdgColor.setActionMouse(null, GralMouseWidgetAction_ifc.mUserAll);
      //this.wdgColor.setTextColor(colText);
    }
    
    @Override public String toString(){ return shortname + ": [" + index.ixLight + ", " + index.ixCol + "]"; }
  }
  
  GralMng gralMng = GralMng.get();
  
  public static void main(String[] args){ main(); }

    
  public static void main(){
    GralColorShow main = new GralColorShow();
    main.execute();
    //main.testGetColor();
  }
  
  private void execute(){
    genDefaultConfig();
    readConfig();
    GralFactory gralFactory = new SwtFactory();
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
  , new ColHue("pu",  2.0f) 
  //)), new ColHue("rp",  2.5f) 
  , new ColHue("rd",  4.0f)  //rd
  , new ColHue("ro",  5.0f) 
  , new ColHue("or",  6.0f) 
  , new ColHue("yo",  7.0f) //or
  , new ColHue("am",  7.5f) //am
  , new ColHue("ye",  8.0f) //ye
  , new ColHue("yg",  8.4f) 
  , new ColHue("ol",  9.0f)                         
  , new ColHue("gy", 10.0f) 
  , new ColHue("gn", 12.0f)  //gn
  //, new ColHue("gc", 13.5f) 
  , new ColHue("sg", 14.0f)  //cy
  , new ColHue("cy", 16.0f) //cy
  , new ColHue("cb", 17.5f) 
  , new ColHue("bl", 19.0f) 
  , new ColHue("bu", 20.0f)   //bl
  , new ColHue("vb", 21.0f)   //bl                   
  , new ColHue("vi", 22.0f)   //vi
  , new ColHue("vm", 23.0f)   //vi
  , new ColHue("ma",  0.0f)   //vi
  };
  

  ColHue[] colHue4 =   ////
  { new ColHue("ma",  0.0f)  //pu
  , new ColHue("pk",  2.0f)  //rd
  , new ColHue("rd",  4.0f)  //rd
  , new ColHue("or",  6.0f) 
  , new ColHue("am",  7.0f) //am
  , new ColHue("ye",  8.0f) //ye
  , new ColHue("ol",  9.0f)                         
  , new ColHue("gn", 12.0f)  //gn
  , new ColHue("cy", 16.0f) //cy
  , new ColHue("bl", 20.0f) 
  , new ColHue("vi", 22.0f)   //vi
  };
  

  
  class LightSat{
    final String lName;
    final float light;
    final float sat;
    public LightSat(String lName, float light, float sat)
    { this.lName = lName;
      this.light = light;
      this.sat = sat;
    }
    
  }
  
  
  
  
  LightSat[] lightSat =
  { new LightSat("p1", 1.90f, 1.00f )
  , new LightSat("p2", 1.90f, 1.00f )
  , new LightSat("p3", 1.85f, 1.00f )
  , new LightSat("p4", 1.80f, 1.00f )
  , new LightSat("l1", 1.70f, 1.00f )
  , new LightSat("l2", 1.60f, 1.00f )
  , new LightSat("l3", 1.30f, 0.60f )
  , new LightSat("l4", 1.50f, 1.00f )
  , new LightSat("l5", 1.40f, 1.00f )
  , new LightSat("s1", 1.30f, 1.00f )
  , new LightSat("g1", 1.20f, 0.70f )
  , new LightSat("s2", 1.20f, 1.00f )
  , new LightSat("s3", 1.10f, 1.00f )
  , new LightSat("g2", 1.00f, 0.40f )                                                                 
  , new LightSat("g3", 1.00f, 0.50f )                                                                 
  , new LightSat("g4", 1.00f, 0.70f )                                                                 
  , new LightSat("s4", 1.00f, 1.00f )
  , new LightSat("s5", 0.90f, 1.00f )
  , new LightSat("g5", 0.90f, 0.70f )
  , new LightSat("g6", 0.90f, 0.50f )
  , new LightSat("g7", 0.80f, 0.40f )
  , new LightSat("g8", 0.80f, 0.60f )
  , new LightSat("s6", 0.80f, 1.00f )
  , new LightSat("d1", 0.65f, 1.00f )
  , new LightSat("d2", 0.60f, 0.70f )
  , new LightSat("g9", 0.60f, 0.50f )
  , new LightSat("d3", 0.50f, 0.60f )
  , new LightSat("d4", 0.45f, 1.00f )
  , new LightSat("d5", 0.40f, 0.70f )
  , new LightSat("d6", 0.30f, 1.00f )
  };
  
  
  LightSat[] lightSat4 =
  { new LightSat("p1", 1.90f, 1.00f )
  , new LightSat("p2", 1.85f, 1.00f )
  , new LightSat("p3", 1.80f, 1.00f )
  , new LightSat("l1", 1.70f, 1.00f )
  , new LightSat("l3", 1.30f, 0.60f )
  , new LightSat("l5", 1.40f, 1.00f )
  , new LightSat("s1", 1.30f, 1.00f )
  , new LightSat("g1", 1.20f, 0.70f )
  , new LightSat("s2", 1.20f, 1.00f )
  , new LightSat("g2", 1.00f, 0.40f )                                                                 
  , new LightSat("g4", 1.00f, 0.70f )                                                                 
  , new LightSat("s4", 1.00f, 1.00f )
  , new LightSat("g5", 0.90f, 0.70f )
  , new LightSat("g6", 0.90f, 0.50f )
  , new LightSat("g8", 0.80f, 0.60f )
  , new LightSat("s6", 0.80f, 1.00f )
  , new LightSat("g9", 0.60f, 0.70f )
  , new LightSat("d3", 0.50f, 0.60f )
  , new LightSat("d5", 0.40f, 0.70f )
  };
  
  
  
  String[] valTest = {"", ""};
  
  //TODO check gy, ol
  

  String[][] colorsLess = 
  { { "ma", ""}
  , { "", ""}
  };
  
  
  String[][] colorsAll = 
  { { "p1ma", "p1pk", "p1rd", "p1ro", "p1op", "p1yo", "p1am", "p1ye", "p1yg", "p1ol", "p1gy", "p1gn", "p1sg", "p1cy", "p1cb", "p1bl", "p1bu", "p1vb", "p1vi", "p1vm", "p1ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "p2ma", "p2pk", "p2rd", "p2ro", "p2op", "p2yo", "p2am", "p2ye", "p2yg", "p2ol", "p2gy", "p2gn", "p2sg", "p2cy", "p2cb", "p2bl", "p2bu", "p2vb", "p2vi", "p2vm", "p2ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "p3ma", "p3pk", "p3rd", "p3ro", "p3op", "p3yo", "p3am", "p3ye", "p3yg", "p3ol", "p3gy", "p3gn", "p3sg", "p3cy", "p3cb", "p3bl", "p3bu", "p3vb", "p3vi", "p3vm", "p3ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "p4ma", "p4pk", "p4rd", "p4ro", "p4op", "p4yo", "p4am", "p4ye", "p4yg", "p4ol", "p4gy", "p4gn", "p4sg", "p4cy", "p4cb", "p4bl", "p4bu", "p4vb", "p4vi", "p4vm", "p4ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "l1ma", "l1pk", "l1rd", "l1ro", "l1op", "l1yo", "l1am", "l1ye", "l1yg", "l1ol", "l1gy", "l1gn", "l1sg", "l1cy", "l1cb", "l1bl", "l1bu", "l1vb", "l1vi", "l1vm", "l1ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "l2ma", "l2pk", "l2rd", "l2ro", "l2op", "l2yo", "l2am", "l2ye", "l2yg", "l2ol", "l2gy", "l2gn", "l2sg", "l2cy", "l2cb", "l2bl", "l2bu", "l2vb", "l2vi", "l2vm", "l2ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "l3ma", "l3pk", "l3rd", "l3ro", "l3op", "l3yo", "l3am", "l3ye", "l3yg", "l3ol", "l3gy", "l3gn", "l3sg", "l3cy", "l3cb", "l3bl", "l3bu", "l3vb", "l3vi", "l3vm", "l3ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "l4ma", "l4pk", "l4rd", "l4ro", "l4op", "l4yo", "l4am", "l4ye", "l4yg", "l4ol", "l4gy", "l4gn", "l4sg", "l4cy", "l4cb", "l4bl", "l4bu", "l4vb", "l4vi", "l4vm", "l4ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "l5ma", "l5pk", "l5rd", "l5ro", "l5op", "l5yo", "l5am", "l5ye", "l5yg", "l5ol", "l5gy", "l5gn", "l5sg", "l5cy", "l5cb", "l5bl", "l5bu", "l5vb", "l5vi", "l5vm", "l5ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "s1ma", "s1pk", "s1rd", "s1ro", "s1op", "s1yo", "s1am", "s1ye", "s1yg", "s1ol", "s1gy", "s1gn", "s1sg", "s1cy", "s1cb", "s1bl", "s1bu", "s1vb", "s1vi", "s1vm", "s1ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "g1ma", "g1pk", "g1rd", "g1ro", "g1op", "g1yo", "g1am", "g1ye", "g1yg", "g1ol", "g1gy", "g1gn", "g1sg", "g1cy", "g1cb", "g1bl", "g1bu", "g1vb", "g1vi", "g1vm", "g1ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "s2ma", "s2pk", "s2rd", "s2ro", "s2op", "s2yo", "s2am", "s2ye", "s2yg", "s2ol", "s2gy", "s2gn", "s2sg", "s2cy", "s2cb", "s2bl", "s2bu", "s2vb", "s2vi", "s2vm", "s2ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "s3ma", "s3pk", "s3rd", "s3ro", "s3op", "s3yo", "s3am", "s3ye", "s3yg", "s3ol", "s3gy", "s3gn", "s3sg", "s3cy", "s3cb", "s3bl", "s3bu", "s3vb", "s3vi", "s3vm", "s3ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "g2ma", "g2pk", "g2rd", "g2ro", "g2op", "g2yo", "g2am", "g2ye", "g2yg", "g2ol", "g2gy", "g2gn", "g2sg", "g2cy", "g2cb", "g2bl", "g2bu", "g2vb", "g2vi", "g2vm", "g2ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "g3ma", "g3pk", "g3rd", "g3ro", "g3op", "g3yo", "g3am", "g3ye", "g3yg", "g3ol", "g3gy", "g3gn", "g3sg", "g3cy", "g3cb", "g3bl", "g3bu", "g3vb", "g3vi", "g3vm", "g3ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "g4ma", "g4pk", "g4rd", "g4ro", "g4op", "g4yo", "g4am", "g4ye", "g4yg", "g4ol", "g4gy", "g4gn", "g4sg", "g4cy", "g4cb", "g4bl", "g4bu", "g4vb", "g4vi", "g4vm", "g4ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "s4ma", "s4pk", "s4rd", "s4ro", "s4op", "s4yo", "s4am", "s4ye", "s4yg", "s4ol", "s4gy", "s4gn", "s4sg", "s4cy", "s4cb", "s4bl", "s4bu", "s4vb", "s4vi", "s4vm", "s4ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "s5ma", "s5pk", "s5rd", "s5ro", "s5op", "s5yo", "s5am", "s5ye", "s5yg", "s5ol", "s5gy", "s5gn", "s5sg", "s5cy", "s5cb", "s5bl", "s5bu", "s5vb", "s5vi", "s5vm", "s5ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "g5ma", "g5pk", "g5rd", "g5ro", "g5op", "g5yo", "g5am", "g5ye", "g5yg", "g5ol", "g5gy", "g5gn", "g5sg", "g5cy", "g5cb", "g5bl", "g5bu", "g5vb", "g5vi", "g5vm", "g5ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "g6ma", "g6pk", "g6rd", "g6ro", "g6op", "g6yo", "g6am", "g6ye", "g6yg", "g6ol", "g6gy", "g6gn", "g6sg", "g6cy", "g6cb", "g6bl", "g6bu", "g6vb", "g6vi", "g6vm", "g6ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "g7ma", "g7pk", "g7rd", "g7ro", "g7op", "g7yo", "g7am", "g7ye", "g7yg", "g7ol", "g7gy", "g7gn", "g7sg", "g7cy", "g7cb", "g7bl", "g7bu", "g7vb", "g7vi", "g7vm", "g7ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "g8ma", "g8pk", "g8rd", "g8ro", "g8op", "g8yo", "g8am", "g8ye", "g8yg", "g8ol", "g8gy", "g8gn", "g8sg", "g8cy", "g8cb", "g8bl", "g8bu", "g8vb", "g8vi", "g8vm", "g8ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "s6ma", "s6pk", "s6rd", "s6ro", "s6op", "s6yo", "s6am", "s6ye", "s6yg", "s6ol", "s6gy", "s6gn", "s6sg", "s6cy", "s6cb", "s6bl", "s6bu", "s6vb", "s6vi", "s6vm", "s6ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "d1ma", "d1pk", "d1rd", "d1ro", "d1op", "d1yo", "d1am", "d1ye", "d1yg", "d1ol", "d1gy", "d1gn", "d1sg", "d1cy", "d1cb", "d1bl", "d1bu", "d1vb", "d1vi", "d1vm", "d1ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "d2ma", "d2pk", "d2rd", "d2ro", "d2op", "d2yo", "d2am", "d2ye", "d2yg", "d2ol", "d2gy", "d2gn", "d2sg", "d2cy", "d2cb", "d2bl", "d2bu", "d2vb", "d2vi", "d2vm", "d2ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "g9ma", "g9pk", "g9rd", "g9ro", "g9op", "g9yo", "g9am", "g9ye", "g9yg", "g9ol", "g9gy", "g9gn", "g9sg", "g9cy", "g9cb", "g9bl", "g9bu", "g9vb", "g9vi", "g9vm", "g9ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "d3ma", "d3pk", "d3rd", "d3ro", "d3op", "d3yo", "d3am", "d3ye", "d3yg", "d3ol", "d3gy", "d3gn", "d3sg", "d3cy", "d3cb", "d3bl", "d3bu", "d3vb", "d3vi", "d3vm", "d3ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "d4ma", "d4pk", "d4rd", "d4ro", "d4op", "d4yo", "d4am", "d4ye", "d4yg", "d4ol", "d4gy", "d4gn", "d4sg", "d4cy", "d4cb", "d4bl", "d4bu", "d4vb", "d4vi", "d4vm", "d4ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "d5ma", "d5pk", "d5rd", "d5ro", "d5op", "d5yo", "d5am", "d5ye", "d5yg", "d5ol", "d5gy", "d5gn", "d5sg", "d5cy", "d5cb", "d5bl", "d5bu", "d5vb", "d5vi", "d5vm", "d5ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  , { "d6ma", "d6pk", "d6rd", "d6ro", "d6op", "d6yo", "d6am", "d6ye", "d6yg", "d6ol", "d6gy", "d6gn", "d6sg", "d6cy", "d6cb", "d6bl", "d6bu", "d6vb", "d6vi", "d6vm", "d6ma", "wh", "whma", "wh1r", "whye", "whgn", "whcy", "whbl"}
  };
  
  
  
  
  
  String[] valOk =
  { "FFE6FF:p1ma  FFF0F5:p1pk+ FFE6E6:p1rd  FAF0E6:p1ro+ FAEBD7:p1or+ FFF8DC:p1yo+ FFFACD:p1am+ F5F5DC:p1ye+ FDF5E6:p1yg+ F5FFD6:p1ol  EBFFD8:p1gy  DBFFDB:p1gn  F5FFFA:p1sg+ F0FFFF:p1cy+ F0F8FF:p1cb+ E5EBFF:p1bl  E6E6FA:p1bu+ ECE6FF:p1vb  F2E6FF:p1vi  F9E6FF:p1vm  FFE6FF:p1ma  FFFFFF:wh+   FFF7FF:whm1  FFFAFA:wh1r+ FFFFF0:whye+ F0FFF0:whgn+ F4FEFE:whc1  F8F8FF:whbl+ "
  , "FFE6FF:p2ma  FFE6F2:p2pu  FFE6E6:p2rd  FFE4E1:p2ro+ FFEBCD:p2or+ FFEFD5:p2yo+ FAFAD2:p2am+ FFFFE0:p2ye+ FBFFD5:p2yg  F5FFD6:p2ol  EBFFD8:p2gy  DBFFDB:p2gn  D8FFEB:p2sg  E0FFFF:p2cy+ E3F5FF:p2cb  E5EBFF:p2bl  E6E6FF:p2bu  ECE6FF:p2vb  F2E6FF:p2vi  F9E6FF:p2vm  FFE6FF:p2ma  F5F5F5:gr1+  EEE2EE:g1m1  EEE2E2:g1r1  EDEDDC:g1y1  DEEDDE:g1g1  DCEDED:g1c1  E2E2EE:g1b1  "
  , "FFD9FF:p3ma  FFD9EC:p3pu  FFD9D9:p3rd  FFE4C4:p3ro+ FFE4B5:p3or+ FFDEAD:p3yo+ EEE8AA:p3am+ FFFFBF:p3ye  F9FFC0:p3yg  F0FFC2:p3ol  E2FFC4:p3gy  C8FFC8:p3gn  C4FFE2:p3sg  BFFFFF:p3cy  B0E0E6:p3cb+ D7E1FF:p3bl  D9D9FF:p3bu  E2D9FF:p3vb  ECD9FF:p3vi  F5D9FF:p3vm  FFD9FF:p3ma  DCDCDC:gr2   DDCEDD:g2m1  DDCECE:g2r1  DBDBC6:g2y1  C8DCC8:g2g1  C6DBDB:g2c1  CECEDD:g2b1  "
  , "FFCCFF:p4ma  FFCCE6:p4pu  FFCCCC:p4rd  FFE4C4:p4ro  FFE4B5:p4or  FFDEAD:p4yo  EEE8AA:p4am  FFFFAA:p4ye  F7FFAB:p4yg  EBFFAD:p4ol  D8FFB1:p4gy  B6FFB6:p4gn  B1FFD8:p4sg  AAFFFF:p4cy  B0E0E6:p4cb  CAD7FF:p4bl  CCCCFF:p4bu  D9CCFF:p4vb  E6CCFF:p4vi  F2CCFF:p4vm  FFCCFF:p4ma  DCDCDC:gr2+  CCBACC:g3m1  CCBABA:g3r1  CACAB1:g3y1  B3CBB3:g3g1  B1CACA:g3c1  BABACC:g3b1  "
  , "FFB3FF:l1ma  FFB6C1:l1pk+ FFC0CB:l1rd+ F5DEB3:l1ro+ FFDAB9:l1or+ FFE492:l1yo  F0E68C:l1am+ FFFF80:l1ye  F2FF82:l1yg  E0FF85:l1ol  C4FF89:l1gy  90EE90:l1gn+ 7FFFD4:l1sg+ 80FFFF:l1cy  ADD8E6:l1cb+ B0C4DE:l1bl+ B3B3FF:l1bu  C6B3FF:l1vb  D9B3FF:l1vi  ECB3FF:l1vm  FFB3FF:l1ma  D3D3D3:gr3+  BBA6BB:g4m1  BBA6A6:g4r1  B9B99D:g4y1  A0B9A0:g4g1  9DB9B9:g4c1  A6A6BB:g4b1  "
  , "FF99FF:l2ma  FF99CC:l2pu  FF9999:l2rd  FFAA8E:l2ro  FFBF80:l2or  FFDB6D:l2yo  FFEB62:l2am  FFFF55:l2ye  EEFF58:l2yg  D6FF5C:l2ol  B1FF62:l2gy  98FB98:l2gn+ 62FFB1:l2sg  AFEEEE:l2cy+ 87CEFA:l2cb+ 95B0FF:l2bl  9999FF:l2bu  B399FF:l2vb  CC99FF:l2vi  E699FF:l2vm  FF99FF:l2ma  C0C0C0:gr4+  AA94AA:g5m1  AA9494:g5r1  A8A88A:g5y1  8DA88D:g5g1  8AA8A8:g5c1  9494AA:g5b1  "
  , "FF94FF:l3ma  FF94C9:l3pu  FF9494:l3rd  FFA07A:l3ro+ FFB76E:l3or  FFD454:l3yo  F6E14E:l3am  EDED48:l3ye  DEEE49:l3yg  C8F14B:l3ol  A2F64E:l3gy  54FF54:l3gn  4EF6A2:l3sg  48EDED:l3cy  87CEFA:l3cb+ 8FABFF:l3bl  9494FF:l3bu  AF94FF:l3vb  C994FF:l3vi  E494FF:l3vm  FF94FF:l3ma  A9A9A9:gr5+  998299:g6m1  998282:g6r1  979778:g6y1  7B977B:g6g1  789797:g6c1  828299:g6b1  "
  , "FF80FF:l4ma  FF80BF:l4pu  F08080:l4rd+ FF9571:l4ro  F4A460:l4or+ FFD149:l4yo  FFE63B:l4am  FFFF2B:l4ye  EAFF2E:l4yg  CCFF33:l4ol  9DFF3B:l4gy  49FF49:l4gn  00FA9A:l4sg+ 00FFFF:l4cy+ 87CEEB:l4cb+ 7B9CFF:l4bl  8080FF:l4bu  9F80FF:l4vb  BF80FF:l4vi  DF80FF:l4vm  FF80FF:l4ma  888888:g71   887188:g7m1  887171:g7r1  868667:g7y1  6A866A:g7g1  678686:g7c1  717188:g7b1  "
  , "FF66FF:l5ma  FF66B3:l5pu  FF6666:l5rd  FF7F50:l5ro+ FFA500:l5or+ FFD700:l5yo+ FFE214:l5am  FFFF00:l5ye+ E6FF04:l5yg  C2FF0A:l5ol  7FFF00:l5gy+ 24FF24:l5gn  00FF7F:l5sg+ 00FFFF:l5cy+ 58C0FF:l5cb  6495ED:l5bl+ 6666FF:l5bu  8C66FF:l5vb  B366FF:l5vi  D966FF:l5vm  FF66FF:l5ma  808080:gr7+  776077:g8m1  776060:g8r1  757557:g8y1  5A755A:g8g1  577575:g8c1  606077:g8b1  "
  , "FF4CFF:s1ma  DB7093:s1pu+ FF4C4C:s1rd  FF6347:s1ro+ FF8F20:s1or  FFBF00:s1yo  F6D700:s1am  EDED00:s1ye  D7EE00:s1yg  B5F100:s1ol  7BF600:s1gy  00FF00:s1gn+ 00F67B:s1sg  00EDED:s1cy  00BFFF:s1cb+ 4675FF:s1bl  4C4CFF:s1bu  794CFF:s1vb  A64CFF:s1vi  D24CFF:s1vm  FF4CFF:s1ma  666666:g91   665166:g9m1  665151:g9r1  646448:g9y1  4A644A:g9g1  486464:g9c1  515166:g9b1  "
  , "FF70FF:g1ma  FF69B4:g1pk+ FA8072:g1rd+ FF845C:g1ro  FFA042:g1or  EBBE38:g1yo  E3CD33:g1am  DBDB2F:g1ye  CBDC30:g1yg  B3DF31:g1ol  8BE333:g1gy  38EB38:g1gn  33E38B:g1sg  48D1CC:g1cy+ 5FC3FF:g1cb  6A8FFF:g1bl  7070FF:g1bu  9470FF:g1vb  B870FF:g1vi  DB70FF:g1vm  FF70FF:g1ma  555555:d01   554155:d0m1  554141:d0r1  53533A:d0y1  3C533C:d0g1  3A5353:d0c1  414155:d0b1  "
  , "FF33FF:s2ma  FF1493:s2pk+ FF3333:s2rd  FF551C:s2ro  FF8C00:s2or+ EBB100:s2yo  E3C600:s2am  DBDB00:s2ye  C6DC00:s2yg  A7DF00:s2ol  71E300:s2gy  00EB00:s2gn  00E371:s2sg  40E0D0:s2cy+ 20ACFF:s2cb  2C61FF:s2bl  3333FF:s2bu  6633FF:s2vb  9933FF:s2vi  CC33FF:s2vm  FF33FF:s2ma  444444:d11   443344:d1m1  443333:d1r1  42422D:d1y1  2E432E:d1g1  2D4242:d1c1  333344:d1b1  "
  , "FF1AFF:s3ma  FF1A8C:s3pu  FF1A1A:s3rd  FF4500:s3ro+ EA7500:s3or  DAA520:s3yo+ D0B600:s3am  C8C800:s3ye  B6CA00:s3yg  9ACD32:s3ol+ 68D000:s3gy  00D800:s3gn  00D068:s3sg  00CED1:s3cy+ 05A1FF:s3cb  124DFF:s3bl  1A1AFF:s3bu  531AFF:s3vb  8A2BE2:s3vi+ C61AFF:s3vm  FF00FF:s3ma+ 333333:d21   332533:d2m1  332525:d2r1  323220:d2y1  213221:d2g1  203232:d2c1  252533:d2b1  "
  , "D8BFD8:g2ma  FF99CC:g2pu  FF9999:g2rd  F3A68C:g2ro  DEB887:g2or+ DEC475:g2yo  D9CC70:g2am  D5D56B:g2ye  CBD56C:g2yg  BCD76E:g2ol  A5D970:g2gy  75DE75:g2gn  70D9A5:g2sg  6BD5D5:g2cy  8ECEF5:g2cb  94AEFB:g2bl  9999FF:g2bu  B399FF:g2vb  CC99FF:g2vi  E699FF:g2vm  FF99FF:g2ma+ 222222:d31   221822:d3m1  221818:d3r1  212115:d3y1  162116:d3g1  152121:d3c1  181822:d3b1  "
  , "FF80FF:g3ma  FF80BF:g3pu  FF8080:g3rd  E9967A:g3ro+ D2B48C:g3or+ C9AC55:g3yo  BDB76B:g3am+ BCBC4B:g3ye  B1BD4C:g3yg  A2BF4D:g3ol  89C250:g3gy  55C955:g3gn  66CDAA:g3sg+ 4BBCBC:g3cy  72BFED:g3cb  7A99F8:g3bl  8080FF:g3bu  9F80FF:g3vb  BF80FF:g3vi  DF80FF:g3vm  FF80FF:g3ma  000000:bk    110C11:d4m1  110C0C:d4r1  10100A:d4y1  0A100A:d4g1  0A1010:d4c1  0C0C11:d4b1  "
  , "FF4DFF:g4ma  FF4DA6:g4pu  FF4D4D:g4rd  E96B41:g4ro  D2691E:g4or+ C7A12F:g4yo  BFAD2B:g4am  B9B928:g4ye  ACBA29:g4yg  97BC2A:g4ol  75BF2B:g4gy  32CD32:g4gn+ 2BBF75:g4sg  28B9B9:g4cy  43ADEC:g4cb  4874F7:g4bl  6A5ACD:g4bu+ 794DFF:g4vb  A64DFF:g4vi  D24DFF:g4vm  FF4DFF:g4ma  FFFFFF:wh2   FFEBFF:whm2  FFEBEB:whr2  FFFAF0:whye2+ E4FDE4:whg2  E1FDFD:whc2  E6E6FA:whbl2+ "
  , "FF00FF:s4ma  FF0080:s4pu  FF0000:s4rd+ E83A00:s4ro  D56A00:s4or  C49300:s4yo  BDA500:s4am  B6B600:s4ye  A5B700:s4yg  8BB900:s4ol  5EBD00:s4gy  00C400:s4gn  00BD5E:s4sg  20B2AA:s4cy+ 1E90FF:s4cb+ 003EF7:s4bl  0000FF:s4bu+ 4000FF:s4vb  8000FF:s4vi  BF00FF:s4vm  FF00FF:s4ma+ EEEEEE:g12   EED3EE:g1m2  EED3D3:g1r2  EBEBC7:g1y2  CAECCA:g1g2  C7EBEB:g1c2  D3D3EE:g1b2  "
  , "E600E6:s5ma  DC143C:s5pk+ E60000:s5rd  D13400:s5ro  BF6000:s5or  B8860B:s5yo+ AA9500:s5am  A4A400:s5ye  95A500:s5yg  7DA700:s5ol  55AA00:s5gy  00B100:s5gn  00AA55:s5sg  00A4A4:s5cy  0084D4:s5cb  0038DE:s5bl  0000E6:s5bu  3900E6:s5vb  7300E6:s5vi  AC00E6:s5vm  E600E6:s5ma  DDDDDD:g22   DDBDDD:g2m2  DDBDBD:g2r2  DADAAF:g2y2  B2DBB2:g2g2  AFDADA:g2c2  BDBDDD:g2b2  "
  , "E645E6:g5ma  E64595:g5pu  E64545:g5rd  D2603A:g5ro  CD853F:g5or+ B3912A:g5yo  AC9C27:g5am  A6A624:g5ye  9AA825:g5yg  88A925:g5ol  6AAC27:g5gy  2AB32A:g5gn  3CB371:g5sg+ 24A6A6:g5cy  3C9BD5:g5cb  4169E1:g5bl+ 4545E6:g5bu  6D45E6:g5vb  9545E6:g5vi  BA55D3:g5vm+ E645E6:g5ma  CCCCCC:g32   CCA7CC:g3m2  CCA7A7:g3r2  C8C899:g3y2  9CC99C:g3g2  99C8C8:g3c2  A7A7CC:g3b2  "
  , "EE82EE:g6ma  E673AC:g6pu  E67373:g6rd  D38064:g6ro  C28D58:g6or  B59B4D:g6yo  AFA248:g6am  A9A943:g6ye  A0AA44:g6yg  92AC46:g6ol  7BAF48:g6gy  4DB54D:g6gn  48AF7B:g6sg  5F9EA0:g6cy+ 66ACD5:g6cb  6E8ADF:g6bl  7B68EE:g6bu+ 9370DB:g6vb+ AC73E6:g6vi  C973E6:g6vm  E673E6:g6ma+ BBBBBB:g42   BB93BB:g4m2  BB9393:g4r2  B7B784:g4y2  87B887:g4g2  84B7B7:g4c2  9393BB:g4b2  "
  , "CC7ACC:g7ma  CC7AA3:g7pu  BC8F8F:g7rd+ C28570:g7ro  B99066:g7or  B19C5E:g7yo  AEA35A:g7am  AAAA56:g7ye  A2AB56:g7yg  97AC58:g7ol  84AE5A:g7gy  8FBC8F:g7gn+ 5AAE84:g7sg  56AAAA:g7cy  72A5C4:g7cb  778899:g7bl+ 7A7ACC:g7bu  8F7ACC:g7vb  A37ACC:g7vi  B87ACC:g7vm  CC7ACC:g7ma+ AAAAAA:g52   AA80AA:g5m2  AA8080:g5r2  A5A570:g5y2  74A674:g5g2  70A5A5:g5c2  8080AA:g5b2  "
  , "CC52CC:g8ma  C71585:g8pk+ CD5C5C:g8rd+ BA6346:g8ro  AB743C:g8or  9E8434:g8yo  998C30:g8am  93932D:g8ye  8A942D:g8yg  7C962F:g8ol  649930:g8gy  349E34:g8gn  309964:g8sg  2D9393:g8cy  4682B4:g8cb+ 4D6CC6:g8bl  5252CC:g8bu  7052CC:g8vb  8F52CC:g8vi  9932CC:g8vm+ CC52CC:g8ma  999999:g62   996E99:g6m2  996E6E:g6r2  95955F:g6y2  639663:g6g2  5F9595:g6c2  6E6E99:g6b2  "
  , "CC00CC:s6ma  CC0066:s6pu  CC0000:s6rd  B92E00:s6ro  A0522D:s6or+ 9D7600:s6yo  978400:s6am  929200:s6ye  849300:s6yg  6B8E23:s6ol+ 4C9700:s6gy  009D00:s6gn  00974C:s6sg  008B8B:s6cy+ 0076BC:s6cb  0031C5:s6bl  0000CD:s6bu+ 3300CC:s6vb  6600CC:s6vi  9400D3:s6vm+ CC00CC:s6ma  888888:g72   885D88:g7m2  885D5D:g7r2  81814D:g7y2  518251:g7g2  4D8181:g7c2  5D5D88:g7b2  "
  , "A600A6:d1ma  A60053:d1pu  A60000:d1rd  972600:d1ro  8B4513:d1or+ 806000:d1yo  7B6B00:d1am  808000:d1ye+ 6B7700:d1yg  5A7900:d1ol  3D7B00:d1gy  008000:d1gn+ 007B3D:d1sg  008080:d1cy+ 006099:d1cb  0028A0:d1bl  0000A6:d1bu  2900A6:d1vb  5300A6:d1vi  7C00A6:d1vm  A600A6:d1ma+ 777777:g82   774E77:g8m2  774E4E:g8r2  6B6B3C:g8y2  406E40:g8g2  3C6B6B:g8c2  4E4E77:g8b2  "
  , "992E99:d2ma  992E63:d2pu  B22222:d2rd+ 8C4027:d2ro  815121:d2or  77601C:d2yo  73681A:d2am  6F6F18:d2ye  677018:d2yg  5B7119:d2ol  46731A:d2gy  228B22:d2gn+ 2E8B57:d2sg+ 186F6F:d2cy  708090:d2cb+ 2B4694:d2bl  2E2E99:d2bu  492E99:d2vb  663399:d2vi+ 7E2E99:d2vm  992E99:d2ma  666666:g92   663F66:g9m2  663F3F:g9r2  57572E:g9y2  325B32:g9g2  2E5757:g9c2  3F3F66:g9b2  "
  , "994D99:g9ma  994D73:g9pu  B22222:g9rd  8C5543:g9ro  825E3A:g9or  786733:g9yo  746C30:g9am  71712D:g9ye  6A712E:g9yg  61722E:g9ol  527430:g9gy  228B22:g9gn  2E8B57:g9sg  2D7171:g9cy  708090:g9cb  495C95:g9bl  4D4D99:g9bu  604D99:g9vb  663399:g9vi  864D99:g9vm  994D99:g9ma  555555:d02   553255:d0m2  553232:d0r2  464622:d0y2  254925:d0g2  224646:d0c2  323255:d0b2  "
  , "803380:d3ma  803359:d3pu  803333:d3rd  743E2C:d3ro  6B4826:d3or  635220:d3yo  5F571E:d3am  5C5C1C:d3ye  565D1C:d3yg  556B2F:d3ol+ 3F5F1E:d3gy  206320:d3gn  1E5F3F:d3sg  1C5C5C:d3cy  2D5B76:d3cb  30437C:d3bl  483D8B:d3bu+ 463380:d3vb  593380:d3vi  6C3380:d3vm  803380:d3ma  444444:d12   442644:d1m2  442626:d1r2  353518:d1y2  1B381B:d1g2  183535:d1c2  262644:d1b2  "
  , "730073:d4ma  730039:d4pu  800000:d4rd+ 681A00:d4ro  603000:d4or  584200:d4yo  554A00:d4am  525200:d4ye  4A5300:d4yg  3F5300:d4ol  2B5500:d4gy  006400:d4gn+ 00552B:d4sg  005252:d4cy  00426A:d4cb  001C6F:d4bl  00008B:d4bu+ 1D0073:d4vb  4B0082:d4vi+ 560073:d4vm  730073:d4ma+ 333333:d22   331B33:d2m2  331B1B:d2r2  262610:d2y2  122912:d2g2  102626:d2c2  1B1B33:d2b2  "
  , "661F66:d5ma  661F42:d5pu  661F1F:d5rd  5D2B1A:d5ro  563616:d5or  4F4013:d5yo  4D4511:d5am  4A4A10:d5ye  454A10:d5yg  3D4B11:d5ol  2F4D11:d5gy  134F13:d5gn  114D2F:d5sg  2F4F4F:d5cy+ 1B455F:d5cb  1D2E63:d5bl  191970:d5bu+ 301F66:d5vb  421F66:d5vi  541F66:d5vm  661F66:d5ma  222222:d32   221122:d3m2  221111:d3r2  19190A:d3y2  0B1B0B:d3g2  0A1919:d3c2  111122:d3b2  "
  , "4D004D:d6ma  4D0026:d6pu  4D0000:d6rd  461100:d6ro  402000:d6or  3B2C00:d6yo  393200:d6am  373700:d6ye  323700:d6yg  2A3800:d6ol  1C3900:d6gy  003B00:d6gn  00391C:d6sg  003737:d6cy  002C47:d6cb  00134A:d6bl  000080:d6bu+ 13004D:d6vb  26004D:d6vi  39004D:d6vm  4D004D:d6ma  111111:d42   110811:d4m2  110808:d4r2  0C0C04:d4y2  050D05:d4g2  040C0C:d4c2  080811:d4b2  "
  };
  String[] longNames =
  { "LavenderBlush=p1pk: #FFF0F5"
  , "Linen=p1ro: #FAF0E6"
  , "AntiqueWhite=p1or: #FAEBD7"
  , "Cornsilk=p1yo: #FFF8DC"
  , "LemonChiffon=p1am: #FFFACD"
  , "Beige=p1ye: #F5F5DC"
  , "OldLace=p1yg: #FDF5E6"
  , "MintCream=p1sg: #F5FFFA"
  , "Azure=p1cy: #F0FFFF"
  , "AliceBlue=p1cb: #F0F8FF"
  , "Lavender=p1bu: #E6E6FA"
  , "White=wh: #FFFFFF"
  , "Snow=wh1r: #FFFAFA"
  , "Ivory=whye: #FFFFF0"
  , "HoneyDew=whgn: #F0FFF0"
  , "GhostWhite=whbl: #F8F8FF"
  , "MistyRose=p2ro: #FFE4E1"
  , "BlanchedAlmond=p2or: #FFEBCD"
  , "PapayaWhip=p2yo: #FFEFD5"
  , "LightGoldenRodYellow=p2am: #FAFAD2"
  , "LightYellow=p2ye: #FFFFE0"
  , "LightCyan=p2cy: #E0FFFF"
  , "WhiteSmoke=gr1: #F5F5F5"
  , "Bisque=p3ro: #FFE4C4"
  , "Moccasin=p3or: #FFE4B5"
  , "NavajoWhite=p3yo: #FFDEAD"
  , "PaleGoldenRod=p3am: #EEE8AA"
  , "PowderBlue=p3cb: #B0E0E6"
  , "Gainsboro=gr2: #DCDCDC"
  , "LightPink=l1pk: #FFB6C1"
  , "Pink=l1rd: #FFC0CB"
  , "Wheat=l1ro: #F5DEB3"
  , "PeachPuff=l1or: #FFDAB9"
  , "Khaki=l1am: #F0E68C"
  , "LightGreen=l1gn: #90EE90"
  , "Aquamarine=l1sg: #7FFFD4"
  , "LightBlue=l1cb: #ADD8E6"
  , "LightSteelBlue=l1bl: #B0C4DE"
  , "LightGray=gr3: #D3D3D3"
  , "PaleGreen=l2gn: #98FB98"
  , "PaleTurquoise=l2cy: #AFEEEE"
  , "LightSkyBlue=l2cb: #87CEFA"
  , "Silver=gr4: #C0C0C0"
  , "LightSalmon=l3ro: #FFA07A"
  , "LightSkyBlue=l3cb: #87CEFA"
  , "DarkGray=gr5: #A9A9A9"
  , "LightCoral=l4rd: #F08080"
  , "SandyBrown=l4or: #F4A460"
  , "MediumSpringGreen=l4sg: #00FA9A"
  , "Aqua=l4cy: #00FFFF"
  , "SkyBlue=l4cb: #87CEEB"
  , "Coral=l5ro: #FF7F50"
  , "Orange=l5or: #FFA500"
  , "Gold=l5yo: #FFD700"
  , "Yellow=l5ye: #FFFF00"
  , "Chartreuse=l5gy: #7FFF00"
  , "SpringGreen=l5sg: #00FF7F"
  , "Cyan=l5cy: #00FFFF"
  , "CornflowerBlue=l5bl: #6495ED"
  , "Gray=gr7: #808080"
  , "PaleVioletRed=s1pu: #DB7093"
  , "Tomato=s1ro: #FF6347"
  , "Lime=s1gn: #00FF00"
  , "DeepSkyBlue=s1cb: #00BFFF"
  , "HotPink=g1pk: #FF69B4"
  , "Salmon=g1rd: #FA8072"
  , "MediumTurquoise=g1cy: #48D1CC"
  , "DeepPink=s2pk: #FF1493"
  , "DarkOrange=s2or: #FF8C00"
  , "Turquoise=s2cy: #40E0D0"
  , "OrangeRed=s3ro: #FF4500"
  , "GoldenRod=s3yo: #DAA520"
  , "YellowGreen=s3ol: #9ACD32"
  , "DarkTurquoise=s3cy: #00CED1"
  , "BlueViolett=s3vi: #8A2BE2"
  , "Magenta=s3ma: #FF00FF"
  , "BurlyWood=g2or: #DEB887"
  , "Thistle=g2ma: #FF99FF"
  , "DarkSalmon=g3ro: #E9967A"
  , "Tan=g3or: #D2B48C"
  , "DarkKhaki=g3am: #BDB76B"
  , "MediumAquaMarine=g3sg: #66CDAA"
  , "Chocolate=g4or: #D2691E"
  , "LimeGreen=g4gn: #32CD32"
  , "SlateBlue=g4bu: #6A5ACD"
  , "FloralWhite=whye2: #FFFAF0"
  , "Lavender=whbl2: #E6E6FA"
  , "Red=s4rd: #FF0000"
  , "LightSeaGreen=s4cy: #20B2AA"
  , "DodgerBlue=s4cb: #1E90FF"
  , "Blue=s4bu: #0000FF"
  , "Fuchsia=s4ma: #FF00FF"
  , "Crimson=s5pk: #DC143C"
  , "DarkGoldenRod=s5yo: #B8860B"
  , "Peru=g5or: #CD853F"
  , "MediumSeaGreen=g5sg: #3CB371"
  , "RoyalBlue=g5bl: #4169E1"
  , "MediumOrchid=g5vm: #BA55D3"
  , "CadetBlue=g6cy: #5F9EA0"
  , "MediumSlateBlue=g6bu: #7B68EE"
  , "MediumPurple=g6vb: #9370DB"
  , "Orchid=g6ma: #E673E6"
  , "RosyBrown=g7rd: #BC8F8F"
  , "DarkSeaGreen=g7gn: #8FBC8F"
  , "LightSlateGray=g7bl: #778899"
  , "Plum=g7ma: #CC7ACC"
  , "MediumVioletRed=g8pk: #C71585"
  , "IndianRed=g8rd: #CD5C5C"
  , "SteelBlue=g8cb: #4682B4"
  , "DarkOrchid=g8vm: #9932CC"
  , "Sienna=s6or: #A0522D"
  , "OliveDrab=s6ol: #6B8E23"
  , "DarkCyan=s6cy: #008B8B"
  , "MediumBlue=s6bu: #0000CD"
  , "DarkViolett=s6vm: #9400D3"
  , "SaddleBrown=d1or: #8B4513"
  , "Olive=d1ye: #808000"
  , "Green=d1gn: #008000"
  , "Teal=d1cy: #008080"
  , "DarkMagenta=d1ma: #A600A6"
  , "Brown=d2rd: #B22222"
  , "ForestGreen=d2gn: #228B22"
  , "SeaGreen=d2sg: #2E8B57"
  , "SlateGray=d2cb: #708090"
  , "RebeccaPurple=d2vi: #663399"
  , "DarkOliveGreen=d3ol: #556B2F"
  , "DarkSlateBlue=d3bu: #483D8B"
  , "DarkRed=d4rd: #800000"
  , "DarkGreen=d4gn: #006400"
  , "Darkblue=d4bu: #00008B"
  , "Indigo=d4vi: #4B0082"
  , "Purple=d4ma: #730073"
  , "DarkSlateGray=d5cy: #2F4F4F"
  , "MidnightBlue=d5bu: #191970"
  , "Navy=d6bu: #000080"
  };
  
  Map<String, ColorWithField> idxColorsByShortname = new TreeMap<String, ColorWithField>();
  
  
  
  //GralTextField[][] wdgColorBack = new GralTextField[19][10];

  //String[][] shortname = new String[19][10];
  
  //String[][] name = new String[19][10];
  
  final ColorWithField[][] colorF = new ColorWithField[lightSat.length][colHue.length + 7];
  
  
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
  
  
  
  
  
  void genDefaultConfig() {
    int colorVal;
    for(int ixHue = 0; ixHue < colHue.length; ++ixHue){
      //StringBuilder line = new StringBuilder(1000);
      for(int ixSatB = 0; ixSatB < lightSat.length; ++ixSatB){
        float b = lightSat[ixSatB].light;
        float s = lightSat[ixSatB].sat; // * colHue[ixHue][1];
        //if(b > 1.0f){ s -= b -1.0f; b = 1.0f; }
        //colorVal = HSBtoRGB(colHue[ixHue][0], s, b) & 0xffffff;
        if(ixHue == 3 && ixSatB == 1)
          Debugutil.stop();
        colorVal = GralColorConv.HLStoRGB(colHue[ixHue].colorValue, b, s) & 0xffffff;
        String shname = lightSat[ixSatB].lName + colHue[ixHue].colorName;
        ColorWithField colorF1 = new ColorWithField(shname, ixHue, ixSatB, colorVal);
        colorF[ixSatB][ixHue] = colorF1;
        setColorFromRGB(colorF1);
        setColorT(colorF1);
        ///
        //line.append(String.format("%06X", colorVal)).append(":       ");
      }
      colorF[0][ixHue].wdgColor.setText(""+colHue[ixHue].colorValue);
      if(ixHue != colHue.length-1) {
        colorF[17][ixHue].wdgColor.setText(colHue[ixHue].colorName);
      }
    }
    for(int ixsat = 0; ixsat < 2; ++ixsat){
      //float[][] clinesat = ixsat ==0 ? clineSatC1 : clineSatC2;
      char nameSat = (char)('1' + ixsat);
      for(int ixHue=0; ixHue < 7; ++ixHue){
        String[] namesHue = {"", "m", "r", "y", "g", "c", "b"};
        String nameHue = namesHue[ixHue];
        for(int ixSatB = 0; ixSatB < lightSat.length /2; ++ixSatB){
          String name = (ixSatB == 0 ? "wh": ixSatB >=10 ? ("d" + (ixSatB-10)) : ("g" + ixSatB)) + nameHue + nameSat;
          if(ixsat == 0 && ixHue == 1 && ixSatB == 5)
            Debugutil.stop();
          int ixline = ixSatB + lightSat.length/2 * ixsat;
          if(ixline == 7)
            Debugutil.stop();
          float color, sat; 
          float light = (1.0f - ((float)ixSatB) / lightSat.length * 2);
          if(ixHue == 0) {
            color = 4; 
            sat = 0;
          } else {
            color = 4*(ixHue-1);
            if(ixHue == 5 && ixSatB == 10)
              Debugutil.stop();
            //sat = CurveInterpolation.linearInterpolation(light, color, clinesat, -1);
            if(ixsat == 0) {
              sat = 0.03f + 0.3f * (1-light);// * (1-light);
            } else {
              sat = 0.08f + 0.5f * (1-light);// * (1-light);
            }
          }
          //float nlight1 = CurveInterpolation.splineInterpolation(color, clight, -1);
          //float light1 = light / nlight1;
          colorVal = GralColorConv.HLStoRGB(color, light, sat) & 0xffffff;
          ColorWithField colorF1 = new ColorWithField(name, colHue.length + ixHue, ixline, colorVal);
          colorF[ixline][colHue.length + ixHue] = colorF1;
          setColorFromRGB(colorF1);
          setColorT(colorF1);
          if(ixHue ==0){
          }
        }
      }
    }
    for(int ixSatB = 1; ixSatB < lightSat.length; ++ixSatB){
      colorF[ixSatB][colHue.length].wdgColor.setText("" + ixSatB);
      colorF[ixSatB][colHue.length-1].wdgColor.setText(lightSat[ixSatB].lName);
      if(ixSatB !=17){
        colorF[ixSatB][0].wdgColor.setText("" + lightSat[ixSatB].light);
        colorF[ixSatB][1].wdgColor.setText("" + lightSat[ixSatB].sat);
      }
    }    
    //val[colHue.length] = line.toString();
  }
  
  
  
  
  
  void readConfig()
  { 
    //String[] val = valTest;
    String[] val = valOk;
    
    int zLine = Math.min(val.length, colorF.length);
    for(int ixBright = 0; ixBright < zLine; ++ixBright){
      String line = val[ixBright];
      StringPartScan spline = new StringPartScan(line);
      for(int ixCol=0; ixCol<val.length; ++ixCol) {
        GralColor colText;
        if(ixBright < 11){ colText = colBk; }
        else { colText = colWh; }
        int col2;
        spline.setIgnoreWhitespaces(true);
        spline.scanOk();
        try{ 
          spline.scanHex(6); 
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
        if(ixCol < colorF[ixBright].length){
          ColorWithField colorF1 = colorF[ixBright][ixCol];
          if(spline.scan("+").scanOk()){
            //marked.
            colorF1.rgb = col2;
            colorF1.shortname = shortname;
            setColorFromRGB(colorF1);
            setColorT(colorF1);
          }
          if(shortname.length() >0){
            idxColorsByShortname.put(colorF1.shortname, colorF1);
          }
        } else {
          Debugutil.stop();
        }
      }
    }
    //associate names
    for(String longname: longNames) {
      StringPartScan spline = new StringPartScan(longname);
      spline.lento('=');
      CharSequence name = spline.getCurrentPart();
      spline.fromEnd();
      spline.scanStart();
      if(spline.scan("=").scanIdentifier().scanOk()){
        String cname = spline.getLastScannedString().toString();
        ColorWithField colorF1 = idxColorsByShortname.get(cname);
        if(colorF1 == null) {
          System.err.println("GralColor: faulty short name in long name entry; " + longname);
        } else {
          colorF1.name = name.toString();
          colorF1.wdgColor.setText(colorF1.name);
        }
      }
      /*
      int psep = longname.indexOf('=');
      if(psep >0) {
        ColorWithField colorF1 = idxColorsByShortname.get(longname.substring(psep+1).trim());
        if(colorF1 == null) {
          System.err.println("GralColor: faulty short name in long name entry; " + longname);
        } else {
          colorF1.name = longname.substring(0, psep);
          colorF1.wdgColor.setText(colorF1.name);
        }
      }*/ 
      else {
        System.err.println("GralColor: faulty longname entry; " + longname);
      }
    }
    
  }
  
  
  
  
  void outColors(){
    System.out.append("  String[] valOk =\n");
    String sep = "  { \"";
    for(int line=0; line < colorF.length; ++line) {
      System.out.append(sep);
      String spaces = "            ";
      for(int col = 0; col < colorF[0].length; ++col){
        ColorWithField colorF1 = colorF[line][col];
        //String sVal = colorF1.wdgColor.getText();
        GralColor color = colorF1.wdgColor.getBackColor(0);
        int colValue = color.getColorValue();
        String sHex = String.format("%06X", colValue);
        System.out.append(sHex).append(':').append(colorF1.shortname);
        if(colorF1.name !=null && colorF1.name.trim().length()>0){
          System.out.append('+');
        } else {
          System.out.append(' ');
        }
        int zspaces = 5 - colorF1.shortname.length();
        if(zspaces < 1){ zspaces = 1; }
        System.out.append(spaces.substring(0, zspaces));
      }
      System.out.append("\"\n");
      sep = "  , \"";
    }
    System.out.append("  };\n");
  
    System.out.append("  String[] longNames =\n");
    sep = "  { \"";
    for(int line=0; line<colorF.length; ++line) {
      for(int col = 0; col < colorF[0].length; ++col){
        if(colorF[line][col].name.length()>0) {
          GralColor color = colorF[line][col].wdgColor.getBackColor(0);
          int colValue = color.getColorValue();
          String sHex = String.format("%06X", colValue);
          System.out.append(sep).append(colorF[line][col].name)
          .append('=').append(colorF[line][col].shortname)
          .append(": #").append(sHex)
          .append("\"\n");
          sep = "  , \"";
        }
      }
    }
    System.out.append("  };\n");
  
  }


  void setColorFromRGB(ColorWithField colorF1){
    int col2 = colorF1.rgb;
    RGBtoHSB(col2, colorF1.hsb);
    GralColorConv.RGBtoHLS(col2, colorF1.hls);
    //colorF1.color = GralColor.getColor(colorF1.rgb);
    //colorF1.wdgColor.setBackColor(colorF1.color, 0);
  }
        
  
  
  
  void setColorFromHSB(ColorWithField colorF1) {
    colorF1.rgb = HSBtoRGB(colorF1.hsb[0], colorF1.hsb[1], colorF1.hsb[2]) & 0xffffff;
    GralColorConv.RGBtoHLS(colorF1.rgb, colorF1.hls);
    /*    String sHex = String.format("%06X", colorF1.rgb);
    wdgHexValue.setText(sHex);
    colorF1.color = GralColor.getColor(colorF1.rgb);
    colorF1.wdgColor.setBackColor(colorF1.color, 0);
*/  }


  void setColorFromHSL(ColorWithField colorF1) {
    colorF1.rgb = GralColorConv.HLStoRGB(colorF1.hls[0], colorF1.hls[1], colorF1.hls[1]) & 0xffffff;
    RGBtoHSB(colorF1.rgb, colorF1.hsb);
    /*   String sHex = String.format("%06X", colorF1.rgb);
    wdgHexValue.setText(sHex);
    colorF1.color = GralColor.getColor(colorF1.rgb);
    colorF1.wdgColor.setBackColor(colorF1.color, 0);
 */ }

  
  
  void setColorEditFields(int nr) {
    ColorWithField colorF1 = nr == 2 ? colorFocus2 : colorFocus;
    String sHex = String.format("%06X", colorF1.rgb);
    wdgHexValue.setText(sHex); //Integer.toHexString(colValue));
    wdgShortname.setText(colorF1.shortname);
    wdgName.setText(colorF1.name);
    if(colorF1.name.length()>0){
      colorF1.wdgColor.setText(colorF1.name);
    }
    float hue1 = colorF1.hsb[0]; //24 * colorF1.hsb[0] +4;
    //if(hue1 >= 24.0f){ hue1 -= 24; }
    wdgHue.setText(""+hue1);
    wdgSat.setText(""+colorF1.hsb[1]);
    wdgBright.setText(""+colorF1.hsb[2]);
    float col2 = 24 * colorF1.hls[0] +4;
    if(col2 >=24.0f) col2 -= 24.0f;
    wdgHue2.setText(""+colorF1.hls[0]);
    wdgSat2.setText(""+colorF1.hls[2]);
    wdgLight2.setText(""+colorF1.hls[1]);
    GralColor color = GralColor.getColor(colorF1.rgb);
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
    setColorFromRGB(colorFocus);
    setColorEditFields(1);
 
  }
  
  
  void setFocusColor2(ColorWithField field)
  { colorFocus2.wdgColor.setBorderWidth(0);
    colorFocus2 = field; 
    colorFocus2.wdgColor.setBorderWidth(3);
    setColorFromRGB(colorFocus2);
    setColorEditFields(2);
 
  }
  
  
  
  void setColorT(ColorWithField colorF1) {
    colorF1.color = GralColor.getColor(colorF1.rgb);
    colorF1.wdgColor.setBackColor(colorF1.color, 0);
  }

  GralGraphicTimeOrder initGraphicFullColors = new GralGraphicTimeOrder("GralArea9Window.initGraphic"){
    @Override public void executeOrder()
    {
      gralMng.setPosition(4, -2, 2, -2, 0, 'd');
      //gralMng.addTextField();
      for(int ixCol=0; ixCol<colorF[0].length; ++ixCol) {
        gralMng.setPosition(4, GralPos.size -3, 4*ixCol +1, GralPos.size+4, 0, 'd', 0);
        //gralMng.setPosition(3, GralPos.size -2, 9*col, GralPos.size+8, 0, 'd', 1);
        //System.out.append(", \"");
        //int pline = 0;
        ColorWithField colorF1 = null;
        for(int ixBright = 0; ixBright < colorF.length; ++ixBright){
          GralColor colText;
          if(ixBright < 11){ colText = colBk; }
          else { colText = colWh; }
          colorF1 = colorF[ixBright][ixCol];
          if(colorF1 !=null) {
            colorF1.wdgColor.setToPanel(gralMng);
            colorF1.bActive = true;
            colorF1.wdgColor.setTextColor(colText);
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
  
  
  
  GralGraphicTimeOrder initGraphicLessColors = new GralGraphicTimeOrder("initGraphicLessColors"){
    @Override public void executeOrder()
    {
      gralMng.setPosition(4, -2, 2, -2, 0, 'd');
      int posColumn = -3;
      for(int ixCol=0; ixCol<colorF[0].length; ixCol+=2) { //create columns
        posColumn += 4;
        gralMng.setPosition(4, GralPos.size -3, posColumn, GralPos.size+4, 0, 'd', 0);
        //gralMng.setPosition(3, GralPos.size -2, 9*col, GralPos.size+8, 0, 'd', 1);
        //System.out.append(", \"");
        //int pline = 0;
        ColorWithField colorF1 = null;
        for(int ixBright = 0; ixBright < colorF.length; ixBright+=2){ //fill cells of columns.
          GralColor colText;
          colorF1 = colorF[ixBright][ixCol];
          if(ixBright < 11){ colText = colBk; }
          else { colText = colWh; }
          if(colorF1 !=null) {
            colorF1.wdgColor.setToPanel(gralMng);
            colorF1.bActive = true;
            colorF1.wdgColor.setTextColor(colText);
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
            setColorFromRGB(colorFocus);
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
        setColorFromRGB(colorFocus);
        setColorEditFields(0);
      }
      return true;
  } };
  
  
  
  GralUserAction actionEditColor = new GralUserAction("edit color"){
    @Override public boolean exec(int key, GralWidget_ifc widgP, Object... params)
    {
      GralTextField wdg = (GralTextField)widgP;
      if(key == KeyCode.right) {
        int ixRow = colorFocus.index.ixLight;
        int ixCol = colorFocus.index.ixCol;
        int ctw = 100;
        do { if(++ixCol >= colorF[0].length){ ixCol = 0;}
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
        do { if(--ixCol <0){  ixCol = colorF[0].length -1; }
        } while(--ctw >= 0 && !colorF[ixRow][ixCol].bActive);
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
        int ctw = 100;
        do { if(--ixRow <0){  ixRow = colorF.length -1; }
        } while(--ctw >= 0 && !colorF[ixRow][ixCol].bActive);
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
        int ctw = 100;
        do { if(++ixRow >=colorF.length){  ixRow = 0; }
        } while(--ctw >= 0 && !colorF[ixRow][ixCol].bActive);
        ColorWithField newField = colorF[ixRow][ixCol];
        newField.wdgColor.setFocus();
        setFocusColor(newField);
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
        colorF2.rgb = colorFocus.rgb;  //copy from last focused.
        colorF2.wdgColor.setBackColor(colorFocus.wdgColor.getBackColor(0),0);
        colorF2.shortname = colorFocus.shortname;
        colorF2.name = colorFocus.name;
        setColorFromRGB(colorF2);
        //setFocusColor(wdg);
      } else if(key == KeyCode.mouse1Down) {
        ColorWithField colorF2 = (ColorWithField)widgP.getData();
        setColorFromRGB(colorF2);
        setColorT(colorFocus);
        setColorEditFields(1);
        bNewSwitchShow = true;
        System.out.println("mouse down");
        bRightShow = false;
        setFocusColor(colorF2);
      } else if(key == KeyCode.mouse1Down + KeyCode.shift) {
        ColorWithField colorF2 = (ColorWithField)widgP.getData();
        setColorFromRGB(colorF2);
        setColorT(colorFocus);
        bNewSwitchShow = true;
        setColorEditFields(2);
        System.out.println("mouse sh down");
        bRightShow = true;
        setFocusColor2(colorF2);
      } else if(key == (KeyCode.shift + KeyCode.pgup)){
        int rd = (colorFocus.rgb >>16) & 0xff;
        if(rd <255) rd +=1;
        colorFocus.rgb = (colorFocus.rgb & 0x00ffff) | rd <<16;
        setColorFromRGB(colorFocus);
        setColorT(colorFocus);
        setColorEditFields(0);
      } else if(key == (KeyCode.shift + KeyCode.left)){
        int rd = (colorFocus.rgb >>16) & 0xff;
        if(rd >0) rd -=1;
        colorFocus.rgb = (colorFocus.rgb & 0x00ffff) | rd <<16;
        setColorFromRGB(colorFocus);
        setColorT(colorFocus);
        setColorEditFields(0);
      } else if(key == (KeyCode.shift + KeyCode.up)){
        int gn = (colorFocus.rgb >>8) & 0xff;
        if(gn <255) gn +=1;
        colorFocus.rgb = (colorFocus.rgb & 0xff00ff) | gn <<8;
        setColorFromRGB(colorFocus);
        setColorT(colorFocus);
        setColorEditFields(0);
      } else if(key == (KeyCode.shift + KeyCode.dn)){
        int gn = (colorFocus.rgb >>8) & 0xff;
        if(gn >0) gn -=1;
        colorFocus.rgb = (colorFocus.rgb & 0xff00ff) | gn <<8;
        setColorFromRGB(colorFocus);
        setColorT(colorFocus);
        setColorEditFields(0);
      } else if(key == (KeyCode.shift + KeyCode.pgdn)){  //key of a notebook: 
        int bl = (colorFocus.rgb) & 0xff;
        if(bl <255) bl +=1;
        colorFocus.rgb = (colorFocus.rgb & 0xffff00) | bl;
        setColorFromRGB(colorFocus);
        setColorT(colorFocus);
        setColorEditFields(0);
      } else if(key == (KeyCode.shift + KeyCode.right)){
        int bl = (colorFocus.rgb) & 0xff;
        if(bl >0) bl -=1;
        colorFocus.rgb = (colorFocus.rgb & 0xffff00) | bl;
        setColorFromRGB(colorFocus);
        setColorT(colorFocus);
        setColorEditFields(0);
      } else if(key == KeyCode.enter){
        setColorFromRGB(colorFocus);
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
          colorFocus.rgb = Integer.parseInt(text, 16);
          setColorFromRGB(colorFocus);
          GralColor colorBack = GralColor.getColor(colorFocus.rgb);
          colorFocus.color = colorBack;
          colorFocus.wdgColor.setBackColor(colorBack, 0); 
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
        String text = widgt.getText();
        if(colorFocus.shortname.length() >0) {
          //remove given shortname 
          idxColorsByShortname.remove(colorFocus.shortname);
        }
        colorFocus.shortname = text.trim();
        idxColorsByShortname.put(colorFocus.shortname, colorFocus);
      }
      return true;      
  } };

  GralUserAction actionEnterName = new GralUserAction("actionEnterName"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widg, Object... params) {
      if(actionCode == KeyCode.enter || actionCode == KeyCode.focusLost){
        GralTextField widgt = (GralTextField)widg;
        String text = widgt.getText();
        colorFocus.name = text.trim(); 
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

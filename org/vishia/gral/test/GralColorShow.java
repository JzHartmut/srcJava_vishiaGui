//==JZcmd==
//JZcmd main(){ java org.vishia.gral.test.GralColorShow.main(); }
//==endJZcmd==
package org.vishia.gral.test;
import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

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
    
  }
  
  class ColorWithField {
    final Index index;
    GralColor color;
    int rgb;
    float[] hsb = new float[3];
    float[] hsl = new float[3];
    String shortname, name;
    final GralTextField wdgColor;
    
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
    gralMng.gralDevice.addDispatchOrder(initGraphic);
    //initGraphic.awaitExecution(1, 0);
    while(gralMng.gralDevice.isRunning()){
      try{ Thread.sleep(100);} 
      catch (InterruptedException e)
      { //dialogZbnfConfigurator.terminate();
      }
    }
      
  }
  
  
  /**Sensivity of light in respect to the color. It is a table-given function used with interpolation.
   */
  static float[][] clight = 
  { 
    { 0, 1 }  //magenta
  , { 4, 1 }  //red
  , { 8, 1.4f }  //yellow
  , {12, 1.3f }  //green
  , {16, 1.4f }  //cyan
  , {17, 1.1f }  //blue
  , {20, 1 }  //blue
  , {24, 1 }  //magenta
  };
  
  
  static float[][] clightSat = 
  { 
    {0, 0 }  
  , { 0.2f, 0.05f } 
  , { 0.3f, 0.1f } 
  , { 0.5f, 0.9f } 
  , { 1, 1 }  
  };
  
  
  
  static float bfrd = 1.0f;
  static float bfgn = 1.1f;
  static float bfbl = 0.9f;
  
  static float cfrd = 1/0.7f;  //1.8f;
  //static float cfye = 1.0f;
  static float cfgn = 1/0.8f; //1.5f;
  //static float cfcy = 1.0f;
  static float cfbl = 1/0.6f; //2.2f;
  //static float cfma = 1.3f;
  
  /**Factors for rd, ye, gn, cy, bl, ma */
  static float[] cfrgb = { 1.0f, 1.2f, 0.8f}; //1/0.6f, 1/0.8f, 1/0.5f};
  //static float[] cfrgb = { 1/0.6f, 1.0f, 1/0.8f, 1.0f, 1/0.5f, 1/0.8f};
  
  
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
  , new ColHue("rd",  9.0f)                         
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
  , new LightSat("p2", 1.85f, 1.00f )
  , new LightSat("p3", 1.80f, 1.00f )
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
  , new LightSat("d3", 0.50f, 0.60f )
  , new LightSat("d4", 0.45f, 1.00f )
  , new LightSat("d5", 0.40f, 0.70f )
  , new LightSat("d6", 0.30f, 1.00f )
  };
  
  
  float[][] colSatB_old =
  { { 1.00f, 1.45f     , 1.00f, 1.45f     }
  , { 1.00f, 1.40f     , 1.00f, 1.40f     }
  , { 1.00f, 1.30f     , 1.00f, 1.30f     }
  , { 1.00f, 1.25f     , 1.00f, 1.25f     }
  , { 1.00f, 1.20f     , 1.00f, 1.20f     }
  , { 1.00f, 1.10f     , 1.00f, 1.10f     }
  , { 1.00f, 1.00f     , 1.00f, 1.00f     }
  , { 1.00f, 0.95f     , 1.00f, 0.95f     }
  , { 1.00f, 0.90f     , 1.00f, 0.90f     }
  , { 1.00f, 0.85f     , 1.00f, 0.85f     }
  , { 1.00f, 0.80f     , 1.00f, 0.80f     }
  //, { 1.00f, 0.75f     , 1.00f, 0.75f     }
  , { 0.80f, 0.75f     , 0.80f, 0.75f     }
  , { 0.60f, 0.70f     , 0.60f, 0.70f     }                                                                 
  , { 0.70f, 0.70f     , 0.70f, 0.70f     }                                                                 
  , { 1.00f, 0.70f     , 1.00f, 0.70f     }
  , { 1.00f, 0.60f     , 1.00f, 0.60f     }
  , { 0.80f, 0.60f     , 0.80f, 0.60f     }
  , { 0.70f, 0.60f     , 0.70f, 0.60f     }
  , { 0.50f, 0.60f     , 0.50f, 0.60f     }
  , { 0.30f, 0.50f     , 0.30f, 0.50f     }
  , { 0.50f, 0.50f     , 0.50f, 0.50f     }
  , { 0.70f, 0.50f     , 0.70f, 0.50f     }
  , { 1.00f, 0.50f     , 1.00f, 0.50f     }
  , { 1.00f, 0.40f     , 1.00f, 0.40f     }
  , { 0.80f, 0.30f     , 0.80f, 0.30f     }
  , { 0.60f, 0.30f     , 0.60f, 0.30f     }
  , { 1.00f, 0.30f     , 1.00f, 0.30f     }
  , { 1.00f, 0.20f     , 1.00f, 0.20f     }
                                         

  
 
  };
  
  
  String[] valTest = {"", ""};
  
  String[] valOk =
  { "FFE6FF:p1ma  FFF0F5:p1pu  FFE6E6:p1rd  FAF0E6:p1ro  FAEBD7:p1or  FFF8DC:p1yo  FFFACD:p1am  F5F5DC:p1ye  FBFFD5:p1yg  F5FFD6:p1rd  EBFFD8:p1gy  DBFFDB:p1gn  D8FFEB:p1sg  F0FFFF:p1cy  F0F8FF:p1cb  E5EBFF:p1bl  E6E6FA:p1bu  ECE6FF:p1vb  F2E6FF:p1vi  F9E6FF:p1vm  FFE6FF:p1ma  FFFFFF:      FFF7FF:      FFF7F7:      FFFFF0:whye  F0FFF0:whgn  F4FEFE:      F8F8FF:whbl  "
  , "FFD9FF:p2ma  FFD9EC:p2pu  FFD9D9:p2rd  FFDFD5:p2ro  FFEBCD:p2or  FFF1C8:p2yo  FAFAD2:p2am  FFFFE0:p2ye  F9FFC0:p2yg  F0FFC2:p2rd  E2FFC4:p2gy  C8FFC8:p2gn  C4FFE2:p2sg  E0FFFF:p2cy  D5EFFF:p2cb  D7E1FF:p2bl  D9D9FF:p2bu  E2D9FF:p2vb  ECD9FF:p2vi  F5D9FF:p2vm  FFD9FF:p2ma  EDEDED:      EDE1ED:      EDE1E1:      ECECDB:      DCECDC:      DBECEC:      E1E1ED:      "
  , "FFCCFF:p3ma  FFCCE6:p3pu  FFCCCC:p3rd  FFD5C6:p3ro  FFE4C4:p3or  FFEDB6:p3yo  FFF5B1:p3am  FFFFAA:p3ye  F7FFAB:p3yg  EBFFAD:p3rd  D8FFB1:p3gy  B6FFB6:p3gn  B1FFD8:p3sg  AAFFFF:p3cy  C7EAFF:p3cb  CAD7FF:p3bl  CCCCFF:p3bu  D9CCFF:p3vb  E6CCFF:p3vi  F2CCFF:p3vm  FFCCFF:p3ma  DCDCDC:gr2   DBCBDB:      DBCBCB:      D9D9C3:      C5D9C5:      C3D9D9:      CBCBDB:      "
  , "FFB3FF:l1ma  FFB6C1:l1pu  FFB3B3:l1rd  FFBFAA:l1ro  FFCF9F:l1or  FFE492:l1yo  F0E68C:l1am  FFFF80:l1ye  F2FF82:l1yg  E0FF85:l1rd  C4FF89:l1gy  90EE90:l1gn  7FFFD4:l1sg  80FFFF:l1cy  ADD8E6:l1cb  B0C4DE:l1bl  B3B3FF:l1bu  C6B3FF:l1vb  D9B3FF:l1vi  ECB3FF:l1vm  FFB3FF:l1ma  D3D3D3:gr3   C8B5C8:      C8B5B5:      C6C6AD:      AFC7AF:      ADC6C6:      B5B5C8:      "
  , "FF99FF:l2ma  FF99CC:l2pu  FF9999:l2rd  FFAA8E:l2ro  FFBF80:l2or  FFDB6D:l2yo  FFEB62:l2am  FFFF55:l2ye  EEFF58:l2yg  D6FF5C:l2rd  B1FF62:l2gy  6DFF6D:l2gn  62FFB1:l2sg  55FFFF:l2cy  90D5FF:l2cb  95B0FF:l2bl  9999FF:l2bu  B399FF:l2vb  CC99FF:l2vi  E699FF:l2vm  FF99FF:l2ma  B6B6B6:      B6A1B6:      B6A1A1:      B4B498:      9AB59A:      98B4B4:      A1A1B6:      "
  , "FF94FF:l3ma  FF94C9:l3pu  FF9494:l3rd  FFA07A:l3ro  FFB76E:l3or  FFD454:l3yo  F6E14E:l3am  EDED48:l3ye  DEEE49:l3yg  C8F14B:l3rd  A2F64E:l3gy  54FF54:l3gn  4EF6A2:l3sg  48EDED:l3cy  87CEFA:l3cb  8FABFF:l3bl  9494FF:l3bu  AF94FF:l3vb  C994FF:l3vi  E494FF:l3vm  FF94FF:l3ma  A9A9A9:gr6   A48DA4:      A48D8D:      A2A284:      86A286:      84A2A2:      8D8DA4:      "
  , "FF80FF:l4ma  FF80BF:l4pu  F08080:l4rd  FF9571:l4ro  FFAF60:l4or  FFD149:l4yo  FFE63B:l4am  FFFF2B:l4ye  EAFF2E:l4yg  CCFF33:l4rd  9DFF3B:l4gy  49FF49:l4gn  00FA9A:l4sg  00FFFF:l4cy  74CBFF:l4cb  7B9CFF:l4bl  8080FF:l4bu  9F80FF:l4vb  BF80FF:l4vi  DF80FF:l4vm  FF80FF:l4ma  929292:      927B92:      927B7B:      909071:      739073:      719090:      7B7B92:      "
  , "FF66FF:l5ma  FF66B3:l5pu  FF6666:l5rd  FF7F50:l5ro  FF9F40:l5or  FFD700:l5yo  FFE214:l5am  FFFF00:l5ye  E6FF04:l5yg  C2FF0A:l5rd  7FFF00:l5gy  24FF24:l5gn  14FF89:l5sg  00FFFF:l5cy  58C0FF:l5cb  6495ED:l5bl  6666FF:l5bu  8C66FF:l5vb  B366FF:l5vi  D966FF:l5vm  FF66FF:l5ma  808080:gr7   806980:      806969:      7D7D5F:      627E62:      5F7D7D:      696980:      "
  , "FF4CFF:s1ma  FF4CA6:s1pu  FF4C4C:s1rd  FF6A39:s1ro  FF8F20:s1or  FFBF00:s1yo  F6D700:s1am  EDED00:s1ye  D7EE00:s1yg  ADFF2F:s1rd  7BF600:s1gy  00FF00:s1gn  00F67B:s1sg  00EDED:s1cy  00BFFF:s1cb  4675FF:s1bl  4C4CFF:s1bu  794CFF:s1vb  A64CFF:s1vi  D24CFF:s1vm  FF4CFF:s1ma  6D6D6D:      6D576D:      6D5757:      6B6B4F:      516C51:      4F6B6B:      57576D:      "
  , "FF70FF:g1ma  FF69B4:g1pu  FF7070:g1rd  FF845C:g1ro  FFA042:g1or  EBBE38:g1yo  E3CD33:g1am  DBDB2F:g1ye  CBDC30:g1yg  B3DF31:g1rd  8BE333:g1gy  38EB38:g1gn  33E38B:g1sg  48D1CC:g1cy  5FC3FF:g1cb  6A8FFF:g1bl  7070FF:g1bu  9470FF:g1vb  B870FF:g1vi  DB70FF:g1vm  FF70FF:g1ma  5B5B5B:      5B475B:      5B4747:      59593F:      415941:      3F5959:      47475B:      "
  , "FF33FF:s2ma  FF1493:s2pu  FF3333:s2rd  FF551C:s2ro  FF8C00:s2or  EBB100:s2yo  E3C600:s2am  DBDB00:s2ye  C6DC00:s2yg  A7DF00:s2rd  71E300:s2gy  00EB00:s2gn  00E371:s2sg  00DBDB:s2cy  20ACFF:s2cb  2C61FF:s2bl  3333FF:s2bu  6633FF:s2vb  9933FF:s2vi  CC33FF:s2vm  FF33FF:s2ma  494949:      493749:      493737:      474730:      324732:      304747:      373749:      "
  , "FF1AFF:s3ma  FF1A8C:s3pu  FF1A1A:s3rd  FF4000:s3ro  EA7500:s3or  DAA520:s3yo  D0B600:s3am  C8C800:s3ye  B6CA00:s3yg  99CC00:s3rd  68D000:s3gy  00D800:s3gn  00D068:s3sg  00CED1:s3cy  05A1FF:s3cb  124DFF:s3bl  1A1AFF:s3bu  531AFF:s3vb  8A2BE2:s3vi  C61AFF:s3vm  FF00FF:s3ma  373737:      372837:      372828:      353523:      243524:      233535:      282837:      "
  , "FF99FF:g2ma  FF99CC:g2pu  FF9999:g2rd  F3A68C:g2ro  DEB887:g2or  DEC475:g2yo  D9CC70:g2am  D5D56B:g2ye  CBD56C:g2yg  BCD76E:g2rd  A5D970:g2gy  75DE75:g2gn  70D9A5:g2sg  6BD5D5:g2cy  8ECEF5:g2cb  94AEFB:g2bl  9999FF:g2bu  B399FF:g2vb  CC99FF:g2vi  E699FF:g2vm  FF99FF:g2ma  242424:      241A24:      241A1A:      232316:      172417:      162323:      1A1A24:      "
  , "FF80FF:g3ma  FF80BF:g3pu  FF8080:g3rd  E9967A:g3ro  D89D61:g3or  C9AC55:g3yo  BDB76B:g3am  BCBC4B:g3ye  B1BD4C:g3yg  A2BF4D:g3rd  89C250:g3gy  55C955:g3gn  66CDAA:g3sg  4BBCBC:g3cy  72BFED:g3cb  7A99F8:g3bl  8080FF:g3bu  9F80FF:g3vb  BF80FF:g3vi  DF80FF:g3vm  FF80FF:g3ma  121212:      120D12:      120D0D:      11110B:      0B120B:      0B1111:      0D0D12:      "
  , "FF4DFF:g4ma  FF4DA6:g4pu  FF4D4D:g4rd  E96B41:g4ro  D2691E:g4or  C7A12F:g4yo  BFAD2B:g4am  B9B928:g4ye  ACBA29:g4yg  97BC2A:g4rd  75BF2B:g4gy  32CD32:g4gn  2BBF75:g4sg  28B9B9:g4cy  43ADEC:g4cb  4874F7:g4bl  4D4DFF:g4bu  794DFF:g4vb  A64DFF:g4vi  D24DFF:g4vm  FF4DFF:g4ma  FFFFFF:      FFEBFF:      FFEBEB:      FFFAF0:whye2 E4FDE4:      E1FDFD:      E6E6FA:whbl2 "
  , "FF00FF:s4ma  FF0080:s4pu  FF0000:s4rd  E83A00:s4ro  D56A00:s4or  C49300:s4yo  BDA500:s4am  B6B600:s4ye  A5B700:s4yg  8BB900:s4rd  5EBD00:s4gy  00C400:s4gn  00BD5E:s4sg  20B2AA:s4cy  1E90FF:s4cb  003EF7:s4bl  0000FF:s4bu  4000FF:s4vb  8000FF:s4vi  BF00FF:s4vm  FF00FF:s4ma  EDEDED:      EDD1ED:      EDD1D1:      EAEAC5:      C8EBC8:      C5EAEA:      D1D1ED:      "
  , "E600E6:s5ma  DC143C:s5pu  E60000:s5rd  D13400:s5ro  BF6000:s5or  B8860B:s5yo  AA9500:s5am  A4A400:s5ye  95A500:s5yg  7DA700:s5rd  55AA00:s5gy  00B100:s5gn  00AA55:s5sg  00A4A4:s5cy  0084D4:s5cb  0038DE:s5bl  0000E6:s5bu  3900E6:s5vb  7300E6:s5vi  AC00E6:s5vm  E600E6:s5ma  DBDBDB:      DBB9DB:      DBB9B9:      D7D7AC:      AFD8AF:      ACD7D7:      B9B9DB:      "
  , "E645E6:g5ma  E64595:g5pu  E64545:g5rd  D2603A:g5ro  C17932:g5or  B3912A:g5yo  AC9C27:g5am  A6A624:g5ye  9AA825:g5yg  88A925:g5rd  6AAC27:g5gy  2AB32A:g5gn  3CB371:g5sg  24A6A6:g5cy  3C9BD5:g5cb  4169DE:g5bl  4545E6:g5bu  6D45E6:g5vb  9545E6:g5vi  BA55D3:g5vm  E645E6:g5ma  C8C8C8:      C8A3C8:      C8A3A3:      C5C594:      98C698:      94C5C5:      A3A3C8:      "
  , "E673E6:g6ma  E673AC:g6pu  E67373:g6rd  D38064:g6ro  C28D58:g6or  B59B4D:g6yo  AFA248:g6am  A9A943:g6ye  A0AA44:g6yg  92AC46:g6rd  7BAF48:g6gy  4DB54D:g6gn  48AF7B:g6sg  5F9EA0:g6cy  66ACD5:g6cb  6E8ADF:g6bl  7B68EE:g6bu  9370DB:g6vb  AC73E6:g6vi  C973E6:g6vm  E673E6:g6ma  B6B6B6:      B68EB6:      B68E8E:      B2B27E:      82B382:      7EB2B2:      8E8EB6:      "
  , "CC7ACC:g7ma  CC7AA3:g7pu  CC7A7A:g7rd  C28570:g7ro  B99066:g7or  B19C5E:g7yo  AEA35A:g7am  AAAA56:g7ye  A2AB56:g7yg  97AC58:g7rd  84AE5A:g7gy  8FBC8F:g7gn  5AAE84:g7sg  56AAAA:g7cy  72A5C4:g7cb  778899:g7bl  7A7ACC:g7bu  8F7ACC:g7vb  A37ACC:g7vi  B87ACC:g7vm  CC7ACC:g7ma  A4A4A4:      A47AA4:      A47A7A:      9F9F6A:      6EA06E:      6A9F9F:      7A7AA4:      "
  , "CC52CC:g8ma  CC528F:g8pu  CD5C5C:g8rd  BA6346:g8ro  AB743C:g8or  9E8434:g8yo  998C30:g8am  93932D:g8ye  8A942D:g8yg  7C962F:g8rd  649930:g8gy  349E34:g8gn  309964:g8sg  2D9393:g8cy  4891BD:g8cb  4D6CC6:g8bl  5252CC:g8bu  7052CC:g8vb  8F52CC:g8vi  9932CC:g8vm  CC52CC:g8ma  929292:      926792:      926767:      8D8D58:      5B8E5B:      588D8D:      676792:      "
  , "CC00CC:s6ma  CC0066:s6pu  CC0000:s6rd  B92E00:s6ro  AA5500:s6or  9D7600:s6yo  978400:s6am  929200:s6ye  849300:s6yg  6F9400:s6rd  4C9700:s6gy  009D00:s6gn  00974C:s6sg  008B8B:s6cy  0076BC:s6cb  0031C5:s6bl  0000CD:s6bu  3300CC:s6vb  6600CC:s6vi  9400D3:s6vm  CC00CC:s6ma  808080:      805580:      805555:      767645:      497849:      457676:      555580:      "
  , "8B008B:d1ma  A60053:d1pu  A60000:d1rd  972600:d1ro  8A4500:d1or  806000:d1yo  7B6B00:d1am  767600:d1ye  6B7700:d1yg  5A7900:d1rd  3D7B00:d1gy  008000:d1gn  007B3D:d1sg  007676:d1cy  006099:d1cb  0028A0:d1bl  0000A6:d1bu  2900A6:d1vb  5300A6:d1vi  7C00A6:d1vm  A600A6:d1ma  6D6D6D:      6D456D:      6D4545:      606034:      386338:      346060:      45456D:      "
  , "992E99:d2ma  992E63:d2pu  B22222:d2rd  8C4027:d2ro  815121:d2or  77601C:d2yo  73681A:d2am  6F6F18:d2ye  677018:d2yg  5B7119:d2rd  46731A:d2gy  228B22:d2gn  1A7346:d2sg  186F6F:d2cy  28688E:d2cb  2B4694:d2bl  2E2E99:d2bu  492E99:d2vb  632E99:d2vi  7E2E99:d2vm  992E99:d2ma  5B5B5B:      5B375B:      5B3737:      4C4C26:      2A4F2A:      264C4C:      37375B:      "
  , "803380:d3ma  803359:d3pu  803333:d3rd  743E2C:d3ro  6B4826:d3or  635220:d3yo  5F571E:d3am  5C5C1C:d3ye  565D1C:d3yg  556B2F:d3rd  3F5F1E:d3gy  206320:d3gn  1E5F3F:d3sg  1C5C5C:d3cy  2D5B76:d3cb  30437C:d3bl  483D8B:d3bu  463380:d3vb  593380:d3vi  6C3380:d3vm  803380:d3ma  494949:      492949:      492929:      39391B:      1E3D1E:      1B3939:      292949:      "
  , "730073:d4ma  730039:d4pu  800000:d4rd  681A00:d4ro  603000:d4or  584200:d4yo  554A00:d4am  525200:d4ye  4A5300:d4yg  3F5300:d4rd  2B5500:d4gy  006400:d4gn  00552B:d4sg  005252:d4cy  00426A:d4cb  001C6F:d4bl  00008B:d4bu  1D0073:d4vb  4B0082:d4vi  560073:d4vm  730073:d4ma  373737:      371D37:      371D1D:      292912:      142C14:      122929:      1D1D37:      "
  , "661F66:d5ma  661F42:d5pu  661F1F:d5rd  5D2B1A:d5ro  563616:d5or  4F4013:d5yo  4D4511:d5am  4A4A10:d5ye  454A10:d5yg  3D4B11:d5rd  2F4D11:d5gy  134F13:d5gn  114D2F:d5sg  2F4F4F:d5cy  1B455F:d5cb  1D2E63:d5bl  1F1F66:d5bu  301F66:d5vb  421F66:d5vi  541F66:d5vm  661F66:d5ma  242424:      241224:      241212:      1B1B0A:      0C1D0C:      0A1B1B:      121224:      "
  , "4D004D:d6ma  4D0026:d6pu  4D0000:d6rd  461100:d6ro  402000:d6or  3B2C00:d6yo  393200:d6am  373700:d6ye  323700:d6yg  2A3800:d6rd  1C3900:d6gy  003B00:d6gn  00391C:d6sg  003737:d6cy  002C47:d6cb  00134A:d6bl  00004D:d6bu  13004D:d6vb  26004D:d6vi  39004D:d6vm  4D004D:d6ma  121212:      120812:      120808:      0D0D05:      050E05:      050D0D:      48D1CC:      "
  };
  String[] longNames =
  { "LavenderBlush=p1pu"
  , "Linen=p1ro"
  , "AntiqueWhite=p1or"
  , "Cornsilk=p1yo"
  , "LemonChiffon=p1am"
  , "Beige=p1ye"
  , "Azure=p1cy"
  , "AliceBlue=p1cb"
  , "Lavender=p1bu"
  , "Ivory=whye"
  , "HoneyDew=whgn"
  , "GhostWhite=whbl"
  , "BlanchedAlmond=p2or"
  , "LightGoldenRodYellow=p2am"
  , "LightYellow=p2ye"
  , "LightCyan=p2cy"
  , "Bisque=p3or"
  , "Gainsboro=gr2"
  , "LightPink=l1pu"
  , "Khaki=l1am"
  , "LightGreen=l1gn"
  , "Aquamarine=l1sg"
  , "LightBlue=l1cb"
  , "LightSteelBlue=l1bl"
  , "LightGray=gr3"
  , "LightSalmon=l3ro"
  , "LightSkyBlue=l3cb"
  , "DarkGray=gr6"
  , "LightCoral=l4rd"
  , "MediumSpringGreen=l4sg"
  , "Aqua=l4cy"
  , "Coral=l5ro"
  , "Gold=l5yo"
  , "Chartreuse=l5gy"
  , "Cyan=l5cy"
  , "CornflowerBlue=l5bl"
  , "Gray=gr7"
  , "GreenYellow=s1rd"
  , "Lime=s1gn"
  , "DeepSkyBlue=s1cb"
  , "HotPink=g1pu"
  , "MediumTurquoise=g1cy"
  , "DeepPink=s2pu"
  , "DarkOrange=s2or"
  , "GoldenRod=s3yo"
  , "DarkTurquoise=s3cy"
  , "BlueViolett=s3vi"
  , "Magenta=s3ma"
  , "BurlyWood=g2or"
  , "DarkSalmon=g3ro"
  , "DarkKhaki=g3am"
  , "MediumAquaMarine=g3sg"
  , "Chocolate=g4or"
  , "LimeGreen=g4gn"
  , "FloralWhite=whye2"
  , "Lavender=whbl2"
  , "LightSeaGreen=s4cy"
  , "DodgerBlue=s4cb"
  , "Blue=s4bu"
  , "Fuchsia=s4ma"
  , "Crimson=s5pu"
  , "DarkGoldenRod=s5yo"
  , "MediumSeaGreen=g5sg"
  , "MediumOrchid=g5vm"
  , "CadetBlue=g6cy"
  , "MediumSlateBlue=g6bu"
  , "MediumPurple=g6vb"
  , "DarkSeaGreen=g7gn"
  , "LightSlateGray=g7bl"
  , "IndianRed=g8rd"
  , "DarkOrchid=g8vm"
  , "DarkCyan=s6cy"
  , "MediumBlue=s6bu"
  , "DarkViolett=s6vm"
  , "Green=d1gn"
  , "DarkMagenta=d1ma"
  , "Brown=d2rd"
  , "ForestGreen=d2gn"
  , "DarkOliveGreen=d3rd"
  , "DarkSlateBlue=d3bu"
  , "Maroon=d4rd"
  , "DarkRed=d4rd"
  , "DarkGreen=d4gn"
  , "Darkblue=d4bu"
  , "Indigo=d4vi"
  , "DarkSlateGray=d5cy"
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
  
  
 
  static float RGBtoligth(float rd, float gn, float bl, float sat){
    //++++++++++++++
    //++++++++++++++++ ////
    //+++
    float rd1 = bfrd * rd;
    float gn1 = bfgn * gn;
    float bl1 = bfbl * bl;
    //float rd1 =  rd;
    //float gn1 =  gn;
    //float bl1 =  bl;
    
    float lval = Math.min(rd1, Math.min(gn1, bl1));
    float hval = Math.max(rd1, Math.max(gn1, bl1));
    float mid;
    if(hval == rd1 && lval == bl1) mid = gn1;
    else if(hval == rd1 && lval == gn1) mid = bl1;
    else if(hval == gn1 && lval == bl1) mid = rd1;
    else if(hval == gn1 && lval == rd1) mid = bl1;
    else if(hval == bl1 && lval == rd1) mid = gn1;
    else mid = rd1;
    
    //float f = hval == 0 ? 1 : (0.2f * lval/hval + 0.2f * mid/hval + 1) / 1.4f; 
    float fmid = 0.3f; //hval == 0 ? 1 : 1.0f - 0.8f * mid/hval;
    float fl =  0.1f; //hval == 0 ? 1 : 1.0f - 0.8f * lval/hval;
    //return (0.7f/255 * rd + 0.8f/255 * gn + 0.6f/255 * bl); // * (0.5f + 0.5f * sat);
    //return (float)Math.sqrt(0.34f/255 * rd + 0.40f/255 * gn + 0.27f/255 * bl);
    //float light1 = 0.33f/255 * rd + 0.45f/255 * gn + 0.22f/255 * bl; 
    float light1 = (hval + fmid * mid + fl * lval) /255; 
    return  light1 / 1.46f; //  *f;
  }
  
  
  
  static float RGBtoHue(int rgb) {
    float hue;
    int rd = (rgb>>16) & 0xff;
    int gn = (rgb>>8) & 0xff;
    int bl = rgb & 0xff;
    
    
    int lval = Math.min(rd, Math.min(gn, bl));
    int hval = Math.max(rd, Math.max(gn, bl));
    int mid;
    if(hval == lval) { //gray
      hue = 0;
      mid = lval;
    }
    else if(hval == rd) {
      if(lval == gn) { //bl is more significant
        hue = 4 - ((float)(bl-lval) / (rd - lval) * 4 *cfbl / cfrd);
        mid = bl;
      } else {
        hue = 4 + ((float)(gn-lval) / (rd - lval) * 4 *cfgn / cfrd);
        mid = gn;
      }
    } else if(hval == gn) {
      if(lval == bl) { //rd is more significant
        hue = 12 - ((float)(rd-lval) / (gn - lval) * 4 * cfrd/ cfgn);
        mid = rd;
      } else { //bl is more significant
        hue = 12 + ((float)(bl-lval) / (gn - lval) * 4 * cfbl / cfgn);
        mid = bl;
      }
    } else { //bl 
      if(lval == rd) { //gn is more significant
        hue = 20 - ((float)(gn-lval) / (bl - lval) * 4 * cfgn / cfbl);
        mid = gn;
      } else {
        hue = 20 + ((float)(rd-lval) / (bl - lval) * 4 * cfrd / cfbl);
        mid = rd;
      }
    }
    return hue;
  }
  
  
  
  
  static void RGBtoHSL(int rgb, float[] hsl){
    if(rgb == 0xF273FF)
      Debugutil.stop();
    float rd = (rgb>>16) & 0xff;
    float gn = (rgb>>8) & 0xff;
    float bl = rgb & 0xff;
    
    
    float lval = Math.min(rd, Math.min(gn, bl));
    float hval = Math.max(rd, Math.max(gn, bl));
    float mid;
    hsl[0] = RGBtoHue(rgb);
    if(hval == 255) {
      hsl[1] = 1.0f; //white or paster color is not gray.
    } else if(hval == lval) {
      hsl[1] = 0; //gray
    } else {
      hsl[1] = (float)(hval - lval) / hval;
    }
    hsl[2] = RGBtoligth(rd, gn, bl, hsl[1]); 
  }
  
  
  

  


  
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
  
  
  
  
  static int HSLtoRGB(float hue, float sat, float light){  ////
    if(hue == 9 && sat == 1 && light == 1.95f)
      Debugutil.stop();
    float hue1 = (hue-4) / 24;
    if(hue1 < 0 ){ hue1 += 1.0f; }
    float nlight1 = CurveInterpolation.linearInterpolation(hue, clight, -1);
    float nlightsat = CurveInterpolation.splineInterpolation(sat, clightSat, -1);
    float nlight;
    if(light <= 1){
      nlight = 1 + (nlight1 - 1) * nlightsat;
    } else {
      nlight = nlight1;
    }
    float b1 = light / nlight;
    //float sat1 = sat + (1-sat) * (nlight - 1 )/2; //yellow, saturation is greater
    float sat1 = sat + (1-sat) * sat * (nlight1 - 1 ); //yellow, saturation is greater
    if(b1 > 1){
      float satCorr = sat1 * (light - nlight) / (2 - nlight);
      sat1 = sat1 - satCorr;
      b1 = 1;
    }
    return java.awt.Color.HSBtoRGB(hue1, sat1, b1);
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
        colorVal = HSLtoRGB(colHue[ixHue].colorValue, s, b) & 0xffffff;
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
      for(int ixHue=0; ixHue < 7; ++ixHue){
        for(int ixSatB = 0; ixSatB < lightSat.length /2; ++ixSatB){
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
          colorVal = HSLtoRGB(color, sat, light) & 0xffffff;
          ColorWithField colorF1 = new ColorWithField("", colHue.length + ixHue, ixline, colorVal);
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
        if(shortname.length() >0){
          ColorWithField colorF1 = colorF[ixBright][ixCol];
          colorF1.rgb = col2;
          colorF1.shortname = shortname;
          setColorFromRGB(colorF1);
          setColorT(colorF1);
          idxColorsByShortname.put(colorF1.shortname, colorF1);
        }
      }
    }
    //associate names
    for(String longname: longNames) {
      int psep = longname.indexOf('=');
      if(psep >0) {
        ColorWithField colorF1 = idxColorsByShortname.get(longname.substring(psep+1).trim());
        if(colorF1 == null) {
          System.err.println("GralColor: faulty short name in long name entry; " + longname);
        } else {
          colorF1.name = longname.substring(0, psep);
        }
      } else {
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
        int zspaces = 6 - colorF1.shortname.length();
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
          System.out.append(sep).append(colorF[line][col].name).append('=').append(colorF[line][col].shortname).append("\"\n");
          sep = "  , \"";
        }
      }
    }
    System.out.append("  };\n");
  
  }


  void setColorFromRGB(ColorWithField colorF1){
    int col2 = colorF1.rgb;
    RGBtoHSB(col2, colorF1.hsb);
    RGBtoHSL(col2, colorF1.hsl);
    //colorF1.color = GralColor.getColor(colorF1.rgb);
    //colorF1.wdgColor.setBackColor(colorF1.color, 0);
  }
        
  
  
  
  void setColorFromHSB(ColorWithField colorF1) {
    colorF1.rgb = HSBtoRGB(colorF1.hsb[0], colorF1.hsb[1], colorF1.hsb[2]) & 0xffffff;
    RGBtoHSL(colorF1.rgb, colorF1.hsl);
    /*    String sHex = String.format("%06X", colorF1.rgb);
    wdgHexValue.setText(sHex);
    colorF1.color = GralColor.getColor(colorF1.rgb);
    colorF1.wdgColor.setBackColor(colorF1.color, 0);
*/  }


  void setColorFromHSL(ColorWithField colorF1) {
    colorF1.rgb = HSLtoRGB(colorF1.hsl[0], colorF1.hsl[1], colorF1.hsl[2]) & 0xffffff;
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
    float hue1 = colorF1.hsb[0]; //24 * colorF1.hsb[0] +4;
    //if(hue1 >= 24.0f){ hue1 -= 24; }
    wdgHue.setText(""+hue1);
    wdgSat.setText(""+colorF1.hsb[1]);
    wdgBright.setText(""+colorF1.hsb[2]);
    float col2 = 24 * colorF1.hsl[0] +4;
    if(col2 >=24.0f) col2 -= 24.0f;
    wdgHue2.setText(""+colorF1.hsl[0]);
    wdgSat2.setText(""+colorF1.hsl[1]);
    wdgLight2.setText(""+colorF1.hsl[2]);
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

  GralGraphicTimeOrder initGraphic = new GralGraphicTimeOrder("GralArea9Window.initGraphic"){
    @Override public void executeOrder()
    {
      gralMng.setPosition(4, -2, 2, -2, 0, 'd');
      //gralMng.addTextField();
      for(int ixCol=0; ixCol<colorF[0].length; ++ixCol) {
        gralMng.setPosition(4, GralPos.size -3, 4*ixCol +1, GralPos.size+4, 0, 'd', 0);
        //gralMng.setPosition(3, GralPos.size -2, 9*col, GralPos.size+8, 0, 'd', 1);
        //System.out.append(", \"");
        //int pline = 0;
        for(int ixBright = 0; ixBright < colorF.length; ++ixBright){
          GralColor colText;
          if(ixBright < 11){ colText = colBk; }
          else { colText = colWh; }
          if(colorF[ixBright][ixCol] !=null) {
            colorF[ixBright][ixCol].wdgColor.setToPanel(gralMng);
            colorF[ixBright][ixCol].wdgColor.setTextColor(colText);
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
      wdgHue2.setActionChange(new ActionEnterHSL(0));
      wdgHue2.setToPanel(gralMng);
      wdgLight2 = new GralTextField("name");
      wdgLight2.setEditable(true);
      wdgLight2.setActionChange(new ActionEnterHSL(2));
      wdgLight2.setToPanel(gralMng);
      wdgSat2 = new GralTextField("name");
      wdgSat2.setEditable(true);
      wdgSat2.setActionChange(new ActionEnterHSL(1));
      wdgSat2.setToPanel(gralMng);
      //
      /*
      GralTextField[][] colorGen = new GralTextField[11][24];
      for(int line=0; line<colorGen.length; ++line) {
        gralMng.setPosition(3*line+43, GralPos.size -2, 1, GralPos.size+8, 0, 'r', 1);
        GralColor colText;
        if(line < 6){ colText = colBk; }
        else { colText = colWh; }
        for(int col = 0; col < colorGen[line].length; ++col){
          int valColor = genColor(line, col);
          GralColor col3 = new GralColor(valColor);
          String nameColor = col3.getColorName().substring(2);
          colorGen[line][col] = new GralTextField("" + line + "," + col);
          colorGen[line][col].setToPanel(gralMng);
          //colorGen[line][col].setTextStyle(colText, font);
          colorGen[line][col].setText(nameColor);
          colorGen[line][col].setForegroundColor(colText);
          colorGen[line][col].setBackColor(col3, 0);
        }
      }
      */
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
        int ixCol = colorFocus.index.ixCol +1;
        if(ixCol >= colorF[0].length) { ixCol = 0; }
        ColorWithField newField = colorF[colorFocus.index.ixLight][ixCol];
        newField.wdgColor.setFocus();
        setFocusColor(newField);
      } else if(key == KeyCode.shift + KeyCode.right) {
        int ixCol = colorFocus2.index.ixCol +1;
        if(ixCol >= colorF[0].length) { ixCol = 0; }
        ColorWithField newField = colorF[colorFocus2.index.ixLight][ixCol];
        newField.wdgColor.setFocus();
        setFocusColor2(newField);
      } else if(key == KeyCode.left){ 
        int ixCol = colorFocus.index.ixCol -1;
        if(ixCol < 0) { ixCol = colorF[0].length -1; }
        ColorWithField newField = colorF[colorFocus.index.ixLight][ixCol];
        newField.wdgColor.setFocus();
        setFocusColor(newField);
      } else if(key == KeyCode.shift + KeyCode.left){ 
        int ixCol = colorFocus2.index.ixCol -1;
        if(ixCol < 0) { ixCol = colorF[0].length -1; }
        ColorWithField newField = colorF[colorFocus2.index.ixLight][ixCol];
        newField.wdgColor.setFocus();
        setFocusColor2(newField);
      } else if(key == KeyCode.up && colorFocus.index.ixLight > 0){ 
        ColorWithField newField = colorF[colorFocus.index.ixLight-1][colorFocus.index.ixCol];
        newField.wdgColor.setFocus();
        setFocusColor(newField);
      } else if(key == KeyCode.shift + KeyCode.up && colorFocus2.index.ixLight > 0){ 
        ColorWithField newField = colorF[colorFocus2.index.ixLight-1][colorFocus2.index.ixCol];
        newField.wdgColor.setFocus();
        setFocusColor2(newField);
      } else if(key == KeyCode.dn && colorFocus.index.ixLight < colorF.length -1){ 
        ColorWithField newField = colorF[colorFocus.index.ixLight+1][colorFocus.index.ixCol];
        newField.wdgColor.setFocus();
        setFocusColor(newField);
      } else if(key == KeyCode.shift + KeyCode.dn && colorFocus2.index.ixLight < colorF.length -1){ 
        ColorWithField newField = colorF[colorFocus2.index.ixLight+1][colorFocus2.index.ixCol];
        newField.wdgColor.setFocus();
        setFocusColor2(newField);
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

  
  
  class ActionEnterHSL extends GralUserAction {
    final int indexHSL;
    ActionEnterHSL(int indexHSL){
      super("actionEnterHSL" + indexHSL);
      this.indexHSL = indexHSL;
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
        colorFocus.hsl[indexHSL] = value;
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

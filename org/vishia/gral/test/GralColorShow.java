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
      this.wdgColor.setActionFocused(actionFocusColor);
      this.wdgColor.setActionChange(actionEditColor);
      this.wdgColor.setActionMouse(null, GralMouseWidgetAction_ifc.mUserAll);
      //this.wdgColor.setTextColor(colText);
    }
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
    { 0, 1/0.7f }  //magenta
  , { 4, 1/0.7f }  //red
  , { 8, 1.0f }  //yellow
  , {12, 1/0.9f }  //green
  , {16, 1.0f }  //cyan
  , {20, 1/0.6f }  //blue
  , {24, 1/0.7f }  //magenta
  };
  
  
  /**Factor to add light >1 to the background colors in dependency to the color. */
  static float[][] cfOutshine = 
  { 
    { 0, 1.0f }  //magenta
  , { 4, 1.5f }  //red
  , { 8, 1.0f }  //yellow
  , {12, 1.0f }  //green
  , {16, 1.0f }  //cyan
  , {17, 1.0f }  //cyan
  , {20, 1.0f }  //blue
  , {24, 1.0f }  //magenta
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
  
  
  
  float[][] colHue =   ////
  { {  0.0f, 1/0.9f  ,  0.0f} //pu
  , {  2.0f, 1.28f ,  2.0f}
  , {  3.0f, 1.35f ,  2.0f}
  , {  4.0f, 1/0.7f  ,  4.0f} //rd
  , {  5.0f, 1.3f ,  4.3f}
  , {  6.0f, 1.2f ,  4.8f}
  , {  7.0f, 1.1f  ,  5.4f} //or
  , {  7.5f, 1.05f ,  6.1f} //am
  , {  8.0f, 1.0f  ,  7.3f} //ye
  , {  8.4f, 1.01f ,  8.5f}
  , {  9.0f, 1.03f ,  8.5f}
  , { 10.0f, 1.05f ,  9.8f}
  , { 11.0f, 1.08f , 11.3f}
  , { 12.0f, 1/0.9f  , 12.0f} //gn
  , { 13.0f, 1.2f , 12.8f}
  , { 14.0f, 1.3f , 13.8f}
  , { 15.0f, 1.35f  , 15.1f}  //cy
  , { 16.0f, 1.4f  , 16.0f}  //cy
  , { 17.0f, 1.45f  , 17.0f}
  , { 18.5f, 1.5f  , 18.5f}
  , { 20.0f, 1/0.6f  , 20.0f}  //bl
  , { 21.0f, 1/0.7f  , 20.0f}  //bl
  , { 22.0f, 1/0.8f  , 22.0f}  //vi
  , { 23.0f, 1/0.85f  , 22.0f}  //vi
  , {  0.0f, 1/0.9f  , 22.0f}  //vi
  };
  
  
  
  float[][] colSatB =
  { /*
      { 0.10f, 1.00f     , 0.10f, 1.00f     }  ////
  , { 0.25f, 1.00f     , 0.25f, 1.00f     }
  , { 0.40f, 1.00f     , 0.40f, 1.00f     }
  , { 0.50f, 1.00f     , 0.50f, 1.00f     }
  , { 0.60f, 1.00f     , 0.60f, 1.00f     }
  , { 0.70f, 1.00f     , 0.70f, 1.00f     }
  , { 0.80f, 1.00f     , 0.80f, 1.00f     }
  , { 0.90f, 1.00f     , 0.90f, 1.00f     }
  , { 0.10f, 1.00f     , 0.10f, 1.00f     }  ////
  
  ,*/ 
    { 1.00f, 1.45f     , 1.00f, 1.45f     }
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
  { "FFE7FF:      FFE7FF:      FFE7FF:      FFF2FF:      FFF5F4:      FFF5D7:      FFFFEE:wye   FFF9A4:      F4FFA5:      E3FFB6:      E4FFDA:      D3FFEA:      C5FFFD:      A4FFFC:      C1FFFF:      9CFFFF:      C1FFFF:      D5FAFF:      FFE7FF:      FFE7FF:      00BFFF:      "
  , "FFE7FF:      FFE7FF:      FFE7FF:      FFEAFF:      FFEFEC:      FFEECF:      FFFFA0:pye   FFF39C:      E2FF91:      D1FFA2:      D2FFC6:      C1FFD6:      B2FFE8:      92FFE8:      83FFFB:      94F9FF:      B4F9FF:      CEF4FF:      FFE7FF:      FFE7FF:      FFE7FF:      "
  , "FFE5FF:      FFE5FF:      FFE5FF:      FFE6FD:      FFE9E4:      FFEBCB:      FFFF98:lye   FFEF98:      D7FD85:      C7FF97:      BFFFB1:      B7FFCB:      A0FFD4:      88FFDD:      78FDEF:      90F5FF:      ADF3FF:      CAF0FF:      FFE7FF:      FFE5FF:      F9E2FF:      "
  , "FFA9FF:      FFA9FF:      FFC7F3:      FFCCDE:      FFD2C9:      FFDEAD:nw    FFE060:ye1   FFFF00:ye    E0FF4D:gye   ABFF78:      A3FF92:      9CFFAD:      84FFB5:      6CFFBE:      00FFFF:cy    00FFFF:cy    A3EAFF:      C0E7FF:      F6DFFF:      D0D0FF:lbl   F0DAFF:      "
  , "FE73FF:      FF8BF0:      FFB6DF:      FFBCCA:      FFC2B5:      FFC7A0:      FFD700:Gold  F9E187:      C9F075:      99FE64:      91FF7E:      89FF98:      71FFA0:      5AFEAA:      6AF0DF:      80E7FF:      9DE4FF:      BAE2FF:      F1DBFF:      F8A8FF:      E6D1FF:      "
  , "ED63FF:      FF61BC:      FF8BAB:      FF9196:      FF9780:      FFA500:or    F9B36A:      ECD679:      BCE467:      8CF255:      62FF4A:      5AFF64:      43FF6D:      4DF29B:      5DE4D1:      6FD7FF:      8CD5FF:      A9D2FF:      DAC6FF:      E798FF:      DDC8F5:      "
  , "FF00FF:pu1   FF479D:      FF728C:      FF7876:      FF8C00:dor   FD8953:      F1AB61:      E4CE70:      B4DD5E:      84EB4C:      54FA3A:      3EFF44:      35FA5D:      45EB92:      55DDC8:      65CEFD:      77C2FF:      8FBAFF:      BEACFF:      CE81FF:      D3BFEA:      "
  , "D44DEB:      FF0080:rd0   FF3F4D:      FF6000:rd1   FF7550:Coral ED7A41:      E19D50:      D4C05E:      A4CE4C:      74DD3A:      44EB29:      15FA17:      25EB4C:      35E880:      45CEB6:      55C0EB:      4594FD:      5788FF:      8679FF:      964FFF:      CAB7E0:      "
  , "C43ED9:      E4216F:      FF0000:rd    F72713:      EA4921:      DD6C2F:      D18E3E:      DCCA4C:      94C03A:      64CE29:      34DD17:      05EB05:      15DD3A:      25CE6F:      35C0A4:      45B1D9:      3586EB:      255AFD:      4000FF:bl1   6421FD:      C1AFD6:      "
  , "C348D7:      E02F78:      FC1318:      F13225:      E65233:      DA713F:      CF914C:      C3B058:      98BD49:      6DCB39:      42D82A:      16E418:      26D849:      34CB78:      42BDA8:      50B0D7:      4289E8:      0000FF:bl    4E46FF:      6D2FF8:      B8A7CC:      "
  , "C15ED6:      D7498B:      EE3541:      E54D4B:      DC6555:      D37E5F:      CA9769:      C1AF73:      A0B967:      7DC35A:      5CCD4D:      3BD741:      46CD65:      51C38B:      5DB9B1:      68AFD6:      5D91E3:      5171EF:      0000FF:bl    7D49EF:      AF9FC2:      "
  , "BF73D4:      CF649E:      DF5669:      D96771:      D27978:      CC8A7F:      C69C87:      BFAD8D:      A8B585:      8FBB7B:      78C373:      60CB6A:      68C384:      70BC9F:      78B5BA:      7FADD4:      7898DD:      7082E6:      5F56F7:      9065E6:      A696B8:      "
  , "A371B4:      B05A8B:      B65F74:      B85D67:      AE737D:      AE7872:      A78886:      A4947D:      949885:      7D9F6F:      77A07A:      58AB61:      6EA084:      649F8B:      7898A5:      7194B5:      7887BA:      6471C4:      695FC9:      7D5AC4:      9C8DAD:      "
  , "A550B7:      B94078:      CC2E38:      C44241:      BD574A:      B56C52:      AD815B:      A69664:      899E58:      6CA74D:      50B043:      33B938:      3DB058:      46A777:      509E97:      5995B7:      FFFFFF:wh    4661CC:      332EE2:      6C3FCC:      9385A3:      "
  , "A73BB9:      C12464:      DA0C0F:      D0281B:      C64426:      BC6033:      B17B3D:      A89749:      81A33A:      5BAF2C:      34BA1D:      0FC610:      1BBA39:      28AF64:      35A38F:      4297BA:      3575C7:      2852D6:      0F0DF2:      5B24D6:      8A7D99:      "
  , "A926BB:      C90951:      DF0000:      DA0D00:      CF3003:      C35412:      B67620:      A9992E:      7AA81D:      4AB60C:      18C300:      00CA00:      00C31B:      0BB652:      1AA786:      2A99BB:      1A6DCD:      0A42DF:      0000FF:sbl   4909DF:      81758F:      "
  , "8F0E9E:      AC0030:      BA0000:      BA0000:      B21600:      A63A00:      9B5D02:      8F8111:      5E8F00:      2D9C00:      00A800:      00A900:      00A800:      009C31:      008F68:      008B8B:dcy   0054AE:      0027BE:      0000CE:      2C00BE:      786D85:      "
  , "8D239C:      A60B46:      BA0000:      B40F00:      AB2C08:      A14815:      976420:      8D7F2B:      678B1D:      40960E:      1AA201:      00A800:      01A21D:      0D9646:      1B8B73:      277F9C:      1B5DAB:      0D39B8:      0000CD:      400BB8:      6F647B:      "
  , "8B399B:      9E275A:      B1151A:      AA2B24:      A23F2C:      9A5334:      93693E:      8B7E46:      6F873B:      528F30:      359725:      18A01A:      22973A:      2C8F5A:      36877B:      3F7E9B:      3564A4:      2C4AAF:      1815C4:      5227AF:      655B70:      "
  , "894E97:      9A3463:      A33743:      A43736:      98524E:      975A44:      8E6E5A:      8A7D52:      77825A:      5A8B40:      4F8D4A:      2A9A2E:      438D58:      3A8B63:      518284:      4A7D99:      506A9E:      3A51AA:      3D37B4:      5A34AA:      5C5366:      "
  , "6F367A:      801D46:      881E25:      891E18:      7E3A31:      7C4126:      74563D:      6F6434:      5C6A3B:      407423:      35762D:      108211:      29763B:      207446:      366A66:      30657C:      365382:      203A8D:      221E96:      401D8D:      534B5C:      "
  , "71157D:      880133:      940000:      930300:      8B1C00:      833506:      7A4E10:      71661A:      50700E:      2E7B02:      0B8400:      008600:      00840B:      027B33:      0D7058:      008B8B:dcy1  0D488A:      022997:      0000A4:      2E0197:      4A4352:      "
  , "72007E:      900011:      940000:      940000:      940000:      8A2000:      7E4300:      726700:      417400:      108200:      008600:      008000:gn    008600:      008211:      007448:      008080:teal  003A8F:      000E9F:      0000A5:      10009F:      413B48:      "
  , "55005E:      6A0011:      6F0000:      6F0000:      6F0000:      661B00:      5F3100:      554D00:      2F5800:      106000:      006500:      006500:      006500:      006011:      005733:      004D5E:      00296B:      000E75:      00007B:      100075:      38323E:      "
  , "55085F:      650428:      6F0000:      6D0500:      6B0D00:      622A0A:      5D3802:      554D17:      385500:      255B05:      006500:      006500:      006500:      055B28:      015640:      154D5E:      003368:      05216F:      00007B:      25046F:      2E2933:      "
  , "39053E:      480008:      4A0000:      4A0000:      470800:      450F00:      3E2501:      393300:      273901:      084100:      004300:      004200:      004300:      004108:      01392B:      00333F:      012247:      00074F:      000051:      08004F:      252129:      "
  , "39003F:      4A0000:      4A0000:      4A0000:      4A0000:      490000:      411A00:      393300:      173D00:      004300:      004300:      004300:      004300:      004300:      003D19:      00333F:      001449:      000052:      000052:      000052:      1C191F:      "
  , "55005E:      6A0011:      6F0000:      6F0000:      6F0000:      661B00:      612900:      554D00:      255B00:      106000:      006500:      006400:dgn   006500:      006011:      005B29:      004D5E:      00206F:      000E75:      00008B:dbl2  100075:      131115:      "
  , "39003F:      4A0000:      4A0000:      4A0000:      4A0000:      490000:      450F00:      393300:      084000:      004300:      004300:      004300:      004300:      004300:      004009:      00333F:      00064F:      000052:      000080:dbl1  000052:      0A090B:      "
  };
  String[] longNames =
  { "nw=NavajoWhite"
  , "Gold=Gold-css"
  , "or=Orange"
  , "dor=DarkOrange"
  , "rd=Read"
  , "bl=blue"
  , "sbl=Blue"
  , "dcy=DarkCyan"
  , "dcy1=DarkCyan1"
  , "gn=Green"
  , "teal=Teal"
  , "dgn=DarkGreen"
  , "dbl2=DarkBlue"
  , "dbl1=Navy"
  };
  
  Map<String, ColorWithField> idxColorsByShortname = new TreeMap<String, ColorWithField>();
  
  
  
  //GralTextField[][] wdgColorBack = new GralTextField[19][10];

  //String[][] shortname = new String[19][10];
  
  //String[][] name = new String[19][10];
  
  final ColorWithField[][] colorF = new ColorWithField[colSatB.length][colHue.length + 7];
  
  
  ColorWithField colorFocus;
  
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
    int ix1, ix2, ix3;  //Index of first, second and third color.
    float[] rgb = new float[3];
    float nrd,ngn, nbl;    
    float ncol2;  //part of the second color in respect to the main color.
    //float nlight; //factor for the light depending on the color. 
    float nlight = CurveInterpolation.splineInterpolation(hue, clight, -1);
    if(sat < 0.5f) {
      nlight = 1; // + (nlight -1) * 5*sat;
    }
    float nOutshine = CurveInterpolation.splineInterpolation(hue, cfOutshine, -1);
    if(hue < 4){
      ix1 = 0; ix2 = 2; ix3 = 1;  //rd, bl, gn
      ncol2 = (4 - hue) / 4;  //factor second color 0..0.999.
      //nlight = 1.3f - 0.1f* ncol2;  //1.2 on pure magenta
    } else if(hue < 8.0){
      ix1 = 0; ix2 = 1; ix3 = 2;
      ncol2 = (hue -4 ) / 4;  //factor second color 0..0.999.
      //ncol2 = (float)Math.cos(Math.PI/2*(8 - hue) / 4);  //factor second color 0..0.999.
      //nlight = 1.3f - 0.3f* ncol2;  //1.0 on pure yellow
    } else if(hue < 12){
      ix1 = 1; ix2 = 0; ix3 = 2;
      ncol2 = (12 - hue) / 4;  //factor second color 0..0.999.
      //ncol2 = (float)Math.cos(Math.PI/2*(hue-8) / 4);  //factor second color 0..0.999.
      //nlight = 1.1f - 0.1f* ncol2;  //1.0 on pure yellow
    } else if(hue < 16) {
      ix1 = 1; ix2 = 2; ix3 = 0;
      ncol2 = (hue -12 ) / 4;  //factor second color 0..0.999.
      //nlight = 1.1f - 0.1f* ncol2;  //1.0 on pure cyan
    } else if(hue < 20) {
      ix1 = 2; ix2 = 1; ix3 = 0;
      ncol2 = (20 - hue) / 4;  //factor second color 0..0.999.
      //nlight = 1.5f - 0.5f* ncol2;  //1.1 on pure cyan
    } else { //20..24
      ix1 = 2; ix2 = 0; ix3 = 1;
      ncol2 = (hue -20 ) / 4;  //factor second color 0..0.999.
      //nlight = 1.5f - 0.3f* ncol2;  //1.2 on pure magenta
    }
    float lightmax = light * nlight;
    //float darkadd = 0;
    float lightbase = light * (1 - sat);
    if(hue == 7.0f && light == 1.1f && sat == 1.0f)
      Debugutil.stop();
    if(true){
      rgb[ix1] = lightmax;
      if(rgb[ix1] > 1) {
        //float light1 = 1/nlight;  //the value where lightmax == 1,0.7 for red
        float lightadd = 1.5f * nlight -1; //limit of light input
                 // - lig //part other colors from saturation
                 // - 1/nlight    //the value where lightmax == 1,0.7 for red
                 // ;
        //float lightmaxmax = 1.5f * nlight;
        float darkadd = (rgb[ix1] -1) / lightadd * (1-lightbase);
                  ;
        rgb[ix1] = 1;          
        float col2 = ncol2 + lightbase;
        col2 += darkadd * ( 1- col2);
        rgb[ix2] = col2; //ncol2 + lightbase + darkadd;
        rgb[ix3] = lightbase + darkadd;  //basic, gray
      } else {
        rgb[ix1] = lightmax;
        rgb[ix2] = (lightmax - lightbase) * ncol2 + lightbase;
        rgb[ix3] = lightbase;  //basic, gray
      }
    } else {
      float darkadd = 0;
      if(lightmax > 1) {
        //lightmax = 1 + (lightmax -1)*sat;
        darkadd = nOutshine * (lightmax -1);
        lightmax = 1;
      }  
      lightbase = light * (1.0f - darkadd) * (1.0f - sat);
      float dark1 = (1.0f - lightmax); // * cfrgb[ix1];  //light value max. 255               |dddd......
      float dark2 = (1.0f - lightmax) + (1.0f - ncol2) * (lightmax - lightbase);//   |ddddDD....
      rgb[ix1] = 1.0f - dark1;
      rgb[ix2] = 1.0f - dark2 + darkadd;
      rgb[ix3] = lightbase + darkadd;  //basic, gray
    }  
    
    //rgb[ix2] = rgb[ix1] - (rgb[ix1] - rgb[ix3]) * (1.0f - ncol2);
    int rd = (int)(rgb[0] * 255 + 0.5f);
    int gn = (int)(rgb[1] * 255 + 0.5f);
    int bl = (int)(rgb[2] * 255 + 0.5f);
    /*
    nrd *= sat * cfrd;
    ngn *= sat * cfgn;
    nbl *= sat * cfbl;
    
    float ncomax = Math.max(nrd, Math.max(ngn, nbl));
    int base = (int)(light * (1.0f - ncomax) * 255); 
    //int base = (int)((1.0f - sat)*255 + (sat / light )*255  + 0.5f); 
    float rd, gn, bl;
    boolean corr;
    int maxcnt = 5;
    do {
      corr = false;
      if(hue >= 24){ hue -= 24; }
      float ncolor = 255; //(255 - base) * light;
      rd = base + nrd * ncolor;
      gn = base + ngn * ncolor;
      bl = base + nbl * ncolor;
      if(rd >= 255){ base += (rd - 255) /2; corr = true; }
      if(gn >= 255){ base += (gn - 255) /2; corr = true; }
      if(bl >= 255){ base += (bl - 255) /2; corr = true; }
    } while(corr && --maxcnt >=0);
    if(maxcnt <0){
      Debugutil.stop();
    }
    */
    if(rd < 0){ 
      rd = 0; }
    if(rd > 255){ 
      rd = 255; }
    if(gn < 0){ 
      gn = 0; }
    if(gn > 255){ 
      gn = 255; }
    if(bl < 0){ 
      bl = 0; }
    if(bl > 255){ 
      bl = 255; }
    //Test
    float hue2 = RGBtoHue((((int)rd << 16) & 0xff0000) | (((int)gn <<8) & 0x00ff00) | ((int)bl & 0xff));
    if(Math.abs(hue-hue2) > 0.1f)
      Debugutil.stop();
    float l1 = RGBtoligth(rd, gn, bl, sat);
    //
    return (((int)rd) << 16) + (((int)gn) << 8) + (((int)bl));
  }
  
  
  void genDefaultConfig() {
    int colorVal;
    for(int ixHue = 0; ixHue < colHue.length; ++ixHue){
      //StringBuilder line = new StringBuilder(1000);
      for(int ixSatB = 0; ixSatB < colSatB.length; ++ixSatB){
        float s = colSatB[ixSatB][2*(ixHue & 1) + 0];
        float b = colSatB[ixSatB][2*(ixHue & 1) + 1]; // * colHue[ixHue][1];
        //if(b > 1.0f){ s -= b -1.0f; b = 1.0f; }
        //colorVal = HSBtoRGB(colHue[ixHue][0], s, b) & 0xffffff;
        if(ixHue == 3 && ixSatB == 1)
          Debugutil.stop();
        colorVal = HSLtoRGB(colHue[ixHue][0], s, b) & 0xffffff;
        ColorWithField colorF1 = new ColorWithField("", ixHue, ixSatB, colorVal);
        colorF[ixSatB][ixHue] = colorF1;
        setColorFromRGB(colorF1);
        setColorT(colorF1);
        ///
        //line.append(String.format("%06X", colorVal)).append(":       ");
      }
      colorF[0][ixHue].wdgColor.setText(""+colHue[ixHue][0]);
      //colorVal = HSBtoRGB(0,0, 1.0f - ((float)ixHue) / colHue.length) & 0xffffff;
      //val[ixHue] = line.toString();
    }
    float[][] clineSat1 = { {1.0f, 0.05f}, {0,0.25f}};
    float[][] clineSat2 = { {1.0f, 0.1f}, {0,0.49f}};
    float[][] clineSatC1 = { {0,     0   ,   3  , 10   , 12   , 24   }
                           , {0.0f, 0.2f , 0.35f, 0.35f, 0.15f, 0.25f}
                           , {1.1f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f}};
    float[][] clineSatC2 = { {0,     0   ,   3  , 10   , 12   , 24   }
                           , {0.0f, 0.40f, 0.49f, 0.49f, 0.30f, 0.30f}
                           , {1.1f, 0.10f, 0.10f, 0.10f, 0.10f, 0.10f}};
    for(int ixsat = 0; ixsat < 2; ++ixsat){
      float[][] clinesat = ixsat ==0 ? clineSatC1 : clineSatC2;
      for(int ixHue=0; ixHue < 7; ++ixHue){
        for(int ixSatB = 0; ixSatB < colSatB.length /2; ++ixSatB){
          if(ixsat == 0 && ixHue == 1 && ixSatB == 5)
            Debugutil.stop();
          int ixline = ixSatB + colSatB.length/2 * ixsat;
          if(ixline == 7)
            Debugutil.stop();
          float color, sat; 
          float light = 1.0f - ((float)ixSatB) / colSatB.length * 2;
          if(ixHue == 0) {
            color = 8; sat = 0;
          } else {
            color = 4*(ixHue-1);
            if(ixHue == 5 && ixSatB == 10)
              Debugutil.stop();
            sat = CurveInterpolation.linearInterpolation(light, color, clinesat, -1);
            //sat = 0.05f * (ixsat+1);// + 0.9f * (1-light) * (1-light);
          }
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
    for(int ixSatB = 0; ixSatB < colSatB.length; ++ixSatB){
      colorF[ixSatB][colHue.length].wdgColor.setText("" + ixSatB);
      colorF[ixSatB][0].wdgColor.setText("" + colSatB[ixSatB][0]);
      colorF[ixSatB][1].wdgColor.setText("" + colSatB[ixSatB][1]);
    }    
    //val[colHue.length] = line.toString();
  }
  
  
  
  
  
  void readConfig()
  { 
    String[] val = valTest;
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
        ColorWithField colorF1 = idxColorsByShortname.get(longname.substring(0, psep).trim());
        if(colorF1 == null) {
          System.err.println("GralColor: faulty short name in long name entry; " + longname);
        } else {
          colorF1.name = longname.substring(psep+1);
        }
      } else {
        System.err.println("GralColor: faulty longname entry; " + longname);
      }
    }
    
  }
  
  
  
  
  void outColors(){
    System.out.append("  String[] val =\n");
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
          System.out.append(sep).append(colorF[line][col].shortname).append('=').append(colorF[line][col].name).append("\"\n");
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

  
  
  void setColorEditFields() {
    String sHex = String.format("%06X", colorFocus.rgb);
    wdgHexValue.setText(sHex); //Integer.toHexString(colValue));
    wdgShortname.setText(colorFocus.shortname);
    wdgName.setText(colorFocus.name);
    float hue1 = colorFocus.hsb[0]; //24 * colorFocus.hsb[0] +4;
    //if(hue1 >= 24.0f){ hue1 -= 24; }
    wdgHue.setText(""+hue1);
    wdgSat.setText(""+colorFocus.hsb[1]);
    wdgBright.setText(""+colorFocus.hsb[2]);
    float col2 = 24 * colorFocus.hsl[0] +4;
    if(col2 >=24.0f) col2 -= 24.0f;
    wdgHue2.setText(""+colorFocus.hsl[0]);
    wdgSat2.setText(""+colorFocus.hsl[1]);
    wdgLight2.setText(""+colorFocus.hsl[2]);
    GralColor color = GralColor.getColor(colorFocus.rgb);
    if(testText) {
      wdgTest.setTextColor(color);
      wdgTest.setLineColor(color, 0);
    } else {
      wdgTest.setBackColor(color,0);
    }
        
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
      wdgSat = new GralTextField("name");
      wdgSat.setEditable(true);
      wdgSat.setActionChange(new ActionEnterHSB(1));
      wdgSat.setToPanel(gralMng);
      wdgBright = new GralTextField("name");
      wdgBright.setEditable(true);
      wdgBright.setActionChange(new ActionEnterHSB(2));
      wdgBright.setToPanel(gralMng);
      gralMng.setPosition(GralPos.refer +3, GralPos.size +2, 40, GralPos.size+12, 0, 'r',1);
      wdgHue2 = new GralTextField("name");
      wdgHue2.setEditable(true);
      wdgHue2.setActionChange(new ActionEnterHSL(0));
      wdgHue2.setToPanel(gralMng);
      wdgSat2 = new GralTextField("name");
      wdgSat2.setEditable(true);
      wdgSat2.setActionChange(new ActionEnterHSL(1));
      wdgSat2.setToPanel(gralMng);
      wdgLight2 = new GralTextField("name");
      wdgLight2.setEditable(true);
      wdgLight2.setActionChange(new ActionEnterHSL(2));
      wdgLight2.setToPanel(gralMng);
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
  
  
  
  GralUserAction actionFocusColor = new GralUserAction("focus color"){
    @Override public boolean exec(int key, GralWidget_ifc widgP, Object... params)
    {
      if(key == (KeyCode.focusGained)){
        GralTextField wdg = (GralTextField)widgP;
        if(colorFocus !=null) {
          colorFocus.wdgColor.setBorderWidth(0);
          colorFocus = (ColorWithField)widgP.getData();
          colorFocus.wdgColor.setBorderWidth(3);
          //int colValue = color.getColorValue();
          setColorFromRGB(colorFocus);
          setColorEditFields();
          //System.out.println("focus");
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
        setColorEditFields();
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
      } else if(key == KeyCode.left){ 
        int ixCol = colorFocus.index.ixCol -1;
        if(ixCol < 0) { ixCol = colorF[0].length -1; }
        ColorWithField newField = colorF[colorFocus.index.ixLight][ixCol];
        newField.wdgColor.setFocus();
      } else if(key == KeyCode.up && colorFocus.index.ixLight > 0){ 
        ColorWithField newField = colorF[colorFocus.index.ixLight-1][colorFocus.index.ixCol];
        newField.wdgColor.setFocus();
      } else if(key == KeyCode.dn && colorFocus.index.ixLight < colorF.length -1){ 
        ColorWithField newField = colorF[colorFocus.index.ixLight+1][colorFocus.index.ixCol];
        newField.wdgColor.setFocus();
      } else if(key == KeyCode.mouse2Down) {
        ColorWithField colorF2 = (ColorWithField)widgP.getData();
        colorF2.rgb = colorFocus.rgb;  //copy from last focused.
        colorF2.wdgColor.setBackColor(colorFocus.wdgColor.getBackColor(0),0);
        colorF2.shortname = colorFocus.shortname;
        colorF2.name = colorFocus.name;
        setColorFromRGB(colorF2);
      } else if(key == (KeyCode.shift + KeyCode.pgup)){
        int rd = (colorFocus.rgb >>16) & 0xff;
        if(rd <255) rd +=1;
        colorFocus.rgb = (colorFocus.rgb & 0x00ffff) | rd <<16;
        setColorFromRGB(colorFocus);
        setColorT(colorFocus);
        setColorEditFields();
      } else if(key == (KeyCode.shift + KeyCode.left)){
        int rd = (colorFocus.rgb >>16) & 0xff;
        if(rd >0) rd -=1;
        colorFocus.rgb = (colorFocus.rgb & 0x00ffff) | rd <<16;
        setColorFromRGB(colorFocus);
        setColorT(colorFocus);
        setColorEditFields();
      } else if(key == (KeyCode.shift + KeyCode.up)){
        int gn = (colorFocus.rgb >>8) & 0xff;
        if(gn <255) gn +=1;
        colorFocus.rgb = (colorFocus.rgb & 0xff00ff) | gn <<8;
        setColorFromRGB(colorFocus);
        setColorT(colorFocus);
        setColorEditFields();
      } else if(key == (KeyCode.shift + KeyCode.dn)){
        int gn = (colorFocus.rgb >>8) & 0xff;
        if(gn >0) gn -=1;
        colorFocus.rgb = (colorFocus.rgb & 0xff00ff) | gn <<8;
        setColorFromRGB(colorFocus);
        setColorT(colorFocus);
        setColorEditFields();
      } else if(key == (KeyCode.shift + KeyCode.pgdn)){  //key of a notebook: 
        int bl = (colorFocus.rgb) & 0xff;
        if(bl <255) bl +=1;
        colorFocus.rgb = (colorFocus.rgb & 0xffff00) | bl;
        setColorFromRGB(colorFocus);
        setColorT(colorFocus);
        setColorEditFields();
      } else if(key == (KeyCode.shift + KeyCode.right)){
        int bl = (colorFocus.rgb) & 0xff;
        if(bl >0) bl -=1;
        colorFocus.rgb = (colorFocus.rgb & 0xffff00) | bl;
        setColorFromRGB(colorFocus);
        setColorT(colorFocus);
        setColorEditFields();
      } else if(key == KeyCode.enter){
        setColorFromRGB(colorFocus);
      } else if(key == (KeyCode.focusGained)){ //only if it is an edit field
        actionFocusColor.exec(key, widgP);
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
        setColorEditFields();
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
        setColorEditFields();
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

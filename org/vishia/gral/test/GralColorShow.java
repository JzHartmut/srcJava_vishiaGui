//==JZcmd==
//JZcmd main(){ java org.vishia.gral.test.GralColorShow.main(); }
//==endJZcmd==
package org.vishia.gral.test;
import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.swt.SwtFactory;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.KeyCode;
import org.vishia.util.StringPart;
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
    float[] hsb = new float[3];
    String shortname, name;
    final GralTextField wdgColor;
    
    ColorWithField(String shortname, int ixCol, int ixBright, int colValue) {
      this.index = new Index();
      this.index.ixCol = ixCol; index.ixLight = ixBright;
      this.color = GralColor.getColor(colValue);
      this.shortname = shortname;
      this.name = "";
      this.wdgColor = new GralTextField("" + ixCol + "," + ixBright); //, GralTextField.Type.editable);
      this.wdgColor.setBackColor(this.color, 0);
      this.wdgColor.setData(this);
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
    if(val == null) genDefaultConfig();
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
  
  
  
  
  float[][] colHue = 
  { { 0.8333333f, 1.0f } //pu
  , { 0.9f,       0.95f }
  , { 0.0f,       0.9f } //rd
  , { 0.07f,      0.85f }
  , { 0.10f,      0.8f } //or
  , { 0.1333333f, 0.75f } //am
  , { 0.1666667f, 0.7f } //ye
  , { 0.19f,      0.72f }
  , { 0.24f,      0.75f }
  , { 0.29f,      0.77f }
  , { 0.3333333f, 0.8f } //gn
  , { 0.40f,      0.8f }
  , { 0.45f,      0.8f }
  , { 0.5f,       0.8f }  //cy
  , { 0.5333333f, 0.9f }
  , { 0.60f,      0.95f }
  , { 0.6666667f, 1.0f }  //bl
  , { 0.75f,      1.0f }  //vi
  };
  
  
  float[][] colSatB =
  { { 0.20f, 1.30f }
  , { 0.35f, 1.30f }
  , { 0.60f, 1.30f }
  , { 0.80f, 1.30f }
  , { 1.00f, 1.20f }
  , { 1.00f, 1.10f }
  , { 1.00f, 1.00f }
  , { 0.70f, 0.90f }
  
  , { 1.00f, 0.90f }
  , { 0.80f, 0.80f }
  
  , { 1.00f, 0.80f }
  , { 0.80f, 0.70f }
  
  , { 1.00f, 0.70f }
  , { 0.80f, 0.60f }
  
  , { 1.00f, 0.60f }
  , { 0.80f, 0.50f }
  
  , { 1.00f, 0.50f }
  , { 0.80f, 0.40f }

  , { 1.00f, 0.40f }
  , { 0.80f, 0.30f }

  
  , { 0.60f, 0.70f }
  , { 0.50f, 0.60f }

  , { 0.60f, 0.60f }
  , { 0.50f, 0.50f }

  , { 0.60f, 0.50f }
  , { 0.50f, 0.40f }

  , { 0.60f, 0.40f }
  , { 0.50f, 0.30f }

  , { 0.60f, 0.30f }
  , { 0.50f, 0.20f }


  , { 0.60f, 0.20f }
  , { 0.50f, 0.20f }

  
  , { 0.40f, 0.30f }
  , { 0.30f, 0.40f }
  , { 0.20f, 0.55f }
  , { 0.10f, 0.70f }
 
  };
  
  
  String[] val;
  
  String[] XXval =
  { "FFE0FF:pma   FFC0FF:lma   FF80FF:bma   FF40FF:ma    E000E0:sma   C000C0:      A000A0:      800080:      600060:      400040:      "
  , "FFD8E8:ppu   FFC0E0:lpu   FF80E0:      FF4080:pu    F000A0:spu   E00080:      C00080:      B03070:      A04060:      803050:      "
  , "FFE8E8:prd   FFD0D0:lrd   FFA0A0:brd   FF8080:rd    FF0000:srd   E67373:r5090 CC6666:r5080 CC0000:r0080 B35959:r0570 996B6B:r0360 "
  , "FFE8E0:      FFD0C0:      FF9070:      FF7040:      FF6B00:      FF974D:      E67373:      CCA18F:      CCAFA3:      602000:      "
  , "FFF0E0:por   FFE0C0:lor   FFC070:      FFA000:or    FF9900:sor   E06000:      E6B873:      CCB48F:      CCBCA3:      503000:      "
  , "FFF0D0:pam   FCF0B0:lam   FFE070:bam   FFC000:am    E0A000:sam   C09000:      A06000:      A08060:      A07020:      403000:      "
  , "FFFFC0:pye   FFFF90:lye   FFFF60:bye   FFFF00:ye    E0E000:sye   A0A000:dye   808000:      606000:      606040:      404000:      "
  , "FFFFD0:p     FFFFA0:l     E0FF70:      E0FC00:      C0E000:s     90B000:      608000:      507020:      406030:      304000:      "
  , "F4FFE0:plm   F0FF80:llm   C0FF70:blm   A0FC00:lm    80C040:slm   80B000:      6B8E23:od    408000:      406020:      203000:      "
  , "F0FFF0:p     E0FFE0:l     A0FF70:      40FC00:      30B000:s     40B000:      208800:      307010:      406020:      104000:      "
  , "E0FFE0:pgn   C0FFC0:lgn   80FF80:bgn   00FF00:gn    00D000:sgn   00B000:dgn   009000:      228B22:fgn   007000:      004000:      "
  , "C0FFE0:psg   C0FFD0:lsg   70FFA0:      00FC60:sg    00A040:ssg   00A050:      2E8B57:sgn   208020:      305020:      204010:      "
  , "E0FFF0:p     B0FFF0:l     70FFE0:      00F4A0:      00A080:s     00A070:      008040:      208060:      307050:      305050:      "
  , "E0FFFF:pcy   B0FFFF:lcy   00FFFF:bcy   00F0F0:cy    00A0A0:scy   00A0A0:      008080:      006060:      004040:      004040:      "
  , "FFFFFF:p     B0F0FF:l     70F0FF:      20E0FF:      0090E0:s     0090E0:      0060A0:      406080:      003040:      409090:      "
  , "F0F0FF:p     C0D0FF:l     70C0FF:      40A0FC:      2040FF:s     0040F0:      0000E0:      4040F0:      002080:      103070:      "
  , "F0F0FF:pbl   D0D0FF:lbl   B0B0FF:      A0A0FF:bl    4040FF:sbl   0000FF:dbl   0000A0:      000080:      000040:      000040:      "
  , "FCF0FF:pvi   FAE4FF:lvi   E0C0FF:      E060FF:vi    9020FF:svi   8000F0:      4000E0:      6030D0:      6020B0:      000000:      "
  , "FFFFFF:wh    E0E0E0:lgr   C0C0C0:      A0A0A0:gr    808080:sgr   606060:      404040:      202020:      000000:      000000:      "
  };

  String[] longNames =
  { "pma=pastel magenta"
  , "lma=light magenta"
  , "bl=blue"
  };

  
  
  Map<String, ColorWithField> idxColorsByShortname = new TreeMap<String, ColorWithField>();
  
  
  
  //GralTextField[][] wdgColorBack = new GralTextField[19][10];

  //String[][] shortname = new String[19][10];
  
  //String[][] name = new String[19][10];
  
  ColorWithField[][] colorF = new ColorWithField[19][10];
  
  
  ColorWithField colorFocus;
  
  GralTextField wdgTest, wdgTest1, wdgTest2;
  
  GralTextField wdgHexValue, wdgHue, wdgSat, wdgBright, wdgShortname, wdgName;
  
  boolean testText, testLine;
  
  GralColor colWh = GralColor.getColor("wh");  
  GralColor colBk = GralColor.getColor("bk");
  //GralFont fontText = GralFont.fontMonospacedSansSerif;
  
  
  float[] colweight = { 1.0f, 0.95f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.2f, 0}; 
  
  float[] colrd = { 1.0f, 0.9f, 0.8f, 0.7f, 0.3f, 0.2f, 0.1f, 0}; 
  
  float[] colbl = { 1.0f, 0.9f, 0.8f, 0.7f, 0.3f, 0.2f, 0.1f, 0}; 
  
  
  float[] lineWeight = { 3.0f, 2.7f, 2.3f, 1.9f, 1.3f, 1.0f, 0.7f, 0.5f, 0.3f, 0.1f, 0};
  
  int[] lngn = { 0xff, 0xff, 0xe2, 0xd8, 0xc8, 0xb0, 0x80, 0x40, 0        ,0,0}; 
  
  int[] lnrd = { 0xff, 0xff, 0xff, 0xff, 0xe8, 0xd0, 0xc0, 0x80, 0x40        ,0,0}; 
  
  int[] lnbl = { 0xff, 0xff, 0xff, 0xff, 0xe8, 0xd0, 0xc0, 0x80, 0x40        ,0,0}; 
  
  
  int ixCol(int col, int col0){
    int ix1 = col0 - col;
    if(ix1 <-8){ ix1 +=24; }
    if(ix1 >=8){ ix1-= 24;}
    ix1 = Math.abs(ix1);
    if(ix1 > 8){ ix1 = 8; }
    return ix1;
  }
  
  int genColor(int line, int col){
    int ixgn = ixCol(col, 12);
    int ixrd = ixCol(col, 4);
    int ixbl = ixCol(col, 20);
    int[] rdgnbl = new int[3];
    rdgnbl[0] = (int)(colweight[ixrd] * lineWeight[line] * 0xff); //lnrd[line]);
    rdgnbl[1] = (int)(colweight[ixgn] * lineWeight[line] * 0xff); //lngn[line]);
    rdgnbl[2] = (int)(colweight[ixbl] * lineWeight[line] * 0xff); //lnbl[line]);
    for(int ixColor1 =0; ixColor1 <3; ++ixColor1){
      int ixColor0 = ixColor1 +1; if(ixColor0>=3){ ixColor0 -=3;}
      int ixColor2 = ixColor1 -1; if(ixColor2<0){ ixColor2 +=3;}
      
      if(rdgnbl[ixColor1] > 0xff){
        float f = rdgnbl[ixColor1] / 0xff;
        rdgnbl[ixColor1] = 0xff;
        rdgnbl[ixColor0] = (int)(rdgnbl[ixColor0] * f);  //same ratio
        rdgnbl[ixColor2] = (int)(rdgnbl[ixColor2] * f);
        
        /*
        float m = (rdgnbl[ixColor1] - 0xff)/512; //max. 1.0
        rdgnbl[ixColor1] = 0xff;
        rdgnbl[ixColor0] += (int)((0xff - rdgnbl[ixColor0]) * m);  //Erhöhung Richtung 0xff
        rdgnbl[ixColor2] += (int)((0xff - rdgnbl[ixColor2]) * m);
        */
        /*
        rdgnbl[ixColor0] += m;
        if(rdgnbl[ixColor0] > 0xff){
          rdgnbl[ixColor0] = 0xff; }
        rdgnbl[ixColor2] += m;
        if(rdgnbl[ixColor2] > 0xff){
          rdgnbl[ixColor2] = 0xff; }
        */
      }
    }
    return (rdgnbl[0] <<16) + (rdgnbl[1]<<8) + rdgnbl[2];
  }
  
  
  
  void genColor1(){
    //java.awt.Color.HSBtoRGB(hue, saturation, brightness);
  }
  
  
  void t1(){
    //java.awt.Color.RGBtoHSB(r, g, b, hsbvals);
  }
  
  
  void testGetColor(){
    int col =0;
    for(int ixcol = 0; ixcol < 24; ++ixcol){
      int col1 = genColor(0, ixcol);
      col |= col1;
    }
  }
  
  
  
  void genDefaultConfig() {
    val = new String[colHue.length +1];  //gray too!
    int colorVal;
    for(int ixHue = 0; ixHue < colHue.length; ++ixHue){
      StringBuilder line = new StringBuilder(1000);
      for(int ixSatB = 0; ixSatB < colSatB.length; ++ixSatB){
        float b = colSatB[ixSatB][1] * colHue[ixHue][1];
        if(b > 1.0f){ b = 1.0f; }
        colorVal = java.awt.Color.HSBtoRGB(colHue[ixHue][0], colSatB[ixSatB][0], b) & 0xffffff;
        line.append(String.format("%06X", colorVal)).append(":       ");
      }
      colorVal = java.awt.Color.HSBtoRGB(0,0, 1.0f - ((float)ixHue) / colHue.length) & 0xffffff;
      line.append(String.format("%06X", colorVal)).append(":       ");
      val[ixHue] = line.toString();
    }
    StringBuilder line = new StringBuilder(1000);
    for(int ixSatB = 0; ixSatB < colSatB.length; ++ixSatB){
      colorVal = java.awt.Color.HSBtoRGB(0,0, 1.0f - ((float)ixSatB) / colSatB.length) & 0xffffff;
      line.append(String.format("%06X", colorVal)).append(":       ");
    }
    val[colHue.length] = line.toString();
    colorF = new  ColorWithField[colHue.length +1][colSatB.length];
  }
  
  
  
  
  
  void readConfig()
  { 
    for(int ixCol=0; ixCol<colorF.length; ++ixCol) {
      String line = val[ixCol];
      StringPartScan spline = new StringPartScan(line);
      for(int ixBright = 0; ixBright < colorF[0].length; ++ixBright){
        GralColor colText;
        if(ixBright < 5){ colText = colBk; }
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
        ColorWithField colorF1 = new ColorWithField(shortname, ixCol, ixBright, col2);
        colorF[ixCol][ixBright] = colorF1;
        java.awt.Color.RGBtoHSB((col2>>16) & 0xff, (col2>>8) & 0xff, (col2) & 0xff, colorF1.hsb);
        colorF1.wdgColor.setActionFocused(actionFocusColor);
        colorF1.wdgColor.setActionChange(actionEditColor);
        colorF1.wdgColor.setTextColor(colText);
        if(colorF1.shortname.length() >0) {
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
  
  
  GralGraphicTimeOrder initGraphic = new GralGraphicTimeOrder("GralArea9Window.initGraphic"){
    @Override public void executeOrder()
    {
      gralMng.setPosition(4, -2, 2, -2, 0, 'd');
      //gralMng.addTextField();
      for(int ixCol=0; ixCol<colorF.length; ++ixCol) {
        gralMng.setPosition(4, GralPos.size -3, 5*ixCol +1, GralPos.size+5, 0, 'd', 0);
        //gralMng.setPosition(3, GralPos.size -2, 9*col, GralPos.size+8, 0, 'd', 1);
        //System.out.append(", \"");
        //int pline = 0;
        for(int ixBright = 0; ixBright < colorF[0].length; ++ixBright){
          GralColor colText;
          if(ixBright < 5){ colText = colBk; }
          else { colText = colWh; }
          colorF[ixCol][ixBright].wdgColor.setToPanel(gralMng);
          colorF[ixCol][ixBright].wdgColor.setTextColor(colText);
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
        System.out.append(spaces.substring(0, 6 - colorF1.shortname.length()));
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
  

  
  GralUserAction actionFocusColor = new GralUserAction("focus color"){
    @Override public boolean exec(int key, GralWidget_ifc widgP, Object... params)
    {
      if(key == (KeyCode.focusGained)){
        GralTextField wdg = (GralTextField)widgP;
        colorFocus.wdgColor.setBorderWidth(0);
        colorFocus = (ColorWithField)widgP.getData();
        colorFocus.wdgColor.setBorderWidth(3);
        GralColor color = wdg.getBackColor(0);
        int colValue = color.getColorValue();
        String sHex = String.format("%06X", colValue);
        wdgHexValue.setText(sHex); //Integer.toHexString(colValue));
        wdgShortname.setText(colorFocus.shortname);
        wdgName.setText(colorFocus.name);
        wdgHue.setText(""+colorFocus.hsb[0]);
        wdgSat.setText(""+colorFocus.hsb[1]);
        wdgBright.setText(""+colorFocus.hsb[2]);
        if(testText) {
          wdgTest.setTextColor(color);
          wdgTest.setLineColor(color, 0);
        } else {
          wdgTest.setBackColor(color,0);
        }
      }
      return true;
  } };
  
  
  
  GralUserAction actionEditColor = new GralUserAction("edit color"){
    @Override public boolean exec(int key, GralWidget_ifc widgP, Object... params)
    {
      GralTextField wdg = (GralTextField)widgP;
      if(key == KeyCode.right) {
        int ixCol = colorFocus.index.ixCol +1;
        if(ixCol >= colorF.length) { ixCol = 0; }
        ColorWithField newField = colorF[ixCol][colorFocus.index.ixLight];
        newField.wdgColor.setFocus();
      } else if(key == KeyCode.left){ 
        int ixCol = colorFocus.index.ixCol -1;
        if(ixCol < 0) { ixCol = colorF.length -1; }
        ColorWithField newField = colorF[ixCol][colorFocus.index.ixLight];
        newField.wdgColor.setFocus();
      } else if(key == KeyCode.up && colorFocus.index.ixLight > 0){ 
        ColorWithField newField = colorF[colorFocus.index.ixCol][colorFocus.index.ixLight-1];
        newField.wdgColor.setFocus();
      } else if(key == KeyCode.dn && colorFocus.index.ixLight < colorF[0].length -1){ 
        ColorWithField newField = colorF[colorFocus.index.ixCol][colorFocus.index.ixLight+1];
        newField.wdgColor.setFocus();
      } else if(key == KeyCode.enter){
        String text = wdg.getText().substring(0, 6);
        int valColor = Integer.parseInt(text, 16);
        GralColor color = new GralColor(valColor);
        wdg.setBackColor(color,0);
      } else if(key == (KeyCode.focusGained)){
        colorFocus.wdgColor.setBorderWidth(0);
        colorFocus = (ColorWithField)widgP.getData();
        colorFocus.wdgColor.setBorderWidth(3);
        GralColor color = wdg.getBackColor(0);
        int colValue = color.getColorValue();
        String sHex = String.format("%06X", colValue);
        wdgHexValue.setText(sHex); //Integer.toHexString(colValue));
        wdgShortname.setText(colorFocus.shortname);
        wdgName.setText(colorFocus.name);
        if(testText) {
          wdgTest.setTextColor(color);
          wdgTest.setLineColor(color, 0);
        } else {
          wdgTest.setBackColor(color,0);
        }
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
          int colorValue = Integer.parseInt(text, 16);
          GralColor colorBack = GralColor.getColor(colorValue);
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

  
  void setNewColorFromHSB() {
    int colorValue = java.awt.Color.HSBtoRGB(colorFocus.hsb[0], colorFocus.hsb[1], colorFocus.hsb[2]);
    String sHex = String.format("%06X", colorValue);
    wdgHexValue.setText(sHex);
    colorFocus.color = GralColor.getColor(colorValue);
    colorFocus.wdgColor.setBackColor(colorFocus.color, 0);
  }
  
  GralUserAction actionEnterHue = new GralUserAction("actionEnterHue") {
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
        setNewColorFromHSB();
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
          if(value > 1.0f){ value = 1.0f; }
          if(value < 0.0f){ value = 0.0f; }
        } catch(NumberFormatException exc){ value = 0; }  //red
        colorFocus.hsb[indexHSB] = value;
        setNewColorFromHSB();
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

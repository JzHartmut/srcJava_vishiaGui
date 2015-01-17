package org.vishia.gral.test;

import org.vishia.gral.awt.AwtFactory;
import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFactory_ifc;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.swt.SwtFactory;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.KeyCode;


public class GralColorShow
{
  GralMng gralMng;
  
  public static void main(String[] args){
    GralColorShow main = new GralColorShow();
    main.execute();
    //main.testGetColor();
  }
  
  private void execute(){
    GralFactory_ifc gralFactory = new SwtFactory();
    LogMessage log = new LogMessageStream(System.out);
    GralWindow wind = gralFactory.createWindow(log, "Show Colors", 'B', 150, 10,800, 700);
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
  
  
  
  String[] val =  
  { "ma FFFFFF FFC0FF FF80FF FF00FF e000e0 c000c0 a000a0 800080 600060 400040 404040"
  , "   FF0000 FFC0F0 FF80F0 FF00e0 e800d0 d000c0 a000c0 FFFFFF 503050 FFFFFF FFFFFF"
  , "   FFFFFF FFC0E0 FF80E0 FF00a0 F000a0 e00080 c00080 FFFFFF FFFFFF FFFFFF FFFFFF"
  , "   FFFFFF FFC0d0 FF80C0 FF0060 F80060 F00040 d80040 FFFFFF FFFFFF FFFFFF FFFFFF"
  , "rd FFe0e0 FFC0C0 FFa0a0 FF6060 FF0000 F00000 e00000 c00000 a00000 700000 400000"
  , "   FFe8e0 FFD0c0 FF9070 FC9000 F03000 F03000 d81000 FFFFFF FFFFFF 602000 FFFFFF"
  , "   FFf0e0 FFE0c0 FFc070 F8c000 e06000 e06000 c03000 FFFFFF FFFFFF 503000 FFFFFF"
  , "   FFF8e0 FcF0b0 FFe070 F4e000 a08000 c09000 a06000 FFFFFF FFFFFF 403000 FFFFFF"
  , "ye FFFFa0 FFFF00 F0F000 e0e000 808000 a0a000 808000 606000 606040 404000 404000"
  , "   FFFFd0 FFFF80 e0FF70 C0F400 708000 90b000 608000 FFFFFF FFFFFF 304000 FFFFFF"
  , "wh FFFFFF f0FF80 c0FF70 80F800 609000 80b000 408000 FFFFFF FFFFFF 203000 FFFFFF"
  , "   F0FFF0 e0FFe0 a0FF70 40FC00 40a000 40b000 208800 FFFFFF FFFFFF 104000 FFFFFF"
  , "gn e0ffe0 c0FFc0 00FF00 00e000 00a000 00b000 009000 008000 007000 004000 000000"
  , "   c0FFe0 c0FFd0 70FFa0 00FC40 00a040 00A050 008820 208020 305020 204010 FFFFFF"
  , "   c0FFf0 b0FFe0 70FFc0 00F880 00a070 40a060 008840 208030 408050 004020 FFFFFF"
  , "   e0FFF0 b0FFf0 70FFe0 00F4c0 00a080 00a070 008040 208060 FFFFFF 305050 FFFFFF"
  , "cy d0FFFF b0FFFF 00FFFF 00F0F0 00a0a0 00a0a0 008080 006060 004040 004040 003030"
  , "   FFFFFF b0f0FF 70f0FF 00e0F4 0090E0 0090e0 0060a0 406080 003040 409090 FFFFFF"
  , "   FFFFFF b0e0FF 70e0FF 00c0F8 1060F0 0060F0 0030c0 4060e0 003060 FFFFFF FFFFFF"
  , "   F0F0FF c0d0FF 70c0FF 0090FC 2040FF 0040F0 0000e0 4040f0 002080 FFFFFF FFFFFF"
  , "bl e0e0FF c0c0FF a0a0FF 6060FF 4040FF 0000FF 0000c0 0000a0 000080 000040 000040"
  , "   FFFFFF d0c0FF C080FF C000FF 7040FF 4000FF 2000ec 4000d8 4000c0 FFFFFF FFFFFF"
  , "   FFFFFF E0c0FF E080FF E000FF 9020FF 8000F0 4000e0 6030d0 6020b0 FFFFFF FFFFFF"
  , "   FFFFFF F0c0FF F080FF F000FF c000F0 c000d0 6000d0 8050a0 7060a0 505090 400050"
};
  
  

  GralTextField[][] color = new GralTextField[24][11];

  GralTextField wdgTest1, wdgTest2;
  
  
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
  
  
  void testGetColor(){
    int col =0;
    for(int ixcol = 0; ixcol < 24; ++ixcol){
      int col1 = genColor(0, ixcol);
      col |= col1;
    }
  }
  
  GralGraphicTimeOrder initGraphic = new GralGraphicTimeOrder("GralArea9Window.initGraphic"){
    @Override public void executeOrder()
    {
      gralMng.setPosition(4, -2, 2, -2, 0, 'd');
      //gralMng.addTextField();
      for(int line=0; line<color.length; ++line) {
        gralMng.setPosition(3*line+3, GralPos.size -2, 1, GralPos.size+8, 0, 'r', 1);
        //gralMng.setPosition(3, GralPos.size -2, 9*col, GralPos.size+8, 0, 'd', 1);
        //System.out.append(", \"");
        for(int col = 0; col < color[0].length; ++col){
          GralColor colText;
          if(col < 4){ colText = colBk; }
          else { colText = colWh; }
          String col1 = val[line].substring(7*col+3, 7*col+9);
          //System.out.append(' ').append(col1);
          int col2 = Integer.parseInt(col1, 16);
          GralColor col3 = new GralColor(col2);
          color[line][col] = new GralTextField("" + line + "," + col, GralTextField.Type.editable);
          color[line][col].setActionChange(actionEditColor);
          color[line][col].setToPanel(gralMng);
          //color[line][col].setTextStyle(colText, font);
          color[line][col].setText(col1);
          color[line][col].setTextColor(colText);
          color[line][col].setBackColor(col3, 0);
        }
        //System.out.append("\"\n");
      }
      
      gralMng.setPosition(GralPos.refer+4, GralPos.size +10, 1, GralPos.size+15, 0, 'r');
      //
      wdgTest1 = new GralTextField("test1");
      wdgTest2 = new GralTextField("test2");
      wdgTest1.setToPanel(gralMng);
      wdgTest2.setToPanel(gralMng);
      wdgTest1.setText("ABC");
      wdgTest2.setText("XYZ");
      countExecution();
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
    for(int line=0; line<color.length; ++line) {
      //System.out.append(", \"");
      for(int col = 0; col < color[0].length; ++col){
        String sVal = color[line][col].getText();
        System.out.append(' ').append(sVal);
      }
      System.out.append("\n");
    }
  }
  
  
  GralUserAction actionEditColor = new GralUserAction("edit color"){
    @Override public boolean exec(int key, GralWidget_ifc widgP, Object... params)
    {
      GralTextField wdg = (GralTextField)widgP;
      if(key == KeyCode.enter){
        String text = wdg.getText();
        int valColor = Integer.parseInt(text, 16);
        GralColor color = new GralColor(valColor);
        wdg.setBackColor(color,0);
      } else if(key == (KeyCode.focusGained)){
        GralColor color = wdg.getBackColor(0);
        wdgTest1.setBackColor(color,0);
      } else if(key == ('1' + KeyCode.ctrl)){
        GralColor color = wdg.getBackColor(0);
        wdgTest1.setBackColor(color,0);
      } else if(key == ('2' + KeyCode.ctrl)){
        GralColor color = wdg.getBackColor(0);
        wdgTest2.setBackColor(color,0);
      } else if(key == (KeyCode.enter + KeyCode.ctrl)){
        outColors();
      }
      return true;
    }
  };
  
  
  
}

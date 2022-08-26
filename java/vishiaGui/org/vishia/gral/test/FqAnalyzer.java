package org.vishia.gral.test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.widget.GralPlotArea;


public class FqAnalyzer
{

  public static void main(String args[]){
    FqAnalyzer main = new FqAnalyzer();
    //main.genWave();
    main.test();
    System.out.println("success");
  }
  
  
  static float[] fOktave = {55.0f, 110.0f, 220.0f, 440.0f, 880.0f, 1760.0f, 3520.0f};
  static float[] fTon  = new float[24]; 

  static float fm = (float)(Math.pow(2.0, 1.0/24)); 
  
  static float tStep = 1.0f/44000.0f;
  
  float intg[][] = new float[fOktave.length*24][2];
  float magn[] = new float[fOktave.length*24];
  
  float fI[] = new float[fOktave.length*24];
  
  float[] wave = new float[16384];
  
  
  short[] audioBuffer = new short[92000];
  
  
  char[] mc = new char[magn.length];

  String title = "            |A B H C # D # E F # G # A B H c # d # e f # g # a b h c # d # e f # g # a b h c # d # e f # g # a b h c # d # e f # g # a b h c # d # e f # g # a b h c\n";
  
  public FqAnalyzer() {
    float f = 1.0f;
    for(int ton=0; ton<24; ++ton){
      fTon[ton] = f;
      f *= fm;
    }  
    for(int iOktave=0; iOktave<fOktave.length; ++iOktave){
      int ix = iOktave * 24;
      for(int ton=0; ton<24; ++ton){
        float fq = fOktave[iOktave] * fTon[ton];
        float fI1 = tStep * (2.0f * (float)Math.PI * fq); 
        fI[ix] = fI1;
        ix +=1;
      } 
      
    }
  }
  
  void genWave(){
    float w1 = 0; float w2 = 0; float w3 = 0;
    for(int ix = 0; ix < wave.length; ++ix){
      w1 += Math.PI / 100.0;
      w2 += Math.PI / 150.0f;
      w3 += Math.PI / 5.0f;
      if(w1 > Math.PI){ w1 -= 2*Math.PI; }
      if(w2 > Math.PI){ w2 -= 2*Math.PI; }
      if(w3 > Math.PI){ w3 -= 2*Math.PI; }
      float val = (float)(3.5f * Math.sin(w1) + 2.0f * Math.sin(w2) + 3.0f * Math.sin(w3));
      wave[ix] = val;
    }
  }

  
  float step(float val){
    float kA = 0.01f; 
    float kB = -0.01f;
    float kM = 0.1f;
    float dw = val;
    for(int ix=0; ix < intg.length; ++ix) {
      dw -= intg[ix][0];
    }
    for(int ix=0; ix < intg.length; ++ix) {
      intg[ix][0] += dw * kA - intg[ix][1]*fI[ix];
      intg[ix][1] += dw * kB + intg[ix][0]*fI[ix];
      magn[ix] += Math.sqrt(intg[ix][0] * intg[ix][0] + intg[ix][1] * intg[ix][1]);
      //magn[ix] += kM * (intg[ix][0] * intg[ix][0] + intg[ix][1] * intg[ix][1] - magn[ix]* magn[ix] );
    }
    return dw;
  }
  
  
  String wrc = " .-+x#";

  //float[] wrv = {0.05f, 0.1f, 0.4f, 1.0f, 9999f};
  float[] wrv = {25.0f, 100.0f, 400.0f, 1600.0f, 9999f};
  
  
  void writedebugLine(Writer wr, int ix, float dw) {
    //write a line for any magnitude of tones.
    for(int im=0; im < intg.length; ++im) {
      int iv = 0;
      while( magn[im] >= wrv[iv]) iv+=1;
      mc[im] = wrc.charAt(iv);
    }
    String dws = Float.toString(dw);
    if(dws.length() >10){ dws = dws.substring(0, 10); }
    String spaces = "            |";
    String dwa = spaces.substring(dws.length());
    if(wr !=null) { 
      try{ 
        if(ix % 80 == 0) {
          wr.append(title);
        }
        wr.append(dws).append(dwa).append(new String(mc)).append('\n');
      } catch(IOException exc){} 
    }
    
  }
  
  
  void test(){
    
    //WaveFileUtils.fillBuffer(audioBuffer, "t:\\tmp\\--\\piano_g3.wav");
    graphicInit();
    float dw;
    Writer wr = null;
    try{ wr = new FileWriter("t:/tmp/FqAnalyter.txt");}
    catch(IOException exc){System.err.println(exc.getMessage()); }
    for(int ix = 0; ix < audioBuffer.length; ++ix){
      float val = audioBuffer[ix] * 0.0001f;
      dw = step(val);
      writedebugLine(wr, ix, dw); 
      plotStep(ix);
    }
    try{ wr.close(); } catch(IOException exc){}
  }
  
  GralPlotArea plot;
  GralPlotArea.UserUnits scaling;
  GralColor color = org.vishia.gral.ifc.GralColor.getColor("red");
  
  void graphicInit() {
    GralPlotWindow wind = org.vishia.gral.test.GralPlotWindow.create("Test CurveInterpolation");
    plot = wind.canvas();
    scaling = plot.userUnitsPerGrid(0, 0, 1.0f, 1.0f);
  //JZcmd   plot.drawLine(color, scaling, result, 1);
  //JZcmd   
  }
  
  
  void plotStep(int ix){
    if(ix % 100 == 0){
      float[][] points = new float[2][2];
      points[0][0] = ix/100 -1;
      points[0][1] = ix/100;
      points[1][0] = ix/100;
      points[1][1] = ix/100;
      
      plot.drawLine(color, scaling, points, 1);  
      
    }
    
  
  }
  
  
  
  
}

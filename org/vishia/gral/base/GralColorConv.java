package org.vishia.gral.base;

import org.vishia.math.CurveInterpolation;
import org.vishia.util.Debugutil;

public class GralColorConv
{
  
  
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
  
  

  
  /**Converts a color given by hue, light and sat to rgb.
   * @param hue The color in range 0..24.0. 0 is magenta, 4: red, 8 yellow, 12 green 16 cyan, 20 blue, 24 magenta.
   * @param light The light, 0 to 2.0. 1.0 is the saturated red 0xff0000 or a darker yellow. 1.4 is yellow 0xffff00.
   *   till 2.0 it uses the base values for color in white direction. 2.0 it is white.
   * @param sat Saturation 0..1. part of gray. 1.0 is non-gray. 0 is full gray.
   * @return rgb
   */
  public static int HLStoRGB(float hue, float light, float sat){  ////
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
      //if the brightness of HSB system is >1 yet, decrease the saturation to force that brigthness
      float satCorr = sat1 * (light - nlight) / (2 - nlight);
      sat1 = sat1 - satCorr;
      b1 = 1;
    }
    return java.awt.Color.HSBtoRGB(hue1, sat1, b1);
  }
  
  
  public static void HSBtoHLS(float hue, float sat, float brightness, float[] hls) {
    hls[0] = hue;
    float nlight1 = CurveInterpolation.linearInterpolation(hue, clight, -1);
    float nlightsat = CurveInterpolation.splineInterpolation(sat, clightSat, -1);
    float nlight;
    if(brightness <= 1/nlight1) {
      nlight = 1 + (nlight1 - 1) * nlightsat;
    } else {
      nlight = nlight1;
    }
    float light = brightness * nlight; 
    float s1 = brightness * (1-sat);  //part of saturation which does not increase the light.
    float s2 = (sat +  s1);     //increast the satuaration by the part which is add to the light. 1.0 if all is add to light.
    hls[1] = light + (2 - light) * s1;
    //float s2 = (1 - brightness) * sat;
    //hls[2] = 1- ((1 - sat) - s1);  //same:
    hls[2] = s2;
  }
  
  

  public static void RGBtoHLS(int rgb, float[] hls) {
    float[] hsb = new float[3];
    java.awt.Color.RGBtoHSB((rgb>>16)&0xff, (rgb>>8)&0xff, (rgb)&0xff, hsb);
    float hue = 24*hsb[0] +4; 
    if(hue >=24){ hue -=24; }
    HSBtoHLS(hue, hsb[1], hsb[2], hls);
    //float sat = satFromRGB(rgb);
    //hls[2] = sat;
  }
  
  
  
  static void XXXRGBtoHSL(int rgb, float[] hsl){
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
  

  
  public static float satFromRGB(int rgb){
    int rd = (rgb>>16) & 0xff;
    int gn = (rgb>>8) & 0xff;
    int bl = rgb & 0xff;
    float ret;
    int lighti;
    int hi, mi, lo;
    if(rd >= gn && rd >= bl){
      hi = rd;
      if(gn > bl){ mi = gn; lo = bl; }
      else { mi = bl; lo = gn; }
    } else if( gn >= rd && gn >= bl){
      hi = gn;
      if(rd > bl){ mi = rd; lo = bl; }
      else { mi = bl; lo = rd; }
    } else {
      hi = bl;
      if(rd > gn){ mi = rd; lo = gn; }
      else { mi = gn; lo = rd; }
    }
    int add = 255 - hi;
    if(add > lo){ add = lo; }
    //lo -=add; mi -=add; hi +=add;
    ret = hi > lo ? ((float)(hi - mi))/(hi - lo) : 0;
    //ret = hi == 0 ? 0 : lo == 0 ? 1: ((float)hi - lo)/hi * 1- ((255.0f - hi) / lo * (float)lo / hi);
    //ret = lo == 255 ? 0 : 1.0f - (255.0f - hi) / (255 - lo) * lo/hi;
    //ret = hi == 0 ? 0 : 1 - ((float)(mi - lo))/hi; 
    /*
    if(lo > (255 - hi)){
      
    }
    if(ladd )
    
    
    //float mid;
    if(hval == 255) {
      ret = 1.0f; //white or paster color is not gray.
    } else if(hval == lval) {
      ret = 0; //gray
    } else {
      if(l)
      ret = lval 255 - hval
      ret = (float)(hval - lval) / hval;
    }
    */
    return ret;
  }
  
  
  
  
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
  
  

  



  public static String htmlhls(float h, int l, int s) {
    int color =  HLStoRGB(h, l/100.0f, s/100.0f) & 0xffffff;
    String ret = String.format("#%06X", color);
    return ret;
  }
  
  public static String htmlhlb(float h, int l, int s) {
    float hue1 = (h-4) / 24;
    if(hue1 < 0 ){ hue1 += 1.0f; }
    int color =  java.awt.Color.HSBtoRGB(hue1, s/100.0f, l/100.0f) & 0xffffff;
    String ret = String.format("#%06X", color);
    return ret;
  }
  
  
}

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
  
  /**Light value in depending of hex*/
  static float[][] clightVal = 
  { 
    { 0, 0 }  
  , { 0x20, 0.3f } 
  , { 0x40, 0.5f } 
  , { 0x60, 0.65f } 
  , { 0x80, 0.75f } 
  , { 0xa0, 1.0f } 
  , { 0xc0, 1.2f } 
  , { 0xe0, 1.5f } 
  , { 0xff, 2.0f }                           
  };
  
  
  static float[][] loTable =
  { { 0,   0,    4,    5,    7,    8,    12,   16,   18,   19,   20,   21,   24   }
  , {0.0f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }
  , {0.2f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }
  , {0.5f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }
  , {0.8f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x30, 0x00, 0x00 }
  , {1.0f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x20, 0x60, 0x40, 0x00 }
  , {1.3f, 0x40, 0x50, 0x20, 0x00, 0x00, 0x00, 0x00, 0x30, 0x40, 0x70, 0x70, 0x50 }
  , {1.5f, 0x60, 0x80, 0x40, 0x00, 0x00, 0x20, 0x00, 0x60, 0x70, 0x80, 0x70, 0x60 }
  , {2.0f, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff }
  };
  
  
  static float[][] grayTable =
  { {0.0f, 0x00 }
  , {0.2f, 0x20 }
  , {0.5f, 0x50 }
  , {1.0f, 0x8a }
  , {1.3f, 0xb0 }
  , {1.5f, 0xc0 }
  , {2.0f, 0xff }
  };
  
  
  static float[][] hiTable =
  { { 0,   0,    4,    5,    7,    8,    12,   16,   18,   19,   20,   21,   24   }
  , {0.0f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }
  , {0.2f, 0x40, 0x50, 0x40, 0x38, 0x38, 0x40, 0x30, 0x50, 0x50, 0x70, 0x50, 0x50 }
  , {0.5f, 0x98, 0xa6, 0x98, 0x70, 0x68, 0x70, 0x68, 0x80, 0xc0, 0xe0, 0xc0, 0xa6 }
  , {0.8f, 0xc0, 0xe0, 0xc0, 0xa0, 0x90, 0x9c, 0x90, 0xb0, 0xff, 0xff, 0xc0, 0xa6 }
  , {1.0f, 0xe0, 0xff, 0xe8, 0xc0, 0xb0, 0xc4, 0xb0, 0xd0, 0xff, 0xff, 0xff, 0xff }
  , {1.3f, 0xff, 0xff, 0xff, 0xf0, 0xe0, 0xff, 0xd0, 0xf0, 0xff, 0xff, 0xff, 0xff }
  , {1.5f, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff }
  , {2.0f, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff }
  };
  
  
  
  public static int HLStoRGB(float hue, float light, float sat){  ////
    float lo = CurveInterpolation.linearInterpolation(light, hue, loTable, -1);
    float gr = CurveInterpolation.linearInterpolation(light, grayTable, -1);
    float hi = CurveInterpolation.linearInterpolation(light, hue, hiTable, -1);
    //either lo should be 0 or hi should be 0xff, it is full saturation:
    if(hi < 0xff && lo !=0){ 
      hi += lo/4; lo = 0;
    }
    if(lo > 0 && hi !=0xff){ 
      lo -= (0xff - hi); hi = 0xff; 
    }
    if(lo < 0){ 
      hi -= lo; lo = 0; 
    }
    if(hi > 0xff){ 
      lo += hi - 0xff; hi = 0xff; 
    }
    //calculate saturation:
    float sat1 = sat;
    if(lo > 0){ //light color
      lo = gr - sat1 * (gr - lo);  //between gr (sat = 0) and lo (sat = 1) 
      hi = gr + sat1 * (hi - gr);
    } else { //dark color
      lo = gr - sat1 * (gr - lo);  //between gr (sat = 0) and lo (sat = 1) 
      hi = gr + sat1 * (hi - gr);
    }

    float diff = hi - lo;
    int rd, gn, bl;
    float fc;
    switch((int)(hue / 4)){
      case 0: fc = (4 -hue)/4;  gn = (int)lo; bl = (int)(lo +  diff * fc + 0.5f); rd = (int)(hi); break; //0..4, rt, --bl
      case 1: fc = (hue- 4)/4;  bl = (int)lo; gn = (int)(lo +  diff * fc + 0.5f); rd = (int)(hi); break; //4..8  rt, ++gn
      case 2: fc = (12-hue)/4;  bl = (int)lo; rd = (int)(lo +  diff * fc + 0.5f); gn = (int)(hi); break; //8..12 gn, --rt
      case 3: fc = (hue-12)/4;  rd = (int)lo; bl = (int)(lo +  diff * fc + 0.5f); gn = (int)(hi); break; //12..16 gn, ++bl
      case 4: fc = (20 -hue)/4; rd = (int)lo; gn = (int)(lo +  diff * fc + 0.5f); bl = (int)(hi); break; //16..20 bl, --gn
      case 5: fc = (hue-20)/4;  gn = (int)lo; rd = (int)(lo +  diff * fc + 0.5f); bl = (int)(hi); break; //20..24 bl, ++rt
      default: rd = 0x80; gn = 0; bl = 0x80;
    }
    return ((rd << 16) & 0xff0000) + ((gn << 8) & 0x00ff00) + (bl & 0x0000ff);
  }
  
  /**Converts a color given by hue, light and sat to rgb.
   * @param hue The color in range 0..24.0. 0 is magenta, 4: red, 8 yellow, 12 green 16 cyan, 20 blue, 24 magenta.
   * @param light The light, 0 to 2.0. 1.0 is the saturated red 0xff0000 or a darker yellow. 1.4 is yellow 0xffff00.
   *   till 2.0 it uses the base values for color in white direction. 2.0 it is white.
   * @param sat Saturation 0..1. part of gray. 1.0 is non-gray. 0 is full gray.
   * @return rgb
   */
  public static int HLStoRGB3(float hue, float light, float sat){  ////

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
    float satLight = 1.0f;
    if(b1 > 1){
      //if the brightness of HSB system is >1 yet, decrease the saturation to force that brigthness
      //float satCorr = sat1 * (light - nlight) / (2 - nlight);
      //sat1 = sat1 - satCorr;
      float satCorr = (light - nlight) / (2 - nlight);
      satLight = 1.0f - satCorr;
      b1 = 1;
    }
    int rgb = java.awt.Color.HSBtoRGB(hue1, satLight, b1);
    float[] rgb1 = new float[3];
    float rd = (rgb>>16) & 0xff;
    float gn = (rgb>>8) & 0xff;
    float bl = rgb & 0xff;
    float hi = rd, lo = rd;
    if(gn > hi){ hi = gn; }
    if(bl > hi){ hi = bl; }
    if(gn < lo){ lo = gn; }
    if(bl < lo){ lo = bl; }
    if(lo > 0){ //light color
      float lightValue = (5 * hi + lo)/6;
      lo = lightValue - sat1 * (lightValue - lo);  //between lightValue (sat = 0) and lo (sat = 1) 
      rd = lightValue + sat1*(rd - lightValue);
      gn = lightValue + sat1*(gn - lightValue);
      bl = lightValue + sat1*(bl - lightValue);
    } else { //dark color
      //lo = 0, given sat < 1 increased lo
      rd = hi - sat1 * (hi - rd); 
      gn = hi - sat1 * (hi - gn); 
      bl = hi - sat1 * (hi - bl); 
    }
    rgb = (((int)(rd + 0.5f)) << 16) + (((int)(gn + 0.5f)) << 8) +((int)(bl + 0.5f)); 
    return rgb;
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
    float light = ligthFromRGB(rgb);
    hls[1] = light;
    float sat = satFromRGB(rgb);
    hls[2] = sat;
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
    ret = hi > lo ? ((float)(hi - mi))/(hi - lo) : 0;
    ////
    if(lo == 255 || hi == 0) {
      ret = 1.0f;
    }
    else if(255-hi < lo){
      ret = (float)(hi - lo) / (255 - lo);  //colorvalue 0xff contained: sat = 1.0
    } else { // if(hi > 0){
      ret = (float)(hi-lo) / hi;  //same value like HSB-satuartion.
    }
    return ret;
  }
  
  
  
  public static float ligthFromRGB(int rgb){
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
    float ff = hi == 0 ? 1: ((float)hi) / (hi + 0.4f*mi + 0.2f*lo );
    ////
    float rd1 = CurveInterpolation.linearInterpolation(rd, clightVal, -1);
    float gn1 = CurveInterpolation.linearInterpolation(gn, clightVal, -1);
    float bl1 = CurveInterpolation.linearInterpolation(bl, clightVal, -1);
    //return (0.55f * rd + gn + 0.26f * bl) /255  *ff;
    return (rd + (1 + 0.4f * (gn-0.6f*rd)/hi) * gn + (1 - 0.4f * (bl-0.5f*gn)/hi) * bl) /255 *ff;
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

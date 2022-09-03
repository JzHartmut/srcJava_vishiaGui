package org.vishia.gral.cfg;

import org.vishia.gral.base.GralPos;

/**ZBNF: position::= ... ;
 * Class for instance to capture and store the position in an element. */
public final class GralCfgPosition implements Cloneable
{
  /**Version and history
   * <ul>
   * </ul>
   *
   * <b>Copyright/Copyleft</b>:<br>
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
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final int version = 20120303;

  
  public String panel;
  public boolean yPosRelative;
  public int yPos = -1, yPosFrac;
  public int ySizeDown, ySizeFrac;
  boolean yIncr_;
  
  public boolean xPosRelative;
  public int xPos = -1, xPosFrac;
  public int xWidth, xSizeFrac;
  boolean xIncr_ = false;
  
  public void set_xIncr(){ xIncr_ = true; yIncr_ = false; }
  public void set_yIncr(){ yIncr_ = true; xIncr_ = false; }
  public void set_xOwnSize(){ xWidth = GralPos.useNatSize; } //Integer.MAX_VALUE; }
  public void set_yOwnSize(){ ySizeDown = GralPos.useNatSize; } //Integer.MAX_VALUE; }
  
  @Override protected GralCfgPosition clone()
  { GralCfgPosition clone = null;
    try{ clone = (GralCfgPosition)super.clone(); } 
    catch(CloneNotSupportedException exc){ assert(false); }
    return clone;
  }
  
  /**Sets all data from src. It is similar as {@link #clone()} but it uses a given instance.
   * @param src of data
   */
  public void set(GralCfgPosition src){
    panel = src.panel;
    yPosRelative = src.yPosRelative;
    yPos = src.yPos;
    yPosFrac = src.yPosFrac;
    ySizeDown = src.ySizeDown;
    ySizeFrac = src.ySizeFrac;
    yIncr_ = src.yIncr_;
    xPosRelative = src.xPosRelative;
    xPos = src.xPos;
    xPosFrac = src.xPosFrac;
    xWidth = src.xWidth;
    xSizeFrac = src.xSizeFrac;
    xIncr_ = src.yIncr_;
    
  }
  
  
  /**Sets a position element. It is able to call from a configuration input or gui input.
   * @param what use y, x, h, w for pos-y, pos-x, height, width. All other chars causes an IllegalArgumentException.
   * @param sVal String given Value in ZBNF-syntax-form ::=[< ?posRelative> &+]< #?val>[ \. <#?frac> ]. Fault inputs causes return false.
   *        It should not have leeding or trailing spaces! Trim outside. 
   *        It is admissible that the string is empty, then no action is done.
   * @return true on success. False if the sVal contains numberFormat errors. True on empty sVal
   */
  public boolean setPosElement(char what, String sVal)
  { boolean ok = true;
    final int val; final int frac;
    if(sVal.length() >0){
      boolean posRelativ = sVal.charAt(0)=='&';
      int pos1 = posRelativ ? 1: 0;
      if(sVal.charAt(pos1) == '+'){
        pos1 +=1;   //skip over a '+', it disturbs Integer.parseInt
      }
      int posPoint = sVal.indexOf('.');
      try{
        if(posPoint >=0){
          val = Integer.parseInt(sVal.substring(pos1, posPoint));   
          frac = Integer.parseInt(sVal.substring(posPoint +1));   
        } else {
          val = Integer.parseInt(sVal.substring(pos1));
          frac = 0;
        }
        switch(what){
          case 'y': yPos = val; yPosFrac = frac; yPosRelative = posRelativ; break;
          case 'x': xPos = val; xPosFrac = frac; xPosRelative = posRelativ; break;
          case 'h': ySizeDown = val; ySizeFrac = frac; break;
          case 'w': xWidth = val; xSizeFrac = frac; break;
        }
      } catch(NumberFormatException exc){ ok = false; }
    }
    return ok;
  }
  
  
  public String toString = null;  //filled on first invocation, can be set to null to refill

  @Override public String toString() {
    if(false && this.toString !=null) { return this.toString; }
    else {
      StringBuilder sb = new StringBuilder();
      if(this.panel !=null) { sb.append(this.panel.toString()).append(":"); }
      sb.append(this.yPos).append('.').append(this.yPosFrac).append(',').append(this.xPos).append('.').append(this.xPosFrac);
      return this.toString = sb.toString();
    }
  }
  
}

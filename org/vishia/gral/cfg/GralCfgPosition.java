package org.vishia.gral.cfg;

/**ZBNF: position::= ... ;
 * Class for instance to capture and store the position in an element. */
public final class GralCfgPosition implements Cloneable
{
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
  public void set_xOwnSize(){ xWidth = Integer.MAX_VALUE; }
  public void set_yOwnSize(){ ySizeDown = Integer.MAX_VALUE; }
  
  protected GralCfgPosition clone()
  { GralCfgPosition clone = null;
    try{ clone = (GralCfgPosition)super.clone(); } 
    catch(CloneNotSupportedException exc){ assert(false); }
    return clone;
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
  

  
}
package org.vishia.gral.base;


import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralTextFieldUser_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.util.CalculatorExpr;

/**This is the base class for all widgets which represents a simple text.
 * @author Hartmut Schorrig
 *
 */
public abstract class GralTextField extends GralWidget implements GralTextField_ifc
{
  /**Version, history and license.
   * <ul>
   * <li>2012-09-24 Hartmut new: {@link #setValue(double)} and {@link #setValue(Object[])} for double values. 
   * <li>2012-04-17 Hartmut new: {@link #sFormat2} etc: Now format "int16AngleDegree" etc. are recognized
   *   as special format. Improved calling {@link #calculator} in {@link #setValue(float)}.
   * <li>2012-04-16 Hartmut new: {@link #isChanged()}, {@link #setUser(GralTextFieldUser_ifc)}
   * <li>2012-04-01 Hartmut new: {@link #setValue(float)} now supports formatting view.
   *   This algorithm was used for the {@link org.vishia.guiInspc.InspcGui} before.
   * <li>2011-11-18 Hartmut new {@link #setMouseAction(GralUserAction)}. This method should be 
   * an abstract method of all {@link GralWidget} but it is used yet only here.
   * <li>2011-09-00 Hartmut Creation to build a platform-indenpenden representation of text field. 
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
   * <li> But the LPGL ist not appropriate for a whole software product,
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
  @SuppressWarnings("hiding")
  public final static int version = 20120416;
  
  protected String text = "";
  
  protected boolean bTextChanged;
  
  protected int caretPos;
  
  //protected GralColor colorBack = GralColor.getColor("wh"), colorText = GralColor.getColor("bk");
  
  protected GralFont fontText;
  
  /**A calculator to show calculated float values. It is null if it isn't used. */
  private CalculatorExpr calculator;

  private String sFormat2;
  
  protected GralTextFieldUser_ifc user;
  
  /**It is used for some operations. */
  protected final GralGraphicThread windowMng;
  
  
  
  public GralTextField(String name, char whatis, GralMng mng){
    super(name, whatis, mng);
    setBackColor(GralColor.getColor("wh"),0);
    setTextColor(GralColor.getColor("bk"));
    this.windowMng = mng.gralDevice;
  }
  
  
  void setUser(GralTextFieldUser_ifc user){
    this.user = user;
  }
  
  
  
  @Override public void setFormat(String sFormat){
    super.setFormat(sFormat);
    calculator = null;  //set it on first usage.
    sFormat2 = null;
  }
  
  
  
  
  /**Sets a float value into a text field.
   * The float value may be formatted:
   * <ul>
   * <li>If the {@link #setFormat(String)} of this widget starts with '!' and contains a second '!',
   *   the String between that is used as expression to calculate the float value
   *   using {@link CalculatorExpr}. Therewith any calculation can be done.
   * <li>The rest after the "!expression!" or the given format is used for String.format(sFormat, value)
   *   to determine the output appearance.
   * <li>If no format is given, the value will be shown in proper readable appearance. That assures
   *   that the value is able to present in a short text field, using maximal 9 digits. 
   * <li>If no format is given and the absolute of the value is less 0.000001 but not 0, 
   *   a "0.000001" with the correct sign will be shown. It shows, there is a less value, but not null.
   * <li>If no format is given and the value is in range up to 1 Billion, it is shown with "k", "M"
   *   for kilo and Mega with max 3 digits before dot and 3 digits after the dot.
   * <li>if no format is given and the value is greater than 1 Billion, it is shown with exponent.
   * <ul>      
   *   
   * @see org.vishia.gral.base.GralWidget#setValue(float)
   */
  @Override public void setValue(final float valueP){
    final String sFormat1;
    float value;
    String sShow;
    if(calculator !=null){
      value = calculator.calc(valueP);
      sFormat1 = this.sFormat2;
    } else if(sFormat !=null){
      if(sFormat.startsWith("!")){
        int posEnd = sFormat.indexOf('!',1);
        if(posEnd >=0){
          String sExpr = sFormat.substring(1, posEnd);
          this.sFormat2 = sFormat1 = sFormat.substring(posEnd+1);
          if(calculator ==null){
            calculator = new CalculatorExpr();
            String sError = calculator.setExpr(sExpr);
            if(sError !=null){ 
              //console.writeError(sError);
              calculator = null;
            }
          }
          if(calculator !=null){
            value = calculator.calc(valueP);
          } else {
            value = valueP;
          }
        } else {
          sFormat1 = sFormat;
          value = valueP;
        }
      } else if(sFormat.startsWith("int16AngleDegree")){
        this.calculator = new CalculatorAngle16();
        sFormat1 = this.sFormat2 = "%3.3f";
        value = calculator.calc(valueP);
      } else if(sFormat.startsWith("int32AngleDegree")){
        this.calculator = new CalculatorAngle32();
        sFormat1 = this.sFormat2 = "%3.3f";
        value = calculator.calc(valueP);
      } else {
        sFormat1 = sFormat;
        value = valueP;
      }
        
    } else { //sFormat == null
      sFormat1 = null;  //no expression
      value = valueP;
    }
    if(sFormat1 !=null && sFormat.length() >0){
      try{ sShow = String.format(sFormat1, value); }
      catch(java.util.IllegalFormatException exc){ 
        sShow = null;  //maybe integer 
      }
      if(sShow == null){
        try{ sShow = String.format(sFormat, (int)value); }
        catch(java.util.IllegalFormatException exc){ 
          sShow = "?format";  
        }
      }
    } else { //no format given
      float valueAbs = Math.abs(value); 
      if(value == 0.0f){ sShow = "0.0"; }
      else if(valueAbs < 1.0e-7f){ sShow = value < 0 ? "-0.0000001" : "0.0000001"; }  //shorten output, don't show exponent.
      else if(valueAbs < 1.0f){ sShow = String.format("%1.7f", value); }  //shorten output, don't show exponent.
      else if(valueAbs < 1.0e3f){ sShow = String.format("%3.4f", value); }  //shorten output, don't show exponent.
      else if(valueAbs < 1.0e6f){ sShow = String.format("%3.3f k", value/1000); }  //shorten output, don't show exponent.
      else if(valueAbs < 1.0e9f){ sShow = String.format("%3.3f M", value/1000000); }  //shorten output, don't show exponent.
      else if(valueAbs >= 1.0e9f){ sShow = String.format("%3.3g", value); }  //shorten output, don't show exponent.
      //else if(valueAbs >= 1e6){ sShow = Float.toString(value/1000000) + " M"; }  //shorten output, don't show exponent.
      else { sShow = Float.toString(value); }
      
    }
    setText(sShow);
  }
  
  
  
  /**Sets a float value into a text field.
   * The float value may be formatted:
   * <ul>
   * <li>If the {@link #setFormat(String)} of this widget starts with '!' and contains a second '!',
   *   the String between that is used as expression to calculate the float value
   *   using {@link CalculatorExpr}. Therewith any calculation can be done.
   * <li>The rest after the "!expression!" or the given format is used for String.format(sFormat, value)
   *   to determine the output appearance.
   * <li>If no format is given, the value will be shown in proper readable appearance. That assures
   *   that the value is able to present in a short text field, using maximal 9 digits. 
   * <li>If no format is given and the absolute of the value is less 0.000001 but not 0, 
   *   a "0.000001" with the correct sign will be shown. It shows, there is a less value, but not null.
   * <li>If no format is given and the value is in range up to 1 Billion, it is shown with "k", "M"
   *   for kilo and Mega with max 3 digits before dot and 3 digits after the dot.
   * <li>if no format is given and the value is greater than 1 Billion, it is shown with exponent.
   * <ul>      
   *   
   * @see org.vishia.gral.base.GralWidget#setValue(float)
   */
  public void setValue(final double valueP){
    final String sFormat1;
    double value;
    String sShow;
    if(calculator !=null){
      value = calculator.calc(valueP);
      sFormat1 = this.sFormat2;
    } else if(sFormat !=null){
      if(sFormat.startsWith("!")){
        int posEnd = sFormat.indexOf('!',1);
        if(posEnd >=0){
          String sExpr = sFormat.substring(1, posEnd);
          this.sFormat2 = sFormat1 = sFormat.substring(posEnd+1);
          if(calculator ==null){
            calculator = new CalculatorExpr();
            String sError = calculator.setExpr(sExpr);
            if(sError !=null){ 
              //console.writeError(sError);
              calculator = null;
            }
          }
          if(calculator !=null){
            value = calculator.calc(valueP);
          } else {
            value = valueP;
          }
        } else {
          sFormat1 = sFormat;
          value = valueP;
        }
      } else {
        sFormat1 = sFormat;
        value = valueP;
      }
        
    } else { //sFormat == null
      sFormat1 = null;  //no expression
      value = valueP;
    }
    if(sFormat1 !=null && sFormat.length() >0){
      try{ sShow = String.format(sFormat1, value); }
      catch(java.util.IllegalFormatException exc){ 
        sShow = null;  //maybe integer 
      }
      if(sShow == null){
        try{ sShow = String.format(sFormat, (int)value); }
        catch(java.util.IllegalFormatException exc){ 
          sShow = "?format";  
        }
      }
    } else { //no format given
      double valueAbs = Math.abs(value); 
      if(value == 0.0f){ sShow = "0.0"; }
      else if(valueAbs < 1.0e-7f){ sShow = value < 0 ? "-0.0000001" : "0.0000001"; }  //shorten output, don't show exponent.
      else if(valueAbs < 1.0f){ sShow = String.format("%1.12f", value); }  //shorten output, don't show exponent.
      else if(valueAbs < 1.0e3f){ sShow = String.format("%3.9f", value); }  //shorten output, don't show exponent.
      else if(valueAbs < 1.0e6f){ sShow = String.format("%3.8f k", value/1000); }  //shorten output, don't show exponent.
      else if(valueAbs < 1.0e9f){ sShow = String.format("%3.8f M", value/1000000); }  //shorten output, don't show exponent.
      else if(valueAbs >= 1.0e9f){ sShow = String.format("%3.8g", value); }  //shorten output, don't show exponent.
      //else if(valueAbs >= 1e6){ sShow = Float.toString(value/1000000) + " M"; }  //shorten output, don't show exponent.
      else { sShow = Double.toString(value); }
      
    }
    setText(sShow);
  }
  
  
  
  
  @Override public void setValue(Object[] value){
    if(value !=null && value.length ==1 && value[0] instanceof Double){
      double val = ((Double)value[0]).doubleValue();
      setValue(val);
    } else {
      setText("?" + value);
    }
  }

  
  
  
  @Override public void setText(CharSequence arg)
  { setText(arg, 0);
  }
  
  @Override public void setText(CharSequence arg, int caretPos)
  {
    text = arg.toString();
    this.caretPos = caretPos;
    int yet = dyda.whatIsChanged.get();
    int catastrophicCount = 0;
    while( !dyda.whatIsChanged.compareAndSet(yet, yet | chgText)){ 
      if(++catastrophicCount > 10000) throw new RuntimeException("");
    }
    if(Thread.currentThread().getId() == windowMng.getThreadIdGui()){
      repaintGthread();
    } else {
      repaint(100,0);
    }
  }
  
  

  
  /**Sets the action which is invoked while a mouse button is pressed or release on this widget.
   * Implementation hint: It installs a mouse listener.
   * TODO: use GralMouseWidgetAction_ifc instead GralUserAction, use another action for mouse than change.
   */
  abstract public void setMouseAction(GralUserAction action);
  
  
  @Override public boolean isChanged(){ return bTextChanged; }
  
  @Override public String getText(){ return text; }
   


  
  
  /**Returns the Label for a prompt or null if there isn't used a prompt
   */
  abstract public String getPromptLabelImpl();
  
  
  
  private static class CalculatorAngle16 extends CalculatorExpr
  { CalculatorAngle16(){}
    @Override public float calc(float input){
      return input * (180.0f / 32768.0f);   
    }
  } //class CalculatorAngle16
  
  
  private static class CalculatorAngle32 extends CalculatorExpr
  { CalculatorAngle32(){}
    @Override public float calc(float input){
      return input * (180.0f / 0x7fffffff);   
    }
  } //class CalculatorAngle32
  
  
  
  protected GralKeyListener gralKeyListener = new GralKeyListener(itsMng)
  {
    @Override public boolean specialKeysOfWidgetType(int key, GralWidget widgg){ return false; }
  };
  
  
  
}

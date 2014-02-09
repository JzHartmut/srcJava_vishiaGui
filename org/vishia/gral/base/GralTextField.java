package org.vishia.gral.base;


import java.util.Map;

import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralTextFieldUser_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.CalculatorExpr;
import org.vishia.util.DataAccess;
import org.vishia.util.Removeable;
import org.vishia.util.StringFunctions;

/**This is the base class for all widgets which represents a simple text.
 * @author Hartmut Schorrig
 *
 */
public class GralTextField extends GralWidget implements GralTextField_ifc
{
  /**Version, history and license .
   * <ul>
   * <li>2014-02-10 Hartmut chg: Constructor with Parameter {@link Type}, supports password field. 
   * <li>2013-12-22 Hartmut chg: Now {@link GralTextField} uses the new concept of instantiation: It is not
   *   the super class of the implementation class. But it provides {@link GralTextField.GraphicImplAccess}
   *   as the super class. 
   * <li>2013-03-13 Hartmut new {@link #setText(CharSequence, int)}: supports {@link GralWidget_ifc#isNotEditableOrShouldInitialize()} to support 
   *   edit field handling.
   * <li>2013-03-04 Hartmut chg: The {@link #setText(CharSequence, int)} overwriting concept is faulty.
   *   Because an accidentally change of text prevents setting the correct text. Check of {@link #bTextChg}
   *   removed. The preventing of overwriting an edit field should be prevented outside as condition of
   *   call of {@link #setText(CharSequence)}. 
   * <li>2013-02-22 GralTextField: edit field now works with GralShowMethods syncVariableOnFocus,
   *   {@link #bTextChg} detects whether any input is taken. {@link #isChanged(boolean)} returns this information
   *   and resets {@link #bTextChg}. {@link #setText(CharSequence, int)} only sets if the text is unchanged.
   *   Therefore a changed text won't be over-written by an setText().
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
   */
  @SuppressWarnings("hiding")
  public final static int version = 20130313;
  

  public enum Type{ password, editable};
  
  protected int caretPos;
  
  /**The prompt to the text field. */
  protected String sPrompt, sPromptStylePosition;
  
  //protected GralColor colorBack = GralColor.getColor("wh"), colorText = GralColor.getColor("bk");
  
  //protected GralFont fontText;
  
  /**A calculator to show calculated float values. It is null if it isn't used. */
  private CalculatorExpr calculator;

  private String sFormat2;
  
  protected GralTextFieldUser_ifc user;
  
  /**It is used for some operations. */
  protected final GralGraphicThread windowMng;
  
  final boolean bPassword;
  
  /**Constructs a text field with given properties
   * @param name Name of the field. Maybe null if it is not need in management by name
   * @param property password, editable, maybe left empty.
   */
  public GralTextField(String name, Type... property){
    super(name, 't', null);
    boolean bPassword1 = false;
    if(property !=null){
      for(int ii=0; ii<property.length; ++ii){
        switch(property[ii]){
          case password: bPassword1 = true; break;
          case editable: setEditable(true);
        }
      }
    }
    bPassword = bPassword1;
    windowMng = null;
    setBackColor(GralColor.getColor("wh"),0);
    setTextColor(GralColor.getColor("bk"));
  }
  
  
  public GralTextField(String name, char whatis, GralMng mng){
    super(name, whatis, mng);
    bPassword = false;
    setBackColor(GralColor.getColor("wh"),0);
    setTextColor(GralColor.getColor("bk"));
    this.windowMng = mng.gralDevice;
  }
  
  
  
  public void setPrompt(String sPrompt, String sPromptStylePosition){
    this.sPrompt = sPrompt;
    this.sPromptStylePosition = sPromptStylePosition;
    if(wdgImpl !=null){
      dyda.setChanged(GraphicImplAccess.chgPrompt);
      repaint();
    }
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
   * <li>As special feature a format <code>int32AngleDegree</code> and <code>int16AngleDegree</code>
   *   is supported, if that text is contained in the format string. The value should come from an integer,
   *   which contains an angle value with wrap-around-presentation: 0x7fffffff is 1 less 180 degree and
   *   0x80000000 is exactly 180 degree. This format helps to calculate with angle values in range
   *   of -180...+179.99999 degree. The presentation in the text field is shown as degree angle value. 
   * <ul>      
   *   
   * @see org.vishia.gral.base.GralWidget#setValue(float)
   */
  @Override public void setValue(final float valueP){
    final String sFormat1;
    float value;
    String sShow;
    if(calculator !=null){
      try {
        CalculatorExpr.Value value1 = calculator.calcDataAccess(null, valueP);
        value = (float)value1.doubleValue();
      } catch (Exception e) {
        value = 7777777.777f;
      }
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
      try{ sShow = String.format(sFormat1, new Float(value)); }
      catch(java.util.IllegalFormatException exc){ 
        sShow = null;  //maybe integer 
      }
      if(sShow == null){
        try{ sShow = String.format(sFormat, new Integer((int)value)); }
        catch(java.util.IllegalFormatException exc){ 
          sShow = "?format";  
        }
      }
    } else { //no format given
      float valueAbs = Math.abs(value); 
      if(value == 0.0f){ sShow = "0.0"; }
      else if(valueAbs < 1.0e-7f){ sShow = value < 0 ? "-0.0000001" : "0.0000001"; }  //shorten output, don't show exponent.
      else if(valueAbs < 1.0f){ sShow = String.format("%1.7f", new Float(value)); }  //shorten output, don't show exponent.
      else if(valueAbs < 1.0e3f){ sShow = String.format("%3.4f", new Float(value)); }  //shorten output, don't show exponent.
      else if(valueAbs < 1.0e6f){ sShow = String.format("%3.3f k", new Float(value/1000)); }  //shorten output, don't show exponent.
      else if(valueAbs < 1.0e9f){ sShow = String.format("%3.3f M", new Float(value/1000000)); }  //shorten output, don't show exponent.
      else if(valueAbs >= 1.0e9f){ sShow = String.format("%3.3g", new Float(value)); }  //shorten output, don't show exponent.
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
      try{ sShow = String.format(sFormat1, new Double(value)); }
      catch(java.util.IllegalFormatException exc){ 
        sShow = null;  //maybe integer 
      }
      if(sShow == null){
        try{ sShow = String.format(sFormat, new Integer((int)value)); }
        catch(java.util.IllegalFormatException exc){ 
          sShow = "?format";  
        }
      }
    } else { //no format given
      double valueAbs = Math.abs(value); 
      if(value == 0.0f){ sShow = "0.0"; }
      else if(valueAbs < 1.0e-7f){ sShow = value < 0 ? "-0.0000001" : "0.0000001"; }  //shorten output, don't show exponent.
      else if(valueAbs < 1.0f){ sShow = String.format("%1.12f", new Double(value)); }  //shorten output, don't show exponent.
      else if(valueAbs < 1.0e3f){ sShow = String.format("%3.9f", new Double(value)); }  //shorten output, don't show exponent.
      else if(valueAbs < 1.0e6f){ sShow = String.format("%3.8f k", new Double(value/1000)); }  //shorten output, don't show exponent.
      else if(valueAbs < 1.0e9f){ sShow = String.format("%3.8f M", new Double(value/1000000)); }  //shorten output, don't show exponent.
      else if(valueAbs >= 1.0e9f){ sShow = String.format("%3.8g", new Double(value)); }  //shorten output, don't show exponent.
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
  
  /**Sets the textual content. This method sets the text and invokes a {@link #repaint(int, int)} in 100 ms 
   * if the content is changed in another thread than the graphical thread. It invokes a {@link #repaintGthread()}
   * if the content was changed in the graphical thread.
   * Note: If the current content is equals with the new one, a repaint request is not forced.
   * Therewith the cursor can be positioned inside. But if the content is changed, it is set with this given one.
   * @see org.vishia.gral.ifc.GralTextField_ifc#setText(java.lang.CharSequence, int)
   */
  @Override public void setText(CharSequence arg, int caretPos)
  {
    bShouldInitialize= false;  //it is done.
    if(  dyda.displayedText == null   //set the text if no text is stored. Initially!
      //|| !bTextChanged                 //don't set the text if it is changed by user yet.  
         //&& 
        || (!StringFunctions.equals(dyda.displayedText,arg) || caretPos != this.caretPos)  //set the text only if it is changed.
      ){                               //prevent invocation of setText() on non changed values to help move cursor, select etc.
      dyda.displayedText = arg.toString();
      this.caretPos = caretPos;
      dyda.setChanged(GralWidget.ImplAccess.chgText);
      repaint();
    } //else: no change, do nothing. Therewith the field is able to edit on unchanged texts.
  }
  
  

  
  /**Sets the action which is invoked while a mouse button is pressed or release on this widget.
   * Implementation hint: It installs a mouse listener.
   * TODO: use GralMouseWidgetAction_ifc instead GralUserAction, use another action for mouse than change.
   */
  //abstract public void setMouseAction(GralUserAction action);
  
  



  @Override
  public int getCursorPos(){ return caretPos; }


  @Override
  public int setCursorPos(int pos)
  { int pos9 = caretPos;
    if(pos != caretPos){
      caretPos = pos;
      dyda.setChanged(GraphicImplAccess.chgCursor);
      repaint(repaintDelay, repaintDelayMax);
    }
    return pos9;  //the old
  }


  @Override public void setTextStyle(GralColor color, GralFont font)
  {
    dyda.textFont = font;
    dyda.textColor = color;
    dyda.setChanged(GralWidget.ImplAccess.chgColorText);
    if(wdgImpl !=null){
      repaint();
    }
  }
  

  
  
  /**Returns the Label for a prompt or null if there isn't used a prompt
   */
  public final String getPromptLabelImpl(){ return sPrompt; }
  
  
  
  private static class CalculatorAngle16 extends CalculatorExpr
  { CalculatorAngle16(){}
    @Override public float calc(float input){
      return input * (180.0f / 32768.0f);   
    }
    @Override public Value calcDataAccess(Map<String, DataAccess.Variable<Object>> javaVariables, Object... args) throws Exception{
      Float value = (Float)args[0];  //always true, this special class is only used in this context.
      CalculatorExpr.Value valueRet = new CalculatorExpr.Value(calc(value.floatValue()));
      return valueRet;
    }
  } //class CalculatorAngle16
  
  
  private static class CalculatorAngle32 extends CalculatorExpr
  { CalculatorAngle32(){}
    @Override public float calc(float input){
      return input * (180.0f / 0x7fffffff);   
    }
    @Override public Value calcDataAccess(Map<String, DataAccess.Variable<Object>> javaVariables, Object... args) throws Exception{
      Float value = (Float)args[0];  //always true, this special class is only used in this context.
      CalculatorExpr.Value valueRet = new CalculatorExpr.Value(calc(value.floatValue()));
      return valueRet;
    }

  } //class CalculatorAngle32
  
  
  
  protected GralKeyListener gralKeyListener = new GralKeyListener(itsMng)
  {
    @Override public boolean specialKeysOfWidgetType(int key, GralWidget_ifc widgg, Object widgImpl){ return false; }
  };

  
  
  public abstract class GraphicImplAccess extends GralWidget.ImplAccess
  implements GralWidgImpl_ifc
  {

    public static final int chgPrompt = 0x100, chgCursor = 0x200;
    
    protected GraphicImplAccess(GralWidget widgg, GralMng mng)
    {
      super(widgg, mng);
    }
    
    //protected GralFont fontText(){ return GralTextField.this.fontText; }
    
    protected String prompt(){ return GralTextField.this.sPrompt; }
    
    protected String promptStylePosition(){ return GralTextField.this.sPromptStylePosition; }
 
    protected int caretPos(){ return GralTextField.this.caretPos; }
    
    protected void caretPos(int newPos){ GralTextField.this.caretPos = newPos; }
    
    protected GralTextFieldUser_ifc user(){ return GralTextField.this.user; }
    
    protected boolean isPasswordField(){ return GralTextField.this.bPassword; }
    
  }
  
  
}

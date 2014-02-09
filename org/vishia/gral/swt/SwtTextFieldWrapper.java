package org.vishia.gral.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.vishia.byteData.VariableAccessWithIdx;
import org.vishia.gral.base.GralKeyListener;
import org.vishia.gral.base.GralMouseWidgetAction_ifc;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralTextFieldUser_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.KeyCode;

public class SwtTextFieldWrapper extends GralTextField.GraphicImplAccess
{



  /**Version, history and license.
   * <ul>
   * <li>2013-12-22 Hartmut chg: Now {@link GralTextField} uses the new concept of instantiation: It is not
   *   the super class of the implementation class. But it provides {@link GralTextField.GraphicImplAccess}
   *   as the super class. 
   * <li>2012-06-30 Hartmut new actionChange called on typing inside a field.
   * <li>2012-06-30 Hartmut new {@link #swtKeyListener}. The [Enter] key will be send to the User
   *   in opposite to {@link SwtTextBox}.
   * <li>2012-06-08 Hartmut chg: {@link #repaintGthread()} does not do anything if the textFieldSwt is removed 
   *   because the widget was removed. Prevent null-Pointer exception.
   * <li>2012-04-10 Hartmut chg: A key listener, only for experience
   * <li>2012-03-17 Hartmut bugfix: adjustment of prompt for top prompt
   * <li>2012-03-10 Hartmut chg: Minor for top-level prompt.
   * <li>2011-06-00 Hartmut creation
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
   * 
   * 
   */
  @SuppressWarnings("hiding")
  public static final int version = 20120317;
  
  /**It contains the association to the swt widget (Control) and the {@link SwtMng}
   * and implements some methods of {@link GralWidgImpl_ifc} which are delegate from this.
   */
  private final SwtWidgetHelper swtWidgHelper;
  
  protected Text textFieldSwt;
  
  /**A possible prompt for the text field or null. */
  //Label promptSwt;
  SwtTransparentLabel promptSwt;
  //Text promptSwt;
  
  private DropTarget drop;
  
  
  private SwtTextFieldWrapper(GralTextField widgg, SwtMng mng)
  {
    widgg.super(widgg, mng); //NOTE: superclass is a non static inner class of GralTextField. 

    Composite panelSwt = mng.getCurrentPanel();
    //in ctor: setPanelMng(mng);
    //Text widgetSwt;
    //
    int textProperties = SWT.SINGLE;
    if(isPasswordField()){ 
      textProperties |= SWT.PASSWORD; 
    }
    if(prompt() != null && promptStylePosition() !=null && promptStylePosition().startsWith("t")){
      mng.setNextPosition();
      final Font promptFont;
      char sizeFontPrompt;
      GralRectangle boundsAll, boundsPrompt, boundsField;
      final GralPos posPrompt = new GralPos(), posField = new GralPos();

      //boundsAll = mng.calcWidgetPosAndSize(this.pos, 800, 600, 100, 20);
      float ySize = widgg.pos().height();
      //float xSize = pos.width();
      //posPrompt from top, 
      float yPosPrompt, heightPrompt, heightText;
      //switch(promptStylePosition){
        //case 't':{
          if(ySize <= 2.5){ //it is very small for top-prompt:
            yPosPrompt = 1.0f;  //no more less than 1/2 normal line. 
            heightPrompt = 1.0f;
            heightText = ySize - 0.7f;  //max. 1.8
            if(heightText < 1.0f){ heightText = 1.0f; }
          } else if(ySize <=3.3){ //it is normally 2.5..4
            heightPrompt = ySize - 2.0f + 0.5f;   //1 to 1.8
            yPosPrompt = ySize - heightPrompt - 0.1f;  //no more less than 1/2 normal line. 
            heightText = 2.0f;
          } else if(ySize <=4.0){ //it is normally 2.5..4
            heightPrompt = ySize - 2.0f + (4.0f - ySize) * 0.5f; 
            if(heightPrompt < 1.0f){ heightPrompt = 1.0f; }
            yPosPrompt = ySize - heightPrompt + 0.2f;  //no more less than 1/2 normal line. 
            heightText = 2.0f;
          } else { //greater then 4.0
            yPosPrompt = ySize * 0.5f;
            heightPrompt = ySize * 0.4f;;
            heightText = ySize * 0.5f;
          }
          //from top, size of prompt
          posPrompt.setPosition(widgg.pos(), GralPos.same - ySize + yPosPrompt, GralPos.size - heightPrompt, GralPos.same, GralPos.same, 0, '.');
          //from bottom line, size of text
          posField.setPosition(widgg.pos(), GralPos.same, GralPos.size - heightText, GralPos.same, GralPos.same, 0, '.');
        //} break;
      //}
      promptFont = mng.propertiesGuiSwt.getTextFontSwt(heightPrompt, GralFont.typeSansSerif, GralFont.styleNormal); //.smallPromptFont;
      //boundsPrompt = mng.calcWidgetPosAndSize(posPrompt, boundsAll.dx, boundsAll.dy, 10,100);
      //boundsField = mng.calcWidgetPosAndSize(posField, boundsAll.dx, boundsAll.dy, 10,100);
      promptSwt = new SwtTransparentLabel(panelSwt, SWT.TRANSPARENT);
      promptSwt.setFont(promptFont);
      promptSwt.setText(prompt());
      promptSwt.setBackground(null);
      Point promptSize = promptSwt.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
      boundsPrompt = mng.calcWidgetPosAndSizeSwt(posPrompt, promptSwt, 10,100);
      if(promptSize.x > boundsPrompt.dx){
        boundsPrompt.dx = promptSize.x;  //use the longer value, if the prompt text is longer as the field.
      }
      promptSwt.setBounds(boundsPrompt.x, boundsPrompt.y, boundsPrompt.dx, boundsPrompt.dy+1);
      textFieldSwt =  new Text(panelSwt, textProperties);
      mng.setPosAndSizeSwt(posField,textFieldSwt, 800, 600);
      //textFieldSwt.setBounds(boundsField.x, boundsField.y, boundsField.dx, boundsField.dy);
      
    } else {
      //without prompt
      textFieldSwt =  new Text(panelSwt, textProperties);
      mng.setPosAndSize_(textFieldSwt);
    }
    textFieldSwt.setFont(mng.propertiesGuiSwt.stdInputFont);
    textFieldSwt.setEditable(widgg.isEditable());
    textFieldSwt.setBackground(mng.propertiesGuiSwt.colorSwt(GralColor.getColor("wh")));
    textFieldSwt.addFocusListener(mng.focusListener);
    KeyListener swtKeyListener = new TextFieldKeyListener(mng._impl.gralKeyListener);
    textFieldSwt.addKeyListener(swtKeyListener);
    
    Listener[] oldMouseListener = textFieldSwt.getListeners(SWT.MouseDown);
    for(Listener lst: oldMouseListener){
      textFieldSwt.removeListener(SWT.MouseDown, lst);
    }
    textFieldSwt.addMouseListener(mng.mouseClickForInfo);
    textFieldSwt.addFocusListener(mng.focusListener);
    if(widgg.isEditable()){
      TextFieldModifyListener modifyListener = new TextFieldModifyListener();
      textFieldSwt.addModifyListener(modifyListener);
      TextFieldFocusListener focusListener = new TextFieldFocusListener(mng);
      textFieldSwt.addFocusListener(focusListener);
    }
    if(prompt() != null && promptStylePosition() !=null && promptStylePosition().startsWith("r")){
      Rectangle swtField = textFieldSwt.getBounds();
      Rectangle swtPrompt = new Rectangle(swtField.x + swtField.width, swtField.y, 0, swtField.height);
      float hight = widgg.pos().height();
      final Font promptFont;
      if(hight <2.0){
        promptFont = mng.propertiesGuiSwt.smallPromptFont;  
      } else { 
        promptFont = mng.propertiesGuiSwt.stdInputFont;  
      }
      promptSwt = new SwtTransparentLabel(mng.getCurrentPanel(), SWT.TRANSPARENT);
      promptSwt.setFont(promptFont);
      promptSwt.setText(prompt());
      Point promptSize = promptSwt.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
      swtPrompt.width = promptSize.x;
      promptSwt.setBounds(swtPrompt);
      
      
    }
    //
    textFieldSwt.setData(this);
    if(!widgg.isEditable()){
      mng.registerShowField(widgg);
    }
    swtWidgHelper = new SwtWidgetHelper(textFieldSwt, mng);

    mng.registerWidget(widgg);
    
  }

  
  
  /**Creates a SwtTextField. It is package private, only called from the {@link SwtMng}.
   * @param widgg
   * <br>
   * <b>Prompting</b>: The parameter promptStylePosition determines where a prompt is showing.
   * <ul>
   * <li>"t": prompt above, calculates inside position
   * <li>"r": prompt right, calculates outside position.
   * </ul>
   * @param name The name to register it.
   * @param editable false then show field
   * @param prompt maybe null, propmt text
   * @param promptStylePosition maybe null, prompt position
   * @param mng
   */
  static void createTextField(GralTextField widgg, GralMng mng){
    SwtTextFieldWrapper widgswt = new SwtTextFieldWrapper(widgg, (SwtMng)mng);
  }
  
  
  @Override public GralRectangle getPixelPositionSize(){ return swtWidgHelper.getPixelPositionSize(); }



  
  @Override
  protected void setDropEnable(int dropType)
  {
    new SwtDropListener(dropType, textFieldSwt); //associated with textFieldSwt.
  }
  
  
  @Override
  protected void setDragEnable(int dragType)
  {
    new SwtDragListener(dragType, textFieldSwt); //associated with textFieldSwt.
  }
  
  /*
  @Override public void setSelection(String how){
    if(how.equals("|..<")){
      String sText = textFieldSwt.getText();
      int zChars = sText.length();
      int pos0 = 0; //zChars - 20;
      if(pos0 < 0){ pos0 = 0; }
      textFieldSwt.setSelection(pos0, zChars);
    }
  }
  */
  

  
  
  @Override public void repaintGthread(){
    int catastrophicalCount = 0;
    int chg;
    if(textFieldSwt !=null){ //do nothing if the graphic implementation widget is removed.
      GralWidget.DynamicData dyda = dyda();
      while( (chg = getChanged()) !=0){ //widgg.dyda.whatIsChanged.get();
        if(++catastrophicalCount > 10000) 
          throw new RuntimeException("atomic failed");
        if((chg & chgText) !=0 && dyda.displayedText !=null){ 
          textFieldSwt.setText(dyda.displayedText);
          final int selectionStart, selectionEnd;
          final int zText = dyda.displayedText.length();
          if(caretPos() <0){
            selectionEnd = dyda.displayedText.length(); selectionStart = selectionEnd; // -1;
          }
          else if(caretPos() >0){
            selectionEnd = caretPos() > zText ? zText : caretPos();
            selectionStart = selectionEnd; // -1;
          } else {
            assert(caretPos() ==0);
            selectionEnd = selectionStart =-1;  //dont call
          }
          if(selectionStart >=0){
            textFieldSwt.setSelection(selectionStart, selectionEnd);
          }
        }
        if((chg & chgColorText)!=0){
          SwtProperties props = swtWidgHelper.mng.propertiesGuiSwt;
          if(dyda.textColor !=null){
            textFieldSwt.setForeground(props.colorSwt(dyda.textColor));
          }
          if(dyda.backColor !=null){
            textFieldSwt.setBackground(props.colorSwt(dyda.backColor));
          }
          if(dyda.textFont !=null){
            textFieldSwt.setFont(props.fontSwt(dyda.textFont));
          }
        }
        if((chg & chgCursor) !=0){ 
          textFieldSwt.setSelection(caretPos());
        }
        if((chg & chgColorText) !=0){ textFieldSwt.setForeground(swtWidgHelper.mng.getColorImpl(dyda().textColor)); }
        if((chg & chgColorBack) !=0){ textFieldSwt.setBackground(swtWidgHelper.mng.getColorImpl(dyda().backColor)); }
        textFieldSwt.redraw();
        acknChanged(chg);
      }
    }
  }

  

  /**yet it is the same actionChanged of the widget. is called if the write-able text field was changed and the focus is released.
   * The distinction between both is the key code in {@link GralUserAction#userActionGui(int, GralWidget, Object...)}.
   * @param action If null then the action isn't changed. Only the mouse button listener is installed.
   *   The action should be given calling {@link GralWidget#setActionChange(GralUserAction)} then.
   *   If no action is given, The mouse listener has no effect.

   * @see org.vishia.gral.base.GralTextField#setMouseAction(org.vishia.gral.ifc.GralUserAction)
   */
  //@Override 
  @Deprecated
  public void setMouseAction(GralUserAction action)
  {
    if(action !=null){ widgg.setActionChange(action); }
    SwtGralMouseListener.MouseListenerGralAction mouseListener = 
      new SwtGralMouseListener.MouseListenerGralAction(null, GralMouseWidgetAction_ifc.mUserAll);
    textFieldSwt.addMouseListener(mouseListener);  
  }
  
  @Override public Text getWidgetImplementation()
  { return textFieldSwt;
  }

  


  
  @Override public boolean setFocusGThread()
  { return SwtWidgetHelper.setFocusOfTabSwt(textFieldSwt);
  }

  

  @Override public void removeWidgetImplementation()
  {
    if(textFieldSwt !=null){
      textFieldSwt.dispose();
    } else {
      stop();
    }
    textFieldSwt = null;
    if(promptSwt !=null){
      promptSwt.dispose();
      promptSwt = null;
    }
  }

  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { textFieldSwt.setBounds(x,y,dx,dy);
  }
  
  
  protected void textFieldFocusGained(){
    //- done in GralMng.GralMngFocusListener! super.focusGained();  //set HtmlHelp etc.
    if(actionChanging() != null){
      actionChanging().exec(KeyCode.focusGained, widgg, dyda().displayedText);
    }
    if(dyda().displayedText !=null){
      textFieldSwt.setText(dyda().displayedText);
    }
  }
  
  
  protected void textFieldFocusLost(){
    String text = textFieldSwt.getText();
    dyda().displayedText = text;  //transfer the current text
    dyda().displayedText = text;
    caretPos(textFieldSwt.getCaretPosition());
    if(actionChanging() != null){
      actionChanging().exec(KeyCode.focusLost, widgg, text);
    }
  }
  
  
  /**For edit able fields.
   */
  private class TextFieldFocusListener extends SwtMng.SwtMngFocusListener
  {
    
    TextFieldFocusListener(SwtMng mng){
      mng.super();
    }

    @Override public void focusLost(FocusEvent ev){
      SwtTextFieldWrapper.this.textFieldFocusLost();
      super.focusLost(ev);
    }

    
    @Override public void focusGained(FocusEvent ev)
    { SwtTextFieldWrapper.this.textFieldFocusGained();
      super.focusGained(ev);
    }
  }
  

  
  private class TextFieldModifyListener implements ModifyListener{
    @Override public void modifyText(ModifyEvent ev) {
      String text = textFieldSwt.getText();
      SwtTextFieldWrapper.super.dyda().displayedText = text;
      //System.out.println("actionText");
      //SwtTextFieldWrapper.super.caretPos = textFieldSwt.getCaretPosition();
      if(actionChanging() != null){
        actionChanging().exec(KeyCode.valueChanged, widgg, text);
      }
      //if(dyda.displayedText !=null){
        //textFieldSwt.setText(dyda.displayedText);
      //}
    }
    
  };
  
 
  
  protected class TextFieldKeyListener extends SwtKeyListener
  {

    public TextFieldKeyListener(GralKeyListener keyAction)
    { super(keyAction);
    }

    @Override public final boolean specialKeysOfWidgetType(int key, GralWidget_ifc widgg, Object widgImpl){ 
      boolean bDone = true;
      if(KeyCode.isWritingKey(key)){
        setTextChanged();
      }
      if(key != KeyCode.enter && KeyCode.isWritingOrTextNavigationKey(key)){
        bDone = true;
      } else {
        boolean bUserOk;
        GralTextFieldUser_ifc user = user();
        if(user !=null){
          Point selection = textFieldSwt.getSelection();
          bUserOk = user.userKey(key
              , textFieldSwt.getText()
              , textFieldSwt.getCaretPosition()
              , selection.x, selection.y);
        } else bUserOk = false;
        if(!bUserOk ){
          switch(key){
            case KeyCode.ctrl + 'a': { 
              textFieldSwt.selectAll();
            } break;
            default: bDone = false;
          }
        }
      }
      return bDone; 
    }
  };
  
  void stop(){}






}

package org.vishia.gral.swt;

//import java.io.IOException;
//
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.FocusEvent;
//import org.eclipse.swt.events.FocusListener;
//import org.eclipse.swt.events.KeyListener;
//import org.eclipse.swt.events.ModifyEvent;
//import org.eclipse.swt.events.ModifyListener;
//import org.eclipse.swt.graphics.Font;
//import org.eclipse.swt.graphics.Point;
//import org.eclipse.swt.graphics.Rectangle;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Label;
//import org.eclipse.swt.widgets.ScrollBar;
//import org.eclipse.swt.widgets.Text;
//import org.vishia.gral.base.GralGraphicTimeOrder;
//import org.vishia.gral.base.GralKeyListener;
//import org.vishia.gral.base.GralMng;
//import org.vishia.gral.base.GralPanelContent;
//import org.vishia.gral.base.GralTextBox;
//import org.vishia.gral.base.GralTextField;
//import org.vishia.gral.base.GralWidget;
//import org.vishia.gral.ifc.GralColor;
//import org.vishia.gral.ifc.GralFont;
//import org.vishia.gral.ifc.GralRectangle;
//import org.vishia.gral.ifc.GralTextFieldUser_ifc;
//import org.vishia.gral.ifc.GralUserAction;
//import org.vishia.gral.ifc.GralWidget_ifc;
//import org.vishia.gral.swt.SwtTextFieldWrapper.TextFieldKeyListener;
//import org.vishia.gral.swt.SwtTextFieldWrapper.TextFieldModifyListener;
//import org.vishia.util.KeyCode;


//no more used.
public class SwtTextBox //extends GralTextBox.GraphicImplAccess
{

  /**Version and history
   * <ul>
   * <li>2014-08-16 Hartmut chg: GralTextBox not abstract, using GraphicImplAccess like new concept of all GralWidgets. 
   * <li>2012-01-06 Hartmut chg: The {@link #append(CharSequence)} etc. methods are implemented
   *   in this super class instead in the graphic layer implementation classes. Therefore
   *   the methods {@link #appendTextInGThread(CharSequence)} and {@link #setTextInGThread(CharSequence)}
   *   are defined here to implement in the graphic layer. The set- and apppend methods are <b>threadsafe</b> now.
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
   * If you are indent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   * 
   */
//  @SuppressWarnings("hiding")
//  public final static String sVersion = "2014-08-16";
//
//  
//  /**Experience: use SwtTextFieldWrapper as composite? (instead superclass)
//   * 
//   */
//  @SuppressWarnings("unused")
//  private SwtTextFieldWrapper swtText;
//  
//  /**It contains the association to the swt widget (Control) and the {@link SwtMng}
//   * and implements some methods of {@link GralWidgImpl_ifc} which are delegate from this.
//   */
//  private final SwtWidgetHelper wdgh;
//  
//  /*package private*/ Text textFieldSwt;
//  
//  /**A possible prompt for the text field or null. */
//  Label promptSwt;
//  
//  
//  private SwtTextBox(GralTextBox widgg, SwtMng mng)
//  { widgg.super(widgg); //NOTE: superclass is a non static inner class of GralTextField. 
//    Composite panelSwt = mng.getCurrentPanel();
//    //GralPanelContent gralPanel = mng.mng.getCurrentPanel();
//    
//    assert(panelSwt !=null);
//    this.textFieldSwt = new Text(panelSwt, SWT.MULTI|SWT.H_SCROLL|SWT.V_SCROLL); //;style);
//    this.textFieldSwt.setData(this);
//    super.wdgimpl = this.wdgh = new SwtWidgetHelper(this.textFieldSwt, mng);
//    this.textFieldSwt.addFocusListener(mng.focusListener);
//    this.textFieldSwt.setFont(mng.propertiesGuiSwt.stdInputFont);
//    this.textFieldSwt.setEditable(widgg.isEditable());
//    this.textFieldSwt.setBackground(mng.propertiesGuiSwt.colorSwt(0xFFFFFF));
//    this.textFieldSwt.addMouseListener(SwtGralMouseListener.mouseActionStd);
//    KeyListener swtKeyListener = new TextBoxKeyListener(mng.mng._impl.gralKeyListener);
//    this.textFieldSwt.addKeyListener(swtKeyListener);
//    TextBoxModifyListener modifyListener = new TextBoxModifyListener();
//    this.textFieldSwt.addModifyListener(modifyListener);
//    
//    mng.setPosAndSize_(widgg.pos(), this.textFieldSwt);
//    if(prompt() != null && promptStylePosition().equals("t")){
//      final int yPixelField;
//      final Font promptFont;
//      int ySize = (int)(widgg.pos().height());
//      switch(ySize){
//      case 3:  promptFont = mng.propertiesGuiSwt.smallPromptFont;
//               yPixelField = mng.propertiesGuiSwt.yPixelUnit() * 2 -3;
//               break;
//      case 2:  promptFont = mng.propertiesGuiSwt.smallPromptFont;
//               yPixelField = (int)(1.5F * mng.mng.propertiesGui.yPixelUnit());
//               break;
//      default: promptFont = mng.propertiesGuiSwt.smallPromptFont;
//               yPixelField = mng.mng.propertiesGui.yPixelUnit() * 2 -3;
//      }//switch
//      Rectangle boundsField = this.textFieldSwt.getBounds();
//      Rectangle boundsPrompt = new Rectangle(boundsField.x, boundsField.y-3  //occupy part of field above, only above the normal letters
//        , boundsField.width, boundsField.height );
//      
//      if(promptStylePosition().equals("t")){ 
//        boundsPrompt.height -= (yPixelField -4);
//        boundsPrompt.y -= 1;
//        
//        boundsField.y += (boundsField.height - yPixelField );
//        boundsField.height = yPixelField;
//      }
//      Label wgPrompt = new Label(panelSwt, 0);
//      //Text wgPrompt = new Text(((SwtPanel)pos.panel).getPanelImpl(), 0);
//      wgPrompt.setFont(promptFont);
//      wgPrompt.setText(prompt());
//      Point promptSize = wgPrompt.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
//      if(promptSize.x > boundsPrompt.width){
//        boundsPrompt.width = promptSize.x;  //use the longer value, if the prompt text is longer as the field.
//      }
//      this.textFieldSwt.setBounds(boundsField);
//      wgPrompt.setBounds(boundsPrompt);
//    } 
//    mng.mng.registerWidget(widgg);
//  }
//
//  
//  static void createTextBox(GralTextBox widgg, SwtMng mng){
//    SwtTextBox widgetSwt = new SwtTextBox(widgg, (SwtMng)mng);
//    //
//  }
//  
//
//  
//  
//  //@Override public Widget getWidgetImplementation(){ return textFieldSwt; } 
//  //@Override public boolean setFocus(){ return this.textFieldSwt.setFocus(); }
//
//  @Override public GralRectangle getPixelPositionSize(){ return wdgh.getPixelPositionSize(); }
//
//
//  @Override protected int getCurrentCaretPos() { return this.textFieldSwt.getCaretPosition(); }
//  
//  @Override protected int getCurrentCaretLinePos() { return this.textFieldSwt.getCaretLineNumber(); }
//  
//
//  
//  @Override public Object getWidgetImplementation()
//  { return this.textFieldSwt;
//  }
//
//  /*
//
//  @Override public String getText()
//  {
//    String oldText = this.textFieldSwt.getText();
//    return oldText;
//  }
//   
//  @Override public GralColor setBackgroundColor(GralColor color)
//  { return SwtWidgetHelper.setBackgroundColor(color, this.textFieldSwt);
//  }
//  
//
//  @Override public GralColor setForegroundColor(GralColor color)
//  { return SwtWidgetHelper.setForegroundColor(color, this.textFieldSwt);
//  }
//  
//  
//
//
//
//  
//  @Override public void setTextInGThread(CharSequence text){ 
//    this.textFieldSwt.setText(text.toString()); 
//  }
//  
//
//  
//  @Override public void appendTextInGThread(CharSequence text){ 
//    this.textFieldSwt.append(text.toString()); 
//  }
//  
//  */
//  
//  
//  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
//  { this.textFieldSwt.setBounds(x,y,dx,dy);
//  }
//  
//
//  
//  
//  @Override public void repaintGthread(){
//    int catastrophicalCount = 0;
//    int chg;
//    if(this.textFieldSwt !=null){ //do nothing if the graphic implementation widget is removed.
//      GralWidget.DynamicData dyda = dyda();
//      while( (chg = getChanged()) !=0){ //widgg.dyda.whatIsChanged.get();
//        if(++catastrophicalCount > 10000) 
//          throw new RuntimeException("atomic failed");
//        if((chg & chgText) !=0 && dyda.displayedText !=null){ 
//          this.textFieldSwt.setText(dyda.displayedText);
//          final int selectionStart, selectionEnd;
//          final int zText = dyda.displayedText.length();
//          if(caretPos() <0){
//            selectionEnd = dyda.displayedText.length(); selectionStart = selectionEnd; // -1;
//          }
//          else if(caretPos() >0){
//            selectionEnd = caretPos() > zText ? zText : caretPos();
//            selectionStart = selectionEnd; // -1;
//          } else {
//            assert(caretPos() ==0);
//            selectionEnd = selectionStart =-1;  //dont call
//          }
//          if(selectionStart >=0){
//            this.textFieldSwt.setSelection(selectionStart, selectionEnd);
//          }
//        }
//        if((chg & chgAddText) !=0) {
//          this.textFieldSwt.append(getAndClearNewText());
//        }
//        if((chg & chgColorText)!=0){
//          SwtProperties props = wdgh.mng.propertiesGuiSwt;
//          if(dyda.textColor !=null){
//            this.textFieldSwt.setForeground(props.colorSwt(dyda.textColor));
//          }
//          if(dyda.backColor !=null){
//            this.textFieldSwt.setBackground(props.colorSwt(dyda.backColor));
//          }
//          if(dyda.textFont !=null){
//            this.textFieldSwt.setFont(props.fontSwt(dyda.textFont));
//          }
//        }
//        if((chg & chgEditable)!=0){ 
//          this.textFieldSwt.setEditable(true); 
//        }
//        if((chg & chgNonEditable)!=0){ 
//          this.textFieldSwt.setEditable(false); 
//        }
//        
//        if((chg & chgViewTrail)!=0) {
//          ScrollBar scroll = this.textFieldSwt.getVerticalBar();
//          int maxScroll = scroll.getMaximum();
//          scroll.setSelection(maxScroll);
//          this.textFieldSwt.update();
//        }
//        if((chg & chgCursor) !=0){ 
//          this.textFieldSwt.setSelection(caretPos());
//        }
//        if((chg & chgVisible) !=0) {
//          this.textFieldSwt.getShell().setVisible(true);
//        }
//        if((chg & chgInvisible) !=0) {
//          this.textFieldSwt.getShell().setVisible(false);
//        }
//        if((chg & chgColorText) !=0){ this.textFieldSwt.setForeground(wdgh.mng.getColorImpl(dyda().textColor)); }
//        if((chg & chgColorBack) !=0){ this.textFieldSwt.setBackground(wdgh.mng.getColorImpl(dyda().backColor)); }
//        this.textFieldSwt.redraw();
//        acknChanged(chg);
//      }
//    }
//  }
//
//  
//  
//  
//
//  @Override public boolean setFocusGThread()
//  { return SwtWidgetHelper.setFocusOfTabSwt(this.textFieldSwt);
//  }
//
//
//  @Override public void setVisibleGThread(boolean bVisible) { super.setVisibleState(bVisible); wdgh.setVisibleGThread(bVisible); }
//
//  
//  @Override public void removeWidgetImplementation()
//  {
//    if(this.textFieldSwt !=null) {
//      this.textFieldSwt.dispose();
//      this.textFieldSwt = null;
//    }
//    if(promptSwt !=null){
//      promptSwt.dispose();
//      promptSwt = null;
//    }
//  }
//
//  
//  protected class TextBoxKeyListener extends SwtKeyListener
//  //protected SwtKeyListener swtKeyListener = new SwtKeyListener(SwtTextBox.this.swtWidgHelper.mng._impl.gralKeyListener)
//  {
//
//    public TextBoxKeyListener(GralKeyListener keyAction)
//    { super(keyAction);
//    }
//
//    @Override public final boolean specialKeysOfWidgetType(int key, GralWidget_ifc widgg, Object widgImpl){ 
//      boolean bDone = true;
//      if(KeyCode.isWritingKey(key)){
//        setTextChanged();
//      }
//      boolean bUserOk;
//      GralTextFieldUser_ifc user = user();
//      //SwtTextBox.super.caretPos(this.textFieldSwt.getCaretPosition());
//      if(user !=null){
//        Point selection = SwtTextBox.this.textFieldSwt.getSelection();
//        bUserOk = user.userKey(key
//            , SwtTextBox.this.textFieldSwt.getText()
//            , SwtTextBox.this.textFieldSwt.getCaretPosition()
//            , selection.x, selection.y);
//      } else { bUserOk = false; }
//      //
//      if(!bUserOk ){  //user has not accept the key
//        if(KeyCode.isWritingOrTextNavigationKey(key)) return true;
//        switch(key){
//          case KeyCode.ctrl + 'a': { 
//            SwtTextBox.this.textFieldSwt.selectAll();
//          } break;
//          default: bDone = false;
//        }
//      }
//      return bDone; 
//    }
//    
//    
//    
//  }
//
//
//  
//  
//  protected class TextBoxModifyListener implements ModifyListener{
//    @Override public void modifyText(ModifyEvent ev) {
//      String text = SwtTextBox.this.textFieldSwt.getText();
//      SwtTextBox.super.dyda().displayedText = text;
//      //System.out.println("actionText");
//      //SwtTextFieldWrapper.super.caretPos = this.textFieldSwt.getCaretPosition();
//      GralWidget_ifc.ActionChange action = getActionChange(GralWidget_ifc.ActionChangeWhen.onAnyChgContent);
//      if(action !=null){
//        Object[] args = action.args();
//        if(args == null){ action.action().exec(KeyCode.valueChanged, SwtTextBox.this.widgg, text); }
//        else { action.action().exec(KeyCode.valueChanged, SwtTextBox.this.widgg, args, text); }
//      }
//      //if(dyda.displayedText !=null){
//        //this.textFieldSwt.setText(dyda.displayedText);
//      //}
//    }
//    
//  }
//  
//  protected FocusListener XXXfocusLstn = new FocusListener()
//  {
//    
//    @Override
//    public void focusLost(FocusEvent e)
//    {
//      //System.err.println("TextBox focus lost");
//    }
//    
//    @Override
//    public void focusGained(FocusEvent e)
//    {
//      //System.err.println("TextBox focus gained");
//    }
//  };
//
//  
//
//  

}

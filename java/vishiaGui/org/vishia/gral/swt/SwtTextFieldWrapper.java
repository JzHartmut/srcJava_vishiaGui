package org.vishia.gral.swt;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.vishia.byteData.VariableAccessWithIdx;
import org.vishia.gral.base.GralButton.GraphicImplAccess;
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
import org.vishia.gral.impl_ifc.GralWidgetImpl_ifc;
import org.vishia.util.Debugutil;
import org.vishia.util.KeyCode;
import org.vishia.util.StringFunctions;

public class SwtTextFieldWrapper extends GralTextField.GraphicImplAccess
{

  /**Version, history and license.
   * <ul>
   * <li>2023-01-14 Hartmut fix: after improve of {@link #updateValuesForAction()}: 
   *   a text changed with {@link org.vishia.gral.base.GralTextBox#append(CharSequence)} was not mirrored in {@link GralWidget.DynamicData#displayedText}.
   *   The reason was: It was no more updated, before updated not planned on Focus lost. 
   *   Now the displayedText is updated in {@link #redrawGthread()} on evaluation of dyda().
   * <li>2022-12-26 Hartmut fix: {@link #updateValuesForAction()} a {@link GralTextField#setText(CharSequence)} was pending
   *   during a focus lost event. The focus lost calls updateValues... but the new value was not written till yet
   *   (delayed {@link GralWidget.ImplAccess#redrawGthread()}
   * <li>2015-05-04 Hartmut new: {@link #textFieldFocusLost()} calls {@link org.vishia.gral.base.GralWidgImplAccess_ifc#updateValuesForAction()}.
   *   implemented here in {@link #updateValuesForAction()} which sets the cursor line and column. 
   * <li>2015-05-04 Hartmut new: Contains all also for the TextBox, replaces SwtTextBox.
   * <li>2015-05-04 Hartmut new: {@link #setBorderWidth(int)} to show the text field with a border. That is not a property
   *   of an SWT Text, therefore a new {@link #paintListener} was added to draw the border.
   * <li>2013-12-22 Hartmut chg: Now {@link GralTextField} uses the new concept of instantiation: It is not
   *   the super class of the implementation class. But it provides {@link GralTextField.GraphicImplAccess}
   *   as the super class. 
   * <li>2012-06-30 Hartmut new actionChange called on typing inside a field.
   * <li>2012-06-30 Hartmut new {@link #swtKeyListener}. The [Enter] key will be send to the User
   *   in opposite to {@link SwtTextBox}.
   * <li>2012-06-08 Hartmut chg: {@link #redrawGthread()} does not do anything if the textFieldSwt is removed 
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
  //@SuppressWarnings("hiding")
  public static final String version = "2022-01-29";
  
  /**It contains the association to the swt widget (Control) and the {@link SwtMng}
   * and implements some methods of {@link GralWidgImplAccess_ifc} which are delegate from this.
   */
  private final SwtWidgetHelper swtWidgHelper;
  
  protected Text textFieldSwt;
  
  protected final boolean bbox;
  
  /**A possible prompt for the text field or null. */
  //Label promptSwt;
  SwtTransparentLabel promptSwt;
  //Text promptSwt;
  
  private DropTarget drop;
  
  

  /**This is a helper array for a text box to calculate the column from the position. 
   * Unfortunately the {@link Text#getCaretPosition()} returns only the absolute position,
   * not the column. Whereas {@link Text#getCaretLineNumber()} returns the line. 
   * 
   */
  short[] startPosLine;
  
  
  
  /**Note: use {@link #createTextBox(GralTextField, GralMng)} or {@link #createTextField(GralTextField, GralMng)}
   * @param widgg
   * @param swtMng
   * @param bbox
   */
  private SwtTextFieldWrapper(GralTextField widgg, SwtMng swtMng, boolean bbox)
  {
    widgg.super(widgg, swtMng); //NOTE: superclass is a non static inner class of GralTextField. 
    if(widgg.name !=null && widgg.name.startsWith("showSrc"))
      Debugutil.stop();
    this.bbox = bbox;
    Composite panelSwt = SwtMng.getWidgetsPanel(widgg);
    //in ctor: setPanelMng(mng);
    //Text widgetSwt;
    //
    int textProperties = bbox ? SWT.MULTI|SWT.H_SCROLL|SWT.V_SCROLL : SWT.SINGLE;
    if(isPasswordField()){ 
      textProperties |= SWT.PASSWORD; 
    }
    if(posPrompt !=null) {
      final Font promptFont;
      float heightPrompt = posPrompt.height();
      promptFont = swtMng.propertiesGuiSwt.getTextFontSwt(heightPrompt, GralFont.typeSansSerif, GralFont.styleNormal); //.smallPromptFont;
      //boundsPrompt = mng.calcWidgetPosAndSize(posPrompt, boundsAll.dx, boundsAll.dy, 10,100);
      //boundsField = mng.calcWidgetPosAndSize(posField, boundsAll.dx, boundsAll.dy, 10,100);
      promptSwt = new SwtTransparentLabel(panelSwt, SWT.TRANSPARENT);
      promptSwt.setFont(promptFont);
      promptSwt.setText(prompt());
      promptSwt.setBackground(null);
    }
    this.textFieldSwt =  new Text(panelSwt, textProperties);
    //textFieldSwt.setBounds(boundsField.x, boundsField.y, boundsField.dx, boundsField.dy);
    textFieldSwt.setFont(swtMng.propertiesGuiSwt.stdInputFont);
    textFieldSwt.setEditable(widgg.isEditable());
    textFieldSwt.setBackground(swtMng.propertiesGuiSwt.colorSwt(GralColor.getColor("wh")));
    KeyListener swtKeyListener = new TextFieldKeyListener(swtMng.gralMng._implListener.gralKeyListener);
    textFieldSwt.addKeyListener(swtKeyListener);
    textFieldSwt.setMenu(null);  //default: no contextMenu, use GralMenu?
    
//    Listener[] oldMouseListener = textFieldSwt.getListeners(SWT.MouseDown);
//    for(Listener lst: oldMouseListener){
//      textFieldSwt.removeListener(SWT.MouseDown, lst);
//    }
//    textFieldSwt.addMouseListener(mng.mouseClickForInfo);
    this.textFieldSwt.addMouseListener(SwtGralMouseListener.mouseActionStd);  //from SwtTextBox
    //textFieldSwt.addFocusListener(mng.focusListener);
    TextFieldFocusListener focusListener = new TextFieldFocusListener(swtMng);
    textFieldSwt.addFocusListener(focusListener);
    if(widgg.isEditable()){
      TextFieldModifyListener modifyListener = new TextFieldModifyListener();
      textFieldSwt.addModifyListener(modifyListener);
    } else {
      
    }
    //
    textFieldSwt.setData(this);
    textFieldSwt.addPaintListener(paintListener);
    if(!widgg.isEditable()){
      swtMng.gralMng.registerShowField(widgg);
    }
    super.wdgimpl = swtWidgHelper = new SwtWidgetHelper(textFieldSwt, swtMng);

    swtMng.gralMng.registerWidget(widgg);
    //GralRectangle parentPix = SwtMng.  //widgg.pos().parent.getImplAccess().getPixelPositionSize();
    GralRectangle pix = this.mngImpl.calcWidgetPosAndSize(this.widgg.pos(), 800, 600);
    setBoundsPixel(pix.x, pix.y, pix.dx, pix.dy);
    //setPosBounds(parentPix);
    redrawGthread();
    
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
  static GralWidget.ImplAccess createTextField(GralTextField widgg, GralMng mng){
    return new SwtTextFieldWrapper(widgg, (SwtMng)mng._mngImpl, false);
  }
  
  static GralWidget.ImplAccess createTextBox(GralTextField widgg, GralMng mng){
    return new SwtTextFieldWrapper(widgg, (SwtMng)mng._mngImpl, true);
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
  

  
  //tag::redrawGthread[]
  @Override public void redrawGthread(){
    int catastrophicalCount = 0;
    int chg;
    if(this.textFieldSwt !=null){ //do nothing if the graphic implementation widget is removed.
      GralWidget.DynamicData dyda = dyda();
      while( (chg = getChanged()) !=0){ //widgg.dyda.whatIsChanged.get();
        if(++catastrophicalCount > 10000) {
          throw new RuntimeException("acknowledge failed");
        }
        if((chg & chgText) !=0 && dyda.displayedText !=null){ 
          this.textFieldSwt.setText(dyda.displayedText);
          //end::redrawGthread[]
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
            this.textFieldSwt.setSelection(selectionStart, selectionEnd);
          }
        }
        if((chg & chgAddText) !=0) {
          String newText = getAndClearNewText(); // temporary stored in GralTextField#newText 
          this.textFieldSwt.append(newText);     // update also the text on GralLevel.
          dyda.displayedText = this.textFieldSwt.getText();  // get the currently text, this is the truth
          // Hint new since 2023-01, before 2022-12 it was updated on focus lost.
          
        }
        if((chg & chgColorText)!=0){
          SwtProperties props = this.swtWidgHelper.mng.propertiesGuiSwt;
          if(dyda.textColor !=null){
            this.textFieldSwt.setForeground(props.colorSwt(dyda.textColor));
          }
          if(dyda.backColor !=null){
            this.textFieldSwt.setBackground(props.colorSwt(dyda.backColor));
          }
          if(dyda.textFont !=null){
            this.textFieldSwt.setFont(props.fontSwt(dyda.textFont));
          }
        }
        if((chg & chgEditable) !=0){ 
          this.textFieldSwt.setEditable(this.widgg.isEditable());
        }
        if((chg & chgNonEditable)!=0){ 
          this.textFieldSwt.setEditable(false); 
        }
        if((chg & chgViewTrail)!=0) {
          ScrollBar scroll = this.textFieldSwt.getVerticalBar();
          int maxScroll = scroll.getMaximum();
          scroll.setSelection(maxScroll);
          this.textFieldSwt.update();
        }
        if((chg & chgCursor) !=0){ 
          this.textFieldSwt.setSelection(caretPos());
        }
        if((chg & chgPrompt) !=0){ 
          this.promptSwt.setText(this.prompt());
          this.promptSwt.redraw();
        }
        if((chg & chgFocus) !=0){ 
          this.textFieldSwt.setFocus();
        }
        if((chg & chgVisible) !=0){ 
          this.textFieldSwt.setVisible(true);
        }
        if((chg & chgInvisible) !=0){ 
          this.textFieldSwt.setVisible(false);
        }
        if((chg & chgColorText) !=0){ 
          this.textFieldSwt.setForeground(this.swtWidgHelper.mng.getColorImpl(dyda().textColor)); 
        }
        //tag::redrawGthreadEnd[]
        if((chg & chgColorBack) !=0){ 
          this.textFieldSwt.setBackground(this.swtWidgHelper.mng.getColorImpl(dyda().backColor)); 
        }
        this.textFieldSwt.redraw();
        //textFieldSwt.
        acknChanged(chg);
      }
    }
    //end::redrawGthreadEnd[]
  }

  

  @Override public Text getWidgetImplementation()
  { return textFieldSwt;
  }

  


  
  @Override public boolean setFocusGThread()
  { return SwtWidgetHelper.setFocusOfTabSwt(textFieldSwt);
  }

  @Override public void setVisibleGThread(boolean bVisible) { super.setVisibleState(bVisible); swtWidgHelper.setVisibleGThread(bVisible); }


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

  @Override public void setBoundsPixel(int x, int y, int dx, int dy) { 
    //throw new IllegalStateException("should not called because setPosBounds() is overridden.");
    if(this.posPrompt ==null) {
      this.textFieldSwt.setBounds(x,y,dx,dy);
    } else if(prompt() != null && promptStylePosition() !=null && promptStylePosition().startsWith("t")){
      int yPrompt = dy / 3 +4;                // should follow the text size, todo...
      int yText = dy - yPrompt;
      if(yPrompt < 7) { yPrompt = 7; }
      this.promptSwt.setBounds(x, y, dx, yPrompt);
      this.textFieldSwt.setBounds(x, y + dy - yPrompt, dx, dy - yPrompt);
    } else if(prompt() != null && promptStylePosition() !=null && promptStylePosition().startsWith("r")){
      this.promptSwt.setBounds(x, y + dy, dx, dy);
      this.textFieldSwt.setBounds(x, y, dx, dy);
    }
  }
  
  
  @Override public GralRectangle setPosBounds ( GralRectangle parentPix ) {
    SwtMng swtMng = (SwtMng)super.mngImpl;
    if(posPrompt !=null) {
      GralRectangle boundsPrompt;
      Point promptSize = promptSwt.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
      boundsPrompt = swtMng.calcWidgetPosAndSizeSwt(posPrompt, promptSwt.getParent(), 10,100);
      if(promptSize.x > boundsPrompt.dx){
        boundsPrompt.dx = promptSize.x;  //use the longer value, if the prompt text is longer as the field.
      }
      promptSwt.setBounds(boundsPrompt.x, boundsPrompt.y, boundsPrompt.dx, boundsPrompt.dy+1);
    }
    swtMng.setPosAndSizeSwt(posField,textFieldSwt, 800, 600);
//    if(prompt() != null && promptStylePosition() !=null && promptStylePosition().startsWith("r")){
//      Rectangle swtField = textFieldSwt.getBounds();
//      Rectangle swtPrompt = new Rectangle(swtField.x + swtField.width, swtField.y, 0, swtField.height);
//      float hight = widgg.pos().height();
//      final Font promptFont;
//      if(hight <2.0){
//        promptFont = swtMng.propertiesGuiSwt.smallPromptFont;  
//      } else { 
//        promptFont = swtMng.propertiesGuiSwt.stdInputFont;  
//      }
//      promptSwt = new SwtTransparentLabel(swtMng.getCurrentPanel(), SWT.TRANSPARENT);
//      promptSwt.setFont(promptFont);
//      promptSwt.setText(prompt());
//      Point promptSize = promptSwt.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
//      swtPrompt.width = promptSize.x;
//      promptSwt.setBounds(swtPrompt);
//      
//      
//    }
    return null;  //GralRectanvle pos))
  }
  
  
  protected void textFieldFocusGained(){
    //- done in GralMng.GralMngFocusListener! super.focusGained();  //set HtmlHelp etc.
    if(super.widgg.isEditable()) {
      GralWidget_ifc.ActionChange action = getActionChange(GralWidget_ifc.ActionChangeWhen.onFocusGained); 
      if(action !=null){
        Object[] args = action.args();
        if(args == null){ action.action().exec(KeyCode.focusGained, widgg, dyda().displayedText); }
        else { action.action().exec(KeyCode.focusGained, widgg, args, dyda().displayedText); }
      }
      if(dyda().displayedText !=null){
        textFieldSwt.setText(dyda().displayedText);
      }
    }
  }
  
  
  /**On focus lost all values are updated, because it is possible to evaluate from any other widget. 
   * Calls a user action if it is registered as {@link ActionChangeWhen#onChangeAndFocusLost}.
   */
  protected void textFieldFocusLost(){
    updateValuesForAction();
    //
    if(super.widgg.isEditable()) {
      GralWidget_ifc.ActionChange action = getActionChange(GralWidget_ifc.ActionChangeWhen.onChangeAndFocusLost); 
      if(action !=null){
        Object[] args = action.args();
        GralWidget.DynamicData dyda = SwtTextFieldWrapper.super.dyda();
        if(args == null){ action.action().exec(KeyCode.focusLost, this.widgg, dyda.displayedText); }
        else { action.action().exec(KeyCode.focusLost, this.widgg, args, dyda.displayedText); }
      }
    }
  }
  
  
  
  protected void paintWidget(Text swt, PaintEvent e){
    GC gc = e.gc;
    //gc.d
    int borderwidth = super.borderwidth();
    if(borderwidth >0) {
      GralTextField widg = (GralTextField)super.widgg;
      Rectangle dim = swt.getBounds();
      gc.setLineWidth(borderwidth);
      Color colorLine = swtWidgHelper.mng.getColorImpl(dyda().lineColor);
      gc.setForeground(colorLine);
      //gc.drawLine(0, 0, dim.width, dim.height);  //test of coordinates
      Rectangle rect = new Rectangle(0,0,dim.width, dim.height);  //the rect which should drawn counts from (0,0)
      gc.drawRectangle(rect);
    }
  } 
  
  PaintListener paintListener = new PaintListener(){
    @Override public void paintControl(PaintEvent e) {
      SwtTextFieldWrapper.this.paintWidget((Text)swtWidgHelper.widgetSwt, e);
    }
  };
  

  
  /**For edit able fields.
   */
  private class TextFieldFocusListener extends SwtMng.SwtMngFocusListener
  {
    
    TextFieldFocusListener(SwtMng mng){
      mng.super(mng.gralMng);
    }

    @Override public void focusLost(FocusEvent ev){
      SwtTextFieldWrapper.this.textFieldFocusLost();
      setTouched(false);   //assumes that the changed field content is processed with routine above. Can be automaticly overwritten newly be application.
      super.focusLost(ev);
    }

    
    @Override public void focusGained(FocusEvent ev)
    { SwtTextFieldWrapper.this.textFieldFocusGained();
      super.focusGained(ev);
    }
  }
  

  
  protected class TextFieldModifyListener implements ModifyListener{
    @Override public void modifyText(ModifyEvent ev) {
      String text = textFieldSwt.getText();
      GralWidget.DynamicData dyda = SwtTextFieldWrapper.super.dyda();
      if(! text.equals(dyda.displayedText)) {
        dyda.displayedText = text;
        setTouched(true);
        //System.out.println("actionText");
        //SwtTextFieldWrapper.super.caretPos = textFieldSwt.getCaretPosition();
        GralWidget_ifc.ActionChange action = getActionChange(GralWidget_ifc.ActionChangeWhen.onAnyChgContent); 
        if(action !=null){
          Object[] args = action.args();
          if(args == null){ action.action().exec(KeyCode.valueChanged, widgg, text); }
          else { action.action().exec(KeyCode.valueChanged, widgg, args, text); }
        }
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
      boolean bEditable = widgg.isEditable();
      if(bEditable && KeyCode.isWritingKey(key)){
        setTextChanged();
      }
      if(bEditable && key != KeyCode.enter && KeyCode.isWritingOrTextNavigationKey(key)){
        bDone = true;
        setTouched(true);
      } else {
        boolean bUserOk = true;
        GralWidget_ifc.ActionChange action = getActionChange(GralWidget_ifc.ActionChangeWhen.onAnyKey);
        if(action !=null) {
          bUserOk = action.action().exec(key, SwtTextFieldWrapper.this.widgg);
        }
        GralTextFieldUser_ifc user = user();
        if(!bUserOk && user !=null){
          Point selection = textFieldSwt.getSelection();
          bUserOk = user.userKey(key
              , textFieldSwt.getText()
              , textFieldSwt.getCaretPosition()
              , selection.x, selection.y);
        }
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



  @Override
  public void updateValuesForAction() {
    GralTextField wdg = (GralTextField)SwtTextFieldWrapper.super.widgg;
    GralWidget.DynamicData dyda = SwtTextFieldWrapper.super.dyda();
    if(  wdg.isEditable()                                  // only call on editable fields, all other cannot be changed.
      && (dyda.getChanged() & (GralTextField.GraphicImplAccess.chgText | GralTextField.GraphicImplAccess.chgText)) 
         ==0 ) {                                           // prevent update the Gral text if a chgText (with a new text) is pending!
      String text2 = this.textFieldSwt.getText();          // get the text from the graphic and write into dyda
      if(! text2.equals(dyda.displayedText)) {             // but only if a text change is not pending. 
        dyda.displayedText = text2;
        setTouched(true);                                  // mark as changed only if it is changed.
      }
    }
    //There was a problem. Because of TextFieldModifyListener the field is already set. Newly read of getText() gets the old text. Bug of windows?
    //dyda().displayedText = text2;  //transfer the current text
    int pos = this.textFieldSwt.getCaretPosition();
    final int col;
    int line =0;
    if(bbox) {
      line = this.textFieldSwt.getCaretLineNumber();
      if(line >0) {
        short zlines = this.startPosLine == null ? 0 : this.startPosLine[0];
        if(  this.startPosLine == null 
          || line >= zlines          //store oldLength!
          || this.startPosLine[line] ==0
          ) {                                    // necessity of new Array:
          int pos1 = zlines ==0 ? 0: this.startPosLine[zlines];      //from 0 or from last line end.
          String text = this.textFieldSwt.getText();
          int ztext = text.length();
          while(pos1 < ztext) {
            if(zlines ==0 || (zlines+1) >= this.startPosLine.length) {
              int sizenew = this.startPosLine == null ? 0x20: 2* this.startPosLine.length;
              short[] startPosLine1;             // create a greater array
              if(this.startPosLine !=null) {     // copy content to array with greater size
                startPosLine1 = Arrays.copyOf(this.startPosLine, sizenew);
              } else {                           // create firstly, empty
                startPosLine1 = new short[sizenew ];
              }
              this.startPosLine = startPosLine1;     // replace / create with new size.
            }
            int posEnd1 = text.indexOf('\n', pos1);
            int posEnd2 = text.indexOf('\r', pos1);
            if(posEnd1 <0) { posEnd1 = posEnd2; }
            if(posEnd1 <0) { posEnd1 = ztext -1; }         // end is after not ended line...
            pos1 = posEnd1 +1;
            this.startPosLine[++zlines] = (short)pos1;
          }
          this.startPosLine[0] = zlines;         // first element is number of existing lines.
        }
        col = pos - this.startPosLine[line];          // column in line.
      } else {
        col = pos;
      }
    } else {
      col = pos;
    }
    super.caretPos(pos, line, col);
  }






}

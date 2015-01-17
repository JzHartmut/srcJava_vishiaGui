package org.vishia.gral.swt;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralKeyListener;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralTextFieldUser_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.swt.SwtTextFieldWrapper.TextFieldKeyListener;
import org.vishia.gral.swt.SwtTextFieldWrapper.TextFieldModifyListener;
import org.vishia.util.KeyCode;

public class SwtTextBox extends GralTextBox.GraphicImplAccess
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
  @SuppressWarnings("hiding")
  public final static String sVersion = "2014-08-16";

  
  /**Experience: use SwtTextFieldWrapper as composite? (instead superclass)
   * 
   */
  @SuppressWarnings("unused")
  private SwtTextFieldWrapper swtText;
  
  /**It contains the association to the swt widget (Control) and the {@link SwtMng}
   * and implements some methods of {@link GralWidgImpl_ifc} which are delegate from this.
   */
  private final SwtWidgetHelper wdgh;
  
  /*package private*/ Text textFieldSwt;
  
  /**A possible prompt for the text field or null. */
  Label promptSwt;
  
  
  protected SwtTextBox(GralTextBox widgg, SwtMng mng)
  { widgg.super(widgg, mng.mng); //NOTE: superclass is a non static inner class of GralTextField. 
    Composite panelSwt = mng.getCurrentPanel();
    textFieldSwt = new Text(panelSwt, SWT.MULTI|SWT.H_SCROLL|SWT.V_SCROLL); //;style);
    textFieldSwt.setData(this);
    wdgh = new SwtWidgetHelper(textFieldSwt, mng);

    textFieldSwt.setFont(mng.propertiesGuiSwt.stdInputFont);
    textFieldSwt.setEditable(widgg.isEditable());
    textFieldSwt.setBackground(mng.propertiesGuiSwt.colorSwt(0xFFFFFF));
    textFieldSwt.addMouseListener(mng.mouseClickForInfo);
    KeyListener swtKeyListener = new TextBoxKeyListener(mng.mng._impl.gralKeyListener);
    textFieldSwt.addKeyListener(swtKeyListener);
    TextBoxModifyListener modifyListener = new TextBoxModifyListener();
    textFieldSwt.addModifyListener(modifyListener);
    
    mng.setPosAndSize_(textFieldSwt);
    if(prompt() != null && promptStylePosition().equals("t")){
      final int yPixelField;
      final Font promptFont;
      int ySize = (int)(widgg.pos().height());
      switch(ySize){
      case 3:  promptFont = mng.propertiesGuiSwt.smallPromptFont;
               yPixelField = mng.propertiesGuiSwt.yPixelUnit() * 2 -3;
               break;
      case 2:  promptFont = mng.propertiesGuiSwt.smallPromptFont;
               yPixelField = (int)(1.5F * mng.mng.propertiesGui.yPixelUnit());
               break;
      default: promptFont = mng.propertiesGuiSwt.smallPromptFont;
               yPixelField = mng.mng.propertiesGui.yPixelUnit() * 2 -3;
      }//switch
      Rectangle boundsField = textFieldSwt.getBounds();
      Rectangle boundsPrompt = new Rectangle(boundsField.x, boundsField.y-3  //occupy part of field above, only above the normal letters
        , boundsField.width, boundsField.height );
      
      if(promptStylePosition().equals("t")){ 
        boundsPrompt.height -= (yPixelField -4);
        boundsPrompt.y -= 1;
        
        boundsField.y += (boundsField.height - yPixelField );
        boundsField.height = yPixelField;
      }
      Label wgPrompt = new Label(panelSwt, 0);
      //Text wgPrompt = new Text(((SwtPanel)pos.panel).getPanelImpl(), 0);
      wgPrompt.setFont(promptFont);
      wgPrompt.setText(prompt());
      Point promptSize = wgPrompt.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
      if(promptSize.x > boundsPrompt.width){
        boundsPrompt.width = promptSize.x;  //use the longer value, if the prompt text is longer as the field.
      }
      textFieldSwt.setBounds(boundsField);
      wgPrompt.setBounds(boundsPrompt);
    } 
    mng.mng.registerWidget(widgg);
  }

  
  static void createTextBox(GralTextBox widgg, SwtMng mng){
    SwtTextBox widgetSwt = new SwtTextBox(widgg, (SwtMng)mng);
    //
  }
  

  
  
  //@Override public Widget getWidgetImplementation(){ return textFieldSwt; } 
  //@Override public boolean setFocus(){ return textFieldSwt.setFocus(); }

  @Override public GralRectangle getPixelPositionSize(){ return wdgh.getPixelPositionSize(); }



  

  
  @Override public Object getWidgetImplementation()
  { return textFieldSwt;
  }

  /*

  @Override public String getText()
  {
    String oldText = textFieldSwt.getText();
    return oldText;
  }
   
  @Override public GralColor setBackgroundColor(GralColor color)
  { return SwtWidgetHelper.setBackgroundColor(color, textFieldSwt);
  }
  

  @Override public GralColor setForegroundColor(GralColor color)
  { return SwtWidgetHelper.setForegroundColor(color, textFieldSwt);
  }
  
  



  
  @Override public void setTextInGThread(CharSequence text){ 
    textFieldSwt.setText(text.toString()); 
  }
  

  
  @Override public void appendTextInGThread(CharSequence text){ 
    textFieldSwt.append(text.toString()); 
  }
  
  */
  
  
  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { textFieldSwt.setBounds(x,y,dx,dy);
  }
  

  
  
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
        if((chg & chgAddText) !=0) {
          textFieldSwt.append(getAndClearNewText());
        }
        if((chg & chgColorText)!=0){
          SwtProperties props = wdgh.mng.propertiesGuiSwt;
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
        if((chg & chgEditable)!=0){ 
          textFieldSwt.setEditable(true); 
        }
        if((chg & chgNonEditable)!=0){ 
          textFieldSwt.setEditable(false); 
        }
        
        if((chg & chgViewTrail)!=0) {
          ScrollBar scroll = textFieldSwt.getVerticalBar();
          int maxScroll = scroll.getMaximum();
          scroll.setSelection(maxScroll);
          textFieldSwt.update();
        }
        if((chg & chgCursor) !=0){ 
          textFieldSwt.setSelection(caretPos());
        }
        if((chg & chgColorText) !=0){ textFieldSwt.setForeground(wdgh.mng.getColorImpl(dyda().textColor)); }
        if((chg & chgColorBack) !=0){ textFieldSwt.setBackground(wdgh.mng.getColorImpl(dyda().backColor)); }
        textFieldSwt.redraw();
        acknChanged(chg);
      }
    }
  }

  
  
  

  @Override public boolean setFocusGThread()
  { return SwtWidgetHelper.setFocusOfTabSwt(textFieldSwt);
  }

  
  @Override public void removeWidgetImplementation()
  {
    if(textFieldSwt !=null) {
      textFieldSwt.dispose();
      textFieldSwt = null;
    }
    if(promptSwt !=null){
      promptSwt.dispose();
      promptSwt = null;
    }
  }

  
  protected class TextBoxKeyListener extends SwtKeyListener
  //protected SwtKeyListener swtKeyListener = new SwtKeyListener(SwtTextBox.this.swtWidgHelper.mng._impl.gralKeyListener)
  {

    public TextBoxKeyListener(GralKeyListener keyAction)
    { super(keyAction);
    }

    @Override public final boolean specialKeysOfWidgetType(int key, GralWidget_ifc widgg, Object widgImpl){ 
      boolean bDone = true;
      if(KeyCode.isWritingKey(key)){
        setTextChanged();
      }
      boolean bUserOk;
      GralTextFieldUser_ifc user = user();
      if(user !=null){
        Point selection = textFieldSwt.getSelection();
        bUserOk = user.userKey(key
            , textFieldSwt.getText()
            , textFieldSwt.getCaretPosition()
            , selection.x, selection.y);
      } else { bUserOk = false; }
      //
      if(!bUserOk ){  //user has not accept the key
        if(KeyCode.isWritingOrTextNavigationKey(key)) return true;
        switch(key){
          case KeyCode.ctrl + 'a': { 
            textFieldSwt.selectAll();
          } break;
          default: bDone = false;
        }
      }
      return bDone; 
    }
    
    
    
  }


  
  
  protected class TextBoxModifyListener implements ModifyListener{
    @Override public void modifyText(ModifyEvent ev) {
      String text = textFieldSwt.getText();
      SwtTextBox.super.dyda().displayedText = text;
      //System.out.println("actionText");
      //SwtTextFieldWrapper.super.caretPos = textFieldSwt.getCaretPosition();
      if(actionChanging() != null){
        actionChanging().exec(KeyCode.valueChanged, widgg, text);
      }
      //if(dyda.displayedText !=null){
        //textFieldSwt.setText(dyda.displayedText);
      //}
    }
    
  }
  

  
}

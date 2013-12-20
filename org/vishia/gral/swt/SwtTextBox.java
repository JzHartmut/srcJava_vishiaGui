package org.vishia.gral.swt;

import java.io.IOException;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.vishia.gral.base.GralDispatchCallbackWorker;
import org.vishia.gral.base.GralKeyListener;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.KeyCode;

public class SwtTextBox extends GralTextBox
{

  /**Experience: use SwtTextFieldWrapper as composite? (instead superclass)
   * 
   */
  @SuppressWarnings("unused")
  private SwtTextFieldWrapper swtText;
  
  
  /*package private*/ Text textFieldSwt;
  
  /**A possible prompt for the text field or null. */
  Label promptSwt;
  
  
  public SwtTextBox(String name, Composite parent, int style, SwtMng mng)
  { super(name, 't', mng);
    textFieldSwt = new Text(parent, style);
    textFieldSwt.addKeyListener(swtKeyListener);
  }

  //@Override public Widget getWidgetImplementation(){ return textFieldSwt; } 
  //@Override public boolean setFocus(){ return textFieldSwt.setFocus(); }

  
  @Override public void setTextStyle(GralColor color, GralFont font)
  {
    SwtProperties props = ((SwtMng)itsMng).propertiesGuiSwt;
    textFieldSwt.setForeground(props.colorSwt(color));
    textFieldSwt.setFont(props.fontSwt(font));
  }


  @Override public void setEditable(boolean editable){
    textFieldSwt.setEditable(editable);
  }


  
  @Override public void viewTrail()
  {
    //textAreaOutput.setCaretPosition(textAreaOutput.getLineCount());
    ScrollBar scroll = textFieldSwt.getVerticalBar();
    int maxScroll = scroll.getMaximum();
    scroll.setSelection(maxScroll);
    textFieldSwt.update();
    
  }

  @Override public int getNrofLines(){ return textFieldSwt.getLineCount(); }

  @Override public int getCursorPos(){ return textFieldSwt.getCaretPosition(); }

  
  @Override public String getText()
  {
    String oldText = textFieldSwt.getText();
    return oldText;
  }
   
  @Override public Object getWidgetImplementation()
  { return textFieldSwt;
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
  
  
  
  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { textFieldSwt.setBounds(x,y,dx,dy);
  }
  

  
  @Override public void repaintGthread(){  
    int whatisChanged1 = dyda.whatIsChanged.get();
    int catastrophicCount = 0;
    while( !dyda.whatIsChanged.compareAndSet(whatisChanged1, 0)){ 
      whatisChanged1 = dyda.whatIsChanged.get();  //maybe new requests
      if(++catastrophicCount > 10000) throw new RuntimeException("");
    }
    if((whatisChanged1 & ImplAccess.chgText) !=0 && dyda.displayedText !=null){
      textFieldSwt.setText(dyda.displayedText);
    }
    textFieldSwt.redraw(); textFieldSwt.update(); 
  }


  @Override public boolean setFocusGThread()
  { return SwtWidgetHelper.setFocusOfTabSwt(textFieldSwt);
  }


  @Override public int setCursorPos(int pos){
    int oldPos = textFieldSwt.getCaretPosition();
    textFieldSwt.setSelection(pos);
    return oldPos;
  }

  
  @Override public void removeWidgetImplementation()
  {
    textFieldSwt.dispose();
    textFieldSwt = null;
    if(promptSwt !=null){
      promptSwt.dispose();
      promptSwt = null;
    }
  }

  
  protected SwtKeyListener swtKeyListener = new SwtKeyListener(itsMng._impl.gralKeyListener)
  {

    @Override public final boolean specialKeysOfWidgetType(int key, GralWidget widgg, Object widgImpl){ 
      boolean bDone = true;
      if(KeyCode.isWritingKey(key)){
        bTextChanged = true;
      }
      if(KeyCode.isWritingOrTextNavigationKey(key)) return true;
      boolean bUserOk;
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
      return bDone; 
    }
    
    
    
  };


  
}

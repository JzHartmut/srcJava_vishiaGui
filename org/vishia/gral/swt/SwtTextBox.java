package org.vishia.gral.swt;

import java.io.IOException;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.vishia.gral.base.GralDispatchCallbackWorker;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.KeyCode;

public class SwtTextBox extends GralTextBox
{
  protected Text textFieldSwt;
  
  /**A possible prompt for the text field or null. */
  Label promptSwt;
  
  
  public SwtTextBox(String name, Composite parent, int style, SwtMng mng)
  { super(name, 't', mng);
    textFieldSwt = new Text(parent, style);
    textFieldSwt.addKeyListener((new SwtTextBoxKeyListener(mng)).swtListener);
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

  @Override public String getPromptLabelImpl(){ return promptSwt.getText(); }


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
    int whatisChanged1 = whatIsChanged.get();
    int catastrophicCount = 0;
    while( !whatIsChanged.compareAndSet(whatisChanged1, 0)){ 
      whatisChanged1 = whatIsChanged.get();  //maybe new requests
      if(++catastrophicCount > 10000) throw new RuntimeException("");
    }
    if((whatisChanged1 & chgText) !=0){
      textFieldSwt.setText(text);
    }
    textFieldSwt.redraw(); textFieldSwt.update(); 
  }


  @Override public boolean setFocus()
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

  @Override
  public void setMouseAction(GralUserAction action)
  {
    // TODO Auto-generated method stub
    
  }


  @Override public GralWidgetGthreadSet_ifc getGthreadSetifc(){ return gThreadSet; }

  /**Implementation of the graphic thread widget set interface. */
  GralWidgetGthreadSet_ifc gThreadSet = new GralWidgetGthreadSet_ifc(){

    @Override public void clearGthread()
    { // TODO Auto-generated method stub
    }

    @Override public void insertGthread(int pos, Object visibleInfo, Object data)
    { // TODO Auto-generated method stub
    }

    @Override public void redrawGthread()
    { // TODO Auto-generated method stub
    }

    @Override public void setBackGroundColorGthread(GralColor color)
    { // TODO Auto-generated method stub
    }

    @Override public void setForeGroundColorGthread(GralColor color)
    { // TODO Auto-generated method stub
    }

    @Override public void setTextGthread(String text, Object data)
    { // TODO Auto-generated method stub
    }
  };
  
  
  
  class SwtTextBoxKeyListener extends SwtKeyListener
  {

    public SwtTextBoxKeyListener(SwtMng swtMng)
    {
      super(swtMng);
    }

    @Override protected final boolean specialKeysOfWidgetType(int key, GralWidget widgg){ 
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

    
    
    
  }


  
}

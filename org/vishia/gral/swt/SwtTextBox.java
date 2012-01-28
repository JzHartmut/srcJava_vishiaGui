package org.vishia.gral.swt;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.vishia.gral.base.GralDispatchCallbackWorker;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.util.KeyCode;

public class SwtTextBox extends GralTextBox
{
  protected Text textFieldSwt;
  
  /**A possible prompt for the text field or null. */
  Label promptSwt;
  
  
  public SwtTextBox(String name, Composite parent, int style, SwtWidgetMng mng)
  { super(name, 't', mng);
    textFieldSwt = new Text(parent, style);
    textFieldSwt.addKeyListener((new SwtTextBoxKeyListener(mng)).swtListener);
  }

  //@Override public Widget getWidgetImplementation(){ return textFieldSwt; } 
  //@Override public boolean setFocus(){ return textFieldSwt.setFocus(); }

  
  @Override public void setTextStyle(GralColor color, GralFont font)
  {
    SwtProperties props = ((SwtWidgetMng)itsMng).propertiesGuiSwt;
    textFieldSwt.setForeground(props.colorSwt(color));
    textFieldSwt.setFont(props.fontSwt(font));
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
    textFieldSwt.redraw(); textFieldSwt.update(); 
  }


  @Override public boolean setFocus()
  { return SwtWidgetHelper.setFocusOfTabSwt(textFieldSwt);
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

    public SwtTextBoxKeyListener(SwtWidgetMng swtMng)
    {
      super(swtMng);
    }

    @Override protected final boolean specialKeysOfWidgetType(int key, GralWidget widgg){ 
      boolean bDone = true;
      if(KeyCode.isWritingOrTextNavigationKey(key)) return true;
      switch(key){
        case KeyCode.ctrl + 'a': { 
          textFieldSwt.selectAll();
        } break;
        default: bDone = false;
      }
      return bDone; 
    }

    
    
    
  }


  
}

package org.vishia.gral.awt;

import java.awt.Container;
import java.awt.Label;
import java.awt.TextArea;
import java.io.IOException;

import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;

/**
 *
 */
public class AwtTextBox extends GralTextBox.GraphicImplAccess
{
  protected AwtTextAreaImpl textFieldSwt;
  
  /**A possible prompt for the text field or null. */
  /*packagePrivate*/ Label promptSwt;
  
  StringBuffer newText = new StringBuffer();
  
  
  public AwtTextBox(GralTextBox widgg, Container parent, int style, AwtMng mng)
  { widgg.super(widgg);
    textFieldSwt = new AwtTextAreaImpl();
    parent.add(textFieldSwt);
  }

  //@Override public Widget getWidgetImplementation(){ return textFieldSwt; } 
  //@Override public boolean setFocus(){ return textFieldSwt.setFocus(); }


 
  /*
  
  @Override public void setTextInGThread(CharSequence text){ 
    textFieldSwt.setText(text.toString()); 
  }
  
  
    
  @Override public void appendTextInGThread(CharSequence text){ 
    textFieldSwt.append(text.toString()); 
  }
  
  
*/
  
  
   
  @Override public Object getWidgetImplementation()
  { return textFieldSwt;
  }


   



  
  protected GralGraphicTimeOrder changeTextBoxTrail = new GralGraphicTimeOrder("AwtTextBox.changeTextBoxTrail", widgg.gralMng())
  { @Override public void executeOrder()
    { if(newText.length() >0){
        textFieldSwt.append(newText.toString());
        newText.setLength(0);
      }
    }
  };
  
  
  protected GralGraphicTimeOrder changeText = new GralGraphicTimeOrder("AwtTextBox.changeTextB", widgg.gralMng())
  { @Override public void executeOrder()
    { if(newText.length() >0){
        textFieldSwt.setText(newText.toString());
        newText.setLength(0);
      }
      //countExecution();
      //windowMng.removeDispatchListener(this);
    }
  };
  
  
  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { textFieldSwt.setBounds(x,y,dx,dy);
  }
  


  
  @Override public void redrawGthread(){  textFieldSwt.repaint();  }

  

  
  @Override public boolean setFocusGThread()
  { return AwtWidgetHelper.setFocusOfTabSwt(textFieldSwt);
  }



  
  @Override public void removeWidgetImplementation()
  {
    Container parent = textFieldSwt.getParent();
    parent.remove(textFieldSwt);
    textFieldSwt = null;
  }


  
  
  
public static class AwtTextAreaImpl extends TextArea implements AwtWidget
  {
    Object data;
    
    AwtTextAreaImpl()
    { super("test");
      
    }
    
    @Override public Object getData(){ return data; }
  
    @Override public void setData(Object dataP){ this.data = dataP; }
    
  }





@Override
public void setVisibleGThread(boolean bVisible)
{
  // TODO Auto-generated method stub
  
}

@Override
public GralRectangle getPixelPositionSize()
{
  // TODO Auto-generated method stub
  return null;
}

@Override
public void updateValuesForAction() {
  // TODO Auto-generated method stub
  
}

//@Override
//protected int getCurrentCaretPos()
//{
//  // TODO Auto-generated method stub
//  return 0;
//}
//
//@Override protected int getCurrentCaretLinePos() 
//{
//  // TODO Auto-generated method stub
//  return 0;
//}
//
  
  
  
  
}

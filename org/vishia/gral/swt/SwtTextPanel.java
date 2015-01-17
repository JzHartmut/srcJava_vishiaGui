package org.vishia.gral.swt;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralTextBox_ifc;

public class SwtTextPanel extends GralPanelContent implements GralTextBox_ifc, Appendable
{
  
  GralTextBox textAreaOutput;
  
  SwtTextPanel(String namePanel, Shell shell, int style, SwtMng mng) //GralPrimaryWindow_ifc mainWindow)
  { super(namePanel, mng.mng, shell);
    Display device = shell.getDisplay();
    //Composite panel = (Composite) panelComposite;
    textAreaOutput = new GralTextBox(namePanel + "-widg");
    textAreaOutput.setToPanel(mng.mng);
    Text swtText = (Text) textAreaOutput.getWidgetImplementation();
    swtText.setFont(new Font(device, "Monospaced",11, SWT.NORMAL));
    panelComposite = swtText;  //it is a control,    
  }

  
  @Override public void setTextStyle(GralColor color, GralFont font)
  { textAreaOutput.setTextStyle(color, font);
  }

  
  
  @Override public void setEditable(boolean editable){
    textAreaOutput.setEditable(editable);
  }


  
  @Override
  public GralColor setBackgroundColor(GralColor color)
  { return textAreaOutput.setBackgroundColor(color);
  }

  @Override
  public boolean setFocusGThread()
  { return textAreaOutput.setFocusGThread();
  }

  @Override public int setCursorPos(int pos){
    return textAreaOutput.setCursorPos(pos);
  }


  

  
  
  @Override
  public GralColor setForegroundColor(GralColor color)
  { return textAreaOutput.setForegroundColor(color);
  }

  @Override
  public int getNrofLines()
  { return textAreaOutput.getNrofLines();
  }

  @Override public void setText(CharSequence text){ textAreaOutput.setText(text); }
  
  @Override public void setText(CharSequence text, int caretPos){ textAreaOutput.setText(text, caretPos); }
  
  //@Override public void setSelection(String how){ textAreaOutput.setSelection(how); }
  

  @Override public boolean isChanged(boolean setUnchanged){ return textAreaOutput.isChanged(setUnchanged); }
  

  
  @Override public String getText(){ return textAreaOutput.getText(); }

  @Override public int getCursorPos(){ return textAreaOutput.getCursorPos(); }
  
  @Override
  public void viewTrail()
  {
    textAreaOutput.viewTrail();
  }

  @Override public void repaintGthread(){  textAreaOutput.repaintGthread(); }


  
  @Override
  public Appendable append(CharSequence csq) throws IOException
  { return textAreaOutput.append(csq);
  }

  @Override
  public Appendable append(char c) throws IOException
  { return textAreaOutput.append(c);
  }

  @Override
  public Appendable append(CharSequence csq, int start, int end) throws IOException
  { return textAreaOutput.append(csq, start, end);
  }
  
  @Override public Control getPanelImpl() { return (Control)panelComposite; }

  @Override public GralRectangle getPixelPositionSize(){ return textAreaOutput.getPixelPositionSize(); }


  @Override public GralRectangle getPixelSize(){
    Rectangle r = ((Composite)panelComposite).getClientArea();
    GralRectangle posSize = new GralRectangle(0, 0, r.width, r.height);
    return posSize;
  }



  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { ((Control)panelComposite).setBounds(x,y,dx,dy);
  }




  
  @Override public void removeWidgetImplementation()
  { ((Control)panelComposite).dispose();
    panelComposite = null;
  }
  

}

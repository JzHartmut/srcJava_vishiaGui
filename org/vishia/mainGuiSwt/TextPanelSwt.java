package org.vishia.mainGuiSwt;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralTextBox_ifc;

public class TextPanelSwt extends GralPanelContent implements GralTextBox_ifc, Appendable
{
  
  TextBoxSwt textAreaOutput;
  
  TextPanelSwt(String namePanel, Shell shell, int style, GralPrimaryWindow_ifc mainWindow)
  { super(namePanel, mainWindow);
    Display device = shell.getDisplay();
    //Composite panel = (Composite) panelComposite;
    textAreaOutput = new TextBoxSwt(namePanel + "-widg", shell, style, super.mainWindow);
    textAreaOutput.textFieldSwt.setFont(new Font(device, "Monospaced",11, SWT.NORMAL));
    panelComposite = textAreaOutput.textFieldSwt;  //it is a control,    
  }

  @Override
  public GralColor setBackgroundColor(GralColor color)
  { return textAreaOutput.setBackgroundColor(color);
  }

  @Override
  public boolean setFocus()
  { return textAreaOutput.setFocus();
  }

  @Override
  public GralColor setForegroundColor(GralColor color)
  { return textAreaOutput.setForegroundColor(color);
  }

  @Override
  public void append(String text)
  {
    textAreaOutput.append(text);
    
  }

  @Override
  public int getNrofLines()
  { return textAreaOutput.getNrofLines();
  }

  @Override
  public void setText(String text)
  {
    textAreaOutput.setText(text);
  }
  
  @Override public String getText(){ return textAreaOutput.getText(); }

  @Override
  public void viewTrail()
  {
    textAreaOutput.viewTrail();
  }

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
  

}

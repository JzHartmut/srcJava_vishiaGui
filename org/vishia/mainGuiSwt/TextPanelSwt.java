package org.vishia.mainGuiSwt;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.widget.TextBoxGuifc;

public class TextPanelSwt extends GralPanelContent implements TextBoxGuifc, Appendable
{
  
  TextBoxSwt textAreaOutput;
  
  TextPanelSwt(String namePanel, Shell shell, int style)
  { super(namePanel);
    Display device = shell.getDisplay();
    //Composite panel = (Composite) panelComposite;
    textAreaOutput = new TextBoxSwt(shell, style);
    textAreaOutput.text.setFont(new Font(device, "Monospaced",11, SWT.NORMAL));
    panelComposite = textAreaOutput.text;  //it is a control,    
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
  public String setText(String text)
  {
    return textAreaOutput.setText(text);
  }

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

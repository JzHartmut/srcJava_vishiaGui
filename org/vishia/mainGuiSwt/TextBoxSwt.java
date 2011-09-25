package org.vishia.mainGuiSwt;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.widget.TextBoxGuifc;

public class TextBoxSwt implements TextBoxGuifc, Appendable
{
  final Text text;
  
  public TextBoxSwt(Composite parent, int style)
  { text = new Text(parent, style);
  }

  @Override public Widget getWidget(){ return text; } 
  @Override public boolean setFocus(){ return text.setFocus(); }


  @Override
  public void viewTrail()
  {
    //textAreaOutput.setCaretPosition(textAreaOutput.getLineCount());
    ScrollBar scroll = text.getVerticalBar();
    int maxScroll = scroll.getMaximum();
    scroll.setSelection(maxScroll);
    text.update();
    
  }

  @Override
  public int getNrofLines(){ return text.getLineCount(); }

  @Override
  public Appendable append(CharSequence arg0) throws IOException
  { text.append(arg0.toString());
    return this;
  }

  @Override
  public Appendable append(char arg0) throws IOException
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Appendable append(CharSequence arg0, int arg1, int arg2)
      throws IOException
  {
    text.append(arg0.subSequence(arg1, arg2).toString());
    return this;
  }

  @Override
  public String setText(String arg)
  {
    String oldText = text.getText();
    text.setText(arg);
    return oldText;
  }

  @Override
  public void append(String src)
  {
    text.append(src);
    
  }

  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

}

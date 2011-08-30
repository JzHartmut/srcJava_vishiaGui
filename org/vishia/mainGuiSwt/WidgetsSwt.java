package org.vishia.mainGuiSwt;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.ifc.ColorGui;
import org.vishia.gral.widget.TextBoxGuifc;
import org.vishia.gral.widget.Widgetifc;

public class WidgetsSwt
{

  public static class TextSwt extends Text implements Widgetifc
  {

    public TextSwt(Composite parent, int style)
    { super(parent, style);
    }
    @Override public Widget getWidget(){ return this; } 
    
  }
  
  public static class TextBoxSwt implements TextBoxGuifc, Appendable
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
    public void setText(String arg)
    {
      text.setText(arg);
      
    }

    @Override
    public void append(String src)
    {
      text.append(src);
      
    }

    
  }
  
  
}

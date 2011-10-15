package org.vishia.gral.widget;

import java.io.IOException;

import org.vishia.gral.base.GralSubWindow;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.gridPanel.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.ifc.GralTextBox_ifc;

/**This class presents a sub window which is used as info box for any messages.
 * @author Hartmut Schorrig
 *
 */
public class InfoBox implements GralTextBox_ifc, GralWindow_ifc
{

  /**The window is created invoking the {@link GralGridBuild_ifc#createWindow(String, boolean)}. 
   * It has its implementation in the underlying graphic system.  */
  private final GralSubWindow window;
  
  
  /**The widget which holds the text in the {@link #window}. */
  private final GralWidget textBox;
  
  private final GralWidget buttonOk;
  
  public InfoBox(GralSubWindow window, GralWidget textBox, GralWidget buttonOk)
  {
    this.window = window;
    this.textBox = textBox;
    this.buttonOk = buttonOk;
  }
  
  public static InfoBox create(GralGridBuild_ifc mng, String name, String title)
  {
    GralSubWindow window = mng.createWindow(title, false);
    mng.setPosition(0, -3, 0, 0, 0, '.');
    GralWidget text = mng.addText(name, 'C', 0);
    mng.setPosition(-3, 0, -6, 0, 0, '.');
    GralWidget buttonOk = mng.addButton(name + "-Info-ok", null, "", null, null, "OK");
    InfoBox box = new InfoBox(window, text, buttonOk);
    box.buttonOk.setActionChange(null);
    return box; 

  }
  
  @Override
  public void append(String text)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public int getNrofLines()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void setText(String text)
  {
    // TODO Auto-generated method stub
  }

  @Override public String getText()
  { return null;
  
  }
  
  @Override
  public void viewTrail()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Object getWidgetImplementation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean setFocus()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public boolean isWindowsVisible()
  {
    // TODO Auto-generated method stub
    return false;
  }



  @Override
  public void setWindowVisible(boolean visible)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Appendable append(CharSequence arg0) throws IOException
  {
    // TODO Auto-generated method stub
    return null;
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
    // TODO Auto-generated method stub
    return null;
  }
  
  


}

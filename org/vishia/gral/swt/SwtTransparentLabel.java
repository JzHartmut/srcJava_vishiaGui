package org.vishia.gral.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**This class supports drawing a Label without background. 
 * The Problem is: A swt.Color doesn't support the transparent/opaque/alpha property of colors.
 * Therefore setBackground(Color) with an non-opaque color can't be used in SWT (in the current version).
 * But a swt.widgets.Composite support drawing without background. 
 * This class isn't offer in the Gral interface. It is used for Prompt labels which overlay an input field.
 * 
 * @author Hartmut Schorrig
 *
 */
public class SwtTransparentLabel extends Canvas
{
  String text = "";
  
  Font font = null;
  
  public SwtTransparentLabel(Composite parent, int style)
  {
    super(parent, style | SWT.TRANSPARENT);
    addPaintListener(paintListener);
  }
  
  
  public void setText (String string) {
    text = string;
  }
  
  public void setFont (Font font) {
    this.font = font; 
  }
  
  
  public void setBackground (Color color) {
    if (color != null) {
    }
  }

  
  public String getText(){ return text; }
  
  
  
  /**The listener for paint events. It is called whenever the window is shown newly. */
  protected PaintListener paintListener = new PaintListener()
  {

    @Override
    public void paintControl(PaintEvent e) {
      // TODO Auto-generated method stub
      GC gc = e.gc;
      if(font !=null){ gc.setFont(font); }
      //gc.setAlpha(100);
      gc.drawString(text, 0, 0, true);
    }
    
  };
  
  
  
}

package org.vishia.gral.awt;

import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWidgImpl_ifc;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralRectangle;


public class AwtSubWindow extends GralWindow.GraphicImplAccess implements GralWidgImpl_ifc
{
  
  protected final Frame window;

  private final boolean isMainWindow;
  
  public AwtSubWindow(AwtWidgetMng mng, GralWindow wdgGral, boolean isMainWindow)
  { super(wdgGral, GralMng.get());
    this.isMainWindow = isMainWindow;
    window = new Frame(getTitle());
    int xPos = 100; int yPos = 50; int xSize = 640; int ySize = 480;
    window.setBounds(xPos, yPos, xSize, ySize);
    window.setVisible(true);
    window.setLayout(null);
    window.addWindowListener(windowClosingAdapter);
    window.addWindowListener(windowListener);
    //window.add
    
  }


  @Override
  public GralRectangle getPixelPositionSize()
  {
    Rectangle r = window.getBounds();
    // TODO Auto-generated method stub
    return new GralRectangle(r.x, r.y, r.height, r.width);
  }


  /*@Override public GralRectangle getPixelSize(){
    Dimension r = ((Component)panelComposite).getSize();
    GralRectangle posSize = new GralRectangle(0, 0, r.width, r.height);
    return posSize;
  }*/



  
  

  @Override
  public void setBoundsPixel(int x, int y, int dx, int dy)
  { window.setBounds(x,y,dx,dy);
  }
  
  

  @Override
  public boolean setFocusGThread()
  {
    // TODO Auto-generated method stub
    return false;
  }


  @Override
  public void removeWidgetImplementation()
  {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void repaintGthread() {
    // TODO Auto-generated method stub
    
  }


  @Override public Object getWidgetImplementation()
  {
    // TODO Auto-generated method stub
    return window;
  }
  
  
  

  WindowListener windowListener = new WindowListener()
  {

    @Override public void windowOpened(WindowEvent e)
    {
      // TODO Auto-generated method stub
      
    }

    @Override public void windowClosing(WindowEvent e)
    {
      // TODO Auto-generated method stub
      
    }

    @Override public void windowClosed(WindowEvent e)
    {
      // TODO Auto-generated method stub
      
    }

    @Override public void windowIconified(WindowEvent e)
    {
      // TODO Auto-generated method stub
      
    }

    @Override public void windowDeiconified(WindowEvent e)
    {
      // TODO Auto-generated method stub
      
    }

    @Override public void windowActivated(WindowEvent e)
    {
      // TODO Auto-generated method stub
      
    }

    @Override public void windowDeactivated(WindowEvent e)
    {
      // TODO Auto-generated method stub
      
    }
  };
  
  
  
  @SuppressWarnings("synthetic-access") 
  WindowListener windowClosingAdapter = new WindowAdapter()
  {

    public void windowClosing(WindowEvent event)
    {
      event.getWindow().setVisible(false);
      event.getWindow().dispose();
      if (isMainWindow)
      {
        System.exit(0);
      }
    }
  };

}

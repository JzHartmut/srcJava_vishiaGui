package org.vishia.gral.awt;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.IOException;

import org.eclipse.swt.graphics.Point;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWidgImplAccess_ifc;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.util.Debugutil;


public class AwtSubWindow extends GralWindow.WindowImplAccess implements GralWidgImplAccess_ifc
{
  
  protected final Frame window;

  private final boolean isMainWindow;
  
  public AwtSubWindow(GralWindow wdgGral)
  { super(wdgGral);
    int windProps = super.getWindowProps();
    this.window = new Frame(getTitle());
    Component wdgc = window;
    
    int xPos = 100; int yPos = 50; int xSize = 640; int ySize = 480;
    window.setBounds(xPos, yPos, xSize, ySize);
    window.setVisible(true);
    window.setLayout(null);
    if((windProps & GralWindow.windIsMain)!=0) {
      this.isMainWindow = true;
      //window.addKeyListener(AwtWidgetMng.mainKeyListener);
    } else {
      this.isMainWindow = false;
    }
    window.addComponentListener(componentListener);
    window.addHierarchyBoundsListener(hierarchyBoundsListener);
    //window.addContainerListener(containerListener);
    //window.addMouseMotionListener(mousMotionListener);
    //window.addMouseListener(mouseListener);
    window.addWindowListener(new GralAwtWindowAdapter());
    //window.addWindowListener(windowClosingAdapter);
    //window.addWindowListener(windowListener);
    //window.add
    assert(gralWindow.mainPanel !=null);
    super.wdgimpl = this.window;                 // in GralWidget.ImplAccess
    //gralWindow.mainPanel.createImplWidget_Gthread();  //extra panel because the Shell is only a simple Composite

  }


  @Override
  public GralRectangle getPixelPositionSize()
  {
    Rectangle r = window.getBounds();
    Debugutil.stop();
    return new GralRectangle(r.x, r.y, r.width, r.height);
  }





  
  

  @Override
  public void setBoundsPixel(int x, int y, int dx, int dy)
  { window.setBounds(x,y,dx,dy);
  }
  
  

  @Override
  public boolean setFocusGThread()
  {
    Debugutil.stop();
    return false;
  }


  /**Sets the implementation widget vible or not.
   * @see org.vishia.gral.base.GralWidgImplAccess_ifc#setVisibleGThread(boolean)
   */
  @Override public void setVisibleGThread(boolean bVisible){ super.setVisibleState(bVisible); window.setVisible(bVisible); }


  @Override
  public void removeWidgetImplementation()
  {
    Debugutil.stop();
    
  }


  @Override
  public void redrawGthread() {
    Debugutil.stop();
    
  }


  @Override public Object getWidgetImplementation()
  {
    Debugutil.stop();
    return window;
  }
  
  
  

  @Override
  public void updateValuesForAction() {
    // TODO Auto-generated method stub
    
  }




  @Override public void reportAllContentImpl ( Appendable out ) throws IOException {
    // TODO Auto-generated method stub
    
  }




  WindowListener windowListener = new WindowListener()
  {

    @Override public void windowOpened(WindowEvent e)
    {
      Debugutil.stop();
      
    }

    @Override public void windowClosing(WindowEvent e)
    {
      Debugutil.stop();
      
    }

    @Override public void windowClosed(WindowEvent e)
    {
      Debugutil.stop();
      
    }

    @Override public void windowIconified(WindowEvent e)
    {
      Debugutil.stop();
      
    }

    @Override public void windowDeiconified(WindowEvent e)
    {
      Debugutil.stop();
      
    }

    @Override public void windowActivated(WindowEvent e)
    {
      Debugutil.stop();
      
    }

    @Override public void windowDeactivated(WindowEvent e)
    {
      Debugutil.stop();
      
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

  
  
  public class GralAwtWindowAdapter
  implements WindowListener, WindowStateListener, WindowFocusListener
  {

    @Override public void windowGainedFocus(WindowEvent e)
    {
      Debugutil.stop();
      
    }

    @Override public void windowLostFocus(WindowEvent e)
    {
      Debugutil.stop();
      
    }

    @Override public void windowStateChanged(WindowEvent e)
    {
      Debugutil.stop();
      System.out.println("state changed");
           
    }

    @Override public void windowOpened(WindowEvent e)
    {
      Debugutil.stop();
      
    }

    @Override public void windowClosing(WindowEvent event)
    {
      event.getWindow().setVisible(false);
      event.getWindow().dispose();
      if (isMainWindow)
      {
        System.exit(0);
      }
    }

    @Override public void windowClosed(WindowEvent e)
    {
      Debugutil.stop();
      
    }

    @Override public void windowIconified(WindowEvent e)
    {
      Debugutil.stop();
      
    }

    @Override public void windowDeiconified(WindowEvent e)
    {
      Debugutil.stop();
      
    }

    @Override public void windowActivated(WindowEvent e)
    {
      Debugutil.stop();
      System.out.println("AwtWindow: activated");
      
    }

    @Override public void windowDeactivated(WindowEvent e)
    {
      Debugutil.stop();
      
    }
    
  }
  
  
  MouseMotionListener mousMotionListener = new MouseMotionListener()
  {

    @Override public void mouseDragged(MouseEvent e)
    {
      Debugutil.stop();
      System.out.println("AwtWindow: mouseDragged");
      
    }

    @Override public void mouseMoved(MouseEvent e)
    {
      Debugutil.stop();
      //System.out.println("AwtWindow: mouseMoved");
      
    }
    
  };

  
  MouseListener mouseListener = new MouseListener()
  {

    @Override public void mouseClicked(MouseEvent e)
    {
      System.out.println("AwtWindow: mouseclicked");
      
    }

    @Override public void mousePressed(MouseEvent e)
    {
      System.out.println("AwtWindow: mousePressed");
      
    }

    @Override public void mouseReleased(MouseEvent e)
    {
      System.out.println("AwtWindow: mouseReleased");
      
    }

    @Override public void mouseEntered(MouseEvent e)
    {
      System.out.println("AwtWindow: mouseEntered");
      
    }

    @Override public void mouseExited(MouseEvent e)
    {
      System.out.println("AwtWindow: mouseExited");
      
    }

    
  };
  
  
  //ContainerListener containerListener;
  
  
  HierarchyBoundsListener hierarchyBoundsListener = new HierarchyBoundsListener()
  {

    @Override public void ancestorMoved(HierarchyEvent e)
    {
      System.out.println("AwtWindow: hierarchy-anchestorMoved");
      
    }

    @Override public void ancestorResized(HierarchyEvent e)
    {
      System.out.println("AwtWindow: hierarchy-anchestorResized");
      
    }
    
  };
  
  
  /**The componentListener is the resize listener especially.
   * 
   */
  @SuppressWarnings("synthetic-access") 
  ComponentListener componentListener = new ComponentListener()
  {
    
    @Override public void componentShown(ComponentEvent e)
    {
      System.out.println("AwtWindow: component shown");
      
    }
    
    
    
    @Override public void componentResized(ComponentEvent e)
    {
      System.out.println("AwtWindow: component resized");
      //invoke resize see swt.
    }
    
    
    
    @Override public void componentMoved(ComponentEvent e)
    {
      System.out.println("AwtWindow: component moved");
      
    }
    
    
    
    @Override public void componentHidden(ComponentEvent e)
    {
      System.out.println("AwtWindow: component hidden");
      
    }
  };
  
  
}

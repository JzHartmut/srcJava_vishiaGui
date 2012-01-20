package org.vishia.gral.swt;


import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.KeyCode;

public class SwtPrimaryWindow extends SwtSubWindow implements GralPrimaryWindow_ifc //GralWindowMng implements GralWindow_ifc
{
  //protected final Display displaySwt; 

  /**Version and history.
   * <ul>
   * <li>2011-11-27 Hartmut chg: {@link #addMenuItemGThread(String, String, GralUserAction)} moved from
   *   {@link SwtPrimaryWindow} to this, because the capability to have a menu bar may needed on a sub-window too.
   * <li>2011-11-12 Hartmut chg: {@link #addMenuItemGThread(String, String, GralUserAction)} now only
   *   with a GralUserAction. The Swt-internal SelectionListener action is not supported up to now.
   * <li>2011-11-10 Hartmut chg: move all files from mainGuiSwt to gral.swt, dissolve of package mainGuiSwt
   * <li>2011-11-09 Hartmut chg: Renamed from PrimaryWindowSwt to SwtPrimaryWindow
   * <li>2011-11-02 Hartmut chg: Some improvements,  Menus now have a GralWidget as data too, 
   *   GralUserAction called with the widget to detect what menu is pressed.
   * <li>2011-10-29 Hartmut chg: Now the build of the primary window is simplified. A new class 
   *   GralGraphicThread and its implementation SwtGraphicThread is created.
   * <li>2011-10-20 Hartmut chg: Order of initialization reviewed, works now, but not ready documented and checked in detail.
   * <li>2011-10-15 Hartmut new {@link #removeWidgetImplementation()}  
   * <li>2011-09-18 Hartmut chg: improved,  Now a PrimaryWindow and a SubWindow are defined.  
   * <li>2011-09-11 Hartmut created: A new class PrimaryWindowSwt contains only things for a primary Window, 
   *   in Swt with a new Display and a new Shell. The code is dissolved from MainCmdSwt, which references 
   *   that new class yet. Therefore a GralDeviceSwt is unnecessary now. 
   *   The GralDevice is the primary window always. TODO rename GralDevice to GralPrimaryWindow
   * </ul>
   */
  @SuppressWarnings("hiding")
  public final static int version = 0x20111127;
  
  /** The frame of the Window in the GUI (Graphical Unit Interface)*/
  //protected Shell graphicFrame;

  /** */
  final SwtGraphicThread graphicThreadSwt;
  

  
  private SwtPrimaryWindow(GralWidgetMng gralMng, SwtGraphicThread graphicThread, Display displaySwt)
  { super("primaryWindow", GralWindow.windHasMenu, graphicThread.windowSwt, gralMng);
    //super(gralMng, graphicThread);
    this.graphicThreadSwt = graphicThread;  //refers SWT type
  }  
  
  
  public static SwtPrimaryWindow create(LogMessage log, String sTitle, int left, int top, int xSize, int ySize)
  { SwtGraphicThread graphicThread = new SwtGraphicThread(sTitle, left, top, xSize, ySize);
    //GuiThread graphicThread = startGraphicThread(init);  

    synchronized(graphicThread){
      while(graphicThread.guiThreadId == 0){
        try{ graphicThread.wait(1000);} catch(InterruptedException exc){}
      }
    }
    //The propertiesGuiSwt needs the Display instance for Font and Color. Therefore the graphic thread with creation of Display should be executed before. 
    SwtProperties propertiesGui = new SwtProperties(graphicThread.displaySwt, 'C');
    GralWidgetMng gralMng = new SwtWidgetMng(propertiesGui, null, log);
    
    //The PrimaryWindowSwt is a derivation of the GralPrimaryWindow. It is more as only a SWT Shell.
    SwtPrimaryWindow instance = new SwtPrimaryWindow(gralMng, graphicThread, graphicThread.displaySwt);
    instance.panelComposite = graphicThread.windowSwt; //window.sTitle, window.xPos, window.yPos, window.xSize, window.ySize);
    gralMng.setGralDevice(graphicThread);
    gralMng.registerPanel(instance);
    
    //init.setWindow(instance);  //now the initializing of the window occurs.
    return instance;
  }
  
  

  
  
  /*
  @Override protected Object initGraphic() //String sTitle, int left, int top, int xSize, int ySize)
  { ///
    guiThreadId = Thread.currentThread().getId(); ///
    displaySwt.addFilter(SWT.Close, windowsCloseListener);
    graphicFrame = new Shell(displaySwt); //, SWT.ON_TOP | SWT.MAX | SWT.TITLE);
    graphicFrame.addKeyListener(keyListener);
    
    //graphicFramePos = new Position(graphicFrame.getContentPane());
    //graphicFramePos.set(0,0,xSize,ySize);
    // main = this;
    graphicFrame.setText(sTitle);
    //graphicFrame.getContentPane().setLayout(new BorderLayout());
    //graphicFrame.addWindowListener(new WindowClosingAdapter(true));
    //graphicFrame.setSize( xSize, ySize );
    if(xSize == -1 || ySize == -1){
      graphicFrame.setFullScreen(true);
    } else {
      graphicFrame.setBounds(xPos,yPos, xSize, ySize );  //Start position.
    }
    graphicFrame.open();
    graphicFrame.setVisible( true ); 

    //graphicFrame.getContentPane().setLayout(new FlowLayout());
    graphicFrame.setLayout(null);
    graphicFrame.addShellListener(mainComponentListerner);
    
    return graphicFrame;
    
  }
  */
  
  
  /**Sets the title and size before initialization.
   * @param sTitle
   * @param xSize
   * @param ySize
  @Override public void buildMainWindow(String sTitle, int left, int top, int xSize, int ySize)
  { this.xSize = xSize;
    this.ySize = ySize;
    this.xPos = left;
    this.yPos = top;
    if(bStarted){
      if(xSize < 0 || ySize < 0){
        graphicFrame.setFullScreen(true);
      } else {
        graphicFrame.setBounds(left,top, xSize, ySize );  //Start position.
      }  
      graphicFrame.setText(sTitle);
    } else {
      synchronized(guiThread){
        if(!bStarted){
          this.sTitle = sTitle;
          guiThread.notify();     //Run the GUI Thread
        }
      }
      synchronized(this){
        while(!bStarted){
          bWaitStart = true;
          try{ wait(10000);  } catch(InterruptedException exc){}   //Await the GUI Thread
        }
      }
    }    
  }
   */
  
  
  private static class CntSleep
  {
    long lastTime;
    long minMillisecBetweenSleep= Long.MAX_VALUE;
    long maxMillisecBetweenSleep= 0;
    double avMillisecBetweenSleep = 0.0;
    double percentActive = 0.0;
  };
  
  private CntSleep cntSleep;
  
  
  
  //public boolean isWakedUpOnly(){ return graphicThread.isWakedUpOnly; }
  
  public void terminate()
  {
    if(graphicThreadSwt.isRunning() && !graphicThreadSwt.displaySwt.isDisposed()){ 
      graphicThreadSwt.displaySwt.dispose();  //forces windowsCloseListener and exit=true
    }  

  }
  
  @Override public boolean isWindowsVisible()
  { return graphicThreadSwt.windowSwt.isVisible();
  }


  @Override
  public void setWindowVisible(boolean visible)
  {
    graphicThreadSwt.windowSwt.setVisible(visible);
  }
  
  
  
  
  
  void stop()
  { //to set breakpoint
  }

  



  @Override
  public MainCmd_ifc getMainCmd()
  {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public Shell getitsGraphicFrame()
  {
    // TODO Auto-generated method stub
    return (graphicThreadSwt).windowSwt;
  }



  @Override
  public void setStandardMenusGThread(File openStandardDirectory, GralUserAction actionFile)
  {
    // TODO Auto-generated method stub
    
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

  @Override public void repaint(){  graphicThreadSwt.windowSwt.redraw(); graphicThreadSwt.windowSwt.update(); }

  
  public void removeWidgetImplementation()
  {
    graphicThreadSwt.windowSwt.dispose();
    graphicThreadSwt.windowSwt = null;
  }


  @Override public Composite getPanelImpl() { return graphicThreadSwt.windowSwt; }

  @Override public GralRectangle getPixelPositionSize(){
    Rectangle r = graphicThreadSwt.windowSwt.getBounds();
    GralRectangle posSize = new GralRectangle(r.x, r.y, r.width, r.height);
    return posSize;
  }

  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { graphicThreadSwt.windowSwt.setBounds(x,y,dx,dy);
  }
  

  @Override public void closeWindow()
  { 
    terminate();
  }
  
  
  ControlListener resizeListener = new ControlListener()
  { @Override public void controlMoved(ControlEvent e) 
    { //do nothing if moved.
    }

    @Override public void controlResized(ControlEvent e) 
    { if(resizeAction !=null){
        resizeAction.userActionGui(0, null);
      }
    }
  };
  
  

}

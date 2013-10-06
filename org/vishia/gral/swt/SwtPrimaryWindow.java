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
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.KeyCode;

public class SwtPrimaryWindow extends SwtSubWindow implements GralPrimaryWindow_ifc //GralWindowMng implements GralWindow_ifc
{
  //protected final Display displaySwt; 

  /**Version and history.
   * <ul>
   * <li>2011-11-27 Hartmut chg: {@link #addMenuBarArea9ItemGThread(String, String, GralUserAction)} moved from
   *   {@link SwtPrimaryWindow} to this, because the capability to have a menu bar may needed on a sub-window too.
   * <li>2011-11-12 Hartmut chg: {@link #addMenuBarArea9ItemGThread(String, String, GralUserAction)} now only
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
  

  
  SwtPrimaryWindow(GralWindow windowGral, SwtMng mng, SwtGraphicThread graphicThread, Display displaySwt)
  { super(mng, windowGral);
    //super("primaryWindow", GralWindow.windHasMenu, graphicThread.windowSwt, gralMng);
    //super(gralMng, graphicThread);
    this.graphicThreadSwt = graphicThread;  //refers SWT type
  }  
  
  
  public static GralWindow create(LogMessage log, String sTitle, char sizeShow, int left, int top, int xSize, int ySize)
  { int windProps = GralWindow_ifc.windResizeable;
    GralWindow windowGral = new GralWindow("main", sTitle, windProps, null, null );
    SwtGraphicThread graphicThread = new SwtGraphicThread(windowGral, sizeShow, left, top, xSize, ySize, log);
    SwtPrimaryWindow windowSwt = new SwtPrimaryWindow(windowGral, graphicThread.gralMng, graphicThread, graphicThread.displaySwt);
    //GuiThread graphicThread = startGraphicThread(init);  

    synchronized(graphicThread){
      while(graphicThread.getThreadIdGui() == 0){
        try{ graphicThread.wait(1000);} catch(InterruptedException exc){}
      }
    }
    graphicThread.gralMng.registerPanel(windowGral);
    //gralMng = graphicThread.gralMng;
    return windowGral;
  }
  
  

  
  /*
  
  
  private static class CntSleep
  {
    long lastTime;
    long minMillisecBetweenSleep= Long.MAX_VALUE;
    long maxMillisecBetweenSleep= 0;
    double avMillisecBetweenSleep = 0.0;
    double percentActive = 0.0;
  };
  
  private CntSleep cntSleep;
  
  */
  
  //public boolean isWakedUpOnly(){ return graphicThread.isWakedUpOnly; }
  
  public void terminate()
  {
    if(graphicThreadSwt.isRunning() && !graphicThreadSwt.displaySwt.isDisposed()){ 
      graphicThreadSwt.displaySwt.dispose();  //forces windowsCloseListener and exit=true
    }  

  }
  
  
  
  
  @Override
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
  public boolean setFocusGThread()
  {
    // TODO Auto-generated method stub
    return false;
  }




  
  @Override
  public void removeWidgetImplementation()
  {
    graphicThreadSwt.windowSwt.dispose();
    graphicThreadSwt.windowSwt = null;
  }



  /**Returns the position and the size of the working area of the window on the screen.
   * The title, borders and menu bar are not regarded.
   * It uses {@link org.eclipse.swt.widgets.Shell#getBounds()} to get the position of the window
   * inclusive title, border and menu bar and {@link org.eclipse.swt.widgets.Decorations#getClientArea()}
   * to get the width and heigth of the working area. With them the position of the working area is calculated.
   * Whereby the border left, right and bottom are taken as same.  
   * @since Algorithm tuned on 2013-09-28.
   * */
  @Override public GralRectangle getPixelPositionSize(){
    Rectangle w = graphicThreadSwt.windowSwt.getBounds();
    Rectangle r = graphicThreadSwt.windowSwt.getClientArea();
    int pixelBorder = (w.width - r.width) /2;   
    int pixelTitleMenu = w.height - r.height - pixelBorder;
    int x = w.x + pixelBorder;
    int y = w.y + pixelTitleMenu;
    //returns position and size of the working area.
    GralRectangle posSize = new GralRectangle(x, y, r.width, r.height);
    return posSize;
  }

  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { graphicThreadSwt.windowSwt.setBounds(x,y,dx,dy);
  }
  

  public void closeWindow()
  { 
    terminate();
  }
  
  

}

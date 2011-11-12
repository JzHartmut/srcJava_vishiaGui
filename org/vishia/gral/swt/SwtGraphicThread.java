package org.vishia.gral.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralGridPos;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;

/**This class is the implementation class of a simple graphic implementation for SWT.
 * It doesn't depend of complex functionality of the org.vishia.gral. But that implementations based on this.
 * This class can be used for a simple SWT graphic implementation.
 */
class SwtGraphicThread extends GralGraphicThread //implements Runnable
{
  /**Version, able to read as hex yyyymmdd.
   * Changes:
   * <ul>
   * <li>2011-11-12 Hartmut chg: Now the primary window has a menu bar anyway. 
   * </ul>
   */
  public final static int version = 0x20111112;

  /**The graphical device for the application for all windows of this application. */
  Display displaySwt;
  
  /**The main window. SWT: named as Shell. */
  Shell windowSwt;
  
  /**The windows-closing event handler. It is used private only, but public set because documentation. 
   * The close event will be fired also when a SubWindow is closed. Therefore test the Shell instance.
   * Only if the main window is closed, bExit should be set to true.
   * */
  public final class WindowsCloseListener implements Listener{
    /**Invoked when the window is closed; it sets {@link #bExit}, able to get with {@link #isRunning()}.
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    @Override public void handleEvent(Event event) {
      bExit = event.widget == windowSwt; //true;
    }
  }

  /**Instance of windowsCloseHandler. */
  private final WindowsCloseListener windowsCloseListener = new WindowsCloseListener(); 
  
  KeyListener keyListener = new KeyListener()
  {
    @Override public void keyPressed(KeyEvent key)
    {
      // TODO Auto-generated method stub
      stop();
    }

    @Override public void keyReleased(KeyEvent e)
    {
      // TODO Auto-generated method stub
      
    }
    
  };
  
  
  ShellListener mainComponentListerner = new ShellListener()
  {

        
    @Override
    public void shellActivated(ShellEvent e) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void shellClosed(ShellEvent e) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void shellDeactivated(ShellEvent e) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void shellDeiconified(ShellEvent e) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void shellIconified(ShellEvent e) {
      // TODO Auto-generated method stub
      
    }
    
  };
  
  
  
  
  final String sTitle; final int xPos, yPos, xSize, ySize;
  
  SwtGraphicThread(String sTitle, int left, int top, int xSize, int ySize)
  { super();
    this.sTitle = sTitle; this.xPos = left; this.yPos = top; this.xSize = xSize; this.ySize = ySize; 
    thread.start();
    
  }
  
  @Override protected void initGraphic(){
    displaySwt = new Display();
    displaySwt.addFilter(SWT.Close, windowsCloseListener);
    windowSwt = new Shell(displaySwt); //, SWT.ON_TOP | SWT.MAX | SWT.TITLE);
    windowSwt.addKeyListener(keyListener);
    
    //graphicFramePos = new Position(graphicFrame.getContentPane());
    //graphicFramePos.set(0,0,xSize,ySize);
    // main = this;
    windowSwt.setText(sTitle);
    //
    //set a menu bar anyway. Otherwise the calculation of used area of the window may be faulty
    //if it is calculated first, and after them menu items are added.
    //It isn't able to expected that the user won't have a menu bar in a primary window.
    Menu menuBar = new Menu(windowSwt, SWT.BAR);
    windowSwt.setMenuBar(menuBar);
    //graphicFrame.getContentPane().setLayout(new BorderLayout());
    //graphicFrame.addWindowListener(new WindowClosingAdapter(true));
    //graphicFrame.setSize( xSize, ySize );
    if(xSize == -1 || ySize == -1){
      windowSwt.setFullScreen(true);
    } else {
      windowSwt.setBounds(xPos,yPos, xSize, ySize );  //Start position.
    }
    windowSwt.open();
    windowSwt.setVisible( true ); 

    //graphicFrame.getContentPane().setLayout(new FlowLayout());
    windowSwt.setLayout(null);
    windowSwt.addShellListener(mainComponentListerner);
    //
    ///
  }

  

  
  protected boolean dispatchOsEvents()
  { return displaySwt.readAndDispatch();
  }

  protected void graphicThreadSleep()
  {
    displaySwt.sleep ();
  }
  
  public void wakeup(){
    displaySwt.wake();
    extEventSet.set(true);
    isWakedUpOnly = true;
  }

  
  
  void stop(){}
  
}

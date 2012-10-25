package org.vishia.gral.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.base.GralMng;
import org.vishia.msgDispatch.LogMessage;

/**This class is the implementation class of a simple graphic implementation for SWT.
 * It doesn't depend of complex functionality of the org.vishia.gral. But that implementations based on this.
 * This class can be used for a simple SWT graphic implementation.
 */
class SwtGraphicThread extends GralGraphicThread //implements Runnable
{
  /**Version, able to read as hex yyyymmdd, history and license.
   * Changes:
   * <ul>
   * <li<2012-07-14 Hartmut chg: {@link #traverseKeyFilter} now excludes [ctrl-tab]. Only [tab] is a traversal key.
   * <li>2012-04-16 Hartmut chg: {@link #initGraphic()} now creates the main window, creates the
   *   {@link SwtMng} instead it is doing in the non-graphic thread. 
   * <li>2012-04-10 Hartmut chg: Now the traversal keys ctrl-Pgdn/up are disabled.
   * <li>2011-11-12 Hartmut chg: Now the primary window has a menu bar anyway. 
   * </ul>
   * <b>Copyright/Copyleft</b>:
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL ist not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   */
  @SuppressWarnings("hiding")
  public final static int version = 20120416;

  /**The graphical device for the application for all windows of this application. */
  Display displaySwt;
  
  /**The main window. SWT: named as Shell. */
  Shell windowSwt;
  
  SwtPrimaryWindow instance;
  
  LogMessage log;
  
  GralMng gralMng;
  
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
  
  
  
  /**Disables the ctrl-pgUp and ctrl-Pgdn as traversal key listener. It should be able to use
   * by the application. Only Tab and sh-Tab are usual. */
  TraverseListener XXXkeyTraverse = new TraverseListener(){
    @Override public void keyTraversed(TraverseEvent e) {
      stop();
      if(  e.detail == SWT.TRAVERSE_PAGE_NEXT //|| e.keyCode == SWT.PAGE_DOWN){
        || e.detail == SWT.TRAVERSE_PAGE_PREVIOUS
         ) {
        e.doit = true;
  } } };
  
  
  /**This interface routine is invoked on any key which is used as 'traverse' key to switch
   * between widgets, panels etc. SWT uses the ctrl-pgup and ctrl-pgdn to switch between the
   * tab cards on a TabbedPanel. This is not a standard behavior for all graphic systems.
   * That keys should be able to use in the application. Therefore they are disabled as traversal keys.
   * To switch between the tabs - it may be application specific to do it with keys - or the mouse
   * can be used. 
   * 
   */
  Listener traverseKeyFilter = new Listener(){
    @Override public void handleEvent(Event event) {
      int traverseIdent = event.detail; 
      int key = event.keyCode;
      int keyModifier = event.stateMask;
      if(  traverseIdent == SWT.TRAVERSE_PAGE_NEXT         //the pg-dn key in SWT
        || traverseIdent == SWT.TRAVERSE_PAGE_PREVIOUS   //the pg-up key in SWT
        || key == '\t' && keyModifier == SWT.CTRL
        ) {
        event.doit = false;
      }
      stop();
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
  
  SwtGraphicThread(String sTitle, char sizeShow, int left, int top, int xSize, int ySize, LogMessage log)
  { super(sizeShow);
    this.log = log;
    this.sTitle = sTitle; this.xPos = left; this.yPos = top; this.xSize = xSize; this.ySize = ySize; 
    threadGuiDispatch.start();
  }
  
  @Override protected void initGraphic(){
    displaySwt = new Display();
    displaySwt.addFilter(SWT.Close, windowsCloseListener);
    displaySwt.addFilter(SWT.Traverse, traverseKeyFilter);
    windowSwt = new Shell(displaySwt); //, SWT.ON_TOP | SWT.MAX | SWT.TITLE);
    windowSwt.addKeyListener(keyListener);
    //windowSwt.addTraverseListener(keyTraverse);
    
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

    
    if(xSize == -1 || ySize == -1){
      windowSwt.setFullScreen(true);
    }
    //The propertiesGuiSwt needs the Display instance for Font and Color. Therefore the graphic thread with creation of Display should be executed before. 
    SwtProperties propertiesGui = new SwtProperties(this.displaySwt, sizeCharProperties);
    gralMng = new SwtMng(this, displaySwt, propertiesGui, null, log);
    
    //The PrimaryWindowSwt is a derivation of the GralPrimaryWindow. It is more as only a SWT Shell.
    instance = new SwtPrimaryWindow(gralMng, this, this.displaySwt);
    ///
  }

  

  
  @Override
  protected boolean dispatchOsEvents()
  { return displaySwt.readAndDispatch();
  }

  @Override
  protected void graphicThreadSleep()
  {
    displaySwt.sleep ();
  }
  
  @Override
  public void wakeup(){
    displaySwt.wake();
    extEventSet.set(true);
    isWakedUpOnly = true;
  }

  
  
  void stop(){}
  
}

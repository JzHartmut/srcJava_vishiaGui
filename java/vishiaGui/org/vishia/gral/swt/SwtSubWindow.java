package org.vishia.gral.swt;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWidgImplAccess_ifc;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GralWindow_setifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.Debugutil;
import org.vishia.util.KeyCode;

//public class SubWindowSwt extends GralPanelContent implements WidgetCmpnifc
//public class SwtSubWindow extends GralWindow implements SwtSetValue_ifc
/**The SwtSubWindow is the wrapper arroung a {@link Shell} to implement a {@link GralWindow}.
 * The following inheritance structure is given (hint: only readable in Javadoc, not in src, because of length of link controls
 * <pre>
 * {@link SwtSubWindow}&lt;>-->{@link SwtPanel}&lt;>-->{@link GralPanelContent.ImplAccess}&lt;>-->{@link GralWidget.ImplAccess}
 *                   |                                    |
 *                   +&lt;*>-->{@link SwtPanel#swtGralWindow}        +&lt;*>---------------------------------------+
 *                            |                                                                      ,
 *                            +&lt;*>-->{@link GralWindow.WindowImplAccess#gralWindow} : {@link GralWindow}&lt;>-->{@link GralPanelContent}&lt;>-->{@link GralWidget}
 *                                     ^
 *   {@link SwtWindowImplAccess}&lt;>-------------+
 *   
 * SwtSubWindow<>--->SwtPanel<>--->GralPanelContent.ImplAccess<>---->GralWidget.ImplAccess
 *  |                 |                              |                               +-widgg-->GralWindow<>-->GralWidget
 *  |                 |                              +-widgg : GralPanelContent      +-wdgimpl-->GralWidgetImpl_ifc<>-+
 *  |                 +-panelComposite--->
 *  |                 +-tabFolder : TabFolder
 *  |                 +-itsTabSwt : TabItem
 *  |
 *  +-swtWidgWrapper--------------->SwtWidgetHelper<------------------------------------------------------------------
 *  +-window-->swt.Shell<>-->Control<--widgetSwt-+ 
 *  +-swtPanel--->SwtPanel
 *  +-menuBar--->SwtMenu
 * </pre>
 * To access the {@link GralWindow.WindowImplAccess#gralWindow} you should use the aggregation {@link SwtPanel#swtGralWindow}
 * from the SwtPanel super class.
 * @author Hartmut Schorrig
 *
 */
public class SwtSubWindow extends GralWindow.WindowImplAccess implements GralWidgImplAccess_ifc
{

  
  /**Version, history and license:
   * <ul>
   * <li>2016-08-31 Hartmut chg: Now disposes the window in {@link #shellListener} 
   * <li>2015-05-31 Hartmut {@link #setFocusGThread()} now regards the {@link GralPanelContent#setFocusedWidget(GralWidget)}.
   * <li>2015-04-27 Hartmut new {@link #windRemoveOnClose}
   * <li>2012-07-13 Hartmut new:  {@link #getPixelSize()}, chg: {@link #getPixelPositionSize()} in all implementations. 
   *   A swt.widget.Shell now returns the absolute position and the real size of its client area without menu and title bar.
   * <li>2012-06-29 Hartmut new: {@link #setResizeAction(GralUserAction)} now for both ctors, resize on subwindow works.
   * <li>2012-03-10 Hartmut new: calls invisibleSetAction.userActionGui if the window is set invisible by pressing the X closing icon.
   * <li>2012-02-11 Hartmut chg: The menu of the window is managed now in {@link SwtMenu}. Instance refered with {@link #menuBar}
   * <li>2011-11-27 Hartmut chg: {@link #addMenuBarArea9ItemGThread(String, String, GralUserAction)} moved from
   *   {@link SwtPrimaryWindow} to this, because the capability to have a menu bar may needed on a sub-window too.
   * <li>2011-11-18 Hartmut chg: {@link SwtSubWindow#SwtSubWindow(String, Display, String, int, GralMng)}
   *   now gets an int-property instead boolean 'exclusive'. A window has more as one property. Constants
   *   are defined in {@link GralWindow#windExclusive} etc.
   * <li>2011-11-10 Hartmut chg:  move all files from mainGuiSwt to gral.swt, dissolve of package mainGuiSwt
   * <li>2011-11-09 Hartmut chg: renamed from SubWindowSwt to SwtSubWindow
   * <li>2011-11-01 Hartmut new: {@link #shellListener} and {@link #disposeListener} added, but empty yet.
   * <li>2011-10-31 Hartmut new: {@link #mouseListener} and {@link #setMouseAction(GralUserAction)}.
   * <li>2011-10-30 Hartmut new: {@link #resizeListener}, {@link #setResizeAction(GralUserAction)} etc:
   *   Not this class is base of {@link SwtPrimaryWindow} and contains this capabilities yet.
   * <li>2011-10-30 Hartmut new: {@link #getPixelPositionSize()} and {@link #setBoundsPixel(int, int, int, int)}
   *   to support pixel-orientation too. Note it isn't a concept of GRAL anyway, but it is needed.
   * <li>2011-10-20 Hartmut chg: {@link GralMng} as parameter of constructor: The {@link GralWidget}
   *   refers it, it should be initialized.
   * <li>2011-10-15 Hartmut new: {@link #removeWidgetImplementation()}.
   * <li>2011-09-23 Hartmut bugfix: setFocus() called in {@link #setWindowVisible(boolean)}, it hasn't
   *   the focus automatically.
   * <li>2011-09-18 Hartmut chg: Now it is a SubWindowSwt and inherits from the new {@link GralWindow}.
   *   Some methods {@link #setBackColor(GralColor, int)}, {@link #setWindowVisible(boolean)} etc added
   *   to comply the interface and super class definitions.
   * <li>2011-09-03 Hartmut creation as wrapper arround a SWT.Shell inherits {@link org.vishia.gral.base.GralPanelContent}.
   * </ul>
   * <br><br> 
   * <b>Copyright/Copyleft</b>:
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   */
  @SuppressWarnings("hiding")
  public static final String sVersion = "2016-08-31";
  
  /**It contains the association to the swt widget (Control) and the {@link SwtMng}
   * and implements some methods of {@link GralWidgImplAccess_ifc} which are delegate from this.
   */
  SwtWidgetHelper swtWidgWrapper;
  
  
  protected Shell window;
  
  
  protected SwtMenu menuBar;
  
  /**State of the window in swt. */
  protected boolean bFullScreen;
  
  /**Some flags to set an action listener a first time if the action is given. */
  private boolean bHasResizeAction, bHasMouseAction;
  
  
  

  
  
  //private final CloseListener closeListener;
  

  
  /**Constructs a window (primary or sub window maybe for dialog etc.).
   * @param name
   * @param display
   * @param title
   * @param windProps
   * @param gralMng
   * @throws IOException 
   */
  SwtSubWindow(SwtMng mng, GralWindow gralWindow) //throws IOException
  //SwtSubWindow(String name, Display display, String title, int windProps, GralMng gralMng)
  { //super(name, windProps, gralMng, null);
    super(gralWindow );  //Invoke constructor of the super class, with knowledge of its outer class.
    int props = 0; ////|SWT.CLOSE;
    String sTitle = this.getTitle();
    int windProps = super.getWindowProps();
    if(sTitle !=null){ 
      props |= SWT.TITLE | SWT.BORDER | SWT.CLOSE | SWT.MIN | SWT.MAX; 
    } else {
      if((windProps & GralWindow.windHasMenu) !=0) throw new IllegalArgumentException("Window without title but with menu is not supported");
    }
    //if(exclusive){ props |= SWT.PRIMARY_MODAL | SWT.SYSTEM_MODAL | SWT.APPLICATION_MODAL; }
    if((windProps & GralWindow.windExclusive) !=0){ 
      assert((windProps & GralWindow.windConcurrently) ==0);
      props |= SWT.APPLICATION_MODAL; 
    } else {
      assert((windProps & GralWindow.windExclusive) ==0);
    }
    if((windProps & GralWindow.windOnTop) !=0){ props |= SWT.ON_TOP; }
    if((windProps & GralWindow.windResizeable) !=0){ props |= SWT.RESIZE; }
    //
    //create the window:
    window = new Shell(mng.displaySwt, props);
    super.wdgimpl = this.swtWidgWrapper = new SwtWidgetHelper(window, mng);
    GralRectangle rect = mng.calcPositionOfWindow(gralWindow.pos());
    window.setBounds(rect.x, rect.y, rect.dx, rect.dy );
    if(rect.dx == -1)  { //TODO
      window.setMaximized(true);
    }    
    
    //window.addPaintListener(windowPaintListener);
    window.addShellListener(shellListener);
    if((windProps & GralWindow.windIsMain)!=0) {
      window.addDisposeListener(disposeListenerMainWindow);
    } else {
      window.addDisposeListener(disposeListener);
    }
    window.addFocusListener(mng.focusListener);
    //window.add
    //if((windProps & GralWindow.windHasMenu) !=0){
    GralMenu menu = getMenubar();
    if(menu !=null){
      //this.mngImpl.createContextMenu(menu);
      SwtMenu swtMenu = new SwtMenu(gralWindow, menu, window);
//      Menu menuBarSwt = new Menu(window, SWT.BAR);
//      window.setMenuBar(menuBarSwt);
    }
    //super.panelComposite = window;
    if(sTitle !=null){ window.setText(sTitle); }
    if(!bHasResizeAction && super.resizeAction() != null){
      window.addControlListener(resizeListener);  //This listener calls the resizeAction
    }
    //this.checkCreateTabFolder(this.window, mng);
    SwtMng.storeGralPixBounds(this, this.window);
    this.widgg.toString(System.out);
    this.window.setVisible(this.widgg.isVisible());        // sets the window visible due to the Gral state.
    //super.checkCreateTabFolder(this.window, mng);
    assert(gralWindow.mainPanel !=null);
    //if(gralWindow.mainPanel.isTabbed()) {
    //  gralWindow.mainPanel.createImplWidget_Gthread();  //extra panel because the Shell is only a simple Composite
//    } else {
//      gralWindow.mainPanel._wdgImpl = new SwtPanel(gralWindow.mainPanel, window);
//    }
    
    
    /* test
    Label testLabel = new Label(window, 0);
    testLabel.setText("T");
    testLabel.setBounds(0,0,10,10);
    Rectangle bounds = testLabel.getBounds();
    Point size = window.getSize();
    Rectangle windowPos = window.getBounds();
    Rectangle clientAread = window.getClientArea();
    */
  }
  
  /*
  SwtSubWindow(String name, int windProps, Shell shell, GralMng gralMng)
  { super(name, windProps, gralMng, shell);
    this.window = shell;
    setResizeAction(actionResizeOnePanel);
    
  }
  */
  
  
  /**The Swt widget implementation is either the Shell itself which is a Composite,
   * or it is the TabFolder, which is created as only one Composite inside this shell composite. 
   * Both are referenced via the abstraction class {@link SwtPanel#panelSwtImpl}.
   */
  @Override public Object getWidgetImplementation(){ 
    return window;
    //return window; 
  }
  
  
  
  
  
  
  @Override
  public void removeWidgetImplementation()
  {
    window.dispose();
    window = null;
  }

  
  @Override public GralRectangle getPixelPositionSize(){
    return swtWidgWrapper.getPixelPositionSize();
  }



  /**Returns the size of the working area without border, menu and title of the window.
   * Uses {@link org.eclipse.swt.widgets.Scrollable#getClientArea()}  to get the size.
   */

  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { window.setBounds(x,y,dx,dy);
  }
  
  
  
  
  void stop(){}

  @Override
  public boolean setFocusGThread()
  { 
    //setVisibleState(true);  //has focus, 
    this.window.setVisible(true);
    this.window.setFocus();
    return true;
  }

  @Override public void setVisibleGThread(boolean bVisible) { 
    this.window.open();                                    // on primary window
    super.setVisibleState(bVisible); 
    this.window.setVisible(bVisible); 
  }

  @Override public void redrawGthread() {
    int chg = getChanged();
    int acknChg = 0;
    if(!this.bHasResizeAction && this.resizeAction() != null){
      this.window.addControlListener(this.resizeListener);
    }
    if(!this.bHasMouseAction && this.mouseAction() != null){
      this.window.addControlListener(this.resizeListener);
    }
    if(super.shouldClose()){
      this.window.close();
    } 
    if((chg & chgText)!=0){
      acknChg |= chgText;   //sets the title of the window
      this.window.setText(dyda().displayedText);;
    }
    if((chg & chgVisible)!=0){
      acknChg |= chgVisible;
      setFocusGThread();
    }
    if((chg & chgInvisible)!=0){
      acknChg |= chgInvisible;
      this.window.setVisible(false);
    }
    if(this.bFullScreen != super.isFullScreen()){
      window.setFullScreen(bFullScreen = super.isFullScreen());
    }
    acknChanged(acknChg);
    this.window.update();
    this.window.redraw();
    SwtMng.storeGralPixBounds(this, this.window);
    //swtWindow_setifc.repaintGthread(); 
  }

  @Override
  public void updateValuesForAction() {
    // TODO Auto-generated method stub
    
  }

  
  /**Reports the tree of content of the window from view of swt {@link Composite#getChildren()}
   * @param out 
   * @throws IOException
   */
  @Override public void reportAllContentImpl ( Appendable out) throws IOException {
    GralPanelContent mainPanel = super.gralWindow.mainPanel;
    SwtPanel swtPanel = (SwtPanel)mainPanel.getImplAccess();
    Composite swtComposite = swtPanel.panelSwtImpl;
    if(swtComposite != this.window) {
      Control[] content = this.window.getChildren();
      if(content.length !=1 || content[0] != swtComposite ) {
        out.append("bad Shell content ...");
      }
    }
    SwtPanel.reportAllContentImpl(swtComposite, out);
  }

  
  
  protected PaintListener windowPaintListener = new PaintListener() {

    @Override public void paintControl ( PaintEvent e ) {
      // TODO Auto-generated method stub
      Debugutil.stop();
      System.out.println("SwtSubWindow-PaintListener");
    }
    
  };
  
  


  @SuppressWarnings("synthetic-access") 
  protected ShellListener shellListener = new ShellListener(){

    @Override
    public void shellActivated(ShellEvent e)
    {
      stop();
      
    }

    /**Sets the Window invisible instead dispose it. Note that all sub windows should be build 
     * as static instances. They should be set visible and invisible instead remove and build new.
     * @see org.eclipse.swt.events.ShellListener#shellClosed(org.eclipse.swt.events.ShellEvent)
     */
    @Override public void shellClosed(ShellEvent e) ////
    { int windProps = SwtSubWindow.this.getWindowProps();
      if(SwtSubWindow.this.actionOnCloseWindow() !=null){
        SwtSubWindow.this.actionOnCloseWindow().exec(KeyCode.menuEntered, SwtSubWindow.this.gralWindow);
      }
      setVisibleState(false);
      if((windProps & GralWindow_ifc.windRemoveOnClose)!=0) {
        window.dispose();
        window = null;
        menuBar = null;
        SwtSubWindow.this.widgg._wdgImpl = null;  //therewith garbage this class.

        SwtSubWindow.this.gralWindow.remove();  //remove the window as widget.
        GralWindow windg = (GralWindow)SwtSubWindow.this.widgg;
        if( windg == windg.gralMng.getPrimaryWindow()) {
          windg.gralMng.closeApplication();
        }
        e.doit = true;
      } else {
        e.doit = false;
        if((windProps & GralWindow_ifc.windMinimizeOnClose)!=0) {
          ((Shell)e.widget).setMinimized(true); //set active with operation-system handling possible.
        } else {
          ((Shell)e.widget).setVisible(false);  //there should be an action to set visible again.
        }
      }
    }

    @Override
    public void shellDeactivated(ShellEvent e)
    {
      if((SwtSubWindow.this.getWindowProps() & GralWindow_ifc.windRemoveOnClose) !=0) {
      }
    }

    @Override
    public void shellDeiconified(ShellEvent e)
    {
      setVisibleState(true);
    }

    @Override
    public void shellIconified(ShellEvent e)
    {
      setVisibleState(false);
    }
    
  };
  
  
  
  private final DisposeListener disposeListener = new DisposeListener(){
    
    
    @Override
    public void widgetDisposed(DisposeEvent e)
    {
    }
  };
  
  
  private final DisposeListener disposeListenerMainWindow = new DisposeListener(){
    @Override
    public void widgetDisposed(DisposeEvent e)
    {
      GralMng gralMng = SwtSubWindow.this.swtWidgWrapper.mng.gralMng;
      gralMng.closeImplGraphic();                          // this forces exit of the graphic thread.
      // whats happen with the application, is programmed in the main thread. 
    }
  };
  
  
  /**The mouseListener will be activated if {@link #setMouseAction(GralUserAction)} will be called.
   * It calls this user action on resize. */
  MouseListener mouseListener = new MouseListener()
  {
    int captureAreaDivider;
    
    @Override public void mouseDoubleClick(MouseEvent e) 
    { GralRectangle pos = new GralRectangle(e.x, e.y, 0,0);
      int key = KeyCode.mouse1Double;  //TODO select key, alt, ctrl etc.
      SwtSubWindow.this.mouseAction().exec(key, SwtSubWindow.this.gralWindow, pos);
    }

    @Override public void mouseDown(MouseEvent e) 
    { GralRectangle pos = new GralRectangle(e.x, e.y, 0,0);
      int key = KeyCode.mouse1Down;  //TODO select key, alt, ctrl etc.
      SwtSubWindow.this.mouseAction().exec(key, SwtSubWindow.this.gralWindow, pos);
    }

    @Override public void mouseUp(MouseEvent e) 
    { GralRectangle pos = new GralRectangle(e.x, e.y, 0,0);
      int key = KeyCode.mouse1Up;  //TODO select key, alt, ctrl etc.
      SwtSubWindow.this.mouseAction().exec(key, SwtSubWindow.this.gralWindow, pos);
    }
    
  };
  
  
  class ActionUserMenuItem implements SelectionListener
  { 
    final GralUserAction action;
    
    public ActionUserMenuItem(GralUserAction action)
    { this.action = action;
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
      // TODO Auto-generated method stub
      
    }
  
    @Override
    public void widgetSelected(SelectionEvent e)
    { Object oWidgSwt = e.getSource();
      final GralWidget widgg;
      if(oWidgSwt instanceof Widget){
        Widget widgSwt = (Widget)oWidgSwt;
        Object oGralWidg = widgSwt.getData();
        if(oGralWidg instanceof GralWidget){
          widgg = (GralWidget)oGralWidg;
        } else { widgg = null; }
      } else { widgg = null; }
      action.userActionGui(KeyCode.menuEntered, widgg);
    }
  }
  
  


  
  /**The resizeListener is always associated to the Shell (Swt Window). 
   * It checks whether the Shell has only one child, which is usual a Composite (a GralPanel).
   * Then this Composite will be resized with the same size. 
   * <br>
   * The resizing of the Composite will force calling the {@link SwtPanel#resizeItemListener}
   * which is the listener for the Composite to resize the content. See there.
   * <br>
   * Resizing of the Window can call a user defined resize action. 
   * This can be defined from user level with {@link GralWindow#setResizeAction(GralUserAction)}.
   * The user's action is optional, it is not necessary for resizing itself (changed in 2022-09).
   * History: before 2022-09 this action does the resizing itself. 
   */
  private final ControlListener resizeListener = new ControlListener()
  { @Override public void controlMoved(ControlEvent e) 
    { //do nothing if moved.
    }
  
    @Override public void controlResized(ControlEvent e) 
    { 
      Shell window = SwtSubWindow.this.window;
      Object widgg = window.getData();
      if(widgg instanceof GralPanelContent.ImplAccess) {
        Debugutil.stop();  //TODO same algorithm as on SwtPanel
      } else {
        Control[] children = window.getChildren();          //Note: on first call only after creation of Shell it has no children.
        for(Control child: children) {
          if(children.length==1 && child instanceof Composite) {
//            Object widgachild = child.getData();
//            if(widgachild instanceof GralPanelContent.ImplAccess) {
              //GralUserAction resizeAction = ((GralPanelContent.ImplAccess)widgachild).resizeAction();
              Debugutil.stop();
//              Rectangle xy = window.getBounds();           // x and y is the absolute position on screen
//              xy.x = 0;                                    // relative position inside the window is necessary.
//              xy.y = 0;                                    // relative from left top [0,0] to given size
              Rectangle xy = window.getClientArea();
              child.setBounds(xy);                         // call its resize as Swt-listener.
//            } else {
//              Rectangle xy = window.getBounds();
//              child.setBounds(xy);     //call its resize as Swt-listener.
//            }
          } else {
            // the window may be a GralPanelContent.ImplAccess
            // this is quest above.
            Debugutil.stop(); //TODO
          }
        }
      }
      
      
      if(SwtSubWindow.this.resizeAction() !=null){
        SwtSubWindow.this.resizeAction().exec(0, SwtSubWindow.super.gralWindow);
      }
    }
  };

  
  
  
//  private class SwtWindowImplAccess extends GralWindow.WindowImplAccess {
//
//    public SwtWindowImplAccess(GralWindow gralWdg) {
//      super(gralWdg);
//    }
//    
//    @Override public boolean setFocusGThread () {
//      // TODO Auto-generated method stub
//      return false;
//    }
//
//    @Override public void setVisibleGThread (
//        boolean bVisible ) {
//      // TODO Auto-generated method stub
//      
//    }
//
//    @Override public void removeWidgetImplementation () {
//      // TODO Auto-generated method stub
//      
//    }
//
//    @Override public void repaintGthread () {
//      // TODO Auto-generated method stub
//      Debugutil.stop();
//    }
//
//    @Override public Object getWidgetImplementation () {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override public void setBoundsPixel ( int x, int y,
//        int dx, int dy ) {
//      // TODO Auto-generated method stub
//      
//    }
//
//    @Override public void updateValuesForAction () {
//      // TODO Auto-generated method stub
//      
//    }
//
//    @Override public GralRectangle getPixelPositionSize () {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
// 
//  }

}

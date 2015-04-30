package org.vishia.gral.swt;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralWidgImpl_ifc;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GralWindow_setifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.KeyCode;

//public class SubWindowSwt extends GralPanelContent implements WidgetCmpnifc
//public class SwtSubWindow extends GralWindow implements SwtSetValue_ifc
public class SwtSubWindow extends GralWindow.GraphicImplAccess implements GralWidgImpl_ifc
{

  
  /**Version, history and license:
   * <ul>
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
  public static final int version = 20120310;
  
  /**It contains the association to the swt widget (Control) and the {@link SwtMng}
   * and implements some methods of {@link GralWidgImpl_ifc} which are delegate from this.
   */
  SwtWidgetHelper swtWidgWrapper;

  protected Shell window;
  
  
  protected SwtMenu menuBar;
  
  /**State of the window in swt. */
  protected boolean bFullScreen;
  
  /**Some flags to set an action listener a first time if the action is given. */
  private boolean bHasResizeAction, bHasMouseAction;
  
  /**The resizeListener will be activated if {@link #setResizeAction(GralUserAction)} will be called.
   * It calls this user action on resize. */
  private final ControlListener resizeListener = new ControlListener()
  { @Override public void controlMoved(ControlEvent e) 
    { //do nothing if moved.
    }

    @Override public void controlResized(ControlEvent e) 
    { if(resizeAction() !=null){
        resizeAction().exec(0, SwtSubWindow.super.gralWindow);
      }
    }
  };
  
  

  
  /**Constructs a window (primary or sub window maybe for dialog etc.).
   * @param name
   * @param display
   * @param title
   * @param windProps
   * @param gralMng
   */
  SwtSubWindow(SwtMng mng, GralWindow wdgGral)
  //SwtSubWindow(String name, Display display, String title, int windProps, GralMng gralMng)
  { //super(name, windProps, gralMng, null);
    super(wdgGral, mng.mng);  //Invoke constructor of the super class, with knowledge of its outer class.
    int props = 0; ////|SWT.CLOSE;
    String sTitle = super.getTitle();
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
    window = new Shell(mng.displaySwt, props);
    swtWidgWrapper = new SwtWidgetHelper(window, mng);
    window.addShellListener(shellListener);
    window.addDisposeListener(disposeListener);
    //window.add
    if((windProps & GralWindow.windHasMenu) !=0){
      Menu menuBarSwt = new Menu(window, SWT.BAR);
      window.setMenuBar(menuBarSwt);
    }
    //super.panelComposite = window;
    if(sTitle !=null){ window.setText(sTitle); }
    if(!bHasResizeAction && resizeAction() != null){
      window.addControlListener(resizeListener);  //This listener calls the resizeAction
    }
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
  
  
  @Override public Object getWidgetImplementation(){ return window; }
  
  
  
  
  
  
  //@Override
  @Override
  public boolean setFocusGThread()
  { return window.setFocus();
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
    { int windProps = getWindowProps();
      if(invisibleSetAction() !=null){
        invisibleSetAction().exec(KeyCode.menuEntered, SwtSubWindow.this.gralWindow);
      }
      if((windProps & GralWindow_ifc.windRemoveOnClose)!=0) {
        gralWindow.remove();  //remove the window as widget.
        e.doit = true;
      } else {
        e.doit = false;
        ((Shell)e.widget).setVisible(false);
      }
    }

    @Override
    public void shellDeactivated(ShellEvent e)
    {
      stop();
      
    }

    @Override
    public void shellDeiconified(ShellEvent e)
    {
      stop();
      
    }

    @Override
    public void shellIconified(ShellEvent e)
    {
      stop();
      
    }
    
  };
  
  
  
  private final DisposeListener disposeListener = new DisposeListener(){
    
    
    @Override
    public void widgetDisposed(DisposeEvent e)
    {
      stop();
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
  
  


  
  void stop(){}

  @Override public void repaintGthread() {
    int chg = getChanged();
    int acknChg = 0;
    if(!bHasResizeAction && resizeAction() != null){
      window.addControlListener(resizeListener);
    }
    if(!bHasMouseAction && mouseAction() != null){
      window.addControlListener(resizeListener);
    }
    if(super.shouldClose()){
      window.close();
    } 
    if((chg & chgVisible)!=0){
      acknChg |= chgVisible;
      window.setVisible(true);
      window.setFocus();
    }
    if((chg & chgInvisible)!=0){
      acknChg |= chgInvisible;
      window.setVisible(false);
    }
    if(bFullScreen != super.isFullScreen()){
      window.setFullScreen(bFullScreen = super.isFullScreen());
    }
    acknChanged(acknChg);
    window.update();
    window.redraw();
    //swtWindow_setifc.repaintGthread(); 
  }
  

}

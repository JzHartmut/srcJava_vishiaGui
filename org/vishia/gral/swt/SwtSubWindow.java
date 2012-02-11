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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GralWindow_setifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.util.KeyCode;

//public class SubWindowSwt extends GralPanelContent implements WidgetCmpnifc
public class SwtSubWindow extends GralWindow implements SwtSetValue_ifc
{

  
  /**Version and history:
   * <ul>
   * <li>2012-02-11 Hartmut chg: The menu of the window is managed now in {@link SwtMenu}. Instance refered with {@link #menuBar}
   * <li>2011-11-27 Hartmut chg: {@link #addMenuItemGThread(String, String, GralUserAction)} moved from
   *   {@link SwtPrimaryWindow} to this, because the capability to have a menu bar may needed on a sub-window too.
   * <li>2011-11-18 Hartmut chg: {@link SwtSubWindow#SwtSubWindow(String, Display, String, int, GralWidgetMng)}
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
   * <li>2011-10-20 Hartmut chg: {@link GralWidgetMng} as parameter of constructor: The {@link GralWidget}
   *   refers it, it should be initialized.
   * <li>2011-10-15 Hartmut new: {@link #removeWidgetImplementation()}.
   * <li>2011-09-23 Hartmut bugfix: setFocus() called in {@link #setWindowVisible(boolean)}, it hasn't
   *   the focus automatically.
   * <li>2011-09-18 Hartmut chg: Now it is a SubWindowSwt and inherits from the new {@link GralWindow}.
   *   Some methods {@link #setBackColor(GralColor, int)}, {@link #setWindowVisible(boolean)} etc added
   *   to comply the interface and super class definitions.
   * <li>2011-09-03 Hartmut creation as wrapper arround a SWT.Shell inherits {@link org.vishia.gral.base.GralPanelContent}.
   * </ul>
   */
  @SuppressWarnings("hiding")
  public static final int version = 0x20111127;
  
  protected Shell window;
  
  
  protected SwtMenu menuBar;
  

  
  SwtSubWindow(String name, Display display, String title, int windProps, GralWidgetMng gralMng)
  { super(name, windProps, gralMng, null);
    int props = 0; ////|SWT.CLOSE;
    if(title !=null){ 
      props |= SWT.TITLE | SWT.BORDER | SWT.CLOSE; 
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
    window = new Shell(display, props);
    window.addShellListener(shellListener);
    window.addDisposeListener(disposeListener);
    if((windProps & GralWindow.windHasMenu) !=0){
      Menu menuBar = new Menu(window, SWT.BAR);
      window.setMenuBar(menuBar);
    }
    super.panelComposite = window;
    if(title !=null){ window.setText(title); }
    
  }
  
  SwtSubWindow(String name, int windProps, Shell shell, GralWidgetMng gralMng)
  { super(name, windProps, gralMng, shell);
    this.window = shell;
    
  }
  
  

  
  //@Override
  public boolean setFocus()
  { return window.setFocus();
  }

  @Override public boolean isWindowsVisible(){ return window.isVisible(); }


  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  
  public void removeWidgetImplementation()
  {
    window.dispose();
    window = null;
  }

  @Override public Composite getPanelImpl() { return window; }
  
  @Override public GralRectangle getPixelPositionSize(){
    Rectangle r = window.getBounds();
    GralRectangle posSize = new GralRectangle(r.x, r.y, r.width, r.height);
    return posSize;
  }

  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { window.setBounds(x,y,dx,dy);
  }
  
  
  @Override public void setResizeAction(GralUserAction action){
    if(resizeAction == null){
      window.addControlListener(resizeListener);
    }
    resizeAction = action;
  }
  
  @Override public void setMouseAction(GralUserAction action){
    if(mouseAction == null){
      window.addControlListener(resizeListener);
    }
    mouseAction = action;
  }
  
  
  

  @Override public void addMenuItemGThread(String nameWidg, String sMenuPath, GralUserAction gralAction){
    if(menuBar == null){
      menuBar = new SwtMenu("menubar", window, itsMng);
    }
    menuBar.addMenuItemGthread(nameWidg, sMenuPath, gralAction);
  }
  
  
  
  
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
    @Override public void shellClosed(ShellEvent e)
    { e.doit = false;
      ((Shell)e.widget).setVisible(false);
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
  
  
  
  DisposeListener disposeListener = new DisposeListener(){
    
    
    @Override
    public void widgetDisposed(DisposeEvent e)
    {
      stop();
    }
  };
  
  
  /**The resizeListener will be activated if {@link #setResizeAction(GralUserAction)} will be called.
   * It calls this user action on resize. */
  private ControlListener resizeListener = new ControlListener()
  { @Override public void controlMoved(ControlEvent e) 
    { //do nothing if moved.
    }

    @Override public void controlResized(ControlEvent e) 
    { if(resizeAction !=null){
        resizeAction.userActionGui(0, null);
      }
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
      mouseAction.userActionGui(key, null, pos);
    }

    @Override public void mouseDown(MouseEvent e) 
    { GralRectangle pos = new GralRectangle(e.x, e.y, 0,0);
      int key = KeyCode.mouse1Down;  //TODO select key, alt, ctrl etc.
      mouseAction.userActionGui(key, null, pos);
    }

    @Override public void mouseUp(MouseEvent e) 
    { GralRectangle pos = new GralRectangle(e.x, e.y, 0,0);
      int key = KeyCode.mouse1Up;  //TODO select key, alt, ctrl etc.
      mouseAction.userActionGui(key, null, pos);
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
  
  

  /**The methods which are defined here should be called in the graphic thread only. */
  GralWindow_setifc swtWindow_setifc = new GralWindow_setifc()  //, GralWidget_ifc
  {

    @Override public void setWindowVisible(boolean visible)
    { visibleFirst |= visible;
      window.setVisible(visible);
      if(visible){ 
        window.setFocus();
        window.setActive();
      }
    }

    
    @Override public void repaintGthread(){  window.redraw(); window.update(); }

    @Override public void closeWindow(){ window.close(); }

    
  };

  
  void stop(){}

  @Override public GralWindow_setifc getSwtWindow_ifc(){  return swtWindow_setifc; }

  @Override public GralWidgetGthreadSet_ifc getSwtWidget_ifc(){ return null; }

  @Override public void repaintGthread() { swtWindow_setifc.repaintGthread(); }
  
  @Override public GralWidgetGthreadSet_ifc getGthreadSetifc(){ return gThreadSet; }

  /**Implementation of the graphic thread widget set interface. */
  GralWidgetGthreadSet_ifc gThreadSet = new GralWidgetGthreadSet_ifc(){

    @Override public void clearGthread()
    { // TODO Auto-generated method stub
    }

    @Override public void insertGthread(int pos, Object visibleInfo, Object data)
    { // TODO Auto-generated method stub
    }

    @Override public void redrawGthread()
    { // TODO Auto-generated method stub
    }

    @Override public void setBackGroundColorGthread(GralColor color)
    { // TODO Auto-generated method stub
    }

    @Override public void setForeGroundColorGthread(GralColor color)
    { // TODO Auto-generated method stub
    }

    @Override public void setTextGthread(String text, Object data)
    { // TODO Auto-generated method stub
    }
  };
  
  
  

}

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
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GralWindow_setifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.KeyCode;

//public class SubWindowSwt extends GralPanelContent implements WidgetCmpnifc
public class SwtSubWindow extends GralWindow implements SwtSetValue_ifc
{

  
  /**Version, history and license:
   * <ul>
   * <li>2012-07-13 Hartmut new:  {@link #getPixelSize()}, chg: {@link #getPixelPositionSize()} in all implementations. 
   *   A swt.widget.Shell now returns the absolute position and the real size of its client area without menu and title bar.
   * <li>2012-06-29 Hartmut new: {@link #setResizeAction(GralUserAction)} now for both ctors, resize on subwindow works.
   * <li>2012-03-10 Hartmut new: calls invisibleSetAction.userActionGui if the window is set invisible by pressing the X closing icon.
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
  
  protected Shell window;
  
  
  protected SwtMenu menuBar;
  
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
    if((windProps & GralWindow.windResizeable) !=0){ props |= SWT.RESIZE; }
    window = new Shell(display, props);
    window.addShellListener(shellListener);
    window.addDisposeListener(disposeListener);
    if((windProps & GralWindow.windHasMenu) !=0){
      Menu menuBar = new Menu(window, SWT.BAR);
      window.setMenuBar(menuBar);
    }
    super.panelComposite = window;
    if(title !=null){ window.setText(title); }
    if((windProps & GralWindow.windResizeable) !=0){
      setResizeAction(actionResizeOnePanel);
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
  
  SwtSubWindow(String name, int windProps, Shell shell, GralWidgetMng gralMng)
  { super(name, windProps, gralMng, shell);
    this.window = shell;
    setResizeAction(actionResizeOnePanel);
    
  }
  
  

  
  //@Override
  public boolean setFocus()
  { return window.setFocus();
  }

  
  
  @Override public void setFullScreen(boolean full){ window.setFullScreen(full); }

  
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
    return SwtWidgetHelper.getPixelPositionSize(window);
    /*
    Rectangle r = window.getBounds();
    Rectangle s = window.getClientArea();
    int dframe = (r.width - s.width) /2;   //width of the frame line.
    int posx = r.x + dframe;               //absolute position of the client area!
    int posy = r.y + (r.height - s.height) - dframe;
    GralRectangle posSize = new GralRectangle(posx, posy, s.width, s.height);
    return posSize;
    */
  }


  @Override public GralRectangle getPixelSize(){
    Rectangle r = ((Composite)panelComposite).getClientArea();
    GralRectangle posSize = new GralRectangle(0, 0, r.width, r.height);
    return posSize;
  }


  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { window.setBounds(x,y,dx,dy);
  }
  
  
  @Override public void setResizeAction(GralUserAction action){
    if(resizeAction == null){
      ((Shell)panelComposite).addControlListener(resizeListener);
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
      menuBar = new SwtMenu("menubar", window, (GralWidgetMng)itsMng);
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
      if(invisibleSetAction !=null){
        invisibleSetAction.userActionGui(KeyCode.menuEntered, SwtSubWindow.this);
      }
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

    @Override public void setFullScreen(boolean full){ window.setFullScreen(full); }

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

package org.vishia.gral.base;

import java.io.IOException;
import java.text.ParseException;

import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralPanel_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.ExcUtil;

/**This class represents a window of an application, either the primary window or any sub window.
 * The {@link GralPos#pos} of the baseclass is the position of the window derived from any other 
 * Position.
 * @author Hartmut Schorrig
 *
 */
public class GralWindow extends GralWidget implements GralWindow_ifc
{

  /**Version, history and license.
   * <ul>
   * <li>2016-09-23 Hartmut chg: {@link #create(String, char, LogMessage, GralGraphicTimeOrder)} now needs an obligate argument which can be null
   *   for the first callback routine for graphic initializing.  
   * <li>2016-09-18 Hartmut chg: renaming {@link #specifyActionOnCloseWindow(GralUserAction)} instead 'setActionOnSettingInvisible', more expressive name. 
   * <li>2016-08-28 Hartmut new {@link #create(String, char, LogMessage, GralGraphicTimeOrder)} to create either the primary window inclusive the whole graphic machine,
   *   or create any secondary window.
   * <li>2015-05-31 Hartmut The {@link GraphicImplAccess} is now derived from {@link GralPanelContent.ImplAccess}
   *   because this class is derived from that too. Parallel inheritance. 
   * <li>2013-12-19 Hartmut bugfix: {@link #setFullScreen(boolean)} now works. 
   * <li>2013-12-19 Hartmut new: Now it is able to instantiate without Graphic Layer and {@link #setToPanel(GralMngBuild_ifc)}
   *   is supported.  
   * <li>2012-04-16 Hartmut new: {@link #actionResizeOnePanel}
   * <li>2012-03-13 Hartmut chg: Some abstract method declarations moved to its interface.
   * <li>2011-12-31 Hartmut chg: Implements the set-methods of {@link GralWindow_ifc} in form of calling
   *   {@link GralMng_ifc#setInfo(GralWidget, int, int, Object, Object)}. This methods
   *   can be called in any thread, it may be stored using 
   *   {@link GralGraphicThread#addRequ(org.vishia.gral.base.GralWidgetChangeRequ)}.
   * <li>2011-11-27 Hartmut new: {@link #addMenuBarItemGThread(String, String, GralUserAction)} copied
   *   from {@link org.vishia.gral.ifc.GralPrimaryWindow_ifc}. The capability to have a menu bar
   *   should be enabled for sub-windows too. To support regularity, the property bit {@link #windHasMenu}
   *   is created. The property whether a window has a menu bar or not should be given on creation already.
   * <li>2011-11-27 Hartmut new: {@link #windProps} now stored for all implementations here.
   *   {@link #visibleFirst}: Maybe a problem while creation a menu bar in SWT (?)  
   * <li>2011-11-18 Hartmut new: {@link #windExclusive} etc. as properties for creation a window.
   * <li>2011-11-12 Hartmut chg: Because the {@link GralPanelContent} inherits {@link GralWidget}
   *   and this class has a {@link GralWidget#pos}, the member 'posWindow' is removed.
   * <li>2011-10-31 Hartmut new: {@link #setResizeAction(GralUserAction)} and {@link #setMouseAction(GralUserAction)}
   *   for operations with the whole window.
   * <li>2011-10-30 Hartmut new: {@link #resizeAction}
   * <li>2011-09-23 Hartmut new: member GralGridPos: Position of the window. The position is referred 
   *   to any Panel in another Window. It can be tuned and it is used if the Window is set visible. 
   * <li>2011-09-18 Hartmut creation: Now a PrimaryWindow and a SubWindow are defined.
   * </ul>
   * 
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
   * 
   */
  @SuppressWarnings("hiding")
  public static final String version = "2016-08-28";
  
  /**Standard action for resizing, used if the window contains one panel.
   * It calls {@link GralMng_ifc#resizeWidget(GralWidget, int, int)} 
   * for all widgets in the {@link GralPanelContent#widgetsToResize}
   */
  protected class ActionResizeOnePanel extends GralUserAction 
  { 
    ActionResizeOnePanel(){ super("actionResizeOnePanel - window: " + GralWindow.this.name); }
    @Override public boolean exec(int keyCode, GralWidget_ifc widgi, Object... params)
    { for(GralWidget widgd: GralWindow.this.mainPanel.getWidgetsToResize()){
        if(widgd instanceof GralWindow) {
          System.err.println("GralWindow.ActionResizeOnePanel - A window itself should not be added to widgetsToResize");
        } else {
          widgd.gralMng()._mngImpl.resizeWidget(widgd, 0, 0);
        }
      }
      return true;
    }
  };

  /**Or of some wind... constants.
   */
  int windProps;  
  
  public GralPanelContent mainPanel;
  
  /**This action is called whenever the window is resized by user handling on GUI
   * and the window is determined as {@link GralWindow_ifc#windResizeable}.
   * Per default an instance of {@link ActionResizeOnePanel} is called. 
   * See {@link GralWindow_ifc#setResizeAction(GralUserAction)}. */
  protected GralUserAction resizeAction;
  
  protected GralUserAction actionOnCloseWindow;  
  
  /**See {@link GralWindow_ifc#setMouseAction(GralUserAction)}. */
  protected GralUserAction mouseAction;
  
  protected GralMenu menuBarGral;
  
  protected boolean visibleFirst;
  
  /**State of visible, fullScreen, close set by {@link #setVisible(boolean)}, {@link #setFullScreen(boolean)},
   * {@link #closeWindow()} called in another thread than the graphic thread. It is stored here
   * and executed in the {@link GralWidgImplAccess_ifc#repaintGthread()}. */
  protected boolean XXXbVisible, bFullScreen, bShouldClose;
  

  
  /**Constructs a window. 
   * 
   * @param nameWindow
   * @param windProps
   * @param mng
   * @param panelComposite The implementing instance for a panel.
   * @deprecated: the panelComposite is not used. The GralMng is singleton. use {@link GralWindow#GralWindow(String, String, String, int)}.
   */
  @Deprecated public GralWindow(String posString, String nameWindow, String sTitle, int windProps, GralMng mng, Object panelComposite)
  {
    super((GralPos)null, posString, nameWindow, 'w');
    dyda.displayedText = sTitle;  //maybe null
    this.windProps = windProps;
    if((windProps & windResizeable)!=0){
      resizeAction = new ActionResizeOnePanel();
    }

  }

  
  
  /**Constructs a window with an empty {@link #mainPanel}
   * @param currPos will be cloned to the widget, either ready to use or base pos with posString.
   *   The position of a window describes the size and the first position on the screen.
   * @param refPos reference position for build a relative position
   * @param posName possible position and name. Syntax [@<position> =]<$?name>
   *   If name ends with "Win" or "Window" then the panel name is the same without "Win" or "Window".
   *   Else the panel name is the same + "Panel".
   * @param sTitle can be null then the name is used as title without trailing "Win" or "Window".
   * @param windProps See {@link GralWindow_ifc#windResizeable} etc.
   */
  public GralWindow(GralPos refPos, String posName, String sTitle, int windProps, GralMng gralMng)
  {
    super(refPos.setParent(gralMng.screen), posName, 'w', gralMng);
    int lenNameWindow = super.name.length();
    final String sNamePanel;
    final String sTitleDefault;
    if(super.name.endsWith("Window")) {
      sNamePanel = sTitleDefault = super.name.substring(0, lenNameWindow-6);
    } else if(super.name.endsWith("Win")) {
      sNamePanel = sTitleDefault = super.name.substring(0, lenNameWindow-3);
    } else {
      sNamePanel = super.name + "Panel";
      sTitleDefault = super.name;
    }
    this.dyda.displayedText = sTitle == null ? sTitleDefault : sTitle;  //maybe null
    this.windProps = windProps;
    GralPos posPanel = new GralPos(this);                  // initial GralPos for the main Panel inside the window.
    this.mainPanel = new GralPanelContent(posPanel, sNamePanel, this.gralMng());
    //                                                     // A window has anytime only one GralPanel, the mainPanel.
    super.itsMng.registerWindow(this);
    if((windProps & windResizeable)!=0){
      this.resizeAction = new ActionResizeOnePanel();
    }
    refPos.set(posPanel);
    
  }

  public GralWindow(GralPos currPos, String posName, String sTitle, int windProps)
  { this(currPos, posName, sTitle, windProps, currPos.parent.gralMng());
  }
  
  public GralWindow(GralPos currPos, String posString, String nameWindow, String sTitle, int windProps)
  { this(currPos, posString + "=" + nameWindow, sTitle, windProps, null);
  }
  
//  public GralWindow(String posString, String nameWindow, String sTitle, int windProps)
//  { this(null, posString, nameWindow, sTitle, windProps);
//  }
  
  
  @Override public void specifyActionOnCloseWindow(GralUserAction action)
  { actionOnCloseWindow = action;
  }



  /**Creates the window. Either the {@link GralGraphicThread#isRunning()} already then it is a second window.
   * If the graphic thread is not running, it would be started and this is the primary window.
   * The it invokes {@link GralFactory#createGraphic(GralWindow, char, LogMessage, String)}. 
   * The application should not know whether it is the primary or any secondary window.
   * That helps for applications which are started from a Gral graphic application itself without an own operation system process. 
   * <br>
   * The new implementation graphic starts on {@link GralGraphicThread#runGraphicThread()}
   * <br>
   * @param awtOrSwt see {@link GralFactory#createGraphic(GralWindow, char, LogMessage, String)}
   * @param size 'A'..'G', 'A' is a small size, 'G' is the largest.
   * @param log maybe null. If not given a {@link LogMessageStream} with System.out will be created. For internal logging.
   * @param initializeInGraphicThread maybe null, an order which will be executed in the graphic thread after creation of the window.
   * @deprecated create new {@link org.vishia.gral.swt.SwtFactory} or new {@link org.vishia.gral.awt.AwtFactory} and then
   *   call the overridden operation {@link org.vishia.gral.ifc.GralFactory#createGraphic(GralWindow, char, LogMessage)}.
   *   You can then use also any other Factory for the graphic system for any other graphic system. 
   */
//  @Deprecated public void create(String awtOrSwt, char size, LogMessage log, GralGraphicTimeOrder initializeInGraphicThread){
//    if(this._wdgImpl !=null) throw new IllegalStateException("window already created.");
//    GralMng mng = GralMng.get();
//    if(mng.isRunning()) {
//      mng.addDispatchOrder(this.createImplWindow);
//    } else {
//      //it is the primary window, start the graphic with it.
//      if(log == null) { log = new LogMessageStream(System.out); }
//      GralFactory.createGraphic(this, size, log, awtOrSwt);
//    }
//    if(initializeInGraphicThread !=null) {
//      mng.addDispatchOrder(initializeInGraphicThread);
//    }
//  }

  

  
  @Override public void setWindowVisible(boolean visible){
    setVisible(visible);
  }
  

  @Override public void closeWindow(){
    setVisible(false);
  }
  
  
  
  @Override public boolean remove() {
    super.remove();
    itsMng.deregisterPanel(this.mainPanel);
    itsMng.deregisterWindow(this);
    return true;
  }
  
  @Override
  public GralRectangle getPixelPositionSize()
  {
    return _wdgImpl.getPixelPositionSize();
  }




  /**It assumes that the window implementation is present. 
   * It calls {@link GralWindowImpl_ifc#addMenuBarArea9ItemGThread(String, String, GralUserAction)}
   * with the known {@link GralWidget#_wdgImpl} instance
   * to invoke the graphic implementation layer method for the window. 
   * @deprecated use {@link #getMenuBar()} and then {@link GralMenu#addMenuItem(String, String, GralUserAction)}
   * */
  @Override @Deprecated
  public void addMenuBarItemGThread(String nameMenu, String sMenuPath, GralUserAction action)
  { GralMenu menu = getMenuBar();
    menu.addMenuItem(nameMenu, sMenuPath, action);
    //((GralWindowImpl_ifc)wdgImpl).addMenuItemGThread(nameMenu, sMenuPath, action);
  }

  
  
  /**Gets the menu bar to add a menu item. If this window hasn't a gral menu bar, then the menu bar
   * is created by calling {@link GralMng#createMenuBar(GralWindow)}.
   * If the window has a menu bar already, it is stored in the reference {@link #menuBarGral}.
   * @return the menu root for this window.
   */
  @Override public GralMenu getMenuBar(){
    if(this.menuBarGral == null){
      this.menuBarGral = new GralMenu(); //itsMng.createMenuBar(this);   //delegation, the widget mng knows the implementation platform.
    }
    return this.menuBarGral;
  }
  
  



  @Override
  public void setMouseAction(GralUserAction action)
  {
    mouseAction = action;
    //repaint(repaintDelay, repaintDelayMax);
  }



  @Override
  public void setResizeAction(GralUserAction action)
  { resizeAction = action;
    //repaint(repaintDelay, repaintDelayMax);
  }



  @Override
  public void setTitle(String sTitle)
  {
    dyda.displayedText = sTitle;
    dyda.setChanged(ImplAccess.chgText); 
    repaint(repaintDelay, repaintDelayMax);
  }

  
  @Override
  public void setFullScreen(boolean val){
    if(bFullScreen !=val){
      bFullScreen = val;
      repaint();
    }
  }


  @Override
  public boolean isWindowsVisible()
  {
    return bVisibleState;
  }
  
  
  public void reportAllContent(Appendable out) {
    try {
      out.append("==== GralWindow.reportAllContent():\n");
      out.append("Window: ").append(this.name).append(", main");
      this.mainPanel.reportAllContent(out,0);
      out.append("\n====\n");
    } catch(Exception exc) {
      System.err.println("unexpected exception on reportAllContent: " + exc.getMessage());
    }
  }

  
  public void reportAllContentImpl(Appendable out) throws IOException {
    GralWindow.WindowImplAccess wdga = (GralWindow.WindowImplAccess)getImplAccess();
    out.append("\n==== GralWindow.reportAllContent Implementation ====\n");
    if(wdga ==null) {
      out.append("No implementation\n");
    } else {
      wdga.reportAllContentImpl(out);
    }
  }
  
  
  
  
  
  /**This class is not intent to use from an application.
   * It is instantiated with the implementation graphic, 
   * for SWT especially aggregated from {@link org.vishia.gral.swt.SwtPanel}.
   * This implementation access class has a special role with the aggregation,
   * in opposite to ImplAccess from other widgets, which are the super class for the implementation layer.
   * This class allows access to all necessary data and methods of the environment class with protected access rights.
   * The class and methods are public here because elsewhere cannot access from the swt package.
   * An application should not use it. This class is public because
   * it should be visible from the graphic implementation which is located in another package. 
   */
  public abstract static class WindowImplAccess extends GralWidget.ImplAccess //access to GralWidget
  implements GralWidgImplAccess_ifc
  {
    
    public final GralWindow gralWindow;  //its outer class.
    
    protected WindowImplAccess(GralWindow gralWdg){
      super(gralWdg);
      this.gralWindow = gralWdg;  //References the environment class
      gralWdg._wdgImpl = this;
    }
    
    /**The title is stored in the {@link GralWidget.DynamicData#displayedText}. */
    public String getTitle(){ return gralWindow.dyda.displayedText; }
    
    /**Window properties as Gral bits given on ctor of GralWindow. */
    public int getWindowProps(){ return gralWindow.windProps; }
    
    
    
    //protected boolean isVisible(){ return gralWindow.bVisible; }
    
    public boolean isFullScreen(){ return gralWindow.bFullScreen; }
    
    public boolean shouldClose(){ return gralWindow.bShouldClose; }
    
    /**The resizeAction from the {@link GralWindow_ifc#setResizeAction(GralUserAction)} */
    public GralUserAction resizeAction(){ return gralWindow.resizeAction; }  
  
    /**The mouseAction from the {@link GralWindow_ifc#setMouseAction(GralUserAction)} */
    public GralUserAction mouseAction(){ return gralWindow.mouseAction; }  
  
    /**The invisibleSetAction from the {@link GralWindow_ifc#specifyActionOnCloseWindow(GralUserAction)} */
    public GralUserAction actionOnCloseWindow(){ return gralWindow.actionOnCloseWindow; }

    
    protected final GralMenu getMenubar() { return this.gralWindow.menuBarGral; }
    
    abstract public void reportAllContentImpl(Appendable out) throws IOException;

    
    
  }



  /**Code snippet for initializing the GUI area (panel). This snippet will be executed
   * in the GUI-Thread if the GUI is created. 
   */
  GralGraphicTimeOrder createImplWindow = new GralGraphicTimeOrder("GralWindow.createImplWindow", this.itsMng)
  {
    @Override public void executeOrder()
    { GralMng mng = gralMng();
      mng.selectPrimaryWindow();
      GralWindow.this.createImplWidget_Gthread();
      GralWindow.this.setVisible(true);
    }
  };

  

  
}

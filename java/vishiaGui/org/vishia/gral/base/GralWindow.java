package org.vishia.gral.base;

import java.io.IOException;
import java.text.ParseException;
import java.util.EventObject;

import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralPanel_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidgetBase_ifc;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.Debugutil;
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
   * <li>2023-04-16 Hartmut chg: resizing is done via {@link GralWidgComposite#resizeWidgets(GralRectangle)}.
   * <li>2016-09-23 Hartmut chg: {@link #create(String, char, LogMessage, GralGraphicOrder)} now needs an obligate argument which can be null
   *   for the first callback routine for graphic initializing.
   * <li>2016-09-18 Hartmut chg: renaming {@link #specifyActionOnCloseWindow(GralUserAction)} instead 'setActionOnSettingInvisible', more expressive name.
   * <li>2016-08-28 Hartmut new {@link #create(String, char, LogMessage, GralGraphicOrder)} to create either the primary window inclusive the whole graphic machine,
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


  /**Or of some wind... constants.
   */
  int windProps;

  public GralPanelContent mainPanel;

  protected GralUserAction actionOnCloseWindow;

  /**See {@link GralWindow_ifc#setMouseAction(GralUserAction)}. */
  protected GralUserAction mouseAction;

  protected GralMenu menuBarGral;

  protected boolean visibleFirst;

  /**State of visible, fullScreen, close set by {@link #setVisible(boolean)}, {@link #setFullScreen(boolean)},
   * {@link #closeWindow()} called in another thread than the graphic thread. It is stored here
   * and executed in the {@link GralWidgImplAccess_ifc#redrawGthread()}. */
  protected boolean bFullScreen, bShouldClose;



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
    this.dyda.displayedText = sTitle;  //maybe null
    this.windProps = windProps;
//    if((windProps & windResizeable)!=0){
//      this.resizeAction = new ActionResizeOnePanel();
//    }

  }



  /**Constructs a window with an empty {@link #mainPanel}
   * @param refPos The parent will be set related to to the screen: {@link GralMng#screen}.
   *   The internal used {@link GralWidget#pos()} is cloned from this refPos instance (as for all Widgets)..
   *   After construction the refPos will be set to the whole main panel of this window.
   * @param posName possible position and name. Syntax [@<position> =]<$?name>
   *   If name ends with "Win" or "Window" then the panel name is the same without "Win" or "Window".
   *   Else the panel name is the same + "Panel".
   * @param sTitle can be null then the name is used as title without trailing "Win" or "Window".
   * @param windProps See {@link GralWindow_ifc#windResizeable} etc.
   * @param gralMng the gral Mng should be the same as in refPos, hence this is deprecated.
   * @Deprecated use {@link #GralWindow(GralPos, String, String, int)}
   */
  @Deprecated public GralWindow(GralPos refPos, String posName, String sTitle, int windProps, GralMng gralMng)
  {
    super(refPos.setParent(gralMng.screen), posName, 'w', true);
    assert(gralMng == null || gralMng == refPos.parent.gralMng());
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
    this.mainPanel = new GralPanelContent(posPanel, sNamePanel);
    //                                                     // A window has anytime only one GralPanel, the mainPanel.
    super.gralMng.registerWindow(this);
//  ^  }
    refPos.set(posPanel);                                  // at least: the refPos for further usage is the position on the whole panel.

  }

  /**Constructs a window with an empty {@link #mainPanel}
   * @param refPos The parent will be set related to to the screen: {@link GralMng#screen}.
   *   The internal used {@link GralWidget#pos()} is cloned from this refPos instance (as for all Widgets)..
   *   After construction the refPos will be set to the whole main panel of this window.
   * @param posName possible position and name. Syntax [@<position> =]<$?name>
   *   If name ends with "Win" or "Window" then the panel name is the same without "Win" or "Window".
   *   Else the panel name is the same + "Panel".
   * @param sTitle can be null then the name is used as title without trailing "Win" or "Window".
   * @param windProps See {@link GralWindow_ifc#windResizeable} etc.
   */
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
  { this.actionOnCloseWindow = action;
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



  @Override public GralPanelContent getItsPanel(){ return this.mainPanel; }

  @Override public void setWindowVisible(boolean visible){
    setVisible(visible);
  }


  @Override public void closeWindow(){
    setVisible(false);
  }



  @Override public boolean remove() {
    super.remove();
    this.gralMng.deregisterPanel(this.mainPanel);
    this.gralMng.deregisterWindow(this);
    return true;
  }

  @Override
  public GralRectangle getPixelPositionSize()
  {
    return this._wdgImpl.getPixelPositionSize();
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
      this.menuBarGral = new GralMenu(this); //itsMng.createMenuBar(this);   //delegation, the widget mng knows the implementation platform.
    }
    return this.menuBarGral;
  }





  @Override
  public void setMouseAction(GralUserAction action)
  {
    this.mouseAction = action;
    //repaint(repaintDelay, repaintDelayMax);
  }





  @Override
  public void setTitle(String sTitle)
  {
    this.dyda.displayedText = sTitle;
    this.dyda.setChanged(ImplAccess.chgText);
    redraw(this.redrawtDelay, this.redrawDelayMax);
  }


  @Override
  public void setFullScreen(boolean val){
    if(this.bFullScreen !=val){
      this.bFullScreen = val;
      redraw();
    }
  }


  @Override
  public boolean isWindowsVisible()
  {
    return this.bVisibleState;
  }



  public GralWidgetBase_ifc getFocusedWidget() { return this.mainPanel; }


  public int windProps ( ) { return windProps; }
  

  public void reportAllContent(Appendable out) {
    try {
      out.append("==== GralWindow.reportAllContent():\n");
      out.append("Window: ").append(this.name);
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


  /**Implementation of the creation of implementation graphic for the GralWindow.
   *
   */
  @Override public boolean createImplWidget_Gthread() throws IllegalStateException {
//    if(name.equals("windSettings"))
//      Debugutil.stop();
    if(super.createImplWidget_Gthread()) {
      GralPos pos = this.pos();
      if(pos.x.p1 ==0 && pos.x.p2 == 0 && pos.y.p1 == 0 && pos.y.p2 == 0){
        this.setFullScreen(true);
      }
      this.mainPanel.createImplWidget_Gthread();
      return true;
    } else {
      return false;
    }
  }

  /**Removes the implementation widget, maybe to re-create with changed properties
   * or also if the GralWidget itself should be removed.
   * This is a internal operation not intent to use by an application.
   * It is called from the {@link GralMng#runGraphicThread()} and hence package private.
   */
  @Override public void removeImplWidget_Gthread() {
    this.mainPanel.removeImplWidget_Gthread();                     // recursively call of same
    super.removeImplWidget_Gthread();
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
    public String getTitle(){ return this.gralWindow.dyda.displayedText; }

    /**Window properties as Gral bits given on ctor of GralWindow. */
    public int getWindowProps(){ return this.gralWindow.windProps; }

    /**This operation should be called by the resize listener of the implementing graphic
     * (for example {@link org.vishia.gral.swt.SwtSubWindow#resizeListener}.
     * @param parentPixelRectangle the client area of the GralWindow implementation.
     */
    protected void resizeWidgets ( GralRectangle parentPixelRectangle) {
      this.gralWindow.mainPanel._wdgImpl.setBoundsPixel(parentPixelRectangle.x, parentPixelRectangle.y, parentPixelRectangle.dx, parentPixelRectangle.dy);
      this.gralWindow.mainPanel._cdata.resizeWidgets(parentPixelRectangle, 0);  //note: does not resize itself, only the children
    }

    //protected boolean isVisible(){ return gralWindow.bVisible; }

    public boolean isFullScreen(){ return this.gralWindow.bFullScreen; }

    public boolean shouldClose(){ return this.gralWindow.bShouldClose; }

    /**The mouseAction from the {@link GralWindow_ifc#setMouseAction(GralUserAction)} */
    public GralUserAction mouseAction(){ return this.gralWindow.mouseAction; }

    /**The invisibleSetAction from the {@link GralWindow_ifc#specifyActionOnCloseWindow(GralUserAction)} */
    public GralUserAction actionOnCloseWindow(){ return this.gralWindow.actionOnCloseWindow; }


    protected final GralMenu getMenubar() { return this.gralWindow.menuBarGral; }

    abstract public void reportAllContentImpl(Appendable out) throws IOException;



  }



  /**Code snippet for initializing the GUI area (panel). This snippet will be executed
   * in the GUI-Thread if the GUI is created.
   */
  @SuppressWarnings("serial")
  GralGraphicOrder createImplWindow = new GralGraphicOrder("GralWindow.createImplWindow", this.gralMng)
  {
    @Override public int processEvent ( EventObject ev) {
      GralMng mng = gralMng();
      mng.selectPrimaryWindow();
      GralWindow.this.createImplWidget_Gthread();
      GralWindow.this.setVisible(true);
      return 0;
    }
  };




}

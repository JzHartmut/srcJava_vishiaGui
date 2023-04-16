package org.vishia.gral.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralPanel_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralVisibleWidgets_ifc;
import org.vishia.gral.ifc.GralWidgetBase_ifc;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.widget.GralHorizontalSelector;
import org.vishia.util.Debugutil;
import org.vishia.util.KeyCode;


/**This class describes a panel which contains either some other widgets (also sub GralPanelContent)
 * or as second variant tabs (new since 2022-08). For a tabbed panel call {@link #setToTabbedPanel()}
 * and add only panels.
 * <br>
 * The implementing widget in Swt is a Composite, a Canvas or just a TabFolder.
 * To support showing lines and figures a {@link Data#canvas} is referenced here.
 * It means the GralPanelContent can contain lines, figures beside widgets.
 * <br>
 * For positioning grid lines can be drawn in the implementing graphic.
 */
public class GralPanelContent extends GralWidgetComposite implements GralPanel_ifc, GralWidget_ifc, GralVisibleWidgets_ifc
{

  /**Version history:
   *
   * <ul>
   * <li>2023-01-20: refactoring: up to now the swt.TabFolder and TabItem is no more used.
   *   Instead a {@link GralHorizontalSelector} is used to select panels. The panels are normal panels
   *   which are set visible or invisible. Also remove a tab is possible.
   * <li>2022-11-15: new {@link #addTabPanel(String, String)}, {@link #removeWidget(String)}
   * <li>2022-11-12: can replace the {@link GralWindow#mainPanel} now if another panel type is given to build.
   *   See {@link GralPanelContent#GralPanelContent(GralPos, String, char, GralMng)}.
   * <li>2022-09-25: own colors for grid lines used in implementation.
   * <li>2022-09-14: new {@link #reportAllContent(Appendable)}
   * <li>2022-08: {@link Data#bTabbed} as designation, this is a tabbed panel.
   *   The old class GralTabbedPanel is no more necessary.
   * <li>2022-08: New class {@link Data} to encapsulate all elements of this. It is better to view in debug to distinguish form GralWidget fields.
   * <li>2018-08-17 Hartmut new: {@link #getWidget(String)}, {@link #getTextFrom(String)}
   * <li>2016-07-16 Hartmut chg: On {@link #setToPanel(GralMngBuild_ifc)} all widgets of this panel are initialized too. More simple for user application.
   * <li>2015-05-02 Hartmut new: {@link #setTextIn(String, CharSequence)}, change of registering a widget. Now any panel knows its widgets
   *   by name.
   * <li>2012-07-13 Hartmut new:  {@link #getPixelSize()}, chg: {@link #getPixelPositionSize()} in all implementations.
   *   A swt.widget.Shell now returns the absolute position and the real size of its client area without menu and title bar.
   * <li>2012-04-22 Hartmut new: {@link #canvas} as property maybe null for each panel to support stored graphics.
//   * <li>2012-03-31 Hartmut new: {@link #implMethodPanel_} and {@link MethodsCalledbackFromImplementation#setVisibleState(boolean)}.
   * <li>2012-01-14 Hartmut new: {@link #setFocusedWidget(GralWidget)} for panel focus.
   * <li>2012-01-08 Hartmut new: {@link #remove()}
   * <li>2011-11-19 Hartmut chg: The 'itsTabSwt' is moved to {@link org.vishia.gral.swt.SwtPanel} now.
   * <li>2011-11-12 Hartmut new: {@link #getPixelPositionSize()}.
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
   */
  @SuppressWarnings("hiding")
  public final static int version = 20221112;

  //public GralPrimaryWindow_ifc mainWindow;

  //public final GralMng gralMng;

  public static class Data {   //Hint: the class is public to see it in implementation graphic, but the reference is protected.

     /**List of all widgets which are contained in this panel or Window, to refresh the graphic.
     * This list is used in the communication thread to update the content of all widgets in the panel.
     */
    //private List<GralWidget> _wdgList = new ArrayList<GralWidget>();

    /**This widget is existing on construction for a tabbed panel. It contains the tabs.
     * It is null if the
     */
    protected GralHorizontalSelector<GralWidget> widgTabs;


    /*final*/ public GralPanelActivated_ifc notifyingUserInstanceWhileSelectingTab;


    /**True then the content of the panel is zoomed with the actual size of panel.
     * It means that all widgets are zoomed in position and size,  but there content isn't changed. */
    protected boolean bZoomed;

    /**True then the grid of the panel is zoomed with the actual size of panel.
     * It means that all fonts are changed too.
     * */
    protected boolean bGridZoomed;

    /**If set, it is a tab of a tabbed panel. It means the parent has {@link #pixelTab} >=0.
     * The text on the tab.
     */
    public final String labelTab;

    /**If this values are set, a grid should be shown.
     * yGrid, xGrid are the spaces for the fine grid, yGrid2, xGrid2 describes which nth grid line should be more determined.
     * Typical values 1 and 10 or 2 and 5
     */
    public int yGrid, xGrid, yGrid2, xGrid2;

    /**Value for difference for all 3 color components for the grid relative to the background.
     * Use a negative value for more darker grid lines, or a positive for more lighter.
     * Typical values -8 and -10 (in relation to 255) for a light gray background.
     * General the grid lines have the same color as the background, all 3 components are vary with the same difference.
     */
    public GralColor colorGridLine, colorGridLine2;


    /**If this instance is not null, the content of that should be paint in the paint routine
     * of the implementation graphic. */
    public GralCanvasStorage canvas;

    protected Data(String labelTab) {
      this.labelTab = labelTab;
    }
  };
  public final Data _panel;




  /**Create a panel, registers it and sets the refPos to this panel.
   * @param refPos reference position, also with aggregation to the GralMng
   * @param posName possible position string in the parent, starting with "@..." and the name
   * @param labelTab =null for a whatIsit ==$ :simple panel,
   *   if given for  whatIsit ==^ :tabbed panel, then the first tab is created as select list for all tabs.
   *   should be given as string, name of the tab for whatIsit ==@
   * @param whatIsit should be one of $ ^ @ for $=simple panel, ^=a tabbed panel, @= a tab of a tabbed panel
   * @param gralMng
   */
  public GralPanelContent(GralPos refPos, String posName, String labelTab, char whatIsit, GralMng gralMng) {
  //public PanelContent(CanvasStorePanel panelComposite)
    super(refPos, posName, whatIsit);
    assert("^$@".indexOf(whatIsit) >=0);    //TODO the ^ is not used, remove it again. (Test!)
    this._panel = new Data(labelTab);
    refPos.setFullPanel(this);
    gralMng.registerPanel(this);
    if(labelTab !=null) {
      assert(whatIsit == '@');

    }
    if(whatIsit == '^') {
      GralUserAction actionTabs = new ActionSetFromTabSelection(this._compt);
      this._panel.widgTabs = new GralHorizontalSelector<GralWidget>(refPos, "@0..2,0..0=tabs-" + this.name, actionTabs);
    }

    if(super._wdgPos.parent instanceof GralWindow) {       // replaces the main panel of the window:
      GralWindow window = (GralWindow) super._wdgPos.parent;
      GralPanelContent mainPanel = window.mainPanel;
      if(mainPanel !=null) {
        if(mainPanel._compt.idxWidgets.size() >0) {
          throw new IllegalStateException("association of a main panel not possible, mainPanel " + mainPanel.name + " of window: " + window.name + " has already content." );
        } else {
          gralMng.deregisterPanel(mainPanel);
        }
      }
      window.mainPanel = this;
    } else {
      GralPanelContent parentPanel = (GralPanelContent) super._wdgPos.parent;
      parentPanel._compt.idxWidgets.put(this.name, this);
    }
    if( pos()!=null) {                                     // set the panel pos for gralMng
      gralMng.setPosPanel(this);
    }
    int property = 0; //TODO parameter
    this._panel.bZoomed = (property & GralMngBuild_ifc.propZoomedPanel) !=0;
    this._panel.bGridZoomed = (property & GralMngBuild_ifc.propGridZoomedPanel) !=0;
    setBackColor(GralColor.getColor("pgr"), 0);
  }

  public GralPanelContent(GralPos currPos, String posName, GralMng gralMng) {
    this(currPos, posName, null, '$', gralMng);
  }

  public GralPanelContent(GralPos currPos, String posName, String labelTab, GralMng gralMng) {
    this(currPos, posName, labelTab, '@', gralMng);
    GralPanelContent parent = (GralPanelContent)this._wdgPos.parent;
    parent.setToTabbedPanel();           //checks also whether it has already faulty non tab children
  }




  /**The primary widget is that widget which should be focused if the panel is focused.
   * If this is a tabed panel the focused widget is the current opened tab.
   * It is possible to set any actual widget to store the focus situation,
   * It is possible too to have only one widget to focus. if the panel gets the focus. */
  @Override public void setFocusedWidget(GralWidgetBase_ifc widg){
    this._compt.primaryWidget = widg;
    if(isTabbed()) {
      this.dyda.setChanged(GralWidget.ImplAccess.chgCurrTab);
    }
    redraw1(50,100);     // redraw deleayed because more changes on the same time.
  }


  public void setPanelVisible ( boolean bVisible) {
    this.setVisible(bVisible);
    for(GralWidget widget: this._compt.widgetList){
      widget.setVisible(bVisible);
    }
  }



  /**Should be set before call of {@link #addWidget(GralWidget, boolean)}.
   * Then the widgets are presented as tabs in this panel.
   * The widgets then are usual {@link GralPanelContent} by itself or a more complex {@link GralWidget}, for example a {@link GralTable}.
   * This feature can only be set initially, not removed.
   */
  public void setToTabbedPanel() {
    if(this._panel.widgTabs ==null) {
      GralPos refPos = new GralPos(this);
      GralUserAction actionTabs = new ActionSetFromTabSelection(this._compt);
      this._panel.widgTabs = new GralHorizontalSelector<GralWidget>(refPos, "@0..2,0..0=tabs-" + this.name, actionTabs);

    }
  }



  /**Adds a new panel as Tab for this tabbed panel.
   * The panel must not contain any other widgets before.
   * {@link #setToTabbedPanel()} is called here.
   * @param name name of the panel in the GralMng.
   * @param tabLabel label shown on the tab.
   * @return a new created panel as child of this panel, which acts as tab
   */
  public GralPanelContent addTabPanel(String name, String tabLabel, boolean removable) {
    this.gralMng.log.sendMsg(GralMng.LogMsg.addTabPanel, "add tab panel %s(%s) to %s", name, tabLabel, this.name);
    setToTabbedPanel();
    GralPos refPos = new GralPos(this);                    //has full spread 0..0
    GralPanelContent widgPanel = new GralPanelContent(refPos, "@2..0,0..0=" + name, tabLabel, this.gralMng());
    //                                                     // it is added automatically because refPos
    GralWidget currTab = this._panel.widgTabs.getCurrItem();
    if(currTab !=null) {                                   // set the current tab invisible
      currTab.setVisible(false);
    }
    this._panel.widgTabs.addItem(tabLabel, -1, widgPanel, removable);
    this._compt.primaryWidget = widgPanel;
    return widgPanel;                                      // the last added tab is visible;
  }






  /**If yGrid and xGrid are >0, show a grid.
   * For tabbed panels show the grid for all tabs.
   * If yGrid==0 or xGrid ==0 does not show a grid.
   * @param yGrid
   * @param xGrid
   * @param yGrid2
   * @param xGrid2
   */
  public void setGrid(int yGrid, int xGrid, int yGrid2, int xGrid2, int colordiff, int colordiff2) {
    this._panel.yGrid = yGrid;
    this._panel.xGrid = xGrid;
    this._panel.yGrid2 = yGrid2;
    this._panel.xGrid2 = xGrid2;
    GralColor colorBack = this.getBackColor(0);
    int rd1 = colorBack.red + colordiff;
    int gn1 = colorBack.green + colordiff;
    int bl1 = colorBack.blue + colordiff;
    this._panel.colorGridLine = GralColor.getColor(rd1, gn1, bl1);
    int rd2 = colorBack.red + colordiff2;
    int gn2 = colorBack.green + colordiff2;
    int bl2 = colorBack.blue + colordiff2;
    this._panel.colorGridLine2 = GralColor.getColor(rd2, gn2, bl2);
  }

  /**This overridden form of {@link GralWidget_ifc#remove()} removes all widgets of this panel.
   * It includes the disposition  of the widgets in the graphic. It is done by invocation
   * {@link GralWidget#remove()}.
   * @return true because it is done.
   */
  @Override public boolean remove(){
    int catastrophicCt = 100000; //safety of all while loops! No more than 100000 widgets.
    while(--catastrophicCt >=0 && this._compt.widgetList.size() >0){
      //remove all widgets from the panel via Widget.remove, it removes it from this list too.
      this._compt.widgetList.get(0).remove();
    }
    assert(catastrophicCt >0);
    this._compt.widgetList.clear();      //the lists may be cleared already
    this._compt.widgetsToResize.clear(); //because widg.remove() removes the widget from the panel.
    super.remove();
    return true;
  }



  /**Removes a widget in this panel given by name.
   * It searches the widget in this panel ({@link Data#idxWidgets}) and calls {@link GralWidget#remove()}.
   * It removes also the implementing widget.
   * @param name name of the widget for GralMng as also in the {@link Data#idxWidgets}
   * @return true then widget is removed, false: Widget not exists here.
   */
  public boolean removeWidget(String name) {
    GralWidget child = this._compt.idxWidgets.get(name);
    if(child !=null) {
      return child.remove();
    } else {
      gralMng().log.writeError("remove - does not exist: " + name);
      return false;
    }
  }

  @Override public GralCanvasStorage canvas() { return this._panel.canvas; }

  /**Returns the GralCanvasStorage for this panel, creates one if not given yet.
   * This operation is intended to use for adding a Figure to the panel's canvas.
   * @return The composite instance of the canvas storage.
   */
  public GralCanvasStorage getCreateCanvas() {
    if(this._panel.canvas == null) {
      this._panel.canvas = new GralCanvasStorage();
    }
    return this._panel.canvas;
  }

  public boolean isTabbed() { return this._panel.widgTabs !=null; } //pixelTab >=0; }


  @Override public GralWidget getPanelWidget () {
    return this;
  }


  public GralWidgetBase_ifc getFocusedWidget() { return this._compt.primaryWidget; }

  /**
   * @deprecated use {@link #getWidgetList()}
   */
  @Deprecated public List<GralWidget> widgetList(){ return this._compt.widgetList; }

  @Override public List<GralWidget> getWidgetList(){ return this._compt.widgetList; }

  @Override public List<GralWidget> getWidgetsVisible () {
    return this._compt.widgetList;    //all widgets is too much, first version. Compare with GralTabbedPanel
  }



  public List<GralWidget> getWidgetsToResize(){ return this._compt.widgetsToResize; }

  /**Gets a named widget on this panel. Returns null if faulty name.
   * @since 2018-09
   */
  public GralWidget getWidget(String name){ return this._compt.idxWidgets.get(name); }




  /**Sets the text to the named widget
   * @param nameWidget the registered widget in its panel.
   * @param text The text to set.
   * @throws IllegalArgumentException on faulty widget name
   * @since 2015-05-02
   */
  public void setTextIn(String nameWidget, CharSequence text) {
    GralWidget widg = this._compt.idxWidgets.get(nameWidget);
    if(widg == null) throw new IllegalArgumentException("GralPanel - Widget not found, " + nameWidget);
    widg.setText(text);
  }



  /**Gets the text from the named widget
   * @param nameWidget the registered widget in its panel.
   * @param text The text to set.
   * @throws IllegalArgumentException on faulty widget name
   * @since 2015-05-02
   */
  public String getTextFrom(String nameWidget) {
    GralWidget widg = this._compt.idxWidgets.get(nameWidget);
    if(widg == null) throw new IllegalArgumentException("GralPanel - Widget not found, " + nameWidget);
    return widg.getText();
  }






  /**Sets the focus to the primary widget if it is set.
   * Elsewhere do nothing and returns false.
   * The focus may be set then by the inherit implementation class.
   * <br>See {@link #setFocusedWidget(GralWidget)}.
   * @return true if the focus is set to the primary widget.
   */
  //@Override
  public boolean XXXsetFocusGThread()
  {
    if(this._compt.primaryWidget !=null) {
      //invokes the setFocus routine to mark focus in table etc.
      this._compt.primaryWidget.setFocus();  //invokes setFocusGThread because it is the graphic thread.
      return true;  //TODO check focus
      //return primaryWidget.setFocusGThread();
    }
    else return false;
  }

  /**Use GralWidet._wdgImpl.getWidgetImplementation() internally! Not public.
   * @return
   */
  @Deprecated public Object getWidgetImplementation()
  { return this._wdgImpl.getWidgetImplementation(); //panelComposite;
  }


  /**Returns the container instance of the panel of the implementing graphic.
   * @return The container.
   * @throws IOException
   */
  //public abstract Object getPanelImpl();

  void reportAllContent(Appendable out, int level) throws IOException {
    if(level < 20) {
      final String nl = "\n| | | |                               ";
      if(level >0) {
        out.append(nl.substring(0, 2*level-1));
      }
      out.append("+-Panel: ").append(this.name);
      if(this._panel.labelTab !=null) {
        out.append('(').append(this._panel.labelTab).append(')');
      }
      out.append(" @").append(this._wdgPos.toString());
      for(GralWidget widg: this._compt.widgetList) {
        if(widg instanceof GralPanelContent) {
          ((GralPanelContent)widg).reportAllContent(out, level+1);
        } else {
          out.append(nl.substring(0,2*level+1)).append("+-");
          widg.toString(out);
        }
      }
    } else {
      out.append("\n .... more");
    }
  }

  @Override public String toString(){ return "GralPanel:" + this.name + (super._wdgPos == null ? "" : "@" + super._wdgPos.toString()); }


  /**This class is instantiated and used for a {@link GralHorizontalSelector} as action for tab is selection and rmove. */
  private static class ActionSetFromTabSelection extends GralUserAction {
    final GralWidgetComposite._Composite _compt;

    ActionSetFromTabSelection(GralWidgetComposite._Composite _compt) {
      super("ActionSetFromTabSelection");
      this._compt = _compt;
    }

    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params) {
      GralWidget tab = (GralWidget)params[0];
      GralWidget tab2 = (GralWidget)params[1];
      if(actionCode == KeyCode.removed) {
        assert(this._compt.primaryWidget == tab2);           // already done before, the now active tab
        //tab2.setVisible(true);
        tab.remove();
      } else {
        this._compt.primaryWidget.setVisible(false);
        this._compt.primaryWidget = tab;
        tab.setVisible(true);
      }
      return true;
  } }






  public abstract static class ImplAccess extends GralWidget.ImplAccess
  {

    public final GralPanelContent gralPanel;

    public final GralPanelContent.Data _panel;

    /**Same reference as {@link GralWidget.ImplAccess#widgg} but type of this class. */
    //GralPanelContent gralPanel;

    protected ImplAccess(GralPanelContent widgg)
    {
      super(widgg);
      this.gralPanel = widgg;
      this._panel = widgg._panel;
      //for all following actions: this is the current panel.
      //GralMng mng = GralMng.get();
      //mng.setPosPanel((GralPanelContent)widgg);
    }

    public GralPanelContent gralPanel(){ return (GralPanelContent) this.gralPanel; } //It is the correct type.


    /**Called on creation of the Panel, maybe also from a window's panel.
     *
     */
    protected void createALlImplWidgets ( ) {
      for(GralWidget widg: this.gralPanel._compt.widgetList) {
        widg.createImplWidget_Gthread();
      }
    }

  }

}


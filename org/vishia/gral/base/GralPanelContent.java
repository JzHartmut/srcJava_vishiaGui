package org.vishia.gral.base;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.ifc.GralCanvasStorage;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.Debugutil;


/**This class describes a panel with its content for managing. */
public class GralPanelContent extends GralWidget implements GralWidget_ifc
{

  /**Version history:
   * 
   * <ul>
   * <li>2018-08-17 Hartmut new: {@link #getWidget(String)}, {@link #getTextFrom(String)}  
   * <li>2016-07-16 Hartmut chg: On {@link #setToPanel(GralMngBuild_ifc)} all widgets of this panel are initialized too. More simple for user application. 
   * <li>2015-05-02 Hartmut new: {@link #setTextIn(String, CharSequence)}, change of registering a widget. Now any panel knows its widgets
   *   by name. 
   * <li>2012-07-13 Hartmut new:  {@link #getPixelSize()}, chg: {@link #getPixelPositionSize()} in all implementations. 
   *   A swt.widget.Shell now returns the absolute position and the real size of its client area without menu and title bar.
   * <li>2012-04-22 Hartmut new: {@link #canvas} as property maybe null for each panel to support stored graphics.
   * <li>2012-03-31 Hartmut new: {@link #implMethodPanel_} and {@link MethodsCalledbackFromImplementation#setVisibleState(boolean)}.
   * <li>2012-01-14 Hartmut new: {@link #setPrimaryWidget(GralWidget)} for panel focus.
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
  public final static int version = 20120713;

  //public GralPrimaryWindow_ifc mainWindow;
  
  //public final GralMng gralMng;
  
  
  /**The widget which should be focused if the panel is focused. 
   * It is possible to set any actual widget to store the focus situation,
   * It is possible too to have only one widget to focus. if the panel gets the focus. */
  protected GralWidget primaryWidget;
  
  /**List of all widgets which are contained in this panel or Window, to refresh the graphic.
   * This list is used in the communication thread to update the content of all widgets in the panel.
   */
  //private List<GralWidget> _wdgList = new ArrayList<GralWidget>();

  /**List of all widgets which are contained in this panel.
   * This list is used in the communication thread to update the content of all widgets in the panel.
   */
  protected List<GralWidget> widgetList = new ArrayList<GralWidget>();

  public List<GralWidget> widgetsToResize = new LinkedList<GralWidget>();

  private final Map<String, GralWidget> idxWidgets = new TreeMap<String, GralWidget>();

  /**True then the content of the panel is zoomed with the actual size of panel. 
   * It means that all widgets are zoomed in position and size,  but there content isn't changed. */
  protected boolean bZoomed;
  
  /**True then the grid of the panel is zoomed with the actual size of panel. 
   * It means that all fonts are changed too.
   * */
  protected boolean bGridZoomed;
  
  
  /**If this instance is not null, the content of that should be paint in the paint routine
   * of the implementation graphic. */
  public GralCanvasStorage canvas;

  
  @Deprecated public GralPanelContent(String namePanel, GralMng mng, Object panelComposite)
	//public PanelContent(CanvasStorePanel panelComposite)
	{ super(namePanel, '$');
	  this.name = namePanel;
		//this.panelComposite = panelComposite;
    //GralMng mng = GralMng.get();
    mng.registerPanel(this);
    if( pos()!=null) {
      mng.setPosPanel(this);
    }
    int property = 0; //TODO parameter
    bZoomed = (property & GralMngBuild_ifc.propZoomedPanel) !=0;
    bGridZoomed = (property & GralMngBuild_ifc.propGridZoomedPanel) !=0;
	}
	


  /**Create a panel, registers it and sets the {@link GralMng#pos()} of this thread to the panel. */
  public GralPanelContent(String posString, String namePanel)
  { this(posString, namePanel, '$');
  }

  /**Create a panel, registers it and sets the {@link GralMng#pos()} of this thread to the panel. */
  public GralPanelContent(String posString, String namePanel, char whatIsit)
  //public PanelContent(CanvasStorePanel panelComposite)
  { super(posString, namePanel, whatIsit);
    this.name = namePanel;
    GralMng mng = GralMng.get();
    mng.registerPanel(this);
    if( pos()!=null) {
      mng.setPosPanel(this);
    }
    int property = 0; //TODO parameter
    bZoomed = (property & GralMngBuild_ifc.propZoomedPanel) !=0;
    bGridZoomed = (property & GralMngBuild_ifc.propGridZoomedPanel) !=0;
  }
	/*
  private GralPanelContent(String namePanel, GralPrimaryWindow_ifc mainWindow)
  { super(namePanel, '$', null);
    this.namePanel = namePanel; this.gralMng = null; //mainWindow.;
  }

  
  private GralPanelContent(String namePanel, GralWidgetMng mng)
  { super(namePanel, '$', mng);
    this.namePanel = namePanel; this.gralMng = mng;
  }
  */
  
	
	
  public void setPrimaryWidget(GralWidget widg){ primaryWidget = widg; }
  
  
  /*package private*/
  /**Adds a widget to its panel. This method will be called in {@link GralWidget#initPosAndRegisterWidget(GralPos)} 
   * either on creation the GralWidget with a given position String or on {@link GralWidget#setToPanel(GralMngBuild_ifc)}
   * with the given currently {@link GralMng#pos()}.
   * @param widg
   * @param toResize
   */
  void addWidget(GralWidget widg, boolean toResize){
    String nameWidg = widg.name;
    if(widg instanceof GralWindow)
      Debugutil.stop();
    if(nameWidg !=null) {
      String nameGlobal;
      if(nameWidg.startsWith("@")) {
        nameWidg = nameWidg.substring(1);  //without @
        nameGlobal = super.name + "." + nameWidg;  //panel.widget
      } else {
        nameGlobal = nameWidg;
      }
      itsMng.registerWidget(nameGlobal, widg);
      idxWidgets.put(nameWidg, widg);
    }
    if(widgetList.remove(widg)){
      System.err.println("Widget added twice; " + nameWidg);
    }
    widgetList.add(widg);
    if(toResize) {
      if(widg instanceof GralWindow) {
        System.err.println("GralPanelContent.addWidget - A window itself should not be added to widgetsToResize, " + widg.name);
      } else {
        widgetsToResize.add(widg);
      }
    }
    if(primaryWidget ==null && !(widg instanceof GralPanelContent)) {  //register only a non-panel widget as primary - for the panel or window.
      primaryWidget = widg; 
    }
  }
	
	
	
  /**Overridden implementation of {@link GralWidget#createImplWidget_Gthread()} for instantiation of all widgets of this panel.
   */
  @Override public void createImplWidget_Gthread() throws IllegalStateException {
    super.createImplWidget_Gthread(); //from GralWidget.
    for(GralWidget widg: widgetList) {
      widg.createImplWidget_Gthread();
    }
  }	
	
	/**Removes this widget from the lists in this panel. This method is not intent to invoke
   * by an application. It is only used in {@link GralWidget#remove()}. Use the last one method
   * to remove a widget includint is disposition and remove from the panel.
   * @param widg The widget.
   */
  public void removeWidget(GralWidget widg)
  { String nameWidg = widg.name;
    if(nameWidg !=null) {
      String nameGlobal;
      if(nameWidg.startsWith("@")) {
        nameWidg = nameWidg.substring(1);  //without @
        nameGlobal = super.name + "." + nameWidg;  //panel.widget
      } else {
        nameGlobal = nameWidg;
      }
      itsMng.removeWidget(nameGlobal);
      idxWidgets.remove(nameWidg);
    }
    
    widgetList.remove(widg);
    widgetsToResize.remove(widg);
    
  }

  /**This overridden form of {@link GralWidget_ifc#remove()} removes all widgets of this panel.
   * It includes the disposition  of the widgets in the graphic. It is done by invocation
   * {@link GralWidget#remove()}.
   * @return true because it is done.
   */
  @Override public boolean remove(){
    int catastrophicCt = 100000; //safety of all while loops! No more than 100000 widgets.
    while(--catastrophicCt >=0 && widgetList.size() >0){
      //remove all widgets from the panel via Widget.remove, it removes it from this list too.
      widgetList.get(0).remove(); 
    }
    assert(catastrophicCt >0);
    widgetList.clear();      //the lists may be cleared already 
    widgetsToResize.clear(); //because widg.remove() removes the widget from the panel.
    super.remove();
    return true;
  }

  
  
  
  
  
  /**
   * @deprecated use {@link #getWidgetList()}
   */
  @Deprecated public List<GralWidget> widgetList(){ return widgetList; }
  
  public List<GralWidget> getWidgetList(){ return widgetList; }
  
  /**Gets a named widget on this panel. Returns null if faulty name. 
   * @since 2018-09
   */
  public GralWidget getWidget(String name){ return idxWidgets.get(name); }
  
  
  
  
  /**Sets the text to the named widget
   * @param nameWidget the registered widget in its panel. 
   * @param text The text to set.
   * @throws IllegalArgumentException on faulty widget name
   * @since 2015-05-02
   */
  public void setTextIn(String nameWidget, CharSequence text) {
    GralWidget widg = idxWidgets.get(nameWidget);
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
    GralWidget widg = idxWidgets.get(nameWidget);
    if(widg == null) throw new IllegalArgumentException("GralPanel - Widget not found, " + nameWidget);
    return widg.getText();
  }
  
  
  
  
  
  
  /**Sets the focus to the primary widget if it is set.
   * Elsewhere do nothing and returns false. 
   * The focus may be set then by the inherit implementation class.
   * <br>See {@link #setPrimaryWidget(GralWidget)}.
   * @return true if the focus is set to the primary widget. 
   */
  //@Override 
  public boolean XXXsetFocusGThread()
  {
    if(primaryWidget !=null) {
      //invokes the setFocus routine to mark focus in table etc.
      primaryWidget.setFocus();  //invokes setFocusGThread because it is the graphic thread.
      return true;  //TODO check focus
      //return primaryWidget.setFocusGThread();
    }
    else return false;
  }

  /**Use GralWidet._wdgImpl.getWidgetImplementation() internally! Not public.
   * @return
   */
  @Deprecated public Object getWidgetImplementation()
  { return _wdgImpl.getWidgetImplementation(); //panelComposite;
  }
	
  
  /**Returns the container instance of the panel of the implementing graphic.
   * @return The container.
   */
  //public abstract Object getPanelImpl();
  
  
  
  @Override public String toString(){ return "GralPanel:" + name; }

  
  
  /**This inner class contains methods which can call by the implementation layer.
   * That methods are not intent to be called by the application. 
   * It is public because the implementation level in another package should accesses it.
   */
  public abstract static class MethodsCalledbackFromImplementation 
  extends GralWidget.ImplAccess
  {
    
    private final GralPanelContent panelg;
    
    MethodsCalledbackFromImplementation(GralPanelContent panelg, GralMng mng){
      super(panelg, mng);
      this.panelg = panelg;
    }
    @Override public void setVisibleState(boolean visible){
      for(GralWidget widget: panelg.widgetList){
        widget.setVisibleState(visible);
      }
    }
  } //class MethodsCalledbackFromImplementation

  /**This reference is a inner class which contains methods which can call by the implementation layer.
   * That methods are not intent to be called by the application. 
   * It is public because the implementation level in another package should accesses it.
   */
  //public MethodsCalledbackFromImplementation implMethodPanel_ = new MethodsCalledbackFromImplementation(this);
  
  /**Sets the visible state to all widgets of the panel, but not to windows (that is only in the primaryWindow)
   * @see org.vishia.gral.base.GralWidget#setVisibleState(boolean)
   */
  @Override public void setVisibleState(boolean visible){
    if(isVisible() != visible) {
      //only invoke for all sub widgets when the visible state of the parent is changed. Otherwise do nothing - nothing is changed, no effort.
      //for tabbed panels etc. it is specially processed
      super.setVisibleStateWidget(visible);  //for the own panel
      for(GralWidget widget: widgetList){
        if(widget != this && !(widget instanceof GralWindow)) {
          widget.setVisibleState(visible);
        }
      }
    }
  }

  
  public abstract static class ImplAccess extends GralWidget.ImplAccess
  {

    /**Same reference as {@link GralWidget.ImplAccess#widgg} but type of this class. */
    //GralPanelContent gralPanel;
    
    protected ImplAccess(GralPanelContent widgg)
    {
      super(widgg);
      //for all following actions: this is the current panel.
      GralMng mng = GralMng.get();
      mng.setPosPanel((GralPanelContent)widgg);   
    }
    
    public GralPanelContent gralPanel(){ return (GralPanelContent) widgg; } //It is the correct type.
    
    /**Returns the absolute position of this panel on screen and its size.
     * If it is a main window, the useable area of the window without title and menu bar is returned.
     * @return
     */
    @Override
    public abstract   GralRectangle getPixelPositionSize();
    
    
    
  /**Returns the size of this panel in pixel.
   * @return the x and y is 0, the dy and dy is the size.
   */
    public abstract GralRectangle getPixelSize();
    
    
    
  }
  
}


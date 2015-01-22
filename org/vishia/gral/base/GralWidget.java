package org.vishia.gral.base;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.vishia.byteData.VariableAccessWithIdx;
import org.vishia.byteData.VariableAccess_ifc;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralSetValue_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidgetCfg_ifc;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.widget.GralHorizontalSelector;
import org.vishia.util.Assert;
import org.vishia.util.KeyCode;



/**This class is the base class of representative of a graphical widget in the gral concept. 
 * All widgets in the gral concept have this base data.
 * The widget of the implementation layer graphic is referred in the derived class of this 
 * with a proper association.
 * <br><br> 
 * Any widget is represented in the user's level by a derived instance of a {@link GralWidget}. 
 * There are 2 strategies yet to create widgets in respect to the graphical implementation.
 * Either the Widget is created independent of the graphic implementation first 
 * and then taken as parameter for the graphic widget-creating method. In this case the GralWidget 
 * contains nothing of the graphic implementation but has a reference to it.
 * The second strategy yet is, the Widget is returned by the graphic-implementation widget-creating method
 * as a instance which has {@link GralWidget} and its non-graphic specializations as super class
 * and the graphic depending parts in the created instance.
 * <br><br>
 * The first form is more universal. Especially generic can be used for the class definition if necessary.
 * It us used yet only (2013-06) for
 * <ul> 
 * <li>{@link org.vishia.gral.widget.GralHorizontalSelector#setToPanel(GralMngBuild_ifc)}
 * <li>{@link GralTable#setToPanel(GralMngBuild_ifc)}.
 * </ul>
 * but it may be used more and more in the future. The <code>setToPanel(GralMng)</code> method
 * invokes the graphic implementation specific derivation of the {@link GralMng}, which creates
 * the implementation widgets. <br>
 * For the UML presentation see {@link org.vishia.util.Docu_UML_simpleNotation}:
 * <pre>
 * 
 *   GralHorizontalSelector< UserType > <-----<*>UserCanCreateIt_GraphicIndependently
 *   - some special non-graphic data
 *     |
 *     |<------------------------------------------------------------------data---|
 *     |                                                                          |
 *     |&<--------&GraphicImplAccess<|---+                                        |   
 *     |                                 |                                        |
 *     +--|>GralWidget                   |                                        |
 *     |     |                           |                                        |
 *     |     |<>--->GralWidgImpl_ifc<|---+--SwtHorizontalSelector                 |
 *     |     |                                 |                                  |      
 *     |     |                                 |                                  |     
 *                                             |<*>------------------------>swt.Canvas
 *                                             |                                  |
 *                                             |                                  |
 *                                          -paintRoutine <-------paintListener---|
 *                                          -mouseListener<-------mouseListener---|
 *                                          -etc.                          
 *                                      
 * </pre>
 * <br><br>
 * The user creates an instance of {@link GralHorizontalSelector} in any thread maybe as composite, 
 * it is independent of the graphic itself.
 * Then the user builds its graphic appearance with invocation of {@link GralHorizontalSelector#setToPanel(GralMngBuild_ifc)}
 * with the derived instance of {@link GralMng} as parameter. 
 * <br><br>  
 * The <code>GralWidget</code> knows the graphic implementation via the {@link GralWidgImpl_ifc} to invoke some methods
 * for the graphical appearance. The interface is independent
 * of the implementation itself. The implementor of this interface for this example 
 * is the {@link org.vishia.gral.swt.SwtHorizontalSelector} implements this methods. 
 * <br><br>
 * The SwtHorizontalSelector refers the platform-specific widget Object (Swing: javax.swing.JComponent, 
 * org.eclipse.swt.widgets.Control etc.), in this case a {@link org.eclipse.swt.widgets.Canvas}. 
 * It contains the special paint routines, mouse handling etc. 
 * <br><br>
 * The platform-specific widget has a reference to the GralWidget, in this case the {@link GralHorizontalSelector}
 * stored as Object-reference. This reference can be used in the paintRoutine, mouseListerner etc. to get
 * information about the GralWidget.  
 * <br><br> 
 * The second form takes most of the characteristics as parameters for the creating method. 
 * It needs inheritance.
 * <pre>
 *   GralButton <-------UserCanAssociate it but can't create the instance with constructor.
 *       |
 *       |-------------|>SwtButton
 * </pre> 
 * The instance is Graphic-Implementation-specific and it is created with this interface as factory:<br>
 * {@link #addButton(String, GralUserAction, String)}.
 * <br><br>
 * The ObjectModelDiagram may shown the relations:<br>
 * <img src="../../../../../img/Widget_gral.png"><br>
 * In this graphic the relationship between this class and the graphical implementation layer widget
 * is shown with the example 'text field' in SWT:
 * <ul>
 * <li>The class {@link GralTextField} is the derived class of this to represent a text field
 *   in an implementation independent way.
 * <li>The class {@link org.vishia.gral.swt.SwtTextFieldWrapper} is the implementor for the GralTextField
 *   for SWT graphic. 
 * <li>That implementor knows is SWT-specific and refers the SWT widget {@link org.eclipse.swt.widgets.Text}
 *   It based on the SWT-specific {@link org.eclipse.swt.widgets.Widget}. 
 * </ul>   
 * <br>   
 * The implementation layer widget should to be deal with this GralWidget because of some overridden
 * methods of the implementation layer widget need that. The general contract is, 
 * that the implementation layer widget refers this GralWidget in its commonly user data field.
 * For SWT it is {@link org.eclipse.swt.widgets.Widget#setData(Object)} method.
 * <br><br>
 * The Widget knows its {@link GralPos} at its panel where it is placed. The panel knows all widgets
 * which are placed there (widgetList).
 * 
 * 
 * <br>
 * <br>
 * <b>Change a widget from application in any thread</b>:<br> 
 * The user can invoke the methods of the widget to animate it in a GUI etc, for example {@link #setBackColor(GralColor, int)}
 * or {@link #setValue(int, int, Object)}. This methods can be called in any thread. This concept is described with the
 * method {@link #setText(CharSequence)}, appropriate for all set methods.
 * 
 * 
 * <br>
 * <br>
 * <b>Concept of data binding</b><br>
 * 2012-05-19
 * <br>
 * <img src="../../../../../img/WidgetVariable_gral.png"><br>
 * A widget has 2 associations: {@link #variable} and {@link #variables} to a management class {@link VariableAccessWithIdx}
 * which knows the user data via a commonly {@link VariableAccess_ifc}. The data can be existing in the 
 * user space with this interface. That part of user software doesn't know the graphical view of the data.
 * The graphical part of software calls any refresh of showing, it calls the method {@link #refreshFromVariable(VariableContainer_ifc)}
 * for all visible widgets. With this the widget gets the data from user with the variable associations
 * and prepares it for the proper appearance depending on the kind of widget, format String etc.
 * <br><br>
 * The association to the correct variable is given only with a String as argument of the {@link #setDataPath(String)}
 * method. The variable is found with the second interface {@link VariableContainer_ifc} which should be known
 * by the graphical part of software and which is one parameter of the {@link #refreshFromVariable(VariableContainer_ifc)} method.
 * The conclusion between the String given variable name or path and the data can be supplied in any form
 * in the users software, which knows the data.
 * <br><br>
 * The data can be coming from any remote device. In that kind there are two ways to get the actually values:
 * <ol>
 * <li>There is a cyclically communication. The remote device sends all data in a cycle maybe some 100 milliseconds.
 *   Then the actual data are present. The superior control should be call the {@link #refreshFromVariable(VariableContainer_ifc)}-method
 *   if the data are received yet or in any other proper cycle.
 * <li>The data are requested from the remote device only if they are need either for displaying in widgets 
 *   of for other reasons. This kind of data holding are proper especially if they are a lot of data, 
 *   not all of them should be communicated any time.
 * </ol>
 * For the second approach a time of actuality and a time of requesting are used. The method {@link #requestNewValueForVariable(long)}
 * can be used to force communication.     
 * <br><br>
 * <b>Strategy of changing the graphical content of a widget</b>:<br>
 * See {@link GralWidget_ifc} 
 * <br><br>
 * <b>Strategy to create widgets and positioning</b>: see {@link GralMng.GralMngFocusListener}.  
 *
 * @author Hartmut Schorrig
 *
 */
public class GralWidget implements GralWidget_ifc, GralSetValue_ifc, GetGralWidget_ifc, GralWidgImpl_ifc
{
  
  /**Version, history and license.
   * <ul>
   * <li>2014-01-15 Hartmut new: {@link #getCmd(int)} with options.
   * <li>2014-01-03 Hartmut new: {@link #isInFocus()} 
   * <li>2013-12-21 Hartmut chg: {@link #repaint()} invokes redraw immediately if it is in graphic thread.
   *   It invokes {@link #repaint(int, int)} with the {@link #repaintDelay} if it is not in graphic thread.
   *   It does nothing if the implementation layer widget is not created yet. It means it can invoked
   *   without parameter in any case.
   * <li>2013-12-21 Hartmut chg: {@link ImplAccess#setDragEnable(int)} and setDropEnable moved from the core class.
   *   It is adapt after change {@link GralTextField}. 
   * <li>2013-12-21 Hartmut new: {@link #setToPanel(GralMngBuild_ifc)} is final now and invokes 
   *   {@link GralMngBuild_ifc#setToPanel(GralWidget)}. That method handles all widget types. 
   * <li>2013-11-11 Hartmut new: {@link #refreshFromVariable(VariableContainer_ifc, long, GralColor, GralColor)}
   *   which shows old values grayed. 
   * <li>2013-11-11 Hartmut chg: {@link #setFocus()} searches the {@link GralTabbedPanel} where the widget is
   *   member of and invokes its {@link GralTabbedPanel#selectTab(String)}. It is not correct that the graphic
   *   implementation layer does that itself. 
   * <li>2013-06-29 Hartmut new: {@link #setToPanel(GralMngBuild_ifc)} as common method.
   * <li>2013-06-16 Hartmut new {@link #_wdgImpl}. This instance was present in the past but removed. The concept is re-activated
   *   because a graphic-implementation-independent GralWidget instance can have any generic types
   *   and can be created as composite (with final type name = new Type(...)). 
   *   See comments of class {@link GralMngBuild_ifc}.
   * <li>2013-03-13 Hartmut new {@link #getContentIdent()}, {@link #setContentIdent(long)}
   * <li>2013-03-13 Hartmut new {@link #bShouldInitialize}
   * <li>2012-09-24 Hartmut chg: {@link #getName()} now returns {@link #sDataPath} or {@link #sCmd} if the other info are null.
   * <li>2012-09-24 Hartmut chg: {@link #refreshFromVariable(VariableContainer_ifc)} for long and double values.
   * <li>2012-09-17 Hartmut new {@link ConfigData} and {@link #cfg}, used yet only for {@link ConfigData#showParam}.
   * <li>2012-09-17 Hartmut chg whatIsChanged#whatIsChanged} moved from {@link GralTextField}. The concept is valid for all widgets
   *   in cohesion with the concept of the whatIsChanged}.
   * <li>2012-09-17 Hartmut chg {@link #setActionShow(GralUserAction, String[])} now with parameters.  
   *   
   * <li>2012-08-21 Hartmut new {@link DynamicData} and {@link #dyda} for all non-static widget properties, the dynamic data
   *   are that data which are used for all widget types in runtime. TODO: store the configuration data (all other) in an
   *   inner class CfgData or in a common class cfgdata see {@link org.vishia.gral.cfg.GralCfgData}.
   * <li>2012-08-21 The method {@link #setBackColor(GralColor, int)}, {@link #setLineColor(GralColor, int)} and {@link #setTextColor(GralColor)}
   *  are declared in the {@link GralWidget_ifc} yet and implemented here using {@link #dyda}.  
   * <li>2012-07-29 Hartmut chg: {@link #setFocus()} and {@link #setFocus(int, int)} can be called in any thread yet.
   * <li>2012-04-25 Hartmut some enhancements
   * <li>2012-04-07 Hartmut chg: {@link #refreshFromVariable(VariableContainer_ifc)} regards int16, int8
   * <li>2012-04-01 Hartmut new: {@link #refreshFromVariable(VariableContainer_ifc)}. A GralWidget is binded now
   *   more to a variable via the new {@link VariableAccessWithIdx} and then to any {@link VariableAccess_ifc}.
   *   It is possible to refresh the visible information from the variable.
   * <li>2012-01-04 Hartmut new: {@link #repaintDelay}, use it.  
   * <li>2012-03-31 Hartmut new: {@link #isVisible()} and {@link ImplAccess#setVisibleState(boolean)}.
   *   renamed: {@link #implMethodWidget_} instead old: 'gralWidgetMethod'.
   * <li>2012-03-08 Hartmut chg: {@link #repaintRequ} firstly remove the request from queue before execution,
   *   a new request after that time will be added newly therefore, then execute it.
   * <li>2012-02-22 Hartmut new: catch on {@link #repaintGthread()} and continue the calling level
   *   because elsewhere the repaint order isn't removed from the {@link org.vishia.gral.base.GralGraphicThread#addDispatchOrder(GralGraphicTimeOrder)}-queue.
   * <li>2012-02-22 Hartmut new: implements {@link GralSetValue_ifc} now.
   * <li>2012-01-16 Hartmut new Concept {@link #repaint()}, can be invoked in any thread. With delay possible. 
   *   All inherit widgets have to be implement  {@link #repaintGthread()}.
   * <li>2011-12-27 Hartmut new {@link #setHtmlHelp(String)}. For context sensitive help.
   * <li>2011-11-18 Hartmut bugfix: {@link #setFocusGThread()} had called {@link GralMng#setFocus(GralWidget)} and vice versa.
   *   Instead it should be a abstract method here and implemented in all Widgets. See {@link org.vishia.gral.swt.SwtWidgetHelper#setFocusOfTabSwt(org.eclipse.swt.widgets.Control)}.
   * <li>2011-10-15 Hartmut chg: This class is now abstract. It is the super class for all wrapper implementations.
   *   The wrapper implements special interfaces for the kind of widgets. It is more simple for usage, less instances to know.
   *   A GralWidget is able to test with instanceof whether it is a special widget. The element widget is removed because the reference
   *   to the implementation widget will be present in the derived classes.
   * <li>2011-10-01 Hartmut new: method {@link #setFocusGThread()}. It wrappes the {@link GralMng_ifc#setFocus(GralWidget)}.
   * <li>2011-09-18 Hartmut chg: rename from WidgetDescriptor to GralWidget. It is the representation of a Widget in the graphic adapter
   *     inclusive some additional capabilities in comparison to basic graphic widgets, like {@link #sFormat} etc.
   * <li>2011-09-11 Hartmut chg: rename itsPanel to {@link #itsMng}. The original approach was, that the PanelManager manages only one panel
   *     then one window. Now the GralPanelMng manages all panels of one application. It is instantiated only one time.
   *     Therefore this association isn't the associated panel where the widget is member of. 
   * <li>2011-09-08 Hartmut new: method {@link #setLineColor(GralColor, int)}.
   *     Background: Any widget have a background. Most of widgets have lines. The color of them 
   *     should be able to animate if user data are changed.        
   * <li>2011-09-04 Hartmut new: method {@link #setBackColor(GralColor, int)}.        
   * <li>2011-08-14 Hartmut chg: {@link #widget} is now type of {@link GralWidget_ifc} and not Object.
   *    Generally it is the reference to the implementing code of the widget. The implementing code 
   *    may based on a graphic base widget (SWT: Control) and implements the {@link GralWidget_ifc}, 
   *    or it references the graphic base widget instance. The class {@link SwtWidgetSimpleWrapper} 
   *    is able to wrap simple graphical base widget instances.
   * <li>2011-08-13 Hartmut new: WidgetDescriptor now contains the position of the widget. 
   *     It is used for resizing of large widgets.
   *     A large widget is a widget, which lengthens over the panel and it is changed in size with panel size change. 
   *     A typical example is a text-area-widget.
   * <li>2011-06-20 Hartmut new: method {@link #gralMng()} It is the panel manager!
   * <li>2011-05-26 Hartmut new: separate action in {@link #actionChanging} and {@link #actionShow}.
   *     The actionChanging was the old action. It was called from the listener of the widgets of the underlying graphic
   *     if any changing is done on the widget (mouse click etc). But the actionShow is necessary too 
   *     to prepare values to animate widgets without knowledge of the special kind of widget. The application
   *     should call only the actionShow, all specifics should be done in the action.
   * <li>2011-05-22 Hartmut new: The Widget knows its {@link #itsCfgElement} now if it is present.
   *   It is possible to configurate widgets with the GralDesigner up to now.
   * <li>2011-05-14 Hartmut chg: The WidgetDescriptor is now non-generic. 
   *   Older Concept: Store the GUI implementation widget type as generic type there.
   *   But now a widget is stored as Object and it is casted in the implementation. It is more simple 
   *   because the type is only used and the casting is only necessary in the implementation level.       
   * <li>2011-06-00 Hartmut created
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
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
   */
  public static final int version = 20130313;

  
  /**The widget manager from where the widget is organized. Most of methods need the information
   * stored in the panel manager. This reference is used to set values to other widgets. */
  protected GralMng itsMng;
  
  /**The position of the widget. It may be null if the widget should not be resized. */
  private GralPos _wdgPos;


  /**The implementation specific widget. The instance is derived from the graphic implementation-specific
   * super class of all widgets such as {@link org.eclipse.swt.widgets.Control} or {@link java.awt.Component}. 
   * The user can check and cast this instance if some special operations may be need graphic-implementation-dependent.
   * It is recommended that implementation specific features should not used if they are not necessary
   * and the application should be held graphic implementation independent.
   * <br><br>
   * This reference is null if the GralWidgets extends the Graphic specific implementation widget.
   * It should be used in the future (2013-06).
   */
  public ImplAccess _wdgImpl;


  protected GralMngBuild_ifc buildMng;
  
  /**Association to the configuration element from where this widget was built. 
   * If the widget is moved or its properties are changed in the 'design mode' of the GUI,
   * this aggregate data are adjusted and re-written to a file. The configuration elemenet
   * contains all data which are necessary to build the appearance of the GUI.
   * <br>
   * If this aggregation is null, the widget can't be changed in the design mode of the GUI.
   * It is created directly without configuration data. 
   */
  private GralWidgetCfg_ifc itsCfgElement;

  
  /**This class holds common configuration data for widgets.
   */
  public static class ConfigData
  {
    /**Parameter which are used from a {@link GralWidget#setActionShow(GralUserAction, String[])} method.
     * The parameter are converted from a String given form.
     */
    public Object[] showParam;
  }
  
  /**Reference to the common configuration data for widgets. */
  public ConfigData cfg = new ConfigData();
  
  /**Name of the widget in the panel. */
  public String name;
  
    
  
  
  /**Panel where the widget is member of. */
  //public final GralPanelContent itsPanel;
  
  /**The graphical widget. It is untyped because it depends on the underlying graphic system. 
   * It may be a wrapper class arround a graphical widget too. 
   * This element is used for setting operations, which depends from the graphic system
   * and the type of the widget. It is only used in the graphic-system-special implementation.
   * */
  //protected GralWidget_ifc widget;
  
  /**numeric info what to do (kind of widget). 
   * <ul>
   * <li>B: a Button: has a color, has an action method.
   * <li>c: curve view
   * <li>C: traCk of curve view
   * <li>D: a LED
   * <li>xxx E: an edit field, 1 line
   * <li>xxx e: an edit area
   * <li>F: input file selection field
   * <li>h; HTML text  box (browser)
   * <li>I: a line
   * <li>i: an image
   * <li>k: a tree node
   * <li>K: a tree leafe
   * <li>l: a list or table
   * <li>L: a list or table line
   * <li>M: a Menu entry
   * <li>n: a Horizontal Selector
   * <li>r: a rectangle area 
   * <li>R: a rectangle line 
   * <li>S: a text field to show
   * <li>s: a text area.
   * <li>T: a text input field
   * <li>t: a text input area.
   * <li>U: a graphical value representation (bar etc)
   * <li>V: a graphical value enter representation (slider etc)
   * <li>w: A window.
   * <li>@: A Tabbed Panel
   * <li>$: Any Panel (composite)
   * <li>+: A canvas panel
   * <li>*: A type (not a widget, common information) See {@link org.vishia.gral.cfg.GralCfgData#new_Type()}
   * </ul>
   * */
  public char whatIs;
  
  public String sToolTip;
  
  /**Textual description of the showing method. */
  private String sShowMethod;

  private String[] sShowParam;
  
  /**Textual informations about content. It may be a data path or adequate. */
  private String sDataPath;
  
  /**If not null, it is the right-mouse-button menu for this widget. */
  protected GralMenu contextMenu;
  
  
  /**An index associated to the data. */
  private int dataIx;
  

  /**Textual info about representation format. */
  protected String sFormat;
  
  /**Numeric informations about the content. */
  //int[] indices;
  
  /**One variable which is associated with this widget. This reference may be null.
   * Alternatively {@link #variables} may be set.
   * See {@link #getVariableFromContentInfo(VariableContainer_ifc)}.
   * See {@link #setValue(float)}, {@link #setValue(String)}.
   * See {@link #indices} to use an array- or bit-variable.
   */
  private VariableAccess_ifc variable;
  
  /**More as one variable which are associated with this widget. This reference may be null.
   * Alternatively {@link #variable} may be set.
   * See {@link #getVariableFromContentInfo(VariableContainer_ifc)}.
   * See {@link #setValue(float)}, {@link #setValue(String)}.
   * See {@link #indices} to use an array- or bit-variable.
   */
  private List<VariableAccess_ifc> variables;
  
  
  /**Action method on activating, changing or release the widget-focus. */
  protected GralUserAction actionChanging;

  
  /**Action method for showing. */
  protected GralUserAction actionShow;

  protected GralUserAction actionDrag, actionDrop;
  
  /**This action will be called if the widget gets the focus. */
  protected GralUserAction actionFocused;
  
  /**command string given by the action as parameter. */
  public String sCmd;
  
  
  /**The relative path to a html help label (maybe an URL, or file, or file with label). */
  protected String htmlHelp;
  
  /**Any special info, may be set from any user class. It should help to present the content. 
   * This info can be set and changed after registration. */
  private Object oContentInfo;
  
  
  
  
  /**Set true if its shell, tab card etc is be activated. Set false if it is deactivated.
   * It is an estimation whether this widget is be shown yet. 
   */
  protected boolean bVisibleState = true;
  
  
  /**Set to true on {@link #setEditable(boolean)}. 
   * With that designation the cyclically refresh of the text field can be prevented. 
   * */
  protected boolean bEditable;
  
  
  /**Set on focus gained, false on focus lost. */
  protected boolean bHasFocus;
  
  /**If this bit is true on an edit field, it should be initialized.
   * 
   */
  protected boolean bShouldInitialize = true;
  
  
  /**Set to true from any listener of the implementation level if the data of the widget was changed from GUI handling.
   * If the data are changed from any Gral method invocation, this bit should not set to true.
   * For example a key listener changes the content of a text edit field, then this bit should be set.
   * This bit should be cleared if the GUI-content of the widget is synchronized with the widget data cells. 
   * Note that the GUI-content of a widget can be changed only in the GUI thread, whereby the content of the 
   * {@link #dyda} can be read and write in any threat. This bit helps to synchronize. */
  protected boolean bTextChanged;
  


  /**Delay to repaint.
   * 
   */
  protected int repaintDelay = 100, repaintDelayMax = 100;

  /**The time when the bVisible state was changed. */
  private long lastTimeSetVisible;
  
  protected long dateUser;
  
  /**This inner class holds the dynamic data of a widget.
   * This data are existent for any widget independent of its type.
   * It can be used in a specific way depending on the widget type.
   */
  public final static class DynamicData {
    
    /**32 bit what is changed, see {@link GralWidget#chgColorText} etc. 
     * TODO should be protected. */
    public AtomicInteger whatIsChanged = new AtomicInteger(); 
    
    /**Sets what is changed, Bits defined in {@link GralWidget.ImplAccess#chgColorBack} etc.
     * @param mask one bit or some bits. ImplAccess.chgXYZ
     */
    public void setChanged(int mask){
      int catastrophicalCount = 1000;
      boolean bOk;
      do {
        int act =whatIsChanged.get();
        int newValue = act | mask;
        bOk = whatIsChanged.compareAndSet(act, newValue);
      } while(!bOk && --catastrophicalCount >= 0);
    }
    
    /**Resets what is changed, Bits defined in {@link GralWidget.ImplAccess#chgColorBack} etc.
     * This routine should be called in the paint routine whenever the change was succeeded.
     * @param mask one bit or some bits. ImplAccess.chgXYZ
     */
    public void acknChanged(int mask){
      int catastrophicalCount = 1000;
      boolean bOk;
      do {
        int act =whatIsChanged.get();
        int newValue = act & ~mask;
        bOk = whatIsChanged.compareAndSet(act, newValue);
      } while(!bOk && --catastrophicalCount >= 0);
    }
    
    /**Three colors for background, line and text should be convert to the platforms color and used in the paint routine. 
     * If this elements are null, the standard color should be used. */
    public GralColor backColor = GralColor.getColor("wh"), backColorNoFocus = GralColor.getColor("lgr")
      , lineColor = GralColor.getColor("bk"), textColor = GralColor.getColor("bk");

    /**It depends of the pixel size. Therefore set after setToPanel, if GralMng is known. */
    public GralFont textFont;
    
    /**A text to display. */
    public String displayedText;
    
    /**Any specific value. */
    public Object[] oValues;
    
    public float fValue, minValue, maxValue;
    
    public Object visibleInfo;
    
    public Object userData;
    
    public float[] fValues;
  }
  
  
  protected final DynamicData dyda = new DynamicData();
  
  
  //protected GralWidget(char whatIs)
  //{ this.whatIs = whatIs;
  //}

  /**Creates a widget which is not positioned.
   * @param sName
   * @param whatIs
   */
  @Deprecated public GralWidget(String sName, char whatIs){ this(null, sName, whatIs); }

  
  
  /**Creates a widget.
   * @param whatIs
   * @param sName
   * @param pos If null then the widget is not positioned. !=null then a position string.
   *   The position is taken relative to the {@link GralMng#pos}, the {@link GralMng#pos} is changed
   *   using 
   */
  protected GralWidget(String posString, String sName, char whatIs)
  { this.name = sName;
    //this.widget = null;
    this.whatIs = whatIs;
    this.itsCfgElement = null;
    itsMng = GralMng.get();
    assert(itsMng !=null);  //should be created firstly in the application, since 2015-01-18
    if(posString !=null) {
      initPos(itsMng.pos.setNextPos(posString));
    } //else: don't set the pos, it is done later 
  }
  
  
  void initPos(GralPos pos) {
    this._wdgPos = pos;
    if(_wdgPos.panel !=null){
      _wdgPos.panel.addWidget(this);
    } else {
      System.out.println("GralWidget.GralWidget - pos without panel");
    }
  }
  
  
  @Deprecated public GralWidget(String sName, char whatIs, GralMng mng)
  { this(sName, whatIs);
    itsMng = GralMng.get();
    assert(itsMng !=null);  //should be created firstly in the application, since 2015-01-18
    if(mng !=null){
      assert(this.itsMng == mng);
      //sets the mng and the pos of Window in that cases
      //where the mng is present on ctor. (not in new form)
      //setPanelMng(mng);   
      /*
      if(mng.posUsed){
        mng.pos.setNextPosition();
        mng.posUsed = false;
      }
      this.pos = mng.getPositionInPanel();  //Note: makes a clone because the pos in panel is reused. 
      */
    }
  }
  
  
  
  /**Sets this widget to the current panel at the current given position. 
   * It creates the graphical appearance using the capabilities of the derived GralMng for the systems graphic level.
   * The type of the derived GralWidget is tested (instanceof) to create the correct graphical widget.
   * 
   * @param mng The instance of derived Graphic Manager
   * @throws IllegalStateException This routine can be called only if the graphic implementation widget is not 
   *   existing. It is one time after startup or more as one time if {@link #removeWidgetImplementation()}
   *   was called. 
   */
  public final void setToPanel(GralMngBuild_ifc mng) throws IllegalStateException {
    if(_wdgImpl !=null) throw new IllegalStateException("setToPanel faulty call - GralTable;");
    if(dyda.textFont == null) { //maybe set with knowledge of the GralMng before.
      GralMng mngg = (GralMng)mng;
      dyda.textFont = mngg.propertiesGui.getTextFont(mngg.pos.height()); 
    }
    mng.setToPanel(this);
  }
  

  
  public GralPos pos(){ return _wdgPos; } 
  
  
  
  public void chgPos(GralPos newPos){
    dyda.setChanged(ImplAccess.chgPos);
    _wdgPos = newPos;
  }
  
  /**Returns this.
   * @see org.vishia.gral.base.GetGralWidget_ifc#getGralWidget()
   */
  @Override public GralWidget getGralWidget(){ return this; }
  
  
  /**Default implementation:Most of widgets may have only one implementation widget. This returns null.
   * @see org.vishia.gral.ifc.GralWidget_ifc#getWidgetMultiImplementations()
   */
  //public Object[] getWidgetMultiImplementations(){ return null; }

  
  
  public void setPrimaryWidgetOfPanel(){
    _wdgPos.panel.setPrimaryWidget(this);
  }

  
  @Override public String getName(){ 
    if(name !=null) return name;
    else if(sDataPath !=null) return sDataPath;
    else if(sCmd !=null) return sCmd;
    else return toString();
  }
  
  /**Sets the graphical widget. It is a wrapper around the widget of the graphic implementation base.
   * This method shouldn't invoke by an user's application. It is only invoked by the gral itself. 
   * @param widget The wrapper.
   */
  //public void setGraphicWidgetWrapper(GralWidget_ifc widget){ this.widget = widget; }
  
  /**Gets the graphical widget. The difference between this class and the graphical widget is:
   * This class contains unified description data to any kind of widget, where the graphical widget
   * is a special or simple wrapper around the implementation of the widget in the graphical implementation base.
   * 
   * @return The gral graphical widget. Note: The type can be instanceof some derived interfaces of the gral.
   * @deprecated
   */
  //@Deprecated
  //public GralWidget_ifc getGraphicWidgetWrapper(){ return this; }
  
  
  /**Sets a application specific info. 
   * It should help to present user data which are associated to this widget. 
   * This info can be set and changed anytime. */
  public void setContentInfo(Object content){  oContentInfo = content;}
  
  /**Gets the application specific info. See {@link #setContentInfo(Object)}. */
  public Object getContentInfo(){ return oContentInfo; }
  
 
  
  @Override public void setEditable(boolean editable){
    if(bEditable != editable) {
      bEditable = editable;
      dyda.setChanged(ImplAccess.chgEditable); 
      repaint(repaintDelay, repaintDelayMax);
    }
  }

  @Override public boolean isEditable(){ return bEditable; }
  

  @Override public boolean isNotEditableOrShouldInitialize(){ return !bEditable || bShouldInitialize; }
  
  /**Sets the data path. It is a String in application context.
   * @param sDataPath
   */
  @Override public void setDataPath(String sDataPath){  
    this.sDataPath = sDataPath;
    variable = null;
    variables = null;
  }
  
  /**Changes the data path
   * @param sDataPath the new one
   * @return the last one.
   */
  public String getDataPath(String sDataPath)
  {  String sDataPathLast = this.sDataPath;
    this.sDataPath = sDataPath;
    return sDataPathLast;
  }
  
  public String getCmd(){ return sCmd; }
  
  
  /**Get the command string from the {@link #setCmd(String)} with choice of an option.
   * The command string should have the form "base[Option1|Option2|Option3]End" 
   * whereby base and end can be empty. if option is <0 an IndexOutOfBoundsException is thrown.
   * If option is >= the given number of options, the option part is replaced by "??".
   * That may be more helpfull to detect errors. 
   * @param option number >=0
   * @return "baseOptionEnd"
   */
  public String getCmd(int option){
    int pos1 = sCmd.indexOf('[');
    int pos2 = sCmd.indexOf(']', pos1+1);
    if(pos1 >=0 && pos2 > pos1){
      String sBase = sCmd.substring(0, pos1);
      String sEnd = sCmd.substring(pos2+1);  //maybe ""
      String[] sOptions = sCmd.substring(pos1+1, pos2).split("\\|");
      if(option >= sOptions.length){ return sBase + "??" + sEnd; }
      else{ return sBase + sOptions[option] + sEnd; }
    } else {
      return sCmd;
    }
  }
  
  public void setCmd(String cmd){ sCmd = cmd; }
  
  
  /**Gets the data path. It is a String in application context.
   */
  @Override public String getDataPath(){ return sDataPath; }
  
  /**Sets the action in application context for processing of user handling for the widget.
   * Handling means, pressing button, user inputs of text fields
   * The method {@link GralUserAction#exec(int, GralWidget_ifc, Object...)} will be called with following key codes:
   * <ul>
   * <li>{@link KeyCode#focusGained}, {@link KeyCode#focusLost} on enter and leave a text field.
   * <li>{@link KeyCode#mouse1Down} etc, any mouse events on any widget.
   * <li>{@link KeyCode#valueChanged} if the content of a text field is changed.
   * <li>Some more TODO, set breakpoint in the routine.
   * </ul> 
   * @param action any instance. Its action method is invoked depending of the type of widget
   *        usual if the user takes an action on screen, press button etc.
   *        
   */
  public void setActionChange(GralUserAction action){ actionChanging = action; }
  
  /**Gets the action for change the widget. */
  public GralUserAction getActionChange(){ return actionChanging; }
  
  
  /**Sets the action in application context which is invoked for applying user data to show in the widget.
   * <br><br>
   * The invocation of the action should be organized in the user context, maybe cyclically for all widgets
   * of visible windows  or if any data are received. 
   * <br><br>
   * In the action the user should read any data from its application
   * and invoke {@link #setValue(int, int, Object, Object)} after data preparation to display the value.
   * Because the {@link GralWidget} is given as parameter, the implementation can use the information
   * for example {@link #sDataPath} or {@link #sFormat}. The implementation of the action can be done
   * in the users context in a specialized form, or some standard actions can be used. 
   * See notes of {@link #getActionShow()}.
   * <br><br>
   * To get the action in a script context (GuiCfgBuilder) some actions can be registered 
   * using {@link org.vishia.gral.ifc.GralMngBuild_ifc#registerUserAction(String, GralUserAction)}. They are gotten by name
   * invoking {@link org.vishia.gral.ifc.GralMngBuild_ifc#getRegisteredUserAction(String)} 
   * in the {@link org.vishia.gral.cfg.GralCfgBuilder}.
   * 
   * @param action The action instance.
   * @param param maybe param for the show method.
   */
  public void setActionShow(GralUserAction action, String[] param){ actionShow = action; sShowParam = param; }
  
  /**Gets the action to show the widget. This method is helpfully to invoke showing after receiving data
   * in the users context. Invoke {@link GralUserAction#userActionGui(String, GralWidget, Object...)}
   * with this WidgetDescriptor and additional user data. The implementation of that method
   * may be done in the users context but in another module or the implementation may be given in any 
   * library superordinated to this graphic adapter library but subordinated in respect to the explicit application.
   * The usage of a show method given in the implementation of {@link GralUserAction} helps to separate
   * the invocation of showing and the decision what and how is to show.
   */
  public GralUserAction getActionShow(){ return actionShow; }
  
  public void setActionFocused(GralUserAction action){ actionFocused = action; }

  public GralUserAction getActionFocused(){ return actionFocused; }
  
  
  public String getsToolTip()
  {
    return sToolTip;
  }


  public void setToolTip(String sToolTip)
  {
    this.sToolTip = sToolTip;
  }

  
  /**Sets the action to receive a drop event and initializes the drop feature of the widget.
   * For drag file the 'drag get action' method will be offered in the params[0] a String[][] reference. 
   * This String reference array has to be filled with the absolute path of the file using String[0][0]. 
   * After that callback invocation a drag file object will be created therewith internally.
   *  
   * @param action The drag file get action.
   * @param dropType one of {@link org.vishia.util.KeyCode#dropFiles} or ..dropText
   */
  public void setDragEnable(GralUserAction action, int dragType)
  {
    actionDrag = action;
    if(_wdgImpl !=null) _wdgImpl.setDragEnable(dragType);  // call implementation specific drop handling. 
  }

  public GralUserAction getActionDrag(){ return actionDrag; }
  
  
  /**Sets the action to receive a drop event and initializes the drop feature of the widget.
   * @param action The action will be called
   * @param dropType one of {@link org.vishia.util.KeyCode#dropFiles} or ..dropText
   */
  public void setDropEnable(GralUserAction action, int dropType)
  {
    actionDrop = action;
    if(_wdgImpl !=null) _wdgImpl.setDropEnable(dropType);  // call implementation specific drop handling. 
  }

  public GralUserAction getActionDrop(){ return actionDrop; }
  
  
  public String getShowMethod()
  {
    return sShowMethod;
  }

  
  public int getDataIx(){ return dataIx; }

  public void setDataIx(int dataIx){ this.dataIx = dataIx; }

  
  /**Returns the parameter of the show method.
   * The parameters for the show-method are given as "showMethod(param, param, ...)"
   * while calling {@link #setActionShow(GralUserAction, String[])}. They are split in extra Strings,
   * this  
   * @return
   */
  public String[] getShowParam(){ return sShowParam; }
  
  /**Clear the parameter if they are over-taken already.
  */
  public void clearShowParam(){ sShowParam = null; }


  public String getFormat()
  {
    return sFormat;
  }


  public void setFormat(String sFormat)
  {
    this.sFormat = sFormat;
  }

  
  /**Gets the context menu to add a menu item. If this widget hasn't a gral context menu, then 
   * the context menu is created by calling {@link GralMng#addContextMenu(GralWidget)}.
   * If the widget has a context menu already, it is stored in the reference {@link #contextMenu}.
   * @return the context menu root for this widget.
   */
  public GralMenu getContextMenu(){
    if(contextMenu == null){
      contextMenu = itsMng.createContextMenu(this);   //delegation, the widget mng knows the implementation platform.
    }
    return contextMenu;
  }
  
  
  public void setHtmlHelp(String url){ htmlHelp = url; }
  
  public String getHtmlHelp(){ return htmlHelp; }
  
  
  
  
  /**Sets the GralMng.
   * @deprecated it should be set by the MethodsCalledbackFromImplementation ctor. 
   */
  @Deprecated
  public void setPanelMng(GralMng mng)
  { this.itsMng = mng; 
    if(this._wdgPos !=null) 
      throw new IllegalStateException("GralWidget - setPos() is set already.");
    this.initPos(mng.getPosCheckNext());  //always clone it from the central pos 

  }
  
  
  
  /**Gets the info to access the values for this widget in the users context.
   * If this method is called the first time for the widget after start the application, the access info
   * is searched in the container calling {@link VariableContainer_ifc#getVariable(String, int[])}
   * with the stored textual info {@link #setDataPath(String)} and {@link #setDataIx(int)}.
   * This operation may need a little bit of calculation time, which were to expensive if a lot of widgets
   * should be provided with user values. Therefore the returned {@link VariableAccess_ifc} instance is stored
   * in the {@link #oContentInfo} of the widget and returned on the further calls.
   * <br>
   * The returned {@link VariableAccess_ifc} should be allow the fast access to users values.
   *  
   * @param container The container where all {@link VariableAccess_ifc} should be found.
   * @return The access to a user variable in the user's context, null if the data path is empty.
   */
  public VariableAccess_ifc getVariableFromContentInfo(VariableContainer_ifc container)
  {
    //DBbyteMap.Variable variable;
    VariableAccess_ifc variable;
    Object oContentInfo = this.getContentInfo();
    if(oContentInfo == null){
      //first usage:
      String sPath1 = this.getDataPath();
      if(sPath1 !=null && (sPath1 = sPath1.trim()).length()>0){
        String sPath = itsMng.replaceDataPathPrefix(sPath1);
        variable = container.getVariable(sPath1);
        this.setContentInfo(variable);
      } else {
        variable = null;
      }
    } else if(oContentInfo instanceof VariableAccessWithIdx){
      variable = (VariableAccessWithIdx)oContentInfo;
    } else {
      variable = null;  //other info in widget, not a variable.
    }
    return variable; 
  }
  

  
  
  @Override public void refreshFromVariable(VariableContainer_ifc container){
    refreshFromVariable(container, -1, null, null);
  }

  
  
  
  
  
  /**Refreshes the graphical content with the content of the variables.
   * First time if a variables is not associated the variable is searched in the container
   * by the given {@link #setDataPath(String)}. The next times the variable is used independent of
   * the reference to the container and independent of the data path. If {@link #setDataPath(String)}
   * was called again, the variables are searched in the container newly.
   * <br><br>
   * If the data path contains ',' as separator, more as one variable is associated.
   * 
   * @param container contains variables able to search by string.
   */
  @Override public void refreshFromVariable(VariableContainer_ifc container
      , long timeLatest, GralColor colorRefreshed, GralColor colorOld
  ){    
    if(sDataPath !=null && sDataPath.startsWith("intern"))
      stop();
    if(this instanceof GralLed)
      stop();
    //
    //check and search the variable(s):
    //
    if(variable ==null && variables == null && sDataPath !=null && !sDataPath.startsWith("#")){ //no variable known, get it.
      if(sDataPath.contains(",")){
        String[] sDataPaths = sDataPath.split(",");
        variables = new LinkedList<VariableAccess_ifc>();
        for(String sPath1: sDataPaths){
          if(sPath1.contains("["))
            stop();
          String sPath2 = sPath1.trim();
          String sPath = itsMng.replaceDataPathPrefix(sPath2);
          VariableAccess_ifc variable1 = container.getVariable(sPath);
          if(variable1 !=null){
            variables.add(variable1);
          }
        }
      } else {
        if(sDataPath.contains("["))
          stop();
        String sPath2 = sDataPath.trim();
        String sPath = itsMng.replaceDataPathPrefix(sPath2);
        variable = container.getVariable(sPath);
      }
    }
    //
    //
    //
    if(actionShow !=null){
      //The users method to influence how the widget is presented in view:
      if(!actionShow.exec(0, this, variable !=null ? variable : variables)){
        System.err.println("GralWidget fault actionShow in " + name + "; returns false; sShowMethod = " + sShowMethod);
      }
    } else {
      //standard behavior to show: call setValue or setText which may overridden by the widget type.
      if(variable !=null){
        if(sDataPath !=null && sDataPath.contains("#dEB:activeDamping.i1intg"))
          Assert.stop();
        if(colorRefreshed !=null && colorOld !=null){
          long timeVariable = variable.getLastRefreshTime();
          long timediff = timeVariable - timeLatest;
          boolean bOld = timeVariable == 0 || timediff < 0;
          if(bOld ){
            setBackColor(colorOld, 0);
          } else {
            setBackColor(colorRefreshed, 0);
          }
        }
        char cType = variable.getType();
        String sValue = null;
        switch(cType){
          case 'Z': case 'S': case 'B':
          case 'I': setValue(variable.getInt()); break;
          case 'L': setValue(variable.getLong()); break;
          case 'F': setValue(variable.getFloat()); break;
          case 'D': 
            Object[] value = new Double[1]; 
            value[0] = new Double(variable.getDouble()); 
            setValue(value);
            break;
          case 's': setText(variable.getString()); break;
          default:  sValue = "?" + cType; //variable.getInt());  //at least request newly if type is faulty
          }
          if(sValue !=null){
            //if(bOld){ setText("? " + sValue); }
            //else 
            { setText(sValue); }
          }
      } else if(variables !=null){
        if(variables.size() == 0){ variables = null; }
        else {
          Object[] values = new Object[variables.size()];
          int ixVal = -1;
          for(VariableAccess_ifc variable1: variables){
            char cType = variable1.getType();
            switch(cType){
              case 'Z': case 'S': case 'B':
                case 'I': values[++ixVal] = variable1.getInt(); break;
              case 'L': setValue(variable.getFloat()); break;
              case 'F': values[++ixVal] = variable1.getFloat(); break;
              case 's': values[++ixVal] = variable1.getString(); break;
              default:  setText("?" + cType); //variable.getInt());  //at least request newly
            } //switch
            
          }
          setValue(values);
        }
      } else if(sDataPath !=null){
        setText("?? " + sDataPath); //called on fault variable path.
        setBackColor(colorOld, 0);
      }
    }
    
  }
  
  
  
  /**Requests new values for all variables which are associated to this widget. This method is usefull
   * if the variables are filled by a communication with any remote device and that filling
   * should be requested for the current visible variables.
   */
  public void requestNewValueForVariable(long timeRequested){
    if(variable !=null){ variable.requestValue(timeRequested); }
    else if(variables !=null){
      for(VariableAccess_ifc variable1: variables){
        variable1.requestValue(timeRequested);
      }
    }
  }
  
  
  
  /**Gets the current value of the content of the widget in the given context.
   * @param mng The context.
   * @return The value in String representation, null if the widget has no possibility of input.
   */
  public String getValue()
  { return itsMng.getValueFromWidget(this);
  }
  
  
  
  @Override public boolean isVisible(){
    return bVisibleState;
  }

  
  @Override public boolean isInFocus(){
    return bHasFocus;
  }

  
  /**Sets the widget visible or not.
   * @param visible
   * @return the old state.
   */
  @Override public boolean setVisible(boolean visible){
    dyda.setChanged(visible ? ImplAccess.chgVisible : ImplAccess.chgInvisible);
    repaint(repaintDelay, repaintDelayMax);
    return bVisibleState;
  }
  

  
  
  @Override public GralRectangle getPixelPositionSize(){
    if(_wdgImpl !=null) return _wdgImpl.getPixelPositionSize();
    else throw new IllegalArgumentException("GralWidget - does not know its implementation widget; ");
  }
  
  
  @Override public boolean isChanged(boolean setUnchanged){ 
    boolean bChanged = this.bTextChanged;
    if(setUnchanged){ 
      this.bTextChanged = false; 
    }
    return bChanged; 
  }
  

  
  /**Sets the current value of the content of the widget in the given context.
   * @param cmd see {@link GralMng_ifc#cmdSet} etc. It is possible to set the color etc.
   * @param ident Any number to specify set, maybe 0
   * @param value The value in the necessary representation.
   */
  public void setValue(int cmd, int ident, Object visibleInfo)
  { dyda.visibleInfo = visibleInfo;
    dyda.setChanged(ImplAccess.chgVisibleInfo);
    repaint(repaintDelay, repaintDelayMax);
    //itsMng.setInfo(this, cmd, ident, visibleInfo, null);
  }
  
  
  @Override public void setBackColor(GralColor color, int ix){ 
    if(dyda.backColor == null || !dyda.backColor.equals(color)){
      dyda.backColor = color; 
      dyda.setChanged(ImplAccess.chgColorBack); 
      repaint(repaintDelay, repaintDelayMax);
    }
  }
  
  @Override public GralColor getBackColor(int ix){ 
    return dyda.backColor; 
  }
  
  @Override public void setLineColor(GralColor color, int ix){ 
    if(dyda.lineColor == null || !dyda.lineColor.equals(color)){
      dyda.lineColor = color; 
      dyda.setChanged(ImplAccess.chgColorLine); 
      repaint(repaintDelay, repaintDelayMax);
    }
  }
  
  @Override public void setTextColor(GralColor color){ 
    if(dyda.textColor == null || !dyda.textColor.equals(color)){
      dyda.textColor = color; 
      dyda.setChanged(ImplAccess.chgColorText); 
      repaint(repaintDelay, repaintDelayMax);
    }
  }
  
  
  
  /**Sets the current value of the content of the widget in the given context.
   * @param cmd see {@link GralMng_ifc#cmdSet} etc. It is possible to set the color etc.
   * @param ident Any number to specify set, maybe 0
   * @param value The value in the necessary representation.
   */
  public void setValue(int cmd, int ident, Object visibleInfo, Object userData)
  { dyda.visibleInfo = visibleInfo;
    dyda.userData = userData;
    repaint(repaintDelay, repaintDelayMax);
    //itsMng.setInfo(this, cmd, ident, visibleInfo, userData);
  }
  
  /**Sets a value to show.
   * @param value
   * This routine may be overridden by some specialized widgets.
   */
  @Override public void setValue(float value){
    dyda.fValue = value;
    repaint(repaintDelay, repaintDelayMax);
    //itsMng.setInfo(this, GralMng_ifc.cmdSet, 0, value, null);
  }
  
  
  
  /**Gets the float attribute value of this widget. Returns 0.0 if a float value is never used.
   */
  public float getFloatValue(){ return dyda.fValue; }
  
  
  /**Sets some value to show any content.
   * @param value
   * This routine may be overridden by some specialized widgets.
   */
  @Override public void setValue(Object[] value){
    dyda.oValues = value;
    repaint(repaintDelay, repaintDelayMax);
    //itsMng.setInfo(this, GralMng_ifc.cmdSet, 0, value, null);
  }
  
  
  
  @Override public void setText(CharSequence text){
    dyda.displayedText = text.toString(); 
    dyda.setChanged(ImplAccess.chgText);
    repaint(repaintDelay, repaintDelayMax);
    //System.err.println(Assert.stackInfo("GralWidget - non overridden setText called; Widget = " + name + "; text=" + text, 5));
  }
  
  /**Get the text of this widget. It can be invoked in any thread. 
   * If it is a edit able text field, it returns the current text after focus lost 
   * or pressing any control key.
   * @return The current text.
   */
  public String getText(){ return dyda.displayedText == null? "" : dyda.displayedText; }
  
  
  /**Sets the border of the value range for showing. 
   * If it is a ValueBar, for exmaple, it is the value for 0% and 100%
   * This routine is empty per default, should be overridden if it is necessary.
   * @param minValue
   * @param maxValue
   */
  @Override public void setMinMax(float minValue, float maxValue){
    dyda.minValue = minValue;
    dyda.maxValue = maxValue;
    repaint(repaintDelay, repaintDelayMax);
    //
  }

  
  @Override public long setContentIdent(long date){ long last = dateUser; dateUser = date; return last; }
  
  @Override public long getContentIdent(){ return dateUser; }

  
  
  
  public void setCfgElement(GralWidgetCfg_ifc cfge)
  { this.itsCfgElement = cfge;
  }
  
  
  public GralWidgetCfg_ifc getCfgElement()
  { return itsCfgElement;
  }
  
  
  public final void setFocus(){ setFocus(0,0); }

  
  
  /**Sets the focus to this widget. This method is possible to call in any thread.
   * If it is called in the graphic thread and the delay = 0, then it is executed immediately.
   * Elsewhere the request is stored in the graphic thread execution queue and invoked later.
   * @param delay Delay in ms for invoking the focus request 
   * @param latest 
   */
  public void setFocus(int delay, int latest){
    
    GralPanelContent panel1 = _wdgPos.panel;
    while(panel1 !=null && panel1.pos() !=null){
      GralPanelContent panel2 = panel1.pos().panel;
      if(panel2 instanceof GralTabbedPanel){
        GralTabbedPanel panelTabbed = (GralTabbedPanel)panel2;
        
        String name = panel1.getName();
        panelTabbed.selectTab(name);  //why with name, use GralPanel inside GralTabbedPanel immediately!
      }
      panel1 = panel2;
    }
    if(delay == 0 && itsMng.currThreadIsGraphic()){
      setVisibleState(true);  //has focus, 
      setFocusGThread();
    } else {
      dyda.setChanged(ImplAccess.chgVisible);
      repaint(delay, latest);
      //setFocusRequ.addToGraphicThread(itsMng.gralDevice(), delay);
    }
  }
  
  
  
  
  /**Gets the working interface of the manager. 
   * It can be used to set and get values from other widgets symbolic identified by its name.
   * Note: It is possible too to store the {@link GralWidget} of specific widgets
   * to get and set values and properties of this widgets non-symbolic.
   * @return The manager.
   */
  @Override public GralMng gralMng(){ return itsMng; }
  
  
  /**Gets the panel where the widget is member of. 
   * @return The panel.
   */
  public GralPanelContent getItsPanel(){ return _wdgPos.panel; }
  
  
  /* (non-Javadoc)
   * @see org.vishia.gral.ifc.GralWidget_ifc#repaint()
   */
  @Override public void repaint(){ 
    if(itsMng !=null){ //NOTE: set of changes is possible before setToPanel was called. 
      if(itsMng.currThreadIsGraphic()){
        repaintGthread();     //do it immediately if no thread switch is necessary.
      } else {
        repaintRequ.addToList(itsMng.gralDevice().orderList(), repaintDelay);  //TODO repaintDelayMax
      }
    }
  }
  
  
  /**The Implementation of repaint calls {@link #repaintGthread()} if it is the graphic thread and the delay is 0.
   * Elsewhere the {@link #repaintRequ} is added as request to the graphic thread. 
   * @see org.vishia.gral.ifc.GralWidget_ifc#repaint(int, int)
   */
  @Override public void repaint(int delay, int latest){
    if(itsMng !=null){ //NOTE: set of changes is possible before setToPanel was called. 
      if(delay == 0 && itsMng.currThreadIsGraphic()){
        repaintGthread();
      } else {
        repaintRequ.addToList(itsMng.gralDevice().orderList(), delay);
      }
    }
  }
  
  /**Removes the widget from the lists in its panel and from the graphical representation.
   * It calls the protected {@link #removeWidgetImplementation()} which is implemented in the adaption.
   */
  @Override public boolean remove()
  {
    removeWidgetImplementation();
    _wdgPos.panel.removeWidget(this);
    return true;
  }
  
  
  
  /**Especially for test and debug, short info about widget.
   * @see java.lang.Object#toString()
   */
  @Override public String toString()
  { StringBuilder u = new StringBuilder(240);
    u.append(whatIs).append(" - ").append(name).append(": ").append(sDataPath);
    if(_wdgPos !=null && _wdgPos.panel !=null){
      u.append(" @").append(_wdgPos.panel.namePanel);
    } else {
      u.append(" @?");
    }
    if(variable !=null){
      String vString = variable.toString();
      u.append(" var=").append(vString);
    }
    u.append('\n');
    return u.toString();
  }

  
  /**Methods which should be called back by events of the implementation layer.
   * This class is used only for the implementation level of the graphic. It is not intent to use
   * by any application. It is public because the implementation level should accesses it.
   */
  public abstract static class ImplAccess implements GralWidgImpl_ifc {
    
    /**What is changed in the dynamic data, see {@link GralWidget.DynamicData#whatIsChanged}. */  
    public static final int chgText = 1, chgColorBack=2, chgColorText=4, chgFont = 8, chgColorLine = 0x10;
    
    public static final int chgEditable = 0x20;
    
    public static final int chgVisibleInfo = 0x10000, chgObjects = 0x20000, chgFloat = 0x40000, chgIntg = 0x80000;
    
    public static final int chgPos = 0x20000000, chgVisible = 0x40000000, chgInvisible = 0x80000000;
    
    /**This is only documentation. These bits are used specialized in derived classes.*/
    public static final int chgBitsDerived = 0x0ff0ff00;

    public final GralWidget widgg;
    
    /**Bounds of the implementation widget in its container. null if not used. */
    public GralRectangle pixBounds;
    
    @Deprecated protected ImplAccess(GralWidget widgg, GralMng mng){
      this.widgg = widgg;
      widgg.itsMng = mng;
      widgg._wdgImpl = this; 
      if(widgg._wdgPos !=null) 
        throw new IllegalStateException("GralWidget - setPos() is set already.");
      widgg.initPos( mng.getPosCheckNext());  //always clone it from the central pos 
      // Note: widgg.posWidg.panel.getWidgetImplementation() ==null yet because it will be initialize after super(widgg); 
    }
    
    
    /**Constructs the base of the graphic implemantion widget wrapper (SWT, AWT).
     * Stores the reference to the GralWidget in this.{@link #widgg}
     * Stores the reference to the graphic implementation widget in {@link GralWidget#_wdgImpl}
     * Initializes the pos() from the given {@link GralMng#pos} if it is not given by construction. 
     * @param widgg The associated derived class of GralWidget.
     */
    protected ImplAccess(GralWidget widgg){
      this.widgg = widgg;
      widgg._wdgImpl = this; 
      if(widgg._wdgPos ==null) {
        //set the position now, because it is given yet.
        widgg.initPos( widgg.itsMng.getPosCheckNext());  //always clone it from the central pos 
      } //else: The position was given by construction already.
      // Note: widgg.posWidg.panel.getWidgetImplementation() ==null yet because it will be initialize after super(widgg); 
    }
    
    
    /**This method is not intent to call by user. It may be called from all widget implementation 
     * if the focus of the widget is gained. Use {@link #setFocus()} to set a widget in the focus.
     * 
     * It sets the html help for the widget and notifies the widgets in focus for the GralWidgetMng. 
     * Don't override this method in the graphic implementation!
     * It should be overridden only in a Gral widget inheritance only if necessary.
     */
    public void XXXfocusGained(){
      //System.out.println(Assert.stackInfo("GralWidget - Debuginfo; focusgained", 1, 10));
      if(widgg.htmlHelp !=null){
        widgg.itsMng.setHtmlHelp(widgg.htmlHelp);
      }
      if(widgg.actionFocused !=null){ widgg.actionFocused.exec(KeyCode.focusGained, widgg); }
      //notify GralWidgetMng about focused widget.
      widgg.itsMng.notifyFocus(widgg);
    }
    
    /**Sets the state of the widget whether it seams to be visible.
     * This method should not be invoked by the application. It is 
     * @param visible
     */
    public void setVisibleState(boolean visible){
      widgg.bVisibleState = visible;
      widgg.lastTimeSetVisible = System.currentTimeMillis();
    }

    protected GralUserAction actionShow(){ return widgg.actionShow; }
    
    protected GralUserAction actionChanging(){ return widgg.actionChanging; }
    
    
    protected GralWidget.DynamicData dyda(){ return widgg.dyda; }
    
    //public void setWidgetImpl(GralWidgImpl_ifc widg, GralMng mng){ widgg.wdgImpl = widg; widgg.itsMng = mng; }

    protected void setTextChanged(){ widgg.bTextChanged = true; }
    
    
    /**Implementation routine to set receiving a drag event and initializes the drag feature of the widget.
     * A overridden routine should be implemented for the implementation graphic layer widget.
     * This routine is invoked when it isn't overridden, it throws an exception because the drag feature
     * isn't supported for the implementation.
     * @param dragType one of {@link org.vishia.util.KeyCode#dragFiles} or ..dragText
     */
    protected void setDragEnable(int dragType)
    { //default implementation: causes an exception. The type must override it.
      throw new IllegalArgumentException("drag not supported for this widget type");
    }
    
    /**Implementation routine to set receiving a drop event and initializes the drop feature of the widget.
     * @param dropType one of {@link org.vishia.util.KeyCode#dropFiles} or ..dropText
     */
    protected void setDropEnable(int dropType)
    { //default implementation: causes an exception. The type must override it.
      throw new IllegalArgumentException("drop not supported for this widget type");
    }
    

    
    public int getChanged(){ return widgg.dyda.whatIsChanged.get(); }
    
    public void acknChanged(int mask){ widgg.dyda.acknChanged(mask); }
    
    
    public static GralWidget gralWidgetFromImplData(Object data){
      if(data instanceof GralWidget) return (GralWidget)data;
      else if(data instanceof GralWidget.ImplAccess) {
        return ((GralWidget.ImplAccess)data).widgg;
      } else return null;
    }
    
    
    public static void setFocused(GralWidget widgg, boolean focus){
      widgg.bHasFocus = focus;
    }
  }
  
  /**Not intent to get from user: The instance which's methods can be called from an event method of the implementation of the GralWidget. 
   * Note: This Method is public only because the implementation in another package need to use it.
   * It should not be used by any application. */
  //public MethodsCalledbackFromImplementation implMethodWidget_ = new MethodsCalledbackFromImplementation();
  
  
  /**Returns the instance which extends the {@link ImplAccess} of this widget.
   * @return null if the widget has not an implementation yet.
   */
  public ImplAccess getImpl(){ return _wdgImpl; }
  
  
  /**This callback worker calls the {@link #repaintGthread()} if it is invoked in the graphical thread.
   * It is used with delay and wind up whenever {@link #repaint(int, int)} with an delay is called.
   * If its callback method was run, it is dequeued till the next request of {@link #repaint()}.
   */
  private final GralGraphicTimeOrder repaintRequ = new GralGraphicTimeOrder("GralWidget.repaintRequ"){
    @Override public void executeOrder() {
      //first remove from queue to force add new, if a new request is given.
      //thread safety: If a new request is given, it is not add yet, because it isn't execute.
      removeFromList(itsMng.gralDevice().orderList());
      //now a new request will be added.
      try{ repaintGthread();
      
      } catch(Throwable exc){
        System.out.println(Assert.exceptionInfo("GralWidget - unexpected Exception; ", exc, 0, 7));
        exc.printStackTrace(System.err);
        //NOTE: removeFromQueue should invoked on exception too.
      }
      countExecution();
    }
    @Override public String toString(){ return name + ":" + GralWidget.this.name; }
  };
  
  
  /**Sets the state of the widget whether it seams to be visible.
   * This method should not be invoked by the application. It is 
   * @param visible
   */
  public void setVisibleState(boolean visible){
    bVisibleState = visible;
    lastTimeSetVisible = System.currentTimeMillis();
  }

  void stop(){
    
  }


  @Override @Deprecated
  public GralColor setBackgroundColor(GralColor color)
  { 
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public void setBoundsPixel(int x, int y, int dx, int dy)
  { if(_wdgImpl !=null) _wdgImpl.setBoundsPixel(x, y, dx, dy);
  }


  @Override
  public GralColor setForegroundColor(GralColor color)
  { // TODO Auto-generated method stub
    return null;
  }


  @Override
  public Object getWidgetImplementation()
  { if(_wdgImpl !=null) return _wdgImpl.getWidgetImplementation();
    else return null;
  }


  @Override
  public void removeWidgetImplementation()
  { if(_wdgImpl !=null) _wdgImpl.removeWidgetImplementation();
  }


  @Override
  public void repaintGthread()
  {
    if(_wdgImpl !=null) _wdgImpl.repaintGthread();
  }


  @Override
  public boolean setFocusGThread()
  { boolean ret;
    try{
      if(_wdgImpl !=null) ret = _wdgImpl.setFocusGThread();
      else ret = false;
    } catch(Exception exc){
      System.err.println("GralWidget - setFocusGThread fails");
      ret = false;
    }
    return ret;
  }
  
  
}


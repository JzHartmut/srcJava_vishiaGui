package org.vishia.gral.ifc;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWidgImpl_ifc;
import org.vishia.gral.base.GralWidget;
import org.vishia.util.Removeable;


/**It is a basic interface for any widget of the Graphic Adaption Layer (gral).
 * <br>
 * <b>Strategy of changing the graphical content of a widget</b>:<br>
 * 2013-09-30<br>
 * The SWT graphical implementation prohibits changing the graphical appearance, for example setText(newText),  
 * in another thread. Other graphical implementations are not thread-safe. Often a graphic application
 * runs in only one thread, so that isn't a problem. But applications with complex data gathering processes
 * needs to run in several threads. It may be fine to set the widgets in that threads immediately,
 * seen from the programmers perspective. For multithread usage see {@link org.vishia.gral.base.GralGraphicThread}.
 * <br><br>
 * The solution of setting any content in a Widget in any thread in Gral is:
 * <ul>
 * <li>The thread invokes methods of this <code>GralWidget_ifc</code> or methods of an derived <code>GralWidget</code>
 *   such as {@link GralTable}, for example {@link #setText(CharSequence)}, {@link #setTextColor(GralColor)}
 *   or {@link org.vishia.gral.base.GralTable#setColorCurrLine(GralColor)}.
 * <li>That methods store the given data either in the common {@link GralWidget.DynamicData} {@link GralWidget#dyda} of the widget
 *   or in specific data of a GralWidget implementation.
 * <li>That method calls {@link #repaint(int, int)} with a proper millisecond delay (usual 100).
 *   The graphic implementation widget is not touched in this time. Usual it is not necessary to show information
 *   in a faster time than 100 ms if it is not a high speed animated graphic. The delayed repaint request
 *   saves calculation time if more as one property is changed on the same widget.
 * <li>The delayed repaint request queues the instance {@link GralWidget#repaintRequ} (only private visible)
 *   of {@link org.vishia.gral.base.GralDispatchCallbackWorker} in the central queue of requests using 
 *   {@link org.vishia.gral.base.GralGraphicThread#addDispatchOrder(org.vishia.gral.base.GralDispatchCallbackWorker)}. 
 *   The {@link org.vishia.gral.base.GralGraphicThread} is known by {@link org.vishia.gral.base.GralWidget#itsMng}.
 * <li>If for example 20 widgets are changed in maybe 40 properties, that queue contains the 20 instances of
 *   {@link org.vishia.gral.base.GralDispatchCallbackWorker}. Any of them may have a specific delay. 
 *   The graphic thread organizes it in a proper kind of time.
 * <li>If a {@link org.vishia.gral.base.GralDispatchCallbackWorker} is dequeued in the graphic thread, 
 *   its method {@link org.vishia.gral.base.GralDispatchCallbackWorker#doBeforeDispatching(boolean)} is invoked. 
 *   This method calls {@link GralWidgImpl_ifc#repaintGthread()} via the association {@link org.vishia.gral.base.GralWidget#wdgImpl}.
 * <li>The <code>rerepaintGthread()</code> method is overridden in the implementation layer
 *   with the necessary statements to transfer the non-graphic data of this {@link GralWidget} especially
 *   stored in {@link org.vishia.gral.base.GralWidget#dyda} to the special implementation widget method invocations
 *   such as {@link org.eclipse.swt.widgets.Text#setText(String)} which touches the graphic widget.
 *   Then a {@link org.eclipse.swt.widgets.Control#update()} and {@link org.eclipse.swt.widgets.Control#redraw()}
 *   is invoked to show the content.         
 * </ul>
 * It is a complex approach. But it is simple for usage. The user can change the content in any thread.
 * The user does not need to organize a queue for the graphic thread by itself. The queue is a part of Gral.
 * 
 * <br><br>
 * <b>Strategy to create widgets and positioning</b>: see {@link GralMngBuild_ifc}.  
 *  
 * @author Hartmut Schorrig
 *
 */
public interface GralWidget_ifc extends Removeable
{
  
  /**Version, history and license.
   * <ul>
   * <li>2013-03-13 Hartmut new {@link #getContentIdent()}, {@link #setContentIdent(long)}
   * <li>2013-03-13 Hartmut new {@link #isNotEditableOrShouldInitialize()} to support edit fields.
   * <li>2012-08-21 The method {@link #setBackColor(GralColor, int)}, {@link #setLineColor(GralColor, int)} and {@link #setTextColor(GralColor)}
   *  are declared here. What methods are deprecated? I thing {@link #setBackgroundColor(GralColor)}.  
   * <li>2012-07-29 Hartmut chg: {@link #setFocus()} and {@link #setFocus(int, int)} can be called in any thread yet.
   * <li>2012-07-13 Hartmut new. {@link #getWidgetMultiImplementations()}. This method is not used in any widget yet,
   *   but it may be necessary for complex widgets.
   * <li>2012-04-25 Hartmut new: {@link #refreshFromVariable(VariableContainer_ifc)}: Capability for any widget
   *   to update its content from its associated variables described in its sDataPath.
   * <li>2012-03-31 Hartmut new: {@link #isVisible()}
   * <li>2012-01-16 Hartmut new: Concept {@link #repaint()}, can be invoked in any thread. With delay possible.
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
  public static final int version = 20130521;

  
  
  /**Returns the implementation class of more as one widget. If the widget has only one implementation widget,
   * this method returns null. You should call {@link #getWidgetImplementation()} instead. */
  //public Object[] XXXgetWidgetMultiImplementations();
  
  
  public String getName();
  
  /**This routine should be called only one time after the Gral widget was created. It is overridden by the 
   * implementation widget. It invokes the proper method of the {@link GralMngBuild_ifc} to set and show 
   * the graphical implementation layer widgets.
   * @param mng This instance knows the graphical implementation layer.
   */
  void setToPanel(GralMngBuild_ifc mng);
  
  /**Returns the associated singleton GralMng. The GralMng is associated only if the widget is setToPanel,
   * see {@link #setToPanel(GralMngBuild_ifc)}.
   * @return null if the Widget is created yet without connection to the graphical implementation layer
   */
  GralMng gralMng();
  
  /**Sets the focus to the widget. . It can be called in any thread. If it is called in the graphic thread,
   * the repaint action is executed immediately in the thread. Elsewhere the graphic thread will be woken up.
   */
  public abstract void setFocus();
  
  /**Sets the focus to the widget. . It can be called in any thread. If it is called in the graphic thread,
   * the repaint action is executed immediately in the thread. Elsewhere the graphic thread will be woken up.
   * @param delay
   * @param latest
   */
  public abstract void setFocus(int delay, int latest);
  
  /**Sets this widget visible on graphic or invisible. Any widget can be visible or not. More as one widgets
   * can use the same position, only one of them may set visible. 
   * For a {@link GralWindow}, its the visibility of the whole window. 
   * Note that a window which is invisible is not shown in the task bar of the operation system. 
   * Note that an application can have more as one window. 
   * Note that a dialog window can be set to invisible if it is not need yet instead destroy and build newly.
   * @param visible
   * @return
   */
  boolean setVisible(boolean visible);
  
  /**Returns whether the widget is visible or not. This method can be invoked in any thread.
   * It is an estimation because the state of the widget may be changed in the last time or a window
   * can be covered by another one. The widget is not visible if it is a member of a card in a tabbed
   * panel and that tab is not the selected one.
   * @return true if the widget seams to be visible.
   */
  boolean isVisible();
  
  /**Sets whether it is able to edit the content of the text field or text box.
   * If a content is not able to edit, it is a showing field or box. The user can't change the
   * content. But the user can set the cursor, select any text and copy to the systems clipboard.
   * If the content is able to edit, the change should be notified and the content should be gotten.
   * To do that TODO
   * @param editable true then the content is going to be able to change. 
   *   False then the edit functionality is disabled. 
   */
  void setEditable(boolean editable);
  
  /**Query whether this widget is able to change from user handling. */
  boolean isEditable();
  
  
  /**Query whether this field should be written from any initial or actual data. */
  public boolean isNotEditableOrShouldInitialize();

  
  
  /**
   * @deprecated use {@link #setBackColor(GralColor, int)}
   */
  @Deprecated
  public abstract GralColor setBackgroundColor(GralColor color);
  
  /**
   * @deprecated use {@link #setLineColor(GralColor, int)}
   */
  @Deprecated
  public abstract GralColor setForegroundColor(GralColor color);
  
  
  /**Sets the background color for the widget.
   * @param color Any color
   * @param ix 0 if only one color is supported, 1, ... if the widget has more as one background.
   */
  void setBackColor(GralColor color, int ix);
  
  /**Gets the background color for the widget.
   * @param ix 0 if only one color is supported, 1, ... if the widget has more as one background.
   * @return color Any color
   */
  GralColor getBackColor(int ix);
  
  /**Sets the line color for the widget.
   * @param color Any color
   * @param ix 0 if only one color is supported, 1, ... if the widget has more as one line.
   */
  void setLineColor(GralColor color, int ix);
  
  /**Sets the text color for the widget.
   * @param color Any color
   */
  void setTextColor(GralColor color);
  

  /**Set the text of the widget. If the widget is a button, the standard button text is changed.
   * If it is a window, its title is changed.
   * <br><br>
   * <b>Concept of changing a widget from application</b>:<br>
   * Here the generally approach is described, appropriate for this method, but in the same kind
   * for all methods to set something like {@link #setBackColor(GralColor, int)} etc.
   * <br><br>
   * With the set methods the user stores the text, color etc. in graphic-independent attributes. Then the method
   * {@link #repaint(int, int)} is invoked with the standard delay of {@link #repaintDelay} and {@link #repaintDelayMax}.
   * With that the widget-specific private instance of {@link #repaintRequ} is added to the queue of requests
   * in the {@link GralGraphicThread#addDispatchOrder(GralDispatchCallbackWorker)}. In the requested time that 
   * dispatch order is executed in the graphic thread. It calls {@link GralWidgImpl_ifc#repaintGthread()}. 
   * That method is implemented in the graphic implementation layer of the widget. It sets the appropriate values 
   * from the independent Gral attributes to the implementation specifics and invoke a redraw of the graphic layer.
   * <br><br>
   * If more as one attribute is changed one after another, only one instance of the {@link GralDispatchCallbackWorker}
   * is queued. All changed attributes are stored in {@link DynamicData#whatIsChanged} and the
   * {@link GralWidgImpl_ifc#repaintGthread()} quests all changes one after another. 
   * It means that a thread switch is invoked only one time per widget for more as one change.
   * <br>
   * See {@link DynamicData}. That composite part of a widget stores all standard dynamic data of a widget. 
   */
  void setText(CharSequence text);

  
  /**Sets the html help url for this widget. That information will be used if the widget is focused
   * to control the help window output.
   * @param url String given url, maybe a local file too with #internalLabel.
   */
  void setHtmlHelp(String url);
  
  //public abstract GralWidget getGralWidget();

  /**repaint request. It can be called in any thread. If it is called in the graphic thread,
   * the repaint action is executed immediately in the thread. Elsewhere the graphic thread will be waken up
   * in the repaint time. If this routine is invoked more as one time in a short time not in the graphic thread,
   * the repaint is executed only one time after the given delay.
   */
  public void repaint();
  
  /**Possible delayed repaint, can be called in any thread.
   * If this method is re-called in the time where the delay is not elapsed
   * then the delay will be set newly, the timer is winding up again. 
   * But the latest time on first call will be used.
   * @param delay in milliseconds. If 0 or less 0, then it is executed immediately if the calling
   *   thread is the graphic thread.
   * @param latest The latest draw time in milliseconds. If it is less 0, then it is unused.
   *   If it is 0 or less delay if a delay is given, then the delay isn't wound up on re-call.
   *   
   */
  public void repaint(int delay, int latest);
  
  void setBoundsPixel(int x, int y, int dx, int dy);
 
  
  /**Sets the data path. It is a String in application context. If variables are used, it is the path
   * of one or more variables, see {@link #refreshFromVariable(VariableContainer_ifc)}.
   * @param sDataPath
   */
  void setDataPath(String sDataPath);
  
  
  /**Returns true if the content is changed from a user action on GUI.
   * @param setUnchanged If true then set to unchanged with this call.
   * @return true if the content was changed since last setUnchanged.
   */
  boolean isChanged(boolean setUnchanged);
  
  
  /**Sets a identification for the shown data. A widget may not need to refresh if the contentIdent
   * is not changed. A refresh of a widget's content may have side effects
   * in user handling such as selection of text etc. See {@link #getContentIdent()}.
   * @param ident any number which identifies the value of content.
   * @return the last stored ident.
   */
  long setContentIdent(long ident);
  
  /**Gets the content identification of the users data which are set with {@link #setContentIdent(long)}.
   */
  long getContentIdent();
  
  
  Object getContentInfo();

  /**Capability for any widget to update its content from its associated variables described in its sDataPath.
   * @param container The container is used only if the variable is not known by direct reference
   *   in the private {@link GralWidget#variable} or {@link GralWidget#variables}. If the variable(s) is/are
   *   not known, they are searched by there data path set by {@link #setDataPath(String)}. More as one 
   *   variables are possible separated by "," in the setDataPath("variable1, variable2").
   *   The variables are searched in the container calling {@link VariableContainer_ifc#getVariable(String)}. 
   */
  void refreshFromVariable(VariableContainer_ifc container);
  
  
  
  void refreshFromVariable(VariableContainer_ifc container, long timeAtleast, GralColor colorRefreshed, GralColor colorOld);
}

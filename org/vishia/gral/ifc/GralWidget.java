package org.vishia.gral.ifc;

import org.vishia.byteData.VariableAccess_ifc;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralTable;



/**This class holds some information about a widget for showing and animating in a GUI and refers the graphical widget. 
 * <br>
 * The ObjectModelDiagram may shown the relations:
 * <img src="../../../../img/Widget_gral.png"><br>
 * This class GralWidget knows the gral graphic widget via an {@link GralWidget_ifc}. It is a wrapper around the widget of the adaption layer.
 * In this figure a wrapper {@link org.vishia.gral.swt.SwtTable} is shown which wraps 
 * a org.eclipse.swt.widgets.Table. The wrapper based on {@link org.vishia.gral.base.GralTable} 
 * and supports the interface {@link org.vishia.gral.ifc.GralTable_ifc}.
 * This interface allows to deal with the table procured by the wrapper. See the derived interfaces of {@link GralWidget_ifc}.  
 * The {@link GralWidget_ifc} allows some fundamental operations with any widget like {@link GralWidget_ifc#setFocus()}. 
 * <br><br>
 * The Widget knows its {@link GralGridPos} at its panel where it is placed. The panel knows all widgets
 * which are placed there (widgetList).
 * <br><br>
 * The user can invoke the methods of the widget to animate it in a GUI etc, for example {@link #setBackColor(GralColor, int)}
 * or {@link #setValue(int, int, Object)}. This methods can be called in any thread. There are thread safe. 
 * The organization of this actions are done in the implementation of the {@link org.vishia.gral.base.GralWidgetMng}
 * like {@link org.vishia.gral.swt.SwtWidgetMng}. This implementation adapts the basic graphic and knows theire methods
 * to set colors, values etc. The user need deal only with this widget class. The thread safe  capability is organized
 * with a ConcurrentLinkedQueue which contains requests of type {@link org.vishia.gral.GralWidgetChangeRequ}.
 * <br><br>
 * The widget may know any user data with its association oContentInfo, see {@link #getContentInfo()} and {@link #setContentInfo(Object)}.
 * Mostly there are classes to support getting of values, formatting etc. The type of this classes depends on the user's application.
 * <br><br>
 * This class may contain associations to methods of {@link GralUserAction} to animate (change viewable content) and get values from this widget.
 * An independent part of the user application can invoke the methods {@link GralUserAction#userActionGui(String, GralWidget, Object...)}
 * which maybe implemented in another part of the user's application.
 * <br><br>
 * Last and least the properties of widget are able to change. The widget may know its {@link GralWidgetCfg_ifc} which provides
 * data for design the GUI.
 * 
 * @author Hartmut Schorrig
 *
 */
public abstract class GralWidget implements GralWidget_ifc
{
  
  /**Changes:
   * <ul>
   * <li>2011-11-18 Hartmut bugfix: {@link #setFocus()} had called {@link GralWidgetMng#setFocus(GralWidget)} and vice versa.
   *   Instead it should be a abstract method here and implemented in all Widgets. See {@link org.vishia.gral.swt.SwtWidgetHelper#setFocusOfTabSwt(org.eclipse.swt.widgets.Control)}.
   * <li>2011-10-15 Hartmut chg: This class is now abstract. It is the super class for all wrapper implementations.
   *   The wrapper implements special interfaces for the kind of widgets. It is more simple for usage, less instances to know.
   *   A GralWidget is able to test with instanceof whether it is a special widget. The element widget is removed because the reference
   *   to the implementation widget will be present in the derived classes.
   * <li>2011-10-01 Hartmut new: method {@link #setFocus()}. It wrappes the {@link GralPanelMngWorking_ifc#setFocus(GralWidget)}.
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
   *    or it references the graphic base widget instance. The class {@link WidgetSimpleWrapperSwt} 
   *    is able to wrap simple graphical base widget instances.
   * <li>2011-08-13 Hartmut new: WidgetDescriptor now contains the position of the widget. 
   *     It is used for resizing of large widgets.
   *     A large widget is a widget, which lengthens over the panel and it is changed in size with panel size change. 
   *     A typical example is a text-area-widget.
   * <li>2011-06-20 Hartmut new: method {@link #getMng()} It is the panel manager!
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
   * </ul>
   */
  public final static int version = 0x20111001;
  
  /**The panel manager from where the widget is organized. Most of methods need the information
   * stored in the panel manager. This reference is used to set values to other widgets. */
  protected GralWidgetMng itsMng;
  
  /**Association to the configuration element from where this widget was built. 
   * If the widget is moved or its properties are changed in the 'design mode' of the GUI,
   * this aggregate data are adjusted and re-written to a file. The configuration elemenet
   * contains all data which are necessary to build the appearance of the GUI.
   * <br>
   * If this aggregation is null, the widget can't be changed in the design mode of the GUI.
   * It is created directly without configuration data. 
   */
  private GralWidgetCfg_ifc itsCfgElement;
  
	/**Name of the widget in the panel. */
	public String name;
	
	/**The position of the widget. It may be null if the widget should not be resized. */
	public final GralGridPos pos;  
	
	
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
	 * <li>I: a line
   * <li>i: an image
   * <li>k: a tree node
   * <li>K: a tree leafe
   * <li>l: a list or table
   * <li>L: a list or table line
   * <li>M: a Menu entry
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
	 * <li>
	 * </ul>
	 * */
	public char whatIs;
	
	public String sToolTip;
	
	/**Textual description of the showing method. */
	private String sShowMethod;

	private String[] sShowParam;
	
	/**Textual informations about content. It may be a data path or adequate. */
	public String sDataPath;
	
	/**An index associated to the data. */
	private int dataIx;
	

	/**Textual info about representation format. */
	public String sFormat;
	
	/**Numeric informations about the content. */
	int[] indices;
	
  /**Action method on activating, changing or release the widget-focus. */
  private GralUserAction actionChanging;

  
  /**Action method for showing. */
  private GralUserAction actionShow;

  private GralUserAction actionDrag, actionDrop;
	
	/**command string given by the action as parameter. */
	public String sCmd;
	
	/**Any special info, may be set from any user class. It should help to present the content. 
	 * This info can be set and changed after registration. */
	private Object oContentInfo;
	
	
	//protected GralWidget(char whatIs)
  //{ this.whatIs = whatIs;
  //}

	
	
	public GralWidget(String sName, char whatIs, GralWidgetMng mng)
	{ this.name = sName;
		//this.widget = null;
		this.whatIs = whatIs;
    this.itsCfgElement = null;
    this.itsMng = mng;
    this.pos = mng.getPositionInPanel();  //Note: makes a clone because the pos in panel is reused. 
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
  public GralWidget_ifc getGraphicWidgetWrapper(){ return (GralWidget_ifc)this; }
  
	
	/**Sets a application specific info. 
	 * It should help to present user data which are associated to this widget. 
   * This info can be set and changed anytime. */
  public void setContentInfo(Object content){	oContentInfo = content;}
  
  /**Gets the application specific info. See {@link #setContentInfo(Object)}. */
  public Object getContentInfo(){ return oContentInfo; }
	
  /**Sets the data path. It is a String in application context.
   * @param sDataPath
   */
  public void setDataPath(String sDataPath){	this.sDataPath = sDataPath;}
  
  /**Changes the data path
   * @param sDataPath the new one
   * @return the last one.
   */
  public String getDataPath(String sDataPath)
  {	String sDataPathLast = this.sDataPath;
    this.sDataPath = sDataPath;
    return sDataPathLast;
  }
  
  /**Gets the data path. It is a String in application context.
   */
  public String getDataPath(){ return sDataPath; }
	
  /**Sets the action in application context for processing of user handling for the widget.
   * Handling means, pressing button, user inputs of text fields
   * @param action any instance. Its action method is invoked depending of the type of widget
   *        usual if the user takes an action on screen, press button etc.
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
   * using {@link org.vishia.gral.ifc.GralGridBuild_ifc#registerUserAction(String, GralUserAction)}. They are gotten by name
   * invoking {@link org.vishia.gral.ifc.GralGridBuild_ifc#getRegisteredUserAction(String)} 
   * in the {@link org.vishia.gral.cfg.GralCfgBuilder}.
   * 
   * @param action The action instance.
   */
  public void setActionShow(GralUserAction action){ actionShow = action; }
  
  /**Gets the action to show the widget. This method is helpfully to invoke showing after receiving data
   * in the users context. Invoke {@link GralUserAction#userActionGui(String, GralWidget, Object...)}
   * with this WidgetDescriptor and additional user data. The implementation of that method
   * may be done in the users context but in another module or the implementation may be given in any 
   * library superordinated to this graphic adapter library but subordinated in respect to the explicit application.
   * The usage of a show method given in the implementation of {@link GralUserAction} helps to separate
   * the invocation of showing and the decision what and how is to show.
   */
  public GralUserAction getActionShow(){ return actionShow; }
  
  
	public String getsToolTip()
	{
		return sToolTip;
	}


	public void setToolTip(String sToolTip)
	{
		this.sToolTip = sToolTip;
	}

	
  /**Sets the action to receive a drop event and initializes the drop feature of the widget.
   * @param action The action will be called
   * @param dropType one of {@link org.vishia.util.KeyCode#dropFiles} or ..dropText
   */
  public void setDragEnable(GralUserAction action, int dragType)
  {
    actionDrag = action;
    setDragEnable(dragType);  // call implementation specific drop handling. 
  }

  /**Implementation routine to set receiving a drop event and initializes the drop feature of the widget.
   * @param dropType one of {@link org.vishia.util.KeyCode#dropFiles} or ..dropText
   */
  protected void setDragEnable(int dragType)
  { //default implementation: causes an exception. The type must override it.
    throw new IllegalArgumentException("drag not supported for this widget type");
  }
  
  public GralUserAction getActionDrag(){ return actionDrag; }
  
  
  /**Sets the action to receive a drop event and initializes the drop feature of the widget.
   * @param action The action will be called
   * @param dropType one of {@link org.vishia.util.KeyCode#dropFiles} or ..dropText
   */
  public void setDropEnable(GralUserAction action, int dropType)
  {
    actionDrop = action;
    setDropEnable(dropType);  // call implementation specific drop handling. 
  }

  /**Implementation routine to set receiving a drop event and initializes the drop feature of the widget.
   * @param dropType one of {@link org.vishia.util.KeyCode#dropFiles} or ..dropText
   */
  protected void setDropEnable(int dropType)
  { //default implementation: causes an exception. The type must override it.
    throw new IllegalArgumentException("drop not supported for this widget type");
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
	 * while calling {@link #setShowMethod(String)}. They are split in extra Strings,
	 * this  
	 * @return
	 */
	public String[] getShowParam(){ return sShowParam; }
	
	/**Clear the parameter if they are over-taken already.
	*/
	public void clearShowParam(){ sShowParam = null; }

	/**
	 * @param sShowMethod
	 * @deprecated use {@link #setActionShow(GralUserAction)}.
	 */
	public void setShowMethod(String sShowMethod)
	{ if(sShowMethod == null){
			this.sShowMethod = null;
		  this.sShowParam = null;
		} else {
			int posParanthesis = sShowMethod.indexOf("("); 
			if(posParanthesis >0){
				int posParanthesisEnd = sShowMethod.indexOf(")");
				String sParam1 = sShowMethod.substring(posParanthesis+1, posParanthesisEnd);
				String[] sParamA = sParam1.split(",");
				this.sShowParam = new String[sParamA.length];
				for(int ix=0; ix < sParamA.length; ++ix){
					this.sShowParam[ix] = sParamA[ix].trim();
				}
				this.sShowMethod = sShowMethod.substring(0, posParanthesis).trim();
				
			} else {
			  this.sShowMethod = sShowMethod;
			  this.sShowParam = null;
			}
		}
	}


	public String getFormat()
	{
		return sFormat;
	}


	public void setFormat(String sFormat)
	{
		this.sFormat = sFormat;
	}

	public void setPanelMng(GralWidgetMng panel)
	{ this.itsMng = panel; 
	}
	
	
	
	/**Gets the info to access the values for this widget in the users context.
	 * If this method is called the first time for the widget after start the application, the access info
	 * is searched in the container calling {@link VariableContainer_ifc#getVariable(String, int[])}
	 * with the stored textual info {@link #setDataPath(String)} and {@link #setDataIx(int)}.
	 * This operation may need a little bit of calcualtion time, which were to expensive if a lot of widgets
	 * should be provided with user values. Therefore the returned {@link VariableAccess_ifc} instance is stored
	 * in the {@link #oContentInfo} of the widget and returned on the further calls.
	 * <br>
	 * The returned {@link VariableAccess_ifc} should be allow the fast access to users values.
	 *  
	 * @param container The container where all {@link VariableAccess_ifc} should be found.
	 * @return The access to a user variable in the user's context.
	 */
	public VariableAccess_ifc getVariableFromContentInfo(VariableContainer_ifc container)
	{
		//DBbyteMap.Variable variable;
		VariableAccess_ifc variable;
		Object oContentInfo = this.getContentInfo();
		final int[] ixArrayA = new int[1];
		if(oContentInfo == null){
			//first usage:
			variable = container.getVariable(this.getDataPath(), ixArrayA);
			this.setContentInfo(variable);
			this.setDataIx(ixArrayA[0]);
		} else if(oContentInfo instanceof VariableAccess_ifc){
			variable = (VariableAccess_ifc)oContentInfo;
		} else {
			variable = null;  //other info in widget, not a variable.
		}
	  return variable; 
	}
	
  /**Gets the current value of the content of the widget in the given context.
   * @param mng The context.
   * @return The value in String representation, null if the widget has no possibility of input.
   */
  public String getValue()
  { return itsMng.getValueFromWidget(this);
  }
  
  /**Sets the current value of the content of the widget in the given context.
   * @param cmd see {@link GralPanelMngWorking_ifc#cmdSet} etc. It is possible to set the color etc.
   * @param ident Any number to specify set, maybe 0
   * @param value The value in the necessary representation.
   */
  public void setValue(int cmd, int ident, Object visibleInfo)
  { itsMng.setInfo(this, cmd, ident, visibleInfo, null);
  }
  
  
  public void setBackColor(GralColor color, int ix){ itsMng.setBackColor(this, ix, color.getColorValue()); }
  
  public void setLineColor(GralColor color, int ix){ itsMng.setLineColor(this, ix, color.getColorValue()); }
  
  
  
  /**Sets the current value of the content of the widget in the given context.
   * @param cmd see {@link GralPanelMngWorking_ifc#cmdSet} etc. It is possible to set the color etc.
   * @param ident Any number to specify set, maybe 0
   * @param value The value in the necessary representation.
   */
  public void setValue(int cmd, int ident, Object visibleInfo, Object userData)
  { itsMng.setInfo(this, cmd, ident, visibleInfo, userData);
  }
  
  public void setCfgElement(GralWidgetCfg_ifc cfge)
  { this.itsCfgElement = cfge;
  }
  
  
  public GralWidgetCfg_ifc getCfgElement()
  { return itsCfgElement;
  }
  
  
  /**Sets the focus to the widget.
   * See {@link GralPanelMngWorking_ifc#setFocus(GralWidget)}.
   * @return true if the focus is set really.
   */
  public abstract boolean setFocus();
  /*
  {
    return itsMng.setFocus(this);
  }
  */
  
  
  /**Gets the working interface of the manager. 
   * It can be used to set and get values from other widgets symbolic identified by its name.
   * Note: It is possible too to store the {@link GralWidget} of specific widgets
   * to get and set values and properties of this widgets non-symbolic.
   * @return The manager.
   */
  public GralWidgetMng getMng(){ return itsMng; }
  
  
  /**Gets the panel where the widget is member of. 
   * @return The panel.
   */
  public GralPanelContent getItsPanel(){ return pos.panel; }
  
  
  
  
  /**Removes the graphical widget in the graphic. */
  protected abstract void removeWidgetImplementation();
  
  /**Removes the widget from the lists in its panel and from the graphical representation.
   * It calls the protected {@link #removeWidgetImplementation()} which is implemented in the adaption.
   */
  public void remove()
  {
    removeWidgetImplementation();
    pos.panel.removeWidget(this);
  }
  
  
  
	/**Especially for test and debug, short info about widget.
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString()
	{ if(pos !=null && pos.panel !=null){
		  return whatIs + "-" + name + ":" + sDataPath + "@" + pos.panel.namePanel + "\n";
	  } else {
	    return whatIs + "-" + name + ":" + sDataPath + "@?" + "\n";
	  }
	}

	
}


package org.vishia.mainGui;

import org.vishia.byteData.VariableAccess_ifc;
import org.vishia.byteData.VariableContainer_ifc;



/**This class holds some infos about a widget. 
 * Instances of this class are places on the widget, and in a Index of all widgets for the panel and globally.
 * @author Hartmut Schorrig
 *
 * @param <WidgetTYPE>
 */
public class WidgetDescriptor
{
  GuiPanelMngWorkingIfc itsPanel;
  
	/**Name of the widget in the panel. */
	public String name;
	
	/**The graphical widget. It is untyped because it depends on the underlying graphic system. 
	 * It may be a wrapper class arround a graphical widget too. 
	 * This element is used for setting operations, which depends from the graphic system
	 * and the type of the widget. It is only used in the graphic-system-special implementation.
	 * */
	public Object widget;
	
	/**numeric info what to do (kind of widget). 
	 * <ul>
	 * <li>B: a Button: has a color, has an action method.
	 * <li>c: curve view
	 * <li>D: a LED
	 * <li>E: an edit field, 1 line
	 * <li>e: an edit area
	 * <li>i: an image
	 * <li>l: a list or table
	 * <li>m: a Tree
	 * <li>S: a text field
	 * <li>s: a text area.
	 * <li>T: a text input field
	 * <li>t: a text input area.
	 * <li>U: a graphical value representation (bar etc)
	 * <li>V: a graphical value enter representation (slider etc)
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
	public UserActionGui action;

	/**command string given by the action as parameter. */
	public String sCmd;
	
	/**Any special info, may be set from any user class. It should help to present the content. 
	 * This info can be set and changed after registration. */
	private Object oContentInfo;
	
	public WidgetDescriptor(String sName, Object widget, char whatIs)
	{ this.name = sName;
		this.widget = widget;
		this.whatIs = whatIs;
	}

	public WidgetDescriptor(String sName, char whatIs)
	{ this.name = sName;
		this.widget = null;
		this.whatIs = whatIs;
	}

  
	public WidgetDescriptor(String sName, Object widget, char whatIs, String sContentInfo, Object oContentInfo)
	{ this.name = sName;
		this.widget = widget;
		this.whatIs = whatIs;
		this.sDataPath = sContentInfo;
		this.oContentInfo = oContentInfo;
	}
  
	/**Sets a application specific info. It should help to present the content. 
   * This info can be set and changed anytime after registration. */
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
	
  /**Sets the action for application context.
   * @param action any instance. Its action method is invoked depending of the type of widget
   *        usual if the user takes an action on sceen, press button etc.
   */
  public void setAction(UserActionGui action){ this.action = action; }
  
	public String getsToolTip()
	{
		return sToolTip;
	}


	public void setToolTip(String sToolTip)
	{
		this.sToolTip = sToolTip;
	}


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


	public void setContentInfo(String sContentInfo)
	{
		this.sDataPath = sContentInfo;
	}


	public String getFormat()
	{
		return sFormat;
	}


	public void setFormat(String sFormat)
	{
		this.sFormat = sFormat;
	}

	public void setPanelMng(GuiPanelMngWorkingIfc panel)
	{ this.itsPanel = panel; 
	}
	
	
	//DBbyteMap.Variable 
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
  { return itsPanel.getValueFromWidget(this);
  }
  
  /**Sets the current value of the content of the widget in the given context.
   * @param cmd see {@link GuiPanelMngWorkingIfc#cmdSet} etc. It is possible to set the color etc.
   * @param ident Any number to specifiy set, maybe 0
   * @param value The value in the necessary representation.
   */
  public void setValue(int cmd, int ident, Object value)
  { itsPanel.setInfo(this, cmd, ident, value);
  }
  
	/**Especially for test and debug, short info about widget.
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString()
	{
		return name + ":" + sDataPath;
	}
	
}


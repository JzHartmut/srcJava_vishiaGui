package org.vishia.guiViewCfg;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vishia.byteData.RawDataAccess;
import org.vishia.mainCmd.Report;
import org.vishia.mainGui.GuiPanelMngWorkingIfc;
import org.vishia.mainGui.GuiSetValueIfc;
import org.vishia.mainGui.PanelActivatedGui;
import org.vishia.mainGui.UserActionGui;
import org.vishia.mainGui.ValueBar;
import org.vishia.mainGui.WidgetDescriptor;

import org.vishia.byteData.ByteDataSymbolicAccess;

public class OamShowValues
{

	final Report log;

	/**Index (fast access) of all variable which are sent from the automation device (contained in the cfg-file). */
	protected final ByteDataSymbolicAccess accessOamVariable;

	boolean dataValid = false;
	
	List<WidgetDescriptor<?>> widgetsInTab;
	
	/**The access to the gui, to change data to show. */
	protected final GuiPanelMngWorkingIfc guiAccess;
	
	Set<Map.Entry<String, WidgetDescriptor<?>>> fieldsToShow;
	
	
	private final float[] valueUserCurves = new float[6];  

	public OamShowValues(
		Report log
	, GuiPanelMngWorkingIfc guiAccess
	)
	{
		this.log = log;
		this.guiAccess = guiAccess;
		accessOamVariable = new ByteDataSymbolicAccess(log);
		//assign an empty array, it is necessary for local test or without data receive.
		//elsewhere a null-pointer-exception is thrown if the tab-pane is shown.
		//If data are received, this empty array isn't referenced any more.
		accessOamVariable.assignData(new byte[1500]);
		dataValid = true;   //it can be set because empty data are present, see above, use to test.
	}
	
	public boolean readVariableCfg()
	{ int nrofVariable = accessOamVariable.readVariableCfg("GUI/oamVar.cfg");
	  if( nrofVariable>0){
	  	log.writeInfoln("success read " + nrofVariable + " variables from file \"GUI/oamVar.cfg\".");
	  } else {
	  	log.writeError(" variables not access-able from file \"exe/SES_oamVar.cfg\".");
	  }
	  return nrofVariable >0;
	}
	
	public void setFieldsToShow(Set<Map.Entry<String, WidgetDescriptor<?>>> fields)
	{
		fieldsToShow = fields;
	}
	
	/**This routine presents the new received values at the GUI
	 * or saves values in traces.
	 * <br><br>
	 * It is possible that this routine is called more as one time after another. 
	 * That is if more as one data set are transfered in 1 datagram. 
	 * Because that a redraw isn't send here, see {@link #showRedraw()}
	 * 
	 * @param binData
	 * @param nrofBytes
	 * @param from
	 */
	public void show(byte[] binData, int nrofBytes, int from)
	{
		accessOamVariable.assignData(binData, nrofBytes, from);
		dataValid = true;
		writeValuesOfTab();   //write the values in the current tab, most of them will be received here newly.
		//TEST TODO:
		//accessOamVariable.setFloat("ctrl/energyLoadCapac2Diff", checkWithoutNewdata);
		//current panel:
		List<WidgetDescriptor<?>> listWidgets = guiAccess.getListCurrWidgets();
		for(WidgetDescriptor<?> widgetInfo : listWidgets){
			String sName = widgetInfo.name;
		}
		//read all variables which are necessary to show.
		writeCurveValues();   //write values for curve scoping

	}


	public void showRedraw()
	{
		redrawCurveValues();
	}
	
	private void writeField(WidgetDescriptor<?> widgetInfo)
	{ String sName = widgetInfo.name;
		String sInfo = widgetInfo.sDataPath;
		String sValue;
		/*int posFormat = sInfo.indexOf('%');
		final String sPathValue = posFormat <0 ? sInfo : sInfo.substring(0, posFormat);
		*/
		String sFormat = widgetInfo.sFormat;
		ByteDataSymbolicAccess.Variable variable = getVariableFromContentInfo(widgetInfo);
		//DBbyteMap.Variable variable = accessOamVariable.getVariable(sPathVariable);
		if(variable == null){
			sValue = "XXXXX";
		} else {
  		char varType = variable.getTypeChar();
  		if(varType == 'F'){
    		float value= variable.bytes.getFloat(variable, widgetInfo.getDataIx());
    		if(sFormat ==null){
      		if(value < 1.0F && value > -1.0F){ sFormat = "%1.5f"; }
      		else if(value < 100.0F && value > -100.0F){ sFormat = "% 2.3f"; }
      		else if(value < 10000.0F && value > -10000.0F){ sFormat = "% 4.1f"; }
      		else if(value < 100000.0F && value > -100000.0F){ value = value / 1000.0F; sFormat = "% 2.3f k"; }
      		else { sFormat = "%3.3g"; }
    		} 
    		sValue = String.format(sFormat, value);
    	} else if("JISB".indexOf(varType)>=0){
  			//integer
    		int value = variable.bytes.getInt(variable, widgetInfo.getDataIx());
    		if(sFormat ==null){
	      	sFormat = "%d";
    		} 
    		sValue = String.format(sFormat, value);
    	} else {
    		//other format
    		sValue = "?type=" + varType;
    	}
		guiAccess.insertInfo(sName, 0, sValue);
		}
		
	}
	
	
	
	

	private void writeValuesOfTab()
	{ if(dataValid){
			if(widgetsInTab != null)
			for(WidgetDescriptor<?> widgetInfo: widgetsInTab){
				String sContentInfo = widgetInfo.sDataPath;
				if(sContentInfo !=null && sContentInfo.length() >0 && widgetInfo.widget !=null){
					stop();
					if(!callMethod(widgetInfo)){
						//show value direct
						writeField(widgetInfo);
					}
				  //log.reportln(3, "TAB: " + sContentInfo);
				}
			}
	  }
	}
	
	
	ByteDataSymbolicAccess.Variable getVariableFromContentInfo(WidgetDescriptor<?> widgetInfo)
	{
		ByteDataSymbolicAccess.Variable variable;
		Object oContentInfo = widgetInfo.getContentInfo();
		final int[] ixArrayA = new int[1];
		if(oContentInfo == null){
			//first usage:
			variable = getVariable(widgetInfo.getDataPath(), ixArrayA);
			widgetInfo.setContentInfo(variable);
			widgetInfo.setDataIx(ixArrayA[0]);
		} else if(oContentInfo instanceof ByteDataSymbolicAccess.Variable){
			variable = (ByteDataSymbolicAccess.Variable)oContentInfo;
		} else {
			variable = null;  //other info in widget, not a variable.
		}
	  return variable; 
	}
	
	
	
	ByteDataSymbolicAccess.Variable getVariable(String sDataPath, int[] ixArrayA)
	{
		final String sPathVariable = ByteDataSymbolicAccess.separateIndex(sDataPath, ixArrayA);
		ByteDataSymbolicAccess.Variable variable = accessOamVariable.getVariable(sDataPath);
		return variable;
	}
	
	
	boolean callMethod(WidgetDescriptor<?> widgetInfo)
	{ String sName = widgetInfo.name;
		String sInfo = widgetInfo.sDataPath;
		final String sMethodName;
		final String sVariablePath;
		final String[] sParam;
		final int posParanthesis = sInfo.indexOf('(');
		if(posParanthesis >=0){
			sMethodName = sInfo.substring(0, posParanthesis);
			sParam = sInfo.substring(posParanthesis+1).split("[,)]");
		  sVariablePath = sParam[0].trim();
		} else {
			sMethodName = widgetInfo.getShowMethod();
			sParam = sInfo.split(",");
		  sVariablePath = sParam[0];
		}
		if(sMethodName != null){
			if(sMethodName.equals("setValue")){
		  	setValue(widgetInfo);
		  }
			if(sMethodName.equals("setBar")){
		  	setBar(widgetInfo);
		  }
		  else if(sMethodName.equals("uCapMaxRed")){
		  	float value= accessOamVariable.getFloat(sVariablePath);
    		if(value > 120.0F){
    			guiAccess.setBackColor(sName, 0, 0xffe0e0);
    		} else {
    			guiAccess.setBackColor(sName, 0, 0xffffff);
      	}
    		String sValue = "" + value;
    		guiAccess.insertInfo(sName, 0, sValue);
    	} 
		  else if(sMethodName.equals("showBinManValue")){
		  	int value= accessOamVariable.getInt(sVariablePath);
    		int color;
    		if((value & 0x10)==0){
    			color = 0xffffff;  //white: not set
    		}
    		else { //it is set
	    		int mode = value & 0x60;  //bit 6=manEnable, 5=manMode
	    		switch(mode){
	    		case 0: color=0xff0000; break;     //dark red: error, manual preset but not enabled
	    		case 0x20: color=0xff0000; break;  //dark red: error, manual preset but not enabled
	    		case 0x40: color=0xff0000; break;  //orange: set, enabled or not
	    		case 0x60: color=0xff8000; break;  //orange: set and enabled.
	    		default: color=0xff00ff;    //it isn't used.
	    		}
    		}
		  	guiAccess.setBackColor(widgetInfo, 0, color);
			}
			else if(sMethodName.equals("showBinEnable")){
		  	int value= accessOamVariable.getInt(sVariablePath);
    		int color;
    		int mode = value & 0x60;  //bit 6=manEnable, 5=manMode
    		switch(mode){
    		case 0: color=0xffffff; break;     //white: no manual
    		case 0x20: color=0xff0000; break;  //dark red: error, manual not enabled, but on
    		case 0x40: color=0x00ff00; break; //green: manual enabled
    		case 0x60: color=0xff8000; break;  //orange: manual enabled and switched.
    		default: color=0xff00ff;    //it isn't used.
    		}
		  	guiAccess.setBackColor(widgetInfo, 0, color);
			}
			else if(sMethodName.equals("xxxshowBin")){
		  	int value= accessOamVariable.getInt(sVariablePath);
    		int color;
		  	if((value & 0x06) ==0x04) color = 0x00ff00;  //green: manual enable
		  	else if((value & 0x06) ==0x06) color = 0x0000ff;  //blue: manual enable and manual control
		  	else color = 0xffffff;  
    		guiAccess.setBackColor(sName, 0, color);
			}
			else if(sMethodName.equals("showBool")){
				ByteDataSymbolicAccess.Variable variable = accessOamVariable.getVariable(sVariablePath);
    		int color;
    		if(variable !=null){
					int value= accessOamVariable.getInt(variable, -1);
	    		
			  	if((value & 0xff) ==0x0) color = guiAccess.getColorValue(sParam[1].trim());  
			  	else color = guiAccess.getColorValue(sParam[2].trim());  
			  	//guiAccess.setBackColor(sName, 0, color);
    		} else {
    			color = 0xb0b0b0;  //gray
    		}
		  	if(widgetInfo.whatIs == 'D'){
		  		//a LED
		  	 	guiAccess.setLed(widgetInfo, color, color);
				} else {
		  	  guiAccess.setBackColor(widgetInfo, 0, color);
				}
			}
			else if(sMethodName.equals("setColor")){
	  		widgetSetColor(sName, sParam, widgetInfo);
			}
			else if(sMethodName.equals("showBinFromByte")){
				showBinFromByte(widgetInfo);
			}
		  else {
	  		stop();
	  	}
		}
		return sMethodName !=null;
	}
	
	
	static class ValueColorAssignment
	{ 
		static class Element{
		  int from; int to;
		  int color;
		}
		
		Element[] data;
		
		ValueColorAssignment(String[] sParams, GuiPanelMngWorkingIfc guiAccess){
			data = new Element[sParams.length-1];
			int state = 0;
			for(int ii = 1; ii < sParams.length; ++ii){
				String sParam = sParams[ii];
				int posValue = sParam.indexOf('=')+1;
				final String sColor;
				final Element data1 = new Element();
				if(posValue >1){
					state = Integer.parseInt(sParam.substring(posValue));
					data1.from = state; data1.to = state;
					sColor = sParam.substring(0, posValue-1).trim();
				} else {
					sColor = sParam.trim();
					data1.from = Integer.MIN_VALUE; data1.to = Integer.MAX_VALUE;
				}
				//String[] sColParam = sParams[ii].split("[=:+]");
				final int color = guiAccess.getColorValue(sColor);
        data1.color = color;
				data[ii-1] = data1;
			}
		}
		
		int getColor(int value){
			for(Element element: data){
				if(value >= element.from && value <= element.to){
					return element.color;
				}
			}
			return 0x00880088;  //not found, return in for-loop!
		}
		
	}
	
	
	
	
	/**Information to a widget, which is colored by the value.
	 */
	static class ColoredWidget
	{ 
		ValueColorAssignment valueColorAssignment;
		
		ByteDataSymbolicAccess valueContainer;
		
		ByteDataSymbolicAccess.Variable variableContainsValue;

		
		
		public ColoredWidget(ValueColorAssignment valueColorAssignment, ByteDataSymbolicAccess valueContainerBbyteMap
			, ByteDataSymbolicAccess.Variable variableContainsValue)
		{ this.valueColorAssignment = valueColorAssignment;
		  this.valueContainer = valueContainer;
			this.variableContainsValue = variableContainsValue;
		}
		
	}
		
	
	
	void widgetSetColor(String sName, String[] sParam, WidgetDescriptor<?> widgetInfo)
	{ ColoredWidget userData;
	  Object oUserData = widgetInfo.getContentInfo();
		if(oUserData == null){
			//first usage:
			ByteDataSymbolicAccess.Variable variable = accessOamVariable.getVariable(sParam[0]);
			ValueColorAssignment colorAssignment = new ValueColorAssignment(sParam, guiAccess);
			userData = new ColoredWidget(colorAssignment, accessOamVariable, variable);
		  widgetInfo.setContentInfo(userData);
		} else {
			userData = (ColoredWidget)oUserData;
		}
		int color;
		if(userData.valueContainer !=null){
			int value = userData.valueContainer.getInt(userData.variableContainsValue, -1);
			color = userData.valueColorAssignment.getColor(value);
		} else {
			color=0xaaaaaa;
		}
		guiAccess.setBackColor(sName, -1, color);
		
	}
	
	
	void showBinFromByte(WidgetDescriptor<?> widgetInfo)
	{ ByteDataSymbolicAccess.Variable variable;
	  Object oUserData = widgetInfo.getContentInfo();
		if(oUserData == null){
			//first usage:
			variable = accessOamVariable.getVariable(widgetInfo.getDataPath());
			widgetInfo.setContentInfo(variable);
		} else {
			variable = (ByteDataSymbolicAccess.Variable)oUserData;
		}
		int value = variable.bytes.getInt(variable, -1);
		int mode = value & 0x0c;
		int colorBorder;
		int colorInner;
		switch(mode){
		case 0: colorBorder = colorInner = 0xffffff; break; 
		case 4: colorBorder = 0xff8000; colorInner = 0xffffff; break;  //incoming value is 1, border is red, used value is 0, inner is white 
		case 8: colorBorder = 0xffff80; colorInner = 0xff0000; break;  //incoming value is 0, border is yellow to distinguish, used value is 1, inner is red 
		case 0x0c: colorBorder = colorInner = 0xff8000; break; 
		default: colorBorder = colorInner = 0;  //not realistic
		}
		guiAccess.setLed(widgetInfo, colorBorder, colorInner);
		
	}
	
	
	void setValue(WidgetDescriptor<?> widgetInfo)
	{ ByteDataSymbolicAccess.Variable variable;
	  Object oUserData = widgetInfo.getContentInfo();
		if(oUserData == null){
			//first usage:
			variable = accessOamVariable.getVariable(widgetInfo.getDataPath());
			widgetInfo.setContentInfo(variable);
		} else {
			variable = (ByteDataSymbolicAccess.Variable)oUserData;
		}
		float value = variable.bytes.getFloat(variable, -1);
		Object oWidget = widgetInfo.widget;
		if(oWidget instanceof GuiSetValueIfc){
			GuiSetValueIfc widget = (GuiSetValueIfc) oWidget;
			widget.setValue(value);
		}
	}
	
	
	void setBar(WidgetDescriptor<?> widgetInfo)
	{ ByteDataSymbolicAccess.Variable variable = getVariableFromContentInfo(widgetInfo);
	  if(variable == null){
			debugStop();
		} else {
			float value = variable.bytes.getFloat(variable, -1);
			
			Object oWidget = widgetInfo.widget;
			if(oWidget instanceof ValueBar){
				ValueBar widget = (ValueBar) oWidget;
				String[] sParam;
				if( (sParam = widgetInfo.getShowParam()) != null){
					widget.setBorderAndColors(sParam);
					widgetInfo.clearShowParam();
				}
				widget.setValue(value);
			}
		}
	}
	
	
	/**Only for test. This class isn't a thread's run but the run of this class.
	 * It is called direct, it is the only one routine of the class.
	 */
	private Runnable writeCurveRoutine = new Runnable(){
		float y1,y2,y3 = 0.02F;
		final float[] valuesCurve = new float[7];
		
		@Override public void run()
  	{
			for(int ii=0; ii<1; ii++){
				//create curve points.
				
				y1 += y2/20;
				y2 += y3;
				if(y2 >=1){y3 = -0.02F;}
				else if(y2 <=-1){y3 = 0.02F;}
				valuesCurve[0] = 220 + 5*y2;
				valuesCurve[1] = 200 + 10*y2;
				valuesCurve[2] = 205 + y2;
				valuesCurve[3] = 180 + y2;
				valuesCurve[4] = y3;
				valuesCurve[5] = y3;
				valuesCurve[6] = y3;
				guiAccess.setSampleCurveViewY("curves", valuesCurve);
			}
  	}
		
	};

	



	/**Agent instance to offer the interface for updating the tab in the main TabPanel
	 */
	public final PanelActivatedGui tabActivatedImpl = new PanelActivatedGui()
	{
		@Override	public void panelActivatedGui(List<WidgetDescriptor<?>> widgets)
		{
			widgetsInTab = widgets;
			writeValuesOfTab();
		}
		
	};
	

	
	final UserActionGui actionSetValueTestInInput = new UserActionGui()
  { public void userActionGui(String sCmd, WidgetDescriptor<?> widgetInfos, Object... values)
    { 
  		final int[] ixArrayA = new int[1];
  		ByteDataSymbolicAccess.Variable variable = getVariable(widgetInfos.sDataPath, ixArrayA);
  		int value = 0; //TODO Integer.parseInt(sParam);
  		if(variable.bytes.lengthData() == 0){
  			variable.bytes.assignData(new byte[1500]);
  		}
  		variable.bytes.setFloat(variable, -1, 2.5F* value -120);
  		dataValid = true;
  		writeValuesOfTab();  //to show
    }
  };

	private void writeCurveValues()
	{
		valueUserCurves[0] = accessOamVariable.getFloat("way");
		valueUserCurves[1] = accessOamVariable.getFloat("wway");
		valueUserCurves[2] = accessOamVariable.getFloat("targetWay");
		valueUserCurves[3] = accessOamVariable.getFloat("dway");
		valueUserCurves[4] = accessOamVariable.getFloat("output");
		guiAccess.setSampleCurveViewY("userCurves", valueUserCurves);
		
	}
	
	private void redrawCurveValues()
	{
		guiAccess.redrawWidget("userCurves");
	}
	

	
	
	

  void stop(){}

  void debugStop(){
  	stop();
  }
}

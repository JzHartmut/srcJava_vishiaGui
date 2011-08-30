package org.vishia.gral.gridPanel;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.text.ParseException;


import org.vishia.gral.ifc.UserActionGui;
import org.vishia.gral.ifc.WidgetDescriptor;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.mainCmd.Report;
import org.vishia.util.StringPart;
import org.vishia.util.StringPartFromFileLines;
import org.vishia.zbnf.ZbnfParseResultItem;
import org.vishia.zbnf.ZbnfParser;




/**Class to build a simple GUI (Graphical User Interface-Application) which is configured
 * using a describing String in a ZBNF-defined syntax. 
 * <br>
*/

public class GuiDialogZbnfControlled
{

  private static final long serialVersionUID = -5745557828207884305L;


  /** True, than repeat the parsed file from begin.*/
  private boolean bAgain = false;

  

  /** The MainCmd_Ifc */
  final MainCmd_ifc main;

  final Report console;
  
  int ctNameDefault = 0;
  
  private final File fileSyntax;

  /**If true, than the application is to be terminate. */
  boolean bTerminated = false;

  /**The current directory is that directory, where the config file is found. */
  File currentDir;
  

  
  //private final Map<String, GuiPanelMngSwt> panels = new TreeMap<String, GuiPanelMngSwt>();
  
  
  /**Types saves commonly properties for more as one widget. */
  private final Map<String,WidgetDescriptor> types = new TreeMap<String,WidgetDescriptor>();
  
  private final Map<String, String> nameReplace = new TreeMap<String,String>();
  
  

  public GuiDialogZbnfControlled(MainCmd_ifc cmdLineIfc, File fileSyntax)
  { //super("", 80,80, 'C');
  	this.fileSyntax = fileSyntax;
  	this.console = cmdLineIfc;
    this.main = cmdLineIfc;
    //dialog = mainDialog; 
  }









  public boolean isTerminate(){ return bTerminated; }
  
  public void terminate(){ bTerminated = true; }
  
  
  

  /**Configures the GUI using a description in a file. The syntax is described see {@link #configureWithZbnf(String, String)}.
   * Because the configuration is containing in a user-accessible file, it may be faulty.
   * Than a error message was written on the own Report output. Therefore this routine
   * should be called after the main application is started. See example in {@link org.vishia.appl.menu.Menu}.
   * 
   *  
   * @param sTitle Title line for the application
   * @param fileConfigurationZbnf File containing the configuration. The file should be exist and able to read.
   * @return true if successfully, false on any error. If false, an error message was written
   *         using the own Report-implementation.
   */
  public String configureWithZbnf(String sTitle, File fileConfigurationZbnf, final GuiPanelMngBuildIfc panel)
  { boolean bOk = true;
  	String sError = null;
    File dirOfconfig = fileConfigurationZbnf.getParentFile();
  	StringPartFromFileLines spToParse = null;
    try
    { //spToParse = new StringPartFromFileLines(new File(sFileIn));
      spToParse = new StringPartFromFileLines(fileConfigurationZbnf, -1, null, null);
    }
    catch(FileNotFoundException exception)
    { console.writeError("file not found:" + fileConfigurationZbnf.getAbsolutePath());
      bOk = false;
    }
    catch(IOException exception)
    { console.writeError("file read error:" + fileConfigurationZbnf.getAbsolutePath());
      bOk = false;
    }
    if(spToParse != null)
    { sError = configureWithZbnf(sTitle, spToParse, panel, dirOfconfig);
    	spToParse.close();  //close the StringPart, it means it can't be used furthermore.
    }
    return sError;
  }
  
  
  /**Configures the GUI using a description given with a string. The String may be given fix programmed,
   * than its correctness of syntax will be checked while developing. 
   * If the description depends on user-inputs, see comments of {@link #configureWithZbnf(String, File)}.
   * <br><br>
   * The syntax is contained in the variable {@link ConfigureWithZbnf#syntax}, see there.
   * 
   *  
   * @param sTitle Title line for the application.
   * @param sConfigurationZbnf configuration.
   * @return true if successfully, false on any error. If false, an error message was written
   *         using the own Report-implementation.
   */
  public String configureWithZbnf(String sTitle, String sConfigurationZbnf, GuiPanelMngBuildIfc panel, File currentDir) throws ParseException
  {
    StringPart spToParse = new StringPart(sConfigurationZbnf);
    return configureWithZbnf(sTitle, spToParse, panel, currentDir);
  }
  
  
  
  /**Helper class for configuration only used in the method {@link GuiDialogZbnfControlled#configureWithZbnf(String, StringPart)}.
   * The class is defined outside to make it visible for documentation.
   *
   */
  private class ConfigureWithZbnf
  { 
    /**Current positions of next components to place, in percent. */
    int xPos = 0, yPos = 0;
    
    int xWidth = 0;

    int yWidth = 0;
      /**The ZbnfParser will be used only inside {@link configureGui}
       * 
       */
      private ZbnfParser parserCfg;

  }
  
  /**Inner, working method of {@link GuiDialogZbnfControlled#configureWithZbnf(String, File)} respectively {@link GuiDialogZbnfControlled#configureWithZbnf(String, String)}.
   * @param sTitle
   * @param spConfigurationZbnf
   * @return
   */
  private String configureWithZbnf(String sTitle, StringPart spConfigurationZbnf, final GuiPanelMngBuildIfc panelMng, File currentDir)
  { boolean bOk = true;
    //int xWindows = 80 * propertiesGui.xPixelUnit();
    //int yWindows = 30 * propertiesGui.yPixelUnit();
  
  	this.currentDir = currentDir;
    ConfigureWithZbnf mdata = new ConfigureWithZbnf();
    mdata.parserCfg = new ZbnfParser(console);
    try{ 
    	mdata.parserCfg.setSyntax(fileSyntax);  //ConfigureWithZbnf.syntax); }
    }catch(FileNotFoundException exc){
      console.writeError("Error syntaxfile not found:" + fileSyntax.getAbsolutePath(), exc);
      bOk = false;
    }catch(IOException exc){
      console.writeError("Error syntaxfile reading:" + fileSyntax.getAbsolutePath(), exc);
      bOk = false;
    } catch(ParseException exc) {
      console.writeError("Error syntax in file:" + fileSyntax.getAbsolutePath(), exc);
      bOk = false;
    }
    if(bOk){ //syntax is ok:
      if(console.getReportLevel() >=Report.debug){
    	  mdata.parserCfg.reportSyntax(console, Report.debug);
      }
      try{ bOk = mdata.parserCfg.parse(spConfigurationZbnf); }
      catch(Exception exception)
      { console.writeError("any exception while parsing:" + exception.getMessage());

        console.report("any exception while parsing", exception);
        mdata.parserCfg.reportStore(console);
        //evaluateStore(parser.getFirstParseResult());
        bOk = false;
      }
      if(!bOk)
      { console.writeError(mdata.parserCfg.getSyntaxErrorReport());
        //evaluateStore(parser.getFirstParseResult());
      }
    }
    if(console.getReportLevel() >=Report.debug){
      mdata.parserCfg.reportStore(console, Report.debug);
    }
    if(bOk) { //parsing of cfg is OK
      ZbnfParseResultItem zbnfTop = mdata.parserCfg.getFirstParseResult();
      int xSize22, ySize22;
      ZbnfParseResultItem zbnfsize = zbnfTop.getChild("xSize");
      if(zbnfsize != null){
        xSize22 = (int)zbnfsize.getParsedInteger();
        ySize22 = (int)zbnfTop.getChild("ySize").getParsedInteger();
      }
      else {
      	xSize22 = 300;
      	ySize22 = 200;
      }
      //JPanel mainDialog = new GuiDialog(null, 80,80,'C');
      //mainDialog.setBounds(0,0,xSize22, ySize22);
      //mainDialog.setLayout(null); //new FlowLayout()); 
      
      //GuiPanelMngSwt dialog = panels.get("main");
      GuiPanelMngBuildIfc dialog = panelMng;
      if(dialog == null){
        console.writeError("panel not found: ");      	
      }
      else 
      { Iterator<ZbnfParseResultItem> iter;
        //get all NameReplace
        iter = zbnfTop.iterChildren("DataReplace");
        while(iter.hasNext()){
          ZbnfParseResultItem item = iter.next();  //<?Element>
          String sKey = item.getChildString("key");
          String sValue = item.getChildString("string");
          nameReplace.put(sKey, sValue);
        }
        //get all GUI elements
        iter = zbnfTop.iterChildren("Element");
        String selectedPanel = "";  //The panel which is used.
      	while(iter.hasNext())
	      { ZbnfParseResultItem item = iter.next();  //<?Element>
	        String sHelp = item.getChildString("help");
	        Iterator<ZbnfParseResultItem> iterElement = item.iterChildren();
	        item = iterElement.next();  //first child of <Element>
	        String semantic = item.getSemantic();
	        if(semantic.equals("position")){
	        	final int xPosFrac, yPosFrac, xSizeFrac, ySizeFrac;
	          ZbnfParseResultItem zbnfPos;
	          ZbnfParseResultItem zbnfPanel = item.getChild("panel");
	          if(zbnfPanel !=null){
	          	//if a panel is given, it is used up to now. Else the current is used furthermore.
	          	selectedPanel = zbnfPanel.getParsedString();
	          	dialog.selectPanel(selectedPanel);
	          }
	          //fractional part of position and size:
	          yPosFrac =  (zbnfPos = item.getChild("yPosFrac" )) != null ? (int)zbnfPos.getParsedInteger() :  0;
	          xPosFrac =  (zbnfPos = item.getChild("xPosFrac" )) != null ? (int)zbnfPos.getParsedInteger() :  0;
	          ySizeFrac = (zbnfPos = item.getChild("ySizeFrac")) != null ? (int)zbnfPos.getParsedInteger() :  0;
	          xSizeFrac = (zbnfPos = item.getChild("xSizeFrac")) != null ? (int)zbnfPos.getParsedInteger() :  0;
	          
	          if( (zbnfPos = item.getChild("yPos")) != null){
	          	mdata.yPos = (int)zbnfPos.getParsedInteger();
	          } else {
		        	mdata.yPos = -1;  //not given. Use internal next position.
	          }
	        	if( (zbnfPos = item.getChild("xPos")) != null){
	          	mdata.xPos = (int)zbnfPos.getParsedInteger();
	          	if(mdata.xPos == 67)
	          		stop();
	          } else {
		        	mdata.xPos = -1;  //not given. Use internal next position.
	          }
	        	if( (zbnfPos = item.getChild("xWidth")) != null){
	          	mdata.xWidth = (int)zbnfPos.getParsedInteger();
	          } else {
		        	mdata.xWidth = 0;  //not given.
		        }
	        	if( (zbnfPos = item.getChild("ySizeDown")) != null){
	          	mdata.yWidth = (int)zbnfPos.getParsedInteger();
	          	if(mdata.yWidth == 1)
	          		stop();
	          	//if(mdata.yPos>=0){
	          	  //mdata.yPos += mdata.yWidth;  //yPos is the base line below the widget.
	          	//} else throw new IllegalArgumentException("not supported: ySizeDown without yPos.");
	          } else if( (zbnfPos = item.getChild("ySizeUp")) != null){
	          	mdata.yWidth = -(int)zbnfPos.getParsedInteger();
	          } else {
		        	mdata.yWidth = 0;  //not given.
		        }
	        	char direction;  //for the next widget without pos
	        	if(item.getChild("yIncr") !=null){ direction = 'd'; }
	        	else if(item.getChild("xIncr") !=null){ direction = 'r'; }
	        	else if(item.getChild("yDecr") !=null){ direction = 'u'; }
	        	else if(item.getChild("yDecr") !=null){ direction = 'l'; }
	        	else { direction = '.'; } //default no change of previous setting
	        	if(mdata.xPos >=0 || mdata.yPos >=0){
	        	  //only if one of the position is given, set it. 
	        		//If a position is -1, than the automatic increment is used by the calling method.
	        		dialog.setFinePosition(mdata.yPos, yPosFrac, mdata.yWidth + GralPos.size, ySizeFrac
	        		    , mdata.xPos, xPosFrac, mdata.xWidth + GralPos.size, xSizeFrac, direction, 0);
	        	} else if(mdata.xWidth !=0 || mdata.yWidth != 0) {
	        		dialog.setSize(mdata.yWidth, ySizeFrac, mdata.xWidth, xSizeFrac);
	        	}
	        	/**Get the next element after position, it is the element to show. */
		      	item = iterElement.next();
	          semantic = item.getSemantic();
	        
	        }else{
	        	//no position given:
	        	dialog.setNextPositionX();
	        }
	        
	        //gather all informations maybe available for the element.
	        String sText = item.getChildString("text");
	        ZbnfParseResultItem child;
          boolean noPrompt = item.getChild("noPrompt") !=null; 
          String sPromptText = item.getChildString("prompt");
          final char promptPosition;
          { String sPromptPosition = item.getChildString("promptPosition");
	          if(sPromptPosition != null){
	          	promptPosition = sPromptPosition.charAt(0);
	          } else { promptPosition = '.'; }
          }
	      String sCmd = item.getChildString("cmd");
	      if(sCmd != null && sCmd.equals("edit"))
	    	  stop();
          String sUserAction = item.getChildString("userAction");
          String sInfo = item.getChildString("info");
          String sName = item.getChildString("name");  //maybe null
          String sShowMethod = item.getChildString("showMethod");  //maybe null
          String sFormat = item.getChildString("format");  //maybe null
          String sType = item.getChildString("type");
          String sColor0 = item.getChildString("color0/color");
          String sColor1 = item.getChildString("color1/color");
          UserActionGui action = null;
          if(sFormat ==null && sInfo !=null){
						int posFormat=sInfo.indexOf('%');  //Format on end of datapath
						if(posFormat >0){
							sFormat = sInfo.substring(posFormat);
							sInfo = sInfo.substring(0, posFormat);
						}
					}
          if(sText !=null && sInfo == null){
						sInfo = sText;                        //a simple info is given, without "info="
					}
          if(sText !=null && sName == null){
						sName = sText;                        //a simple info is given, without "info="
					}
					/*
					sName = sName != null                    //if the name isn't given?
            ? sName
            : selectedPanel + "." + ( sPromptText != null         //old: $ in name uses the currentTab-name as prefix
            	       ? sPromptText                 //use promptText as name if given
                     : sInfo != null ? sInfo     //else use the data info if given
                     : ++ctNameDefault);         //else build a name automatically 
					if(sInfo == null){ 
					  sInfo = sName;                       //if no info path is given, use the name
					}
          */
					if(sType != null){
          	//if a type is given, use that widgetDescriptor and copy it.
          	WidgetDescriptor type = types.get(sType);
          	if(type == null) throw new IllegalArgumentException("type is unknown: " + sType + " for Element " + sName + "/" + sInfo);
            //take not defined informations from the type:
          	if(sCmd == null){ sCmd = type.sCmd; }
          	if(sUserAction == null){ action = type.getActionChange(); } //(UserActionGui)type.action; }
          	if(sInfo == null){ sInfo = type.sDataPath; }
          	if(sShowMethod == null){ sShowMethod = type.getShowMethod(); }
          }
          if(sUserAction != null){
          	action = dialog.getRegisteredUserAction(sUserAction);
          }
          //check whether sInfo starts with any sequence in nameReplace
          if(sInfo !=null){
            int posSep = sInfo.indexOf(':');
            if(posSep > 0){
              String sPre = nameReplace.get(sInfo.substring(0, posSep));
              if(sPre !=null){
                sInfo = sPre + sInfo.substring(posSep+1);
              }
            } else {
              String sReplace = nameReplace.get(sInfo);
              if(sReplace !=null){
                sInfo = sReplace;
              }
            }
          }//sInfo !=null
          
          //Check type of element and create it.
          if(semantic.equals("Type"))
	        { String sTypeName = item.getChildString("typeName");
	        	WidgetDescriptor widgetDescr = new WidgetDescriptor(sTypeName, '@');
	          widgetDescr.setActionChange(action);
	          widgetDescr.sCmd = sCmd;
	          widgetDescr.sDataPath = sInfo;
	          widgetDescr.sFormat = null;
	          widgetDescr.setShowMethod(sShowMethod);
	          widgetDescr.sToolTip = null;
	          types.put(sTypeName, widgetDescr);
	        } else {
	        	//create a widget description with the given data, it should be used by all elements except some.
	        	WidgetDescriptor widgetInfo = new WidgetDescriptor(sName, '.');
	          widgetInfo.setActionChange(action);
	          widgetInfo.sCmd = sCmd;
	          widgetInfo.sDataPath = sInfo;
	          widgetInfo.sFormat = sFormat;
	          widgetInfo.setShowMethod(sShowMethod);
	          widgetInfo.sToolTip = null;
	          
	          if(widgetInfo.name == null){
	          	widgetInfo.name = sInfo;    //use sInfo
	          }
	          if(semantic.equals("Button"))
		        { WidgetDescriptor widgetDescr = 
		        	dialog.addButton(sName, action, sCmd, sShowMethod, sInfo, sText);
		        }
	          else if(semantic.equals("ValueBar"))
		        { dialog.addValueBar(sName, sShowMethod, sInfo);
		        }
	          else if(semantic.equals("Slider"))
		        { dialog.addSlider(sName, action, sShowMethod, sInfo);
		        }
	          else if(semantic.equals("SwitchButton"))
		        { WidgetDescriptor widgetDescr = dialog.addSwitchButton(sName, action, sCmd, sShowMethod, sInfo, sText, sColor0, sColor1);
		        }
	          else if(semantic.equals("Led"))
		        { WidgetDescriptor widgetDescr = dialog.addLed(sName, sShowMethod, sInfo);
		          //widgetDescr.setsShowMethod(sShowMethod);
		        }
		        else if(semantic.equals("Table"))
		        { String sTitleTable = item.getChildString("text");
		          int height = (int)item.getChild("height").getParsedInteger();
		          int width = 0;
		          List<ZbnfParseResultItem> zbnfWidths = item.listChildren("columnWidth");
		          int[] widths = new int[zbnfWidths.size()];
		          int ixWidths = -1;
		          for(ZbnfParseResultItem zbnfWidth: zbnfWidths){
		          	int columnWidth = (int)zbnfWidth.getParsedInteger();
		          	widths[++ixWidths] = columnWidth;
		          	width += columnWidth;
		          }
		          
		          panelMng.addTable(sName, height, widths);
		        }
		        else if(semantic.equals("Curveview"))
		        { int nrofPoints;
		          if((child = item.getChild("nrofPoints"))!=null){
		          	nrofPoints = (int)child.getParsedInteger();
		          } else {
		          	nrofPoints = 500;
		          }
		          List<ZbnfParseResultItem> zbnfLines = item.listChildren("line");
		          panelMng.addCurveViewY(sName, nrofPoints, zbnfLines.size());
		          int ixLine = 0;
		          for(ZbnfParseResultItem zbnfLine : zbnfLines){
		          	String sNameLine = zbnfLine.getChildString("name");
		          	int colorValue = getColorValue(zbnfLine, panelMng);
		          	float offset = 0.0F, scale = 100.0F;
		          	int y0 = 0;
		          	if((child = zbnfLine.getChild("offset"))!=null){
		          		offset = (float)child.getParsedFloat();
		          	}
		          	if((child = zbnfLine.getChild("scale"))!=null){
		          		scale = (float)child.getParsedFloat();
		          	}
		          	if((child = zbnfLine.getChild("nullLine"))!=null){
		          		y0 = (int)child.getParsedInteger();
		          	}
		  	          	
		          	panelMng.setLineCurveView(sName, ixLine, sNameLine, sNameLine, colorValue, 0, y0, scale, offset);
		            ixLine +=1;
		          }
		          
		        }
		        else if(semantic.equals("Text"))
		        { int colorValue = getColorValue(item, panelMng);
		          char size = 'C';
		          if( (child = item.getChild("size"))!=null){ size = child.getParsedString().charAt(0); }
		          dialog.addText(sText, size, colorValue);
		          //JLabel text = new JLabel(sText);
		          //mdata.setBounds(dialog.propertiesGui, text);
		          //mainDialog.add(text);
		        }
		        else if(semantic.equals("Imagefile"))
		        { final String sNameImage = sName !=null ? sName : "Image"; 
		        	final String sFilePath = item.getChildString("file");
		          final int colorValue = getColorValue(item, panelMng);
		          char size = 'C';
		          if( (child = item.getChild("size"))!=null){ size = child.getParsedString().charAt(0); }
		          final InputStream input;
		          try{ 
		          	final File fileInput = sFilePath.charAt(0) == '.' 
		          		                   ? new File(currentDir, sFilePath) 
		          	                     : new File(sFilePath);
		          	input= new FileInputStream(fileInput); 
		          	dialog.addImage(sNameImage, input, mdata.yWidth, mdata.xWidth, null);
		          }
		          catch(IOException exc){
		          	console.writeError("GuiDialogZbnf: Image file not found: " + sFilePath);
		          }
		          //JLabel text = new JLabel(sText);
		          //mdata.setBounds(dialog.propertiesGui, text);
		          //mainDialog.add(text);
		        }
		        else if(semantic.equals("Line"))
		        { int colorValue = 0;
		          char size = 'C';
		          colorValue = getColorValue(item, panelMng);
		          if( (child = item.getChild("size"))!=null){ size = child.getParsedString().charAt(0); }
		          List<ZbnfParseResultItem> zbnfCoords = item.listChildren("coord");
		          float x = 0, y = 0;
		          boolean bFirst = true;
		          if(zbnfCoords!=null)for(ZbnfParseResultItem zbnfCoord: zbnfCoords){
		           	float x2, y2;
		          	x2 = (float)zbnfCoord.getChild("x").getParsedFloat(); 
	          		y2 = (float)zbnfCoord.getChild("y").getParsedFloat(); 
	          		if(bFirst){
	          			bFirst = false;
	          			x = x2; y = y2; //the first values.
		          	} else {
		              dialog.addLine(colorValue, x,y,x2,y2);
		              x = x2; y = y2; //for the next line.
		    	      }
		          } else {
		            dialog.addLine(colorValue, 0,0, mdata.yWidth, mdata.xWidth);
		          }
		        }
		        else if(semantic.equals("ShowField"))
            { widgetInfo.whatIs = 'S';
            dialog.addTextField(widgetInfo, false, (noPrompt? null : sPromptText), promptPosition);
              if(action != null){
                    dialog.addMouseButtonAction(widgetInfo.name, action, "P", "r", "d");
              }
            }
		        else if(semantic.equals("ShowTextBox")){ 
		          widgetInfo.whatIs = 'S';
		          dialog.addTextBox(widgetInfo, false, (noPrompt? null : sPromptText), promptPosition);
              if(action != null){
                dialog.addMouseButtonAction(widgetInfo.name, action, "P", "r", "d");
              }
            }
            else if(semantic.equals("InputTextline"))
		        { widgetInfo.whatIs = 'T';
		          dialog.addTextField(widgetInfo, true, sPromptText, 't');
		          if(action != null){
	          		dialog.addFocusAction(widgetInfo, action, "i", "o");
		          } else {
		          	dialog.addFocusAction(widgetInfo, dialog.getRegisteredUserAction("syncVariableOnFocus"), "i", "o");
			        }
		        }
		        else if(semantic.equals("FileInputField"))
		        { //int width = (child = item.getChild("width")) != null ? (int)child.getParsedInteger() : 20;
		          String[] sHelpLines = new String[]{"help isn't supported yet", "2. line-test"};
		          dialog.addTextField(widgetInfo, true, sPromptText, 't');
		          //TODO inputFields.put(sName, new InputFieldDescriptor(sName, widget.getTextField()));
		        }
	        }	        
	      }
	      panelMng.repaint();  //necessary!!!
      }
	    return "";
  
    }


    return null;
  }

  private int getColorValue(ZbnfParseResultItem parent, GuiPanelMngBuildIfc mng){
	  ZbnfParseResultItem child;
	  int colorValue = 0;
  	if( (child = parent.getChild("colorValue"))!=null) { 
  		colorValue = (int)child.getParsedInteger();
  	} else if((child = parent.getChild("colorName"))!=null) {
      String sColorName = child.getChildString("color");
      colorValue = mng.getColorValue(sColorName);
  	}
    return colorValue;
  }  
  
  
  void stop(){}
  
}




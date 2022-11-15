package org.vishia.gral.cfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralCanvasStorage;
import org.vishia.gral.base.GralCurveView;
import org.vishia.gral.base.GralLed;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralValueBar;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralPoint;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralLabel;
import org.vishia.mainCmd.MainCmd;
import org.vishia.mainCmd.MainCmdLogging_ifc;
import org.vishia.mainCmd.Report;
import org.vishia.util.CalculatorExpr;
import org.vishia.util.Debugutil;
import org.vishia.util.FileFunctions;
import org.vishia.util.KeyCode;
import org.vishia.zbnf.ZbnfJavaOutput;
import org.vishia.zbnf.ZbnfParser;

/**Class to read the configuration script for a Graphical User Interface. The syntax of the configuration
 * matches to the class {@link GralCfgData}, which contains all data of read configuration
 * after invoking the read method. That configuration data are associated in 
 * {@link GralCfgBuilder#GuiCfgBuilder(GralCfgData, GralMngBuild_ifc, File)} and used in
 * {@link GralCfgBuilder#buildGui(org.vishia.msgDispatch.LogMessage, int)} to build the GUI appearance.
 * The building of the GUI with the {@link GralCfgData} can be done without this script reader too,
 * but this class reads that data from a script.
 * 
 * @author Hartmut Schorrig
 *
 */
public class GralCfgZbnf
{
  
  /**Version and history
   * <ul>
   * <li>2022-11-14 new now accepts also a Window in script, syntax also adapted 
   * <li>2022-09-26 new {@link #configureWithZbnf(File, GralCfgData)} and {@link #configWithZbnf(File)} 
   *   Should be used on file input, also regarding encoding.  
   * <li>2022-08 new {@link #configWithZbnf(CharSequence)}, {@link #buildGui()}. 
   *   This operations were (are yet) part of GralCfgBuilder too, but for the new concept, it should be contained here
   *   (refactored here as copy). It means this class is complete responsible to build the GUI with config data.  
   * <li>2018-09-17 Now the syntax is contained in <code>org/vishia/gral/cfg/Syntax.zbnf</code> as file inside jar (ressource).
   *   It is read with {@link #getSyntaxFromJar()}
   * <li>2011-05-00 Hartmut created, the old ZbnfCfg.. class is obsolte now.
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
  public static final int version = 20221114;

  
  private final ZbnfParser parser;
  
  private final ZbnfJavaOutput zbnfJavaOutput;

  private final MainCmdLogging_ifc console;

  private final File fileSyntax;
  
  /**Used in new approach: {@link #configWithZbnf(CharSequence)} since 2022-08
   * 
   */
  private GralCfgData cfgData;
  
  final GralMng gralMng;
  
  private final Map<String, String> indexAlias = new TreeMap<String, String>();

  
  /**The current position for building all widgets.
   * This current position holds the value from the last built widget
   * which can be changed relative to always the next widget.
   * It means this current position is really the current while building.
   * Note that the aggregated internal widget position is always a clone of this with the current values whilce building.  
   */
  private GralPos currPos;
  
  public GralWindow window;
  
  public List<GralWidget> widgets = new LinkedList<GralWidget>();


  /**The standard syntax for graphic configuration. 
   * Note: it is possible to use an abbreviated syntax with the same semantic if the constructor {@link GralCfgZbnf#GralCfgZbnf(Report, File)} is used. 
   * This syntax is used with the constructor
   * */
  @Deprecated public final String XXXsyntaxStd = 
    " GuiDialogZbnfControlled::=\n"
  + " [ size( <#?ySize> , <#?xSize> ) ;]\n"
  + " { DataReplace: <DataReplace>\n"
  + " | Type <Type>\n"
  + " | if <Conditional>\n" 
  + " | <Element>    \n"
  + " } \\e.\n"
  + "\n"
  + "\n"
  + "Element::=\n"
  + "[@ <*:?positionString>:] \n"
  + "[ Led  ( <param?Led> ) ;\n" 
  + "| Button ( <param?Button> ) ; \n"
  + "| SwitchButton ( <param?SwitchButton> ) ;\n" 
  + "| ValueBar ( <param?ValueBar> ) ;\n"
  + "| InputTextline ( <param?InputTextline> ) ;\n"
  + "| InputField ( <param?InputTextline> ) ;\n"
  + "| InputBox ( <param?InputTextbox> ) ;\n"
  + "| InputFile ( <param?InputFile> ) ;\n"
  + "| Slider ( <param?Slider> ) ;\n"
  + "| Show ( <param?ShowField> ) ;\n"
  + "| <Table> \n"
  + "| <Text> \n"
  + "| <Imagefile>\n"
  + "| <Line>\n"
  + "| <Curveview>\n"
  + "## | <InputField>\n"
  + "| <FileInputField>\n"
  + "].\n"
  + "\n"
  + "\n"
  + "param::=\n"
  + " { [%top<?promptPosition=t>|%t<?promptPosition=t>|%r<?promptPosition=r>] : [<\"\"?prompt> | <*,)?prompt>]xxx"
  + " | cmd =  [<\"\"?cmd>|\\[<*\\]?cmd>\\]] \n"
  + " | text = [<\"\"?text>|<$-/?text>] \n"
  + " | name = [<\"\"?name>|<$-/?name>] \n"
  + " | info = [<\"\"?data>|<$-/?data>] \n"
  + " | data = [<\"\"?data>|<*,);\\ ?data>] \n"
  + " | action = [<\"\"?userAction>|<$-?userAction>] \n"
  + " | show = [<\"\"?showMethod>|<$?showMethod>] \n"
  + " | type = <$?type>\n"
  + " | format = [<\"\"?format>|<* ,);?format>]\n"
  + " | help = [<\"\"?help>|<* ,);?help>]\n"
  + " ##| prompt = [<\"\"?prompt>|<* ,);?prompt>]\n"
  + " | color = <colorName?color0> [ / <colorName?color1> ]\n" 
  + " | dropFiles = [<\"\"?dropFiles>|<$-/\\.?dropFiles>]\n"
  + " | dropText = [<\"\"?dropText>|<$-/\\.?dropText>]\n"
  + " | dragFiles = [<\"\"?dragFiles>|<$-/\\.?dragFiles>]\n"
  + " | dragText = [<\"\"?dragText>|<$-/\\.?dragText>]\n"
  + " | <\"\"?text>\n"
  + " | <$-/\\.:?text>\n"
  + " ##| <*,)?text>\n"
  + " ? , \n"
  + "}.\n"
  + "\n"
  + "\n"
  + "Text::= Text ( [<\"\"?text>|<*)?text>]\n" 
  + "    [ ,{ <!\\[ABC\\]?size> \n"
  + "       | <colorName> \n"
  + "         | color = <#x?colorValue> | <colorName>\n"
  + "       ? , }\n"
  + "    ]) ; .\n"
  + "\n"
  + "\n"
  + "Table::=Table ( [<\"\"?text>|<*)?text>] ) :\n"
  + "{ size ( { <#?columnWidth> ? + } x <#?height> ) \n"
  + "| cmd = [<\"\"?cmd>|\\[<*\\]?cmd>\\]] \n"
  + "| userAction = <$?userAction> \n"
  + "| name = [<\"\"?name>|<$?name>] ? , \n"
  + "} ; \n"
  + "\n"
  + "\n"
  + "Curveview::=Curveview ( <$?name> [ , <#?nrofPoints>][ , active<?activate>]) :\n"
  + "{<?line> line ( <$?name> \n"
  + "[ , { color = [<colorName>| <#x?colorValue>] \n"
  + " | offset = <#f?offset> \n" 
  + " | scale = <#f?scale>\n" 
  + " | data = [<\"\"?data>|<*,);\\ ?data>]\n"
  + "| nullLine = <#?nullLine>\n"
  + "? , } ] )                       ##line-parameter\n"
  + " ? , } ;.                            ##lines\n"
  + "\n"
  + "\n"
  + " Imagefile::= Imagefile \n"
  + "(  { <!\\[ABCDE\\]?size>\n"
  + "| name = <$?name>\n"
  + "| file = <\"\"?file>|<* ,)?file>\n"
  + "? , }\n"
  + ") ; ."
  + "Line::= Line ( {<colorName> | color = <#x?colorValue> |<?coord> <#f?x> , <#f?y> ? , } ) ;.\n" 
  + "?en:Line/xCoord::=\"There may be at least 2, but more possible pairs of x,y for polygons.\".\n" + 
  "colorName::=[<?color> red|green|blue|black|white|gray|brown|cyan|magenta|orange|amber|yellow|violet|purple\n" + 
  "|rd|gn|bl|gr|bn|cy|ma|or|wh|bk|ye|or|vi|pk|pu|am\n" + 
  "|lrd|lgn|lbl|lgr|lye|lor|lam|lma|lcy\n" + 
  "|prd|pgn|pbl|pgr|pye|por|pam|pma|pcy\n" + 
  "|drd|dgn|dbl|drg|dye|dor|dam|dma|dcy\n" + 
  "]\n" + 
  "\n"
 ;

  /**The current directory is that directory, where the config file is located. 
   * It is used if other files are given with relative path.*/
  File currentDir;

  public GralCfgZbnf(GralMng gralMng)
  { this.console = MainCmd.getLogging_ifc();
    this.gralMng = gralMng;
    this.currPos = new GralPos(this.gralMng);
    this.fileSyntax = null;
    String syntax = getSyntaxFromJar();
    this.parser = new ZbnfParser(console);
    try{ 
      this.parser.setSyntax(syntax); //Std);
    } catch(ParseException exc){
      throw new RuntimeException(exc);  //unexpected because syntax is given here. 
    }
    this.zbnfJavaOutput = new ZbnfJavaOutput(console);
  }


  public GralCfgZbnf(Report log, File fileSyntax, GralMng gralMng)
  { this.console = log;
    this.fileSyntax = fileSyntax;
    this.gralMng = gralMng;
    this.currPos = new GralPos(this.gralMng);
    this.parser = new ZbnfParser(log);
    try{ 
      String syntax = FileFunctions.readFile(this.fileSyntax);
      this.parser.setSyntax(syntax); 
    } catch(ParseException exc){
      throw new RuntimeException(exc);  //unexpected because syntax is given here. 
    }
    this.zbnfJavaOutput = new ZbnfJavaOutput(log);
  }

  
  /**
   * @return null if not found.
   */
  String getSyntaxFromJar() {
    String syntax = null;
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    //classLoader.getResource("org.vishia.gral.cfg.Syntax.txt");
    InputStream in = classLoader.getResourceAsStream("org/vishia/gral/cfg/Syntax.zbnf");
    if(in == null) return null; //not found
    byte[] data = new byte[10000];
    try {
      int nBytes;
      do{ 
        nBytes = in.read(data);
        String sdata = new String(data, 0, nBytes);
        if(syntax == null) { syntax = sdata; }
        else { syntax += sdata; }
      } while(nBytes == data.length);
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return syntax;
  }
  
  

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
   * @throws IOException 
   * @throws FileNotFoundException 
   * @throws UnsupportedCharsetException 
   * @throws IllegalCharsetNameException 
   * @throws ParseException 
   */
  public void configureWithZbnf(File fileConfigurationZbnf, GralCfgData destination) 
      throws IllegalCharsetNameException, UnsupportedCharsetException, FileNotFoundException, IOException, ParseException
  { //parses the configuration file and fill the configuration data.
    //Note: The building of the graphic appearance will be done in the graphic thread with this data later.
    boolean bOk = parser.parseFile(fileConfigurationZbnf, 10000, "encoding=", Charset.forName("UTF-8"));
    if(!bOk) {
      String sError = parser.getSyntaxErrorReport();
      throw new ParseException(sError, 0);
    }
    else {
      try{ zbnfJavaOutput.setContent(destination.getClass(), destination, parser.getFirstParseResult());
      } catch(Exception exc) {
        throw new RuntimeException(exc);  //unexpected because semantic and data structure is given here. 
     }
    }
  }
  

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
  public void configureWithZbnf(CharSequence configurationZbnf, GralCfgData destination)
  throws ParseException
  { //parses the configuration file and fill the configuration data.
    //Note: The building of the graphic appearance will be done in the graphic thread with this data later.
    boolean bOk = parser.parse(configurationZbnf.toString());
    if(!bOk) {
      String sError = parser.getSyntaxErrorReport();
      throw new ParseException(sError, 0);
    }
    else {
      try{ zbnfJavaOutput.setContent(destination.getClass(), destination, parser.getFirstParseResult());
      } catch(Exception exc) {
        throw new RuntimeException(exc);  //unexpected because semantic and data structure is given here. 
     }
    }  
  }
  
  
  /**Creates a Graphic application with a given config String.
   * <br>It calls:
   * <ul><li>{@link #configureWithZbnf(CharSequence, GralCfgData)}
   * <li>{@link #buildGui()}
   * </ul>
   * @param sGui The config file.
   * @return The main window of the graphic application
   * @throws ParseException on syntax error in sGui
   */
  public static GralWindow configWithZbnf ( CharSequence sGui, GralMng gralMng ) throws ParseException { 
    GralCfgZbnf thiz = new GralCfgZbnf(gralMng);                  // temporary instance of this
    thiz.cfgData = new GralCfgData(null);
    thiz.configureWithZbnf(sGui, thiz.cfgData);
    thiz.buildGui();                                       // build only the Gral instances without implementation graphic
    return thiz.window;                                    // only the window is used, the rest can be garbaged.
  }
  
  
  /**Creates a Graphic application with a given config File.
   * <br>It calls:
   * <ul><li>{@link #configureWithZbnf(CharSequence, GralCfgData)}
   * <li>{@link #buildGui()}
   * </ul>
   * @param fGui The config file.
   * @return The main window of the graphic application
   * @throws ParseException on syntax error in sGui
   */
  public static GralWindow configWithZbnf ( File fGui, GralMng gralMng) throws Exception { 
    GralCfgZbnf thiz = new GralCfgZbnf(gralMng);                  // temporary instance of this
    thiz.cfgData = new GralCfgData(null);
    thiz.configureWithZbnf(fGui, thiz.cfgData);
    thiz.buildGui();                                       // build only the Gral instances without implementation graphic
    return thiz.window;                                    // only the window is used, the rest can be garbaged.
  }
  
  
  
  
  /**Builds the appearance of the whole graphic with the given {@link GralCfgData} cfgData.
   * The cfgData can be filled manually per programming, or especially by {@link #configureWithZbnf(CharSequence, GralCfgData)}.
   * Calls {@link #buildPanel(org.vishia.gral.cfg.GralCfgPanel)} for the any panel 
   * in the {@link GralCfgData#idxPanels}. Fills the panels one after another.
   * 
   * @param log maybe null, errors and warnings are written
   * @param msgIdent The message identification for output.
   * @return null if ok, elsewhere the error hints which maybe written to log too, one per line.
   *   The window can be gotten by #window. The rest of this class may be not furthermore used.
   */
  public String buildGui ( ) {
    String sError = null;
    this.gralMng.getReplacerAlias().addDataReplace(this.cfgData.dataReplace);
    this.currPos = new GralPos(this.gralMng);
    try {
      Set<Map.Entry<String, GralCfgElement>> iterWindow = this.cfgData.getWindows();
      for(Map.Entry<String, GralCfgElement> eWin : iterWindow) {
        GralCfgElement cfg = eWin.getValue();
        GralCfgWindow win = (GralCfgWindow)cfg.widgetType;
        String posName = cfg.positionString !=null ? "@" + cfg.positionString + "=" + win.name
                       : "@screen, 10+80, 20+120 = mainWin";
        int windowProps = GralWindow_ifc.windResizeable | GralWindow_ifc.windRemoveOnClose;
        this.window = new GralWindow(this.currPos, posName, win.title, windowProps, this.gralMng);
        this.window.mainPanel.setGrid(2,2,5,5,-8,-30);
        this.currPos = new GralPos(this.window.mainPanel);             // initial GralPos for widgets inside the window.
        //
        //======>>>>
        buildPanel(win.panel, null);
        
      }
    } catch (Exception exc) {
      sError = exc.getMessage();
    }
    return sError;
  }
  
  
  
  
  /**Builds the appearance of one panel with the given {@link GralCfgPanel} cfgData.
   * @param cfgDataPanel
   * @return null if ok, elsewhere the error hints, one per line.
   */
  public String buildPanel(GralCfgPanel cfgPanel, GralPanelContent currPanel)
  { String sError = null;
    if(cfgPanel.listTabs.size()>0) {                  // tabs in this panel
      this.window.mainPanel.setToTabbedPanel();
      this.currPos = new GralPos(this.window.mainPanel);
      for(GralCfgPanel cfgTabPanel : cfgPanel.listTabs) {
        GralPanelContent panel = new GralPanelContent(this.currPos, cfgPanel.name, '@', this.gralMng);
        panel.setGrid(2,2,5,5,-12,-20);
        //GralPanelContent gralPanel = this.window.addGridPanel(this.currPos, cfgPanel.name, cfgPanel.name, 0,0,0,0);
        this.currPos = new GralPos(panel);               // GralPos describes the whole panel area of this panel.
        sError = buildPanel(cfgPanel, panel);
        if(sError !=null) { break; }
      }
    }
    else {
      for(GralCfgElement cfge: cfgPanel.listElements){
        //=================>>
        String sErrorWidgd;
        try{
          //======>>>>
          sErrorWidgd = buildWidget(cfge, currPanel); 
        }
        catch(ParseException exc) { sErrorWidgd = exc.getMessage(); }
        if(sErrorWidgd !=null){
          if(sError == null){ sError = sErrorWidgd; }
          else { sError += "\n" + sErrorWidgd; }
        }
      }
    }
    return sError;
  }
  

  
  
  /**Builds the instance of one of the {@link GralWidget} from the read ZBNF data.
   * This operation does nothing with the Graphic Implementation (SWT, AWT,...). 
   * Hence it can run in the main thread (or any other thread).
   * <br>
   * It is new since 2022-08. History: First the GralMng creates also the Implementation widgets
   * calling {@link GralMng#addSwitchButton(String, String, String, org.vishia.gral.ifc.GralColor, org.vishia.gral.ifc.GralColor)}
   * and the other operations. This was the originally concept. Hence the building of the GUI should run only in the GUI thread
   * and was a little bit more difficult to debug.
   * Since ~2014 more and more only the GralWidget instances without GUI Implementation are firstly created on manual building of the GUI,
   * for example in {@link org.vishia.gitGui.GitGui}. This is better to manage, better to debug.
   * The graphical implementation is created with all given GralWidgets later based on even this given GralWidgets.
   * That is more simple. 
   * - Now this concept is also used for the configured GralWidgets . 
   * 
   * @param cfge The configuration element data read from config file or set from the GUI-editor.
   * @return null if OK, an error String for a user info message on warning or error.
   *         It is possible that a named user action is not found etc. 
   * <br>
   */
  public String buildWidget(GralCfgElement cfge, GralPanelContent currPanel)
  throws ParseException {
    String sError = null;
    
    if(cfge.widgetType.type !=null){
      GralCfgData.GuiCfgWidget typeData = this.cfgData.idxTypes.get(cfge.widgetType.type);
      if(typeData == null){
        throw new IllegalArgumentException("GralCfgBuilder.buildWidget - unknown type; " + cfge.widgetType.type + "; in " + cfge.content); 
      } else {
        cfge.widgetType.setFromType(typeData);
      }
    }
    
    GralWidget widgd = null;
    String sName = cfge.widgetType.name;
    if(sName !=null && sName.equals("msgOfDay"))
      Debugutil.stop();
    
    if(sName ==null && cfge.widgetType.text !=null ){ sName = cfge.widgetType.text; }  //text of button etc.
    if(sName ==null && cfge.widgetType.prompt !=null){ sName = cfgData.currWindow.panel.name + "/" + cfge.widgetType.prompt; } //the prompt as name
    //the name may be null, then the widget is not registered.
    //
    
    String sDataPath = cfge.widgetType.data;
    //text is the default for a datapath.
    //no confuse: if(sDataPath ==null && cfge.widgetType.text !=null){ sDataPath = cfge.widgetType.text; }
    /*
    if(sDataPath !=null){
      //replace a prefix before ':' with its replacement, if the prefix is found.
      //This is a possibility to shorten data path.
      int posSep = sDataPath.indexOf(':');
      if(posSep > 0){
        String sPre = cfge.itsCfgData.dataReplace.get(sDataPath.substring(0, posSep));
        if(sPre !=null){
          sDataPath = sPre + sDataPath.substring(posSep+1);
        }
      } else {
        String sReplace = cfge.itsCfgData.dataReplace.get(sDataPath);
        if(sReplace !=null){
          sDataPath = sReplace;
        }
      }
    }
    */

    GralColor color0 = null;
    if(cfge.widgetType.color0 !=null) {                    // color name for main color given: 
      color0 = cfge.widgetType.color0.color;  //by name 
    }
    GralColor color1 = null;
    if(cfge.widgetType.color1 !=null) {                    // color name for main color given: 
      color1 = cfge.widgetType.color1.color;  //by name 
    }
    //char promptPosition = cfge.widgetType.promptPosition == null ? '.' : cfge.widgetType.promptPosition.charAt(0);
    String sPrompt = cfge.widgetType.prompt;
    String sPromptPos = cfge.widgetType.promptPosition;
    boolean bHasPrompt = sPrompt !=null;
    
    //
    final GralUserAction userAction; //, mouseAction;
    final String[] sUserActionArgs;
    GralWidget_ifc.ActionChangeWhen whenUserAction = null;
    if(cfge.widgetType.userAction !=null){
      String sUserAction = cfge.widgetType.userAction; 
      if(sUserAction.startsWith("@")){
        int posEnd = sUserAction.indexOf(':');
        if(posEnd < 0) { this.gralMng.log.writeError("GuiCfgBuilder - @m: ':' not found. ");  sUserAction = null; }
        else {
          for(int ix = 1; ix < posEnd; ++ix){
            char whatMouseKey = sUserAction.charAt(ix);
            switch(whatMouseKey){
            case (char)(KeyCode.mouse1Double & 0xff): whenUserAction = GralWidget_ifc.ActionChangeWhen.onMouse1Double; break;
            }
          }
          sUserAction = sUserAction.substring(posEnd+1).trim(); 
        }  
      }
      if(sUserAction !=null) {
        String[] sMethod = CalculatorExpr.splitFnNameAndParams(sUserAction);
        userAction = gralMng.getRegisteredUserAction(sMethod[0]);
        if(userAction == null){
          
          this.gralMng.log.writeError("GuiCfgBuilder - user action ignored because not found: " + cfge.widgetType.userAction);
          sUserActionArgs = null;
        } else {
          sUserActionArgs = sMethod[1] == null ? null : CalculatorExpr.splitFnParams(sMethod[1]);
        }
 
      } else { userAction = null; sUserActionArgs = null; }
    } else { userAction = null; sUserActionArgs = null; }
    

    if(cfge.positionString==null) {
      this.currPos.setNextPosition();
    } else {
      this.currPos.calcNextPos(cfge.positionString);
    }
    
    
    /*
    if(cfge.widgetType.mouseAction !=null){
      mouseAction = gralMng.getRegisteredUserAction(cfge.widgetType.mouseAction);
      if(mouseAction == null){
        this.gralMng.log.writeError("GuiCfgBuilder - mouse action ignored because not found: " + cfge.widgetType.mouseAction;
      }
    } else { mouseAction = null; }
    */
    boolean bColor0Set = false, bColor1Set = false;
    //
    if(cfge.widgetType instanceof GralCfgData.GuiCfgButton){
      GralCfgData.GuiCfgButton wButton = (GralCfgData.GuiCfgButton) cfge.widgetType;
      GralButton widg = new GralButton(this.currPos, cfge.widgetType.name, cfge.widgetType.text, userAction);
      widg.setData(cfge.widgetType.data);
      widg.sCmd = cfge.widgetType.cmd;
      if(wButton.bSwitch) {
        widg.setSwitchMode(cfge.widgetType.color0.color, cfge.widgetType.color1.color);
        int textSep;
        if(cfge.widgetType.text !=null) {
          textSep = cfge.widgetType.text.indexOf('/');
          if(textSep>0) {
            widg.setSwitchMode(cfge.widgetType.text.substring(0, textSep), cfge.widgetType.text.substring(textSep+1));
          } else {
            widg.setText(cfge.widgetType.text);
          }
        }
      }
      this.widgets.add(widgd = widg);
    } 
    else if(cfge.widgetType instanceof GralCfgData.GuiCfgText){
      GralCfgData.GuiCfgText wText = (GralCfgData.GuiCfgText)cfge.widgetType;
      int origin = 0; //TODO
      GralLabel widg = new GralLabel(this.currPos, wText.name, wText.text, origin);
      if(color0 !=null) widg.setTextColor(color0);
      if(color1 !=null) widg.setBackColor(color1, 0);
      bColor0Set = bColor1Set = true;
      this.widgets.add(widgd = widg);
    } 
    else if(cfge.widgetType instanceof GralCfgData.GuiCfgLed){
      GralCfgData.GuiCfgLed ww = (GralCfgData.GuiCfgLed)cfge.widgetType;
      widgd = new GralLed(this.currPos, sName);
      this.widgets.add(widgd);
    } 
    else if(cfge.widgetType instanceof GralCfgData.GuiCfgImage){
      GralCfgData.GuiCfgImage wImage = (GralCfgData.GuiCfgImage)cfge.widgetType;
      File fileImage = new File(this.currentDir, wImage.file_);
      if(fileImage.exists()){
        try{ InputStream imageStream = new FileInputStream(fileImage); 
          //TODO widgd = new GralImage(imageStream)
          imageStream.close();
        } catch(IOException exc){ }
      }
    } else if(cfge.widgetType instanceof GralCfgData.GuiCfgShowField){
      GralTextField widg = new GralTextField(this.currPos, sName);
      widg.setEditable(cfge.widgetType.editable);
      if(sPrompt !=null) { widg.setPrompt(sPrompt, sPromptPos); }
      this.widgets.add(widgd = widg);
    } 
    else if(cfge.widgetType instanceof GralCfgData.GuiCfgInputFile) {
      GralCfgData.GuiCfgInputFile widgt = (GralCfgData.GuiCfgInputFile)cfge.widgetType;
      final String dirMask;
      if(widgt.data !=null){
        dirMask = replaceAlias(widgt.data);
      } else { dirMask = ""; }
      //reduce the length of the text field:
      GralPos pos1 = new GralPos(this.currPos);
      pos1.setPosition(this.currPos, GralPos.same, GralPos.same, GralPos.same, GralPos.same -2);
      GralTextField widg = new GralTextField(this.currPos, sName);
      widg.setEditable(cfge.widgetType.editable);
      if(sPrompt !=null) { widg.setPrompt(sPrompt, sPromptPos); }
      this.widgets.add(widgd = widg);
      pos1.setPositionSize(GralPos.same, GralPos.next, 2, 2, 'r', null); // small button right beside the file path field.
      GralButton widgb = new GralButton(pos1, sName + "<",  "<", this.gralMng.actionFileSelect);
      List<String> listRecentFiles = null;
      GralMng.FileSelectInfo fileSelectInfo = new GralMng.FileSelectInfo(sName, listRecentFiles, dirMask, widg);
      widgb.setData(fileSelectInfo); 
      //xSize = xSize1;
      this.widgets.add(widgd = widg);
      this.widgets.add(widgb);

      //widgd = gralMng.addFileSelectField(sName, null, dirMask, null, "t");
    } 
    else if(cfge.widgetType instanceof GralCfgData.GuiCfgTable){
      GralCfgData.GuiCfgTable widgt = (GralCfgData.GuiCfgTable)cfge.widgetType;
      List<Integer> columns = widgt.getColumnWidths();
      int zColumn = columns.size();
      int[] aCol = new int[zColumn];
      int ix = -1;
      for(Integer column: columns){ aCol[++ix] = column; }
      widgd = new GralTable<>(this.currPos, sName, widgt.height, aCol);
      this.widgets.add(widgd);
    } 
    else if(cfge.widgetType instanceof GralCfgData.GuiCfgCurveview){
      GralCfgData.GuiCfgCurveview widgt = (GralCfgData.GuiCfgCurveview)cfge.widgetType;
      int nrofTracks = widgt.lines.size(); 
      GralCurveView widg = new GralCurveView(this.currPos, sName, widgt.nrofPoints, null, null);
      widg.activate(widgt.activate);
      for(GralCfgData.GuiCfgCurveLine line: widgt.lines){
        String sDataPathLine = line.data;
        final GralColor colorLine;
        if(line.color0 !=null){
          colorLine = line.color0.color;
        } else {
          colorLine = GralColor.getColor(line.colorValue);  //maybe 0 = black if not given.
        }
        widg.addTrack(line.name, sDataPathLine, colorLine, 0, line.nullLine, line.scale, line.offset);
      }
      this.widgets.add(widgd = widg);
    } else {
      switch(cfge.widgetType.whatIs){
        case 'T': {                                        // T= editable text field
          GralTextField.Type type = GralTextField.Type.editable;
          GralTextField widg = new GralTextField(this.currPos, sName, type);
          if(sPrompt !=null) { widg.setPrompt(sPrompt, sPromptPos); }
          this.widgets.add(widgd = widg);
        } break;
        case 't': {                                        // t= text box
          GralTextBox widg = new GralTextBox(this.currPos, sName);
          widg.setEditable(true);
          this.widgets.add(widgd = widg);
        } break;
        case 'U':{
          widgd = new GralValueBar(this.currPos, sName);
          this.widgets.add(widgd);
        } break;
        case 'I':{                                         // L= Line
          GralCfgData.GuiCfgLine cfgLine = (GralCfgData.GuiCfgLine)cfge.widgetType;
          //copy the points from the type GuiCfgCoord to GralPoint
          List<GralPoint> points = new LinkedList<GralPoint>();
          for(GralCfgData.GuiCfgCoord coord: cfgLine.coords){
            points.add(new GralPoint(coord.x, coord.y));
          }
          GralCanvasStorage canvas = currPanel.getCreateCanvas();
          canvas.drawLine(this.currPos, color0, points);
        } break;
        default: {
          widgd = null;
        }//default
      }
      
    }
    if(widgd !=null){
      //set common attributes for widgets:
      //widgd.pos = gui.getPositionInPanel();
      String sShowMethod1 = cfge.widgetType.showMethod;
      if(sShowMethod1 !=null){
        String[] sShowMethod = CalculatorExpr.splitFnNameAndParams(sShowMethod1);
       
        GralUserAction actionShow = gralMng.getRegisteredUserAction(sShowMethod[0]);
        if(actionShow == null){
          this.gralMng.log.writeError("GuiCfgBuilder - show method not found: " + sShowMethod[0]);
        } else {
          String[] param = sShowMethod[1] == null ? null : CalculatorExpr.splitFnParams(sShowMethod[1]);
          widgd.setActionShow(actionShow, param);
        }
      }
      widgd.sCmd = cfge.widgetType.cmd;
      /*
      String sCmd = cfge.widgetType.cmd;
      if(sCmd !=null){
        GralUserAction actionCmd = gralMng.getRegisteredUserAction(sCmd);
        if(actionCmd == null){
          this.gralMng.log.writeError("GuiCfgBuilder - cmd action not found: " + sCmd;
        } else {
          widgd.setActionChange(actionCmd);
        }
      }*/
      if(userAction !=null){
        if(whenUserAction == null) { widgd.specifyActionChange(cfge.widgetType.userAction, userAction, sUserActionArgs); }
        else { widgd.specifyActionChange(cfge.widgetType.userAction, userAction, sUserActionArgs, whenUserAction); }
      }
      //if(mouseAction !=null){
        //fauly type, does not work: widgd.setActionMouse(mouseAction, 0);
      //}
      String sFormat = cfge.widgetType.format;
      if(sFormat !=null){
         widgd.setFormat(sFormat);
      }
      if(cfge.widgetType.help!=null){
        widgd.setHtmlHelp(cfge.widgetType.help);
      }
      if(cfge.widgetType.color0 != null && !bColor0Set){
        widgd.setBackColor(cfge.widgetType.color0.color, 0);
      }
      if(cfge.widgetType.color1 != null && !bColor1Set){
        widgd.setLineColor(cfge.widgetType.color1.color, 0);
      }
      if(cfge.widgetType.dropFiles !=null){
        GralUserAction actionDrop = gralMng.getRegisteredUserAction(cfge.widgetType.dropFiles);
        if(actionDrop == null){
          this.gralMng.log.writeError("GuiCfgBuilder - action for drop not found: " + cfge.widgetType.dropFiles);
        } else {
          widgd.setDropEnable(actionDrop, KeyCode.dropFiles);
        }
      }
      if(cfge.widgetType.dragFiles !=null){
        GralUserAction actionDrag = gralMng.getRegisteredUserAction(cfge.widgetType.dragFiles);
        if(actionDrag == null){
          this.gralMng.log.writeError("GuiCfgBuilder - action for drag not found: " + cfge.widgetType.dragFiles);
        } else {
          widgd.setDragEnable(actionDrag, KeyCode.dragFiles);
        }
      }
      if(cfge.widgetType.dragText !=null){
        GralUserAction actionDrag = gralMng.getRegisteredUserAction(cfge.widgetType.dragText);
        if(actionDrag == null){
          this.gralMng.log.writeError("GuiCfgBuilder - action for drag not found: " + cfge.widgetType.dragText);
        } else {
          widgd.setDragEnable(actionDrag, KeyCode.dragText);
        }
      }
      if(sDataPath !=null) {
        widgd.setDataPath(sDataPath);
      }
      //save the configuration element as association from the widget.
      widgd.setCfgElement(cfge);
    }
    if(sError == null) {
      
    }
    return sError;
  }
  
  
  
  String replaceAlias(String src)
  {
    int posSep;
    if((posSep = src.indexOf("<*")) < 0) { return src; } //unchanged
    else {
      StringBuilder u = new StringBuilder(src);
      do{
        int posEnd = u.indexOf(">", posSep+2);
        String sAlias = src.substring(posSep + 2, posEnd);
        String sValue = indexAlias.get(sAlias);
        if(sValue == null){ sValue = "??" + sAlias + "??";}
        u.replace(posSep, posEnd+1, sValue);
      } while((posSep = u.indexOf("<*", posSep)) >=0);  //note nice side effect: replace in sValue too
      return u.toString();
    }
  }

}

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
import org.vishia.util.StringFunctions_B;
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
   * <li>2023-08-08 The {@link GralCfgBuilder} is reactivated. It has gotten the content from here, 
   *   because it was copied before, improved, tested and used since 2022-08. Now there are back copied.
   *   It is comparable with the older version in GralCfgBuilder.
   * <li>2023-08-08 Now possible fill a specific panel: {@link #configWithZbnf(File, GralPanelContent)} 
   *   used for {@link org.vishia.guiInspc.InspcGui} 
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
  public static GralWindow configWithZbnf ( CharSequence sGui, String sWinTitle, GralMng gralMng ) throws ParseException { 
    GralCfgZbnf thiz = new GralCfgZbnf(gralMng);                  // temporary instance of this
    thiz.cfgData = new GralCfgData(null);
    thiz.configureWithZbnf(sGui, thiz.cfgData);
    GralCfgBuilder cfgBuilder = new GralCfgBuilder(thiz.cfgData, gralMng, new File("."));

    cfgBuilder.buildGui(sWinTitle);                                       // build only the Gral instances without implementation graphic
    return cfgBuilder.window;                                    // only the window is used, the rest can be garbaged.
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
  public static GralWindow configWithZbnf ( File fGui, String sWinTitle, GralMng gralMng) throws Exception { 
    GralCfgZbnf thiz = new GralCfgZbnf(gralMng);           // temporary instance of this
    thiz.cfgData = new GralCfgData(null);
    thiz.configureWithZbnf(fGui, thiz.cfgData);
    GralCfgBuilder cfgBuilder = new GralCfgBuilder(thiz.cfgData, gralMng, fGui.getParentFile());
    cfgBuilder.buildGui(sWinTitle);                              // builds only the Gral instances without implementation graphic
    return cfgBuilder.window;                                    // returns the last instantiated or the only one Window.
  }
  
  
  
  /**Builds the content of the given panel.
   * It is assumed that the config file does not contain a Window.
   * Especially all unknown panel names are recognized as a tab of the given tab panel.
   * @param fGui The config file
   * @param dstPanel The panel where the content should applied to
   * @throws Exception
   */
  public static void configWithZbnf ( File fGui, GralPanelContent dstPanel) throws Exception { 
    GralCfgZbnf thiz = new GralCfgZbnf(dstPanel.gralMng);  // temporary instance of this
    thiz.cfgData = new GralCfgData(null);
    thiz.configureWithZbnf(fGui, thiz.cfgData);
    GralCfgBuilder cfgBuilder = new GralCfgBuilder(thiz.cfgData, dstPanel.gralMng, fGui.getParentFile());

    cfgBuilder.buildPanel(thiz.cfgData.currWindow.panelWin, dstPanel);                               // builds only the Gral instances without implementation graphic
  }
  
  
  
  
  
  

}

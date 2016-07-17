package org.vishia.gral.cfg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.mainCmd.MainCmd;
import org.vishia.mainCmd.MainCmdLogging_ifc;
import org.vishia.mainCmd.Report;
import org.vishia.util.StringPartFromFileLines;
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
  public static final int version = 20120303;

  
  private final ZbnfParser parser;
  
  private final ZbnfJavaOutput zbnfJavaOutput;

  private final MainCmdLogging_ifc console;

  private final File fileSyntax;
  
  /**The standard syntax for graphic configuration. 
   * Note: it is possible to use an abbreviated syntax with the same semantic if the constructor {@link GralCfgZbnf#GralCfgZbnf(Report, File)} is used. 
   * This syntax is used with the constructor
   * */
  public final String syntaxStd = 
    " GuiDialogZbnfControlled::=\n"
  + " [ size( <#?ySize> , <#?xSize> ) ;]\n"
  + " { DataReplace: <DataReplace>\n"
  + " | Type <Type>\n"
  + " | if <Conditional>\n" 
  + " | <Element>    \n"
  + " } \\e.\n"
  + "Element::=\n"
  + "[<?position> XXX@ [<$?panel> ,]\n" 
  + "               [<?yPosRelative> &+]\n"
  + "               [<#?yPos>[\\.<#?yPosFrac>]]\n" 
  + "               [ [+] <#-?ySizeDown>[\\.<#?ySizeFrac>]| +* <?yOwnSize> |] ##| - <#?ySizeUp>|]\n" 
  + "               [ <?yIncr> ++] \n"
  + "               [ ,\n"
  + "                 [<?xPosRelative> &+]\n" 
  + "                 [<#?xPos>[\\.<#?xPosFrac>]]\n" 
  + "                 [ [+] <#-?xWidth>[\\.<#?xSizeFrac>]| +* <?xOwnSize> |]\n"
  + "                 [ <?xIncr> ++]\n"
  + "               ] :\n"
  + "] \n"
  + "[@ <*:?positionString>:] \n"
  + "[ Led <Led>\n" 
  + "| <Button> \n"
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
  + "] \n"
 + ".\n"
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
 + "Text::= Text ( [<\"\"?text>|<*)?text>]\n" 
 + "    [ ,{ <!\\[ABC\\]?size> \n"
 + "       | <colorName> \n"
 + "         | color = <#x?colorValue> | <colorName>\n"
 + "       ? , }\n"
 + "    ]) ; .\n"
 + "\n"
 ;

  /**The current directory is that directory, where the config file is located. 
   * It is used if other files are given with relative path.*/
  File currentDir;

  public GralCfgZbnf()
  { this.console = MainCmd.getLogging_ifc();
    this.fileSyntax = null;
    this.parser = new ZbnfParser(console);
    try{ this.parser.setSyntax(syntaxStd);
    } catch(ParseException exc){
      throw new RuntimeException(exc);  //unexpected because syntax is given here. 
    }
    this.zbnfJavaOutput = new ZbnfJavaOutput(console);
  }


  public GralCfgZbnf(Report log, File fileSyntax)
  { this.console = log;
    this.fileSyntax = fileSyntax;
    this.parser = new ZbnfParser(log);
    this.zbnfJavaOutput = new ZbnfJavaOutput(log);
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
  public String configureWithZbnf(File fileConfigurationZbnf, GralCfgData destination)
  { String sError = null;
    File dirOfconfig = fileConfigurationZbnf.getParentFile();
    System.out.println("GralCfgZbnf - start parse cfg file; " + fileConfigurationZbnf.getAbsolutePath());
    //parses the configuration file and fill the configuration data.
    //Note: The building of the graphic appearance will be done in the graphic thread with this data later.
    sError = ZbnfJavaOutput.parseFileAndFillJavaObject(destination.getClass(), destination
      , fileConfigurationZbnf, fileSyntax, console, 0);
    if(sError != null)
    { return "Error reading config file" + sError;
    }

    
    StringPartFromFileLines spToParse = null;
    try
    { //spToParse = new StringPartFromFileLines(new File(sFileIn));
      spToParse = new StringPartFromFileLines(fileConfigurationZbnf, -1, null, null);
    }
    catch(FileNotFoundException exception)
    { sError = "file not found:" + fileConfigurationZbnf.getAbsolutePath();
      console.writeError(sError);
    }
    catch(IOException exception)
    { sError = "file read error:" + fileConfigurationZbnf.getAbsolutePath();
      console.writeError(sError);
    }
    if(spToParse != null)
    { //sError = configureWithZbnf(sTitle, spToParse, panel, dirOfconfig);
      spToParse.close();  //close the StringPart, it means it can't be used furthermore.
    }
    return sError;
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
  

  
  
}

package org.vishia.gral.cfg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import org.vishia.gral.gridPanel.GuiDialogZbnfControlled;
import org.vishia.gral.gridPanel.GuiPanelMngBuildIfc;
import org.vishia.mainCmd.Report;
import org.vishia.util.StringPart;
import org.vishia.util.StringPartFromFileLines;
import org.vishia.zbnf.ZbnfJavaOutput;
import org.vishia.zbnf.ZbnfParseResultItem;
import org.vishia.zbnf.ZbnfParser;

public class GuiCfgZbnf
{
  
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

  }//class ConfigureWithZbnf
  
  private final ZbnfParser parser;
  
  private final ZbnfJavaOutput zbnfJavaOutput;

  private final Report console;

  private final File fileSyntax;

  /**The current directory is that directory, where the config file is located. 
   * It is used if other files are given with relative path.*/
  File currentDir;

  public GuiCfgZbnf(Report log, File fileSyntax)
  { this.console = log;
    this.fileSyntax = fileSyntax;
    this.parser = new ZbnfParser(log);
    this.zbnfJavaOutput = new ZbnfJavaOutput();
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
  public String configureWithZbnf(File fileConfigurationZbnf, GuiCfgData destination)
  { String sError = null;
    File dirOfconfig = fileConfigurationZbnf.getParentFile();
    
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
  

  /**Inner, working method of {@link GuiDialogZbnfControlled#configureWithZbnf(String, File)} respectively {@link GuiDialogZbnfControlled#configureWithZbnf(String, String)}.
   * @param sTitle
   * @param spConfigurationZbnf
   * @return
   */
  private String configureWithZbnf(String sTitle, StringPart spConfigurationZbnf, final GuiPanelMngBuildIfc panelMng, File currentDirP)
  { boolean bOk = true;
    //int xWindows = 80 * propertiesGui.xPixelUnit();
    //int yWindows = 30 * propertiesGui.yPixelUnit();
  
    this.currentDir = currentDirP;
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
      
    }
    return null;
  }
  
  
  
  
}

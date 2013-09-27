package org.vishia.gral.cfg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import org.vishia.gral.base.GralPos;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.mainCmd.Report;
import org.vishia.util.StringPart;
import org.vishia.util.StringPartFromFileLines;
import org.vishia.zbnf.ZbnfJavaOutput;
import org.vishia.zbnf.ZbnfParseResultItem;
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

  private final Report console;

  private final File fileSyntax;

  /**The current directory is that directory, where the config file is located. 
   * It is used if other files are given with relative path.*/
  File currentDir;

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
  

  
  
}

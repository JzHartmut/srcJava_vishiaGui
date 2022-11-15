package org.vishia.stimuliSelector;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.script.ScriptException;

import org.vishia.cmd.JZtxtcmdExecuter;
import org.vishia.cmd.JZtxtcmdScript;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.swt.SwtFactory;
import org.vishia.jztxtcmd.JZtxtcmd;
import org.vishia.mainCmd.PrintStreamAdapter;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.DataAccess;
import org.vishia.util.DataAccess.Variable;
import org.vishia.util.Debugutil;
import org.vishia.util.KeyCode;
import org.vishia.util.StringFunctions_C;

/*Test with jzcmd: call jzcmd with this java file with its full path:
D:/vishia/Java/srcJava_vishiaGui/org/vishia/gral/test/StimuliSelector.java
==JZcmd==
java org.vishia.gral.test.SimSelector.main(null);                 
==endJZcmd==
 */


/**A Gui Application which reads a JZtxtcmd script containing data, it shows 6 tables for selection. 
 * On selection a subroutine from thiz JZtxtcmd script "execSelection(Map line1, ... Map line6)" will be executed.
 * <br>
 * The script needs <pre>
 * ==JZtxtcmd==
 * List tab1 = ##content of tab1
 * [ { name="ident",   descr="...",     var="anyString", anyOtherVar="anyInfo" }
 * , { name="ident2",  descr="...",     var="xyz", anyOtherVar="anyInfo" }
 * ];
 * 
 * List tab2AnyIdent = ##content of tab2
 * [ { name="ident",   descr="...",     var="anyString", anyOtherVar="anyInfo" }
 * , { name="ident2",  descr="...",     var="xyz", anyOtherVar="anyInfo" }
 * ];
 * 
 * 
 * class ToGui  ##determines how the tabs named. 
 * {
 *  List tdata1 = tab1;
 *  List tdata2 = tab2AnyIdent;
 * }
 * 
 * ##execute the following routine on selection button:
 * sub btnGenSelection ( Map line1, Map line2, Map line3, Map line4, Map line5, Map line6){
 *   <+out><&scriptdir>/<&scriptfile>: btnGenSelection (<: > <&line1.name>, <&line2.name>) ..... <.+n>;  
 *     ... here some statements to create any output to use anywhere other from the selection.
 *   <.+>
 * }
 * 
 * sub btnGenTestcases ( String select) {
 *   if(jztc.envar.soRx) {    ##hint: use definitely the script variable, not the local copy.
 *   ....
 * }
 * </pre>


 *  
 * @author Hartmut Schorrig LPGL license. Do not remove the license declaration. 
 *
 */
public class StimuliSelector
{
  /**Version, history and license.
   * <ul>
   * <li>2021-06-17 renew possibilities to assemble the selection expression 
   * <li>2021-06-12 offer {@link #soRxVar} and {@link #selectorVariables} accessible from the jzTc script via envar.
   * <li>2021-06-09 Hartmut better support for building test cases
   * <li>2020-07-20 hartmut new If the script contains subroutines "exec1" ... "exec4"
   *   Buttons for that will be created, for special functions. 
   * <li>2020-07-20 hartmut new now invokes <pre>
   *   sub execSelection(Map line1, Map line2, Map line3, Map line4, Map line5, Map line6){...\</pre>
   *   it is more simple to build the output. 
   * <li>2016-11-72 Hartmut adaption, System.out redirection to out textarea
   * <li>2016-01-10 Hartmut Created to create test scripts 
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:
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
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   * 
   */
  String version = "2020-07-20";
  
  final LogMessage log;
  
  GralMng gralMng = new GralMng(null);  //Note: log is set in the ctor of this
  
  /**The main window */
  GralWindow window;
  
  @SuppressWarnings("unchecked") 
  GralTable<Map<String, DataAccess.Variable<Object>>>[] wdgTables = new GralTable[6];
  
  /**Set on selection of a line in a table, the last touched one. */
  volatile GralTable<Map<String, DataAccess.Variable<Object>>> wdgLastSelectedTable;
  
  public GralButton btnReadConfig, btnGenSelection, btnCleanOut, btnGenTestcases;
  public GralButton btnAddTestcase, btnDeselectLines, btnExampleSel, btnHelp;
  
  public GralButton[] btnExecSelection = new GralButton[2];
  
  public GralTextBox wdgSelects;
  
  public GralTextBox output;
  
  
  private final JZtxtcmd jzcmd;
  
  private JZtxtcmdExecuter executer = new JZtxtcmdExecuter();
    
  private JZtxtcmdScript script;
  
  boolean isTableInitialized;
  
  public final File fileConfig;
  
  /**This variable is accessible from JZtxtcmd execution. It can be uses as an instance to communicate 
   * with the test system, especially via {@link org.vishia.communication.SocketCmd_InterProcessComm}
   * using emC/soCmd/SocketCmd.exe on the other side as command line program. 
   * <br>
   * It is from the common type Object to use in JZtxtcmd container.
   * Reason for this variable: Should preserve on re read the JZtxtcmd script.
   * If a simple script variable would be used, it will be destroyed while re-compilation.
   */
  private DataAccess.Variable<Object> soRxVar = new DataAccess.Variable<Object>('O', "soRx", null);

  /**This variable is accessible from JZtxtcmd execution to access all elements in the graphic. */
  private DataAccess.Variable<Object> thisVar = new DataAccess.Variable<Object>('O', "stimuliSelector", this, true);

  
  
  /**container to offer to the JZtxtcmd script. */
  private final Map<String, DataAccess.Variable<Object>> selectorVariables;
  
  
  PrintStream outOld, errOld, outNew = null, errNew = null;
  
  /**Start
   * @param args only the JZtxcmd script path/to/config.jzt.cmd
   */
  public static void main(String[] args){
    if(args.length < 1) {
      System.err.println("argument 1: path/to/config.jzt.cmd");
    }
    StimuliSelector main = new StimuliSelector(args[0]);
    char size = 'C';
    if(args.length >=2) {
      if(args[1].startsWith("-size:")) {
        size = args[1].charAt(6);
      }
    }
    main.openWindow1(size);
    main.waitForClosePrimaryWindow();
  }
  
  
  /**local ctor only from main
   * @param fileConfig immediately the command line argument
   */
  StimuliSelector(String fileConfig)
  {
    this.fileConfig = new File(System.getProperty("user.dir"), fileConfig);
    JZtxtcmd jzcmd = null;
    try {
      jzcmd = new JZtxtcmd();
      this.script = jzcmd.compile(this.fileConfig, null);
      assert(this.script !=null);
    } catch( ScriptException exc) {
      System.err.println("ERROR unexpected" + exc.getMessage());
    }
    this.jzcmd = jzcmd;
    String sTitleWindow = "Stimuli Selector"; //Todo title may depend on script
    //
    this.log = new LogMessageStream(System.out);
    this.gralMng.setLog(log);
    int windProps = GralWindow_ifc.windRemoveOnClose | GralWindow_ifc.windResizeable;
    this.window = this.gralMng.createWindow("@screen,10+80,20+125 = StimuliSelector", sTitleWindow, windProps);
    for(int iTable = 0; iTable < this.wdgTables.length; ++iTable) {
      int xtable = iTable %3;
      int ytable = iTable /3;
      this.gralMng.setPosition(21*ytable + 10, 21*ytable + 30, xtable * 40, xtable * 40 +40, 0, 'd');
      String name = "table" + iTable;
      int[] columnWidths = new int[2];
      columnWidths[0] = 15;
      columnWidths[1] = 0;
      this.wdgTables[iTable] = new GralTable<Map<String, DataAccess.Variable<Object>>>(this.gralMng.refPos(), name, 20, columnWidths); //new GralTable<>(name, columnWidths);
      this.wdgTables[iTable].specifyActionChange("actionTouchLine", this.actionTouchLine, null);
      this.wdgTables[iTable].specifyActionOnLineSelected(this.actionSelectInTable);
      StimuliSelector.this.wdgTables[iTable].addContextMenuEntryGthread(1, "test", "add to select rule", this.actionTouchLine);
      this.wdgTables[iTable].setData(new Integer(iTable +1));
      //, GralWidget_ifc.ActionChangeWhen.onCtrlEnter, GralWidget_ifc.ActionChangeWhen.onMouse1Double);
      //this.wdgTables[iTable].specifyContextMenu(null);
    }
    this.wdgLastSelectedTable = this.wdgTables[0]; //default
    this.gralMng.setPosition(2, 5, 1, 12, 0, 'r', 1);
    this.btnCleanOut = this.gralMng.addButton("cleanOut", this.actionCleanOut, "clean output");
    this.btnReadConfig = this.gralMng.addButton("readConfig", this.actionReadConfig, "read config");
    StimuliSelector.this.gralMng.setPosition(2, 5, 27, 39, 0, 'r', 1);
    this.btnGenTestcases = this.gralMng.addButton("genTestCase", this.actionGenTestcases, "gen test cases");
    StimuliSelector.this.gralMng.setPosition(6, 9, 1, 12, 0, 'r', 1);
    this.btnGenSelection = this.gralMng.addButton("genSelection", new GralUserActionButton("btnGenSelection"), "gen selection");
    JZtxtcmdScript.Subroutine sub1 = this.script.getSubroutine("btnExec1");
    if(sub1 !=null) {
      String btnText = sub1.formalArgs.get(0).textArg;
      if(btnText == null) { btnText = sub1.name; }
      this.btnExecSelection[0] = this.gralMng.addButton("btnExec11", new GralUserActionButton("btnExec1"), btnText);
    }
    JZtxtcmdScript.Subroutine sub2 = this.script.getSubroutine("btnExec2");
    if(sub2 !=null) {
      String btnText = sub2.formalArgs.get(0).textArg;
      if(btnText == null) { btnText = sub2.name; }
      this.btnExecSelection[1] = this.gralMng.addButton("btnExec12", btnText, new GralUserActionButton("btnExec2"));
    }
    JZtxtcmdScript.Subroutine sub3 = this.script.getSubroutine("btnExec3");
    if(sub3 !=null) {
      String btnText = sub3.formalArgs.get(0).textArg;
      if(btnText == null) { btnText = sub3.name; }
      this.btnExecSelection[2] = this.gralMng.addButton("btnExec13", btnText, new GralUserActionButton("btnExec3"));
    }
    JZtxtcmdScript.Subroutine sub4 = this.script.getSubroutine("btnExec4");
    if(sub4 !=null) {
      String btnText = sub4.formalArgs.get(0).textArg;
      if(btnText == null) { btnText = sub4.name; }
      this.btnExecSelection[3] = this.gralMng.addButton("btnExec4", btnText, new GralUserActionButton("btnExec4"));
    }
    StimuliSelector.this.gralMng.setPosition(2, 10, 40, 104, 0, 'd');
    this.wdgSelects = StimuliSelector.this.gralMng.addTextBox("test", true, null, 'r');
    this.wdgSelects.setText("");
    this.wdgSelects.specifyActionChange("actionTouchTestCaseString", StimuliSelector.this.actionTouchTestcaseString, null);
    this.gralMng.setPosition(2, 5, 105, 112, 0, 'r');
    this.btnAddTestcase = this.gralMng.addButton("addTestCase", "add sel", this.actionAddTestcases);
    this.btnHelp = this.gralMng.addButton("help", "help", null);   //this.actionHelp);
    this.gralMng.setPosition(6, 9, 105, 112, 0, 'r');
    this.btnExampleSel = this.gralMng.addButton("exmpl", "show", this.actionShowSel);
    this.btnDeselectLines = this.gralMng.addButton("cleanSelTable ", "clean", this.actionDeselectLines);
    this.gralMng.setPosition(52, 0, 0, 0, 0, 'U');
    this.output = this.gralMng.addTextBox("output");
    this.outOld = System.out; this.errOld = System.err;
    System.setOut(this.outNew = new PrintStreamAdapter("", StimuliSelector.this.output));
    System.setErr(this.errNew = new PrintStreamAdapter("", StimuliSelector.this.output));
    
    this.isTableInitialized = true;
    this.gralMng.createHtmlInfoBoxes(null);
    String sHelpdir = StimuliSelector.this.fileConfig.getAbsoluteFile().getParent() + "/";
    this.gralMng.setHelpBase(sHelpdir);
    this.gralMng.setHelpUrl("+StimuliSelector_help.html");
    this.btnHelp.specifyActionChange("help", StimuliSelector.this.gralMng.actionHelp, null);
    
    this.selectorVariables = new TreeMap<String, DataAccess.Variable<Object>>();
    this.selectorVariables.put("soRx", this.soRxVar);
    this.selectorVariables.put(this.thisVar.name(), this.thisVar);
    this.selectorVariables.put("colorGenTestcaseActive", new  DataAccess.Variable<Object>('O', "colorGenTestcaseActive", GralColor.getColor("lgn")));
    this.selectorVariables.put("colorGenTestcaseError", new  DataAccess.Variable<Object>('O', "colorGenTestcaseActive", GralColor.getColor("lrd")));
    this.selectorVariables.put("colorGenTestcaseWaitRx", new  DataAccess.Variable<Object>('O', "colorGenTestcaseActive", GralColor.getColor("lye")));
    this.selectorVariables.put("colorGenTestcaseInactive", new  DataAccess.Variable<Object>('O', "colorGenTestcaseInactive", GralColor.getColor("wh")));
    
    
  }
  
  
  
  public static void openWindow(){
    StimuliSelector main = new StimuliSelector(null);
    main.openWindow1('D');
  }
  
  
  /**Initializes/creates the main window
     * @param size
     */
    @SuppressWarnings("deprecation")
    private void openWindow1(char size){
      GralFactory gralFactory = new SwtFactory();
      gralFactory.createGraphic(this.gralMng, 'C');
  
  //    LogMessage log = new LogMessageStream(System.out);
  //    
  //    this.window = gralFactory.createWindow(log, "Select Stimuli", size, 100, 50, 600, 400);
  //    this.gralMng = this.window.gralMng();
  //    this.gralMng.gralDevice.addDispatchOrder(this.initGraphic);
      //initGraphic.awaitExecution(1, 0);
      
    }


  public void waitForClosePrimaryWindow()
  {
    while(gralMng.isRunning()){
      try{ Thread.sleep(100);} 
      catch (InterruptedException e)
      { //dialogZbnfConfigurator.terminate();
      }
      if(this.isTableInitialized) {
        this.isTableInitialized = false;
        readConfig();
      }
    }
    if(this.outNew !=null) { System.setOut(this.outOld); this.outNew.close(); }
    if(this.errNew !=null) { System.setErr(this.errOld); this.errNew.close(); }
    
  }
  
  
  
  
  /**Read and translate the JZtxtcmd to config the GUI
   * It is also invoked from the button [read config] on the opened GUI.
   */
  void readConfig()
  {
    JZtxtcmdExecuter.ExecuteLevel level = null;
    try {
      this.script = this.jzcmd.compile(this.fileConfig, null);
      assert(this.script !=null);
      this.executer.initialize(this.script, false, null, this.selectorVariables, null);
      level = this.executer.execute_Scriptclass("ToGui"); 
      
    } catch( ScriptException exc) {
      System.err.println(exc.getMessage());
    }
    if(level !=null) {
      for(int iList = 0; iList < this.wdgTables.length; ++iList ) {
        //boolean bErr = false;
        this.wdgTables[iList].clearTable();;
        String nameList = "tdata" + (iList +1);
        DataAccess.Variable<Object> vlist1 = level.localVariables.get(nameList);
        if(vlist1 !=null && vlist1.value() instanceof List) {
          List<?> list1 = (List<?>)vlist1.value();
          for(Object listElement: list1) {
            if(listElement instanceof Map) {
              @SuppressWarnings("unchecked") 
              Map<String, DataAccess.Variable<Object>> set1 = (Map<String, DataAccess.Variable<Object>>) listElement;
              DataAccess.Variable<Object> descrv = set1.get("descr");
              DataAccess.Variable<Object> namev = set1.get("name");
              if(descrv !=null && namev !=null) {
                String[] lineTexts = new String[2];
                lineTexts[0] = namev.value().toString();
                lineTexts[1] = descrv.value().toString();
                this.wdgTables[iList].addLine(lineTexts[0], lineTexts, set1);
              }
            }
            System.out.println(listElement.toString());
          }
        }
        this.wdgTables[iList].repaint();
      }
      level.close();
    }
    
  }
  
  
  
  
  /**This is the common routine for all buttons which should call a sub routine from the JZtxtcmd control script.
   * It is used also for gen Stimuli and the exec buttons. 
   * @param subroutine name of the sub routine in the JZtxtcmd script for this button.
   */
  void execBtnAction(String subroutine)
  {
    String[] identifier = new String[this.wdgTables.length];
    Map<String, DataAccess.Variable<Object>> idents = new TreeMap<String, DataAccess.Variable<Object>>();
    Map<String, DataAccess.Variable<Object>> args = new TreeMap<String, DataAccess.Variable<Object>>();
    //Map<String, DataAccess.Variable<Map<String, DataAccess.Variable<Object>>>> args = new TreeMap<String, DataAccess.Variable<Map<String, DataAccess.Variable<Object>>>>();
    for(int iTable = 0; iTable < this.wdgTables.length; ++iTable) {
      GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line = this.wdgTables[iTable].getCurrentLine();
      if(line !=null) {
        Map<String, DataAccess.Variable<Object>> data = line.getUserData();
        String argName = "line" + (iTable+1);
        args.put(argName, new DataAccess.Variable<Object>('V', argName, data));
        identifier[iTable] = data.get("name").value().toString();
        String key = "key" + (iTable+1); 
        idents.put(key, new DataAccess.Variable<Object>('S', key, identifier[iTable])); 
    } }
    try{
      Appendable out = this.output;
      if(subroutine !=null) {
        this.executer.execSub(null, subroutine, args, false, out, null);
      }
      else if(this.script.getSubroutine("genStimuli") !=null) {
        this.executer.execSub(null, "genStimuli", idents, false, out, null);
      }
      else if(this.script.getSubroutine("execSelection") !=null) {
        this.executer.execSub(null, "execSelection", args, false, out, null);
      }
      else {
        System.out.println("subRoutine not found, either:\n");
        System.out.println("sub genStimuli((String key1, String key2, String key3, String key4, String key5, String key6){ ...\n");
        System.out.println("or:\n");
        System.out.println("sub execSelection(Map line1){ ...\n");
      }
    } catch(ScriptException exc) {
      System.err.println(exc.getMessage());
    }
  }
  
  
  
  /**Routine called from the button [gen test cases].
   * It searches and calls the JZtxtcmd sub btnGenTestCases(select)
   * with a JZtxtcmd variable as argument with the select string.
   */
  void genTestcases ( ) {
    //String[] identifier = new String[wdgTables.length];
    Map<String, DataAccess.Variable<Object>> args = new TreeMap<String, DataAccess.Variable<Object>>();
    args.put("select", new DataAccess.Variable<Object>('S', "select", this.wdgSelects.getText())); 
    try{
      Appendable out = this.output;
      this.executer.execSub(null, "btnGenTestcases", args, false, out, null);
    } catch(ScriptException exc) {
      System.err.println(exc.getMessage());
    }
  }
  

  
  
  /**routine for Button [add sel]
   * It analyzes the given content in the {@link #wdgSelects} text box and marked lines in the tables
   * and produces the proper part of the selection expression.
   * Calls {@link #addTestCaseAllTables(StringBuilder, int)} for an empty part in the expression
   * or calls {@link #prcTablesInSelectionPart(StringBuilder, int, int, org.vishia.gral.base.GralTable.TableLineData, boolean)}
   * if some info are contained already. 
   * <br>
   * A part in the select string is the text area from start or from : + & to end or to one of this operators.
   */
  void addTestcases ( ) {
    int nCursor = this.wdgSelects.getCursorPos();
    String textSelect = this.wdgSelects.getText();
    int zTextSelect = textSelect.length();
    StringBuilder sbselect = new StringBuilder(textSelect);
    char cc = 0;
    boolean bAddAllTable = false;
    if(nCursor >= zTextSelect) { nCursor -=1; } //start on last character
    while(nCursor >=0 && " \n\t\r".indexOf(cc = textSelect.charAt(nCursor))>=0) { nCursor -=1; }  //lands on -1 or last char before cursor 
    nCursor +=1;
    if(nCursor ==0 || ":&+".indexOf(cc)>=0) {
      int nCursor1 = nCursor;
      while(nCursor1 < zTextSelect && " \n\t\r".indexOf(cc = textSelect.charAt(nCursor1))>=0) { nCursor1 +=1; } 
      if(nCursor1 > nCursor) {
      //  sbselect.delete(nCursor, nCursor1);  //remove white spaces from : till current
      }
      bAddAllTable = (nCursor1 == sbselect.length() || ":&+".indexOf(cc)>=0);
    }
    if(bAddAllTable) {
      nCursor = addTestCaseAllTables(sbselect, nCursor);
    }
    else {
      nCursor = prcTablesInSelectionPart(sbselect, nCursor, -1, null, false);
    }
    this.wdgSelects.setText(sbselect);
    this.wdgSelects.setCursorPos(nCursor);

  }

  
  
  
  
  /**Processes the selected lines of all tables but only in the found part of the selection expression.
   * It searches which tables are contained in that part and processes only content from this tables.
   * But for bMarkLinesInTable = false: If the table is given (nTable >0), 
   * this table content is added in this part as a new table too.
   * <br>
   * This routine has 2 different functions:
   * <ul><li> use marked lines or the current line for selection: bMarkLinesInTable==false.
   * <li>Mark the lines which are contained in the part of selection expression: bMarkLinesInTable==true.
   * <ul>
   * Both algorithm are widely similar, hence done in this routine and distinct by bMarkLinesInTable.
   *     
   * @param sbselect contains the content of the select textbox
   * @param nCursor position of the cursor there
   * @param nTable if given (>=0) then only this table is handled (because of double clicked of its line).
   *               if -1, then check any table of selection
   * @param lineSel only used if nTable >=0, then this line.
   * @param bMarkLinesInTable true then the found keys marks the lines in the table (other direction).
   * @return new cursor position
   */
  int prcTablesInSelectionPart ( StringBuilder sbselect, int nCursor
      , int nTable, GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData lineSel
      , boolean bMarkLinesInTable) {
    //                                                     // search backward which table
    int zText0 = sbselect.length();
    int nTable1 = -1;
    char cc = '\0';
    int nCursor1 = nCursor-1;                              // back to the section part start from : + %
    while(nCursor1 >=0 && ":+&".indexOf(cc = sbselect.charAt(nCursor1))<0) { nCursor1 -=1; }  
    nCursor1 +=1;
    //cursor @0 or @:
    //now search table:
    boolean bTableFound = false;                           // forward to one table entries start on =
    while(!bTableFound && nCursor1 < zText0 && ":+&".indexOf(cc = sbselect.charAt(nCursor1++)) <0) {
      if(cc == '=') {
        int nCursor2 = nCursor1;  //after =
        nCursor1 -=2;  //at char before = from after =
        while(nCursor1 >0 && (cc = sbselect.charAt(nCursor1)) ==' ') { nCursor1 -=1; }
        while(nCursor1 >=0 && (cc = sbselect.charAt(nCursor1)) >='0' && cc <= '9') { 
          nCursor1 -=1;   //back to start of nr
        }
        nCursor1 +=1;  //to the number, or from -1 to 0
        nTable1 = StringFunctions_C.parseIntRadix(sbselect, nCursor1, 2, 10, null);
        nCursor1 = nCursor2;  //after = to search next table entry
        //                                                 // A table was detected.
        if(nTable <0 || nTable == nTable1) {
          Map<String, String> selInTable = new TreeMap<String, String>();    //skip over
          do {                                               // gather all existing entries for this segment
            while(nCursor1 >0 && " ,".indexOf(cc = sbselect.charAt(nCursor1))>=0) { nCursor1 +=1; }
            int nCursor0 = nCursor1;                           //read identifiert
            while(nCursor1 >0 && Character.isJavaIdentifierPart(cc = sbselect.charAt(nCursor1))) { nCursor1 +=1; }
            String selKey = sbselect.substring(nCursor0, nCursor1);
            selInTable.put(selKey, selKey);                  // gather all already existing select keys for this table
          } while(";&:+".indexOf(cc) <0);                    // till the end of this section part
          
          GralTable<Map<String, DataAccess.Variable<Object>>> table = this.wdgTables[nTable1-1];
          if(table !=null) {
            boolean bSomeMarked = false;
            for(GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line: table.iterLines()) {
              String keyLine = getKeyfromLine(line);
              int mark = line.getMark();
              if(bMarkLinesInTable) {                      // set marked if key is contains in select expression part
                if(selInTable.get(keyLine) != null) {
                  if((mark & 1)==0) { 
                    line.setMarked(1, null); bSomeMarked = true; 
                  }
                } else {
                  if((mark & 1)!=0) { 
                    line.setNonMarked(1, null); bSomeMarked = true;
                  }
                }
              } else {                                     // get marked line as key
                if( (mark & 1)!=0) {   //line is marked (red)
                  bSomeMarked = true;
                  nCursor1 = addLineToSegm(sbselect, nCursor1, line, selInTable);
                }
              }
            }
            if(bSomeMarked && bMarkLinesInTable) {
              table.repaint();
            }
            else if(!bSomeMarked && !bMarkLinesInTable) {
              GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line = table.getCurrentLine();
              nCursor1 = addLineToSegm(sbselect, nCursor1, line, selInTable);
            }
          }
        }
      }
    }
    return nCursor1;
  }
  
  
  /**Reads the key from the line of the table, internal.
   * @param line
   * @return The key in the line, often field "name"
   */
  private String getKeyfromLine ( GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line) {
    Map<String, DataAccess.Variable<Object>> lineData = line.getData();
    DataAccess.Variable<Object> lineContent = lineData.get("name");
    assert(lineContent.type() == 'S');      //contains a String
    String sel = (String)lineContent.value();
    int posSpace = sel.indexOf(' ');
    if(posSpace <0) { posSpace = sel.length(); }
    return sel;
  }
  
  
  
  
  /**Adds the key of the given line in this part of selection string
   * but only if it is not contained. 
   * @param sbselect contains the select expression, will be changed
   * @param nCursor cursor position where exactly should be inserted.
   * @param line
   * @param alreadyContained Map of all found keys (contained in the part of the select expression)
   * @return The new cursor position proper for a next entry.
   */
  int addLineToSegm ( StringBuilder sbselect, int nCursor
      , GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line
      , Map<String, String> alreadyContained
      ) {
    int nCursorNew = nCursor;
    String key = getKeyfromLine(line);
    if(alreadyContained.get(key) ==null) {
      nCursorNew = insertTestCase(sbselect, nCursor, false, 0, line);
      alreadyContained.put(key, key);
    }
    return nCursorNew;
  }
  
  
  
  

  /**Adds the selection from all tables as initial string, called if the selection (part) is empty. 
   * This routine is called from {@link #addTestcases()} if there is detected that the part is empty.
   * If some lines are marked, only this lines are used. 
   * If no mark is given, the current lines of all tables are added.
   * @param sbselect contains the select expression, will be changed
   * @param nCursor cursor position where exactly should be inserted. It is 0 if the field is empty,
   * or it can be after a ':' character.
   */
  int addTestCaseAllTables ( StringBuilder sbselect, int nCursor) {
    int nCursor1 = nCursor;
    boolean bFoundMarked = false;
    for(int ixTable = 0; ixTable < this.wdgTables.length; ++ixTable) {
      GralTable<Map<String, DataAccess.Variable<Object>>> table = this.wdgTables[ixTable];
      if(table !=null) {
        boolean bFirstInTable = true;
        for(GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line: table.iterLines()) {
          String keyLine = getKeyfromLine(line);
          int mark = line.getMark();
          if((mark & 1)!=0) {                              // add marked line
            bFoundMarked = true;
            nCursor1 = insertTestCase(sbselect, nCursor1, bFirstInTable, ixTable+1, line);
            bFirstInTable = false;
          }
        }
        if(!bFirstInTable) { //only if somewhat inserted:
          sbselect.insert(nCursor1, "; ");                 // close the entry of a table with ;
          nCursor1 += 2;
        }
      }
    }
    if(!bFoundMarked) {                                    // add all current lines
      for(int ixTable = 0; ixTable < this.wdgTables.length; ++ixTable) {
        GralTable<Map<String, DataAccess.Variable<Object>>> table = this.wdgTables[ixTable];
        if(table !=null) {
          GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line = table.getCurrentLine();
          if(line !=null) {
            nCursor1 = insertTestCase(sbselect, nCursor1, true, ixTable+1, line);
            sbselect.insert(nCursor1, "; ");                   // close the entry of a table with ;
            nCursor1 += 2;
          }
        }
      }
    }
    return nCursor1;
  }
  
  
  
  
  /**Add the entry for one line of a table exactly on the cursor position.
   * @param sbselect The StringBuffer contains the current content, will be changed
   * @param nCursor position where to insert
   * @param bNewTableEntry true then write "<&nTable>=" for a new table, false then write ", "
   * @param nTable The table number counted from 1 for the first.
   * @param line The line of table, get key from there
   */
  int insertTestCase ( StringBuilder sbselect, int nCursor, boolean bNewTableEntry
      , int nTable, GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line ) 
  //throws IOException 
  { int nCursor1 = nCursor;
    int sblen0 = sbselect.length();
    if(bNewTableEntry) {
      sbselect.insert(nCursor1, Integer.toString(nTable)+"=");
    } else {
      sbselect.insert(nCursor1, ", ");
    }
    int sblen = sbselect.length();
    nCursor1 += sblen - sblen0;
    Map<String, DataAccess.Variable<Object>> lineData = line.getData();
    DataAccess.Variable<Object> lineContent = lineData.get("name");
    assert(lineContent.type() == 'S');      //contains a String
    String sel = (String)lineContent.value();
    int posSpace = sel.indexOf(' ');
    if(posSpace <0) { posSpace = sel.length(); }
    //select.append(sep)
    sblen0 = sblen;
    sbselect.insert(nCursor1, sel.substring(0, posSpace));
    sblen = sbselect.length();
    nCursor1 += sblen - sblen0;
    return nCursor1;
  }
  
  
  
  
  
  /**Double click on one line
   * @param line
   */
  void addTestCaseFromTable ( GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line ) {
    Object o = line.getTable().getData();
    assert(o instanceof Integer);
    int nTable = ((Integer)o).intValue();
    
    int nCursor = this.wdgSelects.getCursorPos();
    String textSelect = this.wdgSelects.getText();
    StringBuilder sbselect = new StringBuilder(textSelect);
    nCursor = prcTablesInSelectionPart(sbselect, nCursor, nTable, line, false);
    this.wdgSelects.setText(sbselect);
    this.wdgSelects.setCursorPos(nCursor);
 
  }

  
  
  
  /**Routine for the button [desel] remove the mark in all table lines. 
   * 
   */
  void deselectLines ( ) {
    for(int ixTable = 0; ixTable < this.wdgTables.length; ++ixTable) {
      boolean bSomeDone = false;
      for(GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line: this.wdgTables[ixTable].iterLines()) {
        int mark = line.getMark();
        if( (mark & 1)!=0) {                  //line is marked (red)
          bSomeDone = true;
          line.setNonMarked(0xFFFF, null);
        }
      }
      if(bSomeDone) {
        this.wdgTables[ixTable].repaint();
      }
    }
  }  
  
  
  
  /**Routine for button [show] shows the lines which are contained in the current part (cursor)
   * of the selection expression.
   * 
   */
  void showSelectExpressionPart ( ) {
    int nCursor = this.wdgSelects.getCursorPos();
    String textSelect = this.wdgSelects.getText();
    StringBuilder sbselect = new StringBuilder(textSelect);
    prcTablesInSelectionPart(sbselect, nCursor, -1, null, true);
  }
  
  
  


  /**Action for the button [read config] calls {@link #readConfig()}
   * 
   */
  GralUserAction actionReadConfig = new GralUserAction("readConfig")
  { @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        readConfig();
      }
      return true;
    }
  };
  
  
  
  /**An user action which calls {@link StimuliSelector#execBtnAction(String)}, assigned to some Buttons.
   * The name in the constructor determines the name of the called subroutine in the JZtxtcmd script.
   * @author hartmut
   *
   */
  class GralUserActionButton extends GralUserAction {
    
    /**Creates
     * @param name it is the name of the subroutine in the JZtxtcmd script which will be called.
     */
    GralUserActionButton(String name){ super(name); }
    
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        execBtnAction(this.name);
      }
      return true;
    }
  }
  
  
  
  
  /**Action for the button [gen test cases] invokes {@link StimuliSelector#genTestcases()} 
   * 
   */
  GralUserAction actionGenTestcases = new GralUserAction("genTestcases")
  { @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        genTestcases();
      }
      return true;
    }
  };
  
  

  /**Action for the button [add sel] invokes {@link StimuliSelector#addTestcases()} 
   * 
   */
  GralUserAction actionAddTestcases = new GralUserAction("addTestcases")
  { @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        try { 
          addTestcases();
        } catch(Exception exc) {
          System.out.println(exc.getMessage());
        }
      }
      return true;
    }
  };
  
  
  /**Action for the button [desel] invokes {@link StimuliSelector#deselectLines()()} 
   * 
   */
  GralUserAction actionDeselectLines = new GralUserAction("actionDeselectLines")
  { @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        try { 
          deselectLines();
        } catch(Exception exc) {
          System.out.println(exc.getMessage());
        }
      }
      return true;
    }
  };
  
  
  /**Action for the button [clean] removes the text in the {@link #output} text box. 
   * 
   */
  GralUserAction actionCleanOut = new GralUserAction("cleanOut")
  { @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        StimuliSelector.this.output.setText("");
      }
      return true;
    }
  };
  
  
  
  
  /**Action for a double click on a line invokes {@link StimuliSelector#addTestCaseFromTable(org.vishia.gral.base.GralTable.TableLineData)} 
   * 
   */
  GralUserAction actionTouchLine = new GralUserAction("touchLine")
  { @Override public boolean exec ( int actionCode, GralWidget_ifc widgd, Object... params) { 
      if( actionCode == KeyCode.mouse1Double){
        assert(params[0] instanceof GralTable.TableLineData);
        @SuppressWarnings("unchecked")
        GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line = 
          (GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData)params[0];  //it is the table line.
        addTestCaseFromTable(line);
      }
      else if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode) ) {
        
      }
      return true;
    }
  };
  
  
  /**This action is only to remark which tables is last touched (line is selected).
   */
  GralUserAction actionSelectInTable = new GralUserAction("selectInTable")
  { @Override public boolean exec ( int actionCode, GralWidget_ifc widgd, Object... params) { 
      assert(widgd instanceof GralTable);
      @SuppressWarnings("unchecked")
      GralTable<Map<String, Variable<Object>>> gralTable = (GralTable<Map<String, DataAccess.Variable<Object>>>) widgd;
      StimuliSelector.this.wdgLastSelectedTable = gralTable;
      return true;
    }
  };
  
  
  
  /**Action is invoked if the text box was touched or changed, yet not used.
   * 
   */
  GralUserAction actionTouchTestcaseString = new GralUserAction("touchTestcaseString")
  { @Override public boolean exec ( int actionCode, GralWidget_ifc widgd, Object... params) { 
      //System.out.println(Integer.toHexString(actionCode));
      if(KeyCode.isWritingKey(actionCode)) {
        //StimuliSelector.this.nTableTestcase = -1;    //set -1 in any case if this field is touched.
      }
      return true;
    }
  };
  
  
  
  
  /**Action for the button [Show] calls {@link #showSelectExpressionPart()} 
   * 
   */
  GralUserAction actionShowSel = new GralUserAction("actionShowSel")
  { @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        showSelectExpressionPart();
      }
      return true;
    }
  };
  
  
  
  
  /**Action for the button [help] yet improveable.
   * 
   */
  GralUserAction actionHelp = new GralUserAction("help")
  { @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        StimuliSelector.this.output.setText("");
        try {
          StimuliSelector.this.output.append("help...\n2. line\n");
        } catch (IOException e) {}
      }
      return true;
    }
  };
  
  
  
  
  
  
  
  /**The {@link GralGraphicTimeOrder#awaitExecution(int, int)} implemented here is invoked after creation.
   * It builds the content of the main window called in the Graphic thread of the {@link GralMng}
   * This routine determines the position of all widgets using {@link GralMng#setPosition(float, float, float, float, int, char)}
   */
  GralGraphicTimeOrder initGraphic = new GralGraphicTimeOrder("GralArea9Window.initGraphic", this.gralMng) {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override public void executeOrder()
    { 
      // gralMng.selectPanel(window);
//      StimuliSelector.this.gralMng.setPosition(2, 5, 1, 12, 0, 'r', 1);
//      StimuliSelector.this.btnCleanOut.createImplWidget_Gthread();
//      StimuliSelector.this.btnReadConfig.createImplWidget_Gthread();
//      
//      StimuliSelector.this.gralMng.setPosition(2, 5, 27, 39, 0, 'r', 1);
//      StimuliSelector.this.btnGenTestcases.createImplWidget_Gthread();
//      
//      StimuliSelector.this.gralMng.setPosition(6, 9, 1, 12, 0, 'r', 1);
//      StimuliSelector.this.btnGenSelection.createImplWidget_Gthread();
//      for(GralButton execBtn : StimuliSelector.this.btnExecSelection) {
//        if(execBtn !=null) {
//          execBtn.createImplWidget_Gthread();
//        }
//      }
//      StimuliSelector.this.gralMng.setPosition(2, 10, 40, 104, 0, 'd');
//      StimuliSelector.this.wdgSelects = StimuliSelector.this.gralMng.addTextBox("test", true, null, 'r');
//      StimuliSelector.this.wdgSelects.setText("");
//      StimuliSelector.this.wdgSelects.specifyActionChange("actionTouchTestCaseString", StimuliSelector.this.actionTouchTestcaseString, null);
//      StimuliSelector.this.gralMng.setPosition(2, 5, 105, 112, 0, 'r');
//      StimuliSelector.this.btnAddTestcase.createImplWidget_Gthread();
//      StimuliSelector.this.btnHelp.createImplWidget_Gthread();
//      StimuliSelector.this.gralMng.setPosition(6, 9, 105, 112, 0, 'r');
//      StimuliSelector.this.btnExampleSel.createImplWidget_Gthread();
//      StimuliSelector.this.btnDeselectLines.createImplWidget_Gthread();
//      //int last = 1; //tables.length
//      for(int iTable = 0; iTable < StimuliSelector.this.wdgTables.length; ++iTable) {
//        int xtable = iTable %3;
//        int ytable = iTable /3;
//        StimuliSelector.this.gralMng.setPosition(21*ytable + 10, 21*ytable + 30, xtable * 40, xtable * 40 +40, 0, 'd');
//        
//        StimuliSelector.this.wdgTables[iTable].createImplWidget_Gthread();
//        StimuliSelector.this.wdgTables[iTable]._wdgImpl.repaintGthread();
//        StimuliSelector.this.wdgTables[iTable].addContextMenuEntryGthread(1, "test", "add to select rule", StimuliSelector.this.actionTouchLine);
//      }
//      StimuliSelector.this.gralMng.setPosition(52, 0, 0, 0, 0, 'U');
//      StimuliSelector.this.output.createImplWidget_Gthread();
//      
//      StimuliSelector.this.outOld = System.out; StimuliSelector.this.errOld = System.err;
//      System.setOut(StimuliSelector.this.outNew = new PrintStreamAdapter("", StimuliSelector.this.output));
//      System.setErr(StimuliSelector.this.errNew = new PrintStreamAdapter("", StimuliSelector.this.output));
//      StimuliSelector.this.isTableInitialized = true;
//      //
//      //GralTextField input = new GralTextField();
//      StimuliSelector.this.gralMng.createHtmlInfoBoxes(null);
//      String sHelpdir = StimuliSelector.this.fileConfig.getAbsoluteFile().getParent() + "/";
//      StimuliSelector.this.gralMng.setHelpBase(sHelpdir);
//      StimuliSelector.this.gralMng.setHelpUrl("+StimuliSelector_help.html");
//      StimuliSelector.this.btnHelp.specifyActionChange("help", StimuliSelector.this.gralMng.actionHelp, null);
      
    }
    
    
    
  };
}


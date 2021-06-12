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
import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.swt.SwtFactory;
import org.vishia.jztxtcmd.JZtxtcmd;
import org.vishia.mainCmd.PrintStreamAdapter;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.DataAccess;
import org.vishia.util.DataAccess.Variable;
import org.vishia.util.Debugutil;
import org.vishia.util.KeyCode;

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
 * sub execSelection(Map line1, Map line2, Map line3, Map line4, Map line5, Map line6){
 *   <+out>execSelection <&line1.descr> <&line2.var> 
 *     ... here some statements to create any output to use anywhere other from the selection.
 *   <.+>
 * }
 * 
 * sub exec1(String button="Exec", Map line1, Map line2, Map line3, Map line4, Map line5, Map line6){
 *   cmd cmd.exe /C start sh.exe -c build/testAllBase.sh;  ##example starts a execution with a [Exec] button.
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
  
  
  GralMng gralMng;
  
  GralWindow window;
  
  @SuppressWarnings("unchecked") 
  GralTable<Map<String, DataAccess.Variable<Object>>>[] wdgTables = new GralTable[6];
  
  /**Set on selection of a line in a table, the last touched one. */
  volatile GralTable<Map<String, DataAccess.Variable<Object>>> wdgLastSelectedTable;
  
  GralButton btnReadConfig, btnGenSelection, btnCleanOut, btnGenTestcase;
  GralButton btnAddTestcase, btnDeselectLines, btnExampleSel, btnHelp;
  
  GralButton[] btnExecSelection = new GralButton[4];
  
  GralTextBox wdgSelects;
  
  GralTextBox output;
  
  
  final JZtxtcmd jzcmd;
  
  JZtxtcmdExecuter executer = new JZtxtcmdExecuter();
    
  JZtxtcmdScript script;
  
  boolean isTableInitialized;
  
  final File fileConfig;
  
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
  
  
  StimuliSelector(String fileConfig)
  {
    this.fileConfig = new File(fileConfig);
    JZtxtcmd jzcmd = null;
    try {
      jzcmd = new JZtxtcmd();
      this.script = jzcmd.compile(this.fileConfig, null);
    } catch( ScriptException exc) {
      System.err.println("ERROR unexpected" + exc.getMessage());
    }
    this.jzcmd = jzcmd;
    
    for(int iTable = 0; iTable < this.wdgTables.length; ++iTable) {
      //String pos = "@PrimaryWindow, 6..40, " + 20 * iTable + ".." + (18 + 20 * iTable);
      String name = "table" + iTable;
      int[] columnWidths = new int[2];
      columnWidths[0] = 15;
      columnWidths[1] = 0;
      this.wdgTables[iTable] = new GralTable<>(name, columnWidths);
      this.wdgTables[iTable].specifyActionChange("actionTouchLine", this.actionTouchLine, null);
      this.wdgTables[iTable].specifyActionOnLineSelected(this.actionSelectInTable);
      this.wdgTables[iTable].setData(new Integer(iTable +1));
      //, GralWidget_ifc.ActionChangeWhen.onCtrlEnter, GralWidget_ifc.ActionChangeWhen.onMouse1Double);
      //this.wdgTables[iTable].specifyContextMenu(null);
    }
    this.wdgLastSelectedTable = this.wdgTables[0]; //default
    this.btnReadConfig = new GralButton("readConfig", "read config", this.actionReadConfig);
    this.btnGenSelection = new GralButton("genSelection", "gen selection", new GralUserActionButton("btnGenSelection"));
    this.btnGenTestcase = new GralButton("genTestCase", "gen test cases", this.actionGenTestcases);
    this.btnAddTestcase = new GralButton("addTestCase", "add sel", this.actionAddTestcases);
    this.btnDeselectLines = new GralButton("addTestCase", "desel", this.actionDeselectLines);
    this.btnCleanOut = new GralButton("cleanOut", "clean output", this.actionCleanOut);
    this.btnExampleSel = new GralButton("exmpl", "exmpl", this.actionExampleSel);
    this.btnHelp = new GralButton("help", "help", this.actionHelp);
    //
    JZtxtcmdScript.Subroutine sub1 = this.script.getSubroutine("btnExec1");
    if(sub1 !=null) {
      String btnText = sub1.formalArgs.get(0).textArg;
      if(btnText == null) { btnText = sub1.name; }
      this.btnExecSelection[0] = new GralButton("btnExec11", btnText, new GralUserActionButton("btnExec1"));
    }
    JZtxtcmdScript.Subroutine sub2 = this.script.getSubroutine("btnExec2");
    if(sub2 !=null) {
      String btnText = sub2.formalArgs.get(0).textArg;
      if(btnText == null) { btnText = sub2.name; }
      this.btnExecSelection[1] = new GralButton("btnExec12", btnText, new GralUserActionButton("btnExec2"));
    }
    JZtxtcmdScript.Subroutine sub3 = this.script.getSubroutine("btnExec3");
    if(sub3 !=null) {
      String btnText = sub3.formalArgs.get(0).textArg;
      if(btnText == null) { btnText = sub3.name; }
      this.btnExecSelection[2] = new GralButton("btnExec13", btnText, new GralUserActionButton("btnExec3"));
    }
    JZtxtcmdScript.Subroutine sub4 = this.script.getSubroutine("btnExec4");
    if(sub4 !=null) {
      String btnText = sub4.formalArgs.get(0).textArg;
      if(btnText == null) { btnText = sub4.name; }
      this.btnExecSelection[3] = new GralButton("btnExec4", btnText, new GralUserActionButton("btnExec4"));
    }
    
    
    this.output = new GralTextBox("output");
  }
  
  
  
  public static void openWindow(){
    StimuliSelector main = new StimuliSelector(null);
    main.openWindow1('D');
  }
  
  
  public void waitForClosePrimaryWindow()
  {
    while(GralMng.get().gralDevice.isRunning()){
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
  
  
  
  
  void readConfig()
  {
    JZtxtcmdExecuter.ExecuteLevel level = null;
    try {
      this.script = this.jzcmd.compile(this.fileConfig, null);
      this.executer.initialize(this.script, false, null);
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
  
  
  
  
  /**This is the common routine for all buttons which does anything with the selection.
   * It is used also for gen Stimuli and the exec buttons. 
   * @param subroutine
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
  
  
  void genTestcases ( ) {
    //String[] identifier = new String[wdgTables.length];
    Map<String, DataAccess.Variable<Object>> args = new TreeMap<String, DataAccess.Variable<Object>>();
    args.put("select", new DataAccess.Variable<Object>('S', "select", this.wdgSelects.getText())); 
    try{
      Appendable out = this.output;
      this.executer.execSub(null, "genTestcases", args, false, out, null);
    } catch(ScriptException exc) {
      System.err.println(exc.getMessage());
    }
  }
  

  
  
  void addTestcases ( ) {
    StringBuilder select = new StringBuilder();
    String sepSpace = "";
    boolean bFoundMark = false;
    for(int ixTable = 0; ixTable < this.wdgTables.length; ++ixTable) {
      String sep = sepSpace + Integer.toString(ixTable+1) + "=";
      boolean bAdd = false;
      for(GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line: this.wdgTables[ixTable].iterLines()) {
        int mark = line.getMark();
        if( (mark & 1)!=0) {                  //line is marked (red)
          if(!bFoundMark) {                   //first time found a line
            this.nTableTestcase = -1;         //decides about syntax in addTestCase(..)
            
          }
          addTestCase(select, line, sep, ixTable +1);
          bAdd = true;
          bFoundMark = true;
          sep = ",";
        }
        Debugutil.stop();
      }
      if(bAdd) { select.append(';'); }
      sepSpace = " ";
    }
    if(bFoundMark==false) { //nothing is marked, it means the user press this key without preparing mark.
      //Then add the last selected line in the last selected table as default behavior to do anything what's obviously.
      GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line = this.wdgLastSelectedTable.getCurrentLine();
      if(line == null) { 
        this.wdgSelects.setText("first select a line in table, or press [exmpl]\nremove this content with mark and delete in this textbox");
      }
      else { addTestCaseFromTable(line); }
    }
    else {
      String selWdg = this.wdgSelects.getText();
      int selWdgPos = selWdg.indexOf('|');
      int selWdgPos2;
      if(selWdgPos >=0) {
        selWdgPos2 = selWdgPos +1;
        while( selWdgPos2 < selWdg.length() && selWdg.charAt(selWdgPos2) == '|') { selWdgPos2 +=1; }
      } else {
        selWdgPos = this.wdgSelects.getCursorPos();
        selWdgPos2 = selWdgPos;
      }
      selWdg = selWdg.substring(0, selWdgPos) + select + selWdg.substring(selWdgPos2);
      this.wdgSelects.setText(selWdg);
      this.wdgSelects.setCursorPos(selWdgPos + select.length()); 
    }
  }  

  
  int nTableTestcase = -1;
  
  String sTextTestCases = "";
  
  int nPosCursorTextCases = 0;
  
  void addTestCase ( StringBuilder select, GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line, String sepXXX, int nTable ) 
  //throws IOException 
  {
    if(this.nTableTestcase != nTable) {
      if(this.nTableTestcase != -1) {           //a following table
        select.append("; ");
      }
      select.append(Integer.toString(nTable)).append("=");
    } else {
      select.append(", ");
    }
    this.nTableTestcase = nTable;
    Map<String, DataAccess.Variable<Object>> lineData = line.getData();
    DataAccess.Variable<Object> lineContent = lineData.get("name");
    assert(lineContent.type() == 'S');      //contains a String
    String sel = (String)lineContent.value();
    int posSpace = sel.indexOf(' ');
    if(posSpace <0) { posSpace = sel.length(); }
    //select.append(sep)
    select.append(sel.substring(0, posSpace));
  }
  
  
  void addTestCaseFromTable ( GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line ) {
    Object o = line.getTable().getData();
    assert(o instanceof Integer);
    int nTable = ((Integer)o).intValue();
    int nCursor = this.wdgSelects.getCursorPos();
    String text0 = this.wdgSelects.getText();
    if(nCursor != this.nPosCursorTextCases || !text0.equals(this.sTextTestCases)) {
      this.nTableTestcase = -1; //anything is changed, it starts with new table. 
    }
    StringBuilder sCase = new StringBuilder(text0.substring(0, nCursor));
    //
    addTestCase(sCase, line, null, nTable);
    //
    int nCursorNew = sCase.length();
    sCase.append(text0.substring(nCursor));
    this.wdgSelects.setText(sCase);
    this.wdgSelects.setCursorPos(nCursorNew);
    this.nPosCursorTextCases = nCursorNew; this.sTextTestCases = sCase.toString();
     
  }

  
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
  
  
  void prepareExampleSel() {
    StringBuilder select = new StringBuilder();
    String sepSpace = "";
    boolean bAddTable = false;
    for(int ixTable = 0; ixTable <=1; ++ixTable) {
      String sep = sepSpace + Integer.toString(ixTable+1) + "=";
      boolean bAdd = false;
      int nrLine = 0;
      int nrLines = this.wdgTables[ixTable].size();
      int nrEndLine = nrLines >2? 2 :1;
      this.nTableTestcase = -1;         //decides about syntax in addTestCase(..)
      for(GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line: this.wdgTables[ixTable].iterLines()) {
        if(++nrLine > nrEndLine) break;
        addTestCase(select, line, sep, ixTable +1);
        sep = ","; bAdd = true; bAddTable = true;
      }
      select.append("; ");
      //if(bAdd) { select.append(';'); }
      sepSpace = " ";
    }
    //                                // add selection + second combination
    this.nTableTestcase = -1;         //decides about syntax in addTestCase(..)
    boolean bAddSel2 = false;
    for(int ixTable = 0; ixTable <=1; ++ixTable) {
      String sep = sepSpace + Integer.toString(ixTable+1) + "=";
      boolean bAdd = false;
      int nrLine = 0;
      int nrLines = this.wdgTables[ixTable].size();
      int nrUsedLine = nrLines >=2? nrLines -2 :1;
      
      this.nTableTestcase = -1;         //decides about syntax in addTestCase(..)
      for(GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line: this.wdgTables[ixTable].iterLines()) {
        if(++nrLine >nrUsedLine) {
          if(bAddTable && !bAddSel2) {
            select.append(" + ");    //additional combination
          }
          addTestCase(select, line, sep, ixTable +1);
          sep = ","; bAdd = true; bAddTable = true; bAddSel2 = true;
        }
      }
      select.append("; ");
      //if(bAdd) { select.append(';'); }
      sepSpace = " ";
    }
    if(bAddTable) {
      select.append("\n & ");    //with this combination
    }
    bAddTable = false;
    this.nTableTestcase = -1;         //decides about syntax in addTestCase(..)
    for(int ixTable = 2; ixTable <6; ++ixTable) {
      String sep = sepSpace + Integer.toString(ixTable+1) + "=";
      boolean bAdd = false;
      int nrLine = 0;
      for(GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line: this.wdgTables[ixTable].iterLines()) {
        if(++nrLine >2) break;
        addTestCase(select, line, sep, ixTable +1);
        sep = ","; bAdd = true;
      }
      sepSpace = " ";
    }
    this.wdgSelects.setText(select);
    this.wdgSelects.setCursorPos(select.length()); 
  }


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
  
  
  
  
  GralUserAction actionGenTestcases = new GralUserAction("genTestcases")
  { @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        genTestcases();
        StimuliSelector.this.nTableTestcase = -1;
      }
      return true;
    }
  };
  
  
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
  
  
  GralUserAction actionCleanOut = new GralUserAction("cleanOut")
  { @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        StimuliSelector.this.output.setText("");
      }
      return true;
    }
  };
  
  
  
  
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
  
  
  
  GralUserAction actionTouchTestcaseString = new GralUserAction("touchTestcaseString")
  { @Override public boolean exec ( int actionCode, GralWidget_ifc widgd, Object... params) { 
      //System.out.println(Integer.toHexString(actionCode));
      if(KeyCode.isWritingKey(actionCode)) {
        //StimuliSelector.this.nTableTestcase = -1;    //set -1 in any case if this field is touched.
      }
      return true;
    }
  };
  
  
  
  
  /**Assembles an example for selection using the given tables. */
  GralUserAction actionExampleSel = new GralUserAction("actionExampleSel")
  { @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        prepareExampleSel();
      }
      return true;
    }
  };
  
  
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
  
  
  
  
  
  
  
  @SuppressWarnings("deprecation")
  private void openWindow1(char size){
    GralFactory gralFactory = new SwtFactory();
    LogMessage log = new LogMessageStream(System.out);
    this.window = gralFactory.createWindow(log, "Select Stimuli", size, 100, 50, 600, 400);
    this.gralMng = this.window.gralMng();
    this.gralMng.gralDevice.addDispatchOrder(this.initGraphic);
    //initGraphic.awaitExecution(1, 0);
    
  }
  
  
  GralGraphicTimeOrder initGraphic = new GralGraphicTimeOrder("GralArea9Window.initGraphic"){
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override public void executeOrder()
    { 
      // gralMng.selectPanel(window);
      StimuliSelector.this.gralMng.setPosition(2, 5, 1, 12, 0, 'r', 1);
      StimuliSelector.this.btnCleanOut.createImplWidget_Gthread();
      StimuliSelector.this.btnReadConfig.createImplWidget_Gthread();
      
      StimuliSelector.this.gralMng.setPosition(2, 5, 27, 39, 0, 'r', 1);
      StimuliSelector.this.btnGenTestcase.createImplWidget_Gthread();
      
      StimuliSelector.this.gralMng.setPosition(6, 9, 1, 12, 0, 'r', 1);
      StimuliSelector.this.btnGenSelection.createImplWidget_Gthread();
      for(GralButton execBtn : StimuliSelector.this.btnExecSelection) {
        if(execBtn !=null) {
          execBtn.createImplWidget_Gthread();
        }
      }
      StimuliSelector.this.gralMng.setPosition(2, 10, 40, 104, 0, 'd');
      StimuliSelector.this.wdgSelects = StimuliSelector.this.gralMng.addTextBox("test", true, null, 'r');
      StimuliSelector.this.wdgSelects.setText("");
      StimuliSelector.this.wdgSelects.specifyActionChange("actionTouchTestCaseString", StimuliSelector.this.actionTouchTestcaseString, null);
      StimuliSelector.this.gralMng.setPosition(2, 5, 105, 112, 0, 'r');
      StimuliSelector.this.btnAddTestcase.createImplWidget_Gthread();
      StimuliSelector.this.btnDeselectLines.createImplWidget_Gthread();
      StimuliSelector.this.gralMng.setPosition(6, 9, 105, 112, 0, 'r');
      StimuliSelector.this.btnExampleSel.createImplWidget_Gthread();
      StimuliSelector.this.btnHelp.createImplWidget_Gthread();
      //int last = 1; //tables.length
      for(int iTable = 0; iTable < StimuliSelector.this.wdgTables.length; ++iTable) {
        int xtable = iTable %3;
        int ytable = iTable /3;
        StimuliSelector.this.gralMng.setPosition(21*ytable + 10, 21*ytable + 30, xtable * 40, xtable * 40 +40, 0, 'd');
        
        StimuliSelector.this.wdgTables[iTable].createImplWidget_Gthread();
        StimuliSelector.this.wdgTables[iTable]._wdgImpl.repaintGthread();
        StimuliSelector.this.wdgTables[iTable].addContextMenuEntryGthread(1, "test", "add to select rule", StimuliSelector.this.actionTouchLine);
      }
      StimuliSelector.this.gralMng.setPosition(52, 0, 0, 0, 0, 'U');
      StimuliSelector.this.output.createImplWidget_Gthread();
      
      StimuliSelector.this.outOld = System.out; StimuliSelector.this.errOld = System.err;
      System.setOut(StimuliSelector.this.outNew = new PrintStreamAdapter("", StimuliSelector.this.output));
      System.setErr(StimuliSelector.this.errNew = new PrintStreamAdapter("", StimuliSelector.this.output));
      StimuliSelector.this.isTableInitialized = true;
      //
      //GralTextField input = new GralTextField();
    }
    
    
    
  };
}


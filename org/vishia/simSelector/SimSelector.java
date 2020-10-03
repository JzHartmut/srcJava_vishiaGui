package org.vishia.simSelector;

import java.io.File;
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
import org.vishia.gral.base.GralTextField;
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
import org.vishia.util.KeyCode;

/*Test with jzcmd: call jzcmd with this java file with its full path:
D:/vishia/Java/srcJava_vishiaGui/org/vishia/gral/test/SimSelector.java
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
 * [ { name="ident",   descr="...",     select="x", var="anyString", anyOtherVar="anyInfo" }
 * , { name="ident2",  descr="...",     select="x", var="xyz", anyOtherVar="anyInfo" }
 * ];
 * 
 * List tab2AnyIdent = ##content of tab2
 * [ { name="ident",   descr="...",     select="x", var="anyString", anyOtherVar="anyInfo" }
 * , { name="ident2",  descr="...",     select="x", var="xyz", anyOtherVar="anyInfo" }
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
public class SimSelector
{
  /**Version, history and license.
   * <ul>
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
  
  
  GralButton btnReadConfig, btnGenSim, btnCleanOut, btnGenTestcase;
  
  GralButton[] btnActions = new GralButton[4];
  
  GralTextField wdgSelects;
  
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
    SimSelector main = new SimSelector(args[0]);
    main.openWindow1();
    main.waitForClosePrimaryWindow();
  }
  
  
  SimSelector(String fileConfig)
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
      int[] columnWidths = new int[3];
      columnWidths[0] = 15;
      columnWidths[1] = 8;
      columnWidths[2] = 0;
      this.wdgTables[iTable] = new GralTable<>(name, columnWidths);
    }
    this.btnReadConfig = new GralButton("readConfig", "read config", this.actionReadConfig);
    this.btnGenSim = new GralButton("genSim", "gen stimuli", this.actionGenSim);
    this.btnGenTestcase = new GralButton("genTestCase", "gen testcases.m", this.actionGenTestcases);
    this.btnCleanOut = new GralButton("cleanOut", "clean output", this.actionCleanOut);
    //
    JZtxtcmdScript.Subroutine sub1 = this.script.getSubroutine("exec1");
    if(sub1 !=null) {
      String btnText = sub1.formalArgs.get(0).textArg;
      if(btnText == null) { btnText = sub1.name; }
      this.btnActions[0] = new GralButton("action1", btnText, this.action1);
    }
    JZtxtcmdScript.Subroutine sub2 = this.script.getSubroutine("exec2");
    if(sub2 !=null) {
      this.btnActions[1] = new GralButton("action2", sub1.name, this.action2);
    }
    JZtxtcmdScript.Subroutine sub3 = this.script.getSubroutine("exec3");
    if(sub3 !=null) {
      this.btnActions[2] = new GralButton("action3", sub1.name, this.action3);
    }
    JZtxtcmdScript.Subroutine sub4 = this.script.getSubroutine("exec4");
    if(sub4 !=null) {
      this.btnActions[3] = new GralButton("action4", sub1.name, this.action4);
    }
    
    
    this.output = new GralTextBox("output");
  }
  
  
  
  public static void openWindow(){
    SimSelector main = new SimSelector(null);
    main.openWindow1();
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
      this.script = jzcmd.compile(this.fileConfig, null);
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
              DataAccess.Variable<Object> selectv = set1.get("select");
              if(descrv !=null && namev !=null) {
                String[] lineTexts = new String[3];
                lineTexts[0] = namev.value().toString();
                lineTexts[1] = selectv.value().toString();
                lineTexts[2] = descrv.value().toString();
                this.wdgTables[iList].addLine(lineTexts[0], lineTexts, set1);
              }
            }
            System.out.println(listElement.toString());
          }
        }
        this.wdgTables[iList].repaint();
      }
    }
    
  }
  
  
  
  
  void genStimuli(String subroutine)
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
  
  
  void genTestcases()
  {
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
  
  
  
  
  GralUserAction actionReadConfig = new GralUserAction("readConfig")
  { @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        readConfig();
      }
      return true;
    }
  };
  
  
  GralUserAction actionGenSim = new GralUserAction("genStimuli")
  { @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        genStimuli(null);
      }
      return true;
    }
  };
  
  
  GralUserAction action1 = new GralUserAction("action1")
  { @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        genStimuli("exec1");
      }
      return true;
    }
  };
  
  
  GralUserAction action2 = new GralUserAction("action2")
  { @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        genStimuli("exec2");
      }
      return true;
    }
  };
  
  
  GralUserAction action3 = new GralUserAction("action3")
  { @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        genStimuli("exec3");
      }
      return true;
    }
  };
  
  
  GralUserAction action4 = new GralUserAction("action4")
  { @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        genStimuli("exec4");
      }
      return true;
    }
  };
  
  
  GralUserAction actionGenTestcases = new GralUserAction("genTestcases")
  { @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        genTestcases();
      }
      return true;
    }
  };
  
  
  GralUserAction actionCleanOut = new GralUserAction("genStimuli")
  { @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        SimSelector.this.output.setText("");
      }
      return true;
    }
  };
  
  
  
  
  @SuppressWarnings("deprecation")
  private void openWindow1(){
    GralFactory gralFactory = new SwtFactory();
    LogMessage log = new LogMessageStream(System.out);
    this.window = gralFactory.createWindow(log, "Select Simulation", 'C', 100, 50, 600, 400);
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
      SimSelector.this.gralMng.setPosition(2, 5, 2, 19, 0, 'r', 2);
      SimSelector.this.btnReadConfig.createImplWidget_Gthread();
      SimSelector.this.btnCleanOut.createImplWidget_Gthread();
      SimSelector.this.btnGenSim.createImplWidget_Gthread();
      SimSelector.this.btnGenTestcase.createImplWidget_Gthread();
      for(GralButton execBtn : SimSelector.this.btnActions) {
        if(execBtn !=null) {
          execBtn.createImplWidget_Gthread();
        }
      }
      SimSelector.this.gralMng.setPosition(6, 8, 60, 99, 0, 'd');
      SimSelector.this.wdgSelects = SimSelector.this.gralMng.addTextField("test", true, null, "r");
      SimSelector.this.wdgSelects.setText("t");
      //int last = 1; //tables.length
      for(int iTable = 0; iTable < SimSelector.this.wdgTables.length; ++iTable) {
        int xtable = iTable %3;
        int ytable = iTable /3;
        SimSelector.this.gralMng.setPosition(21*ytable + 10, 21*ytable + 30, xtable * 40, xtable * 40 +40, 0, 'd');
        
        SimSelector.this.wdgTables[iTable].createImplWidget_Gthread();
        SimSelector.this.wdgTables[iTable]._wdgImpl.repaintGthread();
      }
      SimSelector.this.gralMng.setPosition(52, 0, 0, 0, 0, 'U');
      SimSelector.this.output.createImplWidget_Gthread();
      
      SimSelector.this.outOld = System.out; SimSelector.this.errOld = System.err;
      System.setOut(SimSelector.this.outNew = new PrintStreamAdapter("", SimSelector.this.output));
      System.setErr(SimSelector.this.errNew = new PrintStreamAdapter("", SimSelector.this.output));
      SimSelector.this.isTableInitialized = true;
      //
      //GralTextField input = new GralTextField();
    }
    
    
    
  };
}


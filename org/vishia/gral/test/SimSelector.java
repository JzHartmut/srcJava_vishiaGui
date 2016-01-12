package org.vishia.gral.test;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.script.ScriptException;

import org.vishia.cmd.JZcmdExecuter;
import org.vishia.cmd.JZcmdScript;
import org.vishia.gral.awt.AwtFactory;
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
import org.vishia.mainCmd.PrintStreamAdapter;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.DataAccess;
import org.vishia.util.IndexMultiTable;
import org.vishia.util.KeyCode;
import org.vishia.zcmd.JZcmd;

/*Test with jzcmd: call jzcmd with this java file with its full path:
D:/vishia/Java/srcJava_vishiaGui/org/vishia/gral/test/SimSelector.java
==JZcmd==
java org.vishia.gral.test.SimSelector.main(null);                 
==endJZcmd==
 */


public class SimSelector
{
  GralMng gralMng;
  
  GralWindow window;
  
  @SuppressWarnings("unchecked") 
  GralTable<Map<String, DataAccess.Variable<Object>>>[] wdgTables = new GralTable[6];
  
  
  GralButton btnReadConfig, btnGenSim, btnCleanOut, btnGenTestcase;
  
  GralTextField wdgSelects;
  
  GralTextBox output;
  
  JZcmdExecuter executer = new JZcmdExecuter();
    
  boolean isTableInitialized;
  
  File fileConfig;
  
  public static void main(String[] args){
    if(args.length < 1) {
      System.err.println("parameter 1: path/to/config.jzcmd.bat");
    }
    SimSelector main = new SimSelector();
    main.fileConfig = new File(args[0]);
    main.openWindow1();
    main.waitForClosePrimaryWindow();
  }
  
  
  SimSelector()
  {
    for(int iTable = 0; iTable < wdgTables.length; ++iTable) {
      String pos = "@PrimaryWindow, 6..40, " + 20 * iTable + ".." + (18 + 20 * iTable);
      String name = "table" + iTable;
      int[] columnWidths = new int[3];
      columnWidths[0] = 8;
      columnWidths[1] = 8;
      columnWidths[2] = 0;
      wdgTables[iTable] = new GralTable<>(name, columnWidths);
    }
    btnReadConfig = new GralButton("readConfig", "read config", actionReadConfig);
    btnGenSim = new GralButton("genSim", "gen stimuli", actionGenSim);
    btnGenTestcase = new GralButton("genTestCase", "gen testcases.m", actionGenTestcases);
    btnCleanOut = new GralButton("cleanOut", "clean output", actionCleanOut);
    output = new GralTextBox("output");
  }
  
  
  
  public static void openWindow(){
    SimSelector main = new SimSelector();
    main.openWindow1();
  }
  
  
  public void waitForClosePrimaryWindow()
  {
    while(GralMng.get().gralDevice.isRunning()){
      try{ Thread.sleep(100);} 
      catch (InterruptedException e)
      { //dialogZbnfConfigurator.terminate();
      }
      if(isTableInitialized) {
        isTableInitialized = false;
        readConfig();
      }
    }
    
  }
  
  
  
  
  void readConfig()
  {
    JZcmdExecuter.ExecuteLevel level = null;
    try {
      JZcmd jzcmd = new JZcmd();
      JZcmdScript script = jzcmd.compile(fileConfig, null);
      executer.initialize(script, false, null, null);
      level = executer.execute_Scriptclass("ToGui"); 
      
    } catch( ScriptException exc) {
      System.err.println(exc.getMessage());
    }
    if(level !=null) {
      for(int iList = 0; iList < wdgTables.length; ++iList ) {
        boolean bErr = false;
        wdgTables[iList].clearTable();;
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
                wdgTables[iList].addLine(lineTexts[0], lineTexts, set1);
              }
            }
            System.out.println(listElement.toString());
          }
        }
        wdgTables[iList].repaint();
      }
    }
    
    
    /*
  IndexMultiTable<String, DataAccess.Variable<Object>> args = new IndexMultiTable<>(IndexMultiTable.providerString);
  DataAccess.Variable<Object> tdata1 = new DataAccess.Variable<Object>('L', "tdata1", new LinkedList<String>()); 
  args.put(tdata1.name(), tdata1);
  DataAccess.Variable<Object> tdata2 = new DataAccess.Variable<Object>('L', "tdata2", new LinkedList<String>()); 
  args.put(tdata2.name(), tdata2);
  DataAccess.Variable<Object> tdata3 = new DataAccess.Variable<Object>('L', "tdata3", new LinkedList<String>()); 
  args.put(tdata3.name(), tdata3);
  DataAccess.Variable<Object> tdata4 = new DataAccess.Variable<Object>('L', "tdata4", new LinkedList<String>()); 
  args.put(tdata4.name(), tdata4);
  JZcmd.execSub(fileScript, "toGui", args, executer.scriptLevel());
     */
  }
  
  
  
  
  void genStimuli()
  {
    String[] identifier = new String[wdgTables.length];
    Map<String, DataAccess.Variable<Object>> idents = new TreeMap<String, DataAccess.Variable<Object>>();
    for(int iTable = 0; iTable < wdgTables.length; ++iTable) {
      GralTable<Map<String, DataAccess.Variable<Object>>>.TableLineData line = wdgTables[iTable].getCurrentLine();
      if(line !=null) {
        Map<String, DataAccess.Variable<Object>> data = line.getUserData();
        identifier[iTable] = data.get("name").value().toString();
        String key = "key" + (iTable+1); 
        idents.put(key, new DataAccess.Variable<Object>('S', key, identifier[iTable])); 
    } }
    try{
      Appendable out = output;
      executer.execSub(null, "genStimuli", idents, false, out, null);
    } catch(ScriptException exc) {
      System.err.println(exc.getMessage());
    }
  }
  
  
  void genTestcases()
  {
    String[] identifier = new String[wdgTables.length];
    Map<String, DataAccess.Variable<Object>> args = new TreeMap<String, DataAccess.Variable<Object>>();
    args.put("select", new DataAccess.Variable<Object>('S', "select", wdgSelects.getText())); 
    try{
      Appendable out = output;
      executer.execSub(null, "genTestcases", args, false, out, null);
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
        genStimuli();
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
        output.setText("");
      }
      return true;
    }
  };
  
  
  
  
  private void openWindow1(){
    GralFactory gralFactory = new SwtFactory();
    LogMessage log = new LogMessageStream(System.out);
    window = gralFactory.createWindow(log, "Select Simulation", 'C', 100, 50, 600, 400);
    gralMng = window.gralMng();
    gralMng.gralDevice.addDispatchOrder(initGraphic);
    //initGraphic.awaitExecution(1, 0);
    
  }
  
  
  GralGraphicTimeOrder initGraphic = new GralGraphicTimeOrder("GralArea9Window.initGraphic"){
    @Override public void executeOrder()
    { 
      // gralMng.selectPanel(window);
      gralMng.setPosition(2, 5, 2, 19, 0, 'r', 2);
      btnReadConfig.setToPanel();
      btnCleanOut.setToPanel();
      btnGenSim.setToPanel();
      btnGenTestcase.setToPanel();
      gralMng.setPosition(6, 8, 60, 79, 0, 'd');
      wdgSelects = gralMng.addTextField("test", true, null, "r");
      wdgSelects.setText("t");
      int last = 1; //tables.length
      for(int iTable = 0; iTable < wdgTables.length; ++iTable) {
        int xtable = iTable %3;
        int ytable = iTable /3;
        gralMng.setPosition(21*ytable + 10, 21*ytable + 30, xtable * 30, xtable * 30 +30, 0, 'd');
        
        wdgTables[iTable].setToPanel();
        wdgTables[iTable].repaintGthread();
      }
      gralMng.setPosition(52, 0, 0, 0, 0, 'U');
      output.setToPanel();
      
      System.setOut(new PrintStreamAdapter("", output));
      isTableInitialized = true;
      //
      //GralTextField input = new GralTextField();
    }
    
    
    
  };
}


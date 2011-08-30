package org.vishia.gral.widget;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.cmd.CmdGetFileArgs_ifc;
import org.vishia.cmd.CmdQueue;
import org.vishia.cmd.CmdStore;
import org.vishia.cmd.PrepareCmd;
import org.vishia.gral.gridPanel.GuiPanelMngBuildIfc;
import org.vishia.gral.ifc.GuiPanelMngWorkingIfc;
import org.vishia.gral.ifc.WidgetDescriptor;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.mainCmd.Report;

/**This class is a widget to select and view commands.
 * It can be used in any application in a window, which is opened on demand 
 * or which is open any time. 
 * 
 * The commands are shown in a table. The appearance of the table can be controlled by the application
 * or maybe by the user. Only one command can be selected to execute.
 * 
 * @author Hartmut Schorrig
 *
 */
public class CommandSelector extends SelectList
{

  public final CmdStore cmdStore;
  
  private final CmdQueue cmdQueue;
  
  /**Gets in graphical thread!
   * 
   */
  private CmdGetFileArgs_ifc getterFiles;
  
  
  
  
  
  
  public CommandSelector(CmdQueue cmdQueue)
  {
    this.cmdStore = new CmdStore();
    this.cmdQueue = cmdQueue;
  }
  
  public void setGetterFiles(CmdGetFileArgs_ifc getterFiles)
  {
    this.getterFiles = getterFiles;
  }
  
  
  public void fillIn()
  {
    wdgdTable.setValue(GuiPanelMngWorkingIfc.cmdClear, -1, null, null);
    for(CmdStore.CmdBlock data: cmdStore.listCmd){
      
      wdgdTable.setValue(GuiPanelMngWorkingIfc.cmdInsert, 0, data.name, data);
    }

  }
  
  
  @Override public void actionOk(Object userData, TableLineGui_ifc line)
  {
    CmdStore.CmdBlock cmdBlock = (CmdStore.CmdBlock)userData;
    getterFiles.prepareFileSelection();
    File[] files = new File[3];
    files[0] = getterFiles.getFile1();
    files[1] = getterFiles.getFile2();
    files[2] = getterFiles.getFile3();
    cmdQueue.addCmd(cmdBlock, files);  //to execute.
  }
  
  
  
  @Override public void actionLeft(Object userData, TableLineGui_ifc line)
  {
  }
  
  @Override public void actionRight(Object userData, TableLineGui_ifc line)
  {
  }
  
  
  @Override
  void actionUserKey(String sKey, Object userData, TableLineGui_ifc line)
  {
    // TODO Auto-generated method stub
    
  }
  
  
  
}

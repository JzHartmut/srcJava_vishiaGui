package org.vishia.commander;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.KeyCode;

public class FcmdStatusLine
{
 
  /**Version, history and license
   * <ul>
   * <li>2012-03-09 Hartmut new: {@link #widgSyncInfo}
   * <li>2012-03-04 Hartmut created as extra class. Functionality was in {@link FcmdFileCard} (setText)
   *   and {@link FcmdButtons} (initialization of widgets) 
   * </ul>
   * 
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
   * <li> But the LPGL ist not appropriate for a whole software product,
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
  public static final int version = 0x20120309;

  
  private final Fcmd main;
  
  boolean showBackslash = false;
  
  GralTextField widgFileInfo, widgFilePath, widgRunInfo, widgSyncInfoLeft, widgSyncInfoRight;
  
  final DateFormat formatDateInfo = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
  

  String sPath = "";
  
  FcmdStatusLine(Fcmd main){
    this.main = main;
  }
  
  
  
  void buildGraphic(){
    this.main.gui.gralMng.setPosition(0, 2, 0, 0, 'r');
    this.widgFilePath = this.main.gui.gralMng.addTextField(this.main.nameTextFieldFilePath, false, null, null);
    this.widgFilePath.setHtmlHelp(this.main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.layout.pathCurr.");
    this.widgFilePath.setDragEnable(this.main.fcmdActions.actionDragFileFromStatusLine, KeyCode.dragFiles);
    GralMenu menuWidg = this.widgFilePath.getContextMenu();
    menuWidg.addMenuItem("menuContextShowBackslash", this.main.idents.menuContextShowBackslash, this.actionShowBackslash);
    menuWidg.addMenuItem("menuContextShowSlash", this.main.idents.menuContextShowSlash, this.actionShowSlash);
    this.main.gui.gralMng.setPosition(2, 4, 0, 9.8f, 'r');
    this.widgRunInfo = this.main.gui.gralMng.addTextField(this.main.nameTextFieldRunInfo, false, null, null);
    this.widgRunInfo.setHtmlHelp(this.main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.layout.runInfo.");
    this.main.gui.gralMng.setPosition(2, 4, 10, -8, 'r');
    this.widgFileInfo = this.main.gui.gralMng.addTextField(this.main.nameTextFieldInfo, false, null, null);
    this.widgFileInfo.setHtmlHelp(this.main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.layout.fileInfo.");
    this.main.gui.gralMng.setPosition(2, 4, -8, -5, 'r', 1);
    this.widgSyncInfoLeft = this.main.gui.gralMng.addTextField(this.main.nameTextFieldInfo, false, null, null);
    this.widgSyncInfoLeft.setHtmlHelp(this.main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.layout.syncInfo.");
    this.widgSyncInfoRight = this.main.gui.gralMng.addTextField("syncInforRight", false, null, null);
    this.widgSyncInfoRight.setHtmlHelp(this.main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.layout.syncInfo.");

  }
  
  
  void setFileInfo(String sTabSelected, FileRemote file){
    long lastModified = file.lastModified();
    String sDate = formatDateInfo.format(new Date(lastModified));
    String sLenShort = //String.format("", file.length)
      file.length() >= (1024 * 1024) ? String.format("%2.3f MByte = %d Byte", file.length()/(1024 * 1024.0), file.length()) :
      file.length() >=    1024 ? String.format("%3.2f kByte = %d Byte", file.length()/1024.0, file.length()) :
      String.format("%3d Byte", file.length());  
    StringBuilder info = new StringBuilder(100);
    info.append(sTabSelected).append(' ');
    info.append(sDate)/*.append(" = ").append(lastModified)*/;
    if(file instanceof FileRemote){
      FileRemote filer = file;
      FileRemote filep = file.getParentFile();
      int parentId = filep !=null ? filep.ident(): 0;
      info.append(" #").append(filer.ident()).append('/').append(parentId).append(" flags=0x")
      .append(Integer.toHexString(filer.getFlags()));
      if(file.mark !=null){
        int mark = file.mark.getMark();
        info.append(" sel=").append(Integer.toHexString((mark >>16) & 0xffff)).append('\'').append(Integer.toHexString(mark & 0xffff));
        if(file.isDirectory()){
          info.append("; files=").append(file.mark.nrofFilesSelected());
        }
      }
    }
    long creationTime = file.creationTime();
    if(creationTime !=0){
      sDate = formatDateInfo.format(new Date(creationTime));
      info.append("; creation=").append(sDate);
    }
    long lastAccess = file.lastAccessTime();
    if(lastAccess !=0){
      sDate = formatDateInfo.format(new Date(lastAccess));
      info.append("; lastAccess=").append(sDate);
    }
    info.append(", length= ").append(sLenShort);
    main.statusLine.widgFileInfo.setText(info);
    sPath = file.getAbsolutePath();
    if(showBackslash){
      sPath = sPath.replace('/', '\\');
    }
    main.statusLine.widgFilePath.setText(sPath);
    if(main.filePropsCmd.isVisible){
      main.filePropsCmd.showFileInfos(file);
    }
    main.fileViewer.quickView();
    
  }
  
  GralUserAction actionShowBackslash = new GralUserAction("actionShowBackslash"){
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params) {
      showBackslash = true;
      sPath = sPath.replace('/', '\\');
      main.statusLine.widgFilePath.setText(sPath);
      return true;
    }    
  };

  GralUserAction actionShowSlash = new GralUserAction("actionShowSlash"){
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params) {
      showBackslash = false;
      sPath = sPath.replace('\\', '/');
      main.statusLine.widgFilePath.setText(sPath);
      return true;
    }    
  };



}

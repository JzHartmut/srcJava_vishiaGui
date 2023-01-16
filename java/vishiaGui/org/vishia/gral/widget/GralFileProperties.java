package org.vishia.gral.widget;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;

import org.vishia.event.EventConsumer;
import org.vishia.event.EventSource;
import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralSwitchButton;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetBase;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidgetBase_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.FileFunctions;
import org.vishia.util.KeyCode;

/**This comprehensive widget contains all to show and change properties of a File
 * given as FileRemote.
 * @author Hartmut Schorrig
 *
 */
public class GralFileProperties extends GralWidgetBase {

  /**Version, history and license.
   * <ul>
   * <li>2023-01-15 Now it is an independent common usable class especially for {@link GralFileSelector}
   * <li>2011 Hartmut created with The.file.Commander
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
  public final static String version = "2023-01-15";
  
  
  final String buttonFilePropsChg = "change file";

  final String buttonFilePropsChanging = "changing ...";

  final String buttonFilePropsCopying = "copying ...";

  final String buttonFilePropsRetry = "retry";

  final String buttonFilePropsAbort = "abort change";

  final String buttonFilePropsOk = "done";

  final String buttonFilePropsCopy = "copy file";

  final String buttonFilePropsChgRecurisve = "change recursive";

  final String buttonFilePropsGetAll = "get all properties";

  final String buttonFilePropsCntLen = "count length all files in dir";

  GralWindow_ifc windFileProps;
  
  GralPanelContent panel;
  
  GralTextField_ifc widgName, widgDir, widgLink, widgDate, widgLength;
  
  GralButton[] widgRd, widgWr, widgEx;
  GralButton widgUID, widgGID, widgSticky;
  GralButton widgHidden, widgDirectory;
  GralButton widgBtnDirBytes;
  
  /**Action button. */
  GralButton widGetAllProps, widgChrRecurs, widgChgFile, widgCopyFile;
  
  
  DateFormat formatDate;
  
  final String sWrAble = "wr / ?rd", sRdOnly = "rd / ?wr";
  
  final String sHidden = "hidden / ?", sNonHidden = "non hidden / ?";
  
  final String sSubdir = "recursive / ?", sNonSubdir = "non recurs/ ?";
  
  static final String sCmdChg = "change", sCmdCopy = "copy", sCmdChgRecurs = "chgRecurs"
    , sCmdQuit = "quit", sCmdAbort = "abort";
  
  /**True if it is a unix system. It checks the "OS" environment variable. */
  boolean bUnixSystem;
  
  /**True then the window is opened. Write acutal file into. */
  boolean isVisible;
  
  /**True while a change commission is send and no answer is received yet. */
  //boolean busyChanging;
  
  FileRemote actFile;
  
  /**
   * 
   */
  final FileRemote.CallbackEvent evChg;
  
  final FileRemote.CallbackEvent evCntLen;
 
  
  
  
  /**Ctor either with a given new window or to create a Panel
   * @param refPos If wind is given, it is the refPos related to the wind.mainPanel,
   *   elsewhere a reference for given sPosName in a panel to create a sub panel. 
   * @param sPosName if wind is given, the name of its mainPanel, else maybe with position String
   * @param wind null, then a new Panel for this content will be created.
   */
  public GralFileProperties(GralPos refPos, String sPosName, GralWindow wind) {
    super(refPos, sPosName, null);
    this.windFileProps = wind;
    if(wind == null) {
      this.panel = new GralPanelContent(refPos, sPosName, null);
    } else {
      this.panel = wind.mainPanel;                         // given panel, refPosP not used, it is the whole panel
    }
    this.formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String sUnix = System.getenv("OS");
    if(sUnix !=null){
      sUnix = sUnix.toUpperCase();
      if(sUnix.contains("WINDOWS")){
        this.bUnixSystem = false;
      } else {
        this.bUnixSystem = true;
      }
    } else {
      this.bUnixSystem = System.getenv("WINDIR") == null;
    }
    this.evChg = new FileRemote.CallbackEvent(this.evSrc, null, null, this.callbackChgProps, null, this.evSrc);
    this.evCntLen = new FileRemote.CallbackEvent(this.evSrc, null, null, this.callbackCntLen, null, this.evSrc);
    this.widgLink = new GralTextField(refPos, "@3.5-3++, 1..-1=link-" + this.name, "symbolic link", "t");
    this.widgDir = new GralTextField(refPos, "dir-" + this.name, "directory path", "t");
    this.widgName = new GralTextField(refPos, "@10-4, =name-" + this.name, "filename", "t", GralTextField.Type.editable);
    this.widgLength = new GralTextField(refPos, "@14-3.5, 1..24++=length-" + this.name, "file-length", "t");
    this.widgDate = new GralTextField(refPos, "@,25..-1=data-" + this.name, "last modified", "t", GralTextField.Type.editable);
//    GralColor colorBack = main.gui.gralMng.propertiesGui.colorBackground_;
    GralColor colorText = GralColor.getColor("bk");
    int ii;
    GralColor colorOn = GralColor.getColor("lgn");
    GralColor colorOff = GralColor.getColor("wh");
    GralColor colorDis = GralColor.getColor("gr");
    String textOn = "X", textOff = " ", textDis = "?";
    
    this.widgBtnDirBytes = new GralButton(refPos, "@17-2, 1..20++0.2=dirBytes-" + this.name, "dirBytes", actionBtnCntLen);
    this.widgBtnDirBytes.setDisableColorText(colorDis, " (file) ");
    //widgDirectory = main.gralMng.addCheckButton("FileProp:btndir", textOn, textOff, textDis, colorOn, colorOff, colorDis);
    //main.gralMng.addText("directory"); 
    this.widgHidden = new GralSwitchButton(refPos, "@17-2, 22+2++0.2=btnhidden-" + this.name, textOn, textOff, textDis, colorOn, colorOff, colorDis);
    new GralLabel(refPos, "hidden"); 
    
    //bUnixSystem = true;
    if(bUnixSystem){
//      main.gui.gralMng.setPosition(20, GralPos.size -2, 10, 28, 'd');
//      widGetAllProps = new GralButton("buttonFilePropsGetAll", actionButton, "getAll", null, main.idents.buttonFilePropsGetAll);
//
//      main.gui.gralMng.setPosition(20, GralPos.size -2, 1, GralPos.size +2, 'd');
//      new GralText("rd");
//      widgRd = new GralButton[3];
//      for(ii=0; ii < 3; ++ii){
//        widgRd[ii] = new GralCheckButton("FileProp:btnro", textOn, textOff, textDis, colorOn, colorOff, colorDis);
//        //widgReadonly[ii] = main.gralMng.addSwitchButton("FileProp:btnro", "X", " ", GralColor.getColor("wh"), GralColor.getColor("lgn"));
//      }
//      main.gui.gralMng.setPosition(20, GralPos.size -2, 4, GralPos.size +2, 'd');
//      new GralText("wr"); 
//      widgWr = new GralButton[3];
//      for(ii=0; ii < 3; ++ii){
//        widgWr[ii] = new GralCheckButton("FileProp:btnro", textOn, textOff, textDis, colorOn, colorOff, colorDis);
//      }
//      main.gui.gralMng.setPosition(20, GralPos.size -2, 7, GralPos.size +2, 'd');
//      new GralText("ex");
//      widgEx = new GralButton[3];
//      for(ii=0; ii < 3; ++ii){
//        widgEx[ii] = new GralCheckButton("FileProp:btnro", textOn, textOff, textDis, colorOn, colorOff, colorDis);
//      }
//      main.gui.gralMng.setPosition(22, GralPos.size -2, 10, GralPos.size +6, 'd');
//      new GralText("owner");
//      new GralText("group");
//      new GralText("all");
//      main.gui.gralMng.setPosition(22, GralPos.size -2, 18, GralPos.size +2, 'r', 0.2f);
//      widgUID = new GralCheckButton("FileProp:btnUID", textOn, textOff, textDis, colorOn, colorOff, colorDis);
//      new GralText("UID"); 
//      main.gui.gralMng.setPosition(24, GralPos.size -2, 18, GralPos.size +2, 'r', 0.2f);
//      widgGID = new GralCheckButton("FileProp:btnGID", textOn, textOff, textDis, colorOn, colorOff, colorDis);
//      new GralText("GID"); 
//      main.gui.gralMng.setPosition(26, GralPos.size -2, 18, GralPos.size +2, 'r', 0.2f);
//      widgSticky = new GralCheckButton("FileProp:btnSticky", textOn, textOff, textDis, colorOn, colorOff, colorDis);
//      new GralText("sticky"); 
    } else {
      new GralLabel(refPos, "@20-2,1+2++1=rd-" + this.name, "rd", 1);
      new GralLabel(refPos, "wr");
      new GralLabel(refPos, "ex");
      this.widgRd = new GralButton[1];
      this.widgWr = new GralButton[1];
      this.widgEx = new GralButton[1];
      this.widgRd[0] = new GralSwitchButton(refPos, "@22-2,1+2++1=btnro" + this.name, textOn, textOff, textDis, colorOn, colorOff, colorDis);
      this.widgWr[0] = new GralSwitchButton(refPos, "btnro2" + this.name, textOn, textOff, textDis, colorOn, colorOff, colorDis);
      this.widgEx[0] = new GralSwitchButton(refPos, "btnro3" + this.name, textOn, textOff, textDis, colorOn, colorOff, colorDis);
      
    }
    this.widgCopyFile =  new GralButton(refPos, "@-12+3++1, -16..-1=buttonFilePropsCopy" + this.name, sCmdCopy, this.actionButton);
    this.widgChrRecurs =   new GralButton(refPos, "buttonFilePropsChgRecursive" + this.name, sCmdChgRecurs, this.actionButton);
    this.widgChgFile = new GralButton(refPos, "buttonFilePropsChg" + this.name, sCmdChg,  this.actionButton);
  }


  /**Builds the content of the file property window. 
   * @param refPosP a position related to the position of the window. It won't be changed.
   * @param sPosName position of the window, maybe relative to given refPosP
   * @param sTitle of the window
   */
  public static GralFileProperties createWindow(GralPos refPosP, String sPosName, String sTitle) { 
    GralPos refPos = new GralPos(refPosP);                 // copy the refPos, it is changed for the window.    
    int windProps = GralWindow_ifc.windConcurrently | GralWindow_ifc.windResizeable;
    GralWindow wind = new GralWindow(refPos, sPosName + "Window", sTitle, windProps);
    wind.setVisible(false);
    GralPanelContent panel = wind.mainPanel;
    String name = panel.name;
    //Note: the refPos describes the whole panel in the window. name is the namePanel without maybe position string
    return new GralFileProperties(refPos, name, wind);
  }
  
  
  /**Opens the view window and fills its content.
   * @param src The path which is selected as source. It may be a directory or a file.
   */
  void openDialog(FileRemote src)
  { //String sSrc, sTrash;
    isVisible = true;
    showFileInfos(src);
    windFileProps.setFocus(); //WindowVisible(true);

  }
  
  
  /**This can be called from outside to show the properties of the given file
   * @param src the file
   */
  public void showFileInfos(FileRemote src){
    if(this.isVisible && !this.evChg.isOccupied()){
      this.actFile = src;
      //TODO don't access the file system without user activity!!! need: a refresh button!!
      this.widgChgFile.setText("change file");
      this.widgChrRecurs.setText("change recursive");
      this.widgCopyFile.setText("copy file");
      this.widgName.setText(src.getName());
      this.widgDir.setText(src.getParent());
      String sDate = this.formatDate.format(new Date(src.lastModified()));
      widgDate.setText(sDate);
      String sLength;
      long length = src.length();
      sLength = "" + length;
      if(length >= 10000 && length < 10000000){
        sLength += " = " + length/1000 + "k";
      } else if( length >= 10000000){
        sLength += " = " + length/1000000 + "M";
      }
      this.widgLength.setText(sLength);
      if(src instanceof FileRemote && (src).isSymbolicLink()){
        this.widgLink.setText(FileFunctions.getCanonicalPath(src));
      } else {
        this.widgLink.setText("");
      }
      this.widgRd[0].setState(src.canRead() ? GralButton.State.On : GralButton.State.Off);
      this.widgEx[0].setState(src.canExecute() ? GralButton.State.On : GralButton.State.Off);
      this.widgWr[0].setState(src.canWrite() ? GralButton.State.On : GralButton.State.Off);
      if(this.bUnixSystem){
        this.widgRd[1].setState(GralButton.State.Disabled);
        this.widgRd[2].setState(GralButton.State.Disabled);
        this.widgWr[1].setState(GralButton.State.Disabled);
        this.widgWr[2].setState(GralButton.State.Disabled);
        this.widgEx[1].setState(GralButton.State.Disabled);
        this.widgEx[2].setState(GralButton.State.Disabled);
        this.widgSticky.setState(GralButton.State.Disabled);
        this.widgUID.setState(GralButton.State.Disabled);
        this.widgGID.setState(GralButton.State.Disabled);
      }
      this.widgHidden.setState(src.isHidden() ? GralButton.State.On : GralButton.State.Off);
      this.widgBtnDirBytes.setState(src.isDirectory() ? GralButton.State.On : GralButton.State.Disabled);
      //widgDirectory.setState(src.isDirectory() ? GralButton.State.On : GralButton.State.Off);
    }
  }
  
  
  boolean countLength ( GralWidget infos ) {
  String name = GralFileProperties.this.widgName.getText();
    if(name.equals(GralFileProperties.this.actFile.getName())){ name = null; } //don't change it.
    int noMask = 0;
    int val = 0; //actFileRemote.getFlags();
    int mask;
    String sDate = GralFileProperties.this.widgDate.getText();
    long date;
    try{ 
      Date date1 = GralFileProperties.this.formatDate.parse(sDate);
      date = date1.getTime();
    } catch(ParseException exc){
      date = 0;
    }
    if(GralFileProperties.this.bUnixSystem){
      mask = FileRemote.mCanRead | FileRemote.mCanWrite | FileRemote.mExecute;
    } else {
      mask = FileRemote.mCanWrite | FileRemote.mHidden;
    }
    switch(GralFileProperties.this.widgRd[0].getState()){
      case Off:       val &= ~FileRemote.mCanRead; break;
      case On:       val |= FileRemote.mCanRead; break;
      case Disabled: mask &= ~FileRemote.mCanRead; break;
    }
    switch(GralFileProperties.this.widgWr[0].getState()){
      case Off:       val &= ~FileRemote.mCanWrite; break;
      case On:       val |= FileRemote.mCanWrite; break;
      case Disabled: mask &= ~FileRemote.mCanWrite; break;
    }
    switch(GralFileProperties.this.widgEx[0].getState()){
      case Off:       val &= ~FileRemote.mExecute; break;
      case On:       val |= FileRemote.mExecute; break;
      case Disabled: mask &= ~FileRemote.mExecute; break;
    }
    if(GralFileProperties.this.bUnixSystem){
      switch(GralFileProperties.this.widgRd[1].getState()){
        case Off:       val &= ~FileRemote.mCanReadGrp; break;
        case On:       val |= FileRemote.mCanReadGrp; break;
        case Disabled: mask &= ~FileRemote.mCanReadGrp; break;
      }
      switch(GralFileProperties.this.widgWr[1].getState()){
        case Off:       val &= ~FileRemote.mCanWriteGrp; break;
        case On:       val |= FileRemote.mCanWriteGrp; break;
        case Disabled: mask &= ~FileRemote.mCanWriteGrp; break;
      }
      switch(this.widgEx[1].getState()){
        case Off:       val &= ~FileRemote.mExecuteGrp; break;
        case On:       val |= FileRemote.mExecuteGrp; break;
        case Disabled: mask &= ~FileRemote.mExecuteGrp; break;
      }
      switch(this.widgRd[2].getState()){
        case Off:       val &= ~FileRemote.mCanReadAny; break;
        case On:       val |= FileRemote.mCanReadAny; break;
        case Disabled: mask &= ~FileRemote.mCanReadAny; break;
      }
      switch(this.widgWr[2].getState()){
        case Off:       val &= ~FileRemote.mCanWriteAny; break;
        case On:       val |= FileRemote.mCanWriteAny; break;
        case Disabled: mask &= ~FileRemote.mCanWriteAny; break;
      }
      switch(this.widgEx[2].getState()){
        case Off:       val &= ~FileRemote.mExecuteAny; break;
        case On:       val |= FileRemote.mExecuteAny; break;
        case Disabled: mask &= ~FileRemote.mExecuteAny; break;
      }
    }
    switch(this.widgHidden.getState()){
      case Off:       val &= ~FileRemote.mHidden; break;
      case On:        val |=  FileRemote.mHidden; break;
      case Disabled: mask &= ~FileRemote.mHidden; break;
    }
    val &= mask;   //only used bits.
    boolean bAbort = false;
    if(infos.sCmd.equals(sCmdAbort)){
      if(this.evChg.occupy(this.evSrc, this.callbackChgProps, null, true)){
        this.widgChgFile.setText(this.buttonFilePropsChg);
        infos.sCmd = sCmdChg;
      } else {
        System.err.println("chg properties hangs");
      }
    } else if(infos.sCmd.equals(sCmdChg)){
      if(this.evChg.occupy(this.evSrc, this.callbackChgProps, null, true)){
        //cmds with callback
        this.widgChgFile.setText(this.buttonFilePropsChanging);
        this.actFile.chgProps(name, mask, val, date, this.evChg);
        //TODO main.refreshFilePanel(actFile.getParentFile());  //refresh the panel if the directory is shown there
      } else { bAbort = true; }
      //
    } else if(infos.sCmd.equals(sCmdChgRecurs)){
      if(this.evChg.occupy(this.evSrc, this.callbackChgProps, null, true)){
        //cmds with callback
        this.widgChrRecurs.setText(this.buttonFilePropsChanging);
        this.actFile.chgPropsRecursive(mask, val, date, this.evChg);
      } else { bAbort = true; }
      //
    } else if(infos.sCmd.equals(sCmdCopy)){
      if(this.evChg.occupy(this.evSrc, this.callbackChgProps, null, true)){
        if(name !=null && !name.equals(this.actFile.getName())){
          this.widgCopyFile.setText(this.buttonFilePropsCopying);
          FileRemote fileNew = this.actFile.getParentFile().child(name);
          this.actFile.copyTo(fileNew, this.evChg, FileRemote.modeCopyReadOnlyOverwrite | FileRemote.modeCopyCreateYes | FileRemote.modeCopyExistAll);
        } else {
          this.widgCopyFile.setText("copy - name?");
        }
      } else { bAbort = true; }
    }
    if(bAbort){
      this.widgChgFile.setText(this.buttonFilePropsAbort);
      this.widgChgFile.sCmd = sCmdAbort;
    }
    return true;
  }

  EventSource evSrc = new EventSource("FcmdFileProps"){
    @Override public void notifyDequeued(){
      
    }
    @Override public void notifyConsumed(int ctConsumed){}
    @Override public void notifyRelinquished(int ctConsumed){}
  };



  /**Action for all buttons of 'file properties' window: 
   */
  GralUserAction actionButton = new GralUserAction("actionBtnCntLen")
  {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){  //not on mouse down but on mouse up.
        return countLength( infos );
      }
      return false;
      // /
    }
  };



  GralUserAction actionInvisible = new GralUserAction("actionInvisible")
  { @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { GralFileProperties.this.isVisible = false;
      return true;
    }
  };


  EventConsumer callbackChgProps = new EventConsumer()
  { @Override public int processEvent(EventObject evP)
    { FileRemote.CallbackEvent ev = (FileRemote.CallbackEvent)evP;
      if(ev.getCmd() == FileRemote.CallbackCmd.done){
        showFileInfos(GralFileProperties.this.actFile);
        GralFileProperties.this.widgChgFile.setText(GralFileProperties.this.buttonFilePropsOk);
      } else {
        GralFileProperties.this.widgChgFile.setText(GralFileProperties.this.buttonFilePropsRetry);
      }
      ev.relinquish();
      return 1;
    } 
  
     @Override public String toString(){ return "FcmdFileProps-callbackChgProps"; }

  };


  
  
  /**Action for Key F2 for view command. 
   */
  GralUserAction actionBtnCntLen = new GralUserAction("actionBtnCntLen")
  {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){
        GralFileProperties.this.widgBtnDirBytes.setText("counting ...");
        if(0 != GralFileProperties.this.evCntLen.occupyRecall(100, GralFileProperties.this.evSrc, GralFileProperties.this.callbackCntLen, null, true)){
          GralFileProperties.this.actFile.countAllFileLength(GralFileProperties.this.evCntLen);
        }
      }
      return true;
  } };
  
  EventConsumer callbackCntLen = new EventConsumer()
  { @Override public int processEvent(EventObject evP)
    { FileRemote.CallbackEvent ev = (FileRemote.CallbackEvent)evP;
      if(ev.getCmd() == FileRemote.CallbackCmd.done){
        String sLen = "" + ev.nrofBytesAll;
        GralFileProperties.this.widgLength.setText(sLen);
      } else {
        GralFileProperties.this.widgLength.setText("error count bytes");
      }
      GralFileProperties.this.widgBtnDirBytes.setText(GralFileProperties.this.buttonFilePropsCntLen);
      ev.relinquish();
      return 1;
    } 
  
    @Override public String toString(){ return "FcmdFileProps - callback cnt length"; }

  };


  
  
  
  @Override public void setFocus () {
    // TODO Auto-generated method stub
    
  }

  @Override public void setFocus ( int delay, int latest ) {
    // TODO Auto-generated method stub
    
  }

  @Override public boolean isInFocus () {
    // TODO Auto-generated method stub
    return false;
  }

  @Override public boolean isVisible () {
    return this.isVisible;
  }

  @Override public void setFocusedWidget ( GralWidgetBase_ifc widg ) {
    // TODO Auto-generated method stub
    
  }

  @Override public GralWidgetBase_ifc getFocusedWidget () {
    return null;
  }

  @Override public boolean setVisible ( boolean visible ) {
    this.isVisible = visible;
    if(this.windFileProps !=null) {
      this.windFileProps.setVisible(visible);
    } else {
      this.panel.setVisible(visible);
    }
    return true;
  }

  @Override public boolean createImplWidget_Gthread () throws IllegalStateException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override public void removeImplWidget_Gthread () {
    // TODO Auto-generated method stub
    
  }

}

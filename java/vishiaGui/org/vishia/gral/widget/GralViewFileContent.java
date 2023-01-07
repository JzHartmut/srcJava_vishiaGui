package org.vishia.gral.widget;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetBase;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTextFieldUser_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidgetBase_ifc;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.KeyCode;
import org.vishia.util.StringFormatter;
import org.vishia.util.StringFunctions_C;

/**This is a comprehensive widget to show the content of files
 * in some different text coding and also in hexa.
 * Also support search, also support a simple edit. 
 * This widget can be used for all situations where file contents should be shown and edit.  
 * @author Hartmut Schorrig
 *
 */
public class GralViewFileContent extends GralWidgetBase {

  /**Version, history and license. This String is visible in the about info.
   * <ul>
   * <li>2022-12-26 Hartmut moved from FcmdView, can be used commonly outside the.File.commander,
   *   especially for ordinary file selection and view of its content.  
   * <li>2021-02-05 Hartmut chg for FcmdView, change and write in hex. It is under construction yet now. 
   *     There were no other text editor simply found, which shows characters as hexa, and which allows hexa edit.
   *     Hexa changes should be very interesting for experience. Of course not for the common user.  
   * <li>2011-10-00 Hartmut created
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
  //@SuppressWarnings("hiding")
  public static final String version = "2021-02-05";

  final GralMng gralMng;
  
  final LogMessage log;
  
  /**The window of this functionallity. */
  private GralWindow windView;

  /**The widget to show content. */
  private GralTextBox widgContent;
  
  private GralTextField widgFindText, widgShowInfo;
  
  private GralButton btnFind, btnWholeword, btnCase, btnQuickview;
  
  boolean bVisible;
  
  boolean bEditable;
  
  int nrQuickview;
  
  private GralTextBox widgQuickView;
  
  FileRemote file;
  
  /**A buffer to get bytes from the file using the java.nio.Channel mechanism. 
   * The channel mechanism is proper to work with remote file access especially.
   * Note: The ByteBuffer may be a part of the channel mechanism itself, 
   * because it is placed JVM-internally,
   * for example for socket communication. TODO check and change it.
   * The size of the byteBuffer is set to less than 1 UDP-telegram payload
   * to support any communication.
   */
  private final ByteBuffer tmpReadTransmissionBuffer = ByteBuffer.allocate(1200);

  /**Number of read bytes. */
  private int zContent;


  /**The gotten bytes from bytebuffer. This buffer is set to the size of the file, if the file
   * is less than a maximal size. */
  private final byte[] uContent = new byte[10000000];
  
  
  private Charset encodingContent;
  
  //private byte[] outBuffer = new byte[20000];
  
  /**The current choiced view format.
   * <ul>
   * <li>a: us-ascii-text
   * <li>w: iso (Windows)-text
   * <li>u: UTF16-text
   * <li>1: Hexa byte wise
   * <li>2: Hexa 16-bit-words
   * <li>4: Hexa 32-bit-words
   * <li>F: contains, float values, shows it
   * </ul>  
   */
  private char format = 't';
  
  private int cursorPos;
  
  private static Charset ascii7 = Charset.forName("US-ASCII");
  
  private static Charset utf8 = Charset.forName("UTF8");
  
  private static Charset iso8859_1 = Charset.forName("ISO-8859-1");
  
  private static byte[] endl_0a = { 0x0a };
  
  private static byte[] endl_0d0a = { 0x0d, 0x0a };
  
  
  /**Instance to prepare the text especially for hex view. */
  private final StringFormatter formatterHex = new StringFormatter(120);
  
  public GralViewFileContent ( GralPos refPosP, String posName )
  { super(refPosP, posName, null);
    this.gralMng = refPosP.parent.gralMng();
    this.log = this.gralMng.log();
    //
    GralPos refPos = refPosP.screenPos(20, 20, 100, 80);
    int windProps = GralWindow_ifc.windConcurrently | GralWindow.windHasMenu | GralWindow.windResizeable
                  | GralWindow.windOnTop;
    this.windView = new GralWindow(refPos, posName + "Wind", "View", windProps); 
    String name = this.windView.getName();
    
//    wind.addMenuBarItemGThread(null, "&File/&Save", actionSave);
//    wind.addMenuBarItemGThread(null, "&File/Save-as &UTF8-Unix-lf", actionSaveTextAsUTF8unix);
//    wind.addMenuBarItemGThread(null, "&File/Save-as &Windows (ISO-8859-1)", actionSaveTextAsWindows);
//    wind.addMenuBarItemGThread(null, "&File/Save-as &ISO-8859-1-Unix-lf", actionSaveTextAsISO8859_1_unix);
//    wind.addMenuBarItemGThread("view-Search", "&View/&Hex-Byte", actionSetHexView);
//    wind.addMenuBarItemGThread("view-Search", "&View/text-&Windows", actionSetTextViewISO8859_1);
//    wind.addMenuBarItemGThread("view-Search", "&View/text-&UTF", actionSetTextViewUTF8);
//    wind.addMenuBarItemGThread("view-Search", "&View/text-&ASCII-7", actionSetTextViewISO8859_1);
//    wind.addMenuBarItemGThread("view-Search", "&View/text-&Encoding", actionSetTextViewISO8859_1);
//    wind.addMenuBarItemGThread("view-Search", "&Edit/&Search", actionSetTextViewISO8859_1);
//    wind.addMenuBarItemGThread("view-Search", "&Edit/set &Writeable", actionSetEditable);
    
    this.widgFindText = new GralTextField(refPos, "@0.5+2, 2+20=find-" + name , GralTextField.Type.editable);
    this.btnFind = new GralButton(refPos, "@0.5+2, 22+15++1=btnfind-" + name, "Search (ctrl-F)", this.actionFind);
    this.btnWholeword = new GralButton(refPos, "wholeW-" + name, null, null);
    this.btnWholeword.setSwitchMode(GralColor.getColor("wh"), GralColor.getColor("gn"));
    this.btnWholeword.setSwitchMode("wholeWord - no", "wholeWord- yes");
    this.btnCase = new GralButton(refPos, "caseS-" + name, null, null);
    this.btnWholeword.setSwitchMode(GralColor.getColor("wh"), GralColor.getColor("gn"));
    this.btnWholeword.setSwitchMode("case- no", "case- yes");
    this.btnQuickview = new GralButton(refPos, "btnQuickView-" + name, "quick view", null);
    this.btnQuickview.setSwitchMode(GralColor.getColor("wh"), GralColor.getColor("gn"));
    this.btnQuickview.setSwitchMode("quick view off", "quick view on");
    this.widgShowInfo = new GralTextField(refPos, "info-" + name, "info", "r");
      this.widgContent = new GralTextBox(refPos, "@3..0, 0..0=" + name + "_content");    
//    widgContent = this.main.gui.gralMng.addTextBox("view-content", false, null, '.');
    this.widgContent.setUser(userKeys);
    //this.widgContent.setTextStyle(GralColor.getColor("bk"), this.main.gui.gralMng.propertiesGui.getTextFont(2.0f, 'm', 'n'));
    this.windView.specifyActionOnCloseWindow(this.actionOnSetInvisible);
    this.windView.setWindowVisible(false);
    //windView1.
  }

  
  
  
  /**Reads the current (last selected) file in the binary content buffer, 
   * detects its encoding, shows it.
   * The file is held in a binary content buffer independent of its content type.
   * The hexa view shows the bytes in any case. If the file will be shown as text,
   * either the encoding can be self - detect, or the encoding can be selected by the user.
   * Opens the view window and fills its content.
   * @param src The path which is selected as source. It may be a directory or a file.
   */
  void view(FileRemote file)
  { //String sSrc, sTrash;
    if(file !=null){
      long len = file.length();
      //if(len > 1000000){ len = 1000000; } //nor more than 1MByte, 
      //uContent = new byte[(int)len + 10000];
      zContent = 0;
      ReadableByteChannel reader = file.openRead(0);
      try{
        if(reader == null){
          widgContent.setText("File is not able to read:\n");
          widgContent.append(file.getAbsolutePath());
        } else {
          int nrofBytesRead;
          do{
            tmpReadTransmissionBuffer.clear();
            nrofBytesRead = reader.read(tmpReadTransmissionBuffer);
            if(nrofBytesRead >0){
              tmpReadTransmissionBuffer.rewind();
              if(zContent + nrofBytesRead > uContent.length){
                nrofBytesRead = uContent.length - zContent;
              }
              if(nrofBytesRead > 0){
                tmpReadTransmissionBuffer.get(uContent, zContent, nrofBytesRead);
                zContent += nrofBytesRead;
              }
            }
          } while(nrofBytesRead >0); //Stop if no bytes read or uContent is full.
          reader.close();
          //byteBuffer.rewind();
          presentContent();
        }
      } catch(IOException exc){
        
      }
    }
    if(!bVisible){
      windView.setWindowVisible(true);
      windView.setFocus();
      bVisible = true;
    }
  }
  
  
  
  /**This routine will be called whenever a file is selected newly, it checks quickview.
   * 
   * 
   */
  public void quickView(){
    if(btnQuickview !=null && btnQuickview.isOn()){   //check !=null only temporary
      nrQuickview +=1;
      widgShowInfo.setText("" + nrQuickview);
      view(null);
    }
  }
  

  
  void detectEncoding(){
    encodingContent = iso8859_1;
    format = 'w';
    for(int ii =0; ii<zContent; ++ii){
      byte cc = uContent[ii];
      if(cc < 0x20 && cc >=0 && "\r\n\t".indexOf(cc) <0){
        //non-text character
        encodingContent = null;
        return;
      }
    }
  }
  
  
  
  void presentContent() throws IOException
  {
    widgContent.setEditable(false);
    bEditable = false;
    detectEncoding();
    if(encodingContent!=null){
      presentContentText(encodingContent);
    } else {
      widgContent.setText("file ...\n");
      cursorPos = 0;
      format = 'h';
      presentContentHex();
    }
  }
  
  
  
  void presentContentHex() throws IOException
  {
    int pos0;
    try{
      String sFile = "file: " + file.getAbsolutePath() + "\n";
      widgContent.setText(sFile);
      pos0 = sFile.length();
      int ctBytes = this.zContent;
      if(ctBytes > 0x1000) { ctBytes = 0x1000; }
      //byteBuffer.get(buffer);
      for(int ii = 0; ii < uContent.length /16; ++ii) {
        formatterHex.reset();
        formatterHex.addHex(ii<<4, 4).add(": ");
        int nrHex = ctBytes > 16 ? 16 : ctBytes;
        formatterHex.addHexLine(uContent, nrHex*ii, nrHex, StringFormatter.k1);
        formatterHex.add(" : ").addStringLine(uContent, nrHex*ii, nrHex, ascii7.name());
        widgContent.append(formatterHex.getContent()).append('\n');
        ctBytes -=nrHex;
        if(ctBytes <=0) { break; }
      }
      int posCursor = pos0 + (6 + 3*16 + 2 + 17)* (cursorPos/16) + 6 + 3 * (cursorPos % 16);
      widgContent.setCursorPos(posCursor);
    } catch(Exception exc){
      widgContent.append(exc.getMessage());
    }
  }
  
  
  
  void presentContentText(Charset charset) throws UnsupportedEncodingException
  {
    encodingContent = charset;
    String content = new String(uContent, 0, zContent, charset);
    widgContent.setText(content, 0);
    widgContent.setCursorPos(cursorPos);
  }
  
  
  void syncContentFromWidg(){
    switch(format){
    case 'h':{
      int posInWidg = widgContent.getCursorPos();
      syncHexFromWidg();
      //cursorPos;
    }break;
    case 'w': syncTextFromWidg(iso8859_1); break;
    case 'u': syncTextFromWidg(utf8); break;
    default:  
    }
  }

  
  
  void syncHexFromWidg() {
    int posInWidg = widgContent.getCursorPos();
    cursorPos = posInWidg;
    ////
    if(bEditable) {
    //if(widgContent.isChanged(true)){    //TODO isChanged does not work yet. It may better.
      String sContent = widgContent.getText();
      int len = sContent.length();
      int beg = 0;
      int end = sContent.indexOf('\n');
      if(end<0) { end = len; }
      int pos = -1;
      int[] endCC = new int[1];
      while(beg < len) {
        String sLine = sContent.substring(beg, end);
        int zLine = sLine.length();
        char cc;
        int addr = StringFunctions_C.parseIntRadix(sLine, 0, 8, 16, endCC);
        int ix = endCC[0];
        if(endCC[0] >0 && ix < zLine && sLine.charAt(ix++) == ':') {
          int w = 0;
          int zB = 0;  //number of bytess in the line
          int zD = 0;  //number of digits in w
          if(sLine.charAt(4) ==':' && zLine >5) {
            cc = sLine.charAt(ix);
            while(ix < zLine && cc ==' ') { cc = sLine.charAt(ix++); }  //skip over space, space is separator
            while(zB < 16 && ix <= zLine) {  //not, it is incremented already
              if(cc >='0' && cc <='9') { w = (w <<4) + (cc - '0'); zD +=1; } 
              else if(cc >='a' && cc <='f') { w = (w <<4) + (cc - 'a' +10); zD +=1; } 
              else if(cc >='A' && cc <='F') { w = (w <<4) + (cc - 'A' +10); zD +=1; }
              else if(cc == ':') { break; }          //finish this line
              else if(cc == ' ') {                   //space after digits
                if(zD >=1) {
                  uContent[++pos] = (byte)w;
                  w = 0;
                  zB +=1;
                  zD = 0;
                }
              }
              else; //faulty char ignore it. 
              cc = sLine.charAt(ix++);
            }
          }
        }
        beg = end +1;
        end = sContent.indexOf('\n', beg);
        if(end<0) { end = len; }
      }
    }
  }
  
  
  
  
  void syncTextFromWidg(Charset charset){
    int posInWidg = widgContent.getCursorPos();
    cursorPos = posInWidg;
    ////
    if(bEditable) {
    //if(widgContent.isChanged(true)){    //TODO isChanged does not work yet. It may better.
      String sContent = widgContent.getText();
      int zContentnew = sContent.length();
      int pos = -1;
      
      byte[] bytes = sContent.getBytes(charset);  //TODO portions of substring with size about 4096 ...16000
      for(byte bb: bytes){
        uContent[++pos] = bb;
      }
      int end = zContent -1;  //to set 0-bytes
      zContent = ++pos;       //new size
      while(pos < end){
        uContent[++pos] = 0;  //remove rest, let it clean.
      }
    }
    
  }
    
  
  
  void saveTextAs(Charset encoding, byte[] lineEnd){
    try{
      //read the content in the given encoding:
      InputStream inpBytes = new ByteArrayInputStream(uContent, 0, zContent);
      InputStreamReader inpText = new InputStreamReader(inpBytes, encodingContent);
      BufferedReader inpLines = new BufferedReader(inpText);
      FileRemote filedst = this.file;
      WritableByteChannel outchn =filedst.openWrite(0);
      ByteBuffer outBuffer = ByteBuffer.allocate(1200);
      //Writer out = new FileWriter();
      String sLine;
      do{
        sLine = inpLines.readLine();
        if(sLine !=null){
          byte[] bytes = sLine.getBytes(encoding);
          if(outBuffer.remaining() < bytes.length+1){
            int zOut = outBuffer.position();
            outBuffer.limit(zOut);
            outBuffer.rewind();
            outchn.write(outBuffer);
            outBuffer.limit(outBuffer.capacity());
            outBuffer.clear();  
          }
          int posBytes = 0;
          int zOutBuffer;
          while( (zOutBuffer = outBuffer.remaining()) < (bytes.length - posBytes +1)){
            outBuffer.put(bytes, posBytes, zOutBuffer);
            outBuffer.limit(zOutBuffer);
            outBuffer.rewind();
            outchn.write(outBuffer);
            outBuffer.clear();
            posBytes += zOutBuffer; 
          }
          outBuffer.put(bytes, posBytes, bytes.length - posBytes)
                   .put(lineEnd);
          //outText.append(sLine).append('\n');
        }
      } while(sLine !=null);
      int zOut = outBuffer.position();
      outBuffer.limit(zOut);
      outBuffer.rewind();
      outchn.write(outBuffer);
      outBuffer.clear(); 
      outchn.close();
      
    } catch(Exception exc){
      this.log.writeError("unexpected", exc);
    }
    
  }
  
  
  
  void openQuickView(FileRemote src){
    if(widgQuickView == null){
      //creates an grid panel and select its in gralMng:
      //main.favorPathSelector.panelRight.tabbedPanelFileCards.addTabPanel("qview", "qview");
      //this.main.gui.gralMng.setPosition(1, -1, 0, 0, 1, 'd');
      //adds a textBox in that grid panel.
      widgQuickView = this.gralMng.addTextBox("qview-content", false, null, '.');
      widgQuickView.setText("quick view");
      widgQuickView.setFocus();
    }
    else {
      closeQuickView();
    }
  }
  
  
  
  void closeQuickView(){
    //main.favorPathSelector.panelRight.tabbedPanelFileCards.removeWidget("qview");
    widgQuickView.remove();
    widgQuickView = null;
  }
  
  
  @Override public void setFocus () {
    this.windView.setFocus();
  }




  @Override public void setFocus ( int delay, int latest ) {
    this.windView.setFocus(delay, latest);
  }




  @Override public boolean isInFocus () {
    return this.windView.isInFocus();
  }




  @Override public boolean isVisible () {
    return this.windView.isVisible();
  }




  @Override public void setFocusedWidget ( GralWidgetBase_ifc widg ) {
    this.windView.setFocusedWidget(widg);
  }




  @Override public GralWidgetBase_ifc getFocusedWidget () {
    return this.windView.getFocusedWidget();
  }




  @Override public boolean setVisible ( boolean visible ) {
    this.windView.setVisible(visible);
    return true;
  }




  @Override public boolean createImplWidget_Gthread () throws IllegalStateException {
    this.windView.createImplWidget_Gthread();
    return true;
  }




  @Override public void removeImplWidget_Gthread () {
    this.windView.removeImplWidget_Gthread();
    
  }


  
  
  /**Action for Key crl-Q for quick view command. Its like Norton Commander.
   */
  public GralUserAction actionQuickView = new GralUserAction("quick view")
  {
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        openQuickView(null);
        return true;
      } else return false; 
      // /
    }
  };


  

  
  
  
  /**Action for Key F3 for view command. Its like Norton Commander.
   */
  GralUserAction actionFind = new GralUserAction("actionFind")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        String text = widgContent.getText();
        String find = widgFindText.getText();
        int pos0 = widgContent.getCursorPos();
        int pos1 = text.indexOf(find, pos0+1);
        if(pos1 > 0){
          widgContent.setCursorPos(pos1);
        }
        return true;
      } else return false; 
      // /
    }
  };


  
  
  /**Action for Key F3 for view command. Its like Norton Commander.
   */
  public GralUserAction actionOpenView = new GralUserAction("actionOpenView")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
      btnQuickview.setState(GralButton.State.On);
      view(null);
        return true;
      } else return false; 
      // /
    }
  };


  /**Action for Key F3 for view command. Its like Norton Commander.
   */
  GralUserAction actionSetTextViewUTF8 = new GralUserAction("actionSetTextViewUTF8")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    { if(key != KeyCode.mouse1Down){  //supress both mouse up and down reaction
      try{ 
        format = '?';
        presentContentText(utf8);
        format = 'u';
      } catch(UnsupportedEncodingException exc){
        System.err.println("GralViewFileContent.actionSetTextViewUTF8 - UnsupportedEncodingException; unexpected");
      }
      return true;
      } else return false; 
      // /
    }
  };


  /**Action for Key F3 for view command. Its like Norton Commander.
   */
  GralUserAction actionSetHexView = new GralUserAction("actionSetHexView")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    {  if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
       try{ 
        syncContentFromWidg();
        format = '?';
        presentContentHex();
        format = 'h';
      } catch(Exception exc){
        System.err.println("GralViewFileContent.actionSetHexView - Exception; unexpected");
      }
      return true;
      } else return false; 
      // /
    }
  };


  /**Action for Key F3 for view command. Its like Norton Commander.
   */
  GralUserAction actionSetTextViewISO8859_1 = new GralUserAction("actionSetTextViewISO8859_1")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    { if(key != KeyCode.mouse1Down){  //supress both mouse up and down reaction
      try{ 
        format = '?';
        presentContentText(iso8859_1);
        format = 'w';
      } catch(UnsupportedEncodingException exc){
        System.err.println("GralViewFileContent.actionSetTextViewISO8859_1 - UnsupportedEncodingException; unexpected");
      }
      return true;
      } else return false; 
      // /
    }
  };


  /**Action for Key F3 for view command. Its like Norton Commander.
   */
  GralUserAction actionSetEditable = new GralUserAction("actionSetEditable")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        widgContent.setEditable(true);
        bEditable = true;
        return true;
      } else return false; 
      // /
    }
  };


  GralUserAction actionSave = new GralUserAction("actionSave")
  {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { 
      if(GralViewFileContent.this.bEditable){
        try{
        //InputStream inpBytes = new ByteArrayInputStream(uContent);
        //InputStreamReader inpText = new InputStreamReader(inpBytes);
        //BufferedReader inpLines = new BufferedReader(inpText);
          FileRemote filedst = GralViewFileContent.this.file;
          WritableByteChannel outchn =filedst.openWrite(0);  //use Channel-io to support remote files. 
          ByteBuffer outBuffer = ByteBuffer.allocate(1200);
          //Writer out = new FileWriter();
          syncContentFromWidg();
          int nrofBytes = zContent; //uContent.length;
          int posBytes = 0;
          while(nrofBytes > 0){
            int zWrite = nrofBytes >=1200 ? 1200 : nrofBytes;
            outBuffer.put(uContent, posBytes, zWrite);
            nrofBytes -= zWrite;
            posBytes += zWrite;
            outBuffer.limit(zWrite);
            outBuffer.rewind();
            outchn.write(outBuffer);
            outBuffer.limit(outBuffer.capacity());
            outBuffer.clear();
          }
          outchn.close();
          
        } catch(Exception exc){
          GralViewFileContent.this.log.writeError("unexpected", exc);
        }
      }
      return true;
      // /
    }
  };


  GralUserAction actionSaveTextAsUTF8unix = new GralUserAction("actionSaveTextAsUTF8unix")
  { @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { saveTextAs(utf8, endl_0a);
      return true;
    }
  };


  GralUserAction actionSaveTextAsWindows = new GralUserAction("actionSaveTextAsWindows")
  { @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { saveTextAs(iso8859_1, endl_0d0a);
      return true;
    }
  };


  GralUserAction actionSaveTextAsISO8859_1_unix = new GralUserAction("actionSaveTextAsISO8859_1_unix")
  { @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { saveTextAs(iso8859_1, endl_0d0a);
      return true;
    }
  };


  GralUserAction actionOnSetInvisible = new GralUserAction("view-setInvisible")
  { @Override public boolean exec(int keyCode, GralWidget_ifc widgi, Object... params)
    { bVisible = false;
      nrQuickview = 0;
      btnQuickview.setState(GralButton.State.Off);
      return true;
    }
  };


  
  GralTextFieldUser_ifc userKeys = new GralTextFieldUser_ifc() {
    
    @Override
    public boolean userKey(int keyCode, String content, int cursorPos, int selectStart, int selectEnd) {
      boolean bDone = true;
      widgShowInfo.setText("" + cursorPos);
      switch(keyCode){
      case KeyCode.ctrl + 'F': actionFind.userActionGui(KeyCode.menuEntered, null); break;
        default: bDone = false;
      }
      return bDone;
    }
  };

  
}

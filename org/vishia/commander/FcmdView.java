package org.vishia.commander;

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

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTextFieldUser_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.FileRemote;
import org.vishia.util.KeyCode;
import org.vishia.util.StringFormatter;

/**All functionality of view (F3-Key) and edit a file. 
 * @author Hartmut Schorrig
 * */
public class FcmdView
{
  protected final Fcmd main;

  /**The window of this functionallity. */
  private GralWindow_ifc windView;

  /**The widget to show content. */
  private GralTextBox widgContent;
  
  private GralTextField widgFindText;
  
  private GralButton btnFind, btnWholeword, btnCase;
  
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
  private int nrofBytes;


  /**The gotten bytes from bytebuffer. This buffer is set to the size of the file, if the file
   * is less than a maximal size. */
  private byte[] uContent;
  
  
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
  private StringFormatter formatterHex = new StringFormatter(120);
  
  public FcmdView(Fcmd main)
  { this.main = main;
  }
  
  
  /**Builds the content of the confirm-delete window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowView()
  {
    main.gralMng.selectPanel("primaryWindow");
    main.gralMng.setPosition(10, 0, 10, 0, 1, 'r'); //right buttom, about half less display width and hight.
    int windProps = GralWindow.windConcurrently | GralWindow.windHasMenu | GralWindow.windResizeable;
    GralWindow wind =  main.gralMng.createWindow("windView", "view - The.file.Commander", windProps);
    wind.addMenuItemGThread(null, "&File/&Save", actionSave);
    wind.addMenuItemGThread(null, "&File/Save-as &UTF8-Unix-lf", actionSaveTextAsUTF8unix);
    wind.addMenuItemGThread(null, "&File/Save-as &Windows (ISO-8859-1)", actionSaveTextAsWindows);
    wind.addMenuItemGThread(null, "&File/Save-as &ISO-8859-1-Unix-lf", actionSaveTextAsISO8859_1_unix);
    wind.addMenuItemGThread("view-Search", "&View/&Hex-Byte", actionSetHexView);
    wind.addMenuItemGThread("view-Search", "&View/text-&Windows", actionSetTextViewISO8859_1);
    wind.addMenuItemGThread("view-Search", "&View/text-&UTF", actionSetTextViewUTF8);
    wind.addMenuItemGThread("view-Search", "&View/text-&ASCII-7", actionSetTextViewISO8859_1);
    wind.addMenuItemGThread("view-Search", "&View/text-&Encoding", actionSetTextViewISO8859_1);
    wind.addMenuItemGThread("view-Search", "&Edit/&Search", actionSetTextViewISO8859_1);
    wind.addMenuItemGThread("view-Search", "&Edit/set &Writeable", actionSetEditable);
    main.gralMng.setPosition(0.5f, 2.5f, 1, 20, 0, 'r');
    widgFindText = main.gralMng.addTextField(null, true, null, null);
    main.gralMng.setPosition(0.5f, 2.5f, 22, GralPos.size + 10, 0, 'r', 1);
    btnFind = main.gralMng.addButton(null, actionFind, null, null, null, "Search (ctrl-F)");
    btnWholeword = main.gralMng.addSwitchButton(null, "wholeWord - no", "wholeWord- yes", GralColor.getColor("wh"), GralColor.getColor("gn"));
    btnCase = main.gralMng.addSwitchButton(null, "case - no", "case - yes", GralColor.getColor("wh"), GralColor.getColor("gn"));
    main.gralMng.setPosition(3, 0, 0, 0, 1, 'r');
    widgContent = main.gralMng.addTextBox("view-content", false, null, '.');
    widgContent.setTextStyle(GralColor.getColor("bk"), main.gralMng.propertiesGui.getTextFont(2.0f, 'm', 'n'));
    windView = wind; 
    windView.setWindowVisible(false);
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
  void view(FileRemote XXXsrc)
  { //String sSrc, sTrash;
    file = main.currentFile;
    if(file !=null){
      long len = file.length();
      if(len > 1000000){ len = 1000000; } //nor more than 1MByte, 
      uContent = new byte[(int)len];
      int iBuffer = 0;
      ReadableByteChannel reader = file.openRead(0);
      try{
        if(reader == null){
          widgContent.setText("File is not able to read:\n");
          widgContent.append(file.getAbsolutePath());
        } else {
          do{
            tmpReadTransmissionBuffer.clear();
            nrofBytes = reader.read(tmpReadTransmissionBuffer);
            if(nrofBytes >0){
              tmpReadTransmissionBuffer.rewind();
              tmpReadTransmissionBuffer.get(uContent, iBuffer, nrofBytes);
              iBuffer += nrofBytes;
            }
          } while(nrofBytes >0);
          reader.close();
          //byteBuffer.rewind();
          detectEncoding();
          presentContent();
        }
      } catch(IOException exc){
        
      }
    }
    windView.setWindowVisible(true);

  }
  

  
  void detectEncoding(){
    encodingContent = iso8859_1;
  }
  
  
  
  void presentContent() throws IOException
  {
    widgContent.setText("file ...\n");
    cursorPos = 0;
    format = 'h';
    presentContentHex();  
  }
  
  
  
  void presentContentHex() throws IOException
  {
    int pos0;
    try{
      String sFile = "file: " + file.getAbsolutePath() + "\n";
      widgContent.setText(sFile);
      pos0 = sFile.length();
      //byteBuffer.get(buffer);
      for(int ii = 0; ii < 16 && ii < uContent.length /16; ++ii){
        formatterHex.reset();
        formatterHex.addHex(ii, 4).add(": ");
        formatterHex.addHexLine(uContent, 16*ii, 16, StringFormatter.k1);
        formatterHex.add("  ").addStringLine(uContent, 16*ii, 16, ascii7.name());
        widgContent.append(formatterHex.getContent()).append('\n');
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
    String content = new String(uContent, charset);
    widgContent.setText(content, 0);
    widgContent.setCursorPos(cursorPos);
  }
  
  
  void syncContentFromWidg(){
    switch(format){
    case 'h':{
      int posInWidg = widgContent.getCursorPos();
      
      //cursorPos;
    }break;
    case 'w': syncTextFromWidg(iso8859_1); break;
    case 'u': syncTextFromWidg(utf8); break;
    default:  
    }
  }
  
  
  void syncTextFromWidg(Charset charset){
    int posInWidg = widgContent.getCursorPos();
    cursorPos = posInWidg;
    if(widgContent.isChanged()){
      String sContent = widgContent.getText();
      uContent = sContent.getBytes(utf8);
    }
    
  }
    
  
  
  void saveTextAs(Charset encoding, byte[] lineEnd){
    try{
      //read the content in the given encoding:
      InputStream inpBytes = new ByteArrayInputStream(uContent);
      InputStreamReader inpText = new InputStreamReader(inpBytes, encodingContent);
      BufferedReader inpLines = new BufferedReader(inpText);
      FileRemote filedst = main.currentFile;
      WritableByteChannel outchn =filedst.openWrite(0);
      ByteBuffer outBuffer = ByteBuffer.allocate(1200);
      //Writer out = new FileWriter();
      String sLine;
      do{
        sLine = inpLines.readLine();
        if(sLine !=null){
          byte[] bytes = sLine.getBytes(encoding);
          if(outBuffer.remaining() < bytes.length+1){
            outBuffer.rewind();
            outchn.write(outBuffer);
            outBuffer.clear();  
          }
          int posBytes = 0;
          int zOutBuffer;
          while( (zOutBuffer = outBuffer.remaining()) < (bytes.length - posBytes +1)){
            outBuffer.put(bytes, posBytes, zOutBuffer);
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
      outBuffer.rewind();
      outchn.write(outBuffer);
      outBuffer.clear(); 
      outchn.close();
      
    } catch(Exception exc){
      main.gralMng.writeLog(0, exc);
    }
    
  }
  
  
  
  /**Action for Key F3 for view command. Its like Norton Commander.
   */
  GralUserAction actionFind = new GralUserAction()
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
  GralUserAction actionOpenView = new GralUserAction()
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        view(null);
        return true;
      } else return false; 
      // /
    }
  };


  /**Action for Key F3 for view command. Its like Norton Commander.
   */
  GralUserAction actionSetTextViewUTF8 = new GralUserAction()
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    { if(key != KeyCode.mouse1Down){  //supress both mouse up and down reaction
      try{ 
        format = '?';
        presentContentText(utf8);
        format = 'u';
      } catch(UnsupportedEncodingException exc){
        System.err.println("FcmdView.actionSetTextViewUTF8 - UnsupportedEncodingException; unexpected");
      }
      return true;
      } else return false; 
      // /
    }
  };


  /**Action for Key F3 for view command. Its like Norton Commander.
   */
  GralUserAction actionSetHexView = new GralUserAction()
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    {  if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
       try{ 
        syncContentFromWidg();
        format = '?';
        presentContentHex();
        format = 'h';
      } catch(Exception exc){
        System.err.println("FcmdView.actionSetHexView - Exception; unexpected");
      }
      return true;
      } else return false; 
      // /
    }
  };


  /**Action for Key F3 for view command. Its like Norton Commander.
   */
  GralUserAction actionSetTextViewISO8859_1 = new GralUserAction()
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    { if(key != KeyCode.mouse1Down){  //supress both mouse up and down reaction
      try{ 
        format = '?';
        presentContentText(iso8859_1);
        format = 'w';
      } catch(UnsupportedEncodingException exc){
        System.err.println("FcmdView.actionSetTextViewISO8859_1 - UnsupportedEncodingException; unexpected");
      }
      return true;
      } else return false; 
      // /
    }
  };


  /**Action for Key F3 for view command. Its like Norton Commander.
   */
  GralUserAction actionSetEditable = new GralUserAction()
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        widgContent.setEditable(true);
      return true;
      } else return false; 
      // /
    }
  };


  GralUserAction actionSave = new GralUserAction()
  {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { 
      try{
        //InputStream inpBytes = new ByteArrayInputStream(uContent);
        //InputStreamReader inpText = new InputStreamReader(inpBytes);
        //BufferedReader inpLines = new BufferedReader(inpText);
        FileRemote filedst = main.currentFile;
        WritableByteChannel outchn =filedst.openWrite(0);
        ByteBuffer outBuffer = ByteBuffer.allocate(1200);
        //Writer out = new FileWriter();
        int nrofBytes = uContent.length;
        int posBytes = 0;
        while(nrofBytes > 0){
          int zWrite = nrofBytes >=1200 ? 1200 : nrofBytes;
          outBuffer.put(uContent, posBytes, zWrite);
          nrofBytes -= zWrite;
          outBuffer.rewind();
          outchn.write(outBuffer);
          outBuffer.clear();
        }
        outchn.close();
        
      } catch(Exception exc){
        main.gralMng.writeLog(0, exc);
      }
      return true;
      // /
    }
  };


  GralUserAction actionSaveTextAsUTF8unix = new GralUserAction()
  { @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { saveTextAs(utf8, endl_0a);
      return true;
    }
  };


  GralUserAction actionSaveTextAsWindows = new GralUserAction()
  { @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { saveTextAs(iso8859_1, endl_0d0a);
      return true;
    }
  };


  GralUserAction actionSaveTextAsISO8859_1_unix = new GralUserAction()
  { @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { saveTextAs(iso8859_1, endl_0d0a);
      return true;
    }
  };


  
  GralTextFieldUser_ifc userKeys = new GralTextFieldUser_ifc() {
    
    @Override
    public boolean userKey(int keyCode, String content, int cursorPos,
        int selectStart, int selectEnd) {
      boolean bDone = true;
      switch(keyCode){
      case KeyCode.ctrl + 'F': actionFind.userActionGui(KeyCode.menuEntered, null); break;
        default: bDone = false;
      }
      return bDone;
    }
  };
  
}

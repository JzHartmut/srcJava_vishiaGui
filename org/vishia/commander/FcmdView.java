package org.vishia.commander;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.FileRemote;
import org.vishia.util.StringFormatter;

/**All functionality of view (F3-Key) and quick view. 
 * @author Hartmut Schorrig
 * */
public class FcmdView
{
  protected final Fcmd main;

  /**The window of this functionallity. */
  private GralWindow_ifc windView;

  /**The widget to show content. */
  private GralTextBox widgContent;
  
  /**A buffer to get bytes from the file using the java.nio.Channel mechanism. 
   * The channel mechanism is proper to work with remote file access especially.
   * Note: The ByteBuffer may be a part of the channel mechanism itself, because it is placed JVM-internally,
   * for example for socket communication. TODO check and change it.
   */
  private final ByteBuffer byteBuffer = ByteBuffer.allocate(1200);

  /**Number of read bytes. */
  private int nrofBytes;


  /**The gotten bytes from bytebuffer. */
  private byte[] buffer = new byte[1200];
  
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
  
  
  private static Charset ascii7 = Charset.forName("US-ASCII");
  
  
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
    int windProps = GralWindow.windConcurrently | GralWindow.windHasMenu;
    GralWindow wind =  main.gralMng.createWindow("windView", "view - The.file.Commander", windProps);
    wind.addMenuItemGThread("view-Search", "&Edit/&Search", actionOpenView);
    wind.addMenuItemGThread("view-Search", "&Edit/set &Writeable", actionOpenView);
    wind.addMenuItemGThread("view-Search", "&View/&Hex-Byte", actionOpenView);
    wind.addMenuItemGThread("view-Search", "&View/text-&Windows", actionOpenView);
    wind.addMenuItemGThread("view-Search", "&View/text-&UTF", actionOpenView);
    wind.addMenuItemGThread("view-Search", "&View/text-&ASCII-7", actionOpenView);
    wind.addMenuItemGThread("view-Search", "&View/text-&Encoding", actionOpenView);
    main.gralMng.setPosition(0, 0, 0, 0, 1, 'r');
    widgContent = main.gralMng.addTextBox("view-content", false, null, '.');
    widgContent.setTextStyle(GralColor.getColor("bk"), main.gralMng.propertiesGui.getTextFont(2.0f, 'm', 'n'));
    windView = wind; 
    windView.setWindowVisible(false);
    //windView1.
  }

  
  
  
  /**Opens the view window and fills its content.
   * @param src The path which is selected as source. It may be a directory or a file.
   */
  void view(FileRemote src)
  { String sSrc, sTrash;
    src = main.currentFile;
    ReadableByteChannel reader = src.openRead(0);
    try{
      if(reader == null){
        widgContent.setText("File is not able to read:\n");
        widgContent.append(src.getAbsolutePath());
      } else {
        byteBuffer.clear();
        nrofBytes = reader.read(byteBuffer);
        reader.close();
        byteBuffer.rewind();
        presentContent();
      }
    } catch(IOException exc){
      
    }
    windView.setWindowVisible(true);

  }
  

  
  
  void presentContent() throws IOException
  {
    widgContent.setText("file ...\n");
    presentContentHex();  
  }
  
  
  
  void presentContentHex() throws IOException
  {
    try{
      byteBuffer.get(buffer);
      for(int ii = 0; ii < 16; ++ii){
        formatterHex.reset();
        formatterHex.addHex(ii, 4).add(": ");
        formatterHex.addHexLine(buffer, 16*ii, 16, StringFormatter.k1);
        formatterHex.add("  ").addStringLine(buffer, 16*ii, 16, ascii7.name());
        widgContent.append(formatterHex.getContent()).append('\n');
      }
    } catch(Exception exc){
      widgContent.append(exc.getLocalizedMessage());
    }
  }
  
  
  
  void presentContentText() throws IOException
  {
    
    CharBuffer tContent = ascii7.decode(byteBuffer);
    try{
      do{
        char cc = tContent.get();
        widgContent.append(cc);
      } while(true);
    } catch(Exception exc){
      widgContent.append('?');
    }
  }
  
  
  /**Action for Key F3 for view command. Its like Norton Commander.
   */
  GralUserAction actionOpenView = new GralUserAction()
  {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { view(null);
      return true;
      // /
    }
  };


  
}

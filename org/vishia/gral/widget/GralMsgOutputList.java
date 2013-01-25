package org.vishia.gral.widget;

import java.text.SimpleDateFormat;
import java.util.IllegalFormatConversionException;
import java.util.IllegalFormatPrecisionException;
import java.util.Locale;
import java.util.TimeZone;

import org.vishia.bridgeC.OS_TimeStamp;
import org.vishia.bridgeC.Va_list;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralTable_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.Assert;

/**This class supports output of messages in a GralTable to view and scroll.
 * The table contains only a defined maximum of messages, older messages will be removed if the capacity is used.
 * It is recommended to write messages in a persistent medium additionally 
 * (usual a {@link org.vishia.msgDispatch.LogMessageFile}). 
 *   
 * @author Hartmut Schorrig
 *
 */
public class GralMsgOutputList  implements LogMessage
{

  /**Version, history and license.
   * <ul>
   * <li>2013-01-26 Hartmut:fine tuning while adapt message system in component javaSrc_vishiaRun.
   * <li>2011-04-05 Hartmut creation
   * </ul>
   * <br><br>
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
   */
  public static final int version = 20130126;

  /**The access to the gui, to change data to show. */
  private final GralMng_ifc guiAccess;
  
  private final SimpleDateFormat dateFormat;
  
  private final Locale localization;
  

  public GralMsgOutputList(GralMng_ifc guiAccess, String sTimeZoneShow, String sTimeFormat){
    this.localization = Locale.ROOT;
    this.guiAccess = guiAccess;
    this.dateFormat = new SimpleDateFormat(sTimeFormat, localization);
    this.dateFormat.setTimeZone(TimeZone.getTimeZone(sTimeZoneShow));


  }
  
  @Override public void close() {
  }

  @Override public void flush() {
  }

  @Override public boolean isOnline() {
    return true;
  }

  @Override public boolean sendMsg(int identNumber, String text, Object... args) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override public boolean sendMsgTime(int identNumber, OS_TimeStamp creationTime,
      String text, Object... args) {
    // TODO Auto-generated method stub
    return false;
  }

  /**This is the only one method, which is called from the message dispatcher. Only this is implemented.
   * @see org.vishia.msgDispatch.LogMessage#sendMsgVaList(int, org.vishia.bridgeC.OS_TimeStamp, java.lang.String, org.vishia.bridgeC.Va_list)
   */
  @Override public boolean sendMsgVaList(int identNumber, OS_TimeStamp creationTime,
      String text, Va_list args) {
    String sTime = dateFormat.format(creationTime);
    String state = identNumber <0 ? "-" : "+'";  //going/coming
    if(identNumber < 0){ identNumber = -identNumber; }
    //The configuration for this msg ident.
    String sText;
    try{ sText = String.format(localization, text,args.get());
    } catch(IllegalFormatConversionException exc){
      sText = "error in text format: " + text;
    }catch(IllegalFormatPrecisionException exc){
      sText = "error-precision in text format: " + text;
    }
    //String sInfoLine = sTime + '\t' + identNumber + '\t' + state + '\t' + sText;
    String[] sInfoLine = {sTime, "" + identNumber, state, "" + sText};

    GralWidget oTable = guiAccess.getWidget("msgOfDay");
    if(oTable == null){
      Assert.consoleErr("GuiMainDialog:insertInfo: unknown widget; %s; message:%d;%s;\n", "msgOfDay", identNumber, sText);
    } else {
      GralTable_ifc table = (GralTable)oTable;
      GralTableLine_ifc line = table.insertLine(null, Integer.MAX_VALUE, sInfoLine, null);
      int nLine = line.getLineNr();
      table.setCurrentLine(nLine);
    }
    //guiAccess.insertInfo("msgOfDay", Integer.MAX_VALUE, sInfoLine);
    return true;
  }
  
}


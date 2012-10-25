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

public class GralMsgOutputList  implements LogMessage
{

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
      System.out.println("GuiMainDialog:insertInfo: unknown widget; %s" + "msgOfDay");
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


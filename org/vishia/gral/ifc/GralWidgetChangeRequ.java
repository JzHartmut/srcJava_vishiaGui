package org.vishia.gral.ifc;

public class GralWidgetChangeRequ
{
  
  /**Version and history:
   * <ul>
   * <li>2012-01-08 Hartmut new fields und methods {@link #delayExecution(int)} etc.
   * <li>2010-06-00 Hartmut created.
   * </ul>
   * 
   */
  public final static int version = 0x20120108;
  
  /**The widget where the change should be done. */
  public final GralWidget_ifc widgetDescr;
  
  /**The command which should be done to change. It is one of the static definitions cmd... of this class. */
  public final int cmd;
  
  /**Numeric value describes the position of widget where the change should be done.
   * For example, if the widget is a table, it is either the table line or it is
   * Integer.MAX_VALUE or 0 to designate top or end.
   */
  public final int ident;
  
  /**If not null, it is the first time to execute it. Elsewhere it should be delayed. */
  private long timeExecution;
  
  /**The textual information which were to be changed or add. */
  public final Object visibleInfo;
  
  public final Object userData;
  
  public GralWidgetChangeRequ(GralWidget_ifc widgetDescr, int cmd, int indent, Object visibleInfo, Object userData) 
  { this.widgetDescr = widgetDescr;
    this.cmd = cmd;
    this.ident = indent;
    this.visibleInfo = visibleInfo;
    this.userData = userData;
  }
  
  
  /**Checks whether it should be executed.
   * @return time in milliseconds for first execution or value <0 to execute immediately.
   */
  public int timeToExecution(){ 
    return timeExecution == 0 ? -1 : (int)( timeExecution - System.currentTimeMillis()); 
  }
  
  
  /**Sets the delay to execute. It can be set newly whenever this instance isn't used to execute yet.
   * @param millisec delay.
   */
  public void delayExecution(int millisec){
    timeExecution = System.currentTimeMillis() + millisec;
  }
  
}

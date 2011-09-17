package org.vishia.gral.ifc;

public class GralWidgetChangeRequ
{
  /**The widget where the change should be done. */
  public final GralWidget widgetDescr;
  
  /**The command which should be done to change. It is one of the static definitions cmd... of this class. */
  public final int cmd;
  
  /**Numeric value describes the position of widget where the change should be done.
   * For example, if the widget is a table, it is either the table line or it is
   * Integer.MAX_VALUE or 0 to designate top or end.
   */
  public final int ident;
  
  /**The textual information which were to be changed or add. */
  public final Object visibleInfo;
  
  public final Object userData;
  
  public GralWidgetChangeRequ(GralWidget widgetDescr, int cmd, int indent, Object visibleInfo, Object userData) 
  { this.widgetDescr = widgetDescr;
    this.cmd = cmd;
    this.ident = indent;
    this.visibleInfo = visibleInfo;
    this.userData = userData;
  }
  
  
}

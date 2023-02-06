package org.vishia.gral.swt;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.Debugutil;

/**This class is the implementation class of a simple graphic implementation for SWT.
 * It doesn't depend of complex functionality of the org.vishia.gral. But that implementations based on this.
 * This class can be used for a simple SWT graphic implementation.
 */
class SwtGraphicThread //extends GralGraphicThread.ImplAccess //implements Runnable
{
  /**Version, history and license.
   * <ul>
   * <li>2016-07-16 Hartmut chg: The main window will be created with same methods like all other windows. 
   * <li>2015-01-17 Hartmut chg: Now {@link GralGraphicThread} is an own instance able to create before the graphic is established.
   *   This graphical implementation extends the {@link ImplAccess}. 
   * <li<2012-07-14 Hartmut chg: {@link #traverseKeyFilter} now excludes [ctrl-tab]. Only [tab] is a traversal key.
   * <li>2012-04-16 Hartmut chg: {@link #initGraphic()} now creates the main window, creates the
   *   {@link SwtMng} instead it is doing in the non-graphic thread. 
   * <li>2012-04-10 Hartmut chg: Now the traversal keys ctrl-Pgdn/up are disabled.
   * <li>2011-11-12 Hartmut chg: Now the primary window has a menu bar anyway. 
   * </ul>
   * <b>Copyright/Copyleft</b>:
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
  //@SuppressWarnings("hiding")
  public final static String version = "2015-01-17";

  //SwtPrimaryWindow instance;
  
  SwtMng swtMng;
  
  
  
  void stop(){}
  
}

/****************************************************************************/
/* Copyright/Copyleft: 
 * 
 * For this source the LGPL Lesser General Public License, 
 * published by the Free Software Foundation is valid.
 * It means:
 * 1) You can use this source without any restriction for any desired purpose.
 * 2) You can redistribute copies of this source to everybody.
 * 3) Every user of this source, also the user of redistribute copies 
 *    with or without payment, must accept this license for further using.
 * 4) But the LPGL ist not appropriate for a whole software product,
 *    if this source is only a part of them. It means, the user 
 *    must publish this part of source,
 *    but don't need to publish the whole source of the own product.
 * 5) You can study and modify (improve) this source 
 *    for own using or for redistribution, but you have to license the
 *    modified sources likewise under this LGPL Lesser General Public License.
 *    You mustn't delete this Copyright/Copyleft inscription in this source file.    
 *
 * @author www.vishia.de/Java
 * @version 2006-06-15  (year-month-day)
 * list of changes: 
 * 2009-03-07: Hartmut: bugfix: setOutputWindow() 
 * 2006-05-00: Hartmut Schorrig www.vishia.de creation
 *
 ****************************************************************************/

package org.vishia.mainGuiSwt;

import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.vishia.gral.area9.GuiMainAreaBase;
import org.vishia.gral.area9.GuiMainAreaifc;
import org.vishia.gral.base.GralSubWindow;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWindowMng;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralGridPos;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralTextBox_ifc;
import org.vishia.gral.widget.InfoBox;
import org.vishia.mainCmd.MainCmd;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.msgDispatch.LogMessage;


//import java.awt.event.*;
//import java.util.*;  //List
//import javax.swing.*;

/**
<h1>class MainCmdSwt - Description</h1>
<font color="0x00ffff">
  Diese abstrakte Klasse dient als Basisklasse f�r alle grafischen Applikationen (GUI = Graphical User Interface),
  die �ber eine cmdLine gestartet werden. Diese Klasse basiert wiederum auf MainCmd.
  Diese Klasse enth�lt neben MainCmd folgende Leistungseigenschaften:
  <ul>
  <li>Bereitstellen eines JFrame, Rahmen f�r das Gesamtfenster.</li>
  <li>Bereitstellen eines Output-Textbereiches, der anstelle Konsolenausgaben verwendet werden kann.
      Die Methoden writeInfo() und writeInfoln() aus MainCmd werden hierher geleitet.
      Dieser Outputbereich muss vom Anwender initialisiert werden, auf seine Verwendung kann auch verzichtet
      werden, falls das nicht notwendig ist.</li>
  <li>Bereitstellen eines leeren Men�s mit einem FileMen� mit Exit und einem Hilfemen� mit "about"</li>
  <li>Bereitstellen eines leeren Dialog-Containers</li>
  </ul>
</font>
<hr/>
<pre>
date       who      change
2006-01-07 HarmutS  initial revision
*
</pre>
<hr/>


*/

//public abstract class MainCmdSwt extends MainCmd implements GuiMainAreaifc
public class MainCmdSwt extends GuiMainAreaBase implements GuiMainAreaifc
{
  
  
  /**Version history:
   * <ul>
   * <li>2011-05-01 Hartmut chg: The method switchToWindowOrStartCmdLine(...) is a poor windows-based method
   *     and doesn't run under Linux. It is removed here, instead add in org.vishia.windows.WindowMng.java 
   * <li>All other changes from 2010 and in the past
   * </ul>
   * 
   */
  final static int version = 0x20110502;

  
	
  //private final PrimaryWindowSwt swtWindow;
	
	
  
  /** If it is set, it is a area for some Buttons, edit windows and others.*/
  private Composite mainDialog = null;
  
  
  class ActionClearOutput implements SelectionListener
  { 
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void widgetSelected(SelectionEvent e)
    { textAreaOutput.setText("--clean--\n");
    }
  }
  

  
  
  
  
  
  MouseListener mouseListener = new MouseListener()
  {
    int captureAreaDivider;
    
		@Override	public void mouseDoubleClick(MouseEvent e) 
		{ //do nothing
		}

		@Override	public void mouseDown(MouseEvent e) 
		{ 
			int yf1 = ypFrameArea[1];
	    int yf2 = ypFrameArea[2];
	    int xf1 = xpFrameArea[1]; //percent right
	    int xf2 = xpFrameArea[2]; //percent right
			if(e.x < 20){
		    //calculate pixel size for the component:
		    int y1 = (int)(yf1  * pixelPerYpercent);
		    int y2 = (int)(yf1  * pixelPerYpercent);
		    if(e.y > y1-20 && e.y < y1 + 20){
		      captureAreaDivider = 1;    	
		    }
		  }
		}

		@Override	public void mouseUp(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
  	
  };
  


  
  /*	
  protected MainCmdWin(String[] args, MainApplicationWin_ifc application)
  { super(args);
    this.application = application;
  }
  */
  
  public MainCmdSwt(MainCmd cmdP, GralSubWindow window, String sOutputArea) //String[] args)
  { //super(args);
    //assert(false);
    super(cmdP, window, sOutputArea); //gralDevice);
  
    //swtWindow = (PrimaryWindowSwt)guiDevice; //PrimaryWindowSwt.create(cmdP.getLogMessageOutputConsole());
    //super.gralDevice = swtWindow;
  }
  
  
  

  

  void stop()
  { //to set breakpoint
  }



  /*
  @Override public void buildMainWindow(String sTitle, int left, int top, int xSize, int ySize)
  { swtWindow.buildMainWindow(sTitle, left, top, xSize, ySize);
  }
  */
  
  //@Override public void addGuiBuildOrder(Runnable order){ swtWindow.addGuiBuildOrder(order); }


  
}
















class GuiActionExit //implements ActionListener
{
  /**Association to the main class*/
  MainCmdSwt main;

  GuiActionExit(MainCmdSwt mainP) { this.main = mainP;}

  public void actionPerformed( int e) //ActionEvent e )
  { System.exit(0);
  }
}



class GuiActionAbout //implements ActionListener
{
  /**Association to the main class*/
  final MainCmdSwt main;

  GuiActionAbout(MainCmdSwt mainP) { main = mainP;}

  public void actionPerformed( int e) //ActionEvent e )
  { main.mainCmd.writeAboutInfo();
  }


  



}

                           
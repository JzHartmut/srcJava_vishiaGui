package org.vishia.mainGuiSwt;

import java.io.File;
import java.text.ParseException;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.mainGui.GuiPanelMngWorkingIfc;
import org.vishia.mainGui.UserActionGui;
import org.vishia.mainGui.WidgetDescriptor;

public class SampleGuiSwtButtonInput 
{
	
	/**This class saves command line arguments it is given form. 
	 * It is an aggregate for the {@link MainCmd} class. 
	 * The {@link MainCmd}-class should not know its environment class (it is static),
	 * because the creation of the environment class should done only if the Args are known.
	 */
	static class Args
	{
	  //empty here.	
	}
	
	
	
  /**The class for cmd line and console support.
   * It extends MainCmdSwt, because a GUI-output is supported with that.
   * It is the base class for GUI too.
   * <br><br>
   * The environment class is independent from the GUI and console.
   */
	private static class MainCmd extends MainCmdSwt
  {
    final Args args;
		
		
		protected MainCmd(String[] argCmd, Args args) {
			super(argCmd);  //the command line arguments will be tested in the super class.
			this.args = args;
		}

		@Override
		protected boolean testArgument(String argc, int nArg) throws ParseException {
			return false;  //any argument is false.
		}
		
		@Override
		protected boolean checkArguments() {
			//Checks nothing.
			return true;
		}

	  /**Invoked from parseArguments if no argument is given. 
	   * This implementation prevents an exception. */
	  protected void callWithoutArguments()
	  throws ParseException
	  { //writeAboutInfo();
	    //writeHelpInfo();
	  }

		
		
  }
	
	//Table testTable;
	
	
  /**This thread takes action in another thread to the SWT-GUI. 
   * The GUI has the property, that all actions which changes the build of the GUI,
   * can be only execute in the same thread where Display is created and where 
   * the dispatch-loop runs. If data are produced in another thread, which should be 
   * shown in the GUI, a queue of order should be used. The run-method shows,
   * how a order can be posted to the GUI.
   * 
   */
  private class TestThread extends Thread
  {
  	int nrofInsert = 3;
  	boolean writeCurve = false;
  	
  	Runnable writeCurveRoutine = new Runnable(){
  		float y1,y2,y3 = 0.02F;
  		/**This run routine isn't a thread's run but the run of this class.
  		 * It is called direct, it is the only one routine of the class.
  		 */
  		final float[] valuesCurve = new float[3];
  		
  		@Override public void run()
    	{
  			for(int ii=0; ii<100; ii++){
  				//create curve points.
  				
  				y1 += y2/20;
  				y2 += y3;
  				if(y2 >=1){y3 = -0.02F;}
  				else if(y2 <=-1){y3 = 0.02F;}
  				valuesCurve[0] = y1;
  				valuesCurve[1] = y2;
  				valuesCurve[2] = y3;
  				dlgOut.setSampleCurveViewY("curve", valuesCurve);
  			}
    	}
  	};
  	
  	@Override public void run()
  	{
  		do{
	  		if(nrofInsert >0){
	  			nrofInsert -=1;
	  		  dlg.insertInfo("testTable",Integer.MAX_VALUE, "testB\tC\t" + nrofInsert);
	  		  try{ Thread.sleep(500); }
	  		  catch(InterruptedException exc){}
	  		}
	  		if(writeCurve){
	  			writeCurve = false;
	  			writeCurveRoutine.run();
	  		}
	  	  try{ Thread.sleep(100); }
  		  catch(InterruptedException exc){}
  		
  		} while(true);	
  	}
  }

  TestThread testThread = new TestThread();
	
	UserActionGui buttonTest1Action = new UserActionGui()
	{	@Override
		public void userActionGui(String sCmd, WidgetDescriptor widgetInfos, Object... params) 
		{
      console.writeInfoln("Button pressed, Test1Action.");		  
			testThread.nrofInsert = 3;
    }

	};
	
	UserActionGui buttonTestCurveAction = new UserActionGui()
	{	@Override
		public void userActionGui(String sCmd, WidgetDescriptor widgetInfos, Object... params) 
		{
      if(sCmd != null && sCmd.charAt(0)!='-'){
      	console.writeInfoln("Button curveAction.");		  
      	testThread.writeCurve = true;
      }
    }
	};
	
	/**The main dialog GUI-surface. It contains some widgets.*/
	private final GuiPanelMngSwt dlg; 
	
	private final GuiPanelMngWorkingIfc dlgOut;
	
	private final MainCmd_ifc console;
	
	
	/**The constructor for the sample class. It builds the graphic.
	 * The graphic will be built in the mainCmd-instance. 
	 * @param mainCmd
	 */
	SampleGuiSwtButtonInput(MainCmd mainCmd)
	{ //add a dialog area:

		console = mainCmd;
		mainCmd.initGrafic("Example MainCmdSwt", 100, 50, 800, 600);  //Title, size
    mainCmd.setFrameAreaBorders(30,70,30,70); //it is default, not used here, but further.
    
		mainCmd.addStandardMenus(new File("V:/vishia"));       //some simple standard menus
		mainCmd.addOutputFrameArea(1,3,3,1);                   //output area
		
		PropertiesGuiSwt propertiesGui = new PropertiesGuiSwt(mainCmd.guiDevice, 'C');
    
		//Composite dlgContainer = new Composite(mainCmd.getContentPane(), 0);
		Color colorBackground = propertiesGui.colorSwt(0xeeeeee);
	  
		Composite dlgContainer = new GridPanelSwt(mainCmd.getContentPane(), 0, colorBackground, propertiesGui.xPixelUnit(), propertiesGui.yPixelUnit(), 10,10);
	  dlg = new GuiPanelMngSwt(null, mainCmd.getContentPane(), 300,200, propertiesGui, null, mainCmd.getLogMessageOutputConsole());
	  dlg.registerPanel("test", dlgContainer);
	  dlg.setPosition(10,0,3,3,'d');
	  //An action is registered until now independent from any button or menu item,
	  //because it may be used more as one time.
	  dlg.registerUserAction("action1", buttonTest1Action);
	  dlg.registerUserAction("actionCurve", buttonTestCurveAction);
	  //add the button with a named action.
	  dlg.setSize(-3,0,8,0);
	  dlg.addButton("Name", buttonTestCurveAction, "cmd", null, null, "TEST");
	  dlg.setSize(-3,0,8,0);
	  dlg.addButton("Curve", buttonTestCurveAction, "cmd", null, null, "actionCurve");
	  dlg.setPosition(0,15,3,8,'r');
	  dlg.addTable("testTable", 8, new int[]{15,25,10});
	  dlg.setPosition(25, 0,-2,20,'r');
	  WidgetDescriptor widgetInfo = new WidgetDescriptor("edit", 'T');
	  widgetInfo.setDataPath("info");
	  dlg.addTextField(widgetInfo, false, null, '\0');
	  dlg.setPosition(30, 20,20,40,'r');  //foot line!
	  dlg.addCurveViewY("curve", 500, 5);
	  dlg.addMouseButtonAction("curve", buttonTestCurveAction, "-", "curve", "-");
	  mainCmd.addFrameArea(1, 1,3,2, dlgContainer);
	  mainCmd.addDispatchListener(dlg.getTheGuiChangeWorker());
	  dlgOut = dlg;  //to access for outputs
	  
	}

	
	public static void main(String[] argsCmdline)
	{ boolean bOk = true;
		Args args = new Args();
	  //On creation, the command-line-arguments will be parsed. 
	  //It invokes the method testArgument(). 
		MainCmd mainCmd = new MainCmd(argsCmdline, args);
		try{ mainCmd.parseArguments();}
		catch(Exception exception)
    { mainCmd.writeError("Cmdline argument error:", exception);
      mainCmd.setExitErrorLevel(MainCmd_ifc.exitWithArgumentError);
      //gui.exit();
      bOk = false;  //not exiting, show error in GUI
    }
    //Establish the graphical appearance:
		SampleGuiSwtButtonInput main = new SampleGuiSwtButtonInput(mainCmd);
		
		
		mainCmd.validateGraphic();                             //show it.

		main.testThread.setPriority(Thread.MAX_PRIORITY);
		main.testThread.start();
		
		mainCmd.dispatch();
		/*
		while(mainCmd.isRunning()){
			try{ Thread.sleep(100); }
			catch(InterruptedException exc){}
		}
		*/
	}

	
	void stop()
	{ //debug
	}
	
}

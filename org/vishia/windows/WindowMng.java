package org.vishia.windows;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.mainCmd.Report;

public class WindowMng
{
	
	static{
    //NOTE: it isn't used currently, the dll doesn't exists.
		File fileDll = new File("exe/windowMng.dll");
    if(fileDll.exists()){
		  //System.load(fileDll.getAbsolutePath());
    }
		
	}

  final MainCmd_ifc cmdIfc;	

  public final static native void listAllWindows(); 
  
	public static class WindowPropertied
	{
		String title;
		int pid;
		int window_id;
	}

	
	
	public WindowMng(MainCmd_ifc cmdIfc)
	{
		this.cmdIfc = cmdIfc;
	}



	/**Searches the windows-handler (hWnd) for the given title of window.
	 * It works with a executable written in C, which produces a list of all windows
	 * with its hWind.
	 * @param sTitle The title, or the start of title.
	 * @return 0 if not found, elsewhere the hWnd.
	 */
	public static int getWindowsHandler(String sTitle)
	{
		//listAllWindows();
		//Map<String, WindowPropertied> list = new TreeMap<String, WindowPropertied>();
		int hWnd = 0;
		
		ProcessBuilder processBuilder = new ProcessBuilder("exe/getListWindow.exe");
		//ClassLoader loader = getClass().getClassLoader();
		//getClass().getPackage().
		
		StringBuffer uOut = new StringBuffer();
		try
    { Process process = processBuilder.start();
			BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
	    String sOut;
	    while( (sOut = output.readLine()) != null)
      { String[] s1 = sOut.split(";");
	      if(s1.length >=4){
	        String sTitleWin = s1[3].trim().substring(1);  //without " 
	  	      if(sTitleWin.startsWith(sTitle)
	  	                          ){ //||	s1[3].contains("Editor")  )
	  	      	hWnd = Integer.parseInt(s1[0].trim());
	  	      }
	  	    	if(uOut!=null) uOut.append(sOut + "\n");
	        	
	      }
      }
    }
    catch(Exception exception){ System.out.println(exception.toString()); }
    //System.out.println("***** ende **********");

		//cmdIfc.executeCmdLine(processBuilder, "getListWindow", null, 0, out, null);
		//while(processBuilder.)
		//return list;
    return hWnd;
	}
	
}

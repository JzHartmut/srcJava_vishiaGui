package org.vishia.gral.swt;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralWidget_ifc;

public class CurveView extends Canvas implements GralWidget_ifc
{
  
  private final CurveView widgSwt;

	//final Canvas canvas;
	
	private final float[][] values;
	
	/**The percent from 0..100 where the 0-line is presented. */
	private final int[] y0Line;
	
	/**The pixel value for y0Line refered to pixel-height. */
	private final float[] y0Pix;
	
	
	
	/**The value of input data which is shown at the 0-line. */
	private final float[] yOffset;
	
	/**The factor to multiply for 1 pixel. for 10 percent.. */
	private final float[] yFactor;
	
	/**The scale for 10 percent of view without zoom.. */
	private final float[] yScale;

	/**last values for paint. 
	 * The current paint goes from lastValueY[track][1] to the current point.
	 * The lastValueY[track][0] is used to repaint the last curve peace while shifting draw content.
	 */
	private final int[][] lastValueY;
	
	/**last x-positions for paint. 
	 * The lastPositionX[0] is used to repaint the last curve peace while shifting draw content.
	 * Where, xShift (number of shifted pixel) is considered by subtraction.
	 */
	private final int[] lastPositionX = new int[2];
	
	/**The last index to data, to detect grid line. */
	private int iDataLast;
	
	private final Color[] lineColors;
	
	
	/**Current number of values in the data field. 
	 * If less values are set ({@link #setSample(float[])}, 
	 * then the nrofValues is less than values.length.
	 * Else it is ==values.length. */
	private int nrofValues = 0;
	
	
	private int nrofValuesForGrid;
	
	
	/**Distance of nrofValues for one vertical grid line (strong or not strong). */
	private int gridDistanceY;
	
	/**Distance of percent of y-view for one vertical grid line (strong or not strong). */
	private float gridDistanceX;
	
	/**period of strong lines. */
	private int gridStrongPeriodX, gridStrongPeriodY;
	
	/**Distance of nrofValues for one vertical strong grid line.
	 * This value is used to limit nrofValuesForGrid. */
	private int gridDistanceStrongY;
	
	private Color gridColor = new Color(getDisplay(), 0, 255, 255);
	
	private Color gridColorStrong = new Color(getDisplay(), 0, 255, 255);
	
	/**Zoom factor. If zoom = 1.0, the curve will be shown in the current canvas area.
	 * It the zoom-factor is greater 1.0, not all points will be shown. 
	 * The origin point of the values is given with {@link #xOrigin} and {@link #yOrigin}.
	 */
	private float xZoom = 1.0F, yZoom= 1.0F;
	
	/**Origin point from 0.0 to 1.0 for the zoomed area. */
	private float xOrigin = 0.0F, yOrigin = 0.0F;
	
	private Color colorBack = new Color(getDisplay(), 0xff, 0xff, 0xff);
	
	private boolean focusChanged = false;  //it doesn't work
	
	/**Set to true to force a paint all. */
	private boolean paintAllCmd = false;
	
	private int newSamples;
	
	/**last point in x where values were drawn. */
	private float xViewLastF = 0;
	
	
	
	/**Number of iData-indices, which are shifted in the {@link #values}. 
	 * This number have to be shifted in the pixel area if a draw-all is not requested. 
	 * This field is set to 0 if the draw action is done.*/
	private AtomicInteger nrofDataShift = new AtomicInteger(0);
	
	private float nrofDataShiftFracPart = 0.0F;
	
	/**Set true if {@link #redrawData()} is called. Then only the area for new data is drawn 
	 * in the {@link #drawBackground(GC, int, int, int, int)}-routine.
	 * 
	 */
	boolean redrawBecauseNewData;
	
	/**This class is only used to store values to inspect. The Inspector is a tool which works with
	 * reflection and with it internal variable can be visited in runtime. See {@link org.vishia.inspector.Inspector}.
	 */
	@SuppressWarnings("unused")
	private static class TestHelp{
		/**Counts any redraw action for complete redraw, to see how often.  */
		int ctRedrawAll;
		/**Counts any redraw action for complete redraw because nrof shift>100, to see how often.  */
		int ctRedrawAllShift;
		
		/**Counts any redraw action for partial redraw, to see how often.  */
		int ctRedrawPart;
		
    /**Ct if redraw only because new data. */
		int ctRedrawBecauseNewData;
		
		/**Coordinates while redraw. {@link CurveView#drawBackground(GC, int, int, int, int)}. */
		int xView, yView, dxView, dyView;
	} 
	TestHelp testHelp = new TestHelp();

	
	
	public CurveView(Composite parent, int xPixel, int yPixel, int nrofXvalues,
			int nrofTracks){
		super(parent, org.eclipse.swt.SWT.NO_SCROLL|org.eclipse.swt.SWT.NO_BACKGROUND);
		setData("Control", this);
		setSize(xPixel, yPixel);  //the size may be changed later by drag the window.
		//this.xPixel = xPixel;
		//this.yPixel = yPixel;
		values = new float[nrofXvalues][nrofTracks];
		y0Line = new int[nrofTracks];
		y0Pix = new float[nrofTracks];
		yOffset = new float[nrofTracks];
		yFactor = new float[nrofTracks];
		yScale = new float[nrofTracks];
		lastValueY = new int[nrofTracks][2];
		lineColors = new Color[nrofTracks];
		Color defaultColor = new Color(getDisplay(), 255,0,0);
		for(int iTrack=0; iTrack < nrofTracks; ++iTrack){
			y0Line[iTrack] = (iTrack+1) * (100 / (nrofTracks+1));
			yOffset[iTrack] = 0.0F;
			yScale[iTrack] = 10.0F;
			yFactor[iTrack] = yPixel / 10.0F / yScale[iTrack];
			lineColors[iTrack] = defaultColor;
		}
		addPaintListener(paintListener);
		addFocusListener(focusListener);
		addMouseListener(mouseLeftButtonListener);
		widgSwt = this;
	}
	
	public void setGridVertical(int dataPointsBetweenGridLines, int periodStrongLine){
		this.gridDistanceY = dataPointsBetweenGridLines;
		this.gridStrongPeriodY = periodStrongLine;
		this.gridDistanceStrongY = dataPointsBetweenGridLines * periodStrongLine;
	}
	
	
	/**Set distance for horizontal grid lines.
	 * @param percentY percent of y-view for grid lines. For example 50.0: divide y-axis in 50 steps. 
	 * @param periodStrongLine period for strong lines For example 5, any 5. line is stroke.
	 */
	public void setGridHorizontal(float percentY, int periodStrongLine){
		this.gridDistanceX = percentY;
		this.gridStrongPeriodX = periodStrongLine;
	}
	
	
	public void setGridColor(Color gridColor, Color gridStrongColor){
		this.gridColor = gridColor;
		this.gridColorStrong = gridStrongColor;
	}
	
	
	public void redrawData(){
		redrawBecauseNewData = true;
		redraw();
	}
	
	
	public void setLine(int trackNr, String sNameLine, int colorValue, int style
			, int nullLine, float scale, float offset)
	{
		y0Line[trackNr] = nullLine;
		yOffset[trackNr] = offset;
		yScale[trackNr] = scale;
	  lineColors[trackNr] = new Color(getDisplay(), (colorValue >>16) & 0xff, (colorValue >>8) & 0xff, (colorValue) & 0xff); 	
	}
	
	
	
	
	
  /**Adds a sampling value set.
   * <br><br> 
   * This method can be called in any thread. It updates only data,
   * a GUI-call isn't done. But the method is not thread-safe. 
   * If more as one threads writes data, an external synchronization should be done
   * which may encapsulate more as only this call.
   * <br><br> 
   * The forcing redraw should be triggered outside. It may be triggered only
   * if more as one samples are set. The redraw-call have to be execute in the GUI-thread.
   * Hint: use {@link org.vishia.mainGuiSwt.MainCmdSwt#addDispatchOrder(Runnable)}
   * to force redraw for this component. The Runnable-method should call widget.redraw().
   * @param sName The registered name
   * @param values The values.
   */
  public void setSample(float[] newValues) {
	  if(this.nrofValues >= this.values.length){
	  	float[] firstLine = this.values[0];
	  	//NOTE: arraycopy doesn't copy all data, but the references of float[] in the values-array.
	  	System.arraycopy(this.values, 1, this.values, 0, this.values.length-1);
	  	this.nrofValues = this.values.length-1;
	  	this.values[this.nrofValues] = firstLine;  //reuse first line as last, prevent new allocation.
	    iDataLast -=1;  //because the data are shifted, the index of last access is to be decrement.
	  	nrofDataShift.incrementAndGet(); //shift data in graphic.
	  }
	  int nrofTrack = newValues.length;
	  if(nrofTrack > this.values[0].length){
	  	nrofTrack = this.values[0].length;     //it is the lesser value of both.
	  }
	  //copy the values in the local area:
	  for(int ix=0; ix < nrofTrack; ++ix){
	  	this.values[nrofValues][ix] = newValues[ix];	
	  }
	  this.nrofValues +=1;
	  this.newSamples +=1;  //information for paint event
	  this.nrofValuesForGrid +=1;
	  if(nrofValuesForGrid > values.length + gridDistanceStrongY){
	  	nrofValuesForGrid -= gridDistanceStrongY;  //prevent large overflow.
	  }
	  //getDisplay().wake();  //wake up the GUI-thread
	  //values.notify();
	}
	
  @Override public Object getWidgetImplementation()
  { return this;
  }
  

	
  PaintListener paintListener = new PaintListener()
  {

		@Override public void paintControl(PaintEvent e) {
			GC gc = e.gc;
			drawBackground(e.gc, e.x, e.y, e.width, e.height);
		}
  	
  };

	
  
  FocusListener focusListener = new FocusListener()
  { @Override public void focusGained(FocusEvent e) {
			focusChanged = true;
		}

		@Override public void focusLost(FocusEvent e) {
			focusChanged = true;
		}
  }; 

  
  MouseListener mouseLeftButtonListener = new MouseListener()
  { 
  	/**A mouse double-click may call a dialog box for the curve view. TODO.
  	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
  	 */
  	@Override public void mouseDoubleClick(MouseEvent e) {
		}

		/**If the left button of the mouse is pressed, then the curve is drawn full.
		 * It is a helper to correct the view, because elsewhere only the new area is drawn.
		 * Sometimes it isn't detect whether a full draw is necessary. 
		 * Then the mouse click at the curve area helps. 
		 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
		 */
		@Override public void mouseDown(MouseEvent e) {
      paintAllCmd = true;
		}

		/**The mouse up is left empty, because the mouse down has its effect.
		 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
		 */
		@Override public void mouseUp(MouseEvent e) {
		}
  	
  };
  
  /**Only used in drawBackground, true if a paintAll is executed. 
   * It is outside because inspector-usage. */
  private boolean paintAllExec;
	
  
  @Override
  public void drawBackground(GC g, int xView, int yView, int dxView, int dyView) {
  	//NOTE: forces stack overflow because calling of this routine recursively: super.paint(g);
    Point size = getSize(); //size of the widget.
    float pixelPerDataStep = (float)(size.x +1) * xZoom /values.length ;
    float idataStepPerxPixel = values.length / (float)(size.x +1) / xZoom;
    float dataStepPerPixel = (float)values.length / (xZoom * size.x +1);
    //draw grid and 0-lines:
  	int xShift = -1;   //nr of new pixel if only a part is drawn  
    int xViewLast = (int)xViewLastF;
    testHelp.xView =xView; testHelp.yView =yView; testHelp.dxView =dxView; testHelp.dyView =dyView;
    if(!this.paintAllCmd && redrawBecauseNewData){
  	//if(newSamples >0 && newSamples <=10){
  		redrawBecauseNewData = false;  //it is done.
  		testHelp.ctRedrawBecauseNewData +=1;
  		paintAllExec = false;
      boolean bSuccess;
      //detect how many new data are given. Because the data are written in another thread,
      //the number of data are hold in an atomic integer. It is get and decrement 
      //in an atomic operation to prevent data lost without a locking mechanism.
      do{
      	int nrofDataShift1 = nrofDataShift.get();  //the number of new data.
      	float nrofDataShift2 = nrofDataShift1 + nrofDataShiftFracPart;  //add not used new data.
        xShift = (int)(pixelPerDataStep * nrofDataShift2);
        if(xShift >0){
        	nrofDataShiftFracPart = nrofDataShift2 - xShift / pixelPerDataStep;
          //only if the nrofDataShift is enaugh to write new data, it is set to 0.
        	//if pixelPerDataStep < 1.0, it means, only more as 1 sample is not present in 1 pixel,
        	//there isn't any to display. Let the nrofDataShift increase until at least 1 pixel is need.
        	bSuccess = nrofDataShift.compareAndSet(nrofDataShift1, 0);
        } else { bSuccess = true; } //not to do!  
      }while(!bSuccess);
      //Shift the graphic if the reason of redraw is only increment samples
      //and the number the values are shifted at least by that number, which is mapped to 1 pixel.
      if(xShift >0 && xShift < 100){
      	xViewLast -= xShift;  //the values are shifted too!
      	xViewLastF -= xShift;
      	//xViewLast = dxView - xShift;  //the values are shifted too!
      	assert(xView == 0);
      	g.copyArea(xView + xShift, yView, xViewLast - xView , dyView, xView, yView, false);
      	testHelp.ctRedrawPart +=1;
        //g.copyArea(xView + xShift, yView, dxView - xShift, dyView, xView, yView);
      } else if(xShift >=100){
      	//too many new values.
      	paintAllExec = true;
      	testHelp.ctRedrawAllShift +=1;
        xViewLast = 0;
      	this.xViewLastF = 0.0F;
      }
    } else {
  		this.paintAllCmd = false; //accepted, done
  		testHelp.ctRedrawAll +=1;
      paintAllExec = true;
      xViewLast = 0;
    	this.xViewLastF = 0.0F;
      nrofDataShift.set(0);
  	}
  	newSamples = 0;  //next call of setSample will cause only draw of less area.
    
  	if(xViewLast < dxView){
    	//only if a new point should be drawn.
    	try{Thread.sleep(2);} catch(InterruptedException exc){}
    	g.setBackground(colorBack);
    	//fill, it means clear the area either from 0 to end (=dxView) or from the last+1-position to end.
      g.fillRectangle(xViewLast, yView, dxView - xViewLast, dyView);  //fill the current background area
      { //draw horizontal grid
	      float yG = dyView / gridDistanceX;
      	int yS = gridStrongPeriodX;
      	float yGridF = yG;
	    	int yS1 = yS;
	    	while(yGridF < dyView){
	      	int yGrid = (int)yGridF;
	    		if(--yS1 <=0){
	    			yS1 = yS; g.setForeground(gridColorStrong);
	    		} else { g.setForeground(gridColor);
	    		}
	    		g.drawLine(xViewLast, yGrid, dxView, yGrid);
	    		yGridF += yG;
	    	}
      }	
      int iData0 = (int)(xOrigin * values.length);  //xOrigin = percent start
    	int nrofTrack = values[0].length;
    	int iData = iData0 + (int)(xViewLast * idataStepPerxPixel);
  		if(paintAllExec){
  			//paint the widget.
  		  //the window may be resized. Therefore, calculate the scaling factors newly.
  			for(int iTrack=0; iTrack < nrofTrack; ++iTrack){
  				yFactor[iTrack] = size.y / -10.0F / yScale[iTrack];  //y-scaling
  				y0Pix[iTrack] = (1.0F - y0Line[iTrack]/100.0F) * size.y; //y0-line
  			  //save the first value as the last one.
    			int yValue = (int)((values[iData][iTrack] - yOffset[iTrack]) * yFactor[iTrack]) + y0Line[iTrack];
  	  		lastValueY[iTrack][1] = yValue;
  	  	}
  		}
  		//g.drawString(""+ iDataLast + "->" + iData + ":" + nrofValuesForGrid, 200, dyView-40);
    	boolean bCont;
  		boolean bFirst = true;
    	float xViewActF;
  		do{
    		xViewLast = (int)xViewLastF;
      	xViewActF = xViewLastF + pixelPerDataStep;  //1 if redraw
      	while(xViewActF == xViewLast){
      		//more as on data per pixel, build middle value TODO
      		xViewActF += pixelPerDataStep;
      	}
      	iData = iData0 + (int)(xViewActF * idataStepPerxPixel);
    		if(gridDistanceY >0){
  				int gridY1 = nrofValuesForGrid - (nrofValues - iDataLast);
  				int gridY2 = nrofValuesForGrid - (nrofValues - iData);
  				int testGrid1 = gridY1 / gridDistanceY;
  				int testGrid2 = gridY2 / gridDistanceY;
    			//int testGrid1 = (int)(gridY - idataStepPerxPixel) / gridDistanceVertical;
    			if(testGrid2 >testGrid1){
    				//there must be a grid line between
    				if(testGrid2 % gridStrongPeriodY == 0){
    				  g.setForeground(gridColorStrong);
    				} else {  
    				  g.setForeground(gridColor);
    				}  
  	        g.drawLine(xViewLast, 0, xViewLast, dyView);
	      	}
  			} 
      	int xViewAct = (int)xViewActF;
    		if(bFirst && !paintAllExec){
    			//redraw the last line, it may be deleted partially.
    			if(lastValueY[1][0] < lastValueY[1][1])
    				stop();
    			for(int iTrack=0; iTrack < nrofTrack; ++iTrack){
        		if(iData >=0){
  	  	  		g.setForeground(lineColors[iTrack]);
  	      		g.drawLine(lastPositionX[0] - xShift, lastValueY[iTrack][0], lastPositionX[1] - xShift, lastValueY[iTrack][1]);
  	        }	
          }
    		}
    		bFirst = false;
      	//int iDataAct = (int)(xViewAct / pixelPerDataStep) + iData0;
    		bCont = iData < nrofValues && xViewAct < dxView;  
    		//continue if there are data, and the windows size matches
    		if(bCont){
    			for(int iTrack=0; iTrack < nrofTrack; ++iTrack){
        		if(iData >=0){
  	  				float yF = values[iData][iTrack];
  	    			int yValue = (int)( (yF - yOffset[iTrack]) * yFactor[iTrack] + y0Pix[iTrack]);
  	      		g.setForeground(lineColors[iTrack]);
  	      		g.drawLine(xViewLast, lastValueY[iTrack][1], xViewAct, yValue);
  	      		lastValueY[iTrack][0] = lastValueY[iTrack][1];
  	      		lastValueY[iTrack][1] = yValue;
  	        }
        		lastPositionX[0] = xViewLast;  //to repeat the last paint action while shift the content
	      		lastPositionX[1] = xViewAct;
	        }
    			xViewLastF = xViewActF;  //used and done
    			iDataLast = iData;
        }
    	} while(bCont);
  		/*
  		while( (++iData) < nrofValues ){
      	int xView1 = (int)((iData - iData0) * pixelPerDataStep);
    		//xView1 is incremented by more as one if zoom is great, 
      	//it may be not incremented if zoom is less.
      	if(xView1 > xViewLast){  //only on progress in x-direction:
    			//write new y-Value
    			for(int iTrack=0; iTrack < nrofTrack; ++iTrack){
        		if(iData >=0){
  	  				float yF = values[iData][iTrack];
  	    			int yValue = (int)( (yF - yOffset[iTrack]) * yFactor[iTrack] + y0Pix[iTrack]);
  	      		g.setForeground(lineColors[iTrack]);
  	      		g.drawLine(xViewLast, lastValue[iTrack], xView1, yValue);
  	      		lastValue[iTrack] = yValue;
        		}	
          }
    			xViewLast = xView1;
    		}
      }
      */
    }
    //g.drawString(""+xShift+ ":"+ xViewLast + ":" + nrofDataShift.get(), 200, dyView-28);
  	//g.drawString("xx", 200, dyView-16);
    focusChanged = false; //paint only last area by next paint event without focus event.
  	
  }	
	
	
	void stop(){} //marker for debug

  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { widgSwt.setBounds(x,y,dx,dy);
  }

  @Override
  public boolean remove()
  {
    // TODO Auto-generated method stub
    return false;
  }
  

}

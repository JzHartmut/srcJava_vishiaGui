package org.vishia.gral.swt;


import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.widget.GralCurveView;




public class SwtCurveView extends GralCurveView
{
  
  /**Version and history
   * <ul>
   * <li>2012-02-21 Hartmut Now the CurveView works in the new environment. Some adjustments necessary yet.
   * <li>2011-06-00 Hartmut New concept of GralWidget etc and new Configuration concept
   *   with {@link org.vishia.gral.cfg.GralCfgBuilder}. The old GuiDialogZbnfControlled.class
   *   was not use nevermore. But the CurveView was not adapted for that.
   * <li>2010-03-00 Hartmut The curve view was development as basic feature.
   * </ul>
   */
  public final static int version = 0x20120222;

  private final CurveView curveSwt;
  
  public SwtCurveView(String sName, SwtWidgetMng mng, int nrofXvalues, int nrofTracks)
  {
    super(sName, mng, nrofXvalues, nrofTracks);
    int ySize = (int)(pos.height());
    int xSize = (int)(pos.width());
    int dxWidget = xSize * mng.propertiesGui.xPixelUnit();
    int dyWidget = ySize * mng.propertiesGui.yPixelUnit();
    Composite panelSwt = (Composite)pos.panel.getPanelImpl();
    curveSwt = this.new CurveView(panelSwt, dxWidget, dyWidget, nrofXvalues, nrofTracks);
    curveSwt.setSize(dxWidget, dyWidget);
    mng.setBounds_(curveSwt); //, dyGrid, dxGrid);
    curveSwt.setGridVertical(10, 5);   //10 data-points per grid line, 50 data-points per strong line.
    curveSwt.setGridHorizontal(50.0F, 5);  //10%-divisions, with 5 sub-divisions
    curveSwt.setGridColor(mng.propertiesGuiSwt.colorGrid, mng.propertiesGuiSwt.colorGridStrong);

  }
  
  @Override public void initLine(String sNameLine, String sDataPath, int colorValue, int style
      , int nullLine, float scale, float offset)
  {
    Line line = lines[ixLineInit] = new Line(sNameLine);
    listlines.add(line);
    //listDataPaths.add(sDataPath);
    line.sDataPath =sDataPath;
    y0Line[ixLineInit] = nullLine;
    yOffset[ixLineInit] = offset;
    yScale[ixLineInit] = scale;
    curveSwt.lineColors[ixLineInit] = new Color(curveSwt.getDisplay(), (colorValue >>16) & 0xff, (colorValue >>8) & 0xff, (colorValue) & 0xff);  
    ixLineInit +=1;
  }
  
  
  @Override protected void repaintGthread(){
    curveSwt.redraw();
  }

  @Override public GralWidgetGthreadSet_ifc getGthreadSetifc(){ return gThreadSet; }

  
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
    redrawBecauseNewData = true;
    repaint(50,50);
    //getDisplay().wake();  //wake up the GUI-thread
    //values.notify();
  }

  
  
  /**Implementation of the graphic thread widget set interface. */
  GralWidgetGthreadSet_ifc gThreadSet = new GralWidgetGthreadSet_ifc(){

    @Override public void clearGthread()
    { // TODO Auto-generated method stub
    }

    @Override public void insertGthread(int pos, Object visibleInfo, Object data)
    { // TODO Auto-generated method stub
    }

    @Override public void redrawGthread()
    { // TODO Auto-generated method stub
    }

    @Override public void setBackGroundColorGthread(GralColor color)
    { // TODO Auto-generated method stub
    }

    @Override public void setForeGroundColorGthread(GralColor color)
    { // TODO Auto-generated method stub
    }

    @Override public void setTextGthread(String text, Object data)
    { // TODO Auto-generated method stub
    }
  };
  
  
  private class CurveView extends Canvas
  {
    
    //private final CurveView widgSwt;

    //final Canvas canvas;
    
    private final Color[] lineColors;
    
    
    private Color gridColor = new Color(getDisplay(), 0, 255, 255);
    
    private Color gridColorStrong = new Color(getDisplay(), 0, 255, 255);
    
    private Color colorBack = new Color(getDisplay(), 0xff, 0xff, 0xff);
    
    public CurveView(Composite parent, int xPixel, int yPixel, int nrofXvalues,
        int nrofTracks){
      super(parent, org.eclipse.swt.SWT.NO_SCROLL|org.eclipse.swt.SWT.NO_BACKGROUND);
      setData("Control", this);
      setSize(xPixel, yPixel);  //the size may be changed later by drag the window.
      //this.xPixel = xPixel;
      //this.yPixel = yPixel;
      lineColors = new Color[nrofTracks];
      Color defaultColor = new Color(getDisplay(), 255,0,0);
      for(int iTrack=0; iTrack < nrofTracks; ++iTrack){
        yFactor[iTrack] = yPixel / 10.0F / yScale[iTrack];
        lineColors[iTrack] = defaultColor;
      }
      addPaintListener(paintListener);
      addFocusListener(focusListener);
      addMouseListener(mouseLeftButtonListener);
      //widgSwt = this;
    }
    
    

    
    public void setGridVertical(int dataPointsBetweenGridLines, int periodStrongLine){
      gridDistanceY = dataPointsBetweenGridLines;
      gridStrongPeriodY = periodStrongLine;
      gridDistanceStrongY = dataPointsBetweenGridLines * periodStrongLine;
    }
    
    
    /**Set distance for horizontal grid lines.
     * @param percentY percent of y-view for grid lines. For example 50.0: divide y-axis in 50 steps. 
     * @param periodStrongLine period for strong lines For example 5, any 5. line is stroke.
     */
    public void setGridHorizontal(float percentY, int periodStrongLine){
      gridDistanceX = percentY;
      gridStrongPeriodX = periodStrongLine;
    }
    
    
    public void setGridColor(Color gridColor, Color gridStrongColor){
      this.gridColor = gridColor;
      this.gridColorStrong = gridStrongColor;
    }
    
    
    public void redrawData(){
      redrawBecauseNewData = true;
      redraw();
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
      try{
      Point size = getSize(); //size of the widget.
      float pixelPerDataStep = (float)(size.x +1) * xZoom /values.length ;
      float idataStepPerxPixel = values.length / (float)(size.x +1) / xZoom;
      float dataStepPerPixel = (float)values.length / (xZoom * size.x +1);
      //draw grid and 0-lines:
      int xShift = -1;   //nr of new pixel if only a part is drawn  
      int xViewLast = (int)xViewLastF;
      testHelp.xView =xView; testHelp.yView =yView; testHelp.dxView =dxView; testHelp.dyView =dyView;
      if(!paintAllCmd && redrawBecauseNewData){
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
          xViewLastF = 0.0F;
        }
      } else {
        paintAllCmd = false; //accepted, done
        testHelp.ctRedrawAll +=1;
        paintAllExec = true;
        xViewLast = 0;
        xViewLastF = 0.0F;
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
      } catch(Exception exc){
        System.err.println("SwtCurveView-paint; " + exc.getMessage());
      }
      //g.drawString(""+xShift+ ":"+ xViewLast + ":" + nrofDataShift.get(), 200, dyView-28);
      //g.drawString("xx", 200, dyView-16);
      focusChanged = false; //paint only last area by next paint event without focus event.
      
    } 
    
  }

  
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

  

  void stop(){}  
  
}

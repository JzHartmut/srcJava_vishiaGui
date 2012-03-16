package org.vishia.gral.swt;


import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPos;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.widget.GralCurveView;




public class SwtCurveView extends GralCurveView
{
  
  /**Version, history and license.
   * <ul>
   * <li>2012-03-17 Hartmut some improvements in paint routine.
   * <li>2012-02-26 Hartmut A lot of details, see {@link GralCurveView}
   * <li>2012-02-21 Hartmut Now the CurveView works in the new environment. Some adjustments necessary yet.
   * <li>2011-06-00 Hartmut New concept of GralWidget etc and new Configuration concept
   *   with {@link org.vishia.gral.cfg.GralCfgBuilder}. The old GuiDialogZbnfControlled.class
   *   was not use nevermore. But the CurveView was not adapted for that.
   * <li>2010-03-00 Hartmut The curve view was development as basic feature.
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
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   */
  public final static int version = 20120317;

  private final CurveView curveSwt;
  
  private Image cursorStore1, cursorStore2;
  
  public SwtCurveView(String sName, GralPos pos, SwtWidgetMng mng, int nrofXvalues, int nrofTracks)
  {
    super(sName, mng, nrofXvalues, nrofTracks);
    
    GralRectangle bounds = mng.calcWidgetPosAndSize(pos, 800, 600);
    Composite panelSwt = (Composite)pos.panel.getPanelImpl();
    curveSwt = this.new CurveView(panelSwt, bounds.dx, bounds.dy, nrofXvalues, nrofTracks);
    curveSwt.setSize(bounds.dx, bounds.dy);
    curveSwt.setBounds(bounds.x, bounds.y, bounds.dx, bounds.dy);
    //mng.setBounds_(curveSwt); //, dyGrid, dxGrid);
    curveSwt.setGridVertical(10, 5);   //10 data-points per grid line, 50 data-points per strong line.
    curveSwt.setGridHorizontal(50.0F, 5);  //10%-divisions, with 5 sub-divisions
    curveSwt.setGridColor(mng.propertiesGuiSwt.colorGrid, mng.propertiesGuiSwt.colorGridStrong);
    cursorStore1 = new Image(panelSwt.getDisplay(), 1, 2000);
    cursorStore2 = new Image(panelSwt.getDisplay(), 1, 2000);
  }
  
  @Override public void initTrack(String sNameTrack, String sDataPath, GralColor color, int style
      , int nullLine, float scale, float offset)
  {
    Track track = tracks[ixLineInit] = new Track(sNameTrack);
    track.values = new float[this.maxNrofXValues];
    listTracks.add(track);
    listTrackSet.add(track);
    //listDataPaths.add(sDataPath);
    track.sDataPath =sDataPath;
    track.y0Line = nullLine;
    track.yOffset = offset;
    track.yScale = scale;
    //yScale[ixLineInit] = scale;
    track.lineColor = color;
    Color colorSwt = (Color)itsMng.getColorImpl(color);
    curveSwt.lineColors[ixLineInit] = colorSwt; //new Color(curveSwt.getDisplay(), (colorValue >>16) & 0xff, (colorValue >>8) & 0xff, (colorValue) & 0xff);  
    ixLineInit +=1;
  }
  
  
  @Override protected void repaintGthread(){
    curveSwt.redraw();
  }

  @Override public GralWidgetGthreadSet_ifc getGthreadSetifc(){ return gThreadSet; }

  
  /**
   * @see org.vishia.gral.widget.GralCurveView#setSample(float[], int)
   */
  @Override public void setSample(float[] newValues, int timeshort) {
    /*
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
    */
    if(testStopWr) return;  //only for debug test.
    if(nrofValues < maxNrofXValues){
      this.nrofValues +=1;
    } else {
      nrofDataShift.incrementAndGet(); //shift data in graphic.
    }
    //if(++ixDataWr >= maxNrofXValues){ ixDataWr = 0; } //wrap arround.
    ixDataWr += adIxData;
    if(!bFreeze){
      ixDataShowRight = ixDataWr;
    }
    this.newSamples +=1;  //information for paint event
    this.nrofValuesForGrid +=1;
    if(nrofValuesForGrid > maxNrofXValues + gridDistanceStrongY){
      nrofValuesForGrid -= gridDistanceStrongY;  //prevent large overflow.
    }
    int ixSource = -1;
    int ixWr = (ixDataWr >> shIxiData) & mIxiData;
    for(Track track: tracks){
      track.values[ixWr] = newValues[++ixSource];  //write in the values.
    }
    timeValues[ixWr] = timeshort;
    redrawBecauseNewData = true;
    //System.out.println("" + timeshort);
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
        lineColors[iTrack] = defaultColor;
      }
      addPaintListener(paintListener);
      addFocusListener(focusListener);
      addMouseListener(mouseLeftButtonListener);
      addMouseMoveListener(mouseMoveListener);
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
       * 
       * The left mouse down in designated ranges executes:
       * <ul>
       * <li>left top: view more time spread, to the past
       * <li>right top: view lesser time spread, finer solution.
       * <li>TODO: only if the curve is running, 
       * <li>TODO right click context menu
       * </ul>
       * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
       */
      @Override public void mouseDown(MouseEvent e) {
        Control widgSwt = (Control)e.widget;
        //Widget widgSwt = e.widget;
        Point size = widgSwt.getSize();
        if(e.y < size.y/4) {                  //top range
          int xr = size.x - e.x;
          if(xr < xpCursor1 && xpCursor2 > 0 && xr > xpCursor2){
            //zoom between cursor
            ixDataCursor1 = ixDataShown[xpCursor1];
            ixDataCursor2 = ixDataShown[xpCursor2];
            ixDataShowRight = ixDataCursor2 + (((ixDataCursor2 - ixDataCursor1) / 10) & mIxData);
            int ixiData1 = (ixDataShown[xpCursor1] >> shIxiData) & mIxiData;
            int ixiData2 = (ixDataShown[xpCursor2] >> shIxiData) & mIxiData;
            int time1 = timeValues[ixiData1];
            int time2 = timeValues[ixiData2];
            timeSpread = (time2 - time1) * 10/8;
            assert(timeSpread >0);
            xpCursor1 = xpCursor2 = cmdSetCursor;  
            
          }
          if(e.x < size.x / 4) {              //edge left top
            //zoom out
            if(xpCursor1 >=0){
              ixDataCursor1 = ixDataShown[xpCursor1];
            }
            if(xpCursor2 >=0){
              ixDataCursor2 = ixDataShown[xpCursor2];
            }
            xpCursor1 = xpCursor2 = cmdSetCursor;  
            timeSpread *=2;    //double timespread
            System.out.println("left-top");
          } else if(e.x > size.x * 3 / 4){    //edge right top
            timeSpread /=2;    //half timespread
            System.out.println("right-top");
          } else {
            System.out.println("mid-top");
          }
        } else if(e.y > size.y * 3/4 ) { 
          if(e.x < size.x / 4) {              //edge left bottom
            if(!bFreeze){ 
              bFreeze = true;
              //now ixDataShow remain unchanged.
            } else {
              int ixdDataSpread = ixDataShowRight - ixDataShown[nrofValuesShow * 7/8];
              //int ixDataShowRight1 = ixDataShown[nrofValuesShow * 7/8];
              //int ixdDataSpread = ixDataShowRight - ixDataShowRight1;
              ixDataShowRight -= ixdDataSpread;
              if((ixDataShowRight - ixDataWr) < 0 && (ixDataWr - ixDataShowRight) < ixdDataSpread) {
                //left end reached.
                ixDataShowRight = ixDataWr + ixdDataSpread;
              }
            }
            System.out.println("left-bottom");
          } else if(e.x > size.x * 3 / 4){    //edge right bottom
            if(bFreeze){
              //assume that the same time is used for actual shown data spread as need
              //for the future.
              int ixdDataSpread = ixDataShowRight - ixDataShown[nrofValuesShow * 7/8];
              ixDataShowRight += ixdDataSpread;
              if((ixDataShowRight - ixDataWr) > 0 && (ixDataShowRight - ixDataWr) < ixdDataSpread * 2) {
                //right end reached.
                ixDataShowRight = ixDataWr;
                bFreeze = false;
                //ixDataShowRight1 = ixDataWr + ixdDataSpread;
              }
              ixDataShowRight += ixDataShown[0] - ixDataShown[nrofValuesShow-1]; 
            }
            System.out.println("right-bottom");
          } else {
            System.out.println("mid-bottom");
          }
        } else { //middle range y
          System.out.println("middle");
          int xr = nrofValuesShow - e.x;  //from right
          if(xpCursor1 < 0){
            xpCursor1 = xr;
            bMouseDownCursor1 = true;
          } else if(xpCursor2 < 0){
            xpCursor2 = xr;
            bMouseDownCursor2 = true;
          } else { //decide which cursor
            int xm = (xpCursor1 + xpCursor2) /2;
            if(xr > xm){ //more left
              xpCursor1 = xr;
              bMouseDownCursor1 = true;
            } else {
              xpCursor2 = xr;
              bMouseDownCursor2 = true;
            }
          }
        }
        paintAllCmd = true;
      }

      /**The mouse up is left empty, because the mouse down has its effect.
       * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
       */
      @Override public void mouseUp(MouseEvent e) {
        bMouseDownCursor1 = bMouseDownCursor2 = false;
        System.out.println("SwtCurveView.mouseUp");
      }
      
    };
    
    
    
    
    MouseMoveListener mouseMoveListener = new MouseMoveListener(){

      @Override
      public void mouseMove(MouseEvent e)
      {
        if(bMouseDownCursor1){
          xpCursor1 = nrofValuesShow - e.x;  //from right;
          System.out.println("SwtCurveView.mouseMove cursor x,y=" + e.x + ", " + e.y);
        } else if(bMouseDownCursor2){
          xpCursor2 = nrofValuesShow - e.x;  //from right;
        } else {
          System.out.println("SwtCurveView.mouseMove x,y=" + e.x + ", " + e.y);
            
        }
      }
      
    };
    /**Only used in drawBackground, true if a paintAll is executed. 
     * It is outside because inspector-usage. */
    private boolean paintAllExec;
    

    
    
    /**prepares indices of data.
     * All data have a timestamp. the xpixel-values is calculated from the timestamp.
     * The ixDataShown contains the index into the data for each x pixel from right to left.
     * If there are more as one data record for one pixel, the stepwidth of ixData is >1,
     * If there are more as one x-pixel for one data record, the same index is written for that pixel
     */
    private void drawPrepareIndicesData(int ixDataRight, int xViewPart, int size_x){
      ixDataShown[0] = ixDataRight;
      int ixData = ixDataRight;
      int ixD = (ixData >> shIxiData) & mIxiData;
      int ixp2 = 0;
      int ixp1 = 0;
      int time9 = timeValues[ixD];
      //xViewPart = nrof pixel from right
      //ixp1 counts from 0... right to left
      while(ixp1 < xViewPart && ixp1 >= ixp2){ //singularly: ixp1 < ixp2 if a faulty timestamp is found.
        do{ //all values per 1 pixel
          ixData -= adIxData;                  //decrement to older values
          ixD = (ixData >> shIxiData) & mIxiData;
          int dtime = time9 - timeValues[ixD]; //offset to first right point
          ixp1 = (int)(dtime * pixel7time + 0.5f);  //calculate pixel offset from right, right =0 
          if(xpCursor1 == cmdSetCursor && ixData == ixDataCursor1){
            xpCursor1 = ixp1;                  //set cursor xp if the data index is gotten.
          }
          if(xpCursor2 == cmdSetCursor && ixData == ixDataCursor2){
            xpCursor2 = ixp1;
          }
        } while( ixp1 == ixp2   //all values for the same 1 pixel
              && ixData != ixDataRight  //no more as one wrap around, if dtime == 0 or is less
               );
        //
        if(ixp1 > ixp2 && ixp1 <= size_x){ //prevent drawing on false timestamp in data (missing data)
          do { 
            ixDataShown[++ixp2] = ixData;
          } while(ixp2 < ixp1);
        } else {
          ixDataShown[++ixp2] = -1;  //xp1 is invalid, stop curve
        }
      } 
      ixDataShown[++ixp2] = -1;      //the last
      
    }
    
    
    private void drawTrack(GC g, Point size, Track track, int iTrack){
      int ixixiData = 0;
      int xp2 = size.x -1;
      int xp1 = xp2;
      int ixData2 = ixDataShown[ixixiData];
      int ixData = ixData2;
      int ixD = (ixData >> shIxiData) & mIxiData;
      //
      float yFactor = size.y / -10.0F / track.yScale;  //y-scaling
      float y0Pix = (1.0F - track.y0Line/100.0F) * size.y; //y0-line
      float yF = track.values[ixD];
      int yp9 = (int)( (yF - track.yOffset) * yFactor + y0Pix);
      int yp2 = yp9;  //right value
      int yp1; //left value
      int ixData1;
      if(iTrack == 0){
        System.out.println("SwtCurveView-drawTrack-start(y0Pix=" + y0Pix + ", yFactor=" + yFactor + ", y=" + yF + ")");
      }
      //
      while( (ixData1 = ixDataShown[++ixixiData]) !=-1){ //for all gotten ixData
        xp1 -=1;
        if(ixData != ixData1) {
          int yp1min = Integer.MAX_VALUE, yp1max = Integer.MIN_VALUE;
          int nrofYp = 0;
          yp1 = 0;
          do{ //all values per 1 pixel
            ixData -= adIxData;
            ixD = (ixData >> shIxiData) & mIxiData;
            int yp11;
            if(ixData == ixDataDraw){
              yp11 = track.ypixLast;
            } else {
              yF = track.values[ixD];
              yp11 = (int)( (yF - track.yOffset) * yFactor + y0Pix);
            }
            yp1 += yp11;  //build middle value or init first.
            if(ixData != ixData1){  //more as one value on the same xp
              if(yp1min > yp11){ yp1min = yp11; }
              if(yp1max < yp11){ yp1max = yp11; }
            }
            nrofYp +=1;
          } while(ixData != ixData1); // iData != ixDrawValues); //all values per 1 pixel
          g.setForeground(lineColors[iTrack]);
          if(nrofYp > 1){ //more as one value on the same xp
            g.drawLine(xp1, yp1min, xp1, yp1max);  //draw vertical line to show range.
            yp1 = yp1 / nrofYp;
          }
          g.drawLine(xp2, yp2, xp1, yp1);
          if(iTrack == 0){
            System.out.println("SwtCurveView-drawTrack(" + xp2 + "+" + (xp1 - xp2) + ", " + yp2 + "+" + (yp1 - yp2) + ")");
          }
          xp2 = xp1; //next xp from right to left.
          yp2 = yp1;
          ixData2 = ixData1;
        }
      } //while(iData != ixDrawValues);
      track.ypixLast = yp9;
      
    }
    
    
    
    
    @Override public void drawBackground(GC g, int xView, int yView, int dxView, int dyView) {
      //NOTE: forces stack overflow because calling of this routine recursively: super.paint(g);
      try{
        //
        //detect how many new data are given. Because the data are written in another thread,
        //the number of data, the write index are accessed only one time from this
        //Note that ixDataShowRight is set by ixDataWr if the curve is running.
        int ixDataRight = SwtCurveView.this.ixDataShowRight; 
        Point size = getSize(); //size of the widget.
        nrofValuesShow = size.x;
        timePerPixel = timeSpread / (size.x +1);
        pixel7time = (float)(size.x +1) / timeSpread;
        int xViewPart = -1;   //nr of new pixel if only a part is drawn
        final int xp0;  //left point to draw.
        testHelp.xView =xView; testHelp.yView =yView; testHelp.dxView =dxView; testHelp.dyView =dyView;
        if(!bFreeze && !paintAllCmd && redrawBecauseNewData) {
          redrawBecauseNewData = false;  //it is done.
          testHelp.ctRedrawBecauseNewData +=1;
          if(ixDataRight != ixDataDraw){
            testStopWr = true;
            stop();
          }  
          paintAllExec = false;
          //
          //calculate the number of x-pixel to shift in graphic to left and the width of the range to paint new:
          int timeLast = timeValues[(ixDataDraw >> shIxiData) & mIxiData];
          int timeNow = timeValues[(ixDataRight >> shIxiData) & mIxiData];
          int timeDiff = timeNow - timeLast;  //0 if nothing was written.
          xViewPart = (int)(pixel7time * timeDiff + 0.5f);
          //
          //Shift the graphic if the reason of redraw is only increment samples
          //and the number the values are shifted at least by that number, which is mapped to 1 pixel.
          if(xViewPart >0 && xViewPart < size.x){
            xViewLastF -= xViewPart;
            if(xView != 0){
              //TODO what is if only a part of control is shown
              stop();
            }
            xp0 = xView + dxView - xViewPart;
            if(xpCursor1 >= xViewPart){  //only if the cursor is in the shifted area:
              //restore graphic under cursor
              g.drawImage(cursorStore1, size.x - xpCursor1, 0);
            }
            if(xpCursor2 >= xViewPart){  //only if the cursor is in the shifted area:
              //restore graphic under cursor
              g.drawImage(cursorStore2, size.x - xpCursor2, 0);
            }
            g.copyArea(xView + xViewPart, yView, dxView - xViewPart , dyView, xView, yView, false);
            System.arraycopy(ixDataShown, 0, ixDataShown, xViewPart, size.x - xViewPart);
            testHelp.ctRedrawPart +=1;
          } else if(xViewPart >=size.x){ 
            //too many new values. Show all
            xViewPart = size.x;
            xp0 = 0;
            paintAllExec = true;
            testHelp.ctRedrawAllShift +=1;
            xViewLastF = 0.0F;
          } else { //xViewPart <=0
            //don't paint.
            xViewPart = 0;
            xp0 = size.x;
          }
        } else { //paintall
          xViewPart = size.x;
          xp0 = 0;
          paintAllCmd = false; //accepted, done
          testHelp.ctRedrawAll +=1;
          paintAllExec = true;
          //xViewLast = 0;
          xViewLastF = 0.0F;
          nrofDataShift.set(0);
        }
        newSamples = 0;  //next call of setSample will cause only draw of less area.
        
        //draw grid and 0-lines:
        if(xViewPart >0) { //only if anything is to draw
          //only if a new point should be drawn.
          try{Thread.sleep(2);} catch(InterruptedException exc){}
          int nrofTrack = listTracks.size();
          g.setBackground(colorBack);
          //fill, clear the area either from 0 to end or from size.x - xView to end,
          g.fillRectangle(xp0, yView, xViewPart, dyView);  //fill the current background area
          { //draw horizontal grid
            float yG = dyView / gridDistanceX;
            int yS = gridStrongPeriodX;
            float yGridF = yG;
            int yS1 = yS;
            /*TODO
            while(yGridF < dyView){
              int yGrid = (int)yGridF;
              if(--yS1 <=0){
                yS1 = yS; g.setForeground(gridColorStrong);
              } else { g.setForeground(gridColor);
              }
              g.drawLine(xViewLast, yGrid, dxView, yGrid);
              yGridF += yG;
            }
            */
          } 
          //  
          //prepare indices of data.
          drawPrepareIndicesData(ixDataRight, xViewPart, size.x);
          // 
          //write all tracks.
          int iTrack = 0;
          int ixData;
          for(Track track: listTracks){
            //draw line per track
            drawTrack(g, size, track, iTrack);
            iTrack +=1;
          } //for listlines
          ixDataDraw = ixDataRight;
          
          
          //set the cursors
          if(xpCursor1 >=0){
            int xpCursor = size.x - xpCursor1;
            g.copyArea(cursorStore1, xpCursor, 0);
            g.drawLine(xpCursor, 0, xpCursor, size.y);
          }
          if(xpCursor2 >=0){
            int xpCursor = size.x - xpCursor2;
            g.copyArea(cursorStore2, xpCursor, 0);
            g.drawLine(xpCursor, 0, xpCursor, size.y);
          }
  
        } else { //xViewPart == 0
          System.out.println("SwtCurveView xViewPart=0");
        }
      } catch(Exception exc){
        System.err.println("SwtCurveView-paint; " + exc.getMessage());
      }
      //g.drawString(""+xShift+ ":"+ xViewLast + ":" + nrofDataShift.get(), 200, dyView-28);
      //g.drawString("xx", 200, dyView-16);
      focusChanged = false; //paint only last area by next paint event without focus event.
      testStopWr = false;
      
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

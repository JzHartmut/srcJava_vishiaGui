package org.vishia.gral.base;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.vishia.byteData.VariableAccessWithIdx;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralCurveViewTrack_ifc;
import org.vishia.gral.ifc.GralCurveView_ifc;
import org.vishia.gral.ifc.GralSetValue_ifc;
import org.vishia.mainCmd.Report;
import org.vishia.mainCmd.ReportWrapperLog;
import org.vishia.util.Assert;
import org.vishia.zbnf.ZbnfJavaOutput;
import org.vishia.zbnf.ZbnfParser;


/**Curve representation for timed values. It is the base class for all implementation.
 * @see GralCurveViewTrack_ifc.
 * @author Hartmut Schorrig
 *
 */
public abstract class GralCurveView extends GralWidget implements GralCurveView_ifc
{
  
  /**Version, history and license.
   * <ul>
   * <li>2012-06-08 Hartmut new: {@link #applySettings(String)} and {@link #writeSettings(Appendable)} for saving
   *   and getting the configuration of curve view from a file or another text.
   * <li>2012-04-01 Hartmut new: Using {@link VariableAccessWithIdx} to access values.
   * <li>2012-03-25 Hartmut chg: Some routines from SWT moved to this because there are independent.
   * <li>2012-03-17 Hartmut chg: All track-associated data now in the {@link Track} inner class.
   * <li>2012-02-25 Hartmut new: All data have a short timestamp. The x-pixel are calculated with timestamp,
   *   not only with currently storage.
   * <li>2012-02-25 Hartmut new: Zoom functinality with timestamp and cursors
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
  
  
  
  public class ZbnfSetTrack {
    public String name;
    public String datapath;
    GralColor color_; int style_ = 0;
    public int nullLine;
    public float scale;
    public float offset;
    
    public void set_color(String color){ color_ = GralColor.getColor(color.trim()); }
  }
  
  
  public class ZbnfSetCurve{
    public ZbnfSetTrack new_Track(){
      return new ZbnfSetTrack();
    }
    public void add_Track(ZbnfSetTrack track){
      initTrack(track.name, track.datapath, track.color_, track.style_, track.nullLine, track.scale, track.offset);
    }
    
  }
  
  ZbnfSetCurve zbnfSetCurve = new ZbnfSetCurve();
  
  /**The describing and the actual data of one track (one curve)
   */
  protected static class Track implements GralCurveViewTrack_ifc, GralSetValue_ifc {
    public final String name;
    public String sDataPath;
    
    public VariableAccessWithIdx variable;
    
    private Object oContent;
    
    private int dataIx;
    
    public float actValue;
    
    public float min, max;
    
    /**The scale for 10 percent of view without zoom.. */
    public float yScale;
    
    /**The value of input data which is shown at the 0-line. */
    public float yOffset;
    
    //public float yFactor;
    
    /**The percent from 0..100 where the 0-line is presented. */
    public int y0Line;
    
    /**The color of the line. */
    public GralColor lineColor;
    
    /**The brightness of the line. It is used to show the selected line. */
    public int lineWidth = 1;
    
    //public float y0Pix;
    
    /**Array stores the last values which are able to show. */
    public float[] values;
    
    /**last values for paint. 
     * The current paint goes from lastValueY[1] to the current point.
     * The lastValueY[0] is used to repaint the last curve peace while shifting draw content.
     */
    protected final int[] XXXlastValueY = new int[2];

    
    /**The value from last draw, do not calculate twice. */
    public int ypixLast;
    
    public Track(String name){ this.name = name; }

    @Override public void setContentInfo(Object content) { oContent = content; }

    @Override public Object getContentInfo() { return oContent;  }

    @Override public void setDataPath(String sDataPath) { 
      this.sDataPath = sDataPath; 
      this.variable = null;
    }

    @Override public String getDataPath() { return sDataPath; }

    @Override public int getDataIx() { return dataIx; }

    @Override public void setDataIx(int dataIx) { this.dataIx = dataIx; }

    @Override public void setValue(float value) { this.actValue = value; }

    @Override public void setValue(Object[] value) { } //TODO this.actValue = value[0]; }

    @Override public void setMinMax(float minValue, float maxValue) {
      this.min = minValue; this.max = maxValue;
    }

    @Override public int getLinePercent(){ return y0Line; }
   
    @Override public float getOffset(){ return yOffset; }
    
    @Override public float getScale7div(){ return yScale;  }

    @Override public GralColor getLineColor(){ return lineColor; }

    
    /**Change the scaling of a track.
     * @param trackNr Number of the track in order of creation, 0 ist the first.
     * @param scale7div value per division
     * @param offset value, which is shown at line0
     * @param line0 percent of 0-line in graphic.
     */
    public void setTrackScale(float scale7div, float offset, int line0){
      yScale = scale7div;
      yOffset = offset;
      y0Line = line0;
    }

    @Override public void setLineProperties(GralColor color, int width, int pattern){ 
      lineColor = color; 
      lineWidth = width;
    }

    @Override public void setText(CharSequence text){
      System.err.println("GralCurveView - setText not supported; Widget = " + name + "; text=" + text);
    }
   
    
    @Override public String toString(){ return "Track: " + name + "(" + sDataPath + "," + (variable !=null ? variable.toString(): "variable = null") + ")"; }
    
  }
  
  protected Track[] tracks;
  
  
  /**All tracks. */
  protected final List<Track> listTracks = new LinkedList<Track>();
  
  /**All tracks to return for filling. */
  protected final List<GralSetValue_ifc> listTrackSet = new LinkedList<GralSetValue_ifc>();
  
  /**A short timestamp for the values in {@link GralCurveView.Track#values}.
   * It maybe a millisecond timestamp. Then about 2000000 seconds are able to display.
   * This array and the {@link Track#actValue} array are filled wrapping.
   * <br><br> 
   * A negative difference between 2 successive time stamps designates the time break point between newer values
   * and the successive currently points to the newest one in ascending direction (past to future).
   * Initially this array will be filled with successive values from 0 to negatives in step -1 
   * to designate a time break at all points. 
   * <br><br>
   * At startup of filling 3 data points this array is filled for example with
   * <pre>
   * -1234, -1245, -1256, -3, -4, -5, ... 
   * </pre>
   * In this example the wrapping time stamps are negative but successive ascending of course.
   * The not used 4. point is accepted as a correct point faulty, it produces a presentation to the value 0
   * because nothing is stored in data in 1253 time units (which may be milliseconds). This faulty presentation
   * is only present on startup of graphic and only if the difference is in range of presentation width,
   * see {@link #drawPrepareIndicesData(int, int)}
   */
  protected final int[] timeValues;
  
  /**The index in data for each shown pixel, from right to left.
   * [0] is right. 2000 are enough for a large representation.
   * It is the number of pixel.
   */
  protected final int[] ixDataShown = new int[2000];
  
  protected int ixLineInit = 0;
  
  //protected final float[][] values;
  
  /**last x-positions for paint. 
   * The lastPositionX[0] is used to repaint the last curve peace while shifting draw content.
   * Where, xShift (number of shifted pixel) is considered by subtraction.
   */
  protected final int[] lastPositionX = new int[2];
  
  /**Deepness of the storage of values to present.
   * It is a power of 2 anytime.
   */
  protected final int maxNrofXValues;
  
  /**The increment step of ixData.
   * Concept of wrap around: The values[]-array are used wrap around, to prevent
   * expensive shift operations. The size of the array is a power of 2 anytime.
   * The index is hold in an integer variable, which wraps around in the bit space too.
   * To use cheap increment and compare functionality, the range for the index
   * is the full integer range. To step from one to next index, use this adding value. 
   */
  protected final int adIxData;
  
  /**The number of shift right to get the numeric index in values. */
  protected final int shIxiData;
  
  /**Mask of index in values. */
  protected final int mIxiData;
  
  
  /**Mask of any ixData. */
  protected final int mIxData;
  
  
  
  /**Number of values to show in graphic. */
  protected int nrofValuesShow;
  
  /**Number of time units for 1 pixel.
   * A time unit may be 1 millisecond for currently showing curves.
   * This value may be e.g. 100 to show 30 seconds in 300 pixel or e.g. 10 to show 5 seconds in 500 pixel.  
   * This value may be given in another unit for example 1 microseconds. It should be the same value unit
   * as used in {@link #setSample(float[], int)}.
   */
  protected float timePerPixel;
  
  
  protected float pixel7time;
  
  /**Number of time units for the whole curve.
   * If this value is given (>=0) then the {@link #timePerPixel} will be calculated from this
   * regarding the current size.
   * This value should be given in the same value unit as used for {@link #setSample(float[], int)}.
   */
  protected int timeSpread = 50000;
  
  
  /**Current number of values in the data field. 
   * If less values are set ({@link #setSample(float[])}, 
   * then the nrofValues is less than values.length.
   * Else it is ==values.length. */
  protected int nrofValues = 0;
  
  /**Write index of values, increment by {@link #setSample(float[], int)}.
   * The index refers to the last written value.
   * Initially it is -1, the first write will be increment ?? to prevent any exception while values are not set before.
   */
  protected int ixDataWr;
  
  protected boolean testStopWr;
  
  /**Index of the last drawn values. 
   * The index refers to the last drawn value. */
  protected int ixDataDraw = 0;
  
  /**If true, then the display is freezed.
   */
  protected boolean bFreeze = false;
  
  /**True then saves values.  */
  protected boolean bActive;
  
  /**The index to show values, it increments with ixWrValues
   * if bFreeze is false
   */
  protected int ixDataShowRight = 0;
  
  
  /**Pixel from right for the cursor1 and cursor2. If -1 then the cursor is unused yet.
  */
  protected int xpCursor1 = -1, xpCursor2 = -1;
  
  /**During mouse move, internal use. */
  protected boolean bMouseDownCursor1, bMouseDownCursor2;
  
  protected final static int cmdSetCursor = -2;
  
  /**Position of cursor in the data. */
  protected int ixDataCursor1, ixDataCursor2;
  
  protected int nrofValuesForGrid;
  
  
  /**Distance of nrofValues for one vertical grid line (strong or not strong). */
  protected int gridDistanceY;
  
  /**Distance of percent of y-view for one vertical grid line (strong or not strong). */
  protected float gridDistanceX;
  
  /**period of strong lines. */
  protected int gridStrongPeriodX, gridStrongPeriodY;
  
  /**Distance of nrofValues for one vertical strong grid line.
   * This value is used to limit nrofValuesForGrid. */
  protected int gridDistanceStrongY;
  
  protected GralColor gridColorGral; // = new Color(getDisplay(), 0, 255, 255);
  
  protected GralColor gridColorGralStrong; // = new Color(getDisplay(), 0, 255, 255);
  
  /**Zoom factor. If zoom = 1.0, the curve will be shown in the current canvas area.
   * It the zoom-factor is greater 1.0, not all points will be shown. 
   * The origin point of the values is given with {@link #xOrigin} and {@link #yOrigin}.
   */
  //protected float xZoom = 1.0F, yZoom= 1.0F;
  
  /**Origin point from 0.0 to 1.0 for the zoomed area. */
  //protected float xOrigin = 0.0F, yOrigin = 0.0F;
  
  protected GralColor colorBackGral; // = new Color(getDisplay(), 0xff, 0xff, 0xff);

  
  protected boolean focusChanged = false;  //it doesn't work
  
  /**Set to true to force a paint all. */
  protected boolean paintAllCmd = false;
  
  protected int newSamples;
  
  /**last point in x where values were drawn. */
  protected float xViewLastF = 0;
  
  
  
  /**Number of iData-indices, which are shifted in the {@link #values}. 
   * This number have to be shifted in the pixel area if a draw-all is not requested. 
   * This field is set to 0 if the draw action is done.*/
  protected AtomicInteger nrofDataShift = new AtomicInteger(0);
  
  protected float nrofDataShiftFracPart = 0.0F;
  
  /**Set true if {@link #redrawData()} is called. Then only the area for new data is drawn 
   * in the {@link #drawBackground(GC, int, int, int, int)}-routine.
   * 
   */
  protected boolean redrawBecauseNewData;
  
  




  public GralCurveView(String sName, GralWidgetMng mng, int maxNrofXvaluesP, int nrofTracks)
  {
    super(sName, 'c', mng);
    
    int maxNrofXvalues1 = 1;
    int shIxData1 = 32;
    while(maxNrofXvalues1 < maxNrofXvaluesP){
      maxNrofXvalues1 <<=1;
      shIxData1 -=1;
    }
    this.shIxiData = shIxData1;
    this.maxNrofXValues = maxNrofXvalues1; //maxNrofXvalues;
    this.adIxData = 0x40000000 / (maxNrofXValues >>2);  //NOTE: integer division, all >>2
    this.mIxData = ~(this.adIxData -1); //all bits which have to be used, mask out lower bits.
    this.mIxiData = maxNrofXValues -1;  //e.g. from 0x1000 to 0xfff
    this.ixDataWr = -adIxData; //initial write position, first increment to 0.
    //
    this.nrofValuesShow = 0;
    tracks = new Track[nrofTracks];
    timeValues = new int[maxNrofXValues];
    for(int ix = 0; ix < maxNrofXValues; ++ix){
      timeValues[ix] = ix;  //store succession of time values to designate it as empty.  
    }
    //values = new float[maxNrofXvalues][nrofTracks];
    setPanelMng(mng);
    mng.registerWidget(this);
  }

  
  /**Initializes a track of the curve view.
   * This routine should be called after construction, only one time per track
   * in the order of tracks.
   * @param sNameTrack
   * @param sDataPath
   * @param colorValue
   * @param style
   * @param nullLine
   * @param scale
   * @param offset
   */
  public GralCurveViewTrack_ifc initTrack(String sNameTrack, String sDataPath, GralColor color, int style
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
    ixLineInit +=1;
    return track;
  }


  
  public void setTimePerPixel(int time){
    timePerPixel = time;
  }
  
  
  public void setTimeSpread(int time){
    timeSpread = time; 
  }
  
  
  /**Change the scaling of a track.
   * @param trackNr Number of the track in order of creation, 0 ist the first.
   * @param scale7div value per division
   * @param offset value, which is shown at line0
   * @param line0 percent of 0-line in graphic.
   */
  public void setTrackScale(int trackNr, float scale7div, float offset, int line0){
    Track track = listTracks.get(trackNr);
    track.setTrackScale(scale7div, offset, line0);
  }
  
  
  
  /**This list describes the data paths in that order, which should be regard
   * calling {@link #setSample(float[])}.
   */
  public List<GralSetValue_ifc> getTracks(){ return listTrackSet; }
  
  
  /**This list describes the data paths in that order, which should be regard
   * calling {@link #setSample(float[])}.
   */
  public List<? extends GralCurveViewTrack_ifc> getTrackInfo(){ return listTracks; }
  
  
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
   * @param timeshort relative time-stamp as currently wrapping time in milliseconds.
   */
  //public abstract void setSample(float[] newValues, int timeshort);
  /**
   * @see org.vishia.gral.base.GralCurveView#setSample(float[], int)
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

  
  
  @Override public void refreshFromVariable(VariableContainer_ifc container){
    if(bActive){
      float[] values = new float[tracks.length];
      int ixTrack = -1;
      for(Track track: tracks){
        if(track.variable ==null ){ //no variable known, get it.
          String sDataPath = track.getDataPath();
          if(sDataPath !=null){
            String sPath2 = sDataPath.trim();
            String sPath = itsMng.replaceDataPathPrefix(sPath2);
            track.variable = container.getVariable(sPath);
          }
        }
        final float value;
        if(track.variable !=null ){
          value = track.variable.getFloat();
          track.variable.getVariable().requestValue(System.currentTimeMillis());
        } else {
          value = 0;
        }
        values[++ixTrack] = value;
      }
      setSample(values, (int)System.currentTimeMillis());
    }
  }
  
  
  
  /**prepares indices of data.
   * All data have a timestamp. the xpixel-values are calculated from the timestamp.
   * The {@link #ixDataShown} array will be filled with the indices to the data for each x pixel from right to left.
   * If there are more as one data record for one pixel, the step width of ixData is >1,
   * If there are more as one x-pixel for one data record, the same index is written for that pixel
   * <br><br>
   * It uses the {@link timeValues} to get the timestamp of the values starting from param ixDataRight
   * for the right pixel of view. The ixDataRight will be decremented. It is a wrapping index with
   * step width of {@link #adIxData}. If the timestamp of any next index is greater than the last one,
   * it is a indication that any newer data are reached after wrapping. Then the preparation stops.
   * <br><br>
   * It fills {@link #ixDataShown} with indices to data per x pixel point.
   * If there are the same data for more as one pixel (zoomed near), then {@link #ixDataShown} will contain
   * the same index. The presentation can decide whether it should be shown with the same level (stepwise)
   * or with any linear approximation
   * 
   * @param ixDataRight Index in data array for the right point. 
   *   It is a index which wraps around full integer range, see {@link #adIxData}.
   * @param xViewPart width of the spread to prepare in pixel
   * @return nrof pixel to draw. It is xViewPart if enough data are available, elsewhere less. 
   */
  protected int drawPrepareIndicesData(int ixDataRight, int xViewPart){
    System.arraycopy(ixDataShown, 0, ixDataShown, xViewPart, ixDataShown.length - xViewPart);
    int ixDataRightLast = ixDataShown[0];  //the index in data of right point for last paint.
    int ixixDataShownSh = -1;  //store which index is proper to ixDataRightLast
    ixDataShown[0] = ixDataRight;
    int ixData = ixDataRight;
    int ixD = (ixData >> shIxiData) & mIxiData;
    int ixp2 = 0;
    int ixp = 0;
    int time9 = timeValues[ixD];
    int time2 = time9;  //start time
    int time = time9;
    int dtime2 = 0;
    //int dtime = 0;
    //xViewPart = nrof pixel from right
    //ixp1 counts from 0... right to left
    //int nrofValues1 = nrofValues;
    while(ixp < xViewPart
         && dtime2 <=0
         ){ // && ixp1 >= ixp2){ //singularly: ixp1 < ixp2 if a faulty timestamp is found.
      do{ //all values per 1 pixel
        ixData -= adIxData; //decrement to older values
        //nrofValues -=1;
        ixD = (ixData >> shIxiData) & mIxiData;
        time = timeValues[ixD];
        dtime2 = time - time2;
        //dtime = time9 - time; //offset to first right point
        if((dtime2) <0){
          int dtime0 = time9 - time;
          ixp = (int)(dtime0 * pixel7time + 0.5f);  //calculate pixel offset from right, right =0 
          if(ixp > xViewPart){   //next time stamp is in the past of requested view
            ixp = xViewPart;     //no more than requested nr of points. 
          }
          if(xpCursor1 == cmdSetCursor && ixData == ixDataCursor1){
            xpCursor1 = ixp;                  //set cursor xp if the data index is gotten.
          }
          if(xpCursor2 == cmdSetCursor && ixData == ixDataCursor2){
            xpCursor2 = ixp;
          }
        }
      } while( ixp == ixp2   //all values for the same 1 pixel
            && dtime2 <00     //stop at time crack to newer values.
          //&& nrofValues >0
            //&& ixData != ixDataRight  //no more as one wrap around, if dtime == 0 or is less
             );
      //
      //if(ixp1 > ixp2 && ixp1 <= size_x){ //prevent drawing on false timestamp in data (missing data)
      while(ixp2 < ixp) { 
        ixDataShown[++ixp2] = ixData;  //same index to more as one point.
      }
    } 
    //ixDataShown[++ixp2] = -1;      //the last
    return ixp;
  }
  
  

  
  
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
  { //widgetSwt.setBounds(x,y,dx,dy);
  }

  @Override
  public boolean setFocus()
  {
    // TODO Auto-generated method stub
    return false;
  }
  
  @Override public void activate(boolean activate){ bActive = activate; }
  
  @Override public boolean isActiv(){ return bActive; }
  

  
  @Override public boolean applySettings(String in){
    boolean bOk;
    try{
      //variate syntax in test... 
      String syntax1 = "curveSettings::= { <Track> } \\e."
                    + "Track::= track <*:?name> : { datapath = <* ,;?datapath> | color = <* ,;?color> | "
                    + "scale = <#f?scale> | offset = <#f?offset> | 0-line-percent = <#?nullLine> "
                    + "? , } ; .";
      syntax1 = syntaxSettings;
      Report console = new ReportWrapperLog(itsMng.log());
      ZbnfParser parser = new ZbnfParser(console);
      parser.setSyntax(syntax1);
      bOk = parser.parse(in);
      if(!bOk){
        console.writeError(parser.getSyntaxErrorReport());
      } else {
        ixLineInit = 0;
        listTracks.clear();
        listTrackSet.clear();
        ZbnfJavaOutput setData = new ZbnfJavaOutput(console);
        setData.setContent(zbnfSetCurve.getClass(), zbnfSetCurve, parser.getFirstParseResult());
      }
    } catch(Exception exc){
      System.err.println("GralCurveView.writeSettings() - unexpected IOException;" + exc.getMessage());
      bOk = false;
    }
    return bOk;
  }
  
  
  
  @Override public void writeSettings(Appendable out){
    for(Track track: listTracks){
      if(track.sDataPath !=null){
        try{
          out.append("track ").append(track.name).append(":");
          out.append(" datapath=").append(track.sDataPath);
          out.append(", color=").append(track.lineColor.toString());
          out.append(", scale=").append(Float.toString(track.yScale) );
          out.append(", offset=").append(Float.toString(track.yOffset));
          out.append("0-line-percent=").append(Integer.toString(track.y0Line));
          out.append(";\n");
        } catch(IOException exc){
          System.err.println("GralCurveView.writeSettings() - unexpected IOException;" + exc.getMessage());
        }
      }
    }
  }
  
  
  
  
  protected void moveCursors(int xPos){
    System.out.println("middle");
    int xr = nrofValuesShow - xPos;  //from right
    if(xpCursor1 < 0){
      xpCursor1 = xr;
      bMouseDownCursor1 = true;
    } else if(xpCursor2 < 0){
      xpCursor2 = xr;
      bMouseDownCursor2 = true;
    } else { //decide which cursor
      if(xpCursor1 < xpCursor2){
        int xp = xpCursor1;      //swap
        xpCursor1 = xpCursor2;
        xpCursor2 = xp;
      }
      int xm = (xpCursor1 + xpCursor2) /2;
      if(xr > xm){ //more left
        xpCursor1 = xr;
        bMouseDownCursor1 = true;
        System.out.println("SwtCurveView.mouseDown; cursor1");
      } else {
        xpCursor2 = xr;
        bMouseDownCursor2 = true;
        System.out.println("SwtCurveView.mouseDown; cursor1");
      }
    }

  }
  

  
  /**Zooms the curve presentation with same index right with a lesser time spread. 
   * If the curve presentation is running yet, a finer solution in the present is given. 
   * Note that in this case the right index is the actual write index.*/
  protected void zoomToPresent(){
    timeSpread /=2;    //half timespread
    System.out.println("right-top");
  }
  
  
  
  
  /**Zooms the curve presentation with same index right with a greater time spread. 
   * If the curve presentation is running yet, a broader solution in the present is given. 
   * Note that in this case the right index is the actual write index.*/
  protected void zoomToPast(){
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

  }
  
  
  /**Zooms between the given vertical cursors.
   * The time spread is calculated so that the cursors are places on the same data indices at 
   * 1/10 and 9/10 of the presentation range.
   */
  protected void zoomBetweenCursors(){
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
  
  
  /**Unzooms between the given vertical cursors.
   * The cursors will be set newly in graphic at the same data indices.
   * The middle value of both cursors will be at the same position.
   * The time spread is multiplied by the value 5.
   * If the right border of presentation will be in the future, the {@link #ixDataShowRight}
   * will be greater than {@link #ixDataWr}, then the presentation will be adjusted to the current
   * right {@link #ixDataWr} and the cursor positions are changed adequate.
   *
   */
  protected void cursorUnzoom()
  {
    int xpCursorMid = (xpCursor1 + xpCursor2) /2;
    int ixDataMid = ixDataShown[xpCursorMid];  //from Pixel value of cursor to data index
    //int timeMid = timeValues[(ixDataMid >> shIxiData) & mIxiData];
    //int timeRight = timeValues[(ixDataShowRight >> shIxiData) & mIxiData];
    //int dtime = timeRight - timeMid;
    //Assert.check(dtime > 0);
    ixDataShowRight = ixDataMid + (ixDataShowRight - ixDataMid) * 5;  //5 times longer
    if((ixDataShowRight - ixDataWr) >0){
      ixDataShowRight = ixDataWr;    //show full to right
    }
    //ixDataCursor1 = ixDataShown[xpCursor1];  //from Pixel value of cursor to data index
    //ixDataCursor2 = ixDataShown[xpCursor2];
    //ixDataShowRight = ixDataCursor2 + (((ixDataCursor2 - ixDataCursor1)));
    timeSpread *=5;
    /*
    int ixiData2 = (ixDataCursor2 >> shIxiData) & mIxiData;
    int time1 = timeValues[(ixDataCursor1 >> shIxiData) & mIxiData];
    int time2 = timeValues[ixiData2];
    timeSpread = (time2 - time1) * 5;
    */
    Assert.check(timeSpread >0);
    xpCursor1 = xpCursor2 = cmdSetCursor;  

  }
  

  /**Shifts the curve presentation to the present (actual values).
   * If the TODO
   */
  protected void viewToPresent()
  {
    if(bFreeze){
      //assume that the same time is used for actual shown data spread as need
      //for the future.
      
      int timeRight = timeValues[(ixDataShowRight >> shIxiData) & mIxiData];
      int timeRightNew = timeRight + timeSpread * 7/8;
      
      //int ixdDataSpread = ixDataShowRight - ixDataShown[nrofValuesShow * 7/8];
      //ixDataShowRight += ixdDataSpread;
      //if((ixDataShowRight - ixDataWr) > 0 && (ixDataShowRight - ixDataWr) < ixdDataSpread * 2) {
        //right end reached.
        ixDataShowRight = ixDataWr;
        bFreeze = false;
        //ixDataShowRight1 = ixDataWr + ixdDataSpread;
      //}
      //ixDataShowRight += ixDataShown[0] - ixDataShown[nrofValuesShow-1]; 
    }
    System.out.println("right-bottom");
  }
  
  /**Shifts the curve presentation to the past. If the presentation is running, it is stopped firstly.
   * If it is stopped, the data index on 20% of current presentation will be adjusted to the right border.
   * It means 20% are presented overlapping. The time spread is not changed.
   */
  protected void viewToPast()
  {
    if(!bFreeze){ 
      bFreeze = true;
      //now ixDataShow remain unchanged.
    } else {
      int ixdDataSpread = ixDataShowRight - ixDataShown[nrofValuesShow * 8/10];
      //int ixDataShowRight1 = ixDataShown[nrofValuesShow * 7/8];
      //int ixdDataSpread = ixDataShowRight - ixDataShowRight1;
      ixDataShowRight -= ixdDataSpread;
      if((ixDataShowRight - ixDataWr) < 0 && (ixDataWr - ixDataShowRight) < ixdDataSpread) {
        //left end reached.
        ixDataShowRight = ixDataWr + ixdDataSpread;
      }
    }
    System.out.println("left-bottom");

  }
  
  
  
  
}

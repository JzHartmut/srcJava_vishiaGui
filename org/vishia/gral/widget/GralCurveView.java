package org.vishia.gral.widget;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralSetValue_ifc;
import org.vishia.gral.ifc.GralWidget;


public abstract class GralCurveView extends GralWidget
{
  
  /**Version, history and license.
   * <ul>
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
  
  /**The describing and the actual data of one track (one curve)
   */
  protected static class Track implements GralSetValue_ifc {
    public final String name;
    public String sDataPath;
    
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
    public float y0Line;
    
    /**The color of the line. */
    public GralColor lineColor;
    
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

    @Override public void setDataPath(String sDataPath) { this.sDataPath = sDataPath; }

    @Override public String getDataPath() { return sDataPath; }

    @Override public int getDataIx() { return dataIx; }

    @Override public void setDataIx(int dataIx) { this.dataIx = dataIx; }

    @Override public void setValue(float value) { this.actValue = value; }

    @Override public void setMinMax(float minValue, float maxValue) {
      this.min = minValue; this.max = maxValue;
    }
  }
  
  protected Track[] tracks;
  
  
  /**All tracks. */
  protected final List<Track> listTracks = new LinkedList<Track>();
  
  /**All tracks to return for filling. */
  protected final List<GralSetValue_ifc> listTrackSet = new LinkedList<GralSetValue_ifc>();
  
  /**A short timestamp for the values in {@link GralCurveView.Track#values}.
   * It maybe a millisecond timestamp. Then about 2000000 seconds are able to display.
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
  abstract public void initTrack(String sNameTrack, String sDataPath, GralColor color, int style
      , int nullLine, float scale, float offset);

  
  public void setTimePerPixel(int time){
    timePerPixel = time;
  }
  
  
  public void setTimeSpread(int time){
    timeSpread = time; 
  }
  
  
  /**This list describes the data paths in that order, which should be regard
   * calling {@link #setSample(float[])}.
   */
  public List<GralSetValue_ifc> getTracks(){ return listTrackSet; }
  
  
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
  public abstract void setSample(float[] newValues, int timeshort);

  
  @Override
  public Object getWidgetImplementation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  

  
  @Override
  public void removeWidgetImplementation()
  {
    // TODO Auto-generated method stub
    
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
  

}

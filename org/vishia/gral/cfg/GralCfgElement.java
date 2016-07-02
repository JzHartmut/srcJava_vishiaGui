package org.vishia.gral.cfg;

import java.text.ParseException;

import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.cfg.GralCfgData.GuiCfgButton;
import org.vishia.gral.cfg.GralCfgData.GuiCfgCurveview;
import org.vishia.gral.cfg.GralCfgData.GuiCfgImage;
import org.vishia.gral.cfg.GralCfgData.GuiCfgInputFile;
import org.vishia.gral.cfg.GralCfgData.GuiCfgLed;
import org.vishia.gral.cfg.GralCfgData.GuiCfgLine;
import org.vishia.gral.cfg.GralCfgData.GuiCfgShowField;
import org.vishia.gral.cfg.GralCfgData.GuiCfgTable;
import org.vishia.gral.cfg.GralCfgData.GuiCfgText;
import org.vishia.gral.cfg.GralCfgData.WidgetTypeBase;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralWidgetCfg_ifc;


/**ZBNF: Element::= ... ;
 * Class for instance to capture and store one element. Any widget type is an Element.
 * The distinction between different types are given with the {@link #itsCfgData}. 
 * <pre>
 * 
 * 
 * 
 * */
public class GralCfgElement implements Cloneable, GralWidgetCfg_ifc
{ 
  /**Version and history
   * <ul>
   * <li>2012-04-22 Hartmut chg {@link #setPos(GralMngBuild_ifc)} moved from {@link GralCfgBuilder#buildWidget(GralCfgElement)}
   * <li>2012-04-22 Hartmut new {@link #new_ValueBar()}
   * <li>2011-05-00 Hartmut created, the old ZbnfCfg.. class is obsolte now.
   * </ul>
   *
   * <b>Copyright/Copyleft</b>:<br>
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
   */
  public static final int version = 20120422;

  /**The previous element is necessary because non complete coordinates are resolved with previous.
   * The next element is need to build a queue in order of the text. */
  GralCfgElement previous, next;
  
  //final GralCfgData itsCfgData;
  
  
  /**The content is set in textual form too. It is because [<?Element>...] was written */
  String content;
  
  /**The position is set in textual form too. It is because [<?Element>...] was written */
  String positionString;
  
  /**ZBNF: Position coordinates will be filled from [<?position>.... 
   * The instance contains only that position data, which are found in the textual config file. 
   * It is important to rewrite only that data, which are contained in the originally one. */
  final GralCfgPosition positionInput = new GralCfgPosition();
  
  /**This obj contains all position data. Missed position data in the {@link #positionInput}
   * are completed by knowledge of the position of the previous elements. 
   * The position is filled only in {@link #setPos(GralMngBuild_ifc)} and used nearly only temporary
   * */
  private final GralCfgPosition position = new GralCfgPosition();
  
  //private final GralPos posInput = new GralPos();
  
  WidgetTypeBase widgetType;
  
  GralCfgElement() //GralCfgData itsCfgData)
  { //this.itsCfgData = itsCfgData;
  }
  
  @Override
  public GralCfgElement clone(){ 
    GralCfgElement newObj = new GralCfgElement();  //this.itsCfgData);
    newObj.widgetType = widgetType.clone(); ///use a new cloned instance (use data).
    newObj.positionInput.set(this.positionInput);
    /*NOTE: don't use super.clone() because it copies the references to final elements. We need cloned new elements
    GralCfgElement newObj = null;
    try{ newObj = (GralCfgElement)super.clone(); 
      newObj.position = new GralCfgPosition(); //use a new empty instance in cloned object, empty data.
      newObj.positionInput = positionInput.clone(); ///use a new cloned instance (use data).
      newObj.widgetType = widgetType.clone(); ///use a new cloned instance (use data).
    } catch(CloneNotSupportedException exc){ assert(false); }
    */
    newObj.previous = this;  //link it in queue after this.
    newObj.next = next;
    next = newObj;
    return newObj; 
  }
  
  
  
  
  /**Builds the position.
   * @param cfge deprecated, it is this.
   * @param guiMng to set the position.
   * @return
   * @throws ParseException 
   */
  void setPos(GralMngBuild_ifc guiMng) 
  throws ParseException 
  { GralMng mng = GralMng.get();
    if(positionString !=null) {
      mng.setPos(positionString);
    } else {
      GralCfgPosition p = positionInput;
      if(p.yPos >=0 || p.xPos >=0 || p.ySizeDown !=0 || p.xWidth !=0) {
          
        int ySize = p.ySizeDown == GralPos.useNatSize ? p.ySizeDown :
          p.ySizeDown == 0 ? GralPos.samesize : 
          (p.yPosFrac > 0 && p.ySizeDown < 0 ? p.ySizeDown -1 : p.ySizeDown) + GralPos.size;
        int xSize = p.xWidth == GralPos.useNatSize ? p.xWidth : 
          p.xWidth == 0 ? GralPos.samesize :
          (p.xPosFrac > 0 && p.xWidth < 0 ? p.xWidth -1 : p.xWidth) + GralPos.size;
        mng.setFinePosition(p.yPos, p.yPosFrac, ySize, p.ySizeFrac, p.xPos, p.xPosFrac, xSize, p.xSizeFrac, 0, 'r', 0, 0, null);  
      }
      else {
        stop();
        //no position given. The new position will be set relative to the old one on widget.setToPanel.
      }
      //  setPosOld(guiMng);  //use inputPos
    }
  }  
  
  
  
  /**Builds the position.
   * @param cfge deprecated, it is this.
   * @param guiMng to set the position.
   * @return
   */
  private void XXXsetPosOld(GralMngBuild_ifc guiMng) {
    GralCfgPosition prevPos = this.previous !=null ? this.previous.position : this.positionInput;
    GralCfgPosition pos = this.position;        //NOTE: it is filled here.
    GralCfgPosition inp = this.positionInput;
    if(this.widgetType.text !=null && this.widgetType.text.equals("wd:yCos"))
      stop();

    pos.xIncr_ = inp.xIncr_ || (!inp.yIncr_ && prevPos.xIncr_);  //inherit xIncr but not if yIncr. 
    pos.yIncr_ = inp.yIncr_ || (!inp.xIncr_ && prevPos.yIncr_);
    //yPos
    if(inp.yPosFrac !=0){
      stop();
    }
    if(inp.yPos >=0){
      pos.yPos = inp.yPos;
      pos.yPosFrac = inp.yPosFrac;
    } else if(pos.yIncr_){ //position = previous + heigth/width
      int yPosAdd = 0;  
      if(prevPos.ySizeDown >=0){ //positive if yPos is on top of widget.
        pos.yPosFrac = prevPos.yPosFrac + prevPos.ySizeFrac;  //frac part from pos + size
        if(pos.yPosFrac >=10){ yPosAdd = 1; pos.yPosFrac -=10; } //overflow detection >1
      } else { //negative if yPos is the bottom line.
        pos.yPosFrac = prevPos.yPosFrac - prevPos.ySizeFrac;  //ySizeFrac is a positiv number always.
        if(pos.yPosFrac <=0){ yPosAdd = -1; pos.yPosFrac +=10; }
      }
      pos.yPos = prevPos.yPos + prevPos.ySizeDown + yPosAdd;
    } else { //!prevPos.Incr: use the previous position
      pos.yPos = prevPos.yPos;
      pos.yPosFrac = prevPos.yPosFrac;
    }
    //xPos
    if(inp.xPos >=0){
      pos.xPos = inp.xPos;
      pos.xPosFrac = inp.xPosFrac;
    } else if(pos.xIncr_ || (inp.yPos < 0 && ! pos.yIncr_)){ //if same x and y but no increment, then default increment x 
      //position = previous + width
      int xPosAdd = 0;  
      if(prevPos.xWidth >=0){ //positive if yPos is on top of widget.
        pos.xPosFrac = prevPos.xPosFrac + prevPos.xSizeFrac;
        if(pos.xPosFrac >=10){ xPosAdd = 1; pos.xPosFrac -=10; }
      } else { //negative if yPos is the bottom line.
        pos.xPosFrac = prevPos.xPosFrac - prevPos.xSizeFrac;  //ySizeFrac is a positiv number always.
        if(pos.xPosFrac <=0){ xPosAdd = -1; pos.xPosFrac +=10; }
      }
      pos.xPos = prevPos.xPos + prevPos.xWidth + xPosAdd;
    } else { //!prevPos.Incr: use the previous position
      pos.xPos = prevPos.xPos;
      pos.xPosFrac = prevPos.xPosFrac;
    }
    //ySizeDown, xWidth
    if(inp.ySizeFrac !=0 && inp.ySizeDown == -3){
      stop();
    }
    if(inp.ySizeDown !=0){ //ySize is given here.
      pos.ySizeDown = inp.ySizeDown;
      pos.ySizeFrac = inp.ySizeFrac;
    } else { //use ySize from previous.
      pos.ySizeDown = prevPos.ySizeDown;
      pos.ySizeFrac = prevPos.ySizeFrac;
    }
    if(inp.xWidth !=0){ //xWidth is given here
      pos.xWidth = inp.xWidth;
      pos.xSizeFrac = inp.xSizeFrac;
    } else { //use xWidth from previous
      pos.xWidth = prevPos.xWidth;
      pos.xSizeFrac = prevPos.xSizeFrac;
    }
    //
    pos.panel = inp.panel !=null ? inp.panel : prevPos.panel;
    //
    if(pos.xWidth == Integer.MAX_VALUE)
      stop();
    if(pos.yPos == 4 && pos.xPos == 56){
      stop();
    }
 
    final char dirNext;
    if(pos.yIncr_){ dirNext = 'd';}
    else if(pos.xIncr_){ dirNext = 'r';}
    else { dirNext = '.'; }
    final int heightArg;
    if(pos.ySizeDown == Integer.MAX_VALUE){ heightArg = GralPos.useNatSize; }
    else if(pos.ySizeDown < 0 && pos.ySizeFrac !=0) { 
      heightArg = pos.ySizeDown -1 + GralPos.size;    //forex -3.5 means really 3.5 to up, use -4, 5 
    } else {heightArg = pos.ySizeDown + GralPos.size; }
    
    int widthArg = pos.xWidth == Integer.MAX_VALUE ? GralPos.useNatSize : pos.xWidth + GralPos.size;
    try{
      guiMng.setFinePosition(pos.yPos, pos.yPosFrac, heightArg, pos.ySizeFrac
        , pos.xPos, pos.xPosFrac, widthArg, pos.xSizeFrac, 1, dirNext, 0, 0, null);
    } catch(IllegalArgumentException exc){
      System.err.println("GralCfgElement - setPos Argument Error; " + exc.getMessage() + "; line=" + this.content);
      throw exc;
    }
    

  }
  
  
  public String getPanel(){ return positionInput.panel; }

  void setPanel(String panel){ position.panel = positionInput.panel = panel; }
  
  public int get_xPos(){ return positionInput.xPos; }
  
  public int get_yPos(){ return positionInput.yPos; }
  
  
  /**ZBNF: <?position> */
  public GralCfgPosition new_position(){ return positionInput; }  
  
  /**ZBNF: <*:?position> */
  public void set_positionString(String val) { this.positionString = val; }
 
  /**ZBNF: [<?position>   ] it is unnecessary*/
  public void set_position(String val) {  }
 
  /**ZBNF: <?position> */
  public void set_position(GralCfgPosition val) {  } //is set only
 
  /**ZBNF: Text::= */
  public GuiCfgText new_Text()
  { GuiCfgText widgetType1 = new GuiCfgText(this); 
    this.widgetType = widgetType1; 
    return widgetType1;
  }
  
  /**ZBNF: Text::= */
  public void set_Text(GuiCfgText data){  }
  
  public WidgetTypeBase new_InputTextbox()
  { GralCfgData.WidgetTypeBase widgt = new GralCfgData.WidgetTypeBase(this, 't');
    this.widgetType = widgt;
    return widgt;
  }
  
  public void set_InputTextbox(WidgetTypeBase data) {  }
  
  public WidgetTypeBase new_InputTextline()
  { GralCfgData.WidgetTypeBase widgt = new GralCfgData.WidgetTypeBase(this, 'T');
    this.widgetType = widgt;
    return widgt;
  }
  
  public void set_InputTextline(WidgetTypeBase data) {  }
  
  public WidgetTypeBase new_InputFile()
  { GuiCfgInputFile widgt = new GuiCfgInputFile(this);
    this.widgetType = widgt;
    return widgt;
  }
  
  public void set_InputFile(WidgetTypeBase data) {  }
  
  public WidgetTypeBase new_Button()
  { GuiCfgButton widgt = new GuiCfgButton(this);
    this.widgetType = widgt;
    return widgt;
  }
  
  public void set_Button(WidgetTypeBase data) {  }
  
  public WidgetTypeBase new_SwitchButton()
  { GuiCfgButton widgt = new GuiCfgButton(this, true);
    this.widgetType = widgt;
    return widgt;
  }
  
  public void set_SwitchButton(WidgetTypeBase data) {  }
  
  /**ZBNF: Led::= */
  public GuiCfgLed new_Led()
  { GuiCfgLed widgetType1 = new GuiCfgLed(this); 
    this.widgetType = widgetType1; 
    return widgetType1;
  }
  
  /**ZBNF: Led::= */
  public void set_Led(GuiCfgLed data){  }
  
  /**ZBNF: ValueBar::= */
  public GralCfgData.WidgetTypeBase new_ValueBar()
  { GralCfgData.WidgetTypeBase widgt = new GralCfgData.WidgetTypeBase(this, 'U');
    this.widgetType = widgt;
    return widgt;
  }

  
  /**ZBNF: ValueBar::= */
  public void set_ValueBar(GralCfgData.WidgetTypeBase data){  }
  
  /**ZBNF: Line::= */
  public GuiCfgLine new_Line()
  { GuiCfgLine widgetType1 = new GuiCfgLine(this); 
    this.widgetType = widgetType1; 
    return widgetType1;
  }
  
  /**ZBNF: Line::= */
  public void set_Line(GuiCfgLine data){  }
  
  /**ZBNF: Imagefile::= */
  public GuiCfgImage new_Imagefile()
  { GuiCfgImage widgetType1 = new GuiCfgImage(this); 
    this.widgetType = widgetType1; 
    return widgetType1;
  }
  
  /**ZBNF: ShowField::= */
  public void set_Imagefile(GuiCfgImage data){  }
  
  /**ZBNF: ShowField::= */
  public GuiCfgShowField new_ShowField()
  { GuiCfgShowField widgetType1 = new GuiCfgShowField(this); 
    this.widgetType = widgetType1; 
    return widgetType1;
  }
  
  /**ZBNF: ShowField::= */
  public void set_ShowField(GuiCfgShowField data){  }
  
  /**ZBNF: Table::= */
  public GuiCfgTable new_Table()
  { GuiCfgTable widgetType1 = new GuiCfgTable(this); 
    this.widgetType = widgetType1; 
    return widgetType1;
  }
  
  /**ZBNF: Table::= */
  public void set_Table(GuiCfgTable data){  }
  
  /**ZBNF: Curveview::= */
  public GuiCfgCurveview new_Curveview()
  { GuiCfgCurveview widgetType1 = new GuiCfgCurveview(this); 
    this.widgetType = widgetType1; 
    return widgetType1;
  }
  
  /**ZBNF: Curveview::= */
  public void set_Curveview(GuiCfgCurveview data){  }

  
  private void stop(){}
  
}//class GuiCfgElement


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
import org.vishia.gral.cfg.GralCfgData.GuiCfgWidget;
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
  
  String panel;
  
  /**ZBNF: Position coordinates will be filled from [<?position>.... 
   * The instance contains only that position data, which are found in the textual config file. 
   * It is important to rewrite only that data, which are contained in the originally one. */
  final GralCfgPosition XXXpositionInput = new GralCfgPosition();
  
  /**This obj contains all position data. Missed position data in the {@link #positionInput}
   * are completed by knowledge of the position of the previous elements. 
   * The position is filled only in {@link #setPos(GralMngBuild_ifc)} and used nearly only temporary
   * */
  private final GralCfgPosition position = new GralCfgPosition();
  
  //private final GralPos posInput = new GralPos();
  
  GuiCfgWidget widgetType;
  
  GralCfgElement() //GralCfgData itsCfgData)
  { //this.itsCfgData = itsCfgData;
  }
  
  @Override
  public GralCfgElement clone(){ 
    GralCfgElement newObj = new GralCfgElement();  //this.itsCfgData);
    newObj.widgetType = widgetType.clone(); ///use a new cloned instance (use data).
    //newObj.positionInput.set(this.positionInput);
    newObj.positionString = positionString;
    newObj.panel = panel;
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
  GralPos XXXsetPos(GralMngBuild_ifc guiMng) 
  throws ParseException 
  { GralMng mng = GralMng.get();
    if(positionString !=null) {
      return mng.setPos(positionString);
    } else {
      GralCfgPosition p = XXXpositionInput;
      if(p.yPos >=0 || p.xPos >=0 || p.ySizeDown !=0 || p.xWidth !=0) {
          
        int ySize = p.ySizeDown == GralPos.useNatSize ? p.ySizeDown :
          p.ySizeDown == 0 ? GralPos.samesize : 
          (p.ySizeFrac > 0 && p.ySizeDown < 0 ? p.ySizeDown -1 : p.ySizeDown) + GralPos.size;
        int xSize = p.xWidth == GralPos.useNatSize ? p.xWidth : 
          p.xWidth == 0 ? GralPos.samesize :
          (p.xSizeFrac > 0 && p.xWidth < 0 ? p.xWidth -1 : p.xWidth) + GralPos.size;
        mng.setFinePosition(p.yPos, p.yPosFrac, ySize, p.ySizeFrac, p.xPos, p.xPosFrac, xSize, p.xSizeFrac, 0, 'r', 0, 0, null);  
      }
      else {
        stop();
        //no position given. The new position will be set relative to the old one on widget.setToPanel.
      }
      return mng.getPositionInPanel();
      //  setPosOld(guiMng);  //use inputPos
    }
  }  
  
  
  
  
  public String getPanel(){ return panel; }

  void setPanel(String panel){ this.panel = panel; }
  
  public int XXXget_xPos(){ return XXXpositionInput.xPos; }
  
  public int XXXget_yPos(){ return XXXpositionInput.yPos; }
  
  
  /**ZBNF: <?position> */
  public GralCfgPosition XXXnew_position(){ return XXXpositionInput; }  
  
  /**ZBNF: <*:?position> */
  public void set_positionString(String val) { 
    this.positionString = val; 
  }
 
  /**ZBNF: <*:?position> */
  public void set_panel(String val) { 
    this.panel = val; 
  }
 
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
  
  public GuiCfgWidget new_InputTextbox()
  { GralCfgData.GuiCfgWidget widgt = new GralCfgData.GuiCfgWidget(this, 't');
    this.widgetType = widgt;
    return widgt;
  }
  
  public void set_InputTextbox(GuiCfgWidget data) {  }
  
  public GuiCfgWidget new_OutputTextbox()
  { GralCfgData.GuiCfgWidget widgt = new GralCfgData.GuiCfgWidget(this, 't');
    this.widgetType = widgt;
    return widgt;
  }
  
  public void set_OutputTextbox(GuiCfgWidget data) {  }
  
  public GuiCfgWidget new_InputTextline()
  { GralCfgData.GuiCfgWidget widgt = new GralCfgData.GuiCfgWidget(this, 'T');
    this.widgetType = widgt;
    return widgt;
  }
  
  public void set_InputTextline(GuiCfgWidget data) {  }
  
  public GuiCfgWidget new_InputFile()
  { GuiCfgInputFile widgt = new GuiCfgInputFile(this);
    this.widgetType = widgt;
    return widgt;
  }
  
  public void set_InputFile(GuiCfgWidget data) {  }
  
  public GuiCfgWidget new_Button()
  { GuiCfgButton widgt = new GuiCfgButton(this);
    this.widgetType = widgt;
    return widgt;
  }
  
  public void set_Button(GuiCfgWidget data) {  }
  
  public GuiCfgWidget new_SwitchButton()
  { GuiCfgButton widgt = new GuiCfgButton(this, true);
    this.widgetType = widgt;
    return widgt;
  }
  
  public void set_SwitchButton(GuiCfgWidget data) {  }
  
  /**ZBNF: Led::= */
  public GuiCfgLed new_Led()
  { GuiCfgLed widgetType1 = new GuiCfgLed(this); 
    this.widgetType = widgetType1; 
    return widgetType1;
  }
  
  /**ZBNF: Led::= */
  public void set_Led(GuiCfgLed data){  }
  
  /**ZBNF: ValueBar::= */
  public GralCfgData.GuiCfgWidget new_ValueBar()
  { GralCfgData.GuiCfgWidget widgt = new GralCfgData.GuiCfgWidget(this, 'U');
    this.widgetType = widgt;
    return widgt;
  }

  
  /**ZBNF: ValueBar::= */
  public void set_ValueBar(GralCfgData.GuiCfgWidget data){  }
  
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

  
  
  @Override public String toString() {
    return (this.positionString + ":" + this.widgetType);
  }
  
  private void stop(){}
  
}//class GuiCfgElement


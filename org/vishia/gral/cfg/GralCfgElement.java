package org.vishia.gral.cfg;

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
import org.vishia.gral.ifc.GralWidgetCfg_ifc;


/**ZBNF: Element::= ... ;
 * Class for instance to capture and store one element. */
public class GralCfgElement implements Cloneable, GralWidgetCfg_ifc
{ 
  /**The previous element is necessary because non complete coordinates are resolved with previous.
   * The next element is need to build a queue in order of the text. */
  GralCfgElement previous, next;
  
  final GralCfgData itsCfgData;
  
  /**The content is set in textual form too. It is because [<?Element>...] was written */
  String content;
  
  /**The position is set in textual form too. It is because [<?Element>...] was written */
  private String positionString;
  
  /**ZBNF: Position coordinates will be filled from [<?position>.... 
   * The instance contains only that position data, which are found in the textual config file. 
   * It is important to rewrite only that data, which are contained in the originally one. */
  GralCfgPosition positionInput = new GralCfgPosition();
  
  /**This obj contains all position data. Missed position data in the {@link #positionInput}
   * are completed by knowledge of the position of the previous elements. */
  GralCfgPosition position = new GralCfgPosition();
  
  WidgetTypeBase widgetType;
  
  GralCfgElement(GralCfgData itsCfgData)
  { this.itsCfgData = itsCfgData;
  }
  
  public GralCfgElement clone(){ 
    GralCfgElement newObj = null;
    try{ newObj = (GralCfgElement)super.clone(); 
      newObj.position = new GralCfgPosition(); //use a new empty instance in cloned object, empty data.
      newObj.positionInput = positionInput.clone(); ///use a new cloned instance (use data).
      newObj.widgetType = widgetType.clone(); ///use a new cloned instance (use data).
    } catch(CloneNotSupportedException exc){ assert(false); }
    newObj.previous = this;  //link it in queue after this.
    newObj.next = next;
    next = newObj;
    return newObj; 
  }
  
  /**ZBNF: <?position> */
  public GralCfgPosition new_position(){ return positionInput; }  
  
  /**ZBNF: <?position> */
  public void set_position(String val) { positionString = val; }
 
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
  
  /**ZBNF: Text::= */
  public void set_Led(GuiCfgLed data){  }
  
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
  
}//class GuiCfgElement


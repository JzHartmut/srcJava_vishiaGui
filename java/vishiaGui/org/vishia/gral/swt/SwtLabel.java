package org.vishia.gral.swt;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.widget.GralLabel;

public class SwtLabel extends GralLabel.GraphicImplAccess
{
  /**Version, history and license.
   * <ul>
   * <li>2023-02-11 Hartmut bugfix, bug on negative position values (from left/bottom) 
   * <li>2013..22 some changes see git repository.
   * <li>2013-12-23 Hartmut new: Uses the new concept of widget implementation.
   * </ul>
   * 
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
   * 
   */
  @SuppressWarnings("hiding")
  public static final String version = "2023-02-11";
  
  /**It contains the association to the swt widget (Control) and the {@link SwtMng}
   * and implements some methods of {@link GralWidgImplAccess_ifc} which are delegate from this.
   */
  private final SwtWidgetHelper swtWidgHelper;
  
  protected SwtTransparentLabel labelSwt;

  private Font fontSwt;
  
  SwtLabel(GralLabel widgg, SwtMng swtMng)
  {
    widgg.super(widgg, swtMng.gralMng);                           // calls the super ctor of this but with the instance of the environment class.
    GralPos pos = widgg.pos();
    GralWidget.ImplAccess parentImpl = pos.parent.getImplAccess();
    Composite panelSwt = SwtMng.getSwtParent(pos);
    //assert(parentImpl.tabFolder ==null);
    int styleSwt = 0;
    this.labelSwt = new SwtTransparentLabel(panelSwt, styleSwt);
    super.wdgimpl = this.swtWidgHelper = new SwtWidgetHelper(this.labelSwt, swtMng);
    int mode;
    switch(origin()){
    case 1: mode = SWT.LEFT; break;
    case 2: mode = SWT.CENTER; break;
    case 3: mode = SWT.RIGHT; break;
    case 4: mode = SWT.LEFT; break;
    case 5: mode = SWT.CENTER; break;
    case 6: mode = SWT.RIGHT; break;
    case 7: mode = SWT.LEFT; break;
    case 8: mode = SWT.CENTER; break;
    case 9: mode = SWT.RIGHT; break;
    default: mode = 0;
    }
    this.labelSwt.setAlignment(mode);
    //swtMng.setPosAndSizeSwt(this.widgg.pos(), this.labelSwt, 0, 0);
    GralRectangle rectangle = swtMng.calcWidgetPosAndSizeSwt(this.widgg.pos(), panelSwt, 0, 0);
    this.labelSwt.setBounds(rectangle.x, rectangle.y, rectangle.dx, rectangle.dy );
    Color color = swtMng.getColorImpl(super.dyda().textColor);
    this.labelSwt.setForeground(color);
    float height = this.widgg.pos().height();
    GralFont gralFont = swtMng.gralMng.gralProps.getTextFont(height);
    Font swtFont = swtMng.propertiesGuiSwt.fontSwt(gralFont);
    this.labelSwt.setFont(swtFont);
    FontData[] fontData = swtFont.getFontData();
    float fontHeigth = fontData[0].height;
    System.out.println(super.dyda().displayedText + " dy=" + rectangle.dy + " height=" + height + " fontHeight = " + fontHeigth);
    //widgg.setText(widgg.getText() + " dy=" + rectangle.dy + " height=" + height);
    //on SWT it invokes the resize listener if given.
    //swtMng.gralMng.registerWidget(widgg);
    redrawGthread();  //to set text etc.
  }


  @Override
  public GralRectangle getPixelPositionSize(){ return swtWidgHelper.getPixelPositionSize(); }


  @Override
  public Object getWidgetImplementation(){ return labelSwt; }


  @Override public void removeWidgetImplementation()
  { swtWidgHelper.removeWidgetImplementation();
    labelSwt = null;
  }


  @Override
  public void redrawGthread()
  {
    if(labelSwt !=null){ //do nothing if the graphic implementation widget is removed.
      GralWidget.DynamicData dyda = dyda();
      int chg, catastrophicalCount =0;
      while( (chg = getChanged()) !=0){ //widgg.dyda.whatIsChanged.get();
        if(++catastrophicalCount > 10000) 
          throw new RuntimeException("atomic failed");
        if((chg & chgColorText)!=0) { //firstly set the font
          SwtProperties props = swtWidgHelper.mng.propertiesGuiSwt;
          if((chg & chgVisible)!=0){
            labelSwt.setVisible(true);
          }
          if((chg & chgInvisible)!=0){
            labelSwt.setVisible(false);
          }
          if(dyda.textFont !=null){
            fontSwt = props.fontSwt(dyda.textFont);
            labelSwt.setFont(fontSwt);
          }
          if(dyda.textColor !=null){
            labelSwt.setForeground(props.colorSwt(dyda.textColor));
          }
          if(dyda.backColor !=null){
            labelSwt.setBackground(props.colorSwt(dyda.backColor));
          }
        }
        if((chg & chgText) !=0 && dyda.displayedText !=null){ 
          labelSwt.setText(dyda.displayedText);
          //font.getFontData()[0].
          Point textSize = labelSwt.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
          //int width = sText.length();
          //widget.setSize(sizePixel);
          
          Point widgetSize = labelSwt.getSize();
          if(widgetSize.x < textSize.x){
            labelSwt.setSize(textSize);
          }
          //labelSwt.setSize(textSize);
        }
        acknChanged(chg);
      }
      labelSwt.redraw();
      labelSwt.update();
    }
  }


  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { swtWidgHelper.setBoundsPixel(x, y, dx, dy); 
  }


  @Override public boolean setFocusGThread()
  { return swtWidgHelper.setFocusGThread(); }
  
  @Override public void setVisibleGThread(boolean bVisible) { super.setVisibleState(bVisible); swtWidgHelper.setVisibleGThread(bVisible); }


  @Override
  public void updateValuesForAction() {
    // TODO Auto-generated method stub
    
  }

}

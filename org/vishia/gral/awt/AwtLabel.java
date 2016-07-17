package org.vishia.gral.awt;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Label;

import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.widget.GralLabel;

public class AwtLabel extends GralLabel.GraphicImplAccess
{
  /**Version, history and license.
   * <ul>
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
  public static final int version = 20120317;
  
  /**It contains the association to the swt widget (Control) and the {@link SwtMng}
   * and implements some methods of {@link GralWidgImpl_ifc} which are delegate from this.
   */
  private final AwtWidgetHelper widgHelper;
  
  protected Label labelAwt;

  private Font fontAwt;
  
  
  AwtLabel(GralLabel widgg, AwtWidgetMng mng)
  {
    widgg.super(widgg, mng.mng);
    //Container panelSwt = mng.getCurrentPanel();
    //int styleSwt = 0;
    int mode;
    switch(origin()){
    case 1: mode = Label.LEFT; break;
    case 2: mode = Label.CENTER; break;
    case 3: mode = Label.RIGHT; break;
    case 4: mode = Label.LEFT; break;
    case 5: mode = Label.CENTER; break;
    case 6: mode = Label.RIGHT; break;
    case 7: mode = Label.LEFT; break;
    case 8: mode = Label.CENTER; break;
    case 9: mode = Label.RIGHT; break;
    default: mode = 0;
    }
    labelAwt = new Label(dyda().displayedText, mode);
    Container panelAwt = (Container)widgg.pos().panel.getWidgetImplementation();
    panelAwt.add(labelAwt);
    widgHelper = new AwtWidgetHelper(labelAwt, mng);
    labelAwt.setAlignment(mode);
    mng.setBounds_(widgg.pos(), labelAwt);
    mng.mng.registerWidget(widgg);
    repaintGthread();  //to set text etc.
  }


  @Override
  public GralRectangle getPixelPositionSize(){ return widgHelper.getPixelPositionSize(); }


  @Override
  public Object getWidgetImplementation(){ return labelAwt; }


  @Override public void removeWidgetImplementation()
  { widgHelper.removeWidgetImplementation();
    labelAwt = null;
  }


  @Override
  public void repaintGthread()
  {
    if(labelAwt !=null){ //do nothing if the graphic implementation widget is removed.
      GralWidget.DynamicData dyda = dyda();
      int chg, catastrophicalCount =0;
      while( (chg = getChanged()) !=0){ //widgg.dyda.whatIsChanged.get();
        if(++catastrophicalCount > 10000) 
          throw new RuntimeException("atomic failed");
        if((chg & chgColorText)!=0) {  //firstly set the font.
          AwtProperties props = widgHelper.mng.propertiesGuiAwt;
          if(dyda.textFont !=null){
            fontAwt = props.fontAwt(dyda.textFont);
            labelAwt.setFont(fontAwt);
          }
          if(dyda.textColor !=null){
            labelAwt.setForeground(props.colorAwt(dyda.textColor));
          }
          if(dyda.backColor !=null){
            labelAwt.setBackground(props.colorAwt(dyda.backColor));
          }
        }
        if((chg & chgText) !=0 && dyda.displayedText !=null){ 
          labelAwt.setText(dyda.displayedText);
          //font.getFontData()[0].
          //FontMetrics ff; ff.
          //labelAwt.getFontMetrics(font)
          Dimension textSize = new Dimension(10* dyda.displayedText.length(), 20);
              //labelSwt.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
          //int width = sText.length();
          //widget.setSize(sizePixel);
          
          Dimension widgetSize = labelAwt.getSize();
          if(widgetSize.width < textSize.width){
            labelAwt.setSize(textSize);
          }
          //labelAwt.setSize(textSize);
        }
        acknChanged(chg);
      }
      labelAwt.repaint();
    }
  }


  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { widgHelper.setBoundsPixel(x, y, dx, dy); 
  }


  @Override public boolean setFocusGThread()
  { return widgHelper.setFocusGThread(); }


  /**Sets the implementation widget vible or not.
   * @see org.vishia.gral.base.GralWidgImpl_ifc#setVisibleGThread(boolean)
   */
  @Override public void setVisibleGThread(boolean bVisible){ super.setVisibleState(bVisible); widgHelper.setVisibleGThread(bVisible); }

  
}

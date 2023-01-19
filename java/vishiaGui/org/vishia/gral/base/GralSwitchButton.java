package org.vishia.gral.base;

import org.vishia.gral.ifc.GralColor;

/**A switch button is exactly also a GralButton. The switch capability is contained in GralButton.
 * Only the proper constructors are delivered here. 
 * @author hartmut Schorrig
 * @since 2023-01
 */
public class GralSwitchButton extends GralButton {

  /**Constructs a Switch Button with 2 states.
   * @param refPos Reference position
   * @param sPosName maybe position string and name due to {@link GralWidget#GralWidget(GralPos, String, char)}
   * @param sTextOn
   * @param sTextOff
   * @param color maybe dismiss, elsewhere 2 colors for on, off
   */
  public GralSwitchButton(GralPos refPos, String sPosName, String sTextOn, String sTextOff, GralColor ... color) {
    super(refPos, sPosName, null, null);
    super.setSwitchMode(sTextOn, sTextOff);
    if(color !=null) {
      assert(color.length==2);
      super.setSwitchMode(color[0], color[1]);
    }
  }

  
  /**Constructs a Switch Button with also disabled 3th state.
   * @param refPos Reference position
   * @param sPosName maybe position string and name due to {@link GralWidget#GralWidget(GralPos, String, char)}
   * @param sTextOn
   * @param sTextOff
   * @param sDisabled
   * @param color maybe dismiss, elsewhere 3 colors for on, off and disabled
   */
  public GralSwitchButton(GralPos refPos, String sPosName, String sTextOn, String sTextOff, String sDisabled, GralColor ... color) {
    super(refPos, sPosName, null, null);
    super.setSwitchMode(sTextOn, sTextOff, sDisabled);
    if(color !=null) {
      assert(color.length==3);
      super.setSwitchMode(color[0], color[1], color[2]);
    }
  }

  
}

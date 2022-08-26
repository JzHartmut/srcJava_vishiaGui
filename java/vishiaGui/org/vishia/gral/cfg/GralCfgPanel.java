package org.vishia.gral.cfg;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.vishia.gral.base.GralPos;
import org.vishia.gral.ifc.GralRectangle;

final public class GralCfgPanel extends GralCfgElement
{
  /**Version, history and license.
   * <ul>
   * <li>2016-06-27 Hartmut new: The panel can be a window too. 
   * <li>2011-06-00 Hartmut created
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
  public static final int version = 20120303;

  String name;
  final List<GralCfgElement> listElements = new ArrayList<GralCfgElement>();
  
  
  String windTitle;
  
  String windPos;
  
  GralCfgPanel(){  }

  GralCfgPanel(String name){ this.name = name; }


  /**Set from ZBNF: */
  public void set_windIdent(String val){ name = val;  }
  
  public void set_windTitle(String val){ windTitle = val; }
  
  public void set_windPos(String val) { windPos = val; }


}

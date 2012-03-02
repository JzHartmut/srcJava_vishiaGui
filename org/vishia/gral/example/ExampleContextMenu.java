package org.vishia.gral.example;

import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralWidgetMng;

public class ExampleContextMenu extends ExampleSimpleButton
{

  /**Version, history and license.
   * <ul>
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

  ExampleContextMenu(GralWidgetMng gralMng)
  {
    super(gralMng);
  }
  
  
  /**The main routine. It creates the factory of this class
   * and then calls {@link #main(String[], Factory)}.
   * With that pattern a derived class may have a simple main routine too.
   * @param args command line arguments.
   */
  public static void main(String[] args)
  {
    main(args, new Factory());
  
  }  


 
  protected class InitGuiCodeContextMenu extends ExampleSimpleButton.InitGuiCodeSimpleButton{
    @Override public void doBeforeDispatching(boolean onlyWakeup){
      super.doBeforeDispatching(onlyWakeup);
      //GralMenu menuInput = gui.gralMng.createContextMenu("menu-test", gui.widgInput);
      GralMenu menuInput = gui.widgInput.getContextMenu();
    }

  }
  
  
  
  /**This inner class creates this class with given parameter.
   */
  static class Factory extends ExampleSimpleButton.Factory{
    ExampleSimpleButton create(GralWidgetMng gralMng){
      ExampleContextMenu obj = new ExampleContextMenu(gralMng);
      obj.setInitGuiCode(obj.new InitGuiCodeContextMenu());
      return obj;
    }
  }
  

  
  
}

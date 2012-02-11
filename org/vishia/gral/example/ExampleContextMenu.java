package org.vishia.gral.example;

import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralWidgetMng;

public class ExampleContextMenu extends ExampleSimpleButton
{

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

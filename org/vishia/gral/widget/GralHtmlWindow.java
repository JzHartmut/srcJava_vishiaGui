package org.vishia.gral.widget;

import org.vishia.gral.base.GralHtmlBox;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWindow;

public final class GralHtmlWindow
{
  /**The window is created invoking the {@link GralMngBuild_ifc#createWindow(String, boolean)}. 
   * It has its implementation in the underlying graphic system.  */
  private final GralWindow window;
  
  
  /**The widget which holds the text in the {@link #window}. */
  private final GralTextBox textBox;
  
  private final GralHtmlBox htmlBox;

  public GralHtmlWindow(GralWindow window, GralHtmlBox htmlBox)
  {
    this.window = window;
    this.textBox = null;
    this.htmlBox = htmlBox;
  }

}

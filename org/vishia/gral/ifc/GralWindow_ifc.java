package org.vishia.gral.ifc;

import org.vishia.gral.base.GralWindow_setifc;


public interface GralWindow_ifc extends GralWindow_getifc, GralWindow_setifc 
{
  public final static int version = 0; 
  
  /**Property defines that the window is opened exclusive for the application. 
   * It means that the primary window is not accessible if this window is opened.
   * It is 'application modal'. 
   */
  public static final int windExclusive = 1<<16;
  
  /**Property defines that the window is opened concurrently together with other windows 
   * of the application, especially concurrently to the primary window. This property should be set
   * if the {@link #windExclusive} property is not set.
   * It is 'application non-modal'. 
   */
  public static final int windConcurrently = 1<<30;
  
  public static final int windOnTop =  1 << 14;
  
  public static final int windHasMenu =  1 << 31;
  

}

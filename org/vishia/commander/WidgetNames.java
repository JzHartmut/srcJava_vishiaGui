package org.vishia.commander;

public class WidgetNames
{

  /**Name of the Table-widget and the key in {@link JavaCmd#idxFileSelector} for a file table. 
   * The name is following by the visible name of the tab. */
  public final static String tableFile = "panelFile_";
  
  /**Name of the tab panel which contains a file table. 
   * The name is following by the visible name of the tab. */
  public final static String tabFile = "tabFile_";
  
  /**Name of the main tabbed panel for left,mid and right. */
  public final static String panelLeftMidRigth = "mainTabs";  //+1 +2 +3
  
  
  /**Name of the tab inside the tabbed main panel for left, mid, right 
   * which contains a tabbed panel for the tabs for selection. */
  public final static String tabFavoritesLeftMidRight = "tabSelectTabs";   //+1 +2 +3 
  
  /**Name of the panel inside the main left, mid, right panel 
   * which contains a tabbed panel for the tabs for selection. */
  public final static String panelFavoritesLeftMidRight = "selectTabs";   //+1 +2 +3 
  
  /**Name of the tab inside panelFavoritesLeftMidRight for the main selection. */
  public final static String tabMainFavorites = "tabSelectAll";   //+1 +2 +3 
  
  /**Name of the tab inside panelFavoritesLeftMidRight for the main selection. */
  public final static String tabFavorites = "tabSelect-";   //+1 +2 +3 
  
  /**Name of the tab inside panelFavoritesLeftMidRight for the main selection. */
  public final static String tableFavorites = "tableSelect-";   //+label +1 +2 +3 
  
  /**Name of the tab inside panelFavoritesLeftMidRight for the main selection. */
  public final static String tableFavoritesMain = "tableSelectMain.";   //+1 +2 +3 
  
  /**Name of the tab inside panelFavoritesLeftMidRight for the main selection. */
  public final static String selectMainFavorites = "selectAll";   //+1 +2 +3 
  
}

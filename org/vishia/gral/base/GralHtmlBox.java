package org.vishia.gral.base;

import org.vishia.gral.ifc.GralWidget;

public abstract class GralHtmlBox extends GralWidget 
{
  protected GralHtmlBox(String name, GralWidgetMng mng)
  { super(name, 'h', mng);
  }

  public abstract void setUrl(String url);

  public abstract void activate();
  
}

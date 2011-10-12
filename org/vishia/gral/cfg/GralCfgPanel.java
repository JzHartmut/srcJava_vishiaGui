package org.vishia.gral.cfg;

import java.util.LinkedList;
import java.util.List;

final public class GralCfgPanel
{
  final String name;
  final List<GralCfgElement> listElements = new LinkedList<GralCfgElement>();
  GralCfgPanel(String name){ this.name = name; }

}

package org.vishia.guiViewCfg;

import org.vishia.byteData.VariableContainer_ifc;

public interface OamEvalValues_ifc {
  
  /**Sets another variable container to reference and used here.
   * @param vars
   */
  void setReferencedVariableContainer(VariableContainer_ifc vars);
  
  /**Set all internal used variables. */
  void setVariables ( );
  
  /**Gets the variable container, which accesses also the given {@link #setReferencedVariableContainer(VariableContainer_ifc)}
   * @return access to all variables.
   */
  VariableContainer_ifc getVariableContainer();
  
  /**Calculates with the variables, sets some variables, to use afterwards. 
   * The calculation is the core functionality of the implementation. */
  void calc ( );
  

}

package org.vishia.guiViewCfg;

import org.vishia.byteData.VariableContainer_ifc;

import java.util.List;
import java.util.Map;

import org.vishia.byteData.VariableAccess_ifc;

/**This is a general interface which can be implemented by any user class.
 * The user class can be given as plugin class per calling argument {@link ViewCfg.CallingArguments#argClassEvalRcvValues}.
 * @author Hartmut Schorrig
 *
 */
public interface OamEvalValues_ifc {
  
  /**Sets another variable container to reference and used here.
   * @param vars
   */
  void setReferencedVariableContainer(VariableContainer_ifc vars);
  
  /**Set all internal used variables. */
  void setVariables ( );
  
  Map<String, VariableAccess_ifc> getVariables();
  
  /**Gets the variable container, which accesses also the given {@link #setReferencedVariableContainer(VariableContainer_ifc)}
   * @return access to all variables.
   */
  VariableContainer_ifc getVariableContainer();
  
  /**Calculates with the variables, sets some variables, to use afterwards. 
   * The calculation is the core functionality of the implementation. */
  void calc ( );
  

}

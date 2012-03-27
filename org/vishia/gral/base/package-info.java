/**This package contains base classes which are used as super classes for the implementation level.
 * The implementation should derive this classes. Usual the implementation should contain 
 * inner non-static classes maybe derived from the graphic base classes too. Don't mix it!
 * <br><br>
 * The user may use refer this base classes only or their interfaces. The user should not refer
 * any implementation classes. To create instances of the derived implementation, use the 
 * {@link GralMng} which is derived too and which is instantiated from the factory for the correct
 * graphic implementation.
 *   
 * @author Hartmut Schorrig
 *
 */
package org.vishia.gral.base;

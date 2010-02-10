package org.antlr.v4.tool;

import java.util.LinkedHashMap;

/** Track the attributes within a scope.  A named scoped has just its list
 *  of attributes.  Each rule has potentially 3 scopes: return values,
 *  parameters, and an implicitly-named scope (i.e., a scope defined in a rule).
 *  Implicitly-defined scopes are named after the rule; rules and scopes then
 *  must live in the same name space--no collisions allowed.
 */
public class AttributeScope {
    /** The scope name */
    private String name;

    public static enum Type {
        ARG, RET, TOKEN, PREDEFINED_RULE, PREDEFINED_LEXER_RULE,
        GLOBAL_SCOPE,   // scope symbols { ...}
        RULE_SCOPE;     // scope { int i; int j; }
    }

    /** The list of Attribute objects */

    public LinkedHashMap<String, Attribute> attributes =
        new LinkedHashMap<String, Attribute>();

    public String getName() {
//        if ( isParameterScope ) {
//            return name+"_parameter";
//        }
//        else if ( isReturnScope ) {
//            return name+"_return";
//        }
        return name;
    }
    
    public String toString() {
        return getName()+":"+attributes;
    }
}

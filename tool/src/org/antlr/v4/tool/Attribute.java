package org.antlr.v4.tool;

/** Track the names of attributes define in arg lists, return values,
 *  scope blocks etc...
 */
public class Attribute {
    /** The entire declaration such as "String foo;" */
    public String decl;

    /** The type; might be empty such as for Python which has no static typing */
    public String type;

    /** The name of the attribute "foo" */
    public String name;

    /** The optional attribute intialization expression */
    public String initValue;

    public Attribute() {;}
    
    public Attribute(String name, String decl) {
        this.name = name;
        this.decl = decl;
    }

    public String toString() {
        if ( initValue!=null ) {
            return type+" "+name+"="+initValue;
        }
        return type+" "+name;
    }
}
/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.tool;

import org.antlr.runtime.Token;

/** Track the names of attributes defined in arg lists, return values,
 *  scope blocks etc...
 */
public class Attribute {
    /** The entire declaration such as "String foo" or "x:int" */
    public String decl;

    /** The type; might be empty such as for Python which has no static typing */
    public String type;

    /** The name of the attribute "foo" */
    public String name;

	/** A {@link Token} giving the position of the name of this attribute in the grammar. */
	public Token token;

    /** The optional attribute initialization expression */
    public String initValue;

	/** Who contains us? */
	public AttributeDict dict;

    public Attribute() {}

    public Attribute(String name) { this(name,null); }

    public Attribute(String name, String decl) {
        this.name = name;
        this.decl = decl;
    }

    @Override
    public String toString() {
        if ( initValue!=null ) {
	        return name+":"+type+"="+initValue;
        }
        if ( type!=null ) {
	        return name+":"+type;
        }
        return name;
    }
}

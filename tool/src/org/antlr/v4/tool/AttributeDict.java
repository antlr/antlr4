/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.tool;

import java.util.*;

/** Track the attributes within retval, arg lists etc...
 *
 *  Each rule has potentially 3 scopes: return values,
 *  parameters, and an implicitly-named scope (i.e., a scope defined in a rule).
 *  Implicitly-defined scopes are named after the rule; rules and scopes then
 *  must live in the same name space--no collisions allowed.
 */
public class AttributeDict {
    public String name;
    public GrammarAST ast;
	public DictType type;

    /** All token scopes (token labels) share the same fixed scope of
     *  of predefined attributes.  I keep this out of the runtime.Token
     *  object to avoid a runtime type leakage.
     */
    public static AttributeDict predefinedTokenDict = new AttributeDict(DictType.TOKEN) {{
        add(new Attribute("text"));
        add(new Attribute("type"));
        add(new Attribute("line"));
        add(new Attribute("index"));
        add(new Attribute("pos"));
        add(new Attribute("channel"));
        add(new Attribute("tree"));
        add(new Attribute("int"));
    }};

    public static enum DictType {
        ARG, RET, TOKEN,
		PREDEFINED_RULE, PREDEFINED_TREE_RULE, PREDEFINED_LEXER_RULE,
        GLOBAL_SCOPE,   // scope symbols { ...}
        RULE_SCOPE;     // scope { int i; int j; }
    }

    /** The list of Attribute objects */

    public LinkedHashMap<String, Attribute> attributes =
        new LinkedHashMap<String, Attribute>();

	public AttributeDict() {;}
	public AttributeDict(DictType type) { this.type = type; }

	public Attribute add(Attribute a) { a.dict = this; return attributes.put(a.name, a); }
    public Attribute get(String name) { return attributes.get(name); }

    public String getName() {
//        if ( isParameterScope ) {
//            return name+"_parameter";
//        }
//        else if ( isReturnScope ) {
//            return name+"_return";
//        }
        return name;
    }

    public int size() { return attributes==null?0:attributes.size(); }

    /** Return the set of keys that collide from
     *  this and other.
     */
    public Set intersection(AttributeDict other) {
        if ( other==null || other.size()==0 || size()==0 ) {
            return null;
        }
        Set<String> inter = new HashSet<String>();
        Set thisKeys = attributes.keySet();
        for (Iterator it = thisKeys.iterator(); it.hasNext();) {
            String key = (String) it.next();
            if ( other.attributes.get(key)!=null ) {
                inter.add(key);
            }
        }
        if ( inter.size()==0 ) {
            return null;
        }
        return inter;
    }

    public String toString() {
        return getName()+":"+attributes;
    }
}

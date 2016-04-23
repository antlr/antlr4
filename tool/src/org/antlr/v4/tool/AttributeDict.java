/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.tool;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

/** Track the attributes within retval, arg lists etc...
 *  <p>
 *  Each rule has potentially 3 scopes: return values,
 *  parameters, and an implicitly-named scope (i.e., a scope defined in a rule).
 *  Implicitly-defined scopes are named after the rule; rules and scopes then
 *  must live in the same name space--no collisions allowed.
 */
public class AttributeDict {
    public String name;
    public GrammarAST ast;
	public DictType type;

    /** All {@link Token} scopes (token labels) share the same fixed scope of
     *  of predefined attributes.  I keep this out of the {@link Token}
     *  interface to avoid a runtime type leakage.
     */
    public static final AttributeDict predefinedTokenDict = new AttributeDict(DictType.TOKEN);
    static {
        predefinedTokenDict.add(new Attribute("text"));
        predefinedTokenDict.add(new Attribute("type"));
        predefinedTokenDict.add(new Attribute("line"));
        predefinedTokenDict.add(new Attribute("index"));
        predefinedTokenDict.add(new Attribute("pos"));
        predefinedTokenDict.add(new Attribute("channel"));
        predefinedTokenDict.add(new Attribute("int"));
    }

    public static enum DictType {
        ARG, RET, LOCAL, TOKEN,
		PREDEFINED_RULE, PREDEFINED_LEXER_RULE,
    }

    /** The list of {@link Attribute} objects. */

    public final LinkedHashMap<String, Attribute> attributes =
        new LinkedHashMap<String, Attribute>();

	public AttributeDict() {}
	public AttributeDict(DictType type) { this.type = type; }

	public Attribute add(Attribute a) { a.dict = this; return attributes.put(a.name, a); }
    public Attribute get(String name) { return attributes.get(name); }

    public String getName() {
        return name;
    }

    public int size() { return attributes.size(); }

    /** Return the set of keys that collide from
     *  {@code this} and {@code other}.
     */

    public Set<String> intersection(AttributeDict other) {
        if ( other==null || other.size()==0 || size()==0 ) {
            return Collections.emptySet();
        }

		Set<String> result = new HashSet<String>(attributes.keySet());
		result.retainAll(other.attributes.keySet());
		return result;
    }

    @Override
    public String toString() {
        return getName()+":"+attributes;
    }
}

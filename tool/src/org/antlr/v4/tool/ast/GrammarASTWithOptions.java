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

package org.antlr.v4.tool.ast;

import org.antlr.runtime.Token;
import org.antlr.v4.misc.CharSupport;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class GrammarASTWithOptions extends GrammarAST {
    protected Map<String, GrammarAST> options;

	public GrammarASTWithOptions(GrammarASTWithOptions node) {
		super(node);
		this.options = node.options;
	}

	public GrammarASTWithOptions(Token t) { super(t); }
    public GrammarASTWithOptions(int type) { super(type); }
    public GrammarASTWithOptions(int type, Token t) { super(type, t); }
    public GrammarASTWithOptions(int type, Token t, String text) { super(type,t,text); }

    public void setOption(String key, GrammarAST node) {
        if ( options==null ) options = new HashMap<String, GrammarAST>();
        options.put(key, node);
    }

	public String getOptionString(String key) {
		GrammarAST value = getOptionAST(key);
		if ( value == null ) return null;
		if ( value instanceof ActionAST ) {
			return value.getText();
		}
		else {
			String v = value.getText();
			if ( v.startsWith("'") || v.startsWith("\"") ) {
				v = CharSupport.getStringFromGrammarStringLiteral(v);
			}
			return v;
		}
	}

	/** Gets AST node holding value for option key; ignores default options
	 *  and command-line forced options.
	 */
    public GrammarAST getOptionAST(String key) {
        if ( options==null ) return null;
        return options.get(key);
    }

	public int getNumberOfOptions() {
		return options==null ? 0 : options.size();
	}

	@Override
	public abstract GrammarASTWithOptions dupNode();


	public Map<String, GrammarAST> getOptions() {
		if (options == null) {
			return Collections.emptyMap();
		}

		return options;
	}
}

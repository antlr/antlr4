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

package org.antlr.v4.tool.ast;

import org.antlr.runtime.Token;

import java.util.HashMap;
import java.util.Map;

public class GrammarASTWithOptions extends GrammarAST {
    protected Map<String, String> options;

	public GrammarASTWithOptions(GrammarAST node) {
		super(node);
		this.options = ((GrammarASTWithOptions)node).options;
	}

	public GrammarASTWithOptions(Token t) { super(t); }
    public GrammarASTWithOptions(int type) { super(type); }
    public GrammarASTWithOptions(int type, Token t) { super(type, t); }
    public GrammarASTWithOptions(int type, Token t, String text) { super(type,t,text); }

    public void setOption(String key, String value) {
        if ( options==null ) options = new HashMap<String, String>();
        options.put(key, value);
    }

    public String getOption(String key) {
        if ( options==null ) return null;
        return options.get(key);
    }

	public int getNumberOfOptions() {
		return options==null ? 0 : options.size();
	}

    public Map<String, String> getOptions() { return options; }
}

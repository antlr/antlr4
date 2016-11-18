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

package org.antlr.v4.codegen.model;

import org.antlr.v4.Tool;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.ActionAST;

import java.util.HashMap;
import java.util.Map;

public abstract class OutputFile extends OutputModelObject {
	public final String fileName;
	public final String grammarFileName;
	public final String ANTLRVersion;
    public final String TokenLabelType;
    public final String InputSymbolType;

    public OutputFile(OutputModelFactory factory, String fileName) {
        super(factory);
        this.fileName = fileName;
        Grammar g = factory.getGrammar();
		grammarFileName = g.fileName;
		ANTLRVersion = Tool.VERSION;
        TokenLabelType = g.getOptionString("TokenLabelType");
        InputSymbolType = TokenLabelType;
    }

	public Map<String, Action> buildNamedActions(Grammar g) {
		Map<String, Action> namedActions = new HashMap<String, Action>();
		for (String name : g.namedActions.keySet()) {
			ActionAST ast = g.namedActions.get(name);
			namedActions.put(name, new Action(factory, ast));
		}
		return namedActions;
	}
}

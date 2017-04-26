/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.Tool;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.ActionAST;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class OutputFile extends OutputModelObject {
	public final String fileName;
	public final String grammarFileName;
	public final String ANTLRVersion;
    public final String TokenLabelType;
    public final String InputSymbolType;
	public final String antlrRuntimeImport; // from -DruntimeImport or options in grammars
	public final boolean newcb; 
	private static HashSet<String> optionMessage = new HashSet<String>();

	
    public OutputFile(OutputModelFactory factory, String fileName) {
        super(factory);
        this.fileName = fileName;
        Grammar g = factory.getGrammar();
		grammarFileName = g.fileName;
		ANTLRVersion = Tool.VERSION;
        TokenLabelType = g.getOptionString("TokenLabelType");
        InputSymbolType = TokenLabelType;
		antlrRuntimeImport = factory.getGrammar().getOptionString("runtimeImport");
		String cbVersion = factory.getGrammar().getOptionString("newCallback");
		if( cbVersion != null && !optionMessage.contains(g.fileName) && !cbVersion.equals("true") ) {
			optionMessage.add(g.fileName);
			g.tool.errMgr.grammarError(ErrorType.ILLEGAL_OPTION_VALUE_EXPECTED, g.fileName, null, "callbackVersion", cbVersion, "true");			
		}
		if( cbVersion != null && cbVersion.equals("true") ) {
			newcb = true;
		} else {
			newcb = false;
		}
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

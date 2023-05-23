/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.Tool;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.ActionAST;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

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
		grammarFileName = g.fileName.replace("\\", "/"); // Prevent a path with windows delim and u breaking Java pre-parser on comments
		ANTLRVersion = Tool.VERSION;
        TokenLabelType = g.getOptionString("TokenLabelType");
        InputSymbolType = TokenLabelType;
    }

	public Map<String, Action> buildNamedActions(Grammar g) {
		return buildNamedActions(g, null);
	}

	public Map<String, Action> buildNamedActions(Grammar g, Predicate<ActionAST> filter) {
		Map<String, Action> namedActions = new HashMap<String, Action>();
		for (String name : g.namedActions.keySet()) {
			ActionAST ast = g.namedActions.get(name);
			if(filter==null || filter.test(ast))
				namedActions.put(name, new Action(factory, ast));
		}
		return namedActions;
	}
}

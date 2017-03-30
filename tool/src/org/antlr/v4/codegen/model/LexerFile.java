/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;

import java.util.Map;

public class LexerFile extends OutputFile {
	public String genPackage; // from -package cmd-line
	public String exportMacro; // from -DexportMacro cmd-line
	public boolean genListener; // from -listener cmd-line
	public boolean genVisitor; // from -visitor cmd-line
	@ModelElement public Lexer lexer;
	@ModelElement public Map<String, Action> namedActions;

	public LexerFile(OutputModelFactory factory, String fileName) {
		super(factory, fileName);
		namedActions = buildNamedActions(factory.getGrammar());
		genPackage = factory.getGrammar().tool.genPackage;
		exportMacro = factory.getGrammar().getOptionString("exportMacro");
		genListener = factory.getGrammar().tool.gen_listener;
		genVisitor = factory.getGrammar().tool.gen_visitor;
	}
}

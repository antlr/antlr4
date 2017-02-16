/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.BaseListenerFile.ImportedGrammar;
import org.antlr.v4.codegen.model.chunk.ActionChunk;
import org.antlr.v4.codegen.model.chunk.ActionText;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Grammar.ImportParam;

/** */
public class ParserFile extends OutputFile {
	public String genPackage; // from -package cmd-line
	public String antlrRuntimeImport; // from -DruntimeImport		
	public String exportMacro; // from -DexportMacro cmd-line
	public boolean genListener; // from -listener cmd-line
	public boolean genVisitor; // from -visitor cmd-line
	@ModelElement public Parser parser;
	@ModelElement public Map<String, Action> namedActions;
	@ModelElement public ActionChunk contextSuperClass;
	public String grammarName;
	public Set<ImportedGrammar> importedGrammars = new LinkedHashSet<ImportedGrammar>(); 

	public ParserFile(OutputModelFactory factory, String fileName) {
		super(factory, fileName);
		Grammar g = factory.getGrammar();
		namedActions = buildNamedActions(factory.getGrammar());
		genPackage = g.tool.genPackage;
		antlrRuntimeImport = factory.getGrammar().getOptionString("runtimeImport");
		exportMacro = factory.getGrammar().getOptionString("exportMacro");
		// need the below members in the ST for Python, C++
		genListener = g.tool.gen_listener;
		genVisitor = g.tool.gen_visitor;
		grammarName = g.name;

		if (g.getOptionString("contextSuperClass") != null) {
			contextSuperClass = new ActionText(null, g.getOptionString("contextSuperClass"));
		}
		
		if( g.getImportParams() != null ) {
			for( String igName : factory.getGrammar().getImportParams().keySet() ) {
				ImportParam importParam = g.getImportParams().get(igName);
				ImportedGrammar importedGrammar = new ImportedGrammar(igName, importParam.prefix, importParam.packageName);
				importedGrammars.add( importedGrammar );			
			}
		}
	}
}

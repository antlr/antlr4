/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.Tool.importParam;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.ListenerFile.ImportedGrammar;
import org.antlr.v4.codegen.model.chunk.ActionChunk;
import org.antlr.v4.codegen.model.chunk.ActionText;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/** */
public class ParserFile extends OutputFile {
	public String genPackage; // from -package cmd-line
	public String antlrRuntimeImport; // from -runtimeImport cmd-line
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
		antlrRuntimeImport = factory.getGrammar().tool.antlrRuntimeImport;		
		exportMacro = factory.getGrammar().getOptionString("exportMacro");
		// need the below members in the ST for Python, C++
		genListener = g.tool.gen_listener;
		genVisitor = g.tool.gen_visitor;
		grammarName = g.name;

		if (g.getOptionString("contextSuperClass") != null) {
			contextSuperClass = new ActionText(null, g.getOptionString("contextSuperClass"));
		}
		
		for( String igName : factory.getGrammar().tool.importParamsMap.keySet() ) {
			importParam importParam = g.tool.importParamsMap.get(igName);
			ImportedGrammar importedGrammar = new ImportedGrammar(igName, importParam.prefix, importParam.packageName);
			importedGrammars.add( importedGrammar );			
		}
		
//		if ( factory.getGrammar().tool.addImports.size() > 0 ) {
//			Iterator<String> iter = factory.getGrammar().tool.addImports.iterator();
//			addImport = iter.next(); 
//			if ( iter.hasNext() ) {
//				factory.getGrammar().tool.errMgr.toolError(ErrorType.INTERNAL_ERROR, "multiple addImport not implemented");
//			}
//		}
		
	}
}

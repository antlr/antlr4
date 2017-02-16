/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Grammar.ImportParam;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.AltAST;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** A model object representing a parse tree listener file.
 *  These are the rules specific events triggered by a parse tree visitor.
 */
public class BaseListenerFile extends OutputFile {
	public String genPackage; // from -package cmd-line
	public String antlrRuntimeImport; // from -DruntimeImport		
	public String exportMacro; // from -DexportMacro cmd-line
	public String grammarName;
	public String parserName;
	
	public Set<String> importPackages = new LinkedHashSet<String>();
	public Set<ImportedGrammar> importedGrammars = new LinkedHashSet<ImportedGrammar>(); 
	
	public Set<String> listenerRuleNamesLocal = new LinkedHashSet<String>();
	public Set<String> listenerAltNamesLocal = new LinkedHashSet<String>();
	public Set<ImportedRule> listenerRuleNamesImported = new LinkedHashSet<ImportedRule>();
	public Set<ImportedRule> listenerRuleWithAltNamesImported = new LinkedHashSet<ImportedRule>();
	public Set<String> listenerAltNamesExtension = new LinkedHashSet<String>();
	public Set<ImportedRule> listenerAltAndRuleImported = new LinkedHashSet<ImportedRule>();
	public Set<String> listenerAltNamesIsExtensionLocal = new LinkedHashSet<String>();

	/**
	 * The names of all listener contexts.
	 */
	public Set<String> listenerNames = new LinkedHashSet<String>();
	
	/**
	 * For listener contexts created for a labeled outer alternative, maps from
	 * a listener context name to the name of the rule which defines the
	 * context.
	 */
	public Map<String, String> listenerLabelRuleNames = new LinkedHashMap<String, String>();

	@ModelElement public Action header;
	@ModelElement public Map<String, Action> namedActions;

	public BaseListenerFile(OutputModelFactory factory, String fileName) {
		super(factory, fileName);
		Grammar g = factory.getGrammar();
		parserName = g.getRecognizerName();
		grammarName = g.name;
		namedActions = buildNamedActions(factory.getGrammar());
		
		ActionAST ast = g.namedActions.get("header");
		if ( ast!=null ) header = new Action(factory, ast);
		genPackage = factory.getGrammar().tool.genPackage;
		antlrRuntimeImport = factory.getGrammar().getOptionString("runtimeImport");		
		exportMacro = factory.getGrammar().getOptionString("exportMacro");

		
		for (Rule r : g.rules.values()) {
			//TODO(garym) does? extended rules mean anything without importParams?
			// should we check and raise an error?
			// 06.
			if( r.isExtention ) {  
				g.tool.logMgr.log("grammar-inheritance", String.format(_06SkipMSG, r.name) );
				continue;
			}
			if ( g.getImportParams() == null ) {
				Map<String, List<Pair<Integer,AltAST>>> labels = r.getAltLabels();
				if ( labels!=null ) {
					for (Map.Entry<String, List<Pair<Integer, AltAST>>> pair : labels.entrySet()) {
						listenerNames.add(pair.getKey());
						// 00
						listenerAltNamesLocal.add(pair.getKey());
						listenerLabelRuleNames.put(pair.getKey(), r.name);
					}
				}
				else {
					listenerNames.add(r.name);
					// 01
					// only add rule context if no labels
					listenerRuleNamesLocal.add(r.name);
				}
				continue;
			} 

			Map<String, List<Pair<Integer,AltAST>>> labels = r.getAltLabels();

			
			if ( labels == null && !r.imported ){
					// 01
					g.tool.logMgr.log("grammar-inheritance", String.format(_01listenerRuleNamesLocalMSG, r.name) ); 
					listenerRuleNamesLocal.add(r.name);
					continue;
			}
			
			// Imported Grammar with parameters

			// 02
			if ( labels == null ) {
				// only add rule context if no labels
				String importedG = g.tool.RorA2IGN.get(r.name);
				ImportParam importParam = g.getImportParams().get(importedG);
				ImportedGrammar importedGrammar = new ImportedGrammar(importedG, importParam.prefix, importParam.packageName);; 
				importedGrammars.add( importedGrammar );
				g.tool.logMgr.log("grammar-inheritance", String.format(_02listenerRuleNamesImportedMSG, r.name) ); 
				listenerRuleNamesImported.add(new ImportedRule(r.name, r.prefix, importedGrammar ));
				continue;
			}
			
			// Labeled Alternatives from extension rules
			for (Map.Entry<String, List<Pair<Integer, AltAST>>> pair : labels.entrySet()) {
				List<Pair<Integer, AltAST>> v = pair.getValue();
				listenerLabelRuleNames.put(pair.getKey(), r.name);
				boolean isExtention = v.get(0).b.isExtention;
				for( Pair<Integer, AltAST> a : v ) {
					if( a.b.isExtention != isExtention ) {
						g.tool.errMgr.grammarError(ErrorType.ALT_LABEL_REDEF_BY_IMPORT, g.fileName, a.b.getToken() , r.name );
					}
				}
				if( isExtention ) {
					// 04
					g.tool.logMgr.log("grammar-inheritance", String.format(_04listenerAltNamesLocalMSG01, pair.getKey(), r.name) );
					listenerAltNamesIsExtensionLocal.add(pair.getKey());						
					continue;
				}

				// 05
				String importedGrammarByAltName = g.tool.AltOver2IGN.get(pair.getKey());
				if( importedGrammarByAltName != null ) {
					ImportParam importParam = g.getImportParams().get(importedGrammarByAltName);
					ImportedGrammar importedAltGrammar = new ImportedGrammar(importedGrammarByAltName, importParam.prefix, importParam.packageName);
					importedGrammars.add( importedAltGrammar );
					g.tool.logMgr.log("grammar-inheritance", String.format(_05MSG, pair.getKey(), r.name) );
					listenerRuleWithAltNamesImported.add(new ImportedRule(pair.getKey(), r.prefix, importedAltGrammar));
					continue;
				} 
				
				// 07
				if( r.imported ) {
					String importedG = g.tool.RorA2IGN.get(r.name);
					ImportParam importParam = g.getImportParams().get(importedG);
					ImportedGrammar importedGrammar = new ImportedGrammar(importedG, importParam.prefix, importParam.packageName);; 
					importedGrammars.add( importedGrammar );					
					g.tool.logMgr.log("grammar-inheritance", String.format(_07MSG, pair.getKey(), r.name) ); 
					listenerAltAndRuleImported.add(new ImportedRule(pair.getKey(), r.prefix, importedGrammar ));
					continue;
				}
				
				// 03
				{ // else
					// RULE with alt-names imported
					g.tool.logMgr.log("grammar-inheritance", String.format(_03MSG, pair.getKey(), r.name, r.imported) );	
					listenerAltNamesExtension.add(pair.getKey());
				}
			}
		}
	}

	static String _01listenerRuleNamesLocalMSG    = "Adding to 'listenerRuleNamesLocal'    - (not label & !imported)           \n1. RULE '%s'               ";
	static String _02listenerRuleNamesImportedMSG = "Adding to 'listenerRuleNamesImported' - (not named alts) -                \n2. RULE '%s'               ";
	static String _03MSG                          = "Adding to 'listenerAltNamesExtension'  - (NEW)                            \n3. ALT  '%s' in RULE '%s'  ";
	static String _04listenerAltNamesLocalMSG01   = "Adding to 'listenerAltNamesLocal'     - (is an extension)                 \n4. ALT  '%s' in RULE '%s'  ";
	static String _05MSG                          = "Adding to 'listenerRuleWithAltNamesImported' - (DEFAULT)                  \n5. ALT  '%s' in RULE '%s'  ";
	static String _06SkipMSG                      = "Extension rule ... skipping                                               \n6. RULE '%s'               ";
	static String _07MSG                          = "Adding to 'listenerAltAndRuleImported' - (rule & alt in super)            \n7. ALT  '%s' in RULE '%s'  ";
	
	
	public static class ImportedGrammar {
		public String grammar;
		public String packageName;
		public String prefix;
		public ImportedGrammar(String grammar, String prefix, String packageName) {
			super();
			this.grammar = grammar;
			this.packageName = packageName;
			this.prefix = prefix;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((grammar == null) ? 0 : grammar.hashCode());
			result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
			result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			ImportedGrammar other = (ImportedGrammar) obj;
			if (grammar == null) { 
				if (other.grammar != null) { return false; }
			} else if (!grammar.equals(other.grammar))
				return false;
			if (packageName == null) {
				if (other.packageName != null) { return false; }
			} else if (!packageName.equals(other.packageName))
				return false;
			if (prefix == null) {
				if (other.prefix != null) { return false; }
			} else if (!prefix.equals(other.prefix))
				return false;
			return true;
		}
	}
	
	public static class ImportedRule {
		public String name;
		public String prefix;
		public ImportedGrammar grammar;
			
		public ImportedRule(String name, String prefix, ImportedGrammar importedGrammar) {
			this.name = name;
			this.prefix = prefix;
			this.grammar = importedGrammar;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ImportedRule other = (ImportedRule) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (prefix == null) {
				if (other.prefix != null)
					return false;
			} else if (!prefix.equals(other.prefix))
				return false;
			return true;
		}		
	}

}

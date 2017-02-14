/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.codegen.model;

import org.antlr.v4.Tool.importParam;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
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
public class ListenerFile extends OutputFile {
	public String genPackage; // from -package cmd-line
	public String antlrRuntimeImport; // from -runtimeImport cmd-line
	public String exportMacro; // from -DexportMacro cmd-line
	public String grammarName;
	public String parserName;
	public boolean hasImport;
	
	/**
	 * The names of all listener contexts.
	 */
	public Set<String> listenerNames = new LinkedHashSet<String>();

	public Set<ImportedRule> listenerNamesImported = new LinkedHashSet<ImportedRule>();
	public Set<ImportedRule> listenerNamesMaybeAlt = new LinkedHashSet<ImportedRule>();
	public Set<String> importPackages = new LinkedHashSet<String>();
	public Set<String> listenerNamesLocal = new LinkedHashSet<String>();
	public Set<ImportedGrammar> importedGrammars = new LinkedHashSet<ImportedGrammar>(); 
	
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
			result = prime * result
					+ ((grammar == null) ? 0 : grammar.hashCode());
			result = prime * result
					+ ((packageName == null) ? 0 : packageName.hashCode());
			result = prime * result
					+ ((prefix == null) ? 0 : prefix.hashCode());
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
			ImportedGrammar other = (ImportedGrammar) obj;
			if (grammar == null) {
				if (other.grammar != null)
					return false;
			} else if (!grammar.equals(other.grammar))
				return false;
			if (packageName == null) {
				if (other.packageName != null)
					return false;
			} else if (!packageName.equals(other.packageName))
				return false;
			if (prefix == null) {
				if (other.prefix != null)
					return false;
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
			result = prime * result
					+ ((prefix == null) ? 0 : prefix.hashCode());
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
	
	/**
	 * For listener contexts created for a labeled outer alternative, maps from
	 * a listener context name to the name of the rule which defines the
	 * context.
	 */
	public Map<String, String> listenerLabelRuleNames = new LinkedHashMap<String, String>();

	@ModelElement public Action header;
	@ModelElement public Map<String, Action> namedActions;

	public ListenerFile(OutputModelFactory factory, String fileName) {
		super(factory, fileName);
		Grammar g = factory.getGrammar();
		parserName = g.getRecognizerName();
		grammarName = g.name;
		namedActions = buildNamedActions(factory.getGrammar());		
		for (Rule r : g.rules.values()) {
			if( r.isExtention ) {
				continue;
			}
			ImportedGrammar importedGrammar = null; 
			String importedG = g.tool.importRules_Alts.get(r.name);
			if ( importedG != null ) {
				importParam importParam = g.tool.importParamsMap.get(importedG);
				r.prefix = importParam.prefix;
				importedGrammar = new ImportedGrammar(importedG, importParam.prefix, importParam.packageName);
				importedGrammars.add( importedGrammar );
				r.imported = true;
			}
			
			Map<String, List<Pair<Integer,AltAST>>> labels = r.getAltLabels();
			if ( labels!=null ) {
				for (Map.Entry<String, List<Pair<Integer, AltAST>>> pair : labels.entrySet()) {					
					List<Pair<Integer, AltAST>> v = pair.getValue();
					listenerNames.add(pair.getKey());
					listenerLabelRuleNames.put(pair.getKey(), r.name);
					String maybeAltGrammar = g.tool.importMaybeAlts.get(pair.getKey());
					if( maybeAltGrammar != null ) {
						importParam importParam = g.tool.importParamsMap.get(maybeAltGrammar);
						ImportedGrammar importedAltGrammar = new ImportedGrammar(maybeAltGrammar, importParam.prefix, importParam.packageName);
						listenerNamesMaybeAlt.add(new ImportedRule(pair.getKey(), r.prefix, importedAltGrammar));
						hasImport = true;
					} else {
						if( !r.imported ){
							listenerNamesLocal.add(pair.getKey());
						} else {
							boolean isExtention = v.get(0).b.isExtention;
							for( Pair<Integer, AltAST> a : v ) {
								if( a.b.isExtention != isExtention ) {
									g.tool.errMgr.grammarError(ErrorType.ALT_LABEL_REDEF_BY_IMPORT, g.fileName, a.b.getToken() , r.name );
								}
							}
							if( !isExtention ) {
								listenerNamesImported.add(new ImportedRule(pair.getKey(), r.prefix, importedGrammar));								 
							} else {
								listenerNamesLocal.add(pair.getKey());								
							}
							hasImport = true;
						}
					}
				}
			}
			else {
				// only add rule context if no labels
				listenerNames.add(r.name);
				if( !r.imported ){
					listenerNamesLocal.add(r.name);
				} else {
					listenerNamesImported.add(new ImportedRule(r.name, r.prefix, importedGrammar ));
					hasImport = true;
				}
			}
		}
		ActionAST ast = g.namedActions.get("header");
		if ( ast!=null ) header = new Action(factory, ast);
		genPackage = factory.getGrammar().tool.genPackage;
		antlrRuntimeImport = factory.getGrammar().tool.antlrRuntimeImport;
		
		exportMacro = factory.getGrammar().getOptionString("exportMacro");
	}
}

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

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.misc.Pair;
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
	public String grammarName;
	public String parserName;
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

	public ListenerFile(OutputModelFactory factory, String fileName) {
		super(factory, fileName);
		Grammar g = factory.getGrammar();
		parserName = g.getRecognizerName();
		grammarName = g.name;
		for (Rule r : g.rules.values()) {
			Map<String, List<Pair<Integer,AltAST>>> labels = r.getAltLabels();
			if ( labels!=null ) {
				for (Map.Entry<String, List<Pair<Integer, AltAST>>> pair : labels.entrySet()) {
					listenerNames.add(pair.getKey());
					listenerLabelRuleNames.put(pair.getKey(), r.name);
				}
			}
			else {
				// only add rule context if no labels
				listenerNames.add(r.name);
			}
		}
		ActionAST ast = g.namedActions.get("header");
		if ( ast!=null ) header = new Action(factory, ast);
		genPackage = factory.getGrammar().tool.genPackage;
	}
}

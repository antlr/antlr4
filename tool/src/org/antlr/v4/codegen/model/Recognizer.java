/*
 * [The "BSD license"]
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Sam Harwell
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

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.chunk.ActionChunk;
import org.antlr.v4.codegen.model.chunk.ActionText;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class Recognizer extends OutputModelObject {
	public String name;
	public String grammarName;
	public String grammarFileName;
	public Map<String,Integer> tokens;
	public String[] literalNames;
	public String[] symbolicNames;
	public Set<String> ruleNames;
	public Collection<Rule> rules;
	@ModelElement public ActionChunk superClass;

	@ModelElement public SerializedATN atn;
	@ModelElement public LinkedHashMap<Rule, RuleSempredFunction> sempredFuncs =
		new LinkedHashMap<Rule, RuleSempredFunction>();

	public Recognizer(OutputModelFactory factory) {
		super(factory);

		Grammar g = factory.getGrammar();
		grammarFileName = new File(g.fileName).getName();
		grammarName = g.name;
		name = g.getRecognizerName();
		tokens = new LinkedHashMap<String,Integer>();
		for (Map.Entry<String, Integer> entry : g.tokenNameToTypeMap.entrySet()) {
			Integer ttype = entry.getValue();
			if ( ttype>0 ) {
				tokens.put(entry.getKey(), ttype);
			}
		}

		ruleNames = g.rules.keySet();
		rules = g.rules.values();
		atn = new SerializedATN(factory, g.atn);
		if (g.getOptionString("superClass") != null) {
			superClass = new ActionText(null, g.getOptionString("superClass"));
		}
		else {
			superClass = null;
		}

		CodeGenerator gen = factory.getGenerator();
		literalNames = translateTokenStringsToTarget(g.getTokenLiteralNames(), gen);
		symbolicNames = translateTokenStringsToTarget(g.getTokenSymbolicNames(), gen);
	}

	protected static String[] translateTokenStringsToTarget(String[] tokenStrings, CodeGenerator gen) {
		String[] result = tokenStrings.clone();
		for (int i = 0; i < tokenStrings.length; i++) {
			result[i] = translateTokenStringToTarget(tokenStrings[i], gen);
		}

		int lastTrueEntry = result.length - 1;
		while (lastTrueEntry >= 0 && result[lastTrueEntry] == null) {
			lastTrueEntry --;
		}

		if (lastTrueEntry < result.length - 1) {
			result = Arrays.copyOf(result, lastTrueEntry + 1);
		}

		return result;
	}

	protected static String translateTokenStringToTarget(String tokenName, CodeGenerator gen) {
		if (tokenName == null) {
			return null;
		}

		if (tokenName.charAt(0) == '\'') {
			boolean addQuotes = false;
			String targetString =
				gen.getTarget().getTargetStringLiteralFromANTLRStringLiteral(gen, tokenName, addQuotes);
			return "\"'" + targetString + "'\"";
		}
		else {
			return gen.getTarget().getTargetStringLiteralFromString(tokenName, true);
		}
	}

}

/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.codegen.model.chunk.ActionChunk;
import org.antlr.v4.codegen.model.chunk.ActionText;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;

import java.io.File;
import java.util.*;

public abstract class Recognizer extends OutputModelObject {
	public final String name;
	public final String grammarName;
	public final String grammarFileName;
	public final String accessLevel;
	public final Map<String,Integer> tokens;

	/**
	 * @deprecated This field is provided only for compatibility with code
	 * generation targets which have not yet been updated to use
	 * {@link #literalNames} and {@link #symbolicNames}.
	 */
	@Deprecated
	public List<String> tokenNames;

	public final List<String> literalNames;
	public final List<String> symbolicNames;
	public final List<String> ruleNames;
	public final Collection<Rule> rules;
	@ModelElement public final ActionChunk superClass;

	@ModelElement public final SerializedATN atn;
	@ModelElement public final LinkedHashMap<Rule, RuleSempredFunction> sempredFuncs =
		new LinkedHashMap<Rule, RuleSempredFunction>();

	public Recognizer(OutputModelFactory factory) {
		super(factory);

		Grammar g = factory.getGrammar();
		grammarFileName = new File(g.fileName).getName();
		grammarName = g.name;
		name = g.getRecognizerName();
		accessLevel = g.getOptionString("accessLevel");
		tokens = new LinkedHashMap<String,Integer>();
		for (Map.Entry<String, Integer> entry : g.tokenNameToTypeMap.entrySet()) {
			Integer ttype = entry.getValue();
			if ( ttype>0 ) {
				tokens.put(entry.getKey(), ttype);
			}
		}

		Target target = factory.getGenerator().getTarget();
		ruleNames = new ArrayList<>(g.rules.size());
		for (String ruleKey : g.rules.keySet()) {
			ruleNames.add(target.escapeIfWordEscapingNotSupported(ruleKey));
		}
		rules = g.rules.values();
		atn = new SerializedATN(factory, g.atn);
		if (g.getOptionString("superClass") != null) {
			superClass = new ActionText(null, g.getOptionString("superClass"));
		}
		else {
			superClass = null;
		}

		CodeGenerator gen = factory.getGenerator();
		tokenNames = translateTokenStringsToTarget(g.getTokenDisplayNames(), gen);
		literalNames = translateTokenStringsToTarget(g.getTokenLiteralNames(), gen);
		symbolicNames = translateTokenStringsToTarget(g.getTokenSymbolicNames(), gen);
	}

	protected static List<String> translateTokenStringsToTarget(String[] tokenStrings, CodeGenerator gen) {
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

		return Arrays.asList(result);
	}

	protected static String translateTokenStringToTarget(String tokenName, CodeGenerator gen) {
		if (tokenName == null) {
			return null;
		}

		if (tokenName.charAt(0) == '\'') {
			String targetString =
				gen.getTarget().getTargetStringLiteralFromANTLRStringLiteral(gen, tokenName, false, true);
			return "\"'" + targetString + "'\"";
		}
		else {
			return gen.getTarget().getTargetStringLiteralFromString(tokenName, true);
		}
	}

}

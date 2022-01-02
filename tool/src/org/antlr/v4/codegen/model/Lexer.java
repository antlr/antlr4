/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;

import java.util.*;

public class Lexer extends Recognizer {
	public final Map<String, Integer> channels;
	public final Collection<String> channelNames;
	public final LexerFile file;
	public final Collection<String> modes;
	public final Collection<String> runtimeModeNames;

	@ModelElement public LinkedHashMap<Rule, RuleActionFunction> actionFuncs =
		new LinkedHashMap<Rule, RuleActionFunction>();

	public Lexer(OutputModelFactory factory, LexerFile file) {
		super(factory);
		this.file = file; // who contains us?

		Grammar g = factory.getGrammar();
		Target target = factory.getGenerator().getTarget();

		channels = new LinkedHashMap<>();
		channelNames = new ArrayList<>();
		for (String key : g.channelNameToValueMap.keySet()) {
			Integer value = g.channelNameToValueMap.get(key);
			String escaped = target.escapeIfNeeded(key);
			channels.put(escaped, value);
			channelNames.add(target.supportsWordEscaping() ? key : escaped);
		}

		MultiMap<String, Rule> grammarModes = ((LexerGrammar) g).modes;
		modes = new ArrayList<>(grammarModes.size());
		runtimeModeNames = new ArrayList<>(grammarModes.size());
		for (String mode : grammarModes.keySet()) {
			String escaped = target.escapeIfNeeded(mode);
			modes.add(target.supportsWordEscaping() ? mode : escaped);
			runtimeModeNames.add(escaped);
		}
	}
}

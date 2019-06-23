/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.v4.misc.FrequencySet;
import org.antlr.v4.misc.MutableInt;
import org.antlr.v4.parse.GrammarTreeVisitor;
import org.antlr.v4.tool.ErrorManager;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.TerminalAST;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public class ElementFrequenciesVisitor extends GrammarTreeVisitor {
	/**
	 * This special value means "no set", and is used by {@link #minFrequencies}
	 * to ensure that {@link #combineMin} doesn't merge an empty set (all zeros)
	 * with the results of the first alternative.
	 */
	private static final FrequencySet<String> SENTINEL = new FrequencySet<String>();

	final Deque<FrequencySet<String>> frequencies;
	private final Deque<FrequencySet<String>> minFrequencies;

	public ElementFrequenciesVisitor(TreeNodeStream input) {
		super(input);
		frequencies = new ArrayDeque<FrequencySet<String>>();
		frequencies.push(new FrequencySet<String>());
		minFrequencies = new ArrayDeque<FrequencySet<String>>();
		minFrequencies.push(SENTINEL);
	}

	FrequencySet<String> getMinFrequencies() {
		assert minFrequencies.size() == 1;
		assert minFrequencies.peek() != SENTINEL;
		assert SENTINEL.isEmpty();

		return minFrequencies.peek();
	}

	/** During code gen, we can assume tree is in good shape */
	@Override
	public ErrorManager getErrorManager() { return super.getErrorManager(); }

	/*
	 * Common
	 */

	/**
	 * Generate a frequency set as the union of two input sets. If an
	 * element is contained in both sets, the value for the output will be
	 * the maximum of the two input values.
	 *
	 * @param a The first set.
	 * @param b The second set.
	 * @return The union of the two sets, with the maximum value chosen
	 * whenever both sets contain the same key.
	 */
	protected static FrequencySet<String> combineMax(FrequencySet<String> a, FrequencySet<String> b) {
		FrequencySet<String> result = combineAndClip(a, b, 1);
		for (Map.Entry<String, MutableInt> entry : a.entrySet()) {
			result.get(entry.getKey()).v = entry.getValue().v;
		}

		for (Map.Entry<String, MutableInt> entry : b.entrySet()) {
			MutableInt slot = result.get(entry.getKey());
			slot.v = Math.max(slot.v, entry.getValue().v);
		}

		return result;
	}

	/**
	 * Generate a frequency set as the union of two input sets. If an
	 * element is contained in both sets, the value for the output will be
	 * the minimum of the two input values.
	 *
	 * @param a The first set.
	 * @param b The second set. If this set is {@link #SENTINEL}, it is treated
	 * as though no second set were provided.
	 * @return The union of the two sets, with the minimum value chosen
	 * whenever both sets contain the same key.
	 */
	protected static FrequencySet<String> combineMin(FrequencySet<String> a, FrequencySet<String> b) {
		if (b == SENTINEL) {
			return a;
		}

		assert a != SENTINEL;
		FrequencySet<String> result = combineAndClip(a, b, Integer.MAX_VALUE);
		for (Map.Entry<String, MutableInt> entry : result.entrySet()) {
			entry.getValue().v = Math.min(a.count(entry.getKey()), b.count(entry.getKey()));
		}

		return result;
	}

	/**
	 * Generate a frequency set as the union of two input sets, with the
	 * values clipped to a specified maximum value. If an element is
	 * contained in both sets, the value for the output, prior to clipping,
	 * will be the sum of the two input values.
	 *
	 * @param a The first set.
	 * @param b The second set.
	 * @param clip The maximum value to allow for any output.
	 * @return The sum of the two sets, with the individual elements clipped
	 * to the maximum value given by {@code clip}.
	 */
	protected static FrequencySet<String> combineAndClip(FrequencySet<String> a, FrequencySet<String> b, int clip) {
		FrequencySet<String> result = new FrequencySet<String>();
		for (Map.Entry<String, MutableInt> entry : a.entrySet()) {
			for (int i = 0; i < entry.getValue().v; i++) {
				result.add(entry.getKey());
			}
		}

		for (Map.Entry<String, MutableInt> entry : b.entrySet()) {
			for (int i = 0; i < entry.getValue().v; i++) {
				result.add(entry.getKey());
			}
		}

		for (Map.Entry<String, MutableInt> entry : result.entrySet()) {
			entry.getValue().v = Math.min(entry.getValue().v, clip);
		}

		return result;
	}

	@Override
	public void tokenRef(TerminalAST ref) {
		frequencies.peek().add(ref.getText());
		minFrequencies.peek().add(ref.getText());
	}

	@Override
	public void ruleRef(GrammarAST ref, ActionAST arg) {
		frequencies.peek().add(ref.getText());
		minFrequencies.peek().add(ref.getText());
	}

	@Override
	public void stringRef(TerminalAST ref) {
		String tokenName = ref.g.getTokenName(ref.getText());

		if (tokenName != null && !tokenName.startsWith("T__")) {
			frequencies.peek().add(tokenName);
			minFrequencies.peek().add(tokenName);
		}
	}

	/*
	 * Parser rules
	 */

	@Override
	protected void enterAlternative(AltAST tree) {
		frequencies.push(new FrequencySet<String>());
		minFrequencies.push(new FrequencySet<String>());
	}

	@Override
	protected void exitAlternative(AltAST tree) {
		frequencies.push(combineMax(frequencies.pop(), frequencies.pop()));
		minFrequencies.push(combineMin(minFrequencies.pop(), minFrequencies.pop()));
	}

	@Override
	protected void enterElement(GrammarAST tree) {
		frequencies.push(new FrequencySet<String>());
		minFrequencies.push(new FrequencySet<String>());
	}

	@Override
	protected void exitElement(GrammarAST tree) {
		frequencies.push(combineAndClip(frequencies.pop(), frequencies.pop(), 2));
		minFrequencies.push(combineAndClip(minFrequencies.pop(), minFrequencies.pop(), 2));
	}

	@Override
	protected void enterBlockSet(GrammarAST tree) {
		frequencies.push(new FrequencySet<String>());
		minFrequencies.push(new FrequencySet<String>());
	}

	@Override
	protected void exitBlockSet(GrammarAST tree) {
		for (Map.Entry<String, MutableInt> entry : frequencies.peek().entrySet()) {
			// This visitor counts a block set as a sequence of elements, not a
			// sequence of alternatives of elements. Reset the count back to 1
			// for all items when leaving the set to ensure duplicate entries in
			// the set are treated as a maximum of one item.
			entry.getValue().v = 1;
		}

		if (minFrequencies.peek().size() > 1) {
			// Everything is optional
			minFrequencies.peek().clear();
		}

		frequencies.push(combineAndClip(frequencies.pop(), frequencies.pop(), 2));
		minFrequencies.push(combineAndClip(minFrequencies.pop(), minFrequencies.pop(), 2));
	}

	@Override
	protected void exitSubrule(GrammarAST tree) {
		if (tree.getType() == CLOSURE || tree.getType() == POSITIVE_CLOSURE) {
			for (Map.Entry<String, MutableInt> entry : frequencies.peek().entrySet()) {
				entry.getValue().v = 2;
			}
		}

		if (tree.getType() == CLOSURE || tree.getType() == OPTIONAL) {
			// Everything inside a closure is optional, so the minimum
			// number of occurrences for all elements is 0.
			minFrequencies.peek().clear();
		}
	}

	/*
	 * Lexer rules
	 */

	@Override
	protected void enterLexerAlternative(GrammarAST tree) {
		frequencies.push(new FrequencySet<String>());
		minFrequencies.push(new FrequencySet<String>());
	}

	@Override
	protected void exitLexerAlternative(GrammarAST tree) {
		frequencies.push(combineMax(frequencies.pop(), frequencies.pop()));
		minFrequencies.push(combineMin(minFrequencies.pop(), minFrequencies.pop()));
	}

	@Override
	protected void enterLexerElement(GrammarAST tree) {
		frequencies.push(new FrequencySet<String>());
		minFrequencies.push(new FrequencySet<String>());
	}

	@Override
	protected void exitLexerElement(GrammarAST tree) {
		frequencies.push(combineAndClip(frequencies.pop(), frequencies.pop(), 2));
		minFrequencies.push(combineAndClip(minFrequencies.pop(), minFrequencies.pop(), 2));
	}

	@Override
	protected void exitLexerSubrule(GrammarAST tree) {
		if (tree.getType() == CLOSURE || tree.getType() == POSITIVE_CLOSURE) {
			for (Map.Entry<String, MutableInt> entry : frequencies.peek().entrySet()) {
				entry.getValue().v = 2;
			}
		}

		if (tree.getType() == CLOSURE) {
			// Everything inside a closure is optional, so the minimum
			// number of occurrences for all elements is 0.
			minFrequencies.peek().clear();
		}
	}
}

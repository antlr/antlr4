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
	final Deque<FrequencySet<String>> frequencies;

	public ElementFrequenciesVisitor(TreeNodeStream input) {
		super(input);
		frequencies = new ArrayDeque<FrequencySet<String>>();
		frequencies.push(new FrequencySet<String>());
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
	 * Generate a frequency set as the union of two input sets, with the
	 * values clipped to a specified maximum value. If an element is
	 * contained in both sets, the value for the output, prior to clipping,
	 * will be the sum of the two input values.
	 *
	 * @param a The first set.
	 * @param b The second set.
	 * @param clip The maximum value to allow for any output.
	 * @return The sum of the two sets, with the individual elements clipped
	 * to the maximum value gived by {@code clip}.
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
	}

	@Override
	public void ruleRef(GrammarAST ref, ActionAST arg) {
		frequencies.peek().add(ref.getText());
	}

	/*
	 * Parser rules
	 */

	@Override
	protected void enterAlternative(AltAST tree) {
		frequencies.push(new FrequencySet<String>());
	}

	@Override
	protected void exitAlternative(AltAST tree) {
		frequencies.push(combineMax(frequencies.pop(), frequencies.pop()));
	}

	@Override
	protected void enterElement(GrammarAST tree) {
		frequencies.push(new FrequencySet<String>());
	}

	@Override
	protected void exitElement(GrammarAST tree) {
		frequencies.push(combineAndClip(frequencies.pop(), frequencies.pop(), 2));
	}

	@Override
	protected void exitSubrule(GrammarAST tree) {
		if (tree.getType() == CLOSURE || tree.getType() == POSITIVE_CLOSURE) {
			for (Map.Entry<String, MutableInt> entry : frequencies.peek().entrySet()) {
				entry.getValue().v = 2;
			}
		}
	}

	/*
	 * Lexer rules
	 */

	@Override
	protected void enterLexerAlternative(GrammarAST tree) {
		frequencies.push(new FrequencySet<String>());
	}

	@Override
	protected void exitLexerAlternative(GrammarAST tree) {
		frequencies.push(combineMax(frequencies.pop(), frequencies.pop()));
	}

	@Override
	protected void enterLexerElement(GrammarAST tree) {
		frequencies.push(new FrequencySet<String>());
	}

	@Override
	protected void exitLexerElement(GrammarAST tree) {
		frequencies.push(combineAndClip(frequencies.pop(), frequencies.pop(), 2));
	}

	@Override
	protected void exitLexerSubrule(GrammarAST tree) {
		if (tree.getType() == CLOSURE || tree.getType() == POSITIVE_CLOSURE) {
			for (Map.Entry<String, MutableInt> entry : frequencies.peek().entrySet()) {
				entry.getValue().v = 2;
			}
		}
	}
}

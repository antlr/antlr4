package org.antlr.v4.codegen.model;

import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.v4.misc.FrequencyRange;
import org.antlr.v4.misc.FrequencySet;
import org.antlr.v4.parse.GrammarTreeVisitor;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.TerminalAST;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public class ElementFrequenciesVisitor extends GrammarTreeVisitor {
	final Deque<FrequencySet<String>> frequencies = new ArrayDeque<FrequencySet<String>>();

	public ElementFrequenciesVisitor(TreeNodeStream input) {
		super(input);
	}

	/*
	 * Common
	 */

	private void newFrequencySet() {
		frequencies.push(new FrequencySet<String>());
	}

	private void combineUnion() {
		// The condition below is always true except for the very last call to this method.
		if (frequencies.size() >= 2) {
			FrequencySet<String> popped = frequencies.pop();
			frequencies.peek().union(popped);
		}
	}

	private void combineSum() {
		FrequencySet<String> popped = frequencies.pop();
		frequencies.peek().addAll(popped);
	}

	private void unionFrequencyRangesWith(FrequencyRange range) {
		final FrequencySet<String> freqSet = frequencies.peek();
		for (Map.Entry<String, FrequencyRange> entry : freqSet.entrySet()) {
			final String key = entry.getKey();
			freqSet.put(key, entry.getValue().union(range));
		}
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
		newFrequencySet();
	}

	@Override
	protected void exitAlternative(AltAST tree) {
		combineUnion();
	}

	@Override
	protected void enterElement(GrammarAST tree) {
		newFrequencySet();
	}

	@Override
	protected void exitElement(GrammarAST tree) {
		combineSum();
	}

	@Override
	protected void enterBlockSet(GrammarAST tree) {
		newFrequencySet();
	}

	@Override
	protected void exitBlockSet(GrammarAST tree) {
		combineUnion();
	}

	@Override
	protected void exitSubrule(GrammarAST tree) {
		if (tree.getType() == CLOSURE || tree.getType() == POSITIVE_CLOSURE) {
			unionFrequencyRangesWith(FrequencyRange.MANY);
		}
		if (tree.getType() == CLOSURE || tree.getType() == OPTIONAL) {
			unionFrequencyRangesWith(FrequencyRange.NONE);
		}
	}

	/*
	 * Lexer rules
	 */

	@Override
	protected void enterLexerAlternative(GrammarAST tree) {
		newFrequencySet();
	}

	@Override
	protected void exitLexerAlternative(GrammarAST tree) {
		combineUnion();
	}

	@Override
	protected void enterLexerElement(GrammarAST tree) {
		newFrequencySet();
	}

	@Override
	protected void exitLexerElement(GrammarAST tree) {
		combineSum();
	}

	@Override
	protected void exitLexerSubrule(GrammarAST tree) {
		exitSubrule(tree);
	}
}

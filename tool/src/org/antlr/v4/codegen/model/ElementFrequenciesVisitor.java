package org.antlr.v4.codegen.model;

import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.v4.misc.Frequency;
import org.antlr.v4.misc.FrequencyRange;
import org.antlr.v4.misc.FrequencySet;
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
	final Deque<FrequencySet<String>> frequencies = new ArrayDeque<FrequencySet<String>>();

	public ElementFrequenciesVisitor(TreeNodeStream input) {
		super(input);
	}

	/** During code gen, we can assume tree is in good shape */
	@Override
	public ErrorManager getErrorManager() { return super.getErrorManager(); }

	/*
	 * Common
	 */

	protected void combineUnion() {
		// The condition below is always true except for the very last call to this method.
		if (frequencies.size() >= 2) {
			FrequencySet<String> popped = frequencies.pop();
			frequencies.peek().union(popped);
		}
	}

	protected void combineSum() {
		FrequencySet<String> popped = frequencies.pop();
		frequencies.peek().addAll(popped);
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
		combineUnion();
	}

	@Override
	protected void enterElement(GrammarAST tree) {
		frequencies.push(new FrequencySet<String>());
	}

	@Override
	protected void exitElement(GrammarAST tree) {
		combineSum();
	}

	@Override
	protected void enterBlockSet(GrammarAST tree) {
		frequencies.push(new FrequencySet<String>());
	}

	@Override
	protected void exitBlockSet(GrammarAST tree) {
		combineUnion();
	}

	@Override
	protected void exitSubrule(GrammarAST tree) {
		if (tree.getType() == CLOSURE || tree.getType() == POSITIVE_CLOSURE) {
			for (Map.Entry<String, FrequencyRange> entry : frequencies.peek().entrySet()) {
				entry.getValue().max = Frequency.MANY;
			}
		}
		if (tree.getType() == CLOSURE || tree.getType() == OPTIONAL) {
			for (Map.Entry<String, FrequencyRange> entry : frequencies.peek().entrySet()) {
				entry.getValue().min = Frequency.NONE;
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
		combineUnion();
	}

	@Override
	protected void enterLexerElement(GrammarAST tree) {
		frequencies.push(new FrequencySet<String>());
	}

	@Override
	protected void exitLexerElement(GrammarAST tree) {
		combineSum();
	}

	@Override
	protected void exitLexerSubrule(GrammarAST tree) {
		if (tree.getType() == CLOSURE || tree.getType() == POSITIVE_CLOSURE) {
			for (Map.Entry<String, FrequencyRange> entry : frequencies.peek().entrySet()) {
				entry.getValue().max = Frequency.MANY;
			}
		}
		if (tree.getType() == CLOSURE || tree.getType() == OPTIONAL) {
			for (Map.Entry<String, FrequencyRange> entry : frequencies.peek().entrySet()) {
				entry.getValue().min = Frequency.NONE;
			}
		}
	}
}

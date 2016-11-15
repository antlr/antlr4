package org.antlr.v4.test.runtime;

import org.antlr.v4.runtime.misc.Pair;

import java.util.List;

public interface RuntimeTestDescriptor {
	String getTestName();
	String getTestType();
	String getInput();
	String getOutput();
	String getErrors();
	String getANTLRToolErrors();
	String getStartRule(); // TODO: alter tests to use same default start rule?
	String getAfterGrammar(); // TODO: only a few use. make part of testrig or rename?
	boolean showDFA();
	boolean showDiagnosticErrors();

	Pair<String,String> getGrammar(); // Associates name of grammar like M in M.g4 to string (template) of grammar
	List<Pair<String,String>> getSlaveGrammars();

	String getTarget();
	void setTarget(String targetName);

	boolean ignore(String targetName);
}

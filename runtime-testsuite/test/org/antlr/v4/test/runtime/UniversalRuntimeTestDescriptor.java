package org.antlr.v4.test.runtime;

import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;

/** This object represents all the information we need about a single test and is the
 * in-memory representation of a descriptor file
 */
public class UniversalRuntimeTestDescriptor implements RuntimeTestDescriptor {
	public String testType;
	public String targetName;
	public String name;
	public String notes;
	public String input = "";
	public String output;
	public String errors;
	public String startRule;
	public String grammarName;
	public String grammar;
	public List<Pair<String, String>> slaveGrammars = new ArrayList<>();
	public boolean showDFA = false;
	public boolean showDiagnosticErrors = false;

	public List<String> skipTargets = new ArrayList<>();

	@Override
	public String getTestName() {
		return name;
	}

	@Override
	public String getTestType() {
		return testType;
	}

	@Override
	public String getInput() {
		return input;
	}

	@Override
	public String getOutput() {
		return output;
	}

	@Override
	public String getErrors() {
		return errors;
	}

	@Override
	public String getANTLRToolErrors() {
		return null;
	}

	@Override
	public String getStartRule() {
		return startRule;
	}

	@Override
	public List<Pair<String, String>> getSlaveGrammars() {
		if ( slaveGrammars.size()==0 ) return null;
		return slaveGrammars;
	}

	@Override
	public String getTarget() {
		return targetName;
	}

	@Override
	public void setTarget(String targetName) {
		this.targetName = targetName;
	}

	@Override
	public boolean showDFA() {
		return showDFA;
	}

	@Override
	public boolean showDiagnosticErrors() {
		return showDiagnosticErrors;
	}

	@Override
	public Pair<String, String> getGrammar() {
		return new Pair<>(grammarName,grammar);
	}

	@Override
	public boolean ignore(String targetName) {
		return skipTargets.contains(targetName);
	}


	@Override
	public String toString() {
		return getTarget()+":"+getTestName();
	}
}

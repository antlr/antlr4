package org.antlr.v4.test.runtime;

import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;

public class UniversalRuntimeTestDescriptor extends BaseRuntimeTestDescriptor {
	public String name;
	public String notes;
	public String input;
	public String output;
	public String errors;
	public String startRule;
	public String grammarName;
	public String grammar;
	public List<Pair<String, String>> slaveGrammars = new ArrayList<>();

	public String testGroup;
	public String testType;
	public boolean showDFA = false;
	public boolean showDiagnosticErrors = false;

	public List<String> skipTargets = new ArrayList<>();

	@Override
	public String getTestName() {
		return name;
	}

	public String getTestGroup() {
		return testGroup;
	}

	@Override
	public String getTestType() {
		return testType;
	}

	@Override
	public List<Pair<String, String>> getSlaveGrammars() {
		if ( slaveGrammars.size()==0 ) return null;
		return slaveGrammars;
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
	public boolean ignore(String targetName) {
		return skipTargets.contains(targetName);
	}
}

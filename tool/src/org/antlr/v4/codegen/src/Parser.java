package org.antlr.v4.codegen.src;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.misc.IntSet;
import org.antlr.v4.tool.Rule;

import java.util.ArrayList;
import java.util.List;

/** */
public class Parser extends OutputModelObject {
	public String name;
	public List<RuleFunction> funcs = new ArrayList<RuleFunction>();
	public List<DFADef> dfaDefs = new ArrayList<DFADef>();
	public List<IntSet> bitsetDefs;

	public Parser(CodeGenerator gen) {
		this.gen = gen;
		name = gen.g.getRecognizerName();
		for (Rule r : gen.g.rules.values()) funcs.add( new RuleFunction(gen, r) );

		// build DFA, bitset defs
		for (DFA dfa : gen.g.decisionDFAs.values()) {
			dfaDefs.add( new DFADef("DFA"+dfa.decision, dfa) );
		}
	}

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>() {{ add("funcs"); add("dfaDefs"); }};
	}
}

package org.antlr.v4.codegen.src;

import org.antlr.v4.automata.DFA;

/** */
public class DFADef extends OutputModelObject {
	public String name;
	public DFA dfa;

	public DFADef(String name, DFA dfa) {
		this.dfa = dfa;
		this.name = name;
	}
}

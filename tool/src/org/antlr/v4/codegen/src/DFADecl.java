package org.antlr.v4.codegen.src;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.codegen.OutputModelFactory;

/** */
public class DFADecl extends Decl {
	public DFA dfa;

	public DFADecl(OutputModelFactory factory, String name, DFA dfa) {
		super(factory, name);
		this.dfa = dfa;
	}
}

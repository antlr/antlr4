package org.antlr.v4.codegen.src;

import org.antlr.v4.automata.DFA;

import java.util.List;

/** */
public class Choice extends SrcOp {
	//public DFADef dfaDef; ???	
	public DFA dfa;
	public List<CodeBlock> alts;
}

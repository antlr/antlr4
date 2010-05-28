package org.antlr.v4.codegen;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.DFAState;
import org.antlr.v4.automata.Edge;
import org.antlr.v4.codegen.pda.*;
import org.antlr.v4.runtime.pda.Bytecode;

/** */
public class DFACompiler {
	public DFA dfa;
	boolean[] marked;
	int[] stateToAddr;
	PDABytecodeGenerator gen;

	public DFACompiler(DFA dfa) {
		this.dfa = dfa;
		gen = new PDABytecodeGenerator(dfa.g.getMaxTokenType());
	}
	
	public CompiledPDA compile() {
		walk();
		gen.compile();
		System.out.println("DFA: ");
		System.out.println(Bytecode.disassemble(gen.obj.code,false));		
		return gen.obj;
	}

	void walk() {
		marked = new boolean[dfa.stateSet.size()+1];
		stateToAddr = new int[dfa.stateSet.size()+1];
		walk(dfa.startState);

		// walk code, update jump targets.
		for (Instr I : gen.obj.instrs) {
			if ( I instanceof JumpInstr) {
				JumpInstr J = (JumpInstr)I;
				J.target = stateToAddr[J.target];
			}
		}
	}

	// recursive so we follow chains in DFA, leading to fewer
	// jmp instructions.
	// start by assuming state num is bytecode addr then translate after
	// in one pass
	void walk(DFAState d) {
		if ( marked[d.stateNumber] ) return;
		marked[d.stateNumber] = true;
		stateToAddr[d.stateNumber] = gen.ip;
		System.out.println("visit "+d.stateNumber+" @"+ gen.ip);
		if ( d.isAcceptState ) {
			AcceptInstr A = new AcceptInstr(d.predictsAlt);
			gen.emit(A);
			return;
		}
		SplitInstr S = null;
		if ( d.edges.size()>1 ) {
			S = new SplitInstr(d.edges.size());
			gen.emit(S);
		}
		for (Edge e : d.edges) {
			if ( S!=null ) S.addrs.add(gen.ip);
			if ( e.label.getMinElement() == e.label.getMaxElement() ) {
				MatchInstr M = new MatchInstr(e.label.getSingleElement());
				gen.emit(M);
			}
			else {
				gen.emit(new SetInstr(e.label));
			}
			JumpInstr J = new JumpInstr(e.target.stateNumber);
			gen.emit(J);
			walk(e.target);
		}
	}
}

/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.dfa.DFAState;

/** This class encapsulates the functionality associated with tracing ATN simulation during parsing.
 *  The goal is to create a machine readable traits from simulation so that we can compare across target.
 *  Starting out by tracking DFA state construction.
 *
 * @since 4.10.2
 */
public class TracingParserATNSimulator extends ParserATNSimulator {
	public TracingParserATNSimulator(Parser parser) {
		super(parser,
			parser.getInterpreter().atn,
			parser.getInterpreter().decisionToDFA,
			parser.getInterpreter().sharedContextCache);
	}

	/** Invoked after we have added state D to dfa (confirming that it does not already exist in dfa).
	 *  Active if boolean trace is true.
	 *
	 * @since 4.10.2
	 */
	public void trace_addDFAState(DFA dfa, DFAState D) {
		System.out.println("NEW STATE: "+D.stateNumber+" in DFA for ATN.s0 "+dfa.atnStartState.stateNumber);
		System.out.println("\t"+trace_toString(D));
	}

	/** Invoked in {@link ParserATNSimulator#addDFAState} or {@link LexerATNSimulator#addDFAState}
	 *  after we discover that dfa already has a state that is equivalent to D. No DFA state was added to dfa.
	 *
	 * @since 4.10.2
	 */
	public void trace_addDFAState_existing(DFA dfa, DFAState D) {
		System.out.println("EXISTS: "+D.stateNumber+" in DFA for ATN.s0 "+dfa.atnStartState.stateNumber);
		System.out.println("\t"+trace_toString(D));
	}

	/** Invoked after we have added from -> to edge in dfa.
	 *
	 *  Java target: This call is synchronized on `from` state in {@link ParserATNSimulator#addDFAState}
	 *  and on `dfa.states` in {@link LexerATNSimulator#addDFAState}.
	 *
	 * @since 4.10.2
	 */
	public void trace_addDFAEdge(DFA dfa, DFAState from, int t, DFAState to) {
		System.out.println("EDGE: "+from.stateNumber+" -> "+to.stateNumber+" upon "+t+" in DFA for ATN.s0 "+dfa.atnStartState.stateNumber);
		System.out.println("\t"+trace_toString(from));
		System.out.println("\t"+"->");
		System.out.println("\t"+trace_toString(to));
	}
}

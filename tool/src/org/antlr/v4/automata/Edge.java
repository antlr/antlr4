package org.antlr.v4.automata;

import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.Grammar;

/** A DFA edge (NFA edges are called transitions) */
public class Edge {
	public int atom = Label.INVALID;
	public IntervalSet set;
	
	public DFAState target;

	public Edge(DFAState target) { this.target = target; }

	public String toString(Grammar g) {
		if ( set==null ) return g.getTokenDisplayName(atom);
		else return set.toString(g);
	}
}

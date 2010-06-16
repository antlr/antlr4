package org.antlr.v4.automata;

import org.antlr.v4.analysis.SemanticContext;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.Grammar;

/** A DFA edge (NFA edges are called transitions) */
public class Edge {
	public IntervalSet label;
	public SemanticContext semanticContext; // predicated edge?
	public DFAState target;

	public Edge(DFAState target) {
		this.target = target;
	}

	public Edge(DFAState target, IntervalSet label) {
		this(target);
		semanticContext = target.getGatedPredicatesInNFAConfigurations();
		this.label = label;
	}

	public String toString() { return label.toString(); }	

	public String toString(Grammar g) {
		return label!=null?label.toString(g):"";
	}
}

package org.antlr.v4.codegen;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.DFAState;
import org.antlr.v4.automata.Edge;
import org.antlr.v4.automata.Label;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.tool.ErrorManager;

import java.util.Vector;

/** From a DFA, create transition table etc... */
public class CompiledDFA {
	public DFA dfa;
	public Vector<Vector<Integer>> transition;
	public Vector<Integer> min;
	public Vector<Integer> max;

	public CompiledDFA(DFA dfa) {
		this.dfa = dfa;

		int n = dfa.states.size();
		min = new Vector<Integer>(n); min.setSize(n);
		max = new Vector<Integer>(n); max.setSize(n);
		transition = new Vector<Vector<Integer>>(n); transition.setSize(n);

		for (DFAState d : dfa.states) {
			if ( d == null ) continue;
			createMinMaxTables(d);
			createTransitionTableEntryForState(d);
		}
	}

	protected void createMinMaxTables(DFAState d) {
		int smin = Label.MAX_CHAR_VALUE + 1;
		int smax = Label.MIN_ATOM_VALUE - 1;
		int n = d.edges.size();
		for (int j = 0; j < n; j++) {
			Edge edge = d.edge(j);
			IntervalSet label = edge.label;
			int lmin = label.getMinElement();
			// if valid char (don't do EOF) and less than current min
			if ( lmin<smin && lmin>=Label.MIN_CHAR_VALUE ) {
				smin = label.getMinElement();
			}
			if ( label.getMaxElement()>smax ) {
				smax = label.getMaxElement();
			}
		}

		if ( smax<0 ) {
			// must be predicates or pure EOT transition; just zero out min, max
			smin = Label.MIN_CHAR_VALUE;
			smax = Label.MIN_CHAR_VALUE;
		}

		min.set(d.stateNumber, Utils.integer((char)smin));
		max.set(d.stateNumber, Utils.integer((char)smax));

		if ( smax<0 || smin>Label.MAX_CHAR_VALUE || smin<0 ) {
			ErrorManager.internalError("messed up: min="+min+", max="+max);
		}
	}

	void createTransitionTableEntryForState(DFAState s) {
		/*
		System.out.println("createTransitionTableEntryForState s"+s.stateNumber+
			" dec "+s.dfa.decisionNumber+" cyclic="+s.dfa.isCyclic());
			*/
		if ( s.edges.size() == 0 ) return;
		int smax = ((Integer)max.get(s.stateNumber)).intValue();
		int smin = ((Integer)min.get(s.stateNumber)).intValue();

		Vector<Integer> stateTransitions = new Vector<Integer>(smax-smin+1);
		stateTransitions.setSize(smax-smin+1);
		transition.set(s.stateNumber, stateTransitions);
		for (Edge e : s.edges) {
			int[] atoms = e.label.toArray();
			for (int a = 0; a < atoms.length; a++) {
				// set the transition if the label is valid (don't do EOF)
				if ( atoms[a] >= Label.MIN_CHAR_VALUE ) {
					int labelIndex = atoms[a]-smin; // offset from 0
					stateTransitions.set(labelIndex,
										 Utils.integer(e.target.stateNumber));
				}
			}
		}
		// track unique state transition tables so we can reuse
//		Integer edgeClass = (Integer)edgeTransitionClassMap.get(stateTransitions);
//		if ( edgeClass!=null ) {
//			//System.out.println("we've seen this array before; size="+stateTransitions.size());
//			transitionEdgeTables.set(s.stateNumber, edgeClass);
//		}
//		else {
//			edgeClass = Utils.integer(edgeTransitionClass);
//			transitionEdgeTables.set(s.stateNumber, edgeClass);
//			edgeTransitionClassMap.put(stateTransitions, edgeClass);
//			edgeTransitionClass++;
//		}
	}
}

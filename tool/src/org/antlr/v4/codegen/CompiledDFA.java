package org.antlr.v4.codegen;

import org.antlr.v4.analysis.SemanticContext;
import org.antlr.v4.automata.*;
import org.antlr.v4.misc.Interval;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.tool.ErrorManager;
import org.stringtemplate.v4.misc.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** From a DFA, create transition table etc... */
public class CompiledDFA {
	/** How big can char get before DFA states overflow and we need a set? */
	public static int MAX_EDGE_VALUE_FOR_TABLE = 255;
	
	public DFA dfa;
	public int[][] transition;
	public int[][] set_edges;
	public int[][] pred_edges; // 'a'&&{p1}?
	public int[] accept;
	public int[] eof;
	public int[] max;
	public int[] action_index;
	public List<String> actions;
	public List<String> sempreds;

	public CompiledDFA(DFA dfa) {
		this.dfa = dfa;

		int n = dfa.states.size();
		accept = new int[n];
		eof = new int[n];
		Arrays.fill(eof, -1);
		//min = new int[n];
		max = new int[n];
		transition = new int[n][];
		set_edges = new int[n][];
		pred_edges = new int[n][];
		action_index = new int[n];
		Arrays.fill(action_index, -1);
		actions = new ArrayList<String>();
		sempreds = new ArrayList<String>();

		for (DFAState d : dfa.states) {
			if ( d == null ) continue;
			if ( d.isAcceptState ) createAcceptTable(d);
			createMinMaxTables(d);
			createEOFTable(d);
			if ( d.edges.size() > 0 ) {
				createTransitionTableEntryForState(d);
				createSetTable(d);
				createPredTable(d);
			}
			if ( dfa.g.isLexer() ) createActionTable((LexerState)d);
		}
	}

	void createAcceptTable(DFAState d) {
		int predicts = d.predictsAlt;
		if ( d.dfa.g.isLexer() ) {
			// for lexer, we don't use predicted alt; we use token type
			LexerState ld = (LexerState)d;
			String predictedLexerRuleName = ld.predictsRule.name;
			predicts = d.dfa.g.getTokenType(predictedLexerRuleName);
		}
		accept[d.stateNumber] = predicts;
	}

	void createMinMaxTables(DFAState d) {
		int smin = Label.MAX_CHAR_VALUE + 1;
		int smax = Label.MIN_ATOM_VALUE - 1;
		int n = d.edges.size();
		for (int j = 0; j < n; j++) {
			Edge edge = d.edge(j);
			IntervalSet label = edge.label;
			if ( label==null ) continue; // must be pred only transition
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

		//min[d.stateNumber] = smin;
		max[d.stateNumber] = smax;

		if ( smax<0 || smin<0 ) {
			ErrorManager.internalError("messed up: max="+Arrays.toString(max));
		}
	}

	void createTransitionTableEntryForState(DFAState d) {
		/*
		System.out.println("createTransitionTableEntryForState s"+s.stateNumber+
			" dec "+s.dfa.decisionNumber+" cyclic="+s.dfa.isCyclic());
			*/
		int max = Math.min(this.max[d.stateNumber], MAX_EDGE_VALUE_FOR_TABLE);

		int[] stateTransitions = new int[max+1]; // make table only up to max
		Arrays.fill(stateTransitions, -1);		
		transition[d.stateNumber] = stateTransitions;

		int[] predTransitions = new int[max+1];
		Arrays.fill(stateTransitions, -1);
		transition[d.stateNumber] = stateTransitions;

		for (Edge e : d.edges) {
			if ( e.label==null ) continue; // must be pred only transition
			for (Interval I : e.label.getIntervals()) {
				SemanticContext preds =	e.target.getGatedPredicatesInNFAConfigurations();
				if ( I.a > MAX_EDGE_VALUE_FOR_TABLE || preds!=null ) break;
				// make sure range is MIN_CHAR_VALUE..MAX_EDGE_VALUE_FOR_TABLE and no preds
				int a = Math.max(I.a, Label.MIN_CHAR_VALUE);
				int b = Math.min(I.b, MAX_EDGE_VALUE_FOR_TABLE);
				//System.out.println("interval "+I+"->"+a+":"+b);
				for (int i=a; i<=b; i++) stateTransitions[i] = e.target.stateNumber;
			}
		}

		// TODO: track unique state transition tables so we can reuse
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

	/** Set up the EOF table; we cannot use -1 min/max values so
	 *  we need another way to test that in the DFA transition function.
	 */
	void createEOFTable(DFAState d) {
		for (Edge e : d.edges) {
			if ( e.label==null ) continue; // must be pred only transition			
			int[] atoms = e.label.toArray();
			for (int a : atoms) {
				if ( a==Label.EOF ) eof[d.stateNumber] = e.target.stateNumber;
			}
		}
	}

	void createSetTable(DFAState d) {
		// only pay attention if at least one edge's max char is > MAX
		if ( max[d.stateNumber] > MAX_EDGE_VALUE_FOR_TABLE ) {
			List<Integer> edges = new ArrayList<Integer>();
			// { target1, npairs1, range-pairs1,
			//   target2, npairs2, range-pairs2, ... }
			for (Edge e : d.edges) {
				// don't gen target if edge has all edges <= max
				if ( e.label.getMaxElement() <= MAX_EDGE_VALUE_FOR_TABLE ) continue;
				edges.add(e.target.stateNumber);
				edges.add(0); // leave whole for n
				List<Interval> intervals = e.label.getIntervals();
				int n = 0;
				for (Interval I : intervals) {
					// make sure range is beyond max or truncate left side to be above max
					if ( I.b <= MAX_EDGE_VALUE_FOR_TABLE ) continue;
					int a = Math.max(I.a, MAX_EDGE_VALUE_FOR_TABLE+1);
					edges.add(a);
					edges.add(I.b);
					n++;
				}
				edges.set(1, n);
			}
			if ( edges.size()>0 ) set_edges[d.stateNumber] = Utils.toIntArray(edges);
		}
	}

	void createPredTable(DFAState d) {
		List<Integer> edges = new ArrayList<Integer>();
		// { target1, sempred_index1, target2, sempred_index2, ... }
		for (Edge e : d.edges) {
			System.out.println("edge pred "+e.semanticContext);
			if ( e.semanticContext!=null ) {
				System.out.println("gated preds for "+e.target.stateNumber+": "+e.semanticContext);
				// TODO: translate sempreds and gen proper && expressions for target
				String p = e.semanticContext.toString();
				edges.add(e.target.stateNumber);
				int prevIndex = sempreds.indexOf(p);
				int i = prevIndex;
				if ( prevIndex<0 ) {
					i = sempreds.size();
					sempreds.add(p);
				}
				edges.add(i);
			}
		}
		if ( edges.size()>0 ) pred_edges[d.stateNumber] = Utils.toIntArray(edges);
	}

	void createActionTable(LexerState d) {
		if ( d.isAcceptState && d.action!=null ) {
			action_index[d.stateNumber] = actions.size();
			actions.add(Misc.strip(d.action.getText(),1)); // TODO: translate action
		}
	}
}

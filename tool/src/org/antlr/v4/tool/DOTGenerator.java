package org.antlr.v4.tool;

import org.antlr.v4.Tool;
import org.antlr.v4.analysis.NFAConfig;
import org.antlr.v4.automata.*;
import org.antlr.v4.misc.Utils;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;

import java.util.*;

/** The DOT (part of graphviz) generation aspect. */
public class DOTGenerator {
	public static final boolean STRIP_NONREDUCED_STATES = false;

	protected String arrowhead="normal";
	protected String rankdir="LR";

	/** Library of output templates; use <attrname> format */
    public static STGroup stlib = new STGroupDir("org/antlr/v4/tool/templates/dot");

    protected Grammar grammar;

    /** This aspect is associated with a grammar */
	public DOTGenerator(Grammar grammar) {
		this.grammar = grammar;
	}

    /** Return a String containing a DOT description that, when displayed,
     *  will show the incoming state machine visually.  All nodes reachable
     *  from startState will be included.
     */
	public String getDOT(NFAState startState) {
		if ( startState==null )	return null;

		// The output DOT graph for visualization
		ST dot = null;
		Set<NFAState> markedStates = new HashSet<NFAState>();
		dot = stlib.getInstanceOf("nfa");
		dot.add("startState", Utils.integer(startState.stateNumber));
		dot.add("rankdir", rankdir);

		List<NFAState> work = new LinkedList<NFAState>();

		work.add(startState);
		while ( work.size()>0 ) {
			NFAState s = work.get(0);
			if ( markedStates.contains(s) ) { work.remove(0); continue; }
			markedStates.add(s);

			// don't go past end of rule node to the follow states
			if ( s instanceof RuleStopState ) continue;

			// special case: if decision point, then line up the alt start states
			// unless it's an end of block
			if ( s instanceof DecisionState ) {
				GrammarAST n = ((NFAState)s).ast;
				if ( n!=null && s instanceof BlockEndState ) {
					ST rankST = stlib.getInstanceOf("decision-rank");
					NFAState alt = (NFAState)s;
					while ( alt!=null ) {
						rankST.add("states", alt.stateNumber);
						if ( alt.transition(1) !=null ) {
							alt = (NFAState)alt.transition(1).target;
						}
						else {
							alt=null;
						}
					}
					dot.add("decisionRanks", rankST);
				}
			}

			// make a DOT edge for each transition
			ST edgeST = null;
			for (int i = 0; i < s.getNumberOfTransitions(); i++) {
				Transition edge = (Transition) s.transition(i);
				if ( edge instanceof RuleTransition ) {
					RuleTransition rr = ((RuleTransition)edge);
					// don't jump to other rules, but display edge to follow node
					edgeST = stlib.getInstanceOf("edge");
					if ( rr.rule.g != grammar ) {
						edgeST.add("label", "<"+rr.rule.g.name+"."+rr.rule.name+">");
					}
					else {
						edgeST.add("label", "<"+rr.rule.name+">");
					}
					edgeST.add("src", "s"+s.stateNumber);
					edgeST.add("target", "s"+rr.followState.stateNumber);
					edgeST.add("arrowhead", arrowhead);
					dot.add("edges", edgeST);
					work.add(rr.followState);
					continue;
				}
				if ( edge instanceof ActionTransition ) {
					edgeST = stlib.getInstanceOf("action-edge");
				}
				else if ( edge instanceof PredicateTransition ) {
					edgeST = stlib.getInstanceOf("edge");
				}
				else if ( edge.isEpsilon() ) {
					edgeST = stlib.getInstanceOf("epsilon-edge");
				}
				else {
					edgeST = stlib.getInstanceOf("edge");
				}
				edgeST.add("label", getEdgeLabel(edge.toString(grammar)));
				edgeST.add("src", "s"+s.stateNumber);
				edgeST.add("target", "s"+edge.target.stateNumber);
				edgeST.add("arrowhead", arrowhead);
				dot.add("edges", edgeST);
				work.add(edge.target);
			}
		}

		// define nodes we visited (they will appear first in DOT output)
		// this is an example of ST's lazy eval :)
		// define stop state first; seems to be a bug in DOT where doublecircle
		// shape only works if we define them first. weird.
		NFAState stopState = startState.nfa.ruleToStopState.get(startState.rule);
		ST st = stlib.getInstanceOf("stopstate");
		st.add("name", "s"+stopState.stateNumber);
		st.add("label", getStateLabel(stopState));
		dot.add("states", st);
		for (NFAState s : markedStates) {
			if ( s instanceof RuleStopState ) continue;
			st = stlib.getInstanceOf("state");
			st.add("name", "s"+s.stateNumber);
			st.add("label", getStateLabel(s));
			dot.add("states", st);
		}

		return dot.render();
	}

	public String getDOT(DFAState startState) {
		if ( startState==null )	return null;

		ST dot = stlib.getInstanceOf("dfa");
		dot.add("startState", startState.stateNumber);
		dot.add("useBox", Tool.internalOption_ShowNFAConfigsInDFA);
		dot.add("rankdir", rankdir);

		Set<DFAState> markedStates = new HashSet<DFAState>();
		List<DFAState> work = new LinkedList<DFAState>();

		// define stop states first; seems to be a bug in DOT where doublecircle
		for (List<DFAState> stops : startState.dfa.altToAcceptStates) {
			if ( stops==null ) continue;
			for (DFAState d : stops) {
				if ( d==null ) continue;
				ST st = stlib.getInstanceOf("stopstate");
				st.add("name", "s"+d.stateNumber);
				st.add("label", getStateLabel(d));
				dot.add("states", st);
			}
		}

		for (DFAState d : startState.dfa.stateSet.values()) {
			if ( d.isAcceptState ) continue;
			ST st = stlib.getInstanceOf("state");
			st.add("name", "s"+d.stateNumber);
			st.add("label", getStateLabel(d));
			dot.add("states", st);
		}

		work.add(startState);
		while ( work.size()>0 ) {
			DFAState d = work.get(0);
			if ( markedStates.contains(d) ) { work.remove(0); continue; }
			markedStates.add(d);
			
			// make a DOT edge for each transition
			for (int i = 0; i < d.getNumberOfEdges(); i++) {
				Edge edge = d.edge(i);
				/*
				System.out.println("dfa "+s.dfa.decisionNumber+
					" edge from s"+s.stateNumber+" ["+i+"] of "+s.getNumberOfTransitions());
				*/
				ST st = stlib.getInstanceOf("edge");
//			SemanticContext preds = s.getGatedPredicatesInNFAConfigurations();
//			if ( preds!=null ) {
//				String predsStr = "";
//				predsStr = "&&{"+preds.toString()+"}?";
//				label += predsStr;
//			}

				st.add("label", getEdgeLabel(edge.toString(grammar)));
				st.add("src", "s"+d.stateNumber);
				st.add("target", "s"+edge.target.stateNumber);
				st.add("arrowhead", arrowhead);
				dot.add("edges", st);
				work.add(edge.target); // add targets to list of states to visit
			}

			work.remove(0);
		}

		return dot.render();
	}

    /** Do a depth-first walk of the state machine graph and
     *  fill a DOT description template.  Keep filling the
     *  states and edges attributes.  We know this is an NFA
     *  for a rule so don't traverse edges to other rules and
     *  don't go past rule end state.
     */
//    protected void walkRuleNFACreatingDOT(ST dot,
//                                          NFAState s)
//    {
//        if ( markedStates.contains(s) ) {
//            return; // already visited this node
//        }
//
//        markedStates.add(s.stateNumber); // mark this node as completed.
//
//        // first add this node
//        ST stateST;
//        if ( s instanceof RuleStopState ) {
//            stateST = stlib.getInstanceOf("stopstate");
//        }
//        else {
//            stateST = stlib.getInstanceOf("state");
//        }
//        stateST.add("name", getStateLabel(s));
//        dot.add("states", stateST);
//
//        if ( s instanceof RuleStopState )  {
//            return; // don't go past end of rule node to the follow states
//        }
//
//        // special case: if decision point, then line up the alt start states
//        // unless it's an end of block
//		if ( s instanceof DecisionState ) {
//			GrammarAST n = ((NFAState)s).ast;
//			if ( n!=null && s instanceof BlockEndState ) {
//				ST rankST = stlib.getInstanceOf("decision-rank");
//				NFAState alt = (NFAState)s;
//				while ( alt!=null ) {
//					rankST.add("states", getStateLabel(alt));
//					if ( alt.transition(1) !=null ) {
//						alt = (NFAState)alt.transition(1).target;
//					}
//					else {
//						alt=null;
//					}
//				}
//				dot.add("decisionRanks", rankST);
//			}
//		}
//
//        // make a DOT edge for each transition
//		ST edgeST = null;
//		for (int i = 0; i < s.getNumberOfTransitions(); i++) {
//            Transition edge = (Transition) s.transition(i);
//            if ( edge instanceof RuleTransition ) {
//                RuleTransition rr = ((RuleTransition)edge);
//                // don't jump to other rules, but display edge to follow node
//                edgeST = stlib.getInstanceOf("edge");
//				if ( rr.rule.g != grammar ) {
//					edgeST.add("label", "<"+rr.rule.g.name+"."+rr.rule.name+">");
//				}
//				else {
//					edgeST.add("label", "<"+rr.rule.name+">");
//				}
//				edgeST.add("src", getStateLabel(s));
//				edgeST.add("target", getStateLabel(rr.followState));
//				edgeST.add("arrowhead", arrowhead);
//                dot.add("edges", edgeST);
//				walkRuleNFACreatingDOT(dot, rr.followState);
//                continue;
//            }
//			if ( edge instanceof ActionTransition ) {
//				edgeST = stlib.getInstanceOf("action-edge");
//			}
//			else if ( edge instanceof PredicateTransition ) {
//				edgeST = stlib.getInstanceOf("edge");
//			}
//			else if ( edge.isEpsilon() ) {
//				edgeST = stlib.getInstanceOf("epsilon-edge");
//			}
//			else {
//				edgeST = stlib.getInstanceOf("edge");
//			}
//			edgeST.add("label", getEdgeLabel(edge.toString(grammar)));
//            edgeST.add("src", getStateLabel(s));
//			edgeST.add("target", getStateLabel(edge.target));
//			edgeST.add("arrowhead", arrowhead);
//            dot.add("edges", edgeST);
//            walkRuleNFACreatingDOT(dot, edge.target); // keep walkin'
//        }
//    }

    /** Fix edge strings so they print out in DOT properly;
	 *  generate any gated predicates on edge too.
	 */
    protected String getEdgeLabel(String label) {
		label = Utils.replace(label,"\\", "\\\\");
		label = Utils.replace(label,"\"", "\\\"");
		label = Utils.replace(label,"\n", "\\\\n");
		label = Utils.replace(label,"\r", "");
        return label;
    }

	protected String getStateLabel(NFAState s) {
		if ( s==null ) return "null";
		String stateLabel = String.valueOf(s.stateNumber);
		if ( s instanceof DecisionState ) {
			stateLabel = stateLabel+"\\nd="+((DecisionState)s).decision;
		}
		return stateLabel;
	}

	protected String getStateLabel(DFAState s) {
		if ( s==null ) return "null";
		StringBuffer buf = new StringBuffer(250);
		buf.append('s');
		buf.append(s.stateNumber);
		if ( s.isAcceptState ) {
			if ( s instanceof LexerState ) {
				buf.append("=>");
				for (Rule r : ((LexerState)s).matchesRules) {
					buf.append(" "+r.name);
				}
			}
			else {
				buf.append("=>"+s.getUniquelyPredictedAlt());
			}
        }
		if ( Tool.internalOption_ShowNFAConfigsInDFA ) {
			Set<Integer> alts = ((DFAState)s).getAltSet();
			if ( s instanceof LexerState ) {
				buf.append("\\n");
				buf.append( ((LexerState)s).nfaStates.toString() );
			}
			else if ( alts!=null ) {
				buf.append("\\n");
				// separate alts
				List<Integer> altList = new ArrayList<Integer>();
				altList.addAll(alts);
				Collections.sort(altList);
				Set configurations = ((DFAState) s).nfaConfigs;
				for (int altIndex = 0; altIndex < altList.size(); altIndex++) {
					Integer altI = (Integer) altList.get(altIndex);
					int alt = altI.intValue();
					if ( altIndex>0 ) {
						buf.append("\\n");
					}
					buf.append("alt");
					buf.append(alt);
					buf.append(':');
					// get a list of configs for just this alt
					// it will help us print better later
					List<NFAConfig> configsInAlt = new ArrayList<NFAConfig>();
					for (Iterator it = configurations.iterator(); it.hasNext();) {
						NFAConfig c = (NFAConfig) it.next();
						if ( c.alt!=alt ) continue;
						configsInAlt.add(c);
					}
					int n = 0;
					for (int cIndex = 0; cIndex < configsInAlt.size(); cIndex++) {
						NFAConfig c =
							(NFAConfig)configsInAlt.get(cIndex);
						n++;
						buf.append(c.toString(false));
						if ( (cIndex+1)<configsInAlt.size() ) {
							buf.append(", ");
						}
						if ( n%5==0 && (configsInAlt.size()-cIndex)>3 ) {
							buf.append("\\n");
						}
					}
				}
			}
		}
		String stateLabel = buf.toString();
        return stateLabel;
    }
}

package org.antlr.v4.automata;

import org.antlr.v4.misc.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.tool.Rule;

import java.util.*;

public class ATNSerializer {
	public ATN atn;
	public List<IntervalSet> sets = new ArrayList<IntervalSet>();

	public ATNSerializer(ATN atn) { this.atn = atn; }

	/** Serialize state descriptors, edge descriptors, and decision->state map
	 *  into list of ints:
	 *
	 * 		grammar-type, (ANTLRParser.LEXER, ...)
	 *  	max token type,
	 *  	num states,
	 *  	state-0-type ruleIndex, state-1-type ruleIndex, ...
	 *  	num rules,
	 *  	rule-1-start-state rule-1-args, rule-2-start-state  rule-2-args, ...
	 *  	(args are token type,actionIndex in lexer else 0,0)
	 *      num modes,
	 *      mode-0-start-state, mode-1-start-state, ... (parser has 0 modes)
	 *      num sets
	 *      set-0-interval-count intervals, set-1-interval-count intervals, ...
	 *  	num total edges,
	 *      src, trg, edge-type, edge arg1, optional edge arg2 (present always), ...
	 *      num decisions,
	 *      decision-0-start-state, decision-1-start-state, ...
	 *
	 *  Convenient to pack into unsigned shorts to make as Java string.
	 */
	public List<Integer> serialize() {
		List<Integer> data = new ArrayList<Integer>();
		data.add(atn.g.getType());
		data.add(atn.g.getMaxTokenType());
		data.add(atn.states.size());
		int nedges = 0;
		// dump states, count edges and collect sets while doing so
		for (ATNState s : atn.states) {
			data.add(ATNState.serializationTypes.get(s.getClass()));
			if ( s.rule!=null ) data.add(s.rule.index);
			else data.add(s.ruleIndex);
			nedges += s.getNumberOfTransitions();
			for (int i=0; i<s.getNumberOfTransitions(); i++) {
				Transition t = s.transition(i);
				int edgeType = Transition.serializationTypes.get(t.getClass());
				if ( edgeType == Transition.SET || edgeType == Transition.NOT_SET ) {
					SetTransition st = (SetTransition)t;
					sets.add(st.label);
				}
			}
		}
		int nrules = atn.rules.size();
		data.add(nrules);
		for (int r=1; r<=nrules; r++) {
			ATNState ruleStartState = atn.rules.get(r-1);
			data.add(ruleStartState.stateNumber);
			if ( atn.g.isLexer() ) {
				data.add(atn.ruleToTokenType.get(r));
				String ruleName = atn.g.rules.getKey(r-1);
				Rule rule = atn.g.getRule(ruleName);
				data.add(rule.actionIndex);
			}
			else {
				data.add(0);
				data.add(0);
			}
		}
		int nmodes = atn.modeToStartState.size();
		data.add(nmodes);
		if ( nmodes>0 ) {
			for (ATNState modeStartState : atn.modeToStartState) {
				data.add(modeStartState.stateNumber);
			}
		}
		int nsets = sets.size();
		data.add(nsets);
		for (IntervalSet set : sets) {
			data.add(set.getIntervals().size());
			for (Interval I : set.getIntervals()) {
				data.add(I.a);
				data.add(I.b);
			}
		}
		data.add(nedges);
		int setIndex = 0;
		for (ATNState s : atn.states) {
			for (int i=0; i<s.getNumberOfTransitions(); i++) {
				Transition t = s.transition(i);
				int src = s.stateNumber;
				int trg = t.target.stateNumber;
				int edgeType = Transition.serializationTypes.get(t.getClass());
				int arg1 = 0;
				int arg2 = 0;
				switch ( edgeType ) {
					case Transition.RULE :
						trg = ((RuleTransition)t).followState.stateNumber;
						arg1 = ((RuleTransition)t).target.stateNumber;
						if ( ((RuleTransition)t).rule!=null ) {
							arg2 = ((RuleTransition)t).rule.index;
						}
						else {
							arg2 = ((RuleTransition)t).ruleIndex;
						}
						break;
					case Transition.PREDICATE :
						PredicateTransition pt = (PredicateTransition)t;
						arg1 = pt.ruleIndex;
						arg2 = pt.predIndex;
						break;
					case Transition.RANGE :
						arg1 = ((RangeTransition)t).from;
						arg2 = ((RangeTransition)t).to;
						break;
					case Transition.ATOM :
						arg1 = ((AtomTransition)t).label;
						break;
					case Transition.ACTION :
						ActionTransition at = (ActionTransition)t;
						arg1 = at.ruleIndex;
						arg2 = at.actionIndex;
						break;
					case Transition.SET :
						arg1 = setIndex++;
						break;
					case Transition.NOT_ATOM :
						arg1 = ((NotAtomTransition)t).label;
						break;
					case Transition.NOT_SET :
						arg1 = setIndex++;
						break;
					case Transition.WILDCARD :
						break;
				}
				data.add(src);
				data.add(trg);
				data.add(edgeType);
				data.add(arg1);
				data.add(arg2);
			}
		}
		int ndecisions = atn.decisionToATNState.size();
		data.add(ndecisions);
		for (ATNState decStartState : atn.decisionToATNState) {
			data.add(decStartState.stateNumber);
		}
		return data;
	}

	public String decode(char[] data) {
		StringBuilder buf = new StringBuilder();
		int p = 0;
		int grammarType = ATNInterpreter.toInt(data[p++]);
		int maxType = ATNInterpreter.toInt(data[p++]);
		buf.append("max type "+maxType+"\n");
		int nstates = ATNInterpreter.toInt(data[p++]);
		for (int i=1; i<=nstates; i++) {
			int stype = ATNInterpreter.toInt(data[p++]);
			int ruleIndex = ATNInterpreter.toInt(data[p++]);
			if ( stype==0 ) continue; // ignore bad type of states
			buf.append((i - 1) + ":" +
					   ATNState.serializationNames[stype] + " "+
					   ruleIndex + "\n");
		}
		int nrules = ATNInterpreter.toInt(data[p++]);
		for (int i=1; i<=nrules; i++) {
			int s = ATNInterpreter.toInt(data[p++]);
			int arg1 = ATNInterpreter.toInt(data[p++]);
			int arg2 = ATNInterpreter.toInt(data[p++]);
			buf.append("rule "+i+":"+s+" "+arg1+","+arg2+'\n');
		}
		int nmodes = ATNInterpreter.toInt(data[p++]);
		for (int i=0; i<nmodes; i++) {
			int s = ATNInterpreter.toInt(data[p++]);
			buf.append("mode "+i+":"+s+'\n');
		}
		int nsets = ATNInterpreter.toInt(data[p++]);
		for (int i=1; i<=nsets; i++) {
			int nintervals = ATNInterpreter.toInt(data[p++]);
			buf.append((i-1)+":");
			for (int j=1; j<=nintervals; j++) {
				if ( j>1 ) buf.append(", ");
				buf.append(getTokenName(ATNInterpreter.toInt(data[p]))+".."+getTokenName(ATNInterpreter.toInt(data[p+1])));
				p += 2;
			}
			buf.append("\n");
		}
		int nedges = ATNInterpreter.toInt(data[p++]);
		for (int i=1; i<=nedges; i++) {
			int src = ATNInterpreter.toInt(data[p]);
			int trg = ATNInterpreter.toInt(data[p+1]);
			int ttype = ATNInterpreter.toInt(data[p+2]);
			int arg1 = ATNInterpreter.toInt(data[p+3]);
			int arg2 = ATNInterpreter.toInt(data[p+4]);
			buf.append(src+"->"+trg+
					   " "+Transition.serializationNames[ttype]+
					   " "+arg1+","+arg2+
					   "\n");
			p += 5;
		}
		int ndecisions = ATNInterpreter.toInt(data[p++]);
		for (int i=1; i<=ndecisions; i++) {
			int s = ATNInterpreter.toInt(data[p++]);
			buf.append((i-1)+":"+s+"\n");
		}
		return buf.toString();
	}

	public String getTokenName(int t) {
		if ( t==-1 ) return "EOF";
		if ( atn.g!=null ) return atn.g.getTokenDisplayName(t);
		return String.valueOf(t);
	}
}

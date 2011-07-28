/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.automata;

import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.tool.*;

import java.util.*;

public class ATNSerializer {
	public Grammar g;
	public ATN atn;
	public List<IntervalSet> sets = new ArrayList<IntervalSet>();

	public ATNSerializer(Grammar g, ATN atn) {
		this.g = g;
		this.atn = atn;
	}

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
		// convert grammar type to ATN const to avoid dependence on ANTLRParser
		if ( g.getType()== ANTLRParser.LEXER ) data.add(ATN.LEXER);
		else if ( g.getType()== ANTLRParser.PARSER ) data.add(ATN.PARSER);
		else data.add(ATN.TREE_PARSER);
		data.add(g.getMaxTokenType());
		data.add(atn.states.size());
		int nedges = 0;
		// dump states, count edges and collect sets while doing so
		for (ATNState s : atn.states) {
			data.add(ATNState.serializationTypes.get(s.getClass()));
			data.add(s.ruleIndex);
			nedges += s.getNumberOfTransitions();
			for (int i=0; i<s.getNumberOfTransitions(); i++) {
				Transition t = s.transition(i);
				int edgeType = Transition.serializationTypes.get(t.getClass());
				if ( edgeType == Transition.SET || edgeType == Transition.NOT_SET ) {
					SetTransition st = (SetTransition)t;
					sets.add(st.set);
				}
			}
		}
		int nrules = atn.ruleToStartState.length;
		data.add(nrules);
		for (int r=0; r<nrules; r++) {
			ATNState ruleStartState = atn.ruleToStartState[r];
			data.add(ruleStartState.stateNumber);
			if ( g.isLexer() ) {
				data.add(atn.ruleToTokenType[r]);
				String ruleName = g.rules.getKey(r);
				Rule rule = g.getRule(ruleName);
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
						arg2 = ((RuleTransition)t).ruleIndex;
						break;
					case Transition.PREDICATE :
						PredicateTransition pt = (PredicateTransition)t;
						arg1 = pt.ruleIndex;
						arg2 = pt.predIndex;
						if ( pt.isCtxDependent ) edgeType = Transition.DEPENDENT_PREDICATE;
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
//					case Transition.NOT_ATOM :
//						arg1 = ((NotAtomTransition)t).label;
//						break;
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
		int ndecisions = atn.decisionToState.size();
		data.add(ndecisions);
		for (ATNState decStartState : atn.decisionToState) {
			data.add(decStartState.stateNumber);
		}
		return data;
	}

	public String decode(char[] data) {
		StringBuilder buf = new StringBuilder();
		int p = 0;
		int grammarType = ATNSimulator.toInt(data[p++]);
		int maxType = ATNSimulator.toInt(data[p++]);
		buf.append("max type "+maxType+"\n");
		int nstates = ATNSimulator.toInt(data[p++]);
		for (int i=1; i<=nstates; i++) {
			int stype = ATNSimulator.toInt(data[p++]);
			int ruleIndex = ATNSimulator.toInt(data[p++]);
			if ( stype==0 ) continue; // ignore bad type of states
			buf.append((i - 1) + ":" +
					   ATNState.serializationNames[stype] + " "+
					   ruleIndex + "\n");
		}
		int nrules = ATNSimulator.toInt(data[p++]);
		for (int i=0; i<nrules; i++) {
			int s = ATNSimulator.toInt(data[p++]);
			int arg1 = ATNSimulator.toInt(data[p++]);
			int arg2 = ATNSimulator.toInt(data[p++]);
			buf.append("rule "+i+":"+s+" "+arg1+","+arg2+'\n');
		}
		int nmodes = ATNSimulator.toInt(data[p++]);
		for (int i=0; i<nmodes; i++) {
			int s = ATNSimulator.toInt(data[p++]);
			buf.append("mode "+i+":"+s+'\n');
		}
		int nsets = ATNSimulator.toInt(data[p++]);
		for (int i=1; i<=nsets; i++) {
			int nintervals = ATNSimulator.toInt(data[p++]);
			buf.append((i-1)+":");
			for (int j=1; j<=nintervals; j++) {
				if ( j>1 ) buf.append(", ");
				buf.append(getTokenName(ATNSimulator.toInt(data[p]))+".."+getTokenName(ATNSimulator.toInt(data[p + 1])));
				p += 2;
			}
			buf.append("\n");
		}
		int nedges = ATNSimulator.toInt(data[p++]);
		for (int i=1; i<=nedges; i++) {
			int src = ATNSimulator.toInt(data[p]);
			int trg = ATNSimulator.toInt(data[p + 1]);
			int ttype = ATNSimulator.toInt(data[p + 2]);
			int arg1 = ATNSimulator.toInt(data[p + 3]);
			int arg2 = ATNSimulator.toInt(data[p + 4]);
			buf.append(src+"->"+trg+
					   " "+Transition.serializationNames[ttype]+
					   " "+arg1+","+arg2+
					   "\n");
			p += 5;
		}
		int ndecisions = ATNSimulator.toInt(data[p++]);
		for (int i=1; i<=ndecisions; i++) {
			int s = ATNSimulator.toInt(data[p++]);
			buf.append((i-1)+":"+s+"\n");
		}
		return buf.toString();
	}

	public String getTokenName(int t) {
		if ( t==-1 ) return "EOF";
		if ( g!=null ) return g.getTokenDisplayName(t);
		return String.valueOf(t);
	}

	/** Used by Java target to encode short/int array as chars in string. */
	public static String getSerializedAsString(Grammar g, ATN atn) {
		return new String(Utils.toCharArray(getSerialized(g, atn)));
	}

	public static List<Integer> getSerialized(Grammar g, ATN atn) {
		return new ATNSerializer(g, atn).serialize();
	}

	public static char[] getSerializedAsChars(Grammar g, ATN atn) {
		return Utils.toCharArray(new ATNSerializer(g, atn).serialize());
	}

	public static String getDecoded(Grammar g, ATN atn) {
		return new ATNSerializer(g, atn).decode(Utils.toCharArray(getSerialized(g, atn)));
	}
}

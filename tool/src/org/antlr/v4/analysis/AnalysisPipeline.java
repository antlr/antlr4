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

package org.antlr.v4.analysis;

import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.LL1Analyzer;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.tool.Grammar;

import java.util.ArrayList;
import java.util.Arrays;

public class AnalysisPipeline {
	public Grammar g;

	public AnalysisPipeline(Grammar g) {
		this.g = g;
	}

	public void process() {
		// LEFT-RECURSION CHECK
		LeftRecursionDetector lr = new LeftRecursionDetector(g, g.atn);
		lr.check();
		if ( lr.listOfRecursiveCycles.size()>0 ) return; // bail out

		// BUILD DFA FOR EACH DECISION
		if ( !g.isLexer() ) processParserOrTreeParser();
	}

	void processParserOrTreeParser() {
		g.decisionLOOK =
			new ArrayList<IntervalSet[]>(g.atn.getNumberOfDecisions()+1);
		for (DecisionState s : g.atn.decisionToState) {
            g.tool.log("LL1", "\nDECISION "+s.decision+" in rule "+g.getRule(s.ruleIndex).name);

			LL1Analyzer anal = new LL1Analyzer(g.atn);
			IntervalSet[] look = anal.getDecisionLookahead(s);
            g.tool.log("LL1", "look=" + Arrays.toString(look));
			Utils.setSize(g.decisionLOOK, s.decision+1);
			g.decisionLOOK.set(s.decision, look);
            g.tool.log("LL1", "LL(1)? " + disjoint(look));
		}
	}

	/** Return lookahead depth at which lookahead sets are disjoint or return 0 */
	public static boolean disjoint(IntervalSet[] altLook) {
		boolean collision = false;
		IntervalSet combined = new IntervalSet();
		for (int a=1; a<altLook.length; a++) {
			IntervalSet look = altLook[a];
			if ( look==null ) return false; // lookahead must've computation failed
			if ( !look.and(combined).isNil() ) {
				collision = true;
				break;
			}
			combined.addAll(look);
		}
		return !collision;
	}
}

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

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.*;

import java.util.*;

/** Build ATNs for tree grammars */
public class TreeParserATNFactory extends ParserATNFactory {
	// track stuff for ^(...) patterns in grammar to fix up nullable after ATN build
	List<TreePatternAST> treePatternRootNodes = new ArrayList<TreePatternAST>();
	List<ATNState> firstChildStates = new ArrayList<ATNState>();
	List<ATNState> downStates = new ArrayList<ATNState>();
	List<ATNState> upTargetStates = new ArrayList<ATNState>();

	public TreeParserATNFactory(Grammar g) {
		super(g);
	}

	public ATN createATN() {
		super.createATN();

		for (int i=0; i<firstChildStates.size(); i++) {
			ATNState firstChild = firstChildStates.get(i);
			LL1Analyzer analyzer = new LL1Analyzer(atn);
			IntervalSet look = analyzer.LOOK(firstChild, RuleContext.EMPTY);
			TreePatternAST root = treePatternRootNodes.get(i);
			System.out.println(root.toStringTree()+"==nullable? "+look.contains(Token.UP));

			if ( look.contains(Token.UP) ) {
				// nullable child list if we can see the UP as the next token.
				// convert r DN kids UP to r (DN kids UP)?; leave AST alone--
				// that drives code gen. This just affects analysis
				epsilon(downStates.get(i), upTargetStates.get(i));
			}
		}

		return atn;
	}

	/** x y z from ^(x y z) becomes o-x->o-DOWN->o-y->o-z->o-UP->o
	 *  ANTLRParser.g has added DOWN_TOKEN, UP_TOKEN into AST.
	 *  Elems are [root, DOWN_TOKEN, x, y, UP_TOKEN]
	 */
	public Handle tree(GrammarAST node, List<Handle> els) {
		TreePatternAST root = (TreePatternAST) node;

		Handle h = elemList(els);
		treePatternRootNodes.add(root);
		// find DOWN node then first child
		for (Handle elh : els) {
			Transition trans = elh.left.transition(0);
			if ( !trans.isEpsilon() && trans.label().contains(Token.DOWN) ) {
				ATNState downState = elh.left;
				downStates.add(downState);
				root.downState = downState;
				firstChildStates.add(downState.transition(0).target);
				break;
			}
		}
		// find UP node
		for (Handle elh : els) {
			Transition trans = elh.left.transition(0);
			if ( trans instanceof AtomTransition && trans.label().contains(Token.UP) ) {
				ATNState upTargetState = elh.right;
				root.upState = elh.left;
				upTargetStates.add(upTargetState);
				break;
			}
		}

		return h;
	}
}

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

import org.antlr.v4.tool.*;

import java.util.List;

/** Build ATNs for tree grammars */
public class TreeParserATNFactory extends ParserATNFactory {
	public TreeParserATNFactory(Grammar g) {
		super(g);
	}

	/** x y z from ^(x y z) becomes o-x->o-DOWN->o-y->o-z->o-UP->o */
	public Handle tree(GrammarAST node, List<Handle> els) {
		Handle h = elemList(els);
		return h;

//		ATNState first = h.left;
//		ATNState last = h.right;
//		node.atnState = first;
//
//		// find root transition first side node
//		ATNState p = first;
//		while ( p.transition(0) instanceof EpsilonTransition ||
//				p.transition(0) instanceof PredicateTransition ||
//				p.transition(0) instanceof RangeTransition ||
//				p.transition(0) instanceof ActionTransition )
//		{
//			p = p.transition(0).target;
//		}
//		ATNState rootLeftNode = p;
//		ATNState rootRightNode = rootLeftNode.transition(0).target;
//		ATNState downLeftNode = newState(node);
//		downLeftNode.transition = new AtomTransition(Token.DOWN, rootRightNode);
//		rootRightNode.incidentTransition = downLeftNode.transition;
//		rootLeftNode.transition.target = downLeftNode;
//		downLeftNode.incidentTransition = rootLeftNode.transition;
//
//		ATNState upRightNode = newState(node);
//		last.transition = new AtomTransition(Token.UP, upRightNode);
//		upRightNode.incidentTransition = last.transition;
//		last = upRightNode;
//
//		return new Handle(first, last);
	}

}

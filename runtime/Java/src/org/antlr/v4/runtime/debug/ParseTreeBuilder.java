/*
 [The "BSD license"]
 Copyright (c) 2005-2009 Terence Parr
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
package org.antlr.v4.runtime.debug;

import org.antlr.runtime.tree.ParseTree;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/** This parser listener tracks rule entry/exit and token matches
 *  to build a simple parse tree using ParseTree nodes.
 */
public class ParseTreeBuilder extends BlankDebugEventListener {
	public static final String EPSILON_PAYLOAD = "<epsilon>";

	Stack callStack = new Stack();
	List hiddenTokens = new ArrayList();
	int backtracking = 0;

	public ParseTreeBuilder(String grammarName) {
		ParseTree root = create("<grammar "+grammarName+">");
		callStack.push(root);
	}

	public ParseTree getTree() {
		return (ParseTree)callStack.elementAt(0);
	}

	/**  What kind of node to create.  You might want to override
	 *   so I factored out creation here.
	 */
	public ParseTree create(Object payload) {
		return new ParseTree(payload);
	}

	public ParseTree epsilonNode() {
		return create(EPSILON_PAYLOAD);
	}

	/** Backtracking or cyclic DFA, don't want to add nodes to tree */
	public void enterDecision(int d, boolean couldBacktrack) { backtracking++; }
	public void exitDecision(int i) { backtracking--; }

	public void enterRule(String filename, String ruleName) {
		if ( backtracking>0 ) return;
		ParseTree parentRuleNode = (ParseTree)callStack.peek();
		ParseTree ruleNode = create(ruleName);
		parentRuleNode.addChild(ruleNode);
		callStack.push(ruleNode);
	}

	public void exitRule(String filename, String ruleName) {
		if ( backtracking>0 ) return;
		ParseTree ruleNode = (ParseTree)callStack.peek();
		if ( ruleNode.getChildCount()==0 ) {
			ruleNode.addChild(epsilonNode());
		}
		callStack.pop();
	}

	public void consumeToken(Token token) {
		if ( backtracking>0 ) return;
		ParseTree ruleNode = (ParseTree)callStack.peek();
		ParseTree elementNode = create(token);
		elementNode.hiddenTokens = this.hiddenTokens;
		this.hiddenTokens = new ArrayList();
		ruleNode.addChild(elementNode);
	}

	public void consumeHiddenToken(Token token) {
		if ( backtracking>0 ) return;
		hiddenTokens.add(token);
	}

	public void recognitionException(RecognitionException e) {
		if ( backtracking>0 ) return;
		ParseTree ruleNode = (ParseTree)callStack.peek();
		ParseTree errorNode = create(e);
		ruleNode.addChild(errorNode);
	}
}

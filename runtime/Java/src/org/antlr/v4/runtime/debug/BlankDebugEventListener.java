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


import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

/** A blank listener that does nothing; useful for real classes so
 *  they don't have to have lots of blank methods and are less
 *  sensitive to updates to debug interface.
 */
public class BlankDebugEventListener implements DebugEventListener {
	public void enterRule(String grammarFileName, String ruleName) {}
	public void exitRule(String grammarFileName, String ruleName) {}
	public void enterAlt(int alt) {}
	public void enterSubRule(int decisionNumber) {}
	public void exitSubRule(int decisionNumber) {}
	public void enterDecision(int decisionNumber, boolean couldBacktrack) {}
	public void exitDecision(int decisionNumber) {}
	public void location(int line, int pos) {}
	public void consumeToken(Token token) {}
	public void consumeHiddenToken(Token token) {}
	public void LT(int i, Token t) {}
	public void mark(int i) {}
	public void rewind(int i) {}
	public void seek(int i) {}
	public void recognitionException(RecognitionException e) {}
	public void beginResync() {}
	public void endResync() {}
	public void semanticPredicate(boolean result, String predicate) {}
	public void commence() {}
	public void terminate() {}

	// Tree parsing stuff

	public void consumeNode(Object t) {}
	public void LT(int i, Object t) {}

	// AST Stuff

	public void nilNode(Object t) {}
	public void errorNode(Object t) {}
	public void createNode(Object t) {}
	public void createNode(Object node, Token token) {}
	public void becomeRoot(Object newRoot, Object oldRoot) {}
	public void addChild(Object root, Object child) {}
	public void setTokenBoundaries(Object t, int tokenStartIndex, int tokenStopIndex) {}
}



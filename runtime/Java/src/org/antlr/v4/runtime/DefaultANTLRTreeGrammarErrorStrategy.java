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

package org.antlr.v4.runtime;

import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.tree.gui.TreeViewer;

import java.util.*;

public class DefaultANTLRTreeGrammarErrorStrategy<T> extends DefaultANTLRErrorStrategy<T> {
	@Override
	public void beginErrorCondition(BaseRecognizer<T> recognizer) {
	}

	@Override
	public void reportError(BaseRecognizer<T> recognizer, RecognitionException e)
		throws RecognitionException
	{
		super.reportError(recognizer, e);
		TreeParser<T> parser = (TreeParser<T>)recognizer;
		ASTNodeStream<T> input = parser.getInputStream();
		Object root = input.getTreeSource();
		// If instanceof Tree, we can show in TreeViewer
		if ( root instanceof Tree ) {
			TreeViewer viewer = new TreeViewer(recognizer, (Tree)root);
			viewer.open();
			List<T> unmatchedNodes = null;
			if ( e instanceof NoViableTreeGrammarAltException ) {
				NoViableTreeGrammarAltException nva =
					(NoViableTreeGrammarAltException)e;
				unmatchedNodes = getNodeList(input, nva);
			}
			else {
				unmatchedNodes = new ArrayList<T>();
				unmatchedNodes.add((T)e.offendingNode);
			}
			viewer.setHighlightedBoxColor(TreeViewer.LIGHT_RED);
			viewer.addHighlightedNodes((List<Tree>)unmatchedNodes);
		}
	}

	@Override
	public void reportNoViableAlternative(BaseRecognizer<T> recognizer,
										  NoViableAltException e)
		throws RecognitionException
	{
		TreeParser<T> parser = (TreeParser<T>)recognizer;
		ASTNodeStream<T> input = parser.getInputStream();
		List<T> unmatchedNodes =
			getNodeList(input, (NoViableTreeGrammarAltException)e);
		StringBuilder buf = new StringBuilder();
		ASTAdaptor<T> adap = input.getTreeAdaptor();
		for (int i = 0; i < unmatchedNodes.size(); i++) {
			if ( i>0 ) buf.append(" ");
			T t = unmatchedNodes.get(i);
			buf.append(adap.getText(t));
		}
		String s = buf.toString();
		String msg = "no viable alternative at node(s) "+escapeWSAndQuote(s);
		recognizer.notifyListeners((T)e.offendingNode, msg, e);
	}

	protected List<T> getNodeList(ASTNodeStream<T> input,
								  NoViableTreeGrammarAltException nva)
	{
		List<T> unmatchedNodes;
		T start = (T)nva.startNode;
		T stop = (T)nva.offendingNode;
		if ( input instanceof BufferedASTNodeStream) {
			BufferedASTNodeStream<T> b =
				(BufferedASTNodeStream<T>)input;
			unmatchedNodes = b.get(start, stop);
		}
		else {
			// if not buffered then we can't get from start to stop;
			// just highlight the start/stop nodes, but not in between
			unmatchedNodes = new ArrayList<T>();
			if ( nva.startNode!=null ) {
				unmatchedNodes.add((T)nva.startNode);
			}
			if ( nva.startNode==null || nva.offendingNode!=nva.startNode ) {
				unmatchedNodes.add((T)nva.offendingNode);
			}
		}
		return unmatchedNodes;
	}

	@Override
	public T recoverInline(BaseRecognizer<T> recognizer) throws RecognitionException {
		InputMismatchException e = new InputMismatchException(recognizer);
		reportError(recognizer, e);
		throw e;
	}

	@Override
	public void recover(BaseRecognizer<T> recognizer, RecognitionException e) {
		throw new RuntimeException(e);
	}

	@Override
	public void sync(BaseRecognizer<T> recognizer) {
	}

	@Override
	public boolean inErrorRecoveryMode(BaseRecognizer<T> recognizer) {
		return false;
	}

	@Override
	public void endErrorCondition(BaseRecognizer<T> recognizer) {
	}
}

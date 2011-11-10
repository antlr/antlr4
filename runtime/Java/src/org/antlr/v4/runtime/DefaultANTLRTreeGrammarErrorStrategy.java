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

public class DefaultANTLRTreeGrammarErrorStrategy implements ANTLRErrorStrategy {
	@Override
	public void beginErrorCondition(BaseRecognizer recognizer) {
	}

	@Override
	public void reportError(BaseRecognizer recognizer, RecognitionException e)
		throws RecognitionException
	{
		Object root = ((TreeParser)recognizer).getInputStream().getTreeSource();
		if ( root instanceof Tree ) {
			TreeViewer viewer = new TreeViewer(recognizer, (Tree)root);
			viewer.open();
			// TODO: highlight error node
		}
		recognizer.notifyListeners(e.offendingToken, e.getMessage(), e);
	}

	@Override
	public Object recoverInline(BaseRecognizer recognizer) throws RecognitionException {
		throw new InputMismatchException(recognizer);
	}

	@Override
	public void recover(BaseRecognizer recognizer, RecognitionException e) {
		throw new RecognitionException(recognizer,
									   recognizer.getInputStream(),
									   recognizer._ctx);
	}

	@Override
	public void sync(BaseRecognizer recognizer) {
	}

	@Override
	public boolean inErrorRecoveryMode(BaseRecognizer recognizer) {
		return false;
	}

	@Override
	public void endErrorCondition(BaseRecognizer recognizer) {
	}
}

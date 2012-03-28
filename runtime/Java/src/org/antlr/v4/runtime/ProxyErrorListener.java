/*
 [The "BSD license"]
 Copyright (c) 2012 Terence Parr
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

import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.Collection;

/**
 *
 * @author Sam Harwell
 */
public class ProxyErrorListener<Symbol> implements ANTLRErrorListener<Symbol> {
	private final Collection<? extends ANTLRErrorListener<? super Symbol>> delegates;

	public ProxyErrorListener(Collection<? extends ANTLRErrorListener<? super Symbol>> delegates) {
		this.delegates = delegates;
	}

	@Override
	public <T extends Symbol> void error(Recognizer<T, ?> recognizer,
										 T offendingSymbol,
										 int line,
										 int charPositionInLine,
										 String msg,
										 RecognitionException e)
	{
		for (ANTLRErrorListener<? super Symbol> listener : delegates) {
			listener.error(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
		}
	}

	@Override
	public void reportAmbiguity(Parser recognizer,
								DFA dfa,
								int startIndex,
								int stopIndex,
								IntervalSet ambigAlts,
								ATNConfigSet configs)
	{
		for (ANTLRErrorListener<? super Symbol> listener : delegates) {
			listener.reportAmbiguity(recognizer, dfa, startIndex, stopIndex, ambigAlts, configs);
		}
	}

	@Override
	public void reportAttemptingFullContext(Parser recognizer,
											DFA dfa,
											int startIndex,
											int stopIndex,
											ATNConfigSet configs)
	{
		for (ANTLRErrorListener<? super Symbol> listener : delegates) {
			listener.reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, configs);
		}
	}

	@Override
	public void reportContextSensitivity(Parser recognizer,
										 DFA dfa,
										 int startIndex,
										 int stopIndex,
										 ATNConfigSet configs)
	{
		for (ANTLRErrorListener<? super Symbol> listener : delegates) {
			listener.reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, configs);
		}
	}
}

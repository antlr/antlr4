/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 * This implementation of {@link ANTLRErrorStrategy} responds to syntax errors
 * by immediately canceling the parse operation with a
 * {@link ParseCancellationException}. The implementation ensures that the
 * {@link ParserRuleContext#exception} field is set for all parse tree nodes
 * that were not completed prior to encountering the error.
 *
 * <p>
 * This error strategy is useful in the following scenarios.</p>
 *
 * <ul>
 * <li><strong>Two-stage parsing:</strong> This error strategy allows the first
 * stage of two-stage parsing to immediately terminate if an error is
 * encountered, and immediately fall back to the second stage. In addition to
 * avoiding wasted work by attempting to recover from errors here, the empty
 * implementation of {@link BailErrorStrategy#sync} improves the performance of
 * the first stage.</li>
 * <li><strong>Silent validation:</strong> When syntax errors are not being
 * reported or logged, and the parse result is simply ignored if errors occur,
 * the {@link BailErrorStrategy} avoids wasting work on recovering from errors
 * when the result will be ignored either way.</li>
 * </ul>
 *
 * <p>
 * {@code myparser.setErrorHandler(new BailErrorStrategy());}</p>
 *
 * @see Parser#setErrorHandler(ANTLRErrorStrategy)
 */
public class BailErrorStrategy extends DefaultErrorStrategy {
    /** Instead of recovering from exception {@code e}, re-throw it wrapped
     *  in a {@link ParseCancellationException} so it is not caught by the
     *  rule function catches.  Use {@link Exception#getCause()} to get the
	 *  original {@link RecognitionException}.
     */
    @Override
    public void recover(Parser recognizer, RecognitionException e) {
		for (ParserRuleContext context = recognizer.getContext(); context != null; context = context.getParent()) {
			context.exception = e;
		}

        throw new ParseCancellationException(e);
    }

    /** Make sure we don't attempt to recover inline; if the parser
     *  successfully recovers, it won't throw an exception.
     */
    @Override
    public Token recoverInline(Parser recognizer)
        throws RecognitionException
    {
		InputMismatchException e = new InputMismatchException(recognizer);
		for (ParserRuleContext context = recognizer.getContext(); context != null; context = context.getParent()) {
			context.exception = e;
		}

        throw new ParseCancellationException(e);
    }

    /** Make sure we don't attempt to recover from problems in subrules. */
    @Override
    public void sync(Parser recognizer) { }
}

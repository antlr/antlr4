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

package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

/** Alter response to errors in rules so it checks for special RULE tokens
 *  representing patterns like <expr>.

 catch (RecognitionException re) {
         _localctx.exception = re;
 		 // DO NOTHING IF RULE TOKEN
         _errHandler.reportError(this, re);
         _errHandler.recover(this, re);
 }

 if it's a rule token, it'll cause a mismatch or no viable alt error
 immediately at start token of rule attempt.

 When parsing "x = <expr>;" pattern, we use nextTokenOrRuleToken() not
 nextToken() so <expr> is converted to RULE token instead of tokenizing.
 */

public class ParseTreePatternErrorStrategy extends DefaultErrorStrategy {

	public boolean isRuleToken(Token t) {
		return t.getType() == 33;
	}

	@Override
	public void reportError(Parser recognizer, RecognitionException e) {
		if ( isRuleToken(e.getOffendingToken()) ) {
			super.reportError(recognizer, e);
		}
	}

	@Override
	public void recover(Parser recognizer, RecognitionException e) {
		if ( isRuleToken(e.getOffendingToken()) ) {
			super.recover(recognizer, e);
		}
	}
}


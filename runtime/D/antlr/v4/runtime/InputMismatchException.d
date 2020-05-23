/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.InputMismatchException;

import antlr.v4.runtime.RecognitionException;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.Parser;
import antlr.v4.runtime.ParserRuleContext;

/**
 * This signifies any kind of mismatched input exceptions such as
 * when the current input does not match the expected token.
 */
class InputMismatchException : RecognitionException
{

    public this(Parser recognizer)
    {
	super(recognizer, recognizer.getInputStream, recognizer.ctx_);
        this.setOffendingToken(recognizer.getCurrentToken);
    }

    public this(Parser recognizer, int state, ParserRuleContext ctx)
    {
        super(recognizer, recognizer.getInputStream, ctx);
        this.setOffendingState(state);
        this.setOffendingToken(recognizer.getCurrentToken);
    }

}

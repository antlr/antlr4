/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.RecognitionException;

import antlr.v4.runtime.RuntimeException;
import antlr.v4.runtime.InterfaceRecognizer;
import antlr.v4.runtime.RuleContext;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.IntStream;
import antlr.v4.runtime.ParserRuleContext;
import antlr.v4.runtime.misc.IntervalSet;

/**
 * The root of the ANTLR exception hierarchy. In general, ANTLR tracks just
 * 3 kinds of errors: prediction errors, failed predicate errors, and
 * mismatched input errors. In each case, the parser knows where it is
 * in the input, where it is in the ATN, the rule invocation stack,
 * and what kind of problem occurred.
 */
class RecognitionException : RuntimeException
{

    /**
     * The {@link Recognizer} where this exception originated.
     */
    public InterfaceRecognizer recognizer;

    private RuleContext ctx;

    private IntStream input;

    /**
     * The current {@link Token} when an error occurred. Since not all streams
     * support accessing symbols by index, we have to track the {@link Token}
     * instance itself.
     */
    private Token offendingToken;

    private int offendingState = -1;

    public this(InterfaceRecognizer recognizer, IntStream input, ParserRuleContext ctx)
    {
        this.recognizer = recognizer;
        this.input = input;
        this.ctx = ctx;
        if (recognizer)
            this.offendingState = recognizer.getState;
    }

    public this(string message, InterfaceRecognizer recognizer, IntStream input, ParserRuleContext ctx)
    {
        super(message);
        this.recognizer = recognizer;
        this.input = input;
        this.ctx = ctx;
        if (recognizer)
            this.offendingState = recognizer.getState;
    }

    /**
     * Get the ATN state number the parser was in at the time the error
     * occurred. For {@link NoViableAltException} and
     * {@link LexerNoViableAltException} exceptions, this is the
     * {@link DecisionState} number. For others, it is the state whose outgoing
     * edge we couldn't match.
     *
     * <p>If the state number is not known, this method returns -1.</p>
     * @uml
     * Get the ATN state number the parser was in at the time the error
     * occurred. For {@link NoViableAltException} and
     * {@link LexerNoViableAltException} exceptions, this is the
     * {@link DecisionState} number. For others, it is the state whose outgoing
     * edge we couldn't match.
     *
     * <p>If the state number is not known, this method returns -1.</p>
     */
    public int getOffendingState()
    {
        return offendingState;
    }

    protected void setOffendingState(int offendingState)
    {
        this.offendingState = offendingState;
    }

    /**
     * Gets the set of input symbols which could potentially follow the
     * previously matched symbol at the time this exception was thrown.
     *
     * <p>If the set of expected tokens is not known and could not be computed,
     * this method returns {@code null}.</p>
     *
     *  @return The set of token types that could potentially follow the current
     * state in the ATN, or {@code null} if the information is not available.
     * @uml
     * Gets the set of input symbols which could potentially follow the
     * previously matched symbol at the time this exception was thrown.
     *
     * <p>If the set of expected tokens is not known and could not be computed,
     * this method returns {@code null}.</p>
     *
     *  @return The set of token types that could potentially follow the current
     * state in the ATN, or {@code null} if the information is not available.
     */
    public IntervalSet getExpectedTokens()
    {
        if (recognizer) {
            return recognizer.getATN().getExpectedTokens(offendingState, ctx);
        }
        return null;
    }

    /**
     * Gets the {@link RuleContext} at the time this exception was thrown.
     *
     * <p>If the context is not available, this method returns {@code null}.</p>
     *
     *  @return The {@link RuleContext} at the time this exception was thrown.
     * If the context is not available, this method returns {@code null}.
     * @uml
     * Gets the {@link RuleContext} at the time this exception was thrown.
     *
     * <p>If the context is not available, this method returns {@code null}.</p>
     *
     *  @return The {@link RuleContext} at the time this exception was thrown.
     * If the context is not available, this method returns {@code null}.
     */
    public RuleContext getCtx()
    {
        return ctx;
    }

    public IntStream getInputStream()
    {
        return input;
    }

    public Token getOffendingToken()
    {
        return offendingToken;
    }

    public void setOffendingToken(Token offendingToken)
    {
        this.offendingToken = offendingToken;
    }

    /**
     * Gets the {@link Recognizer} where this exception occurred.
     *
     * <p>If the recognizer is not available, this method returns {@code null}.</p>
     *
     *  @return The recognizer where this exception occurred, or {@code null} if
     *  the recognizer is not available.
     * @uml
     * Gets the {@link Recognizer} where this exception occurred.
     *
     * <p>If the recognizer is not available, this method returns {@code null}.</p>
     *
     *  @return The recognizer where this exception occurred, or {@code null} if
     *  the recognizer is not available.
     */
    public InterfaceRecognizer getRecognizer()
    {
        return recognizer;
    }

}

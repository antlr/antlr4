/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.ParserInterpreter;

import std.typecons;
import std.format;
import std.container : DList;
import antlr.v4.runtime.Parser;
import antlr.v4.runtime.CharStream;
import antlr.v4.runtime.InterpreterRuleContext;
import antlr.v4.runtime.ParserRuleContext;
import antlr.v4.runtime.RecognitionException;
import antlr.v4.runtime.FailedPredicateException;
import antlr.v4.runtime.UnsupportedOperationException;
import antlr.v4.runtime.InputMismatchException;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.TokenConstantDefinition;
import antlr.v4.runtime.TokenStream;
import antlr.v4.runtime.TokenSource;
import antlr.v4.runtime.Vocabulary;
import antlr.v4.runtime.VocabularyImpl;
import antlr.v4.runtime.atn.ATN;
import antlr.v4.runtime.atn.ATNState;
import antlr.v4.runtime.atn.AtomTransition;
import antlr.v4.runtime.atn.StateNames;
import antlr.v4.runtime.atn.StarLoopEntryState;
import antlr.v4.runtime.atn.ActionTransition;
import antlr.v4.runtime.atn.RuleTransition;
import antlr.v4.runtime.atn.PredicateTransition;
import antlr.v4.runtime.atn.PrecedencePredicateTransition;
import antlr.v4.runtime.atn.Transition;
import antlr.v4.runtime.atn.LoopEndState;
import antlr.v4.runtime.atn.ParserATNSimulator;
import antlr.v4.runtime.atn.RuleStartState;
import antlr.v4.runtime.atn.TransitionStates;
import antlr.v4.runtime.dfa.DFA;
import antlr.v4.runtime.atn.DecisionState;
import antlr.v4.runtime.atn.PredictionContextCache;

alias ParentContextPair = Tuple!(ParserRuleContext, "a", int, "b");
alias TokenFactorySourcePair = Tuple!(TokenSource, "a", CharStream, "b");

/**
 * @uml
 * A parser simulator that mimics what ANTLR's generated
 * parser code does. A ParserATNSimulator is used to make
 * predictions via adaptivePredict but this class moves a pointer through the
 * ATN to simulate parsing. ParserATNSimulator just
 * makes us efficient rather than having to backtrack, for example.
 *
 * This properly creates parse trees even for left recursive rules.
 *
 * We rely on the left recursive rule invocation and special predicate
 * transitions to make left recursive rules work.
 *
 * See TestParserInterpreter for examples.
 */
class ParserInterpreter : Parser
{

    protected string grammarFileName;

    protected ATN atn;

    /**
     * @uml
     * not shared like it is for generated parsers
     */
    protected DFA[] decisionToDFA;

    protected PredictionContextCache sharedContextCache ;

    protected string[] tokenNames;

    protected string[] ruleNames;

    private Vocabulary vocabulary;

    /**
     * @uml
     * This stack corresponds to the _parentctx, _parentState pair of locals
     * that would exist on call stack frames with a recursive descent parser;
     * in the generated function for a left-recursive rule you'd see:
     *
     *  private EContext e(int _p) throws RecognitionException {
     *      ParserRuleContext _parentctx = _ctx;    // Pair.a
     *      int _parentState = getState();          // Pair.b
     *      ...
     *   }
     *
     * Those values are used to create new recursive rule invocation contexts
     * associated with left operand of an alt like "expr '*' expr".
     */
    protected DList!ParentContextPair _parentContextStack;

    /**
     * @uml
     * We need a map from (decision,inputIndex)->forced alt for computing ambiguous
     * parse trees. For now, we allow exactly one override.
     */
    protected int overrideDecision = -1;

    protected int overrideDecisionInputIndex = -1;

    protected int overrideDecisionAlt = -1;

    /**
     * @uml
     * latch and only override once; error might trigger infinite loop
     */
    protected bool overrideDecisionReached = false;;

    /**
     * @uml
     * What is the current context when we override a decisions?
     * This tellsus what the root of the parse tree is when using override
     * for an ambiguity/lookahead check.
     */
    protected InterpreterRuleContext overrideDecisionRoot = null;

    protected InterpreterRuleContext rootContext;

    /**
     * @uml
     * deprecated Use {@link #ParserInterpreter(String, Vocabulary, Collection, ATN, TokenStream)} instead.
     */
    public this(string grammarFileName, string[] tokenNames, string[] ruleNames, ATN atn,
        TokenStream input)
    {
        this(grammarFileName,
             VocabularyImpl.fromTokenNames(tokenNames),
             ruleNames, atn, input);
    }

    public this(string grammarFileName, Vocabulary vocabulary, string[] ruleNames, ATN atn,
        TokenStream input)
    {
    super(input);
        this.grammarFileName = grammarFileName;
        this.atn = atn;
        for (int i = 0; i < tokenNames.length; i++) {
            tokenNames ~= vocabulary.getDisplayName(i);
        }

        this.ruleNames = ruleNames;
        this.vocabulary = vocabulary;

        // init decision DFA
        int numberOfDecisions = atn.getNumberOfDecisions();
        for (int i = 0; i < numberOfDecisions; i++) {
            DecisionState decisionState = atn.getDecisionState(i);
            decisionToDFA ~= new DFA(decisionState, i);
        }

        // get atn simulator that knows how to do predictions
        setInterpreter(new ParserATNSimulator(this, atn,
                                              decisionToDFA,
                                              sharedContextCache));
    }

    /**
     * @uml
     * @override
     */
    public override void reset()
    {
        super.reset();
        overrideDecisionReached = false;
        overrideDecisionRoot = null;
    }

    /**
     * @uml
     * @override
     */
    public override ATN getATN()
    {
        return atn;
    }

    /**
     * @uml
     * @override
     */
    public override string[] getTokenNames()
    {
        return tokenNames;
    }

    /**
     * @uml
     * @override
     */
    public override Vocabulary getVocabulary()
    {
        return vocabulary;
    }

    /**
     * @uml
     * @override
     */
    public override string[] getRuleNames()
    {
        return ruleNames;
    }

    /**
     * @uml
     * @override
     */
    public override string getGrammarFileName()
    {
        return grammarFileName;
    }

    /**
     * @uml
     * Begin parsing at startRuleIndex
     */
    public ParserRuleContext parse(int startRuleIndex)
    {
    RuleStartState startRuleStartState = atn.ruleToStartState[startRuleIndex];
        rootContext = createInterpreterRuleContext(null, ATNState.INVALID_STATE_NUMBER, startRuleIndex);
        if (startRuleStartState.isLeftRecursiveRule) {
            enterRecursionRule(rootContext, startRuleStartState.stateNumber, startRuleIndex, 0);
        }
        else {
            enterRule(rootContext, startRuleStartState.stateNumber, startRuleIndex);
        }

        while ( true ) {
            ATNState p = getATNState();
            switch ( p.getStateType() ) {
            case StateNames.RULE_STOP :
                // pop; return from rule
                if (ctx_.isEmpty() ) {
                    if (startRuleStartState.isLeftRecursiveRule) {
                        ParserRuleContext result = ctx_;
                        ParentContextPair parentContext = _parentContextStack.back;
                        _parentContextStack.removeBack();
                        unrollRecursionContexts(parentContext.a);
                        return result;
                    }
                    else {
                        exitRule();
                        return rootContext;
                    }
                }
                visitRuleStopState(p);
                break;
            default :
                try {
                    visitState(p);
                }
                catch (RecognitionException e) {
                    setState(atn.ruleToStopState[p.ruleIndex].stateNumber);
                    ctx_.exception = e;
                    getErrorHandler.reportError(this, e);
                    recover(e);
                }

                     break;
            }
        }
    }

    /**
     * @uml
     * @override
     */
    public override void enterRecursionRule(ParserRuleContext localctx, int state, int ruleIndex,
        int precedence)
    {
        ParentContextPair pair = tuple(ctx_, localctx.invokingState);
        _parentContextStack.insert(pair);
        super.enterRecursionRule(localctx, state, ruleIndex, precedence);
    }

    protected ATNState getATNState()
    {
        return atn.states[getState];
    }

    protected void visitState(ATNState p)
    {
        //      System.out.println("visitState "+p.stateNumber);
        int predictedAlt = 1;
        if (cast(DecisionState)p) {
            predictedAlt = visitDecisionState(cast(DecisionState)p);
        }

        Transition transition = p.transition(predictedAlt - 1);
        switch (transition.getSerializationType()) {
        case TransitionStates.EPSILON:
            if ( p.getStateType()== StateNames.STAR_LOOP_ENTRY &&
                 (cast(StarLoopEntryState)p).isPrecedenceDecision &&
                 !cast(LoopEndState)transition.target)
                {
                    // We are at the start of a left recursive rule's (...)* loop
                    // and we're not taking the exit branch of loop.
                    InterpreterRuleContext localctx =
                        createInterpreterRuleContext(_parentContextStack.front.a,
                                                     _parentContextStack.front.b,
                                                     ctx_.getRuleIndex());
                    pushNewRecursionContext(localctx,
                                            atn.ruleToStartState[p.ruleIndex].stateNumber,
                                            ctx_.getRuleIndex());
                }
            break;
        case TransitionStates.ATOM:
            match((cast(AtomTransition)transition)._label);
            break;
        case TransitionStates.RANGE:
        case TransitionStates.SET:
        case TransitionStates.NOT_SET:
            if (!transition.matches(_input.LA(1), TokenConstantDefinition.MIN_USER_TOKEN_TYPE, 65535)) {
                recoverInline();
            }
            matchWildcard();
            break;

        case TransitionStates.WILDCARD:
            matchWildcard();
            break;

        case TransitionStates.RULE:
            RuleStartState ruleStartState = cast(RuleStartState)transition.target;
            int ruleIndex = ruleStartState.ruleIndex;
            InterpreterRuleContext newctx = createInterpreterRuleContext(ctx_, p.stateNumber, ruleIndex);
            if (ruleStartState.isLeftRecursiveRule) {
                enterRecursionRule(newctx, ruleStartState.stateNumber, ruleIndex, (cast(RuleTransition)transition).precedence);
            }
            else {
                enterRule(newctx, transition.target.stateNumber, ruleIndex);
            }
            break;

        case TransitionStates.PREDICATE:
            PredicateTransition predicateTransition = cast(PredicateTransition)transition;
            if (!sempred(ctx_, predicateTransition.ruleIndex, predicateTransition.predIndex)) {
                throw new FailedPredicateException(this);
            }

            break;
        case TransitionStates.ACTION:
            ActionTransition actionTransition = cast(ActionTransition)transition;
            action(ctx_, actionTransition.ruleIndex, actionTransition.actionIndex);
            break;
        case TransitionStates.PRECEDENCE:
            if (!precpred(ctx_, (cast(PrecedencePredicateTransition)transition).precedence)) {
                throw new FailedPredicateException(this, format("precpred(ctx_, %d)", (cast(PrecedencePredicateTransition)transition).precedence));
            }
            break;
        default:
            throw new UnsupportedOperationException("Unrecognized ATN transition type.");
        }
        setState(transition.target.stateNumber);
    }

    protected int visitDecisionState(DecisionState p)
    {
    int predictedAlt = 1;
        if (p.getNumberOfTransitions() > 1) {
            getErrorHandler.sync(this);
            int decision = p.decision;
            if (decision == overrideDecision && _input.index() == overrideDecisionInputIndex &&
                !overrideDecisionReached )
                {
                    predictedAlt = overrideDecisionAlt;
                    overrideDecisionReached = true;
                }
            else {
                predictedAlt = getInterpreter().adaptivePredict(_input, decision, ctx_);
            }
        }
        return predictedAlt;
    }

    /**
     * @uml
     * Provide simple "factory" for InterpreterRuleContext's.
     */
    protected InterpreterRuleContext createInterpreterRuleContext(ParserRuleContext parent,
        int invokingStateNumber, size_t ruleIndex)
    {
        return new InterpreterRuleContext(parent, invokingStateNumber, ruleIndex);
    }

    protected void visitRuleStopState(ATNState p)
    {
        RuleStartState ruleStartState = atn.ruleToStartState[p.ruleIndex];
        if (ruleStartState.isLeftRecursiveRule) {
            ParentContextPair parentContext = _parentContextStack.back;
            _parentContextStack.removeBack;
            unrollRecursionContexts(parentContext.a);
            setState(parentContext.b);
        }
        else {
            exitRule;
        }

        RuleTransition ruleTransition = cast(RuleTransition)atn.states[getState].transition(0);
        setState(ruleTransition.followState.stateNumber);
    }

    /**
     * @uml
     * Override this parser interpreters normal decision-making process
     * at a particular decision and input token index. Instead of
     * allowing the adaptive prediction mechanism to choose the
     * first alternative within a block that leads to a successful parse,
     * force it to take the alternative, 1..n for n alternatives.
     *
     * As an implementation limitation right now, you can only specify one
     * override. This is sufficient to allow construction of different
     * parse trees for ambiguous input. It means re-parsing the entire input
     * in general because you're never sure where an ambiguous sequence would
     * live in the various parse trees. For example, in one interpretation,
     * an ambiguous input sequence would be matched completely in expression
     * but in another it could match all the way back to the root.
     *
     * s : e '!'? ;
     * e : ID
     *   | ID '!'
     *   ;
     *
     * Here, x! can be matched as (s (e ID) !) or (s (e ID !)). In the first
     * case, the ambiguous sequence is fully contained only by the root.
     * In the second case, the ambiguous sequences fully contained within just
     * e, as in: (e ID !).
     *
     * Rather than trying to optimize this and make
     * some intelligent decisions for optimization purposes, I settled on
     * just re-parsing the whole input and then using
     * {link Trees#getRootOfSubtreeEnclosingRegion} to find the minimal
     * subtree that contains the ambiguous sequence. I originally tried to
     * record the call stack at the point the parser detected and ambiguity but
     * left recursive rules create a parse tree stack that does not reflect
     * the actual call stack. That impedance mismatch was enough to make
     * it it challenging to restart the parser at a deeply nested rule
     * invocation.
     *
     * Only parser interpreters can override decisions so as to avoid inserting
     * override checking code in the critical ALL(*) prediction execution path.
     */
    public void addDecisionOverride(int decision, int tokenIndex, int forcedAlt)
    {
        overrideDecision = decision;
        overrideDecisionInputIndex = tokenIndex;
        overrideDecisionAlt = forcedAlt;
    }

    public InterpreterRuleContext getOverrideDecisionRoot()
    {
        return overrideDecisionRoot;
    }

    /**
     * @uml
     * Rely on the error handler for this parser but, if no tokens are consumed
     * to recover, add an error node. Otherwise, nothing is seen in the parse
     * tree.
     */
    protected void recover(RecognitionException e)
    {
        TokenFactorySourcePair tokenFactorySourcePair;
        auto i = _input.index();
        getErrorHandler.recover(this, e);
        if ( _input.index()==i ) {
            // no input consumed, better add an error node
            if (cast(InputMismatchException)e) {
                InputMismatchException ime = cast(InputMismatchException)e;
                Token tok = e.getOffendingToken();
                int expectedTokenType = ime.getExpectedTokens().getMinElement(); // get any element
                tokenFactorySourcePair = tuple(tok.getTokenSource(), tok.getTokenSource().getInputStream());
                auto errToken =
                    tokenFactory().create(tokenFactorySourcePair,
                                             expectedTokenType, tok.getText(),
                                             TokenConstantDefinition.DEFAULT_CHANNEL,
                                             -1, -1, // invalid start/stop
                                             tok.getLine(), tok.getCharPositionInLine());
                ctx_.addErrorNode(errToken);
            }
            else { // NoViableAlt
                auto tok = e.getOffendingToken;
                tokenFactorySourcePair = tuple(tok.getTokenSource(), tok.getTokenSource().getInputStream());
                auto errToken =
                    tokenFactory().create(tokenFactorySourcePair,
                                             TokenConstantDefinition.INVALID_TYPE, tok.getText(),
                                             TokenConstantDefinition.DEFAULT_CHANNEL,
                                             -1, -1, // invalid start/stop
                                             tok.getLine(), tok.getCharPositionInLine());
                ctx_.addErrorNode(errToken);
            }
        }
    }

    protected Token recoverInline()
    {
        return _errHandler.recoverInline(this);
    }

    /**
     * @uml
     * Return the root of the parse, which can be useful if the parser
     * bails out. You still can access the top node. Note that,
     * because of the way left recursive rules add children, it's possible
     * that the root will not have any children if the start rule immediately
     * called and left recursive rule that fails.
     */
    public InterpreterRuleContext getRootContext()
    {
        return rootContext;
    }

}

/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.Recognizer;

import std.stdio;
import std.algorithm;
import std.array;
import std.conv;
import antlr.v4.runtime.ANTLRErrorListener;
import antlr.v4.runtime.ConsoleErrorListener;
import antlr.v4.runtime.InterfaceRuleContext;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.TokenConstantDefinition;
import antlr.v4.runtime.IntStream;
import antlr.v4.runtime.InterfaceRecognizer;
import antlr.v4.runtime.UnsupportedOperationException;
import antlr.v4.runtime.RecognitionException;
import antlr.v4.runtime.TokenFactory;
import antlr.v4.runtime.CommonToken;
import antlr.v4.runtime.ProxyErrorListener;
import antlr.v4.runtime.Vocabulary;
import antlr.v4.runtime.VocabularyImpl;
import antlr.v4.runtime.atn.ATN;
import antlr.v4.runtime.atn.ParseInfo;

/**
 * Base for Lexer and Parser
 */
abstract class Recognizer(U, V) : InterfaceRecognizer
{

    enum int EOF = -1;

    public int[string][Vocabulary] tokenTypeMapCache;

    public int[string][string] ruleIndexMapCache;

    public ANTLRErrorListener!(U,V)[] _listeners;

    protected V _interp;

    private int _stateNumber = -1;

    public this()
    {
        _listeners = [ConsoleErrorListener!(U,V).instance];
    }

    /**
     * Used to print out token names like ID during debugging and
     * error reporting.  The generated parsers implement a method
     * that overrides this to point to their String[] tokenNames.
     *
     *  @deprecated Use {@link #getVocabulary()} instead.
     */
    abstract public string[] getTokenNames();

    abstract public string[] getRuleNames();

    /**
     * Get the vocabulary used by the recognizer.
     *
     *  @return A {@link Vocabulary} instance providing information about the
     *  vocabulary used by the grammar.
     */
    public Vocabulary getVocabulary()
    {
        return VocabularyImpl.fromTokenNames(getTokenNames());
    }

    /**
     * Get a map from token names to token types.
     * <p>Used for XPath and tree pattern compilation.</p>
     */
    public int[string] getTokenTypeMap()
    {
        Vocabulary vocabulary = getVocabulary;
        int[string] result = tokenTypeMapCache[vocabulary];
        if (result is null) {
            int[string] result_mapping;
            result = result_mapping;
            for (int i = 0; i < getATN.maxTokenType; i++) {
                string literalName = vocabulary.getLiteralName(i);
                if (literalName !is null) {
                    result[literalName] = i;
                }

                string symbolicName = vocabulary.getSymbolicName(i);
                if (symbolicName != null) {
                    result[symbolicName] = i;
                }
            }

            result["EOF"] = TokenConstantDefinition.EOF;
            result.rehash; // for faster lookups
            tokenTypeMapCache[vocabulary] = result;
        }
        return result;
    }

    /**
     * Get a map from rule names to rule indexes.
     *
     * <p>Used for XPath and tree pattern compilation.
     */
    public int[string] getRuleIndexMap()
    {
        string[] ruleNames = getRuleNames();
        if (!ruleNames) {
            throw new UnsupportedOperationException("The current recognizer does not provide a list of rule names.");
        }
        int[string] result;
        if (to!string(joiner(ruleNames, ",")) in ruleIndexMapCache) {
            result = ruleIndexMapCache[to!string(joiner(ruleNames, ","))];
        }
        else {
            foreach (i, rn; ruleNames) {
                result[rn] = to!int(i);
            }
            ruleIndexMapCache[to!string(joiner(ruleNames, ","))] = result;
        }
        return result;
    }

    public int getTokenType(string tokenName)
    {
        int ttype = getTokenTypeMap()[tokenName];
        if (ttype) return ttype;
        return TokenConstantDefinition.INVALID_TYPE;
    }

    /**
     * If this recognizer was generated, it will have a serialized ATN
     * representation of the grammar.
     *
     * <p>For interpreters, we don't know their serialized ATN despite having
     * created the interpreter from it.</p>
     */
    public wstring getSerializedATN()
    {
        throw new UnsupportedOperationException("there is no serialized ATN");
    }

    /**
     * For debugging and other purposes, might want the grammar name.
     * Have ANTLR generate an implementation for this method.
     */
    abstract public string getGrammarFileName();

    /**
     * Get the {@link ATN} used by the recognizer for prediction.
     *
     *  @return The {@link ATN} used by the recognizer for prediction.
     */
    abstract public ATN getATN();

    /**
     * Get the ATN interpreter used by the recognizer for prediction.
     *
     *  @return The ATN interpreter used by the recognizer for prediction.
     */
    public V getInterpreter()
    {
        return _interp;
    }

    /**
     * If profiling during the parse/lex, this will return DecisionInfo records
     * for each decision in recognizer in a ParseInfo object.
     */
    public ParseInfo getParseInfo()
    {
        return null;
    }

    public void setInterpreter(V interpreter)
    {
        _interp = interpreter;
    }

    /**
     * What is the error header, normally line/character position information?
     */
    public string getErrorHeader(RecognitionException e)
    {
        int line = e.getOffendingToken().getLine();
        int charPositionInLine = e.getOffendingToken().getCharPositionInLine();
        return "line " ~ to!string(line) ~ ":" ~ to!string(charPositionInLine);
    }

    /**
     * How should a token be displayed in an error message? The default
     * is to display just the text, but during development you might
     * want to have a lot of information spit out.  Override in that case
     * to use t.toString() (which, for CommonToken, dumps everything about
     * the token). This is better than forcing you to override a method in
     * your token objects because you don't have to go modify your lexer
     * so that it creates a new Java type.
     *
     *  @deprecated This method is not called by the ANTLR 4 Runtime. Specific
     * implementations of {@link ANTLRErrorStrategy} may provide a similar
     * feature when necessary. For example, see
     * {@link DefaultErrorStrategy#getTokenErrorDisplay}.
     */
    public string getTokenErrorDisplay(Token t)
    {
	if (t is null) return "<no token>";
        string s = to!string(t.getText);
        if (s is null) {
            if (t.getType() == TokenConstantDefinition.EOF) {
                s = "<EOF>";
            }
            else {
                s = "<" ~ to!string(t.getType) ~ ">";
            }
        }
        s = s.replace("\n","\\n");
        s = s.replace("\r","\\r");
        s = s.replace("\t","\\t");
        return "'" ~ s ~ "'";
    }

    public void addErrorListener(ANTLRErrorListener!(U, V) listener)
    {
	if (listener is null) {
            assert(0, "listener cannot be null.");
        }

       	_listeners ~= listener;
    }

    public void removeErrorListener(ANTLRErrorListener!(U, V) listener)
    {
        foreach (elementRemoveIndex, el; _listeners) {
            if (listener is el) {
                _listeners.remove(to!int(elementRemoveIndex));
                break;
            }
        }
    }

    public void removeErrorListeners()
    {
        _listeners.length = 0;
    }

    public ANTLRErrorListener!(U,V)[] getErrorListeners()
    {
        return _listeners;
    }

    public ANTLRErrorListener!(U, V) getErrorListenerDispatch()
    {
        return new ProxyErrorListener!(U, V)(getErrorListeners());
    }

    /**
     * subclass needs to override these if there are sempreds or actions
     * that the ATN interp needs to execute
     */
    public bool sempred(InterfaceRuleContext localctx, int ruleIndex, int actionIndex)
    {
        return true;
    }

    public bool precpred(InterfaceRuleContext localctx, int precedence)
    {
        return true;
    }

    public void action(InterfaceRuleContext localctx, int ruleIndex, int actionIndex)
    {
    }

    /**
     * @uml
     * @final
     */
    public final int getState()
    {
        return _stateNumber;
    }

    /**
     * Indicate that the recognizer has changed internal state that is
     * consistent with the ATN state passed in.  This way we always know
     * where we are in the ATN as the parser goes along. The rule
     * context objects form a stack that lets us see the stack of
     * invoking rules. Combine this and we have complete ATN
     * configuration information.
     * @uml
     * @final
     */
    public final void setState(int atnState)
    {
        _stateNumber = atnState;
    }

    abstract public IntStream getInputStream();

    abstract public void setInputStream(IntStream input);

    abstract public TokenFactory!CommonToken tokenFactory();

    abstract public void tokenFactory(TokenFactory!CommonToken input);

}

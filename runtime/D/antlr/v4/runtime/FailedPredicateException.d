module antlr.v4.runtime.FailedPredicateException;

import std.format;
import antlr.v4.runtime.RecognitionException;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.Parser;
import antlr.v4.runtime.atn.ATNState;
import antlr.v4.runtime.atn.AbstractPredicateTransition;
import antlr.v4.runtime.atn.PredicateTransition;

/**
 * TODO add class description
 */
class FailedPredicateException : RecognitionException
{

    private int ruleIndex;

    private int predicateIndex;

    private string predicate;

    public this(Parser recognizer)
    {
	this(recognizer, null);
    }

    public this(Parser recognizer, string predicate)
    {
        this(recognizer, predicate, null);
    }

    public this(Parser recognizer, string predicate, string message)
    {
	super(formatMessage(predicate, message), recognizer, recognizer.getInputStream(), recognizer.ctx_);
        ATNState s = recognizer.getInterpreter().atn.states[recognizer.getState];

        AbstractPredicateTransition trans = cast(AbstractPredicateTransition)s.transition(0);
        if (trans.classinfo == PredicateTransition.classinfo) {
            this.ruleIndex = (cast(PredicateTransition)trans).ruleIndex;
            this.predicateIndex = (cast(PredicateTransition)trans).predIndex;
        }
        else {
            this.ruleIndex = 0;
            this.predicateIndex = 0;
        }

        this.predicate = predicate;
        this.setOffendingToken(recognizer.getCurrentToken());

    }

    public int getRuleIndex()
    {
	return ruleIndex;
    }

    public int getPredIndex()
    {
        return predicateIndex;
    }

    public string getPredicate()
    {
        return predicate;
    }

    private static string formatMessage(string predicate, string message)
    {
        if (message !is null) {
            return message;
        }
        return format("failed predicate: {%s}?", predicate);
    }

}

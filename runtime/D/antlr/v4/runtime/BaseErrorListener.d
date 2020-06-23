module antlr.v4.runtime.BaseErrorListener;

import antlr.v4.runtime.ANTLRErrorListener;
import antlr.v4.runtime.InterfaceRecognizer;
import antlr.v4.runtime.RecognitionException;
import antlr.v4.runtime.InterfaceParser;
import antlr.v4.runtime.dfa.DFA;
import antlr.v4.runtime.atn.ATNConfigSet;
import antlr.v4.runtime.misc.BitSet;

/**
 * Provides an empty default implementation of {@link ANTLRErrorListener}. The
 * default implementation of each method does nothing, but can be overridden as
 * necessary.
 */
class BaseErrorListener(U, V) : ANTLRErrorListener!(U, V)
{

    public void syntaxError(InterfaceRecognizer recognizer, Object offendingSymbol, int line,
        int charPositionInLine, string msg, RecognitionException e)
    {
    }

    public void reportAmbiguity(InterfaceParser recognizer, DFA dfa, size_t startIndex, size_t stopIndex,
        bool exact, BitSet ambigAlts, ATNConfigSet configs)
    {
    }

    public void reportAttemptingFullContext(InterfaceParser recognizer, DFA dfa, size_t startIndex, size_t stopIndex,
        BitSet conflictingAlts, ATNConfigSet configs)
    {
    }

    public void reportContextSensitivity(InterfaceParser recognizer, DFA dfa, size_t startIndex, size_t stopIndex,
        int prediction, ATNConfigSet configs)
    {
    }

}

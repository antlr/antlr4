module antlr.v4.runtime.tree.xpath.XPathLexerErrorListener;

import antlr.v4.runtime.BaseErrorListener;
import antlr.v4.runtime.InterfaceRecognizer;
import antlr.v4.runtime.RecognitionException;
import antlr.v4.runtime.atn.LexerATNSimulator;

/**
 * TODO add class description
 */
class XPathLexerErrorListener : BaseErrorListener!(int, LexerATNSimulator)
{

    /**
     * @uml
     * @override
     */
    public override void syntaxError(InterfaceRecognizer recognizer, Object offendingSymbol,
        int line, int charPositionInLine, string msg, RecognitionException e)
    {
    }

}

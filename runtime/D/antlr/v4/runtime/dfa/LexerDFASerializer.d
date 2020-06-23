module antlr.v4.runtime.dfa.LexerDFASerializer;

import std.conv;
import antlr.v4.runtime.dfa.DFA;
import antlr.v4.runtime.dfa.DFASerializer;
import antlr.v4.runtime.VocabularyImpl;

/**
 * TODO add class description
 */
class LexerDFASerializer : DFASerializer
{

    public this(DFA dfa)
    {
	super(dfa, new VocabularyImpl(null, null, null));
    }

    /**
     * @uml
     * @override
     */
    public override string getEdgeLabel(int i)
    {
        import std.format : format;
        return format!"'%s'"(cast(char)i);
    }

}

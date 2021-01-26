import org.antlr.v4.runtime.*;

public abstract class ParserBase extends Parser
{
//    public ParserBase(TokenStream input, TextWriter output, TextWriter errorOutput)
//    {
//	super(input);
//    }

    public ParserBase(TokenStream input)
    {
	super(input);
    }
}

import org.antlr.v4.runtime.*;

public abstract class LexerBase extends Lexer
{
	public LexerBase(CharStream input)
	{
	    super(input);
	}

	public boolean testIsJavaIdentifierStart1()
	{
		return Character.isJavaIdentifierStart(_input.LA(-1));
	}

	public boolean testIsJavaIdentifierStart2()
	{
		return Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2),(char)_input.LA(-1)));
	}

	public boolean testIsJavaIdentifierStart3()
	{
		return Character.isJavaIdentifierPart(_input.LA(-1));
	}

	public boolean testIsJavaIdentifierStart4()
	{
		return Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2),(char)_input.LA(-1)));
	}
}


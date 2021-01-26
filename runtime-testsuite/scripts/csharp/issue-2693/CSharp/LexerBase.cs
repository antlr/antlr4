using System;
using System.IO;
using System.Text;
using Antlr4.Runtime;
using System.Linq;
using System.Text.RegularExpressions;

public abstract class LexerBase : Lexer
{
	public LexerBase(ICharStream input)
			: base(input)
	{
	}

	protected LexerBase(ICharStream input, TextWriter output, TextWriter errorOutput)
			: base(input, output, errorOutput)
	{
	}

	public bool testIsJavaIdentifierStart1()
	{
		return Character.isJavaIdentifierStart(this.InputStream.LA(-1));
	}

	public bool testIsJavaIdentifierStart2()
	{
		return Character.isJavaIdentifierStart(Character.toCodePoint((char)this.InputStream.LA(-2),(char)this.InputStream.LA(-1)));
	}

	public bool testIsJavaIdentifierStart3()
	{
		return Character.isJavaIdentifierPart(this.InputStream.LA(-1));
	}

	public bool testIsJavaIdentifierStart4()
	{
		return Character.isJavaIdentifierPart(Character.toCodePoint((char)this.InputStream.LA(-2),(char)this.InputStream.LA(-1)));
	}

	public class Character
	{
		public static bool isJavaIdentifierPart(int c)
		{
			if (Char.IsLetter((char)c))
				return true;
			else if (c == (int)'$')
				return true;
			else if (c == (int)'_')
				return true;
			else if (Char.IsDigit((char)c))
				return true;
			else if (Char.IsNumber((char)c))
				return true;
			return false;
		}

		public static bool isJavaIdentifierStart(int c)
		{
			if (Char.IsLetter((char)c))
				return true;
			else if (c == (int)'$')
				return true;
			else if (c == (int)'_')
				return true;
			return false;
		}

		public static int toCodePoint(int high, int low)
		{
			return Char.ConvertToUtf32((char)high, (char)low);
		}
	}
}


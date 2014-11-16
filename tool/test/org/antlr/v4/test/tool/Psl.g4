grammar Psl;

@parser::members
{
	public void printPosition(String name, Token tok)
	{
		System.out.printf("%s: pos %d, len %d%n",
				name, tok.getCharPositionInLine(), tok.getText().length());
	}


	/**
	 * Checks whether a set of digit groups and commas construct
	 * a valid command-number.
	 *
	 * @param digits
	 *		The groups of digits, each group in a separate item.
	 * @param commas
	 *		The commas found separating the digit groups.
	 *
	 * There should be one more digit group than commas.
	 * There should be no internal white space.
	 *
	 * @returns true (valid), false (invalid)
	 */

	public boolean isValidCommaNumber(List<Token> digits, List<Token> commas)
	{
		Token[]	aDigits = new Token[0];
		Token[]	aCommas = new Token[0];
		int		j;

		aDigits = digits.toArray(aDigits);
		aCommas = commas.toArray(aCommas);
		if (aDigits.length != aCommas.length + 1)
		{
			return false;
		}
		for (j = 0; j < aCommas.length; ++j)
		{
			int	p1, p2, p3;
			p1 = aDigits[j].getCharPositionInLine()
					+ aDigits[j].getText().length();
			p2 = aCommas[j].getCharPositionInLine();
			p3 = aDigits[j + 1].getCharPositionInLine();
			if (p1 != p2 || (p2 + 1) != p3)
			{
				return false;
			}
		}
		return true;
	}


	/**
	 * Checks whether a the pieces of a floating-point number
	 * construct a valid number.
	 *
	 * @param whole
	 *		The whole part of the number.  Can be null.
	 * @param period
	 *		The decimal point.
	 * @param fraction
	 *		The fraction part of the number.  Can be null.
	 *
	 * At least one of the whole or fraction must be present.
	 * The decimal point is required.
	 *
	 * @returns true (valid), false (invalid)
	 */

	public boolean isValidFloatingConstant(
		Token whole,
		Token period,
		Token fraction
	)
	{
		boolean		foundDigits = false;
		int			column;

		if (whole != null)
		{
			foundDigits = true;
			column = whole.getCharPositionInLine()
					+ whole.getText().length();
			if (column != period.getCharPositionInLine())
			{
				return false;
			}
		}
		if (fraction != null)
		{
			foundDigits = true;
			column = period.getCharPositionInLine() + 1;
			if (column != fraction.getCharPositionInLine())
			{
				return false;
			}
		}
		return foundDigits;
	}
}

translation_unit
	:	numeric_range
		EOF
	;

pattern
	:	numeric_range
	;

numeric_range
	:	EURO_NUMBER
		PAREN_LEFT
		numeric_endpoint
		TILDE
		numeric_endpoint
		PAREN_RIGHT
	|	NUMBER
		PAREN_LEFT
		numeric_endpoint
		TILDE
		numeric_endpoint
		PAREN_RIGHT
	;

numeric_endpoint
	:	( PLUS | MINUS )? integer_constant
	|	( PLUS | MINUS )? floating_constant
	|	( PLUS | MINUS )? comma_number
	;

	/* Floating-point numbers and comma numbers are valid only
	 * as numeric endpoints in number() or euro_number().  Otherwise,
	 * the pieces should be parsed as separate lexical tokens, such as
	 *
	 *	integer_constant '.' integer_constant
	 *
	 * Because of parser lookahead and the subtle interactions between
	 * the parser and the lexer, changing lexical modes from the parser
	 * is not safe.  The code below checks the constraints for floating
	 * numbers, forbidding internal white space.
	 */

floating_constant
	:	comma_number PERIOD fraction=DIGIT_SEQUENCE?
		{
			isValidFloatingConstant($comma_number.stop, $PERIOD, $fraction)
		}?<fail = {
			"COMMA:A floating-point constant cannot have internal white space"
		}>

	/*|	whole=DIGIT_SEQUENCE PERIOD fraction=DIGIT_SEQUENCE?
		{
			isValidFloatingConstant($whole, $PERIOD, $fraction)
		}?/* <fail = {
			"DIG:A floating-point constant cannot have internal white space"
		}>*/

	|	PERIOD fraction=DIGIT_SEQUENCE
		{
			isValidFloatingConstant(null, $PERIOD, $fraction)
		}?<fail = {
			"DEC:A floating-point constant cannot have internal white space"
		}>
	;

comma_number
	:	digits+=DIGIT_SEQUENCE ( commas+=COMMA digits+=DIGIT_SEQUENCE )+
		{
			isValidCommaNumber($digits, $commas)
		}?<fail = {
			"A comma-number cannot have internal white space"
		}>
	;

term_expression
	:	term
	|	RETURN
		(
			PAREN_LEFT
			( integer_constant | ALL )
			PAREN_RIGHT
		)?
		term
	;

term
	:	pattern
	|	PAREN_LEFT term_expression PAREN_RIGHT
	;

integer_constant
	:	DIGIT_SEQUENCE
	|	INTEGER_CONSTANT
	|	BINARY_CONSTANT
	|	DECIMAL_CONSTANT
	|	HEXADECIMAL_CONSTANT
	|	OCTAL_CONSTANT
	;

// LEXER

/* Letter fragments
 */

fragment A: [Aa] ;
fragment B: [BB] ;
fragment C: [Cc] ;
fragment D: [Dd] ;
fragment E: [Ee] ;
fragment F: [Ff] ;
fragment G: [Gg] ;
fragment H: [Hh] ;
fragment I: [Ii] ;
fragment J: [Jj] ;
fragment K: [Kk] ;
fragment L: [Ll] ;
fragment M: [Mm] ;
fragment N: [Nn] ;
fragment O: [Oo] ;
fragment P: [Pp] ;
fragment Q: [Qq] ;
fragment R: [Rr] ;
fragment S: [Ss] ;
fragment T: [Tt] ;
fragment U: [Uu] ;
fragment V: [Vv] ;
fragment W: [Ww] ;
fragment X: [Xx] ;
fragment Y: [Yy] ;
fragment Z: [Zz] ;


WHITESPACE_IN_LINE
	:	[ \t]+
		-> skip
	;

NEWLINE
	:	'\r'? '\n'
		-> skip
	;

WHITESPACE_ALL
	:	[ \n\r\t]+
		-> skip
	;


	/* A sequence of decimal digits is useful on its own,
	 * to avoid the base-prefixes (0b, 0x, ...) that an
	 * INTEGER_CONTANT would allow.
	 * Need to define before INTEGER_CONSTANT to make sure
	 * DIGIT_SEQUENCE is recognized before INTEGER_CONSTANT.
	 */

DIGIT_SEQUENCE
	:	[0-9]+
	;

INTEGER_CONSTANT
	:	BINARY_CONSTANT
	|	DECIMAL_CONSTANT
	|	HEXADECIMAL_CONSTANT
	|	OCTAL_CONSTANT
	;

BINARY_CONSTANT
	:	'0' [Bb] [0-1]+
	;

DECIMAL_CONSTANT
	:	( '0' [Dd] )? [0-9]+
	;

HEXADECIMAL_CONSTANT
	:	'0' [HhXx] [0-9a-fA-F]+
	;

OCTAL_CONSTANT
	:	'0' [Oo] [0-7]+
	;

/*	keywords
 */

ALL
	:	A L L
	;

EURO_NUMBER
	:	E U R O '_' N U M B E R
	;


NUMBER
	:	N U M B E R
	;

RETURN
	:	R E T U R N
	;

IDENTIFIER
	:	[A-Za-z][A-Za-z0-9_]*
	;


/* The single-character tokens.
 */

COMMA
	:	','
	;

MINUS
	:	'-'
	;

PAREN_LEFT
	:	'('
	;

PAREN_RIGHT
	:	')'
	;

PERIOD
	:	'.'
	;

PLUS
	:	'+'
	;

TILDE
	:	'~'
	;

	/* This rule must be last (or nearly last) to avoid
	 * matching individual characters for other rules.
	 */

ANY_CHAR_BUT_NEWLINE
	:	~[\n\r]
	;

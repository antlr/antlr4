package org.antlr.v4.automata;

import org.antlr.runtime.Token;

/** A state machine transition label.  A label can be either a simple
 *  label such as a token or character.  A label can be a set of char or
 *  tokens.  It can be an epsilon transition.  It can be a semantic predicate
 *  (which assumes an epsilon transition) or a tree of predicates (in a DFA).
 *  Special label types have to be < 0 to avoid conflict with char.
 */
public abstract class Label implements /*Comparable, */ Cloneable {
    public static final int INVALID = -7;

//	public static final int ACTION = -6;

	//public static final int EPSILON = -5;

    //public static final String EPSILON_STR = "<EPSILON>";

    /** label is a semantic predicate; implies label is epsilon also */
//    public static final int SEMPRED = -4;

    /** label is a set of tokens or char */
//    public static final int SET = -3;

    /** End of Token is like EOF for lexer rules.  It implies that no more
     *  characters are available and that NFA conversion should terminate
     *  for this path.  For example
     *
     *  A : 'a' 'b' | 'a' ;
     *
     *  yields a DFA predictor:
     *
     *  o-a->o-b->1   predict alt 1
     *       |
     *       |-EOT->o predict alt 2
     *
     *  To generate code for EOT, treat it as the "default" path, which
     *  implies there is no way to mismatch a char for the state from
     *  which the EOT emanates.
     */
    public static final int EOT = -2;

    public static final int EOF = -1;

	/** We have labels like EPSILON that are below 0; it's hard to
	 *  store them in an array with negative index so use this
	 *  constant as an index shift when accessing arrays based upon
	 *  token type.  If real token type is i, then array index would be
	 *  NUM_FAUX_LABELS + i.
	 */
	public static final int NUM_FAUX_LABELS = -INVALID;

    /** Anything at this value or larger can be considered a simple atom int
     *  for easy comparison during analysis only; faux labels are not used
	 *  during parse time for real token types or char values.
     */
    public static final int MIN_ATOM_VALUE = EOT;

    public static final int MIN_CHAR_VALUE = '\u0000';
    public static final int MAX_CHAR_VALUE = '\uFFFE';

	/** End of rule token type; imaginary token type used only for
	 *  local, partial FOLLOW sets to indicate that the local FOLLOW
	 *  hit the end of rule.  During error recovery, the local FOLLOW
	 *  of a token reference may go beyond the end of the rule and have
	 *  to use FOLLOW(rule).  I have to just shift the token types to 2..n
	 *  rather than 1..n to accommodate this imaginary token in my bitsets.
	 *  If I didn't use a bitset implementation for runtime sets, I wouldn't
	 *  need this.  EOF is another candidate for a run time token type for
	 *  parsers.  Follow sets are not computed for lexers so we do not have
	 *  this issue.
	 */
	public static final int EOR_TOKEN_TYPE = Token.EOR_TOKEN_TYPE;

	public int compareTo(Object o) {
		return 0; // TODO: impl
	}
}
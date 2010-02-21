package org.antlr.v4.analysis;

public class Label implements Comparable, Cloneable {
    public static final int INVALID = -7;

	public static final int ACTION = -6;

	public static final int EPSILON = -5;

    public static final String EPSILON_STR = "<EPSILON>";

    /** label is a semantic predicate; implies label is epsilon also */
    public static final int SEMPRED = -4;

    /** label is a set of tokens or char */
    public static final int SET = -3;

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

	public int compareTo(Object o) {
		return 0;
	}
}
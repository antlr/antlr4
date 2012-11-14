/**
derived from http://svn.r-project.org/R/trunk/src/main/gram.y
http://cran.r-project.org/doc/manuals/R-lang.html#Parser
*/
grammar R;

// ambig upon a(i)<-  (delayed a bit since ';' could follow--really ambig on "a(i)")

/** ambig since stacks are exact as it loops around; no way to distinguish

    I tried tracking input index in stack to differentiate the 2 invocations
    of expr_or_assign, but that would mean altering the our context from
    the decision-making in expr_or_assign.  Also, later we need to have
    context stacks that are not dependent on input position to reuse them.

    The fact that the recursive version correctly matches the input while the
    looping version does not is a problem. We base the notion of ambiguous
    on the same state, different alternatives, same stack. But, if the
    rule invocation stack does not uniquely indicate context, we are not accurately
    detecting ambiguities. We are detecting ambiguities overzealously.

    We need a way for the context stack or configuration to distinguish between
    iterations of the loop that dive into the same rule such as expr_or_assign*.
    Perhaps the answer is to track iteration number in the configuration:

	(s, alt, ctx, iter#)

    When we reached the state following '<-', say p, in expr then we need

	(p, 1, [expr expr_or_assign prog], 1)
	(p, 2, [expr expr_or_assign prog], 2)

    But, that number would be useful... we might pass through 3 or 4 loops.
    The iteration index really has to be a part of the stack context.
    Perhaps we and an additional stack element as if we were doing the
    recursive version

	prog : expr_or_assign prog | ;

	(p, 1, [expr expr_or_assign prog])
	(p, 2, [expr expr_or_assign prog expr_or_assign prog])

    The "expr expr_or_assign prog" represents the second call back down
    into expr_or_assign like the loop would except that the stack looks different.
    
    Or, we could mark stack references with the loop iteration index.

	(p, 1, [expr expr_or_assign prog])
	(p, 2, [expr expr_or_assign.2 prog])

    This seems reusable as opposed to the input index. It might be complicated
    to track this. In the general case, we would need a mapping from rule
    invocation of rule r to a count, and within a specific rule context. That
    might add a HashMap for every RuleContext. ick. Also, what about the context
    that I create during ATN simulation? I would have to track that as well
    as the generated code in the parser. Rule invocation states would act
    like triggers that would bump account for that target rule in the current ctx.

    Actually, maybe only my ATN sim would have to do it. prog then expr_or_assign
    would be real elements on stack then I would create expr, expr_primary, pop
    them both (for 2nd alt of expr_or_assign) and pop back into prog. Then, I'd
    push expr_or_assign again but could notice I was calling 2nd time from prog.
    Maybe make one big map: count[ctx][invocation-state] -> value to keep out
    of RuleContext. Used only during sim anyway.

    Make sure that this doesn't cause r* for optional r to miss an ambiguity
    since 2nd invocation would have diff stack.
*/
prog	:	expr_or_assign* ;

/** This one is not ambig since 2nd time into expr_or_assign has different
    context where expr_or_assign* shows same context.
 */
//prog	:	expr_or_assign expr_or_assign ;

// not ambig, context different
//prog	:	expr_or_assign prog | ;

expr_or_assign
@after {System.out.println(getRuleInvocationStack());}
	:	expr '++'
	|	expr	// match ID a, fall out, reenter, match "(i)<-x" via alt 1
                // it thinks it's same context from prog, but it's not; it's
               // 2nd time through expr_or_assign* loop.
    ;

expr : expr_primary ('<-' ID)? ;
expr_primary
    : '(' ID ')'
    | ID '(' ID ')'
    | ID
    ;

/*
expr	:	'(' ID ')'  // and this
	|	expr '<-'<assoc=right> ID
	|	ID '(' ID ')'
 	|	ID
	;
*/

HEX	:	'0' ('x'|'X') HEXDIGIT+ [Ll]? ;

INT	:	DIGIT+ [Ll]? ;

fragment
HEXDIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

FLOAT	:	DIGIT+ '.' DIGIT* EXP? [Ll]?
	|	DIGIT+ EXP? [Ll]?
	|	'.' DIGIT+ EXP? [Ll]?
	;
fragment
DIGIT	:   '0'..'9' ;
fragment
EXP	:   ('E' | 'e') ('+' | '-')? INT ;

COMPLEX	:   INT 'i'
	|   FLOAT 'i'
	;

STRING	:	'"' ( ESC | ~('\\'|'"') )* '"'
	|	'\'' ( ESC | ~('\\'|'\'') )* '\''
	;

fragment
ESC
    :   '\\' ([abtnfrv]|'"'|'\'')
    |   UNICODE_ESCAPE
    |	HEX_ESCAPE
    |   OCTAL_ESCAPE
    ;

fragment
UNICODE_ESCAPE
    :   '\\' 'u' HEXDIGIT HEXDIGIT HEXDIGIT HEXDIGIT
    |   '\\' 'u' '{' HEXDIGIT HEXDIGIT HEXDIGIT HEXDIGIT '}'
    ;

fragment
OCTAL_ESCAPE
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
HEX_ESCAPE
    :   '\\' HEXDIGIT HEXDIGIT?
    ;

ID      :   '.'? (LETTER|'_'|'.') (LETTER|DIGIT|'_'|'.')*
	|   LETTER (LETTER|DIGIT|'_'|'.')*
	;

fragment
LETTER      :   'a'..'z'|'A'..'Z'|'\u0080'..'\u00FF' ;

USER_OP	    :	'%' .*? '%' ;

COMMENT :   '#' .*? '\n' {skip();} ;

/** Doesn't handle '\n' correctly. it's context-sensitive */
WS          :   (' '|'\t'|'\n'|'\r')+ {skip();} ;

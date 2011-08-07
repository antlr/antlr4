grammar Errors;

stat:	'return' INT
	|	ID '=' expr ';'
	|	ID '(' expr (',' expr)* ')' ';'
	/	ID .* '(' expr (',' expr)* ')' ';'
	/	ID '=' .* ';' // bad assignment
	/	.* ';'		// bad stat
	/	.*			// match anything else? when to stop?
	/				// match anything else?
	;
	catch[Exception e] { }
	finally { }

// error to match might be diff than how to resynch? maybe just
// include resynch pattern on end of error alt.

/*
Traps any recog exception in anything called from rule or matched in that rule.
a : expr ';'
  / '--' ID ';'	// catches any problem in expr or matching ';'
  ;

If no err alt matches, defaults to normal error mechanism at rule level.
report. resync.
*/

atom:	'(' expr ')'
	|	INT
	/	'(' expr		// missing RP; how to resync?
	/	'(' ')'
	;

// do error alts affect FOLLOW sync sets? nope.

// foo -> bar says how to make resulting tree for bad alts

expr:	atom ('*' atom)* ;

atom:	INT ;

ID : 'a'..'z'+ ;

WS : (' '|'\n')* ;

/*
Stop .* when it sees any viable following token, even if it uses FOLLOW. So,
err alt

	/ .*

would match until it sees something in FOLLOW (but not context-sensitive follow).
actually maybe it would be sensitive; just use real outer context when matching
error alts. who cares about speed.

*/

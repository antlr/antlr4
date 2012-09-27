lexer grammar A;

/*
For input 

{{x}
}

This matches {{x} and then thinks that it can stop because it can match that
without going into the recursive call. The context for the stop state in ACTION
is (2,1,[[$, 6 $]]) so it deletes everything else associated with this token.
Seems like we should favor the first alternative, but we can't do that within
a single rule.

 weird though that this one works

STRING : '"' ( '\\' '"' | . )* '"' ;

wouldn't it get to the end of the rule also by the wild-card route?
 Maybe it's a simple order of operations or order in which i process the
 alternatives?

*/
//STRING : '"' ( 'x' | . )* '"' ;

ACTION : '{' ( ACTION | . )* '}' ;
WS     : [ \r\t\n]+ -> skip ;

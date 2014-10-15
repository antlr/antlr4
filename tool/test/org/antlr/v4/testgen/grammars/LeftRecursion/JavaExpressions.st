grammar <grammarName>;
s @after {<ToStringTree("$ctx"):writeln()>} : e EOF ; // must indicate EOF can follow
expressionList
    :   e (',' e)*
    ;
e   :   '(' e ')'
    |   'this' 
    |   'super'
    |   INT
    |   ID
    |   type '.' 'class'
    |   e '.' ID
    |   e '.' 'this'
    |   e '.' 'super' '(' expressionList? ')'
    |   e '.' 'new' ID '(' expressionList? ')'
	 |	 'new' type ( '(' expressionList? ')' | ('[' e ']')+)
    |   e '[' e ']'
    |   '(' type ')' e
    |   e ('++' | '--')
    |   e '(' expressionList? ')'
    |   ('+'|'-'|'++'|'--') e
    |   ('~'|'!') e
    |   e ('*'|'/'|'%') e
    |   e ('+'|'-') e
    |   e ('\<\<' | '>>>' | '>>') e
    |   e ('\<=' | '>=' | '>' | '\<') e
    |   e 'instanceof' e
    |   e ('==' | '!=') e
    |   e '&' e
    |\<assoc=right> e '^' e
    |   e '|' e
    |   e '&&' e
    |   e '||' e
    |   e '?' e ':' e
    |\<assoc=right>
        e ('='
          |'+='
          |'-='
          |'*='
          |'/='
          |'&='
          |'|='
          |'^='
          |'>>='
          |'>>>='
          |'\<\<='
          |'%=') e
    ;
type: ID 
    | ID '[' ']'
    | 'int'
	 | 'int' '[' ']' 
    ;
ID : ('a'..'z'|'A'..'Z'|'_'|'$')+;
INT : '0'..'9'+ ;
WS : (' '|'\n') -> skip ;

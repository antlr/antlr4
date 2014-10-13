grammar <grammarName>;
ifStatement
@after {
<local()>items = $ctx.elseIfStatement() 
}
    : 'if' expression
      ( ( 'then'
          executableStatement*
          elseIfStatement*  // \<--- problem is here
          elseStatement?
          'end' 'if'
        ) | executableStatement )
    ;

elseIfStatement
    : 'else' 'if' expression 'then' executableStatement*
    ;
expression : 'a' ;
executableStatement : 'a' ;
elseStatement : 'a' ;

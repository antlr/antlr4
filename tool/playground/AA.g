grammar AA;

prog:   classDef+ ;

classDef
	:	'class' ID '{' member+ '}'
    ;

member
	:	'int' ID ';' {System.out.println("var "+$ID.text);}
	|	'int' ID '(' ')' '{' stat '}' {System.out.println("func "+$ID.text);}
	;

stat:   expr ';'
        {System.out.println("found expr: "+$stat.text);}
    |   ID '=' expr ';'
        {System.out.println("found assign: "+$stat.text+$ID.text);}
    ;

expr:   INT 
	|	ID '(' ')'
    ;

ID  :   ('a'..'z'|'A'..'Z')+ ;
INT :   '0'..'9'+ ;
WS  :   (' '|'\t'|'\n'|'\r')+ {$channel=HIDDEN;} ;

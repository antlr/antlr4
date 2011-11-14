grammar E;

@lexer::members {
public void recover(LexerNoViableAltException e) {
	super.recover(e);
	throw new RuntimeException(e);
}
}

prog:   classDef+ ; // match one or more class definitions

classDef
    :   'class' ID '{' member+ '}' // a class has one or more members
        {System.out.println("class "+$ID.text);}
    ;

member
    :   'int' ID ';'                       // field definition
        {System.out.println("var "+$ID.text);}
    |   'int' f=ID '(' ID ')' '{' stat '}' // method definition
        {System.out.println("method: "+$f.text);}
    ;

stat:   expr ';'
        {System.out.println("found expr: "+$stat.text);}
    |   ID '=' expr ';'
        {System.out.println("found assign: "+$stat.text);}
    ;

expr:   INT 
    |   ID '(' INT ')'
    ;

ID  :   ('a'..'z'|'A'..'Z')+ ;
INT :   '0'..'9'+ ;
WS  :   (' '|'\t'|'\n'|'\r')+ {$channel=HIDDEN;} ;

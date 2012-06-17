grammar U;

@members {public static boolean java5 = true;}

prog:   (   enumDecl
        |   stat
        )*
        EOF
    ;

enumDecl
    :   {java5}? 'enum' ID '{' ID (',' ID)* '}'
    ;

args
    :   arg (',' arg )*
    ;

arg
    :   INT
    ;
    
stat:   ID '=' expr ';' ;

expr:   ID                      {System.out.println("ID "+$ID.text);}
    |   {!java5}? 'enum'        {System.out.println("ID enum");}
    |   INT
    ;

ID  :   [a-zA-Z]+ ;
INT :   [0-9]+ ;
WS  :   [ \t\n\r]+ -> skip ;

grammar Simple;

sourceFile
    :   id+ EOF
    ;

id
@version{1}
    :   ID
    ;

ID
    :   [a-zA-Z_] [a-zA-Z0-9_]*
    ;

WS
    :   [ \t\r\n]+ -> channel(HIDDEN)
    ;

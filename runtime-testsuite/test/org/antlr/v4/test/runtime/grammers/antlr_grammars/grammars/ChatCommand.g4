grammar ChatCommand;

// Entry rule
command
    : SLASH CMD (WS ARG)* EOF
    ;

// Lexer rules
SLASH   : '/' ;
CMD     : [a-zA-Z]+ ;
ARG     : [a-zA-Z0-9@._-]+ ;
WS      : [ \t\r\n]+ -> skip ;

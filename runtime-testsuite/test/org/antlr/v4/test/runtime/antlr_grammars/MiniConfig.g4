grammar MiniConfig;
config: (section | pair)* EOF;
section: '[' NAME ']' ;
pair: NAME '=' VALUE ;
NAME: [a-zA-Z_][a-zA-Z0-9_]* ;
VALUE: ~[\r\n#;]+ ;
WS: [ \t\r\n]+ -> skip ;
COMMENT: ('#'|';').*? -> skip ;

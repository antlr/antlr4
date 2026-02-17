grammar MiniMarkdown;
doc : element+ EOF ;
element
    : heading
    | bold
    | text
    ;
heading : '#' WS? TEXT NEWLINE ;
bold : '**' TEXT '**' ;
text : TEXT ;
TEXT : ~[\r\n#*]+ ;
NEWLINE : ('\r'? '\n')+ ;
WS : [ \t]+ -> skip ;
grammar MiniMarkdown;
doc: (heading | bold | text)+ ;
heading: '#' WS? TEXT NL ;
bold: '**' TEXT '**' ;
text: TEXT NL? ;
TEXT: ~[\r\n#*]+ ;
WS: [ \t]+ -> skip ;
NL: '\r'? '\n' ;

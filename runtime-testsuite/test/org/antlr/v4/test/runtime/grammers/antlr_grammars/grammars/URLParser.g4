grammar URLParser;
url: scheme '://' host (':' port)? path? EOF ;
scheme: [a-z]+ ;
host: [a-zA-Z0-9.-]+ ;
port: [0-9]+ ;
path: '/' [a-zA-Z0-9./_-]* ;
WS: [ \t\r\n]+ -> skip ;

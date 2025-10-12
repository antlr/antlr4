grammar ChatCommand;
command: '/' name (arg+)? EOF ;
name: [a-zA-Z]+ ;
arg: WS+ [a-zA-Z0-9@._-]+ ;
WS: [ \t\r\n]+ -> skip ;

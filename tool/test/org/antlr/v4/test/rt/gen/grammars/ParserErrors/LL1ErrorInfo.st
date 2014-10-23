grammar <grammarName>;
start : animal (AND acClass)? service EOF;
animal : (DOG | CAT );
service : (HARDWARE | SOFTWARE) ;
AND : 'and';
DOG : 'dog';
CAT : 'cat';
HARDWARE: 'hardware';
SOFTWARE: 'software';
WS : ' ' -> skip ;
acClass
@init
{<GetExpectedTokenNames():writeln()>}
  : ;

grammar U;
a : ({true}?ID|{false}?ID{;})* ID ;

INT : '0'..'9'+ ;
ID : 'a'..'z'+ ;
WS : (' '|'\n')+ {skip();} ;

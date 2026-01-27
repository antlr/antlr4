grammar ReferenceToATN;

a : (ID|ATN)* ATN? {println!("{}",$text);};
ID : 'a'..'z'+ ;
ATN : '0'..'9'+;
WS : (' '|'\n') -> skip ;

grammar <grammarName>;
@members {<InitIntMember("i","0")>}
s : a[2] a[1];
a[int i]
  : {<ValEquals("$i","1")>}? ID 
  | {<ValEquals("$i","2")>}? ID 
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;

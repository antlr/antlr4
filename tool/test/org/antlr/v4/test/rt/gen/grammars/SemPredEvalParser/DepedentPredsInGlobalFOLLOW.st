grammar <grammarName>;
@members {
<Declare_pred()>
}
s : a[99] ;
a[int i] : e {<ValEquals("$i","99"):Invoke_pred()>}? {<writeln("\"parse\"")>} '!' ;
b[int i] : e {<ValEquals("$i","99"):Invoke_pred()>}? ID ;
e : ID | ; // non-LL(1) so we use ATN
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;

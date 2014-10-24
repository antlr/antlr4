grammar <grammarName>;
@members {
<Declare_pred()>
}
s : e {} {<true:Invoke_pred()>}? {<writeln("\"parse\"")>} '!' ;
t : e {} {<false:Invoke_pred()>}? ID ;
e : ID | ; // non-LL(1) so we use ATN
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;

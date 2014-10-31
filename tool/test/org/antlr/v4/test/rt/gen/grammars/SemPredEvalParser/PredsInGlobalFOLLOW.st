grammar <grammarName>;
@members {
<Declare_pred()>
}
s : e {<True():Invoke_pred()>}? {<writeln("\"parse\"")>} '!' ;
t : e {<False():Invoke_pred()>}? ID ;
e : ID | ; // non-LL(1) so we use ATN
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;

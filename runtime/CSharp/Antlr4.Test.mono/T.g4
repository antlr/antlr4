grammar T;
@members {
bool pred(bool v) {
	Console.WriteLine("eval="+v.ToString().ToLower());
	return v;
}
}
s : e {this.pred(true)}? {Console.WriteLine("parse");} '!' ;
t : e {this.pred(false)}? ID ;
e : ID | ; // non-LL(1) so we use ATN
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;
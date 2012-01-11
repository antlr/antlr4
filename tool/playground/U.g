grammar U;
s @after {System.out.println($ctx.toStringTree(this));} : e EOF ;
e : e '.' ID
  | e '.' 'this'
  | '-' e
  | e '*' e
  | e ('+'|'-') e
  | INT
  | ID
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+ ;
WS : (' '|'\n') {skip();} ;

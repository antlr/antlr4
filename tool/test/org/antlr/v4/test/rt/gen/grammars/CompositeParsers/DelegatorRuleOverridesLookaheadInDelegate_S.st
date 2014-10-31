parser grammar S;
type_ : 'int' ;
decl : type_ ID ';'
	| type_ ID init ';' {<write("\"Decl: \" + $text")>};
init : '=' INT;

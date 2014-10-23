parser grammar S;
type_ : 'int' ;
decl : type_ ID ';'
	| type_ ID init ';' {
		<write("\"decl: \" + $text")>
	};
init : '=' INT;

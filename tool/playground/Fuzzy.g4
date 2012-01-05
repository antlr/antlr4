grammar Fuzzy;

options { tokenVocab=JavaLR; }

scan: (pattern|.)* EOF ;

pattern
	:	a=Identifier '=' b=Identifier ';'
		{System.out.printf("assign %s=%s", $a.text, $b.text);}
	;

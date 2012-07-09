parser grammar T;

options {tokenVocab=CharVocab;}

@header {
import java.util.Set;
import java.util.HashSet;
}

@members {
public static Set<String> keywords = new HashSet<String>() {{
    add("void");
    add("int");
    add("return");
}};
boolean notLetterOrDigit(int la) {
    return !Character.isLetterOrDigit(_input.LA(la));
}
}

// parser rules

prog:	ws? (var|func)+ EOF ;

type:	kint | kvoid | id ;

var	:	type id semi ;

func:	type id lp args rp body ;

args:	arg (',' arg)* ;

arg	:	type id ;

body:	'{' stat+ '}' ;

stat:	kreturn e semi
    |	id eq e semi
    |	id colon
    ;

e   :   integer | id ;

// lexical rules

id  :   letter+ {!keywords.contains($text)}? ws? ;

lp	:	'(' ws? ;
rp	:	')' ws? ;
eq  :   '=' ws? ;
colon:  ':' ws? ;
semi:   ';' ws? ;

kint:		{notLetterOrDigit(4)}? 'i' 'n' 't' ;
kvoid:		{notLetterOrDigit(5)}? 'v' 'o' 'i' 'd' ;
kreturn:	{notLetterOrDigit(7)}? 'r' 'e' 't' 'u' 'r' 'n' ;

integer : digit+ ws? ;

digit
    :   '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
    ;

letter
    :   'a' | 'b' | 'c' | 'd' | 'e' | 'f' | 'g' | 'h' | 'i' | 'j'
    |   'k' | 'l' | 'm' | 'n' | 'o' | 'p' | 'q' | 'r' | 's' | 't'
    |   'u' | 'v' | 'w' | 'x' | 'y' | 'z'
    ;

ws  :   (' ' | '\t' | '\n' | comment)+ ;
comment : '/' '/' ~('\r'|'\n')* '\r' '\n' ;

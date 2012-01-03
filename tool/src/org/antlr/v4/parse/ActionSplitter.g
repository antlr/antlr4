lexer grammar ActionSplitter;

options { filter=true; }

@header {
package org.antlr.v4.parse;
import org.antlr.v4.tool.*;
import org.antlr.v4.tool.ast.*;
}

@members {
ActionSplitterListener delegate;

public ActionSplitter(CharStream input, ActionSplitterListener delegate) {
    this(input, new RecognizerSharedState());
    this.delegate = delegate;
}

public void emit(Token token) {
	super.emit(token);

}

/** force filtering (and return tokens). triggers all above actions. */
public List<Token> getActionTokens() {
    List<Token> chunks = new ArrayList<Token>();
    Token t = nextToken();
    while ( t.getType()!=Token.EOF ) {
        chunks.add(t);
        t = nextToken();
    }
    return chunks;
}
}

// ignore comments right away

COMMENT
    :   '/*' ( options {greedy=false;} : . )* '*/' {delegate.text($text);}
    ;

LINE_COMMENT
    : '//' ~('\n'|'\r')* '\r'? '\n' {delegate.text($text);}
    ;

SET_NONLOCAL_ATTR
	:	'$' x=ID '::' y=ID WS? '=' expr=ATTR_VALUE_EXPR ';'
		{
		delegate.setNonLocalAttr($text, $x, $y, $expr);
		}
	;

NONLOCAL_ATTR
	:	'$' x=ID '::' y=ID {delegate.nonLocalAttr($text, $x, $y);}
	;

SET_QUALIFIED_ATTR
	:	'$' x=ID '.' y=ID WS? '=' expr=ATTR_VALUE_EXPR ';'
		{
		delegate.setQualifiedAttr($text, $x, $y, $expr);
		}
	;

QUALIFIED_ATTR
	:	'$' x=ID '.' y=ID {input.LA(1)!='('}? {delegate.qualifiedAttr($text, $x, $y);}
	;

SET_ATTR
	:	'$' x=ID WS? '=' expr=ATTR_VALUE_EXPR ';'
		{
		delegate.setAttr($text, $x, $expr);
		}
	;

ATTR
	:	'$' x=ID {delegate.attr($text, $x);}
	;

// Anything else is just random text
TEXT
@init {StringBuilder buf = new StringBuilder();}
@after {delegate.text(buf.toString());}
	:	(	c=~('\\'| '$') {buf.append((char)$c);}
		|	'\\$' {buf.append("$");}
		|	'\\' c=~('$') {buf.append("\\"+(char)$c);}
		)+
	;

fragment
ACTION
	:	'{' ('\\}'|~'}')* '}'
	;

fragment
ARG	:	ID '=' ACTION
	;

fragment
ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

/** Don't allow an = as first char to prevent $x == 3; kind of stuff. */
fragment
ATTR_VALUE_EXPR
	:	~'=' (~';')*
	;

fragment
SCOPE_INDEX_EXPR
	:	('\\]'|~']')+
	;

fragment
WS	:	(' '|'\t'|'\n'|'\r')+
	;


lexer grammar ActionSplitter;

options { filter=true; }

@header {
package org.antlr.v4.parse;
import org.antlr.v4.tool.*;
}

@members {
/*
public void setQualifiedAttr(Token x, Token y, Token expr) { }
public void qualifiedAttr(Token x, Token y) { }
public void setDynamicScopeAttr(Token x, Token y, Token expr) { }
public void dynamicScopeAttr(Token x, Token y) { }
public void setDynamicNegativeIndexedScopeAttr(Token x, Token y, Token index, Token expr) { }
public void dynamicNegativeIndexedScopeAttr(Token x, Token y, Token index) { }
public void setDynamicAbsoluteIndexedScopeAttr(Token x, Token y, Token index, Token expr) { }
public void dynamicAbsoluteIndexedScopeAttr(Token x, Token y, Token index) { }
public void setAttr(Token x, Token expr) { }
public void attr(Token x) { }
public void templateInstance() { }
public void indirectTemplateInstance() { }
public void setExprAttribute() { }
public void setAttribute() { }
public void templateExpr() { }
public void unknownSyntax(String text) { }
public void text(String text) { }
*/
ActionSplitterListener delegate;

public ActionSplitter(CharStream input, ActionSplitterListener delegate) {
    this(input, new RecognizerSharedState());
    this.delegate = delegate;
}

/** force filtering (and return tokens). triggers all above actions. */
public List<Token> getActionChunks() { 
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

SET_QUALIFIED_ATTR
	:	'$' x=ID '.' y=ID WS? '=' expr=ATTR_VALUE_EXPR ';'
		{delegate.setQualifiedAttr($x, $y, $expr);}
	;

QUALIFIED_ATTR
	:	'$' x=ID '.' y=ID {input.LA(1)!='('}? {delegate.qualifiedAttr($x, $y);}
	;

SET_DYNAMIC_SCOPE_ATTR
	:	'$' x=ID '::' y=ID WS? '=' expr=ATTR_VALUE_EXPR ';'
		{delegate.setDynamicScopeAttr($x, $y, $expr);}
	;

DYNAMIC_SCOPE_ATTR
	:	'$' x=ID '::' y=ID {delegate.dynamicScopeAttr($x, $y);}
	;

/**		To access deeper (than top of stack) scopes, use the notation:
 *
 * 		$x[-1]::y previous (just under top of stack)
 * 		$x[-i]::y top of stack - i where the '-' MUST BE PRESENT;
 * 				  i.e., i cannot simply be negative without the '-' sign!
 * 		$x[i]::y  absolute index i (0..size-1)
 * 		$x[0]::y  is the absolute 0 indexed element (bottom of the stack)
 */
SET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR
	:	'$' x=ID '[' '-' index=SCOPE_INDEX_EXPR ']' '::' y=ID
		WS? ('=' expr=ATTR_VALUE_EXPR ';')?
		{delegate.setDynamicNegativeIndexedScopeAttr($x, $y, $index, $expr);}
	;

DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR
	:	'$' x=ID '[' '-' index=SCOPE_INDEX_EXPR ']' '::' y=ID
		{delegate.dynamicNegativeIndexedScopeAttr($x, $y, $index);}
	;

SET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR
	:	'$' x=ID '[' index=SCOPE_INDEX_EXPR ']' '::' y=ID
		WS? ('=' expr=ATTR_VALUE_EXPR ';')?
		{delegate.setDynamicAbsoluteIndexedScopeAttr($x, $y, $index, $expr);}
	;

DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR
	:	'$' x=ID '[' index=SCOPE_INDEX_EXPR ']' '::' y=ID
		{delegate.dynamicAbsoluteIndexedScopeAttr($x, $y, $index);}
	;

SET_ATTR
	:	'$' x=ID WS? '=' expr=ATTR_VALUE_EXPR ';' {delegate.setAttr($x, $expr);}
	;

ATTR
	:	'$' x=ID {delegate.attr($x);}
	;

/** %foo(a={},b={},...) ctor */
TEMPLATE_INSTANCE
	:	'%' ID '(' ( WS? ARG (',' WS? ARG)* WS? )? ')'
	;

/** %({name-expr})(a={},...) indirect template ctor reference */
INDIRECT_TEMPLATE_INSTANCE
	:	'%' '(' ACTION ')' '(' ( WS? ARG (',' WS? ARG)* WS? )? ')'
	;

/**	%{expr}.y = z; template attribute y of StringTemplate-typed expr to z */
SET_EXPR_ATTRIBUTE
	:	'%' a=ACTION '.' ID WS? '=' expr=ATTR_VALUE_EXPR ';'
	;
	
/*    %x.y = z; set template attribute y of x (always set never get attr)
 *        to z [languages like python without ';' must still use the
 *        ';' which the code generator is free to remove during code gen]
 */
SET_ATTRIBUTE
	:	'%' x=ID '.' y=ID WS? '=' expr=ATTR_VALUE_EXPR ';'
	;

/** %{string-expr} anonymous template from string expr */
TEMPLATE_EXPR
	:	'%' a=ACTION
	;

UNKNOWN_SYNTAX
@after {delegate.unknownSyntax($text);}
	:	'%' (ID|'.'|'('|')'|','|'{'|'}'|'"')*
	;

// Anything else is just random text
TEXT
@after {delegate.text($text);}
	:	(	'\\$'
		|	'\\%'
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


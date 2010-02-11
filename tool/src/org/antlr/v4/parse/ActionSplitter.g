lexer grammar ActionSplitter;

options { filter=true; }

@header {
package org.antlr.v4.parse;
import org.antlr.v4.tool.*;
}

@members {
public void setQualifiedAttr(Token x, Token y, Token expr) { }
public void qualifiedAttr(Token x, Token y) { }
public void setDynamicScopeAttr() { }
public void dynamicScopeAttr() { }
public void setDynamicNegativeIndexedScopeAttr() { }
public void dynamicNegativeIndexedScopeAttr() { }
public void setDynamicAbsoluteIndexedScopeAttr() { }
public void dynamicAbsoluteIndexedScopeAttr() { }
public void setAttr() { }
public void attr() { }
public void templateInstance() { }
public void indirectTemplateInstance() { }
public void setExprAttribute() { }
public void setAttribute() { }
public void templateExpr() { }
public void unknownSyntax() { }
public void text() { }

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

SET_QUALIFIED_ATTR
	:	'$' x=ID '.' y=ID WS? '=' expr=ATTR_VALUE_EXPR ';'
		{setQualifiedAttr($x, $y, $expr);}
	;

QUALIFIED_ATTR
	:	'$' x=ID '.' y=ID {input.LA(1)!='('}? {qualifiedAttr($x, $y);}
	;

SET_DYNAMIC_SCOPE_ATTR
	:	'$' x=ID '::' y=ID WS? '=' expr=ATTR_VALUE_EXPR ';'
	;

DYNAMIC_SCOPE_ATTR
	:	'$' x=ID '::' y=ID
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
	:	'$' x=ID '[' '-' expr=SCOPE_INDEX_EXPR ']' '::' y=ID
		WS? ('=' expr=ATTR_VALUE_EXPR ';')?
	;

DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR
	:	'$' x=ID '[' '-' expr=SCOPE_INDEX_EXPR ']' '::' y=ID
	;

SET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR
	:	'$' x=ID '[' expr=SCOPE_INDEX_EXPR ']' '::' y=ID
		WS? ('=' expr=ATTR_VALUE_EXPR ';')?
	;

DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR
	:	'$' x=ID '[' expr=SCOPE_INDEX_EXPR ']' '::' y=ID
	;

SET_ATTR
	:	ATTR WS? '=' expr=ATTR_VALUE_EXPR ';'
	;

ATTR
	:	'$' ID
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
	:	'$'
		{
//		chunks.add(getText());
		// shouldn't need an error here.  Just accept \$ if it doesn't look like anything
		}
	|	'%' (ID|'.'|'('|')'|','|'{'|'}'|'"')*
		{
/*
		chunks.add(getText());
		ErrorManager.grammarError(ErrorManager.MSG_INVALID_TEMPLATE_ACTION,
								  grammar,
								  actionToken,
								  getText());
*/
		}
	;

TEXT:	(	'\\$'
		|	'\\%'
		|	~('$'|'%')
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


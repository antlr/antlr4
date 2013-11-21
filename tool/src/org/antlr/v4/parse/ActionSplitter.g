/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

private boolean isIDStartChar(int c) {
	return c == '_' || Character.isLetter(c);
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
		|	'\\$' {buf.append('$');}
		|	'\\' c=~('$') {buf.append('\\').append((char)$c);}
		|	{!isIDStartChar(input.LA(2))}? => '$' {buf.append('$');}
		)+
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
WS	:	(' '|'\t'|'\n'|'\r')+
	;


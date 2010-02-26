/*
 [The "BSD license"]
 Copyright (c) 2010 Terence Parr
 All rights reserved.
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.
 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

tree grammar NFABuilder;
options {
	language     = Java;
	tokenVocab   = ANTLRParser;
	ASTLabelType = GrammarAST;
	filter 	     = true;
}

// Include the copyright in this source and also the generated source
@header {
/*
 [The "BSD license"]
 Copyright (c) 2010 Terence Parr
 All rights reserved.
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.
 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.antlr.v4.parse;
import org.antlr.v4.tool.*;
import org.antlr.v4.automata.NFAFactory;
import org.antlr.v4.runtime.tree.CommonTree; // use updated v4 one not v3
}

@members {
    NFAFactory factory;
    public NFABuilder(TreeNodeStream input, NFAFactory factory) {
    	this(input);
    	this.factory = factory;
    }
}

// IGNORE EVERYTHING UNTIL WE SEE A RULE OR BLOCK SUBTREE

topdown
	:	rule
	;

bottomup
	:	block // match block innermost to outermost all the way out to rule block
	;

rule returns [NFAFactory.Handle p]
	:   ^(RULE name=ID .+) {factory.setCurrentRuleName($name.text);}
	;

block returns [NFAFactory.Handle p]
@init {List<NFAFactory.Handle> alts = new ArrayList<NFAFactory.Handle>();}
    :	^(BLOCK ~ALT* (a=alternative {alts.add($a.p);})+)
    	{factory.block(alts);}
    ;

alternative returns [NFAFactory.Handle p]
    :	^(ALT_REWRITE alternative .)
    |	^(ALT EPSILON)
    |   ^(ALT element+)
    ;

element returns [NFAFactory.Handle p]
	:	labeledElement
	|	atom
	|	ebnf
	|   ACTION
	|   SEMPRED
	|	GATED_SEMPRED
	|	treeSpec
	;
	
labeledElement returns [NFAFactory.Handle p]
	:	^(ASSIGN ID atom)
	|	^(ASSIGN ID block)
	|	^(PLUS_ASSIGN ID atom)
	|	^(PLUS_ASSIGN ID block)
	;

treeSpec returns [NFAFactory.Handle p]
    : ^(TREE_BEGIN element+)
    ;

ebnf returns [NFAFactory.Handle p]
	:	^(blockSuffix block)
	| 	block
    ;

blockSuffix returns [NFAFactory.Handle p]
    : ebnfSuffix
    | ROOT
    | IMPLIES
    | BANG
    ;

ebnfSuffix returns [NFAFactory.Handle p]
	:	OPTIONAL
  	|	CLOSURE
   	|	POSITIVE_CLOSURE
	;
	
atom returns [NFAFactory.Handle p]	
	:	^(ROOT range)
	|	^(BANG range)
	|	^(ROOT notSet)
	|	^(BANG notSet)
	|	range
	|	^(DOT ID terminal)
	|	^(DOT ID ruleref)
    |   terminal
    |   ruleref
    ;

notSet returns [NFAFactory.Handle p]
    : ^(NOT notTerminal)
    | ^(NOT block)
    ;

notTerminal returns [NFAFactory.Handle p]
    : TOKEN_REF
    | STRING_LITERAL
    ;

ruleref returns [NFAFactory.Handle p]
    :	^(ROOT ^(RULE_REF ARG_ACTION?))	{factory.ruleRef($RULE_REF);}
    |	^(BANG ^(RULE_REF ARG_ACTION?))	{factory.ruleRef($RULE_REF);}
    |	^(RULE_REF ARG_ACTION?)			{factory.ruleRef($RULE_REF);}
    ;

range returns [NFAFactory.Handle p]
    : ^(RANGE a=STRING_LITERAL b=STRING_LITERAL) {factory.range($a,$b);}
    ;

terminal returns [NFAFactory.Handle p]
    :  ^(STRING_LITERAL .)			{factory.stringLiteral($start);}
    |	STRING_LITERAL				{factory.stringLiteral($start);}
    |	^(TOKEN_REF ARG_ACTION .)	{factory.tokenRef((TerminalAST)$start);}
    |	^(TOKEN_REF .)				{factory.tokenRef((TerminalAST)$start);}
    |	TOKEN_REF					{factory.tokenRef((TerminalAST)$start);}
    |	^(WILDCARD .)				{factory.wildcard($start);}
    |	WILDCARD					{factory.wildcard($start);}
    |	^(ROOT terminal)
    |	^(BANG terminal)
    ;
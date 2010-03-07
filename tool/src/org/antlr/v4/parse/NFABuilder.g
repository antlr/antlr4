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
//	filter 	     = true;
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

block returns [NFAFactory.Handle p]
@init {List<NFAFactory.Handle> alts = new ArrayList<NFAFactory.Handle>();}
    :	^(BLOCK (^(OPTIONS .+))? (a=alternative {alts.add($a.p);})+)
    	{$p = factory.block($BLOCK, alts);}
    ;

alternative returns [NFAFactory.Handle p]
@init {List<NFAFactory.Handle> els = new ArrayList<NFAFactory.Handle>();}
    :	^(ALT_REWRITE a=alternative .)	{$p = $a.p;}
    |	^(ALT EPSILON)					{$p = factory.epsilon($EPSILON);}
    |   ^(ALT (e=element {els.add($e.p);})+)
    									{$p = factory.alt(els);}
    ;

element returns [NFAFactory.Handle p]
	:	labeledElement				{$p = $labeledElement.p;}
	|	atom						{$p = $atom.p;}
	|	ebnf						{$p = $ebnf.p;}
	|   ACTION						{$p = factory.action($ACTION);}
	|   SEMPRED						{$p = factory.sempred($SEMPRED);}
	|	GATED_SEMPRED				{$p = factory.gated_sempred($GATED_SEMPRED);}
	|	treeSpec					{$p = $treeSpec.p;}
	;
	
labeledElement returns [NFAFactory.Handle p]
	:	^(ASSIGN ID atom)			{$p = $atom.p;}
	|	^(ASSIGN ID block)			{$p = $block.p;}
	|	^(PLUS_ASSIGN ID atom)		{$p = $atom.p;}
	|	^(PLUS_ASSIGN ID block)		{$p = $block.p;}
	;

treeSpec returns [NFAFactory.Handle p]
@init {List<NFAFactory.Handle> els = new ArrayList<NFAFactory.Handle>();}
    : ^(TREE_BEGIN  (e=element {els.add($e.p);})+)	{$p = factory.tree(els);}
    ;

ebnf returns [NFAFactory.Handle p]
	:	^(astBlockSuffix block)		{$p = $block.p;}
	|	^(OPTIONAL block)			{$p = factory.optional($start, $block.p);}
	|	^(CLOSURE block)			{$p = factory.star($start, $block.p);}
	|	^(POSITIVE_CLOSURE block)	{$p = factory.plus($start, $block.p);}
	| 	block						{$p = $block.p;}
    ;

astBlockSuffix
    : ROOT
    | IMPLIES
    | BANG
    ;

atom returns [NFAFactory.Handle p]	
	:	^(ROOT range)			{$p = $range.p;}
	|	^(BANG range)			{$p = $range.p;}
	|	^(ROOT notSet)			{$p = $notSet.p;}
	|	^(BANG notSet)			{$p = $notSet.p;}
	|	range					{$p = $range.p;}
	|	^(DOT ID terminal)		{$p = $terminal.p;}
	|	^(DOT ID ruleref)		{$p = $ruleref.p;}
    |   terminal				{$p = $terminal.p;}
    |   ruleref					{$p = $ruleref.p;}
    ;

notSet returns [NFAFactory.Handle p]
    : ^(NOT notTerminal)	{$p = factory.not($notTerminal.p);}
    | ^(NOT block)			{$p = factory.not($block.p);}
    ;

notTerminal returns [NFAFactory.Handle p]
    : TOKEN_REF				{$p = factory.tokenRef((TerminalAST)$TOKEN_REF);}
    | STRING_LITERAL		{$p = factory.stringLiteral((TerminalAST)$start);}
    ;

ruleref returns [NFAFactory.Handle p]
    :	^(ROOT ^(RULE_REF ARG_ACTION?))	{$p = factory.ruleRef($RULE_REF);}
    |	^(BANG ^(RULE_REF ARG_ACTION?))	{$p = factory.ruleRef($RULE_REF);}
    |	^(RULE_REF ARG_ACTION?)			{$p = factory.ruleRef($RULE_REF);}
    ;

range returns [NFAFactory.Handle p]
    : ^(RANGE a=STRING_LITERAL b=STRING_LITERAL) {$p = factory.range($a,$b);}
    ;

terminal returns [NFAFactory.Handle p]
    :  ^(STRING_LITERAL .)			{$p = factory.stringLiteral((TerminalAST)$start);}
    |	STRING_LITERAL				{$p = factory.stringLiteral((TerminalAST)$start);}
    |	^(TOKEN_REF ARG_ACTION .)	{$p = factory.tokenRef((TerminalAST)$start);}
    |	^(TOKEN_REF .)				{$p = factory.tokenRef((TerminalAST)$start);}
    |	TOKEN_REF					{$p = factory.tokenRef((TerminalAST)$start);}
    |	^(WILDCARD .)				{$p = factory.wildcard($start);}
    |	WILDCARD					{$p = factory.wildcard($start);}
    |	^(ROOT t=terminal)			{$p = $t.p;}
    |	^(BANG t=terminal)			{$p = $t.p;}
    ;
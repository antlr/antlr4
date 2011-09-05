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

tree grammar ATNBuilder;
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
import org.antlr.v4.automata.ATNFactory;
}

@members {
    ATNFactory factory;
    public ATNBuilder(TreeNodeStream input, ATNFactory factory) {
    	this(input);
    	this.factory = factory;
    }
}

dummy : block[null] ; // avoid error about no start rule

block[GrammarAST ebnfRoot] returns [ATNFactory.Handle p]
@init {List<ATNFactory.Handle> alts = new ArrayList<ATNFactory.Handle>();}
    :	^(BLOCK (^(OPTIONS .+))? (a=alternative {alts.add($a.p);})+)
    	{$p = factory.block((BlockAST)$BLOCK, ebnfRoot, alts);}
    ;

alternative returns [ATNFactory.Handle p]
@init {List<ATNFactory.Handle> els = new ArrayList<ATNFactory.Handle>();}
    :	^(ALT_REWRITE a=alternative .*)	{$p = $a.p;}
    |	^(ALT EPSILON)					{$p = factory.epsilon($EPSILON);}
    |   ^(ALT (e=element {els.add($e.p);})+)
    									{$p = factory.alt(els);}
    ;

element returns [ATNFactory.Handle p]
	:	labeledElement				{$p = $labeledElement.p;}
	|	atom						{$p = $atom.p;}
	|	subrule						{$p = $subrule.p;}
	|   ACTION						{$p = factory.action((ActionAST)$ACTION);}
	|   SEMPRED						{$p = factory.sempred((PredAST)$SEMPRED);}
	|	treeSpec					{$p = $treeSpec.p;}
	|	^(ROOT a=astOperand)		{$p = $a.p;}
	|	^(BANG a=astOperand)		{$p = $a.p;}
    |	^(NOT b=blockSet[true])		{$p = $b.p;}
	;

astOperand returns [ATNFactory.Handle p]
	:	atom						{$p = $atom.p;}
	|	^(NOT blockSet[true])		{$p = $blockSet.p;}
	;

labeledElement returns [ATNFactory.Handle p]
	:	^(ASSIGN ID element)	    {$p = factory.label($element.p);}
	|	^(PLUS_ASSIGN ID element)   {$p = factory.listLabel($element.p);}
	;

treeSpec returns [ATNFactory.Handle p]
@init {List<ATNFactory.Handle> els = new ArrayList<ATNFactory.Handle>();}
    : ^(TREE_BEGIN  (e=element {els.add($e.p);})+)	{$p = factory.tree(els);}
    ;

subrule returns [ATNFactory.Handle p]
	:	^(astBlockSuffix block[null])		{$p = $block.p;}
	|	^(OPTIONAL block[$start])			{$p = $block.p;}
	|	^(CLOSURE block[$start])			{$p = $block.p;}
	|	^(POSITIVE_CLOSURE block[$start])	{$p = $block.p;}
	| 	block[null] 						{$p = $block.p;}
    ;

blockSet[boolean invert] returns [ATNFactory.Handle p]
@init {List<GrammarAST> alts = new ArrayList<GrammarAST>();}
	:	^(SET (setElement {alts.add($setElement.start);})+) {$p = factory.set($start, alts, $invert);}
	;

/** Don't combine with atom otherwise it will build spurious ATN nodes */
setElement
	:	STRING_LITERAL
	|	TOKEN_REF
	|	^(RANGE a=STRING_LITERAL b=STRING_LITERAL)
	;

astBlockSuffix
    : ROOT
    | IMPLIES
    | BANG
    ;

atom returns [ATNFactory.Handle p]
	:	range					{$p = $range.p;}
	|	^(DOT ID terminal)		{$p = $terminal.p;}
	|	^(DOT ID ruleref)		{$p = $ruleref.p;}
    |	^(WILDCARD .)			{$p = factory.wildcard($start);}
    |	WILDCARD				{$p = factory.wildcard($start);}
    |	blockSet[false]			{$p = $blockSet.p;}
    |   terminal				{$p = $terminal.p;}
    |   ruleref					{$p = $ruleref.p;}
    ;

ruleref returns [ATNFactory.Handle p]
    :	^(RULE_REF ARG_ACTION?)			{$p = factory.ruleRef($RULE_REF);}
    ;

range returns [ATNFactory.Handle p]
    : ^(RANGE a=STRING_LITERAL b=STRING_LITERAL) {$p = factory.range($a,$b);}
    ;

terminal returns [ATNFactory.Handle p]
    :  ^(STRING_LITERAL .)			{$p = factory.stringLiteral((TerminalAST)$start);}
    |	STRING_LITERAL				{$p = factory.stringLiteral((TerminalAST)$start);}
    |	^(TOKEN_REF ARG_ACTION .)	{$p = factory.tokenRef((TerminalAST)$start);}
    |	^(TOKEN_REF .)				{$p = factory.tokenRef((TerminalAST)$start);}
    |	TOKEN_REF					{$p = factory.tokenRef((TerminalAST)$start);}
    ;

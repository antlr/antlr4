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

tree grammar BlockSetTransformer;
options {
	language     = Java;
	tokenVocab   = ANTLRParser;
	ASTLabelType = GrammarAST;
	output		 = AST;
	filter		 = true;
}

@header {
package org.antlr.v4.parse;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.misc.*;
import org.antlr.v4.tool.*;
import org.antlr.v4.tool.ast.*;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import org.antlr.v4.runtime.misc.IntervalSet;
}

@members {
public String currentRuleName;
public GrammarAST currentAlt;
public Grammar g;
public BlockSetTransformer(TreeNodeStream input, Grammar g) {
    this(input, new RecognizerSharedState());
    this.g = g;
}
}

topdown
    :	^(RULE (id=TOKEN_REF|id=RULE_REF) {currentRuleName=$id.text;} .+)
    |	setAlt
    |	ebnfBlockSet
    |	blockSet
	;

setAlt
	:	{inContext("RULE BLOCK")}?
		ALT {currentAlt = $start;}
	;

// (BLOCK (ALT (+ (BLOCK (ALT INT) (ALT ID)))))
ebnfBlockSet
@after {
	GrammarTransformPipeline.setGrammarPtr(g, $tree);
}
	:	^(ebnfSuffix blockSet) -> ^(ebnfSuffix ^(BLOCK<BlockAST> ^(ALT<AltAST> blockSet)))
	;

ebnfSuffix
@after {$tree = (GrammarAST)adaptor.dupNode($start);}
	:	OPTIONAL
  	|	CLOSURE
   	|	POSITIVE_CLOSURE
	;

blockSet
@init {
boolean inLexer = Grammar.isTokenName(currentRuleName);
}
@after {
	GrammarTransformPipeline.setGrammarPtr(g, $tree);
}
	:	{inContext("RULE")}? // top-level: rule block and > 1 alt
		^(BLOCK ^(alt=ALT elementOptions? {((AltAST)$alt).altLabel==null}? setElement[inLexer]) ( ^(ALT elementOptions? setElement[inLexer]) )+)
		-> ^(BLOCK<BlockAST>[$BLOCK.token] ^(ALT<AltAST>[$BLOCK.token,"ALT"] ^(SET[$BLOCK.token, "SET"] setElement+)))
	|	{!inContext("RULE")}? // if not rule block and > 1 alt
		^(BLOCK ^(ALT elementOptions? setElement[inLexer]) ( ^(ALT elementOptions? setElement[inLexer]) )+)
		-> ^(SET[$BLOCK.token, "SET"] setElement+)
	;

setElement[boolean inLexer]
@after {
	GrammarTransformPipeline.setGrammarPtr(g, $tree);
}
	:	(	^(a=STRING_LITERAL elementOptions) {!inLexer || CharSupport.getCharValueFromGrammarCharLiteral($a.getText())!=-1}?
		|	  a=STRING_LITERAL {!inLexer || CharSupport.getCharValueFromGrammarCharLiteral($a.getText())!=-1}?
		|	{!inLexer}?=> ^(TOKEN_REF elementOptions)
		|	{!inLexer}?=>   TOKEN_REF
		|	{inLexer}?=>  ^(RANGE a=STRING_LITERAL b=STRING_LITERAL)
			{CharSupport.getCharValueFromGrammarCharLiteral($a.getText())!=-1 &&
			 CharSupport.getCharValueFromGrammarCharLiteral($b.getText())!=-1}?
		)
	;

elementOptions
	:	^(ELEMENT_OPTIONS elementOption*)
	;

elementOption
	:	ID
	|	^(ASSIGN id=ID v=ID)
	|	^(ASSIGN ID v=STRING_LITERAL)
	|	^(ASSIGN ID v=ACTION)
	|	^(ASSIGN ID v=INT)
	;

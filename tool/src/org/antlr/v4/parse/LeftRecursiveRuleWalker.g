/*
 * [The "BSD license"]
 * Copyright (c) 2011 Terence Parr
 * All rights reserved.
 *
 * Grammar conversion to ANTLR v3:
 * Copyright (c) 2011 Sam Harwell
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/** Find left-recursive rules */
tree grammar LeftRecursiveRuleWalker;

options {
	tokenVocab=ANTLRParser;
    ASTLabelType=GrammarAST;
}

@header {
package org.antlr.v4.parse;

import org.antlr.v4.misc.*;
import org.antlr.v4.tool.*;
import org.antlr.v4.tool.ast.*;
}

@members {
private String ruleName;
private int currentOuterAltNumber; // which outer alt of rule?
public int numAlts;  // how many alts for this rule total?

public void setTokenPrec(GrammarAST t, int alt) {}
public void binaryAlt(GrammarAST altTree, GrammarAST rewriteTree, int alt) {}
public void ternaryAlt(GrammarAST altTree, GrammarAST rewriteTree, int alt) {}
public void prefixAlt(GrammarAST altTree, GrammarAST rewriteTree, int alt) {}
public void suffixAlt(GrammarAST altTree, GrammarAST rewriteTree, int alt) {}
public void otherAlt(GrammarAST altTree, GrammarAST rewriteTree, int alt) {}
public void setReturnValues(GrammarAST t) {}
}

@rulecatch { }

// TODO: can get parser errors for not matching pattern; make them go away
public
rec_rule returns [boolean isLeftRec]
@init
{
	currentOuterAltNumber = 1;
}
	:	^(	r=RULE id=RULE_REF {ruleName=$id.getText();}
			DOC_COMMENT? ruleModifier?
//			(ARG_ACTION)? shouldn't allow args, right?
			(^(RETURNS a=ARG_ACTION {setReturnValues($a);}))?
//      		( ^(THROWS .+) )? don't allow
      		( ^(LOCALS ARG_ACTION) )? // TODO: copy these to gen'd code
      		(	^(OPTIONS .*)
		    |   ^(AT ID ACTION) // TODO: copy
		    )*
			ruleBlock {$isLeftRec = $ruleBlock.isLeftRec;}
			exceptionGroup
		)
	;

exceptionGroup
    :	exceptionHandler* finallyClause?
    ;

exceptionHandler
	: ^(CATCH ARG_ACTION ACTION)
	;

finallyClause
	: ^(FINALLY ACTION)
	;

ruleModifier
    : PUBLIC
    | PRIVATE
    | PROTECTED
    ;

ruleBlock returns [boolean isLeftRec]
@init{boolean lr=false; this.numAlts = $start.getChildCount();}
	:	^(	BLOCK
			(
				o=outerAlternative[null]
				{if ($o.isLeftRec) $isLeftRec = true;}
				{currentOuterAltNumber++;}
			)+
		)
	;

/** An alt is either prefix, suffix, binary, or ternary operation or "other" */
outerAlternative[GrammarAST rew] returns [boolean isLeftRec]
    :   (binaryMultipleOp)=> binaryMultipleOp
                             {binaryAlt($start, $rew, currentOuterAltNumber); $isLeftRec=true;}
    |   (binary)=>           binary
                             {binaryAlt($start, $rew, currentOuterAltNumber); $isLeftRec=true;}
    |   (ternary)=>          ternary
                             {ternaryAlt($start, $rew, currentOuterAltNumber); $isLeftRec=true;}
    |   (prefix)=>           prefix
                             {prefixAlt($start, $rew, currentOuterAltNumber);}
    |   (suffix)=>           suffix
                             {suffixAlt($start, $rew, currentOuterAltNumber); $isLeftRec=true;}
    |   ^(ALT element+) // "other" case
                             {otherAlt($start, $rew, currentOuterAltNumber);}
    ;

binary
	:	^( ALT recurseNoLabel (op=token)+ {setTokenPrec($op.t, currentOuterAltNumber);} recurse )
	;

binaryMultipleOp
	:	^( ALT recurseNoLabel ^( BLOCK ( ^( ALT (op=token)+ {setTokenPrec($op.t, currentOuterAltNumber);} ) )+ ) recurse )
	;

ternary
	:	^( ALT recurseNoLabel op=token recurse token recurse ) {setTokenPrec($op.t, currentOuterAltNumber);}
	;

prefix
	:	^(	ALT {setTokenPrec((GrammarAST)input.LT(1), currentOuterAltNumber);}
			({!((CommonTree)input.LT(1)).getText().equals(ruleName)}? element)+
			recurse
		 )
	;

suffix : ^( ALT recurseNoLabel {setTokenPrec((GrammarAST)input.LT(1), currentOuterAltNumber);} element+  ) ;

recurse
	:	^(ASSIGN ID recurseNoLabel)
	|	^(PLUS_ASSIGN ID recurseNoLabel)
	|	recurseNoLabel
	;

recurseNoLabel : {((CommonTree)input.LT(1)).getText().equals(ruleName)}? RULE_REF;

token returns [GrammarAST t=null]
	:	^(ASSIGN ID s=token {$t = $s.t;})
	|	^(PLUS_ASSIGN ID s=token {$t = $s.t;})
	|	b=STRING_LITERAL    					{$t = $b;}
    |	^(b=STRING_LITERAL elementOptions)		{$t = $b;}
    |	^(c=TOKEN_REF elementOptions)			{$t = $c;}
	|	c=TOKEN_REF        						{$t = $c;}
	;

elementOptions
    :	^(ELEMENT_OPTIONS elementOption+)
    ;

elementOption
    :	ID
    |   ^(ASSIGN ID ID)
    |   ^(ASSIGN ID STRING_LITERAL)
    ;

element
	:	atom
	|	^(NOT element)
	|	^(RANGE atom atom)
	|	^(ASSIGN ID element)
	|	^(PLUS_ASSIGN ID element)
    |	^(SET setElement+)
    |   RULE_REF
	|	ebnf
	|	ACTION
	|	SEMPRED
	|	EPSILON
	;

setElement
	:	STRING_LITERAL
	|	TOKEN_REF
	;

ebnf:   block
    |   ^( OPTIONAL block )
    |   ^( CLOSURE block )
    |   ^( POSITIVE_CLOSURE block )
    ;

block
    :	^(BLOCK ACTION? alternative+)
    ;

alternative
	:	^(ALT element+)
    ;

atom
	:	^(RULE_REF ARG_ACTION?)
    |  ^(STRING_LITERAL elementOptions)
	|	STRING_LITERAL
    |	^(TOKEN_REF elementOptions)
	|	TOKEN_REF
    |	^(WILDCARD elementOptions)
	|	WILDCARD
	|	^(DOT ID element)
	;
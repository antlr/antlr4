/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
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

/** The definitive ANTLR v3 tree grammar to walk/visit ANTLR v4 grammars.
 *  Parses trees created by ANTLRParser.g.
 */
tree grammar GrammarTreeVisitor;
options {
	language      = Java;
	tokenVocab    = ANTLRParser;
	ASTLabelType  = GrammarAST;
}

// Include the copyright in this source and also the generated source
@header {
/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
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
import org.antlr.v4.runtime.tree.CommonTree; // use updated v4 one not v3
}

@members {
public String grammarName;
public GrammarAST currentRule;
public String currentRuleName;
public GrammarAST currentRuleBlock;
public GrammarAST currentOuterAltRoot;
public int currentOuterAltNumber = 1; // 1..n
public int rewriteEBNFLevel = 0;
public boolean inRewrite;
public boolean currentOuterAltHasRewrite;

public void visitGrammar(ErrorManager errMgr) {
	try { grammarSpec(); }
	catch (org.antlr.runtime.RecognitionException re) {
		errMgr.grammarError(ErrorType.INTERNAL_ERROR,
								   null, re.token, re);
	}
}
public void visitRewrite(ErrorManager errMgr) {
	try { rewrite(); }
	catch (org.antlr.runtime.RecognitionException re) {
		errMgr.grammarError(ErrorType.INTERNAL_ERROR,
								   null, re.token, re);
	}
}
public void visitRewriteEBNF(ErrorManager errMgr) {
	try { rewriteTreeEbnf(); }
	catch (org.antlr.runtime.RecognitionException re) {
		errMgr.grammarError(ErrorType.INTERNAL_ERROR,
								   null, re.token, re);
	}
}

public void rewriteTokenRef(GrammarAST ast, GrammarAST options, GrammarAST arg) { }
public void rewriteStringRef(GrammarAST ast, GrammarAST options) { }
public void rewriteRuleRef(GrammarAST ast) { }
public void rewriteLabelRef(GrammarAST ast) { }
public void rewriteAction(GrammarAST ast) { }
}

grammarSpec
    :   ^(GRAMMAR ID {grammarName=$ID.text;} DOC_COMMENT? prequelConstruct* rules mode*)
	;

prequelConstruct
	:   optionsSpec
    |   delegateGrammars
    |   tokensSpec
    |   attrScope
    |   action
    ;

optionsSpec
	:	^(OPTIONS option*)
    ;

option
    :   ^(ASSIGN ID optionValue)
    ;

optionValue
    :   ID
    |   STRING_LITERAL
    |   INT
    |   STAR
    ;

delegateGrammars
	:   ^(IMPORT delegateGrammar+)
	;

delegateGrammar
    :   ^(ASSIGN ID ID)
    |   ID
    ;

tokensSpec
	:   ^(TOKENS tokenSpec+)
	;

tokenSpec
	:	^(ASSIGN ID STRING_LITERAL)
	|	ID
	;

attrScope
	:	^(SCOPE ID ACTION)
	;

action
	:	^(AT ID? ID ACTION)
	;

rules
    : ^(RULES rule*)
    ;

mode:	^( MODE ID rule+ ) ;

rule:   ^(	RULE ID {currentRuleName=$ID.text; currentRule=$RULE;}
			DOC_COMMENT? ruleModifiers? ARG_ACTION?
      		ruleReturns? rulePrequel* ruleBlock exceptionGroup
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

rulePrequel
    :   throwsSpec
    |   ruleScopeSpec
    |   optionsSpec
    |   ruleAction
    ;

ruleReturns
	: ^(RETURNS ARG_ACTION)
	;
throwsSpec
    : ^(THROWS ID+)
    ;

ruleScopeSpec
	:	^(SCOPE ACTION)
	|	^(SCOPE ID+)
	;

ruleAction
	:	^(AT ID ACTION)
	;

ruleModifiers
    : ^(RULEMODIFIERS ruleModifier+)
    ;

ruleModifier
    : PUBLIC
    | PRIVATE
    | PROTECTED
    | FRAGMENT
    ;

altList
    : alternative+
    ;

ruleBlock
    :	{currentOuterAltNumber=0;}
    	^(	BLOCK
    		(	{
    			currentOuterAltRoot = (GrammarAST)input.LT(1);
				currentOuterAltNumber++;
				currentOuterAltHasRewrite=false;
				inRewrite=false;
				currentOuterAltHasRewrite = false;
				if ( currentOuterAltRoot.getType()==ALT_REWRITE ) {
					currentOuterAltHasRewrite=true;
				}
				}
    			alternative
    		)+
    	)
    ;

alternative
    :	^(ALT_REWRITE {inRewrite=true;} alternative rewrite)
    |	^(ALT EPSILON)
    |   ^(ALT element+)
    ;

element
	:	labeledElement
	|	atom
	|	subrule
	|   ACTION
    |   FORCED_ACTION
	|   SEMPRED
	|	GATED_SEMPRED
	|	treeSpec
	|	^(ROOT astOperand)
	|	^(BANG astOperand)
	|	^(NOT blockSet)
	|	^(NOT block)
	;

astOperand
	:	atom
	|	^(NOT blockSet)
	|	^(NOT block)
	;

labeledElement
	:	^((ASSIGN|PLUS_ASSIGN) ID element)
	;

treeSpec
    : ^(TREE_BEGIN element+)
    ;

subrule
	:	^(blockSuffix block)
	| 	block
    ;

blockSuffix
    : ebnfSuffix
    | ROOT
    | IMPLIES
    | BANG
    ;

ebnfSuffix
	:	OPTIONAL
  	|	CLOSURE
   	|	POSITIVE_CLOSURE
	;

atom:	range
	|	^(DOT ID terminal)
	|	^(DOT ID ruleref)
    |	^(WILDCARD elementOptions)
    |	WILDCARD
    |   terminal
    |	blockSet
    |   ruleref
    ;

blockSet
	:	^(SET setElement+)
	;
	
setElement
	:	STRING_LITERAL
	|	TOKEN_REF
	;

block
    :	^(BLOCK optionsSpec? ruleAction* ACTION? altList)
    ;

ruleref
    :	^(RULE_REF ARG_ACTION?)
    ;

range
    : ^(RANGE STRING_LITERAL STRING_LITERAL)
    ;

terminal
    :  ^(STRING_LITERAL elementOptions)
    |	STRING_LITERAL
    |	^(TOKEN_REF ARG_ACTION elementOptions)
    |	^(TOKEN_REF ARG_ACTION)
    |	^(TOKEN_REF elementOptions)
    |	TOKEN_REF
    ;

elementOptions
    :	^(ELEMENT_OPTIONS elementOption+)
    ;

elementOption
    :	ID
    |   ^(ASSIGN ID ID)
    |   ^(ASSIGN ID STRING_LITERAL)
    ;

rewrite
	:	predicatedRewrite* nakedRewrite
	;

predicatedRewrite
	:	^(ST_RESULT SEMPRED rewriteAlt)
	|	^(RESULT SEMPRED rewriteAlt)
	;

nakedRewrite
	:	^(ST_RESULT rewriteAlt)
	|	^(RESULT rewriteAlt)
	;

rewriteAlt
@init {rewriteEBNFLevel=0;}
    :	rewriteTemplate
    |	rewriteTreeAlt
    |	ETC
    |	EPSILON
    ;

rewriteTreeAlt
    :	^(ALT rewriteTreeElement+)
    ;

rewriteTreeElement
	:	rewriteTreeAtom
	|	rewriteTree
	|   rewriteTreeEbnf
	;

rewriteTreeAtom
    :   ^(TOKEN_REF elementOptions ARG_ACTION)	{rewriteTokenRef($start,$elementOptions.start,$ARG_ACTION);}
    |   ^(TOKEN_REF elementOptions)				{rewriteTokenRef($start,$elementOptions.start,null);}
    |   ^(TOKEN_REF ARG_ACTION)					{rewriteTokenRef($start,null,$ARG_ACTION);}
	|   TOKEN_REF								{rewriteTokenRef($start,null,null);}
    |   RULE_REF								{rewriteRuleRef($start);}
	|   ^(STRING_LITERAL elementOptions)		{rewriteStringRef($start,$elementOptions.start);}
	|   STRING_LITERAL							{rewriteStringRef($start,null);}
	|   LABEL									{rewriteLabelRef($start);}
	|	ACTION									{rewriteAction($start);}
	;

rewriteTreeEbnf
	:	^(ebnfSuffix ^(REWRITE_BLOCK {rewriteEBNFLevel++;} rewriteTreeAlt {rewriteEBNFLevel--;}))
	;
	
rewriteTree
	:	^(TREE_BEGIN rewriteTreeAtom rewriteTreeElement* )
	;

rewriteTemplate
	:	^(TEMPLATE rewriteTemplateArgs? DOUBLE_QUOTE_STRING_LITERAL)
	|	^(TEMPLATE rewriteTemplateArgs? DOUBLE_ANGLE_STRING_LITERAL)
	|	rewriteTemplateRef
	|	rewriteIndirectTemplateHead
	|	ACTION
	;

rewriteTemplateRef
	:	^(TEMPLATE ID rewriteTemplateArgs?)
	;

rewriteIndirectTemplateHead
	:	^(TEMPLATE ACTION rewriteTemplateArgs?)
	;

rewriteTemplateArgs
	:	^(ARGLIST rewriteTemplateArg+)
	;

rewriteTemplateArg
	:   ^(ARG ID ACTION)
	;

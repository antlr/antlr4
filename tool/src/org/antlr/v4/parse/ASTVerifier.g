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

/** The definitive ANTLR v3 tree grammar to parse ANTLR v4 grammars.
 *  Parses trees created in ANTLRParser.g.
 */
tree grammar ASTVerifier;
options {
	language      = Java;
	tokenVocab    = ANTLRParser;
	ASTLabelType  = GrammarAST;
}

// Include the copyright in this source and also the generated source
@header {
/*
 [The "BSD license"]
 Copyright (c) 2005-2009 Terence Parr
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
public String getErrorMessage(RecognitionException e,
                              String[] tokenNames)
{
    List stack = getRuleInvocationStack(e, this.getClass().getName());
    String msg = null;
        String inputContext =
          input.LT(-3) == null ? "" : ((Tree)input.LT(-3)).getText()+" "+
          input.LT(-2) == null ? "" : ((Tree)input.LT(-2)).getText()+" "+
          input.LT(-1) == null ? "" : ((Tree)input.LT(-1)).getText()+" >>>"+
          input.LT(1) == null ? "" : ((Tree)input.LT(1)).getText()+"<<< "+
          input.LT(2) == null ? "" : ((Tree)input.LT(2)).getText()+" "+
          input.LT(3) == null ? "" : ((Tree)input.LT(3)).getText();
    if ( e instanceof NoViableAltException ) {
       NoViableAltException nvae = (NoViableAltException)e;
       msg = " no viable alt; token="+e.token+
          " (decision="+nvae.decisionNumber+
          " state "+nvae.stateNumber+")"+
          " decision=<<"+nvae.grammarDecisionDescription+">>";
    }
    else {
       msg = super.getErrorMessage(e, tokenNames);
    }
    return stack+" "+msg+"\ncontext=..."+inputContext+"...";
}
public String getTokenErrorDisplay(Token t) {
    return t.toString();
}
public void traceIn(String ruleName, int ruleIndex)  {
   	System.out.print("enter "+ruleName+" "+
                     ((GrammarAST)input.LT(1)).token+" "+
                     ((GrammarAST)input.LT(2)).token+" "+
                     ((GrammarAST)input.LT(3)).token+" "+
                     ((GrammarAST)input.LT(4)).token);
	if ( state.backtracking>0 ) {
		System.out.print(" backtracking="+state.backtracking);
	}
	System.out.println();
}
	protected void mismatch(IntStream input, int ttype, BitSet follow)
		throws RecognitionException {
		throw new MismatchedTokenException(ttype, input);
	}
	public void recoverFromMismatchedToken(IntStream input,
										   RecognitionException e, BitSet follow)
		throws RecognitionException

	{
		throw e;
	}
}

// Alter code generation so catch-clauses get replace with // this action.
@rulecatch { catch (RecognitionException e) {
throw e;
}
}

grammarSpec
    :   ^(GRAMMAR ID DOC_COMMENT? prequelConstruct* rules mode*)
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

optionValue returns [String v]
@init {$v = $start.token.getText();}
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

rule:   ^( RULE ID DOC_COMMENT? ruleModifiers? ARG_ACTION?
      	   ruleReturns? rulePrequel* altListAsBlock exceptionGroup
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

altListAsBlock
    : ^(BLOCK altList)
    ;

alternative
    :	^(ALT_REWRITE alternative rewrite)
    |	^(ALT EPSILON)
    |   elements
    ;

elements
    : ^(ALT element+)
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
    :   ^(TOKEN_REF elementOptions ARG_ACTION)
    |   ^(TOKEN_REF elementOptions)
    |   ^(TOKEN_REF ARG_ACTION)
	|   TOKEN_REF
    |   RULE_REF
	|   ^(STRING_LITERAL elementOptions)
	|   STRING_LITERAL
	|   LABEL
	|	ACTION
	;

rewriteTreeEbnf
	:	^(ebnfSuffix ^(REWRITE_BLOCK rewriteTreeAlt))
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

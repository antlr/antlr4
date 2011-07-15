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
import java.lang.reflect.Method;
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

public GrammarTreeVisitor() { this(null); }

public ErrorManager getErrorManager() { return null; }

public void visitGrammar(GrammarAST t) { visit(t, "grammarSpec"); }
public void visitRewrite(GrammarAST t) { visit(t, "rewrite"); }
public void visitRewriteEBNF(GrammarAST t) { visit(t, "rewriteTreeEbnf"); }
public void visit(GrammarAST t, String ruleName) {
	CommonTreeNodeStream nodes = new CommonTreeNodeStream(t);
	setTreeNodeStream(nodes);
	try {
		Method m = getClass().getMethod(ruleName);
		m.invoke(this);
	}
	catch (Exception e) {
		ErrorManager errMgr = getErrorManager();
		if ( errMgr==null ) System.err.println("can't find rule "+ruleName+
											   " or tree structure error: "+t.toStringTree()
											  );
		else errMgr.toolError(ErrorType.INTERNAL_ERROR, e);
	}
}

public void discoverGrammar(GrammarRootAST root, GrammarAST ID) { }
public void finishPrequels(GrammarAST firstPrequel) { }
public void finishGrammar(GrammarRootAST root, GrammarAST ID) { }

public void grammarOption(GrammarAST ID, String value) { }
public void ruleOption(GrammarAST ID, String value) { }
public void blockOption(GrammarAST ID, String value) { }
public void tokenAlias(GrammarAST ID, GrammarAST literal) { }

public void importGrammar(GrammarAST label, GrammarAST ID) { }

public void modeDef(GrammarAST m, GrammarAST ID) { }

public void discoverRules(GrammarAST rules) { }
public void finishRules(GrammarAST rule) { }
public void discoverRule(GrammarAST rule, GrammarAST ID) { }
public void finishRule(GrammarAST rule, GrammarAST ID) { }
public void discoverAlt(GrammarAST alt) { }
public void finishAlt(GrammarAST alt) { }
public void discoverAltWithRewrite(GrammarAST alt) { }
public void finishAltWithRewrite(GrammarAST alt) { }
public void discoverSTRewrite(GrammarAST rew) { }
public void discoverTreeRewrite(GrammarAST rew) { }

public void ruleRef(GrammarAST ref, GrammarAST arg) { }
public void tokenRef(GrammarAST ref, GrammarAST options) { }
public void terminalOption(TerminalAST t, GrammarAST ID, GrammarAST value) { }
public void stringRef(GrammarAST ref, GrammarAST options) { }
public void wildcardRef(GrammarAST ref, GrammarAST options) { }

public void rootOp(GrammarAST op, GrammarAST opnd) { }
public void bangOp(GrammarAST op, GrammarAST opnd) { }

public void discoverRewrites(GrammarAST result) { }
public void finishRewrites(GrammarAST result) { }
public void rewriteTokenRef(GrammarAST ast, GrammarAST options, GrammarAST arg) { }
public void rewriteTerminalOption(TerminalAST t, GrammarAST ID, GrammarAST value) { }
public void rewriteStringRef(GrammarAST ast, GrammarAST options) { }
public void rewriteRuleRef(GrammarAST ast) { }
public void rewriteLabelRef(GrammarAST ast) { }
public void rewriteAction(GrammarAST ast) { }
}

grammarSpec
    :   ^(	GRAMMAR ID {grammarName=$ID.text;} DOC_COMMENT?
    		{discoverGrammar((GrammarRootAST)$GRAMMAR, $ID);}
 		   	prequelConstructs
    		{finishPrequels($prequelConstructs.start);}
 		   	rules mode*
    		{finishGrammar((GrammarRootAST)$GRAMMAR, $ID);}
 		 )
	;

prequelConstructs
	:	prequelConstruct*
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
    	{
    	if ( inContext("RULE") ) ruleOption($ID, $optionValue.v);
    	else if ( inContext("BLOCK") ) blockOption($ID, $optionValue.v);
    	else grammarOption($ID, $optionValue.v);
    	}
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
    :   ^(ASSIGN label=ID id=ID)	{importGrammar($label, $id);}
    |   id=ID						{importGrammar(null, $id);}
    ;

tokensSpec
	:   ^(TOKENS tokenSpec+)
	;

tokenSpec
	:	^(ASSIGN ID STRING_LITERAL)	{tokenAlias($ID, $STRING_LITERAL);}
	|	ID
	;

attrScope
	:	^(SCOPE ID ACTION)
	;

action
	:	^(AT ID? ID ACTION)
	;

rules
    : ^(RULES {discoverRules($RULES);} rule* {finishRules($RULES);})
    ;

mode:	^( MODE ID {modeDef($MODE, $ID);} rule+ ) ;

rule:   ^(	RULE ID {currentRuleName=$ID.text; currentRule=$RULE; discoverRule($RULE, $ID);}
			DOC_COMMENT? ruleModifiers? ARG_ACTION?
      		ruleReturns? rulePrequel* ruleBlock exceptionGroup
      		{finishRule($RULE, $ID);}
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
@init {
	if ( $start.getType()==ALT_REWRITE ) discoverAltWithRewrite($start);
	else discoverAlt($start);
}
@after {
	if ( $start.getType()==ALT_REWRITE ) finishAltWithRewrite($start);
	else finishAlt($start);
}
    :	^(ALT_REWRITE alternative {inRewrite=true;} rewrite)
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
	|	^(ROOT astOperand)	{rootOp($ROOT, $astOperand.start);}
	|	^(BANG astOperand)	{bangOp($BANG, $astOperand.start);}
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
    |	^(WILDCARD elementOptions)	{wildcardRef($WILDCARD, $elementOptions.start);}
    |	WILDCARD					{wildcardRef($WILDCARD, null);}
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
    :	^(RULE_REF ARG_ACTION?) {ruleRef($RULE_REF, $ARG_ACTION);}
    ;

range
    : ^(RANGE STRING_LITERAL STRING_LITERAL)
    ;

terminal
    :  ^(STRING_LITERAL elementOptions)
    								{stringRef($STRING_LITERAL, $elementOptions.start);}
    |	STRING_LITERAL				{stringRef($STRING_LITERAL, null);}
    |	^(TOKEN_REF elementOptions)	{tokenRef($TOKEN_REF, $elementOptions.start);}
    |	TOKEN_REF	    			{tokenRef($TOKEN_REF, null);}
    ;

elementOptions
    :	^(ELEMENT_OPTIONS elementOption[(TerminalAST)$start.getParent()]+)
    ;

elementOption[TerminalAST t]
    :	ID								{terminalOption(t, $ID, null);}
    |   ^(ASSIGN id=ID v=ID)			{terminalOption(t, $id, $v);}
    |   ^(ASSIGN ID v=STRING_LITERAL)	{terminalOption(t, $ID, $v);}
    ;

rewrite
	:	{discoverRewrites($start);} predicatedRewrite* nakedRewrite {finishRewrites($start);} 
	;

predicatedRewrite
	:	^(ST_RESULT SEMPRED {discoverSTRewrite($start);} rewriteAlt)
	|	^(RESULT SEMPRED {discoverTreeRewrite($start);} rewriteAlt)
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
    :   ^(TOKEN_REF rewriteElementOptions ARG_ACTION)
    										{rewriteTokenRef($start,$rewriteElementOptions.start,$ARG_ACTION);}
    |   ^(TOKEN_REF rewriteElementOptions)	{rewriteTokenRef($start,$rewriteElementOptions.start,null);}
    |   ^(TOKEN_REF ARG_ACTION)				{rewriteTokenRef($start,null,$ARG_ACTION);}
	|   TOKEN_REF							{rewriteTokenRef($start,null,null);}
    |   RULE_REF							{rewriteRuleRef($start);}
	|   ^(STRING_LITERAL rewriteElementOptions)
											{rewriteStringRef($start,$rewriteElementOptions.start);}
	|   STRING_LITERAL						{rewriteStringRef($start,null);}
	|   LABEL								{rewriteLabelRef($start);}
	|	ACTION								{rewriteAction($start);}
	;

rewriteElementOptions
    :	^(ELEMENT_OPTIONS rewriteElementOption[(TerminalAST)$start.getParent()]+)
    ;
   
rewriteElementOption[TerminalAST t]
    :	ID								{rewriteTerminalOption(t, $ID, null);}
    |   ^(ASSIGN id=ID v=ID)			{rewriteTerminalOption(t, $id, $v);}
    |   ^(ASSIGN ID v=STRING_LITERAL)	{rewriteTerminalOption(t, $ID, $v);}
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

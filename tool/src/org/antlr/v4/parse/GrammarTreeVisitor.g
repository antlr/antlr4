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
 *
 *  Rather than have multiple tree grammars, one for each visit, I'm
 *  creating this generic visitor that knows about context. All of the
 *  boilerplate pattern recognition is done here. Then, subclasses can
 *  override the methods they care about. This prevents a lot of the same
 *  context tracking stuff like "set current alternative for current
 *  rule node" that is repeated in lots of tree filters.
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
import org.antlr.v4.tool.ast.*;
import java.lang.reflect.Method;
}

@members {
public String grammarName;
public GrammarAST currentRuleAST;
public String currentModeName = LexerGrammar.DEFAULT_MODE_NAME;
public String currentRuleName;
//public GrammarAST currentRuleBlock;
public GrammarAST currentOuterAltRoot;
public int currentOuterAltNumber = 1; // 1..n
public int rewriteEBNFLevel = 0;

public GrammarTreeVisitor() { this(null); }

public ErrorManager getErrorManager() { return null; }

public void visitGrammar(GrammarAST t) { visit(t, "grammarSpec"); }
public void visit(GrammarAST t, String ruleName) {
	CommonTreeNodeStream nodes = new CommonTreeNodeStream(t);
	setTreeNodeStream(nodes);
	try {
		Method m = getClass().getMethod(ruleName);
		m.invoke(this);
	}
	catch (Exception e) {
		ErrorManager errMgr = getErrorManager();
		if ( errMgr==null ) {
			System.err.println("can't find rule "+ruleName+
							   " or tree structure error: "+t.toStringTree()
							   );
			e.printStackTrace(System.err);
		}
		else errMgr.toolError(ErrorType.INTERNAL_ERROR, e);
	}
}

public void discoverGrammar(GrammarRootAST root, GrammarAST ID) { }
public void finishPrequels(GrammarAST firstPrequel) { }
public void finishGrammar(GrammarRootAST root, GrammarAST ID) { }

public void grammarOption(GrammarAST ID, GrammarAST valueAST) { }
public void ruleOption(GrammarAST ID, GrammarAST valueAST) { }
public void blockOption(GrammarAST ID, GrammarAST valueAST) { }
public void tokenAlias(GrammarAST ID, GrammarAST literal) { }
public void globalNamedAction(GrammarAST scope, GrammarAST ID, ActionAST action) { }
public void importGrammar(GrammarAST label, GrammarAST ID) { }

public void modeDef(GrammarAST m, GrammarAST ID) { }

public void discoverRules(GrammarAST rules) { }
public void finishRules(GrammarAST rule) { }
public void discoverRule(RuleAST rule, GrammarAST ID, List<GrammarAST> modifiers,
						 ActionAST arg, ActionAST returns, GrammarAST thrws,
						 GrammarAST options, GrammarAST locals,
						 List<GrammarAST> actions,
						 GrammarAST block) { }
public void finishRule(RuleAST rule, GrammarAST ID, GrammarAST block) { }
public void discoverLexerRule(RuleAST rule, GrammarAST ID, List<GrammarAST> modifiers,
                              GrammarAST block) { }
public void finishLexerRule(RuleAST rule, GrammarAST ID, GrammarAST block) { }
public void ruleCatch(GrammarAST arg, ActionAST action) { }
public void finallyAction(ActionAST action) { }
public void discoverOuterAlt(AltAST alt) { }
public void finishOuterAlt(AltAST alt) { }
public void discoverAlt(AltAST alt) { }
public void finishAlt(AltAST alt) { }

public void ruleRef(GrammarAST ref, ActionAST arg) { }
public void tokenRef(TerminalAST ref) { }
public void elementOption(GrammarASTWithOptions t, GrammarAST ID, GrammarAST valueAST) { }
public void stringRef(TerminalAST ref) { }
public void wildcardRef(GrammarAST ref) { }
public void actionInAlt(ActionAST action) { }
public void sempredInAlt(PredAST pred) { }
public void label(GrammarAST op, GrammarAST ID, GrammarAST element) { }
public void lexerCallCommand(int outerAltNumber, GrammarAST ID, GrammarAST arg) { }
public void lexerCommand(int outerAltNumber, GrammarAST ID) { }

	public void traceIn(String ruleName, int ruleIndex)  {
		System.err.println("enter "+ruleName+": "+input.LT(1));
	}

	public void traceOut(String ruleName, int ruleIndex)  {
		System.err.println("exit "+ruleName+": "+input.LT(1));
	}
}

grammarSpec
    :   ^(	GRAMMAR ID {grammarName=$ID.text;} DOC_COMMENT?
    		{discoverGrammar((GrammarRootAST)$GRAMMAR, $ID);}
 		   	prequelConstructs
    		{finishPrequels($prequelConstructs.firstOne);}
 		   	rules mode*
    		{finishGrammar((GrammarRootAST)$GRAMMAR, $ID);}
 		 )
	;

prequelConstructs returns [GrammarAST firstOne=null]
	:	{$firstOne=$start;} prequelConstruct+
	|
	;

prequelConstruct
	:   optionsSpec
    |   delegateGrammars
    |   tokensSpec
    |   action
    ;

optionsSpec
	:	^(OPTIONS option*)
    ;

option
@init {
boolean rule = inContext("RULE ...");
boolean block = inContext("BLOCK ...");
}
    :   ^(a=ASSIGN ID v=optionValue)
    	{
    	if ( block ) blockOption($ID, $v.start); // most specific first
    	else if ( rule ) ruleOption($ID, $v.start);
    	else grammarOption($ID, $v.start);
    	}
    ;

optionValue returns [String v]
@init {$v = $start.token.getText();}
    :   ID
    |   STRING_LITERAL
    |   INT
    ;

delegateGrammars
	:   ^(IMPORT delegateGrammar+)
	;

delegateGrammar
    :   ^(ASSIGN label=ID id=ID)	{importGrammar($label, $id);}
    |   id=ID						{importGrammar(null, $id);}
    ;

tokensSpec
	:   ^(TOKENS_SPEC tokenSpec+)
	;

tokenSpec
	:	^(ASSIGN ID STRING_LITERAL)	{tokenAlias($ID, $STRING_LITERAL);}
	|	ID							{tokenAlias($ID, null);}
	;

action
	:	^(AT sc=ID? name=ID ACTION) {globalNamedAction($sc, $name, (ActionAST)$ACTION);}
	;

rules
    : ^(RULES {discoverRules($RULES);} (rule|lexerRule)* {finishRules($RULES);})
    ;

mode : ^( MODE ID {currentModeName=$ID.text; modeDef($MODE, $ID);} lexerRule+ ) ;

lexerRule
@init {
List<GrammarAST> mods = new ArrayList<GrammarAST>();
currentOuterAltNumber=0;
}
	:	^(	RULE TOKEN_REF
			{currentRuleName=$TOKEN_REF.text; currentRuleAST=$RULE;}
			DOC_COMMENT? (^(RULEMODIFIERS m=FRAGMENT {mods.add($m);}))?
      		{discoverLexerRule((RuleAST)$RULE, $TOKEN_REF, mods, (GrammarAST)input.LT(1));}
      		lexerRuleBlock
      		{
      		finishLexerRule((RuleAST)$RULE, $TOKEN_REF, $lexerRuleBlock.start);
      		currentRuleName=null; currentRuleAST=null;
      		}
      	 )
	;

rule
@init {
List<GrammarAST> mods = new ArrayList<GrammarAST>();
List<GrammarAST> actions = new ArrayList<GrammarAST>(); // track roots
currentOuterAltNumber=0;
}
	:   ^(	RULE RULE_REF {currentRuleName=$RULE_REF.text; currentRuleAST=$RULE;}
			DOC_COMMENT? (^(RULEMODIFIERS (m=ruleModifier{mods.add($m.start);})+))?
			ARG_ACTION?
      		ret=ruleReturns?
      		thr=throwsSpec?
      		loc=locals?
      		(	opts=optionsSpec
		    |   a=ruleAction {actions.add($a.start);}
		    )*
      		{discoverRule((RuleAST)$RULE, $RULE_REF, mods, (ActionAST)$ARG_ACTION,
      					  $ret.start!=null?(ActionAST)$ret.start.getChild(0):null,
      					  $thr.start, $opts.start,
      					  $loc.start!=null?(ActionAST)$loc.start.getChild(0):null,
      					  actions, (GrammarAST)input.LT(1));}
      		ruleBlock exceptionGroup
      		{finishRule((RuleAST)$RULE, $RULE_REF, $ruleBlock.start); currentRuleName=null; currentRuleAST=null;}
      	 )
    ;

exceptionGroup
    :	exceptionHandler* finallyClause?
    ;

exceptionHandler
	: ^(CATCH ARG_ACTION ACTION)	{ruleCatch($ARG_ACTION, (ActionAST)$ACTION);}
	;

finallyClause
	: ^(FINALLY ACTION)				{finallyAction((ActionAST)$ACTION);}
	;

locals
	:	^(LOCALS ARG_ACTION)
	;

ruleReturns
	: ^(RETURNS ARG_ACTION)
	;
throwsSpec
    : ^(THROWS ID+)
    ;

ruleAction
	:	^(AT ID ACTION)
	;

ruleModifier
    : PUBLIC
    | PRIVATE
    | PROTECTED
    | FRAGMENT
    ;

lexerRuleBlock
    :	^(	BLOCK
    		(	{
    			currentOuterAltRoot = (GrammarAST)input.LT(1);
				currentOuterAltNumber++;
				}
    			lexerOuterAlternative
    		)+
    	)
    ;

ruleBlock
    :	^(	BLOCK
    		(	{
    			currentOuterAltRoot = (GrammarAST)input.LT(1);
				currentOuterAltNumber++;
				}
    			outerAlternative
    		)+
    	)
    ;

lexerOuterAlternative
@init {
	discoverOuterAlt((AltAST)$start);
}
@after {
	finishOuterAlt((AltAST)$start);
}
	:	lexerAlternative 
	;


outerAlternative
@init {
	discoverOuterAlt((AltAST)$start);
}
@after {
	finishOuterAlt((AltAST)$start);
}
	:	alternative 
	;

lexerAlternative
	:	^(LEXER_ALT_ACTION lexerElements lexerCommand+)
    |   lexerElements
    ;

lexerElements
    :	^(ALT lexerElement+)
    ;

lexerElement
	:	labeledLexerElement
	|	lexerAtom
	|	lexerSubrule
	|   ACTION						{actionInAlt((ActionAST)$ACTION);}
	|   SEMPRED						{sempredInAlt((PredAST)$SEMPRED);}
	|   ^(ACTION elementOptions)	{actionInAlt((ActionAST)$ACTION);}
	|   ^(SEMPRED elementOptions)	{sempredInAlt((PredAST)$SEMPRED);}
	;

labeledLexerElement
    :   ^((ASSIGN|PLUS_ASSIGN) ID (lexerAtom|block))
	;
	
lexerBlock
 	:	^(BLOCK lexerAlternative+)
    ;

lexerAtom
    :   terminal
    |   ^(NOT blockSet)
    |   blockSet
    |   ^(WILDCARD elementOptions)
    |   WILDCARD
    |	LEXER_CHAR_SET
    |   range
    ;

actionElement
	:	ACTION
	|   ^(ACTION elementOptions)
	|   SEMPRED
	|   ^(SEMPRED elementOptions)
	;
    
alternative
@init {
	discoverAlt((AltAST)$start);
}
@after {
	finishAlt((AltAST)$start);
}
	:	^(ALT element+)
	|	^(ALT EPSILON)
    ;

lexerCommand
	:	^(LEXER_ACTION_CALL ID lexerCommandExpr)
        {lexerCallCommand(currentOuterAltNumber, $ID, $lexerCommandExpr.start);}
	|	ID
        {lexerCommand(currentOuterAltNumber, $ID);}
	;

lexerCommandExpr
	:	ID 
	|	INT
	;
	
element
	:	labeledElement
	|	atom
	|	subrule
	|   ACTION						{actionInAlt((ActionAST)$ACTION);}
	|   SEMPRED						{sempredInAlt((PredAST)$SEMPRED);}
	|   ^(ACTION elementOptions)	{actionInAlt((ActionAST)$ACTION);}
	|   ^(SEMPRED elementOptions)	{sempredInAlt((PredAST)$SEMPRED);}
	
	|	^(NOT blockSet)
	|	^(NOT block)
	;

astOperand
	:	atom
	|	^(NOT blockSet)
	|	^(NOT block)
	;

labeledElement
	:	^((ASSIGN|PLUS_ASSIGN) ID element) {label($start, $ID, $element.start);}
	;

subrule
	:	^(blockSuffix block)
	| 	block
    ;

lexerSubrule
	:	^(blockSuffix lexerBlock)
	| 	lexerBlock
    ;

blockSuffix
    : ebnfSuffix
    ;

ebnfSuffix
	:	OPTIONAL
  	|	CLOSURE
   	|	POSITIVE_CLOSURE
	;

atom:	^(DOT ID terminal)
	|	^(DOT ID ruleref)
    |	^(WILDCARD elementOptions)	{wildcardRef($WILDCARD);}
    |	WILDCARD					{wildcardRef($WILDCARD);}
    |   terminal
    |	blockSet
    |   ruleref
    ;

blockSet
	:	^(SET setElement+)
	;

setElement
	:	STRING_LITERAL	{stringRef((TerminalAST)$STRING_LITERAL);}
	|	TOKEN_REF		{tokenRef((TerminalAST)$TOKEN_REF);}
	|	^(RANGE a=STRING_LITERAL b=STRING_LITERAL)
		{
		stringRef((TerminalAST)$a);
		stringRef((TerminalAST)$b);
		}
        |       LEXER_CHAR_SET
	;

block
    :	^(BLOCK optionsSpec? ruleAction* ACTION? alternative+)
    ;

ruleref
    :	^(RULE_REF arg=ARG_ACTION?)
    	{
    	ruleRef($RULE_REF, (ActionAST)$ARG_ACTION);
    	if ( $arg!=null ) actionInAlt((ActionAST)$arg);
    	}
    ;

range
    : ^(RANGE STRING_LITERAL STRING_LITERAL)
    ;

terminal
    :  ^(STRING_LITERAL elementOptions)
    								{stringRef((TerminalAST)$STRING_LITERAL);}
    |	STRING_LITERAL				{stringRef((TerminalAST)$STRING_LITERAL);}
    |	^(TOKEN_REF elementOptions)	{tokenRef((TerminalAST)$TOKEN_REF);}
    |	TOKEN_REF	    			{tokenRef((TerminalAST)$TOKEN_REF);}
    ;

elementOptions
    :	^(ELEMENT_OPTIONS elementOption[(GrammarASTWithOptions)$start.getParent()]+)
    ;

elementOption[GrammarASTWithOptions t]
    :	ID								{elementOption(t, $ID, null);}
    |   ^(ASSIGN id=ID v=ID)			{elementOption(t, $id, $v);}
    |   ^(ASSIGN ID v=STRING_LITERAL)	{elementOption(t, $ID, $v);}
    |   ^(ASSIGN ID v=ACTION)			{elementOption(t, $ID, $v);}
    ;
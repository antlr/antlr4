/*
 * [The "BSD license"]
 *  Copyright (c) 2012-2016 Terence Parr
 *  Copyright (c) 2012-2016 Sam Harwell
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
import org.antlr.v4.Tool;
import org.antlr.v4.tool.*;
import org.antlr.v4.tool.ast.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
}

@members {
public String grammarName;
public GrammarAST currentRuleAST;
public String currentModeName = LexerGrammar.DEFAULT_MODE_NAME;
public String currentRuleName;
public GrammarAST currentOuterAltRoot;
public int currentOuterAltNumber = 1; // 1..n
public int rewriteEBNFLevel = 0;

public GrammarTreeVisitor() { this(null); }

// Should be abstract but can't make gen'd parser abstract;
// subclasses should implement else everything goes to stderr!
public ErrorManager getErrorManager() { return null; }

public void visitGrammar(GrammarAST t) { visit(t, "grammarSpec"); }
public void visit(GrammarAST t, String ruleName) {
	CommonTreeNodeStream nodes = new CommonTreeNodeStream(new GrammarASTAdaptor(), t);
	setTreeNodeStream(nodes);
	try {
		Method m = getClass().getMethod(ruleName);
		m.invoke(this);
	}
	catch (Throwable e) {
		ErrorManager errMgr = getErrorManager();
		if ( e instanceof InvocationTargetException ) {
			e = e.getCause();
		}
		//e.printStackTrace(System.err);
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
public void defineToken(GrammarAST ID) { }
public void defineChannel(GrammarAST ID) { }
public void globalNamedAction(GrammarAST scope, GrammarAST ID, ActionAST action) { }
public void importGrammar(GrammarAST label, GrammarAST ID) { }

public void modeDef(GrammarAST m, GrammarAST ID) { }

public void discoverRules(GrammarAST rules) { }
public void finishRules(GrammarAST rule) { }
public void discoverRule(RuleAST rule, GrammarAST ID, List<GrammarAST> modifiers,
						 ActionAST arg, ActionAST returns, GrammarAST thrws,
						 GrammarAST options, ActionAST locals,
						 List<GrammarAST> actions,
						 GrammarAST block) { }
public void finishRule(RuleAST rule, GrammarAST ID, GrammarAST block) { }
public void discoverLexerRule(RuleAST rule, GrammarAST ID, List<GrammarAST> modifiers, GrammarAST options,
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

protected void enterGrammarSpec(GrammarAST tree) { }
protected void exitGrammarSpec(GrammarAST tree) { }

protected void enterPrequelConstructs(GrammarAST tree) { }
protected void exitPrequelConstructs(GrammarAST tree) { }

protected void enterPrequelConstruct(GrammarAST tree) { }
protected void exitPrequelConstruct(GrammarAST tree) { }

protected void enterOptionsSpec(GrammarAST tree) { }
protected void exitOptionsSpec(GrammarAST tree) { }

protected void enterOption(GrammarAST tree) { }
protected void exitOption(GrammarAST tree) { }

protected void enterOptionValue(GrammarAST tree) { }
protected void exitOptionValue(GrammarAST tree) { }

protected void enterDelegateGrammars(GrammarAST tree) { }
protected void exitDelegateGrammars(GrammarAST tree) { }

protected void enterDelegateGrammar(GrammarAST tree) { }
protected void exitDelegateGrammar(GrammarAST tree) { }

protected void enterTokensSpec(GrammarAST tree) { }
protected void exitTokensSpec(GrammarAST tree) { }

protected void enterTokenSpec(GrammarAST tree) { }
protected void exitTokenSpec(GrammarAST tree) { }

protected void enterChannelsSpec(GrammarAST tree) { }
protected void exitChannelsSpec(GrammarAST tree) { }

protected void enterChannelSpec(GrammarAST tree) { }
protected void exitChannelSpec(GrammarAST tree) { }

protected void enterAction(GrammarAST tree) { }
protected void exitAction(GrammarAST tree) { }

protected void enterRules(GrammarAST tree) { }
protected void exitRules(GrammarAST tree) { }

protected void enterMode(GrammarAST tree) { }
protected void exitMode(GrammarAST tree) { }

protected void enterLexerRule(GrammarAST tree) { }
protected void exitLexerRule(GrammarAST tree) { }

protected void enterRule(GrammarAST tree) { }
protected void exitRule(GrammarAST tree) { }

protected void enterExceptionGroup(GrammarAST tree) { }
protected void exitExceptionGroup(GrammarAST tree) { }

protected void enterExceptionHandler(GrammarAST tree) { }
protected void exitExceptionHandler(GrammarAST tree) { }

protected void enterFinallyClause(GrammarAST tree) { }
protected void exitFinallyClause(GrammarAST tree) { }

protected void enterLocals(GrammarAST tree) { }
protected void exitLocals(GrammarAST tree) { }

protected void enterRuleReturns(GrammarAST tree) { }
protected void exitRuleReturns(GrammarAST tree) { }

protected void enterThrowsSpec(GrammarAST tree) { }
protected void exitThrowsSpec(GrammarAST tree) { }

protected void enterRuleAction(GrammarAST tree) { }
protected void exitRuleAction(GrammarAST tree) { }

protected void enterRuleModifier(GrammarAST tree) { }
protected void exitRuleModifier(GrammarAST tree) { }

protected void enterLexerRuleBlock(GrammarAST tree) { }
protected void exitLexerRuleBlock(GrammarAST tree) { }

protected void enterRuleBlock(GrammarAST tree) { }
protected void exitRuleBlock(GrammarAST tree) { }

protected void enterLexerOuterAlternative(AltAST tree) { }
protected void exitLexerOuterAlternative(AltAST tree) { }

protected void enterOuterAlternative(AltAST tree) { }
protected void exitOuterAlternative(AltAST tree) { }

protected void enterLexerAlternative(GrammarAST tree) { }
protected void exitLexerAlternative(GrammarAST tree) { }

protected void enterLexerElements(GrammarAST tree) { }
protected void exitLexerElements(GrammarAST tree) { }

protected void enterLexerElement(GrammarAST tree) { }
protected void exitLexerElement(GrammarAST tree) { }

protected void enterLexerBlock(GrammarAST tree) { }
protected void exitLexerBlock(GrammarAST tree) { }

protected void enterLexerAtom(GrammarAST tree) { }
protected void exitLexerAtom(GrammarAST tree) { }

protected void enterActionElement(GrammarAST tree) { }
protected void exitActionElement(GrammarAST tree) { }

protected void enterAlternative(AltAST tree) { }
protected void exitAlternative(AltAST tree) { }

protected void enterLexerCommand(GrammarAST tree) { }
protected void exitLexerCommand(GrammarAST tree) { }

protected void enterLexerCommandExpr(GrammarAST tree) { }
protected void exitLexerCommandExpr(GrammarAST tree) { }

protected void enterElement(GrammarAST tree) { }
protected void exitElement(GrammarAST tree) { }

protected void enterAstOperand(GrammarAST tree) { }
protected void exitAstOperand(GrammarAST tree) { }

protected void enterLabeledElement(GrammarAST tree) { }
protected void exitLabeledElement(GrammarAST tree) { }

protected void enterSubrule(GrammarAST tree) { }
protected void exitSubrule(GrammarAST tree) { }

protected void enterLexerSubrule(GrammarAST tree) { }
protected void exitLexerSubrule(GrammarAST tree) { }

protected void enterBlockSuffix(GrammarAST tree) { }
protected void exitBlockSuffix(GrammarAST tree) { }

protected void enterEbnfSuffix(GrammarAST tree) { }
protected void exitEbnfSuffix(GrammarAST tree) { }

protected void enterAtom(GrammarAST tree) { }
protected void exitAtom(GrammarAST tree) { }

protected void enterBlockSet(GrammarAST tree) { }
protected void exitBlockSet(GrammarAST tree) { }

protected void enterSetElement(GrammarAST tree) { }
protected void exitSetElement(GrammarAST tree) { }

protected void enterBlock(GrammarAST tree) { }
protected void exitBlock(GrammarAST tree) { }

protected void enterRuleref(GrammarAST tree) { }
protected void exitRuleref(GrammarAST tree) { }

protected void enterRange(GrammarAST tree) { }
protected void exitRange(GrammarAST tree) { }

protected void enterTerminal(GrammarAST tree) { }
protected void exitTerminal(GrammarAST tree) { }

protected void enterElementOptions(GrammarAST tree) { }
protected void exitElementOptions(GrammarAST tree) { }

protected void enterElementOption(GrammarAST tree) { }
protected void exitElementOption(GrammarAST tree) { }

	@Override
	public void traceIn(String ruleName, int ruleIndex)  {
		System.err.println("enter "+ruleName+": "+input.LT(1));
	}

	@Override
	public void traceOut(String ruleName, int ruleIndex)  {
		System.err.println("exit "+ruleName+": "+input.LT(1));
	}
}

grammarSpec
@init {
	enterGrammarSpec($start);
}
@after {
	exitGrammarSpec($start);
}
    :   ^(	GRAMMAR ID {grammarName=$ID.text;}
    		{discoverGrammar((GrammarRootAST)$GRAMMAR, $ID);}
 		   	prequelConstructs
    		{finishPrequels($prequelConstructs.firstOne);}
 		   	rules mode*
    		{finishGrammar((GrammarRootAST)$GRAMMAR, $ID);}
 		 )
	;

prequelConstructs returns [GrammarAST firstOne=null]
@init {
	enterPrequelConstructs($start);
}
@after {
	exitPrequelConstructs($start);
}
	:	{$firstOne=$start;} prequelConstruct+
	|
	;

prequelConstruct
@init {
	enterPrequelConstructs($start);
}
@after {
	exitPrequelConstructs($start);
}
	:   optionsSpec
    |   delegateGrammars
    |   tokensSpec
    |   channelsSpec
    |   action
    ;

optionsSpec
@init {
	enterOptionsSpec($start);
}
@after {
	exitOptionsSpec($start);
}
	:	^(OPTIONS option*)
    ;

option
@init {
	enterOption($start);
	boolean rule = inContext("RULE ...");
	boolean block = inContext("BLOCK ...");
}
@after {
	exitOption($start);
}
    :   ^(a=ASSIGN ID v=optionValue)
    	{
    	if ( block ) blockOption($ID, $v.start); // most specific first
    	else if ( rule ) ruleOption($ID, $v.start);
    	else grammarOption($ID, $v.start);
    	}
    ;

optionValue returns [String v]
@init {
	enterOptionValue($start);
	$v = $start.token.getText();
}
@after {
	exitOptionValue($start);
}
    :   ID
    |   STRING_LITERAL
    |   INT
    ;

delegateGrammars
@init {
	enterDelegateGrammars($start);
}
@after {
	exitDelegateGrammars($start);
}
	:   ^(IMPORT delegateGrammar+)
	;

delegateGrammar
@init {
	enterDelegateGrammar($start);
}
@after {
	exitDelegateGrammar($start);
}
    :   ^(ASSIGN label=ID id=ID)	{importGrammar($label, $id);}
    |   id=ID						{importGrammar(null, $id);}
    ;

tokensSpec
@init {
	enterTokensSpec($start);
}
@after {
	exitTokensSpec($start);
}
	:   ^(TOKENS_SPEC tokenSpec+)
	;

tokenSpec
@init {
	enterTokenSpec($start);
}
@after {
	exitTokenSpec($start);
}
	:	ID					{defineToken($ID);}
	;

channelsSpec
@init {
	enterChannelsSpec($start);
}
@after {
	exitChannelsSpec($start);
}
	:   ^(CHANNELS channelSpec+)
	;

channelSpec
@init {
	enterChannelSpec($start);
}
@after {
	exitChannelSpec($start);
}
	:	ID					{defineChannel($ID);}
	;

action
@init {
	enterAction($start);
}
@after {
	exitAction($start);
}
	:	^(AT sc=ID? name=ID ACTION) {globalNamedAction($sc, $name, (ActionAST)$ACTION);}
	;

rules
@init {
	enterRules($start);
}
@after {
	exitRules($start);
}
    : ^(RULES {discoverRules($RULES);} (rule|lexerRule)* {finishRules($RULES);})
    ;

mode
@init {
	enterMode($start);
}
@after {
	exitMode($start);
}
	:	^( MODE ID {currentModeName=$ID.text; modeDef($MODE, $ID);} lexerRule* )
	;

lexerRule
@init {
	enterLexerRule($start);
	List<GrammarAST> mods = new ArrayList<GrammarAST>();
	currentOuterAltNumber=0;
}
@after {
	exitLexerRule($start);
}
	:	^(	RULE TOKEN_REF
			{currentRuleName=$TOKEN_REF.text; currentRuleAST=$RULE;}
			(^(RULEMODIFIERS m=FRAGMENT {mods.add($m);}))?
			opts=optionsSpec*
      		{discoverLexerRule((RuleAST)$RULE, $TOKEN_REF, mods, $opts.start, (GrammarAST)input.LT(1));}
      		lexerRuleBlock
      		{
      		finishLexerRule((RuleAST)$RULE, $TOKEN_REF, $lexerRuleBlock.start);
      		currentRuleName=null; currentRuleAST=null;
      		}
      	 )
	;

rule
@init {
	enterRule($start);
	List<GrammarAST> mods = new ArrayList<GrammarAST>();
	List<GrammarAST> actions = new ArrayList<GrammarAST>(); // track roots
	currentOuterAltNumber=0;
}
@after {
	exitRule($start);
}
	:   ^(	RULE RULE_REF {currentRuleName=$RULE_REF.text; currentRuleAST=$RULE;}
			(^(RULEMODIFIERS (m=ruleModifier{mods.add($m.start);})+))?
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
@init {
	enterExceptionGroup($start);
}
@after {
	exitExceptionGroup($start);
}
    :	exceptionHandler* finallyClause?
    ;

exceptionHandler
@init {
	enterExceptionHandler($start);
}
@after {
	exitExceptionHandler($start);
}
	: ^(CATCH ARG_ACTION ACTION)	{ruleCatch($ARG_ACTION, (ActionAST)$ACTION);}
	;

finallyClause
@init {
	enterFinallyClause($start);
}
@after {
	exitFinallyClause($start);
}
	: ^(FINALLY ACTION)				{finallyAction((ActionAST)$ACTION);}
	;

locals
@init {
	enterLocals($start);
}
@after {
	exitLocals($start);
}
	:	^(LOCALS ARG_ACTION)
	;

ruleReturns
@init {
	enterRuleReturns($start);
}
@after {
	exitRuleReturns($start);
}
	: ^(RETURNS ARG_ACTION)
	;

throwsSpec
@init {
	enterThrowsSpec($start);
}
@after {
	exitThrowsSpec($start);
}
    : ^(THROWS ID+)
    ;

ruleAction
@init {
	enterRuleAction($start);
}
@after {
	exitRuleAction($start);
}
	:	^(AT ID ACTION)
	;

ruleModifier
@init {
	enterRuleModifier($start);
}
@after {
	exitRuleModifier($start);
}
    : PUBLIC
    | PRIVATE
    | PROTECTED
    | FRAGMENT
    ;

lexerRuleBlock
@init {
	enterLexerRuleBlock($start);
}
@after {
	exitLexerRuleBlock($start);
}
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
@init {
	enterRuleBlock($start);
}
@after {
	exitRuleBlock($start);
}
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
	enterLexerOuterAlternative((AltAST)$start);
	discoverOuterAlt((AltAST)$start);
}
@after {
	finishOuterAlt((AltAST)$start);
	exitLexerOuterAlternative((AltAST)$start);
}
	:	lexerAlternative
	;


outerAlternative
@init {
	enterOuterAlternative((AltAST)$start);
	discoverOuterAlt((AltAST)$start);
}
@after {
	finishOuterAlt((AltAST)$start);
	exitOuterAlternative((AltAST)$start);
}
	:	alternative
	;

lexerAlternative
@init {
	enterLexerAlternative($start);
}
@after {
	exitLexerAlternative($start);
}
	:	^(LEXER_ALT_ACTION lexerElements lexerCommand+)
    |   lexerElements
    ;

lexerElements
@init {
	enterLexerElements($start);
}
@after {
	exitLexerElements($start);
}
    :	^(ALT lexerElement+)
    ;

lexerElement
@init {
	enterLexerElement($start);
}
@after {
	exitLexerElement($start);
}
	:	lexerAtom
	|	lexerSubrule
	|   ACTION						{actionInAlt((ActionAST)$ACTION);}
	|   SEMPRED						{sempredInAlt((PredAST)$SEMPRED);}
	|   ^(ACTION elementOptions)	{actionInAlt((ActionAST)$ACTION);}
	|   ^(SEMPRED elementOptions)	{sempredInAlt((PredAST)$SEMPRED);}
	|	EPSILON
	;

lexerBlock
@init {
	enterLexerBlock($start);
}
@after {
	exitLexerBlock($start);
}
 	:	^(BLOCK optionsSpec? lexerAlternative+)
    ;

lexerAtom
@init {
	enterLexerAtom($start);
}
@after {
	exitLexerAtom($start);
}
    :   terminal
    |   ^(NOT blockSet)
    |   blockSet
    |   ^(WILDCARD elementOptions)
    |   WILDCARD
    |	LEXER_CHAR_SET
    |   range
    |   ruleref
    ;

actionElement
@init {
	enterActionElement($start);
}
@after {
	exitActionElement($start);
}
	:	ACTION
	|   ^(ACTION elementOptions)
	|   SEMPRED
	|   ^(SEMPRED elementOptions)
	;

alternative
@init {
	enterAlternative((AltAST)$start);
	discoverAlt((AltAST)$start);
}
@after {
	finishAlt((AltAST)$start);
	exitAlternative((AltAST)$start);
}
	:	^(ALT elementOptions? element+)
	|	^(ALT elementOptions? EPSILON)
    ;

lexerCommand
@init {
	enterLexerCommand($start);
}
@after {
	exitLexerCommand($start);
}
	:	^(LEXER_ACTION_CALL ID lexerCommandExpr)
        {lexerCallCommand(currentOuterAltNumber, $ID, $lexerCommandExpr.start);}
	|	ID
        {lexerCommand(currentOuterAltNumber, $ID);}
	;

lexerCommandExpr
@init {
	enterLexerCommandExpr($start);
}
@after {
	exitLexerCommandExpr($start);
}
	:	ID
	|	INT
	;

element
@init {
	enterElement($start);
}
@after {
	exitElement($start);
}
	:	labeledElement
	|	atom
	|	subrule
	|   ACTION						{actionInAlt((ActionAST)$ACTION);}
	|   SEMPRED						{sempredInAlt((PredAST)$SEMPRED);}
	|   ^(ACTION elementOptions)	{actionInAlt((ActionAST)$ACTION);}
	|   ^(SEMPRED elementOptions)	{sempredInAlt((PredAST)$SEMPRED);}
	|	range
	|	^(NOT blockSet)
	|	^(NOT block)
	;

astOperand
@init {
	enterAstOperand($start);
}
@after {
	exitAstOperand($start);
}
	:	atom
	|	^(NOT blockSet)
	|	^(NOT block)
	;

labeledElement
@init {
	enterLabeledElement($start);
}
@after {
	exitLabeledElement($start);
}
	:	^((ASSIGN|PLUS_ASSIGN) ID element) {label($start, $ID, $element.start);}
	;

subrule
@init {
	enterSubrule($start);
}
@after {
	exitSubrule($start);
}
	:	^(blockSuffix block)
	| 	block
    ;

lexerSubrule
@init {
	enterLexerSubrule($start);
}
@after {
	exitLexerSubrule($start);
}
	:	^(blockSuffix lexerBlock)
	| 	lexerBlock
    ;

blockSuffix
@init {
	enterBlockSuffix($start);
}
@after {
	exitBlockSuffix($start);
}
    : ebnfSuffix
    ;

ebnfSuffix
@init {
	enterEbnfSuffix($start);
}
@after {
	exitEbnfSuffix($start);
}
	:	OPTIONAL
  	|	CLOSURE
   	|	POSITIVE_CLOSURE
	;

atom
@init {
	enterAtom($start);
}
@after {
	exitAtom($start);
}
	:	^(DOT ID terminal)
	|	^(DOT ID ruleref)
    |	^(WILDCARD elementOptions)	{wildcardRef($WILDCARD);}
    |	WILDCARD					{wildcardRef($WILDCARD);}
    |   terminal
    |	blockSet
    |   ruleref
    ;

blockSet
@init {
	enterBlockSet($start);
}
@after {
	exitBlockSet($start);
}
	:	^(SET setElement+)
	;

setElement
@init {
	enterSetElement($start);
}
@after {
	exitSetElement($start);
}
	:	^(STRING_LITERAL elementOptions)    {stringRef((TerminalAST)$STRING_LITERAL);}
	|	^(TOKEN_REF elementOptions) 		{tokenRef((TerminalAST)$TOKEN_REF);}
	|	STRING_LITERAL                  	{stringRef((TerminalAST)$STRING_LITERAL);}
	|	TOKEN_REF		                    {tokenRef((TerminalAST)$TOKEN_REF);}
	|	^(RANGE a=STRING_LITERAL b=STRING_LITERAL)
		{
		stringRef((TerminalAST)$a);
		stringRef((TerminalAST)$b);
		}
        |       LEXER_CHAR_SET
	;

block
@init {
	enterBlock($start);
}
@after {
	exitBlock($start);
}
    :	^(BLOCK optionsSpec? ruleAction* ACTION? alternative+)
    ;

ruleref
@init {
	enterRuleref($start);
}
@after {
	exitRuleref($start);
}
    :	^(RULE_REF arg=ARG_ACTION? elementOptions?)
    	{
    	ruleRef($RULE_REF, (ActionAST)$ARG_ACTION);
    	if ( $arg!=null ) actionInAlt((ActionAST)$arg);
    	}
    ;

range
@init {
	enterRange($start);
}
@after {
	exitRange($start);
}
    : ^(RANGE STRING_LITERAL STRING_LITERAL)
    ;

terminal
@init {
	enterTerminal($start);
}
@after {
	exitTerminal($start);
}
    :  ^(STRING_LITERAL elementOptions)
    								{stringRef((TerminalAST)$STRING_LITERAL);}
    |	STRING_LITERAL				{stringRef((TerminalAST)$STRING_LITERAL);}
    |	^(TOKEN_REF elementOptions)	{tokenRef((TerminalAST)$TOKEN_REF);}
    |	TOKEN_REF	    			{tokenRef((TerminalAST)$TOKEN_REF);}
    ;

elementOptions
@init {
	enterElementOptions($start);
}
@after {
	exitElementOptions($start);
}
    :	^(ELEMENT_OPTIONS elementOption[(GrammarASTWithOptions)$start.getParent()]*)
    ;

elementOption[GrammarASTWithOptions t]
@init {
	enterElementOption($start);
}
@after {
	exitElementOption($start);
}
    :	ID								{elementOption(t, $ID, null);}
    |   ^(ASSIGN id=ID v=ID)			{elementOption(t, $id, $v);}
    |   ^(ASSIGN ID v=STRING_LITERAL)	{elementOption(t, $ID, $v);}
    |   ^(ASSIGN ID v=ACTION)			{elementOption(t, $ID, $v);}
    |   ^(ASSIGN ID v=INT)				{elementOption(t, $ID, $v);}
    ;

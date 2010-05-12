tree grammar SourceGenTriggers;
options {
	language     = Java;
	tokenVocab   = ANTLRParser;
	ASTLabelType = GrammarAST;
}

@header {
package org.antlr.v4.codegen;
import org.antlr.v4.codegen.src.*;
import org.antlr.v4.tool.*;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
}

@members {
// TODO: identical grammar to NFABytecodeTriggers; would be nice to combine
	public OutputModelFactory factory;
    public SourceGenTriggers(TreeNodeStream input, OutputModelFactory factory) {
    	this(input);
    	this.factory = factory;
    }
}

block[GrammarAST label, GrammarAST ebnfRoot] returns [SrcOp omo]
    :	^( blk=BLOCK (^(OPTIONS .+))?
		{List<CodeBlock> alts = new ArrayList<CodeBlock>();}
    		( alternative {alts.add($alternative.omo);} )+
    	)
    	{
    	if ( alts.size()==1 && ebnfRoot==null) return alts.get(0);
    	$omo = factory.getChoiceBlock((BlockAST)$blk, $ebnfRoot, alts);
    	}
    ;

alternative returns [CodeBlock omo]
@init {List<SrcOp> elems = new ArrayList<SrcOp>();}
    :	^(ALT_REWRITE a=alternative .)	
    |	^(ALT EPSILON) {$omo = factory.epsilon();}
    |   ^( ALT ( element {elems.add($element.omo);} )+ ) {$omo = factory.alternative(elems);}
    ;

element returns [SrcOp omo]
	:	labeledElement					{$omo = $labeledElement.omo;}
	|	atom[null]						{$omo = $atom.omo;}
	|	ebnf							{$omo = $ebnf.omo;}						
	|   ACTION							{$omo = factory.action($ACTION);}
	|   SEMPRED							{$omo = factory.sempred($SEMPRED);}
	|	GATED_SEMPRED	
	|	treeSpec					
	;
	
labeledElement returns [SrcOp omo]
	:	^(ASSIGN ID atom[$ID] )				{$omo = $atom.omo;}
	|	^(ASSIGN ID block[$ID,null])		{$omo = $block.omo;}
	|	^(PLUS_ASSIGN ID atom[$ID])			{$omo = $atom.omo;}
	|	^(PLUS_ASSIGN ID block[$ID,null])	{$omo = $block.omo;}	
	;

treeSpec returns [SrcOp omo]
    : ^(TREE_BEGIN  (e=element )+)	
    ;

ebnf returns [SrcOp omo]
	:	^(astBlockSuffix block[null,null])		
	|	^(OPTIONAL block[null,$OPTIONAL])	{$omo = $block.omo;}		
	|	^(CLOSURE block[null,$CLOSURE])		{$omo = $block.omo;}		
	|	^(POSITIVE_CLOSURE block[null,$POSITIVE_CLOSURE])
										    {$omo = $block.omo;}
	| 	block[null, null]					{$omo = $block.omo;}
    ;

astBlockSuffix
    : ROOT
    | IMPLIES
    | BANG
    ;

// TODO: combine ROOT/BANG into one then just make new op ref'ing return value of atom/terminal...
// TODO: same for NOT
atom[GrammarAST label] returns [SrcOp omo]
	:	^(ROOT range[label])			
	|	^(BANG range[label])		{$omo = $range.omo;}	
	|	^(ROOT notSet[label])			
	|	^(BANG notSet[label])		{$omo = $notSet.omo;}	
	|	notSet[label]					
	|	range[label]				{$omo = $range.omo;}	
	|	^(DOT ID terminal[label])
	|	^(DOT ID ruleref[label])
    |	^(WILDCARD .)				
    |	WILDCARD				
    |   terminal[label]				{$omo = $terminal.omo;}
    |   ruleref[label]				{$omo = $ruleref.omo;}
    ;

notSet[GrammarAST label] returns [SrcOp omo]
    : ^(NOT terminal[label])		
    | ^(NOT block[label,null])			
    ;

ruleref[GrammarAST label] returns [SrcOp omo]
    :	^(ROOT ^(RULE_REF ARG_ACTION?))
    |	^(BANG ^(RULE_REF ARG_ACTION?))	{$omo = new InvokeRule(factory, $RULE_REF, $label);}
    |	^(RULE_REF ARG_ACTION?)			{$omo = new InvokeRule(factory, $RULE_REF, $label);}
    ;

range[GrammarAST label] returns [SrcOp omo]
    :	^(RANGE a=STRING_LITERAL b=STRING_LITERAL)    	
    ;

terminal[GrammarAST label] returns [MatchToken omo]
    :  ^(STRING_LITERAL .)			{$omo = new MatchToken(factory, (TerminalAST)$STRING_LITERAL, $label);}
    |	STRING_LITERAL				{$omo = new MatchToken(factory, (TerminalAST)$STRING_LITERAL, $label);}
    |	^(TOKEN_REF ARG_ACTION .)	{$omo = new MatchToken(factory, (TerminalAST)$TOKEN_REF, $label);}
    |	^(TOKEN_REF .)				{$omo = new MatchToken(factory, (TerminalAST)$TOKEN_REF, $label);}
    |	TOKEN_REF					{$omo = new MatchToken(factory, (TerminalAST)$TOKEN_REF, $label);}
    |	^(ROOT terminal[label])			
    |	^(BANG terminal[label])			
    ;

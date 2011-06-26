tree grammar SourceGenTriggers;
options {
	language     = Java;
	tokenVocab   = ANTLRParser;
	ASTLabelType = GrammarAST;
}

@header {
package org.antlr.v4.codegen;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.codegen.model.*;
import org.antlr.v4.tool.*;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
}

@members {
	public OutputModelFactory factory;
    public SourceGenTriggers(TreeNodeStream input, OutputModelFactory factory) {
    	this(input);
    	this.factory = factory;
    }
}

dummy : block[null, null] ;

block[GrammarAST label, GrammarAST ebnfRoot] returns [SrcOp omo]
    :	^( blk=BLOCK (^(OPTIONS .+))?
		{List<CodeBlock> alts = new ArrayList<CodeBlock>();}
    		( alternative {alts.add($alternative.omo);} )+
    	)
    	{
    	if ( alts.size()==1 && ebnfRoot==null) return alts.get(0);
    	if ( ebnfRoot==null ) {
    	    $omo = factory.getChoiceBlock((BlockAST)$blk, alts);
    	}
    	else {
    	    $omo = factory.getEBNFBlock($ebnfRoot, alts);
    	}
    	}
    ;

alternative returns [CodeBlock omo]
@init {
	List<SrcOp> elems = new ArrayList<SrcOp>();
	if ( ((AltAST)$start).alt!=null ) factory.currentAlt = ((AltAST)$start).alt;

}
    :	^(ALT_REWRITE a=alternative .)
    |	^(ALT EPSILON) {$omo = factory.epsilon();}
    |   ^( ALT ( element {elems.addAll($element.omos);} )+ ) {$omo = factory.alternative(elems);}
    ;

element returns [List<SrcOp> omos]
	:	labeledElement					{$omos = $labeledElement.omos;}
	|	atom[null]						{$omos = $atom.omos;}
	|	ebnf							{$omos = Utils.list($ebnf.omo);}
	|   ACTION							{$omos = Utils.list(factory.action($ACTION));}
	|   FORCED_ACTION					{$omos = Utils.list(factory.forcedAction($FORCED_ACTION));}
	|   SEMPRED							{$omos = Utils.list(factory.sempred($SEMPRED));}
	|	GATED_SEMPRED
	|	treeSpec
	;

labeledElement returns [List<SrcOp> omos]
	:	^(ASSIGN ID atom[$ID] )				{$omos = $atom.omos;}
	|	^(ASSIGN ID block[$ID,null])		{$omos = Utils.list($block.omo);}
	|	^(PLUS_ASSIGN ID atom[$ID])			{$omos = $atom.omos;}
	|	^(PLUS_ASSIGN ID block[$ID,null])	{$omos = Utils.list($block.omo);}
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
atom[GrammarAST label] returns [List<SrcOp> omos]
	:	^(ROOT range[label])
	|	^(BANG range[label])		{$omos = $range.omos;}
	|	^(ROOT notSet[label])
	|	^(BANG notSet[label])		{$omos = $notSet.omos;}
	|	notSet[label]
	|	range[label]				{$omos = $range.omos;}
	|	^(DOT ID terminal[label])
	|	^(DOT ID ruleref[label])
    |	^(WILDCARD .)
    |	WILDCARD
    |   terminal[label]				{$omos = $terminal.omos;}
    |   ruleref[label]				{$omos = $ruleref.omos;}
    ;

notSet[GrammarAST label] returns [List<SrcOp> omos]
    : ^(NOT terminal[label])
    | ^(NOT block[label,null])
    ;

ruleref[GrammarAST label] returns [List<SrcOp> omos]
    :	^(ROOT ^(RULE_REF ARG_ACTION?))
    |	^(BANG ^(RULE_REF ARG_ACTION?))	{$omos = factory.ruleRef($RULE_REF, $label, $ARG_ACTION);}
    |	^(RULE_REF ARG_ACTION?)			{$omos = factory.ruleRef($RULE_REF, $label, $ARG_ACTION);}
    ;

range[GrammarAST label] returns [List<SrcOp> omos]
    :	^(RANGE a=STRING_LITERAL b=STRING_LITERAL)
    ;

terminal[GrammarAST label] returns [List<SrcOp> omos]
    :  ^(STRING_LITERAL .)			{$omos = factory.stringRef($STRING_LITERAL, $label);}
    |	STRING_LITERAL				{$omos = factory.stringRef($STRING_LITERAL, $label);}
    |	^(TOKEN_REF ARG_ACTION .)	{$omos = factory.tokenRef($TOKEN_REF, $label, $ARG_ACTION);}
    |	^(TOKEN_REF .)				{$omos = factory.tokenRef($TOKEN_REF, $label, null);}
    |	TOKEN_REF					{$omos = factory.tokenRef($TOKEN_REF, $label, null);}
    |	^(ROOT terminal[label])
    |	^(BANG terminal[label])
    ;

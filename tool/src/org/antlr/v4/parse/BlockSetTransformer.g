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
public Set<String> rewriteElems = new HashSet<String>();
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
		(	ALT {currentAlt = $start; rewriteElems.clear();}
		|	ALT_REWRITE {currentAlt = $start;}
			{
			IntervalSet s = new IntervalSet();
			s.add(RULE_REF);
			s.add(STRING_LITERAL);
			s.add(TOKEN_REF);
			List<GrammarAST> nodes = ((GrammarAST)(currentAlt.getChild(1))).getNodesWithType(s);
			for (GrammarAST n : nodes) {rewriteElems.add(n.getText());}
			}
		)

	;

// (BLOCK (ALT (+ (BLOCK (ALT INT) (ALT ID)))))
ebnfBlockSet
@after {
	GrammarTransformPipeline.setGrammarPtr(g, $tree);
}
	:	^(ebnfSuffix blockSet) -> ^(ebnfSuffix ^(BLOCK<BlockAST> ^(ALT blockSet)))
	;

ebnfSuffix
@after {$tree = (GrammarAST)adaptor.dupNode($start);}
	:	OPTIONAL
  	|	CLOSURE
   	|	POSITIVE_CLOSURE
	;

blockSet
@init {
boolean inLexer = Character.isUpperCase(currentRuleName.charAt(0));
}
@after {
	GrammarTransformPipeline.setGrammarPtr(g, $tree);
}
	:	{!inContext("RULE")}? // if not rule block and > 1 alt
		^(BLOCK ^(ALT setElement[inLexer]) ( ^(ALT setElement[inLexer]) )+)
		-> ^(SET[$BLOCK.token, "SET"] setElement+)
	;

setElement[boolean inLexer]
@after {
	GrammarTransformPipeline.setGrammarPtr(g, $tree);
}
	:	{!rewriteElems.contains($start.getText())}?
		(	a=STRING_LITERAL {!inLexer || CharSupport.getCharValueFromGrammarCharLiteral($a.getText())!=-1}?
		|	{!inLexer}?=> TOKEN_REF
		|	{inLexer}?=>  ^(RANGE a=STRING_LITERAL b=STRING_LITERAL)
			{CharSupport.getCharValueFromGrammarCharLiteral($a.getText())!=-1 &&
			 CharSupport.getCharValueFromGrammarCharLiteral($b.getText())!=-1}?
		)
	;

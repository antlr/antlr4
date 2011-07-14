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
import org.antlr.v4.tool.*;
import java.util.List;
import java.util.ArrayList;
import org.antlr.v4.runtime.misc.IntervalSet;
}

@members {
public String currentRuleName;
public GrammarAST currentAlt;
}

topdown
    :	^(RULE ID {currentRuleName=$ID.text;} .+)
    |	setAlt
    |	ebnfBlockSet
    |	blockSet
	;

setAlt
	:	{inContext("RULE BLOCK")}? ( ALT | ALT_REWRITE )
		{currentAlt = (AltAST)$start;}
	;

// (BLOCK (ALT (+ (BLOCK (ALT INT) (ALT ID)))))
ebnfBlockSet
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
	if ( currentAlt!=null && currentAlt.getType()==ANTLRParser.ALT_REWRITE ) {
		IntervalSet s = new IntervalSet();
		s.add(RULE_REF);
		s.add(STRING_LITERAL);
		s.add(TOKEN_REF);
		List<GrammarAST> elems = currentAlt.getNodesWithType(s);
		System.out.println("stuff in rewrite: "+elems);
	}
}
	:	{Character.isLowerCase(currentRuleName.charAt(0)) &&
		!inContext("ALT_REWRITE ...") && !inContext("RULE")}?
		^(BLOCK ( ^(ALT setElement) )+) -> ^(SET[$BLOCK.token, "SET"] setElement+)
	;
	
setElement
@after {$tree = new TerminalAST($start);}
	:	STRING_LITERAL
	|	TOKEN_REF
	;
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
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import org.antlr.v4.runtime.misc.IntervalSet;
}

@members {
public String currentRuleName;
public GrammarAST currentAlt;
public Set<String> rewriteElems = new HashSet<String>();
}

topdown
    :	^(RULE ID {currentRuleName=$ID.text;} .+)
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
			System.out.println("stuff in rewrite: "+rewriteElems);
			}
		)
				
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
}
	:	{Character.isLowerCase(currentRuleName.charAt(0)) &&
		 !inContext("RULE")}? // if non-lexer rule and not rule block
		^(BLOCK ( ^(ALT setElement) )+) -> ^(SET[$BLOCK.token, "SET"] setElement+)
	;
	
setElement
@after {$tree = new TerminalAST($start);} // elem can't be to right of ->
	:	{!rewriteElems.contains($start.getText())}? (STRING_LITERAL|TOKEN_REF)
	;
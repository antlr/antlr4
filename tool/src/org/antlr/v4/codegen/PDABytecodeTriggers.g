tree grammar PDABytecodeTriggers;
options {
	language     = Java;
	tokenVocab   = ANTLRParser;
	ASTLabelType = GrammarAST;
//	superClass   = PDABytecodeGenerator;
}

@header {
package org.antlr.v4.codegen;
import org.antlr.v4.codegen.pda.*;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.AltAST;
import org.antlr.v4.tool.GrammarASTWithOptions;
import org.antlr.v4.tool.LexerGrammar;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
}

@members {
	PDABytecodeGenerator gen;
	
	public PDABytecodeTriggers(TreeNodeStream input, PDABytecodeGenerator gen) {
		this(input);
		this.gen = gen;
	}

	// (BLOCK (ALT .)) or (BLOCK (ALT 'a') (ALT .))
	public boolean blockHasWildcardAlt(GrammarAST block) {
		for (Object alt : block.getChildren()) {
			if ( !(alt instanceof AltAST) ) continue;
			AltAST altAST = (AltAST)alt;
			if ( altAST.getChildCount()==1 ) {
				Tree e = altAST.getChild(0);
				if ( e.getType()==WILDCARD ) {
					return true;
				}
			}
		}
		return false;
	}
}

block
    :	^(	BLOCK (^(OPTIONS .+))?
    		{
    		GrammarAST firstAlt = (GrammarAST)input.LT(1);
    		int i = firstAlt.getChildIndex();    		
			int nAlts = $start.getChildCount() - i;
    		System.out.println("alts "+nAlts);
    		List<JumpInstr> jumps = new ArrayList<JumpInstr>();
    		SplitInstr S = null;
    		if ( nAlts>1 ) {
	    		S = new SplitInstr(nAlts);
	    		gen.emit(S);
	    		S.addrs.add(gen.ip);
    		}
    		int alt = 1;
    		}
    		(	alternative
    			{
    			if ( alt < nAlts ) {
	    			JumpInstr J = new JumpInstr();
	    			jumps.add(J);
	    			gen.emit(J);
	    			S.addrs.add(gen.ip);
    			}
    			alt++;
    			}
    		)+
    		{
    		int END = gen.ip;
    		for (JumpInstr J : jumps) J.target = END;
    		}
    	)
    ;

alternative
    :	^(ALT_REWRITE a=alternative .)	
    |	^(ALT EPSILON)					
    |   ^(ALT (e=element )+)    									
    ;

element
	:	labeledElement				
	|	atom						
	|	ebnf						
	|   ACTION			{gen.emit(new ActionInstr($ACTION.token));}			
	|   SEMPRED			{gen.emit(new SemPredInstr($SEMPRED.token));}		
	|	GATED_SEMPRED	{gen.emit(new SemPredInstr($GATED_SEMPRED.token));}
	|	treeSpec					
	;
	
labeledElement
	:	^(ASSIGN ID {gen.emit(new LabelInstr($ID.token));} atom {gen.emit(new SaveInstr($ID.token));} )
	|	^(ASSIGN ID block)			
	|	^(PLUS_ASSIGN ID atom)		
	|	^(PLUS_ASSIGN ID block)		
	;

treeSpec
    : ^(TREE_BEGIN  (e=element )+)	
    ;

ebnf
@init {
	GrammarASTWithOptions blk = null;
	if ( $start.getType()==BLOCK ) blk = (GrammarASTWithOptions)$start;
	else blk = (GrammarASTWithOptions)$start.getChild(0);
	String greedyOption = blk.getOption("greedy");
	if ( blockHasWildcardAlt(blk) && greedyOption==null ) greedyOption = "false";
}
	:	^(astBlockSuffix block)		
	|	{
	   	SplitInstr S = new SplitInstr(2);
		gen.emit(S);
   		S.addrs.add(gen.ip);
		}
		^(OPTIONAL block)			
		{
   		S.addrs.add(gen.ip);
		}
	|	{
		int start=gen.ip;
	   	SplitInstr S = new SplitInstr(2);
		gen.emit(S);
		int blkStart = gen.ip;
		}
		^(CLOSURE block)			
		{
	    JumpInstr J = new JumpInstr();
	    gen.emit(J);
	    J.target = start;
   		S.addrs.add(blkStart);
	    S.addrs.add(gen.ip);
	    if ( greedyOption!=null && greedyOption.equals("false") ) Collections.reverse(S.addrs);
		}
	|	{int start=gen.ip;} ^(POSITIVE_CLOSURE block)
		{
   		SplitInstr S = new SplitInstr(2);
		gen.emit(S);
		int stop = gen.ip;
   		S.addrs.add(start);
   		S.addrs.add(stop);
	    if ( greedyOption!=null && greedyOption.equals("false") ) Collections.reverse(S.addrs);
		}
	| 	block						
    ;

astBlockSuffix
    : ROOT
    | IMPLIES
    | BANG
    ;

atom
	:	^(ROOT range)			
	|	^(BANG range)			
	|	^(ROOT notSet)			
	|	^(BANG notSet)			
	|	notSet					
	|	range					
	|	^(DOT ID terminal)		
	|	^(DOT ID ruleref)		
    |	^(WILDCARD .)		{gen.emit(new WildcardInstr($WILDCARD.token));}		
    |	WILDCARD			{gen.emit(new WildcardInstr($WILDCARD.token));}	
    |   terminal			
    |   ruleref					
    ;

notSet
    : ^(NOT {gen.emit(new NotInstr());} terminal)
    | ^(NOT {gen.emit(new NotInstr());} block)
    ;

ruleref
    :	^(ROOT ^(RULE_REF ARG_ACTION?))	
    |	^(BANG ^(RULE_REF ARG_ACTION?))	
    |	^(RULE_REF ARG_ACTION?)			
    ;

range
    :	^(RANGE a=STRING_LITERAL b=STRING_LITERAL)
    	{gen.emit(new RangeInstr($a.token, $b.token));}
    ;

terminal
    :  ^(STRING_LITERAL .)			{gen.emitString($STRING_LITERAL.token);}
    |	STRING_LITERAL				{gen.emitString($STRING_LITERAL.token);}
    |	^(TOKEN_REF ARG_ACTION .)	{gen.emit(new CallInstr($TOKEN_REF.token));}
    |	^(TOKEN_REF .)				{gen.emit(new CallInstr($TOKEN_REF.token));}
    |	TOKEN_REF					{gen.emit(new CallInstr($TOKEN_REF.token));}
    |	^(ROOT terminal)
    |	^(BANG terminal)
    ;
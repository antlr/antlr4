tree grammar Refs;
options {
	language     = Java;
	tokenVocab   = ANTLRParser;
	ASTLabelType = GrammarAST;
}

@header {
package org.antlr.v4.semantics;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.tool.*;
import java.util.List;
import java.util.ArrayList;
}

@members {
List<GrammarAST> shallow = new ArrayList<GrammarAST>();
List<GrammarAST> deep = new ArrayList<GrammarAST>();
public int desiredShallowLevel;

public Refs(TreeNodeStream input, int desiredShallowLevel) {
	this(input);
	this.desiredShallowLevel = desiredShallowLevel;
}

public void track(GrammarAST t, int level) {
	deep.add(t);
	if ( level==desiredShallowLevel ) shallow.add(t);
}

// TODO: visitor would be better here
}

/*
rewrite
	:	predicatedRewrite* nakedRewrite
	;

predicatedRewrite
	:	^(RESULT SEMPRED rewriteAlt)
	;

nakedRewrite
	:	^(RESULT rewriteAlt)
	;

    */

start
	:	^(RESULT rewriteAlt)
	|	rewriteTreeEbnf[0]
	;

rewriteAlt
    :	rewriteTreeAlt[0]
    |	ETC
    |	EPSILON
    ;

rewriteTreeAlt[int level]
    :	^(ALT rewriteTreeElement[level]+)
    ;

rewriteTreeElement[int level]
	:	rewriteTreeAtom[level]
	|	rewriteTree[level]
	|   rewriteTreeEbnf[level]
	;

rewriteTreeAtom[int level]
    :   ^(TOKEN_REF . .)		{track($start, level);}
    |   ^(TOKEN_REF .)			{track($start, level);}
	|   TOKEN_REF				{track($start, level);}
    |   RULE_REF				{track($start, level);}
	|   ^(STRING_LITERAL .)		{track($start, level);}
	|   STRING_LITERAL			{track($start, level);}
	|   LABEL					{track($start, level);}
	|	ACTION
	;

rewriteTreeEbnf[int level]
	:	^((OPTIONAL | CLOSURE) ^(REWRITE_BLOCK rewriteTreeAlt[level+1]))
	;

rewriteTree[int level]
	:	^(TREE_BEGIN rewriteTreeAtom[level] rewriteTreeElement[level]* )
	;

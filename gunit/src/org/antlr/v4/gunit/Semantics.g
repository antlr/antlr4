tree grammar Semantics;

options {
	filter=true;
	ASTLabelType=CommonTree;
	tokenVocab = gUnit;
}

@header {
package org.antlr.v4.gunit;
import java.util.Map;
import java.util.HashMap;
}

@members {
	public String name;
	public Map<String,String> options = new HashMap<String,String>();
}

topdown
	:	optionsSpec
	|	gUnitDef
	;

gUnitDef
	:	^('gunit' ID .*) {name = $ID.text;}
	;
	
optionsSpec
	:	^(OPTIONS option+)
	;

option
    :   ^('=' o=ID v=ID)	 {options.put($o.text, $v.text);}
    |   ^('=' o=ID v=STRING) {options.put($o.text, $v.text);}
    ;

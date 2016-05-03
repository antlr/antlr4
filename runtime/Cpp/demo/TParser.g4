parser grammar TParser;

options {
	tokenVocab = TLexer;
}

@parser::header {
// Parser header
}

@parser::members {
bool myAction() {
  return true;
}

bool doesItBlend() {
  return true;
}

void cleanUp() {
}

void doInit() {
}

void doAfter() {
}

}

@parser::listenerheader {
 // Listener header.
}

@parser::listenermembers {
 // Listener members.
}

@parser::visitorheader {
 // Visitor header.
}

@parser::visitormembers {
  // Visitor members.
}

main: stat+;
divide : ID (and_ GreaterThan)? {doesItBlend()}?; 
and_ @init{ doInit(); } @after { doAfter(); } : And ;

conquer:
	divide+
	| {doesItBlend()}? and_ { myAction(); }
	| ID (LessThan* divide)??
;

// Unused rule to demonstrate some of the special features.
unused[double input = 111] returns [double calculated] locals [int _a, double _b, int _c] @init{ doInit(); } @after { doAfter(); } :
	stat
;
catch [...] {
  // Replaces the standard exception handling.
}
finally {
  cleanUp();
}

unused2:
	(unused[1] .)+ (Colon | Semicolon | Plus)? ~Semicolon
;

stat: expr Equal expr Semicolon
    | expr Semicolon
;

expr: expr Star expr
    | expr Plus expr
    | OpenPar expr ClosePar
	| <assoc = right> expr QuestionMark expr Colon expr
    | <assoc = right> expr Equal expr
    | identifier = id
	| flowControl
	| INT
;

flowControl:
	Return expr # Return
	| Continue # Continue
;

id: ID;
array : OpenCurly el += INT (Comma el += INT)* CloseCurly;
idarray : OpenCurly element += id (Comma element += id)* CloseCurly;

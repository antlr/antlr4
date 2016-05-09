parser grammar TParser;

options {
	tokenVocab = TLexer;
}

// These are all supported parser sections:

// Parser file header. Appears at the top in all parser related files. Use e.g. for copyrights.
@parser::header {/* parser/listener/visitor header section */}

// Appears before any #include in h + cpp files.
@parser::preinclude {/* parser precinclude section */}

// Follows directly after the standard #includes in h + cpp files.
@parser::postinclude {/* parser postinclude section */}

// Directly preceeds the parser class declaration in the h file (e.g. for additional types etc.).
@parser::context {/* parser context section */}

// Appears in the public part of the parser in the h file.
@parser::declarations {/* public parser declarations section */}

// Appears in the private part of the parser in the h file.
@parser::members {
/* private parser declarations/members section */
bool myAction();
bool doesItBlend();
void cleanUp();
void doInit();
void doAfter();
}

// Appears in line with the other class member definitions in the cpp file.
@parser::definitions {
/* parser definitions section */
bool TParser::myAction() {
  return true;
}

bool TParser::doesItBlend() {
  return true;
}

void TParser::cleanUp() {
}

void TParser::doInit() {
}

void TParser::doAfter() {
}

}

// Additionally there are similar sections for (base)listener and (base)visitor files.
@parser::listenerpreinclude {/* listener preinclude section */}
@parser::listenerpostinclude {/* listener postinclude section */}
@parser::listenerdeclarations {/* listener public declarations/members section */}
@parser::listenermembers {/* listener private declarations/members section */}
@parser::listenerdefinitions {/* listener definitions section */}

@parser::baselistenerpreinclude {/* base listener preinclude section */}
@parser::baselistenerpostinclude {/* base listener postinclude section */}
@parser::baselistenerdeclarations {/* base listener public declarations/members section */}
@parser::baselistenermembers {/* base listener private declarations/members section */}
@parser::baselistenerdefinitions {/* base listener definitions section */}

@parser::visitorpreinclude {/* visitor preinclude section */}
@parser::visitorpostinclude {/* visitor postinclude section */}
@parser::visitordeclarations {/* visitor public declarations/members section */}
@parser::visitormembers {/* visitor private declarations/members section */}
@parser::visitordefinitions {/* visitor definitions section */}

@parser::basevisitorpreinclude {/* base visitor preinclude section */}
@parser::basevisitorpostinclude {/* base visitor postinclude section */}
@parser::basevisitordeclarations {/* base visitor public declarations/members section */}
@parser::basevisitormembers {/* base visitor private declarations/members section */}
@parser::basevisitordefinitions {/* base visitor definitions section */}

// Actual grammar start.
main: stat+ EOF;
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

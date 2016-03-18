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

main: divide and_? conquer;
divide : LessThan and_ GreaterThan {doesItBlend()}?; 
and_ @init{ doInit(); } @after { doAfter(); } : ID ;

conquer:
	divide+
	| {doesItBlend()}? and_ { myAction(); }
	| conquer LessThan* divide
;

// Unused rule to demonstrate some of the special features.
// Note: returns and throws are ignored in the C++ target.
unused returns [double] throws stones, flowers  @init{ doInit(); } @after { doAfter(); } :
;
catch [...] {
  // Replaces the standard exception handling.
}
finally {
  cleanUp();
}

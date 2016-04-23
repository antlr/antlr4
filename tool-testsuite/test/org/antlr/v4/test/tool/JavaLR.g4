/*
 [The "BSD licence"]
 Copyright (c) 2007-2008 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
/** A Java 1.5 grammar for ANTLR v3 derived from the spec
 *
 *  This is a very close representation of the spec; the changes
 *  are comestic (remove left recursion) and also fixes (the spec
 *  isn't exactly perfect).  I have run this on the 1.4.2 source
 *  and some nasty looking enums from 1.5, but have not really
 *  tested for 1.5 compatibility.
 *
 *  I built this with: java -Xmx100M org.antlr.Tool java.g 
 *  and got two errors that are ok (for now):
 *  java.g:691:9: Decision can match input such as
 *    "'0'..'9'{'E', 'e'}{'+', '-'}'0'..'9'{'D', 'F', 'd', 'f'}"
 *    using multiple alternatives: 3, 4
 *  As a result, alternative(s) 4 were disabled for that input
 *  java.g:734:35: Decision can match input such as "{'$', 'A'..'Z',
 *    '_', 'a'..'z', '\u00C0'..'\u00D6', '\u00D8'..'\u00F6',
 *    '\u00F8'..'\u1FFF', '\u3040'..'\u318F', '\u3300'..'\u337F',
 *    '\u3400'..'\u3D2D', '\u4E00'..'\u9FFF', '\uF900'..'\uFAFF'}"
 *    using multiple alternatives: 1, 2
 *  As a result, alternative(s) 2 were disabled for that input
 *
 *  You can turn enum on/off as a keyword :)
 *
 *  Version 1.0 -- initial release July 5, 2006 (requires 3.0b2 or higher)
 *
 *  Primary author: Terence Parr, July 2006
 *
 *  Version 1.0.1 -- corrections by Koen Vanderkimpen & Marko van Dooren,
 *      October 25, 2006;
 *      fixed normalInterfaceDeclaration: now uses typeParameters instead
 *          of typeParameter (according to JLS, 3rd edition)
 *      fixed castExpression: no longer allows expression next to type
 *          (according to semantics in JLS, in contrast with syntax in JLS)
 *
 *  Version 1.0.2 -- Terence Parr, Nov 27, 2006
 *      java spec I built this from had some bizarre for-loop control.
 *          Looked weird and so I looked elsewhere...Yep, it's messed up.
 *          simplified.
 *
 *  Version 1.0.3 -- Chris Hogue, Feb 26, 2007
 *      Factored out an annotationName rule and used it in the annotation rule.
 *          Not sure why, but typeName wasn't recognizing references to inner
 *          annotations (e.g. @InterfaceName.InnerAnnotation())
 *      Factored out the elementValue section of an annotation reference.  Created 
 *          elementValuePair and elementValuePairs rules, then used them in the 
 *          annotation rule.  Allows it to recognize annotation references with 
 *          multiple, comma separated attributes.
 *      Updated elementValueArrayInitializer so that it allows multiple elements.
 *          (It was only allowing 0 or 1 element).
 *      Updated localVariableDeclaration to allow annotations.  Interestingly the JLS
 *          doesn't appear to indicate this is legal, but it does work as of at least
 *          JDK 1.5.0_06.
 *      Moved the Identifier portion of annotationTypeElementRest to annotationMethodRest.
 *          Because annotationConstantRest already references variableDeclarator which 
 *          has the Identifier portion in it, the parser would fail on constants in 
 *          annotation definitions because it expected two identifiers.  
 *      Added optional trailing ';' to the alternatives in annotationTypeElementRest.
 *          Wouldn't handle an inner interface that has a trailing ';'.
 *      Swapped the expression and type rule reference order in castExpression to 
 *          make it check for genericized casts first.  It was failing to recognize a
 *          statement like  "Class<Byte> TYPE = (Class<Byte>)...;" because it was seeing
 *          'Class<Byte' in the cast expression as a less than expression, then failing 
 *          on the '>'.
 *      Changed createdName to use typeArguments instead of nonWildcardTypeArguments.
 *      Changed the 'this' alternative in primary to allow 'identifierSuffix' rather than
 *          just 'arguments'.  The case it couldn't handle was a call to an explicit
 *          generic method invocation (e.g. this.<E>doSomething()).  Using identifierSuffix
 *          may be overly aggressive--perhaps should create a more constrained thisSuffix rule?
 *      
 *  Version 1.0.4 -- Hiroaki Nakamura, May 3, 2007
 *
 *  Fixed formalParameterDecls, localVariableDeclaration, forInit,
 *  and forVarControl to use variableModifier* not 'final'? (annotation)?
 *
 *  Version 1.0.5 -- Terence, June 21, 2007
 *  --a[i].foo didn't work. Fixed unaryExpression
 *
 *  Version 1.0.6 -- John Ridgway, March 17, 2008
 *      Made "assert" a switchable keyword like "enum".
 *      Fixed compilationUnit to disallow "annotation importDeclaration ...".
 *      Changed "Identifier ('.' Identifier)*" to "qualifiedName" in more 
 *          places.
 *      Changed modifier* and/or variableModifier* to classOrInterfaceModifiers,
 *          modifiers or variableModifiers, as appropriate.
 *      Renamed "bound" to "typeBound" to better match language in the JLS.
 *      Added "memberDeclaration" which rewrites to methodDeclaration or 
 *      fieldDeclaration and pulled type into memberDeclaration.  So we parse 
 *          type and then move on to decide whether we're dealing with a field
 *          or a method.
 *      Modified "constructorDeclaration" to use "constructorBody" instead of
 *          "methodBody".  constructorBody starts with explicitConstructorInvocation,
 *          then goes on to blockStatement*.  Pulling explicitConstructorInvocation
 *          out of expressions allowed me to simplify "primary".
 *      Changed variableDeclarator to simplify it.
 *      Changed type to use classOrInterfaceType, thus simplifying it; of course
 *          I then had to add classOrInterfaceType, but it is used in several 
 *          places.
 *      Fixed annotations, old version allowed "@X(y,z)", which is illegal.
 *      Added optional comma to end of "elementValueArrayInitializer"; as per JLS.
 *      Changed annotationTypeElementRest to use normalClassDeclaration and 
 *          normalInterfaceDeclaration rather than classDeclaration and 
 *          interfaceDeclaration, thus getting rid of a couple of grammar ambiguities.
 *      Split localVariableDeclaration into localVariableDeclarationStatement
 *          (includes the terminating semi-colon) and localVariableDeclaration.  
 *          This allowed me to use localVariableDeclaration in "forInit" clauses,
 *           simplifying them.
 *      Changed switchBlockStatementGroup to use multiple labels.  This adds an
 *          ambiguity, but if one uses appropriately greedy parsing it yields the
 *           parse that is closest to the meaning of the switch statement.
 *      Renamed "forVarControl" to "enhancedForControl" -- JLS language.
 *      Added semantic predicates to test for shift operations rather than other
 *          things.  Thus, for instance, the string "< <" will never be treated
 *          as a left-shift operator.
 *      In "creator" we rule out "nonWildcardTypeArguments" on arrayCreation, 
 *          which are illegal.
 *      Moved "nonWildcardTypeArguments into innerCreator.
 *      Removed 'super' superSuffix from explicitGenericInvocation, since that
 *          is only used in explicitConstructorInvocation at the beginning of a
 *           constructorBody.  (This is part of the simplification of expressions
 *           mentioned earlier.)
 *      Simplified primary (got rid of those things that are only used in
 *          explicitConstructorInvocation).
 *      Lexer -- removed "Exponent?" from FloatingPointLiteral choice 4, since it
 *          led to an ambiguity.
 *
 *      This grammar successfully parses every .java file in the JDK 1.5 source 
 *          tree (excluding those whose file names include '-', which are not
 *          valid Java compilation units).
 *
 *  June 26, 2008
 *
 *	conditionalExpression had wrong precedence x?y:z.
 *
 *  February 26, 2011
 *	added left-recursive expression rule
 *
 *  Known remaining problems:
 *      "Letter" and "JavaIDDigit" are wrong.  The actual specification of
 *      "Letter" should be "a character for which the method
 *      Character.isJavaIdentifierStart(int) returns true."  A "Java 
 *      letter-or-digit is a character for which the method 
 *      Character.isJavaIdentifierPart(int) returns true."
 */
grammar JavaLR;

// starting point for parsing a java file
/* The annotations are separated out to make parsing faster, but must be associated with
   a packageDeclaration or a typeDeclaration (and not an empty one). */
compilationUnit
    :   annotations
        (   packageDeclaration importDeclaration* typeDeclaration*
        |   classOrInterfaceDeclaration typeDeclaration*
        )
        EOF
    |   packageDeclaration? importDeclaration* typeDeclaration*
        EOF
    ;

packageDeclaration
    :   'package' qualifiedName ';'
    ;
    
importDeclaration
    :   'import' 'static'? qualifiedName ('.' '*')? ';'
    ;
    
typeDeclaration
    :   classOrInterfaceDeclaration
    |   ';'
    ;
    
classOrInterfaceDeclaration
    :   classOrInterfaceModifiers (classDeclaration | interfaceDeclaration)
    ;
    
classOrInterfaceModifiers
    :   classOrInterfaceModifier*
    ;

classOrInterfaceModifier
    :   annotation       // class or interface
    |   (   'public'     // class or interface
        |   'protected'  // class or interface
        |   'private'    // class or interface
        |   'abstract'   // class or interface
        |   'static'     // class or interface
        |   'final'      // class only -- does not apply to interfaces
        |   'strictfp'   // class or interface
        )
    ;

modifiers
    :   modifier*
    ;

classDeclaration
    :   normalClassDeclaration
    |   enumDeclaration
    ;
    
normalClassDeclaration
    :   'class' Identifier typeParameters?
        ('extends' type)?
        ('implements' typeList)?
        classBody
    ;
    
typeParameters
    :   '<' typeParameter (',' typeParameter)* '>'
    ;

typeParameter
    :   Identifier ('extends' typeBound)?
    ;
        
typeBound
    :   type ('&' type)*
    ;

enumDeclaration
    :   ENUM Identifier ('implements' typeList)? enumBody
    ;

enumBody
    :   '{' enumConstants? ','? enumBodyDeclarations? '}'
    ;

enumConstants
    :   enumConstant (',' enumConstant)*
    ;
    
enumConstant
    :   annotations? Identifier arguments? classBody?
    ;
    
enumBodyDeclarations
    :   ';' (classBodyDeclaration)*
    ;
    
interfaceDeclaration
    :   normalInterfaceDeclaration
    |   annotationTypeDeclaration
    ;
    
normalInterfaceDeclaration
    :   'interface' Identifier typeParameters? ('extends' typeList)? interfaceBody
    ;
    
typeList
    :   type (',' type)*
    ;
    
classBody
    :   '{' classBodyDeclaration* '}'
    ;
    
interfaceBody
    :   '{' interfaceBodyDeclaration* '}'
    ;

classBodyDeclaration
    :   ';'
    |   'static'? block
    |   modifiers memberDecl
    ;
    
memberDecl
    :   genericMethodOrConstructorDecl
    |   memberDeclaration
    |   'void' Identifier voidMethodDeclaratorRest
    |   Identifier constructorDeclaratorRest
    |   interfaceDeclaration
    |   classDeclaration
    ;
    
memberDeclaration
    :   type (methodDeclaration | fieldDeclaration)
    ;

genericMethodOrConstructorDecl
    :   typeParameters genericMethodOrConstructorRest
    ;
    
genericMethodOrConstructorRest
    :   (type | 'void') Identifier methodDeclaratorRest
    |   Identifier constructorDeclaratorRest
    ;

methodDeclaration
    :   Identifier methodDeclaratorRest
    ;

fieldDeclaration
    :   variableDeclarators ';'
    ;
        
interfaceBodyDeclaration
    :   modifiers interfaceMemberDecl
    |   ';'
    ;

interfaceMemberDecl
    :   interfaceMethodOrFieldDecl
    |   interfaceGenericMethodDecl
    |   'void' Identifier voidInterfaceMethodDeclaratorRest
    |   interfaceDeclaration
    |   classDeclaration
    ;
    
interfaceMethodOrFieldDecl
    :   type Identifier interfaceMethodOrFieldRest
    ;
    
interfaceMethodOrFieldRest
    :   constantDeclaratorsRest ';'
    |   interfaceMethodDeclaratorRest
    ;
    
methodDeclaratorRest
    :   formalParameters ('[' ']')*
        ('throws' qualifiedNameList)?
        (   methodBody
        |   ';'
        )
    ;
    
voidMethodDeclaratorRest
    :   formalParameters ('throws' qualifiedNameList)?
        (   methodBody
        |   ';'
        )
    ;
    
interfaceMethodDeclaratorRest
    :   formalParameters ('[' ']')* ('throws' qualifiedNameList)? ';'
    ;
    
interfaceGenericMethodDecl
    :   typeParameters (type | 'void') Identifier
        interfaceMethodDeclaratorRest
    ;
    
voidInterfaceMethodDeclaratorRest
    :   formalParameters ('throws' qualifiedNameList)? ';'
    ;
    
constructorDeclaratorRest
    :   formalParameters ('throws' qualifiedNameList)? constructorBody
    ;

constantDeclarator
    :   Identifier constantDeclaratorRest
    ;
    
variableDeclarators
    :   variableDeclarator (',' variableDeclarator)*
    ;

variableDeclarator
    :   variableDeclaratorId ('=' variableInitializer)?
    ;
    
constantDeclaratorsRest
    :   constantDeclaratorRest (',' constantDeclarator)*
    ;

constantDeclaratorRest
    :   ('[' ']')* '=' variableInitializer
    ;
    
variableDeclaratorId
    :   Identifier ('[' ']')*
    ;

variableInitializer
    :   arrayInitializer
    |   expression
    ;
        
arrayInitializer
    :   '{' (variableInitializer (',' variableInitializer)* (',')? )? '}'
    ;

modifier
    :   annotation
    |   (   'public'
        |   'protected'
        |   'private'
        |   'static'
        |   'abstract'
        |   'final'
        |   'native'
        |   'synchronized'
        |   'transient'
        |   'volatile'
        |   'strictfp'
        )
    ;

packageOrTypeName
    :   qualifiedName
    ;

enumConstantName
    :   Identifier
    ;

typeName
    :   qualifiedName
    ;

type
	:	classOrInterfaceType ('[' ']')*
	|	primitiveType ('[' ']')*
	;

classOrInterfaceType
	:	Identifier typeArguments? ('.' Identifier typeArguments? )*
	;

primitiveType
    :   'boolean'
    |   'char'
    |   'byte'
    |   'short'
    |   'int'
    |   'long'
    |   'float'
    |   'double'
    ;

variableModifier
    :   'final'
    |   annotation
    ;

typeArguments
    :   '<' typeArgument (',' typeArgument)* '>'
    ;
    
typeArgument
    :   type
    |   '?' (('extends' | 'super') type)?
    ;
    
qualifiedNameList
    :   qualifiedName (',' qualifiedName)*
    ;

formalParameters
    :   '(' formalParameterDecls? ')'
    ;
    
formalParameterDecls
    :   variableModifiers type formalParameterDeclsRest
    ;
    
formalParameterDeclsRest
    :   variableDeclaratorId (',' formalParameterDecls)?
    |   '...' variableDeclaratorId
    ;
    
methodBody
    :   block
    ;

constructorBody
    :   block
    ;

qualifiedName
    :   Identifier ('.' Identifier)*
    ;
    
literal 
    :   IntegerLiteral
    |   FloatingPointLiteral
    |   CharacterLiteral
    |   StringLiteral
    |   BooleanLiteral
    |   'null'
    ;

// ANNOTATIONS

annotations
    :   annotation+
    ;

annotation
    :   '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )?
    ;
    
annotationName
    : Identifier ('.' Identifier)*
    ;

elementValuePairs
    :   elementValuePair (',' elementValuePair)*
    ;

elementValuePair
    :   Identifier '=' elementValue
    ;
    
elementValue
    :   expression
    |   annotation
    |   elementValueArrayInitializer
    ;
    
elementValueArrayInitializer
    :   '{' (elementValue (',' elementValue)*)? (',')? '}'
    ;
    
annotationTypeDeclaration
    :   '@' 'interface' Identifier annotationTypeBody
    ;
    
annotationTypeBody
    :   '{' (annotationTypeElementDeclaration)* '}'
    ;
    
annotationTypeElementDeclaration
    :   modifiers annotationTypeElementRest
	|	';' // this is not allowed by the grammar, but apparently allowed by the actual compiler
    ;
    
annotationTypeElementRest
    :   type annotationMethodOrConstantRest ';'
    |   normalClassDeclaration ';'?
    |   normalInterfaceDeclaration ';'?
    |   enumDeclaration ';'?
    |   annotationTypeDeclaration ';'?
    ;
    
annotationMethodOrConstantRest
    :   annotationMethodRest
    |   annotationConstantRest
    ;
    
annotationMethodRest
    :   Identifier '(' ')' defaultValue?
    ;
    
annotationConstantRest
    :   variableDeclarators
    ;
    
defaultValue
    :   'default' elementValue
    ;

// STATEMENTS / BLOCKS

block
    :   '{' blockStatement* '}'
    ;
    
blockStatement
    :   localVariableDeclarationStatement
    |   classOrInterfaceDeclaration
    |   statement
    ;
    
localVariableDeclarationStatement
    :    localVariableDeclaration ';'
    ;

localVariableDeclaration
    :   variableModifiers type variableDeclarators
    ;
    
variableModifiers
    :   variableModifier*
    ;

statement
    : block
    |   ASSERT expression (':' expression)? ';'
    |   'if' parExpression statement ('else' statement)?
    |   'for' '(' forControl ')' statement
    |   'while' parExpression statement
    |   'do' statement 'while' parExpression ';'
    |   'try' block (catches finallyBlock? | finallyBlock)
	|	'try' resourceSpecification block catches? finallyBlock?
    |   'switch' parExpression '{' switchBlockStatementGroups '}'
    |   'synchronized' parExpression block
    |   'return' expression? ';'
    |   'throw' expression ';'
    |   'break' Identifier? ';'
    |   'continue' Identifier? ';'
    |   ';' 
    |   statementExpression ';'
    |   Identifier ':' statement
    ;

catches
    :   catchClause+
    ;

catchClause
    :   'catch' '(' variableModifiers catchType Identifier ')' block
    ;

catchType
	:	qualifiedName ('|' qualifiedName)*
	;

finallyBlock
	:	'finally' block
	;

resourceSpecification
	:	'(' resources ';'? ')'
	;

resources
	:	resource (';' resource)*
	;

resource
	:	variableModifiers classOrInterfaceType variableDeclaratorId '=' expression
	;

formalParameter
    :   variableModifiers type variableDeclaratorId
    ;
        
switchBlockStatementGroups
    :   (switchBlockStatementGroup)*
    ;
    
/* The change here (switchLabel -> switchLabel+) technically makes this grammar
   ambiguous; but with appropriately greedy parsing it yields the most
   appropriate AST, one in which each group, except possibly the last one, has
   labels and statements. */
switchBlockStatementGroup
    :   switchLabel+ blockStatement*
    ;
    
switchLabel
    :   'case' constantExpression ':'
    |   'case' enumConstantName ':'
    |   'default' ':'
    ;
    
forControl
    :   enhancedForControl
    |   forInit? ';' expression? ';' forUpdate?
    ;

forInit
    :   localVariableDeclaration
    |   expressionList
    ;
    
enhancedForControl
    :   variableModifiers type Identifier ':' expression
    ;

forUpdate
    :   expressionList
    ;

// EXPRESSIONS

parExpression
    :   '(' expression ')'
    ;
    
expressionList
    :   expression (',' expression)*
    ;

statementExpression
    :   expression
    ;
    
constantExpression
    :   expression
    ;
    
expression
	:   primary
    |   expression '.' Identifier
    |   expression '.' 'this'
    |   expression '.' 'new' nonWildcardTypeArguments? innerCreator
    |   expression '.' 'super' superSuffix
    |	expression '.' explicitGenericInvocation
    |   'new' creator
    |   expression '[' expression ']'
    |   '(' type ')' expression
    |   expression ('++' | '--')
    |   expression '(' expressionList? ')'
    |   ('+'|'-'|'++'|'--') expression
    |   ('~'|'!') expression
    |   expression ('*'|'/'|'%') expression
    |   expression ('+'|'-') expression
    |   expression ('<' '<' | '>' '>' '>' | '>' '>') expression
    |   expression ('<=' | '>=' | '>' | '<') expression
	|   expression 'instanceof' type
	|   expression ('==' | '!=') expression
	|   expression '&' expression
	|   expression '^' expression
	|   expression '|' expression
	|   expression '&&' expression
	|   expression '||' expression
	|   expression '?' expression ':' expression
	|<assoc=right> expression
        (	'='
        |	'+='
        |	'-='
        |	'*='
        |	'/='
        |	'&='
        |	'|='
        |	'^='
        |	'>>='
        |	'>>>='
        |	'<<='
        |	'%='
        )
        expression
    ;

primary
	:	'(' expression ')'
    |   'this'
    |   'super'
    |   literal
    |   Identifier
    |   type '.' 'class'
    |   'void' '.' 'class'
	|	nonWildcardTypeArguments (explicitGenericInvocationSuffix | 'this' arguments)
    ;

creator
    :   nonWildcardTypeArguments createdName classCreatorRest
    |   createdName (arrayCreatorRest | classCreatorRest)
    ;

createdName
    :   Identifier typeArgumentsOrDiamond? ('.' Identifier typeArgumentsOrDiamond?)*
    |   primitiveType
    ;
    
innerCreator
    :   Identifier nonWildcardTypeArgumentsOrDiamond? classCreatorRest
    ;

arrayCreatorRest
    :   '['
        (   ']' ('[' ']')* arrayInitializer
        |   expression ']' ('[' expression ']')* ('[' ']')*
        )
    ;

classCreatorRest
    :   arguments classBody?
    ;
    
explicitGenericInvocation
    :	nonWildcardTypeArguments explicitGenericInvocationSuffix
    ;
    
nonWildcardTypeArguments
    :   '<' typeList '>'
    ;

typeArgumentsOrDiamond
	:	'<' '>'
	|	typeArguments
	;

nonWildcardTypeArgumentsOrDiamond
	:	'<' '>'
	|	nonWildcardTypeArguments
	;

superSuffix
    :   arguments
    |   '.' Identifier arguments?
    ;

explicitGenericInvocationSuffix
	:	'super' superSuffix
	|	Identifier arguments
	;

arguments
    :   '(' expressionList? ')'
    ;

// LEXER

// §3.9 Keywords

ABSTRACT : 'abstract';
ASSERT : 'assert';
BOOLEAN : 'boolean';
BREAK : 'break';
BYTE : 'byte';
CASE : 'case';
CATCH : 'catch';
CHAR : 'char';
CLASS : 'class';
CONST : 'const';
CONTINUE : 'continue';
DEFAULT : 'default';
DO : 'do';
DOUBLE : 'double';
ELSE : 'else';
ENUM : 'enum';
EXTENDS : 'extends';
FINAL : 'final';
FINALLY : 'finally';
FLOAT : 'float';
FOR : 'for';
IF : 'if';
GOTO : 'goto';
IMPLEMENTS : 'implements';
IMPORT : 'import';
INSTANCEOF : 'instanceof';
INT : 'int';
INTERFACE : 'interface';
LONG : 'long';
NATIVE : 'native';
NEW : 'new';
PACKAGE : 'package';
PRIVATE : 'private';
PROTECTED : 'protected';
PUBLIC : 'public';
RETURN : 'return';
SHORT : 'short';
STATIC : 'static';
STRICTFP : 'strictfp';
SUPER : 'super';
SWITCH : 'switch';
SYNCHRONIZED : 'synchronized';
THIS : 'this';
THROW : 'throw';
THROWS : 'throws';
TRANSIENT : 'transient';
TRY : 'try';
VOID : 'void';
VOLATILE : 'volatile';
WHILE : 'while';

// §3.10.1 Integer Literals

IntegerLiteral
	:	DecimalIntegerLiteral
	|	HexIntegerLiteral
	|	OctalIntegerLiteral
	|	BinaryIntegerLiteral
	;

fragment
DecimalIntegerLiteral
	:	DecimalNumeral IntegerTypeSuffix?
	;

fragment
HexIntegerLiteral
	:	HexNumeral IntegerTypeSuffix?
	;

fragment
OctalIntegerLiteral
	:	OctalNumeral IntegerTypeSuffix?
	;

fragment
BinaryIntegerLiteral
	:	BinaryNumeral IntegerTypeSuffix?
	;

fragment
IntegerTypeSuffix
	:	[lL]
	;

fragment
DecimalNumeral
	:	'0'
	|	NonZeroDigit (Digits? | Underscores Digits)
	;

fragment
Digits
	:	Digit (DigitsAndUnderscores? Digit)?
	;

fragment
Digit
	:	'0'
	|	NonZeroDigit
	;

fragment
NonZeroDigit
	:	[1-9]
	;

fragment
DigitsAndUnderscores
	:	DigitOrUnderscore+
	;

fragment
DigitOrUnderscore
	:	Digit
	|	'_'
	;

fragment
Underscores
	:	'_'+
	;

fragment
HexNumeral
	:	'0' [xX] HexDigits
	;

fragment
HexDigits
	:	HexDigit (HexDigitsAndUnderscores? HexDigit)?
	;

fragment
HexDigit
	:	[0-9a-fA-F]
	;

fragment
HexDigitsAndUnderscores
	:	HexDigitOrUnderscore+
	;

fragment
HexDigitOrUnderscore
	:	HexDigit
	|	'_'
	;

fragment
OctalNumeral
	:	'0' Underscores? OctalDigits
	;

fragment
OctalDigits
	:	OctalDigit (OctalDigitsAndUnderscores? OctalDigit)?
	;

fragment
OctalDigit
	:	[0-7]
	;

fragment
OctalDigitsAndUnderscores
	:	OctalDigitOrUnderscore+
	;

fragment
OctalDigitOrUnderscore
	:	OctalDigit
	|	'_'
	;

fragment
BinaryNumeral
	:	'0' [bB] BinaryDigits
	;

fragment
BinaryDigits
	:	BinaryDigit (BinaryDigitsAndUnderscores? BinaryDigit)?
	;

fragment
BinaryDigit
	:	[01]
	;

fragment
BinaryDigitsAndUnderscores
	:	BinaryDigitOrUnderscore+
	;

fragment
BinaryDigitOrUnderscore
	:	BinaryDigit
	|	'_'
	;

// §3.10.2 Floating-Point Literals

FloatingPointLiteral
	:	DecimalFloatingPointLiteral
	|	HexadecimalFloatingPointLiteral
	;

fragment
DecimalFloatingPointLiteral
	:	Digits '.' Digits? ExponentPart? FloatTypeSuffix?
	|	'.' Digits ExponentPart? FloatTypeSuffix?
	|	Digits ExponentPart FloatTypeSuffix?
	|	Digits FloatTypeSuffix
	;

fragment
ExponentPart
	:	ExponentIndicator SignedInteger
	;

fragment
ExponentIndicator
	:	[eE]
	;

fragment
SignedInteger
	:	Sign? Digits
	;

fragment
Sign
	:	[+-]
	;

fragment
FloatTypeSuffix
	:	[fFdD]
	;

fragment
HexadecimalFloatingPointLiteral
	:	HexSignificand BinaryExponent FloatTypeSuffix?
	;

fragment
HexSignificand
	:	HexNumeral '.'?
	|	'0' [xX] HexDigits? '.' HexDigits
	;

fragment
BinaryExponent
	:	BinaryExponentIndicator SignedInteger
	;

fragment
BinaryExponentIndicator
	:	[pP]
	;

// §3.10.3 Boolean Literals

BooleanLiteral
	:	'true'
	|	'false'
	;

// §3.10.4 Character Literals

CharacterLiteral
	:	'\'' SingleCharacter '\''
	|	'\'' EscapeSequence '\''
	;

fragment
SingleCharacter
	:	~['\\]
	;

// §3.10.5 String Literals

StringLiteral
	:	'"' StringCharacters? '"'
	;

fragment
StringCharacters
	:	StringCharacter+
	;

fragment
StringCharacter
	:	~["\\]
	|	EscapeSequence
	;

// §3.10.6 Escape Sequences for Character and String Literals

fragment
EscapeSequence
	:	'\\' [btnfr"'\\]
	|	OctalEscape
	;

fragment
OctalEscape
	:	'\\' OctalDigit
	|	'\\' OctalDigit OctalDigit
	|	'\\' ZeroToThree OctalDigit OctalDigit
	;

fragment
ZeroToThree
	:	[0-3]
	;

// §3.10.7 The Null Literal

NullLiteral
	:	'null'
	;

// §3.11 Separators

LPAREN : '(';
RPAREN : ')';
LBRACE : '{';
RBRACE : '}';
LBRACK : '[';
RBRACK : ']';
SEMI : ';';
COMMA : ',';
DOT : '.';

// §3.12 Operators

ASSIGN : '=';
GT : '>';
LT : '<';
BANG : '!';
TILDE : '~';
QUESTION : '?';
COLON : ':';
EQUAL : '==';
LE : '<=';
GE : '>=';
NOTEQUAL : '!=';
AND : '&&';
OR : '||';
INC : '++';
DEC : '--';
ADD : '+';
SUB : '-';
MUL : '*';
DIV : '/';
BITAND : '&';
BITOR : '|';
CARET : '^';
MOD : '%';

ADD_ASSIGN : '+=';
SUB_ASSIGN : '-=';
MUL_ASSIGN : '*=';
DIV_ASSIGN : '/=';
AND_ASSIGN : '&=';
OR_ASSIGN : '|=';
XOR_ASSIGN : '^=';
MOD_ASSIGN : '%=';
LSHIFT_ASSIGN : '<<=';
RSHIFT_ASSIGN : '>>=';
URSHIFT_ASSIGN : '>>>=';

// §3.8 Identifiers (must appear after all keywords in the grammar)

Identifier
	:	JavaLetter JavaLetterOrDigit*
	;

fragment
JavaLetter
	:	[a-zA-Z$_] // these are the "java letters" below 0xFF
	|	// covers all characters above 0xFF which are not a surrogate
		~[\u0000-\u00FF\uD800-\uDBFF]
		{Character.isJavaIdentifierStart(_input.LA(-1))}?
	|	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
		[\uD800-\uDBFF] [\uDC00-\uDFFF]
		{Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
	;

fragment
JavaLetterOrDigit
	:	[a-zA-Z0-9$_] // these are the "java letters or digits" below 0xFF
	|	// covers all characters above 0xFF which are not a surrogate
		~[\u0000-\u00FF\uD800-\uDBFF]
		{Character.isJavaIdentifierPart(_input.LA(-1))}?
	|	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
		[\uD800-\uDBFF] [\uDC00-\uDFFF]
		{Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
	;

//
// Additional symbols not defined in the lexical specification
//

AT : '@';
ELLIPSIS : '...';

//
// Whitespace and comments
//

WS  :  [ \t\r\n\u000C]+ -> skip
    ;

COMMENT
    :   '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;

grammar JavaLR;

@lexer::members {
  protected boolean enumIsKeyword = true;
  protected boolean assertIsKeyword = true;
}

@parser::members {
/*
public void enterRule(ParserRuleContext<Token> localctx, int ruleIndex) {
	super.enterRule(localctx, ruleIndex);
	System.out.println("enter "+ruleNames[ruleIndex]+
              ", LT(1)="+_input.LT(1)+
              ", LT(2)="+_input.LT(2));
}
	@Override
	public void exitRule(int ruleIndex) {
		super.exitRule(ruleIndex);
		System.err.println("exit "+ruleNames[ruleIndex]+
	              ", LT(1)="+_input.LT(1)+
	              ", LT(2)="+_input.LT(2));
	}

*/
}

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
    :   annotation   // class or interface
    |   'public'     // class or interface
    |   'protected'  // class or interface
    |   'private'    // class or interface
    |   'abstract'   // class or interface
    |   'static'     // class or interface
    |   'final'      // class only -- does not apply to interfaces
    |   'strictfp'   // class or interface
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
    |   'public'
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
    :   '{' explicitConstructorInvocation? blockStatement* '}'
    ;

explicitConstructorInvocation
    :   nonWildcardTypeArguments? ('this' | 'super') arguments ';'
    |   primary '.' nonWildcardTypeArguments? 'super' arguments ';'
    ;

qualifiedName
    :   Identifier ('.' Identifier)*
    ;

literal
    :   integerLiteral
    |   FloatingPointLiteral
    |   CharacterLiteral
    |   StringLiteral
    |   booleanLiteral
    |   'null'
    ;

integerLiteral
    :   HexLiteral
    |   OctalLiteral
    |   DecimalLiteral
    ;

booleanLiteral
    :   'true'
    |   'false'
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
    |   'try' block
        ( catches 'finally' block
        | catches
        | 'finally' block
        )
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
    :   catchClause (catchClause)*
    ;

catchClause
    :   'catch' '(' formalParameter ')' block
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

//expression : expression_[0] ;

expression
	:   primary
    |   expression '.' Identifier
    |   expression '.' 'this'
    |   expression '.' 'super' '(' expressionList? ')'
    |   expression '.' 'new' Identifier '(' expressionList? ')'
    |   expression '.' 'super' '.' Identifier arguments?
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
    |   expression ('<' '=' | '>' '=' | '>' | '<') expression
    |   expression 'instanceof' type
    |   expression ('==' | '!=') expression
    |   expression '&' expression
    |   expression '^'<assoc=right> expression
    |   expression '|' expression
    |   expression '&&' expression
    |   expression '||' expression
    |   expression '?' expression ':' expression
    |   expression
    	('^='<assoc=right>
        |'+='<assoc=right>
        |'-='<assoc=right>
        |'*='<assoc=right>
        |'/='<assoc=right>
        |'&='<assoc=right>
        |'|='<assoc=right>
        |'='<assoc=right>
        |'>' '>' '='<assoc=right>
        |'>' '>' '>' '='<assoc=right>
        |'<' '<' '='<assoc=right>
        |'%='<assoc=right>
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
    ;
    
/*
expression_[int _p]
    :   expression_primary
        (
                {13 >= $_p}? ('*'|'/'|'%') expression_[14]
                          | {12 >= $_p}? ('+'|'-') expression_[13]
                          | {11 >= $_p}? ('<' '<' | '>' '>' '>' | '>' '>') expression_[12]
                          | {10 >= $_p}? ('<=' | '>=' | '>' | '<') expression_[11]
                          | {8 >= $_p}? ('==' | '!=') expression_[9]
                          | {7 >= $_p}? '&' expression_[8]
                          | {6 >= $_p}? '^'<assoc=right> expression_[6]
                          | {5 >= $_p}? '|' expression_[6]
                          | {4 >= $_p}? '&&' expression_[5]
                          | {3 >= $_p}? '||' expression_[4]
                          | {1 >= $_p}? ('^='<assoc=right>
                        |'+='<assoc=right>
                        |'-='<assoc=right>
                        |'*='<assoc=right>
                        |'/='<assoc=right>
                        |'&='<assoc=right>
                        |'|='<assoc=right>
                        |'='<assoc=right>
                        |'>' '>' '='<assoc=right>
                        |'>' '>' '>' '='<assoc=right>
                        |'<' '<' '='<assoc=right>
                        |'%='<assoc=right>
                        )
                        expression_[1]
                          | {2 >= $_p}? '?' expression ':' expression_[3]
                          | {26 >= $_p}? '.' Identifier
                          | {25 >= $_p}? '.' 'this'
                          | {24 >= $_p}? '.' 'super' '(' expressionList? ')'
                          | {23 >= $_p}? '.' 'new' Identifier '(' expressionList? ')'
                          | {22 >= $_p}? '.' 'super' '.' Identifier arguments?
                          | {21 >= $_p}? '.' explicitGenericInvocation
                          | {19 >= $_p}? '[' expression ']'
                          | {17 >= $_p}? ('++' | '--')
                          | {16 >= $_p}? '(' expressionList? ')'
                          | {9 >= $_p}? 'instanceof' type
        )*
    ;
2011-11-30 18:39:48:343 left-recursion LogManager.java:48 expression_primary
    : '(' type ')' expression_[18]
    | ('+'|'-'|'++'|'--') expression_[15]
    | ('~'|'!') expression_[14]
    | '(' expression ')'
    | 'this'
    | 'super'
    | literal
    | Identifier
    | type '.' 'class'
    | 'new' creator
    ;
*/

creator
    :   nonWildcardTypeArguments createdName classCreatorRest
    |   createdName (arrayCreatorRest | classCreatorRest)
    ;

createdName
    :   classOrInterfaceType
    |   primitiveType
    ;

innerCreator
    :   nonWildcardTypeArguments? Identifier classCreatorRest
    ;

explicitGenericInvocation
    :	nonWildcardTypeArguments Identifier arguments
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

nonWildcardTypeArguments
    :   '<' typeList '>'
    ;

arguments
    :   '(' expressionList? ')'
    ;

// LEXER

HexLiteral : '0' ('x'|'X') HexDigit+ IntegerTypeSuffix? ;

DecimalLiteral : ('0' | '1'..'9' '0'..'9'*) IntegerTypeSuffix? ;

OctalLiteral : '0' ('0'..'7')+ IntegerTypeSuffix? ;

fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
IntegerTypeSuffix : ('l'|'L') ;

FloatingPointLiteral
    :   ('0'..'9')+ '.' ('0'..'9')* Exponent? FloatTypeSuffix?
    |   '.' ('0'..'9')+ Exponent? FloatTypeSuffix?
    |   ('0'..'9')+ Exponent FloatTypeSuffix?
    |   ('0'..'9')+ FloatTypeSuffix
    |   ('0x' | '0X') (HexDigit )*
        ('.' (HexDigit)*)?
        ( 'p' | 'P' )
        ( '+' | '-' )?
        ( '0' .. '9' )+
        FloatTypeSuffix?
    ;

fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
FloatTypeSuffix : ('f'|'F'|'d'|'D') ;

CharacterLiteral
    :   '\'' ( EscapeSequence | ~('\''|'\\') ) '\''
    ;

StringLiteral
    :  '"' ( EscapeSequence | ~('\\'|'"') )* '"'
    ;

fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;

fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

ENUM:   'enum' {if (!enumIsKeyword) $type=Identifier;}
    ;
    
ASSERT
    :   'assert' {if (!assertIsKeyword) $type=Identifier;}
    ;
    
Identifier 
    :   Letter (Letter|JavaIDDigit)*
    ;

/**I found this char range in JavaCC's grammar, but Letter and Digit overlap.
   Still works, but...
 */
fragment
Letter
    :  '\u0024' |
       '\u0041'..'\u005a' |
       '\u005f' |
       '\u0061'..'\u007a' |
       '\u00c0'..'\u00d6' |
       '\u00d8'..'\u00f6' |
       '\u00f8'..'\u00ff' |
       '\u0100'..'\u1fff' |
       '\u3040'..'\u318f' |
       '\u3300'..'\u337f' |
       '\u3400'..'\u3d2d' |
       '\u4e00'..'\u9fff' |
       '\uf900'..'\ufaff'
    ;

fragment
JavaIDDigit
    :  '\u0030'..'\u0039' |
       '\u0660'..'\u0669' |
       '\u06f0'..'\u06f9' |
       '\u0966'..'\u096f' |
       '\u09e6'..'\u09ef' |
       '\u0a66'..'\u0a6f' |
       '\u0ae6'..'\u0aef' |
       '\u0b66'..'\u0b6f' |
       '\u0be7'..'\u0bef' |
       '\u0c66'..'\u0c6f' |
       '\u0ce6'..'\u0cef' |
       '\u0d66'..'\u0d6f' |
       '\u0e50'..'\u0e59' |
       '\u0ed0'..'\u0ed9' |
       '\u1040'..'\u1049'
   ;

WS  :  (' '|'\r'|'\t'|'\u000C'|'\n')+ {$channel=HIDDEN;}
    ;

COMMENT
    :   '/*' .* '*/' {$channel=HIDDEN;}
    ;

LINE_COMMENT
    : '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
	;
    

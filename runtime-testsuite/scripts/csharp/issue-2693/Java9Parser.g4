parser grammar Java9Parser;

options {
    tokenVocab=Java9Lexer;
    superClass=ParserBase;
}

compilationUnit
    : packageDeclaration? importDeclaration* typeDeclaration* EOF
    | modularCompilation
    ;

packageDeclaration
    : annotation* 'package' packageName ';'
    ;

annotation
    : normalAnnotation
    | markerAnnotation
    | singleElementAnnotation
    ;

normalAnnotation
    : '@' typeName '(' elementValuePairList? ')'
    ;

typeName
    : identifier
    | packageOrTypeName '.' identifier
    ;

// LEXER

identifier
    : Identifier
    | 'to'
    | 'module'
    | 'open'
    | 'with'
    | 'provides'
    | 'uses'
    | 'opens'
    | 'requires'
    | 'exports'
    ;

packageOrTypeName
    : identifier
    | packageOrTypeName '.' identifier
    ;

elementValuePairList
    : elementValuePair (',' elementValuePair)*
    ;

elementValuePair
    : identifier '=' elementValue
    ;

elementValue
    : conditionalExpression
    | elementValueArrayInitializer
    | annotation
    ;

conditionalExpression
    : conditionalOrExpression
    | conditionalOrExpression '?' expression ':' (conditionalExpression|lambdaExpression)
    ;

conditionalOrExpression
    : conditionalAndExpression
    | conditionalOrExpression '||' conditionalAndExpression
    ;

conditionalAndExpression
    : inclusiveOrExpression
    | conditionalAndExpression '&&' inclusiveOrExpression
    ;

inclusiveOrExpression
    : exclusiveOrExpression
    | inclusiveOrExpression '|' exclusiveOrExpression
    ;

exclusiveOrExpression
    : andExpression
    | exclusiveOrExpression '^' andExpression
    ;

andExpression
    : equalityExpression
    | andExpression '&' equalityExpression
    ;

equalityExpression
    : relationalExpression
    | equalityExpression '==' relationalExpression
    | equalityExpression '!=' relationalExpression
    ;

relationalExpression
    : shiftExpression
    | relationalExpression '<' shiftExpression
    | relationalExpression '>' shiftExpression
    | relationalExpression '<=' shiftExpression
    | relationalExpression '>=' shiftExpression
    | relationalExpression 'instanceof' referenceType
    ;

shiftExpression
    : additiveExpression
    | shiftExpression '<' '<' additiveExpression
    | shiftExpression '>' '>' additiveExpression
    | shiftExpression '>' '>' '>' additiveExpression
    ;

additiveExpression
    : multiplicativeExpression
    | additiveExpression '+' multiplicativeExpression
    | additiveExpression '-' multiplicativeExpression
    ;

multiplicativeExpression
    : unaryExpression
    | multiplicativeExpression '*' unaryExpression
    | multiplicativeExpression '/' unaryExpression
    | multiplicativeExpression '%' unaryExpression
    ;

unaryExpression
    : preIncrementExpression
    | preDecrementExpression
    | '+' unaryExpression
    | '-' unaryExpression
    | unaryExpressionNotPlusMinus
    ;

preIncrementExpression
    : '++' unaryExpression
    ;

preDecrementExpression
    : '--' unaryExpression
    ;

unaryExpressionNotPlusMinus
    : postfixExpression
    | '~' unaryExpression
    | '!' unaryExpression
    | castExpression
    ;

/*postfixExpression
    :   primary
    |   expressionName
    |   postIncrementExpression
    |   postDecrementExpression
    ;
*/

postfixExpression
    : (primary|expressionName) (postIncrementExpression_lf_postfixExpression|postDecrementExpression_lf_postfixExpression)*
    ;

/*
 * Productions from §15 (Expressions)
 */

/*primary
    :   primaryNoNewArray
    |   arrayCreationExpression
    ;
*/

primary
    : (primaryNoNewArray_lfno_primary|arrayCreationExpression) (primaryNoNewArray_lf_primary)*
    ;

primaryNoNewArray_lfno_primary
    : literal
    | typeName ('[' ']')* '.' 'class'
    | unannPrimitiveType ('[' ']')* '.' 'class'
    | 'void' '.' 'class'
    | 'this'
    | typeName '.' 'this'
    | '(' expression ')'
    | classInstanceCreationExpression_lfno_primary
    | fieldAccess_lfno_primary
    | arrayAccess_lfno_primary
    | methodInvocation_lfno_primary
    | methodReference_lfno_primary
    ;

literal
    : IntegerLiteral
    | FloatingPointLiteral
    | BooleanLiteral
    | CharacterLiteral
    | StringLiteral
    | NullLiteral
    ;

unannPrimitiveType
    : numericType
    | 'boolean'
    ;

numericType
    : integralType
    | floatingPointType
    ;

integralType
    : 'byte'
    | 'short'
    | 'int'
    | 'long'
    | 'char'
    ;

floatingPointType
    : 'float'
    | 'double'
    ;

expression
    : lambdaExpression
    | assignmentExpression
    ;

lambdaExpression
    : lambdaParameters '->' lambdaBody
    ;

lambdaParameters
    : identifier
    | '(' formalParameterList? ')'
    | '(' inferredFormalParameterList ')'
    ;

formalParameterList
    : formalParameters ',' lastFormalParameter
    | lastFormalParameter
    | receiverParameter
    ;

formalParameters
    : formalParameter (',' formalParameter)*
    | receiverParameter (',' formalParameter)*
    ;

formalParameter
    : variableModifier* unannType variableDeclaratorId
    ;

variableModifier
    : annotation
    | 'final'
    ;

unannType
    : unannPrimitiveType
    | unannReferenceType
    ;

unannReferenceType
    : unannClassOrInterfaceType
    | unannTypeVariable
    | unannArrayType
    ;

/*unannClassOrInterfaceType
    :   unannClassType
    |   unannInterfaceType
    ;
*/

unannClassOrInterfaceType
    : (unannClassType_lfno_unannClassOrInterfaceType|unannInterfaceType_lfno_unannClassOrInterfaceType) (unannClassType_lf_unannClassOrInterfaceType|unannInterfaceType_lf_unannClassOrInterfaceType)*
    ;

unannClassType_lfno_unannClassOrInterfaceType
    : identifier typeArguments?
    ;

typeArguments
    : '<' typeArgumentList '>'
    ;

typeArgumentList
    : typeArgument (',' typeArgument)*
    ;

typeArgument
    : referenceType
    | wildcard
    ;

referenceType
    : classOrInterfaceType
    | typeVariable
    | arrayType
    ;

/*classOrInterfaceType
    :   classType
    |   interfaceType
    ;
*/

classOrInterfaceType
    : (classType_lfno_classOrInterfaceType|interfaceType_lfno_classOrInterfaceType) (classType_lf_classOrInterfaceType|interfaceType_lf_classOrInterfaceType)*
    ;

classType_lfno_classOrInterfaceType
    : annotation* identifier typeArguments?
    ;

interfaceType_lfno_classOrInterfaceType
    : classType_lfno_classOrInterfaceType
    ;

classType_lf_classOrInterfaceType
    : '.' annotation* identifier typeArguments?
    ;

interfaceType_lf_classOrInterfaceType
    : classType_lf_classOrInterfaceType
    ;

typeVariable
    : annotation* identifier
    ;

arrayType
    : primitiveType dims
    | classOrInterfaceType dims
    | typeVariable dims
    ;

/*
 * Productions from §4 (Types, Values, and Variables)
 */

primitiveType
    : annotation* numericType
    | annotation* 'boolean'
    ;

dims
    : annotation* '[' ']' (annotation* '[' ']')*
    ;

wildcard
    : annotation* '?' wildcardBounds?
    ;

wildcardBounds
    : 'extends' referenceType
    | 'super' referenceType
    ;

unannInterfaceType_lfno_unannClassOrInterfaceType
    : unannClassType_lfno_unannClassOrInterfaceType
    ;

unannClassType_lf_unannClassOrInterfaceType
    : '.' annotation* identifier typeArguments?
    ;

unannInterfaceType_lf_unannClassOrInterfaceType
    : unannClassType_lf_unannClassOrInterfaceType
    ;

unannTypeVariable
    : identifier
    ;

unannArrayType
    : unannPrimitiveType dims
    | unannClassOrInterfaceType dims
    | unannTypeVariable dims
    ;

variableDeclaratorId
    : identifier dims?
    ;

receiverParameter
    : annotation* unannType (identifier '.')? 'this'
    ;

lastFormalParameter
    : variableModifier* unannType annotation* '...' variableDeclaratorId
    | formalParameter
    ;

inferredFormalParameterList
    : identifier (',' identifier)*
    ;

lambdaBody
    : expression
    | block
    ;

/*
 * Productions from §14 (Blocks and Statements)
 */

block
    : '{' blockStatements? '}'
    ;

blockStatements
    : blockStatement+
    ;

blockStatement
    : localVariableDeclarationStatement
    | classDeclaration
    | statement
    ;

localVariableDeclarationStatement
    : localVariableDeclaration ';'
    ;

localVariableDeclaration
    : variableModifier* unannType variableDeclaratorList
    ;

variableDeclaratorList
    : variableDeclarator (',' variableDeclarator)*
    ;

variableDeclarator
    : variableDeclaratorId ('=' variableInitializer)?
    ;

variableInitializer
    : expression
    | arrayInitializer
    ;

/*
 * Productions from §10 (Arrays)
 */

arrayInitializer
    : '{' variableInitializerList? ','? '}'
    ;

variableInitializerList
    : variableInitializer (',' variableInitializer)*
    ;

/*
 * Productions from §8 (Classes)
 */

classDeclaration
    : normalClassDeclaration
    | enumDeclaration
    ;

normalClassDeclaration
    : classModifier* 'class' identifier typeParameters? superclass? superinterfaces? classBody
    ;

classModifier
    : annotation
    | 'public'
    | 'protected'
    | 'private'
    | 'abstract'
    | 'static'
    | 'final'
    | 'strictfp'
    ;

typeParameters
    : '<' typeParameterList '>'
    ;

typeParameterList
    : typeParameter (',' typeParameter)*
    ;

typeParameter
    : typeParameterModifier* identifier typeBound?
    ;

typeParameterModifier
    : annotation
    ;

typeBound
    : 'extends' typeVariable
    | 'extends' classOrInterfaceType additionalBound*
    ;
additionalBound
    : '&' interfaceType
    ;

interfaceType
    : classType
    ;

classType
    : annotation* identifier typeArguments?
    | classOrInterfaceType '.' annotation* identifier typeArguments?
    ;

superclass
    : 'extends' classType
    ;

superinterfaces
    : 'implements' interfaceTypeList
    ;

interfaceTypeList
    : interfaceType (',' interfaceType)*
    ;

classBody
    : '{' classBodyDeclaration* '}'
    ;

classBodyDeclaration
    : classMemberDeclaration
    | instanceInitializer
    | staticInitializer
    | constructorDeclaration
    ;

classMemberDeclaration
    : fieldDeclaration
    | methodDeclaration
    | classDeclaration
    | interfaceDeclaration
    | ';'
    ;

fieldDeclaration
    : fieldModifier* unannType variableDeclaratorList ';'
    ;

fieldModifier
    : annotation
    | 'public'
    | 'protected'
    | 'private'
    | 'static'
    | 'final'
    | 'transient'
    | 'volatile'
    ;

methodDeclaration
    : methodModifier* methodHeader methodBody
    ;

methodModifier
    : annotation
    | 'public'
    | 'protected'
    | 'private'
    | 'abstract'
    | 'static'
    | 'final'
    | 'synchronized'
    | 'native'
    | 'strictfp'
    ;

methodHeader
    : result methodDeclarator throws_?
    | typeParameters annotation* result methodDeclarator throws_?
    ;

result
    : unannType
    | 'void'
    ;

methodDeclarator
    : identifier '(' formalParameterList? ')' dims?
    ;

throws_
    : 'throws' exceptionTypeList
    ;

exceptionTypeList
    : exceptionType (',' exceptionType)*
    ;

exceptionType
    : classType
    | typeVariable
    ;

methodBody
    : block
    | ';'
    ;

/*
 * Productions from §9 (Interfaces)
 */

interfaceDeclaration
    : normalInterfaceDeclaration
    | annotationTypeDeclaration
    ;

normalInterfaceDeclaration
    : interfaceModifier* 'interface' identifier typeParameters? extendsInterfaces? interfaceBody
    ;

interfaceModifier
    : annotation
    | 'public'
    | 'protected'
    | 'private'
    | 'abstract'
    | 'static'
    | 'strictfp'
    ;

extendsInterfaces
    : 'extends' interfaceTypeList
    ;

interfaceBody
    : '{' interfaceMemberDeclaration* '}'
    ;

interfaceMemberDeclaration
    : constantDeclaration
    | interfaceMethodDeclaration
    | classDeclaration
    | interfaceDeclaration
    | ';'
    ;

constantDeclaration
    : constantModifier* unannType variableDeclaratorList ';'
    ;

constantModifier
    : annotation
    | 'public'
    | 'static'
    | 'final'
    ;

interfaceMethodDeclaration
    : interfaceMethodModifier* methodHeader methodBody
    ;

interfaceMethodModifier
    : annotation
    | 'public'
    | 'private'//Introduced in Java 9
    | 'abstract'
    | 'default'
    | 'static'
    | 'strictfp'
    ;

annotationTypeDeclaration
    : interfaceModifier* '@' 'interface' identifier annotationTypeBody
    ;

annotationTypeBody
    : '{' annotationTypeMemberDeclaration* '}'
    ;

annotationTypeMemberDeclaration
    : annotationTypeElementDeclaration
    | constantDeclaration
    | classDeclaration
    | interfaceDeclaration
    | ';'
    ;

annotationTypeElementDeclaration
    : annotationTypeElementModifier* unannType identifier '(' ')' dims? defaultValue? ';'
    ;

annotationTypeElementModifier
    : annotation
    | 'public'
    | 'abstract'
    ;

defaultValue
    : 'default' elementValue
    ;

instanceInitializer
    : block
    ;

staticInitializer
    : 'static' block
    ;

constructorDeclaration
    : constructorModifier* constructorDeclarator throws_? constructorBody
    ;

constructorModifier
    : annotation
    | 'public'
    | 'protected'
    | 'private'
    ;

constructorDeclarator
    : typeParameters? simpleTypeName '(' formalParameterList? ')'
    ;

simpleTypeName
    : identifier
    ;

constructorBody
    : '{' explicitConstructorInvocation? blockStatements? '}'
    ;

explicitConstructorInvocation
    : typeArguments? 'this' '(' argumentList? ')' ';'
    | typeArguments? 'super' '(' argumentList? ')' ';'
    | expressionName '.' typeArguments? 'super' '(' argumentList? ')' ';'
    | primary '.' typeArguments? 'super' '(' argumentList? ')' ';'
    ;

argumentList
    : expression (',' expression)*
    ;

expressionName
    : identifier
    | ambiguousName '.' identifier
    ;

ambiguousName
    : identifier
    | ambiguousName '.' identifier
    ;

enumDeclaration
    : classModifier* 'enum' identifier superinterfaces? enumBody
    ;

enumBody
    : '{' enumConstantList? ','? enumBodyDeclarations? '}'
    ;

enumConstantList
    : enumConstant (',' enumConstant)*
    ;

enumConstant
    : enumConstantModifier* identifier ('(' argumentList? ')')? classBody?
    ;

enumConstantModifier
    : annotation
    ;

enumBodyDeclarations
    : ';' classBodyDeclaration*
    ;

statement
    : statementWithoutTrailingSubstatement
    | labeledStatement
    | ifThenStatement
    | ifThenElseStatement
    | whileStatement
    | forStatement
    ;

statementWithoutTrailingSubstatement
    : block
    | emptyStatement
    | expressionStatement
    | assertStatement
    | switchStatement
    | doStatement
    | breakStatement
    | continueStatement
    | returnStatement
    | synchronizedStatement
    | throwStatement
    | tryStatement
    ;

emptyStatement
    : ';'
    ;

expressionStatement
    : statementExpression ';'
    ;

statementExpression
    : assignment
    | preIncrementExpression
    | preDecrementExpression
    | postIncrementExpression
    | postDecrementExpression
    | methodInvocation
    | classInstanceCreationExpression
    ;

assignment
    : leftHandSide assignmentOperator expression
    ;

leftHandSide
    : expressionName
    | fieldAccess
    | arrayAccess
    ;

fieldAccess
    : primary '.' identifier
    | 'super' '.' identifier
    | typeName '.' 'super' '.' identifier
    ;

/*arrayAccess
    :   expressionName '[' expression ']'
    |   primaryNoNewArray '[' expression ']'
    ;
*/

arrayAccess
    : (expressionName '[' expression ']'|primaryNoNewArray_lfno_arrayAccess '[' expression ']') (primaryNoNewArray_lf_arrayAccess '[' expression ']')*
    ;

primaryNoNewArray_lfno_arrayAccess
    : literal
    | typeName ('[' ']')* '.' 'class'
    | 'void' '.' 'class'
    | 'this'
    | typeName '.' 'this'
    | '(' expression ')'
    | classInstanceCreationExpression
    | fieldAccess
    | methodInvocation
    | methodReference
    ;

classInstanceCreationExpression
    : 'new' typeArguments? annotation* identifier ('.' annotation* identifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
    | expressionName '.' 'new' typeArguments? annotation* identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
    | primary '.' 'new' typeArguments? annotation* identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
    ;

typeArgumentsOrDiamond
    : typeArguments
    | '<' '>'
    ;

methodInvocation
    : methodName '(' argumentList? ')'
    | typeName '.' typeArguments? identifier '(' argumentList? ')'
    | expressionName '.' typeArguments? identifier '(' argumentList? ')'
    | primary '.' typeArguments? identifier '(' argumentList? ')'
    | 'super' '.' typeArguments? identifier '(' argumentList? ')'
    | typeName '.' 'super' '.' typeArguments? identifier '(' argumentList? ')'
    ;

methodName
    : identifier
    ;

methodReference
    : expressionName '::' typeArguments? identifier
    | referenceType '::' typeArguments? identifier
    | primary '::' typeArguments? identifier
    | 'super' '::' typeArguments? identifier
    | typeName '.' 'super' '::' typeArguments? identifier
    | classType '::' typeArguments? 'new'
    | arrayType '::' 'new'
    ;

primaryNoNewArray_lf_arrayAccess
    :
    ;

assignmentOperator
    : '='
    | '*='
    | '/='
    | '%='
    | '+='
    | '-='
    | '<<='
    | '>>='
    | '>>>='
    | '&='
    | '^='
    | '|='
    ;

postIncrementExpression
    : postfixExpression '++'
    ;

postDecrementExpression
    : postfixExpression '--'
    ;

assertStatement
    : 'assert' expression ';'
    | 'assert' expression ':' expression ';'
    ;

switchStatement
    : 'switch' '(' expression ')' switchBlock
    ;

switchBlock
    : '{' switchBlockStatementGroup* switchLabel* '}'
    ;

switchBlockStatementGroup
    : switchLabels blockStatements
    ;

switchLabels
    : switchLabel+
    ;

switchLabel
    : 'case' constantExpression ':'
    | 'case' enumConstantName ':'
    | 'default' ':'
    ;

constantExpression
    : expression
    ;

enumConstantName
    : identifier
    ;

doStatement
    : 'do' statement 'while' '(' expression ')' ';'
    ;

breakStatement
    : 'break' identifier? ';'
    ;

continueStatement
    : 'continue' identifier? ';'
    ;

returnStatement
    : 'return' expression? ';'
    ;

synchronizedStatement
    : 'synchronized' '(' expression ')' block
    ;

throwStatement
    : 'throw' expression ';'
    ;

tryStatement
    : 'try' block catches
    | 'try' block catches? finally_
    | tryWithResourcesStatement
    ;

catches
    : catchClause+
    ;

catchClause
    : 'catch' '(' catchFormalParameter ')' block
    ;

catchFormalParameter
    : variableModifier* catchType variableDeclaratorId
    ;

catchType
    : unannClassType ('|' classType)*
    ;

unannClassType
    : identifier typeArguments?
    | unannClassOrInterfaceType '.' annotation* identifier typeArguments?
    ;

finally_
    : 'finally' block
    ;

tryWithResourcesStatement
    : 'try' resourceSpecification block catches? finally_?
    ;

resourceSpecification
    : '(' resourceList ';'? ')'
    ;

resourceList
    : resource (';' resource)*
    ;

resource
    : variableModifier* unannType variableDeclaratorId '=' expression
    | variableAccess//Introduced in Java 9
    ;

variableAccess
    : expressionName
    | fieldAccess
    ;

labeledStatement
    : identifier ':' statement
    ;

ifThenStatement
    : 'if' '(' expression ')' statement
    ;

ifThenElseStatement
    : 'if' '(' expression ')' statementNoShortIf 'else' statement
    ;

statementNoShortIf
    : statementWithoutTrailingSubstatement
    | labeledStatementNoShortIf
    | ifThenElseStatementNoShortIf
    | whileStatementNoShortIf
    | forStatementNoShortIf
    ;

labeledStatementNoShortIf
    : identifier ':' statementNoShortIf
    ;

ifThenElseStatementNoShortIf
    : 'if' '(' expression ')' statementNoShortIf 'else' statementNoShortIf
    ;

whileStatementNoShortIf
    : 'while' '(' expression ')' statementNoShortIf
    ;

forStatementNoShortIf
    : basicForStatementNoShortIf
    | enhancedForStatementNoShortIf
    ;

basicForStatementNoShortIf
    : 'for' '(' forInit? ';' expression? ';' forUpdate? ')' statementNoShortIf
    ;

forInit
    : statementExpressionList
    | localVariableDeclaration
    ;

statementExpressionList
    : statementExpression (',' statementExpression)*
    ;

forUpdate
    : statementExpressionList
    ;

enhancedForStatementNoShortIf
    : 'for' '(' variableModifier* unannType variableDeclaratorId ':' expression ')' statementNoShortIf
    ;

whileStatement
    : 'while' '(' expression ')' statement
    ;

forStatement
    : basicForStatement
    | enhancedForStatement
    ;

basicForStatement
    : 'for' '(' forInit? ';' expression? ';' forUpdate? ')' statement
    ;

enhancedForStatement
    : 'for' '(' variableModifier* unannType variableDeclaratorId ':' expression ')' statement
    ;

assignmentExpression
    : conditionalExpression
    | assignment
    ;

classInstanceCreationExpression_lfno_primary
    : 'new' typeArguments? annotation* identifier ('.' annotation* identifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
    | expressionName '.' 'new' typeArguments? annotation* identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
    ;

fieldAccess_lfno_primary
    : 'super' '.' identifier
    | typeName '.' 'super' '.' identifier
    ;

arrayAccess_lfno_primary
    : (expressionName '[' expression ']'|primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary '[' expression ']') (primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary '[' expression ']')*
    ;

primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary
    : literal
    | typeName ('[' ']')* '.' 'class'
    | unannPrimitiveType ('[' ']')* '.' 'class'
    | 'void' '.' 'class'
    | 'this'
    | typeName '.' 'this'
    | '(' expression ')'
    | classInstanceCreationExpression_lfno_primary
    | fieldAccess_lfno_primary
    | methodInvocation_lfno_primary
    | methodReference_lfno_primary
    ;

methodInvocation_lfno_primary
    : methodName '(' argumentList? ')'
    | typeName '.' typeArguments? identifier '(' argumentList? ')'
    | expressionName '.' typeArguments? identifier '(' argumentList? ')'
    | 'super' '.' typeArguments? identifier '(' argumentList? ')'
    | typeName '.' 'super' '.' typeArguments? identifier '(' argumentList? ')'
    ;

methodReference_lfno_primary
    : expressionName '::' typeArguments? identifier
    | referenceType '::' typeArguments? identifier
    | 'super' '::' typeArguments? identifier
    | typeName '.' 'super' '::' typeArguments? identifier
    | classType '::' typeArguments? 'new'
    | arrayType '::' 'new'
    ;

primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary
    :
    ;

arrayCreationExpression
    : 'new' primitiveType dimExprs dims?
    | 'new' classOrInterfaceType dimExprs dims?
    | 'new' primitiveType dims arrayInitializer
    | 'new' classOrInterfaceType dims arrayInitializer
    ;

dimExprs
    : dimExpr+
    ;

dimExpr
    : annotation* '[' expression ']'
    ;

primaryNoNewArray_lf_primary
    : classInstanceCreationExpression_lf_primary
    | fieldAccess_lf_primary
    | arrayAccess_lf_primary
    | methodInvocation_lf_primary
    | methodReference_lf_primary
    ;

classInstanceCreationExpression_lf_primary
    : '.' 'new' typeArguments? annotation* identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
    ;

fieldAccess_lf_primary
    : '.' identifier
    ;

arrayAccess_lf_primary
    : (primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary '[' expression ']') (primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary '[' expression ']')*
    ;

primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary
    : classInstanceCreationExpression_lf_primary
    | fieldAccess_lf_primary
    | methodInvocation_lf_primary
    | methodReference_lf_primary
    ;

methodInvocation_lf_primary
    : '.' typeArguments? identifier '(' argumentList? ')'
    ;

methodReference_lf_primary
    : '::' typeArguments? identifier
    ;

primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary
    :
    ;

postIncrementExpression_lf_postfixExpression
    : '++'
    ;

postDecrementExpression_lf_postfixExpression
    : '--'
    ;

castExpression
    : '(' primitiveType ')' unaryExpression
    | '(' referenceType additionalBound* ')' unaryExpressionNotPlusMinus
    | '(' referenceType additionalBound* ')' lambdaExpression
    ;

elementValueArrayInitializer
    : '{' elementValueList? ','? '}'
    ;

elementValueList
    : elementValue (',' elementValue)*
    ;

markerAnnotation
    : '@' typeName
    ;

singleElementAnnotation
    : '@' typeName '(' elementValue ')'
    ;

packageName
    : identifier
    | packageName '.' identifier
    ;

importDeclaration
    : singleTypeImportDeclaration
    | typeImportOnDemandDeclaration
    | singleStaticImportDeclaration
    | staticImportOnDemandDeclaration
    ;

singleTypeImportDeclaration
    : 'import' typeName ';'
    ;

typeImportOnDemandDeclaration
    : 'import' packageOrTypeName '.' '*' ';'
    ;

singleStaticImportDeclaration
    : 'import' 'static' typeName '.' identifier ';'
    ;

staticImportOnDemandDeclaration
    : 'import' 'static' typeName '.' '*' ';'
    ;

typeDeclaration
    : classDeclaration
    | interfaceDeclaration
    | ';'
    ;

modularCompilation
    : importDeclaration* moduleDeclaration
    ;

moduleDeclaration
    : annotation* 'open'? 'module' moduleName '{' moduleDirective* '}'
    ;

/*
 * Productions from §6 (Names)
 */

moduleName
    : identifier
    | moduleName '.' identifier
    ;

moduleDirective
    : 'requires' requiresModifier* moduleName ';'
    | 'exports' packageName ('to' moduleName (',' moduleName)*)? ';'
    | 'opens' packageName ('to' moduleName (',' moduleName)*)? ';'
    | 'uses' typeName ';'
    | 'provides' typeName 'with' typeName (',' typeName)* ';'
    ;

requiresModifier
    : 'transitive'
    | 'static'
    ;


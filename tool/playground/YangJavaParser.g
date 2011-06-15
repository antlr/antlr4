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

/*
 * This file is modified by Yang Jiang (yang.jiang.z@gmail.com), taken from the original
 * java grammar in www.antlr.org, with the goal to provide a standard ANTLR grammar
 * for java, as well as an implementation to construct the same AST trees as javac does.
 *
 * The major changes of this version as compared to the original version include:
 * 1) Top level rules are changed to include all of their sub-components.
 *    For example, the rule
 *
 *      classOrInterfaceDeclaration
 *          :   classOrInterfaceModifiers (classDeclaration | interfaceDeclaration)
 *      ;
 *
 *    is changed to
 *
 *      classOrInterfaceDeclaration
 *          :   classDeclaration | interfaceDeclaration
 *      ;
 *
 *    with classOrInterfaceModifiers been moved inside classDeclaration and
 *    interfaceDeclaration.
 *
 * 2) The original version is not quite clear on certain rules like memberDecl,
 *    where it mixed the styles of listing of top level rules and listing of sub rules.
 *
 *    memberDecl
 *      :   genericMethodOrConstructorDecl
 *      |   memberDeclaration
 *      |   'void' Identifier voidMethodDeclaratorRest
 *      |   Identifier constructorDeclaratorRest
 *      |   interfaceDeclaration
 *      |   classDeclaration
 *      ;
 *
 *    This is changed to a
 *
 *    memberDecl
 *      :   fieldDeclaration
 *      |   methodDeclaration
 *      |   classDeclaration
 *      |   interfaceDeclaration
 *      ;
 *    by folding similar rules into single rule.
 *
 * 3) Some syntactical predicates are added for efficiency, although this is not necessary
 *    for correctness.
 *
 * 4) Lexer part is rewritten completely to construct tokens needed for the parser.
 *
 * 5) This grammar adds more source level support
 *
 *
 * This grammar also adds bug fixes.
 *
 * 1) Adding typeArguments to superSuffix to alHexSignificandlow input like
 *      super.<type>method()
 *
 * 2) Adding typeArguments to innerCreator to allow input like
 *      new Type1<string, integer="">().new Type2<string>()
 *
 * 3) conditionalExpression is changed to
 *    conditionalExpression
 *      :   conditionalOrExpression ( '?' expression ':' conditionalExpression )?
 *      ;
 *    to accept input like
 *      true?1:2=3
 *
 *    Note: note this is by no means a valid input, by the grammar should be able to parse
 *    this as
 *            (true?1:2)=3
 *    rather than
 *            true?1:(2=3)
 *
 *
 *  Know problems:
 *    Won't pass input containing unicode sequence like this
 *      char c = '\uffff'
 *      String s = "\uffff";
 *    Because Antlr does not treat '\uffff' as an valid char. This will be fixed in the next Antlr
 *    release. [Fixed in Antlr-3.1.1]
 *
 *  Things to do:
 *    More effort to make this grammar faster.
 *    Error reporting/recovering.
 *
 *
 *  NOTE: If you try to compile this file from command line and Antlr gives an exception
 *    like error message while compiling, add option
 *    -Xconversiontimeout 100000
 *    to the command line.
 *    If it still doesn't work or the compilation process
 *    takes too long, try to comment out the following two lines:
 *    |    {isValidSurrogateIdentifierStart((char)input.LT(1), (char)input.LT(2))}?=>('\ud800'..'\udbff') ('\udc00'..'\udfff')
 *    |    {isValidSurrogateIdentifierPart((char)input.LT(1), (char)input.LT(2))}?=>('\ud800'..'\udbff') ('\udc00'..'\udfff')
 *
 *
 *  Below are comments found in the original version.
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
 *          statement like  "Class<byte> TYPE = (Class<byte>)...;" because it was seeing
 *          'Class<byte' in="" the="" cast="" expression="" as="" a="" less="" than="" expression,="" then="" failing="" *="" on="" '="">'.
 *      Changed createdName to use typeArguments instead of nonWildcardTypeArguments.
 *
 *      Changed the 'this' alternative in primary to allow 'identifierSuffix' rather than
 *          just 'arguments'.  The case it couldn't handle was a call to an explicit
 *          generic method invocation (e.g. this.<e>doSomething()).  Using identifierSuffix
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
 *  Known remaining problems:
 *      "Letter" and "JavaIDDigit" are wrong.  The actual specification of
 *      "Letter" should be "a character for which the method
 *      Character.isJavaIdentifierStart(int) returns true."  A "Java
 *      letter-or-digit is a character for which the method
 *      Character.isJavaIdentifierPart(int) returns true."
 */


 /*
    This is a merged file, containing two versions of the Java.g grammar.
    To extract a version from the file, run the ver.jar with the command provided below.

    Version 1 - tree building version, with all source level support, error recovery etc.
                This is the version for compiler grammar workspace.
                This version can be extracted by invoking:
                java -cp ver.jar Main 1 true true true true true Java.g

    Version 2 - clean version, with no source leve support, no error recovery, no predicts,
                assumes 1.6 level, works in Antlrworks.
                This is the version for Alex.
                This version can be extracted by invoking:
                java -cp ver.jar Main 2 false false false false false Java.g
*/

parser grammar YangJavaParser;

options {
	tokenVocab = YangJavaLexer;
}

/********************************************************************************************
                          Parser section
*********************************************************************************************/

compilationUnit
    :   (   (annotations
            )?
            packageDeclaration
        )?
        (importDeclaration
        )*
        (typeDeclaration
        )*
    ;

packageDeclaration
    :   'package' qualifiedName
        ';'
    ;

importDeclaration
    :   'import'
        ('static'
        )?
        IDENTIFIER '.' '*'
        ';'
    |   'import'
        ('static'
        )?
        IDENTIFIER
        ('.' IDENTIFIER
        )+
        ('.' '*'
        )?
        ';'
    ;

qualifiedImportName
    :   IDENTIFIER
        ('.' IDENTIFIER
        )*
    ;

typeDeclaration
    :   classOrInterfaceDeclaration
    |   ';'
    ;

classOrInterfaceDeclaration
    :    classDeclaration
    |   interfaceDeclaration
    ;


modifiers
    :
    (    annotation
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
    )*
    ;


variableModifiers
    :   (   'final'
        |   annotation
        )*
    ;


classDeclaration
    :   normalClassDeclaration
    |   enumDeclaration
    ;

normalClassDeclaration
    :   modifiers  'class' IDENTIFIER
        (typeParameters
        )?
        ('extends' type
        )?
        ('implements' typeList
        )?
        classBody
    ;


typeParameters
    :   '<'
            typeParameter
            (',' typeParameter
            )*
        '>'
    ;

typeParameter
    :   IDENTIFIER
        ('extends' typeBound
        )?
    ;


typeBound
    :   type
        ('&' type
        )*
    ;


enumDeclaration
    :   modifiers
        ('enum'
        )
        IDENTIFIER
        ('implements' typeList
        )?
        enumBody
    ;


enumBody
    :   '{'
        (enumConstants
        )?
        ','?
        (enumBodyDeclarations
        )?
        '}'
    ;

enumConstants
    :   enumConstant
        (',' enumConstant
        )*
    ;

/**
 * NOTE: here differs from the javac grammar, missing TypeArguments.
 * EnumeratorDeclaration = AnnotationsOpt [TypeArguments] IDENTIFIER [ Arguments ] [ "{" ClassBody "}" ]
 */
enumConstant
    :   (annotations
        )?
        IDENTIFIER
        (arguments
        )?
        (classBody
        )?
        /* TODO: $GScope::name = names.empty. enum constant body is actually
        an anonymous class, where constructor isn't allowed, have to add this check*/
    ;

enumBodyDeclarations
    :   ';'
        (classBodyDeclaration
        )*
    ;

interfaceDeclaration
    :   normalInterfaceDeclaration
    |   annotationTypeDeclaration
    ;

normalInterfaceDeclaration
    :   modifiers 'interface' IDENTIFIER
        (typeParameters
        )?
        ('extends' typeList
        )?
        interfaceBody
    ;

typeList
    :   type
        (',' type
        )*
    ;

classBody
    :   '{'
        (classBodyDeclaration
        )*
        '}'
    ;

interfaceBody
    :   '{'
        interfaceBodyDeclaration*        
        '}'
    ;

classBodyDeclaration
    :   ';'
    |   ('static'
        )?
        block
    |   memberDecl
    ;

memberDecl
    :    fieldDeclaration
    |    methodDeclaration
    |    classDeclaration
    |    interfaceDeclaration
    ;


methodDeclaration
    :
        /* For constructor, return type is null, name is 'init' */
         modifiers
        (typeParameters
        )?
        IDENTIFIER
        formalParameters
        ('throws' qualifiedNameList
        )?
        '{'
        (explicitConstructorInvocation
        )?
        (blockStatement
        )*
        '}'
    |   modifiers
        (typeParameters
        )?
        (type
        |   'void'
        )
        IDENTIFIER
        formalParameters
        ('[' ']'
        )*
        ('throws' qualifiedNameList
        )?
        (
            block
        |   ';'
        )
    ;


fieldDeclaration
    :   modifiers
        type
        variableDeclarator
        (',' variableDeclarator
        )*
        ';'
    ;

variableDeclarator
    :   IDENTIFIER
        ('[' ']'
        )*
        ('=' variableInitializer
        )?
    ;

/**
 *TODO: add predicates
 */
interfaceBodyDeclaration
    :
        interfaceFieldDeclaration
    |   interfaceMethodDeclaration
    |   interfaceDeclaration
    |   classDeclaration
    |   ';'
    ;

interfaceMethodDeclaration
    :   modifiers
        (typeParameters
        )?
        (type
        |'void'
        )
        IDENTIFIER
        formalParameters
        ('[' ']'
        )*
        ('throws' qualifiedNameList
        )? ';'
    ;

/**
 * NOTE, should not use variableDeclarator here, as it doesn't necessary require
 * an initializer, while an interface field does, or judge by the returned value.
 * But this gives better diagnostic message, or antlr won't predict this rule.
 */
interfaceFieldDeclaration
    :   modifiers type variableDeclarator
        (',' variableDeclarator
        )*
        ';'
    ;


type
    :   classOrInterfaceType
        ('[' ']'
        )*
    |   primitiveType
        ('[' ']'
        )*
    ;


classOrInterfaceType
    :   IDENTIFIER
        (typeArguments
        )?
        ('.' IDENTIFIER
            (typeArguments
            )?
        )*
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

typeArguments
    :   '<' typeArgument
        (',' typeArgument
        )*
        '>'
    ;

typeArgument
    :   type
    |   '?'
        (
            ('extends'
            |'super'
            )
            type
        )?
    ;

qualifiedNameList
    :   qualifiedName
        (',' qualifiedName
        )*
    ;

formalParameters
    :   '('
        (formalParameterDecls
        )?
        ')'
    ;

formalParameterDecls
    :   ellipsisParameterDecl
    |   normalParameterDecl
        (',' normalParameterDecl
        )*
    |   (normalParameterDecl
        ','
        )+
        ellipsisParameterDecl
    ;

normalParameterDecl
    :   variableModifiers type IDENTIFIER
        ('[' ']'
        )*
    ;

ellipsisParameterDecl
    :   variableModifiers
        type  '...'
        IDENTIFIER
    ;


explicitConstructorInvocation
    :   (nonWildcardTypeArguments
        )?     //NOTE: the position of Identifier 'super' is set to the type args position here
        ('this'
        |'super'
        )
        arguments ';'

    |   primary
        '.'
        (nonWildcardTypeArguments
        )?
        'super'
        arguments ';'
    ;

qualifiedName
    :   IDENTIFIER
        ('.' IDENTIFIER
        )*
    ;

annotations
    :   (annotation
        )+
    ;

/**
 *  Using an annotation.
 * '@' is flaged in modifier
 */
annotation
    :   '@' qualifiedName
        (   '('
                  (   elementValuePairs
                  |   elementValue
                  )?
            ')'
        )?
    ;

elementValuePairs
    :   elementValuePair
        (',' elementValuePair
        )*
    ;

elementValuePair
    :   IDENTIFIER '=' elementValue
    ;

elementValue
    :   conditionalExpression
    |   annotation
    |   elementValueArrayInitializer
    ;

elementValueArrayInitializer
    :   '{'
        (elementValue
            (',' elementValue
            )*
        )? (',')? '}'
    ;


/**
 * Annotation declaration.
 */
annotationTypeDeclaration
    :   modifiers '@'
        'interface'
        IDENTIFIER
        annotationTypeBody
    ;


annotationTypeBody
    :   '{'
        (annotationTypeElementDeclaration
        )*
        '}'
    ;

/**
 * NOTE: here use interfaceFieldDeclaration for field declared inside annotation. they are sytactically the same.
 */
annotationTypeElementDeclaration
    :   annotationMethodDeclaration
    |   interfaceFieldDeclaration
    |   normalClassDeclaration
    |   normalInterfaceDeclaration
    |   enumDeclaration
    |   annotationTypeDeclaration
    |   ';'
    ;

annotationMethodDeclaration
    :   modifiers type IDENTIFIER
        '(' ')' ('default' elementValue
                )?
        ';'
        ;

block
    :   '{'
        (blockStatement
        )*
        '}'
    ;

/*
staticBlock returns [JCBlock tree]
        @init {
            ListBuffer<jcstatement> stats = new ListBuffer<jcstatement>();
            int pos = ((AntlrJavacToken) $start).getStartIndex();
        }
        @after {
            $tree = T.at(pos).Block(Flags.STATIC, stats.toList());
            pu.storeEnd($tree, $stop);
            // construct a dummy static modifiers for end position
            pu.storeEnd(T.at(pos).Modifiers(Flags.STATIC,  com.sun.tools.javac.util.List.<jcannotation>nil()),$st);
        }
    :   st_1='static' '{'
        (blockStatement
            {
                if ($blockStatement.tree == null) {
                    stats.appendList($blockStatement.list);
                } else {
                    stats.append($blockStatement.tree);
                }
            }
        )* '}'
    ;
*/
blockStatement
    :   localVariableDeclarationStatement
    |   classOrInterfaceDeclaration
    |   statement
    ;


localVariableDeclarationStatement
    :   localVariableDeclaration
        ';'
    ;

localVariableDeclaration
    :   variableModifiers type
        variableDeclarator
        (',' variableDeclarator
        )*
    ;

statement
    :   block

    |   ('assert'
        )
        expression (':' expression)? ';'
    |   'assert'  expression (':' expression)? ';'
    |   'if' parExpression statement ('else' statement)?
    |   forstatement
    |   'while' parExpression statement
    |   'do' statement 'while' parExpression ';'
    |   trystatement
    |   'switch' parExpression '{' switchBlockStatementGroups '}'
    |   'synchronized' parExpression block
    |   'return' (expression )? ';'
    |   'throw' expression ';'
    |   'break'
            (IDENTIFIER
            )? ';'
    |   'continue'
            (IDENTIFIER
            )? ';'
    |   expression  ';'
    |   IDENTIFIER ':' statement
    |   ';'

    ;

switchBlockStatementGroups
    :   (switchBlockStatementGroup )*
    ;

switchBlockStatementGroup
    :
        switchLabel
        (blockStatement
        )*
    ;

switchLabel
    :   'case' expression ':'
    |   'default' ':'
    ;


trystatement
    :   'try' block
        (   catches 'finally' block
        |   catches
        |   'finally' block
        )
     ;

catches
    :   catchClause
        (catchClause
        )*
    ;

catchClause
    :   'catch' '(' formalParameter
        ')' block
    ;

formalParameter
    :   variableModifiers type IDENTIFIER
        ('[' ']'
        )*
    ;

forstatement
    :
        // enhanced for loop
        'for' '(' variableModifiers type IDENTIFIER ':'
        expression ')' statement

        // normal for loop
    |   'for' '('
                (forInit
                )? ';'
                (expression
                )? ';'
                (expressionList
                )? ')' statement
    ;

forInit
    :   localVariableDeclaration
    |   expressionList
    ;

parExpression
    :   '(' expression ')'
    ;

expressionList
    :   expression
        (',' expression
        )*
    ;


expression
    :   conditionalExpression
        (assignmentOperator expression
        )?
    ;


assignmentOperator
    :   '='
    |   '+='
    |   '-='
    |   '*='
    |   '/='
    |   '&='
    |   '|='
    |   '^='
    |   '%='
    |    '<' '<' '='
    |    '>' '>' '>' '='
    |    '>' '>' '='
    ;


conditionalExpression
    :   conditionalOrExpression
        ('?' expression ':' conditionalExpression
        )?
    ;

conditionalOrExpression
    :   conditionalAndExpression
        ('||' conditionalAndExpression
        )*
    ;

conditionalAndExpression
    :   inclusiveOrExpression
        ('&&' inclusiveOrExpression
        )*
    ;

inclusiveOrExpression
    :   exclusiveOrExpression
        ('|' exclusiveOrExpression
        )*
    ;

exclusiveOrExpression
    :   andExpression
        ('^' andExpression
        )*
    ;

andExpression
    :   equalityExpression
        ('&' equalityExpression
        )*
    ;

equalityExpression
    :   instanceOfExpression
        (
            (   '=='
            |   '!='
            )
            instanceOfExpression
        )*
    ;

instanceOfExpression
    :   relationalExpression
        ('instanceof' type
        )?
    ;

relationalExpression
    :   shiftExpression
        (relationalOp shiftExpression
        )*
    ;

relationalOp
    :    '<' '='
    |    '>' '='
    |   '<'
    |   '>'
    ;

shiftExpression
    :   additiveExpression
        (shiftOp additiveExpression
        )*
    ;


shiftOp
    :    '<' '<'
    |    '>' '>' '>'
    |    '>' '>'
    ;


additiveExpression
    :   multiplicativeExpression
        (
            (   '+'
            |   '-'
            )
            multiplicativeExpression
         )*
    ;

multiplicativeExpression
    :
        unaryExpression
        (
            (   '*'
            |   '/'
            |   '%'
            )
            unaryExpression
        )*
    ;

/**
 * NOTE: for '+' and '-', if the next token is int or long interal, then it's not a unary expression.
 *       it's a literal with signed value. INTLTERAL AND LONG LITERAL are added here for this.
 */
unaryExpression
    :   '+'  unaryExpression
    |   '-' unaryExpression
    |   '++' unaryExpression
    |   '--' unaryExpression
    |   unaryExpressionNotPlusMinus
    ;

unaryExpressionNotPlusMinus
    :   '~' unaryExpression
    |   '!' unaryExpression
    |   castExpression
    |   primary
        (selector
        )*
        (   '++'
        |   '--'
        )?
    ;

castExpression
    :   '(' primitiveType ')' unaryExpression
    |   '(' type ')' unaryExpressionNotPlusMinus
    ;

/**
 * have to use scope here, parameter passing isn't well supported in antlr.
 */
primary
    :   parExpression
    |   'this'
        ('.' IDENTIFIER
        )*
        (identifierSuffix
        )?
    |   IDENTIFIER
        ('.' IDENTIFIER
        )*
        (identifierSuffix
        )?
    |   'super'
        superSuffix
    |   literal
    |   creator
    |   primitiveType
        ('[' ']'
        )*
        '.' 'class'
    |   'void' '.' 'class'
    ;


superSuffix
    :   arguments
    |   '.' (typeArguments
        )?
        IDENTIFIER
        (arguments
        )?
    ;


identifierSuffix
    :   ('[' ']'
        )+
        '.' 'class'
    |   ('[' expression ']'
        )+
    |   arguments
    |   '.' 'class'
    |   '.' nonWildcardTypeArguments IDENTIFIER arguments
    |   '.' 'this'
    |   '.' 'super' arguments
    |   innerCreator
    ;


selector
    :   '.' IDENTIFIER
        (arguments
        )?
    |   '.' 'this'
    |   '.' 'super'
        superSuffix
    |   innerCreator
    |   '[' expression ']'
    ;

creator
    :   'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest
    |   'new' classOrInterfaceType classCreatorRest
    |   arrayCreator
    ;

arrayCreator
    :   'new' createdName
        '[' ']'
        ('[' ']'
        )*
        arrayInitializer

    |   'new' createdName
        '[' expression
        ']'
        (   '[' expression
            ']'
        )*
        ('[' ']'
        )*
    ;

variableInitializer
    :   arrayInitializer
    |   expression
    ;

arrayInitializer
    :   '{'
            (variableInitializer
                (',' variableInitializer
                )*
            )?
            (',')?
        '}'             //Yang's fix, position change.
    ;


createdName
    :   classOrInterfaceType
    |   primitiveType
    ;

innerCreator
    :   '.' 'new'
        (nonWildcardTypeArguments
        )?
        IDENTIFIER
        (typeArguments
        )?
        classCreatorRest
    ;


classCreatorRest
    :   arguments
        (classBody
        )?
    ;


nonWildcardTypeArguments
    :   '<' typeList
        '>'
    ;

arguments
    :   '(' (expressionList
        )? ')'
    ;

literal
    :   INTLITERAL
    |   LONGLITERAL
    |   FLOATLITERAL
    |   DOUBLELITERAL
    |   CHARLITERAL
    |   STRINGLITERAL
    |   TRUE
    |   FALSE
    |   NULL
    ;


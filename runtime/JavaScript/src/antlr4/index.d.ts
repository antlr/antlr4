// index.d.ts - Hand-crafted typescript declarations of antlr4 runtime for JavaScript
//
//[The "BSD license"]
// Copyright (c) 2016 Burt Harris
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
//
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
// 3. The name of the author may not be used to endorse or promote products
//    derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
// IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
// OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
// IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
// INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
// NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
// THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// This file includes TypeScript style declarations of the interfaces 
// of the ANTLR4 JavaScript runtime.   This enables tools to provide more help 
// by reasoning about the types object despite the fact that at runtime, JavaScript
// is dynamicly typed.   
//
// The /*readonly*/ comments below can be uncommented when working with TypeScript 2.0 tool chain.
// At this time TypeScript 2.0 is still in Beta pre-relase.

export interface IntStream {
    /*readonly*/ index : number;
    /*readonly*/ size : number;

    mark() : number ;
    release( marker : number );
    seek( index : number );
}

export interface CharStream extends IntStream {
    getText(interval : Interval) : string ;
}

export class InputStream implements CharStream {
    constructor( text: string );
    /*readonly*/ index : number;
    /*readonly*/ size : number;

    mark() : number ;
    release( marker : number );
    seek( index : number );
    getText(interval : Interval) : string ;
}

export interface TokenStream extends IntStream {
    LT( number ) : Token ;
    get( number ) : Token ;
    getTokenSource() : TokenSource ;
    getText(spec: Interval | RuleContext) : string;
    getText(start: Token, stop:Token ) : string;
}

interface Interval {}
interface RuleContext {}

interface Vocabulary {}
interface ATN {}

export class Recognizer {
    constructor()

    // getVocabulary() : Vocabulary ;
    getTokenTypeMap() : Map< string, number >;
    getRuleIndexMap() : Map< string, number >;
    getTokenType( tokenName: string ) : number ;
    getSerializedATN() : string ;
    getGrammarFileName() : string ;
    getATN() : ATN
    getErrorHeader(exception) : string;
    getTokenErrorDisplay(t:Token) : string;

    addErrorListener( listener ) : void; 
    removeErrorListeners() : void ;

    sempred( localctx, ruleIndex, actionIndex) : boolean;
    precpred( localctx , precedence ) : boolean;
    state : number;

} 

export module Tree {

    export interface Tree {
	    // getParent() : Tree ;
    	getPayload() : any;
        //getChild(i : number) : Tree ;
    	getChildCount() : number ;
	    // toStringTree() : string;
    }


    export interface SyntaxTree extends Tree {
        getSourceInterval() : Interval ;
    }

    export interface ParseTree extends SyntaxTree { 
        getParent() : ParseTree ; 
        getChild(i : number) : ParseTree;
        accept( visitor );
        getText() : string 
        toStringTree( parser: Parser );
    }

    export interface TerminalNode extends ParseTree {

    }

    export interface ErrorNode extends TerminalNode {

    }

    export interface RuleNode extends ParseTree {
        getRuleContext(): RuleContext
    }

    export class ParseTreeVisitor<T> {
        visit( tree : ParseTree ) : T ;
        visitChildren( node: RuleNode ) : T;
        visitTerminal( node: TerminalNode ) : T;
        visitErrorNode( node : ErrorNode ) : T;
    }

    export class ParseTreeListener { 
 	    visitTerminal(node : TerminalNode ); 
        visitErrorNode(node : ErrorNode ); 
        enterEveryRule(ctx : ParserRuleContext); 
        exitEveryRule(ctx : ParserRuleContext); 

    }

    export class  ParseTreeWalker { 
        walk( listener: ParseTreeListener, tree : ParseTree );
        protected enterRule( listener: ParseTreeListener, r : RuleNode );
        protected exitRule(  listener: ParseTreeListener, r : RuleNode );
    }

}

interface RuleContext extends Tree.RuleNode {
    parent: RuleContext;
    invokingState : number;
    depth() : number; 
    isEmpty() : boolean;
    getSourceInterval() : Interval;
}

interface ParserRuleContext extends RuleContext {

}

export class Lexer extends Recognizer {
    constructor( input : InputStream );
    reset() : void ; 
    nextToken() : Token ;
    skip() : void ;
    more() : void ;
    pushMode() : void ;
    popMode() : number ;
    inputStream : InputStream;
    symbolicNames : string[];
}

export class BufferedTokenStream implements TokenStream {
    constructor( tokenSource: TokenSource );
    
    /*readonly*/ index : number;
    /*readonly*/ size : number;

    mark() : number ;
    release( marker : number );
    seek( index : number );
    getText(interval : Interval) : string ;
    LT( number ) : Token ;
    get( number ) : Token ;
    getTokenSource() : TokenSource ;
    getText(spec: Interval | RuleContext) : string;
    getText(start: Token, stop:Token ) : string;   
}

export class CommonTokenStream extends BufferedTokenStream {
    constructor( lexer: Lexer );
    /*readonly*/ index : number;
    /*readonly*/ size : number;

    mark() : number ;
    release( marker : number );
    seek( index : number );
    getText(interval : Interval) : string ;
    LT( number ) : Token ;
    get( number ) : Token ;
    getTokenSource() : TokenSource ;
    getText(spec: Interval | RuleContext) : string;
    getText(start: Token, stop:Token ) : string;
    fetch( count: number ) : number;
    getTokens( start? : number, stop? : number, types? : number[]) : Token[];
}

export class Parser {
    constructor( tokens : TokenStream )
}

export interface TokenSource {
    nextToken() : Token ;
    getLine() : number ;
    getCharPositionInLine() : number ;
    getInputStream : CharStream ;
    getSourceName : string ;
}

export enum TokenType {
    EPSILON = -2,
    EOF = -1,
    INVALID_TYPE = 0,
    MIN_USER_TOKEN_TYPE = 1,
}

export enum Channel {
    // All tokens go to the parser (unless skip() is called in that rule)
    // on a particular "channel". The parser tunes to a particular channel
    // so that whitespace etc... can go to the parser on a "hidden" channel.

    DEFAULT_CHANNEL = 0,

    // Anything on different channel than DEFAULT_CHANNEL is not parsed
    // by parser.

    HIDDEN_CHANNEL = 1,
}

// Tokens and related

export interface Token {
	/*readonly*/ type       : TokenType ;
                 text       : string ;
	/*readonly*/ channel    : Channel ; // The parser ignores everything not on DEFAULT_CHANNEL
	/*readonly*/ start      : number ; // optional; return -1 if not implemented.
	/*readonly*/ stop       : number ; // optional; return -1 if not implemented.
	/*readonly*/ tokenIndex : number ; // from 0..n-1 of the token object in the input stream
	/*readonly*/ line       : number ; // line=1..n of the 1st character
	/*readonly*/ column     : number ; // beginning of the line at which it occurs, 0..n-1
                 toString() : string ;
                 getInputStream(): CharStream;
                 getTokenSource() : TokenSource;

}
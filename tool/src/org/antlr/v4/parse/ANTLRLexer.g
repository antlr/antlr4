// File      : A3Lexer.g
// Author    : Jim Idle (jimi@temporal-wave.com)
// Copyright : Free BSD - See @header clause below
// Version   : First implemented as part of ANTLR 3.2 this is the self
//             hosting ANTLR 3 Lexer.
//
// Description
// -----------
// This is the definitive lexer grammar for parsing ANTLR V3.x.x grammars. All other
// gramnmars are derived from this grammar via source code control integration (perforce)
// or by the gdiff tool.
//
// This grammar and its associated grmmmars A3Parser.g and A3Walker.g exhibit the following
// traits, which are recommended for all production quality grammars:
//
// 1) They are separate grammars, not composite grammars;
// 2) They implement all supporting methods in a superclass (at least this is recommended
//    for language targets that support inheritence;
// 3) All errors are pushed as far down the parsing chain as possible, which means
//    that the lexer tries to defer error reporting to the parser, and the parser
//    tries to defer error reporting to a semantic phase consisting of a single
//    walk of the AST. The reason for this is that the error messages produced 
//    from later phases of the parse will generally have better context and so
//    be more useful to the end user. Consider the message: "Syntax error at 'options'"
//    vs: "You cannot specify two options{} sections in a single grammar file".
// 4) The lexer is 'programmed' to catch common mistakes such as unterminated literals
//    and report them specifically and not just issue confusing lexer mismatch errors.
//

/** Read in an ANTLR grammar and build an AST.  Try not to do
 *  any actions, just build the tree.
 *
 *  The phases are:
 *
 *		A3Lexer.g (this file)
 *              A3Parser.g
 *              A3Verify.g (derived from A3Walker.g)
 *		assign.types.g
 *		define.g
 *		buildnfa.g
 *		antlr.print.g (optional)
 *		codegen.g
 *
 *  Terence Parr
 *  University of San Francisco
 *  2005
 *  Jim Idle (this v3 grammar)
 *  Temporal Wave LLC
 *  2009
 */
lexer grammar ANTLRLexer;

// ==============================================================================
// Note that while this grammar does not care about order of constructs
// that don't really matter, such as options before @header etc, it must first
// be parsed by the original v2 parser, before it replaces it. That parser does 
// care about order of structures. Hence we are constrained by the v2 parser
// for at least the first bootstrap release that causes this parser to replace
// the v2 version.
// ==============================================================================

// -------
// Options
//
// V3 option directives to tell the tool what we are asking of it for this
// grammar.
//
options {

	// Target language is Java, which is the default but being specific
	// here as this grammar is also meant as a good example grammar for
	// for users.
	//
	language      = Java;
	
	// The super class that this lexer should expect to inherit from, and
	// which contains any and all support routines for the lexer. This is
	// commented out in this baseline (definitive or normative grammar)
	// - see the ANTLR tool implementation for hints on how to use the super
	//   class
	//
	//superclass    = AbstractA3Lexer;
}

tokens { SEMPRED; FORCED_ACTION; }

// Include the copyright in this source and also the generated source
//
@lexer::header {
/*
 [The "BSD licence"]
 Copyright (c) 2005-2009 Terence Parr
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
package org.antlr.v4.parse;
}

// +=====================+
// | Lexer specification |
// +=====================+

// --------
// Comments
//
// ANTLR comments can be multi or single line and we don't care
// which particularly. However we also accept Javadoc style comments
// of the form: /** ... */ and we do take care to distinguish those
// from ordinary multi-line comments
// Note how we guide the lexical PATH because we want to issue a decriptive
// error message in case of a standalone '/' character, which makes no
// sense in ANTLR source code. We alo trap unterminated multi-line comments
//
fragment DOC_COMMENT : ;
COMMENT
@init {

	// Record the start line and offsets as if we need to report an
	// unterminated comment, then we want to show the start of the comment
	// we think is broken, not the end, where people will have to try and work
	// it out themselves. 
	//
	int startLine = $line;
	int offset    = getCharPositionInLine();
}
    : // Eat the first character only, then see if we have a comment
      // or something silly.
      //
      '/'  // Comment introducer
            
      (
          // Single line comment, possibly with embedded src/line directives
          // in a similar style to the C pre-processor, allowing generated
          // code to refer the programmer back to the original source code
          // in case of error.
          //
          '/'
            (
                (' $ANTLR')=> ' $ANTLR' SRC
              | ~(NLCHARS)* 
            )
        
         | // Multi-line comment, which may be a documentation comment
           // if it starts /** (note that we protect against accidentaly
           // recognizing a comment /**/ as a documentation comment
           //
           '*' (
           			  { input.LA(2) != '/'}?=> '*' { $type = DOC_COMMENT; }
           			| { true }?=>  // Required to cover all alts with predicates
           	   )

               // Should we support embedded multiline comments here?
               //           
               (
                   // Pick out end of multiline comment and exit the loop
                   // if we find it.
                   //
               	   {    !(input.LA(1) == '*' && input.LA(2) == '/') }?
                    
              	      // Anything else other than the non-greedy match of
              	      // the comment close sequence
              	      //
              	      .                 
               )*
            (
                 // Look for the comment terminator, but if it is accidentally
                 // unterminated, then we will hit EOF, which will trigger the
                 // epsilon alt and hence we can issue an error message relative
                 // to the start of the unterminated multi-line comment
                 //
                 '*/'
         
               | // Unterminated comment!
                 //
                 {
                   // ErrorManager.msg(Msg.UNTERMINATED_DOC_COMMENT, startLine, offset, $pos, startLine, offset, $pos, (Object)null);
                 }
             )
             
         | // There was nothing that made sense following the opening '/' and so
           // we issue an error regarding the malformed comment
           //
           {
           	 // TODO: Insert error message relative to comment start
             //
           }
       )
       {
         // Unless we had a documentation comment, then we do not wish to
         // pass the comments in to the parser. If you are writing a formatter
         // then you will want to preserve the comments off channel, but could
         // just skip and save token space if not.
         //
         if ($type != DOC_COMMENT) {
          
             $channel=2;  // Comments are on channel 2
         }
       }
    ;


DOUBLE_QUOTE_STRING_LITERAL
	:	'"' (('\\')=>'\\' . | ~'"' )* '"'
	;

DOUBLE_ANGLE_STRING_LITERAL
	:	'<<' (options {greedy=false;} : . )* '>>'
	;
	
// --------------
// Argument specs
//
// Certain argument lists, such as those specifying call parameters
// to a rule invocation, or input parameters to a rule specification
// are contained within square brackets. In the lexer we consume them
// all at once and sort them out later in the grammar analysis.
//
ARG_ACTION
@init
{
	StringBuffer theText = new StringBuffer();
}
	: '[' 
         (
             ('\\')=>'\\'
                 (
                     (']')=>']' 
                       {
                           // We do not include the \ character itself when picking up an escaped ] 
                           //
                           theText.append(']'); 
                       }
                   | c=.   
                       {
                           // We DO include the \ character when finding any other escape
                           //
                           theText.append('\\');
                           theText.append((char)$c);
                       }
                 )

           | ('"')=>as=ACTION_STRING_LITERAL
                {
                    // Append the embedded string literal test
                    //
                    theText.append($as.text);
                }
           
           | ('\'')=>ac=ACTION_CHAR_LITERAL
                {
                    // Append the embedded chracter literal text
                    //
                    theText.append($ac.text);
                } 
                
           | c=~']'
                {
                    // Whatever else we found in the scan
                    //
                    theText.append((char)$c);
                }
	     )*
	     
       ']'
       {
           // Set the token text to our gathered string
           //
           setText(theText.toString());
       }
	;
	
// -------
// Actions
//
// Other than making sure to distinguish between { and } embedded
// within what we have assumed to be literals in the action code, the
// job of the lexer is merely to gather the code within the action
// (delimited by {}) and pass it to the parser as a single token.
// Note the special case of the {{ }} action, which is a forced
// action, that the generated code will execute regardless of
// backtracking (predicate) level.
// We know that this token will be asked for its text somewhere
// in the upcoming parse, so setting the text here to exclude
// the delimiting {} is no additional overhead.
//
ACTION
	:	NESTED_ACTION ('?' {$type = SEMPRED;} )?
		{
            // Note that because of the sempred detection above, we
            // will not see {{ action }}? as a forced action, but as a semantic
            // predicate.
            if ( $text.startsWith("{{") && $text.endsWith("}}") ) {            
                // Switch types to a forced action
                $type = FORCED_ACTION;
            }          
		}
	;

// ----------------
// Action structure
//
// Many language targets use {} as block delimiters and so we
// must recursively match {} delimited blocks to balance the
// braces. Additionally, we must make some assumptions about
// literal string representation in the target language. We assume
// that they are delimited by ' or " and so consume these
// in their own alts so as not to inadvertantly match {}.
// This rule calls itself on matching a {
//
fragment
NESTED_ACTION
@init {

	// Record the start line and offsets as if we need to report an
	// unterminated block, then we want to show the start of the comment
	// we think is broken, not the end, where people will have to try and work
	// it out themselves. 
	//
	int startLine = getLine();
	int offset    = getCharPositionInLine();
}

    : // Action and other blocks start with opening {
      //
	  '{'
      (
	    // And now we can match one of a number of embedded
	    // elements within the action until we find a 
	    // } that balances the opening {. If we do not find
	    // the balanced } then we will hit EOF and can issue
	    // an error message about the brace that we belive to
	    // be mismatched. This won't be foolproof but we will
	    // be able to at least report an error against the
	    // opening brace that we feel is in error and this will
	    // guide the user to the correction as best we can.
	    //
         
         
          // An embedded {} block
	      //
	      NESTED_ACTION
	
        | // What appears to be a literal
          //
          ACTION_CHAR_LITERAL
          
        | // We have assumed that the target language has C/Java
          // type comments.
          //
          COMMENT
          
        | // What appears to be a literal
          //
          ACTION_STRING_LITERAL
	
	    | // What appears to be an escape sequence
	      // 
	      ACTION_ESC
	
	    | // Some other single character that is not
	      // handled above
	      //
	      ~('\\'|'"'|'\''|'/'|'{'|'}')
	      
      )*
     
	(
	    // Correctly balanced closing brace
	    //
	    '}'
	    
	  | // Looks like have an imblanced {} block, report
	    // with respect to the opening brace.
	    //
	    {
	        // TODO: Report imbalanced {}
	        System.out.println("Block starting  at line " + startLine + " offset " + (offset+1) + " contains imbalanced {} or is missing a }");
	    }
	)
   ;
	
    
// Keywords
// --------
// keywords used to specify ANTLR v3 grammars. Keywords may not be used as
// labels for rules or in any other context where they woudl be ambiguous
// with the keyword vs some other identifier
// OPTIONS and TOKENS must also consume the opening brace that captures
// their option block, as this is teh easiest way to parse it separate
// to an ACTION block, despite it usingthe same {} delimiters.
//
OPTIONS      : 'options' WSNLCHARS* '{'  ;
TOKENS       : 'tokens'  WSNLCHARS* '{'  ;

SCOPE        : 'scope'                ;
IMPORT       : 'import'               ;
FRAGMENT     : 'fragment'             ;
LEXER        : 'lexer'                ;
PARSER       : 'parser'               ;
TREE         : 'tree'                 ;
GRAMMAR      : 'grammar'              ;
PROTECTED    : 'protected'            ;
PUBLIC       : 'public'               ;
PRIVATE      : 'private'              ;
RETURNS      : 'returns'              ;
THROWS       : 'throws'               ;
CATCH        : 'catch'                ;
FINALLY      : 'finally'              ;
TEMPLATE     : 'template'             ;
MODE         : 'mode'                 ;

// -----------
// Punctuation
//
// Character sequences used as separators, delimters, operators, etc
//
COLON        : ':'                    ;
COLONCOLON   : '::'                   ;
COMMA        : ','                    ;
SEMI         : ';'                    ;
LPAREN       : '('                    ;
RPAREN       : ')'                    ;
IMPLIES      : '=>'                   ;
LT           : '<'                    ;
GT           : '>'                    ;
ASSIGN       : '='                    ;
QUESTION     : '?'                    ;
BANG         : '!'                    ;
STAR         : '*'                    ;
PLUS         : '+'                    ;
PLUS_ASSIGN  : '+='                   ;
OR           : '|'                    ;
ROOT         : '^'                    ;
DOLLAR       : '$'                    ;
DOT		     : '.'                    ; // can be WILDCARD or DOT in qid or imported rule ref
RANGE        : '..'                   ;
ETC          : '...'                  ;
RARROW       : '->'                   ;
TREE_BEGIN   : '^('                   ;
AT           : '@'                    ;
NOT          : '~'                    ;
RBRACE       : '}'                    ;

// ---------------
// Token reference
//
// The names of all tokens must start with an upper case letter and so
// the lexer can distinguish them directly.
//
TOKEN_REF
    : ('A'..'Z') ('A'..'Z' | 'a'..'z' | '0'..'9' | '_')*
    ;

// --------------
// Rule reference
//
// The names of all rules must start with a lower case letter
// so the lexer can distibguish them directly. The parser takes
// care of the case such as id=rulename
//
RULE_REF
    : ('a'..'z') ('A'..'Z' | 'a'..'z' | '0'..'9' | '_')*
    ;
    
	 	
// ----------------------------
// Literals embedded in actions
//
// Note that we have made the assumption that the language used within
// actions uses the fairly standard " and ' delimiters for literals and
// that within these literals, characters are escaped using the \ character.
// There are some languages which do not conform to this in all cases, such 
// as by using /string/ and so on. We will have to deal with such cases if
// if they come up in targets.
//

// Within actions, or other structures that are not part of the ANTLR
// syntax, we may encounter literal characters. Within these, we do
// not want to inadvertantly match things like '}' and so we eat them
// specifically. While this rule is called CHAR it allows for the fact that
// some languages may use/allow ' as the string delimiter.
//
fragment
ACTION_CHAR_LITERAL
	:	'\'' (('\\')=>ACTION_ESC | ~'\'' )* '\''
	;

// Within actions, or other structures that are not part of the ANTLR
// syntax, we may encounter literal strings. Within these, we do
// not want to inadvertantly match things like '}' and so we eat them
// specifically.
//
fragment
ACTION_STRING_LITERAL
	:	'"' (('\\')=>ACTION_ESC | ~'"')* '"'
	;

// Within literal strings and characters that are not part of the ANTLR
// syntax, we must allow for escaped character sequences so that we do not
// inadvertantly recognize the end of a string or character when the terminating
// delimiter has been esacped.
//
fragment
ACTION_ESC
	: '\\' .
	;

// -------
// Integer
//
// Obviously (I hope) match an aribtrary long sequence of digits.
//
INT : ('0'..'9')+
    ;

// -----------    
// Source spec
//
// A fragment rule for picking up information about an origrinating
// file from which the grammar we are parsing has been generated. This allows
// ANTLR to report errors against the originating file and not the generated
// file.
//
fragment
SRC : 'src' WSCHARS+ file=ACTION_STRING_LITERAL WSCHARS+ line=INT
      {
         // TODO: Add target specific code to change the source file name and current line number
         //     
      } 
    ;

// --------------
// Literal string
//
// ANTLR makes no disticintion between a single character literal and a
// multi-character string. All literals are single quote delimited and
// may contain unicode escape sequences of the form \uxxxx, where x
// is a valid hexadecimal number (as per Java basically).
STRING_LITERAL
@init {
   int len = 0;
}
    :  '\'' ( ( ESC_SEQ | ~('\\'|'\'') ) {len++;} )* '\''
    ;

// A valid hex digit specification
//
fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

// Any kind of escaped character that we can embed within ANTLR
// literal strings.
//
fragment
ESC_SEQ
    : '\\' 
        (
              // The standard escaped character set such as tab, newline,
              // etc.
              //
    		  'b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\'
    		  
    	    | // A Java style Unicode escape sequence
    	      //
    	      UNICODE_ESC
    	      
    	    | // An illegal escape seqeunce
    	      //
    	      {
    	      	// TODO: Issue error message
    	      	//
    	      }
        )
    ;

fragment
UNICODE_ESC

@init {

	// Flag to tell us whether we have a valid number of
	// hex digits in the escape sequence
	//
	int	hCount = 0;
}
    :   'u' // Leadin for unicode escape sequence
    
        // We now require 4 hex digits. Note though
        // that we accept any number of characters
        // and issue an error if we do not get 4. We cannot
        // use an inifinite count such as + because this
        // might consume too many, so we lay out the lexical
        // options and issue an error at the invalid paths.
        // 
    	(
    	   ( 
    	      HEX_DIGIT  { hCount++; }
    	        (
    	             HEX_DIGIT  { hCount++; }
    		         (
    		              HEX_DIGIT  { hCount++; }
    		              (   
    		                  // Four valid hex digits, we are good
    		                  //
    		                  HEX_DIGIT { hCount++; }
    		                  
    		                | // Three valid digits
    		              )
    		              
    		            | // Two valid digits
    		          )
    		          
    		        | // One valid digit
    		    )
            )
          | // No valid hex digits at all
    	)
    	
    	// Now check the digit count and issue an error if we need to
    	//
    	{
    		if	(hCount != 4) {
    		
    			// TODO: Issue error message
    		}
    	}
    ;

// ----------
// Whitespace
//
// Characters and character constructs that are of no import
// to the parser and are used to make the grammar easier to read
// for humans.
//
WS  
    : ( 
    	  ' '
        | '\t'
        | '\r'
        | '\n'
        | '\f'
      )+ 
      {
      
	$channel=2;
      }
    ;

// A fragment rule for use in recognizing end of line in 
// rules like COMMENT.
// 
fragment
NLCHARS
    : '\n' | '\r'
    ;
  
// A fragment rule for recognizing traditional whitespace
// characters within lexer rules.
//  
fragment
WSCHARS
    : ' ' | '\t' | '\f'
    ;

// A fragment rule for recognizing both traditional whitespace and
// end of line markers, when we don't care to distinguish but don't
// want any action code going on.
// 
fragment
WSNLCHARS
    : ' ' | '\t' | '\f' | '\n' | '\r'
    ;
      
// -----------------  
// Illegal Character
//
// This is an illegal character trap which is always the last rule in the
// lexer specification. It matches a single character of any value and being
// the last rule in the file will match when no other rule knows what to do
// about the character. It is reported as an error but is not passed on to the
// parser. This means that the parser to deal with the gramamr file anyway
// but we will not try to analyse or code generate from a file with lexical 
// errors.
//
ERRCHAR
    : .
      {
         // TODO: Issue error message
         //
         skip();
      }
    ;

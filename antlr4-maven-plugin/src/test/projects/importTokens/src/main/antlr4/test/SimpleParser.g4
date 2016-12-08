parser grammar SimpleParser;
options {
  // get token types from SimpleLexer.tokens; don't name it
  // SimpleParser.tokens as ANTLR will overwrite!
  tokenVocab=SimpleLexer; 
}

s : ( ID | INT )* SEMI ;

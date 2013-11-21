package org.antlr.v4.runtime.tree.pattern;

/** A chunk is either a token reference, a rule reference, or some plaintext
 *  within a tree pattern.  Function split() in the pattern matcher returns
 *  a list of chunks in preparation for creating a token stream by tokenize().
 *  From there, we get a parse tree from with compile().
 */
abstract class Chunk {
}

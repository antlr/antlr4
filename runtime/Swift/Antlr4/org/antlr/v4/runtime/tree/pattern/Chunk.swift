/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */



/**
 * A chunk is either a token tag, a rule tag, or a span of literal text within a
 * tree pattern.
 *
 * <p>The method {@link org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher#split(String)} returns a list of
 * chunks in preparation for creating a token stream by
 * {@link org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher#tokenize(String)}. From there, we get a parse
 * tree from with {@link org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher#compile(String, int)}. These
 * chunks are converted to {@link org.antlr.v4.runtime.tree.pattern.RuleTagToken}, {@link org.antlr.v4.runtime.tree.pattern.TokenTagToken}, or the
 * regular tokens of the text surrounding the tags.</p>
 */

public class Chunk {
}

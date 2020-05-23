module antlr.v4.runtime.tree.pattern.Chunk;

/**
 * @uml
 * A chunk is either a token tag, a rule tag, or a span of literal text within a
 * tree pattern.
 *
 * <p>The method {@link ParseTreePatternMatcher#split(String)} returns a list of
 * chunks in preparation for creating a token stream by
 * {@link ParseTreePatternMatcher#tokenize(String)}. From there, we get a parse
 * tree from with {@link ParseTreePatternMatcher#compile(String, int)}. These
 * chunks are converted to {@link RuleTagToken}, {@link TokenTagToken}, or the
 * regular tokens of the text surrounding the tags.</p>
 */
abstract class Chunk
{

}

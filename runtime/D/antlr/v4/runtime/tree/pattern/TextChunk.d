module antlr.v4.runtime.tree.pattern.TextChunk;

import antlr.v4.runtime.IllegalArgumentException;
import antlr.v4.runtime.tree.pattern.Chunk;

/**
 * @uml
 * Represents a span of raw text (concrete syntax) between tags in a tree
 * pattern string.
 */
class TextChunk : Chunk
{

    private string text;

    /**
     * @uml
     * Constructs a new instance of {@link TextChunk} with the specified text.
     */
    public this(string text)
    {
        if (text is null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        this.text = text;
    }

    /**
     * @uml
     * Gets the raw text of this chunk.
     */
    public string getText()
    {
        return text;
    }

    /**
     * @uml
     * <p>The implementation for {@link TextChunk} returns the result of
     * {@link #getText()} in single quotes.</p>
     * @override
     */
    public override string toString()
    {
        return "'" ~ text ~ "'";
    }

}

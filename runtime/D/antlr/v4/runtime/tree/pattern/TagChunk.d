module antlr.v4.runtime.tree.pattern.TagChunk;

import antlr.v4.runtime.tree.pattern.Chunk;
import antlr.v4.runtime.IllegalArgumentException;

/**
 * @uml
 * Represents a placeholder tag in a tree pattern. A tag can have any of the
 * following forms.
 *
 * <ul>
 * <li>{@code expr}: An unlabeled placeholder for a parser rule {@code expr}.</li>
 * <li>{@code ID}: An unlabeled placeholder for a token of type {@code ID}.</li>
 * <li>{@code e:expr}: A labeled placeholder for a parser rule {@code expr}.</li>
 * <li>{@code id:ID}: A labeled placeholder for a token of type {@code ID}.</li>
 * </ul>
 *
 * This class does not perform any validation on the tag or label names aside
 * from ensuring that the tag is a non-null, non-empty string.
 */
class TagChunk : Chunk
{

    /**
     * @uml
     * This is the backing field for {@link #getTag}.
     */
    private string tag;

    /**
     * @uml
     * This is the backing field for {@link #getLabel}.
     */
    private string label;

    /**
     * @uml
     * Construct a new instance of {@link TagChunk} using the specified tag and
     * no label.
     * 	 *
     *  @param tag The tag, which should be the name of a parser rule or token
     * type.
     *
     *  @exception IllegalArgumentException if {@code tag} is {@code null} or
     * empty.
     */
    public this(string tag)
    {
        this(null, tag);
    }

    /**
     * @uml
     * Construct a new instance of {@link TagChunk} using the specified label
     * and tag.
     *
     *  @param label The label for the tag. If this is {@code null}, the
     *  {@link TagChunk} represents an unlabeled tag.
     *  @param tag The tag, which should be the name of a parser rule or token
     *  type.
     *
     *  @exception IllegalArgumentException if {@code tag} is {@code null} or
     * empty.
     */
    public this(string label, string tag)
    {
        if (tag is null || tag.length == 0) {
            throw new IllegalArgumentException("tag cannot be null or empty");
        }
        this.label = label;
        this.tag = tag;
    }

    /**
     * @uml
     * Get the tag for this chunk.
     */
    public string getTag()
    {
        return tag;
    }

    /**
     * @uml
     * et the label, if any, assigned to this chunk.
     *
     *  @return The label assigned to this chunk, or {@code null} if no label is
     * assigned to the chunk.
     */
    public string getLabel()
    {
        return label;
    }

    /**
     * @uml
     * This method returns a text representation of the tag chunk. Labeled tags
     * are returned in the form {@code label:tag}, and unlabeled tags are
     * returned as just the tag name.
     * @override
     */
    public override string toString()
    {
        if (label !is null) {
            return label ~ ":" ~ tag;
        }
        return tag;
    }

}

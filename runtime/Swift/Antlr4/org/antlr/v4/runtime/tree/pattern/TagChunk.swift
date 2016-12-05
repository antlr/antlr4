/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/**
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

public class TagChunk: Chunk, CustomStringConvertible {
    /**
     * This is the backing field for {@link #getTag}.
     */
    private let tag: String
    /**
     * This is the backing field for {@link #getLabel}.
     */
    private let label: String?

    /**
     * Construct a new instance of {@link org.antlr.v4.runtime.tree.pattern.TagChunk} using the specified tag and
     * no label.
     *
     * @param tag The tag, which should be the name of a parser rule or token
     * type.
     *
     * @exception IllegalArgumentException if {@code tag} is {@code null} or
     * empty.
     */
    public convenience init(_ tag: String) throws {
        try self.init(nil, tag)
    }

    /**
     * Construct a new instance of {@link org.antlr.v4.runtime.tree.pattern.TagChunk} using the specified label
     * and tag.
     *
     * @param label The label for the tag. If this is {@code null}, the
     * {@link org.antlr.v4.runtime.tree.pattern.TagChunk} represents an unlabeled tag.
     * @param tag The tag, which should be the name of a parser rule or token
     * type.
     *
     * @exception IllegalArgumentException if {@code tag} is {@code null} or
     * empty.
     */
    public init(_ label: String?, _ tag: String) throws {

        self.label = label
        self.tag = tag
        super.init()
        if tag.isEmpty {
            throw ANTLRError.illegalArgument(msg: "tag cannot be null or empty")
        }
    }

    /**
     * Get the tag for this chunk.
     *
     * @return The tag for the chunk.
     */

    public final func getTag() -> String {
        return tag
    }

    /**
     * Get the label, if any, assigned to this chunk.
     *
     * @return The label assigned to this chunk, or {@code null} if no label is
     * assigned to the chunk.
     */

    public final func getLabel() -> String? {
        return label
    }

    /**
     * This method returns a text representation of the tag chunk. Labeled tags
     * are returned in the form {@code label:tag}, and unlabeled tags are
     * returned as just the tag name.
     */


    public var description: String {
        if label != nil {
            return label! + ":" + tag
        }

        return tag
    }
}

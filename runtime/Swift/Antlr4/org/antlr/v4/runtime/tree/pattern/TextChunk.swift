/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/**
 * Represents a span of raw text (concrete syntax) between tags in a tree
 * pattern string.
 */

public class TextChunk: Chunk, CustomStringConvertible {
    /**
     * This is the backing field for {@link #getText}.
     */

    private let text: String

    /**
     * Constructs a new instance of {@link org.antlr.v4.runtime.tree.pattern.TextChunk} with the specified text.
     *
     * @param text The text of this chunk.
     * @exception IllegalArgumentException if {@code text} is {@code null}.
     */
    public init(_ text: String) {
        self.text = text
    }

    /**
     * Gets the raw text of this chunk.
     *
     * @return The text of the chunk.
     */

    public final func getText() -> String {
        return text
    }

    /**
     * {@inheritDoc}
     *
     * <p>The implementation for {@link org.antlr.v4.runtime.tree.pattern.TextChunk} returns the result of
     * {@link #getText()} in single quotes.</p>
     */


    public var description: String {
        return "'" + text + "'"
    }
}

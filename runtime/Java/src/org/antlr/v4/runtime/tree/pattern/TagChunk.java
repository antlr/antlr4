/*
 * [The "BSD license"]
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Sam Harwell
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime.tree.pattern;

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
class TagChunk extends Chunk {
	/**
	 * This is the backing field for {@link #getTag}.
	 */
	private final String tag;
	/**
	 * This is the backing field for {@link #getLabel}.
	 */
	private final String label;

	/**
	 * Construct a new instance of {@link TagChunk} using the specified tag and
	 * no label.
	 *
	 * @param tag The tag, which should be the name of a parser rule or token
	 * type.
	 *
	 * @exception IllegalArgumentException if {@code tag} is {@code null} or
	 * empty.
	 */
	public TagChunk(String tag) {
		this(null, tag);
	}

	/**
	 * Construct a new instance of {@link TagChunk} using the specified label
	 * and tag.
	 *
	 * @param label The label for the tag. If this is {@code null}, the
	 * {@link TagChunk} represents an unlabeled tag.
	 * @param tag The tag, which should be the name of a parser rule or token
	 * type.
	 *
	 * @exception IllegalArgumentException if {@code tag} is {@code null} or
	 * empty.
	 */
	public TagChunk(String label, String tag) {
		if (tag == null || tag.isEmpty()) {
			throw new IllegalArgumentException("tag cannot be null or empty");
		}

		this.label = label;
		this.tag = tag;
	}

	/**
	 * Get the tag for this chunk.
	 *
	 * @return The tag for the chunk.
	 */

	public final String getTag() {
		return tag;
	}

	/**
	 * Get the label, if any, assigned to this chunk.
	 *
	 * @return The label assigned to this chunk, or {@code null} if no label is
	 * assigned to the chunk.
	 */

	public final String getLabel() {
		return label;
	}

	/**
	 * This method returns a text representation of the tag chunk. Labeled tags
	 * are returned in the form {@code label:tag}, and unlabeled tags are
	 * returned as just the tag name.
	 */
	@Override
	public String toString() {
		if (label != null) {
			return label + ":" + tag;
		}

		return tag;
	}
}

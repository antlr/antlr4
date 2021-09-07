/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

/// A chunk is either a token tag, a rule tag, or a span of literal text within a
/// tree pattern.
///
/// <p>The method {@link ParseTreePatternMatcher#split(String)} returns a list of
/// chunks in preparation for creating a token stream by
/// {@link ParseTreePatternMatcher#tokenize(String)}. From there, we get a parse
/// tree from with {@link ParseTreePatternMatcher#compile(String, int)}. These
/// chunks are converted to [RuleTagToken], [TokenTagToken], or the
/// regular tokens of the text surrounding the tags.</p>
abstract class Chunk {}

/// Represents a placeholder tag in a tree pattern. A tag can have any of the
/// following forms.
///
/// <ul>
/// <li>[expr]: An unlabeled placeholder for a parser rule [expr].</li>
/// <li>[ID]: An unlabeled placeholder for a token of type [ID].</li>
/// <li>{@code e:expr}: A labeled placeholder for a parser rule [expr].</li>
/// <li>{@code id:ID}: A labeled placeholder for a token of type [ID].</li>
/// </ul>
///
/// This class does not perform any validation on the tag or label names aside
/// from ensuring that the tag is a non-null, non-empty string.
class TagChunk extends Chunk {
  /// The tag for the chunk.
  final String tag;

  /// The label assigned to this chunk, or null if no label is
  /// assigned to the chunk.
  final String? label;

  /// Construct a new instance of [TagChunk] using the specified label
  /// and tag.
  ///
  /// @param label The label for the tag. If this is null, the
  /// [TagChunk] represents an unlabeled tag.
  /// @param tag The tag, which should be the name of a parser rule or token
  /// type.
  ///
  /// @exception ArgumentError if [tag] is null or empty.
  TagChunk(this.tag, {this.label}) {
    if (tag.isEmpty) {
      throw ArgumentError.value(tag, 'tag', 'cannot be empty');
    }
  }

  /// This method returns a text representation of the tag chunk. Labeled tags
  /// are returned in the form {@code label:tag}, and unlabeled tags are
  /// returned as just the tag name.
  @override
  String toString() {
    if (label != null) {
      return label! + ':' + tag;
    }

    return tag;
  }
}

/// Represents a span of raw text (concrete syntax) between tags in a tree
/// pattern string.
class TextChunk extends Chunk {
  /// The text of the chunk.
  final String text;

  /// Constructs a new instance of [TextChunk] with the specified text.
  ///
  /// @param text The text of this chunk.
  /// @exception IllegalArgumentException if [text] is null.
  TextChunk(this.text);

  /// {@inheritDoc}
  ///
  /// <p>The implementation for [TextChunk] returns the result of
  /// {@link #getText()} in single quotes.</p>
  @override
  String toString() {
    return "'" + text + "'";
  }
}

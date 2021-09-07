/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import '../../../error/error.dart';
import '../../../input_stream.dart';
import '../../../lexer.dart';
import '../../../misc/multi_map.dart';
import '../../../parser.dart';
import '../../../parser_interpreter.dart';
import '../../../parser_rule_context.dart';
import '../../../token.dart';
import '../../../token_source.dart';
import '../../../token_stream.dart';
import '../../../util/utils.dart';
import '../tree.dart';
import 'chunk.dart';

/// Represents the result of matching a [ParseTree] against a tree pattern.
class ParseTreeMatch {
  /// Get the parse tree we are trying to match to a pattern.
  ///
  /// @return The [ParseTree] we are trying to match to a pattern.
  final ParseTree tree;

  /// Get the tree pattern we are matching against.
  ///
  /// @return The tree pattern we are matching against.
  final ParseTreePattern pattern;

  /// Return a mapping from label &rarr; [list of nodes].
  ///
  /// <p>The map includes special entries corresponding to the names of rules and
  /// tokens referenced in tags in the original pattern. For additional
  /// information, see the description of {@link #getAll(String)}.</p>
  ///
  /// @return A mapping from labels to parse tree nodes. If the parse tree
  /// pattern did not contain any rule or token tags, this map will be empty.
  final MultiMap<String, ParseTree> labels;

  /// Get the node at which we first detected a mismatch.
  ///
  /// @return the node at which we first detected a mismatch, or null
  /// if the match was successful.
  final ParseTree? mismatchedNode;

  /// Constructs a new instance of [ParseTreeMatch] from the specified
  /// parse tree and pattern.
  ///
  /// @param tree The parse tree to match against the pattern.
  /// @param pattern The parse tree pattern.
  /// @param labels A mapping from label names to collections of
  /// [ParseTree] objects located by the tree pattern matching process.
  /// @param mismatchedNode The first node which failed to match the tree
  /// pattern during the matching process.
  ///
  ParseTreeMatch(this.tree, this.pattern, this.labels, this.mismatchedNode);

  /// Get the last node associated with a specific [label].
  ///
  /// <p>For example, for pattern {@code <id:ID>}, {@code get("id")} returns the
  /// node matched for that [ID]. If more than one node
  /// matched the specified label, only the last is returned. If there is
  /// no node associated with the label, this returns null.</p>
  ///
  /// <p>Pattern tags like {@code <ID>} and {@code <expr>} without labels are
  /// considered to be labeled with [ID] and [expr], respectively.</p>
  ///
  /// @param label The label to check.
  ///
  /// @return The last [ParseTree] to match a tag with the specified
  /// label, or null if no parse tree matched a tag with the label.

  ParseTree? get(String label) {
    final parseTrees = labels[label];
    if (parseTrees == null || parseTrees.isEmpty) {
      return null;
    }

    return parseTrees[parseTrees.length - 1]; // return last if multiple
  }

  /// Return all nodes matching a rule or token tag with the specified label.
  ///
  /// <p>If the [label] is the name of a parser rule or token in the
  /// grammar, the resulting list will contain both the parse trees matching
  /// rule or tags explicitly labeled with the label and the complete set of
  /// parse trees matching the labeled and unlabeled tags in the pattern for
  /// the parser rule or token. For example, if [label] is {@code "foo"},
  /// the result will contain <em>all</em> of the following.</p>
  ///
  /// <ul>
  /// <li>Parse tree nodes matching tags of the form {@code <foo:anyRuleName>} and
  /// {@code <foo:AnyTokenName>}.</li>
  /// <li>Parse tree nodes matching tags of the form {@code <anyLabel:foo>}.</li>
  /// <li>Parse tree nodes matching tags of the form {@code <foo>}.</li>
  /// </ul>
  ///
  /// @param label The label.
  ///
  /// @return A collection of all [ParseTree] nodes matching tags with
  /// the specified [label]. If no nodes matched the label, an empty list
  /// is returned.

  List<ParseTree> getAll(String label) {
    final nodes = labels[label];
    if (nodes == null) {
      return [];
    }

    return nodes;
  }

  /// Gets a value indicating whether the match operation succeeded.
  ///
  /// @return [true] if the match operation succeeded; otherwise,
  /// [false].
  bool get succeeded => mismatchedNode == null;

  /// {@inheritDoc}
  @override
  String toString() {
    return "Match ${succeeded ? "succeeded" : "failed"}; found ${labels.length} labels";
  }
}

/// A pattern like {@code <ID> = <expr>;} converted to a [ParseTree] by
/// {@link ParseTreePatternMatcher#compile(String, int)}.
class ParseTreePattern {
  /// Get the parser rule which serves as the outermost rule for the tree
  /// pattern.
  ///
  /// @return The parser rule which serves as the outermost rule for the tree
  /// pattern.
  final int patternRuleIndex;

  /// Get the tree pattern in concrete syntax form.
  ///
  /// @return The tree pattern in concrete syntax form.
  final String pattern;

  /// Get the tree pattern as a [ParseTree]. The rule and token tags from
  /// the pattern are present in the parse tree as terminal nodes with a symbol
  /// of type [RuleTagToken] or [TokenTagToken].
  ///
  /// @return The tree pattern as a [ParseTree].
  final ParseTree patternTree;

  /// Get the [ParseTreePatternMatcher] which created this tree pattern.
  ///
  /// @return The [ParseTreePatternMatcher] which created this tree
  /// pattern.
  final ParseTreePatternMatcher matcher;

  /// Construct a new instance of the [ParseTreePattern] class.
  ///
  /// @param matcher The [ParseTreePatternMatcher] which created this
  /// tree pattern.
  /// @param pattern The tree pattern in concrete syntax form.
  /// @param patternRuleIndex The parser rule which serves as the root of the
  /// tree pattern.
  /// @param patternTree The tree pattern in [ParseTree] form.
  ParseTreePattern(
    this.matcher,
    this.pattern,
    this.patternRuleIndex,
    this.patternTree,
  );

  /// Match a specific parse tree against this tree pattern.
  ///
  /// @param tree The parse tree to match against this tree pattern.
  /// @return A [ParseTreeMatch] object describing the result of the
  /// match operation. The {@link ParseTreeMatch#succeeded()} method can be
  /// used to determine whether or not the match was successful.

  ParseTreeMatch match(ParseTree tree) {
    return matcher.match(tree, pattern: this);
  }

  /// Determine whether or not a parse tree matches this tree pattern.
  ///
  /// @param tree The parse tree to match against this tree pattern.
  /// @return [true] if [tree] is a match for the current tree
  /// pattern; otherwise, [false].
  bool matches(ParseTree tree) {
    return matcher.match(tree, pattern: this).succeeded;
  }
}

/// A tree pattern matching mechanism for ANTLR [ParseTree]s.
///
/// <p>Patterns are strings of source input text with special tags representing
/// token or rule references such as:</p>
///
/// <p>{@code <ID> = <expr>;}</p>
///
/// <p>Given a pattern start rule such as [statement], this object constructs
/// a [ParseTree] with placeholders for the [ID] and [expr]
/// subtree. Then the {@link #match} routines can compare an actual
/// [ParseTree] from a parse with this pattern. Tag {@code <ID>} matches
/// any [ID] token and tag {@code <expr>} references the result of the
/// [expr] rule (generally an instance of [ExprContext].</p>
///
/// <p>Pattern {@code x = 0;} is a similar pattern that matches the same pattern
/// except that it requires the identifier to be [x] and the expression to
/// be {@code 0}.</p>
///
/// <p>The {@link #matches} routines return [true] or [false] based
/// upon a match for the tree rooted at the parameter sent in. The
/// {@link #match} routines return a [ParseTreeMatch] object that
/// contains the parse tree, the parse tree pattern, and a map from tag name to
/// matched nodes (more below). A subtree that fails to match, returns with
/// {@link ParseTreeMatch#mismatchedNode} set to the first tree node that did not
/// match.</p>
///
/// <p>For efficiency, you can compile a tree pattern in string form to a
/// [ParseTreePattern] object.</p>
///
/// <p>See [TestParseTreeMatcher] for lots of examples.
/// [ParseTreePattern] has two static helper methods:
/// {@link ParseTreePattern#findAll} and {@link ParseTreePattern#match} that
/// are easy to use but not super efficient because they create new
/// [ParseTreePatternMatcher] objects each time and have to compile the
/// pattern in string form before using it.</p>
///
/// <p>The lexer and parser that you pass into the [ParseTreePatternMatcher]
/// constructor are used to parse the pattern in string form. The lexer converts
/// the {@code <ID> = <expr>;} into a sequence of four tokens (assuming lexer
/// throws out whitespace or puts it on a hidden channel). Be aware that the
/// input stream is reset for the lexer (but not the parser; a
/// [ParserInterpreter] is created to parse the input.). Any user-defined
/// fields you have put into the lexer might get changed when this mechanism asks
/// it to scan the pattern string.</p>
///
/// <p>Normally a parser does not accept token {@code <expr>} as a valid
/// [expr] but, from the parser passed in, we create a special version of
/// the underlying grammar representation (an [ATN]) that allows imaginary
/// tokens representing rules ({@code <expr>}) to match entire rules. We call
/// these <em>bypass alternatives</em>.</p>
///
/// <p>Delimiters are {@code <} and {@code >}, with {@code \} as the escape string
/// by default, but you can set them to whatever you want using
/// {@link #setDelimiters}. You must escape both start and stop strings
/// {@code \<} and {@code \>}.</p>
class ParseTreePatternMatcher {
  /// Used to convert the tree pattern string into a series of tokens. The
  /// input stream is reset.
  final Lexer lexer;

  /// Used to collect to the grammar file name, token names, rule names for
  /// used to parse the pattern into a parse tree.
  final Parser parser;

  String start = '<';
  String stop = '>';
  String escape = '\\'; // e.g., \< and \> must escape BOTH!

  /// Constructs a [ParseTreePatternMatcher] or from a [Lexer] and
  /// [Parser] object. The lexer input stream is altered for tokenizing
  /// the tree patterns. The parser is used as a convenient mechanism to get
  /// the grammar name, plus token, rule names.
  ParseTreePatternMatcher(this.lexer, this.parser);

  /// Set the delimiters used for marking rule and token tags within concrete
  /// syntax used by the tree pattern parser.
  ///
  /// @param start The start delimiter.
  /// @param stop The stop delimiter.
  /// @param escapeLeft The escape sequence to use for escaping a start or stop delimiter.
  ///
  /// @exception ArgumentError if [start] is null or empty.
  /// @exception ArgumentError if [stop] is null or empty.
  void setDelimiters(String start, String stop, String escapeLeft) {
    if (start.isEmpty) {
      throw ArgumentError.value(start, 'start', 'cannot be empty');
    }

    if (stop.isEmpty) {
      throw ArgumentError.value(stop, 'stop', 'cannot be empty');
    }

    this.start = start;
    this.stop = stop;
    escape = escapeLeft;
  }

  /// Does [pattern] matched as rule patternRuleIndex match tree? Pass in a
  ///  compiled pattern instead of a string representation of a tree pattern.
  bool matches(
    ParseTree tree, {
    ParseTreePattern? pattern,
    String? patternStr,
    int? patternRuleIndex,
  }) {
    assert(pattern != null || patternStr != null && patternRuleIndex != null);
    pattern ??= compile(patternStr!, patternRuleIndex!);

    final labels = MultiMap<String, ParseTree>();
    final mismatchedNode = matchImpl(tree, pattern.patternTree, labels);
    return mismatchedNode == null;
  }

  /// Compare [pattern] matched against [tree] and return a
  /// [ParseTreeMatch] object that contains the matched elements, or the
  /// node at which the match failed. Pass in a compiled pattern instead of a
  /// string representation of a tree pattern.

  ParseTreeMatch match(
    ParseTree tree, {
    ParseTreePattern? pattern,
    String? patternStr,
    int? patternRuleIndex,
  }) {
    assert(pattern != null || patternStr != null && patternRuleIndex != null);
    pattern ??= compile(patternStr!, patternRuleIndex!);

    final labels = MultiMap<String, ParseTree>();
    final mismatchedNode = matchImpl(tree, pattern.patternTree, labels);
    return ParseTreeMatch(tree, pattern, labels, mismatchedNode);
  }

  /// For repeated use of a tree pattern, compile it to a
  /// [ParseTreePattern] using this method.
  ParseTreePattern compile(String pattern, int patternRuleIndex) {
    final tokenList = tokenize(pattern);
    final tokenSrc = ListTokenSource(tokenList);
    final tokens = CommonTokenStream(tokenSrc);

    final parserInterp = ParserInterpreter(
      parser.grammarFileName,
      parser.vocabulary,
      parser.ruleNames,
      parser.ATNWithBypassAlts,
      tokens,
    );

    ParseTree tree;
    try {
      parserInterp.errorHandler = BailErrorStrategy();
      tree = parserInterp.parse(patternRuleIndex);
//			System.out.println("pattern tree = "+tree.toStringTree(parserInterp));
    } on ParseCancellationException {
      rethrow;
    } on RecognitionException {
      rethrow;
    } catch (e) {
      throw CannotInvokeStartRule(e.toString());
    }

    // Make sure tree pattern compilation checks for a complete parse
    if (tokens.LA(1) != Token.EOF) {
      throw StartRuleDoesNotConsumeFullPattern();
    }

    return ParseTreePattern(this, pattern, patternRuleIndex, tree);
  }

  // ---- SUPPORT CODE ----

  /// Recursively walk [tree] against [patternTree], filling
  /// {@code match.}{@link ParseTreeMatch#labels labels}.
  ///
  /// @return the first node encountered in [tree] which does not match
  /// a corresponding node in [patternTree], or null if the match
  /// was successful. The specific node returned depends on the matching
  /// algorithm used by the implementation, and may be overridden.

  ParseTree? matchImpl(
    ParseTree tree,
    ParseTree patternTree,
    MultiMap<String, ParseTree> labels,
  ) {
    // x and <ID>, x and y, or x and x; or could be mismatched types
    if (tree is TerminalNode && patternTree is TerminalNode) {
      final t1 = tree;
      final t2 = patternTree;
      late final ParseTree mismatchedNode;
      // both are tokens and they have same type
      if (t1.symbol.type == t2.symbol.type) {
        if (t2.symbol is TokenTagToken) {
          // x and <ID>
          final tokenTagToken = t2.symbol as TokenTagToken;
          // track label->list-of-nodes for both token name and label (if any)
          labels.put(tokenTagToken.tokenName, tree);
          if (tokenTagToken.label != null) {
            labels.put(tokenTagToken.label!, tree);
          }
        } else if (t1.text == t2.text) {
          // x and x
        } else {
          // x and y
          mismatchedNode = t1;
        }
      } else {
        mismatchedNode = t1;
      }

      return mismatchedNode;
    }

    if (tree is ParserRuleContext && patternTree is ParserRuleContext) {
      final r1 = tree;
      final r2 = patternTree;
      late final ParseTree mismatchedNode;
      // (expr ...) and <expr>
      final ruleTagToken = getRuleTagToken(r2);
      if (ruleTagToken != null) {
        if (r1.ruleContext.ruleIndex == r2.ruleContext.ruleIndex) {
          // track label->list-of-nodes for both rule name and label (if any)
          labels.put(ruleTagToken.ruleName, tree);
          if (ruleTagToken.label != null) {
            labels.put(ruleTagToken.label!, tree);
          }
        } else {
          mismatchedNode = r1;
        }

        return mismatchedNode;
      }

      // (expr ...) and (expr ...)
      if (r1.childCount != r2.childCount) {
        mismatchedNode = r1;

        return mismatchedNode;
      }

      final n = r1.childCount;
      for (var i = 0; i < n; i++) {
        final childMatch =
            matchImpl(r1.getChild(i)!, patternTree.getChild(i)!, labels);
        if (childMatch != null) {
          return childMatch;
        }
      }

      return null;
    }

    // if nodes aren't both tokens or both rule nodes, can't match
    return tree;
  }

  /// Is [t] {@code (expr <expr>)} subtree? */
  RuleTagToken? getRuleTagToken(ParseTree t) {
    if (t is RuleNode) {
      final r = t;
      if (r.childCount == 1 && r.getChild(0) is TerminalNode) {
        final c = r.getChild<TerminalNode>(0)! as TerminalNode;
        if (c.symbol is RuleTagToken) {
          return c.symbol as RuleTagToken;
        }
      }
    }
    return null;
  }

  List<Token> tokenize(String pattern) {
    // split pattern into chunks: sea (raw input) and islands (<ID>, <expr>)
    final chunks = split(pattern);

    // create token stream from text and tags
    final tokens = <Token>[];
    for (var chunk in chunks) {
      if (chunk is TagChunk) {
        final tagChunk = chunk;
        // add special rule token or conjure up new token from name
        if (isUpperCase(tagChunk.tag[0])) {
          final ttype = parser.getTokenType(tagChunk.tag);
          if (ttype == Token.INVALID_TYPE) {
            throw ArgumentError(
                'Unknown token ' + tagChunk.tag + ' in pattern: ' + pattern);
          }
          final t = TokenTagToken(tagChunk.tag, ttype, tagChunk.label);
          tokens.add(t);
        } else if (isLowerCase(tagChunk.tag[0])) {
          final ruleIndex = parser.getRuleIndex(tagChunk.tag);
          if (ruleIndex == -1) {
            throw ArgumentError(
                'Unknown rule ' + tagChunk.tag + ' in pattern: ' + pattern);
          }
          final ruleImaginaryTokenType =
              parser.ATNWithBypassAlts.ruleToTokenType[ruleIndex];
          tokens.add(RuleTagToken(
              tagChunk.tag, ruleImaginaryTokenType, tagChunk.label));
        } else {
          throw ArgumentError(
              'invalid tag: ' + tagChunk.tag + ' in pattern: ' + pattern);
        }
      } else {
        final textChunk = chunk as TextChunk;
        final inputStream = InputStream.fromString(textChunk.text);
        lexer.inputStream = inputStream;
        var t = lexer.nextToken();
        while (t.type != Token.EOF) {
          tokens.add(t);
          t = lexer.nextToken();
        }
      }
    }

//		System.out.println("tokens="+tokens);
    return tokens;
  }

  /// Split {@code <ID> = <e:expr> ;} into 4 chunks for tokenizing by {@link #tokenize}. */
  List<Chunk> split(String pattern) {
    var p = 0;
    final n = pattern.length;
    final chunks = <Chunk>[];
    // find all start and stop indexes first, then collect
    final starts = <int>[];
    final stops = <int>[];
    while (p < n) {
      if (p == pattern.indexOf(escape + start, p)) {
        p += escape.length + start.length;
      } else if (p == pattern.indexOf(escape + stop, p)) {
        p += escape.length + stop.length;
      } else if (p == pattern.indexOf(start, p)) {
        starts.add(p);
        p += start.length;
      } else if (p == pattern.indexOf(stop, p)) {
        stops.add(p);
        p += stop.length;
      } else {
        p++;
      }
    }

//		System.out.println("");
//		System.out.println(starts);
//		System.out.println(stops);
    if (starts.length > stops.length) {
      throw ArgumentError('unterminated tag in pattern: ' + pattern);
    }

    if (starts.length < stops.length) {
      throw ArgumentError('missing start tag in pattern: ' + pattern);
    }

    final ntags = starts.length;
    for (var i = 0; i < ntags; i++) {
      if (starts[i] >= stops[i]) {
        throw ArgumentError(
            'tag delimiters out of order in pattern: ' + pattern);
      }
    }

    // collect into chunks now
    if (ntags == 0) {
      final text = pattern.substring(0, n);
      chunks.add(TextChunk(text));
    }

    if (ntags > 0 && starts[0] > 0) {
      // copy text up to first tag into chunks
      final text = pattern.substring(0, starts[0]);
      chunks.add(TextChunk(text));
    }
    for (var i = 0; i < ntags; i++) {
      // copy inside of <tag>
      final tag = pattern.substring(starts[i] + start.length, stops[i]);
      var ruleOrToken = tag;
      String? label;
      final colon = tag.indexOf(':');
      if (colon >= 0) {
        label = tag.substring(0, colon);
        ruleOrToken = tag.substring(colon + 1, tag.length);
      }
      chunks.add(TagChunk(ruleOrToken, label: label));
      if (i + 1 < ntags) {
        // copy from end of <tag> to start of next
        final text = pattern.substring(stops[i] + stop.length, starts[i + 1]);
        chunks.add(TextChunk(text));
      }
    }
    if (ntags > 0) {
      final afterLastTag = stops[ntags - 1] + stop.length;
      if (afterLastTag < n) {
        // copy text from end of last tag to end
        final text = pattern.substring(afterLastTag, n);
        chunks.add(TextChunk(text));
      }
    }

    // strip out the escape sequences from text chunks but not tags
    for (var i = 0; i < chunks.length; i++) {
      final c = chunks[i];
      if (c is TextChunk) {
        final tc = c;
        final unescaped = tc.text.replaceAll(escape, '');
        if (unescaped.length < tc.text.length) {
          chunks[i] = TextChunk(unescaped);
        }
      }
    }

    return chunks;
  }
}

class CannotInvokeStartRule extends StateError {
  CannotInvokeStartRule(String message) : super(message);
}

// Fixes https://github.com/antlr/antlr4/issues/413
// "Tree pattern compilation doesn't check for a complete parse"
class StartRuleDoesNotConsumeFullPattern extends Error {}

/// This exception is thrown to cancel a parsing operation. This exception does
/// not extend [RecognitionException], allowing it to bypass the standard
/// error recovery mechanisms. [BailErrorStrategy] throws this exception in
/// response to a parse error.
class ParseCancellationException extends StateError {
  ParseCancellationException(String message) : super(message);
}

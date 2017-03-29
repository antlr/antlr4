# Lexers and Unicode text

Prior to ANTLR 4.7, generated lexers only supported part of the Unicode standard (code points up to `U+FFFF`). As of ANTLR 4.7, the lexers in all language runtimes support the full range of Unicode code points up to `U+10FFFF`, as
long as the input `CharStream` is opened using `CharStreams.fromPath()`, `CharStreams.fromFileName()`, etc...
or the equivalent method for your runtime's language. 

The deprecated `ANTLRInputStream` and `ANTLRFileStream` APIs only support Unicode code points up to `U+FFFF`.

A big shout out to Ben Hamilton (github bhamiltoncx) for his superhuman
efforts across all targets to get true Unicode 3.1 support for U+10FFFF.

## Example

### Java

```java
public static void main(String[] args) {
  CharStream charStream = CharStreams.fromPath(Paths.get(args[0]));
  Lexer lexer = new UnicodeLexer(charStream);
  CommonTokenStream tokens = new CommonTokenStream(lexer);
  tokens.fill();
  for (Token token : tokens.getTokens()) {
    System.out.println("Got token: " + token.toString());
  }
}
```

# Unicode Code Points in Lexer Grammars

To refer to Unicode [code points](https://en.wikipedia.org/wiki/Code_point)
in lexer grammars, use the `\u` string escape plus up to 4 hex digits. For example, to create
a lexer rule for a single Cyrillic character by creating a range from
`U+0400` to `U+04FF`:

```ANTLR
CYRILLIC : '\u0400'..'\u04FF' ; // or [\u0400-\u04FF] without quotes
```

Unicode literals larger than U+FFFF must use the extended `\u{12345}` syntax. For example, to create a lexer rule for a selection of smiley faces
from the [Emoticons Unicode block](http://www.unicode.org/charts/PDF/U1F600.pdf):

```ANTLR
EMOTICONS : ('\u{1F600}' | '\u{1F602}' | '\u{1F615}') ; // or [\u{1F600}\u{1F602}\u{1F615}]
```

Finally, lexer char sets can include Unicode properties. Each Unicode code point has at least one property that describes the type group to which it belongs (e.g. alpha, number, punctuation). Other properties can be the language script or special binary properties and Unicode code blocks. That means however, that a property specifies a group of code points, hence they are only allowed in lexer char sets.

```ANTLR
EMOJI : [\p{Emoji}] ;
JAPANESE : [\p{Script=Hiragana}\p{Script=Katakana}\p{Script=Han}] ;
NOT_CYRILLIC : [\P{Script=Cyrillic}] ;
```

See [lexer-rules.md](lexer-rules.md#lexer-rule-elements) for more detail on Unicode
escapes in lexer rules.

## Migration


Code for **4.6** looked like this:


```java
CharStream input = new ANTLRFileStream("myinputfile")
JavaLexer lexer = new JavaLexer(input);
CommonTokenStream tokens = new CommonTokenStream(lexer);
```

Code for **4.7** looks like this:

```java
CharStream input = CharStreams.fromFileName("inputfile");
JavaLexer lexer = new JavaLexer(input);
CommonTokenStream tokens = new CommonTokenStream(lexer);
```

Or, if you'd like to specify the file encoding:

```java
CharStream input = CharStreams.fromFileName("inputfile", StandardCharsets.UTF_16);
```

### Motivation

After a [lively discussion](https://github.com/antlr/antlr4/pull/1771), I (parrt) decided not to simply gut the 4.6 `ANTLRFileStream` and `ANTLRInputStream` to incorporate the new U+10FFFF functionality. I decided to *deprecate* the old interface and recommend use of the new interface to prevent confusion. My reasoning is summarized as:

* I didn't like the idea of breaking all 4.6 code. To get the previous streams to properly support > 16 bit Unicode would require a lot of changes to the method signatures.
* Using `int` buffer element types would double the size of memory required to hold streams in memory, given that we buffer everything (and I didn't want to change that aspect of the streams).
* The new factory-style interface supports creation of the smallest possible code point buffer element size according to the Unicode code points found in the input stream. This means using half as much memory
as the old {@link ANTLRFileStream}, which assumed 16-bit characters, for ASCII text.
* Through some [serious testing and performance tweaking](https://github.com/antlr/antlr4/pull/1781), the new streams perform as fast or faster than the 4.6 streams.

**WARNING**. *You should avoid using both the deprecated and the new streams* in the same application because you will see 
a nontrivial performance degradation. This speed hit is because the 
`Lexer`'s internal code goes from a monomorphic to megamorphic
dynamic dispatch to get characters from the input stream. Java's
on-the-fly compiler (JIT) is unable to perform the same optimizations
so stick with either the old or the new streams, if performance is
a primary concern. See the [extreme debugging and spelunking](https://github.com/antlr/antlr4/pull/1781) needed to identify this issue in our timing rig.

### Character Buffering

The ANTLR character streams still buffer all the input when you create
the stream, as they have done for ~20 years. If you need unbuffered
access, please note that it becomes challenging to create
parse trees. The parse tree has to point to tokens which will either
point into a stale location in an unbuffered stream or you have to copy
the characters out of the buffer into the token. That defeats the purpose
of unbuffered input. See the [ANTLR 4 book](https://www.amazon.com/Definitive-ANTLR-4-Reference/dp/1934356999) "13.8 Unbuffered Character and Token Streams". Unbuffered streams are primarily
useful for processing infinite streams *during the parse* and require that you manually buffer characters.

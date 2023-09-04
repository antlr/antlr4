# Writing target-agnostic grammars

Some grammars require
[semantic predicates](https://github.com/antlr/antlr4/blob/dev/doc/predicates.md)
to add context-sensitive parsing to what would generally be a context-free grammar.

For example:
* In Fortran90, [lines that begin with a 'C' in column 1
are comments](https://github.com/antlr/grammars-v4/blob/43fbb16fec1d474d38a603cc6a6bcbe5edf07b1e/fortran/fortran90/slow/hw.f90#L1),
which should be placed on a token stream other than the default.
But, if the 'C' does not begin in
column 1, then the input is invalid and should be flagged as so.
	```fortran
	c Hello World.
	   c This is a syntax error because 'c' does not start in column 1
	program hello
		print *, 'Hello World!'
	end
	```

* In CSharp, two [greater-than signs](https://util.unicode.org/UnicodeJsps/character.jsp?a=003E)
`'>>'` can either mean
[a right shift expression](https://github.com/antlr/grammars-v4/blob/43fbb16fec1d474d38a603cc6a6bcbe5edf07b1e/csharp/examples/AllInOneNoPreprocessor.cs#L657C15-L657C17)
or [part of a type declaration with templates](https://github.com/antlr/grammars-v4/blob/master/csharp/examples/AllInOneNoPreprocessor.cs#L463C33-L463C35).
Since lexers in Antlr are not parser aware,
the lexer must tokenize the two greater-than signs as two separate tokens.
A semantic predicate should be added to disallow a space between the two greater-than signs in the context
of an expression, but allowed in the context of a type declaration. 
	```C#
	class Foo {
		void Func()
		{
			int x = 1000 > > 2;               // syntax error if a space exists in the double greater-than sign
		}
		Dictionary<int, List<int> > mapping;  // nested template declaration, valid
	}
	```

Antlr does not have a general-purpose language for predicates. These must be
written in the target language of the generated parser. The problem is that
a grammar would need to be forked for each target desired, which adds to the
burden of maintenance.

However, it is possible to write the grammar such that forking is not required,
using _target-agnostic format_.

## Rules in writing target-agnostic grammars

1) You will need to [split your grammar](https://github.com/antlr/antlr4/blob/dev/doc/grammars.md#grammar-structure)
into separate lexer and parser grammars. Then, add `options { tokenVocab=...; }` to the parser grammar.
2) Create target-specific source code files that contain methods in a base class for
the parser or lexer grammar. In these source code files, write the code for the semantic
predicate. For example, the files for the Cpp target would be `Python3LexerBase.{cpp,h}`, `Python3ParserBase.{cpp,h}`.
3) In the grammar(s), add `options { superClass=... }`. This will
[superclass the recognizer](https://github.com/antlr/antlr4/blob/dev/doc/options.md#superclass).
For example, `options { superclass=Python3ParserBase; }`.
4) In the grammar(s), write code to make a single
call to the base-class method. The call should have a `this.` string
before the name of the method, e.g., `OPEN_PAREN : '(' {this.openBrace();};`
The action code must not reference Antlr attributes,
variables, types, or have semi-colons as statement separators or
control-flow statements of any kind.
5) For some targets like Cpp and PHP, you may need to add code to include source
code files so that the generated code compiles.
For these, add a comment
such as `// Insert here @header for lexer include.` or `// Insert here @header for parser include.`
to the grammar, before the first rule.
5) Add a Python script called "transformGrammar.py" that rewrites the grammar(s) 
with some target-specific code syntax.
   a) For Cpp: replace `this.` strings with `this->`.
   b) For PHP: replace `this.` strings with `$this->`.
   c) For Python: replace `this.` strings with `self.`, `l.`, or `p.` depending on
where the action or predicate is in the grammar.
   d) For Cpp: replace `// Insert here @header for lexer include.` (or parser) with
`@header::lexer {#include ...}`.
   e) For PHP: replace `// Insert here @header for lexer include.` (or parser) with
`@header::lexer {require ...}`.
   e) Run `python transformGrammar.py *.g4` before generating the parser and lexer.

## Examples of target-agnostic grammars
* [fortran90](https://github.com/antlr/grammars-v4/tree/master/fortran/fortran90)
* [csharp](https://github.com/antlr/grammars-v4/tree/master/csharp)

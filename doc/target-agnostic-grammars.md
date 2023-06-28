# Writing target-agnostic grammars

Some grammars may require
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
or [a declaration that contains a type with
nested templates](https://github.com/antlr/grammars-v4/blob/master/csharp/examples/AllInOneNoPreprocessor.cs#L463C33-L463C35).
Since lexers in Antlr are not parser aware,
the lexer must tokenize the two greater-than signs as two separate tokens.
A semantic predicate should be added to disallow a space between the two greater-than signs when parsing an
expression.
	```C#
	class Foo {
		void Func()
		{
			int x = 1000 > > 2;               // syntax error if a space exists in the double greater-than sign
		}
		Dictionary<int, List<int> > mapping;  // nested template declaration, valid
	}
	```

Antlr does not have a general-purpose language for actions and predicates;
these must be written in the target language of the generated the parser.
The problem is that each forked version of the grammar for a target would
add to the burden of maintaining yet another file.

However, it is possible to write the grammar without forking and still have
target independence, called _target-agnostic format_.

## Rules in writing target-agnostic grammars

1) Move all actions and semantic predicate written in target-specific
code to base-class methods.
2) Replace all actions and semantic predicates in the grammar with a single
call to the method. For actions, use `{ this.name_of_action() }`. For predicates,
use `{ this.name_of_predicate() }?`.
3) Add a Python script called "transformGrammar.py" that replaces strings
in the grammar per target.
   a) For Cpp: replace `this.` strings with `this->`.
   b) For PHP: replace `this.` strings with `$this->`.
   c) For Python: replace `this.` strings with `self.`, `l.`, or `p.` depending on
where the action or predicate is in the grammar.
   d) Run `python transformGrammar.py *.g4` prior to generating the parser and lexer.

## Examples of target-agnostic grammars
* [fortran90](https://github.com/antlr/grammars-v4/tree/master/fortran/fortran90)
* [csharp](https://github.com/antlr/grammars-v4/tree/master/csharp)

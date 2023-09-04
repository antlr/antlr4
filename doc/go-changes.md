# Changes to the Go Runtime over time

## v4.12.0 to v4.13.0

Strictly speaking, if ANTLR was a go only project following [SemVer](https://semver.org/) release v4.13.0 would be
at least a minor version change and arguably a bump to v5. However, we must follow the ANTLR conventions here or the
release numbers would quickly become confusing. I apologize for being unable to follow the Go release rules absolutely 
to the letter.

There are a lot of changes and improvements in this release, but only the change of repo holding the runtime code,
and possibly the removal of interfaces will cause any code changes. There are no breaking changes to the runtime
interfaces.

ANTLR Go Maintainer: [Jim Idle](https://github.com/jimidle) - Email:  [jimi@idle.ws](mailto:jimi@idle.ws)

### Code Relocation

For complicated reasons, including not breaking the builds of some users who use a monorepo and eschew modules, as well
as not making substantial changes to the internal test suite, the Go runtime code will continue to be maintained in
the main ANTLR4 repo `antlr/antlr4`. If you wish to contribute changes to the Go runtime code, please continue to submit 
PRs to this main repo, against the `dev` branch.

The code located in the main repo at about the depth of the Mariana Trench, means that the go tools cannot reconcile
the module correctly. After some debate, it was decided that we would create a dedicated release repo for the Go runtime
so that it will behave exactly as the Go tooling expects. This repo is auto-maintained and keeps both the dev and master
branches up to date.

Henceforth, all future projects using the ANTLR Go runtime, should import as follows:

```go
import (
    "github.com/antlr4-go/antlr/v4"
    )
```

And use the command:

```shell
go get github.com/antlr4-go/antlr
```

To get the module - `go mod tidy` is probably the best way once imports have been changed. 

Please note that there is no longer any source code kept in the ANTLR repo under `github.com/antlr/antlr4/runtime/Go/antlr`.
If you are using the code without modules, then sync the code from the new release repo.

### Documentation

Prior to this release, the godocs were essentially unusable as the go doc code was essentially copied without
change, from teh Java runtime. The godocs are now properly formatted for Go and pkg.dev.

Please feel free to raise an issue if you find any remaining mistakes. Or submit a PR (remember - not to the new repo).
It is expected that it might take a few iterations to get the docs 100% squeaky clean.

### Removal of Unnecessary Interfaces

The Go runtime was originally produced as almost a copy of the Java runtime but with go syntax. This meant that everything 
had an interface. There is no need to use interfaces in Go if there is only ever going to be one implementation of
some struct and its methods. Interfaces cause an extra deference at runtime and are detrimental to performance if you
are trying to squeeze out every last nanosecond, which some users will be trying to do.

This is 99% an internal refactoring of the runtime with no outside effects to the user.

### Generated Recognizers Return *struct and not Interfaces

The generated recognizer code generated an interface for the parsers and lexers. As they can only be implemented by the
generated code, the interfaces were removed. This is possibly the only place you may need to make a code change to
your driver code.

If your code looked like this:

```go
var lexer = parser.NewMySqlLexer(nil)
var p = parser.NewMySqlParser(nil)
```

Or this:

```go
lexer := parser.NewMySqlLexer(nil)
p := parser.NewMySqlParser(nil)
```

Then no changes need to be made. However, fi you predeclared the parser and lexer variables with there type, such as like
this:

```go
var lexer parser.MySqlLexer
var p parser.MySqlParser
// ...
lexer = parser.NewMySqlLexer(nil)
p = parser.NewMySqlParser(nil)
```

You will need to change your variable declarations to pointers (note the introduction of the `*` below. 

```go
var lexer *parser.MySqlLexer
var p *parser.MySqlParser
// ...
lexer = parser.NewMySqlLexer(nil)
p = parser.NewMySqlParser(nil)
```

This is the only user facing change that I can see. This change though has a very beneficial side effect in that you
no longer need to cast the interface into a struct so that you can access methods and data within it. Any code you
had that needed to do that, will be cleaner and faster.

The performance improvement is worth the change and there was no tidy way for me to avoid it.

### Parser Error Recovery Does Not Use Panic

THe generated parser code was again essentially trying to be Java code in disguise. This meant that every parser rule
executed a `defer {}` and a `recover()`, even if there wer no outstanding parser errors. Parser errors were issued by
issuing a `panic()`! 

While some major work has been performed in the go compiler and runtime to make `defer {}` as fast as possible, 
`recover()` is (relatively) slow as it is not meant to be used as a general error mechanism, but to recover from say
an internal library problem if that problem can be recovered to a known state. 

The generated code now stores a recognition error and a flag in the main parser struct and use `goto` to exit the
rule instead of a `panic()`. As might be imagined, this is significantly faster through the happy path. It is also 
faster at generating errors.

The ANTLR runtime tests do check error raising and recovery, but if you find any differences in the error handling
behavior of your parsers, please raise an issue. 

### Reduction in use of Pointers

Certain internal structs, such as interval sets are small and immutable, but were being passed around as pointers
anyway. These have been change to use copies, and resulted in significant performance increases in some cases. 
There is more work to come in this regard.

### ATN Deserialization

When the ATN and associated structures are deserialized for the first time, there was a bug that caused a needed
optimization to fail to be executed. This could have a significant performance effect on recognizers that were written
in a suboptimal way (as in poorly formed grammars). This is now fixed.

### Prediction Context Caching was not Working

This has a massive effect when reusing a parser for a second and subsequent run. The PredictionContextCache merely
used memory but did not speed up subsequent executions. This is now fixed, and you should see a big difference in 
performance when reusing a parser. This single paragraph does not do this fix justice ;) 

### Cumulative Performance Improvements

Though too numerous to mention, there are a lot of small performance improvements, that add up in accumulation. Everything
from improvements in collection performance to slightly better algorithms or specific non-generic algorithms. 

### Cumulative Memory Improvements

The real improvements in memory usage, allocation and garbage collection are saved for the next major release. However,
if your grammar is well-formed and does not require almost infinite passes using ALL(*), then both memory and performance
will be improved with this release.

### Bug Fixes

Other small bug fixes have been addressed, such as potential panics in funcs that did not check input parameters. There
are a lot of bug fixes in this release that most people were probably not aware of. All known bugs are fixed at the 
time of release preparation.

### A Note on Poorly Constructed Grammars

Though I have made some significant strides on improving the performance of poorly formed grammars, those that are
particularly bad will see much less of an incremental improvement compared to those that are fairly well-formed.

This is deliberately so in this release as I felt that those people who have put in effort to optimize the form of their
grammar are looking for performance, where those that have grammars that parser in seconds, tens of seconds or even
minutes, are presumed to not care about performance. 

A particularly good (or bad) example is the MySQL grammar in the ANTLR grammar repository (apologies to the Author 
if you read this note - this isn't an attack). Although I have improved its runtime performance
drastically in the Go runtime, it still takes about a minute to parse complex select statements. As it is constructed, 
there are no magic answers. I will look in more detail at improvements for such parsers, such as not freeing any
memory until the parse is finished (improved 100x in experiments).

The best advice I can give is to put some effort in to the actual grammar itself. well-formed grammars will potentially
see some huge improvements with this release. Badly formed grammars, not so much. 

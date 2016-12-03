# ANTLR4 Language Target, Runtime for Swift

### Usage

#### 1. Install ANTLR4

[The getting started guide](getting-started.md) should get you started.

#### 2. create a Swift lexer or parser
This is pretty much the same as creating a Java lexer or parser, except you need to specify the language target, for example:

```
$ antlr4 -Dlanguage=Swift MyGrammar.g4
```
For a full list of antlr4 tool options, please visit the [tool documentation page](tool-options.md).

#### 3. Get the Swift ANTLR runtime
You will find Swift runtime (framework project) in

```
antlr4/runtime/Swift 
```

#### 4. Example playground

The Swift runtime includes an Xcode playground to get started with.

In Xcode, open `antlr4/runtime/Swift/Antlr4.xcworkspace`.  Select
"Antlr4 OSX > My Mac" as the build target, and build the project as normal.
The playground should then be active.

The playground includes a simple grammar called "Hello", and an example for
walking the parse tree.  You should see in the playground output that it is
printing messages for each node in the parse tree as it walks.

The playground shows how to create a lexer, token stream, and parser, and
how to execute the parse.

The grammar is defined in the playground's `Resources/Hello.g4`.  The parser
was generated from the grammar using Antlr4 like this:

```
cd 'antlr4/runtime/Swift/Antlr4 playground.playground/Resources'
antlr4 -Dlanguage=Swift -visitor -o ../Sources/Autogen Hello.g4
```

The example tree walker is in `Sources/HelloWalker.swift`.

# C# target for ANTLR 4

### Note to historical users

Versions of ANTLR 4.4.x and before managed the C# 
target as part of a [separate tool provided by Sam Harwell](https://github.com/tunnelvisionlabs/antlr4cs/releases/tag/v4.3.0).
As of 4.5, we our releasing a (mono-compatible) C# target together
with the main tool.

Mono compatibility makes it possible to use ANTLR 4 in any C# development
environment, including of course Microsoft Visual Studio, but also Xamarin Studio, which runs on MacOS X.
Given Microsoft's recent commitment to *cross-platform developer experiences*,
we believe this is a great opportunity for C# developers.

Releasing the runtime with the tool ensures that you can get the exact same behavior across many languages: Java, C#, Python, JavaScript, Go, Swift and C++.

## Getting Started

### Step 1: Install Java

The C# target for ANTLR 4 requires Java for *generating* C# code (but the applications compiled from this C# code will not require Java to be installed).
You can install *any* of the following versions of Java to use this target.

If you already have one of the following installed, you should check to make sure the installation is up-to-date.

* Java 8 runtime environment (x86 or x64)
* Java 8 development kit (x86 or x64, provided that the JRE option is also installed during the development kit installation)
* Java 7 runtime environment (x86 or x64)
* Java 7 development kit (x86 or x64, provided that the JRE option is also installed during the development kit installation)

### Step 2: Download the tool

You need to download the ANTLR tool from the ANTLR web site.
This is a Java archive (*.jar) used to generate the C# code from an ANTLR grammar.


### Step 3: Add or create a grammar file (*.g4) in your project

To avoid confusing your IDE, we suggest setting the build action to None for this file.
See the docs and the book to learn about writing lexer and parser grammars.


### Step 4: Generate the C# code

This can be done either from the cmd line, or by adding a custom pre-build command in your project.
At minimal, the cmd line should look as follows: ``java -jar antlr4-4.7.2.jar -Dlanguage=CSharp grammar.g4``
This will generate the files, which you can then integrate in your project.
This is just a quick start. The tool has many useful options to control generation, please refer to its documentation.

### Step 5: Add a reference to the ANTLR runtime in your project

The Antlr 4 standard runtime for C# is now available from NuGet.
We trust that you know how to do add NuGet references to your project :-).
The package id is [Antlr4.Runtime.Standard](https://www.nuget.org/packages/Antlr4.Runtime.Standard/). We do not support other packages.

Use the GUI or the following command in the Package Manager Console:

```
Install-Package Antlr4.Runtime.Standard
```


### Step 6: You're done!

Of course, the generated code is not going to meet your requirement by magic.
There are 3 ways to use the generated code:
 - by generating a parse tree, and traversing it using a listener. This is the most common method.
 - by generating a parse tree, and traversing it using a visitor. This requires the -visitor option, and is a bit more work.
 - by providing code within your grammar, which gets executed when your input files are parsed. 
While the latter works, it is no longer the recommended approach, because it is not portable, and harder to maintain. More importantly, it breaks the parsing when your code breaks.

See the web site for examples of using the generated code.
 
To learn more about ANTLR 4, read [the book](http://a.co/2n4rJlb).
 
### Visual Studio integration

If you require tighter Visual Studio integration, you can use the tools from [Tunnel Vision Labs](http://tunnelvisionlabs.com/). 
(please note however that they use a different tool and runtime)


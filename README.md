# C# target for ANTLR 4

### Note to existing users

Versions of ANTLR 4.4.x and before managed the C# target as a [separate tool provided by Sam Harwell](https://github.com/tunnelvisionlabs/antlr4cs/releases/tag/v4.3.0). As of 4.5, we our releasing a (mono-compatible) C# target derived from Sam's with the main tool. ANTLR 4.5 is now able to generate C#, Java, Python 2, Python 3 and JavaScript. Sam continues to work on his version of the ANTLR tool and so a C# target is also available through that.

This makes it possible to use ANTLR 4 in any C# development environment, including of course Microsoft Visual Studio, but also Xamarin Studio, which runs on MacOS X.
Given Microsoft's recent commitment to *cross-platform developer experiences*, we believe this is a great opportunity for C# developers.

## Getting Started

### Step 1: Install Java

The C# target for ANTLR 4 requires Java for *generating* C# code (but the applications compiled from this C# code will not require Java to be installed).
You can install *any* of the following versions of Java to use this target.

If you already have one of the following installed, you should check to make sure the installation is up-to-date.

* Java 7 runtime environment (x86 or x64)
* Java 7 development kit (x86 or x64, provided that the JRE option is also installed during the development kit installation)
* Java 6 runtime environment (x86 or x64)
* Java 6 development kit (x86 or x64, provided that the JRE option is also installed during the development kit installation)

### Step 2: Download the tool and runtime, and install the runtime

You need to download the following from the ANTLR web site:

1. The ANTLR tool. This is a Java archive (*.jar) used to generate the C# code from an ANTLR grammar.
2. The ANTLR runtime assembly. This is a regular .Net assembly (*.dll).

You then need to install the runtime assembly in the GAC. 
This is required to easily reference the runtime from your project. 
Go the the directory where you downloaded the runtime, and type the following command: 
`gacutil -i Antlr4.Runtime.dll`

### Step 3: Add a reference to the ANTLR runtime in your project

We trust that you know how to do this :-).

### Step 4: Add or create a grammar file (*.g4) in your project

To avoid confusing your IDE, we suggest setting the build action to None for this file.
See the docs and the book to learn about writing lexer and parser grammars.

### Step 5: Generate the C# code

This can be done either from the cmd line, or by adding a custom pre-build command.
At minimal, the cmd line should look as follows: ``java -jar antlr4-4.5.jar -Dlanguage=CSharp grammar.g4``
This will generate the files, which you can then integrate in your project.
This is just a quick start. The tool has many useful options to control generation, please refer to its documentation. 
 
### Step 6: You're done!

Of course, the generated code is not going to meet your requirement by magic.
There are 3 ways to use the generated code:
 - by generating a parse tree, and traversing it using a listener. This is the most common method.
 - by generating a parse tree, and traversing it using a visitor. This requires the -visitor option, and is a bit more work.
 - by providing code within your grammar, which gets executed when your input files are parsed. 
While the latter works, it is no longer the recommended approach, because it is not portable, and harder to maintain. More importantly, it breaks the parsing when your code breaks.

See the web site for examples of using the generated code.
 
To learn more about ANTLR 4, read the book.
 
### Visual Studio integration

If you require tighter Visual Studio integration, you can use the tools from Tunnel Vision Labs. 
(please note however that they use a different tool and runtime)


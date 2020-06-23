# D target for ANTLR 4

This folder contains the D runtime support for ANTLR.  See [the canonical antlr4 repository](https://github.com/antlr/antlr4) for in depth detail about how to use ANTLR 4.

## Authors and major contributors

ANTLR 4 is the result of substantial effort of the following people:
 
* [Terence Parr](http://www.cs.usfca.edu/~parrt/), parrt@cs.usfca.edu
  ANTLR project lead and supreme dictator for life
  [University of San Francisco](http://www.usfca.edu/)
* [Sam Harwell](http://tunnelvisionlabs.com/) 
  Tool co-author, Java and C# target)

The D target has been the work of the following people:

* Egbert Voigt, Egbert.Voigt@gmx.de

## Project Status

* Building only Linux , but should also work for macOS, Windows, Android
* No errors and warnings
* Library linking
* All runtime tests pass.

### Build + Usage Notes

The minimum LDC (DMD should also work) version to compile the ANTLR D runtime is 1.19.0. The supplied DUB projects (dub.json) build a dynamic library. The DUB version version 1.17 should work fine also on different platforms.

#### Compiling on Windows, Android  and macOS are not tested yet

#### Compiling on Linux
- cd <antlr4-dir>/runtime/D (this is where this readme is located)
- dub [--build=release]
- DESTDIR=<antlr4-dir>/lib

#### Using reriter


The data type to store text is [Variant](https://dlang.org/phobos/std_variant.html). This is handy for the rewriting feature on an indent-based syntax like Python. In this case we need a structure representing the text as well as the number of indents.
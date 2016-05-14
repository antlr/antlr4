# C++ target for ANTLR 4

This folder contains the C++ runtime support for ANTLR.  See [the canonical antlr4 repository](https://github.com/antlr/antlr4) for in depth detail about how to use ANTLR 4.

## Authors and major contributors

ANTLR 4 is the result of substantial effort of the following people:
 
* [Terence Parr](http://www.cs.usfca.edu/~parrt/), parrt@cs.usfca.edu
  ANTLR project lead and supreme dictator for life
  [University of San Francisco](http://www.usfca.edu/)
* [Sam Harwell](http://tunnelvisionlabs.com/) 
  Tool co-author, Java and C# target)

The C++ target has been the work of the following people:

* Dan McLaughlin, dan.mclaughlin@gmail.com C++ Target project leader
* David Sisson, dsisson@google.com
* [Mike Lischke](www.soft-gems.net), mike@lischke-online.de

## Other contributors

* Marcin Szalowicz, mszalowicz@mailplus.pl (cmake build setup)

## Project Status

* Building on OS X, Windows, and Linux (all major Linux flavours)
* No errors and warnings
* Library linking
* Some unit tests in the OSX project, for important base classes with almost 100% code coverage.
* All memory allocations checked
* Simple command line demo application working on all supported platforms.

### Build notes

The minimum C++ version to compile the ANTLR C++ runtime with is C++11. The supplied projects can built the runtime either as static or dynamic library, as both 32bit and 64bit arch. The OSX project contains a target for iOS and can also be built using cmake (instead of XCode).

In order to maximize build speed and keep the runtime interface as clean as possible there are only the absolutely necessary header files included in runtime .h files. The runtime is prepared to build with precompiled headers. For platform neutrality there is no fixed header included in *.cpp files (like the typical stdafx.h on Windows), but instead a forced include is used. The precompiled header file is antlrcpp-Prefix.h and used by all build systems (Visual Studio, XCode and CMake). It includes all needed STL headers, so you won't find any STL include in the library itself. This however requires that you have to add those includes explicitly in your project which uses the ANTLR C++ runtime (something you have probably anyway already). You can do this again with a forced include or use antlrcpp-Prefix.h in your own precompile header, if you have any.

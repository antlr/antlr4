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

* Building on OS X, Windows, and Linux
* No errors and warnings
* Library linking
* Some unit tests in the OSX project, for important base classes with almost 100% code coverage.
* All memory allocations checked
* Simple command line demo application working on all supported platforms.

### Build + Usage Notes

The minimum C++ version to compile the ANTLR C++ runtime with is C++11. The supplied projects can built the runtime either as static or dynamic library, as both 32bit and 64bit arch. The OSX project contains a target for iOS and can also be built using cmake (instead of XCode).

Include the antlr4-runtime.h umbrella header in your target application to get everything needed to use the library.

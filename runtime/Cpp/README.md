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

* Dan McLaughlin, dan.mclaughlin@gmail.com (initial port, got code to compile)
* David Sisson, dsisson@google.com (initial port, made the runtime C++ tests runnable)
* [Mike Lischke](www.soft-gems.net), mike@lischke-online.de (brought the initial port to a working library, made most runtime tests passing)

## Other contributors

* Marcin Szalowicz, mszalowicz@mailplus.pl (cmake build setup)
* Tim O'Callaghan, timo@linux.com (additional superbuild cmake pattern script)

## Project Status

* Building on macOS, Windows, Android and Linux
* No errors and warnings
* Library linking
* Some unit tests in the macOS project, for important base classes with almost 100% code coverage.
* All memory allocations checked
* Simple command line demo application working on all supported platforms.
* All runtime tests pass.

### Build + Usage Notes

The minimum C++ version to compile the ANTLR C++ runtime with is C++11. The supplied projects can built the runtime either as static or dynamic library, as both 32bit and 64bit arch. The macOS project contains a target for iOS and can also be built using cmake (instead of XCode).

Include the antlr4-runtime.h umbrella header in your target application to get everything needed to use the library.

If you are compiling with cmake, the minimum version required is cmake 2.8.
By default, the libraries produced by the CMake build target C++11. If you want to target a different C++ standard, you can explicitly pass the standard - e.g. `-DCMAKE_CXX_STANDARD=17`.

#### Compiling on Windows with Visual Studio using he Visual Studio projects
Simply open the VS project from the runtime folder (VS 2013+) and build it.

#### Compiling on Windows using cmake with Visual Studio VS2017 and later
Use the "Open Folder" Feature from the File->Open->Folder menu to open the runtime/Cpp directory.
It will automatically use the CMake description to open up a Visual Studio Solution.

#### Compiling on macOS
Either open the included XCode project and build that or use the cmake compilation as described for linux.

#### Compiling on Android
Try run cmake -DCMAKE_ANDROID_NDK=/folder/of/android_ndkr17_and_above -DCMAKE_SYSTEM_NAME=Android -DCMAKE_ANDROID_API=14 -DCMAKE_ANDROID_ARCH_ABI=x86 -DCMAKE_ANDROID_STL_TYPE=c++_shared -DCMAKE_ANDROID_NDK_TOOLCHAIN_VERSION=clang -DCMAKE_BUILD_TYPE=Release /folder/antlr4_src_dir -G Ninja.

#### Compiling on Linux
- cd \<antlr4-dir\>/runtime/Cpp (this is where this readme is located)
- mkdir build && mkdir run && cd build
- cmake .. -DANTLR_JAR_LOCATION=full/path/to/antlr4-4.5.4-SNAPSHOT.jar -DWITH_DEMO=True
- make
- DESTDIR=\<antlr4-dir\>/runtime/Cpp/run make install

If you don't want to build the demo then simply run cmake without parameters.
There is another cmake script available in the subfolder cmake/ for those who prefer the superbuild cmake pattern.

#### CMake Package support
If the CMake variable 'ANTLR4_INSTALL' is set, CMake Packages will be build and installed during the install step.
They expose two packages: antlr4_runtime and antlr4_generator which can be referenced to ease up the use of the
ANTLR Generator and runtime.
Use and Sample can be found [here](cmake/Antlr4Package.md)

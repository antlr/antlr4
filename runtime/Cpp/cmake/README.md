# Using ANTLR 4 With CMake

## Prerequisites

In order for CMake to find ANTLR, it must be installed on your system. This can be accomplished in one of several ways. The simplest is to [use CPM](#cpm). If you prefer not to use CPM, you can [install ANTLR manually](#installing-antlr-locally).

TODO: Add section on installing the Antlr generator and getting CMake to recognize it

## CPM

To start building with ANTLR 4 using CPM, create a new source directory structured like this:

```
my-project/
- cmake/
  - CPM.cmake
- CMakeLists.txt
- main.cpp
- SampleGrammar.g4
```

`CPM.cmake` can be obtained from [CPM releases](https://github.com/cpm-cmake/CPM.cmake/releases).

Add the following to `CMakeLists.txt`:

```cmake
cmake_minimum_required (VERSION 3.0)
project(SampleProject)

include(cmake/CPM.cmake)

# Ask CPM to pull down and build antlr4
CPMAddPackage(
    GITHUB_REPOSITORY antlr/antlr4
    VERSION 4.11.1
    SOURCE_SUBDIR runtime/Cpp
)

# Generate parser from grammar file (and regenerated them if it changes)
antlr_target(MyLexerAndParser SampleGrammar.g4)

add_executable(Sample main.cpp)

# Build the generated files with our project
target_link_libraries(Sample PUBLIC ANTLR_MyLexerAndParser)
```

## Installing ANTLR Locally

If you prefer not to use CPM, ANTLR must be available locally. On Linux, it is available as a package. Otherwise, it can be built and installed from source.

### On Linux

`sudo apt-get install antlr4`

Or

`sudo yum install antlr4`

### Installing From Source

If you want to contribute to the project or modify the source code, or if you can't install the package as above (Windows), you will first need to clone and build the source:

1. Clone the ANTLR4 repo: `git clone https://github.com/antlr/antlr4.git`
2. Create and enter a new build directory: `cd antlr4; mkdir build; cd build`
3. Build ANTLR4 for release: `cmake ..; cmake --build . --config Release`

Finally, the built library can be installed. Be advised that, depending on the [default install location](https://cmake.org/cmake/help/v3.0/variable/CMAKE_INSTALL_PREFIX.html), you may be required to run the following command with elevated priveleges. On Windows, the default install location for CMake is `C:\Program Files`, so you will need to launch your shell as an administrator.

Note: If you change the install directory, you will have to tell CMake what additional directories to search to find your package. It is recommended to leave these as default unless your project has specific requirements.

To install, run `cmake --install . --config Release` from the build directory.

## Building From Installed ANTLR

Once you have installed ANTLR 4, CMake `find_package` will be able to find it. Your `CMakeLists.txt` file should look like this:

```cmake
cmake_minimum_required (VERSION 3.0)
project(SampleProject)

# Find ANTLR4 that we installed earlier
find_package(ANTLR4 REQUIRED)

# Generate parser from grammar file (and regenerated them if it changes)
antlr_target(MyLexerAndParser SampleGrammar.g4)

add_executable(Sample main.cpp)

# Build the generated files with our project
target_link_libraries(Sample PUBLIC ANTLR_MyLexerAndParser)
```

## What's next?

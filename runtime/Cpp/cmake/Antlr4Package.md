# CMake Antlr4 Package Usage

## The `antlr4-generator` Package

To use the Package you must insert a 
```cmake
find_package(antlr4-generator REQUIRED)
```
line in your `CMakeList.txt` file.

The package exposes a function `antlr4_generate` that generates the required setup to call ANTLR for a 
given input file during build.

The following table lists the parameters that can be used with the function:
 
Argument# | Required  | Default | Use
----------|-----------|---------|---
0 | Yes | n/a | Unique target name. It is used to generate CMake Variables to reference the various outputs of the generation
1 | Yes | n/a | Input file containing the lexer/parser definition
2 | Yes | n/a | Type of Rules contained in the input: LEXER, PARSER or BOTH
4 | No  | FALSE | Boolean to indicate if a listener interface should be generated
5 | No  | FALSE | Boolean to indicate if a visitor interface should be generated
6 | No  | none | C++ namespace in which the generated classes should be placed
7 | No  | none | Additional files on which the input depends
8 | No  | none | Library path to use during generation

The `ANTLR4_JAR_LOCATION` CMake variable must be set to the location where the `antlr-4*-complete.jar` generator is located. You can download the file from [here](http://www.antlr.org/download.html).

Additional options to the ANTLR4 generator can be passed in the `ANTLR4_GENERATED_OPTIONS` variable. Add the installation prefix of `antlr4-runtime` to `CMAKE_PREFIX_PATH` or set
 `antlr4-runtime_DIR` to a directory containing the files.

The following CMake variables are available following a call to `antlr4_generate`

Output variable  | Meaning
---|---
`ANTLR4_INCLUDE_DIR_<Target name>`       | Directory containing the generated header files
`ANTLR4_SRC_FILES_<Target name>`         | List of generated source files
`ANTLR4_TOKEN_FILES_<Target name>`       | List of generated token files
`ANTLR4_TOKEN_DIRECTORY_<Target name>`  | Directory containing the generated token files

#### Sample:
```cmake
 # generate parser with visitor classes.
 # put the classes in C++ namespace 'antlrcpptest::'
 antlr4_generate( 
   antlrcpptest_parser
   ${CMAKE_CURRENT_SOURCE_DIR}/TLexer.g4
   LEXER
   FALSE
   TRUE
   "antlrcpptest"
   )
```

**Remember that the ANTLR generator requires a working Java installation on your machine!**

## The `antlr4-runtime` Package

To use the Package you must insert a 
```cmake
find_package(antlr4-runtime REQUIRED)
```
line in your `CMakeList.txt` file.

The package exposes two different targets:

Target|Use
--|--
antlr4_shared|Shared library version of the runtime
antlr4_static|Static library version of the runtime

Both set the following CMake variables:

Output variable  | Meaning
---|---
`ANTLR4_INCLUDE_DIR` | Include directory containing the runtime header files
`ANTLR4_LIB_DIR`      | Library directory containing the runtime library files

#### Sample:
```cmake
# add runtime include directories on this project.
include_directories( ${ANTLR4_INCLUDE_DIR} )

# add runtime to project dependencies
add_dependencies( Parsertest antlr4_shared )

# add runtime to project link libraries
target_link_libraries( Parsertest PRIVATE 
                       antlr4_shared)
```

### Full Example:
```cmake
 # Bring in the required packages
 find_package(antlr4-runtime REQUIRED)
 find_package(antlr4-generator REQUIRED)
 
 # Set path to generator
 set(ANTLR4_JAR_LOCATION ${PROJECT_SOURCE_DIR}/thirdparty/antlr/antlr-4.8-complete.jar)
 
 # generate lexer
 antlr4_generate( 
   antlrcpptest_lexer
   ${CMAKE_CURRENT_SOURCE_DIR}/TLexer.g4
   LEXER
   FALSE
   FALSE
   "antlrcpptest"
   )
 
 # generate parser
 antlr4_generate( 
   antlrcpptest_parser
   ${CMAKE_CURRENT_SOURCE_DIR}/TParser.g4
   PARSER
   FALSE
   TRUE
   "antlrcpptest"
   "${ANTLR4_TOKEN_FILES_antlrcpptest_lexer}"
   "${ANTLR4_TOKEN_DIRECTORY_antlrcpptest_lexer}"
   )
 
 # add directories for generated include files
 include_directories( ${PROJECT_BINARY_DIR} ${ANTLR4_INCLUDE_DIR} ${ANTLR4_INCLUDE_DIR_antlrcpptest_lexer} ${ANTLR4_INCLUDE_DIR_antlrcpptest_parser} )
 
 # add generated source files
 add_executable( Parsertest main.cpp ${ANTLR4_SRC_FILES_antlrcpptest_lexer} ${ANTLR4_SRC_FILES_antlrcpptest_parser} )
 
 # add required runtime library
 add_dependencies( Parsertest antlr4_shared )
 
 target_link_libraries( Parsertest PRIVATE 
                        antlr4_shared)
 
```
 

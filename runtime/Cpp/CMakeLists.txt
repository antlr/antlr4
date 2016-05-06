cmake_minimum_required (VERSION 2.8)
# 2.8 needed because of ExternalProject

# Detect build type, fallback to release and throw a warning if use didn't specify any
if(NOT CMAKE_BUILD_TYPE)
  message(WARNING "Build type not set, falling back to Release mode.
 To specify build type use:
 -DCMAKE_BUILD_TYPE=<mode> where <mode> is Debug or Release.")
  set(CMAKE_BUILD_TYPE "Release" CACHE STRING
       "Choose the type of build, options are: Debug Release."
       FORCE)
endif(NOT CMAKE_BUILD_TYPE)

if(NOT WITH_DEMO)
  message(STATUS "Building without demo. To enable demo build use: -DWITH_DEMO=True")
  set(WITH_DEMO False CACHE STRING
    "Chose to build with or without demo executable"
    FORCE)
endif(NOT WITH_DEMO)

project(LIBANTLR4)

if(CMAKE_VERSION VERSION_EQUAL "3.0.0" OR
   CMAKE_VERSION VERSION_GREATER "3.0.0")
  CMAKE_POLICY(SET CMP0026 OLD)
  CMAKE_POLICY(SET CMP0045 OLD)
  CMAKE_POLICY(SET CMP0042 OLD)
  CMAKE_POLICY(SET CMP0059 OLD)
endif()

if(CMAKE_SYSTEM_NAME MATCHES "Linux")
  find_package(PkgConfig REQUIRED)
  pkg_check_modules(UUID REQUIRED uuid)
endif()
if(APPLE)
  find_library(COREFOUNDATION_LIBRARY CoreFoundation)
endif()

find_package(Java REQUIRED)

file(STRINGS "VERSION" ANTLR_VERSION)

if (NOT ANTLR_JAR_LOCATION)
  message(FATAL_ERROR "Missing antlr4.jar location. You can specify it's path using: -DANTLR_JAR_LOCATION=<path>")
else()
  get_filename_component(ANTLR_NAME ${ANTLR_JAR_LOCATION} NAME_WE)
  if(NOT EXISTS "${ANTLR_JAR_LOCATION}")
    message(FATAL_ERROR "Unable to find ${ANTLR_NAME} in ${ANTLR_JAR_LOCATION}")
  else()
    message(STATUS "Found ${ANTLR_NAME}: ${ANTLR_JAR_LOCATION}")
  endif()
endif()
set(MY_CXX_WARNING_FLAGS "  -Wall -pedantic -W")

# Initialize CXXFLAGS.
set(CMAKE_CXX_FLAGS                "${CMAKE_CXX_FLAGS} -Wall -std=c++11 ${MY_CXX_WARNING_FLAGS}")
set(CMAKE_CXX_FLAGS_DEBUG          "${CMAKE_CXX_FLAGS_DEBUG} -O0 -g -std=c++11 ${MY_CXX_WARNING_FLAGS}")
set(CMAKE_CXX_FLAGS_MINSIZEREL     "${CMAKE_CXX_FLAGS_MINSIZEREL} -Os -DNDEBUG -std=c++11 ${MY_CXX_WARNING_FLAGS}")
set(CMAKE_CXX_FLAGS_RELEASE        "${CMAKE_CXX_FLAGS_RELEASE} -O3 -DNDEBUG -std=c++11 ${MY_CXX_WARNING_FLGAS}")
set(CMAKE_CXX_FLAGS_RELWITHDEBINFO "${CMAKE_CXX_FLAGS_RELWITHDEBINFO} -O2 -g -std=c++11 ${MY_CXX_WARNING_FLAGS}")

# Compiler-specific C++11 activation.
if ("${CMAKE_CXX_COMPILER_ID}" MATCHES "GNU")
    execute_process(
        COMMAND ${CMAKE_CXX_COMPILER} -dumpversion OUTPUT_VARIABLE GCC_VERSION)
    if (NOT (GCC_VERSION VERSION_GREATER 4.7 OR GCC_VERSION VERSION_EQUAL 4.7))
        message(FATAL_ERROR "${PROJECT_NAME} requires g++ 4.7 or greater.")
    endif ()
elseif ("${CMAKE_CXX_COMPILER_ID}" MATCHES "Clang" AND APPLE)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -stdlib=libc++")
elseif ("${CMAKE_CXX_COMPILER_ID}" MATCHES "Clang" AND CMAKE_SYSTEM_NAME MATCHES "Linux")
    execute_process(
        COMMAND ${CMAKE_CXX_COMPILER} -dumpversion OUTPUT_VARIABLE CLANG_VERSION)
    if (NOT (CLANG_VERSION VERSION_GREATER 4.2.1 OR CLANG_VERSION VERSION_EQUAL 4.2.1))
        message(FATAL_ERROR "${PROJECT_NAME} requires clang 4.2.1 or greater.")
    endif ()
else ()
    message(FATAL_ERROR "Your C++ compiler does not support C++11.")
endif ()


#==== macro for pch creation
# taken from: https://cmake.org/pipermail/cmake/2006-December/012323.html
MACRO(ADD_PRECOMPILED_HEADER _targetName _input )
    GET_FILENAME_COMPONENT(_name ${_input} NAME)
    SET(_source "${_input}")
    SET(_outdir "${CMAKE_CURRENT_BINARY_DIR}/${_name}.gch")
    MAKE_DIRECTORY(${_outdir})
    SET(_output "${_outdir}/${CMAKE_BUILD_TYPE}.c++")
    STRING(TOUPPER "CMAKE_CXX_FLAGS_${CMAKE_BUILD_TYPE}" _flags_var_name)
    SET(_compiler_FLAGS ${${_flags_var_name}})
    
    GET_DIRECTORY_PROPERTY(_directory_flags INCLUDE_DIRECTORIES)
    FOREACH(item ${_directory_flags})
    LIST(APPEND _compiler_FLAGS "-I${item}")
    ENDFOREACH(item)

  GET_DIRECTORY_PROPERTY(_directory_flags DEFINITIONS)
    LIST(APPEND _compiler_FLAGS ${_directory_flags})

    SEPARATE_ARGUMENTS(_compiler_FLAGS)
    ADD_CUSTOM_COMMAND(
        OUTPUT ${_output}
        COMMAND ${CMAKE_CXX_COMPILER}
        ${_compiler_FLAGS}
        -x c++-header
        -o ${_output} ${_source}
        DEPENDS ${_source} )
    ADD_CUSTOM_TARGET(${_targetName}_gch DEPENDS ${_output})
    ADD_DEPENDENCIES(${_targetName} ${_targetName}_gch)
    #SET(CMAKE_CXX_FLAGS ${CMAKE_CXX_FLAGS} "-include ${_name} -Winvalid-pch -H")
    #SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -include ${_name} -Winvalid-pch")
    SET_TARGET_PROPERTIES(${_targetName} PROPERTIES
      COMPILE_FLAGS "-include ${_name} -Winvalid-pch"
    )
  
ENDMACRO(ADD_PRECOMPILED_HEADER)
#==== end of macro for pch creation

add_subdirectory(runtime)
if (WITH_DEMO)
 add_subdirectory(demo)
endif(WITH_DEMO)

install(FILES License.txt README.md VERSION 
        DESTINATION "share/doc/libantlr4")

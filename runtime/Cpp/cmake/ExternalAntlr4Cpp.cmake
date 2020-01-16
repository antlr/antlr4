cmake_minimum_required(VERSION 3.7)

include(ExternalProject)

set(ANTLR4_ROOT ${CMAKE_CURRENT_BINARY_DIR}/antlr4_runtime/src/antlr4_runtime)
set(ANTLR4_INCLUDE_DIRS ${ANTLR4_ROOT}/runtime/Cpp/runtime/src)
set(ANTLR4_GIT_REPOSITORY https://github.com/antlr/antlr4.git)
if(NOT DEFINED ANTLR4_TAG)
  # Set to branch name to keep library updated at the cost of needing to rebuild after 'clean'
  # Set to commit hash to keep the build stable and does not need to rebuild after 'clean'
  set(ANTLR4_TAG master)
endif()

if(${CMAKE_GENERATOR} MATCHES "Visual Studio.*")
  set(ANTLR4_OUTPUT_DIR ${ANTLR4_ROOT}/runtime/Cpp/dist/$(Configuration))
elseif(${CMAKE_GENERATOR} MATCHES "Xcode.*")
  set(ANTLR4_OUTPUT_DIR ${ANTLR4_ROOT}/runtime/Cpp/dist/$(CONFIGURATION))
else()
  set(ANTLR4_OUTPUT_DIR ${ANTLR4_ROOT}/runtime/Cpp/dist)
endif()

if(MSVC)
  set(ANTLR4_STATIC_LIBRARIES
      ${ANTLR4_OUTPUT_DIR}/antlr4-runtime-static.lib)
  set(ANTLR4_SHARED_LIBRARIES
      ${ANTLR4_OUTPUT_DIR}/antlr4-runtime.lib)
  set(ANTLR4_RUNTIME_LIBRARIES
      ${ANTLR4_OUTPUT_DIR}/antlr4-runtime.dll)
else()
  set(ANTLR4_STATIC_LIBRARIES
      ${ANTLR4_OUTPUT_DIR}/libantlr4-runtime.a)
  if(MINGW)
    set(ANTLR4_SHARED_LIBRARIES
        ${ANTLR4_OUTPUT_DIR}/libantlr4-runtime.dll.a)
    set(ANTLR4_RUNTIME_LIBRARIES
        ${ANTLR4_OUTPUT_DIR}/libantlr4-runtime.dll)
  elseif(CYGWIN)
    set(ANTLR4_SHARED_LIBRARIES
        ${ANTLR4_OUTPUT_DIR}/libantlr4-runtime.dll.a)
    set(ANTLR4_RUNTIME_LIBRARIES
        ${ANTLR4_OUTPUT_DIR}/cygantlr4-runtime-4.8.dll)
  elseif(APPLE)
    set(ANTLR4_RUNTIME_LIBRARIES
        ${ANTLR4_OUTPUT_DIR}/libantlr4-runtime.dylib)
  else()
    set(ANTLR4_RUNTIME_LIBRARIES
        ${ANTLR4_OUTPUT_DIR}/libantlr4-runtime.so)
  endif()
endif()

if(${CMAKE_GENERATOR} MATCHES ".* Makefiles")
  # This avoids
  # 'warning: jobserver unavailable: using -j1. Add '+' to parent make rule.'
  set(ANTLR4_BUILD_COMMAND $(MAKE))
elseif(${CMAKE_GENERATOR} MATCHES "Visual Studio.*")
  set(ANTLR4_BUILD_COMMAND
      ${CMAKE_COMMAND}
          --build .
          --config $(Configuration)
          --target)
elseif(${CMAKE_GENERATOR} MATCHES "Xcode.*")
  set(ANTLR4_BUILD_COMMAND
      ${CMAKE_COMMAND}
          --build .
          --config $(CONFIGURATION)
          --target)
else()
  set(ANTLR4_BUILD_COMMAND
      ${CMAKE_COMMAND}
          --build .
          --target)
endif()

if(NOT DEFINED ANTLR4_WITH_STATIC_CRT)
  set(ANTLR4_WITH_STATIC_CRT ON)
endif()

if(ANTLR4_ZIP_REPOSITORY)
  ExternalProject_Add(
      antlr4_runtime
      PREFIX antlr4_runtime
      URL ${ANTLR4_ZIP_REPOSITORY}
      DOWNLOAD_DIR ${CMAKE_CURRENT_BINARY_DIR}
      BUILD_COMMAND ""
      BUILD_IN_SOURCE 1
      SOURCE_DIR ${ANTLR4_ROOT}
      SOURCE_SUBDIR runtime/Cpp
      CMAKE_CACHE_ARGS
          -DCMAKE_BUILD_TYPE:STRING=${CMAKE_BUILD_TYPE}
          -DWITH_STATIC_CRT:BOOL=${ANTLR4_WITH_STATIC_CRT}
      INSTALL_COMMAND ""
      EXCLUDE_FROM_ALL 1)
else()
  ExternalProject_Add(
      antlr4_runtime
      PREFIX antlr4_runtime
      GIT_REPOSITORY ${ANTLR4_GIT_REPOSITORY}
      GIT_TAG ${ANTLR4_TAG}
      DOWNLOAD_DIR ${CMAKE_CURRENT_BINARY_DIR}
      BUILD_COMMAND ""
      BUILD_IN_SOURCE 1
      SOURCE_DIR ${ANTLR4_ROOT}
      SOURCE_SUBDIR runtime/Cpp
      CMAKE_CACHE_ARGS
          -DCMAKE_BUILD_TYPE:STRING=${CMAKE_BUILD_TYPE}
          -DWITH_STATIC_CRT:BOOL=${ANTLR4_WITH_STATIC_CRT}
      INSTALL_COMMAND ""
      EXCLUDE_FROM_ALL 1)
endif()

# Seperate build step as rarely people want both
set(ANTLR4_BUILD_DIR ${ANTLR4_ROOT})
if(${CMAKE_VERSION} VERSION_GREATER_EQUAL "3.14.0")
  # CMake 3.14 builds in above's SOURCE_SUBDIR when BUILD_IN_SOURCE is true
  set(ANTLR4_BUILD_DIR ${ANTLR4_ROOT}/runtime/Cpp)
endif()

ExternalProject_Add_Step(
    antlr4_runtime
    build_static
    COMMAND ${ANTLR4_BUILD_COMMAND} antlr4_static
    # Depend on target instead of step (a custom command)
    # to avoid running dependent steps concurrently
    DEPENDS antlr4_runtime
    BYPRODUCTS ${ANTLR4_STATIC_LIBRARIES}
    EXCLUDE_FROM_MAIN 1
    WORKING_DIRECTORY ${ANTLR4_BUILD_DIR})
ExternalProject_Add_StepTargets(antlr4_runtime build_static)

add_library(antlr4_static STATIC IMPORTED)
add_dependencies(antlr4_static antlr4_runtime-build_static)
set_target_properties(antlr4_static PROPERTIES
                      IMPORTED_LOCATION ${ANTLR4_STATIC_LIBRARIES})

ExternalProject_Add_Step(
    antlr4_runtime
    build_shared
    COMMAND ${ANTLR4_BUILD_COMMAND} antlr4_shared
    # Depend on target instead of step (a custom command)
    # to avoid running dependent steps concurrently
    DEPENDS antlr4_runtime
    BYPRODUCTS ${ANTLR4_SHARED_LIBRARIES} ${ANTLR4_RUNTIME_LIBRARIES}
    EXCLUDE_FROM_MAIN 1
    WORKING_DIRECTORY ${ANTLR4_BUILD_DIR})
ExternalProject_Add_StepTargets(antlr4_runtime build_shared)

add_library(antlr4_shared SHARED IMPORTED)
add_dependencies(antlr4_shared antlr4_runtime-build_shared)
set_target_properties(antlr4_shared PROPERTIES
                      IMPORTED_LOCATION ${ANTLR4_RUNTIME_LIBRARIES})
if(ANTLR4_SHARED_LIBRARIES)
  set_target_properties(antlr4_shared PROPERTIES
                        IMPORTED_IMPLIB ${ANTLR4_SHARED_LIBRARIES})
endif()

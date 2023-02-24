file(GLOB ANTLR4_SOURCES
  "${CMAKE_CURRENT_SOURCE_DIR}/src/*.cpp"
  "${CMAKE_CURRENT_SOURCE_DIR}/src/atn/*.cpp"
  "${CMAKE_CURRENT_SOURCE_DIR}/src/dfa/*.cpp"
  "${CMAKE_CURRENT_SOURCE_DIR}/src/internal/*.cpp"
  "${CMAKE_CURRENT_SOURCE_DIR}/src/misc/*.cpp"
  "${CMAKE_CURRENT_SOURCE_DIR}/src/support/*.cpp"
  "${CMAKE_CURRENT_SOURCE_DIR}/src/tree/*.cpp"
  "${CMAKE_CURRENT_SOURCE_DIR}/src/tree/pattern/*.cpp"
  "${CMAKE_CURRENT_SOURCE_DIR}/src/tree/xpath/*.cpp"
)

add_library(runtime STATIC ${ANTLR4_SOURCES})
add_library(antlr4::runtime ALIAS runtime)

target_compile_definitions(runtime
    PUBLIC
        ANTLR4CPP_STATIC
)

target_include_directories(runtime
    PUBLIC
        ${PROJECT_SOURCE_DIR}/runtime/src
        ${PROJECT_SOURCE_DIR}/runtime/src/atn
        ${PROJECT_SOURCE_DIR}/runtime/src/dfa
        ${PROJECT_SOURCE_DIR}/runtime/src/internal
        ${PROJECT_SOURCE_DIR}/runtime/src/misc
        ${PROJECT_SOURCE_DIR}/runtime/src/support
        ${PROJECT_SOURCE_DIR}/runtime/src/tree
        ${PROJECT_SOURCE_DIR}/runtime/src/tree/pattern
        ${PROJECT_SOURCE_DIR}/runtime/src/tree/xpath
)

macro(ANTLR_TARGET Name InputFile)
    set(ANTLR_OPTIONS LEXER PARSER LISTENER VISITOR)
    set(ANTLR_ONE_VALUE_ARGS OUTPUT_DIRECTORY)
    set(ANTLR_MULTI_VALUE_ARGS COMPILE_FLAGS DEPENDS)

    cmake_parse_arguments(ANTLR_TARGET
                          "${ANTLR_OPTIONS}"
                          "${ANTLR_ONE_VALUE_ARGS}"
                          "${ANTLR_MULTI_VALUE_ARGS}"
                          ${ARGN})

    # Some terms:
    # - ${Name} is the target name and should be added as a dependency (via target_link_libraries) for your project
    # - ${ANTLR_${Name}_INPUT} is your grammar file: Grammar.g4
    # - ${ANTLR_INPUT} is the grammar name: Grammar
    # - ${ANTLR_${Name}_OUTPUT_DIR} is where the generated sources will go; this should usually be in your CMake build directory

    set(ANTLR_${Name}_INPUT ${InputFile})

    get_filename_component(ANTLR_INPUT ${InputFile} NAME_WE)

    if(ANTLR_TARGET_OUTPUT_DIRECTORY)
        set(ANTLR_${Name}_OUTPUT_DIR ${ANTLR_TARGET_OUTPUT_DIRECTORY})
    else()
        set(ANTLR_${Name}_OUTPUT_DIR ${CMAKE_CURRENT_BINARY_DIR}/antlr4cpp_generated_src/${ANTLR_INPUT})
    endif()

    if((ANTLR_TARGET_LEXER AND NOT ANTLR_TARGET_PARSER) OR (ANTLR_TARGET_PARSER AND NOT ANTLR_TARGET_LEXER))
        list(APPEND ANTLR_${Name}_CXX_OUTPUTS
            ${ANTLR_${Name}_OUTPUT_DIR}/${ANTLR_INPUT}.h
            ${ANTLR_${Name}_OUTPUT_DIR}/${ANTLR_INPUT}.cpp)
        set(ANTLR_${Name}_OUTPUTS
            ${ANTLR_${Name}_OUTPUT_DIR}/${ANTLR_INPUT}.interp
            ${ANTLR_${Name}_OUTPUT_DIR}/${ANTLR_INPUT}.tokens)
    else()
        list(APPEND ANTLR_${Name}_CXX_OUTPUTS
            ${ANTLR_${Name}_OUTPUT_DIR}/${ANTLR_INPUT}Lexer.h
            ${ANTLR_${Name}_OUTPUT_DIR}/${ANTLR_INPUT}Lexer.cpp
            ${ANTLR_${Name}_OUTPUT_DIR}/${ANTLR_INPUT}Parser.h
            ${ANTLR_${Name}_OUTPUT_DIR}/${ANTLR_INPUT}Parser.cpp)
        list(APPEND ANTLR_${Name}_OUTPUTS
            ${ANTLR_${Name}_OUTPUT_DIR}/${ANTLR_INPUT}Lexer.interp
            ${ANTLR_${Name}_OUTPUT_DIR}/${ANTLR_INPUT}Lexer.tokens)
    endif()

    if(ANTLR_TARGET_LISTENER)
        list(APPEND ANTLR_${Name}_CXX_OUTPUTS
            ${ANTLR_${Name}_OUTPUT_DIR}/${ANTLR_INPUT}BaseListener.h
            ${ANTLR_${Name}_OUTPUT_DIR}/${ANTLR_INPUT}BaseListener.cpp
            ${ANTLR_${Name}_OUTPUT_DIR}/${ANTLR_INPUT}Listener.h
            ${ANTLR_${Name}_OUTPUT_DIR}/${ANTLR_INPUT}Listener.cpp)
        list(APPEND ANTLR_TARGET_COMPILE_FLAGS -listener)
    endif()

    if(ANTLR_TARGET_VISITOR)
        list(APPEND ANTLR_${Name}_CXX_OUTPUTS
            ${ANTLR_${Name}_OUTPUT_DIR}/${ANTLR_INPUT}BaseVisitor.h
            ${ANTLR_${Name}_OUTPUT_DIR}/${ANTLR_INPUT}BaseVisitor.cpp
            ${ANTLR_${Name}_OUTPUT_DIR}/${ANTLR_INPUT}Visitor.h
            ${ANTLR_${Name}_OUTPUT_DIR}/${ANTLR_INPUT}Visitor.cpp)
        list(APPEND ANTLR_TARGET_COMPILE_FLAGS -visitor)
    endif()

    add_custom_command(
        OUTPUT ${ANTLR_${Name}_OUTPUTS} ${ANTLR_${Name}_CXX_OUTPUTS}
        COMMAND java -jar c:/dev/antlr-4.11.1-complete.jar
                ${InputFile}
                -o ${ANTLR_${Name}_OUTPUT_DIR}
                -no-listener
                -Dlanguage=Cpp
                ${ANTLR_TARGET_COMPILE_FLAGS}
        DEPENDS ${InputFile}
        WORKING_DIRECTORY ${CMAKE_CURRENT_LIST_DIR}
    )

    add_library(ANTLR_${Name} INTERFACE)
    target_sources(ANTLR_${Name} INTERFACE ${ANTLR_${Name}_CXX_OUTPUTS})
    target_link_libraries(ANTLR_${Name} INTERFACE antlr4::runtime)
    target_include_directories(ANTLR_${Name} INTERFACE ${ANTLR_${Name}_OUTPUT_DIR})
endmacro(ANTLR_TARGET)

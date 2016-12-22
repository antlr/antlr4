include_directories(
  ${PROJECT_SOURCE_DIR}/runtime/src
  ${PROJECT_SOURCE_DIR}/runtime/src/atn
  ${PROJECT_SOURCE_DIR}/runtime/src/dfa
  ${PROJECT_SOURCE_DIR}/runtime/src/misc
  ${PROJECT_SOURCE_DIR}/runtime/src/support
  ${PROJECT_SOURCE_DIR}/runtime/src/tree
  ${PROJECT_SOURCE_DIR}/runtime/src/tree/pattern
  ${PROJECT_SOURCE_DIR}/runtime/src/tree/xpath
)


file(GLOB libantlrcpp_SRC
  "${PROJECT_SOURCE_DIR}/runtime/src/*.cpp"
  "${PROJECT_SOURCE_DIR}/runtime/src/atn/*.cpp"
  "${PROJECT_SOURCE_DIR}/runtime/src/dfa/*.cpp"
  "${PROJECT_SOURCE_DIR}/runtime/src/misc/*.cpp"
  "${PROJECT_SOURCE_DIR}/runtime/src/support/*.cpp"
  "${PROJECT_SOURCE_DIR}/runtime/src/tree/*.cpp"
  "${PROJECT_SOURCE_DIR}/runtime/src/tree/pattern/*.cpp"
  "${PROJECT_SOURCE_DIR}/runtime/src/tree/xpath/*.cpp"
)

add_library(antlr4_shared SHARED ${libantlrcpp_SRC})
add_library(antlr4_static STATIC ${libantlrcpp_SRC})

set(LIB_OUTPUT_DIR "${CMAKE_HOME_DIRECTORY}/dist") # put generated libraries here.
message(STATUS "Output libraries to ${LIB_OUTPUT_DIR}")

# make sure 'make' works fine even if ${LIB_OUTPUT_DIR} is deleted.
add_custom_target(make_lib_output_dir ALL
    COMMAND ${CMAKE_COMMAND} -E make_directory ${LIB_OUTPUT_DIR}
    )

add_dependencies(antlr4_shared make_lib_output_dir)
add_dependencies(antlr4_static make_lib_output_dir)

if(CMAKE_SYSTEM_NAME MATCHES "Linux")
  target_link_libraries(antlr4_shared ${UUID_LIBRARIES})
  target_link_libraries(antlr4_static ${UUID_LIBRARIES})
elseif(APPLE)
  target_link_libraries(antlr4_shared ${COREFOUNDATION_LIBRARY})
  target_link_libraries(antlr4_static ${COREFOUNDATION_LIBRARY})
endif()

set(disabled_compile_warnings "-Wno-overloaded-virtual")
if ("${CMAKE_CXX_COMPILER_ID}" MATCHES "Clang")
  set(disabled_compile_warnings "${disabled_compile_warnings} -Wno-dollar-in-identifier-extension -Wno-four-char-constants")
elseif("${CMAKE_CXX_COMPILER_ID}" MATCHES "GNU")
  set(disabled_compile_warnings "${disabled_compile_warnings} -Wno-multichar")
endif()

  
set_target_properties(antlr4_shared
                      PROPERTIES VERSION   ${ANTLR_VERSION}
                                 SOVERSION ${ANTLR_VERSION}
                                 OUTPUT_NAME antlr4-runtime
                                 LIBRARY_OUTPUT_DIRECTORY ${LIB_OUTPUT_DIR}
                                 # TODO: test in windows. DLL is treated as runtime.
                                 # see https://cmake.org/cmake/help/v3.0/prop_tgt/LIBRARY_OUTPUT_DIRECTORY.html
                                 RUNTIME_OUTPUT_DIRECTORY ${LIB_OUTPUT_DIR}
                                 COMPILE_FLAGS "${disabled_compile_warnings}")

set_target_properties(antlr4_static
                      PROPERTIES VERSION   ${ANTLR_VERSION}
                                 SOVERSION ${ANTLR_VERSION}
                                 OUTPUT_NAME antlr4-runtime
                                 ARCHIVE_OUTPUT_DIRECTORY ${LIB_OUTPUT_DIR}
                                 COMPILE_FLAGS "${disabled_compile_warnings}")

install(TARGETS antlr4_shared
        DESTINATION lib)
install(TARGETS antlr4_static
        ARCHIVE DESTINATION lib)

install(DIRECTORY "${PROJECT_SOURCE_DIR}/runtime/src/" 
        DESTINATION "include/antlr4-runtime"
        COMPONENT dev 
        FILES_MATCHING PATTERN "*.h"
        )




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
)

add_library(antlr4_shared SHARED ${libantlrcpp_SRC})
add_library(antlr4_static STATIC ${libantlrcpp_SRC})
add_precompiled_header(antlr4_shared ${PROJECT_SOURCE_DIR}/runtime/src/antlrcpp-Prefix.h)
add_precompiled_header(antlr4_static ${PROJECT_SOURCE_DIR}/runtime/src/antlrcpp-Prefix.h)

if(CMAKE_SYSTEM_NAME MATCHES "Linux")
  target_link_libraries(antlr4_shared ${UUID_LIBRARIES})
  target_link_libraries(antlr4_static ${UUID_LIBRARIES})
elseif(APPLE)
#  target_link_libraries(antlr4 "-framework CoreFoundation")
  target_link_libraries(antlr4_shared ${COREFOUNDATION_LIBRARY})
  target_link_libraries(antlr4_static ${COREFOUNDATION_LIBRARY})

endif()

set_target_properties(antlr4_shared
                      PROPERTIES VERSION   ${ANTLR_VERSION}
                                 SOVERSION ${ANTLR_VERSION}
                                 OUTPUT_NAME antlr4)

set_target_properties(antlr4_static
                      PROPERTIES VERSION   ${ANTLR_VERSION}
                                 SOVERSION ${ANTLR_VERSION}
                                 OUTPUT_NAME antlr4)

install(TARGETS antlr4_shared
        DESTINATION lib)
install(TARGETS antlr4_static
        ARCHIVE DESTINATION lib)

install(DIRECTORY "${PROJECT_SOURCE_DIR}/runtime/src/" 
        DESTINATION "include" 
        COMPONENT dev 
        FILES_MATCHING PATTERN "*.h"
        )




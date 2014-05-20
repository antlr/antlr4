package org.antlr.v4.runtime;

public class RuntimeMetaData {
    /** Must match version of tool that generated recognizers */
    public static final String VERSION = "4.2"; // use just "x.y" and don't include bug fix release number

    /** As parser or lexer class is loaded, it checks that the version used to generate the code
     *  is compatible with the runtime version. ANTLR tool generates recognizers with a hardcoded string created by
     *  the tool during code gen. That version is passed to checkVersion().
     */
    public static void checkVersion(String toolVersion) {
        if ( !VERSION.equals(toolVersion) ) {
            System.err.println("ANTLR runtime and generated code versions disagree: "+VERSION+"!="+toolVersion);
        }
    }
}

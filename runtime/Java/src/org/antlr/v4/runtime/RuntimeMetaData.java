package org.antlr.v4.runtime;

/** Because targets can be updated at different times than the core tool, which includes this Java target,
 *  we created this runtime information object.  The goal is to test compatibility of the generated parser and
 *  the runtime library for that target it executes with.
 *
 *  Targets generate a tiny bit of code that executes upon loading of
 *  a parser or lexer, which should check its version against what is contained in this object. For example,
 *  here is something from the Java templates:
 *
 *    public class <parser.name> extends <superClass> {
 *        static { RuntimeMetaData.checkVersion("<file.ANTLRVersion>"); }
 *        ...
 *
 *  This way, previous versions of target X that are incompatible (e.g., 4.1 and 4.2) will result in
 *  a warning emitted through the appropriate channel for that target.  This can be a msg to stderr or
 *  an exception; it's up to the target developer. I have decided to throw an exception for Java target
 *  as a message might not be seen if the parser were embedded in a server or something.
 */
public class RuntimeMetaData {
    public static class ANTLRVersionMismatchException extends RuntimeException {
        public String generatedCodeVersion;
        public String runtimeLibVersion;

        public ANTLRVersionMismatchException(String message, String generatedCodeVersion, String runtimeLibVersion) {
            super(message);
            this.generatedCodeVersion = generatedCodeVersion;
            this.runtimeLibVersion = runtimeLibVersion;
        }

        @Override
        public String getMessage() {
            return super.getMessage()+": code version "+generatedCodeVersion+" != runtime version "+runtimeLibVersion;
        }
    }

    /** Must match version of tool that generated recognizers */
    public static final String VERSION = "4.3"; // use just "x.y" and don't include bug fix release number

    /** As parser or lexer class is loaded, it checks that the version used to generate the code
     *  is compatible with the runtime version. ANTLR tool generates recognizers with a hardcoded string created by
     *  the tool during code gen. That version is passed to checkVersion().
     */
    public static void checkVersion(String toolVersion) {
        // I believe that 4.2-generated parsers are compatible with the runtime for 4.3 so no exception in this case.
        // Technically, we don't need this check because 4.2 it doesn't actually check the version. ;) I am just
        // being explicit. Later we can always build a more sophisticated versioning check.
        if ( (toolVersion.startsWith("4.2.") || toolVersion.equals("4.2")) ) {
            return;
        }
        if ( !VERSION.equals(toolVersion) ) {
            throw new ANTLRVersionMismatchException("ANTLR runtime and generated code versions disagree",toolVersion,VERSION);
        }
    }
}

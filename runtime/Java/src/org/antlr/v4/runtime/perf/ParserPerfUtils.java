package org.antlr.v4.runtime.perf;

import org.antlr.v4.runtime.Parser;

/**
 * Utility class to improve parser performance by disabling parse tree construction.
 */
public class ParserPerfUtils {
    public static void disableTreeBuilding(Parser parser) {
        parser.setBuildParseTree(false);
    }
}

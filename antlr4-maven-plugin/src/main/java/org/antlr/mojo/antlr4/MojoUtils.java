package org.antlr.mojo.antlr4;

import java.io.File;


class MojoUtils {
    /**
     * Given the source directory File object and the full PATH to a grammar, produce the
     * path to the named grammar file in relative terms to the {@code sourceDirectory}.
     * This will then allow ANTLR to produce output relative to the base of the output
     * directory and reflect the input organization of the grammar files.
     *
     * @param   sourceDirectory  The source directory {@link File} object
     * @param   grammarFileName  The full path to the input grammar file
     *
     * @return  The path to the grammar file relative to the source directory
     */
    public static String findSourceSubdir(File sourceDirectory, File grammarFile) {
        String srcPath = sourceDirectory.getPath() + File.separator;
        String path = grammarFile.getPath();

        if (!path.startsWith(srcPath)) {
            throw new IllegalArgumentException("expected " + path +
                " to be prefixed with " + sourceDirectory);
        }

        File unprefixedGrammarFileName = new File(path.substring(srcPath.length()));

        if (unprefixedGrammarFileName.getParent() == null) {
            return "";
        }

        return unprefixedGrammarFileName.getParent() + File.separator;
    }
}

/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.mojo.antlr4;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


class MojoUtils {
    /**
     * Creates the MD5 checksum for the given file.
     *
     * @param   file  the file.
     *
     * @return  the checksum.
     */
    public static byte[] checksum(File file) throws IOException {
        try {
            InputStream in = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            MessageDigest complete = MessageDigest.getInstance("MD5");

            try {
                int n;

                do {
                    n = in.read(buffer);

                    if (n > 0) {
                        complete.update(buffer, 0, n);
                    }
                } while (n != -1);
            } finally {
                in.close();
            }

            return complete.digest();
        } catch (NoSuchAlgorithmException ex) {
            throw new IOException("Could not create checksum " + file, ex);
        }
    }

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

/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.mojo.antlr4;

import org.antlr.runtime.tree.Tree;
import org.antlr.v4.Tool;
import org.antlr.v4.misc.Graph;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.GrammarRootAST;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


class GrammarDependencies {
    private final Graph<String> graph = new Graph<String>();
    private final File sourceDirectory;
    private final File libDirectory;
    private final File statusFile;
    private final String packageName;

    /** Map grammars to their checksum and references. */
    private final Map<File, Map.Entry<byte[], Collection<String>>> grammars;
    private final Log log;

    public GrammarDependencies(File sourceDirectory, File libDirectory,
        List<String> arguments, File status, Log log) {
        this.log = log;
        this.sourceDirectory = sourceDirectory;
        this.libDirectory = libDirectory;
        this.statusFile = status;
        this.grammars = loadStatus(status);
        this.packageName = getPackage(arguments);
    }

    /**
     * Determines the package to use.
     *
     * @param   arguments  the tool arguments.
     *
     * @return  the package. Returns {@code null} to indicate that no package should be
     *          used.
     */
    private String getPackage(List<String> arguments) {
        int index = (arguments != null) ? arguments.indexOf("-package") : -1;

        return (index > -1)
            ? (arguments.get(index + 1).replace('.', File.separatorChar) +
                File.separatorChar)
            : null;
    }

    public void save() throws IOException {
        if (!grammars.isEmpty()) {
            log.debug("Persisting grammars dependency status: " + statusFile);

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                        statusFile));

            try {
                out.writeObject(grammars);
            } finally {
                out.close();
            }
        }
    }

    /**
     * Performs dependency analysis for the given grammar files.
     *
     * @param   grammarFiles        the grammar files.
     * @param   importGrammarFiles  the import grammar files.
     * @param   tool                the tool to use.
     *
     * @return  self-reference.
     */
    public GrammarDependencies analyze(Set<File> grammarFiles,
        Set<File> importGrammarFiles, Tool tool) throws IOException {
        log.debug("Analysing grammar dependencies " + sourceDirectory);

        // for dependency analysis we require all grammars
        Collection<File> grammarsAndTokens = new HashSet<File>();
        grammarsAndTokens.addAll(importGrammarFiles);
        grammarsAndTokens.addAll(grammarFiles);

        for (File grammarFile : grammarsAndTokens) {
            // .tokens files must not be parsed, they can just be referenced
            if (!grammarFile.getName().endsWith(".tokens"))
                analyse(grammarFile, grammarsAndTokens, tool);
        }

        for (File grammarFile : grammarFiles) {
            Collection<String> usages = findUsages(getRelativePath(grammarFile));

            if (!usages.isEmpty()) {
                grammars.put(grammarFile,
                    new AbstractMap.SimpleImmutableEntry<byte[], Collection<String>>(
                        MojoUtils.checksum(grammarFile), usages));

                log.debug("  " + getRelativePath(grammarFile) + " used by " + usages);
            }
        }

        for (File grammarFile : importGrammarFiles) {
            // imported files are not allowed to be qualified
            Collection<String> usages = findUsages(grammarFile.getName());

            if (!usages.isEmpty()) {
                grammars.put(grammarFile,
                    new AbstractMap.SimpleImmutableEntry<byte[], Collection<String>>(
                        MojoUtils.checksum(grammarFile), usages));

                log.debug("  " + grammarFile.getName() + " imported by " + usages);
            }
        }

        return this;
    }


    /**
     * Determines whether a grammar used by the given grammar was modified since the last
     * build.
     *
     * @param   grammarFile  the grammar.
     *
     * @return  {@code true} if a grammar used by the given grammar has been modified.
     */
    public boolean isDependencyChanged(File grammarFile) throws IOException {
        String grammarPath = getRelativePath(grammarFile);

        for (Map.Entry<File, Map.Entry<byte[], Collection<String>>> e : grammars.entrySet()) {
            File depGrammarFile = e.getKey();
            byte[] checksum = e.getValue().getKey();
            Collection<String> usages = e.getValue().getValue();

            if (usages.contains(grammarPath)) {
                if (!depGrammarFile.exists() || !Arrays.equals(MojoUtils.checksum(depGrammarFile), checksum)) {
                    log.debug("  " + grammarPath + ": dependency " +
                        depGrammarFile.getName() + " changed");

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines the relative target path of the given grammar file.
     *
     * @param   grammarFile  the grammar file.
     *
     * @return  the relative path.
     */
    private String getRelativePath(File grammarFile) {
        // the library directory does not allow sub-directories
        if (grammarFile.getPath().startsWith(libDirectory.getPath()))
            return grammarFile.getName();

        // if a package is given, we have to use it
        if (packageName != null)
            return packageName + grammarFile.getName();

        // otherwise resolve the path relative to the source directory
        String path = MojoUtils.findSourceSubdir(sourceDirectory, grammarFile);

        return path + grammarFile.getName();
    }

    /**
     * Returns the grammar file names that directly or indirectly use the given grammar.
     *
     * @param   grammarFileName  the grammar file name.
     *
     * @return  the grammar file names that use the given grammar file.
     */
    private Collection<String> findUsages(String grammarFileName) {
        Collection<String> result = new ArrayList<String>();
        explore(grammarFileName, result);

        return result;
    }

    private void explore(String grammarName, Collection<String> result) {
        for (Graph.Node<String> node : graph.getNode(grammarName).edges) {
            result.add(node.payload);
            explore(node.payload, result);
        }
    }

    private void analyse(File grammarFile, Collection<File> grammarFiles, Tool tool) {
        GrammarRootAST grammar = tool.parseGrammar(grammarFile.getAbsolutePath());

        if (grammar == null)
            return;

        for (GrammarAST importDecl : grammar.getAllChildrenWithType(ANTLRParser.IMPORT)) {
            for (Tree id: importDecl.getAllChildrenWithType(ANTLRParser.ID)) {
                // missing id is not valid, but we don't want to prevent the root cause from
                // being reported by the ANTLR tool
                if (id != null) {
                    String grammarPath = getRelativePath(grammarFile);

                    graph.addEdge(id.getText() + ".g4", grammarPath);
                }
            }
        }

        for (GrammarAST options : grammar.getAllChildrenWithType(ANTLRParser.OPTIONS)) {
            for (int i = 0, count = options.getChildCount(); i < count; i++) {
                Tree option = options.getChild(i);

                if (option.getType() == ANTLRParser.ASSIGN) {
                    String key = option.getChild(0).getText();
                    String value = option.getChild(1).getText();

                    if ("tokenVocab".equals(key)) {
                        String name = stripQuotes(value);
                        // the grammar name may be qualified, but we resolve the path anyway
                        String grammarName = stripPath(name);
                        String grammarPath = MojoUtils.findSourceSubdir(sourceDirectory,
                                grammarFile);
                        File depGrammarFile = resolve(grammarName, grammarPath);

                        // if a package has been given, we use it instead of the file directory path
                        // (files probably reside in the root directory anyway with such a configuration )
                        if (packageName != null)
                            grammarPath = packageName;

                        graph.addEdge(getRelativePath(depGrammarFile),
                            grammarPath + grammarFile.getName());
                    }
                }
            }
        }
    }

    /**
     * Resolves the given grammar name.
     *
     * @param   name  the name.
     * @param   path  the relative path.
     *
     * @return  the grammar file.
     */
    private File resolve(String name, String path) {
        File file = new File(sourceDirectory, path + name + ".g4");

        if (file.exists())
            return file;

        file = new File(libDirectory, name + ".g4");

        if (file.exists())
            return file;

        return new File(libDirectory, name + ".tokens");
    }

    private Map<File, Map.Entry<byte[], Collection<String>>> loadStatus(File statusFile) {
        if (statusFile.exists()) {
            log.debug("Load grammars dependency status: " + statusFile);

            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(
                            statusFile));

                try {
                    @SuppressWarnings("unchecked")
                    Map<File, Map.Entry<byte[], Collection<String>>> data =
                        (Map<File, Map.Entry<byte[], Collection<String>>>)
                        in.readObject();

                    return data;
                } finally {
                    in.close();
                }
            } catch (Exception ex) {
                log.warn("Could not load grammar dependency status information", ex);
            }
        }

        return new HashMap<File, Map.Entry<byte[], Collection<String>>>();
    }

    private String stripPath(String str) {
        return str.replaceAll("^.*[/\\\\]", "");
    }

    private String stripQuotes(String str) {
        return str.replaceAll("\\A'|'\\Z", "");
    }
}

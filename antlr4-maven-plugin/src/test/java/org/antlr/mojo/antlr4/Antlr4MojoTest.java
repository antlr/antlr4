/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.mojo.antlr4;

import io.takari.maven.testing.TestMavenRuntime;
import io.takari.maven.testing.TestResources;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class Antlr4MojoTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public final TestResources resources = new TestResources();

    @Rule
    public final TestMavenRuntime maven = new TestMavenRuntime();

    @Test
    public void importTokens() throws Exception {
        Path baseDir = resources.getBasedir("importTokens").toPath();
        Path antlrDir = baseDir.resolve("src/main/antlr4");
        Path generatedSources = baseDir.resolve("target/generated-sources/antlr4");

        Path genParser = generatedSources.resolve("test/SimpleParser.java");
        Path tokens = antlrDir.resolve("imports/SimpleLexer.tokens");

        MavenProject project = maven.readMavenProject(baseDir.toFile());
        MavenSession session = maven.newMavenSession(project);
        MojoExecution exec = maven.newMojoExecution("antlr4");

        ////////////////////////////////////////////////////////////////////////
        // 1st - all grammars have to be processed
        ////////////////////////////////////////////////////////////////////////

        assertFalse(Files.exists(genParser));

        maven.executeMojo(session, project, exec);

        assertTrue(Files.exists(genParser));

        ////////////////////////////////////////////////////////////////////////
        // 2nd - nothing has been modified, no grammars have to be processed
        ////////////////////////////////////////////////////////////////////////

        {
            byte[] sum = checksum(genParser);

            maven.executeMojo(session, project, exec);

            assertTrue(Arrays.equals(sum, checksum(genParser)));
        }

        ////////////////////////////////////////////////////////////////////////
        // 3rd - the imported grammar changed, every dependency has to be processed
        ////////////////////////////////////////////////////////////////////////

        try(Change change = Change.of(tokens, "DOT=4")) {
            byte[] sum = checksum(genParser);

            maven.executeMojo(session, project, exec);

            assertFalse(Arrays.equals(sum, checksum(genParser)));
        }
    }

    @Test
    public void importsCustomLayout() throws Exception {
        Path baseDir = resources.getBasedir("importsCustom").toPath();
        Path antlrDir = baseDir.resolve("src/main/antlr4");
        Path generatedSources = baseDir.resolve("src/main/java");

        Path genTestLexer = generatedSources.resolve("foo/TestLexer.java");
        Path genTestParser = generatedSources.resolve("foo/TestParser.java");
        Path genHello = generatedSources.resolve("foo/HelloParser.java");

        Path baseGrammar = antlrDir.resolve("imports/TestBaseLexer.g4");
        Path lexerGrammar = antlrDir.resolve("TestLexer.g4");
        Path parserGrammar = antlrDir.resolve("TestParser.g4");

        Xpp3Dom outputDirectory = TestMavenRuntime.newParameter("outputDirectory",
                "src/main/java/foo");
        Xpp3Dom arguments = new Xpp3Dom("arguments");
        arguments.addChild(TestMavenRuntime.newParameter("argument", "-package"));
        arguments.addChild(TestMavenRuntime.newParameter("argument", "foo"));

        MavenProject project = maven.readMavenProject(baseDir.toFile());
        MavenSession session = maven.newMavenSession(project);
        MojoExecution exec = maven.newMojoExecution("antlr4", outputDirectory, arguments);

        ////////////////////////////////////////////////////////////////////////
        // 1st - all grammars have to be processed
        ////////////////////////////////////////////////////////////////////////

        assertFalse(Files.exists(genHello));
        assertFalse(Files.exists(genTestParser));
        assertFalse(Files.exists(genTestLexer));

        maven.executeMojo(session, project, exec);

        assertTrue(Files.exists(genHello));
        assertTrue(Files.exists(genTestParser));
        assertTrue(Files.exists(genTestLexer));

        ////////////////////////////////////////////////////////////////////////
        // 2nd - nothing has been modified, no grammars have to be processed
        ////////////////////////////////////////////////////////////////////////

        {
            byte[] testLexerSum = checksum(genTestLexer);
            byte[] testParserSum = checksum(genTestParser);
            byte[] helloSum = checksum(genHello);

            maven.executeMojo(session, project, exec);

            assertTrue(Arrays.equals(testLexerSum, checksum(genTestLexer)));
            assertTrue(Arrays.equals(testParserSum, checksum(genTestParser)));
            assertTrue(Arrays.equals(helloSum, checksum(genHello)));
        }

        ////////////////////////////////////////////////////////////////////////
        // 3rd - the imported grammar changed, every dependency has to be processed
        ////////////////////////////////////////////////////////////////////////

        // modify the grammar to make checksum comparison detect a change
        try(Change change = Change.of(baseGrammar, "DOT: '.' ;")) {
            byte[] testLexerSum = checksum(genTestLexer);
            byte[] testParserSum = checksum(genTestParser);
            byte[] helloSum = checksum(genHello);

            maven.executeMojo(session, project, exec);

            assertFalse(Arrays.equals(testLexerSum, checksum(genTestLexer)));
            assertFalse(Arrays.equals(testParserSum, checksum(genTestParser)));
            assertTrue(Arrays.equals(helloSum, checksum(genHello)));
        }

        ////////////////////////////////////////////////////////////////////////
        // 4th - the lexer grammar changed, the parser grammar has to be processed as well
        ////////////////////////////////////////////////////////////////////////

        // modify the grammar to make checksum comparison detect a change
        try(Change change = Change.of(lexerGrammar, "fragment DOT : '.';")) {
            byte[] testLexerSum = checksum(genTestLexer);
            byte[] testParserSum = checksum(genTestParser);
            byte[] helloSum = checksum(genHello);

            maven.executeMojo(session, project, exec);

            assertFalse(Arrays.equals(testLexerSum, checksum(genTestLexer)));
            assertFalse(Arrays.equals(testParserSum, checksum(genTestParser)));
            assertTrue(Arrays.equals(helloSum, checksum(genHello)));
        }

        ////////////////////////////////////////////////////////////////////////
        // 5th - the parser grammar changed, no other grammars have to be processed
        ////////////////////////////////////////////////////////////////////////

        // modify the grammar to make checksum comparison detect a change
        try(Change change = Change.of(parserGrammar, " t : WS* ;")) {
            byte[] testLexerSum = checksum(genTestLexer);
            byte[] testParserSum = checksum(genTestParser);
            byte[] helloSum = checksum(genHello);

            maven.executeMojo(session, project, exec);

            assertTrue(Arrays.equals(testLexerSum, checksum(genTestLexer)));
            assertFalse(Arrays.equals(testParserSum, checksum(genTestParser)));
            assertTrue(Arrays.equals(helloSum, checksum(genHello)));
        }
    }

    @Test
    public void importsStandardLayout() throws Exception {
        Path baseDir = resources.getBasedir("importsStandard").toPath();
        Path antlrDir = baseDir.resolve("src/main/antlr4");
        Path generatedSources = baseDir.resolve("target/generated-sources/antlr4");

        Path genTestLexer = generatedSources.resolve("test/TestLexer.java");
        Path genTestParser = generatedSources.resolve("test/TestParser.java");
        Path genHello = generatedSources.resolve("test/HelloParser.java");

        Path baseGrammar = antlrDir.resolve("imports/TestBaseLexer.g4");
        Path baseGrammar2 = antlrDir.resolve("imports/TestBaseLexer2.g4");
        Path lexerGrammar = antlrDir.resolve("test/TestLexer.g4");
        Path parserGrammar = antlrDir.resolve("test/TestParser.g4");

        MavenProject project = maven.readMavenProject(baseDir.toFile());
        MavenSession session = maven.newMavenSession(project);
        MojoExecution exec = maven.newMojoExecution("antlr4");

        ////////////////////////////////////////////////////////////////////////
        // 1st - all grammars have to be processed
        ////////////////////////////////////////////////////////////////////////

        assertFalse(Files.exists(genHello));
        assertFalse(Files.exists(genTestParser));
        assertFalse(Files.exists(genTestLexer));

        maven.executeMojo(session, project, exec);

        assertTrue(Files.exists(genHello));
        assertTrue(Files.exists(genTestParser));
        assertTrue(Files.exists(genTestLexer));
        byte[] origTestLexerSum = checksum(genTestLexer);
        byte[] origTestParserSum = checksum(genTestParser);
        byte[] origHelloSum = checksum(genHello);

        ////////////////////////////////////////////////////////////////////////
        // 2nd - nothing has been modified, no grammars have to be processed
        ////////////////////////////////////////////////////////////////////////

        {
            maven.executeMojo(session, project, exec);

            assertTrue(Arrays.equals(origTestLexerSum, checksum(genTestLexer)));
            assertTrue(Arrays.equals(origTestParserSum, checksum(genTestParser)));
            assertTrue(Arrays.equals(origHelloSum, checksum(genHello)));
        }

        ////////////////////////////////////////////////////////////////////////
        // 3rd - the imported grammar changed, every dependency has to be processed
        ////////////////////////////////////////////////////////////////////////

        // modify the grammar to make checksum comparison detect a change
        try(Change change = Change.of(baseGrammar, "DOT: '.' ;")) {
            maven.executeMojo(session, project, exec);

            assertFalse(Arrays.equals(origTestLexerSum, checksum(genTestLexer)));
            assertFalse(Arrays.equals(origTestParserSum, checksum(genTestParser)));
            assertTrue(Arrays.equals(origHelloSum, checksum(genHello)));
        }
        // Restore file and confirm it was restored.
        maven.executeMojo(session, project, exec);
        assertTrue(Arrays.equals(origTestLexerSum, checksum(genTestLexer)));
        assertTrue(Arrays.equals(origTestParserSum, checksum(genTestParser)));
        assertTrue(Arrays.equals(origHelloSum, checksum(genHello)));

        ////////////////////////////////////////////////////////////////////////
        // 4th - the second imported grammar changed, every dependency has to be processed
        ////////////////////////////////////////////////////////////////////////

        // modify the grammar to make checksum comparison detect a change
        try(Change change = Change.of(baseGrammar2, "BANG: '!' ;")) {
            maven.executeMojo(session, project, exec);

            assertFalse(Arrays.equals(origTestLexerSum, checksum(genTestLexer)));
            assertFalse(Arrays.equals(origTestParserSum, checksum(genTestParser)));
            assertTrue(Arrays.equals(origHelloSum, checksum(genHello)));
        }
        // Restore file and confirm it was restored.
        maven.executeMojo(session, project, exec);
        assertTrue(Arrays.equals(origTestLexerSum, checksum(genTestLexer)));
        assertTrue(Arrays.equals(origTestParserSum, checksum(genTestParser)));
        assertTrue(Arrays.equals(origHelloSum, checksum(genHello)));

        ////////////////////////////////////////////////////////////////////////
        // 5th - the lexer grammar changed, the parser grammar has to be processed as well
        ////////////////////////////////////////////////////////////////////////

        // modify the grammar to make checksum comparison detect a change
        try(Change change = Change.of(lexerGrammar, "FOO: 'foo' ;")) {
            maven.executeMojo(session, project, exec);

            assertFalse(Arrays.equals(origTestLexerSum, checksum(genTestLexer)));
            assertFalse(Arrays.equals(origTestParserSum, checksum(genTestParser)));
            assertTrue(Arrays.equals(origHelloSum, checksum(genHello)));
        }
        // Restore file and confirm it was restored.
        maven.executeMojo(session, project, exec);
        assertTrue(Arrays.equals(origTestLexerSum, checksum(genTestLexer)));
        assertTrue(Arrays.equals(origTestParserSum, checksum(genTestParser)));
        assertTrue(Arrays.equals(origHelloSum, checksum(genHello)));

        ////////////////////////////////////////////////////////////////////////
        // 6th - the parser grammar changed, no other grammars have to be processed
        ////////////////////////////////////////////////////////////////////////

        // modify the grammar to make checksum comparison detect a change
        try(Change change = Change.of(parserGrammar, " t : WS* ;")) {
            maven.executeMojo(session, project, exec);

            assertTrue(Arrays.equals(origTestLexerSum, checksum(genTestLexer)));
            assertFalse(Arrays.equals(origTestParserSum, checksum(genTestParser)));
            assertTrue(Arrays.equals(origHelloSum, checksum(genHello)));
        }
        // Restore file and confirm it was restored.
        maven.executeMojo(session, project, exec);
        assertTrue(Arrays.equals(origTestLexerSum, checksum(genTestLexer)));
        assertTrue(Arrays.equals(origTestParserSum, checksum(genTestParser)));
        assertTrue(Arrays.equals(origHelloSum, checksum(genHello)));
    }

    @Test
    public void processWhenDependencyRemoved() throws Exception {
        Path baseDir = resources.getBasedir("dependencyRemoved").toPath();
        Path antlrDir = baseDir.resolve("src/main/antlr4");

        Path baseGrammar = antlrDir.resolve("imports/HelloBase.g4");

        MavenProject project = maven.readMavenProject(baseDir.toFile());
        MavenSession session = maven.newMavenSession(project);
        MojoExecution exec = maven.newMojoExecution("antlr4");

        maven.executeMojo(session, project, exec);

        try(Change temp = Change.of(baseGrammar)) {
            // if the base grammar no longer exists, processing must be performed
            Files.delete(baseGrammar);

            thrown.expect(MojoExecutionException.class);
            thrown.expectMessage("ANTLR 4 caught 1 build errors.");

            maven.executeMojo(session, project, exec);
        }
    }

    private byte[] checksum(Path path) throws IOException {
        return MojoUtils.checksum(path.toFile());
    }

    private static class Change implements AutoCloseable {
        final Path file;
        final byte[] original;

        public Change(Path file, String change) {
            this.file = file;

            try {
                original = Files.readAllBytes(file);
            } catch (IOException ex) {
                throw new RuntimeException("Could not read file " + file);
            }

            String text = new String(original, StandardCharsets.UTF_8) + change;

            write(file, text.getBytes(StandardCharsets.UTF_8));
        }

        private void write(Path file, byte[] data) {
            try {
                Files.write(file, data);
            } catch (IOException ex) {
                throw new RuntimeException("Could not write file " + file);
            }
        }

        public static Change of(Path file, String change) {
            return new Change(file, change);
        }

        public static Change of(Path file) {
            return new Change(file, "\n");
        }

        @Override
        public void close() {
            write(file, original);
        }
    }
}

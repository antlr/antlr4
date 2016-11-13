package org.antlr.mojo.antlr4;

import io.takari.maven.testing.TestMavenRuntime;
import io.takari.maven.testing.TestResources;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.util.xml.Xpp3Dom;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Antlr4MojoTest {
    @Rule
    public final TestResources resources = new TestResources();

    @Rule
    public final TestMavenRuntime maven = new TestMavenRuntime();

    @Test
    public void importTokens() throws Exception {
        Map.Entry<ByteArrayOutputStream, PrintStream> stdout = redirect();
        ByteArrayOutputStream output = stdout.getKey();

        try {
            File baseDir = resources.getBasedir("importTokens");
            File antlrDir = new File(baseDir, "src/main/antlr4");

            File tokens = new File(antlrDir, "imports/SimpleLexer.tokens");

            MavenProject project = maven.readMavenProject(baseDir);
            MavenSession session = maven.newMavenSession(project);
            MojoExecution exec = maven.newMojoExecution("antlr4");

            ////////////////////////////////////////////////////////////////////////
            // 1st - all grammars have to be processed
            ////////////////////////////////////////////////////////////////////////

            maven.executeMojo(session, project, exec);

            assertEquals(Arrays.asList("test/SimpleParser.g4"), processAndReset(output));

            ////////////////////////////////////////////////////////////////////////
            // 2nd - nothing has been modified, no grammars have to be processed
            ////////////////////////////////////////////////////////////////////////

            maven.executeMojo(session, project, exec);

            assertEquals(Collections.emptyList(), processAndReset(output));

            ////////////////////////////////////////////////////////////////////////
            // 3rd - the imported grammar changed, every dependency has to be processed
            ////////////////////////////////////////////////////////////////////////

            String original = modify(tokens);

            try {
                maven.executeMojo(session, project, exec);

                assertEquals(Arrays.asList("test/SimpleParser.g4"),
                    processAndReset(output));
            } finally {
                write(tokens, original);
            }
        } finally {
            System.setOut(stdout.getValue());
        }
    }

    @Test
    public void importsCustomLayout() throws Exception {
        Map.Entry<ByteArrayOutputStream, PrintStream> stdout = redirect();
        ByteArrayOutputStream output = stdout.getKey();

        try {
            File baseDir = resources.getBasedir("importsCustom");
            File antlrDir = new File(baseDir, "src/main/antlr4");
            File generatedSources = new File(baseDir, "target/generated-sources/antlr4");

            File baseGrammar = new File(antlrDir, "imports/TestBaseLexer.g4");
            File lexerGrammar = new File(antlrDir, "TestLexer.g4");
            File parserGrammar = new File(antlrDir, "TestParser.g4");
            File lexerTokens = new File(generatedSources, "TestLexer.tokens");

            Xpp3Dom outputDirectory = TestMavenRuntime.newParameter("outputDirectory",
                    "src/main/java/com/foo");
            Xpp3Dom arguments = new Xpp3Dom("arguments");
            arguments.addChild(TestMavenRuntime.newParameter("argument", "-package"));
            arguments.addChild(TestMavenRuntime.newParameter("argument", "foo"));

            MavenProject project = maven.readMavenProject(baseDir);
            MavenSession session = maven.newMavenSession(project);
            MojoExecution exec = maven.newMojoExecution("antlr4", outputDirectory,
                    arguments);

            ////////////////////////////////////////////////////////////////////////
            // 1st - all grammars have to be processed
            ////////////////////////////////////////////////////////////////////////

            maven.executeMojo(session, project, exec);

            assertEquals(Arrays.asList("Hello.g4", "TestLexer.g4", "TestParser.g4"),
                processAndReset(output));

            ////////////////////////////////////////////////////////////////////////
            // 2nd - nothing has been modified, no grammars have to be processed
            ////////////////////////////////////////////////////////////////////////

            maven.executeMojo(session, project, exec);

            assertEquals(Collections.emptyList(), processAndReset(output));

            ////////////////////////////////////////////////////////////////////////
            // 3rd - the imported grammar changed, every dependency has to be processed
            ////////////////////////////////////////////////////////////////////////

            // modify the grammar to make checksum comparison detect a change
            String original = modify(baseGrammar);

            try {
                maven.executeMojo(session, project, exec);

                assertEquals(Arrays.asList("TestLexer.g4", "TestParser.g4"),
                    processAndReset(output));
            } finally {
                write(baseGrammar, original);
            }

            ////////////////////////////////////////////////////////////////////////
            // 4th - the lexer grammar changed, the parser grammar has to be processed as well
            ////////////////////////////////////////////////////////////////////////

            // modify the grammar to make checksum comparison detect a change
            original = modify(lexerGrammar);

            try {
                lexerTokens.delete();

                maven.executeMojo(session, project, exec);

                assertEquals(Arrays.asList("TestLexer.g4", "TestParser.g4"),
                    processAndReset(output));
            } finally {
                write(lexerGrammar, original);
            }

            ////////////////////////////////////////////////////////////////////////
            // 5th - the parser grammar changed, no other grammars have to be processed
            ////////////////////////////////////////////////////////////////////////

            // modify the grammar to make checksum comparison detect a change
            original = modify(parserGrammar);

            try {
                maven.executeMojo(session, project, exec);

                assertEquals(Arrays.asList("TestParser.g4"), processAndReset(output));
            } finally {
                write(parserGrammar, original);
            }
        } finally {
            System.out.flush();
            System.setOut(stdout.getValue());
        }
    }

    @Test
    public void importsStandardLayout() throws Exception {
        Map.Entry<ByteArrayOutputStream, PrintStream> stdout = redirect();
        ByteArrayOutputStream output = stdout.getKey();

        try {
            File baseDir = resources.getBasedir("importsStandard");
            File antlrDir = new File(baseDir, "src/main/antlr4");
            File generatedSources = new File(baseDir, "target/generated-sources/antlr4");

            File baseGrammar = new File(antlrDir, "imports/TestBaseLexer.g4");
            File lexerGrammar = new File(antlrDir, "test/TestLexer.g4");
            File parserGrammar = new File(antlrDir, "test/TestParser.g4");
            File lexerTokens = new File(generatedSources, "TestLexer.tokens");

            MavenProject project = maven.readMavenProject(baseDir);
            MavenSession session = maven.newMavenSession(project);
            MojoExecution exec = maven.newMojoExecution("antlr4");

            ////////////////////////////////////////////////////////////////////////
            // 1st - all grammars have to be processed
            ////////////////////////////////////////////////////////////////////////

            maven.executeMojo(session, project, exec);

            assertEquals(Arrays.asList("test/Hello.g4", "test/TestLexer.g4",
                    "test/TestParser.g4"), processAndReset(output));

            ////////////////////////////////////////////////////////////////////////
            // 2nd - nothing has been modified, no grammars have to be processed
            ////////////////////////////////////////////////////////////////////////

            maven.executeMojo(session, project, exec);

            assertEquals(Collections.emptyList(), processAndReset(output));

            ////////////////////////////////////////////////////////////////////////
            // 3rd - the imported grammar changed, every dependency has to be processed
            ////////////////////////////////////////////////////////////////////////

            // modify the grammar to make checksum comparison detect a change
            String original = modify(baseGrammar);

            try {
                maven.executeMojo(session, project, exec);

                assertEquals(Arrays.asList("test/TestLexer.g4", "test/TestParser.g4"),
                    processAndReset(output));
            } finally {
                write(baseGrammar, original);
            }

            ////////////////////////////////////////////////////////////////////////
            // 4th - the lexer grammar changed, the parser grammar has to be processed as well
            ////////////////////////////////////////////////////////////////////////

            // modify the grammar to make checksum comparison detect a change
            original = modify(lexerGrammar);

            try {
                lexerTokens.delete();

                maven.executeMojo(session, project, exec);

                assertEquals(Arrays.asList("test/TestLexer.g4", "test/TestParser.g4"),
                    processAndReset(output));
            } finally {
                write(lexerGrammar, original);
            }

            ////////////////////////////////////////////////////////////////////////
            // 5th - the parser grammar changed, no other grammars have to be processed
            ////////////////////////////////////////////////////////////////////////

            // modify the grammar to make checksum comparison detect a change
            original = modify(parserGrammar);

            try {
                maven.executeMojo(session, project, exec);

                assertEquals(Arrays.asList("test/TestParser.g4"),
                    processAndReset(output));
            } finally {
                write(parserGrammar, original);
            }
        } finally {
            System.out.flush();
            System.setOut(stdout.getValue());
        }
    }

    private List<String> processAndReset(ByteArrayOutputStream output) {
        Pattern pattern = Pattern.compile("Processing grammar: (.+)");
        Matcher matcher = pattern.matcher(output.toString());

        // we reset here simply for convenience
        output.reset();

        List<String> result = new ArrayList<String>();

        while (matcher.find()) {
            result.add(matcher.group(1));
        }

        Collections.sort(result);

        return result;
    }

    private String modify(File file) throws IOException {
        String content = text(file);
        write(file, content + "\n");

        return content;
    }

    private void write(File file, String text) throws IOException {
        Files.write(file.toPath(), text.getBytes(StandardCharsets.UTF_8));
    }

    private String text(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    private Map.Entry<ByteArrayOutputStream, PrintStream> redirect() {
        // there does not seem to be a clean way to intercept logging output
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(buf));

        return new AbstractMap.SimpleEntry<ByteArrayOutputStream, PrintStream>(buf, old);
    }
}

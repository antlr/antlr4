package org.antlr.v4.test;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;

public class TestPerformance extends BaseTest {
    /** Parse all java files under this package within the JDK_SOURCE_ROOT. */
    private static final String TOP_PACKAGE = "java";
    /** True to load java files from sub-packages of {@link #TOP_PACKAGE}. */
    private static final boolean RECURSIVE = true;

    /**
     *  True to use the Java grammar with expressions in the v4 left-recursive syntax (Java-LR.g). False to use
     *  the standard grammar (Java.g). In either case, the grammar is renamed in the temporary directory to Java.g
     *  before compiling.
     */
    private static final boolean USE_LR_GRAMMAR = false;
    /**
     *  True to specify the -Xforceatn option when generating the grammar, forcing all decisions in JavaParser to
     *  be handled by {@link v2ParserATNSimulator#adaptivePredict}.
     */
    private static final boolean FORCE_ATN = false;

    /** Parse each file with JavaParser.compilationUnit */
    private static final boolean RUN_PARSER = true;
    /** True to use {@link BailErrorStrategy}, False to use {@link DefaultErrorStrategy} */
    private static final boolean BAIL_ON_ERROR = false;
    /** This value is passed to {@link org.antlr.v4.runtime.Parser#setBuildParseTree}. */
    private static final boolean BUILD_PARSE_TREES = false;
    /**
     *  Use ParseTreeWalker.DEFAULT.walk with the BlankJavaParserListener to show parse tree walking overhead.
     *  If {@link #BUILD_PARSE_TREES} is false, the listener will instead be called during the parsing process via
     *  {@link org.antlr.v4.runtime.Parser#addParseListener}.
     */
    private static final boolean BLANK_LISTENER = false;

    /**
     *  If true, a single JavaLexer will be used, and {@link Lexer#setInputStream} will be called to initialize it
     *  for each source file. In this mode, the cached DFA will be persisted throughout the lexing process.
     */
    private static final boolean REUSE_LEXER = true;
    /**
     *  If true, a single JavaParser will be used, and {@link Parser#setInputStream} will be called to initialize it
     *  for each source file. In this mode, the cached DFA will be persisted throughout the parsing process.
     */
    private static final boolean REUSE_PARSER = true;
    /**
     * If true, the shared lexer and parser are reset after each pass. If false, all passes after the first will
     * be fully "warmed up", which makes them faster and can compare them to the first warm-up pass, but it will
     * not distinguish bytecode load/JIT time from warm-up time during the first pass.
     */
    private static final boolean CLEAR_DFA = false;

    /** Total number of passes to make over the source */
    private static final int PASSES = 4;

    private Lexer sharedLexer;
    private Parser sharedParser;
    @SuppressWarnings({"FieldCanBeLocal"})
    private ParseTreeListener<Token> sharedListener;

    private int tokenCount;

    @Test
//    @Ignore
    public void compileJdk() throws IOException {
        compileParser(USE_LR_GRAMMAR);
        JavaParserFactory factory = getParserFactory();
        String jdkSourceRoot = System.getenv("JDK_SOURCE_ROOT");
        if (jdkSourceRoot == null) {
            System.err.println("The JDK_SOURCE_ROOT environment variable must be set for performance testing.");
            return;
        }

        if (!TOP_PACKAGE.isEmpty()) {
            jdkSourceRoot = jdkSourceRoot + '/' + TOP_PACKAGE.replace('.', '/');
        }

        File directory = new File(jdkSourceRoot);
        assertTrue(directory.isDirectory());

        Collection<CharStream> sources = loadSources(directory, RECURSIVE);
        System.out.format("Lex=true, Parse=%s, ForceAtn=%s, Bail=%s, BuildParseTree=%s, BlankListener=%s\n",
            RUN_PARSER, FORCE_ATN, BAIL_ON_ERROR, BUILD_PARSE_TREES, BLANK_LISTENER);
        parse1(factory, sources);
        for (int i = 0; i < PASSES - 1; i++) {
            if (CLEAR_DFA) {
                sharedLexer = null;
                sharedParser = null;
            }

            parse2(factory, sources);
        }
    }

    /**
     *  This method is separate from {@link #parse2} so the first pass can be distinguished when analyzing
     *  profiler results.
     */
    protected void parse1(JavaParserFactory factory, Collection<CharStream> sources) {
        System.gc();
        parseSources(factory, sources);
    }

    /**
     *  This method is separate from {@link #parse1} so the first pass can be distinguished when analyzing
     *  profiler results.
     */
    protected void parse2(JavaParserFactory factory, Collection<CharStream> sources) {
        System.gc();
        parseSources(factory, sources);
    }

    protected Collection<CharStream> loadSources(File directory, boolean recursive) {
        Collection<CharStream> result = new ArrayList<CharStream>();
        loadSources(directory, recursive, result);
        return result;
    }

    protected void loadSources(File directory, boolean recursive, Collection<CharStream> result) {
        assert directory.isDirectory();

        File[] sources = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".java");
            }
        });
        for (File file : sources) {
            try {
                CharStream input = new ANTLRFileStream(file.getAbsolutePath());
                result.add(input);
            } catch (IOException ex) {

            }
        }

        if (recursive) {
            File[] children = directory.listFiles();
            for (File child : children) {
                if (child.isDirectory()) {
                    loadSources(child, true, result);
                }
            }
        }
    }

    protected void parseSources(JavaParserFactory factory, Collection<CharStream> sources) {
        long startTime = System.currentTimeMillis();
        tokenCount = 0;
        int inputSize = 0;

        for (CharStream input : sources) {
            input.seek(0);
            inputSize += input.size();
            // this incurred a great deal of overhead and was causing significant variations in performance results.
            //System.out.format("Parsing file %s\n", file.getAbsolutePath());
            try {
                factory.parseFile(input);
            } catch (IllegalStateException ex) {
                ex.printStackTrace(System.out);
            }
        }

        System.out.format("Total parse time for %d files (%d KB, %d tokens): %dms\n",
                          sources.size(),
                          inputSize / 1024,
                          tokenCount,
                          System.currentTimeMillis() - startTime);
    }

    protected void compileParser(boolean leftRecursive) throws IOException {
        String grammarFileName = "Java.g";
        String sourceName = leftRecursive ? "Java-LR.g" : "Java.g";
        String body = load(sourceName, null);
        @SuppressWarnings({"ConstantConditions"})
        String[] extraOptions = FORCE_ATN ? new String[] {"-Xforceatn"} : new String[0];
        boolean success = rawGenerateAndBuildRecognizer(grammarFileName, body, "JavaParser", "JavaLexer", extraOptions);
        assertTrue(success);
    }

    protected String load(String fileName, @Nullable String encoding)
        throws IOException
    {
        if ( fileName==null ) {
            return null;
        }

        String fullFileName = getClass().getPackage().getName().replace('.', '/') + '/' + fileName;
        int size = 65000;
        InputStreamReader isr;
        InputStream fis = getClass().getClassLoader().getResourceAsStream(fullFileName);
        if ( encoding!=null ) {
            isr = new InputStreamReader(fis, encoding);
        }
        else {
            isr = new InputStreamReader(fis);
        }
        try {
            char[] data = new char[size];
            int n = isr.read(data);
            return new String(data, 0, n);
        }
        finally {
            isr.close();
        }
    }

    protected JavaParserFactory getParserFactory() {
        try {
            ClassLoader loader = new URLClassLoader(new URL[] { new File(tmpdir).toURI().toURL() }, ClassLoader.getSystemClassLoader());
            @SuppressWarnings({"unchecked"})
            final Class<? extends Lexer> lexerClass = (Class<? extends Lexer>)loader.loadClass("JavaLexer");
            @SuppressWarnings({"unchecked"})
            final Class<? extends Parser> parserClass = (Class<? extends Parser>)loader.loadClass("JavaParser");
            @SuppressWarnings({"unchecked"})
            final Class<? extends ParseTreeListener<Token>> listenerClass = (Class<? extends ParseTreeListener<Token>>)loader.loadClass("BlankJavaListener");
            this.sharedListener = listenerClass.newInstance();

            final Constructor<? extends Lexer> lexerCtor = lexerClass.getConstructor(CharStream.class);
            final Constructor<? extends Parser> parserCtor = parserClass.getConstructor(TokenStream.class);

            // construct initial instances of the lexer and parser to deserialize their ATNs
            lexerCtor.newInstance(new ANTLRInputStream(""));
            parserCtor.newInstance(new CommonTokenStream());

            return new JavaParserFactory() {
                @SuppressWarnings({"PointlessBooleanExpression"})
                @Override
                public void parseFile(CharStream input) {
                    try {
                        if (REUSE_LEXER && sharedLexer != null) {
                            sharedLexer.setInputStream(input);
                        } else {
                            sharedLexer = lexerCtor.newInstance(input);
                        }

                        CommonTokenStream tokens = new CommonTokenStream(sharedLexer);
                        tokens.fill();
                        tokenCount += tokens.size();

                        if (!RUN_PARSER) {
                            return;
                        }

                        if (REUSE_PARSER && sharedParser != null) {
                            sharedParser.setInputStream(tokens);
                        } else {
                            sharedParser = parserCtor.newInstance(tokens);
                            sharedParser.setBuildParseTree(BUILD_PARSE_TREES);
                            if (!BUILD_PARSE_TREES && BLANK_LISTENER) {
                                sharedParser.addParseListener(sharedListener);
                            }
                            if (BAIL_ON_ERROR) {
                                sharedParser.setErrorHandler(new BailErrorStrategy());
                            }
                        }

                        Method parseMethod = parserClass.getMethod("compilationUnit");
                        Object parseResult = parseMethod.invoke(sharedParser);
                        assert parseResult instanceof ParseTree;

                        if (BUILD_PARSE_TREES && BLANK_LISTENER) {
                            ParseTreeWalker.DEFAULT.walk(sharedListener, (ParseTree)parseResult);
                        }
                    } catch (Exception e) {
                        e.printStackTrace(System.out);
                        throw new IllegalStateException(e);
                    }
                }
            };
        } catch (Exception e) {
            e.printStackTrace(System.out);
            lastTestFailed = true;
            Assert.fail(e.getMessage());
            throw new IllegalStateException(e);
        }
    }

    protected interface JavaParserFactory {
        void parseFile(CharStream input);
    }
}

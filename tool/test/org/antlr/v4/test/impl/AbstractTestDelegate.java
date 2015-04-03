package org.antlr.v4.test.impl;

import org.antlr.v4.Tool;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.test.ErrorQueue;
import org.junit.runner.Description;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jason on 4/1/15.
 */
public abstract class AbstractTestDelegate implements AntlrTestDelegate {

    @Override
    public void testWillStart(Description description) {

    }

    @Override
    public void testDidFinish(Description description) {

    }


    @Override
    public String loadFile(URL url, String encoding) {
        int size = 65000;
        InputStreamReader isr = null;
        try {
            if (encoding != null) {
                isr = new InputStreamReader(url.openStream(), encoding);
            } else {
                isr = new InputStreamReader(url.openStream());
            }


            char[] data = new char[size];
            int n = isr.read(data);
            return new String(data, 0, n);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public Pair<Parser, Lexer> getParserAndLexer(String input, String parserName, String lexerName) {
        try {

            ANTLRInputStream in = new ANTLRInputStream(new StringReader(input));

            Constructor<? extends Lexer> ctor = loadCompiledClass(lexerName).asSubclass(Lexer.class)
                                                                            .getConstructor(CharStream.class);
            Lexer lexer = ctor.newInstance(in);

            Constructor<? extends Parser> pctor = loadCompiledClass(parserName).asSubclass(Parser.class)
                                                                               .getConstructor(TokenStream.class);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            Parser parser = pctor.newInstance(tokens);
            return new Pair<Parser, Lexer>(parser, lexer);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return true if all is well
     */
    protected boolean generateAndBuildRecognizer(String grammarFileName,
                                                 String grammarStr,
                                                 String parserName,
                                                 String lexerName,
                                                 String... extraOptions) {
        return generateAndBuildRecognizer(grammarFileName, grammarStr, parserName, lexerName, false, extraOptions);
    }

    public ErrorQueue antlr(String grammarFileName,
                            String grammarStr,
                            boolean defaultListener,
                            String... extraOptions) {
        mkdir(getWorkingDir());
        writeFile(getWorkingDir(), grammarFileName, grammarStr);
        return antlr(grammarFileName,defaultListener,extraOptions);
    }

    protected String[] makeAntlrOptions(String grammarFileName, String... extraOpts) {
        final List<String> options = new ArrayList<String>();
        Collections.addAll(options, extraOpts);
        if (!options.contains("-o")) {
            options.add("-o");
            options.add(getWorkingDir());
        }
        if (!options.contains("-lib")) {
            options.add("-lib");
            options.add(getWorkingDir());
        }
        if (!options.contains("-encoding")) {
            options.add("-encoding");
            options.add("UTF-8");
        }
        options.add(getWorkingDir() + File.separatorChar + grammarFileName);

        return options.toArray(new String[options.size()]);
    }

    public Tool createTool(String... args) {
        return new Tool(args);
    }
}

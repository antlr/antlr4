package org.antlr.v4.test;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Pair;
import org.junit.runner.Description;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

/**
 * Created by jason on 4/1/15.
 */
public abstract class AbstractTestDelegate implements AntlrTestDelegate {

    @Override
    public void testWillStart(Description description) {

    }

    @Override
    public void testDidFinish() {

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

    private Pair<Parser, Lexer> doGetParserAndLexer(String input, String parserName, String lexerName) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final Class<? extends Lexer> lexerClass = loadCompiledClass(lexerName).asSubclass(Lexer.class);
        final Class<? extends Parser> parserClass = loadCompiledClass(parserName).asSubclass(Parser.class);

        ANTLRInputStream in = new ANTLRInputStream(new StringReader(input));

        Class<? extends Lexer> c = lexerClass.asSubclass(Lexer.class);
        Constructor<? extends Lexer> ctor = c.getConstructor(CharStream.class);
        Lexer lexer = ctor.newInstance(in);

        Class<? extends Parser> pc = parserClass.asSubclass(Parser.class);
        Constructor<? extends Parser> pctor = pc.getConstructor(TokenStream.class);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Parser parser = pctor.newInstance(tokens);
        return new Pair<Parser, Lexer>(parser, lexer);
    }

    @Override
    public Pair<Parser, Lexer> getParserAndLexer(String input, String parserName, String lexerName) {
        try {
            return doGetParserAndLexer(input, parserName, lexerName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

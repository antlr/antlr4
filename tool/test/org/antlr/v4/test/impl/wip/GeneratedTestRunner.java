package org.antlr.v4.test.impl.wip;

import org.antlr.v4.test.impl.GeneratedTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by jason on 4/10/15.
 */
public class GeneratedTestRunner {
    public static class Result {
        public final String out;
        public final String err;

        public Result(String out, String err) {
            this.out = out;
            this.err = err;

        }
    }

    static final PrintStream ORIG_OUT = System.out;
    static final PrintStream ORIG_ERR = System.err;
    String err;
    String out;
    final ByteArrayOutputStream baos_out = new ByteArrayOutputStream();
    final ByteArrayOutputStream baos_err = new ByteArrayOutputStream();

    void beginCapture() {
        baos_out.reset();
        baos_err.reset();
        System.setOut(new PrintStream(baos_out, true));
        System.setErr(new PrintStream(baos_err, true));
    }

    Result endCapture() {
        System.setOut(ORIG_OUT);
        System.setErr(ORIG_ERR);

        try {
            String out = baos_out.toString("UTF-8");
            String err = baos_err.toString("UTF-8");
            return new Result(out, err);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }


    }

    Result runTest(GeneratedTest test) {
        Result result;
        beginCapture();
        try {
            test.test();
        } finally {
            result = endCapture();
        }
        return result;

    }

    public static Result run(GeneratedTest test) {
        return INSTANCE.runTest(test);
    }

    static final GeneratedTestRunner INSTANCE = new GeneratedTestRunner();
}

package org.antlr.v4.test.impl;



import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jason on 3/26/15.
 */
public class InProcessTestDelegate extends DefaultTestDelegate {
    public static final InProcessTestDelegate INSTANCE = new InProcessTestDelegate();

    private static final Logger LOGGER = Logger.getLogger(InProcessTestDelegate.class.getName());

    @Override
    public String execClass(String className) {
        try {
            ClassLoader loader = new URLClassLoader(new URL[] { new File(tmpdir).toURI().toURL() }, ClassLoader.getSystemClassLoader());
            final Class<?> mainClass = loader.loadClass(className);
            final Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
            PipedInputStream stdoutIn = new PipedInputStream();
            PipedInputStream stderrIn = new PipedInputStream();
            PipedOutputStream stdoutOut = new PipedOutputStream(stdoutIn);
            PipedOutputStream stderrOut = new PipedOutputStream(stderrIn);
            StreamVacuum stdoutVacuum = new StreamVacuum(stdoutIn);
            StreamVacuum stderrVacuum = new StreamVacuum(stderrIn);

            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(stdoutOut));
            try {
                PrintStream originalErr = System.err;
                try {
                    System.setErr(new PrintStream(stderrOut));
                    stdoutVacuum.start();
                    stderrVacuum.start();
                    mainMethod.invoke(null, (Object)new String[] { new File(tmpdir, "input").getAbsolutePath() });
                }
                finally {
                    System.setErr(originalErr);
                }
            }
            finally {
                System.setOut(originalOut);
            }

            stdoutOut.close();
            stderrOut.close();
            stdoutVacuum.join();
            stderrVacuum.join();
            String output = stdoutVacuum.toString();
            if ( stderrVacuum.toString().length()>0 ) {
                this.stderrDuringParse = stderrVacuum.toString();
                System.err.println("exec stderrVacuum: "+ stderrVacuum);
            }
            return output;
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (NoSuchMethodException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (SecurityException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
}

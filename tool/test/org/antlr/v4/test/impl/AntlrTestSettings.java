package org.antlr.v4.test.impl;


import java.util.Properties;

/**
 * Created by jason on 3/24/15.
 */
public class AntlrTestSettings {
    public static final String PATH_SEP;

    public static String CLASSPATH;


    public static boolean TEST_IN_PROCESS;
   // public static boolean TEST_IN_MEMORY;
    public static boolean PRESERVE_TEST_DIR;

    public static String BASE_TEST_DIR;

    public static boolean CREATE_PER_TEST_DIRECTORIES;

    static {
        PATH_SEP = System.getProperty("path.separator");
        CLASSPATH = System.getProperty("java.class.path");

        TEST_IN_PROCESS = booleanProperty(true, "antlr.testinprocess", "antlr.test.inprocess");
       // TEST_IN_MEMORY = booleanProperty(false, "antlr.test.inmemory");
        PRESERVE_TEST_DIR = booleanProperty(false, "antlr.preserve-test-dir", "antlr.test.preserve-test-dir");

        String baseTestDir = stringProperty("antlr.java-test-dir", "antlr.test.java-test-dir");
        boolean perTestDirectories = false;
        if (baseTestDir == null || baseTestDir.isEmpty()) {
            baseTestDir = System.getProperty("java.io.tmpdir");
            perTestDirectories = true;
        }
        BASE_TEST_DIR = baseTestDir;
        CREATE_PER_TEST_DIRECTORIES = perTestDirectories || PRESERVE_TEST_DIR;

    }

    static String stringProperty(String... possibleKeys) {
        Properties p = System.getProperties();
        for (String key : possibleKeys) {
            if (p.containsKey(key)) return p.getProperty(key);
        }
        return null;
    }

    static boolean booleanProperty(boolean defaultValue, String... possibleKeys) {
        Properties p = System.getProperties();
        for (String key : possibleKeys) {
            if (!p.containsKey(key)) continue;

            String value = p.getProperty(key);
            if (value == null) return true;
            value = value.trim();
            if (value.isEmpty()) return true;
            if ("true".equalsIgnoreCase(value)) return true;
            if ("yes".equalsIgnoreCase(value)) return true;

            if ("false".equalsIgnoreCase(value)) return false;
            if ("no".equalsIgnoreCase(value)) return false;

            System.err.println("???: " + key + "=" + value);
            return defaultValue;
        }
        return defaultValue;

    }


    public static AntlrTestDelegate getHelper() {
        if(TEST_IN_PROCESS){
            return InProcessTestHelper.INSTANCE;
        }
        return DefaultTestHelper.INSTANCE;
    }
}

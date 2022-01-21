package org.antlr.v4.test.runtime.java;

import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.misc.InterpreterDataReader;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TestInterpreterDataReader {

    @Test
    public void testParseFile() throws NoSuchFieldException, IllegalAccessException {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        // pay attention to the path where the InterpDataReaderTest.interp is stored.
        final URL stuff = loader.getResource("org/antlr/v4/test/runtime/InterpDataReaderTest.interp");
        Assert.assertNotNull(stuff);

        InterpreterDataReader.InterpreterData interpreterData = InterpreterDataReader.parseFile(stuff.getPath());
        Field atnField = interpreterData.getClass().getDeclaredField("atn");
        Field vocabularyField = interpreterData.getClass().getDeclaredField("vocabulary");
        Field ruleNamesField = interpreterData.getClass().getDeclaredField("ruleNames");
        Field channelsField = interpreterData.getClass().getDeclaredField("channels");
        Field modesField = interpreterData.getClass().getDeclaredField("modes");

        atnField.setAccessible(true);
        vocabularyField.setAccessible(true);
        ruleNamesField.setAccessible(true);
        channelsField.setAccessible(true);
        modesField.setAccessible(true);

        ATN atn = (ATN) atnField.get(interpreterData);
        Vocabulary vocabulary = (Vocabulary) vocabularyField.get(interpreterData);
        List<String> ruleNames = castList(ruleNamesField.get(interpreterData), String.class);
        List<String> channels = castList(channelsField.get(interpreterData), String.class);
        List<String> modes = castList(modesField.get(interpreterData), String.class);

        Assert.assertNotNull(vocabulary);
        Assert.assertEquals(11, ruleNames.size());
        Assert.assertEquals(2, channels.size());
        Assert.assertEquals(1, modes.size());
    }

    @Test
    public void testParseFileError() {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final URL stuff = loader.getResource("org/antlr/v4/test/runtime/InterpDataReaderTest.interp");
        Assert.assertNotNull(stuff);

        try {
            InterpreterDataReader.InterpreterData interpreterData = InterpreterDataReader.parseFile(stuff.getPath());
        } catch (Exception e) {
            Assert.assertEquals(e.getClass(), RuntimeException.class);
            Assert.assertEquals(e.getMessage(), "Unexpected data entry");
        }
    }

    private <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }
}

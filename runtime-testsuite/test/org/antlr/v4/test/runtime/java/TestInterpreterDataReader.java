/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.java;

import org.antlr.v4.Tool;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ATNSerializer;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.runtime.misc.InterpreterDataReader;
import org.antlr.v4.tool.Grammar;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** This file represents a simple sanity checks on the parsing of the .interp file
 *  available to the Java runtime for interpreting rather than compiling and executing parsers.
 */
public class TestInterpreterDataReader {
    @Test
    public void testParseFile() throws IOException, NoSuchFieldException, IllegalAccessException, org.antlr.runtime.RecognitionException {
		Grammar g = new Grammar(
				"grammar Calc;\n" +
				"s :  expr EOF\n" +
				"  ;\n" +
				"expr\n" +
				"  :  INT            # number\n" +
				"  |  expr (MUL | DIV) expr  # multiply\n" +
				"  |  expr (ADD | SUB) expr  # add\n" +
				"  ;\n" +
				"\n" +
				"INT : [0-9]+;\n" +
				"MUL : '*';\n" +
				"DIV : '/';\n" +
				"ADD : '+';\n" +
				"SUB : '-';\n" +
				"WS : [ \\t]+ -> channel(HIDDEN);");
		String interpString = Tool.generateInterpreterData(g);
		Path interpFile = Files.createTempFile(null, null);
		Files.write(interpFile, interpString.getBytes(StandardCharsets.UTF_8));

        InterpreterDataReader.InterpreterData interpreterData = InterpreterDataReader.parseFile(interpFile.toString());
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
		String[] literalNames = ((VocabularyImpl) vocabulary).getLiteralNames();
		String[] symbolicNames = ((VocabularyImpl) vocabulary).getSymbolicNames();
		List<String> ruleNames = castList(ruleNamesField.get(interpreterData), String.class);
        List<String> channels = castList(channelsField.get(interpreterData), String.class);
        List<String> modes = castList(modesField.get(interpreterData), String.class);

		assertEquals(6, vocabulary.getMaxTokenType());
		assertArrayEquals(new String[]{"s","expr"}, ruleNames.toArray());
		assertArrayEquals(new String[]{"", "", "'*'", "'/'", "'+'", "'-'", ""}, literalNames);
		assertArrayEquals(new String[]{"", "INT", "MUL", "DIV", "ADD", "SUB", "WS"}, symbolicNames);
		assertNull(channels);
		assertNull(modes);

		IntegerList serialized = ATNSerializer.getSerialized(atn);
		assertEquals(ATNDeserializer.SERIALIZED_VERSION, serialized.get(0));
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

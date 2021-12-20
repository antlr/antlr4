package org.antlr.v4.test.runtime.java;

import org.antlr.v4.runtime.misc.InterpreterDataReader;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;

public class TestInterpreterDataReader {
	@Test
	public void test() throws NoSuchFieldException, IllegalAccessException {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		// pay attention to the path where the InterpDataReaderTest.interp is stored.
		final URL stuff = loader.getResource("org/antlr/v4/test/runtime/InterpDataReaderTest.interp");
		InterpreterDataReader.InterpreterData interpreterData = InterpreterDataReader.parseFile(stuff.getPath());
		Field tokenLiteralNames = interpreterData.getClass().getDeclaredField("token literal names");
		Field tokenSymbolicNames = interpreterData.getClass().getDeclaredField("token symbolic names");
		Field ruleNames = interpreterData.getClass().getDeclaredField("ruleNames");
		Field channels = interpreterData.getClass().getDeclaredField("channels");
		Field modes = interpreterData.getClass().getDeclaredField("modes");
		ruleNames.setAccessible(true);
		channels.setAccessible(true);
		modes.setAccessible(true);
		List<String> tokenLiteralList = (List<String>) tokenLiteralNames.get(interpreterData);
		List<String> tokenSymbolicList = (List<String>) tokenSymbolicNames.get(interpreterData);
		List<String> ruleNamesList = (List<String>) ruleNames.get(interpreterData);
		List<String> channelsList = (List<String>) channels.get(interpreterData);
		List<String> modesList = (List<String>) modes.get(interpreterData);

		Assert.assertEquals(12, tokenLiteralList.size());
		Assert.assertEquals(12, tokenSymbolicList.size());
		Assert.assertEquals(11,ruleNamesList.size());
		Assert.assertEquals(2,channelsList.size());
		Assert.assertEquals(1,modesList.size());
	}
}

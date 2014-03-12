/*
 * [The "BSD license"]
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime.test;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RuleVersion;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.test.BaseTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 * @author Sam Harwell
 */
public class TestRuleDependencies extends BaseTest {

	/**
	 * This test ensures that when {@code -rule-versioning} is omitted from the
	 * Tool command line, no {@link RuleVersion} annotations appear in the
	 * generated parser.
	 */
	@Test
	public void testToolOptionOmitted() throws IOException, ClassNotFoundException {
		String grammarFileName = "Simple.g4";
		String body = load(grammarFileName, null);
		String[] extraOptionsArray = { "-Werror" };
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName, body, "SimpleParser", "SimpleLexer", true, extraOptionsArray);
		Assert.assertTrue(success);

		URL[] urls = { new File(tmpdir).toURI().toURL() };
		ClassLoader classLoader = new URLClassLoader(urls);
		Class<? extends Parser> parserClass = classLoader.loadClass("SimpleParser").asSubclass(Parser.class);
		Assert.assertNotNull(parserClass);

		for (Method method : parserClass.getDeclaredMethods()) {
			Assert.assertNull(method.getAnnotation(RuleVersion.class));
		}
	}

	/**
	 * This test ensures that when {@code -rule-versioning} is included in the
	 * Tool command line, {@link RuleVersion} annotations appear on each method
	 * in the generated parser which corresponds to a parser rule.
	 */
	@Test
	public void testToolOptionEnabled() throws IOException, ClassNotFoundException, NoSuchMethodException {
		String grammarFileName = "Simple.g4";
		String body = load(grammarFileName, null);
		String[] extraOptionsArray = { "-Werror", "-rule-versioning" };
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName, body, "SimpleParser", "SimpleLexer", true, extraOptionsArray);
		Assert.assertTrue(success);

		URL[] urls = { new File(tmpdir).toURI().toURL() };
		ClassLoader classLoader = new URLClassLoader(urls);
		Class<? extends Parser> parserClass = classLoader.loadClass("SimpleParser").asSubclass(Parser.class);
		Assert.assertNotNull(parserClass);

		Method sourceFileMethod = parserClass.getDeclaredMethod("sourceFile");
		Assert.assertNotNull(sourceFileMethod);
		Assert.assertNotNull(sourceFileMethod.getAnnotation(RuleVersion.class));
		Assert.assertEquals(0, sourceFileMethod.getAnnotation(RuleVersion.class).value());

		Method idMethod = parserClass.getDeclaredMethod("id");
		Assert.assertNotNull(idMethod);
		Assert.assertNotNull(idMethod.getAnnotation(RuleVersion.class));
		Assert.assertEquals(1, idMethod.getAnnotation(RuleVersion.class).value());

		// make sure these are the only methods with the RuleVersion annotation
		for (Method method : parserClass.getDeclaredMethods()) {
			if (method.equals(sourceFileMethod) || method.equals(idMethod)) {
				continue;
			}

			Assert.assertNull(method.getAnnotation(RuleVersion.class));
		}
	}

	/**
	 * This test verifies that the {@link RuleDependencyProcessor} annotation
	 * processor is enabled by default while compiling Java code. It functions
	 * by watching for the build to write a standard message indicating the
	 * number of rule dependencies which are being validated.
	 */
	@Test
	public void testProcessorEnabled() throws IOException {
		String grammarFileName = "Simple.g4";
		String body = load(grammarFileName, null);
		String[] extraOptionsArray = { "-Werror", "-rule-versioning" };
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName, body, "SimpleParser", "SimpleLexer", true, extraOptionsArray);
		Assert.assertTrue(success);

		String sourceFile = load("TestProcessorEnabled.java.test", null);
		assertNotNullOrEmpty(sourceFile);

		writeFile(tmpdir, "TestProcessorEnabled.java", sourceFile);
		StringWriter writer = new StringWriter();
		success = compile(writer, "TestProcessorEnabled.java");
		Assert.assertTrue(success);
		Assert.assertEquals("Note: ANTLR 4: Validating 1 dependencies on rules in SimpleParser." + newline, writer.getBuffer().toString());
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
}

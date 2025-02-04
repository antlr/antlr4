package org.antlr.v4.test.tool;

import org.antlr.v4.Tool;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class TestSplitParser {

	@Test
	public void testGeneratesDefaultFiles() throws Exception {
		Set<String> created = generate();
		assert created.size() == 4;
		assert created.contains("JavaLexer.java");
		assert created.contains("JavaParser.java");
		assert created.contains("JavaBaseListener.java");
		assert created.contains("JavaListener.java");
	}

	@Test
	public void testGeneratesNoListenerFiles() throws Exception {
		Set<String> created = generate("-no-listener");
		assert created.size() == 2;
		assert created.contains("JavaLexer.java");
		assert created.contains("JavaParser.java");
	}

	@Test
	public void testGeneratesVisitorFiles() throws Exception {
		Set<String> created = generate("-visitor");
		assert created.size() == 6;
		assert created.contains("JavaLexer.java");
		assert created.contains("JavaParser.java");
		assert created.contains("JavaBaseListener.java");
		assert created.contains("JavaListener.java");
		assert created.contains("JavaBaseVisitor.java");
		assert created.contains("JavaVisitor.java");
	}

	@Test
	public void testGeneratesSplitFiles() throws Exception {
		Set<String> created = generate("-split-context", "-no-listener");
		assert created.size() == 3;
		assert created.contains("JavaLexer.java");
		assert created.contains("JavaParser.java");
		assert created.contains("JavaParserContexts.java");
	}

	Set<String> generate(String ... args) throws IOException {
		List<String> options = new ArrayList<>(Arrays.asList(args));
		Path outDir = Files.createTempDirectory("antlr4-tool-test-").toAbsolutePath();
		String grammar = getClass().getPackageName().replace(".", "/") + "/Java.g4";
		URL url = Thread.currentThread().getContextClassLoader().getResource(grammar);
		assert url!=null;
		String path = url.getPath();
		options.add("-o");
		options.add(outDir.toString());
		options.add(path);
		Tool antlr = new Tool(options.toArray(new String[] {}));
		antlr.processGrammarsOnCommandLine();
		return Files.list(outDir)
			.map(Path::getFileName)
			.map(Object::toString)
			.filter(name -> name.endsWith(".java"))
			.collect(Collectors.toSet());

	}
}

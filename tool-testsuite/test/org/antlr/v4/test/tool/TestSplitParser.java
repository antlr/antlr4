package org.antlr.v4.test.tool;

import org.antlr.v4.Tool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.antlr.v4.test.runtime.RuntimeTestUtils.PathSeparator;


public class TestSplitParser {

	JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	Path outDir = null;

	@BeforeEach
	public void beforeEach() throws IOException {
		outDir = Files.createTempDirectory("antlr4-tool-test-").toAbsolutePath();
	}

	@Test
	public void testGeneratesDefaultFiles() throws Exception {
		Set<String> created = generate();
		assert created.size() == 4;
		assert created.contains("JavaLexer.java");
		assert created.contains("JavaParser.java");
		assert created.contains("JavaBaseListener.java");
		assert created.contains("JavaListener.java");
		checkCompiles(created);
	}

	@Test
	public void testGeneratesNoListenerFiles() throws Exception {
		Set<String> created = generate("-no-listener");
		assert created.size() == 2;
		assert created.contains("JavaLexer.java");
		assert created.contains("JavaParser.java");
		checkCompiles(created);
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
		checkCompiles(created);
	}

	@Test
	public void testGeneratesSplitFiles() throws Exception {
		Set<String> created = generate("-split-parser", "-no-listener");
		assert created.size() == 4;
		assert created.contains("JavaLexer.java");
		assert created.contains("JavaParser.java");
		assert created.contains("JavaParserContexts.java");
		assert created.contains("JavaParserDFA.java");
		String content = Files.readString(Path.of(outDir.toString(), "JavaParser.java"));
		assert !content.contains("ATNDeserializer()");
		assert !content.contains("extends ParserRuleContext");
		content = Files.readString(Path.of(outDir.toString(), "JavaParserContexts.java"));
		assert !content.contains("ATNDeserializer()");
		assert content.contains("extends ParserRuleContext");
		content = Files.readString(Path.of(outDir.toString(), "JavaParserDFA.java"));
		assert content.contains("ATNDeserializer()");
		assert !content.contains("extends ParserRuleContext");
		checkCompiles(created);
	}

	Set<String> generate(String ... args) throws IOException {
		List<String> options = new ArrayList<>(Arrays.asList(args));
		String grammar = getClass().getPackageName().replace(".", "/") + "/Java.g4";
		URL url = Thread.currentThread().getContextClassLoader().getResource(grammar);
		assert url!=null;
		String path = url.getPath();
		options.add("-o");
		options.add(outDir.toString());
		options.add("-package");
		options.add("antlr4.split");
		options.add(path);
		Tool antlr = new Tool(options.toArray(new String[] {}));
		antlr.processGrammarsOnCommandLine();
		return Files.list(outDir)
			.map(Path::getFileName)
			.map(Object::toString)
			.filter(name -> name.endsWith(".java"))
			.collect(Collectors.toSet());

	}

	void checkCompiles(Set<String> generated) throws Exception, ClassNotFoundException {
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		List<File> files = generated.stream()
			.map(name -> Path.of(outDir.toString(), name))
			.map(Path::toFile)
			.collect(Collectors.toList());
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(files);
		Iterable<String> compileOptions =
			Arrays.asList("-g", "-source", "1.8", "-target", "1.8", "-implicit:class", "-Xlint:-options", "-d",
				outDir.toString(), "-cp", outDir + PathSeparator + System.getProperty("java.class.path"));
		JavaCompiler.CompilationTask task =
			compiler.getTask(null, fileManager, null, compileOptions, null,
				compilationUnits);
		task.call();
		ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
		ClassLoader loader = new URLClassLoader(new URL[]{outDir.toUri().toURL()}, systemClassLoader);
		for (String name : generated) {
			name = "antlr4.split." + name.substring(0, name.indexOf(".java"));
			Class<?> klass = loader.loadClass(name);
			assert klass != null;
		}
	}
}

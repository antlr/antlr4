package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.test.runtime.java.api.JavaLexer;
import org.antlr.v4.test.runtime.java.api.JavaParser;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSuggestionHelper {

	@Test
	public void matchesEachExpectation() throws Exception {
		List<Pair<Path, Error>> errors = new ArrayList<>();
		URL url = this.getClass().getResource("/org/antlr/v4/test/runtime/expected-tokens/");
		try(Stream<Path> walk = Files.walk(Paths.get(url.toURI()), 1)) {
			for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
				Path path = it.next();
				if(path.toString().endsWith(".yml")) try {
					matchesExpectation(path);
				} catch(AssertionFailedError e) {
					errors.add(new Pair<>(path, e));
				}
			}
		}
		// fail the test if required
		if(!errors.isEmpty())
			throw errors.get(0).b;
	}

	static class Expectation {
		public String input;
		public int line;
		public int column;
		public Set<String> expectedTokens;

	}
	void matchesExpectation(Path expectations) throws Exception {
		Expectation expectation = parseExpectation(expectations);
		CharStream input = CharStreams.fromString(expectation.input);
		Lexer lexer = new JavaLexer(input);
		lexer.setTokenFactory(CommonTokenWithStatesFactory.DEFAULT);
		TokenStream stream = new CommonTokenStream(lexer);
		JavaParser parser = new JavaParser(stream);
		parser.setStateListener(new TokenStateRecorder(stream));
		RuleContext context = parser.compilationUnit();
		Pair<RuleContext, IntervalSet> contextsAndIntervals = parser.getExpectedTokens(context, expectation.line, expectation.column);
		Set<String> actual = contextsAndIntervals.b.toSet().stream()
			.map(t -> parser.getVocabulary().getDisplayName(t))
			.map(s -> s.startsWith("'") ? s.substring(1, s.length() - 1) : s)
			.collect(Collectors.toSet());
		assertEquals(expectation.expectedTokens, actual);
	}

	static Expectation parseExpectation(Path expectationsFile) throws Exception {
		Yaml yaml = new Yaml();
		try(InputStream input = Files.newInputStream(expectationsFile)) {
			Map<String, Object> doc = yaml.load(input);
			Expectation expectation = new Expectation();
			expectation.input = doc.getOrDefault("input", "").toString();
			Object value = doc.get("caret");
			if(value instanceof Map) {
				expectation.line = (int) ((Map<String, Object>) value).get("line");
				expectation.column = (int) ((Map<String, Object>) value).get("column");
			}
			value = doc.get("expected");
			if(value instanceof List) {
				expectation.expectedTokens = new HashSet<>(((List<String>) value));
			}
			return expectation;
		}
	}

}

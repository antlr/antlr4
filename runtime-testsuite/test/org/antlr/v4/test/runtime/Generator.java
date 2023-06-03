/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import org.antlr.v4.Tool;
import org.antlr.v4.test.runtime.java.JavaRunner;
import org.antlr.v4.test.runtime.states.GeneratedState;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.antlr.v4.test.runtime.FileUtils.writeFile;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.FileSeparator;

public class Generator {
	private final static Pattern grammarHeaderRegex = Pattern.compile("^((?<grammarType>(parser|lexer))\\s+)?grammar\\s+(?<grammarName>[^;\\s]+)\\s*;");

	public static GeneratedState generate(RunOptions runOptions, String language, String workingDirectory, String[] extraOptions) {
		if (runOptions.slaveGrammars != null) {
			for (String grammar : runOptions.slaveGrammars) {
				GrammarFile slaveGrammarFile = parseGrammarFile(grammar);
				writeFile(workingDirectory, slaveGrammarFile.grammarName + ".g4", slaveGrammarFile.content);
			}
		}

		List<GrammarFile> grammarFiles = new ArrayList<>();
		for (String grammar : runOptions.grammars) {
			grammarFiles.add(parseGrammarFile(grammar));
		}

		GrammarFile mainFile = grammarFiles.get(0);
		String mainGrammarName = mainFile.grammarName;

		final List<String> options = new ArrayList<>();

		for (GrammarFile grammarFile : grammarFiles) {
			writeFile(workingDirectory, grammarFile.grammarName + ".g4", grammarFile.content);

			if (grammarFile == mainFile) continue;

			// Generate dependent recognizers at first
			options.add(grammarFile.grammarName + ".g4");
		}

		// Generate the main recognizer at last
		options.add(mainFile.grammarName + ".g4");

		if (runOptions.useVisitor) {
			options.add("-visitor");
		}

		String superClass = runOptions.superClass;
		if (runOptions.superClass != null) {
			superClass = runOptions.superClass;
		}

		if (superClass == null && language != null && language.equals("Java")) {
			if (mainFile.containsParser()) {
				superClass = JavaRunner.runtimeTestParserName;
			}
			else {
				superClass = JavaRunner.runtimeTestLexerName;
			}
		}

		if (superClass != null) {
			options.add("-DsuperClass=" + superClass);
		}

		if (extraOptions != null) {
			options.addAll(Arrays.asList(extraOptions));
		}

		String[] extraGenerationOptions = runOptions.extraGenerationOptions;
		if (extraGenerationOptions != null) {
			options.addAll(Arrays.asList(extraGenerationOptions));
		}

		boolean isGo = language != null && language.equals("Go");
		String outputDirectory;

		if (!options.contains("-o")) {
			outputDirectory = isGo ? workingDirectory + FileSeparator + "parser" : workingDirectory;
		}
		else {
			outputDirectory = options.get(options.indexOf("-o") + 1);
		}
		options.add("-o");
		options.add(outputDirectory);

		if (!options.contains("-lib")) {
			options.add("-lib");
			options.add(workingDirectory);
		}
		if (!options.contains("-encoding")) {
			options.add("-encoding");
			options.add("UTF-8");
		}
		if (!options.contains("-Dlanguage=") && language != null) {
			options.add("-Dlanguage=" + language);
		}

		Tool antlr = new Tool(options.toArray(new String[0]));
		ErrorQueue errorQueue = new ErrorQueue(antlr);
		antlr.inputDirectory = new File(workingDirectory);
		antlr.addListener(errorQueue);
		antlr.processGrammarsOnCommandLine();

		LexerParserName lexerParserName = new LexerParserName(null, null);
		List<GeneratedFile> generatedFiles = new ArrayList<>();

		language = language != null ? language : "Java";
		if (errorQueue.errors.isEmpty()) {
			lexerParserName = getLexerParserName(mainFile, grammarFiles);
			generatedFiles = getGeneratedFiles(runOptions, language, mainGrammarName, outputDirectory, lexerParserName);
		}

		return new GeneratedState(grammarFiles, errorQueue, lexerParserName.lexerName, lexerParserName.parserName, generatedFiles, null);
	}

	private static GrammarFile parseGrammarFile(String input) {
		Matcher matcher = grammarHeaderRegex.matcher(input);

		GrammarType grammarType = GrammarType.Combined;
		String grammarName = null;

		if (matcher.find()) {
			String grammarTypeValue = matcher.group("grammarType");
			grammarName = matcher.group("grammarName");

			if (grammarTypeValue != null) {
				grammarType = grammarTypeValue.equals("parser") ? GrammarType.Parser : GrammarType.Lexer;
			}
		}

		return new GrammarFile(grammarName, grammarType, input);
	}

	private static LexerParserName getLexerParserName(GrammarFile mainFile, List<GrammarFile> grammarFiles) {
		String lexerName;
		String parserName = null;

		if (mainFile.type == GrammarType.Combined) {
			lexerName = mainFile.grammarName + "Lexer";
			parserName = mainFile.grammarName + "Parser";
		}
		else if (mainFile.type == GrammarType.Lexer) {
			lexerName = mainFile.grammarName;
		}
		else {
			parserName = mainFile.grammarName;
			Optional<GrammarFile> lexerFile = grammarFiles.stream().filter(g -> g.type == GrammarType.Lexer).findFirst();
			lexerName = lexerFile.map(grammarFile -> grammarFile.grammarName).orElse(null);
		}

		return new LexerParserName(lexerName, parserName);
	}

	private static class LexerParserName {
		public String lexerName;
		public String parserName;

		private LexerParserName(String lexerName, String parserName) {
			this.lexerName = lexerName;
			this.parserName = parserName;
		}
	}

	private static List<GeneratedFile> getGeneratedFiles(RunOptions runOptions, String language, String mainGrammarName, String outputDirectory, LexerParserName lexerParserName) {
		List<GeneratedFile> generatedFiles = new ArrayList<>();

		String extensionWithDot = "." + getExtension(language);

		boolean isGo = language.equals("Go");

		if (lexerParserName.lexerName != null) {
			String generateLexerFileName = getRecognizerFileName(isGo, lexerParserName.lexerName, "lexer") + extensionWithDot;
			if (Files.exists(new File(outputDirectory, generateLexerFileName).toPath())) {
				generatedFiles.add(new GeneratedFile(generateLexerFileName, false));
			}
			else {
				lexerParserName.lexerName = null;
			}
		}

		if (lexerParserName.parserName != null) {
			String generatedParserFileName = getRecognizerFileName(isGo, lexerParserName.parserName, "parser") + extensionWithDot;
			generatedFiles.add(new GeneratedFile(generatedParserFileName, true));

			String normalizedGrammarName = isGo ? mainGrammarName.toLowerCase() : mainGrammarName;
			if (runOptions.useListener) {
				generatedFiles.add(new GeneratedFile(normalizedGrammarName + getListenerSuffix(language) + extensionWithDot, null));
				String baseListenerSuffix = getBaseListenerSuffix(language);
				if (baseListenerSuffix != null) {
					generatedFiles.add(new GeneratedFile(normalizedGrammarName + baseListenerSuffix + extensionWithDot, null));
				}
			}
			if (runOptions.useVisitor) {
				generatedFiles.add(new GeneratedFile(normalizedGrammarName + getVisitorSuffix(language) + extensionWithDot, null));
				String baseVisitorSuffix = getBaseVisitorSuffix(language);
				if (baseVisitorSuffix != null) {
					generatedFiles.add(new GeneratedFile(normalizedGrammarName + baseVisitorSuffix + extensionWithDot, null));
				}
			}
		}

		for (GeneratedFile generatedFile : generatedFiles) {
			if (!Files.exists(new File(outputDirectory, generatedFile.name).toPath())) {
				throw new RuntimeException("Generated file " + generatedFile.name + " should exist");
			}
		}

		return generatedFiles;
	}

	private static String getRecognizerFileName(boolean isGo, String name, String suffix) {
		if (isGo) {
			name = name.toLowerCase();

			if (name.endsWith(suffix))
				name = name.substring(0, name.length() - suffix.length());

			return name + "_" + suffix;
		}

		return name;
	}

	public static String getExtension(String language) {
		switch (language) {
			case "CSharp":
				return "cs";
			case "JavaScript":
				return "js";
			case "TypeScript":
				return "ts";
			case "Python2":
			case "Python3":
				return "py";
			default:
				return language.toLowerCase();
		}
	}

	private static String getBaseListenerSuffix(String language) {
		switch (language) {
			case "JavaScript":
			case "TypeScript":
			case "Python2":
			case "Python3":
				return null;
			case "Go":
				return "_base_listener";
			default:
				return "BaseListener";
		}
	}

	private static String getListenerSuffix(String language) { return language.equals("Go") ?  "_listener" : "Listener"; }

	private static String getBaseVisitorSuffix(String language) {
		switch (language) {
			case "JavaScript":
			case "TypeScript":
			case "Python2":
			case "Python3":
				return null;
			case "Go":
				return "_base_visitor";
			default:
				return "BaseVisitor";
		}
	}

	private static String getVisitorSuffix(String language) { return language.equals("Go") ? "_visitor" :  "Visitor"; }
}

/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import org.antlr.v4.runtime.misc.Pair;

import java.net.URI;
import java.util.*;

public class RuntimeTestDescriptorParser {
	private final static Set<String> sections = new HashSet<>(Arrays.asList(
			"notes", "type", "grammar", "slaveGrammar", "start", "input", "output", "errors", "flags", "skip", "codeDeclaration", "codeCall"
	));

	private final static Set<String> runtimes = new HashSet<>(Arrays.asList(
			"CSharp", "Java", "Cpp", "Dart", "Go", "JavaScript", "Php", "Python2", "Python3", "Swift"
	));

	/**  Read stuff like:
	 [grammar]
	 grammar T;
	 s @after {<DumpDFA()>}
	 : ID | ID {} ;
	 ID : 'a'..'z'+;
	 WS : (' '|'\t'|'\n')+ -> skip ;

	 [grammarName]
	 T

	 [start]
	 s

	 [input]
	 abc

	 [output]
	 Decision 0:
	 s0-ID->:s1^=>1

	 [errors]
	 """line 1:0 reportAttemptingFullContext d=0 (s), input='abc'
	 """

	 Some can be missing like [errors].

	 Get gr names automatically "lexer grammar Unicode;" "grammar T;" "parser grammar S;"

	 Also handle slave grammars:

	 [grammar]
	 grammar M;
	 import S,T;
	 s : a ;
	 B : 'b' ; // defines B from inherited token space
	 WS : (' '|'\n') -> skip ;

	 [slaveGrammar]
	 parser grammar T;
	 a : B {<writeln("\"T.a\"")>};<! hidden by S.a !>

	 [slaveGrammar]
	 parser grammar S;
	 a : b {<writeln("\"S.a\"")>};
	 b : B;
	 */
	public static RuntimeTestDescriptor parse(String name, String text, URI uri) throws RuntimeException {
		String currentField = null;
		StringBuilder currentValue = new StringBuilder();

		List<Pair<String, String>> pairs = new ArrayList<>();
		String[] lines = text.split("\r?\n");

		for (String line : lines) {
			boolean newSection = false;
			String sectionName = null;
			if (line.startsWith("[") && line.length() > 2 && line.endsWith("]")) {
				sectionName = line.substring(1, line.length() - 1);
				newSection = sections.contains(sectionName) || runtimes.contains(sectionName);
			}

			if (newSection) {
				if (currentField != null) {
					pairs.add(new Pair<>(currentField, currentValue.toString()));
				}
				currentField = sectionName;
				currentValue.setLength(0);
			}
			else {
				currentValue.append(line);
				currentValue.append("\n");
			}
		}
		pairs.add(new Pair<>(currentField, currentValue.toString()));

		String notes = "";
		TestType testType = TestType.Lexer;
		String grammar = "";
		String grammarName = "";
		List<Pair<String, String>> slaveGrammars = new ArrayList<>();
		String startRule = "";
		String input = "";
		String output = "";
		String errors = "";
		boolean showDFA = false;
		boolean showDiagnosticErrors = false;
		String[] skipTargets = new String[0];
		boolean isCodeDeclaration = false;
		Map<String, String> codeDeclarations = new HashMap<>();
		Map<String, String> codeCalls = new HashMap<>();

		for (Pair<String,String> p : pairs) {
			String section = p.a;
			String value = "";
			if ( p.b!=null ) {
				value = p.b.trim();
			}
			if ( value.startsWith("\"\"\"") ) {
				value = value.replace("\"\"\"", "");
			}
			else if ( value.indexOf('\n')>=0 ) {
				value = value + "\n"; // if multi line and not quoted, leave \n on end.
			}
			switch (section) {
				case "notes":
					notes = value;
					continue;
				case "type":
					testType = Enum.valueOf(TestType.class, value);
					continue;
				case "grammar":
					grammarName = getGrammarName(value.split("\n")[0]);
					grammar = value;
					continue;
				case "slaveGrammar":
					String gname = getGrammarName(value.split("\n")[0]);
					slaveGrammars.add(new Pair<>(gname, value));
				case "start":
					startRule = value;
					continue;
				case "input":
					input = value;
					continue;
				case "output":
					output = value;
					continue;
				case "errors":
					errors = value;
					continue;
				case "flags":
					String[] flags = value.split("\n");
					for (String f : flags) {
						switch (f) {
							case "showDFA":
								showDFA = true;
								continue;
							case "showDiagnosticErrors":
								showDiagnosticErrors = true;
								continue;
							default:
								break;
						}
					}
					continue;
				case "skip":
					skipTargets = value.split("\\s");
					continue;
				case "codeDeclaration":
					isCodeDeclaration = true;
					continue;
				case "codeCall":
					isCodeDeclaration = false;
					continue;
			}
			if (runtimes.contains(section)) {
				if (isCodeDeclaration) {
					codeDeclarations.put(section, value);
				} else {
					codeCalls.put(section, value);
				}
			} else {
				throw new RuntimeException("Unknown descriptor section ignored: " + section);
			}
		}

		Map<String, ExtraRuntimeCode> extraRuntimeCode = new HashMap<>();
		for (String runtime : runtimes) {
			String codeDeclaration = codeDeclarations.get(runtime);
			String codeCall = codeCalls.get(runtime);

			if (codeDeclaration != null) {
				if (codeCall == null) {
					throw new RuntimeException("codeCall section must be defined is codeDeclaration is defined for runtime: " + runtime);
				}
			}

			if (codeCall != null) {
				extraRuntimeCode.put(runtime, new ExtraRuntimeCode(codeDeclaration, codeCall));
			}
		}

		return new RuntimeTestDescriptor(testType, name, notes, input, output, errors, startRule, grammarName, grammar,
				slaveGrammars, showDFA, showDiagnosticErrors, skipTargets, extraRuntimeCode, uri);
	}

	/** Get A, B, or C from:
	 * "lexer grammar A;" "grammar B;" "parser grammar C;"
	 */
	private static String getGrammarName(String grammarDeclLine) {
		int gi = grammarDeclLine.indexOf("grammar ");
		if ( gi<0 ) {
			return "<unknown grammar name>";
		}
		gi += "grammar ".length();
		int gsemi = grammarDeclLine.indexOf(';');
		return grammarDeclLine.substring(gi, gsemi);
	}
}

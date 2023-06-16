/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.Pair;

import java.net.URI;
import java.util.*;

public class RuntimeTestDescriptorParser {
	private final static String notesSectionName = "notes";
	private final static String grammarSectionName = "grammar";
	private final static String lexerGrammarSectionName = "lexerGrammar";
	private final static String slaveGrammarSectionName = "slaveGrammar";
	private final static String startSectionName = "start";
	private final static String inputSectionName = "input";
	private final static String outputSectionName = "output";
	private final static String errorsSectionName = "errors";
	private final static String flagsSectionName = "flags";
	private final static String skipSectionName = "skip";

	private final static Set<String> sections = new HashSet<>(Arrays.asList(
		notesSectionName,
		grammarSectionName,
		lexerGrammarSectionName,
		slaveGrammarSectionName,
		startSectionName,
		inputSectionName,
		outputSectionName,
		errorsSectionName,
		flagsSectionName,
		skipSectionName
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
			if (line.startsWith("[") && line.length() > 2) {
				sectionName = line.substring(1, line.length() - 1);
				newSection = sections.contains(sectionName);
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
		List<String> grammars = new ArrayList<>();
		List<String> slaveGrammars = new ArrayList<>();
		String startRule = "";
		String input = "";
		String output = "";
		String errors = "";
		boolean showDFA = false;
		boolean showDiagnosticErrors = false;
		boolean traceATN = false;
		PredictionMode predictionMode = PredictionMode.LL;
		boolean buildParseTree = true;
		String[] skipTargets = new String[0];
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
				case notesSectionName:
					notes = value;
					break;
				case grammarSectionName:
					grammars.add(value);
					break;
				case slaveGrammarSectionName:
					slaveGrammars.add(value);
					break;
				case startSectionName:
					startRule = value;
					break;
				case inputSectionName:
					input = value;
					break;
				case outputSectionName:
					output = value;
					break;
				case errorsSectionName:
					errors = value;
					break;
				case flagsSectionName:
					String[] flags = value.split("\n");
					for (String f : flags) {
						String[] parts = f.split("=", 2);
						switch (parts[0]) {
							case "showDFA":
								showDFA = true;
								break;
							case "showDiagnosticErrors":
								showDiagnosticErrors = true;
								break;
							case "traceATN":
								traceATN = true;
								break;
							case "predictionMode":
								predictionMode = PredictionMode.valueOf(parts[1]);
								break;
							case "notBuildParseTree":
								buildParseTree = false;
								break;
							default:
								throw new RuntimeException("Unknown flag: " + parts[0]);
						}
					}
					break;
				case skipSectionName:
					skipTargets = value.split("\n");
					break;
				default:
					throw new RuntimeException("Unknown descriptor section ignored: "+section);
			}
		}
		return new RuntimeTestDescriptor(name, notes, input, output, errors, startRule,
				grammars.toArray(new String[0]), slaveGrammars.toArray(new String[0]),
				showDiagnosticErrors, traceATN, showDFA, predictionMode, buildParseTree, skipTargets, uri);
	}
}

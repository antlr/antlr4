package org.antlr.v4.testgen;

import java.util.Arrays;
import java.util.List;

public class TargetConfiguration {
	
	public static List<TargetConfiguration> ALL = Arrays.asList( new TargetConfiguration[] {
		new TargetConfiguration("tool/test/", 
					"tool/test/org/antlr/v4/test/runtime/java/Java.test.stg"),
		new TargetConfiguration("../antlr4-csharp/tool/test/", 
					"../antlr4-csharp/tool/test/org/antlr/v4/test/runtime/csharp/CSharp.test.stg"),
		new TargetConfiguration("../antlr4-python2/tool/test/", 
				"../antlr4-python2/tool/test/org/antlr/v4/test/runtime/python2/Python2.test.stg"),
		new TargetConfiguration("../antlr4-python3/tool/test/", 
				"../antlr4-python3/tool/test/org/antlr/v4/test/runtime/python3/Python3.test.stg"),
		new TargetConfiguration("../antlr4-javascript/tool/test/", 
				"../antlr4-javascript/tool/test/org/antlr/v4/test/runtime/javascript/node/Node.test.stg"),
		new TargetConfiguration("../antlr4-javascript/tool/test/", 
				"../antlr4-javascript/tool/test/org/antlr/v4/test/runtime/javascript/chrome/Chrome.test.stg"),
		new TargetConfiguration("../antlr4-javascript/tool/test/", 
				"../antlr4-javascript/tool/test/org/antlr/v4/test/runtime/javascript/safari/Safari.test.stg"),
		new TargetConfiguration("../antlr4-javascript/tool/test/", 
				"../antlr4-javascript/tool/test/org/antlr/v4/test/runtime/javascript/firefox/Firefox.test.stg"),
		new TargetConfiguration("../antlr4-javascript/tool/test/", 
				"../antlr4-javascript/tool/test/org/antlr/v4/test/runtime/javascript/explorer/Explorer.test.stg")
		});
	
	String outDir;
	String templates;

	public TargetConfiguration(String outDir, String templates) {
		this.outDir = outDir;
		this.templates = templates;
	}
}

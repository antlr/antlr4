package org.antlr.v4.runtime;

public interface LexerScannerIncludeSource {
	
	public CharStream embedSource(String fileName, String substituteFrom, String substituteTo);
	

}

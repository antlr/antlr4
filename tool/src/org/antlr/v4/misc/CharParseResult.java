package org.antlr.v4.misc;

import org.antlr.v4.tool.ErrorSeverity;

public class CharParseResult {
	public ErrorSeverity errorSeverity;
	public String stringChar = null;
	public int intChar = -1;

	public static CharParseResult ErrorResult = new CharParseResult(ErrorSeverity.ERROR, null);

	public CharParseResult(ErrorSeverity errorSeverity, String stringChar) {
		this.errorSeverity = errorSeverity;
		this.stringChar = stringChar;
	}

	public CharParseResult(ErrorSeverity errorSeverity, int intChar) {
		this.errorSeverity = errorSeverity;
		this.intChar = intChar;
	}

	public CharParseResult(int intChar) {
		this.errorSeverity = ErrorSeverity.INFO;
		this.intChar = intChar;
	}
}

package org.antlr.v4.tool;

public class AnalysisTimeoutMessage extends Message {
	public AnalysisTimeoutMessage() {
		super(ErrorType.ANALYSIS_TIMEOUT);
	}
}

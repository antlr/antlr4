package org.antlr.v4.test.runtime;

public abstract class BaseDebugParserTestDescriptor extends BaseParserTestDescriptor {
	@Override
	public boolean showDiagnosticErrors() {
		return true;
	}
}

package org.antlr.v4.test.runtime;

public class GeneratedFile {
	public final String name;
	public final Boolean isParser;

	public GeneratedFile(String name, Boolean isParser) {
		this.name = name;
		this.isParser = isParser;
	}

	@Override
	public String toString() {
		return name + "; isParser:" + isParser;
	}
}

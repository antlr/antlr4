package org.antlr.v4.codegen;

public enum Language {
	Java("Java"),
	CSharp("C#"),
	Python2("Python2"),
	Python3("Python3"),
	JavaScript("JavaScript"),
	Cpp("C++"),
	Go("Go"),
	Swift("Swift"),
	PHP("PHP"),
	Dart("Dart");

	Language(String name) {
		this.languageName = name;
	}

	public final String languageName;
}

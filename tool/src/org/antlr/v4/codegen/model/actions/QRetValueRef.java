package org.antlr.v4.codegen.model.actions;

/** */
public class QRetValueRef extends RetValueRef {
	public String dict;
	public QRetValueRef(String dict, String name) {
		super(name);
		this.dict = dict;
	}
}

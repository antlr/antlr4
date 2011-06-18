package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;

/** */
public class AddToList extends SrcOp {
	public String listName;
	public LabeledOp opWithResultToAdd;

	public AddToList(OutputModelFactory factory, String listName, LabeledOp opWithResultToAdd) {
		super(factory);
		this.listName = listName;
		this.opWithResultToAdd = opWithResultToAdd;
	}
}

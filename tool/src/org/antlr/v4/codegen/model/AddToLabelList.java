package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;

/** */
public class AddToLabelList extends SrcOp {
	public String listName;
	public LabeledOp opWithResultToAdd;

	public AddToLabelList(OutputModelFactory factory, String listName, LabeledOp opWithResultToAdd) {
		super(factory);
		this.listName = listName;
		this.opWithResultToAdd = opWithResultToAdd;
	}
}

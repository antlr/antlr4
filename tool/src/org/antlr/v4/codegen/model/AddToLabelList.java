package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.CoreOutputModelFactory;

/** */
public class AddToLabelList extends SrcOp {
	public String listName;
	public LabeledOp opWithResultToAdd;

	public AddToLabelList(CoreOutputModelFactory factory, String listName, LabeledOp opWithResultToAdd) {
		super(factory);
		this.listName = listName;
		this.opWithResultToAdd = opWithResultToAdd;
	}
}

package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;

import java.util.ArrayList;
import java.util.List;

/** */
public class ParserFile extends OutputModelObject {
	public String fileName;
	public Parser parser;
	public List<DFADef> dfaDefs = new ArrayList<DFADef>();
	public List<BitSetDef> bitSetDefs = new ArrayList<BitSetDef>();
	
	public ParserFile(OutputModelFactory factory, String fileName) {
		super(factory);
		this.fileName = fileName; 
	}

	public void defineBitSet(BitSetDef b) {
		bitSetDefs.add(b);
	}

	@Override
	public List<String> getChildren() {
		final List<String> sup = super.getChildren();
		return new ArrayList<String>() {{
			if ( sup!=null ) addAll(sup);
			add("parser");
			add("dfaDefs");
			add("bitSetDefs");
		}};
	}
}

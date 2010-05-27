package org.antlr.v4.codegen;

import org.antlr.runtime.Token;
import org.antlr.v4.codegen.pda.Instr;
import org.antlr.v4.misc.DoubleKeyMap;
import org.antlr.v4.tool.Rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** */
public class CompiledPDA {
	public List<Instr> instrs = new ArrayList<Instr>();
	public byte[] code; // instrs in bytecode form
	public int ip = 0; // where to write next
	public Map<String, Integer> ruleToAddr = new HashMap<String, Integer>();
	public int[] tokenTypeToAddr;

	public DoubleKeyMap<Rule, String, Integer> ruleLabels = new DoubleKeyMap<Rule, String, Integer>();
	public DoubleKeyMap<Rule, Token, Integer> ruleActions = new DoubleKeyMap<Rule, Token, Integer>();
	public DoubleKeyMap<Rule, Token, Integer> ruleSempreds = new DoubleKeyMap<Rule, Token, Integer>();
	public int nLabels;
}

package org.antlr.v4.automata;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;

public class RangeBorderCharactersData {
	public final int lowerFrom;
	public final int upperFrom;
	public final int lowerTo;
	public final int upperTo;
	public final boolean mixOfLowerAndUpperCharCase;

	public RangeBorderCharactersData(int lowerFrom, int upperFrom, int lowerTo, int upperTo, boolean mixOfLowerAndUpperCharCase) {
		this.lowerFrom = lowerFrom;
		this.upperFrom = upperFrom;
		this.lowerTo = lowerTo;
		this.upperTo = upperTo;
		this.mixOfLowerAndUpperCharCase = mixOfLowerAndUpperCharCase;
	}

	public static RangeBorderCharactersData getAndCheckCharactersData(int from, int to, Grammar grammar, CommonTree tree,
																	  boolean reportRangeContainsNotImpliedCharacters
	) {
		int lowerFrom = Character.toLowerCase(from);
		int upperFrom = Character.toUpperCase(from);
		int lowerTo = Character.toLowerCase(to);
		int upperTo = Character.toUpperCase(to);

		boolean isLowerFrom = lowerFrom == from;
		boolean isLowerTo = lowerTo == to;
		boolean mixOfLowerAndUpperCharCase = isLowerFrom && !isLowerTo || !isLowerFrom && isLowerTo;
		if (reportRangeContainsNotImpliedCharacters && mixOfLowerAndUpperCharCase && from <= 0x7F && to <= 0x7F) {
			StringBuilder notImpliedCharacters = new StringBuilder();
			for (int i = from; i < to; i++) {
				if (!Character.isAlphabetic(i)) {
					notImpliedCharacters.append((char)i);
				}
			}
			if (notImpliedCharacters.length() > 0) {
				grammar.tool.errMgr.grammarError(ErrorType.RANGE_PROBABLY_CONTAINS_NOT_IMPLIED_CHARACTERS, grammar.fileName, tree.getToken(),
						(char) from, (char) to, notImpliedCharacters.toString());
			}
		}
		return new RangeBorderCharactersData(lowerFrom, upperFrom, lowerTo, upperTo, mixOfLowerAndUpperCharCase);
	}

	public boolean isSingleRange() {
		return lowerFrom == upperFrom && lowerTo == upperTo ||
				mixOfLowerAndUpperCharCase ||
				lowerTo - lowerFrom != upperTo - upperFrom;
	}
}

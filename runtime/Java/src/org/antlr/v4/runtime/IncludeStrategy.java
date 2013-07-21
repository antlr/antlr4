package org.antlr.v4.runtime;

public interface IncludeStrategy {
   public CharStream file2Stream(String inFileName, Integer outFileIdx);
}

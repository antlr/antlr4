package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Pair;

public interface IncludeStrategy {
   public  Pair<CharStream,Integer> file2Stream(String inFileName);
}

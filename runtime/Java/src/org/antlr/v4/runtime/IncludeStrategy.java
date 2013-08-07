package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Pair;

public interface IncludeStrategy {
   /** 
    * 
    * @param inFileName name of recognized filename that should be included into the current lexer stream
    * @return Pair of CharStream and a reference number of the stream
    */
   public  Pair<CharStream,Integer> fileName2StreamPair(String inFileName);
   
   /**
    *
    * @param streamRef
    * @return filename associated with the stream reference
    */
   public String getFileName(Integer streamRef);
}

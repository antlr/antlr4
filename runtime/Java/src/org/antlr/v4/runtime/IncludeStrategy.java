package org.antlr.v4.runtime;

import java.util.List;

import org.antlr.v4.runtime.misc.Pair;

public interface IncludeStrategy {
   /** 
    * 
    * @param inFileName name of recognized filename that should be included into the current lexer stream
    * @return Pair of CharStream and a reference number of the stream
    */
   public Pair<CharStream, Integer> fileName2StreamPair(String inFileName);
   public Pair<CharStream, Integer> fileName2StreamPair(String fileName,
			String substituteFrom, String substituteTo);

   
   /**
    *
    * @param streamRef
    * @return filename associated with the stream reference
    */
   public String getFileName(Integer streamRef);
   

   /**
    * The Lexer will invoke this method to when an include request is recognized
    * @param lexerIncludeRequest text that matches include request
    * @return filename to open
    */
   public String getQualifiedFileName(String lexerIncludeRequest);

   /**
    * Return the list of qualified file names that have been used for the scanning.
    * @return List<String>
    */
   public List<String>  getAllFiles();
   
   
}

package org.antlr.v4.runtime;

import java.util.ArrayList;
import java.util.HashMap;

import org.antlr.v4.runtime.misc.Pair;

public class IncludeStrategyImpl implements IncludeStrategy
	{
		/**
		 * Maps a filename to an index in the ArrayList
		 */
		HashMap<String,Integer>     filenameIndexMap   = new HashMap<String, Integer>();
		
		/**
		 * List of ANTLRInputStream.
		 * Filenames are mapped to the ANTLRInputStream using filenameIndexMap. 
		 */
		ArrayList<ANTLRInputStream> filenameContentList = new ArrayList<ANTLRInputStream>();

		public Pair<CharStream, Integer> file2StreamPair(String inFileName) {
			Integer streamRef=filenameIndexMap.get(inFileName); 
			Pair<CharStream, Integer> streamPair=new Pair<CharStream, Integer>(filenameContentList.get(streamRef),streamRef);
			return streamPair;
		}
		
		public Integer addInclude(String fileName) {
			// TODO: should create a ANTLRStream( Reader(fileName) )
			return null;
		};
		
		public Integer addInclude(String fileName,String content) {

			Integer streamRef=filenameIndexMap.get(fileName);
			if(streamRef==null){
				ANTLRInputStream istrm=new ANTLRInputStream(content);
				filenameContentList.add(istrm);
				streamRef=filenameContentList.indexOf(istrm);
				filenameIndexMap.put(fileName, streamRef);
			}
			else {
				// replace content
				filenameContentList.set(streamRef,new ANTLRInputStream(content));
			}
			return streamRef;
		};
	}

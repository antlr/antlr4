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

		public Pair<CharStream, Integer> file2Stream(String inFileName) {
			Integer outFileIdx=filenameIndexMap.get(inFileName); 
			Pair<CharStream, Integer> outPair=new Pair<CharStream, Integer>(filenameContentList.get(outFileIdx),outFileIdx);
			return outPair;
		}
		
		public Integer addInclude(String fileName) {
			// TODO: should create a ANTLRStream( Reader(fileName) )
			return null;
		};
		
		public Integer addInclude(String fileName,String content) {

			Integer ix=filenameIndexMap.get(fileName);
			if(ix==null){
				ANTLRInputStream istrm=new ANTLRInputStream(content);
				filenameContentList.add(istrm);
				ix=filenameContentList.indexOf(istrm);
				filenameIndexMap.put(fileName, ix);
			}
			else {
				// replace content
				filenameContentList.set(ix,new ANTLRInputStream(content));
			}
			return ix;
		};
	}

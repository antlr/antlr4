package org.antlr.v4.runtime;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.antlr.v4.runtime.misc.Pair;

public class IncludeStrategyImpl implements IncludeStrategy
	{
		/**
		 * Maps a filename to an index in the ArrayList
		 */
		private HashMap<String,Integer>     filenameIndexMap   = new HashMap<String, Integer>();
		
		/**
		 * List of ANTLRInputStream.
		 * Filenames are mapped to the ANTLRInputStream using filenameIndexMap. 
		 */
		private ArrayList<ANTLRInputStream> filenameContentList = new ArrayList<ANTLRInputStream>();

		/**
		 * Returns Pair<CharStream,Integer> for the inFileName
		 */
		public Pair<CharStream, Integer> file2StreamPair(String inFileName) {
			Integer streamRef=addInclude(inFileName);
			Pair<CharStream, Integer> streamPair=new Pair<CharStream, Integer>(filenameContentList.get(streamRef),streamRef);
			return streamPair;
		}
		
		/**
		 * Maintain internal data-structure mapping fileName with ANTLRInputStream
		 * @param fileName
		 * @param ais
		 * @return reference number to input stream
		 */
		protected Integer buildStreamMap(String fileName, ANTLRInputStream ais)
		{
			filenameContentList.add(ais);
			Integer streamRef = filenameContentList.indexOf(ais);
			filenameIndexMap.put(fileName, streamRef);
			//System.out.println("+++Adding include ("+streamRef+") "+fileName);
			return streamRef;
		}
		
		/**
		 * If the fileName has not been added, reads the fileName from the filesystem
		 * @param fileName
		 * @return
		 */
		public Integer addInclude(String fileName) {
			Integer streamRef=filenameIndexMap.get(fileName);
			if (streamRef == null) {
				try {
					InputStream is = new FileInputStream(fileName);
					ANTLRInputStream istrm = new ANTLRInputStream(is);
					
					return buildStreamMap(fileName,istrm);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return streamRef;
		}

		/**
		 * Adding fileName as string content.  If filename is re-added then the content will be replaced. 
		 * @param fileName
		 * @param content
		 * @return
		 */
		public Integer addInclude(String fileName, String content) {
			Integer streamRef=filenameIndexMap.get(fileName);
			if(streamRef==null){
				ANTLRInputStream istrm=new ANTLRInputStream(content);
				return buildStreamMap(fileName,istrm);
			}
			else {
				// replace content
				filenameContentList.set(streamRef,new ANTLRInputStream(content));
			}
			return streamRef;
		}
		
		public String getFileName(Integer ref)
		{
			String s="";
			int i=0;
			Iterator<String> ix=filenameIndexMap.keySet().iterator();
			while(ix.hasNext()) 
			{s=ix.next();
			//System.out.println("+++("+i+") "+s);
			 if(filenameIndexMap.get(s).equals(ref)) break;
			 i++;
			}
			
			return s;
		}
	}

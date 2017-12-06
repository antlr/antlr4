/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.misc;

import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

// A class to read plain text interpreter data produced by ANTLR.
public class InterpreterDataReader {
	
	public static class InterpreterData {
	  ATN atn;
	  Vocabulary vocabulary;
	  List<String> ruleNames;
	  List<String> channels; // Only valid for lexer grammars.
	  List<String> modes; // ditto
	};
	
	/**
	 * The structure of the data file is very simple. Everything is line based with empty lines
	 * separating the different parts. For lexers the layout is:
	 * token literal names:
	 * ...
	 * 
	 * token symbolic names:
	 * ...
	 * 
	 * rule names:
	 * ...
	 * 
	 * channel names:
	 * ...
	 * 
	 * mode names:
	 * ...
	 * 
	 * atn:
	 * <a single line with comma separated int values> enclosed in a pair of squared brackets.
	 * 
	 * Data for a parser does not contain channel and mode names.
	 */
	public static InterpreterData parseFile(String fileName) {
		InterpreterData result = new InterpreterData();
		result.ruleNames = new ArrayList<String>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
		    String line;
		  	List<String> literalNames = new ArrayList<String>();
		  	List<String> symbolicNames = new ArrayList<String>();
		
			line = br.readLine();
			if ( !line.equals("token literal names:") )
				throw new RuntimeException("Unexpected data entry");
		    while ((line = br.readLine()) != null) {
		       if ( line.isEmpty() )
					break;
				literalNames.add(line.equals("null") ? "" : line);
		    }
		
			line = br.readLine();
			if ( !line.equals("token symbolic names:") )
				throw new RuntimeException("Unexpected data entry");
		    while ((line = br.readLine()) != null) {
		       if ( line.isEmpty() )
					break;
				symbolicNames.add(line.equals("null") ? "" : line);
		    }

		  	result.vocabulary = new VocabularyImpl(literalNames.toArray(new String[0]), symbolicNames.toArray(new String[0]));

			line = br.readLine();
			if ( !line.equals("rule names:") )
				throw new RuntimeException("Unexpected data entry");
		    while ((line = br.readLine()) != null) {
		       if ( line.isEmpty() )
					break;
				result.ruleNames.add(line);
		    }
		    
			if ( line.equals("channel names:") ) { // Additional lexer data.
				result.channels = new ArrayList<String>();
			    while ((line = br.readLine()) != null) {
			       if ( line.isEmpty() )
						break;
					result.channels.add(line);
			    }

				line = br.readLine();
				if ( !line.equals("mode names:") )
					throw new RuntimeException("Unexpected data entry");
				result.modes = new ArrayList<String>();
			    while ((line = br.readLine()) != null) {
			       if ( line.isEmpty() )
						break;
					result.modes.add(line);
			    }
			}

		  	line = br.readLine();
		  	if ( !line.equals("atn:") )
		  		throw new RuntimeException("Unexpected data entry");
			line = br.readLine();
			String[] elements = line.split(",");
	  		char[] serializedATN = new char[elements.length];

			for (int i = 0; i < elements.length; ++i) {
				int value;
				String element = elements[i];
				if ( element.startsWith("[") )
					value = Integer.parseInt(element.substring(1).trim());
				else if ( element.endsWith("]") )
					value = Integer.parseInt(element.substring(0, element.length() - 1).trim());
				else
					value = Integer.parseInt(element.trim());
				serializedATN[i] = (char)value;					
			}

		  	ATNDeserializer deserializer = new ATNDeserializer();
		  	result.atn = deserializer.deserialize(serializedATN);
		}
		catch (java.io.IOException e) {
			// We just swallow the error and return empty objects instead.
		}
		
		return result;
	}
	
}

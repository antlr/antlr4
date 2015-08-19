package org.antlr.v4.runtime;

import java.io.IOException;

public class LexerScannerIncludeSourceImpl implements LexerScannerIncludeSource {

	@Override
	public CharStream embedSource(String fileName, String substituteFrom,String substituteTo) {
		ANTLRInputStream istrm=null;
		try {
			istrm = new ANTLRFileStream(fileName);
			if (substituteFrom != null) {
				String beforeStream = String.copyValueOf(istrm.data, 0,istrm.size());
				String replacedStream = beforeStream.replaceAll(substituteFrom,substituteTo);
				istrm = new ANTLRInputStream(replacedStream);
			}
		} catch (IOException e) {
			//TODO: Add error handling
			e.printStackTrace();
		}
		
		return istrm;
	}

}

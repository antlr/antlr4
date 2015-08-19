package org.antlr.v4.test.tool;

import java.io.File;
import java.io.FileWriter;

import org.antlr.v4.test.runtime.java.BaseTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestLexerIncludeActions extends BaseTest {
	// ----- ACTIONS --------------------------------------------------------

	@Test public void testActionPerformIncludeSourceFile() throws Exception {
		// prepare test files
		String path="/tmp/"; //TODO: use tmpdir variable
		String fn[] = {"test_#0.test","test_#1.test"};
		File f[] = {new File(path+fn[0]),new File(path+fn[1])};
		FileWriter f0=new FileWriter(f[0]);
		FileWriter f1=new FileWriter(f[1]);
		f0.write("E F G #1 L M");
		f0.close();
		f1.write("H I J K");
		f1.close();

		String grammar =
			"lexer grammar L;\n"+
			"I : 'A'..'Z' {} ;\n"+
			"CP: '#' ('0'|'1') { performIncludeSourceFile( \"/tmp/test_\"+getText()+\".test\" ); skip(); };\n" +
			"WS: (' '|'\\n') -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "A B C D #0 N O P");
		
		System.err.println("lexer found>"+found+"<");
		
		// clean up test files
		f[0].delete();
		f[1].delete();

		String expecting =
			"[@0,0:0='A',<1>,1:0]\n"+
			"[@1,2:2='B',<1>,1:2]\n"+
			"[@2,4:4='C',<1>,1:4]\n"+
			"[@3,6:6='D',<1>,1:6]\n"+
			"[@4,0:0='E',<1>,1:0]\n"+
			"[@5,2:2='F',<1>,1:2]\n"+
			"[@6,4:4='G',<1>,1:4]\n"+
			"[@7,0:0='H',<1>,1:0]\n"+
			"[@8,2:2='I',<1>,1:2]\n"+
			"[@9,4:4='J',<1>,1:4]\n"+
			"[@10,6:6='K',<1>,1:6]\n"+
			"[@11,9:9='L',<1>,1:8]\n"+
			"[@12,11:11='M',<1>,1:10]\n"+
			"[@13,11:11='N',<1>,1:12]\n"+
			"[@14,13:13='O',<1>,1:14]\n"+
			"[@15,15:15='P',<1>,1:16]\n"+
			"[@16,16:15='<EOF>',<-1>,1:17]\n"
					;
		assertEquals(expecting, found);
	}

	@Test public void testActionPerformIncludeSourceFileSubstitute() throws Exception {
		// prepare test files
		String path="c:/temp/";
		String fn[] = {"test_#0.test","test_#1.test"};
		File f[] = {new File(path+fn[0]),new File(path+fn[1])};
		FileWriter f0=new FileWriter(f[0]);
		FileWriter f1=new FileWriter(f[1]);
		f0.write("Z F G #1 L M");
		f0.close();
		f1.write("H I J K");
		f1.close();

		String grammar =
			"lexer grammar L;\n"+
			"I : 'A'..'Z' {} ;\n"+
			"CP: '#' ('0'|'1') { performIncludeSourceFile( \"c:/temp/test_\"+getText()+\".test\", \"Z\", \"E\" ); skip(); };\n" +
			"WS: (' '|'\\n') -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "A B C D #0 N O P");
		
		System.err.println("lexer found>"+found+"<");
		
		// clean up test files
		f[0].delete();
		f[1].delete();

		String expecting =
			"[@0,0:0='A',<1>,1:0]\n"+
			"[@1,2:2='B',<1>,1:2]\n"+
			"[@2,4:4='C',<1>,1:4]\n"+
			"[@3,6:6='D',<1>,1:6]\n"+
			"[@4,0:0='E',<1>,1:0]\n"+
			"[@5,2:2='F',<1>,1:2]\n"+
			"[@6,4:4='G',<1>,1:4]\n"+
			"[@7,0:0='H',<1>,1:0]\n"+
			"[@8,2:2='I',<1>,1:2]\n"+
			"[@9,4:4='J',<1>,1:4]\n"+
			"[@10,6:6='K',<1>,1:6]\n"+
			"[@11,9:9='L',<1>,1:8]\n"+
			"[@12,11:11='M',<1>,1:10]\n"+
			"[@13,11:11='N',<1>,1:12]\n"+
			"[@14,13:13='O',<1>,1:14]\n"+
			"[@15,15:15='P',<1>,1:16]\n"+
			"[@16,16:15='<EOF>',<-1>,1:17]\n"
					;
		assertEquals(expecting, found);
	}
}
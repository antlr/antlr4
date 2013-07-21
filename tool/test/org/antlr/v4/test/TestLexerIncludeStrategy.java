/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.test;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestLexerIncludeStrategy extends BaseTest {
    @Test public void testIncludeNested() throws Exception {
    	   	
		String src = 
	    	     "A=B. \n" +
	    	     "COPY CD. \n" +
	    	     "E=F. \n"
	    			;
		
		String includeStrategy=
		"IncludeStrategyImpl includeStrategy=new IncludeStrategyImpl();"+
		"includeStrategy.addInclude(\"COPY CD.\",\"C=D.\\n\" +"+
		"		                       \"COPY GH.\\n\""+
		"		           );"+
		"includeStrategy.addInclude(\"COPY GH.\",\"G=H.\\n\");";
		
   		String grammar =
				"lexer grammar L;"+
				"COPY: 'COPY' ' '+ ID '.' {setIncludeStream(getText());} ;"+
				"ID: [A-Z0-9]+;"+
				"EQ:  '=';"+
				"DOT: '.';"+
				"WS: [ \\n] -> skip;";

   		String found = execLexer("L.g4", grammar, "L", src, includeStrategy);
   		String expecting =
			"[@0,0:0='A',<2>,1:0]\n"+
			"[@1,1:1='=',<3>,1:1]\n"+
			"[@2,2:2='B',<2>,1:2]\n"+
			"[@3,3:3='.',<4>,1:3]\n"+
			"[@4,6:13='COPY CD.',<1>,2:0]\n"+
			"[@5,0:0='C',<2>,2:8]\n"+
			"[@6,1:1='=',<3>,2:9]\n"+
			"[@7,2:2='D',<2>,2:10]\n"+
			"[@8,3:3='.',<4>,2:11]\n"+
			"[@9,5:12='COPY GH.',<1>,3:0]\n"+
			"[@10,0:0='G',<2>,3:8]\n"+
			"[@11,1:1='=',<3>,3:9]\n"+
			"[@12,2:2='H',<2>,3:10]\n"+
			"[@13,3:3='.',<4>,3:11]\n"+
			"[@14,16:16='E',<2>,6:0]\n"+
			"[@15,17:17='=',<3>,6:1]\n"+
			"[@16,18:18='F',<2>,6:2]\n"+
			"[@17,19:19='.',<4>,6:3]\n"+
			"[@18,22:21='<EOF>',<-1>,7:0]\n";  				

   				
   		System.out.println("Found:>"+found+"<");
   		assertEquals(expecting, found);
   	}
}

/*
 * [The "BSD license"]
 *  Copyright (c) 2010 Terence Parr
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *      derived from this software without specific prior written permission.
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
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;

/** Test templates in actions; %... shorthands */
public class TestTemplates extends BaseTest {
	private static final String LINE_SEP = System.getProperty("line.separator");

	String template =
		"simpleTemplate(inline) ::= <<\n" +
		"grammar T;\n" +
		"options {\n" +
		"    output=template;\n" +
		"}\n" +
		"\n" +
		"a : ID {#inline#<inline>#end-inline#}\n" +
		"  ;\n" +
		"\n" +
		"ID : 'a';\n" +
		">>\n";

	@Test
    public void testTemplateConstructor() throws Exception {
		String action = "x = %foo(name={$ID.text});";
		String expecting = "x = templateLib.getInstanceOf(\"foo\"," +
			"new STAttrMap().put(\"name\", (ID1!=null?ID1.getText():null)));";
		testActions(template, "inline", action, expecting);
	}

	@Test
    public void testTemplateConstructorNoArgs() throws Exception {
		String action = "x = %foo();";
		String expecting = "x = templateLib.getInstanceOf(\"foo\");";
		testActions(template, "inline", action, expecting);
	}

	@Test
    public void testIndirectTemplateConstructor() throws Exception {
		String action = "x = %({\"foo\"})(name={$ID.text});";
		String expecting = "x = templateLib.getInstanceOf(\"foo\"," +
			"new STAttrMap().put(\"name\", (ID1!=null?ID1.getText():null)));";
		testActions(template, "inline", action, expecting);
	}

	@Test public void testStringConstructor() throws Exception {
		String action = "x = %{$ID.text};";
		String expecting = "x = new StringTemplate(templateLib,(ID1!=null?ID1.getText():null));";
		testActions(template, "inline", action, expecting);
	}

	@Test public void testSetAttr() throws Exception {
		String action = "%x.y = z;";
		String expecting = "(x).setAttribute(\"y\", z);";
		testActions(template, "inline", action, expecting);
	}

	@Test public void testSetAttrOfExpr() throws Exception {
		String action = "%{foo($ID.text).getST()}.y = z;";
		String expecting = "(foo((ID1!=null?ID1.getText():null)).getST()).setAttribute(\"y\", z);";
		testActions(template, "inline", action, expecting);
	}

	@Test public void testCannotHaveSpaceBeforeDot() throws Exception {
		String action = "%x .y = z;";
		String expecting = ": T.g:6:16: invalid StringTemplate % shorthand syntax: '%x'";
		STGroup grammarG = new STGroupString(template);
		ST grammarST = grammarG.getInstanceOf("simpleTemplate");
		grammarST.add("inline", action);
		String grammar = grammarST.render();
		testErrors(new String[] {grammar, expecting}, false);
	}

	@Test public void testCannotHaveSpaceAfterDot() throws Exception {
		String action = "%x. y = z;";
		String expecting = ": T.g:6:16: invalid StringTemplate % shorthand syntax: '%x.'";
		STGroup grammarG = new STGroupString(template);
		ST grammarST = grammarG.getInstanceOf("simpleTemplate");
		grammarST.add("inline", action);
		String grammar = grammarST.render();
		testErrors(new String[] {grammar, expecting}, false);
	}

	// S U P P O R T
	private void assertNoErrors(ErrorQueue equeue) {
		assertTrue("unexpected errors: "+equeue, equeue.errors.size()==0);
	}
}

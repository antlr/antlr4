/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.gui;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.Tree;

import javax.print.PrintException;
import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

public class Trees {
	/** Call this method to view a parse tree in a dialog box visually. */
	public static Future<JFrame> inspect(Tree t, List<String> ruleNames) {
		TreeViewer viewer = new TreeViewer(ruleNames, t);
		return viewer.open();
	}

	/** Call this method to view a parse tree in a dialog box visually. */
	public static Future<JFrame> inspect(Tree t, Parser parser) {
		List<String> ruleNames = parser != null ? Arrays.asList(parser.getRuleNames()) : null;
		return inspect(t, ruleNames);
	}

	/** Save this tree in a postscript file */
	public static void save(Tree t, Parser parser, String fileName)
		throws IOException, PrintException
	{
		List<String> ruleNames = parser != null ? Arrays.asList(parser.getRuleNames()) : null;
		save(t, ruleNames, fileName);
	}

	/** Save this tree in a postscript file using a particular font name and size */
	public static void save(Tree t, Parser parser, String fileName,
					 String fontName, int fontSize)
		throws IOException
	{
		List<String> ruleNames = parser != null ? Arrays.asList(parser.getRuleNames()) : null;
		save(t, ruleNames, fileName, fontName, fontSize);
	}

	/** Save this tree in a postscript file */
	public static void save(Tree t, List<String> ruleNames, String fileName)
		throws IOException, PrintException
	{
		writePS(t, ruleNames, fileName);
	}

	/** Save this tree in a postscript file using a particular font name and size */
	public static void save(Tree t,
	                        List<String> ruleNames, String fileName,
	                        String fontName, int fontSize)
	throws IOException
	{
		writePS(t, ruleNames, fileName, fontName, fontSize);
	}

	public static String getPS(Tree t, List<String> ruleNames,
							   String fontName, int fontSize)
	{
		TreePostScriptGenerator psgen =
			new TreePostScriptGenerator(ruleNames, t, fontName, fontSize);
		return psgen.getPS();
	}

	public static String getPS(Tree t, List<String> ruleNames) {
		return getPS(t, ruleNames, "Helvetica", 11);
	}

	public static void writePS(Tree t, List<String> ruleNames,
							   String fileName,
							   String fontName, int fontSize)
		throws IOException
	{
		String ps = getPS(t, ruleNames, fontName, fontSize);
		FileWriter f = new FileWriter(fileName);
		BufferedWriter bw = new BufferedWriter(f);
		try {
			bw.write(ps);
		}
		finally {
			bw.close();
		}
	}

	public static void writePS(Tree t, List<String> ruleNames, String fileName)
		throws IOException
	{
		writePS(t, ruleNames, fileName, "Helvetica", 11);
	}

	/** Print out a whole tree in LISP form. Arg nodeTextProvider is used on the
	 *  node payloads to get the text for the nodes.
	 *
	 *  @since 4.5.1
	 */
	public static String toStringTree(Tree t, TreeTextProvider nodeTextProvider) {
		if ( t==null ) return "null";
		String s = Utils.escapeWhitespace(nodeTextProvider.getText(t), false);
		if ( t.getChildCount()==0 ) return s;
		StringBuilder buf = new StringBuilder();
		buf.append("(");
		s = Utils.escapeWhitespace(nodeTextProvider.getText(t), false);
		buf.append(s);
		buf.append(' ');
		for (int i = 0; i<t.getChildCount(); i++) {
			if ( i>0 ) buf.append(' ');
			buf.append(toStringTree(t.getChild(i), nodeTextProvider));
		}
		buf.append(")");
		return buf.toString();
	}

	private Trees() {
	}
}

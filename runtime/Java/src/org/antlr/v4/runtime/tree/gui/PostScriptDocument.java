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

package org.antlr.v4.runtime.tree.gui;

public class PostScriptDocument {
	protected int boundingBoxWidth;
	protected int boundingBoxHeight;

	protected BasicFontMetrics fontMetrics;
	protected String fontName;
	protected int fontSize = 12;
	protected double lineWidth = 0.3;
	protected String boundingBox;

	protected StringBuilder ps = new StringBuilder();
	protected boolean closed = false;

	public PostScriptDocument() {
		this("CourierNew", 12);
	}

	public PostScriptDocument(String fontName, int fontSize) {
		header();
		setFont(fontName, fontSize);
	}

	public String getPS() {
		close();
		return header()+ps.toString();
	}

	public void boundingBox(int w, int h) {
		boundingBoxWidth = w;
		boundingBoxHeight = h;
		boundingBox = String.format("%%%%BoundingBox: %d %d %d %d\n", 0,0,
									boundingBoxWidth,boundingBoxHeight);
	}

	public void close() {
		if ( closed ) return;
//		ps.append("showpage\n");
		ps.append("%%Trailer\n");
		closed = true;
	}

	/** Compute the header separately because we need to wait for the bounding box */
	protected StringBuilder header() {
		StringBuilder b = new StringBuilder();
		b.append("%!PS-Adobe-3.0 EPSF-3.0\n");
		b.append(boundingBox).append("\n");
		b.append("0.3 setlinewidth\n");
		b.append("%% x y w h highlight\n" +
				 "/highlight {\n" +
				 "        4 dict begin\n" +
				 "        /h exch def\n" +
				 "        /w exch def\n" +
				 "        /y exch def\n" +
				 "        /x exch def\n" +
				 "        gsave\n" +
				 "        newpath\n" +
				 "        x y moveto\n" +
				 "        0 h rlineto     % up to left corner\n" +
				 "        w 0 rlineto     % to upper right corner\n" +
				 "        0 h neg rlineto % to lower right corner\n" +
				 "        w neg 0 rlineto % back home to lower left corner\n" +
				 "        closepath\n" +
				 "        .95 .83 .82 setrgbcolor\n" +
				 "        fill\n" +
				 "        grestore\n" +
				 "        end\n" +
				 "} def");

		return b;
	}

	// Courier, Helvetica, Times, ... should be available
	public void setFont(String fontName, int fontSize) {
		this.fontName = fontName;
		this.fontSize = fontSize;
		try {
			Class<?> c = Class.forName("org.antlr.v4.runtime.tree.gui." + fontName);
			this.fontMetrics = (BasicFontMetrics)c.newInstance();
		}
		catch (Exception e) {
			throw new UnsupportedOperationException("No font metrics for "+fontName);
		}
		ps.append(String.format("/%s findfont\n%d scalefont setfont\n", fontName, fontSize));
	}

	public void lineWidth(double w) {
		lineWidth = w;
		ps.append(w).append(" setlinewidth\n");
	}

	public void move(double x, double y) {
		ps.append(String.format("%1.3f %1.3f moveto\n", x, y));
	}

	public void lineto(double x, double y) {
		ps.append(String.format("%1.3f %1.3f lineto\n", x, y));
	}

	public void line(double x1, double y1, double x2, double y2) {
		move(x1, y1);
		lineto(x2, y2);
	}

	public void rect(double x, double y, double width, double height) {
		line(x, y, x, y + height);
		line(x, y + height, x + width, y + height);
		line(x + width, y + height, x + width, y);
		line(x + width, y, x, y);
	}

	/** Make red box */
	public void highlight(double x, double y, double width, double height) {
		ps.append(String.format("%1.3f %1.3f %1.3f %1.3f highlight\n", x, y, width, height));
	}

	public void stroke() {
		ps.append("stroke\n");
	}

//	public void rarrow(double x, double y) {
//		ps.append(String.format("%1.3f %1.3f rarrow\n", x,y));
//	}
//
//	public void darrow(double x, double y) {
//		ps.append(String.format("%1.3f %1.3f darrow\n", x,y));
//	}

	public void text(String s, double x, double y) {
		StringBuilder buf = new StringBuilder();
		// escape \, (, ): \\,  \(,  \)
		for (char c : s.toCharArray()) {
			switch ( c ) {
				case '\\' :
				case '(' :
				case ')' :
					buf.append('\\');
					buf.append(c);
					break;
				default :
					buf.append(c);
					break;
			}
		}
		s = buf.toString();
		move(x,y);
		ps.append(String.format("(%s) show\n", s));
		stroke();
	}

	// courier new: wid/hei 7.611979	10.0625
	/** All chars are 600 thousands of an 'em' wide if courier */
	public double getWidth(char c) { return fontMetrics.getWidth(c, fontSize); }
	public double getWidth(String s) { return fontMetrics.getWidth(s, fontSize); }
	public double getLineHeight() { return fontMetrics.getLineHeight(fontSize); }

	public int getFontSize() { return fontSize; }
}

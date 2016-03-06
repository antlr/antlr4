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

package org.antlr.v4.gui;

/** Font metrics.  The only way to generate accurate images
 *  in any format that contain text is to know the font metrics.
 *  Specifically, we need to know the width of every character and the
 *  maximum height (since we want all characters to fit within same line height).
 *  I used ttf2tfm to dump the font metrics from Mac TrueType fonts and
 *  then converted that to a Java class for use in a PostScript generator
 *  for trees. Commands:
 *
 * <pre>
 *	 $ ttf2tfm /Library/Fonts/Arial\ Black.ttf &gt; metrics
 * </pre>
 *
 * 	Then run metrics into python code after stripping header/footer:
 *
 * <pre>
 *	 #
 *	 # Process lines from ttf2tfm that look like this:
 *	 # Glyph  Code   Glyph Name                Width  llx    lly      urx    ury
 *	 # ------------------------------------------------------------------------
 *	 #     3  00020  space                       333  0,     0 --     0,     0
 *	 #
 *	 lines = open("metrics").read().split('\n')
 *	 print "public class FontName {"
 *	 print "    {"
 *	 maxh = 0;
 *	 for line in lines[4:]: # skip header 0..3
 *			 all = line.split(' ')
 *			 words = [x for x in all if len(x)&gt;0]
 *			 ascii = int(words[1], 16)
 *			 height = int(words[8])
 *			 if height&gt;maxh: maxh = height
 *			 if ascii&gt;=128: break
 *			 print "        widths[%d] = %s; // %s" % (ascii, words[3], words[2])
 *
 *	 print "        maxCharHeight = "+str(maxh)+";"
 *	 print "    }"
 *	 print "}"
 * </pre>
 *
 *	Units are 1000th of an 'em'.
 */
public abstract class BasicFontMetrics {
	public static final int MAX_CHAR = '\u00FF';
	protected int maxCharHeight;
	protected int[] widths = new int[MAX_CHAR+1];

	public double getWidth(String s, int fontSize) {
		double w = 0;
		for (char c : s.toCharArray()) {
			w += getWidth(c, fontSize);
		}
		return w;
	}

	public double getWidth(char c, int fontSize) {
		if ( c > MAX_CHAR || widths[c]==0 ) return widths['m']/1000.0; // return width('m')
		return widths[c]/1000.0 * fontSize;
	}

	public double getLineHeight(int fontSize) {
		return maxCharHeight / 1000.0 * fontSize;
	}
}

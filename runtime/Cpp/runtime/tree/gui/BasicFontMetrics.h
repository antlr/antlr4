#pragma once

#include <string>

/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Dan McLaughlin
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

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace tree {
                    namespace gui {

                        /// <summary>
                        /// Font metrics.  The only way to generate accurate images
                        ///  in any format that contain text is to know the font metrics.
                        ///  Specifically, we need to know the width of every character and the
                        ///  maximum height (since we want all characters to fit within same line height).
                        ///  I used ttf2tfm to dump the font metrics from Mac TrueType fonts and
                        ///  then converted that to a Java class for use in a PostScript generator
                        ///  for trees. Commands:
                        /// 
                        /// <pre>
                        ///	 $ ttf2tfm /Library/Fonts/Arial\ Black.ttf > metrics
                        /// </pre>
                        /// 
                        /// 	Then run metrics into python code after stripping header/footer:
                        /// 
                        /// <pre>
                        ///	 #
                        ///	 # Process lines from ttf2tfm that look like this:
                        ///	 # Glyph  Code   Glyph Name                Width  llx    lly      urx    ury
                        ///	 # ------------------------------------------------------------------------
                        ///	 #     3  00020  space                       333  0,     0 --     0,     0
                        ///	 #
                        ///	 lines = open("metrics").read().split('\n')
                        ///	 print "public class FontName {"
                        ///	 print "    {"
                        ///	 maxh = 0;
                        ///	 for line in lines[4:]: # skip header 0..3
                        ///			 all = line.split(' ')
                        ///			 words = [x for x in all if len(x)>0]
                        ///			 ascii = int(words[1], 16)
                        ///			 height = int(words[8])
                        ///			 if height>maxh: maxh = height
                        ///			 if ascii>=128: break
                        ///			 print "        widths[%d] = %s; // %s" % (ascii, words[3], words[2])
                        /// 
                        ///	 print "        maxCharHeight = "+str(maxh)+";"
                        ///	 print "    }"
                        ///	 print "}"
                        /// </pre>
                        /// 
                        /// Units are 1000th of an 'em'.
                        /// </summary>
                        class BasicFontMetrics {
                        public:
                            static const int MAX_CHAR = L'\u00FF';
                        protected:
                            int maxCharHeight;
                            int widths[MAX_CHAR + 1];

                        public:
                            virtual double getWidth(const std::wstring &s, int fontSize);

                            virtual double getWidth(wchar_t c, int fontSize);

                            virtual double getLineHeight(int fontSize);

                        private:
                            void InitializeInstanceFields();

public:
                            BasicFontMetrics() {
                                InitializeInstanceFields();
                            }
                        };

                    }
                }
            }
        }
    }
}

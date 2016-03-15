#pragma once

#include "SystemFontMetrics.h"
#include <string>
#include <unordered_map>

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


                        class PostScriptDocument {
                        public:
                            static const std::wstring DEFAULT_FONT;

                            static Map<std::wstring, std::wstring> *const POSTSCRIPT_FONT_NAMES;
//JAVA TO C++ CONVERTER TODO TASK: Static constructors are not allowed in native C++:
                            static PostScriptDocument();

                        protected:
                            int boundingBoxWidth;
                            int boundingBoxHeight;

                            SystemFontMetrics *fontMetrics;
                            std::wstring fontName;
                            int fontSize;
//JAVA TO C++ CONVERTER NOTE: The variable lineWidth was renamed since C++ does not allow variables with the same name as methods:
                            double lineWidth_Renamed;
//JAVA TO C++ CONVERTER NOTE: The variable boundingBox was renamed since C++ does not allow variables with the same name as methods:
                            std::wstring boundingBox_Renamed;

                            StringBuilder *ps;
                            bool closed;

                        public:
//JAVA TO C++ CONVERTER TODO TASK: Calls to same-class constructors are not supported in C++ prior to C++11:
                            PostScriptDocument(); //this(DEFAULT_FONT, 12);

                            PostScriptDocument(const std::wstring &fontName, int fontSize);

                            virtual std::wstring getPS();

                            virtual void boundingBox(int w, int h);

                            virtual void close();

                            /// <summary>
                            /// Compute the header separately because we need to wait for the bounding box </summary>
                        protected:
                            virtual StringBuilder *header();

                        public:
                            virtual void setFont(const std::wstring &fontName, int fontSize);

                            virtual void lineWidth(double w);

                            virtual void move(double x, double y);

                            virtual void lineto(double x, double y);

                            virtual void line(double x1, double y1, double x2, double y2);

                            virtual void rect(double x, double y, double width, double height);

                            /// <summary>
                            /// Make red box </summary>
                            virtual void highlight(double x, double y, double width, double height);

                            virtual void stroke();

                        //	public void rarrow(double x, double y) {
                        //		ps.append(String.format(Locale.US, "%1.3f %1.3f rarrow\n", x,y));
                        //	}
                        //
                        //	public void darrow(double x, double y) {
                        //		ps.append(String.format(Locale.US, "%1.3f %1.3f darrow\n", x,y));
                        //	}

                            virtual void text(const std::wstring &s, double x, double y);

                            // courier new: wid/hei 7.611979	10.0625
                            /// <summary>
                            /// All chars are 600 thousands of an 'em' wide if courier </summary>
                            virtual double getWidth(wchar_t c);
                            virtual double getWidth(const std::wstring &s);
                            virtual double getLineHeight();

                            virtual int getFontSize();

                        private:
                            void InitializeInstanceFields();
                        };

                    }
                }
            }
        }
    }
}

#pragma once

#include <string>
#include <unordered_map>
#include <vector>

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

// TODO:  Come back to this after the base runtime works.

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace misc {


                    class Utils {
                        // Seriously: why isn't this built in to java? ugh!
                    public:
//                        template<typename T>
//                        static std::wstring join(Iterator<T> *iter, const std::wstring &separator);
//
//                        template<typename T>
//                        static std::wstring join(T array_Renamed[], const std::wstring &separator);

                        static std::wstring escapeWhitespace(const std::wstring &s, bool escapeSpaces);

//                        static void writeFile(const std::wstring &fileName, const std::wstring &content) throw(IOException);
//
//                        static void waitForClose(Window *const window) throw(InterruptedException);
//
//                    private:
//                        class ThreadAnonymousInnerClassHelper : public Thread {
//                        private:
//                            Window *window;
//                            auto lock;
//
//                        public:
//                            ThreadAnonymousInnerClassHelper(Window *window, auto lock);
//
//                            virtual void run() override;
//                        };
//
//                    private:
//                        class WindowAdapterAnonymousInnerClassHelper : public WindowAdapter {
//                        private:
//                            Window *window;
//                            auto lock;
//
//                        public:
//                            WindowAdapterAnonymousInnerClassHelper(Window *window, auto lock);
//
//                            virtual void windowClosing(WindowEvent *arg0) override;
//                        };
//
//                        /// <summary>
//                        /// Convert array of strings to string->index map. Useful for
//                        ///  converting rulenames to name->ruleindex map.
//                        /// </summary>
//                    public:
//                        static Map<std::wstring, int> *toMap(std::wstring keys[]);

                        static wchar_t *toCharArray(const std::vector<int> *data);
                    };

                }
            }
        }
    }
}

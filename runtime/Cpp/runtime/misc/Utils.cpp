#include "Utils.h"
#include "Exceptions.h"

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
                namespace misc {

                    std::wstring Utils::escapeWhitespace(const std::wstring &s, bool escapeSpaces) {
                        throw new TODOException(L"Utils::escapeWhitespace");
                    }
                    wchar_t *Utils::toCharArray(const std::vector<int> *data) {
#ifdef TODO
                        if (data == nullptr) {
                            return nullptr;
                        }
                        wchar_t cdata[data->size()];
                        for (int i = 0; i < data->size(); i++) {
                            cdata[i] = static_cast<wchar_t>(data->get(i));
                        }
                        return cdata;
#else
                        throw new TODOException(L"Utils::toCharArray");
#endif
                    }
                    // TODO:  Come back to this after the base runtime works.
#ifdef TODO
                    template<typename T>
                    std::wstring Utils::join(Iterator<T> *iter, const std::wstring &separator) {
                        StringBuilder *buf = new StringBuilder();
                        while (iter->hasNext()) {
                            buf->append(iter->next());
                            if (iter->hasNext()) {
                                buf->append(separator);
                            }
                            iter++;
                        }
                        return buf->toString();
                    }

                    template<typename T>
                    std::wstring Utils::join(T array_Renamed[], const std::wstring &separator) {
                        StringBuilder *builder = new StringBuilder();
                        for (int i = 0; i < sizeof(array_Renamed) / sizeof(array_Renamed[0]); i++) {
                            builder->append(array_Renamed[i]);
                            if (i < sizeof(array_Renamed) / sizeof(array_Renamed[0]) - 1) {
                                builder->append(separator);
                            }
                        }

                        return builder->toString();
                    }

                    std::wstring Utils::escapeWhitespace(const std::wstring &s, bool escapeSpaces) {
                        StringBuilder *buf = new StringBuilder();
                        for (auto c : s.toCharArray()) {
                            if (c == L' ' && escapeSpaces) {
                                buf->append(L'\u00B7');
                            } else if (c == L'\t') {
                                buf->append(L"\\t");
                            } else if (c == L'\n') {
                                buf->append(L"\\n");
                            } else if (c == L'\r') {
                                buf->append(L"\\r");
                            } else {
                                buf->append(c);
                            }
                        }
                        return buf->toString();
                    }

                    void Utils::writeFile(const std::wstring &fileName, const std::wstring &content) throw(IOException) {
                        FileWriter *fw = new FileWriter(fileName);
                        Writer *w = new BufferedWriter(fw);
                        try {
                            w->write(content);
                        } finally {
                            w->close();
                        }
                    }

                    void Utils::waitForClose(Window *const window) throw(InterruptedException) {
                        const auto lock = new Object();

                        Thread *t = new ThreadAnonymousInnerClassHelper(window, lock);

                        t->start();

                        window->addWindowListener(new WindowAdapterAnonymousInnerClassHelper(window, lock));

                        t->join();
                    }

                    Utils::ThreadAnonymousInnerClassHelper::ThreadAnonymousInnerClassHelper(Window *window, auto lock) {
                        this->window = window;
                        this->lock = lock;
                    }

                    void Utils::ThreadAnonymousInnerClassHelper::run() {
                        synchronized(lock) {
                            while (window->isVisible()) {
                                try {
                                    lock->wait(500);
                                } catch (InterruptedException e) {
                                }
                            }
                        }
                    }

                    Utils::WindowAdapterAnonymousInnerClassHelper::WindowAdapterAnonymousInnerClassHelper(Window *window, auto lock) {
                        this->window = window;
                        this->lock = lock;
                    }

                    void Utils::WindowAdapterAnonymousInnerClassHelper::windowClosing(WindowEvent *arg0) {
//JAVA TO C++ CONVERTER TODO TASK: There is no built-in support for multithreading in native C++:
                        synchronized(lock) {
                            window->setVisible(false);
                            lock->notify();
                        }
                    }

                    Map<std::wstring, int> *Utils::toMap(std::wstring keys[]) {
                        Map<std::wstring, int> *m = std::unordered_map<std::wstring, int>();
                        for (int i = 0; i < sizeof(keys) / sizeof(keys[0]); i++) {
                            m->put(keys[i], i);
                        }
                        return m;
                    }

#endif
                }
            }
        }
    }
}


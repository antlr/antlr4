
#include "Strings.h"
#include "LogManager.h"
#include "StringBuilder.h"

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

                    LogManager::Record::Record() {
                        InitializeInstanceFields();
#ifdef TODO
                        timestamp = System::currentTimeMillis();
                        location = (std::exception())->getStackTrace()[0];
#endif
                    }

                    std::wstring LogManager::Record::toString() {
                        antlrcpp::StringBuilder *buf = new antlrcpp::StringBuilder();
#ifdef TODO
                        buf->append((new SimpleDateFormat(L"yyyy-MM-dd HH:mm:ss:SSS"))->format(Date(timestamp)));
                        buf->append(L" ");
                        buf->append(component);
                        buf->append(L" ");
                        buf->append(location->getFileName());
                        buf->append(L":");
                        buf->append(location->getLineNumber());
                        buf->append(L" ");
                        buf->append(msg);
#endif
                        return buf->toString();
                    }

                    void LogManager::Record::InitializeInstanceFields() {
                        timestamp = 0;
                    }

                    void LogManager::log(const std::wstring &component, const std::wstring &msg) {
                        Record *r = new Record();
                        r->component = component;
                        r->msg = msg;
                        if (records.empty()) {
                            records = std::vector<Record*>();
                        }
                        records.push_back(r);
                    }

                    void LogManager::log(const std::wstring &msg) {
                        log(L"");
                    }

                    void LogManager::save(const std::wstring &filename) {
#ifdef TODO
                        FileWriter *fw = new FileWriter(filename);
                        BufferedWriter *bw = new BufferedWriter(fw);
                        try {
                            bw->write(toString());
                        } finally {
                            bw->close();
                        }
#endif
                    }

                    std::wstring LogManager::save() {
#ifdef TODO
                        //String dir = System.getProperty("java.io.tmpdir");
                        std::wstring dir = L".";
                        std::wstring defaultFilename = dir + std::wstring(L"/antlr-") + (new SimpleDateFormat(L"yyyy-MM-dd-HH.mm.ss"))->format(Date()) + std::wstring(L".log");
                        save(defaultFilename);
                        return defaultFilename;
#else
                        return nullptr;
#endif
                    }

                    std::wstring LogManager::toString() {
#ifdef TODO
                        if (records.empty()) {
                            return L"";
                        }
                        std::wstring nl = System::getProperty(L"line.separator");
                        StringBuilder *buf = new StringBuilder();
                        for (auto r : records) {
                            buf->append(r);
                            buf->append(nl);
                        }
                        return buf->toString();
#else
                        return nullptr;
#endif
                    }

                    void LogManager::main(std::wstring args[]) {
                        LogManager *mgr = new LogManager();
                        mgr->log(L"atn", L"test msg");
                        mgr->log(L"dfa", L"test msg 2");
                        std::cout << mgr << std::endl;
                        mgr->save();
                    }
                }
            }
        }
    }
}

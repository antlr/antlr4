#pragma once

#include <map>
#include <set>

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


                    /// <summary>
                    /// Sometimes we need to map a key to a value but key is two pieces of data.
                    ///  This nested hash table saves creating a single key each time we access
                    ///  map; avoids mem creation.
                    /// </summary>
                    template<typename Key1, typename Key2, typename Value>
                    class DoubleKeyMap {
                    public:
                        std::map<Key1, std::map<Key2, Value>*> *data;
                        
                        Value put(Key1 k1, Key2 k2, Value v) {
                            auto subdata = data->find(k1);
                            Value prev = nullptr;
                            std::map<Key2, Value> *data2;
                            if (subdata == data->end()) {
                                data2 = new std::map<Key2, Value>();
                                data->insert ( std::pair<Key1, std::map<Key2, Value>*>(k1, data2) );
                            } else {
                                data2 = subdata->second;
                                auto elem = data2->find(k2);
                                if (elem != data2->end()) {
                                    prev = elem->second;
                                }
                            }
                            data2->insert ( std::pair<Key2, Value>(k2, v) );
                            return prev;
                        }

                        Value get(Key1 k1, Key2 k2) {
                            auto interior = data->find(k1);
                            if (interior == data->end()) {
                                return nullptr;
                            }
                            std::map<Key1, Value> *data2 = interior->second;
                            auto v = data2->find(k2);
                            if (v == data2->end()) {
                                return nullptr;
                            }
                            return v->second;
                        }

                        std::map<Key2, Value> *get(Key1 k1) {
                            auto data2 = data->find(k1);
                            if (data2 == data->end()) {
                                return nullptr;
                            }
                            return *data2;
                        }

                        /// <summary>
                        /// Get all values associated with primary key </summary>
                        std::set<Value> *values(Key1 k1) {
                            auto data2 = data->find(k1);
                            if (data2 == data->end()) {
                                return nullptr;
                            }
                            return data2->values();
                        }

                        /// <summary>
                        /// get all primary keys </summary>
                        std::set<Key1> *keySet() {
                            return data->keySet();
                        }

                        /// <summary>
                        /// get all secondary keys associated with a primary key </summary>
                        std::set<Key2> *keySet(Key1 k1) {
                            std::map<Key2, Value> *data2 = data->get(k1);
                            if (data2 == nullptr) {
                                return nullptr;
                            }
                            return data2->keySet();
                        }

                    private:
                        void InitializeInstanceFields() {
                            data = new std::map<Key1, std::map<Key2, Value>*>();
                        }

                    public:
                        DoubleKeyMap() {
                            InitializeInstanceFields();
                        }
                    };

                }
            }
        }
    }
}

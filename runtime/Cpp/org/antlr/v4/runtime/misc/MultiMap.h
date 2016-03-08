#pragma once

#include <list>
#include <map>
#include <utility>
#include <vector>
#include <iterator>

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
#ifdef TODO
                    // http://stackoverflow.com/questions/2467000/is-there-a-java-map-keyset-equivalent-for-cs-stdmap
                    template <typename C>
                    class key_iterator : public std::iterator<std::bidirectional_iterator_tag,
                                                                typename C::key_type,
                                                                typename C::difference_type,
                                                                typename C::pointer,
                                                                typename C::reference> {
                    public:
                        
                        key_iterator() { }
                        explicit key_iterator(typename C::const_iterator it) : it_(it) { }
                        
                        typename const C::key_type& operator*() const  { return  it_->first; }
                        typename const C::key_type* operator->() const { return &it_->first; }
                        
                        key_iterator& operator++() { ++it_; return *this; }
                        key_iterator operator++(int) { key_iterator it(*this); ++*this; return it; }
                        
                        key_iterator& operator--() { --it_; return *this; }
                        key_iterator operator--(int) { key_iterator it(*this); --*this; return it; }
                        
                        friend bool operator==(const key_iterator& lhs, const key_iterator& rhs)
                        {
                            return lhs.it_ == rhs.it_;
                        }
                        
                        friend bool operator!=(const key_iterator& lhs, const key_iterator& rhs)
                        {
                            return !(lhs == rhs);
                        }
                        
                    private:
                        
                        typename C::const_iterator it_;
                    };
                    
                    template <typename C>
                    key_iterator<C> begin_keys(const C& c) { return key_iterator<C>(c.begin()); }
                    
                    template <typename C>
                    key_iterator<C> end_keys(const C& c)   { return key_iterator<C>(c.end());   }
#endif

#ifdef TODO
                    This used to derive from LinkedHashMap
                http://stackoverflow.com/questions/2889777/difference-between-hashmap-linkedhashmap-and-sortedmap-in-java
#endif
                    template<typename K, typename V>
                    class MultiMap : public std::map<K, std::vector<V>> {
                    public:
                        
                        virtual void map(K key, V value) {
//                            std::vector<V> elementsForKey = get(key);
//                            if (elementsForKey.empty()) {
//                                elementsForKey = std::vector<V>();
//                                std::map<K, std::list<V>>::put(key, elementsForKey);
//                            }
                        }

                        virtual std::vector<std::pair<K, V>*> getPairs()  {
//                            std::vector<std::pair<K, V>*> pairs = std::vector<std::pair<K, V>*>();
//                            for (K key : keySet()) {
//                                for (V value : get(key)) {
//                                    pairs.push_back(new std::pair<K, V>(key, value));
//                                }
//                            }
//                            return pairs;
                            return std::vector<std::pair<K, V>*>();
                        }
                    };

                }
            }
        }
    }
}

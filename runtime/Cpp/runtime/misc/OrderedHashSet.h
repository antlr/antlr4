#pragma once

#include "Exceptions.h"

#include <string>
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

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace misc {
                    template <typename T>
                    class LinkedHashSet {
                        T remove(T);
                        bool add(T);
                    };

                    /// <summary>
                    /// A HashMap that remembers the order that the elements were added.
                    ///  You can alter the ith element with set(i,value) too :)  Unique list.
                    ///  I need the replace/set-element-i functionality so I'm subclassing
                    ///  LinkedHashSet.
                    /// </summary>
                    template<typename T>
                    class OrderedHashSet : public LinkedHashSet<T> {
                        /// <summary>
                        /// Track the elements as they are added to the set </summary>
                    protected:
//JAVA TO C++ CONVERTER NOTE: The variable elements was renamed since C++ does not allow variables with the same name as methods:
                        std::vector<T> elements_Renamed;

                    public:
                        virtual T get(int i) {
                            return elements_Renamed[i];
                        }

                        /// <summary>
                        /// Replace an existing value with a new value; updates the element
                        ///  list and the hash table, but not the key as that has not changed.
                        /// </summary>
                        virtual T set(int i, T value) {
                            T oldElement = elements_Renamed[i];
                            elements_Renamed[i] = value; // update list
                            remove(oldElement); // now update the set: remove/add
                            add(value);
                            return oldElement;
                        }

                        virtual bool remove(int i) {
                            T o = elements_Renamed.remove(i);
                            return remove(o);
                        }

                        /// <summary>
                        /// Add a value to list; keep in hashtable for consistency also;
                        ///  Key is object itself.  Good for say asking if a certain string is in
                        ///  a list of strings.
                        /// </summary>
                        virtual bool add(T value) override {
                            bool result = add(value);
                            if (result) { // only track if new element not in set
                                elements_Renamed.push_back(value);
                            }
                            return result;
                        }

                        virtual bool remove(void *o) override {
                            throw new UnsupportedOperationException();
                        }

                        virtual void clear() override {
                            elements_Renamed.clear();
                            LinkedHashSet<T>::clear();
                        }

                        virtual int hashCode() override {
                            return elements_Renamed.hashCode();
                        }

                        virtual bool equals(void *o) override {
                            if (!(dynamic_cast<OrderedHashSet<T>*>(o) != nullptr)) {
                                return false;
                            }

                    //		System.out.print("equals " + this + ", " + o+" = ");
                            bool same = elements_Renamed.size() > 0 && elements_Renamed.equals((static_cast<OrderedHashSet<T>*>(o))->elements_Renamed);
                    //		System.out.println(same);
                            return same;
                        }
#ifdef TODO
                        virtual std::iterator<T> *iterator() override {
                            return elements_Renamed.begin();
                        }
#endif
                        /// <summary>
                        /// Return the List holding list of table elements.  Note that you are
                        ///  NOT getting a copy so don't write to the list.
                        /// </summary>
                        virtual std::vector<T> elements() {
                            return elements_Renamed;
                        }

                        virtual void *clone() override { // safe (result of clone)
                            OrderedHashSet<T> *dup = static_cast<OrderedHashSet<T>*>(LinkedHashSet<T>::clone());
                            dup->elements_Renamed = std::vector<T>(this->elements_Renamed);
                            return dup;
                        }

                        virtual void *toArray() override {
                            return elements_Renamed.toArray();
                        }

                        virtual std::wstring toString() override {
                            return elements_Renamed.toString();
                        }

                    private:
                        void InitializeInstanceFields() {
                            elements_Renamed = std::vector<T>();
                        }

public:
                        OrderedHashSet() {
                            InitializeInstanceFields();
                        }
                    };

                }
            }
        }
    }
}

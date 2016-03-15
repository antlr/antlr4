#pragma once

#include <string>
#include <set>
#include <iterator>
#include <vector>

#include "AbstractEqualityComparator.h"
#include "ObjectEqualityComparator.h"
#include "MurmurHash.h"
#include "Array2DHashSet.h"
#include "Exceptions.h"
#include "StringBuilder.h"
#include "Arrays.h"

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
                    /// <seealso cref="Set"/> implementation with closed hashing (open addressing). </summary>
                    template<typename T>
                    class Array2DHashSet : public std::set<T> {
                    protected:
                        // Daughter iterator class
                        class SetIterator : public std::iterator<std::random_access_iterator_tag, T> {

                            Array2DHashSet<T> *const outerInstance;

                        public:
#ifdef TODO
                            // JAVA TO C++ CONVERTER WARNING: Since the array size is not known in
                            // this declaration, Java to C++ Converter has converted this array to
                            // a pointer.  You will need to call 'delete[]' where appropriate:
                            ORIGINAL LINE: final T[] data;
#endif
                            const T *data;
                            int nextIndex;
                            bool removed;

                            SetIterator(Array2DHashSet<T*> * const outerInstance, T data[]);

                            // TODO; these are java overrides probably, not STL
                            virtual bool hasNext();

                            virtual T *next();

                            virtual void remove();

                        private:
                            void InitializeInstanceFields();
                        };
                        
                    public:
                        static const int INITAL_CAPACITY = 16; // must be power of 2
                        static const int INITAL_BUCKET_CAPACITY = 8;
                        static const double LOAD_FACTOR;

                    protected:
                        AbstractEqualityComparator<T> *const comparator;
                        
                        std::vector<std::vector<T>> buckets;

                        /// <summary>
                        /// How many elements in set </summary>
                        int n;

                        int threshold; // when to expand

                        int currentPrime; // jump by 4 primes each expand or whatever
                        int initialBucketCapacity;

                    public:
                        Array2DHashSet() : comparator(nullptr) {
                            //this(nullptr, INITAL_CAPACITY, INITAL_BUCKET_CAPACITY);
                        }
                        template<typename T1>
                        Array2DHashSet(AbstractEqualityComparator<T1> *comparator);
                        
                        template<typename T1>
                        Array2DHashSet(AbstractEqualityComparator<T1> *comparator,
                                       int initialCapacity, int initialBucketCapacity);

                        /// <summary>
                        /// Add {@code o} to set if not there; return existing value if already
                        /// there. This method performs the same operation as <seealso cref="#add"/> aside from
                        /// the return value.
                        /// </summary>
                        T getOrAdd(T o);

                    protected:
                        virtual T getOrAddImpl(T o);

                    public:
                        virtual T get(T o);

                    protected:
                        int getBucket(T o);

                    public:
                        virtual int hashCode();

                        virtual bool equals(T o);

                    protected:
                        virtual void expand();

                    public:
                        bool add(T t);

                        int size();

                        bool isEmpty();

                        bool contains(void *o);

                        virtual bool containsFast(T obj);

                        virtual std::iterator<std::random_access_iterator_tag, T> *iterator();

                        virtual std::vector<T> *toArray();

                        template<typename U>
                        U *toArray(U a[]);
                        
                        bool remove(void *o);

                        virtual bool removeFast(T obj);

                        template<typename T1>
                        bool containsAll(std::set<T1> *collection);

                        template<typename T1>
                        bool addAll(std::set<T1> *c) ;

                        template<typename T1>
                        bool retainAll(std::set<T1> *c);

                        template<typename T1>
                        bool removeAll(std::set<T1> *c);

                        virtual void clear();

                    virtual std::wstring toString();

                    virtual std::wstring toTableString();

                        /// <summary>
                        /// Return {@code o} as an instance of the element type {@code T}. If
                        /// {@code o} is non-null but known to not be an instance of {@code T}, this
                        /// method returns {@code null}. The base implementation does not perform any
                        /// type checks; override this method to provide strong type checks for the
                        /// <seealso cref="#contains"/> and <seealso cref="#remove"/> methods to ensure the arguments to
                        /// the <seealso cref="EqualityComparator"/> for the set always have the expected
                        /// types.
                        /// </summary>
                        /// <param name="o"> the object to try and cast to the element type of the set </param>
                        /// <returns> {@code o} if it could be an instance of {@code T}, otherwise
                        /// {@code null}. </returns>
                    protected:
                        virtual T asElementType(void *o) {
                            throw new TODOException(L"Array2DHashSet::asElementType");
                        };

                        /// <summary>
                        /// Return an array of {@code T[]} with length {@code capacity}.
                        /// </summary>
                        /// <param name="capacity"> the length of the array to return </param>
                        /// <returns> the newly constructed array </returns>
                        virtual std::vector<std::vector<T>> * createBuckets(int capacity);

                        /// <summary>
                        /// Return an array of {@code T} with length {@code capacity}.
                        /// </summary>
                        /// <param name="capacity"> the length of the array to return </param>
                        /// <returns> the newly constructed array </returns>
                        virtual std::vector<T> * createBucket(int capacity);


                    private:
                        void InitializeInstanceFields();
                    };

                }
            }
        }
    }
}

#include "Array2DHashSet.inl"

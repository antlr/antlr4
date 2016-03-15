
#pragma once

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
                    template<typename T>
                    const double Array2DHashSet<T>::LOAD_FACTOR = 0.75;

                    template<typename T>
                    Array2DHashSet<T>::SetIterator::SetIterator(Array2DHashSet<T*> *const outerInstance, T data[])
                    : data(data), outerInstance(outerInstance) {
                        InitializeInstanceFields();
                    }
                    
                    template<typename T>
                    bool Array2DHashSet<T>::SetIterator::hasNext() {
                        return nextIndex < data->length;
                    }

                    template<typename T>
                    T *Array2DHashSet<T>::SetIterator::next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }

                        removed = false;
                        return data[nextIndex++];
                    }

                    template<typename T>
                    void Array2DHashSet<T>::SetIterator::remove() {
                        if (removed) {
                            throw new IllegalStateException();
                        }

                        outerInstance->remove(data[nextIndex - 1]);
                        removed = true;
                    }

                    template<typename T>
                    void Array2DHashSet<T>::SetIterator::InitializeInstanceFields() {
                        nextIndex = 0;
                        removed = true;
                    }
                    
                    template<typename T>
                    template<typename T1>
                    Array2DHashSet<T>::Array2DHashSet(AbstractEqualityComparator<T1> *comparator) { //this(comparator, INITAL_CAPACITY, INITAL_BUCKET_CAPACITY);
                    }
                    
                    template<typename T>
                    template<typename T1>
                    Array2DHashSet<T>::Array2DHashSet(AbstractEqualityComparator<T1> *comparator, int initialCapacity, int initialBucketCapacity) : comparator(ObjectEqualityComparator::INSTANCE) {
                        InitializeInstanceFields();
                        if (comparator == nullptr) {
                        }
                        
                        this->comparator = comparator;
                        this->buckets = createBuckets(initialCapacity);
                        this->initialBucketCapacity = initialBucketCapacity;
                    }
                    
                    template<typename T>
                    T Array2DHashSet<T>::getOrAdd(T o) {
                        if (n > threshold) {
                            expand();
                        }
                        return getOrAddImpl(o);
                    }
                    
                    template<typename T>
                    T Array2DHashSet<T>::getOrAddImpl(T o) {
                        int b = getBucket(o);
                        std::vector<T> * bucket = &buckets[b];
                        
                        // NEW BUCKET
                        if (bucket->size() == 0) {
                            bucket = createBucket(initialBucketCapacity);
                            (*bucket)[0] = o;
                            buckets[b] = *bucket;
                            n++;
                            return o;
                        }
                        
                        // LOOK FOR IT IN BUCKET
                        for (int i = 0; i < (int)bucket->size(); i++) {
                            T existing = (*bucket)[i];
                            if (existing == nullptr) { // empty slot; not there, add.
                                (*bucket)[i] = o;
                                n++;
                                return o;
                            }
                            if (comparator->equals(existing, o)) { // found existing, quit
                                return existing;
                            }
                        }
                        
                        bucket->insert(bucket->end(), o);
                        n++;
                        return o;
                    }
                    
                    template<typename T>
                    T Array2DHashSet<T>::get(T o) {
                        if (o == nullptr) {
                            return o;
                        }
                        int b = getBucket(o);
                        std::vector<T> bucket = buckets[b];
                        if (bucket.size() == 0) { // no bucket
                            return nullptr;
                        }
                        for (auto e : bucket) {
                            if (e == nullptr) { // empty slot; not there
                                return nullptr;
                            }
                            if (comparator->equals(e, o)) {
                                return e;
                            }
                        }
                        return nullptr;
                    }
                    
                    template<typename T>
                    int Array2DHashSet<T>::getBucket(T o) {
                        int hash = comparator->hashCode(o);
                        int b = hash & (buckets.size() - 1); // assumes len is power of 2
                        return b;
                    }
                    
                    template<typename T>
                    int Array2DHashSet<T>::hashCode()  {
                        int hash = MurmurHash::initialize();
                        for (auto bucket : buckets) {
                            if (bucket.size() == 0) {
                                continue;
                            }
                            for (auto o : bucket) {
                                if (o == nullptr) {
                                    break;
                                }
                                hash = MurmurHash::update(hash, comparator->hashCode(o));
                            }
                        }
                        
                        hash = MurmurHash::finish(hash, size());
                        return hash;
                    }
                    
                    template<typename T>
                    bool Array2DHashSet<T>::equals(T o) {
#ifdef TODO
                        barf
                        // Not sure what to do with this. Error is bad comparison between distinct
                        // pointer types, do a dynamic cast?
                        if (o == this) {
                            return true;
                        }
#endif
                        if (!((Array2DHashSet*)(o) != nullptr)) {
                            return false;
                        }
                        Array2DHashSet<void*> *other = (Array2DHashSet<void*>*)(o);
                        if (other->size() != size()) {
                            return false;
                        }
                        bool same = this->containsAll(other);
                        return same;
                    }
                    
                    template<typename T>
                    void Array2DHashSet<T>::expand() {
                        std::vector<std::vector<T>> old = buckets;
                        currentPrime += 4;
                        int newCapacity = (int)buckets.size() * 2;
                        std::vector<std::vector<T>> *newTable = createBuckets(newCapacity);
						std::vector<int> newBucketLengths;
                        buckets = *newTable;
                        threshold = static_cast<int>(newCapacity * LOAD_FACTOR);
                        //		System.out.println("new size="+newCapacity+", thres="+threshold);
                        // rehash all existing entries
                        int oldSize = size();
                        for (auto bucket : old) {
                            if (bucket.size() == 0) {
                                continue;
                            }
                            
                            for (auto o : bucket) {
                                if (o == nullptr) {
                                    break;
                                }
                                
                                int b = getBucket(o);
                                int bucketLength = newBucketLengths[b];
                                std::vector<T> *newBucket;
                                if (bucketLength == 0) {
                                    // new bucket
                                    newBucket = createBucket(initialBucketCapacity);
                                    (*newTable)[b] = *newBucket;
                                } else {
                                    // TODO, dammit is this right?
                                    newBucket = &(*newTable)[b];
                                    if (bucketLength == newBucket->size()) {
                                        (*newTable)[b] = *newBucket;
                                    }
                                }
                                
                                (*newBucket)[bucketLength] = o;
                                newBucketLengths[b]++;
                            }
                        }
                        
                        if (n != oldSize) throw new std::exception();
                    }
                    
                    template<typename T>
                    bool Array2DHashSet<T>::add(T t) {
                        T existing = getOrAdd(t);
                        return existing == t;
                    }
                    
                    template<typename T>
                    int Array2DHashSet<T>::size() {
                        return n;
                    }
                    
                    template<typename T>
                    bool Array2DHashSet<T>::isEmpty() {
                        return n == 0;
                    }
                    
                    template<typename T>
                    bool Array2DHashSet<T>::contains(void *o) {
                        return containsFast(asElementType(o));
                    }
                    
                    template<typename T>
                    bool Array2DHashSet<T>::containsFast(T obj) {
                        if (obj == nullptr) {
                            return false;
                        }
                        
                        return get(obj) != nullptr;
                    }
                    
                    template<typename T>
                    std::iterator<std::random_access_iterator_tag, T> *Array2DHashSet<T>::iterator() {
#ifdef TODO
                        // I'm feeling too weak willed to finish this now, call me a wimp, I dare you
                        // I think we need to propagate getting rid of the raw array in this class and convert
                        // to vector
                        return new SetIterator(this, toArray());
#else
                        return nullptr;
#endif
                    }
                    
                    template<typename T>
                    std::vector<T> *Array2DHashSet<T>::toArray()  {
                        std::vector<T> *a = createBucket(size());
                        int i = 0;
                        for (auto bucket : buckets) {
                            if (bucket.size() == 0) {
                                continue;
                            }
                            
                            for (auto o : bucket) {
                                if (o == nullptr) {
                                    break;
                                }
                                
                                (*a)[i++] = o;
                            }
                        }
                        
                        return a;
                    }
                    
                    template<typename T>
                    template<typename U>
                    U *Array2DHashSet<T>::toArray(U a[]) {
                        if (sizeof(a) / sizeof(a[0]) < size()) {
                            a = antlrcpp::Arrays::copyOf(a, size());
                        }
                        
                        int i = 0;
                        for (auto bucket : buckets) {
                            if (bucket == nullptr) {
                                continue;
                            }
                            
                            for (auto o : bucket) {
                                if (o == nullptr) {
                                    break;
                                }
                                
                                U targetElement = static_cast<U>(o); // array store will check this
                                a[i++] = targetElement;
                            }
                        }
                        return a;
                    }
                    
                    template<typename T>
                    bool Array2DHashSet<T>::remove(void *o) {
                        return removeFast(asElementType(o));
                    }
                    
                    template<typename T>
                    bool Array2DHashSet<T>::removeFast(T obj) {
                        if (obj == nullptr) {
                            return false;
                        }
                        
                        int b = getBucket(obj);
                        std::vector<T> bucket = buckets[b];
                        if (bucket.size() == 0) {
                            // no bucket
                            return false;
                        }
                        
                        for (int i = 0; i < (int)bucket.size(); i++) {
                            T e = bucket[i];
                            if (e == nullptr) {
                                // empty slot; not there
                                return false;
                            }
                            
                            if (comparator->equals(e, obj)) { // found it
                                // shift all elements to the right down one
                                //System::arraycopy(bucket, i + 1, bucket, i, bucket->length - i - 1);
                                for (int j = i; j < (int)bucket.size() - i - 1; j++) {
                                    bucket[j] = bucket[j + 1];
                                }
                                bucket[bucket.size() - 1] = nullptr;
                                n--;
                                return true;
                            }
                        }
                        
                        return false;
                    }
                    
                    template<typename T>
                    template<typename T1>
                    bool Array2DHashSet<T>::containsAll(std::set<T1> *collection) {
                        if ((Array2DHashSet*)(collection) != nullptr) {
                            Array2DHashSet<T> *s = (Array2DHashSet<T>*)(collection);
                            for (auto bucket : s->buckets) {
                                if (bucket.size() != 0) {
                                    continue;
                                }
                                for (auto o : bucket) {
                                    if (o == nullptr) {
                                        break;
                                    }
                                    if (!this->containsFast(asElementType(o))) {
                                        return false;
                                    }
                                }
                            }
                        } else {
#ifdef TODO
                            // barf ... this function isn't even being used
                            for (auto o : collection) {
                                if (!this->containsFast(asElementType(o))) {
                                    return false;
                                }
                            }
#endif
                        }
                        return true;
                    }
                    
                    template<typename T>
                    template<typename T1>
                    bool Array2DHashSet<T>::addAll(std::set<T1> *c) {
                        bool changed = false;
                        for (auto o : c) {
                            T existing = getOrAdd(o);
                            if (existing != o) {
                                changed = true;
                            }
                        }
                        return changed;
                    }
                    
                    template<typename T>
                    template<typename T1>
                    bool Array2DHashSet<T>::retainAll(std::set<T1> *c) {
                        int newsize = 0;
                        for (auto bucket : buckets) {
                            if (bucket == nullptr) {
                                continue;
                            }
                            
                            int i;
                            int j;
                            for (i = 0, j = 0; i < sizeof(bucket) / sizeof(bucket[0]); i++) {
                                if (bucket[i] == nullptr) {
                                    break;
                                }
                                
                                if (!c->contains(bucket[i])) {
                                    // removed
                                    continue;
                                }
                                
                                // keep
                                if (i != j) {
                                    bucket[j] = bucket[i];
                                }
                                
                                j++;
                                newsize++;
                            }
                            
                            newsize += j;
                            
                            while (j < i) {
                                //JAVA TO C++ CONVERTER WARNING: Java to C++ Converter converted the original 'null' assignment to a call to 'delete', but you should review memory allocation of all pointer variables in the converted code:
                                delete bucket[j];
                                j++;
                            }
                        }
                        
                        bool changed = newsize != n;
                        n = newsize;
                        return changed;
                    }
                    
                    template<typename T>
                    template<typename T1>
                    bool Array2DHashSet<T>::removeAll(std::set<T1> *c) {
                        bool changed = false;
                        for (auto o : c) {
                            changed |= removeFast(asElementType(o));
                        }
                        
                        return changed;
                    }
                    
                    template<typename T>
                    std::wstring Array2DHashSet<T>::toString() {
                        if (size() == 0) {
                            return L"{}";
                        }
                        
                        antlrcpp::StringBuilder *buf = new antlrcpp::StringBuilder();
                        buf->append(L"{");
                        bool first = true;
                        for (auto bucket : buckets) {
                            if (bucket.size() == 0) {
                                continue;
                            }
                            for (auto o : bucket) {
                                if (o == nullptr) {
                                    break;
                                }
                                if (first) {
                                    first = false;
                                } else {
                                    buf->append(L", ");
                                }
#ifdef TODO
                                buf->append(o->toString());
#endif
                                buf->append(L"TODO barf");
                            }
                        }
                        buf->append(L"}");
                        return buf->toString();
                    }
                    
                    template<typename T>
                    std::wstring Array2DHashSet<T>::toTableString() {
                        antlrcpp::StringBuilder *buf = new antlrcpp::StringBuilder();
                        for (auto bucket : buckets) {
                            if (bucket.size() == 0) {
                                buf->append(L"null\n");
                                continue;
                            }
                            buf->append(L"[");
                            bool first = true;
                            for (auto o : bucket) {
                                if (first) {
                                    first = false;
                                } else {
                                    buf->append(L" ");
                                }
                                if (o == nullptr) {
                                    buf->append(L"_");
                                } else {
#ifdef TODO
                                    buf->append(o->toString());
#endif
                                    buf->append(L"TODO barf");
                                }
                            }
                            buf->append(L"]\n");
                        }
                        return buf->toString();
                    }
                    /// <summary>
                    /// Return an array of {@code T[]} with length {@code capacity}.
                    /// </summary>
                    /// <param name="capacity"> the length of the array to return </param>
                    /// <returns> the newly constructed array </returns>
                    template<typename T>
                    std::vector<std::vector<T>> *Array2DHashSet<T>::createBuckets(int capacity) {
                        return new std::vector<std::vector<T>>();
                    }
                    
                    /// <summary>
                    /// Return an array of {@code T} with length {@code capacity}.
                    /// </summary>
                    /// <param name="capacity"> the length of the array to return </param>
                    /// <returns> the newly constructed array </returns>
                    template<typename T>
                    std::vector<T> * Array2DHashSet<T>::createBucket(int capacity) {
                        return new std::vector<T>();
                    }
                    
                    template<typename T>
                    void Array2DHashSet<T>::clear() {
                        buckets = *createBuckets(INITAL_CAPACITY);
                        n = 0;
                    }
                    
                    template<typename T>
                    void Array2DHashSet<T>::InitializeInstanceFields() {
                        n = 0;
                        threshold = static_cast<int>(INITAL_CAPACITY * LOAD_FACTOR);
                        currentPrime = 1;
                        initialBucketCapacity = INITAL_BUCKET_CAPACITY;
                    }
                    
                    
                }
            }
        }
    }
}

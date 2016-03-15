#include <algorithm>
#include <vector>
#include <stdarg.h>

#include "IntervalSet.h"
#include "Token.h"
#include "MurmurHash.h"
#include "Exceptions.h"
#include "Interval.h"
#include "Lexer.h"
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

                    IntervalSet *const IntervalSet::COMPLETE_CHAR_SET = IntervalSet::of(0, runtime::Lexer::MAX_CHAR_VALUE);
                    IntervalSet *const IntervalSet::EMPTY_SET = new IntervalSet(0);

                    IntervalSet::IntervalSet(std::vector<Interval*> &intervals) {
                        InitializeInstanceFields();
                        this->intervals = intervals;
                    }

                    IntervalSet::IntervalSet(IntervalSet *set) {
                        addAll(set);
                    }

                    IntervalSet::IntervalSet(int n, ...) {
                        InitializeInstanceFields();
                        va_list vlist;
                        va_start(vlist, n);
                        
                        for (int i = 0; i < n; i++) {
                            add(va_arg(vlist, int));
                        }
                    }

                    org::antlr::v4::runtime::misc::IntervalSet *IntervalSet::of(int a) {
                        IntervalSet *s = new IntervalSet(1, a);
                        return s;
                    }

                    org::antlr::v4::runtime::misc::IntervalSet *IntervalSet::of(int a, int b) {
                        IntervalSet *s = new IntervalSet(2, a, b);
                        return s;
                    }

                    void IntervalSet::clear() {
                        if (readonly) {
                            throw new IllegalStateException(L"can't alter readonly IntervalSet");
                        }
                        intervals.clear();
                    }

                    void IntervalSet::add(int el) {
                        if (readonly) {
                            throw new IllegalStateException(L"can't alter readonly IntervalSet");
                        }
                        add(1,el);
                    }

                    void IntervalSet::add(int a, int b) {
                        add(Interval::of(a, b));
                    }

                    void IntervalSet::add(Interval *addition) {
                        if (readonly) {
                            throw new IllegalStateException(L"can't alter readonly IntervalSet");
                        }
                        //System.out.println("add "+addition+" to "+intervals.toString());
                        if (addition->b < addition->a) {
                            return;
                        }
                        // find position in list
                        for (std::vector<Interval*>::iterator iter = intervals.begin(); iter != intervals.end(); ++iter) {
                            Interval *r = *iter;
                            if (addition->equals(r)) {
                                return;
                            }
                            if (addition->adjacent(r) || !addition->disjoint(r)) {
                                // next to each other, make a single larger interval
                                Interval *bigger = addition->union_Renamed(r);
                                (*iter) = bigger;
                                // make sure we didn't just create an interval that
                                // should be merged with next interval in list
                                
                                
                                while ( iter++ != intervals.end()) {
                                    Interval *next = *iter;
                                    if (!bigger->adjacent(next) && bigger->disjoint(next)) {
                                        break;
                                    }

                                    // if we bump up against or overlap next, merge
                                    intervals.erase(iter);// remove this one
                                    iter--; // move backwards to what we just set
                                    r = *iter;
                                    
                                    intervals.insert(iter, bigger->union_Renamed(next));//(*iter)->set(bigger->union_Renamed(next)); // set to 3 merged ones
                                    //*iter; // first call to next after previous duplicates the result
                                }
                                return;
                            }
                            if (addition->startsBeforeDisjoint(r)) {
                                // insert before r
                                iter--; //(*iter)->previous();
                                intervals.insert(iter, addition);//(*iter)->add(addition);
                                return;
                            }
                            // if disjoint and after r, a future iteration will handle it
                        }
                        // ok, must be after last interval (and disjoint from last interval)
                        // just add it
                        intervals.push_back(addition);
                    }

                    IntervalSet *IntervalSet::Or(std::vector<IntervalSet*> sets) {
                        IntervalSet *r = new IntervalSet(0);
                        for (auto s : sets) {
                            r->addAll(s);
                        }
                        return r;
                    }

                    IntervalSet *IntervalSet::addAll(IntSet *set) {
                        if (set == nullptr) {
                            return this;
                        }
                        if (!(dynamic_cast<IntervalSet*>(set) != nullptr)) {
                            throw IllegalArgumentException(std::wstring(L"can't add non IntSet (") + L"IntSet" + std::wstring(L") to IntervalSet"));
                        }
                        IntervalSet *other = static_cast<IntervalSet*>(set);
                        // walk set and add each interval
                        int n = (int)other->intervals.size();
                        for (int i = 0; i < n; i++) {
                            Interval *I = other->intervals[i];
                            this->add(I->a,I->b);
                        }
                        return this;
                    }

                    org::antlr::v4::runtime::misc::IntervalSet *IntervalSet::complement(int minElement, int maxElement) {
                        return this->complement(IntervalSet::of(minElement,maxElement));
                    }

                    misc::IntervalSet *IntervalSet::complement(IntSet *vocabulary) {
                        if (vocabulary == nullptr) {
                            return nullptr; // nothing in common with null set
                        }
                        if (!(dynamic_cast<IntervalSet*>(vocabulary) != nullptr)) {
                            throw new IllegalArgumentException(std::wstring(L"can't complement with non IntervalSet (") + std::wstring(L"IntSet") + std::wstring(L")"));
                        }
                        IntervalSet *vocabularyIS = (static_cast<IntervalSet*>(vocabulary));
                        int maxElement = vocabularyIS->getMaxElement();

                        IntervalSet *compliment = new IntervalSet(0);
                        size_t n = intervals.size();
                        if (n == 0) {
                            return compliment;
                        }
                        Interval *first = intervals[0];
                        // add a range from 0 to first.a constrained to vocab
                        if (first->a > 0) {
                            IntervalSet *s = IntervalSet::of(0, first->a - 1);
                            IntervalSet *a = s->And(vocabularyIS);
                            compliment->addAll(a);
                        }
                        for (size_t i = 1; i < n; i++) { // from 2nd interval .. nth
                            Interval *previous = intervals[i - 1];
                            Interval *current = intervals[i];
                            IntervalSet *s = IntervalSet::of(previous->b + 1, current->a - 1);
                            IntervalSet *a = s->And(vocabularyIS);
                            compliment->addAll(a);
                        }
                        Interval *last = intervals[n - 1];
                        // add a range from last.b to maxElement constrained to vocab
                        if (last->b < maxElement) {
                            IntervalSet *s = IntervalSet::of(last->b + 1, maxElement);
                            IntervalSet *a = s->And(vocabularyIS);
                            compliment->addAll(a);
                        }
                        return compliment;
                    }

                    misc::IntervalSet *IntervalSet::subtract(IntSet *other) {
                        // assume the whole unicode range here for the complement
                        // because it doesn't matter.  Anything beyond the max of this' set
                        // will be ignored since we are doing this & ~other.  The intersection
                        // will be empty.  The only problem would be when this' set max value
                        // goes beyond MAX_CHAR_VALUE, but hopefully the constant MAX_CHAR_VALUE
                        // will prevent this.
                        return this->And((static_cast<IntervalSet*>(other))->complement(COMPLETE_CHAR_SET));
                    }

                    misc::IntervalSet *IntervalSet::Or(IntSet *a) {
                        IntervalSet *o = new IntervalSet(0);
                        o->addAll(this);
                        o->addAll(a);
                        return o;
                    }

                    misc::IntervalSet *IntervalSet::And(IntSet *other) {
                        if (other == nullptr) { //|| !(other instanceof IntervalSet) ) {
                            return nullptr; // nothing in common with null set
                        }

                        std::vector<Interval*> myIntervals = this->intervals;
                        std::vector<Interval*> theirIntervals = (static_cast<IntervalSet*>(other))->intervals;
                        IntervalSet *intersection = nullptr;
                        size_t mySize = myIntervals.size();
                        size_t theirSize = theirIntervals.size();
                        size_t i = 0;
                        size_t j = 0;
                        // iterate down both interval lists looking for nondisjoint intervals
                        while (i < mySize && j < theirSize) {
                            Interval *mine = myIntervals[i];
                            Interval *theirs = theirIntervals[j];
                            //System.out.println("mine="+mine+" and theirs="+theirs);
                            if (mine->startsBeforeDisjoint(theirs)) {
                                // move this iterator looking for interval that might overlap
                                i++;
                            } else if (theirs->startsBeforeDisjoint(mine)) {
                                // move other iterator looking for interval that might overlap
                                j++;
                            } else if (mine->properlyContains(theirs)) {
                                // overlap, add intersection, get next theirs
                                if (intersection == nullptr) {
                                    intersection = new IntervalSet(0);
                                }
                                intersection->add(mine->intersection(theirs));
                                j++;
                            } else if (theirs->properlyContains(mine)) {
                                // overlap, add intersection, get next mine
                                if (intersection == nullptr) {
                                    intersection = new IntervalSet(0);
                                }
                                intersection->add(mine->intersection(theirs));
                                i++;
                            } else if (!mine->disjoint(theirs)) {
                                // overlap, add intersection
                                if (intersection == nullptr) {
                                    intersection = new IntervalSet(0);
                                }
                                intersection->add(mine->intersection(theirs));
                                // Move the iterator of lower range [a..b], but not
                                // the upper range as it may contain elements that will collide
                                // with the next iterator. So, if mine=[0..115] and
                                // theirs=[115..200], then intersection is 115 and move mine
                                // but not theirs as theirs may collide with the next range
                                // in thisIter.
                                // move both iterators to next ranges
                                if (mine->startsAfterNonDisjoint(theirs)) {
                                    j++;
                                } else if (theirs->startsAfterNonDisjoint(mine)) {
                                    i++;
                                }
                            }
                        }
                        if (intersection == nullptr) {
                            return new IntervalSet(0);
                        }
                        return intersection;
                    }

                    bool IntervalSet::contains(int el) {
                        size_t n = intervals.size();
                        for (size_t i = 0; i < n; i++) {
                            Interval *I = intervals[i];
                            int a = I->a;
                            int b = I->b;
                            if (el < a) {
                                break; // list is sorted and el is before this interval; not here
                            }
                            if (el >= a && el <= b) {
                                return true; // found in this interval
                            }
                        }
                        return false;
                                        /*
                                        		for (ListIterator iter = intervals.listIterator(); iter.hasNext();) {
                                                    Interval I = (Interval) iter.next();
                                                    if ( el<I.a ) {
                                                        break; // list is sorted and el is before this interval; not here
                                                    }
                                                    if ( el>=I.a && el<=I.b ) {
                                                        return true; // found in this interval
                                                    }
                                                }
                                                return false;
                                                */
                    }

                    bool IntervalSet::isNil() {
                        return intervals.empty() || intervals.empty();
                    }

                    int IntervalSet::getSingleElement() {
                        if (intervals.size() > 0 && intervals.size() == 1) {
                            Interval *I = intervals[0];
                            if (I->a == I->b) {
                                return I->a;
                            }
                        }
                        return Token::INVALID_TYPE;
                    }

                    int IntervalSet::getMaxElement() {
                        if (isNil()) {
                            return Token::INVALID_TYPE;
                        }
                        Interval *last = intervals[intervals.size() - 1];
                        return last->b;
                    }

                    int IntervalSet::getMinElement() {
                        if (isNil()) {
                            return Token::INVALID_TYPE;
                        }
                        size_t n = intervals.size();
                        for (size_t i = 0; i < n; i++) {
                            Interval *I = intervals[i];
                            int a = I->a;
                            int b = I->b;
                            for (int v = a; v <= b; v++) {
                                if (v >= 0) {
                                    return v;
                                }
                            }
                        }
                        return Token::INVALID_TYPE;
                    }

                    std::vector<Interval*> IntervalSet::getIntervals() {
                        return intervals;
                    }

                    int IntervalSet::hashCode() {
                        int hash = MurmurHash::initialize();
                        for (auto I : intervals) {
                            hash = MurmurHash::update(hash, I->a);
                            hash = MurmurHash::update(hash, I->b);
                        }

                        hash = MurmurHash::finish(hash, (int)intervals.size() * 2);
                        return hash;
                    }

                    bool IntervalSet::equals(void *obj) {
                        if (obj == nullptr || !(static_cast<IntervalSet*>(obj) != nullptr)) {
                            return false;
                        }
                        IntervalSet *other = static_cast<IntervalSet*>(obj);
                        return std::equal(this->intervals.begin(), this->intervals.end(), other->intervals.begin());
                    }

                    std::wstring IntervalSet::toString() {
                        return toString(false);
                    }

                    std::wstring IntervalSet::toString(bool elemAreChar) {
                        antlrcpp::StringBuilder *buf = new antlrcpp::StringBuilder();
                        if (this->intervals.empty() || this->intervals.empty()) {
                            return L"{}";
                        }
                        if (this->size() > 1) {
                            buf->append(L"{");
                        }
                        std::vector<Interval*>::const_iterator iter = this->intervals.begin();
                        while (iter != this->intervals.end()) {
                            Interval *I = *iter;
                            int a = I->a;
                            int b = I->b;
                            if (a == b) {
                                if (a == -1) {
                                    buf->append(L"<EOF>");
                                } else if (elemAreChar) {
                                    buf->append(L"'").append(static_cast<wchar_t>(a)).append(L"'");
                                } else {
                                    buf->append(a);
                                }
                            } else {
                                if (elemAreChar) {
                                    buf->append(L"'").append(static_cast<wchar_t>(a)).append(L"'..'").append(static_cast<wchar_t>(b)).append(L"'");
                                } else {
                                    buf->append(a).append(L"..").append(b);
                                }
                            }
                            iter++;
                            if (iter == this->intervals.end()) {
                                buf->append(L", ");
                            }
                        }
                        if (this->size() > 1) {
                            buf->append(L"}");
                        }
                        return buf->toString();
                    }

                    std::wstring IntervalSet::toString(std::wstring tokenNames[]) {
                        antlrcpp::StringBuilder *buf = new antlrcpp::StringBuilder();
                        if (this->intervals.empty() || this->intervals.empty()) {
                            return L"{}";
                        }
                        if (this->size() > 1) {
                            buf->append(L"{");
                        }
                        std::vector<Interval*>::const_iterator iter = this->intervals.begin();
                        while (iter != this->intervals.end()) {
                            Interval *I = *iter;
                            int a = I->a;
                            int b = I->b;
                            if (a == b) {
                                buf->append(elementName(tokenNames, a));
                            } else {
                                for (int i = a; i <= b; i++) {
                                    if (i > a) {
                                        buf->append(L", ");
                                    }
                                    buf->append(elementName(tokenNames, i));
                                }
                            }
                            iter++;
                            if (iter == this->intervals.end()) {
                                buf->append(L", ");
                            }
                        }
                        if (this->size() > 1) {
                            buf->append(L"}");
                        }
                        return buf->toString();
                    }
                    
                    std::wstring IntervalSet::toString(std::vector<std::wstring> tokenNames) {
                        antlrcpp::StringBuilder *buf = new antlrcpp::StringBuilder();
                        if (this->intervals.empty() || this->intervals.empty()) {
                            return L"{}";
                        }
                        if (this->size() > 1) {
                            buf->append(L"{");
                        }
                        std::vector<Interval*>::const_iterator iter = this->intervals.begin();
                        while (iter != this->intervals.end()) {
                            Interval *I = *iter;
                            int a = I->a;
                            int b = I->b;
                            if (a == b) {
                                buf->append(elementName(tokenNames, a));
                            } else {
                                for (int i = a; i <= b; i++) {
                                    if (i > a) {
                                        buf->append(L", ");
                                    }
                                    buf->append(elementName(tokenNames, i));
                                }
                            }
                            iter++;
                            if (iter == this->intervals.end()) {
                                buf->append(L", ");
                            }
                        }
                        if (this->size() > 1) {
                            buf->append(L"}");
                        }
                        return buf->toString();
                    }

                    std::wstring IntervalSet::elementName(std::wstring tokenNames[], int a) {
                        if (a == Token::_EOF) {
                            return L"<EOF>";
                        } else if (a == Token::EPSILON) {
                            return L"<EPSILON>";
                        } else {
                            return tokenNames[a];
                        }
                        
                    }
                    
                    std::wstring IntervalSet::elementName(std::vector<std::wstring> tokenNames, int a) {
                        if (a == Token::_EOF) {
                            return L"<EOF>";
                        } else if (a == Token::EPSILON) {
                            return L"<EPSILON>";
                        } else {
                            return tokenNames[a];
                        }
                        
                    }

                    int IntervalSet::size() {
                        size_t n = 0;
                        size_t numIntervals = intervals.size();
                        if (numIntervals == 1) {
                            Interval *firstInterval = this->intervals[0];
                            return firstInterval->b - firstInterval->a + 1;
                        }
                        for (size_t i = 0; i < numIntervals; i++) {
                            Interval *I = intervals[i];
                            n += (I->b - I->a + 1);
                        }
                        return (int)n;
                    }

                    std::vector<int> IntervalSet::toList() {
                        std::vector<int> values = std::vector<int>();
                        size_t n = intervals.size();
                        for (size_t i = 0; i < n; i++) {
                            Interval *I = intervals[i];
                            size_t a = I->a;
                            size_t b = I->b;
                            for (size_t v = a; v <= b; v++) {
                                values.push_back((int)v);
                            }
                        }
                        return values;
                    }

                    std::set<int> *IntervalSet::toSet() {
                        std::set<int> *s = new std::set<int>();
                        for (auto I : intervals) {
                            size_t a = I->a;
                            size_t b = I->b;
                            for (size_t v = a; v <= b; v++) {
                                s->insert((int)v);
                            }
                        }
                        return s;
                    }

                    int IntervalSet::get(int i) {
                        size_t n = intervals.size();
                        size_t index = 0;
                        for (size_t j = 0; j < n; j++) {
                            Interval *I = intervals[j];
                            size_t a = I->a;
                            size_t b = I->b;
                            for (size_t v = a; v <= b; v++) {
			        if (index == (size_t)i) {
                                    return (int)v;
                                }
                                index++;
                            }
                        }
                        return -1;
                    }

                    void IntervalSet::remove(int el) {
                        if (readonly) {
                            throw IllegalStateException(L"can't alter readonly IntervalSet");
                        }
		
                        size_t n = intervals.size();
                        for (size_t i = 0; i < n; i++) {
                            Interval *I = intervals[i];
                            size_t a = I->a;
                            size_t b = I->b;
                            if ((size_t)el < a) {
                                break; // list is sorted and el is before this interval; not here
                            }
                            // if whole interval x..x, rm
                            if ((size_t)el == a && (size_t)el == b) {
                                intervals.erase(intervals.begin() + i);
                                break;
                            }
                            // if on left edge x..b, adjust left
                            if ((size_t)el == a) {
                                I->a++;
                                break;
                            }
                            // if on right edge a..x, adjust right
                            if ((size_t)el == b) {
                                I->b--;
                                break;
                            }
                            // if in middle a..x..b, split interval
                            if ((size_t)el > a && (size_t)el < b) { // found in this interval
                                size_t oldb = I->b;
                                I->b = el - 1; // [a..x-1]
                                add(el + 1, (int)oldb); // add [x+1..b]
                            }
                        }
                    }

                    bool IntervalSet::isReadonly() {
                        return readonly;
                    }

                    void IntervalSet::setReadonly(bool readonly) {
                        this->readonly = readonly;
                    }

                    void IntervalSet::InitializeInstanceFields() {
                        readonly = false;
                    }
                }
            }
        }
    }
}

/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
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

#pragma once

namespace org {
namespace antlr {
namespace v4 {
namespace runtime {
namespace atn {

  class ConfigLookup {
  protected:
    class ConfigLookupIteratorImpl;

  public:
    class ConfigLookupIterator {
    public:
      ConfigLookupIterator(ConfigLookupIteratorImpl* impl) : _iterator(impl) {}

      ConfigLookupIterator& operator++ () { ++*_iterator; return *this; };
      bool operator != (const ConfigLookupIterator& rhs) const { return *_iterator != *rhs._iterator; };
      Ref<ATNConfig> operator * () const { return **_iterator; };
    private:
      Ref<ConfigLookupIteratorImpl> _iterator;
    };

    virtual ~ConfigLookup() {}

    // Java iterator interface.
    virtual Ref<ATNConfig> getOrAdd(Ref<ATNConfig> config) = 0;
    virtual bool isEmpty() const = 0;
    virtual bool contains(Ref<ATNConfig> config) const = 0;
    virtual void clear() = 0;

    // STL iterator interface.
    virtual ConfigLookupIterator begin() = 0;
    virtual ConfigLookupIterator end() = 0;
    virtual size_t size() const = 0;

  protected:
    class ConfigLookupIteratorImpl {
    public:
      virtual ConfigLookupIteratorImpl& operator ++ () = 0;
      virtual bool operator != (const ConfigLookupIteratorImpl&) const = 0;
      virtual Ref<ATNConfig> operator * () const = 0;
      virtual void* underlyingIterator () = 0;
      virtual const void* underlyingIterator () const = 0;
    };
  };

  template <typename Hasher, typename Comparer>
  class ConfigLookupImpl: public ConfigLookup, std::unordered_set<Ref<ATNConfig>, Hasher, Comparer> {
  public:
    using Set = std::unordered_set<Ref<ATNConfig>, Hasher, Comparer>;

    virtual Ref<ATNConfig> getOrAdd(Ref<ATNConfig> config) override {
      auto result = Set::find(config);
      if (result != Set::end())
        // Can potentially be a different config instance which however is considered equal to the given config
        // (wrt. to all those fields involved in hash computation).
        return *result;

      Set::insert(config);
      return config;
    }

    virtual bool isEmpty() const override {
      return Set::empty();
    }

    virtual bool contains(Ref<ATNConfig> config) const override {
      return Set::count(config) > 0;
    }

    virtual void clear() override {
      Set::clear();
    }

    size_t size () const override {
      return Set::size();
    }

    ConfigLookupIterator begin() override {
      return ConfigLookupIterator(
        new ConfigLookupImpl<Hasher, Comparer>::ConfigLookupIteratorImpl(
          std::unordered_set<Ref<ATNConfig>, Hasher, Comparer>::begin())); /* mem check: managed by shared_ptr in the iterator */
    }

    ConfigLookupIterator end() override {
      return ConfigLookupIterator(
        new ConfigLookupImpl<Hasher, Comparer>::ConfigLookupIteratorImpl(
          std::unordered_set<Ref<ATNConfig>, Hasher, Comparer>::end()));  /* mem check: managed by shared_ptr in the iterator */
    }

  protected:
    class ConfigLookupIteratorImpl : public ConfigLookup::ConfigLookupIteratorImpl {
    public:
      using UnderlyingIterator = typename std::unordered_set<Ref<ATNConfig>, Hasher, Comparer>::iterator;

      ConfigLookupIteratorImpl(UnderlyingIterator&& iterator) : _iterator(std::move(iterator)) {
      }

      ConfigLookupIteratorImpl& operator++ () override {
        ++_iterator; return *this;
      }

      bool operator != (const ConfigLookup::ConfigLookupIteratorImpl& rhs) const override {
        return *reinterpret_cast<const UnderlyingIterator*>(underlyingIterator()) != *reinterpret_cast<const UnderlyingIterator*>(rhs.underlyingIterator());
      }

      Ref<ATNConfig> operator * () const override {
        return *_iterator;
      }
      
      void* underlyingIterator () override {
        return &_iterator;
      }
      
      const void* underlyingIterator () const override {
        return &_iterator;
      }
    private:
      UnderlyingIterator _iterator;
    };
  };
  
} // namespace atn
} // namespace runtime
} // namespace v4
} // namespace antlr
} // namespace org

/** @file sbit/usop.h
 * @brief Unsynchronized Object Pool implementation
 * @author Florin Iucha <florin@signbit.net>
 *
 * Copyright 2020 Florin Iucha
 *
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#ifndef SBIT_USOP_H_INCLUDED
#define SBIT_USOP_H_INCLUDED

#include <array>
#include <cassert>
#include <cstdint>
#ifdef USOP_PARANOID_FILL
#include <cstring>
#endif
#include <iterator>
#include <limits>
#include <list>
#include <memory>

/** @addtogroup sbit
 * @{
 */

namespace sbit
{

/** @brief Internal implementation detail
 */
struct WrapperHeader
{
   using DeleterFunc = void (*)(void*, void*);

   uint32_t referenceCount;
   uint32_t busy : 1;
   uint32_t offset : 31;

   void addRef()
   {
      assert(busy);
      assert(static_cast<size_t>(referenceCount) < std::numeric_limits<uint32_t>::max());
      referenceCount++;
   }

   void release() noexcept
   {
      assert(referenceCount != 0);
      assert(busy);

      referenceCount--;
      if (0 == referenceCount)
      {
         /*
          * Find the segment that contains this object,
          */
         auto* segmentImpl = reinterpret_cast<char*>(this) - offset;

         /*
          * ... retrieve the pointer to the deleter,
          */
         auto deleterFunc = *reinterpret_cast<DeleterFunc*>(segmentImpl);

         /*
          * ... call the deleter to release the object
          */
         (*deleterFunc)(segmentImpl, this);
      }
   }
};

/** @brief Smart pointer wrapping an object managed by a pool
 *
 * @tparam T is the object type
 */
template <typename T>
class PooledPointer
{
public:
   struct ObjectWrapper
   {
      WrapperHeader header;

      T t;
   };

   PooledPointer() = default;

   ~PooledPointer()
   {
      release();
   }

   explicit PooledPointer(WrapperHeader* header) noexcept
   {
      if (header != nullptr)
      {
         m_pointer = reinterpret_cast<T*>(std::next(header, 1));
         header->addRef();
      }
   }

   explicit PooledPointer(ObjectWrapper* wrapper) noexcept
   {
      if (wrapper != nullptr)
      {
         m_pointer = &wrapper->t;
         wrapper->header.addRef();
      }
   }

   PooledPointer(const PooledPointer& other) noexcept : m_pointer{other.m_pointer}
   {
      if (m_pointer != nullptr)
      {
         getWrapper()->addRef();
      }
   }

   template <class U, class = typename std::enable_if<std::is_base_of<T, U>::value>::type>
   PooledPointer(const PooledPointer<U>& other) noexcept : m_pointer{other.m_pointer}
   {
      if (m_pointer != nullptr)
      {
         getWrapper()->addRef();
      }
   }

   template <class U, class = typename std::enable_if<std::is_base_of<T, U>::value>::type>
   PooledPointer(PooledPointer<U>&& other) noexcept
   {
      void* temp      = reinterpret_cast<void*>(other.m_pointer);
      other.m_pointer = reinterpret_cast<U*>(m_pointer);
      m_pointer       = reinterpret_cast<T*>(temp);
   }

   PooledPointer& operator=(const PooledPointer& other) noexcept
   {
      if (this != &other)
      {
         release();

         m_pointer = other.m_pointer;
         if (m_pointer != nullptr)
         {
            getWrapper()->addRef();
         }
      }
      return *this;
   }

   template <class U, class = typename std::enable_if<std::is_base_of<T, U>::value>::type>
   PooledPointer& operator=(const PooledPointer<U>& other) noexcept
   {
      if (this != &other)
      {
         release();

         m_pointer = other.m_pointer;
         if (m_pointer != nullptr)
         {
            getWrapper()->addRef();
         }
      }
      return *this;
   }

   template <class U, class = typename std::enable_if<std::is_base_of<T, U>::value>::type>
   PooledPointer& operator=(PooledPointer<U>&& other) noexcept
   {
      if (this != &other)
      {
         void* temp      = reinterpret_cast<void*>(other.m_pointer);
         other.m_pointer = reinterpret_cast<U*>(m_pointer);
         m_pointer       = reinterpret_cast<T*>(temp);
      }
      return *this;
   }

   T* operator->() const
   {
      assert(m_pointer != nullptr);
      assertValid();
      return m_pointer;
   }

   explicit operator bool() const
   {
      return m_pointer != nullptr;
   }

   T& operator*() const
   {
      assert(m_pointer != nullptr);
      assertValid();
      return *m_pointer;
   }

   T* get() const
   {
      return m_pointer;
   }

   bool operator==(const PooledPointer& other) const noexcept
   {
      if (m_pointer == other.m_pointer)
      {
         return true;
      }

      if (m_pointer == nullptr)
      {
         return false;
      }

      return get()->operator==(*other.get());
   }

   template <typename U>
   PooledPointer<U> castTo() const noexcept
   {
      return PooledPointer<U>(getWrapper());
   }

private:
   template <typename U>
   friend class PooledPointer;

   WrapperHeader* getWrapper() const
   {
      return std::prev(reinterpret_cast<WrapperHeader*>(m_pointer), 1);
   }

   void assertValid() const
   {
#ifndef NDEBUG
      auto* wrapper = getWrapper();
      assert(wrapper->busy);
      assert(wrapper->referenceCount > 0);
#endif
   }

   void release() noexcept
   {
      if (m_pointer != nullptr)
      {
         auto* wrapper = getWrapper();
         assert(wrapper->busy);
         wrapper->release();
      }
   }

   T* m_pointer = nullptr;
};

/* @brief Cast between pointer types, unsafely
 *
 * Since the PooledPointers actually point to the typeless wrapper, casting
 * between PooledPointers means just constructing a new pointer with the new
 * type and increasing the reference count. Type safety is left as an exercise
 * to the used.
 */
template <typename T, typename U>
inline PooledPointer<T> static_pointer_cast(const PooledPointer<U>& other) noexcept
{
   return other.template castTo<T>();
}

/** @brief Object pool implementation
 *
 * An object pool contains multiple segments. Some segments are partially in
 * use (contain live objects) and some are completely empty. The segments are
 * contained in doubly-linked lists, so it is easy to move segments between
 * the lists as needed. There is no need to immediately access anything other
 * than the last used segment, pointed to by m_lastUsedSegment
 *
 * The segments contain a standard array of elements, plus a deleter function
 * pointer, a use counter, a free list head, and a high watermark head.
 *
 *  +-----------------------------------------------------------------------+
 *  | Object pool                                                           |
 *  |                                                                       |
 *  |  +---------+   +---------+   +---------+   +---------+   +---------+  |
 *  |  | Segment |   | Segment |   | Segment |   | Segment |   | Segment |  |
 *  |  +---------+   +---------+   +---------+   +---------+   +---------+  |
 *  |  |         |   |         |   |         |   |         |   |         |  |
 *  |  | deleter |   | deleter |   | deleter |   | deleter |   | deleter |  |
 *  |  |         |   |         |   |         |   |         |   |         |  |
 *  |  | element |   | element |   | element |   | element |   | element |  |
 *  |  | element |   | element |   | element |   | element |   | element |  |
 *  |  | element |   | element |   | element |   | element |   | element |  |
 *  |  | element |   | element |   | element |   | element |   | element |  |
 *  |  |   ...   |   |   ...   |   |   ...   |   |   ...   |   |   ...   |  |
 *  |  |   ...   |   |   ...   |   |   ...   |   |   ...   |   |   ...   |  |
 *  |  |         |   |         |   |         |   |         |   |         |  |
 *  |  |         |   |         |   |         |   |         |   |         |  |
 *  |  | usedCnt |   | usedCnt |   | usedCnt |   | usedCnt |   | usedCnt |  |
 *  |  | free->  |   | free->  |   | free->  |   | free->  |   | free->  |  |
 *  |  | last->  |   | last->  |   | last->  |   | last->  |   | last->  |  |
 *  |  |         |   |         |   |         |   |         |   |         |  |
 *  |  +---------+   +---------+   +---------+   +---------+   +---------+  |
 *  |                                                                       |
 *  +-----------------------------------------------------------------------+
 *
 * Free space in a segment is managed with two techniques:
 *   * the area that was never used is pointed to by the
 *     m_highestUsedEntry offset (drawn as last-> above)
 *   * elements that were freed are linked together via the referenceCount
 *     offset; the head of the list is pointed to by m_freeHead (drawn as
 *     free-> above).
 *
 * To allocate an entry from the segment, we first check to see if there is
 * space available (m_used < segment size). If so, m_freeHead will contain the
 * offset of a free element. If m_freeHead is equal to the m_highestUsedEntry
 * then m_highestUsedEntry is incremented to indicate that the "used" zone
 * has been expanded.
 *
 * The elements consist of a wrapper header plus space for the actual object
 * managed by the pool.
 *
 *  +----------------------------+
 *  | Element                    |
 *  +----------+-----------------+
 *  |          |                 |
 *  | offset   | reference count |
 *  |          |                 |
 *  +----------+-----------------+
 *  |                            |
 *  |                            |
 *  |        Object              |
 *  |                            |
 *  |                            |
 *  +----------------------------+
 *
 * The object returned by the pool is managed by PooledPointer which points to
 * the wrapper before the object. Each time the PooledPointer is copied, the
 * reference counter is incremented. When the PooledPointer is destroyed, the
 * reference counter is decremented. When the reference counter reaches 0, the
 * object must be destroyed and its space returned to the pool.
 *
 * To destroy an object, we subtract the 'offset' value from the pointer to
 * the wrapper, and this way we find the address of the segment that contains
 * the memory area. At the beginning of the segment we have a pointer to a
 * deleter function which essentially wraps the call to the actual object
 * destructor.
 *
 * Space overhead:
 *    * 1 uint64_t for each object
 *    * 1 uint64_t + 3 uint32_t for each segment, containing a number of elements
 *
 * Runtime overhead:
 *    * should be negligible since there is no synchronization (no atomics,
 *      no cache flushes)
 *    * dereferencing the pooling pointer happens inline
 *
 *
 * @tparam T is the type of the objects managed by the pool
 */
template <typename T>
class UnsynchronizedObjectPool
{
public:
   using Pointer = sbit::PooledPointer<T>;

   /** @brief Internal implementation detail
    */
   class SegmentImpl
   {
   public:
      SegmentImpl() = default;

      ~SegmentImpl()
      {
         assert(isEmpty());
      }

      SegmentImpl(const SegmentImpl& other) = delete;
      SegmentImpl(SegmentImpl&& other)      = delete;

      SegmentImpl& operator=(const SegmentImpl& other) = delete;
      SegmentImpl& operator=(SegmentImpl&& other) = delete;

      void initializeEntry(typename Pointer::ObjectWrapper* element, uint32_t pos)
      {
         element->header.busy    = 0;
         const auto signedOffset = std::distance(reinterpret_cast<char*>(this), reinterpret_cast<char*>(element));
         assert(signedOffset >= 0);
         assert(signedOffset < std::numeric_limits<uint32_t>::max());
         element->header.offset         = static_cast<uint32_t>(signedOffset);
         element->header.referenceCount = pos + 1;
      }

      void initialize(uint32_t capacity)
      {
         m_freeHead         = 0;
         m_used             = 0;
         m_highestUsedEntry = 0;
         m_capacity         = capacity;

         m_deleterFunc = reinterpret_cast<WrapperHeader::DeleterFunc>(&SegmentImpl::destroyObject);
      }

      typename Pointer::ObjectWrapper* allocate() noexcept
      {
         if (isFull())
         {
            return nullptr;
         }

         assert(m_capacity > m_used);
         m_used++;

         assert(m_freeHead <= m_highestUsedEntry);

         auto* element = getElement(m_freeHead);

         if (m_freeHead == m_highestUsedEntry)
         {
            initializeEntry(element, m_freeHead);
            m_highestUsedEntry++;
            assert(m_capacity >= m_highestUsedEntry);
         }

         assert(0 == element->header.busy);
         m_freeHead                     = element->header.referenceCount;
         element->header.referenceCount = 0;
         element->header.busy           = 1;
         return element;
      }

      bool isFull() const noexcept
      {
         return (m_capacity == m_used);
      }

      bool isEmpty() const noexcept
      {
         return (0 == m_used);
      }

      uint32_t getSize() const noexcept
      {
         return m_capacity;
      }

      template <class... Args>
      typename Pointer::ObjectWrapper* create(Args&&... args) noexcept
      {
         auto* ptr = allocate();
         if (ptr == nullptr)
         {
            return nullptr;
         }

         assert(0 == ptr->header.referenceCount);

#ifdef USOP_PARANOID_FILL
         memset(static_cast<void*>(&ptr->t), 0, sizeof(T));
#endif
         std::allocator<T> globalAllocator;
         std::allocator_traits<std::allocator<T>>::construct(globalAllocator, &ptr->t, std::forward<Args>(args)...);
         return ptr;
      }

      void release(uint32_t offset)
      {
         assert(m_used > 0);
         m_used--;

         auto* element = getElement(offset);

         assert(element->header.busy);
         assert(0 == element->header.referenceCount);

         std::allocator<T> globalAllocator;
         std::allocator_traits<std::allocator<T>>::destroy(globalAllocator, &element->t);
#ifdef USOP_PARANOID_FILL
         memset(static_cast<void*>(&m_elements[offset].t), 0xab, sizeof(T));
#endif

         element->header.busy = 0;
         if (m_used == 0)
         {
            resetFreeList();
         }
         else
         {
            // add to free list
            element->header.referenceCount = m_freeHead;
            m_freeHead                     = offset;
         }
      }

      void release(typename Pointer::ObjectWrapper* wrapper)
      {
         assert(0 == wrapper->header.referenceCount);

         const auto signedOffset = std::distance(getElement(0), wrapper);
         assert(signedOffset >= 0);
         assert(signedOffset < std::numeric_limits<uint32_t>::max());
         const auto offset = static_cast<uint32_t>(signedOffset);
         assert(offset < m_capacity);

         release(offset);
      }

      void resetFreeList()
      {
         assert(m_used == 0);

         m_freeHead         = 0;
         m_highestUsedEntry = 0;
      }

   private:
      typename Pointer::ObjectWrapper* getElement(uint32_t pos)
      {
         const auto segmentImplOverhead = (sizeof(SegmentImpl) + sizeof(typename Pointer::ObjectWrapper) - 1) /
                                          sizeof(typename Pointer::ObjectWrapper);
         assert(pos < m_capacity);

         typename Pointer::ObjectWrapper* elements = reinterpret_cast<typename Pointer::ObjectWrapper*>(this);
         return std::next(elements, segmentImplOverhead + pos);
      }

      static void destroyObject(SegmentImpl* segment, typename Pointer::ObjectWrapper* object)
#if defined(__clang__)
         __attribute__((no_sanitize("undefined")))
#endif
      {
         segment->release(object);
      }

      WrapperHeader::DeleterFunc m_deleterFunc = nullptr;

      uint32_t m_freeHead         = 0;
      uint32_t m_used             = 0;
      uint32_t m_highestUsedEntry = 0;
      uint32_t m_capacity         = 0;

#if 0
      /*
       * can't write this here because the template is incomplete at this point
       * can't write it outside because m_elements and m_deleterFunc are private
       */

      // m_deleterFunc is the first object in SegmentImpl
      static_assert(offsetof(SegmentImpl, m_deleterFunc) == 0,
                    "m_deleterFunc is not the first sub-object of SegmentImpl");
#endif
   };

   /** @brief Internal implementation detail
    */
   class Segment
   {
   public:
      Segment(uint32_t size)
      {
         const auto segmentImplOverhead = (sizeof(SegmentImpl) + sizeof(typename Pointer::ObjectWrapper) - 1) /
                                          sizeof(typename Pointer::ObjectWrapper);

         std::allocator<typename Pointer::ObjectWrapper> globalAllocator;
         m_segment = reinterpret_cast<SegmentImpl*>(
            std::allocator_traits<std::allocator<typename Pointer::ObjectWrapper>>::allocate(
               globalAllocator, size + segmentImplOverhead));
         m_segment->initialize(size);
      }

      ~Segment()
      {
         const auto segmentImplOverhead = (sizeof(SegmentImpl) + sizeof(typename Pointer::ObjectWrapper) - 1) /
                                          sizeof(typename Pointer::ObjectWrapper);

         std::allocator<typename Pointer::ObjectWrapper> globalAllocator;
         std::allocator_traits<std::allocator<typename Pointer::ObjectWrapper>>::deallocate(
            globalAllocator,
            reinterpret_cast<typename Pointer::ObjectWrapper*>(m_segment),
            m_segment->getSize() + segmentImplOverhead);
      }

      Segment(const Segment& other) = delete;
      Segment(Segment&& other)      = delete;

      Segment& operator=(const Segment& other) = delete;
      Segment& operator=(Segment&& other) = delete;

      bool isEmpty()
      {
         return m_segment->isEmpty();
      }

      bool isFull()
      {
         return m_segment->isFull();
      }

      void resetFreeList()
      {
         m_segment->resetFreeList();
      }

      template <class... Args>
      typename Pointer::ObjectWrapper* create(Args&&... args) noexcept
      {
         return m_segment->create(std::forward<Args>(args)...);
      }

      void release(typename Pointer::ObjectWrapper* ptr)
      {
         m_segment->release(ptr);
      }

   private:
      SegmentImpl* m_segment;
   };

   /** Constructor
    */
   UnsynchronizedObjectPool(uint32_t size) : m_size{size}
   {
      m_segments.emplace_back(m_size);
      m_lastUsedSegment = m_segments.begin();
   }

   ~UnsynchronizedObjectPool() = default;

   /// @private
   UnsynchronizedObjectPool(const UnsynchronizedObjectPool& other) = delete;

   /// @private
   UnsynchronizedObjectPool& operator=(const UnsynchronizedObjectPool& other) = delete;

   UnsynchronizedObjectPool(UnsynchronizedObjectPool&& other) noexcept
   {
      std::swap(m_segments, other.m_segments);
      std::swap(m_emptySegments, other.m_emptySegments);
      std::swap(m_stats, other.m_stats);
   }

   UnsynchronizedObjectPool& operator=(UnsynchronizedObjectPool&& other) = delete;

   /** Constructs an object by perfectly forwarding the constructor arguments
    *
    * @tparam Args are the constructor argument types
    * @param args are the constructor arguments
    * @return a smart pointer referencing the newly created object
    */
   template <class... Args>
   Pointer create(Args&&... args)
   {
      auto& segment = getSegmentWithFreeSpace();
      auto* ptr     = segment.create(std::forward<Args>(args)...);
      return sbit::PooledPointer<T>{ptr};
   }

   /** Scans through the used segments and moves the empty ones to the empty list
    *
    */
   void compactFast()
   {
      while (m_lastUsedSegment->isEmpty())
      {
         /*
          * move empty block aside to increase compaction
          */
         auto nextUsedSegment = std::next(m_lastUsedSegment);
         if (nextUsedSegment == m_segments.end())
         {
            nextUsedSegment = m_segments.begin();
         }

         if (nextUsedSegment == m_lastUsedSegment)
         {
            // this is the only element in the active list; can't remove it
            m_stats.poolEmpty++;
            break;
         }

         m_lastUsedSegment->resetFreeList();
         m_emptySegments.splice(m_emptySegments.begin(), m_segments, m_lastUsedSegment);
         m_lastUsedSegment = nextUsedSegment;
         m_stats.releaseEmptySegment++;
      }
   }

private:
   Segment& getSegmentWithFreeSpace()
   {
      compactFast();

      if (!m_lastUsedSegment->isFull())
      {
         m_stats.allocateInCurrentSegment++;
      }
      else
      {
         m_stats.spillIntoNextSegment++;

         const typename std::list<Segment>::iterator startSegment = m_lastUsedSegment;
         m_lastUsedSegment++;
         if (m_lastUsedSegment == m_segments.end())
         {
            m_lastUsedSegment = m_segments.begin();
         }

         while (m_lastUsedSegment->isFull() && (startSegment != m_lastUsedSegment))
         {
            m_lastUsedSegment++;
            if (m_lastUsedSegment == m_segments.end())
            {
               m_lastUsedSegment = m_segments.begin();
            }
         }

         if (startSegment == m_lastUsedSegment)
         {
            m_stats.fullScans++;

            if (m_emptySegments.empty())
            {
               m_lastUsedSegment = m_segments.emplace(/* position = */ startSegment, m_size);
               m_stats.maxAllocatedSegments++;
            }
            else
            {
               assert(m_emptySegments.begin()->isEmpty());
               m_segments.splice(m_segments.begin(), m_emptySegments, m_emptySegments.begin());
               m_lastUsedSegment = m_segments.begin();
               assert(m_lastUsedSegment->isEmpty());
               m_stats.recycledSegment++;
            }
         }
      }

      assert(!m_lastUsedSegment->isFull());
      return *m_lastUsedSegment;
   }

   uint32_t m_size;

   std::list<Segment> m_segments;
   std::list<Segment> m_emptySegments;

   typename std::list<Segment>::iterator m_lastUsedSegment;

   struct Statistics
   {
      size_t poolEmpty            = 0;
      size_t releaseEmptySegment  = 0;
      size_t fullScans            = 0;
      size_t maxAllocatedSegments = 0;
      size_t recycledSegment      = 0;

      size_t spillIntoNextSegment     = 0;
      size_t allocateInCurrentSegment = 0;

#if 0
      // TODO(florin) measure allocations that are in successive slots
      size_t inOrderAllocation    = 0;
      size_t outOfOrderAllocation = 0;
#endif

   } m_stats;
};

} // namespace sbit

/** @} group sbit */

#endif // SBIT_USOP_H_INCLUDED

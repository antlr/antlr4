// SplitOrderedList.cs
//
// Copyright (c) 2010 Jérémie "Garuma" Laval
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
//
//

#if !NET40PLUS || (PORTABLE && !WINRT)

using System;
using System.Threading;
using System.Collections;
using System.Collections.Generic;
using System.Runtime.Serialization;

namespace Antlr4.Runtime.Sharpen
{
	internal class SplitOrderedList<TKey, T>
	{
		class Node
		{
			public bool Marked;
			public ulong Key;
			public TKey SubKey;
			public T Data;
			public Node Next;

			public Node Init (ulong key, TKey subKey, T data)
			{
				this.Key = key;
				this.SubKey = subKey;
				this.Data = data;

				this.Marked = false;
				this.Next = null;

				return this;
			}

			// Used to create dummy node
			public Node Init (ulong key)
			{
				this.Key = key;
				this.Data = default (T);

				this.Next = null;
				this.Marked = false;
				this.SubKey = default (TKey);

				return this;
			}

			// Used to create marked node
			public Node Init (Node wrapped)
			{
				this.Marked = true;
				this.Next = wrapped;

				this.Key = 0;
				this.Data = default (T);
				this.SubKey = default (TKey);

				return this;
			}
		}

		const int MaxLoad = 5;
		const uint BucketSize = 512;

		Node head;
		Node tail;

		Node[] buckets = new Node [BucketSize];
		int count;
		int size = 2;

		SimpleRwLock slim = new SimpleRwLock ();

		readonly IEqualityComparer<TKey> comparer;

		public SplitOrderedList (IEqualityComparer<TKey> comparer)
		{
			this.comparer = comparer;
			head = new Node ().Init (0);
			tail = new Node ().Init (ulong.MaxValue);
			head.Next = tail;
			SetBucket (0, head);
		}

		public int Count {
			get {
				return count;
			}
		}

		public T InsertOrUpdate (uint key, TKey subKey, Func<T> addGetter, Func<T, T> updateGetter)
		{
			Node current;
			bool result = InsertInternal (key, subKey, default (T), addGetter, out current);

			if (result)
				return current.Data;

			// FIXME: this should have a CAS-like behavior
			return current.Data = updateGetter (current.Data);
		}

		public T InsertOrUpdate (uint key, TKey subKey, T addValue, T updateValue)
		{
			Node current;
			if (InsertInternal (key, subKey, addValue, null, out current))
				return current.Data;

			// FIXME: this should have a CAS-like behavior
			return current.Data = updateValue;
		}
		
		public bool Insert (uint key, TKey subKey, T data)
		{
			Node current;
			return InsertInternal (key, subKey, data, null, out current);
		}

		public T InsertOrGet (uint key, TKey subKey, T data, Func<T> dataCreator)
		{
			Node current;
			InsertInternal (key, subKey, data, dataCreator, out current);
			return current.Data;
		}

		bool InsertInternal (uint key, TKey subKey, T data, Func<T> dataCreator, out Node current)
		{
			Node node = new Node ().Init (ComputeRegularKey (key), subKey, data);

			uint b = key % (uint)size;
			Node bucket;

			if ((bucket = GetBucket (b)) == null)
				bucket = InitializeBucket (b);

			if (!ListInsert (node, bucket, out current, dataCreator))
				return false;

			int csize = size;
			if (Interlocked.Increment (ref count) / csize > MaxLoad && (csize & 0x40000000) == 0)
				Interlocked.CompareExchange (ref size, 2 * csize, csize);

			current = node;

			return true;
		}
		
		public bool Find (uint key, TKey subKey, out T data)
		{
			Node node;
			uint b = key % (uint)size;
			data = default (T);
			Node bucket;

			if ((bucket = GetBucket (b)) == null)
				bucket = InitializeBucket (b);

			if (!ListFind (ComputeRegularKey (key), subKey, bucket, out node))
				return false;

			data = node.Data;

			return !node.Marked;
		}

		public bool CompareExchange (uint key, TKey subKey, T data, Func<T, bool> check)
		{
			Node node;
			uint b = key % (uint)size;
			Node bucket;

			if ((bucket = GetBucket (b)) == null)
				bucket = InitializeBucket (b);

			if (!ListFind (ComputeRegularKey (key), subKey, bucket, out node))
				return false;

			if (!check (node.Data))
				return false;

			node.Data = data;

			return true;
		}

		public bool Delete (uint key, TKey subKey, out T data)
		{
			uint b = key % (uint)size;
			Node bucket;

			if ((bucket = GetBucket (b)) == null)
				bucket = InitializeBucket (b);

			if (!ListDelete (bucket, ComputeRegularKey (key), subKey, out data))
				return false;

			Interlocked.Decrement (ref count);
			return true;
		}

		public IEnumerator<T> GetEnumerator ()
		{
			Node node = head.Next;

			while (node != tail) {
				while (node.Marked || (node.Key & 1) == 0) {
					node = node.Next;
					if (node == tail)
						yield break;
				}
				yield return node.Data;
				node = node.Next;
			}
		}

		Node InitializeBucket (uint b)
		{
			Node current;
			uint parent = GetParent (b);
			Node bucket;

			if ((bucket = GetBucket (parent)) == null)
				bucket = InitializeBucket (parent);

			Node dummy = new Node ().Init (ComputeDummyKey (b));
			if (!ListInsert (dummy, bucket, out current, null))
				return current;

			return SetBucket (b, dummy);
		}
		
		// Turn v's MSB off
		static uint GetParent (uint v)
		{
			uint t, tt;
			
			// Find MSB position in v
			var pos = (tt = v >> 16) > 0 ?
				(t = tt >> 8) > 0 ? 24 + logTable[t] : 16 + logTable[tt] :
				(t = v >> 8) > 0 ? 8 + logTable[t] : logTable[v];

			return (uint)(v & ~(1 << pos));
		}

		// Reverse integer bits and make sure LSB is set
		static ulong ComputeRegularKey (uint key)
		{
			return ComputeDummyKey (key) | 1;
		}
		
		// Reverse integer bits
		static ulong ComputeDummyKey (uint key)
		{
			return ((ulong)(((uint)reverseTable[key & 0xff] << 24) |
			                ((uint)reverseTable[(key >> 8) & 0xff] << 16) |
			                ((uint)reverseTable[(key >> 16) & 0xff] << 8) |
			                ((uint)reverseTable[(key >> 24) & 0xff]))) << 1;
		}

		// Bucket storage is abstracted in a simple two-layer tree to avoid too much memory resize
		Node GetBucket (uint index)
		{
			if (index >= buckets.Length)
				return null;
			return buckets[index];
		}

		Node SetBucket (uint index, Node node)
		{
			try {
				slim.EnterReadLock ();
				CheckSegment (index, true);

				Interlocked.CompareExchange (ref buckets[index], node, null);
				return buckets[index];
			} finally {
				slim.ExitReadLock ();
			}
		}

		// When we run out of space for bucket storage, we use a lock-based array resize
		void CheckSegment (uint segment, bool readLockTaken)
		{
			if (segment < buckets.Length)
				return;

			if (readLockTaken)
				slim.ExitReadLock ();
			try {
				slim.EnterWriteLock ();
				while (segment >= buckets.Length)
					Array.Resize (ref buckets, buckets.Length * 2);
			} finally {
				slim.ExitWriteLock ();
			}
			if (readLockTaken)
				slim.EnterReadLock ();
		}

		Node ListSearch (ulong key, TKey subKey, ref Node left, Node h)
		{
			Node leftNodeNext = null, rightNode = null;

			do {
				Node t = h;
				Node tNext = t.Next;
				do {
					if (!tNext.Marked) {
						left = t;
						leftNodeNext = tNext;
					}
					t = tNext.Marked ? tNext.Next : tNext;
					if (t == tail)
						break;
					
					tNext = t.Next;
				} while (tNext.Marked || t.Key < key || (tNext.Key == key && !comparer.Equals (subKey, t.SubKey)));
				
				rightNode = t;
				
				if (leftNodeNext == rightNode) {
					if (rightNode != tail && rightNode.Next.Marked)
						continue;
					else 
						return rightNode;
				}
				
				if (Interlocked.CompareExchange (ref left.Next, rightNode, leftNodeNext) == leftNodeNext) {
					if (rightNode != tail && rightNode.Next.Marked)
						continue;
					else
						return rightNode;
				}
			} while (true);
		}

		bool ListDelete (Node startPoint, ulong key, TKey subKey, out T data)
		{
			Node rightNode = null, rightNodeNext = null, leftNode = null;
			data = default (T);
			Node markedNode = null;
			
			do {
				rightNode = ListSearch (key, subKey, ref leftNode, startPoint);
				if (rightNode == tail || rightNode.Key != key || !comparer.Equals (subKey, rightNode.SubKey))
					return false;

				data = rightNode.Data;
				rightNodeNext = rightNode.Next;

				if (!rightNodeNext.Marked) {
					if (markedNode == null)
						markedNode = new Node ();
					markedNode.Init (rightNodeNext);

					if (Interlocked.CompareExchange (ref rightNode.Next, markedNode, rightNodeNext) == rightNodeNext)
						break;
				}
			} while (true);
			
			if (Interlocked.CompareExchange (ref leftNode.Next, rightNodeNext, rightNode) != rightNode)
				ListSearch (rightNode.Key, subKey, ref leftNode, startPoint);
			
			return true;
		}
		
		bool ListInsert (Node newNode, Node startPoint, out Node current, Func<T> dataCreator)
		{
			ulong key = newNode.Key;
			Node rightNode = null, leftNode = null;
			
			do {
				rightNode = current = ListSearch (key, newNode.SubKey, ref leftNode, startPoint);
				if (rightNode != tail && rightNode.Key == key && comparer.Equals (newNode.SubKey, rightNode.SubKey))
					return false;
				
				newNode.Next = rightNode;
				if (dataCreator != null)
					newNode.Data = dataCreator ();
				if (Interlocked.CompareExchange (ref leftNode.Next, newNode, rightNode) == rightNode)
					return true;
			} while (true);
		}
		
		bool ListFind (ulong key, TKey subKey, Node startPoint, out Node data)
		{
			Node rightNode = null, leftNode = null;
			data = null;
			
			rightNode = ListSearch (key, subKey, ref leftNode, startPoint);
			data = rightNode;
			
			return rightNode != tail && rightNode.Key == key && comparer.Equals (subKey, rightNode.SubKey);
		}

		static readonly byte[] reverseTable = {
			0, 128, 64, 192, 32, 160, 96, 224, 16, 144, 80, 208, 48, 176, 112, 240, 8, 136, 72, 200, 40, 168, 104, 232, 24, 152, 88, 216, 56, 184, 120, 248, 4, 132, 68, 196, 36, 164, 100, 228, 20, 148, 84, 212, 52, 180, 116, 244, 12, 140, 76, 204, 44, 172, 108, 236, 28, 156, 92, 220, 60, 188, 124, 252, 2, 130, 66, 194, 34, 162, 98, 226, 18, 146, 82, 210, 50, 178, 114, 242, 10, 138, 74, 202, 42, 170, 106, 234, 26, 154, 90, 218, 58, 186, 122, 250, 6, 134, 70, 198, 38, 166, 102, 230, 22, 150, 86, 214, 54, 182, 118, 246, 14, 142, 78, 206, 46, 174, 110, 238, 30, 158, 94, 222, 62, 190, 126, 254, 1, 129, 65, 193, 33, 161, 97, 225, 17, 145, 81, 209, 49, 177, 113, 241, 9, 137, 73, 201, 41, 169, 105, 233, 25, 153, 89, 217, 57, 185, 121, 249, 5, 133, 69, 197, 37, 165, 101, 229, 21, 149, 85, 213, 53, 181, 117, 245, 13, 141, 77, 205, 45, 173, 109, 237, 29, 157, 93, 221, 61, 189, 125, 253, 3, 131, 67, 195, 35, 163, 99, 227, 19, 147, 83, 211, 51, 179, 115, 243, 11, 139, 75, 203, 43, 171, 107, 235, 27, 155, 91, 219, 59, 187, 123, 251, 7, 135, 71, 199, 39, 167, 103, 231, 23, 151, 87, 215, 55, 183, 119, 247, 15, 143, 79, 207, 47, 175, 111, 239, 31, 159, 95, 223, 63, 191, 127, 255
		};

		static readonly byte[] logTable = {
			0xFF, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7
		};

		struct SimpleRwLock
		{
			const int RwWait = 1;
			const int RwWrite = 2;
			const int RwRead = 4;

			int rwlock;

			public void EnterReadLock ()
			{
				SpinWait sw = new SpinWait ();
				do {
					while ((rwlock & (RwWrite | RwWait)) > 0)
						sw.SpinOnce ();

#if COMPACT
					if ((InterlockedAdd (ref rwlock, RwRead) & (RwWait | RwWait)) == 0)
						return;

					InterlockedAdd (ref rwlock, -RwRead);
#else
					if ((Interlocked.Add (ref rwlock, RwRead) & (RwWait | RwWait)) == 0)
						return;

					Interlocked.Add (ref rwlock, -RwRead);
#endif
				} while (true);
			}

			public void ExitReadLock ()
			{
#if COMPACT
				InterlockedAdd (ref rwlock, -RwRead);
#else
				Interlocked.Add (ref rwlock, -RwRead);
#endif
			}

			public void EnterWriteLock ()
			{
				SpinWait sw = new SpinWait ();
				do {
					int state = rwlock;
					if (state < RwWrite) {
						if (Interlocked.CompareExchange (ref rwlock, RwWrite, state) == state)
							return;
						state = rwlock;
					}
					// We register our interest in taking the Write lock (if upgradeable it's already done)
					while ((state & RwWait) == 0 && Interlocked.CompareExchange (ref rwlock, state | RwWait, state) != state)
						state = rwlock;
					// Before falling to sleep
					while (rwlock > RwWait)
						sw.SpinOnce ();
				} while (true);
			}

			public void ExitWriteLock ()
			{
#if COMPACT
				InterlockedAdd (ref rwlock, -RwWrite);
#else
				Interlocked.Add (ref rwlock, -RwWrite);
#endif
			}

#if COMPACT
			/// <summary>
			/// Adds two 32-bit integers and replaces the first integer with the sum, as an atomic operation.
			/// </summary>
			/// <param name="location1">A variable containing the first value to be added. The sum of the two values is stored in <paramref name="location1"/>.</param>
			/// <param name="value">The value to be added to the integer at <paramref name="location1"/>.</param>
			/// <returns>The new value stored at <paramref name="location1"/>.</returns>
			private static int InterlockedAdd(ref int location1, int value)
			{
#if false // the code calling this private method will never make use of this optimization
				if (value == 1)
					return Interlocked.Increment(ref location1);
				else if (value == -1)
					return Interlocked.Decrement(ref location1);
#endif

				while (true)
				{
					int previous = location1;
					if (Interlocked.CompareExchange(ref location1, previous + value, previous) == previous)
						return previous + value;
				}
			}
#endif
		}
	}

	internal struct SpinWait
	{
		// The number of step until SpinOnce yield on multicore machine
		const           int  step = 10;
		const           int  maxTime = 200;
#if !COMPACT
		static readonly bool isSingleCpu = (Environment.ProcessorCount == 1);
#endif

		int ntime;

		public void SpinOnce ()
		{
			ntime += 1;
#if COMPACT
			Thread.Sleep(0);
#else
			ManualResetEvent mre = new ManualResetEvent (false);
			if (isSingleCpu) {
				// On a single-CPU system, spinning does no good
				mre.WaitOne (0);
			} else {
				if (ntime % step == 0)
					mre.WaitOne (0);
				else
					// Multi-CPU system might be hyper-threaded, let other thread run
					mre.WaitOne (Math.Min (ntime, maxTime) << 1);
			}
#endif
		}
	}
}

#endif

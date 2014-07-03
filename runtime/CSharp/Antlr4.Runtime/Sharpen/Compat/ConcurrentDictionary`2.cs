// ConcurrentDictionary.cs
//
// Copyright (c) 2009 Jérémie "Garuma" Laval
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
using System.Collections.ObjectModel;
using System.Runtime.Serialization;
using System.Diagnostics;

// declare the namespace so Sharpen-generated using declarations will not produce errors
namespace System.Collections.Concurrent
{
}

namespace Antlr4.Runtime.Sharpen
{
#if !COMPACT
	[DebuggerDisplay ("Count={Count}")]
	[DebuggerTypeProxy (typeof (CollectionDebuggerView<,>))]
#endif
	public class ConcurrentDictionary<TKey, TValue> : IDictionary<TKey, TValue>,
	  ICollection<KeyValuePair<TKey, TValue>>, IEnumerable<KeyValuePair<TKey, TValue>>,
	  IDictionary, ICollection, IEnumerable
	{
		IEqualityComparer<TKey> comparer;

		SplitOrderedList<TKey, KeyValuePair<TKey, TValue>> internalDictionary;

		public ConcurrentDictionary () : this (EqualityComparer<TKey>.Default)
		{
		}

		public ConcurrentDictionary (IEnumerable<KeyValuePair<TKey, TValue>> collection)
			: this (collection, EqualityComparer<TKey>.Default)
		{
		}

		public ConcurrentDictionary (IEqualityComparer<TKey> comparer)
		{
			this.comparer = comparer;
			this.internalDictionary = new SplitOrderedList<TKey, KeyValuePair<TKey, TValue>> (comparer);
		}

		public ConcurrentDictionary (IEnumerable<KeyValuePair<TKey, TValue>> collection, IEqualityComparer<TKey> comparer)
			: this (comparer)
		{
			foreach (KeyValuePair<TKey, TValue> pair in collection)
				Add (pair.Key, pair.Value);
		}

		// Parameters unused
		public ConcurrentDictionary (int concurrencyLevel, int capacity)
			: this (EqualityComparer<TKey>.Default)
		{

		}

		public ConcurrentDictionary (int concurrencyLevel,
		                             IEnumerable<KeyValuePair<TKey, TValue>> collection,
		                             IEqualityComparer<TKey> comparer)
			: this (collection, comparer)
		{

		}

		// Parameters unused
		public ConcurrentDictionary (int concurrencyLevel, int capacity, IEqualityComparer<TKey> comparer)
			: this (comparer)
		{

		}

		void CheckKey (TKey key)
		{
			if (key == null)
				throw new ArgumentNullException ("key");
		}

		void Add (TKey key, TValue value)
		{
			while (!TryAdd (key, value));
		}

		void IDictionary<TKey, TValue>.Add (TKey key, TValue value)
		{
			Add (key, value);
		}

		public bool TryAdd (TKey key, TValue value)
		{
			CheckKey (key);
			return internalDictionary.Insert (Hash (key), key, Make (key, value));
		}

		void ICollection<KeyValuePair<TKey,TValue>>.Add (KeyValuePair<TKey, TValue> pair)
		{
			Add (pair.Key, pair.Value);
		}

		public TValue AddOrUpdate (TKey key, Func<TKey, TValue> addValueFactory, Func<TKey, TValue, TValue> updateValueFactory)
		{
			CheckKey (key);
			if (addValueFactory == null)
				throw new ArgumentNullException ("addValueFactory");
			if (updateValueFactory == null)
				throw new ArgumentNullException ("updateValueFactory");
			return internalDictionary.InsertOrUpdate (Hash (key),
			                                          key,
			                                          () => Make (key, addValueFactory (key)),
			                                          (e) => Make (key, updateValueFactory (key, e.Value))).Value;
		}

		public TValue AddOrUpdate (TKey key, TValue addValue, Func<TKey, TValue, TValue> updateValueFactory)
		{
			return AddOrUpdate (key, (_) => addValue, updateValueFactory);
		}

		TValue AddOrUpdate (TKey key, TValue addValue, TValue updateValue)
		{
			CheckKey (key);
			return internalDictionary.InsertOrUpdate (Hash (key),
			                                          key,
			                                          Make (key, addValue),
			                                          Make (key, updateValue)).Value;
		}

		TValue GetValue (TKey key)
		{
			TValue temp;
			if (!TryGetValue (key, out temp))
				throw new KeyNotFoundException (key.ToString ());
			return temp;
		}

		public bool TryGetValue (TKey key, out TValue value)
		{
			CheckKey (key);
			KeyValuePair<TKey, TValue> pair;
			bool result = internalDictionary.Find (Hash (key), key, out pair);
			value = pair.Value;

			return result;
		}

		public bool TryUpdate (TKey key, TValue newValue, TValue comparisonValue)
		{
			CheckKey (key);
			return internalDictionary.CompareExchange (Hash (key), key, Make (key, newValue), (e) => e.Value.Equals (comparisonValue));
		}

		public TValue this[TKey key] {
			get {
				return GetValue (key);
			}
			set {
				AddOrUpdate (key, value, value);
			}
		}

		public TValue GetOrAdd (TKey key, Func<TKey, TValue> valueFactory)
		{
			CheckKey (key);
			return internalDictionary.InsertOrGet (Hash (key), key, Make (key, default(TValue)), () => Make (key, valueFactory (key))).Value;
		}

		public TValue GetOrAdd (TKey key, TValue value)
		{
			CheckKey (key);
			return internalDictionary.InsertOrGet (Hash (key), key, Make (key, value), null).Value;
		}

		public bool TryRemove (TKey key, out TValue value)
		{
			CheckKey (key);
			KeyValuePair<TKey, TValue> data;
			bool result = internalDictionary.Delete (Hash (key), key, out data);
			value = data.Value;
			return result;
		}

		bool Remove (TKey key)
		{
			TValue dummy;

			return TryRemove (key, out dummy);
		}

		bool IDictionary<TKey, TValue>.Remove (TKey key)
		{
			return Remove (key);
		}

		bool ICollection<KeyValuePair<TKey,TValue>>.Remove (KeyValuePair<TKey,TValue> pair)
		{
			return Remove (pair.Key);
		}

		public bool ContainsKey (TKey key)
		{
			CheckKey (key);
			KeyValuePair<TKey, TValue> dummy;
			return internalDictionary.Find (Hash (key), key, out dummy);
		}

		bool IDictionary.Contains (object key)
		{
			if (!(key is TKey))
				return false;

			return ContainsKey ((TKey)key);
		}

		void IDictionary.Remove (object key)
		{
			if (!(key is TKey))
				return;

			Remove ((TKey)key);
		}

		object IDictionary.this [object key]
		{
			get {
				if (!(key is TKey))
					throw new ArgumentException ("key isn't of correct type", "key");

				return this[(TKey)key];
			}
			set {
				if (!(key is TKey) || !(value is TValue))
					throw new ArgumentException ("key or value aren't of correct type");

				this[(TKey)key] = (TValue)value;
			}
		}

		void IDictionary.Add (object key, object value)
		{
			if (!(key is TKey) || !(value is TValue))
				throw new ArgumentException ("key or value aren't of correct type");

			Add ((TKey)key, (TValue)value);
		}

		bool ICollection<KeyValuePair<TKey,TValue>>.Contains (KeyValuePair<TKey, TValue> pair)
		{
			return ContainsKey (pair.Key);
		}

		public KeyValuePair<TKey,TValue>[] ToArray ()
		{
			// This is most certainly not optimum but there is
			// not a lot of possibilities

			return new List<KeyValuePair<TKey,TValue>> (this).ToArray ();
		}

		public void Clear()
		{
			// Pronk
			internalDictionary = new SplitOrderedList<TKey, KeyValuePair<TKey, TValue>> (comparer);
		}

		public int Count {
			get {
				return internalDictionary.Count;
			}
		}

		public bool IsEmpty {
			get {
				return Count == 0;
			}
		}

		bool ICollection<KeyValuePair<TKey, TValue>>.IsReadOnly {
			get {
				return false;
			}
		}

		bool IDictionary.IsReadOnly {
			get {
				return false;
			}
		}

		public ICollection<TKey> Keys {
			get {
				return GetPart<TKey> ((kvp) => kvp.Key);
			}
		}

		public ICollection<TValue> Values {
			get {
				return GetPart<TValue> ((kvp) => kvp.Value);
			}
		}

		ICollection IDictionary.Keys {
			get {
				return (ICollection)Keys;
			}
		}

		ICollection IDictionary.Values {
			get {
				return (ICollection)Values;
			}
		}

		ICollection<T> GetPart<T> (Func<KeyValuePair<TKey, TValue>, T> extractor)
		{
			List<T> temp = new List<T> ();

			foreach (KeyValuePair<TKey, TValue> kvp in this)
				temp.Add (extractor (kvp));

			return new ReadOnlyCollection<T>(temp);
		}

		void ICollection.CopyTo (Array array, int startIndex)
		{
			KeyValuePair<TKey, TValue>[] arr = array as KeyValuePair<TKey, TValue>[];
			if (arr == null)
				return;

			CopyTo (arr, startIndex, Count);
		}

		void CopyTo (KeyValuePair<TKey, TValue>[] array, int startIndex)
		{
			CopyTo (array, startIndex, Count);
		}

		void ICollection<KeyValuePair<TKey, TValue>>.CopyTo (KeyValuePair<TKey, TValue>[] array, int startIndex)
		{
			CopyTo (array, startIndex);
		}

		void CopyTo (KeyValuePair<TKey, TValue>[] array, int startIndex, int num)
		{
			foreach (var kvp in this) {
				array [startIndex++] = kvp;

				if (--num <= 0)
					return;
			}
		}

		public IEnumerator<KeyValuePair<TKey, TValue>> GetEnumerator ()
		{
			return GetEnumeratorInternal ();
		}

		IEnumerator IEnumerable.GetEnumerator ()
		{
			return (IEnumerator)GetEnumeratorInternal ();
		}

		IEnumerator<KeyValuePair<TKey, TValue>> GetEnumeratorInternal ()
		{
			return internalDictionary.GetEnumerator ();
		}

		IDictionaryEnumerator IDictionary.GetEnumerator ()
		{
			return new ConcurrentDictionaryEnumerator (GetEnumeratorInternal ());
		}

		class ConcurrentDictionaryEnumerator : IDictionaryEnumerator
		{
			IEnumerator<KeyValuePair<TKey, TValue>> internalEnum;

			public ConcurrentDictionaryEnumerator (IEnumerator<KeyValuePair<TKey, TValue>> internalEnum)
			{
				this.internalEnum = internalEnum;
			}

			public bool MoveNext ()
			{
				return internalEnum.MoveNext ();
			}

			public void Reset ()
			{
				internalEnum.Reset ();
			}

			public object Current {
				get {
					return Entry;
				}
			}

			public DictionaryEntry Entry {
				get {
					KeyValuePair<TKey, TValue> current = internalEnum.Current;
					return new DictionaryEntry (current.Key, current.Value);
				}
			}

			public object Key {
				get {
					return internalEnum.Current.Key;
				}
			}

			public object Value {
				get {
					return internalEnum.Current.Value;
				}
			}
		}

		object ICollection.SyncRoot {
			get {
				return this;
			}
		}

		bool IDictionary.IsFixedSize {
			get {
				return false;
			}
		}

		bool ICollection.IsSynchronized {
			get { return true; }
		}

		static KeyValuePair<U, V> Make<U, V> (U key, V value)
		{
			return new KeyValuePair<U, V> (key, value);
		}

		uint Hash (TKey key)
		{
			return (uint)comparer.GetHashCode (key);
		}
	}
}
#endif

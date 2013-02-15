/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
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
using System;
using System.Collections.Generic;
using Antlr4.Runtime.Misc;
using Sharpen;

namespace Antlr4.Runtime.Misc
{
	/// <author>Sam Harwell</author>
	public class IntegerList
	{
		private static int[] EmptyData = new int[0];

		private const int InitialSize = 4;

		private const int MaxArraySize = int.MaxValue - 8;

		[NotNull]
		private int[] _data;

		private int _size;

		public IntegerList()
		{
			_data = EmptyData;
		}

		public IntegerList(int capacity)
		{
			if (capacity < 0)
			{
				throw new ArgumentException();
			}
			if (capacity == 0)
			{
				_data = EmptyData;
			}
			else
			{
				_data = new int[capacity];
			}
		}

		public IntegerList(Antlr4.Runtime.Misc.IntegerList list)
		{
			_data = list._data.Clone();
			_size = list._size;
		}

		public IntegerList(ICollection<int> list) : this(list.Count)
		{
			foreach (int value in list)
			{
				Add(value);
			}
		}

		public void Add(int value)
		{
			if (_data.Length == _size)
			{
				EnsureCapacity(_size + 1);
			}
			_data[_size] = value;
			_size++;
		}

		public void AddAll(int[] array)
		{
			EnsureCapacity(_size + array.Length);
			System.Array.Copy(array, 0, _data, _size, array.Length);
			_size += array.Length;
		}

		public void AddAll(Antlr4.Runtime.Misc.IntegerList list)
		{
			EnsureCapacity(_size + list._size);
			System.Array.Copy(list._data, 0, _data, _size, list._size);
			_size += list._size;
		}

		public void AddAll(ICollection<int> list)
		{
			EnsureCapacity(_size + list.Count);
			int current = 0;
			foreach (int x in list)
			{
				_data[_size + current] = x;
			}
			_size += list.Count;
		}

		public int Get(int index)
		{
			if (index < 0 || index >= _size)
			{
				throw new IndexOutOfRangeException();
			}
			return _data[index];
		}

		public bool Contains(int value)
		{
			for (int i = 0; i < _size; i++)
			{
				if (_data[i] == value)
				{
					return true;
				}
			}
			return false;
		}

		public int Set(int index, int value)
		{
			if (index < 0 || index >= _size)
			{
				throw new IndexOutOfRangeException();
			}
			int previous = _data[index];
			_data[index] = value;
			return previous;
		}

		public int RemoveAt(int index)
		{
			int value = Get(index);
			System.Array.Copy(_data, index + 1, _data, index, _size - index - 1);
			_data[_size - 1] = 0;
			_size--;
			return value;
		}

		public void RemoveRange(int fromIndex, int toIndex)
		{
			if (fromIndex < 0 || toIndex < 0 || fromIndex > _size || toIndex > _size)
			{
				throw new IndexOutOfRangeException();
			}
			if (fromIndex > toIndex)
			{
				throw new ArgumentException();
			}
			System.Array.Copy(_data, toIndex, _data, fromIndex, _size - toIndex);
			Arrays.Fill(_data, _size - (toIndex - fromIndex), _size, 0);
			_size -= (toIndex - fromIndex);
		}

		public bool IsEmpty()
		{
			return _size == 0;
		}

		public int Size()
		{
			return _size;
		}

		public void TrimToSize()
		{
			if (_data.Length == _size)
			{
				return;
			}
			_data = Arrays.CopyOf(_data, _size);
		}

		public void Clear()
		{
			Arrays.Fill(_data, 0, _size, 0);
			_size = 0;
		}

		public int[] ToArray()
		{
			if (_size == 0)
			{
				return EmptyData;
			}
			return Arrays.CopyOf(_data, _size);
		}

		public void Sort()
		{
			Arrays.Sort(_data, 0, _size);
		}

		/// <summary>Compares the specified object with this list for equality.</summary>
		/// <remarks>
		/// Compares the specified object with this list for equality.  Returns
		/// <code>true</code>
		/// if and only if the specified object is also an
		/// <see cref="IntegerList">IntegerList</see>
		/// ,
		/// both lists have the same size, and all corresponding pairs of elements in
		/// the two lists are equal.  In other words, two lists are defined to be
		/// equal if they contain the same elements in the same order.
		/// <p>
		/// This implementation first checks if the specified object is this
		/// list. If so, it returns
		/// <code>true</code>
		/// ; if not, it checks if the
		/// specified object is an
		/// <see cref="IntegerList">IntegerList</see>
		/// . If not, it returns
		/// <code>false</code>
		/// ;
		/// if so, it checks the size of both lists. If the lists are not the same size,
		/// it returns
		/// <code>false</code>
		/// ; otherwise it iterates over both lists, comparing
		/// corresponding pairs of elements.  If any comparison returns
		/// <code>false</code>
		/// ,
		/// this method returns
		/// <code>false</code>
		/// .
		/// </remarks>
		/// <param name="o">the object to be compared for equality with this list</param>
		/// <returns>
		/// 
		/// <code>true</code>
		/// if the specified object is equal to this list
		/// </returns>
		public override bool Equals(object o)
		{
			if (o == this)
			{
				return true;
			}
			if (!(o is Antlr4.Runtime.Misc.IntegerList))
			{
				return false;
			}
			Antlr4.Runtime.Misc.IntegerList other = (Antlr4.Runtime.Misc.IntegerList)o;
			if (_size != other._size)
			{
				return false;
			}
			for (int i = 0; i < _size; i++)
			{
				if (_data[i] != other._data[i])
				{
					return false;
				}
			}
			return true;
		}

		/// <summary>Returns the hash code value for this list.</summary>
		/// <remarks>
		/// Returns the hash code value for this list.
		/// <p/>
		/// This implementation uses exactly the code that is used to define the
		/// list hash function in the documentation for the
		/// <see cref="System.Collections.IList{E}.GetHashCode()">System.Collections.IList&lt;E&gt;.GetHashCode()
		/// 	</see>
		/// method.
		/// </remarks>
		/// <returns>the hash code value for this list</returns>
		public override int GetHashCode()
		{
			int hashCode = 1;
			for (int i = 0; i < _size; i++)
			{
				hashCode = 31 * hashCode + _data[i];
			}
			return hashCode;
		}

		/// <summary>Returns a string representation of this list.</summary>
		/// <remarks>Returns a string representation of this list.</remarks>
		public override string ToString()
		{
			return Arrays.ToString(ToArray());
		}

		public int BinarySearch(int key)
		{
			return System.Array.BinarySearch(_data, 0, _size, key);
		}

		public int BinarySearch(int fromIndex, int toIndex, int key)
		{
			if (fromIndex < 0 || toIndex < 0 || fromIndex > _size || toIndex > _size)
			{
				throw new IndexOutOfRangeException();
			}
			return System.Array.BinarySearch(_data, fromIndex, toIndex, key);
		}

		private void EnsureCapacity(int capacity)
		{
			if (capacity < 0 || capacity > MaxArraySize)
			{
				throw new OutOfMemoryException();
			}
			int newLength;
			if (_data.Length == 0)
			{
				newLength = InitialSize;
			}
			else
			{
				newLength = _data.Length;
			}
			while (newLength < capacity)
			{
				newLength = newLength * 2;
				if (newLength < 0 || newLength > MaxArraySize)
				{
					newLength = MaxArraySize;
				}
			}
			_data = Arrays.CopyOf(_data, newLength);
		}
	}
}

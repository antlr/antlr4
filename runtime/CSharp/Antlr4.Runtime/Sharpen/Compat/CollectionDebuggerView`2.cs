//
// CollectionDebuggerView.cs
//
// Authors:
//	Marek Safar  <marek.safar@gmail.com>
//
// Copyright (C) 2009 Novell, Inc (http://www.novell.com)
//
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

#if !NET40PLUS || (PORTABLE && !WINRT)

using System;
using System.Collections.Generic;
using System.Diagnostics;

namespace Antlr4.Runtime.Sharpen
{
	//
	// Custom debugger type proxy to display collections as arrays
	//
	internal sealed class CollectionDebuggerView<T>
	{
		readonly ICollection<T> c;

		public CollectionDebuggerView (ICollection<T> col)
		{
			this.c = col;
		}
		
#if !COMPACT
		[DebuggerBrowsable(DebuggerBrowsableState.RootHidden)]
#endif
		public T[] Items {
			get {
				var o = new T [c.Count];
				c.CopyTo (o, 0);
				return o;
			}
		}
	}
	
	internal sealed class CollectionDebuggerView<T, U>
	{
		readonly ICollection<KeyValuePair<T, U>> c;

		public CollectionDebuggerView (ICollection<KeyValuePair<T, U>> col)
		{
			this.c = col;
		}

#if !COMPACT
		[DebuggerBrowsable (DebuggerBrowsableState.RootHidden)]
#endif
		public KeyValuePair<T, U>[] Items {
			get {
				var o = new KeyValuePair<T, U> [c.Count];
				c.CopyTo (o, 0);
				return o;
			}
		}
	}	
}

#endif

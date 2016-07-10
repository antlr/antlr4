//
// Tuple.cs
//
// Authors:
//  Zoltan Varga (vargaz@gmail.com)
//
// Copyright (C) 2009 Novell
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

#if !NET40PLUS

using System;

namespace Antlr4.Runtime.Sharpen
{
	internal static class Tuple
	{
		public static Tuple<T1, T2, T3, T4, T5, T6, T7, Tuple<T8>> Create<T1, T2, T3, T4, T5, T6, T7, T8>
			(
			 T1 item1,
			 T2 item2,
			 T3 item3,
			 T4 item4,
			 T5 item5,
			 T6 item6,
			 T7 item7,
			 T8 item8) {
			return new Tuple<T1, T2, T3, T4, T5, T6, T7, Tuple<T8>> (item1, item2, item3, item4, item5, item6, item7, new Tuple<T8> (item8));
		}

		public static Tuple<T1, T2, T3, T4, T5, T6, T7> Create<T1, T2, T3, T4, T5, T6, T7>
			(
			 T1 item1,
			 T2 item2,
			 T3 item3,
			 T4 item4,
			 T5 item5,
			 T6 item6,
			 T7 item7) {
			return new Tuple<T1, T2, T3, T4, T5, T6, T7> (item1, item2, item3, item4, item5, item6, item7);
		}

		public static Tuple<T1, T2, T3, T4, T5, T6> Create<T1, T2, T3, T4, T5, T6>
			(
			 T1 item1,
			 T2 item2,
			 T3 item3,
			 T4 item4,
			 T5 item5,
			 T6 item6) {
			return new Tuple<T1, T2, T3, T4, T5, T6> (item1, item2, item3, item4, item5, item6);
		}

		public static Tuple<T1, T2, T3, T4, T5> Create<T1, T2, T3, T4, T5>
			(
			 T1 item1,
			 T2 item2,
			 T3 item3,
			 T4 item4,
			 T5 item5) {
			return new Tuple<T1, T2, T3, T4, T5> (item1, item2, item3, item4, item5);
		}

		public static Tuple<T1, T2, T3, T4> Create<T1, T2, T3, T4>
			(
			 T1 item1,
			 T2 item2,
			 T3 item3,
			 T4 item4) {
			return new Tuple<T1, T2, T3, T4> (item1, item2, item3, item4);
		}

		public static Tuple<T1, T2, T3> Create<T1, T2, T3>
			(
			 T1 item1,
			 T2 item2,
			 T3 item3) {
			return new Tuple<T1, T2, T3> (item1, item2, item3);
		}

		public static Tuple<T1, T2> Create<T1, T2>
			(
			 T1 item1,
			 T2 item2) {
			return new Tuple<T1, T2> (item1, item2);
		}

		public static Tuple<T1> Create<T1>
			(
			 T1 item1) {
			return new Tuple<T1> (item1);
		}
	}		
}
	
#endif

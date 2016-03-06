//
// System.Func.cs
//
// Authors:
//      Alejandro Serrano "Serras" (trupill@yahoo.es)
//	Marek Safar (marek.safar@gmail.com)
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
// MERCHANTABILITY, FITNESS FOR TArg PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
//

#if !NET35PLUS

using System.Runtime.CompilerServices;

namespace Antlr4.Runtime.Sharpen {

	public delegate TResult Func<TResult> ();
	
	public delegate TResult Func<T, TResult> (T arg);
	
	public delegate TResult Func<in T1, in T2, out TResult> (T1 arg1, T2 arg2);
}

#endif

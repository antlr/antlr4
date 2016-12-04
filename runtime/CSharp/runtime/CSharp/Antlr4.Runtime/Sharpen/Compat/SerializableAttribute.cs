/* Copyright (c) 2012 The ANTLR Project Contributors. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#if PORTABLE

namespace System
{
    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Struct | AttributeTargets.Enum | AttributeTargets.Delegate, Inherited = false)]
    internal sealed class SerializableAttribute : Attribute
    {
    }
}

#endif

namespace Antlr4.Runtime.Misc
{
    using System;

    [AttributeUsage(AttributeTargets.Field | AttributeTargets.Parameter | AttributeTargets.ReturnValue | AttributeTargets.Property, Inherited = true, AllowMultiple = false)]
    public sealed class NullableAttribute : Attribute
    {
    }
}

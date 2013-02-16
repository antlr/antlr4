namespace Antlr4.Runtime.Misc
{
    using System;

    [AttributeUsage(AttributeTargets.Field | AttributeTargets.Parameter | AttributeTargets.ReturnValue, Inherited = true, AllowMultiple = false)]
    public sealed class NotNullAttribute : Attribute
    {
    }
}

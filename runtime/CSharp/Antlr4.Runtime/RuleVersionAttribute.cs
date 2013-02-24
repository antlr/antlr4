namespace Antlr4.Runtime
{
    using System;

    [AttributeUsage(AttributeTargets.Method, Inherited = false, AllowMultiple = false)]
    public sealed class RuleVersionAttribute : Attribute
    {
        private readonly int _version;

        public RuleVersionAttribute(int version)
        {
            _version = version;
        }

        public int Version
        {
            get
            {
                return _version;
            }
        }
    }
}

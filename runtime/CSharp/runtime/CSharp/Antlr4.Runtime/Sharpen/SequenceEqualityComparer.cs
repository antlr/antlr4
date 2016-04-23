namespace Antlr4.Runtime.Sharpen
{
    using System.Collections.Generic;
    using System.Linq;

    internal class SequenceEqualityComparer<T> : EqualityComparer<IEnumerable<T>>
    {
        private static readonly SequenceEqualityComparer<T> _default = new SequenceEqualityComparer<T>();

        private readonly IEqualityComparer<T> _elementEqualityComparer = EqualityComparer<T>.Default;

        public SequenceEqualityComparer()
            : this(null)
        {
        }

        public SequenceEqualityComparer(IEqualityComparer<T> elementComparer)
        {
            _elementEqualityComparer = elementComparer ?? EqualityComparer<T>.Default;
        }

        public new static SequenceEqualityComparer<T> Default
        {
            get
            {
                return _default;
            }
        }

        public override bool Equals(IEnumerable<T> x, IEnumerable<T> y)
        {
            if (x == y)
                return true;
            if (x == null || y == null)
                return false;

            return x.SequenceEqual(y, _elementEqualityComparer);
        }

        public override int GetHashCode(IEnumerable<T> obj)
        {
            if (obj == null)
                return 0;

            int hashCode = 1;
            foreach (T element in obj)
                hashCode = 31 * hashCode + _elementEqualityComparer.GetHashCode(element);

            return hashCode;
        }
    }
}

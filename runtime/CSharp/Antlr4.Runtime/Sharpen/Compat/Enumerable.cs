#if !NET35PLUS

namespace System.Linq
{
    using System.Collections;
    using System.Collections.Generic;

    internal static class Enumerable
    {
        public static bool SequenceEqual<T>(this IEnumerable<T> a, IEnumerable<T> b)
        {
            return SequenceEqual(a, b, null);
        }

        public static bool SequenceEqual<T>(this IEnumerable<T> a, IEnumerable<T> b, IEqualityComparer<T> comparer)
        {
            comparer = comparer ?? EqualityComparer<T>.Default;
            IEnumerator<T> left = a.GetEnumerator();
            IEnumerator<T> right = b.GetEnumerator();
            while (left.MoveNext())
            {
                if (!right.MoveNext())
                    return false;

                if (!comparer.Equals(left.Current, right.Current))
                    return false;
            }

            if (right.MoveNext())
                return false;

            return true;
        }

        public static T Max<T>(this IEnumerable<T> sequence)
        {
            using (IEnumerator<T> enumerator = sequence.GetEnumerator())
            {
                if (!enumerator.MoveNext())
                    throw new ArgumentException();

                IComparer<T> comparer = Comparer<T>.Default;
                T max = enumerator.Current;
                while (enumerator.MoveNext())
                {
                    T test = enumerator.Current;
                    if (comparer.Compare(max, test) < 0)
                        max = test;
                }

                return max;
            }
        }

        public static T Min<T>(this IEnumerable<T> sequence)
            where T : IComparable<T>
        {
            using (IEnumerator<T> enumerator = sequence.GetEnumerator())
            {
                if (!enumerator.MoveNext())
                    throw new ArgumentException();

                IComparer<T> comparer = Comparer<T>.Default;
                T min = enumerator.Current;
                while (enumerator.MoveNext())
                {
                    T test = enumerator.Current;
                    if (comparer.Compare(min, test) > 0)
                        min = test;
                }

                return min;
            }
        }

        public static bool Any(this IEnumerable sequence)
        {
            return sequence.GetEnumerator().MoveNext();
        }

        public static IEnumerable<T> Cast<T>(this IEnumerable sequence)
        {
            IEnumerator enumerator = sequence.GetEnumerator();
            try
            {
                while (enumerator.MoveNext())
                    yield return (T)enumerator.Current;
            }
            finally
            {
                IDisposable disposable = enumerator as IDisposable;
                if (disposable != null)
                    disposable.Dispose();
            }
        }

        public static IEnumerable<T> OfType<T>(this IEnumerable sequence)
        {
            IEnumerator enumerator = sequence.GetEnumerator();
            try
            {
                while (enumerator.MoveNext())
                {
                    object current = enumerator.Current;
                    if (current is T)
                        yield return (T)current;
                }
            }
            finally
            {
                IDisposable disposable = enumerator as IDisposable;
                if (disposable != null)
                    disposable.Dispose();
            }
        }

        public static T[] ToArray<T>(this IEnumerable<T> sequence)
        {
            return new List<T>(sequence).ToArray();
        }

        public static List<T> ToList<T>(this IEnumerable<T> sequence)
        {
            return new List<T>(sequence);
        }

        public static IEnumerable<T> Take<T>(this IEnumerable<T> sequence, int count)
        {
            if (count < 0)
                throw new ArgumentOutOfRangeException("count");

            if (count == 0)
                yield break;

            int i = 0;
            foreach (T element in sequence)
            {
                yield return element;
                i++;
                if (i >= count)
                    yield break;
            }
        }

        public static IEnumerable<T> Skip<T>(this IEnumerable<T> sequence, int count)
        {
            if (count < 0)
                throw new ArgumentOutOfRangeException("count");

            int i = 0;
            foreach (T element in sequence)
            {
                if (i >= count)
                    yield return element;

                i++;
            }
        }
    }
}

#endif

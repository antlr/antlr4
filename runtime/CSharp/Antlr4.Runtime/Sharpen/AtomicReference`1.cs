namespace Sharpen
{
    using Interlocked = System.Threading.Interlocked;
    using Volatile = System.Threading.Volatile;

    public class AtomicReference<T>
        where T : class
    {
        private T _value;

        public AtomicReference()
        {
        }

        public AtomicReference(T value)
        {
            _value = value;
        }

        public T Get()
        {
            return Volatile.Read(ref _value);
        }

        public void Set(T value)
        {
            Volatile.Write(ref _value, value);
        }

        public void LazySet(T value)
        {
            _value = value;
        }

        public bool CompareAndSet(T expect, T update)
        {
            return Interlocked.CompareExchange(ref _value, update, expect) == expect;
        }

        public T GetAndSet(T value)
        {
            return Interlocked.Exchange(ref _value, value);
        }
    }
}

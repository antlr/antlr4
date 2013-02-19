namespace Sharpen
{
    using System;

    public class BitSet
    {
        private static readonly uint[] EmptyBits = new uint[0];

        private uint[] data = EmptyBits;

        public BitSet()
        {
        }

        public BitSet(int nbits)
        {
            if (nbits < 0)
                throw new ArgumentOutOfRangeException("nbits");

            if (nbits > 0)
            {
                int length = (nbits + (8 * sizeof(uint)) - 1) / (8 * sizeof(uint));
                data = new uint[length];
            }
        }

        public BitSet Clone()
        {
            BitSet result = new BitSet();
            result.data = (uint[])data.Clone();
            return result;
        }

        public bool Get(int index)
        {
            if (index < 0)
                throw new ArgumentOutOfRangeException("index");

            int element = index / (8 * sizeof(uint));
            if (element >= data.Length)
                return false;

            return (data[element] & (1U << (index % (8 * sizeof(uint))))) != 0;
        }

        public void Set(int index)
        {
            if (index < 0)
                throw new ArgumentOutOfRangeException("index");

            int element = index / (8 * sizeof(uint));
            if (element >= data.Length)
                Array.Resize(ref data, Math.Max(data.Length * 2, element + 1));

            data[element] |= 1U << (index % (8 * sizeof(uint)));
        }
    }
}

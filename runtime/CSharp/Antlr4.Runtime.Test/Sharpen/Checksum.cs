namespace Antlr4.Runtime.Sharpen
{
    internal interface Checksum
    {
        long Value
        {
            get;
        }

        void Reset();

        void Update(byte[] buffer, int offset, int length);

        void Update(int byteValue);
    }
}

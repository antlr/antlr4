namespace Antlr4.Runtime.Test
{
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Sharpen;

    [TestClass]
    public class TestBitSet
    {
        [TestMethod]
        public void TestCardinality1()
        {
            BitSet set = new BitSet();
            Assert.AreEqual(0, set.Cardinality());
            for (int i = 0; i < 128; i++)
            {
                set.Set(i);
                Assert.AreEqual(i + 1, set.Cardinality());
                Assert.AreEqual(0, set.NextSetBit(0));
                if (i > 0)
                    Assert.AreEqual(i * 1 - 1, set.NextSetBit(i * 1 - 1));
            }
        }

        [TestMethod]
        public void TestCardinality2()
        {
            BitSet set = new BitSet();
            Assert.AreEqual(0, set.Cardinality());
            for (int i = 0; i < 128; i++)
            {
                set.Set(i * 2);
                Assert.AreEqual(i + 1, set.Cardinality());
                Assert.AreEqual(0, set.NextSetBit(0));
                if (i > 0)
                    Assert.AreEqual(i * 2, set.NextSetBit(i * 2 - 1));
            }
        }

        [TestMethod]
        public void TestCardinality7()
        {
            BitSet set = new BitSet();
            Assert.AreEqual(0, set.Cardinality());
            for (int i = 0; i < 128; i++)
            {
                set.Set(i * 7);
                Assert.AreEqual(i + 1, set.Cardinality());
                Assert.AreEqual(0, set.NextSetBit(0));
                if (i > 0)
                    Assert.AreEqual(i * 7, set.NextSetBit(i * 7 - 1));
            }
        }
    }
}

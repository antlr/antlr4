/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Reflection;
using Antlr4.Runtime.Atn;
using Xunit;

namespace PerfOptimizations
{
    /// <summary>
    /// Tests for ATNConfigSet.PoolReset, MergeCache.Clear, and
    /// OrderedATNConfigSet.CreateLookup. PoolReset is internal, so
    /// we invoke it via reflection to validate correct behavior.
    /// </summary>
    public class PoolingTests
    {
        private static readonly MethodInfo PoolResetMethod =
            typeof(ATNConfigSet).GetMethod("PoolReset",
                BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.Public);

        private static ATNConfig MakeConfig(int stateNumber, int alt)
        {
            var state = new BasicState();
            state.stateNumber = stateNumber;
            return new ATNConfig(state, alt, EmptyPredictionContext.Instance);
        }

        private static void InvokePoolReset(ATNConfigSet set)
        {
            Assert.NotNull(PoolResetMethod);
            PoolResetMethod.Invoke(set, null);
        }

        // ── ATNConfigSet.PoolReset ───────────────────────────────────────

        [Fact]
        public void ATNConfigSet_PoolReset_ClearsConfigs()
        {
            var set = new ATNConfigSet();
            set.Add(MakeConfig(0, 1));
            Assert.NotEmpty(set.configs);

            InvokePoolReset(set);
            Assert.Empty(set.configs);
        }

        [Fact]
        public void ATNConfigSet_PoolReset_RestoresWritability()
        {
            var set = new ATNConfigSet();
            set.IsReadOnly = true;

            InvokePoolReset(set);

            // Should be writable again — adding should not throw
            set.Add(MakeConfig(0, 1));
            Assert.Single(set.configs);
        }

        [Fact]
        public void ATNConfigSet_PoolReset_ResetsAuxFields()
        {
            var set = new ATNConfigSet();
            set.hasSemanticContext = true;
            set.dipsIntoOuterContext = true;
            set.uniqueAlt = 5;

            InvokePoolReset(set);

            Assert.False(set.hasSemanticContext);
            Assert.False(set.dipsIntoOuterContext);
            Assert.Equal(0, set.uniqueAlt);
        }

        [Fact]
        public void ATNConfigSet_PoolReset_MultipleResets_Stable()
        {
            var set = new ATNConfigSet();
            for (int i = 0; i < 5; i++)
            {
                set.Add(MakeConfig(i, 1));
                set.IsReadOnly = true;
                InvokePoolReset(set);
                Assert.Empty(set.configs);
            }
        }

        // ── OrderedATNConfigSet PoolReset ────────────────────────────────

        [Fact]
        public void OrderedATNConfigSet_PoolReset_UsesCorrectLookup()
        {
            var ordered = new OrderedATNConfigSet();
            ordered.Add(MakeConfig(0, 1));
            ordered.IsReadOnly = true;

            InvokePoolReset(ordered);

            // Should still work as an ordered set after reset
            ordered.Add(MakeConfig(0, 2));
            Assert.Single(ordered.configs);
        }

        // ── ATNConfigSet.Clear (public) ──────────────────────────────────

        [Fact]
        public void ATNConfigSet_Clear_EmptiesConfigs()
        {
            var set = new ATNConfigSet();
            set.Add(MakeConfig(0, 1));
            set.Add(MakeConfig(1, 2));
            Assert.Equal(2, set.configs.Count);

            set.Clear();
            Assert.Empty(set.configs);
        }

        [Fact]
        public void ATNConfigSet_Clear_AllowsReAdd()
        {
            var set = new ATNConfigSet();
            set.Add(MakeConfig(0, 1));
            set.Clear();
            set.Add(MakeConfig(0, 1));
            Assert.Single(set.configs);
        }

        // ── MergeCache.Clear ─────────────────────────────────────────────

        [Fact]
        public void MergeCache_Clear_EmptiesCache()
        {
            var cache = new MergeCache();
            var ctx = EmptyPredictionContext.Instance;
            cache.Put(ctx, ctx, ctx);

            Assert.NotNull(cache.Get(ctx, ctx));

            cache.Clear();

            Assert.Null(cache.Get(ctx, ctx));
        }

        [Fact]
        public void MergeCache_Clear_ThenReuse()
        {
            var cache = new MergeCache();
            var ctx = EmptyPredictionContext.Instance;
            cache.Put(ctx, ctx, ctx);
            cache.Clear();

            // Should be able to put/get again
            cache.Put(ctx, ctx, ctx);
            Assert.Same(ctx, cache.Get(ctx, ctx));
        }
    }
}

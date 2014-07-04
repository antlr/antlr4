/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
using System;
using System.Collections.Generic;
using System.Reflection;
using System.Security;
using System.Text;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;
using Antlr4.Runtime.Sharpen.Annotation;
using Antlr4.Runtime.Sharpen.Logging;
using Antlr4.Runtime.Sharpen.Reflect;

namespace Antlr4.Runtime.Misc
{
    /// <author>Sam Harwell</author>
    public class RuleDependencyChecker
    {
        private static readonly Logger Logger = Logger.GetLogger(typeof(Antlr4.Runtime.Misc.RuleDependencyChecker).FullName);

        private static readonly HashSet<Type> checkedTypes = new HashSet<Type>();

        public static void CheckDependencies<_T0>(Type<_T0> dependentClass)
        {
            if (IsChecked(dependentClass))
            {
                return;
            }
            IList<Type> typesToCheck = GetTypesToCheck(dependentClass);
            foreach (Type clazz in typesToCheck)
            {
                if (IsChecked(clazz))
                {
                    continue;
                }
                IList<Tuple<RuleDependency, IAnnotatedElement>> dependencies = GetDependencies(clazz);
                if (dependencies.IsEmpty())
                {
                    continue;
                }
                IDictionary<Type, IList<Tuple<RuleDependency, IAnnotatedElement>>> recognizerDependencies = new Dictionary<Type, IList<Tuple<RuleDependency, IAnnotatedElement>>>();
                foreach (Tuple<RuleDependency, IAnnotatedElement> dependency in dependencies)
                {
                    Type recognizerType = dependency.Item1.Recognizer();
                    IList<Tuple<RuleDependency, IAnnotatedElement>> list = recognizerDependencies.Get(recognizerType);
                    if (list == null)
                    {
                        list = new List<Tuple<RuleDependency, IAnnotatedElement>>();
                        recognizerDependencies.Put(recognizerType, list);
                    }
                    list.AddItem(dependency);
                }
                foreach (KeyValuePair<Type, IList<Tuple<RuleDependency, IAnnotatedElement>>> entry in recognizerDependencies.EntrySet())
                {
                    //processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format("ANTLR 4: Validating %d dependencies on rules in %s.", entry.getValue().size(), entry.getKey().toString()));
                    CheckDependencies(entry.Value, entry.Key);
                }
                CheckDependencies(dependencies, dependencies[0].Item1.Recognizer());
            }
        }

        private static IList<Type> GetTypesToCheck<_T0>(Type<_T0> clazz)
        {
            HashSet<Type> result = new HashSet<Type>();
            GetTypesToCheck(clazz, result);
            return new List<Type>(result);
        }

        private static void GetTypesToCheck<_T0>(Type<_T0> clazz, HashSet<Type> result)
        {
            if (!result.AddItem(clazz))
            {
                return;
            }
            foreach (Type declared in clazz.GetDeclaredClasses())
            {
                GetTypesToCheck(declared, result);
            }
        }

        private static bool IsChecked<_T0>(Type<_T0> clazz)
        {
            lock (checkedTypes)
            {
                return checkedTypes.Contains(clazz);
            }
        }

        private static void MarkChecked<_T0>(Type<_T0> clazz)
        {
            lock (checkedTypes)
            {
                checkedTypes.AddItem(clazz);
            }
        }

        private static void CheckDependencies<_T0>(IList<Tuple<RuleDependency, IAnnotatedElement>> dependencies, Type<_T0> recognizerType)
            where _T0 : Recognizer<object, object>
        {
            string[] ruleNames = GetRuleNames(recognizerType);
            int[] ruleVersions = GetRuleVersions(recognizerType, ruleNames);
            RuleDependencyChecker.RuleRelations relations = ExtractRuleRelations(recognizerType);
            StringBuilder errors = new StringBuilder();
            foreach (Tuple<RuleDependency, IAnnotatedElement> dependency in dependencies)
            {
                if (!dependency.Item1.Recognizer().IsAssignableFrom(recognizerType))
                {
                    continue;
                }
                // this is the rule in the dependency set with the highest version number
                int effectiveRule = dependency.Item1.Rule();
                if (effectiveRule < 0 || effectiveRule >= ruleVersions.Length)
                {
                    string message = string.Format("Rule dependency on unknown rule %d@%d in %s%n", dependency.Item1.Rule(), dependency.Item1.Version(), dependency.Item1.Recognizer().ToString());
                    errors.Append(message);
                    continue;
                }
                EnumSet<Dependents> dependents = EnumSet.Of(Dependents.Self, dependency.Item1.Dependents());
                ReportUnimplementedDependents(errors, dependency, dependents);
                BitSet @checked = new BitSet();
                int highestRequiredDependency = CheckDependencyVersion(errors, dependency, ruleNames, ruleVersions, effectiveRule, null);
                if (dependents.Contains(Dependents.Parents))
                {
                    BitSet parents = relations.parents[dependency.Item1.Rule()];
                    for (int parent = parents.NextSetBit(0); parent >= 0; parent = parents.NextSetBit(parent + 1))
                    {
                        if (parent < 0 || parent >= ruleVersions.Length || @checked.Get(parent))
                        {
                            continue;
                        }
                        @checked.Set(parent);
                        int required = CheckDependencyVersion(errors, dependency, ruleNames, ruleVersions, parent, "parent");
                        highestRequiredDependency = Math.Max(highestRequiredDependency, required);
                    }
                }
                if (dependents.Contains(Dependents.Children))
                {
                    BitSet children = relations.children[dependency.Item1.Rule()];
                    for (int child = children.NextSetBit(0); child >= 0; child = children.NextSetBit(child + 1))
                    {
                        if (child < 0 || child >= ruleVersions.Length || @checked.Get(child))
                        {
                            continue;
                        }
                        @checked.Set(child);
                        int required = CheckDependencyVersion(errors, dependency, ruleNames, ruleVersions, child, "child");
                        highestRequiredDependency = Math.Max(highestRequiredDependency, required);
                    }
                }
                if (dependents.Contains(Dependents.Ancestors))
                {
                    BitSet ancestors = relations.GetAncestors(dependency.Item1.Rule());
                    for (int ancestor = ancestors.NextSetBit(0); ancestor >= 0; ancestor = ancestors.NextSetBit(ancestor + 1))
                    {
                        if (ancestor < 0 || ancestor >= ruleVersions.Length || @checked.Get(ancestor))
                        {
                            continue;
                        }
                        @checked.Set(ancestor);
                        int required = CheckDependencyVersion(errors, dependency, ruleNames, ruleVersions, ancestor, "ancestor");
                        highestRequiredDependency = Math.Max(highestRequiredDependency, required);
                    }
                }
                if (dependents.Contains(Dependents.Descendants))
                {
                    BitSet descendants = relations.GetDescendants(dependency.Item1.Rule());
                    for (int descendant = descendants.NextSetBit(0); descendant >= 0; descendant = descendants.NextSetBit(descendant + 1))
                    {
                        if (descendant < 0 || descendant >= ruleVersions.Length || @checked.Get(descendant))
                        {
                            continue;
                        }
                        @checked.Set(descendant);
                        int required = CheckDependencyVersion(errors, dependency, ruleNames, ruleVersions, descendant, "descendant");
                        highestRequiredDependency = Math.Max(highestRequiredDependency, required);
                    }
                }
                int declaredVersion = dependency.Item1.Version();
                if (declaredVersion > highestRequiredDependency)
                {
                    string message = string.Format("Rule dependency version mismatch: %s has maximum dependency version %d (expected %d) in %s%n", ruleNames[dependency.Item1.Rule()], highestRequiredDependency, declaredVersion, dependency.Item1.Recognizer().ToString());
                    errors.Append(message);
                }
            }
            if (errors.Length > 0)
            {
                throw new InvalidOperationException(errors.ToString());
            }
            MarkChecked(recognizerType);
        }

        private static readonly HashSet<Dependents> ImplementedDependents = EnumSet.Of(Dependents.Self, Dependents.Parents, Dependents.Children, Dependents.Ancestors, Dependents.Descendants);

        private static void ReportUnimplementedDependents(StringBuilder errors, Tuple<RuleDependency, IAnnotatedElement> dependency, EnumSet<Dependents> dependents)
        {
            EnumSet<Dependents> unimplemented = dependents.Clone();
            unimplemented.RemoveAll(ImplementedDependents);
            if (!unimplemented.IsEmpty())
            {
                string message = string.Format("Cannot validate the following dependents of rule %d: %s%n", dependency.Item1.Rule(), unimplemented);
                errors.Append(message);
            }
        }

        private static int CheckDependencyVersion(StringBuilder errors, Tuple<RuleDependency, IAnnotatedElement> dependency, string[] ruleNames, int[] ruleVersions, int relatedRule, string relation)
        {
            string ruleName = ruleNames[dependency.Item1.Rule()];
            string path;
            if (relation == null)
            {
                path = ruleName;
            }
            else
            {
                string mismatchedRuleName = ruleNames[relatedRule];
                path = string.Format("rule %s (%s of %s)", mismatchedRuleName, relation, ruleName);
            }
            int declaredVersion = dependency.Item1.Version();
            int actualVersion = ruleVersions[relatedRule];
            if (actualVersion > declaredVersion)
            {
                string message = string.Format("Rule dependency version mismatch: %s has version %d (expected <= %d) in %s%n", path, actualVersion, declaredVersion, dependency.Item1.Recognizer().ToString());
                errors.Append(message);
            }
            return actualVersion;
        }

        private static int[] GetRuleVersions<_T0>(Type<_T0> recognizerClass, string[] ruleNames)
            where _T0 : Recognizer<object, object>
        {
            int[] versions = new int[ruleNames.Length];
            FieldInfo[] fields = recognizerClass.GetFields();
            foreach (FieldInfo field in fields)
            {
                bool isStatic = (field.GetModifiers() & Modifier.Static) != 0;
                bool isInteger = field.FieldType == typeof(int);
                if (isStatic && isInteger && field.Name.StartsWith("RULE_"))
                {
                    try
                    {
                        string name = Sharpen.Runtime.Substring(field.Name, "RULE_".Length);
                        if (name.IsEmpty() || !System.Char.IsLower(name[0]))
                        {
                            continue;
                        }
                        int index = field.GetInt(null);
                        if (index < 0 || index >= versions.Length)
                        {
                            object[] @params = new object[] { index, field.Name, recognizerClass.Name };
                            Logger.Log(Level.Warning, "Rule index {0} for rule ''{1}'' out of bounds for recognizer {2}.", @params);
                            continue;
                        }
                        MethodInfo ruleMethod = GetRuleMethod(recognizerClass, name);
                        if (ruleMethod == null)
                        {
                            object[] @params = new object[] { name, recognizerClass.Name };
                            Logger.Log(Level.Warning, "Could not find rule method for rule ''{0}'' in recognizer {1}.", @params);
                            continue;
                        }
                        RuleVersion ruleVersion = ruleMethod.GetAnnotation<RuleVersion>();
                        int version = ruleVersion != null ? ruleVersion.Value() : 0;
                        versions[index] = version;
                    }
                    catch (ArgumentException ex)
                    {
                        Logger.Log(Level.Warning, null, ex);
                    }
                    catch (MemberAccessException ex)
                    {
                        Logger.Log(Level.Warning, null, ex);
                    }
                }
            }
            return versions;
        }

        private static MethodInfo GetRuleMethod<_T0>(Type<_T0> recognizerClass, string name)
            where _T0 : Recognizer<object, object>
        {
            MethodInfo[] declaredMethods = recognizerClass.GetMethods();
            foreach (MethodInfo method in declaredMethods)
            {
                if (method.Name.Equals(name) && method.IsAnnotationPresent(typeof(RuleVersion)))
                {
                    return method;
                }
            }
            return null;
        }

        private static string[] GetRuleNames<_T0>(Type<_T0> recognizerClass)
            where _T0 : Recognizer<object, object>
        {
            try
            {
                FieldInfo ruleNames = recognizerClass.GetField("ruleNames");
                return (string[])ruleNames.GetValue(null);
            }
            catch (NoSuchFieldException ex)
            {
                Logger.Log(Level.Warning, null, ex);
            }
            catch (SecurityException ex)
            {
                Logger.Log(Level.Warning, null, ex);
            }
            catch (ArgumentException ex)
            {
                Logger.Log(Level.Warning, null, ex);
            }
            catch (MemberAccessException ex)
            {
                Logger.Log(Level.Warning, null, ex);
            }
            return new string[0];
        }

        public static IList<Tuple<RuleDependency, IAnnotatedElement>> GetDependencies<_T0>(Type<_T0> clazz)
        {
            IList<Tuple<RuleDependency, IAnnotatedElement>> result = new List<Tuple<RuleDependency, IAnnotatedElement>>();
            IList<ElementType> supportedTarget = Arrays.AsList(typeof(RuleDependency).GetAnnotation<Target>().Value());
            foreach (ElementType target in supportedTarget)
            {
                switch (target)
                {
                    case ElementType.Type:
                    {
                        if (!clazz.IsAnnotation())
                        {
                            GetElementDependencies(clazz, result);
                        }
                        break;
                    }

                    case ElementType.AnnotationType:
                    {
                        if (!clazz.IsAnnotation())
                        {
                            GetElementDependencies(clazz, result);
                        }
                        break;
                    }

                    case ElementType.Constructor:
                    {
                        foreach (Constructor<object> ctor in clazz.GetDeclaredConstructors())
                        {
                            GetElementDependencies(ctor, result);
                        }
                        break;
                    }

                    case ElementType.Field:
                    {
                        foreach (FieldInfo field in Sharpen.Runtime.GetDeclaredFields(clazz))
                        {
                            GetElementDependencies(field, result);
                        }
                        break;
                    }

                    case ElementType.LocalVariable:
                    {
                        System.Console.Error.WriteLine("Runtime rule dependency checking is not supported for local variables.");
                        break;
                    }

                    case ElementType.Method:
                    {
                        foreach (MethodInfo method in Sharpen.Runtime.GetDeclaredMethods(clazz))
                        {
                            GetElementDependencies(method, result);
                        }
                        break;
                    }

                    case ElementType.Package:
                    {
                        // package is not a subset of class, so nothing to do here
                        break;
                    }

                    case ElementType.Parameter:
                    {
                        System.Console.Error.WriteLine("Runtime rule dependency checking is not supported for parameters.");
                        break;
                    }
                }
            }
            return result;
        }

        private static void GetElementDependencies(IAnnotatedElement annotatedElement, IList<Tuple<RuleDependency, IAnnotatedElement>> result)
        {
            RuleDependency dependency = annotatedElement.GetAnnotation<RuleDependency>();
            if (dependency != null)
            {
                result.AddItem(Tuple.Create(dependency, annotatedElement));
            }
            RuleDependencies dependencies = annotatedElement.GetAnnotation<RuleDependencies>();
            if (dependencies != null)
            {
                foreach (RuleDependency d in dependencies.Value())
                {
                    if (d != null)
                    {
                        result.AddItem(Tuple.Create(d, annotatedElement));
                    }
                }
            }
        }

        private static RuleDependencyChecker.RuleRelations ExtractRuleRelations<_T0>(Type<_T0> recognizer)
            where _T0 : Recognizer<object, object>
        {
            string serializedATN = GetSerializedATN(recognizer);
            if (serializedATN == null)
            {
                return null;
            }
            ATN atn = new ATNDeserializer().Deserialize(serializedATN.ToCharArray());
            RuleDependencyChecker.RuleRelations relations = new RuleDependencyChecker.RuleRelations(atn.ruleToStartState.Length);
            foreach (ATNState state in atn.states)
            {
                if (!state.epsilonOnlyTransitions)
                {
                    continue;
                }
                foreach (Transition transition in state.Transitions)
                {
                    if (transition.TransitionType != TransitionType.Rule)
                    {
                        continue;
                    }
                    RuleTransition ruleTransition = (RuleTransition)transition;
                    relations.AddRuleInvocation(state.ruleIndex, ruleTransition.target.ruleIndex);
                }
            }
            return relations;
        }

        private static string GetSerializedATN<_T0>(Type<_T0> recognizerClass)
        {
            try
            {
                FieldInfo serializedAtnField = Sharpen.Runtime.GetDeclaredField(recognizerClass, "_serializedATN");
                if (Modifier.IsStatic(serializedAtnField.GetModifiers()))
                {
                    return (string)serializedAtnField.GetValue(null);
                }
                return null;
            }
            catch (NoSuchFieldException)
            {
                if (recognizerClass.BaseType != null)
                {
                    return GetSerializedATN(recognizerClass.BaseType);
                }
                return null;
            }
            catch (SecurityException)
            {
                return null;
            }
            catch (ArgumentException)
            {
                return null;
            }
            catch (MemberAccessException)
            {
                return null;
            }
        }

        private sealed class RuleRelations
        {
            private readonly BitSet[] parents;

            private readonly BitSet[] children;

            public RuleRelations(int ruleCount)
            {
                parents = new BitSet[ruleCount];
                for (int i = 0; i < ruleCount; i++)
                {
                    parents[i] = new BitSet();
                }
                children = new BitSet[ruleCount];
                for (int i_1 = 0; i_1 < ruleCount; i_1++)
                {
                    children[i_1] = new BitSet();
                }
            }

            public bool AddRuleInvocation(int caller, int callee)
            {
                if (caller < 0)
                {
                    // tokens rule
                    return false;
                }
                if (children[caller].Get(callee))
                {
                    // already added
                    return false;
                }
                children[caller].Set(callee);
                parents[callee].Set(caller);
                return true;
            }

            public BitSet GetAncestors(int rule)
            {
                BitSet ancestors = new BitSet();
                ancestors.Or(parents[rule]);
                while (true)
                {
                    int cardinality = ancestors.Cardinality();
                    for (int i = ancestors.NextSetBit(0); i >= 0; i = ancestors.NextSetBit(i + 1))
                    {
                        ancestors.Or(parents[i]);
                    }
                    if (ancestors.Cardinality() == cardinality)
                    {
                        // nothing changed
                        break;
                    }
                }
                return ancestors;
            }

            public BitSet GetDescendants(int rule)
            {
                BitSet descendants = new BitSet();
                descendants.Or(children[rule]);
                while (true)
                {
                    int cardinality = descendants.Cardinality();
                    for (int i = descendants.NextSetBit(0); i >= 0; i = descendants.NextSetBit(i + 1))
                    {
                        descendants.Or(children[i]);
                    }
                    if (descendants.Cardinality() == cardinality)
                    {
                        // nothing changed
                        break;
                    }
                }
                return descendants;
            }
        }

        private RuleDependencyChecker()
        {
        }
    }
}

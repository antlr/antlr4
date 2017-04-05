/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#if NET45PLUS

using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Security;
using System.Text;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Misc
{
    /// <author>Sam Harwell</author>
    public class RuleDependencyChecker
    {
        private static readonly HashSet<string> checkedAssemblies = new HashSet<string>();

        public static void CheckDependencies(Assembly assembly)
        {
            if (IsChecked(assembly))
            {
                return;
            }

            IEnumerable<TypeInfo> typesToCheck = GetTypesToCheck(assembly);
            List<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>> dependencies = new List<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>>();
            foreach (TypeInfo clazz in typesToCheck)
            {
                dependencies.AddRange(GetDependencies(clazz));
            }

            if (dependencies.Count > 0)
            {
                IDictionary<TypeInfo, IList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>>> recognizerDependencies = new Dictionary<TypeInfo, IList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>>>();
                foreach (Tuple<RuleDependencyAttribute, ICustomAttributeProvider> dependency in dependencies)
                {
                    TypeInfo recognizerType = dependency.Item1.Recognizer.GetTypeInfo();
                    IList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>> list;
                    if (!recognizerDependencies.TryGetValue(recognizerType, out list))
                    {
                        list = new List<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>>();
                        recognizerDependencies[recognizerType] = list;
                    }
                    list.Add(dependency);
                }

                foreach (KeyValuePair<TypeInfo, IList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>>> entry in recognizerDependencies)
                {
                    //processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format("ANTLR 4: Validating {0} dependencies on rules in {1}.", entry.getValue().size(), entry.getKey().toString()));
                    CheckDependencies(entry.Value, entry.Key);
                }
            }

            MarkChecked(assembly);
        }

        private static IEnumerable<TypeInfo> GetTypesToCheck(Assembly assembly)
        {
            return assembly.DefinedTypes;
        }

        private static bool IsChecked(Assembly assembly)
        {
            lock (checkedAssemblies)
            {
                return checkedAssemblies.Contains(assembly.FullName);
            }
        }

        private static void MarkChecked(Assembly assembly)
        {
            lock (checkedAssemblies)
            {
                checkedAssemblies.Add(assembly.FullName);
            }
        }

        private static void CheckDependencies(IList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>> dependencies, TypeInfo recognizerType)
        {
            string[] ruleNames = GetRuleNames(recognizerType);
            int[] ruleVersions = GetRuleVersions(recognizerType, ruleNames);
            RuleDependencyChecker.RuleRelations relations = ExtractRuleRelations(recognizerType);
            StringBuilder errors = new StringBuilder();
            foreach (Tuple<RuleDependencyAttribute, ICustomAttributeProvider> dependency in dependencies)
            {
                if (!dependency.Item1.Recognizer.GetTypeInfo().IsAssignableFrom(recognizerType))
                {
                    continue;
                }
                // this is the rule in the dependency set with the highest version number
                int effectiveRule = dependency.Item1.Rule;
                if (effectiveRule < 0 || effectiveRule >= ruleVersions.Length)
                {
                    string message = string.Format("Rule dependency on unknown rule {0}@{1} in {2}", dependency.Item1.Rule, dependency.Item1.Version, dependency.Item1.Recognizer.ToString());
                    errors.AppendLine(dependency.Item2.ToString());
                    errors.AppendLine(message);
                    continue;
                }
                Dependents dependents = Dependents.Self | dependency.Item1.Dependents;
                ReportUnimplementedDependents(errors, dependency, dependents);
                BitSet @checked = new BitSet();
                int highestRequiredDependency = CheckDependencyVersion(errors, dependency, ruleNames, ruleVersions, effectiveRule, null);
                if ((dependents & Dependents.Parents) != 0)
                {
                    BitSet parents = relations.parents[dependency.Item1.Rule];
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
                if ((dependents & Dependents.Children) != 0)
                {
                    BitSet children = relations.children[dependency.Item1.Rule];
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
                if ((dependents & Dependents.Ancestors) != 0)
                {
                    BitSet ancestors = relations.GetAncestors(dependency.Item1.Rule);
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
                if ((dependents & Dependents.Descendants) != 0)
                {
                    BitSet descendants = relations.GetDescendants(dependency.Item1.Rule);
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
                int declaredVersion = dependency.Item1.Version;
                if (declaredVersion > highestRequiredDependency)
                {
                    string message = string.Format("Rule dependency version mismatch: {0} has maximum dependency version {1} (expected {2}) in {3}", ruleNames[dependency.Item1.Rule], highestRequiredDependency, declaredVersion, dependency.Item1.Recognizer.ToString());
                    errors.AppendLine(dependency.Item2.ToString());
                    errors.AppendLine(message);
                }
            }
            if (errors.Length > 0)
            {
                throw new InvalidOperationException(errors.ToString());
            }
        }

        private static readonly Dependents ImplementedDependents = Dependents.Self | Dependents.Parents | Dependents.Children | Dependents.Ancestors | Dependents.Descendants;

        private static void ReportUnimplementedDependents(StringBuilder errors, Tuple<RuleDependencyAttribute, ICustomAttributeProvider> dependency, Dependents dependents)
        {
            Dependents unimplemented = dependents;
            unimplemented &= ~ImplementedDependents;
            if (unimplemented != Dependents.None)
            {
                string message = string.Format("Cannot validate the following dependents of rule {0}: {1}", dependency.Item1.Rule, unimplemented);
                errors.AppendLine(message);
            }
        }

        private static int CheckDependencyVersion(StringBuilder errors, Tuple<RuleDependencyAttribute, ICustomAttributeProvider> dependency, string[] ruleNames, int[] ruleVersions, int relatedRule, string relation)
        {
            string ruleName = ruleNames[dependency.Item1.Rule];
            string path;
            if (relation == null)
            {
                path = ruleName;
            }
            else
            {
                string mismatchedRuleName = ruleNames[relatedRule];
                path = string.Format("rule {0} ({1} of {2})", mismatchedRuleName, relation, ruleName);
            }
            int declaredVersion = dependency.Item1.Version;
            int actualVersion = ruleVersions[relatedRule];
            if (actualVersion > declaredVersion)
            {
                string message = string.Format("Rule dependency version mismatch: {0} has version {1} (expected <= {2}) in {3}", path, actualVersion, declaredVersion, dependency.Item1.Recognizer.ToString());
                errors.AppendLine(dependency.Item2.ToString());
                errors.AppendLine(message);
            }
            return actualVersion;
        }

        private static int[] GetRuleVersions(TypeInfo recognizerClass, string[] ruleNames)
        {
            int[] versions = new int[ruleNames.Length];
            IEnumerable<FieldInfo> fields = recognizerClass.DeclaredFields;
            foreach (FieldInfo field in fields)
            {
                bool isStatic = field.IsStatic;
                bool isInteger = field.FieldType == typeof(int);
                if (isStatic && isInteger && field.Name.StartsWith("RULE_"))
                {
                    try
                    {
                        string name = field.Name.Substring("RULE_".Length);
                        if (name.Length == 0 || !System.Char.IsLower(name[0]))
                        {
                            continue;
                        }
                        int index = (int)field.GetValue(null);
                        if (index < 0 || index >= versions.Length)
                        {
                            object[] @params = new object[] { index, field.Name, recognizerClass.Name };
#if false
                            Logger.Log(Level.Warning, "Rule index {0} for rule ''{1}'' out of bounds for recognizer {2}.", @params);
#endif
                            continue;
                        }
                        MethodInfo ruleMethod = GetRuleMethod(recognizerClass, name);
                        if (ruleMethod == null)
                        {
                            object[] @params = new object[] { name, recognizerClass.Name };
#if false
                            Logger.Log(Level.Warning, "Could not find rule method for rule ''{0}'' in recognizer {1}.", @params);
#endif
                            continue;
                        }
                        RuleVersionAttribute ruleVersion = ruleMethod.GetCustomAttribute<RuleVersionAttribute>();
                        int version = ruleVersion != null ? ruleVersion.Version : 0;
                        versions[index] = version;
                    }
                    catch (ArgumentException)
                    {
#if false
                        Logger.Log(Level.Warning, null, ex);
#else
                        throw;
#endif
                    }
                    catch (MemberAccessException)
                    {
#if false
                        Logger.Log(Level.Warning, null, ex);
#else
                        throw;
#endif
                    }
                }
            }
            return versions;
        }

        private static MethodInfo GetRuleMethod(TypeInfo recognizerClass, string name)
        {
            IEnumerable<MethodInfo> declaredMethods = recognizerClass.DeclaredMethods;
            foreach (MethodInfo method in declaredMethods)
            {
                if (method.Name.Equals(name) && method.GetCustomAttribute<RuleVersionAttribute>() != null)
                {
                    return method;
                }
            }
            return null;
        }

        private static string[] GetRuleNames(TypeInfo recognizerClass)
        {
            FieldInfo ruleNames = recognizerClass.DeclaredFields.First(i => i.Name == "ruleNames");
            return (string[])ruleNames.GetValue(null);
        }

        public static IList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>> GetDependencies(TypeInfo clazz)
        {
            IList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>> result = new List<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>>();

            GetElementDependencies(AsCustomAttributeProvider(clazz), result);
            foreach (ConstructorInfo ctor in clazz.DeclaredConstructors)
            {
                GetElementDependencies(AsCustomAttributeProvider(ctor), result);
                foreach (ParameterInfo parameter in ctor.GetParameters())
                    GetElementDependencies(AsCustomAttributeProvider(parameter), result);
            }

            foreach (FieldInfo field in clazz.DeclaredFields)
            {
                GetElementDependencies(AsCustomAttributeProvider(field), result);
            }

            foreach (MethodInfo method in clazz.DeclaredMethods)
            {
                GetElementDependencies(AsCustomAttributeProvider(method), result);
#if COMPACT
                if (method.ReturnTypeCustomAttributes != null)
                    GetElementDependencies(AsCustomAttributeProvider(method.ReturnTypeCustomAttributes), result);
#else
                if (method.ReturnParameter != null)
                    GetElementDependencies(AsCustomAttributeProvider(method.ReturnParameter), result);
#endif

                foreach (ParameterInfo parameter in method.GetParameters())
                    GetElementDependencies(AsCustomAttributeProvider(parameter), result);
            }

            return result;
        }

        private static void GetElementDependencies(ICustomAttributeProvider annotatedElement, IList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>> result)
        {
            foreach (RuleDependencyAttribute dependency in annotatedElement.GetCustomAttributes(typeof(RuleDependencyAttribute), true))
            {
                result.Add(Tuple.Create(dependency, annotatedElement));
            }
        }

        private static RuleDependencyChecker.RuleRelations ExtractRuleRelations(TypeInfo recognizer)
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
                foreach (Transition transition in state.transitions)
                {
                    if (transition.TransitionType != TransitionType.RULE)
                    {
                        continue;
                    }
                    RuleTransition ruleTransition = (RuleTransition)transition;
                    relations.AddRuleInvocation(state.ruleIndex, ruleTransition.target.ruleIndex);
                }
            }
            return relations;
        }

        private static string GetSerializedATN(TypeInfo recognizerClass)
        {
            FieldInfo serializedAtnField = recognizerClass.DeclaredFields.First(i => i.Name == "_serializedATN");
            if (serializedAtnField != null)
                return (string)serializedAtnField.GetValue(null);

            if (recognizerClass.BaseType != null)
                return GetSerializedATN(recognizerClass.BaseType.GetTypeInfo());

            return null;
        }

        private sealed class RuleRelations
        {
            public readonly BitSet[] parents;

            public readonly BitSet[] children;

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

#if PORTABLE || DOTNETCORE
        public interface ICustomAttributeProvider
        {
            object[] GetCustomAttributes(Type attributeType, bool inherit);
        }

        protected static ICustomAttributeProvider AsCustomAttributeProvider(TypeInfo type)
        {
            return new TypeCustomAttributeProvider(type);
        }

        protected static ICustomAttributeProvider AsCustomAttributeProvider(MethodBase method)
        {
            return new MethodBaseCustomAttributeProvider(method);
        }

        protected static ICustomAttributeProvider AsCustomAttributeProvider(ParameterInfo parameter)
        {
            return new ParameterInfoCustomAttributeProvider(parameter);
        }

        protected static ICustomAttributeProvider AsCustomAttributeProvider(FieldInfo field)
        {
            return new FieldInfoCustomAttributeProvider(field);
        }

        protected sealed class TypeCustomAttributeProvider : ICustomAttributeProvider
        {
            private readonly TypeInfo _provider;

            public TypeCustomAttributeProvider(TypeInfo provider)
            {
                _provider = provider;
            }

            public object[] GetCustomAttributes(Type attributeType, bool inherit)
            {
                return _provider.GetCustomAttributes(attributeType, inherit).ToArray();
            }
        }

        protected sealed class MethodBaseCustomAttributeProvider : ICustomAttributeProvider
        {
            private readonly MethodBase _provider;

            public MethodBaseCustomAttributeProvider(MethodBase provider)
            {
                _provider = provider;
            }

            public object[] GetCustomAttributes(Type attributeType, bool inherit)
            {
                return _provider.GetCustomAttributes(attributeType, inherit).ToArray();
            }
        }

        protected sealed class ParameterInfoCustomAttributeProvider : ICustomAttributeProvider
        {
            private readonly ParameterInfo _provider;

            public ParameterInfoCustomAttributeProvider(ParameterInfo provider)
            {
                _provider = provider;
            }

            public object[] GetCustomAttributes(Type attributeType, bool inherit)
            {
                return _provider.GetCustomAttributes(attributeType, inherit).ToArray();
            }
        }

        protected sealed class FieldInfoCustomAttributeProvider : ICustomAttributeProvider
        {
            private readonly FieldInfo _provider;

            public FieldInfoCustomAttributeProvider(FieldInfo provider)
            {
                _provider = provider;
            }

            public object[] GetCustomAttributes(Type attributeType, bool inherit)
            {
                return _provider.GetCustomAttributes(attributeType, inherit).ToArray();
            }
        }
#else
        protected static ICustomAttributeProvider AsCustomAttributeProvider(ICustomAttributeProvider obj)
        {
            return obj;
        }
#endif
    }
}

#else

using System;
using System.Collections.Generic;
using System.Reflection;
using System.Security;
using System.Text;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Misc
{
    /// <author>Sam Harwell</author>
    public class RuleDependencyChecker
    {
#if false
        private static readonly Logger Logger = Logger.GetLogger(typeof(Antlr4.Runtime.Misc.RuleDependencyChecker).FullName);
#endif

        private const BindingFlags AllDeclaredStaticMembers = BindingFlags.DeclaredOnly | BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Static;
        private const BindingFlags AllDeclaredMembers = AllDeclaredStaticMembers | BindingFlags.Instance;
        private static readonly HashSet<string> checkedAssemblies = new HashSet<string>();

        public static void CheckDependencies(Assembly assembly)
        {
            if (IsChecked(assembly))
            {
                return;
            }

            IList<Type> typesToCheck = GetTypesToCheck(assembly);
            ArrayList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>> dependencies = new ArrayList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>>();
            foreach (Type clazz in typesToCheck)
            {
                dependencies.AddRange(GetDependencies(clazz));
            }

            if (dependencies.Count > 0)
            {
                IDictionary<Type, IList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>>> recognizerDependencies = new Dictionary<Type, IList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>>>();
                foreach (Tuple<RuleDependencyAttribute, ICustomAttributeProvider> dependency in dependencies)
                {
                    Type recognizerType = dependency.Item1.Recognizer;
                    IList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>> list;
                    if (!recognizerDependencies.TryGetValue(recognizerType, out list))
                    {
                        list = new ArrayList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>>();
                        recognizerDependencies[recognizerType] = list;
                    }
                    list.Add(dependency);
                }

                foreach (KeyValuePair<Type, IList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>>> entry in recognizerDependencies)
                {
                    //processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format("ANTLR 4: Validating {0} dependencies on rules in {1}.", entry.getValue().size(), entry.getKey().toString()));
                    CheckDependencies(entry.Value, entry.Key);
                }
            }

            MarkChecked(assembly);
        }

        private static IList<Type> GetTypesToCheck(Assembly assembly)
        {
            return assembly.GetTypes();
        }

        private static bool IsChecked(Assembly assembly)
        {
            lock (checkedAssemblies)
            {
                return checkedAssemblies.Contains(assembly.FullName);
            }
        }

        private static void MarkChecked(Assembly assembly)
        {
            lock (checkedAssemblies)
            {
                checkedAssemblies.Add(assembly.FullName);
            }
        }

        private static void CheckDependencies(IList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>> dependencies, Type recognizerType)
        {
            string[] ruleNames = GetRuleNames(recognizerType);
            int[] ruleVersions = GetRuleVersions(recognizerType, ruleNames);
            RuleDependencyChecker.RuleRelations relations = ExtractRuleRelations(recognizerType);
            StringBuilder errors = new StringBuilder();
            foreach (Tuple<RuleDependencyAttribute, ICustomAttributeProvider> dependency in dependencies)
            {
#if DOTNETCORE
                if (!dependency.Item1.Recognizer.GetTypeInfo().IsAssignableFrom(recognizerType))
#else
                    if (!dependency.Item1.Recognizer.IsAssignableFrom(recognizerType))
#endif
                {
                    continue;
                }
                // this is the rule in the dependency set with the highest version number
                int effectiveRule = dependency.Item1.Rule;
                if (effectiveRule < 0 || effectiveRule >= ruleVersions.Length)
                {
                    string message = string.Format("Rule dependency on unknown rule {0}@{1} in {2}", dependency.Item1.Rule, dependency.Item1.Version, dependency.Item1.Recognizer.ToString());
                    errors.AppendLine(dependency.Item2.ToString());
                    errors.AppendLine(message);
                    continue;
                }
                Dependents dependents = Dependents.Self | dependency.Item1.Dependents;
                ReportUnimplementedDependents(errors, dependency, dependents);
                BitSet @checked = new BitSet();
                int highestRequiredDependency = CheckDependencyVersion(errors, dependency, ruleNames, ruleVersions, effectiveRule, null);
                if ((dependents & Dependents.Parents) != 0)
                {
                    BitSet parents = relations.parents[dependency.Item1.Rule];
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
                if ((dependents & Dependents.Children) != 0)
                {
                    BitSet children = relations.children[dependency.Item1.Rule];
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
                if ((dependents & Dependents.Ancestors) != 0)
                {
                    BitSet ancestors = relations.GetAncestors(dependency.Item1.Rule);
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
                if ((dependents & Dependents.Descendants) != 0)
                {
                    BitSet descendants = relations.GetDescendants(dependency.Item1.Rule);
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
                int declaredVersion = dependency.Item1.Version;
                if (declaredVersion > highestRequiredDependency)
                {
                    string message = string.Format("Rule dependency version mismatch: {0} has maximum dependency version {1} (expected {2}) in {3}", ruleNames[dependency.Item1.Rule], highestRequiredDependency, declaredVersion, dependency.Item1.Recognizer.ToString());
                    errors.AppendLine(dependency.Item2.ToString());
                    errors.AppendLine(message);
                }
            }
            if (errors.Length > 0)
            {
                throw new InvalidOperationException(errors.ToString());
            }
        }

        private static readonly Dependents ImplementedDependents = Dependents.Self | Dependents.Parents | Dependents.Children | Dependents.Ancestors | Dependents.Descendants;

        private static void ReportUnimplementedDependents(StringBuilder errors, Tuple<RuleDependencyAttribute, ICustomAttributeProvider> dependency, Dependents dependents)
        {
            Dependents unimplemented = dependents;
            unimplemented &= ~ImplementedDependents;
            if (unimplemented != Dependents.None)
            {
                string message = string.Format("Cannot validate the following dependents of rule {0}: {1}", dependency.Item1.Rule, unimplemented);
                errors.AppendLine(message);
            }
        }

        private static int CheckDependencyVersion(StringBuilder errors, Tuple<RuleDependencyAttribute, ICustomAttributeProvider> dependency, string[] ruleNames, int[] ruleVersions, int relatedRule, string relation)
        {
            string ruleName = ruleNames[dependency.Item1.Rule];
            string path;
            if (relation == null)
            {
                path = ruleName;
            }
            else
            {
                string mismatchedRuleName = ruleNames[relatedRule];
                path = string.Format("rule {0} ({1} of {2})", mismatchedRuleName, relation, ruleName);
            }
            int declaredVersion = dependency.Item1.Version;
            int actualVersion = ruleVersions[relatedRule];
            if (actualVersion > declaredVersion)
            {
                string message = string.Format("Rule dependency version mismatch: {0} has version {1} (expected <= {2}) in {3}", path, actualVersion, declaredVersion, dependency.Item1.Recognizer.ToString());
                errors.AppendLine(dependency.Item2.ToString());
                errors.AppendLine(message);
            }
            return actualVersion;
        }

        private static int[] GetRuleVersions(Type recognizerClass, string[] ruleNames)
        {
            int[] versions = new int[ruleNames.Length];
#if DOTNETCORE
            FieldInfo[] fields = recognizerClass.GetTypeInfo().GetFields();
#else
            FieldInfo[] fields = recognizerClass.GetFields();
#endif
            foreach (FieldInfo field in fields)
            {
                bool isStatic = field.IsStatic;
                bool isInteger = field.FieldType == typeof(int);
                if (isStatic && isInteger && field.Name.StartsWith("RULE_"))
                {
                    try
                    {
                        string name = field.Name.Substring("RULE_".Length);
                        if (name.Length == 0 || !System.Char.IsLower(name[0]))
                        {
                            continue;
                        }
                        int index = (int)field.GetValue(null);
                        if (index < 0 || index >= versions.Length)
                        {
#if false
							object[] @params = new object[] { index, field.Name, recognizerClass.Name };
                            Logger.Log(Level.Warning, "Rule index {0} for rule ''{1}'' out of bounds for recognizer {2}.", @params);
#endif
                            continue;
                        }
                        MethodInfo ruleMethod = GetRuleMethod(recognizerClass, name);
                        if (ruleMethod == null)
                        {
#if false
							object[] @params = new object[] { name, recognizerClass.Name };
                            Logger.Log(Level.Warning, "Could not find rule method for rule ''{0}'' in recognizer {1}.", @params);
#endif
                            continue;
                        }
#if DOTNETCORE
                        RuleVersionAttribute ruleVersion = ruleMethod.GetCustomAttribute<RuleVersionAttribute>();
#else
                        RuleVersionAttribute ruleVersion = (RuleVersionAttribute)Attribute.GetCustomAttribute(ruleMethod, typeof(RuleVersionAttribute));
#endif
                        int version = ruleVersion != null ? ruleVersion.Version : 0;
                        versions[index] = version;
                    }
                    catch (ArgumentException)
                    {
#if false
                        Logger.Log(Level.Warning, null, ex);
#else
                        throw;
#endif
                    }
                    catch (MemberAccessException)
                    {
#if false
                        Logger.Log(Level.Warning, null, ex);
#else
                        throw;
#endif
                    }
                }
            }
            return versions;
        }

        private static MethodInfo GetRuleMethod(Type recognizerClass, string name)
        {
#if DOTNETCORE
            MethodInfo[] declaredMethods = recognizerClass.GetTypeInfo().GetMethods();
#else
            MethodInfo[] declaredMethods = recognizerClass.GetMethods();
#endif
            foreach (MethodInfo method in declaredMethods)
            {
#if DOTNETCORE
                if (method.Name.Equals(name) && method.IsDefined(typeof(RuleVersionAttribute)))
#else
                    if (method.Name.Equals(name) && Attribute.IsDefined(method, typeof(RuleVersionAttribute)))
#endif
                {
                    return method;
                }
            }
            return null;
        }

        private static string[] GetRuleNames(Type recognizerClass)
        {
#if DOTNETCORE
            FieldInfo ruleNames = recognizerClass.GetTypeInfo().GetField("ruleNames");
#else
            FieldInfo ruleNames = recognizerClass.GetField("ruleNames");
#endif
            return (string[])ruleNames.GetValue(null);
        }

        public static IList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>> GetDependencies(Type clazz)
        {
            IList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>> result = new ArrayList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>>();

#if DOTNETCORE
            GetElementDependencies(AsCustomAttributeProvider(clazz.GetTypeInfo()), result);
#else
            GetElementDependencies(AsCustomAttributeProvider(clazz), result);
#endif
#if DOTNETCORE
            foreach (ConstructorInfo ctor in clazz.GetTypeInfo().GetConstructors(AllDeclaredMembers))
#else
            foreach (ConstructorInfo ctor in clazz.GetConstructors(AllDeclaredMembers))
#endif
            {
                GetElementDependencies(AsCustomAttributeProvider(ctor), result);
                foreach (ParameterInfo parameter in ctor.GetParameters())
                    GetElementDependencies(AsCustomAttributeProvider(parameter), result);
            }

#if DOTNETCORE
            foreach (FieldInfo field in clazz.GetTypeInfo().GetFields(AllDeclaredMembers))
#else
            foreach (FieldInfo field in clazz.GetFields(AllDeclaredMembers))
#endif
            {
                GetElementDependencies(AsCustomAttributeProvider(field), result);
            }

#if DOTNETCORE
            foreach (MethodInfo method in clazz.GetTypeInfo().GetMethods(AllDeclaredMembers))
#else
            foreach (MethodInfo method in clazz.GetMethods(AllDeclaredMembers))
#endif
            {
                GetElementDependencies(AsCustomAttributeProvider(method), result);
#if COMPACT
                if (method.ReturnTypeCustomAttributes != null)
                    GetElementDependencies(AsCustomAttributeProvider(method.ReturnTypeCustomAttributes), result);
#else
                if (method.ReturnParameter != null)
                    GetElementDependencies(AsCustomAttributeProvider(method.ReturnParameter), result);
#endif

                foreach (ParameterInfo parameter in method.GetParameters())
                    GetElementDependencies(AsCustomAttributeProvider(parameter), result);
            }

            return result;
        }

        private static void GetElementDependencies(ICustomAttributeProvider annotatedElement, IList<Tuple<RuleDependencyAttribute, ICustomAttributeProvider>> result)
        {
            foreach (RuleDependencyAttribute dependency in annotatedElement.GetCustomAttributes(typeof(RuleDependencyAttribute), true))
            {
                result.Add(Tuple.Create(dependency, annotatedElement));
            }
        }

        private static RuleDependencyChecker.RuleRelations ExtractRuleRelations(Type recognizer)
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
                foreach (Transition transition in state.transitions)
                {
                    if (transition.TransitionType != TransitionType.RULE)
                    {
                        continue;
                    }
                    RuleTransition ruleTransition = (RuleTransition)transition;
                    relations.AddRuleInvocation(state.ruleIndex, ruleTransition.target.ruleIndex);
                }
            }
            return relations;
        }

        private static string GetSerializedATN(Type recognizerClass)
        {
#if DOTNETCORE
            FieldInfo serializedAtnField = recognizerClass.GetTypeInfo().GetField("_serializedATN", AllDeclaredStaticMembers);
#else
            FieldInfo serializedAtnField = recognizerClass.GetField("_serializedATN", AllDeclaredStaticMembers);
#endif
            if (serializedAtnField != null)
                return (string)serializedAtnField.GetValue(null);

#if DOTNETCORE
            if (recognizerClass.GetTypeInfo().BaseType != null)
                return GetSerializedATN(recognizerClass.GetTypeInfo().BaseType);
#else
            if (recognizerClass.BaseType != null)
                return GetSerializedATN(recognizerClass.BaseType);
#endif

            return null;
        }

        private sealed class RuleRelations
        {
            public readonly BitSet[] parents;

            public readonly BitSet[] children;

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

#if PORTABLE
        public interface ICustomAttributeProvider
        {
            object[] GetCustomAttributes(Type attributeType, bool inherit);
        }

        protected static ICustomAttributeProvider AsCustomAttributeProvider(Type type)
        {
            return new TypeCustomAttributeProvider(type);
        }

        protected static ICustomAttributeProvider AsCustomAttributeProvider(MethodBase method)
        {
            return new MethodBaseCustomAttributeProvider(method);
        }

        protected static ICustomAttributeProvider AsCustomAttributeProvider(ParameterInfo parameter)
        {
            return new ParameterInfoCustomAttributeProvider(parameter);
        }

        protected static ICustomAttributeProvider AsCustomAttributeProvider(FieldInfo field)
        {
            return new FieldInfoCustomAttributeProvider(field);
        }

        protected sealed class TypeCustomAttributeProvider : ICustomAttributeProvider
        {
            private readonly Type _provider;

            public TypeCustomAttributeProvider(Type provider)
            {
                _provider = provider;
            }

            public object[] GetCustomAttributes(Type attributeType, bool inherit)
            {
                return Attribute.GetCustomAttributes(_provider, attributeType, inherit);
            }
        }

        protected sealed class MethodBaseCustomAttributeProvider : ICustomAttributeProvider
        {
            private readonly MethodBase _provider;

            public MethodBaseCustomAttributeProvider(MethodBase provider)
            {
                _provider = provider;
            }

            public object[] GetCustomAttributes(Type attributeType, bool inherit)
            {
                return Attribute.GetCustomAttributes(_provider, attributeType, inherit);
            }
        }

        protected sealed class ParameterInfoCustomAttributeProvider : ICustomAttributeProvider
        {
            private readonly ParameterInfo _provider;

            public ParameterInfoCustomAttributeProvider(ParameterInfo provider)
            {
                _provider = provider;
            }

            public object[] GetCustomAttributes(Type attributeType, bool inherit)
            {
                return Attribute.GetCustomAttributes(_provider, attributeType, inherit);
            }
        }

        protected sealed class FieldInfoCustomAttributeProvider : ICustomAttributeProvider
        {
            private readonly FieldInfo _provider;

            public FieldInfoCustomAttributeProvider(FieldInfo provider)
            {
                _provider = provider;
            }

            public object[] GetCustomAttributes(Type attributeType, bool inherit)
            {
                return Attribute.GetCustomAttributes(_provider, attributeType, inherit);
            }
        }
#else
        protected static ICustomAttributeProvider AsCustomAttributeProvider(ICustomAttributeProvider obj)
        {
            return obj;
        }
#endif
    }
}

#endif

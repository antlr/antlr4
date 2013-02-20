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
using Sharpen;
using Sharpen.Annotation;
using Sharpen.Logging;
using Sharpen.Reflect;

namespace Antlr4.Runtime.Misc
{
    /// <author>Sam Harwell</author>
    public class RuleDependencyChecker
    {
        private static readonly Logger Logger = Logger.GetLogger(typeof(Antlr4.Runtime.Misc.RuleDependencyChecker
            ).FullName);

        private static readonly ISet<Type> checkedTypes = new HashSet<Type>();

        public static void CheckDependencies<_T0>(Type<_T0> dependentClass)
        {
            if (IsChecked(dependentClass))
            {
                return;
            }
            IList<Type> typesToCheck = new List<Type>();
            typesToCheck.AddItem(dependentClass);
            Sharpen.Collections.AddAll(typesToCheck, dependentClass.GetDeclaredClasses());
            foreach (Type clazz in typesToCheck)
            {
                if (IsChecked(clazz))
                {
                    continue;
                }
                IList<Tuple<RuleDependency, IAnnotatedElement>> dependencies = GetDependencies(clazz
                    );
                if (dependencies.IsEmpty())
                {
                    continue;
                }
                CheckDependencies(dependencies, dependencies[0].Item1.Recognizer());
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

        private static void CheckDependencies<_T0>(IList<Tuple<RuleDependency, IAnnotatedElement
            >> dependencies, Type<_T0> recognizerClass) where _T0:Recognizer<object, object
            >
        {
            string[] ruleNames = GetRuleNames(recognizerClass);
            int[] ruleVersions = GetRuleVersions(recognizerClass, ruleNames);
            StringBuilder incompatible = new StringBuilder();
            foreach (Tuple<RuleDependency, IAnnotatedElement> dependency in dependencies)
            {
                if (!recognizerClass.IsAssignableFrom(dependency.Item1.Recognizer()))
                {
                    continue;
                }
                if (dependency.Item1.Rule() < 0 || dependency.Item1.Rule() >= ruleVersions.Length)
                {
                    incompatible.Append(string.Format("Element %s dependent on unknown rule %d@%d in %s\n"
                        , dependency.Item2.ToString(), dependency.Item1.Rule(), dependency.Item1.Version
                        (), dependency.Item1.Recognizer().Name));
                }
                else
                {
                    if (ruleVersions[dependency.Item1.Rule()] != dependency.Item1.Version())
                    {
                        incompatible.Append(string.Format("Element %s dependent on rule %s@%d (found @%d) in %s\n"
                            , dependency.Item2.ToString(), ruleNames[dependency.Item1.Rule()], dependency
                            .Item1.Version(), ruleVersions[dependency.Item1.Rule()], dependency.Item1.Recognizer
                            ().Name));
                    }
                }
            }
            if (incompatible.Length != 0)
            {
                throw new InvalidOperationException(incompatible.ToString());
            }
            MarkChecked(recognizerClass);
        }

        private static int[] GetRuleVersions<_T0>(Type<_T0> recognizerClass, string[] ruleNames
            ) where _T0:Recognizer<object, object>
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
                            Logger.Log(Level.Warning, "Rule index {0} for rule ''{1}'' out of bounds for recognizer {2}."
                                , @params);
                            continue;
                        }
                        MethodInfo ruleMethod = GetRuleMethod(recognizerClass, name);
                        if (ruleMethod == null)
                        {
                            object[] @params = new object[] { name, recognizerClass.Name };
                            Logger.Log(Level.Warning, "Could not find rule method for rule ''{0}'' in recognizer {1}."
                                , @params);
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

        private static MethodInfo GetRuleMethod<_T0>(Type<_T0> recognizerClass, string name
            ) where _T0:Recognizer<object, object>
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

        private static string[] GetRuleNames<_T0>(Type<_T0> recognizerClass) where _T0:Recognizer
            <object, object>
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

        public static IList<Tuple<RuleDependency, IAnnotatedElement>> GetDependencies<_T0
            >(Type<_T0> clazz)
        {
            IList<Tuple<RuleDependency, IAnnotatedElement>> result = new List<Tuple<RuleDependency
                , IAnnotatedElement>>();
            IList<ElementType> supportedTarget = Arrays.AsList(typeof(RuleDependency).GetAnnotation
                <Target>().Value());
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
                        System.Console.Error.WriteLine("Runtime rule dependency checking is not supported for local variables."
                            );
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
                        System.Console.Error.WriteLine("Runtime rule dependency checking is not supported for parameters."
                            );
                        break;
                    }
                }
            }
            return result;
        }

        private static void GetElementDependencies(IAnnotatedElement annotatedElement, IList
            <Tuple<RuleDependency, IAnnotatedElement>> result)
        {
            RuleDependency dependency = annotatedElement.GetAnnotation<RuleDependency>();
            if (dependency != null)
            {
                result.AddItem(Tuple.Create(dependency, annotatedElement));
            }
            RuleDependencies dependencies = annotatedElement.GetAnnotation<RuleDependencies>(
                );
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

        public RuleDependencyChecker()
        {
        }
    }
}

/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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

package org.antlr.v4.runtime.misc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a field, parameter, local variable, or method (return
 * value) as potentially having the value {@code null}. The specific semantics
 * implied by this annotation depend on the kind of element the annotation is
 * applied to.
 *
 * <ul>
 * <li><strong>Field or Local Variable:</strong> Code reading the field or local
 * variable may not assume that the value is never {@code null}.</li>
 * <li><strong>Parameter:</strong> Code calling the method might pass
 * {@code null} for this parameter. The documentation for the method should
 * describe the behavior of the method in the event this parameter is
 * {@code null}.
 * </li>
 * <li><strong>Method (Return Value):</strong> Code calling the method may not
 * assume that the result of the method is never {@code null}. The documentation
 * for the method should describe the meaning of a {@code null} reference being
 * returned. Overriding methods may optionally use the {@link NotNull}
 * annotation instead of this annotation for the method, indicating that the
 * overriding method (and any method which overrides it) will never return a
 * {@code null} reference.</li>
 * </ul>
 *
 * <p>
 * The {@link NullUsageProcessor} annotation processor validates certain usage
 * scenarios for this annotation, with compile-time errors or warnings reported
 * for misuse. For detailed information about the supported analysis, see the
 * documentation for {@link NullUsageProcessor}.</p>
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface Nullable {
}

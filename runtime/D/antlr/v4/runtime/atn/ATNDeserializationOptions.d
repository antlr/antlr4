/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
 *  Copyright (c) 2017 Egbert Voigt
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

module antlr.v4.runtime.atn.ATNDeserializationOptions;

/**
 * Options setting for deserialization
 */
class ATNDeserializationOptions
{

    public static ATNDeserializationOptions defaultOptions;

    /**
     * @uml
     * @read
     */
    private bool readOnly_;

    /**
     * @uml
     * @read
     * @write
     */
    private bool verifyATN_;

    /**
     * @uml
     * @read
     * @write
     */
    private bool generateRuleBypassTransitions_;

    /**
     * @uml
     * @read
     * @write
     */
    private bool optimize_;

    public static this()
    {
        defaultOptions = new ATNDeserializationOptions();
        defaultOptions.readOnly_ = true;
    }

    public this()
    {
        this.verifyATN_ = true;
        this.generateRuleBypassTransitions_ = false;
        this.optimize_ = true;
    }

    public this(ATNDeserializationOptions options)
    {
        this.verifyATN_ = options.verifyATN;
        this.generateRuleBypassTransitions_ = options.generateRuleBypassTransitions;
        this.optimize_ = options.optimize;
    }

    public void makeReadOnly()
    {
        readOnly_ = true;
    }

    private void throwIfReadOnly()
    {
        assert(!readOnly_, "The object is read only.");
    }

    public final bool readOnly()
    {
        return this.readOnly_;
    }

    public final bool verifyATN()
    {
        return this.verifyATN_;
    }

    public final void verifyATN(bool verifyATN)
    {
        this.verifyATN_ = verifyATN;
    }

    public final bool generateRuleBypassTransitions()
    {
        return this.generateRuleBypassTransitions_;
    }

    public final void generateRuleBypassTransitions(bool generateRuleBypassTransitions)
    {
        this.generateRuleBypassTransitions_ = generateRuleBypassTransitions;
    }

    public final bool optimize()
    {
        return this.optimize_;
    }

    public final void optimize(bool optimize)
    {
        this.optimize_ = optimize;
    }

}

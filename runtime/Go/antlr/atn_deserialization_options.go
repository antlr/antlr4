// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

// ATNDeserializationDefaultOptions is the default settings for
// deserializing abstract transition networks.
var ATNDeserializationDefaultOptions = &ATNDeserializationOptions{true, false, false}

// ATNDeserializationOptions represents the settings for deserializing abstract
// transition networks.
type ATNDeserializationOptions struct {
	readOnly                      bool
	verifyATN                     bool
	generateRuleBypassTransitions bool
}

// NewATNDeserializationOptions returns a new instance of
// ATNDeserializationOptions
func NewATNDeserializationOptions(copyFrom *ATNDeserializationOptions) *ATNDeserializationOptions {
	if copyFrom != nil {
		return &ATNDeserializationOptions{
			readOnly:                      copyFrom.readOnly,
			verifyATN:                     copyFrom.verifyATN,
			generateRuleBypassTransitions: copyFrom.generateRuleBypassTransitions,
		}
	}

	return &ATNDeserializationOptions{}
}

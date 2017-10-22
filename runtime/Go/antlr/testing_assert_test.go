// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

// These assert functions are borrowed from https://github.com/stretchr/testify/ (MIT License)

package antlr

import (
	"fmt"
	"reflect"
	"testing"
)

type assert struct {
	t *testing.T
}

func assertNew(t *testing.T) *assert {
	return &assert{
		t: t,
	}
}

func (a *assert) Equal(expected, actual interface{}) bool {
	if !objectsAreEqual(expected, actual) {
		return a.Fail(fmt.Sprintf("Not equal:\n"+
			"expected: %#v\n"+
			"  actual: %#v\n", expected, actual))
	}
	return true
}

func objectsAreEqual(expected, actual interface{}) bool {
	if expected == nil || actual == nil {
		return expected == actual
	}
	return reflect.DeepEqual(expected, actual)
}

func (a *assert) Nil(object interface{}) bool {
	if isNil(object) {
		return true
	}
	return a.Fail(fmt.Sprintf("Expected nil, but got: %#v", object))
}

func (a *assert) NotNil(object interface{}) bool {
	if !isNil(object) {
		return true
	}
	return a.Fail("Expected value not to be nil.")
}

// isNil checks if a specified object is nil or not, without Failing.
func isNil(object interface{}) bool {
	if object == nil {
		return true
	}

	value := reflect.ValueOf(object)
	kind := value.Kind()
	if kind >= reflect.Chan && kind <= reflect.Slice && value.IsNil() {
		return true
	}

	return false
}

func (a *assert) Panics(f func()) bool {
	if funcDidPanic, panicValue := didPanic(f); !funcDidPanic {
		return a.Fail(fmt.Sprintf("func %#v should panic\n\r\tPanic value:\t%v", f, panicValue))
	}

	return true
}

// Fail reports a failure through
func (a *assert) Fail(failureMessage string) bool {
	a.t.Errorf("%s", failureMessage)
	return false
}

// didPanic returns true if the function passed to it panics. Otherwise, it returns false.
func didPanic(f func()) (bool, interface{}) {
	didPanic := false
	var message interface{}
	func() {
		defer func() {
			if message = recover(); message != nil {
				didPanic = true
			}
		}()
		// call the target function
		f()
	}()
	return didPanic, message
}

// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import (
	"bytes"
	"io"
	"os"
)

//  This is an InputStream that is loaded from a file all at once
//  when you construct the object.

type FileStream struct {
	*InputStream

	filename string
}

func NewFileStream(fileName string) *FileStream {

	buf := bytes.NewBuffer(nil)

	f, _ := os.Open(fileName) // Error handling elided for brevity.
	io.Copy(buf, f)           // Error handling elided for brevity.
	f.Close()

	fs := new(FileStream)

	fs.filename = fileName
	s := string(buf.Bytes())

	fs.InputStream = NewInputStream(s)

	return fs

}

func (f *FileStream) GetSourceName() string {
	return f.filename
}

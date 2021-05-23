// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import (
	"bytes"
	"io"
	"os"
)

// FileStream is an InputStream that is loaded from a file all at once
// when you construct the object.
type FileStream struct {
	*InputStream

	filename string
}

// NewFileStream returns a new instance of FileStream.
func NewFileStream(fileName string) (*FileStream, error) {

	buf := bytes.NewBuffer(nil)

	f, err := os.Open(fileName)
	if err != nil {
		return nil, err
	}
	defer f.Close()
	_, err = io.Copy(buf, f)
	if err != nil {
		return nil, err
	}
	s := string(buf.Bytes())

	fs := &FileStream{
		InputStream: NewInputStream(s),
		filename:    fileName,
	}

	return fs, nil

}

// GetSourceName returns the name of the file this object was created from.
func (f *FileStream) GetSourceName() string {
	return f.filename
}

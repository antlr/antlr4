// Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
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

//goland:noinspection GoUnusedExportedFunction
func NewFileStream(fileName string) (*FileStream, error) {

	buf := bytes.NewBuffer(nil)

	f, err := os.Open(fileName)
	if err != nil {
		return nil, err
	}
	defer func(f *os.File) {
		errF := f.Close()
		if errF != nil {
		}
	}(f)
	_, err = io.Copy(buf, f)
	if err != nil {
		return nil, err
	}

	fs := new(FileStream)

	fs.filename = fileName
	s := buf.String()
	fs.InputStream = NewInputStream(s)

	return fs, nil

}

func (f *FileStream) GetSourceName() string {
	return f.filename
}

package antlr4

import (
	"bytes"
	"fmt"
	"io"
	"os"
)

//
//  This is an InputStream that is loaded from a file all at once
//  when you construct the object.
//

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

	if PortDebug {
		fmt.Println(s)
	}
	fs.InputStream = NewInputStream(s)

	return fs

}

func (f *FileStream) GetSourceName() string {
	return f.filename
}

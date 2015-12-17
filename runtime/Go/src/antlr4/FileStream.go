package antlr4

import (
	"bytes"
	"os"
	"io"
)

//
//  This is an InputStream that is loaded from a file all at once
//  when you construct the object.
//

type FileStream struct {
	InputStream
	filename string
}

func NewFileStream(fileName string) {

	buf := bytes.NewBuffer(nil)

	f, _ := os.Open(fileName) // Error handling elided for brevity.
	io.Copy(buf, f)           // Error handling elided for brevity.
	f.Close()


}



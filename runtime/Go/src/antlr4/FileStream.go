package antlr4

//
//  This is an InputStream that is loaded from a file all at once
//  when you construct the object.
//

type FileStream struct {
	filename string
}

func FileStream(fileName) {
	var data = fs.readFileSync(fileName, "utf8")

	InputStream.call(this, data)

	fs.fileName = fileName

	return fs
}



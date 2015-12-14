package antlr

//
//  This is an InputStream that is loaded from a file all at once
//  when you construct the object.
// 
var InputStream = require('./InputStream').InputStream
var isNodeJs = typeof window == 'undefined' && typeof importScripts == 'undefined'
var fs = isNodeJs ? require("fs") : null

func FileStream(fileName) {
	var data = fs.readFileSync(fileName, "utf8")
	InputStream.call(this, data)
	this.fileName = fileName
	return this
}

FileStream.prototype = Object.create(InputStream.prototype)
FileStream.prototype.constructor = FileStream



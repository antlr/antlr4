function ATNDeserializer() {
	
	this.text = "Coucou";
	this.deserialize = function () {
		console.log(this.text);
	};
	
}

exports.ATNDeserializer = ATNDeserializer;
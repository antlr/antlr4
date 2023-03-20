const babel = require('@rollup/plugin-babel');

module.exports = {
	input: 'src/antlr4/index.web.js',
	output: {
		file: 'umd/antlr4.js',
		format: 'umd',
        name: 'antlr4'
	},
	plugins: [babel({ babelHelpers: 'bundled' })]
};
const path = require('path');
const ESLintPlugin = require('eslint-webpack-plugin');

module.exports = {
    mode: "production",
    entry: './src/antlr4/index.js',
    output: {
        filename: 'antlr4.js',
        path: path.resolve(__dirname, 'dist'),
        // the name of the exported antlr4
        library: "antlr4",
        libraryTarget: 'window'
    },
    externals: {
        module: "empty",
        net: "empty",
        fs: "empty"
    },
    target: "web",
    module: {
        rules: [{
            test: /\.js$/,
            exclude: /node_modules/,
            use: [ 'babel-loader' ]
        }]
    },
    performance: {
        maxAssetSize: 512000,
        maxEntrypointSize: 512000
    },
    plugins: [ new ESLintPlugin() ]
};

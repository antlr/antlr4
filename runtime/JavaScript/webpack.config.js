const path = require('path');

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
    node: {
        module: "empty",
        net: "empty",
        fs: "empty"
    },
    target: "web",
    module: {
        rules: [{
            test: /\.js$/,
            exclude: /node_modules/,
            use: {
                loader: 'babel-loader',
            }
        }]
    }
};

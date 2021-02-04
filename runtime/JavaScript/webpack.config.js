const path = require('path');

module.exports = (env, argv) => {
    return {
        entry: './src/antlr4/index.js',
        output: {
            filename:
                argv.mode === 'production' ? 'antlr4.min.js' : 'antlr4.js',
            path: path.resolve(__dirname, 'dist'),
            // the name of the exported antlr4
            library: 'antlr4',
            libraryTarget: 'umd',
        },
        node: {
            module: 'empty',
            net: 'empty',
            fs: 'empty',
        },
        target: 'web',
        module: {
            rules: [
                {
                    test: /\.js$/,
                    exclude: /node_modules/,
                    use: {
                        loader: 'babel-loader',
                    },
                },
            ],
        },
    };
};

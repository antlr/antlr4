import path from 'path';
import ESLintPlugin from 'eslint-webpack-plugin';
import {fileURLToPath} from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const nodeConfig = {
    mode: "production",
    entry: './src/antlr4/index.node.js',
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'antlr4.node.js',
        chunkFormat: "module",
        library: {
            type: "module"
        }
    },
    resolve: {
        extensions: [ '.js']
    },
    target: "node",
        module: {
        rules: [{
            test: /\.js$/,
            exclude: /node_modules/,
            use: [ 'babel-loader' ]
        }]
    },
    plugins: [ new ESLintPlugin() ],
        experiments: {
            outputModule: true
    },
    devtool: "source-map"
};

const webConfig = {
    mode: "production",
    entry: './src/antlr4/index.web.js',
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'antlr4.web.js',
        library: {
            type: "module"
        }
    },
    resolve: {
        extensions: [ '.js'],
        fallback: {
            fs: false
        }
    },
    target: "web",
    module: {
        rules: [{
            test: /\.js$/,
            exclude: [ /node_modules/, path.resolve(__dirname, "src/FileStream.js") ],
            use: [ 'babel-loader' ]
        }]
    },
    performance: {
        maxAssetSize: 512000,
        maxEntrypointSize: 512000
    },
    plugins: [ new ESLintPlugin() ],
    experiments: {
        outputModule: true
    },
    devtool: "source-map"
};

export default [ nodeConfig, webConfig ];

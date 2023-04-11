import path from 'path';
import ESLintPlugin from 'eslint-webpack-plugin';
import {fileURLToPath} from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);


const buildConfig = ( platform, extentions ) => ({
    mode: "production",
    entry: `./src/antlr4/index.${platform}.js`,
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: `antlr4.${platform}.${extentions}`,
        chunkFormat: extentions === "mjs" ? "module" : "commonjs",
        library: {
            type: extentions === "mjs" ? "module" : "commonjs"
        }
    },

    ...(platform === 'web' && {
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
        resolve: {
            extensions: [ '.js'],
            fallback: {
                fs: false
            }
        },
    }),

    ...(platform === 'node' && {
        module: {
            rules: [{
                test: /\.js$/,
                exclude: /node_modules/,
                use: [ 'babel-loader' ]
            }]
        },
        resolve: {
            extensions: [ '.js'],
        },
    }),
    target: platform,
    plugins: [ new ESLintPlugin() ],
    devtool: "source-map",
    experiments: {
        outputModule: extentions === "mjs"
    },
})


export default [
    buildConfig("node", "cjs"),
    buildConfig("node", "mjs"),
    buildConfig("web", "cjs"),
    buildConfig("web", "mjs"),
];

# JavaScript target for ANTLR 4

JavaScript runtime libraries for ANTLR 4

This runtime is available through npm. The package name is 'antlr4'.

This runtime has been tested in Node.js, Safari, Firefox, Chrome and IE.

See www.antlr.org for more information on ANTLR

See [Javascript Target](https://github.com/antlr/antlr4/blob/master/doc/javascript-target.md)
for more information on using ANTLR in JavaScript


## publishing

The JavaScript itself is tested using npm, so assumption is npm is already installed.
The current npm version used is 3.10.9.

### to npm

The publishing itself relies on the information in package.json.
To publish run `npm login` from Terminal, then `npm publish antlr4`

That's it!

### to browser

To publish antlr4 for browser usage you need to bundle it into a single
file with `npm run build`. This will create `dist/antlr4.js` file. Upload it
to your favourite server.

That's it!



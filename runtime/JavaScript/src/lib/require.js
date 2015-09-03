// [The "BSD license"]
// Copyright (c) 2015 Eric Vergnaud
// All rights reserved.
// Fragments from Torben Haase and Steven Levithan
//
//  Redistribution and use in source and binary forms, with or without
//  modification, are permitted provided that the following conditions
//  are met:
//
//  1. Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//  2. Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in the
//     documentation and/or other materials provided with the distribution.
//  3. The name of the author may not be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
//  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
//  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
//  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
//  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
//  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
//  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
//  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
//  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

// INFO Standalone require()
// This is a largely rewritten standalone version of the require function.
// The original purpose is to provide a require function compatible with NodeJS
// So that the same code can run in both NodeJS and browsers
// This cannot be achieved using RequireJS and comparable alternatives
// because they all have a prototype not compatible with NodeJS's require
// It is also that the code be able to run without a package builder
// such as RequireJS, WebPack or other alternatives, since they slow dow development

// This code was largely inspired by the following libraries and authors:

// Smoothie, by Torben Haase, Flowy Apps (torben@flowyapps.com)
// But unfortunately Smoothie's require cannot run in web workers
// So I had to rewrite a lot of stuff, although 50% of the code is unchanged

// parseUri, by Steven Levithan <stevenlevithan.com>

// NOTE The load parameter points to the function, which prepares the
// environment for each module and runs its code. Scroll down to the end of
// the file to see the function definition.


(function(load) { 'use strict';

    var RequireError = function(message, fileName, lineNumber) {
        this.name = "RequireError";
        this.message = message;
    }
    RequireError.prototype = Object.create(Error.prototype);

    // INFO RequireOptions
    // The values can be set by defining a object called RequireOptions, which
    // contains properties of the same name as the options to be changed.
    // NOTE The RequireOptions object has to be defined before this script is loaded
    // Changing the values in the RequireOptions object will have no effect afterwards!

    // NOTE Global module paths
    var paths = self.RequireOptions && self.RequireOptions.paths!==undefined ? self.RequireOptions.paths.slice(0) : ['./'];

    // INFO Current module paths
    // pwd[0] contains the path of the currently loading module
    // pwd[1] contains the path its parent module and so on.
    var pwd = Array('');

    // INFO URI parser
    function parseUri (str) {
        var	o   = parseUri.options,
            m   = o.parser[o.strictMode ? "strict" : "loose"].exec(str),
            uri = {},
            i   = 14;

        while (i--) uri[o.key[i]] = m[i] || "";

        uri[o.q.name] = {};
        uri[o.key[12]].replace(o.q.parser, function ($0, $1, $2) {
            if ($1) uri[o.q.name][$1] = $2;
        });

        return uri;
    };

    parseUri.options = {
        strictMode: true,
        key: ["source","protocol","authority","userInfo","user","password","host","port","relative","path","directory","file","query","anchor"],
        q:   {
            name:   "queryKey",
            parser: /(?:^|&)([^&=]*)=?([^&]*)/g
        },
        parser: {
            strict: /^(?:([^:\/?#]+):)?(?:\/\/((?:(([^:@]*)(?::([^:@]*))?)?@)?([^:\/?#]*)(?::(\d*))?))?((((?:[^?#\/]*\/)*)([^?#]*))(?:\?([^#]*))?(?:#(.*))?)/,
            loose:  /^(?:(?![^:@]+:[^:@\/]*@)([^:\/?#.]+):)?(?:\/\/)?((?:(([^:@]*)(?::([^:@]*))?)?@)?([^:\/?#]*)(?::(\d*))?)(((\/(?:[^?#](?![^?#\/]*\.[^?#\/.]+(?:[?#]|$)))*\/?)?([^?#\/]*))(?:\?([^#]*))?(?:#(.*))?)/
        }
    };

    // INFO splitDirs
    // function used to split a full directory path into its components
    // takes car of leading and trailing path separators
    function splitDirs(str) {
        var dirs = str.split("/");
        if(!dirs[0]) dirs = dirs.slice(1);
        if(!dirs[dirs.length-1]) dirs = dirs.slice(0,-1);
        return dirs;
    }

    // INFO resolveUrl
    // function used to build a full and plain url given a relative uri
    // takes an optional location parameter when called recursively
    // otherwise uses the location of the loading page, or script in workers
    // the function also takes car of relative path fragments, such as ./ and ../
    // even when they are included in a full path
    function resolveUrl (str, location) {
        location = location || self.location;
        var full = parseUri(location);
        var uri = parseUri(str);
        if(uri.host) {
            // cater for relative path components, the below takes care of both /./ and /../
            var rel = uri.path.indexOf("/.");
            if (rel > 0) {
                location = full.protocol + "://" + full.authority + uri.path.slice(0, rel + 1); // includes the trailing slash
                str = uri.path.slice(rel + 1);
                return resolveUrl(str, location);
            } else {
                return uri;
            }
        } else {
            if(uri.path.startsWith("/")) {
                str = full.protocol + "://" + full.authority + uri.path;
                return resolveUrl(str);
            } else if(uri.path.startsWith("./")) {
                var paths = splitDirs(uri.path).slice(1);
                str = full.protocol + "://" + full.authority + full.directory + paths.join("/");
                return resolveUrl(str);
            } else if(uri.path.startsWith("..")) {
                var dirs = splitDirs(full.directory);
                var paths = splitDirs(uri.path);
                while (paths[0] === "..") {
                    dirs = dirs.slice(0, -1);
                    paths = paths.slice(1);
                }
                str = full.protocol + "://" + full.authority + "/" + dirs.join("/") + "/" + paths.join("/");
                return resolveUrl(str);
            } else
                throw "Unsupported";
        }
    }

    // INFO Module cache
    // Contains getter functions for the exports objects of all the loaded modules.
    // The getter for the module 'mymod' is named '$mymod' to prevent collisions with
    // predefined object properties (see note below). As long as a module has not been
    // loaded the getter is either undefined or contains the module code as a function
    var cache = new Object();
    var locks = new Object();

    // INFO Module getter
    // Takes a module identifier, resolves it and gets the module code via an
    // AJAX request from the module URI. If this was successful the code and
    // some environment variables are passed to the load function. The return
    // value is the module's `exports` object. If the cache already contains
    // an object for the module id, this object is returned directly.
    // NOTE If a callback function has been passed, the AJAX request is asynchronous
    // and the mpdule exports are passed to the callback function after the
    // module has been loaded.

    function require(identifier, callback) {

        var descriptor = resolveId(identifier);
        var cacheid = '$'+descriptor.id;

        if (cache[cacheid]) {
            if (typeof cache[cacheid] === 'string')
                load(descriptor, cache, pwd, cache[cacheid]);
            // NOTE The callback should always be called asynchronously to
            // ensure that a cached call won't differ from an uncached one.
            callback && setTimeout(function(){callback(cache[cacheid])}, 0);
            return cache[cacheid];
        }

        var request = new XMLHttpRequest();

        // NOTE IE8 doesn't support the onload event, therefore we use onreadystatechange
        // as a fallback here. However, onreadystatechange shouldn't be used for all browsers,
        // since at least mobile Safari seems to have an issue where onreadystatechange
        // is called twice for readyState 4.
        callback && (request[request.onload===null?'onload':'onreadystatechange'] = onLoad);
        request.open('GET', descriptor.uri, !!callback);
        // NOTE Sending the request causes the event loop to continue. Therefore pending
        // AJAX load events for the same url might be executed before this synchronous onLoad
        // is executed. This should be no problem, but in Chrome the responseText of the sneaked
        // in load events will be empty. Therefore we have to lock the loading while executong send().
        locks[cacheid] = locks[cacheid]++||1;
        request.send();
        locks[cacheid]--;
        !callback && onLoad();
        return cache[cacheid];

        function onLoad() {
            if (request.readyState != 4)
                return;
            if (request.status != 200 && request.status != 0 ) // 0 for Safari with file protocol
                throw new RequireError('unable to load '+descriptor.id+" ("+request.status+" "+request.statusText+")");
            if (locks[cacheid]) {
                console.warn("module locked: " + descriptor.id);
                callback && setTimeout(onLoad, 0);
                return;
            }
            if (!cache[cacheid])
                load(descriptor, cache, pwd, 'function(){\n'+request.responseText+'\n}');
            callback && callback(cache[cacheid]);
        }
    }

    // INFO Module resolver
    // Takes a module identifier and resolves it to a module id and URI.
    // Both values are returned as a module descriptor, which can then
    // be passed to `fetch` to load a module, and cache it by id.

    function resolveId(identifier) {
        // NOTE Matches [1]:[..]/[path/to/][file][.js]
        var m = identifier.match(/^(?:([^:\/]+):)?(\.\.?)?\/?((?:.*\/)?)([^\.]+)?(\..*)?$/);
        // NOTE Matches [1]:[/path/to]
        var p = pwd[0].match(/^(?:([^:\/]+):)?(.*)/);
        var root = m[2] ? paths[p[1]?parseInt(p[1]):0] : paths[m[1]?parseInt(m[1]):0];
        var url = resolveUrl((m[2]?root+p[2]+m[2]+'/':root)+m[3]+(m[4]?m[4]:'index'));
        var id = url.source.replace(/^[^:]*:\/\/[^\/]*\/|\/(?=\/)/g, '');
        var uri = "/"+id+(m[5]?m[5]:'.js');
        root.replace(/[^\/]+\//g, function(r) {
            id = (id.substr(0, r.length) == r) ?id.substr(r.length) : id = '../'+id;
        });
        return {'id':id,'uri':uri};
    }

    // INFO Exporting require to global scope
    if (self.require !== undefined)
        throw new RequireError('\'require\' already defined in global scope');

    try {
        Object.defineProperty(self, 'require', {'value':require});
        Object.defineProperty(self.require, 'resolveUrl', {'value':resolveUrl});
        Object.defineProperty(self.require, 'paths', {'get':function(){return paths.slice(0);}});
    }
    catch (e) {
        // NOTE IE8 can't use defineProperty on non-DOM objects, so we have to fall back to unsave property assignments in this case.
        self.require = require;
        self.require.resolve = resolve;
        self.require.paths = paths.slice(0);
        // NOTE We definitely need a getter for the cache, so we make the cache a DOM-object in IE8.
        // NOTE This won't work in IE8 web workers, but as of writing, it's an acceptable trade-off
        cache = document.createElement('DIV');
    }

    // INFO Adding preloaded modules to cache
    for (var id in (self.RequireOptions && self.RequireOptions.preloaded))
        cache['$'+id] = self.RequireOptions.preloaded[id].toString();

    // INFO Parsing module root paths
    for (var i=0; i<paths.length; i++) {
        var url = resolveUrl(paths[i]);
        paths[i] = '/'+url.source.replace(/^[^:]*:\/\/[^\/]*\/|\/(?=\/)/g, '');
    }

})(

    // INFO Module loader
    // Takes the module descriptor, the global variables and the module code,
    // sets up the module environment, defines the module getter in the cache
    // and executes the module code. If module is a bundle the code of the
    // pre-loaded modules will be stored in the cache afterwards.
    // NOTE This function is defined as an anonymous function, which is passed as
    // a parameter to the closure above to provide a clean environment (only
    // global variables, module and exports) for the loaded module. This is
    // also the reason why `source`, `pwd` & `cache` are not named parameters.

    function /*load*/(module/*, cache, pwd, source*/) {
        var exports = new Object();
        Object.defineProperty(module, 'exports', {'get':function(){return exports;},'set':function(e){exports=e;}});
        arguments[2].unshift(module.id.match(/(?:.*\/)?/)[0]);
        Object.defineProperty(arguments[1], '$'+module.id, {'get':function(){return exports;}});
        // NOTE Firebug ignores the sourceUrl when the source is composed inside the eval call.
        var script = '('+arguments[3]+')();\n//# sourceURL='+module.uri;
        eval(script);
        // NOTE Store module code in the cache if the loaded file is a bundle
        if (typeof module.id !== 'string') {
            for (id in module) {
                arguments[1]['$' + require.resolve(id).id] = module[id].toString();
            }
        }
        arguments[2].shift();
    }

);

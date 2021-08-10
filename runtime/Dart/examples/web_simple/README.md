An absolute bare-bones web app.

```sh
$ antlr4 -o lib/src -Dlanguage=Dart -no-listener -visitor Hello.g4
```

```sh
$ dart pub global activate webdev
$ dart pub get
```

```sh
$ webdev serve
...
[INFO] Serving `web` on http://127.0.0.1:8080
...
```

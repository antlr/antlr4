# Contributing to ANTLR 4

1. [Fork](https://help.github.com/articles/fork-a-repo) the [antlr/antlr4 repo](https://github.com/antlr/antlr4), which will give you both key branches, `master` and `dev`
2. Make sure to `git checkout dev` in your fork so that you are working from the latest development branch
3. Create and work from a branch derived from `dev` such as `git checkout -b your-branch-name`
4. Install and configure [EditorConfig](http://editorconfig.org/) so your text editor or IDE uses the ANTLR 4 coding style
5. [Build ANTLR 4](doc/building-antlr.md)
6. [Run the ANTLR project unit tests](doc/antlr-project-testing.md)
7.   إنشاء أ.   [طلب السحب.](https://help.github.com/articles/[بدء التشغيل.MD](https://github.com/user-attachments/files/19559938/getting-started.md)
     استخدام-سحب-طلبات/) مع التغييرات الخاصة بك وتأكد من أنك تقارن الخاص بك. 'dev'-الفرع المشتقة في شوكتك إلى    'ديف.  '    فرع من...    `ANTLR/ANTLR4'ANTLR/ANTLR4.' الريبو:  

<img src="doc/images/PR-on-dev.png" width="600">

**Note:** Each commit requires a "signature", which is simple as using `-s` (not 
`-S`) to the git commit command:

```
git commit -s -m 'This is my commit message'
```

Github's pull request process enforces the sig and gives instructions on how to 
fix any commits that lack the sig. See [Github DCO app](https://github.com/apps/dco) 
for more info.

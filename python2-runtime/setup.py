from distutils.core import setup

setup(
    name='antlr4-python2-runtime',
    version='4.3.0',
    packages=['antlr4', 'antlr4.atn', 'antlr4.dfa', 'antlr4.tree', 'antlr4.error', 'antlr4.xpath'],
    package_dir={'': 'src'},
    url='http://www.antlr.org',
    license='BSD',
    author='Eric Vergnaud, Terence Parr, Sam Harwell',
    author_email='eric@test',
    description='AntLR 4.3.0 runtime for Python 2.7.6'
)

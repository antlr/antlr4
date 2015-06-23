from distutils.core import setup

setup(
    name='antlr4-python3-runtime',
    version='4.5.2',
    packages=['antlr4', 'antlr4.atn', 'antlr4.dfa', 'antlr4.tree', 'antlr4.error', 'antlr4.xpath'],
    package_dir={'': 'src'},
    url='http://www.antlr.org',
    license='BSD',
    author='Eric Vergnaud, Terence Parr, Sam Harwell',
    author_email='eric.vergnaud@wanadoo.fr',
    description='ANTLR 4.5.2 runtime for Python 3.4.0'
)

from setuptools import setup

v = '4.12.0'

setup(
    name='antlr4-python3-runtime',
    version=v,
    packages=['antlr4', 'antlr4.atn', 'antlr4.dfa', 'antlr4.tree', 'antlr4.error', 'antlr4.xpath'],
    package_dir={'': 'src'},
    install_requires=[
        "typing ; python_version<'3.5'",
    ],
    url='http://www.antlr.org',
    license='BSD',
    author='Eric Vergnaud, Terence Parr, Sam Harwell',
    author_email='eric.vergnaud@wanadoo.fr',
    entry_points={'console_scripts': ['pygrun=antlr4._pygrun:main']},
    description='ANTLR %s runtime for Python 3' % v
)

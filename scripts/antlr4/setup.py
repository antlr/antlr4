from setuptools import setup, find_packages

v = '0.1'

setup(
    name='antlr4-scripts',
    version=v,
    py_modules=['antlr4_tool_runner'],
    install_requires=[
        "install-jdk"
    ],
    url='http://www.antlr.org',
    license='BSD',
    author='Terence Parr',
    author_email='parrt@antlr.org',
    entry_points={'console_scripts': ['antlr4=antlr4_tool_runner:main']},
    description='Scripts to run ANTLR4 tool and grammar profiler'
)

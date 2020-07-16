__version__ = "4.8.0"

def major() -> str:
    assert __version__.count('.') == 2
    first = __version__.find('.')
    return __version__[0: first]

def minor() -> str:
    assert __version__.count('.') == 2
    second = __version__.rfind('.')
    return __version__[0: second]

def patch() -> str:
    return __version__

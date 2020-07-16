__version__ = "4.8.0"

def major():
    assert __version__.count('.') == 2
    first = __version__.find('.')
    return __version__[0: first]

def minor():
    assert __version__.count('.') == 2
    second = __version__.rfind('.')
    return __version__[0: second]

def patch():
    return __version__

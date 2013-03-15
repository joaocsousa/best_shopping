from utils import Utils
from random import randrange

class Hiper(object):
    name = None
    domain = None
    mainPath = None
    url = None
    #constructor
    def __init__(self, name = "", domain = "", mainPath = ""):
        self.name = name
        self.domain = domain
        self.mainPath = mainPath
        self.url = self._domain + "/" + self._mainPath
    def startFetchingProducts(self):
        raise NotImplementedError()
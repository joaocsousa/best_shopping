#!/home/tinycool/.env/bin/python2.7

import continente, jumbo
import threading

class Fetcher(threading.Thread):
    def __init__(self, hiper):
        threading.Thread.__init__(self)
        self.hiper = hiper
    def run(self):
        if self.hiper == 'jumbo':
            jumb = jumbo.Jumbo()
            jumb.startFetchingProducts()
        elif self.hiper == 'continente':
            ctn = continente.Continente()
            ctn.startFetchingProducts()
        print 'Finished fetching products of: ', self.hiper


if __name__ == "__main__":
    fetchContinente = Fetcher('continente')
    fetchContinente.start()
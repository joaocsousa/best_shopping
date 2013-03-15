#!/home/tinycool/.env/bin/python2.7

import os
os.environ['DJANGO_SETTINGS_MODULE'] = 'hiper_precos.settings'

from django.db import connection    

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

    # delete everything
    cursor = connection.cursor()
    cursor.execute("set foreign_key_checks = 0")
    cursor.execute("truncate table hipers_categoria")
    cursor.execute("truncate table hipers_hiper")
    cursor.execute("truncate table hipers_produto")
    cursor.execute("set foreign_key_checks = 1")

    print "Tables cleaned"

    fetchContinente = Fetcher('continente')
    fetchContinente.start()

    #fetchJumbo = Fetcher('jumbo')
    #fetchJumbo.start()

    #fetchJumbo.join()    # Wait for the background task to finish
    fetchContinente.join()
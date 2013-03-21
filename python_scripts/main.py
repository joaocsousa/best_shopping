#!/usr/bin/python

import os
os.environ['DJANGO_SETTINGS_MODULE'] = 'hiper_precos.settings'

from django.db import connection
import continente, jumbo, time
from utils import Utils

if __name__ == "__main__":

    start_time = time.time()

    # delete everything
    #cursor = connection.cursor()
    #Utils.clearTables(cursor)

    ctn = continente.Continente()
    ctn.startFetchingProducts()

    jumb = jumbo.Jumbo()
    jumb.startFetchingProducts()

    print "Finished - Elapsed: " + str(time.time()-start_time) + " seconds"
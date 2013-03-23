#!/usr/bin/python

import os
os.environ['DJANGO_SETTINGS_MODULE'] = 'hiper_precos.settings'

from django.db import connections
import continente, jumbo, time
from utils import Utils
import hiper_precos

import requests

if __name__ == "__main__":

    #start_time = time.time()

    # delete everything
    cursor = connections[hiper_precos.utils.Utils.getDbToWriteTo()].cursor()
    Utils.clearTables(cursor)

    ctn = continente.Continente()
    ctn.startFetchingProducts()

    jumb = jumbo.Jumbo()
    jumb.startFetchingProducts()

    # requests.get('http://localhost/hipers_updated', auth=('user', 'pass'))
    textResp = requests.get('http://localhost:8000/hipers_updated')
    print textResp.text

    print "Finished - Elapsed: " + str(time.time()-start_time) + " seconds"
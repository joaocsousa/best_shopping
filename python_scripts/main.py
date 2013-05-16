#!/usr/bin/python

import os
os.environ['DJANGO_SETTINGS_MODULE'] = 'hiper_precos.settings'

from django.db import connections
import continente, jumbo, time, requests
from utils import Utils
from os.path import expanduser
import hiper_precos, hiper_precos.settings

if __name__ == "__main__":

    start_time = time.time()

    currDBToWrite = Utils.makeGetRequest(requests.Session(), "http://www.tinycoolthings.com/get_db_to_write_to/", None)

    lastWrittenDb = Utils.makeGetRequest(requests.Session(), "http://www.tinycoolthings.com/get_db_to_read_from", None)

    hiper_precos.settings.LAST_WRITTEN_DATABASE = lastWrittenDb

    # delete everything
    cursor = connections[currDBToWrite].cursor()
    Utils.clearTables(cursor)

    ctn = continente.Continente()
    ctn.startFetchingProducts()

    jumb = jumbo.Jumbo()
    jumb.startFetchingProducts()

    Utils.makeGetRequest(requests.Session(), 'http://tinycoolthings.com/hipers_updated', None)

    hiper_precos.settings.LAST_WRITTEN_DATABASE = None

    print "Finished - Elapsed: " + str(time.time()-start_time) + " seconds"

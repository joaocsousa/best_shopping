#!/usr/bin/python

import os
os.environ['DJANGO_SETTINGS_MODULE'] = 'hiper_precos.settings'

from django.db import connections
import continente, jumbo, time
from utils import Utils


if __name__ == "__main__":

    start_time = time.time()

    currDBToWrite = Utils.makeRequest('http://tinycoolthings.com/get_db_to_write_to')

    # delete everything
    cursor = connections[currDBToWrite].cursor()
    Utils.clearTables(cursor)

    ctn = continente.Continente()
    ctn.startFetchingProducts()

    jumb = jumbo.Jumbo()
    jumb.startFetchingProducts()

    Utils.makeRequest('http://tinycoolthings.com/hipers_updated')

    currDBToWrite = Utils.makeRequest('http://tinycoolthings.com/get_db_to_write_to')

    file = open("/home/pi/lastWrittenDb.dat", "w")
    file.write(currDbToWrite);
    file.close()

    print "Finished - Elapsed: " + str(time.time()-start_time) + " seconds"

#!/usr/bin/python

import sys
sys.path.append('/Users/joaosousa/Projects/hiper_precos/django-proj/hiper_precos')

import os
os.environ['DJANGO_SETTINGS_MODULE'] = 'hiper_precos.settings'

from django.db import connections
import continente, jumbo, time, requests
from utils import Utils
from os.path import expanduser
import hiper_precos, hiper_precos.settings

import dryscrape

if __name__ == "__main__":

    start_time = time.time()

    sess = dryscrape.Session(base_url = 'http://www.continente.pt/')

    sess.set_attribute('auto_load_images', False)

    sess.set_error_tolerant(True)

    sess.visit('stores/continente/pt-pt/public/Pages/homepage.aspx')

    allCats = sess.at_xpath('//*[@id="categoryMenu"]').children()

    for cat in allCats:
        catName = cat.text().replace("\xc2","").replace("\xa0","").replace("\n","")
        if catName == "Campanhas":
            continue
        print catName

        cat.click(True)

        xPathSubCats = '//*[@id="'+cat.get_attr("id")+'"]/div[4]/div/div[1]/ul'

        print xPathSubCats

        allSubCats = sess.at_xpath(xPathSubCats)

        print allSubCats

        for subCat in allSubCats:
            subCatName = subCat.text().replace("\xc2","").replace("\xa0","").replace("\n","")
            print subCatName

        raise SystemExit

    # currDBToWrite = Utils.makeGetRequest(requests.Session(), "http://www.tinycoolthings.com/get_db_to_write_to/", None)

    # lastWrittenDb = Utils.makeGetRequest(requests.Session(), "http://www.tinycoolthings.com/get_db_to_read_from", None)

    # hiper_precos.settings.LAST_WRITTEN_DATABASE = lastWrittenDb

    # # delete everything
    # cursor = connections[currDBToWrite].cursor()
    # Utils.clearTables(cursor)

    # ctn = continente.Continente()
    # ctn.startFetchingProducts()

    # jumb = jumbo.Jumbo()
    # jumb.startFetchingProducts()

    # Utils.makeGetRequest(requests.Session(), 'http://tinycoolthings.com/hipers_updated', None)

    # hiper_precos.settings.LAST_WRITTEN_DATABASE = None

    print "Finished - Elapsed: " + str(time.time()-start_time) + " seconds"

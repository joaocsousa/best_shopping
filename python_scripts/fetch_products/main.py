#!/home/tinycool/.env/bin/python2.7

import os
os.environ['DJANGO_SETTINGS_MODULE'] = 'hiper_precos.settings'

from django.db import connection    

if __name__ == "__main__":

    # delete everything
    cursor = connection.cursor()
    cursor.execute("set foreign_key_checks = 0")
    cursor.execute("truncate table hipers_categoria")
    cursor.execute("truncate table hipers_hiper")
    cursor.execute("truncate table hipers_produto")
    cursor.execute("set foreign_key_checks = 1")

    print "Tables cleaned"

    # os.system("nohup /home/tinycool/python_scripts/fetch_products/fetchContinente.py > continente.out 2>&1&")
    os.system("/home/tinycool/python_scripts/fetch_products/fetchContinente.py &")

    #fetchJumbo = Fetcher('jumbo')
    #fetchJumbo.start()

    #fetchJumbo.join()    # Wait for the background task to finish
    #fetchContinente.join()
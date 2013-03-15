#! /bin/sh
rsync -av python_scripts --exclude '*.pyc' pi@172.16.1.69:~/
rsync -av django-proj/hiper_precos/ --exclude '*.pyc' pi@172.16.1.69:~/website

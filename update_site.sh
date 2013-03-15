#! /bin/sh
rsync -av django-proj/hiper_precos/ --exclude '*.pyc' tinycool@tinycoolthings.com:~/website

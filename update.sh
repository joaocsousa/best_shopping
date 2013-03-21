#!/bin/bash

CONFIG_FILE=rasp.conf

if [[ -f $CONFIG_FILE ]]; then
        . $CONFIG_FILE
fi

rsync -av python_scripts --exclude '*.pyc' $RASP_USER'@'$RASP_IP:~/
rsync -av django-proj/hiper_precos/ --exclude '*.pyc' $RASP_USER'@'$RASP_IP:~/website/hiper_precos

rsync -av django-proj/hiper_precos/ --exclude '*.pyc' tinycool@tinycoolthings.com:~/website/hiper_precos

#!/bin/bash

CONFIG_FILE=rasp.conf

if [[ -f $CONFIG_FILE ]]; then
        . $CONFIG_FILE
fi

fhp_mac='Fraunhofer'
host_name=${HOSTNAME?}

if [[ "$host_name" != *"$fhp_mac"* ]]; then
  rsync -av python_scripts --exclude '*.pyc' $RASP_USER'@'$RASP_IP:~/
  rsync -av django-proj/hiper_precos/ --exclude '*.pyc' $RASP_USER'@'$RASP_IP:~/website/hiper_precos
fi

rsync -av django-proj/hiper_precos/ --exclude '*.pyc' tinycool@tinycoolthings.com:~/website/hiper_precos

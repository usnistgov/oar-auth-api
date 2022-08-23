#!/bin/bash
if [ -f /home/ubuntu/oar-docker/midas/authapi/oar-auth-api.jar ];
then
  #remove previous build
  sudo rm -r /home/ubuntu/oar-docker/midas/authapi/oar-auth-api.jar
fi

#!/bin/bash
sudo rm -r /opt/data/backup/oar-auth-api/
if [ -f /home/ubuntu/oar-docker/midas/authapi/oar-auth-api.jar ];
then
  #backup previous build
  sudo cp -r /home/ubuntu/oar-docker/midas/authapi/oar-auth-api.jar /opt/data/backup/oar-auth-api/
  #remove previous build
  sudo rm -r /home/ubuntu/oar-docker/midas/authapi/oar-auth-api.jar
fi

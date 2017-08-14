#!/bin/bash

if [ -z "${1}" ]; then
   version="latest"
else
   version="${1}"
fi


docker push gennyproject/kieclient:"${version}"
docker tag -f gennyproject/kieclient:"${version}"  gennyproject/kieclient:latest
docker push gennyproject/kieclient:latest


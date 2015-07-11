#!/bin/bash

ARRAY=(AUVSI DBH EEPIS ERAU FAU GIT NCKU ODUSM ODUBB TUCE CUA UCF UF UOFM ULSAN UWF VU)

while getopts ":c:" opt; do
  case $opt in
    c)
      echo "-c was triggered, Parameter: $OPTARG" >&2
      for i in ${ARRAY[@]}; do
        curl -o /dev/null -sw "%{http_code} %{url_effective}\\n" -X POST http://192.168.1.111:8080/run/end/$OPTARG/${i}
      done
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
      ;;
    :)
      echo "Option -$OPTARG requires an argument." >&2
      exit 1
      ;;
  esac
done

exit 0

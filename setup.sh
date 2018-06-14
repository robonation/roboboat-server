#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR

start() {
  echo "*** Starting roboboat-server ***" > /var/log/roboboat-server.log
  local file=".installed"
  if [ ! -f $file ]; then
    sudo mkdir -p /etc/roboboat
    sudo chown robonation:robonation /etc/roboboat
    generateResources
    touch $file
  fi
 
  mvn jetty:run
}

stop() {
  return 0
}

uninstall() {
  local file=".installed"
  if [ -f $file ]; then
    sudo rm -rf /etc/roboboat
    rm $file
  fi
  sudo rm /etc/init.d/roboboat-server
}

generatePassword() {
  local LEN=0
  local PASSWORD=""
  while [ $LEN -lt 6 ]; do
    PASSWORD=$(curl -s "http://www.randomtext.me/api/gibberish/p-1/1-1" | jq '.text_out | sub("</?p>";"") | sub("\\..*$"; "") | ascii_downcase' | sed -e 's/\"//g')
    let LEN=${#PASSWORD}
  done
  echo "$PASSWORD"
}

generateResources() {
  local path="src/main/resources"
  if [ ! -d $path ]; then
    mkdir -p $path
  fi

  local file="$path/realm.properties"
  if [ ! -f $file ]; then
    password=$(generatePassword)
    echo "** Realm auth is:  login:admin  password:$password"
    echo "admin: $password" > $file
  fi

  local keystore="$path/keystore"
  if [ ! -f $keystore ]; then
    keytool -genkey -alias roboboat -keyalg RSA -keystore $keystore -keysize 2048 -storepass qwerty123 -keypass $password -dname "CN=RoboBoat Team, OU=RoboBoat, O=RoboNation, L='Daytona Beach', ST=FL, C=US"
    echo "** Generated keystore at $keystore with storepass: qwerty123 and keypass $password"
  fi
}

case "$1" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  uninstall)
    uninstall
    ;;
  restart)
    stop
    start
    ;;
  *)
    echo "Usage: $0 {start|stop|restart|uninstall}"
esac

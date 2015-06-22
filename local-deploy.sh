#!/bin/bash
mvn install:install-file -Dfile=target/mission2015/WEB-INF/lib/mission2015-1.0-SNAPSHOT.jar -DpomFile=pom.xml -Dpackaging=jar

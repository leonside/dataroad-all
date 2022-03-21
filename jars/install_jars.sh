#!/usr/bin/env bash

mvn install:install-file -DgroupId=bsh -DartifactId=bsh -Dversion=1.0 -Dpackaging=jar -Dfile=./bsh-1.0.jar

## oracle driver
mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.1.0 -Dpackaging=jar -Dfile=./ojdbc6-11.2.0.1.0.jar

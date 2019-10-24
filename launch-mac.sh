#!/bin/bash

export CLASSPATH=".:dist/*:dist/libs/*"
java -Xmx2048m -Dwzpath=wz/ -Dlogback.configurationFile=logback.xml net.server.Server

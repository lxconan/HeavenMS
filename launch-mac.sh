#!/bin/bash

export CLASSPATH=".:dist/*:dist/libs/*"
java -Xmx2048m -Dwzpath=wz/ net.server.Server

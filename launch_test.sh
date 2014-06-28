#!/bin/sh
find . -name '*.class' -exec rm '{}' \;
javac -d bin/ src/**/*.java
javac -d bin/ -cp bin/ src/*.java
cd bin
java Test

#!/bin/bash

./gradlew build -x test -x checkstyleMain -x :runelite-client:compileTestJava
rm -rf runelite-client/build/libs/*
./gradlew shadowJar

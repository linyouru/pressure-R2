#!/bin/bash

cp ../target/pressure-R2-0.0.1-SNAPSHOT.jar ./
docker build -t zws-r2-pressure-execute .
rm pressure-R2-0.0.1-SNAPSHOT.jar
#!/usr/bin/env bash
java \
-Dport=8191 \
-Dzk=10.211.55.12:2181 \
-DzkPath=/hippo \
-jar hippo-server/target/hippo-server.jar
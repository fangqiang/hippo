#!/usr/bin/env bash
nohup java \
-server \
-Xmx10G \
-Xms10G \
-XX:+UseG1GC \
-XX:MaxGCPauseMillis=20 \
-Djava.net.preferIPv4Stack=true \
-XX:+PrintGCDateStamps  \
-XX:+PrintGCDetails \
-XX:+PrintGCApplicationStoppedTime \
-XX:+PrintTenuringDistribution \
-Xloggc:gc.log \
-XX:MaxDirectMemorySize=100G \
-jar hippo.jar 10.57.27.23 &


curl "http://10.57.27.23:8082/setRead?n=$1"

curl "http://10.57.27.23:8082/read?n=$1"

curl "http://10.57.27.23:8082/write?s=$1&e=$2"

sh write.sh 0 20000000
sh write.sh 20000000 40000000
sh write.sh 40000000 48000000
sh write.sh 60000000 8000000
sh write.sh 80000000 100000000
sh read.sh 12

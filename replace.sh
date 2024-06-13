#!/bin/bash
echo "第一个参数是: $1"
echo "第二个参数是: $2"
echo "第三个参数是: $3"
sed -i "s/REPLACE_QL_URL/$1/g" app/src/main/java/cn/moon/ql/Config.java
sed -i "s/REPLACE_QL_CLIENT_ID/$2/g" app/src/main/java/cn/moon/ql/Config.java
sed -i "s/REPLACE_QL_CLIENT_SECRET/$3/g" app/src/main/java/cn/moon/ql/Config.java
cat app/src/main/java/cn/moon/ql/Config.java
#!/usr/bin/env sh
sed -i  's/REPLACE_QL_URL/$QL_URL/g' app/src/main/java/cn/moon/ql/Config.java
sed -i  's/REPLACE_QL_CLIENT_ID/$QL_CLIENT_ID/g' app/src/main/java/cn/moon/ql/Config.java
sed -i  's/REPLACE_QL_CLIENT_SECRET/$QL_CLIENT_SECRET/g' app/src/main/java/cn/moon/ql/Config.java

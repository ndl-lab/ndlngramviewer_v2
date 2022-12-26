#!/bin/bash
echo "start build.sh"

echo "start node"

cd web
npm install
npm run build
if [ $? != 0 ]; then #【終了コード】0：成功、1：失敗
    echo "npm error"
    exit 1
fi
cd ..

echo "copy assets"
mkdir src/main/resources/static
cp -r web/public/ngramviewer/* src/main/resources/static/

echo "start maven"

echo $JAVA_HOME
mvn --version
mvn clean package
if [ $? != 0 ]; then #【終了コード】0：成功、1：失敗
    echo "mvn error"
    exit 1
fi


echo "docker build start on `date`"
docker build -t "ngramviewer-v1:latest" -f "./Dockerfile" .
if [ $? != 0 ]; then #【終了コード】0：成功、1：失敗
    echo "docker build error"
    exit 1
fi

echo "docker build complete"

#!/bin/bash

echo "start build.sh"

echo "start node"

cd web
npm install
npm run build
if [ $? != 0 ]; then #【終了コード】0：成功、1：失敗
    $CODEBUILD_BUILD_SUCCEEDING = 0 #【ビルドステータス】0：失敗、1：成功
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
    $CODEBUILD_BUILD_SUCCEEDING = 0 #【ビルドステータス】0：失敗、1：成功
    echo "mvn error"
    exit 1
fi

mkdir build
cp ./target/ngramviewer-0.1.jar ./build/
cp -r ./target/lib ./build/lib
cp -r ./src/main/resources/config ./build/

echo "local build complete"
echo Build completed on `date`

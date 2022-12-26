# NDL Ngram Viewer(Version 2)

## 概要
図書資料約97万点及び雑誌資料132万点を対象に、2022年5月からNDLラボの実験サービスとして公開している[NDL Ngram Viewer](https://lab.ndl.go.jp/ngramviewer/)のソースコードです。

対象範囲の大幅な拡大に伴って、2022年5月から2023年1月まで公開していた[Version 1](https://github.com/ndl-lab/ndlngramviewer_v1)に変更を加えたため、リポジトリを分けています。


## このリポジトリについて

このREADMEはローカル環境(http://localhost:9981/ngramviewer/
)にアプリケーションを構築する手順を説明しています。

自分でビルドする場合は、appディレクトリ内のREADME.mdを参考にしてください。

なお、リリース時のアプリケーションのURLやElasticsearchのエンドポイントの設定は
[app/src/main/resources/config/application.yml](app/src/main/resources/config/application.yml)を編集してビルドすることで変更可能です。


## サービス起動手順
### 1. アプリケーションの準備
自分でビルドする場合は、appディレクトリ内のREADME.mdを参考にしてください。

本リポジトリが配布するパッケージを利用する場合にはjavaのリリースバイナリを下記からダウンロードして適当な場所に展開してください。

### 2. サービス提供用Elasticsearchの準備
infra-docker/es_dockerに、サービス提供用Elasticsearchのdocker-composeの設定ファイル一式があります。infra-docker/README.mdの指示に従ってElasticsearchコンテナを起動してください。

### 3. インデックスの初期化
ビルド済アプリケーションを置いたディレクトリで下記のコマンドを実行してください。

**【注意！】Elasticsearchコンテナのデータが初期化されますので気を付けてください**
```
java -jar ngramviewer-0.1.jar batch create-index all
```

### 4. 投入用データのダウンロード
自分で投入用データの作成を行う場合には、datacreationtoolsディレクトリ内のREADME.mdを参考にしてください。


当館が用意したデータを投入する場合、必要な全データセットを次のリポジトリから公開しています。

https://github.com/ndl-lab/ndlngramdata

上記のリポジトリから公開しているデータセットを利用した投入データの作成方法については、
[datacreationtools/4_merge/README.md](datacreationtools/4_merge/README.md)
を参考にしてください。


### 5. 投入用データのインデックス
下記のコマンドを実行してください。
```
java -jar ngramviewer-0.1.jar batch index-gzip sorted-merge-ngram.json.gz
```

### 6. サービスの起動
下記のコマンドを実行してサービスを起動してください。
```
java -jar ngramviewer-0.1.jar web
```

実行後、

http://localhost:9981/ngramviewer/

から起動したサービスにアクセスできます。


## 参考文献
技術的な詳細や機能については、下記の文献を参照してください。

青池亨. 日本語資料の全文テキストデータ分析ツールNDL Ngram Viewerの開発について. じんもんこん2022.

青池亨. E2533 - NDL Ngram Viewerの公開：全文テキストデータ可視化サービス カレントアウェアネス-E, No.442, 国立国会図書館(https://current.ndl.go.jp/e2533)


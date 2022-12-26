
## NDL Ngram Viewerが利用するNoSQLのDokcer設定ファイル

### es_docker(形態素分割用API兼サービス提供用)

es_dockerディレクトリは、形態素分割用兼サービス提供用のdocker-composeの設定ファイルが入っています。

docker-compose up -d

で起動します。デフォルト設定ではesdataディレクトリ内にインデックスのデータが保存され、http://localhost:9210にエンドポイントが立ち上がります。

### keydb_docker（ngramの集計用）

docker-compose up -d

で起動します。デフォルト設定ではredisdataディレクトリ内にダンプデータが保存され、http://localhost:26379にエンドポイントが立ち上がります。

**KeyDBについては、250GBまでメモリを利用する設定にしてあります。適宜ご利用の環境に合わせて修正してからご利用ください。**
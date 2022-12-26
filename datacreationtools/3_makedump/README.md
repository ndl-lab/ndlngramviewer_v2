# STEP3. ngram集計結果のダンプデータ作成


1. ダンプデータの作成
次のコマンドでKeyDBコンテナ内に入り、ダンプデータの出力を行ってください。(コンテナ名keydb_redis_1の箇所は、docker psコマンド等で起動中のコンテナから確認してください。)
```
docker exec -it keydb_redis_1 bash
```
(dockerコンテナ内で)
```
#keydb-cli save
```
実行後、redisdataディレクトリ以下にdump.rdbファイルが出力されます。

2. json.gz形式への変換

以下のコマンドを実行してください。
```
java -jar ngramviewer-0.1.jar batch rdb2json (rdbファイルのパス) (データセット名称(tosho-pdm,tosho-all,zasshi-all等)) (出力したいjson.gzのパス) (※オプション　ngramの総頻度の切り捨ての閾値)
```

3. STEP4以降の作業の準備
一旦gzipファイルを展開し、次のコマンドでngramキーワードに対して辞書順にソートしてください。

```
LC_ALL=C.UTF-8 sort --buffer-size=300G --parallel=16 -t '\t' -k 1,1 (jsonファイル名) -o (出力したいjsonファイル名)
```

ソート後のファイルを再度gzip圧縮してください。



# STEP4. ngram集計結果の併合処理

NDL Ngram Viewer（version 2）に投入するためにはSTEP1からSTEP3までの処理で作成した3つの断面を1つにまとめる必要があります。

STEP1から3までを省略する場合は、
tosho-pdm、tosho-all及びzasshi-allのデータセットを次のリポジトリ(https://github.com/ndl-lab/ndlngramdata)からダウンロードしてください。

ダウンロード及び展開コマンド例

```
curl -OL https://lab.ndl.go.jp/dataset/ngramviewer/sorted-tosho-pdmngram.json.gz
curl -OL https://lab.ndl.go.jp/dataset/ngramviewer/sorted-tosho-allngram.json.gz
curl -OL https://lab.ndl.go.jp/dataset/ngramviewer/sorted-zasshi-allngram.json.gz
gzip -dc sorted-tosho-pdmngram.json.gz > sorted-tosho-pdmngram.json
gzip -dc sorted-tosho-allngram.json.gz > sorted-tosho-allngram.json
gzip -dc sorted-zasshi-allngram.json.gz > sorted-zasshi-allngram.json
```

次に、ダウンロードしたファイルを1つのソート済jsonファイルにします。
```
LC_ALL=C.UTF-8 sort --buffer-size=300G --parallel=16 -t '\t' -k 1,1 -m sorted-tosho-allngram.json sorted-tosho-pdmngram.json sorted-zasshi-allngram.json -o sorted-3all.json
```

最後に併合処理を実行します。
https://github.com/nlohmann/json/blob/develop/include/nlohmann/json.hpp
からjson.hppをダウンロードして配置後、下記のようにmerge.cppをビルドして実行してください。
```
g++ merge.cpp -o merge
./merge
```

併合処理後のjsonは1行1jsonとして、「ngramキーワード」及びデータセット毎に「データセット名」「総頻度情報」「刊行年代毎の頻度情報」を含みます。

1行分の情報の例（見やすくするためにインデントを加えています）
```
{
    "keyword": "いもほりに行った",#ngramキーワード
    "keywordattribute": [
        {
            "materialtype": "zasshi-all",#データセット名
            "count": 14,#総頻度情報
            "ngramyearjson": "{\"1976\":1,\"1964\":1,\"1975\":1,\"1974\":1,\"1985\":1,\"1962\":1,\"1971\":1,\"1981\":1,\"1956\":3,\"1978\":1,\"1966\":1,\"1977\":1}"#刊行年代毎の頻度情報
        },
        {
            "count": 15,
            "materialtype": "tosho-all",
            "ngramyearjson": "{\"1964\":2,\"1961\":2,\"1960\":1,\"1980\":1,\"1959\":1,\"1958\":2,\"1957\":1,\"1967\":4,\"1955\":1}"
        }
    ]
}

```
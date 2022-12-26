# 投入データの作成
投入データの作成は次の3ステップで行っています。
ステップごとにディレクトリを用意し、実行に必要なスクリプト等を配置しています。
各ステップに対応するディレクトリ名と処理内容の概要は次のとおりです。

NDL Ngram Viewer (Version 2)を再現する場合には、STEP1からSTEP3までを実行して断面ごとの頻度統計情報を作成し、作成した3つの頻度統計情報をSTEP4で併合します。

STEP1.(1_ngrammaker) 形態素分割と異体字包摂によるngramの作成、資料毎のngramの集計 

STEP2.(2_ngramcounter) 資料を横断したngramの集計

STEP3.(3_makedump) ngram集計結果のダンプデータ作成

STEP4.(4_datamerge) 3つの断面の併合処理

STEP5.(5_index) NDL Ngram Viewer(Version 2)へのインデックス処理


## 事前準備
infra-dockerディレクトリ内のREADME.mdに従い、KeyDB及びElasticsearchのコンテナを立ち上げてください。

**KeyDBについては、デフォルトで250GBまでメモリを利用する設定にしてあります。適宜ご利用の環境に合わせて修正してからご利用ください。**




## 実行環境情報
1_ngrammaker及び2_ngramcounterについてはjupyter notebookとしています。Python 3.6.9以上の環境で動作を確認しています。

3_makedump及び5_indexについては、インデックス処理に、NDL Ngram Viewerのビルド済アプリケーションを利用しています。適宜自分でビルドするか、配布バイナリをお使い下さい。

4_datamergeは、高速化のためにC++で作成したツールを利用しています。C++03以降であればコンパイル可能です。（当館環境はgcc version 9.4.0）




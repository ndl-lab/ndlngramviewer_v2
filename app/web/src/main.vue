<style lang="scss">
main {
  background: white;
}

.w1 {
  width: 10%;
}
.w3 {
  width: 30%;
}
.w6 {
  width: 60%;
}

.footer {
  .link {
    margin-right: 2rem;
  }
}
</style>

<template>
  <div>
    <main>
      <section class="hero is-info">
        <div class="hero-body">
          <div class="container">
            <h1 class="title">NDL Ngram Viewer</h1>
            <h2 class="subtitle">国立国会図書館の提供するデジタル化資料のOCR全文テキストデータを利用したNgram Viewer</h2>
          </div>
        </div>
      </section>
      <section class="section">
        <div class="container">
          <div class="notification" v-show="deleteNot">
            <button class="delete" @click="deleteNot=false"></button>
            <div class="content">
              国立国会図書館が提供する<a href="https://lab.ndl.go.jp/data_set/ocr/r3_line/">デジタル化資料のOCRテキスト化事業</a>の成果物である全文テキストを活用した実験サービスです。
              <br />OCRによって作成されたテキストデータから、出版年代ごとの出現頻度を可視化することができます。（可視化対象は、デフォルトでは総出現頻度上位5件です）
              <br />可視化グラフの縦軸は、年代ごとに何回出現したかを表す出現頻度と、出現頻度を出版年代ごとの総ngram数で割った値を表す出現比率の2種類を切り替えることができます。
              <br />2022年12月現在の対象は次の通りです。
              <br />図書雑誌資料約230万点（約17億種類の単語及びフレーズ)
              <br />図書資料約97万点（約8.5億種類の単語及びフレーズ)
              <br />雑誌資料約132万点（約8.9億種類の単語及びフレーズ)
              <br />著作権保護期間満了の図書資料約28万点（約8.3億種類の単語及びフレーズ)
              <br />
              <br />図書については刊行年代が1960年代まで、雑誌については刊行年代が1990年代までの資料を主に対象としています。
              <br />・複数のキーワードをスラッシュ(/)区切りでクエリに指定することで、出現頻度を重ねて表示することができます。
              <br />例：「<a href="./?keyword=モダンガール%2Fモダンボーイ">モダンガール/モダンボーイ</a>」
              <br />
              <br />・正規表現を利用したクエリが可能です。
              <br />例：「<a href="./?keyword=平.盛">平.盛</a>」「<a href="./?keyword=風薫る.*">風薫る.*</a>」「<a href="./?keyword=.*温泉">.*温泉</a>」「<a href="./?keyword=%5B%5Eあ-んア-ン%5D%7B2,2%7D羊羹">[^あ-んア-ン]{2,2}羊羹</a>」
              <br />
              <b-collapse 
                  :open="true" 
                  position="is-bottom" 
                  aria-id="contentIdForA11y4">
                  <template #trigger="props">
                      <a
                          aria-controls="contentIdForA11y4"
                          :aria-expanded="props.open">
                          <b-icon :icon="!props.open ? 'menu-down' : 'menu-up'"></b-icon>
                          利用可能な演算子一覧
                      </a>
                  </template>
                  <p>
                    <table class="table is-half">
                      <thead>
                        <th class="w1">演算子</th>
                        <th class="w3">クエリ例</th>
                        <th class="w6">説明</th>
                      </thead>
                      <tbody>
                        <tr>
                          <td>.</td>
                          <td><a href="./?keyword=修.者" rel="noopener noreferrer">修.者</a></td>
                          <td>任意の1文字を表す</td>
                        </tr>
                        <tr>
                          <td>*</td>
                          <td><a href="./?keyword=日*精進" rel="noopener noreferrer">日*精進</a></td>
                          <td>直前の表現が0個以上あることを表す</td>
                        </tr>
                        <tr>
                          <td>+</td>
                          <td><a href="./?keyword=郵便局%2B長" rel="noopener noreferrer">郵便局+長</a></td>
                          <td>直前の表現が1個以上あることを表す</td>
                        </tr>
                        <tr>
                          <td>?</td>
                          <td><a href="./?keyword=巡査部?長" rel="noopener noreferrer">巡査部?長</a></td>
                          <td>直前の表現が0個か1個あることを表す</td>
                        </tr>
                        <tr>
                          <td>{,}</td>
                          <td><a href="./?keyword=私を.%7B2,3%7Dにする" rel="noopener noreferrer">私を.{2,3}にする</a></td>
                          <td>直前の文字の繰り返し回数の範囲を指定する。<br />例1：{2,4}：2回以上4回以下 <br />例2：{3,} ：3回以上 <br />例3：{,5} ：5回以下</td>
                        </tr>
                        <tr>
                          <td>[…]</td>
                          <td><a href="./?keyword=大正%5Bーア-ン%5D%7B6,%7D"  rel="noopener noreferrer">大正[ーア-ン]{6,}</a></td>
                          <td>ブラケット内の1文字を表す。「-」で範囲を、「^」で否定を表す。<br />例1：[abc]：a,b,cのうち1文字 <br />例2：[ア-ン]：アからンまでの1文字（カタカナいずれか1文字）<br /> 例3：[^ア-ン]：アからンまで以外の1文字（カタカナ以外の1文字）</td>
                        </tr>
                        <tr>
                          <td>(…)</td>
                          <td><a href="./?keyword=春の海ひねもす(のたり)*" rel="noopener noreferrer">春の海ひねもす(のたり)*</a></td>
                          <td>かっこで囲んだ範囲のグループを形成し、単一の文字として扱う。（他の演算子と組み合わせて用いる）</td>
                        </tr>
                        <tr>
                          <td>|</td>
                          <td><a href="./?keyword=ご%28機嫌%7Cきげん%29よう" rel="noopener noreferrer">ご(機嫌|きげん)よう</a></td>
                          <td>左辺または右辺の最長のパターンにマッチすることを表す</td>
                        </tr>
                      </tbody>
                    </table>
                  </p>
              </b-collapse>
              <br />【注意1】クエリにスラッシュ(/)が含まれる場合、正規表現は無効化されます。つまり複数キーワードクエリと正規表現クエリを併用することはできません。
              <br />【注意2】処理の特性により、総出現頻度が10程度以下のものについては集計漏れが発生することがあります。
              <br />【注意3】NDL Ngram Viewerの可視化対象は国立国会図書館デジタルコレクションの全文検索対象とほぼ同じですが、厳密に同じデータを参照するものではありません。クエリによっては差異が発生することがあります。
              <br />【注意4】資料に含まれるキーワードの頻度を可視化するサービスであり、一つの資料に繰り返し同じキーワードが含まれる場合があるため、国立国会図書館デジタルコレクションの検索ヒット件数（資料数）とは多くの場合異なります。
              <br />【注意5】負荷対策のため、接頭及び接尾が両方とも正規表現で記述されたクエリは受け付けません。（受け付けない例：「.*テスト.*」）
              <br />
              <br />【技術情報】全文テキストデータに対して異体字等の丸め処理を行った後、NormalモードのKuromojiで形態素解析を行い、形態素gramで1gramから5gramまでの総出現頻度が4以上の単語及びフレーズを集計しています。
              <br />
              <br />詳細については<a href="https://lab.ndl.go.jp/service/ngramviewer/" target="_blank" rel="noopener noreferrer">https://lab.ndl.go.jp/service/ngramviewer/</a>もご覧ください。
            </div>
          </div>
          <div class="field">
            <section>
              <div class="block">
                <b-radio v-model="radiomtype"
                    native-value="full">
                    図書雑誌(※約230万資料から集計)
                </b-radio>
                <b-radio v-model="radiomtype"
                    native-value="tosho-all">
                    図書のみ(※約97万資料から集計)
                </b-radio>
                <b-radio v-model="radiomtype"
                    native-value="zasshi-all">
                    雑誌のみ(※約132万資料から集計)
                </b-radio>
                <b-radio v-model="radiomtype"
                    native-value="tosho-pdm">
                    著作権保護期間満了図書のみ(※約28万資料から集計)
                </b-radio>
              </div>
            </section>
            <div class="control level-right">
              <input
                class="input is-info"
                type="text"
                v-model="input"
              />
              <b-button
                :disabled="empty"
                icon-left="magnify"
                class="button is-info"
                v-on:click="searchbutton()"
                v-shortkey="['enter']"
                @shortkey="searchbutton()"
                :loading="loading"
              >
                <span>検索</span>
              </b-button>
            </div>
          </div>
          <div v-if="dataload==true&&pagefrom==0">
            <div class="columns">
              <div class="column">
                <b-switch v-model="isRate">
                  <div v-if="isRate">キーワードの出現比率を可視化（出版年代ごとの出現頻度/出版年代ごとの総対象Ngram数）</div>
                  <div v-else>キーワードの出現頻度を可視化</div>
                </b-switch>
              </div>
            </div>
            <div class="columns">
              <div class="column is-two-thirds">
                <b-slider class="is-size-7-touch" :min="1801" :max="2023" v-model="yearRange" type="is-danger" tooltip-always></b-slider>
              </div>
              <div class="column">対象とする出版年代の範囲</div>
            </div>
            <div class="columns">
              <div class="column is-two-thirds">
                <b-slider class="is-size-7-touch" :min="1" :max="10" v-model="visibleValue" tooltip-always lazy></b-slider>
              </div>
              <div class="column">可視化対象の件数（デフォルトは上位5件）</div>
            </div>
            <line-chart :chart-data="datacollection"></line-chart>
          </div>
          <div v-if="result!=null" >
            <div class="level-item is-size-7-touch level-left">{{ result.hit}}件ヒットしました</div>
            <div v-if="groupstr==null">
              <b-button
                label="検索結果を編集する"
                type="is-primary"
                size="is-small"
                @click="isComponentModalActive = true" />
            </div>
            <div v-else>
              <b-button
                label="編集後の検索結果"
                type="is-primary"
                size="is-small" disabled/>
            </div>
            
            <a :href="download()" target="_blank"><b-icon icon="download"></b-icon><span>検索結果の出現頻度をダウンロード（最大10,000件）</span></a>
            <a :href="downloadfrequency()"><b-icon icon="download"></b-icon><span>出版年代ごとの総対象ngram数の情報をダウンロード</span></a>
            <section>
              <div class="block">
                <b-radio v-model="radiomtype"
                    native-value="full">
                    図書雑誌
                </b-radio>
                <b-radio v-model="radiomtype"
                    native-value="tosho-all">
                    図書のみ
                </b-radio>
                <b-radio v-model="radiomtype"
                    native-value="zasshi-all">
                    雑誌のみ
                </b-radio>
                <b-radio v-model="radiomtype"
                    native-value="tosho-pdm">
                    著作権保護期間満了図書のみ
                </b-radio>
              </div>
            </section>
            <b-modal
              v-model="isComponentModalActive"
              has-modal-card
              :destroy-on-hide="true"
              aria-role="dialog"
              aria-label="Example Modal"
              close-button-aria-label="Close"
              scroll="keep"
              aria-modal>
                <search-result-editor :input="input" :result="result" :is-rate="isRate" :visible-value="visibleValue" :year-range="yearRange" v-on:closemodal="closeModal"/>
            </b-modal>
            <nav class="search-nav level level-right">
              <div class="level-right">
                <search-pagination :proppagefrom="pagefrom" :allhits="result.hit" :pagesize="pagesize" :keyword="input"></search-pagination>
                表示件数
                <div class="search-pagesize-listbox">
                  <search-pagesize :proppagesize="pagesize" :keyword="input"></search-pagesize>
                </div>
              </div>
            </nav>
            <table class="table is-fullwidth">
              <thead>
                <th class="w6">キーワード</th>
                <th class="w2">総出現頻度</th>
                <th class="w2"></th>
              </thead>
              <tbody>
                <tr v-for="(vs,i) in result.list" :key="i">
                  <td>{{vs.ngramkeyword}}</td>
                  <td>{{vs.count}}</td>
                  <td><a :href="ddbaseurl+vs.ngramkeyword" target="_blank" rel="noopener noreferrer">国立国会図書館デジタルコレクションで検索</a></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>
    </main>
    <footer class="footer">
      <div class="content has-text-centered">
        <p>
          <span class="link">
            <a href="https://www.ndl.go.jp/">国立国会図書館ホームページ</a>
          </span>
          <span class="link">
            <a href="https://lab.ndl.go.jp/">NDLラボ</a>
          </span>
          <br />
          <span>Copyright © 2022- National Diet Library, Japan. All Rights Reserved.</span>
        </p>
      </div>
    </footer>
  </div>
</template>

<script lang="ts">
import Component from "vue-class-component";
import Vue from "vue";
import { Ngramyear } from "./domain/ngramyear";
import { search,downloadurl,getyearfreq,SearchResult} from "./service/search-service";
import { Watch } from "vue-property-decorator";
import { DialogProgrammatic as Dialog } from 'buefy'
import SearchPagesize from "components/search/search-pagesize/search-pagesize";
import SearchPagination from "components/search/search-pagination/search-pagination";
import SearchResultEditor from "components/search-result-editor/search-result-editor";

import LineChart from './components/chart/LineChart.js'

@Component({
  components: {
    LineChart,
    SearchPagination,
    SearchPagesize,
    SearchResultEditor
  }
})
export default class extends Vue {
  input: string = "";
  groupstr:string = null;
  result: SearchResult<Ngramyear>= null;
  loading: boolean = false;
  deleteNot: boolean = true;
  visibleValue:number=5;
  datacollection:any= null;
  dataload:boolean=false;
  pagesize:number=100;
  pagefrom:number=0;
  isRate:boolean=false;
　isComponentModalActive:boolean=false;
  yearRange:number[]=[1860,2022];
  radiomtype:string="full";
  yearfrequencyjson:any = null;
  ddbaseurl:string=null;
  ddquery1url:string=null;
  ddquery2url:string=null;
  
  @Watch("isRate")
  @Watch("visibleValue")
  @Watch("yearRange")
  fillData () {
    this.$nextTick(() => {
      var datasetsarray:any=[];
      let colorcode:string[]=["#FF2800","#66CCFF","#35A16B","#663300","#9A0079","#FF99A0","#C7B2DE","#B4EBFA","	#EDC58F","#FFD1D1"];
      for(var ii=0;ii<Math.min(this.result.list.length,this.visibleValue);ii++){
        let ngramkeyword=this.result.list[ii].ngramkeyword;
        let jsonobj=JSON.parse(this.result.list[ii].ngramyearjson);
        var dataarray:any=[];
        let labelsarray=Object.keys(jsonobj);
        let countarray=Object.values(jsonobj);
        for(var j=0;j<labelsarray.length;j++){
          if(Number(labelsarray[j])>=this.yearRange[0]&&Number(labelsarray[j])<=this.yearRange[1]){
            if(this.isRate){
              let yearsum=this.yearfrequencyjson[labelsarray[j]];
              dataarray.push({x:new Date(Number(labelsarray[j]),1,1),y:Number(countarray[j])/Number(yearsum)});
            }
            else dataarray.push({x:new Date(Number(labelsarray[j]),1,1),y:countarray[j]});
          }
        }
        dataarray.sort(this.custom_compare).reverse();
        datasetsarray.push({label:ngramkeyword,
                            data:dataarray,
                            backgroundColor: colorcode[ii],
                            borderColor: colorcode[ii],
                            lineTension: 0.2,
                            fill: false});
      }
      this.datacollection = {
        datasets:datasetsarray,
        baseurl:this.ddbaseurl,
        query1url:this.ddquery1url,
        query2url:this.ddquery2url
      }
    });
  }
  closeModal(){
    this.isComponentModalActive=false;
  }
  custom_compare (a,b) {
    // I'm assuming all values are numbers
    return a.x - b.x;
  }
  downloadfrequency(){
    return "https://lab.ndl.go.jp/dataset/ngramviewer/yearfrequency_"+this.radiomtype+".tsv";
  }

  get empty() {
    return !this.input;
  }
  editModeFunc(){
    this.isComponentModalActive = true;
  }
  @Watch("radiomtype")
  async search() {
    if(this.input){
        this.loading = true;
        this.dataload=false;
        
        if(this.groupstr!=null){
          var pushobj:any={query: { keyword: this.input,size:this.pagesize,from:this.pagefrom,materialtype:this.radiomtype,groupstr:this.groupstr}};
        }else{
          var pushobj:any={query: { keyword: this.input,size:this.pagesize,from:this.pagefrom,materialtype:this.radiomtype}};
        }
        this.ddquery1url="&publicationFrom=";
        this.ddquery2url="&publicationTo=";
        if(this.radiomtype==="full"){
          this.ddbaseurl="https://dl.ndl.go.jp/search/searchResult?accessRestrictions=internet&accessRestrictions=ooc&accessRestrictions=inlibrary&collection=A00001&collection=A00002&fullText=true&itemToSearch_facet=fullText&eraType=AD&keyword=";  
        }else if(this.radiomtype==="tosho-all"){
          this.ddbaseurl="https://dl.ndl.go.jp/search/searchResult?accessRestrictions=internet&accessRestrictions=ooc&accessRestrictions=inlibrary&collection=A00001&fullText=true&itemToSearch_facet=fullText&eraType=AD&keyword=";
        }else if(this.radiomtype==="tosho-pdm"){
          this.ddbaseurl="https://dl.ndl.go.jp/search/searchResult?accessRestrictions=internet&collection=A00001&fullText=true&itemToSearch_facet=fullText&eraType=AD&keyword=";
        }else if(this.radiomtype==="zasshi-all"){
          this.ddbaseurl="https://dl.ndl.go.jp/search/searchResult?accessRestrictions=internet&accessRestrictions=ooc&accessRestrictions=inlibrary&collection=A00002&fullText=true&itemToSearch_facet=fullText&eraType=AD&keyword=";
        }
        this.$router.push(pushobj).catch(()=>{});
        try {
          this.result = (await search(encodeURIComponent(this.input),this.pagesize,this.pagefrom,this.radiomtype,this.groupstr)).data;
          this.yearfrequencyjson = (await getyearfreq(this.radiomtype)).data;
          this.fillData ();
          this.dataload=true;
        }catch{
          Dialog.alert('エラーが発生しました。不正なクエリ文字列の可能性があります')
          this.dataload=false;
        }
        this.loading = false;
    }
  }
  download(){
    return downloadurl(encodeURIComponent(this.input),this.radiomtype,this.groupstr);
  }
  searchbutton(){
    if(this.input!=null){
      this.pagefrom=0;
      this.groupstr=null;
      this.search();
    }
  }
  beforeMount() {
    var query = Object.assign({}, this.$route.query);
    if (Array.isArray(query["keyword"])){
      this.input=query["keyword"].join(" ");
    }else{
      this.input=query["keyword"];
    }
    if (Array.isArray(query["groupstr"])){
      this.groupstr=query["groupstr"].join(" ");
    }else{
      this.groupstr=query["groupstr"];
    }
    if(query["size"]){
      if(Array.isArray(query["size"]))this.pagesize=parseInt(query["size"][0]);
      else this.pagesize=parseInt(query["size"]);
    }
    if(query["from"]){
      if(Array.isArray(query["from"]))this.pagefrom=parseInt(query["from"][0]);
      else this.pagefrom=parseInt(query["from"]);
    }
    if(query["materialtype"]){
      if(Array.isArray(query["materialtype"]))this.radiomtype=query["materialtype"][0];
      else this.radiomtype=query["materialtype"];
    }
    console.log(query);
    if(this.input!=null)this.search();
  }
  mounted() {
  }
}
</script>
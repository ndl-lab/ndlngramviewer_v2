import Vue from "vue";
import Component from "vue-class-component";
import { Prop,Watch } from "vue-property-decorator";
import { DialogProgrammatic as Dialog } from 'buefy'
import LineChart from '../chart/LineChart.js'
import yearfrequencyjson from '../../yearfrequency.json'
import {downloadurl} from "../../service/search-service";
@Component({
    name: "SearchResultEditor",
    template: require("./search-result-editor.html"),
    components: {
      LineChart
    }
})
export default class SearchResultEditor extends Vue {
  @Prop()
  result:any;
  @Prop()
  visibleValue:number;
  @Prop()
  yearRange:number[];
  @Prop()
  isRate:boolean;
  @Prop()
  input:string;


  resultkeywordlist:any=[];
  totalingkeywordlist:any=[];
  checkedRows:any=[];
  tableDataKey:number=0;

  resultcolumns:any= [
      {
          field: 'ngramkeyword',
          label: 'キーワード',
      },
      {
          field: 'count',
          label: '総頻度',
      }
  ];
  totalingcolumns:any= [
    {
        field: 'ngramkeyword',
        label: '合算キーワード',
    },
    {
        field: 'count',
        label: '合算後の総頻度',
    }
  ];

  datacollection:any=null;
  @Watch("totalingkeywordlist")
  fillData () {
    this.$nextTick(() => {
      var datasetsarray:any=[];
      let colorcode:string[]=["#FF2800","#66CCFF","#35A16B","#663300","#9A0079","#FF99A0","#C7B2DE","#B4EBFA","	#EDC58F","#FFD1D1"];
      var tmpresultlist:any[]=this.result.list.concat(this.totalingkeywordlist);
      tmpresultlist.sort(this.custom_compare_count).reverse();
      for(var ii=0;ii<Math.min(tmpresultlist.length,this.visibleValue);ii++){
        let ngramkeyword=tmpresultlist[ii].ngramkeyword;
        let jsonobj=JSON.parse(tmpresultlist[ii].ngramyearjson);
        var dataarray:any=[];
        let labelsarray=Object.keys(jsonobj);
        let countarray=Object.values(jsonobj);
        for(var j=0;j<labelsarray.length;j++){
          if(Number(labelsarray[j])>=this.yearRange[0]&&Number(labelsarray[j])<=this.yearRange[1]){
            if(this.isRate){
              let yearsum=yearfrequencyjson[labelsarray[j]];
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
        datasets:datasetsarray
      }
      this.resultkeywordlist=[];
      for(var ii=0;ii<this.result.list.length;ii++){
        var obj=this.result.list[ii];
        obj.idx=ii;
        this.resultkeywordlist.push(obj);
      }
    });
  }
  handleCancel(){
    this.$emit("closemodal");
  }
  totaling(){
    if(this.checkedRows.length>=2){
      var summaryjsonobj:any=JSON.parse(this.checkedRows[0].ngramyearjson);
      var summarycount:number=this.checkedRows[0].count;
      var summaryngramkeyword:string=this.checkedRows[0].ngramkeyword;
      var summaryidxarray:number[]=[this.checkedRows[0].idx];
      for(var ii=1;ii<this.checkedRows.length;ii++){
        summarycount+=this.checkedRows[ii].count;
        summaryngramkeyword+="="+this.checkedRows[ii].ngramkeyword;
        summaryidxarray.push(this.checkedRows[ii].idx);
        var tmpjsonobj:any=JSON.parse(this.checkedRows[ii].ngramyearjson);
        for (let [key, value] of Object.entries(tmpjsonobj)) {
          if (summaryjsonobj[key]) {
            summaryjsonobj[key] += value;
          } else {
            summaryjsonobj[key] = value;
          }
        }
      }
      var summaryobj:any={"ngramkeyword":summaryngramkeyword,"count":summarycount,"idxarray":summaryidxarray,
      "ngramyearjson":JSON.stringify(summaryjsonobj)};
      this.totalingkeywordlist.push(summaryobj);
      this.tableDataKey++;
      this.checkedRows=[];
    }else{
      Dialog.alert('合算したい2つ以上のキーワードを選択してください')
    }
  }
  totalingDeleteRow(rowdata){
    var idx=-1;
    for(var ii=0;ii<this.totalingkeywordlist.length;ii++){
      if(this.totalingkeywordlist[ii].ngramkeyword==rowdata.ngramkeyword)idx=ii;
    }
    this.totalingkeywordlist.splice(idx, 1);
    this.tableDataKey++;
  }
  totalingResult(){
    var grouparray:string[]=[];
    for(var ii=0;ii<this.totalingkeywordlist.length;ii++){
      var summaryidxarray:number[]=this.totalingkeywordlist[ii].idxarray;
      summaryidxarray.sort();
      grouparray.push(summaryidxarray.join("="))
    }
    var groupstr:string=grouparray.join("-").trim();
    var url:string=window.location.href+"&groupstr="+groupstr;
    window.open(url);
  }
  totalingDownload(){
    var grouparray:string[]=[];
    for(var ii=0;ii<this.totalingkeywordlist.length;ii++){
      var summaryidxarray:number[]=this.totalingkeywordlist[ii].idxarray;
      summaryidxarray.sort();
      grouparray.push(summaryidxarray.join("="))
    }
    var groupstr:string=grouparray.join("-").trim();
    window.open(downloadurl(encodeURIComponent(this.input),groupstr));
  }
  mounted(){
    this.fillData ();
  }
  custom_compare (a,b) {
    // I'm assuming all values are numbers
    return a.x - b.x;
  }
  custom_compare_count (a,b) {
    // I'm assuming all values are numbers
    return a.count - b.count;
  }
}

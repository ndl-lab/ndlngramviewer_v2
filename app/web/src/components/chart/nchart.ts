import { Ngramyear } from "domain/ngramyear";
import {search,SearchResult} from "../../service/search-service";
import Vue from "vue";
import {Prop} from "vue-property-decorator";
import Component from "vue-class-component";
import "./nchart.scss";
import LineChart from './LineChart.js'


@Component({
  template: require("./nchart.html"),
  components: {
    LineChart
  }
})
export default class NchartPage extends Vue {
  @Prop()
  ss: SearchResult<Ngramyear>;
  datacollection:any = {};
  dataload:boolean=false;
  fillData () {
    this.$nextTick(() => {
      var datasetsarray:any=[];
      for(var ii=0;ii<Math.min(this.ss.list.length,5);ii++){
        let ngramkeyword=this.ss.list[ii].ngramkeyword;
        let count=this.ss.list[ii].count;
        let jsonobj=JSON.parse(this.ss.list[ii].ngramyearjson);
        var dataarray:any=[];
        let labelsarray=Object.keys(jsonobj);
        let countarray=Object.values(jsonobj);
        for(var j=0;j<labelsarray.length;j++){
          dataarray.push({x:new Date(Number(labelsarray[j]),1,1),y:countarray[j]});
        }
        dataarray.sort(this.custom_compare).reverse();
        let colorcode="#"+Math.floor(Math.random()*16777215).toString(16);
        datasetsarray.push({label:ngramkeyword,
                            data:dataarray,
                            backgroundColor: colorcode,
                            borderColor: colorcode,
                            lineTension: 0,
                            fill: false});
      }
      this.datacollection = {
        datasets:datasetsarray
      }
    });
  }
  custom_compare (a,b) {
    // I'm assuming all values are numbers
    return a.x - b.x;
  }
  clickbutton(){
    this.fillData();
    this.dataload=true;
  }
}

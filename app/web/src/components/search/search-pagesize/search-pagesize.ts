import Vue from "vue";
import Component from "vue-class-component";
import { Prop } from "vue-property-decorator";


@Component({
  name: "SearchPagesize",
  template: require("./search-pagesize.html")
})
export default class SearchPagesize extends Vue {
  @Prop({ default: false })
  manual: boolean;

  @Prop()
  proppagesize: number;
  
  @Prop()
  keyword:string;
  
  pagesize:number;

  innnerSize:number = 0;
  values = ["100", "200", "500"];

  sizeChange(event) {
    this.pagesize=event.target.value;
    var pushobj:any={query: { keyword: this.keyword,size:this.pagesize}};
    this.$router.push(pushobj).catch(()=>{});
    this.$router.go(0);
    console.log(this.pagesize);
  }

  get size() {
    return this.pagesize;
  }

  go() {
    this.pagesize=this.innnerSize;
  }
  mounted(){
    this.pagesize=this.proppagesize;
  }
  set size(s) {
    this.innnerSize = s;
  }

}


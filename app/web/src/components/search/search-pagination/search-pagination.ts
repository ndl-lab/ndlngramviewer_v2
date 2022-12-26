import Vue from "vue";
import Component from "vue-class-component";

import "./search-pagination.scss";
import { Prop,Watch } from "vue-property-decorator";

@Component({
  name: "SearchPagination",
  template: require("./search-pagination.html")
})
export default class SearchPagination extends Vue {
  @Prop({ default: 2 })
  size: number;
  
  @Prop()
  keyword:string;
  
  @Prop()
  pagesize: number;
  
  @Prop()
  proppagefrom: number;
  
  pagefrom: number;
  
  @Prop()
  allhits: number;

  handleScroll() {
    var scrollGrid = document.getElementsByClassName("grid").item(0);
    if (scrollGrid != null) {
      window.addEventListener("scroll", () => {});
    }
  }

  goto(page) {
    if (page < 0) page = 0;
    if (page > this.maxPage) page = this.maxPage;
    this.pagefrom=this.pagesize * (page - 1);
    var pushobj:any={query: { keyword: this.keyword,size:this.pagesize,from:this.pagefrom}};
    console.log(pushobj)
    this.$router.push(pushobj).catch(()=>{});
    this.$router.go(0);
  }

  get page() {
    return Math.floor(this.pagefrom / this.pagesize) + 1;
  }

  get show1() {
    return this.page - this.size > 1;
  }

  get show2() {
    return this.page < this.maxPage - this.size && this.maxPage < 20;
  }

  get pageArray() {
    let arr = [];
    for (let i = this.page - this.size; i < this.page + this.size + 1; i++) {
      if (i > 0 && i <= this.maxPage) arr.push(i);
    }
    console.log(this.page,this.size);
    return arr;
  }
  
  
  created(){
    console.log(this.proppagefrom);
    this.pagefrom=this.proppagefrom;
  }
  get maxPage() {
    return Math.ceil(this.allhits / this.pagesize);
  }
}

import * as Axios from "axios";
import * as Config from "config";
import { Ngramyear } from "domain/ngramyear";
const BASE_URL = Config.BASE_PATH + "api/";
const DATASET_URL="https://lab.ndl.go.jp/dataset/ngramviewer/";

/*export function search(s: string): Axios.AxiosPromise<Ngramyear[]> {
  let fd = new FormData();
  fd.append("bib", s);
  return Axios.default.post<Ngramyear[]>(BASE_URL + "search", fd);
}*/
export interface SearchResult<T> {
  list: T[];
  hit: number;
  from: number;
};

export function search(keywords: string,size:number=null,from:number=null,materialtype:string=null,groupstr: string=null): Axios.AxiosPromise<SearchResult<Ngramyear>> {
  var resstring=BASE_URL + "search?keyword=" +keywords;
  if(size!==null){
    resstring+="&size="+size;
  }
  if(from!==null){
    resstring+="&from="+from;
  }
  if(groupstr!==null){
    resstring+="&groupstr="+groupstr;
  }
  if(materialtype!==null){
    resstring+="&materialtype="+materialtype;
  }
  return Axios.default.get<SearchResult<Ngramyear>>(encodeURI(resstring));
}
export function getyearfreq(materialtype:string){
  var resstring=DATASET_URL + "yearfrequency_" +materialtype+".json";
  return Axios.default.get(encodeURI(resstring));
}
export function downloadurl(keywords: string,materialtype:string=null,groupstr: string=null): string {
  var resstring=BASE_URL + "download?keyword=" +keywords+"&size=10000";
  if(materialtype!==null){
    resstring+="&materialtype="+materialtype;
  }
  if(groupstr!==null){
    resstring+="&groupstr="+groupstr;
  }
  return encodeURI(resstring);
}

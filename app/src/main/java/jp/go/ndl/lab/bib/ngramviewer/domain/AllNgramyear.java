package jp.go.ndl.lab.bib.ngramviewer.domain;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jp.go.ndl.lab.bib.ngramviewer.infra.EsData;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllNgramyear  implements EsData{
	public String id;
    public Long version;
    public String ngramkeyword;
    public String reversekeyword;
    public List<keywordattribute> keywordattributes= new ArrayList<>();
    public AllNgramyear() {
    }
    public AllNgramyear(String keyword) {
        this.ngramkeyword = keyword;
        this.reversekeyword=keyword;
        this.keywordattributes=new ArrayList<>();
    }
    
    public void addAttribute(String materialtype,String ngramyearjson,long count,boolean removecheck) {
    	if(removecheck)this.removeAttribute(materialtype);
    	this.keywordattributes.add(new keywordattribute(materialtype,ngramyearjson,count));
    }
    public void removeAttribute(String materialtype) {
    	ArrayList<keywordattribute>newkwdatrlist=new ArrayList<>();
    	for(keywordattribute kwdatr:this.keywordattributes) {
    		if(!(kwdatr.materialtype.equals(materialtype))){
    			newkwdatrlist.add(kwdatr);
    		}
    	}
    	this.keywordattributes=newkwdatrlist;
    }

    public static class keywordattribute {
    	public String materialtype;
        public String ngramyearjson;
        public long count;
        public keywordattribute() {
        }
        public keywordattribute(String materialtype,String ngramyearjson,long count) {
        	this.materialtype=materialtype;
        	this.ngramyearjson=ngramyearjson;
        	this.count=count;
        }
    }
    
}

package jp.go.ndl.lab.bib.ngramviewer.domain;


import com.fasterxml.jackson.annotation.JsonInclude;

import jp.go.ndl.lab.bib.ngramviewer.infra.EsData;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ngramyear  implements EsData{
	public String id;
    public Long version;
    
    public Ngramyear(String keyword, String reversekeyword,long count, String ngramyearjson,boolean confident) {
        this.ngramkeyword = keyword;
        this.reversekeyword=reversekeyword;
        this.ngramyearjson=ngramyearjson;
        this.count = count;
        this.confident=confident;
    }
    public Ngramyear(String keyword, String reversekeyword,long count, String ngramyearjson) {
        this.ngramkeyword = keyword;
        this.reversekeyword=reversekeyword;
        this.ngramyearjson=ngramyearjson;
        this.count = count;
        this.confident=true;
    }
    public Ngramyear(String keyword,String reversekeyword, long count) {
        this.ngramkeyword = keyword;
        this.reversekeyword=reversekeyword;
        this.ngramyearjson= "";
        this.count = count;
    }
    public Ngramyear() {
    }

    public String ngramkeyword;
    public String reversekeyword;
    public String ngramyearjson;
    public long count;
    public boolean confident;
}

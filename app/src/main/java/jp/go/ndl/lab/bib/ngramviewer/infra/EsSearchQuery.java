package jp.go.ndl.lab.bib.ngramviewer.infra;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RegexpFlag;
import org.elasticsearch.index.query.RegexpQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.MultiValueMap;
import com.vladsch.ReverseRegEx.util.RegExPattern;
import com.vladsch.ReverseRegEx.util.ReversePattern;


@Data
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EsSearchQuery {
    public Integer from;
    public Integer size;
    public Boolean enableregex;
    public Boolean reverseregex;
    public String rawkeyword;
    
    
    public List<ArrayList<Integer>> groupid;
    public List<String> keyword;
    public List<String> sort;
    public List<String> materialtypelist;
    public List<String> accessinfolist;
    
    

    @JsonIgnore
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(keyword);
    }
    public static EsSearchQuery readQuery(MultiValueMap<String, String> query) {
        EsSearchQuery esq = new EsSearchQuery();
        esq.from = NumberUtils.toInt(query.getFirst("from"));
        esq.size = NumberUtils.toInt(query.getFirst("size"));
        if (esq.size == 0) esq.size = 100;
        esq.sort = query.get("sort");
        List<String>keywordlist=new ArrayList<>();
        String rawkeywords=query.getFirst("keyword");
        String rawgroupstr=query.getFirst("groupstr");
        esq.materialtypelist=query.get("materialtype");
        
        try {
        	URLCodec codec = new URLCodec("UTF-8");
			rawkeywords=codec.decode(rawkeywords, "UTF-8");
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //log.info("{}",rawkeywords);
        rawkeywords=rawkeywords.replaceAll("\\s+","");
        esq.rawkeyword=rawkeywords;
        esq.enableregex=true;
        esq.reverseregex=false;
        if(rawkeywords.contains("/")) {
        	esq.enableregex=false;
        	keywordlist=Arrays.asList(rawkeywords.split("[/]"));
        }else {
        	final String[] reval={".","?","+","*","|","{","}","[","]","(",")"};//,"\"" ,"\\"};
        	int cnt=0;
	    	for(int i=0;i<rawkeywords.length();i++) {
	    		for(int j = 0; j< reval.length; j++){
	    			if(rawkeywords.substring(i, i+1).equals(reval[j])) {
	    				if(i==0)esq.reverseregex=true;
	    				cnt++;//break;
	    			}
	    		}
	    	}
	    	if(cnt==rawkeywords.length()) {
	    		esq.enableregex=false;
	    	}else if(esq.reverseregex){
	    		RegExPattern p = ReversePattern.compile(rawkeywords);
	    		rawkeywords=p.pattern();
	    		for(int j = 0; j< reval.length; j++){
	    			if(rawkeywords.startsWith(reval[j]))esq.enableregex=false;
	    		}
	    	}
        	keywordlist.add(rawkeywords);
        }
        esq.keyword = keywordlist;
        esq.groupid=new ArrayList<>(); 
        try {
	        if(rawgroupstr!=null) {
	        	for(String grp1 :rawgroupstr.split("-")) {
	        		ArrayList<Integer> grplist=new ArrayList<Integer>();
	        		for(String grp2 :grp1.split("=")) {
	        			grplist.add(Integer.parseInt(grp2));
	        		}
	        		if(grplist.size()>0)esq.groupid.add(grplist);
	        	}
	        }
        } catch (Exception e) {
			e.printStackTrace();
		}
        return esq;
    }

    public SearchSourceBuilder createSearchSource(QueryBuilder... additionalQueries) {
        SearchSourceBuilder ssb = new SearchSourceBuilder();
        ssb.from(from == null ? 0 : from);
        ssb.size(size == null ? 100 : size);
        BoolQueryBuilder base = QueryBuilders.boolQuery();
        ssb.query(base);
        if (!CollectionUtils.isEmpty(keyword)) {
            BoolQueryBuilder kwq = EsSearchQuery.createKeywordBoolQuery(keyword, Arrays.asList("keywords"));
            base.must(kwq);
        }
        ssb.sort("count",SortOrder.DESC);
        return ssb;
    }

    public static BoolQueryBuilder createKeywordBoolQuery(List<String> keywords, List<String> keywordFields) {
        BoolQueryBuilder keywordsBq = QueryBuilders.boolQuery();
        if (!CollectionUtils.isEmpty(keywords) && !CollectionUtils.isEmpty(keywordFields)) {
            for (String keyword : keywords) {
                //BoolQueryBuilder keywordBq = QueryBuilders.boolQuery();
                if (!StringUtils.isBlank(keyword)) {
                    for (String keywordField : keywordFields) {
                        MatchPhraseQueryBuilder mqb = QueryBuilders.matchPhraseQuery(keywordField, keyword).analyzer("keyword");
                        //keywordBq.should(mqb);
                        keywordsBq.should(mqb);
                    }
                }
            }
        }
        return keywordsBq;
    }
    public static BoolQueryBuilder createKeywordRegexQuery(List<String> keywords, List<String> keywordFields) {
        BoolQueryBuilder keywordsBq = QueryBuilders.boolQuery();
        if (!CollectionUtils.isEmpty(keywords) && !CollectionUtils.isEmpty(keywordFields)) {
            for (String keyword : keywords) {
                //BoolQueryBuilder keywordBq = QueryBuilders.boolQuery();
                if (!StringUtils.isBlank(keyword)) {
                    for (String keywordField : keywordFields) {
                        RegexpQueryBuilder mqb = QueryBuilders.regexpQuery(keywordField, keyword).flags(RegexpFlag.NONE);
                        //keywordBq.should(mqb);
                        keywordsBq.should(mqb);
                    }
                }
            }
        }
        return keywordsBq;
    }

    public SearchSourceBuilder createSearchSource() {
        return this.createSearchSource(null);
    }

}

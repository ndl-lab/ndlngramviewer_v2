package jp.go.ndl.lab.bib.ngramviewer.service;


import jp.go.ndl.lab.bib.ngramviewer.domain.AllNgramyear;
import jp.go.ndl.lab.bib.ngramviewer.domain.Ngramyear;
import jp.go.ndl.lab.bib.ngramviewer.domain.AllNgramyear.keywordattribute;
import jp.go.ndl.lab.bib.ngramviewer.infra.EsDataStore;
import jp.go.ndl.lab.bib.ngramviewer.infra.EsDataStoreFactory;
import jp.go.ndl.lab.bib.ngramviewer.infra.EsSearchQuery;
import jp.go.ndl.lab.bib.ngramviewer.infra.EsSearchResult;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.NestedSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortMode;
import org.elasticsearch.search.sort.SortOrder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class NgramService {
	public final EsDataStore<AllNgramyear> ngramStore;
    public NgramService(EsDataStoreFactory  storeFactory) {
    	ngramStore = storeFactory.build(AllNgramyear.class);
    }
    @Autowired
    AnalyzeService as;
    
    public EsSearchResult<Ngramyear> summarizeDocs( EsSearchResult<AllNgramyear> allResult){
    	ObjectMapper mapper = new ObjectMapper();
    	EsSearchResult<Ngramyear> baseResult=new EsSearchResult<Ngramyear>();
    	ArrayList<Ngramyear>reslist=new ArrayList<Ngramyear>();
    	//for(AllNgramyear allngram:(ArrayList<AllNgramyear>) allResult.list) {
    	for(int index=0;index<allResult.list.size();index++) {
    		AllNgramyear allngram=allResult.list.get(index);
    		String ngramkeyword=allngram.ngramkeyword;
    		List<keywordattribute> kwdatrlist=allResult.innerlist.get(index);
    		
    		TreeMap<Integer, Long>mergemap=new TreeMap<Integer, Long>();
    		long mergecount=0;
    		for(keywordattribute kwdatr:kwdatrlist) {
    			mergecount+=kwdatr.count;
    			try {
					TreeMap<Integer, Long> tmpmap= mapper.readValue(kwdatr.ngramyearjson, new TypeReference<TreeMap<Integer, Long>>(){});
					tmpmap.forEach((k, v) -> mergemap.merge(k, v, (v1, v2) -> v1 + v2));
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
    		}
    		JSONObject ngramyearjson =  new JSONObject(mergemap);
    		Ngramyear mergeng=new Ngramyear(ngramkeyword,ngramkeyword,mergecount,ngramyearjson.toString());
    		reslist.add(mergeng);
    	}
    	 baseResult.hit=allResult.hit;
	     baseResult.list=reslist;
	     return baseResult;
    }
    public EsSearchResult<AllNgramyear> searchwithquery(EsSearchQuery q) {
    	SearchSourceBuilder ssb = new SearchSourceBuilder();
    	ssb.from(q.from == null ? 0 : q.from);
        ssb.size(q.size == null ? 200 : q.size);
        
        //System.out.println(q.rawkeyword);
        for(int i=0;i<q.keyword.size();i++) {
        	q.keyword.set(i, as.normalizeString(q.keyword.get(i)));
        }
        BoolQueryBuilder baseq=new BoolQueryBuilder();
        BoolQueryBuilder kwq;
        if(q.enableregex) {
        	if(q.reverseregex) {
        		kwq= EsSearchQuery.createKeywordRegexQuery(q.keyword, Arrays.asList("reversekeyword"));
        	}else {
        		kwq= EsSearchQuery.createKeywordRegexQuery(q.keyword, Arrays.asList("ngramkeyword"));
        	}
        }else {
        	kwq= EsSearchQuery.createKeywordBoolQuery(q.keyword, Arrays.asList("ngramkeyword"));
        }
        baseq.must(kwq);
        BoolQueryBuilder  filterkwq;
        if(q.materialtypelist==null||q.materialtypelist.contains("full")) {
        	filterkwq=EsSearchQuery.createKeywordBoolQuery(Arrays.asList("tosho-all", "zasshi-all"),Arrays.asList("keywordattributes.materialtype"));
        }else{
        	filterkwq=EsSearchQuery.createKeywordBoolQuery(q.materialtypelist,Arrays.asList("keywordattributes.materialtype"));
        }
        NestedQueryBuilder nestedfilter= QueryBuilders.nestedQuery("keywordattributes", filterkwq,org.apache.lucene.search.join.ScoreMode.None).innerHit(new InnerHitBuilder()); 
        baseq.must(nestedfilter);
        
        String[] includes = new String[]{"*"};
        String[] excludes = new String[]{"keywordattributes","reversekeyword"};
        ssb.fetchSource(includes, excludes);
        ssb.query(baseq);
        SortBuilder sortbuilder=SortBuilders.fieldSort("keywordattributes.count")
        		.setNestedSort(new NestedSortBuilder("keywordattributes").setFilter(filterkwq)).sortMode(SortMode.SUM).order(SortOrder.DESC);
        ssb.sort(sortbuilder);
        //ssb.sort(new FieldSortBuilder("keywordattributes.count").setNestedSort(new NestedSortBuilder("keywordattributes").setFilter(filterkwq)).sortMode(SortMode.SUM).order(SortOrder.DESC));
        System.out.println(ssb.toString());
        EsSearchResult<AllNgramyear> result=ngramStore.search(ssb);
        return result;
    }
    
    
    
    public EsSearchResult<Ngramyear> search(EsSearchQuery q) {
        ObjectMapper mapper = new ObjectMapper();
        EsSearchResult<AllNgramyear> allResult=searchwithquery(q);
        EsSearchResult<Ngramyear> baseResult=summarizeDocs(allResult);
        if(q.groupid.size()>0) {
        	ArrayList<Ngramyear> tmphitlist=(ArrayList<Ngramyear>) baseResult.list;	
	        for(ArrayList<Integer>group:q.groupid) {
	        	ArrayList<String> mergekeywordlist=new ArrayList<String>();
	        	long mergecount=0;
	        	TreeMap<Integer,Long>mergemap=new TreeMap<Integer, Long>();
	        	for(int idx: group) {
	        		if(baseResult.list.size()>idx) {
	        			Ngramyear mergetarget=baseResult.list.get(idx);
	        			mergekeywordlist.add(mergetarget.ngramkeyword);
	        			try {
							TreeMap<Integer, Long> tmpmap= mapper.readValue(mergetarget.ngramyearjson, new TypeReference<TreeMap<Integer, Long>>(){});
							tmpmap.forEach((k, v) -> mergemap.merge(k, v, (v1, v2) -> v1 + v2));
						} catch (JsonMappingException e) {
							e.printStackTrace();
						} catch (JsonProcessingException e) {
							e.printStackTrace();
						}
	        			mergecount+=mergetarget.count;
	        		}
	        	}
	        	if(mergekeywordlist.size()>0) {
	        		String mergekeyword= String.join("=",mergekeywordlist);
	        		JSONObject ngramyearjson =  new JSONObject(mergemap);
	        		Ngramyear mergeng=new Ngramyear(mergekeyword,mergekeyword,mergecount,ngramyearjson.toString());
	        		tmphitlist.add(mergeng);
	        	}
	        }
	        Collections.sort( tmphitlist, new Comparator<Ngramyear>(){
	            @Override
	            public int compare(Ngramyear a, Ngramyear b){
	              return Long.compare(a.count ,b.count);
	            }
	        });
	        baseResult.hit=tmphitlist.size();
	        baseResult.list=tmphitlist;
        }
        return baseResult;
    }
}

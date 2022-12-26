package jp.go.ndl.lab.bib.ngramviewer.batch;

import jp.go.ndl.lab.bib.ngramviewer.Application;
import jp.go.ndl.lab.bib.ngramviewer.domain.AllNgramyear;
import jp.go.ndl.lab.bib.ngramviewer.infra.EsBulkIndexer;
import jp.go.ndl.lab.bib.ngramviewer.service.NgramService;
import jp.go.ndl.lab.common.utils.IDUtils;
import jp.go.ndl.lab.common.utils.LabFileUtils;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map.Entry;

@Component(IndexFromGzipBatch.BATCH_NAME)
@Profile({Application.MODE_BATCH})
@Slf4j
@Lazy
public class IndexFromGzipBatch extends AbstractBatch{
	public static final String BATCH_NAME = "index-gzip";
	public static void main(String[] args) throws Throwable {
        Application.main(Application.MODE_BATCH, IndexFromGzipBatch.BATCH_NAME);
    }
	@Autowired
    private NgramService ns;
	
	private static final int BULK_INDEX_SIZE = 100000;
	
	@Override
	public void run(String[] params) {
		ObjectMapper mapper = new ObjectMapper();
		Path gzippath= Paths.get(params[0]);
		int skipcount=(params.length >= 2)?Integer.parseInt(params[1]):0;
		int cnt=0;
		try (EsBulkIndexer ngramIndexer = new EsBulkIndexer(ns.ngramStore, BULK_INDEX_SIZE)) {
			try (LabFileUtils.DataReader reader = LabFileUtils.gDataReader(gzippath)) {
	            for (String[] data : reader) {
	            	cnt++;
		            if(cnt%1000000==0) {
		            	log.info("count: {}",cnt);
		            }
		            if(cnt<skipcount) {
		            	continue;
		            }
	                String json = data[0].replace("\'", "\"");
	                
	                Map<String, Object> kvp = null;
	                try {
	                	kvp=mapper.readValue(json, new TypeReference<Map<String,Object>>(){});
	            	} catch (Exception e) {
	            		e.printStackTrace();
	            	}
	                String keyString= (String) kvp.get("keyword");
	                Object kwdtrobj=kvp.get("keywordattribute");
	                if(kwdtrobj==null)continue;
	                List<Map<String,Object>>valuearray= new ArrayList<Map<String,Object>>();
	                try {
	                	if (kwdtrobj.getClass().isArray()) {
	                		valuearray= Arrays.asList((Map<String,Object>[])kwdtrobj);
	                    } else if (kwdtrobj instanceof Collection) {
	                    	valuearray= new ArrayList<>((Collection<Map<String,Object>>)kwdtrobj);
	                    }
	                	//valuearray=mapper.readValue(kwdtrobj, new TypeReference<ArrayList<Map<String, Object>>>(){});
	            	} catch (Exception e) {
	            		e.printStackTrace();
	            	}
	                //if(kvp.size()==1||keyString.length()>15)continue;
	                String hashid=IDUtils.md5HashId(keyString);
		            String ngramkeyword=keyString;
		            AllNgramyear allnyear=new AllNgramyear(ngramkeyword);
		            for (Map<String, Object> entrymap :valuearray) {
		            	String materialtype=(String) entrymap.get("materialtype");
		            	int count=(int) entrymap.get("count");
		            	String ngramyearjson=(String) entrymap.get("ngramyearjson");
		            	allnyear.addAttribute(materialtype, ngramyearjson, count,false);
		            }
		           
		            ngramIndexer.add(hashid, mapper.writeValueAsString(allnyear));

	            }
			}
        } catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
	}
}

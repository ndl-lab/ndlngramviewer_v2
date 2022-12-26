package jp.go.ndl.lab.bib.ngramviewer.controller;

import jp.go.ndl.lab.bib.ngramviewer.Application;


import jp.go.ndl.lab.bib.ngramviewer.infra.EsSearchQuery;
import jp.go.ndl.lab.bib.ngramviewer.infra.EsSearchResult;
import jp.go.ndl.lab.bib.ngramviewer.domain.Ngramyear;
import jp.go.ndl.lab.bib.ngramviewer.service.NgramService;
import jp.go.ndl.lab.common.utils.RESTUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;



@Controller
@RequestMapping("/api")
@Profile(Application.MODE_WEB)
public class NgramController {

    @Autowired
    private NgramService estimateService;
    
    private final int beginyear=1801;
    private final int endyear=2022;

    @ResponseBody
    @GetMapping("/search")
    @CrossOrigin
    public EsSearchResult<Ngramyear> search(@RequestParam MultiValueMap<String, String> query) {
        return estimateService.search(EsSearchQuery.readQuery(query));
    }
    @PostMapping("/search")
    @CrossOrigin
    public EsSearchResult<Ngramyear> searchpost(@RequestParam MultiValueMap<String, String> query) {
        return estimateService.search(EsSearchQuery.readQuery(query));
    }
    
    @GetMapping("/downloadjson")
    @CrossOrigin
    public ResponseEntity<StreamingResponseBody> downloadjson(@RequestParam MultiValueMap<String, String> query) {
    	EsSearchQuery q=EsSearchQuery.readQuery(query);
    	EsSearchResult<Ngramyear> searchresult=estimateService.search(q);
    	return RESTUtils.streamDownloadResponse("searchresult.tsv",new StreamingResponseBody() {
			@Override
            public void writeTo(OutputStream outputStream) throws IOException {
                IOUtils.write("Keyword\tTotal frequency\tDetails(json)\n",outputStream,"UTF-8");
				for(Ngramyear ng:searchresult.list) {
					String steamToString = ng.ngramkeyword+"\t"+ng.count+"\t"+ng.ngramyearjson+"\n";
	                IOUtils.write(steamToString,outputStream,"UTF-8");
				}
			}
		});
    }
    @GetMapping("/download")
    @CrossOrigin
    public ResponseEntity<StreamingResponseBody> download(@RequestParam MultiValueMap<String, String> query) {
    	EsSearchQuery q=EsSearchQuery.readQuery(query);
    	EsSearchResult<Ngramyear> searchresult=estimateService.search(q);
    	return RESTUtils.streamDownloadResponse("searchresult.tsv",new StreamingResponseBody() {
			@Override
            public void writeTo(OutputStream outputStream) throws IOException {
				ObjectMapper mapper = new ObjectMapper();
				String headerstring="Keyword\tTotal_Frequency\t";
				List<String> headerList = new ArrayList<String>();
				for(int y=beginyear;y<=endyear;y++) headerList.add(String.valueOf(y));
				headerstring+= String.join("\t", headerList)+"\n";
                IOUtils.write(headerstring,outputStream,"UTF-8");
				for(Ngramyear ng:searchresult.list) {
					String contentstring = ng.ngramkeyword+"\t"+ng.count+"\t";
					Map<Integer, Integer> map= mapper.readValue(ng.ngramyearjson, new TypeReference<Map<Integer, Integer>>(){});
					List<String> contentList = new ArrayList<String>();
					for(int y=beginyear;y<=endyear;y++) {
						if(map.containsKey(y)) {
							contentList.add(String.valueOf(map.get(y)));
						}else {
							contentList.add("0");
						}
					}
					contentstring+= String.join("\t", contentList)+"\n";
	                IOUtils.write(contentstring,outputStream,"UTF-8");
				}
			}
		});
    }
    @PostMapping("/download")
    @CrossOrigin
    public ResponseEntity<StreamingResponseBody> downloadpost(@RequestParam MultiValueMap<String, String> query) {
    	EsSearchQuery q=EsSearchQuery.readQuery(query);
    	EsSearchResult<Ngramyear> searchresult=estimateService.search(q);
    	return RESTUtils.streamDownloadResponse("searchresult.tsv",new StreamingResponseBody() {
			@Override
            public void writeTo(OutputStream outputStream) throws IOException {
                IOUtils.write("Keyword\tTotal frequency\tDetails(json)\n",outputStream,"UTF-8");
				for(Ngramyear ng:searchresult.list) {
					String steamToString = ng.ngramkeyword+"\t"+ng.count+"\t"+ng.ngramyearjson+"\n";
	                IOUtils.write(steamToString,outputStream,"UTF-8");
				}
			}
		});
        //return estimateService.search(EsSearchQuery.readQuery(query));
    }
}

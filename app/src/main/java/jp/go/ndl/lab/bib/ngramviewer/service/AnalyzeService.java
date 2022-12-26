package jp.go.ndl.lab.bib.ngramviewer.service;

import java.util.List;

import org.elasticsearch.client.indices.AnalyzeRequest;
import org.springframework.stereotype.Service;

import jp.go.ndl.lab.bib.ngramviewer.domain.AllNgramyear;
import jp.go.ndl.lab.bib.ngramviewer.infra.EsDataStore;
import jp.go.ndl.lab.bib.ngramviewer.infra.EsDataStoreFactory;

@Service
public class AnalyzeService {
	public final EsDataStore<AllNgramyear> ngramyearStore;
	public AnalyzeService(EsDataStoreFactory storeFactory) {
		 	ngramyearStore= storeFactory.build(AllNgramyear.class);
    }
	 public String normalizeString(String querystr) {
			AnalyzeRequest request = AnalyzeRequest.buildCustomAnalyzer("ng-ngramnormalize","whitespace")
					.addCharFilter("normalize_char_filter") 
	    		    .build(querystr);
			List<String> response=ngramyearStore.normalize(request);
	    	return response.get(0);
	 }
}

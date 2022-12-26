package jp.go.ndl.lab.bib.ngramviewer.infra;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EsDataStoreFactory {

    private String host;
    private int port;
    private String path;
    private String scheme;
    public EsDataStoreFactory(@Value("${es.endPoint}")String endPoint) {
    	URI uri;
		try {
			uri = new URI(endPoint);
			 this.host =uri.getHost() ;
		     this.port = uri.getPort();
		     this.scheme= uri.getScheme();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
    }

    public EsDataStore build(Class clazz) {
        return new EsDataStore(host, port, path,scheme, "ng-" + clazz.getSimpleName().toLowerCase(), clazz);
    }

}

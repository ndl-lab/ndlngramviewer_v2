package jp.go.ndl.lab.bib.ngramviewer.batch;

import jp.go.ndl.lab.bib.ngramviewer.Application;
import jp.go.ndl.lab.common.utils.LabFileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map.Entry;

@Component(yearcountFromGzipBatch.BATCH_NAME)
@Profile({Application.MODE_BATCH})
@Slf4j
@Lazy
public class yearcountFromGzipBatch extends AbstractBatch{
	public static final String BATCH_NAME = "count-gzip";
	public static void main(String[] args) throws Throwable {
        Application.main(Application.MODE_BATCH, yearcountFromGzipBatch.BATCH_NAME);
    }
	
	@Override
	public void run(String[] params) {
		ObjectMapper mapper = new ObjectMapper();
		Path gzippath= Paths.get(params[0]);
		Path outputgzippath=Paths.get(params[1]);
		int cnt=0;
		Map<Long, Long> yearsummap = new HashMap<Long, Long>();
		try (LabFileUtils.Writer writer = LabFileUtils.gWriter(outputgzippath)) {
			try (LabFileUtils.DataReader reader = LabFileUtils.gDataReader(gzippath)) {
	            for (String[] data : reader) {
	            	cnt++;
	            	String keyString = data[0];
	                String json = data[2].replace("\'", "\"");
	                Map<Integer, Integer> kvp = null;
	                try {
	                	kvp=mapper.readValue(json, new TypeReference<Map<Integer, Integer>>(){});
	            	} catch (Exception e) {
	            		e.printStackTrace();
	            	}
	                //if(kvp.size()==1||keyString.length()>15)continue;
		            Map<Long, Long> yearmap = new TreeMap<Long, Long>();
		            long sum=0;
		            for (Entry<Integer, Integer> entry :kvp.entrySet()) {
		            	long yearval=entry.getKey();
		            	long intval=entry.getValue();
		            	yearmap.put(yearval, intval);
		            	sum+=intval;
		            }
		            if(sum<=3)continue;
		            if(cnt%1000000==0) {
		            	log.info("count: {}",cnt);
		            }
		            //writer.writeLine(keyString+"\t"+sum+"\t"+json);
		            yearmap.forEach((k, v) -> yearsummap.merge(k, v, (v1, v2) -> v1 + v2));
	            }
			}catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			try {
				log.info("length: {}",yearsummap.size());
				log.info(mapper.writeValueAsString(yearsummap));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

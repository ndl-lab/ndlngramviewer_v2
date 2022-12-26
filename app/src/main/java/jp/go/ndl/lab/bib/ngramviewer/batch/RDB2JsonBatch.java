package jp.go.ndl.lab.bib.ngramviewer.batch;

import jp.go.ndl.lab.bib.ngramviewer.Application;
import jp.go.ndl.lab.common.utils.LabFileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.whitbeck.rdbparser.*;

@Component(RDB2JsonBatch.BATCH_NAME)
@Profile({Application.MODE_BATCH})
@Slf4j
@Lazy
public class RDB2JsonBatch extends AbstractBatch{
	public static final String BATCH_NAME = "rdb2json";
	public static void main(String[] args) throws Throwable {
        Application.main(Application.MODE_BATCH, RDB2JsonBatch.BATCH_NAME);
    }
	
	@Override
	public void run(String[] params) {
		ObjectMapper mapper = new ObjectMapper();
		Path rdbpath= Paths.get(params[0]);
		Path outputgzpath= Paths.get(params[1]);
		int skipthresh=(params.length >= 3)?Integer.parseInt(params[2]):4;
		Map<Long, Long> yearsummap = new HashMap<Long, Long>();
		int gramcnt=0;
		int  cntngram=0;
		try (LabFileUtils.Writer writer = LabFileUtils.gWriter(outputgzpath)) {
			try (RdbParser parser = new RdbParser(rdbpath)) {
			      Entry e;
			      while ((e = parser.readNext()) != null) {
				    	  switch (e.getType()) {
						          case SELECT_DB:
							            System.out.println("Processing DB: " + ((SelectDb)e).getId());
							            System.out.println("------------");
							            break;
						          case EOF:
							            System.out.print("End of file. Checksum: ");
							            for (byte b : ((Eof)e).getChecksum()) {
							            	System.out.print(String.format("%02x", b & 0xff));
							            }
							            System.out.println();
							            System.out.println("------------");
							            break;
						          case KEY_VALUE_PAIR:
							            KeyValuePair kvp = (KeyValuePair)e;
							            gramcnt+=1;
						        	  	if(gramcnt%1000000==0) System.out.println("Now:"+gramcnt);
							            String keyString=new String(kvp.getKey(), Charset.forName("UTF-8"));
							            Map<Long, Long> yearmap = new TreeMap<Long, Long>();
							            int cnt=0;
							            long yearval=0;
							            int sum=0;
							            for (byte[] val : kvp.getValues()) {
							            	String stval=new String(val, Charset.forName("UTF-8"));
							            	long intval=Integer.parseInt(stval);
							            	if(cnt%2==0) {
							            		yearval=intval;
							            	}else {
							            		sum+=intval;
							            		yearmap.put(yearval, intval);
							            	}
							            	cnt++;
							            }
							            if(yearmap.size()==1)break;
							            JSONObject ngramyearjson =  new JSONObject(yearmap);
							            if(sum<skipthresh)break;
							            writer.writeLine(keyString+"\t"+sum+"\t"+ngramyearjson);
							            cntngram+=1;
							            yearmap.forEach((k, v) -> yearsummap.merge(k, v, (v1, v2) -> v1 + v2));
							            break;
							      default:
							    	  System.out.println("Not Found:"+e.getType());
						    }
			      }
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				log.info("length: {} {} {}",yearsummap.size(),gramcnt,cntngram);
				log.info(mapper.writeValueAsString(yearsummap));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
	}
}

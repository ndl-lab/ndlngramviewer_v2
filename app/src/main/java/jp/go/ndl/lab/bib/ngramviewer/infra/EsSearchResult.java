package jp.go.ndl.lab.bib.ngramviewer.infra;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import jp.go.ndl.lab.bib.ngramviewer.domain.AllNgramyear.keywordattribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.elasticsearch.action.search.SearchResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EsSearchResult<E> implements Iterable<E> {

    public EsSearchResult() {
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    public List<E> list = new ArrayList<>();
    public List<List<keywordattribute>> innerlist = new ArrayList<List<keywordattribute>>();
    public long hit;
    public int from;

    @JsonIgnore
    public SearchResponse searchResponse;

}

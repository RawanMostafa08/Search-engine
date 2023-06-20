package dev.SEProject.search_engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QueryService {
    @Autowired
    private QueryRepository queryRepository;
    public List<Query> allQueries() {
        return queryRepository.findAll();
    }
    public void InsertQuery(Query query) {
        queryRepository.insert(query);
    }
    public boolean CheckQuery(String key) {
        Optional<Query> res = queryRepository.findQueryByKey(key);
        if(res.isPresent()==false)
            return true;
        else
            return false;
    }
}

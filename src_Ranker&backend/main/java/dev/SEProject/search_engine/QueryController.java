package dev.SEProject.search_engine;

import com.mongodb.MongoWriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@SpringBootApplication
@RestController
public class QueryController {

    @Autowired
    private QueryService queryService;

    @GetMapping("/resultQuery")
    public ResponseEntity<List<Query>> getAllQueries() {
        return new ResponseEntity<List<Query>>(queryService.allQueries(), HttpStatus.OK);
    }

    @PostMapping("/query")
    public void handlePostRequest(@RequestBody String requestBody) {
        Query query = new Query();
        requestBody = requestBody.replaceAll("[+\\-\\*\\@\\!\\#\\&\\?\\=\\|]", " ");
        requestBody = requestBody.replaceAll("^\"|\"$", "");
        query.key = requestBody;
        boolean res = queryService.CheckQuery(requestBody);
        if(res==true)
            queryService.InsertQuery(query);
    }
}




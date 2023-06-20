
import com.mongodb.client.*;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.util.*;

public class PopularityRanking {
    public MongoClient mongoClient;
    public MongoDatabase database;
    public static MongoCollection<Document> collection;
    public static HashMap<String, List<String>> urlMap;

    PopularityRanking() {
        urlMap = new HashMap<>();
        mongoClient = MongoClients.create("mongodb+srv://nouraymanh:3tAM0HS52jDjQ5wQ@cluster0.skdggct.mongodb.net");

        database = mongoClient.getDatabase("search-engine-db");

        collection = database.getCollection("crawler");
        getURLs();
    }

    public void getURLs() {
//        List<Integer>keys=new ArrayList<>();
        Map<String, List<Integer>> keys = new HashMap<>();
        MongoCursor<Document> cursor = collection.find().iterator();
        try {

            while (cursor.hasNext()) {
                org.bson.Document document = cursor.next();
                // Do something with the retrieved document
                urlMap.put((String) document.get("url"), (List<String>) document.get("links"));

            }
        } finally {
            cursor.close();
        }
        for (String url : urlMap.keySet()) {

            List<String> dummy = new ArrayList<>();
            for (String linkedUrl : urlMap.get(url)) {
                if (urlMap.containsKey(linkedUrl)) {
                    dummy.add(linkedUrl);
                }
            }
            urlMap.put(url, dummy);
        }

//        for (String url : keys.keySet()) {
////
//            for (Integer linkedUrlindx : keys.get(url)) {
//                urlMap.get(url).remove(linkedUrlindx);
//            }
//        }
        System.out.println("lol");
    }


    private static final double DAMPING_FACTOR = 0.85;
    private static final double EPSILON = 0.0001;

    public static void Rank() {
        HashMap<String, Double> pageRankMap = new HashMap<>();

        // Initialize the PageRank scores for each URL to 1/N
        int numUrls = urlMap.size();
        for (String url : urlMap.keySet()) {
            pageRankMap.put(url, 1.0 / numUrls);
        }

        // Iterate and update PageRank scores until convergence
        boolean converged = false;
        int count = 20;
        while (count > 0) {
            HashMap<String, Double> newPageRankMap = new HashMap<>();

            for (String url : urlMap.keySet()) {
                double score = (1 - DAMPING_FACTOR) / numUrls;
                List<String> linkedUrls = urlMap.get(url);

                if (linkedUrls == null || linkedUrls.isEmpty()) {
                    // If a URL has no children, distribute its score evenly among all URLs
                    for (String linkedUrl : urlMap.keySet()) {
                        score += DAMPING_FACTOR * pageRankMap.get(linkedUrl) / numUrls;
                    }
                } else {
                    for (String linkedUrl : linkedUrls) {
                        double linkedScore = 0.0;

                        if (pageRankMap.containsKey(linkedUrl)) {
                            linkedScore = pageRankMap.get(linkedUrl);
                            int numLinks = urlMap.get(linkedUrl).size();

                            if (numLinks != 0) {
                                score += DAMPING_FACTOR * linkedScore / numLinks;
                            }
                        }
                    }
                }

                newPageRankMap.put(url, score);
            }

            // Check for convergence
            converged = true;
            for (String url : pageRankMap.keySet()) {
                if (Math.abs(pageRankMap.get(url) - newPageRankMap.get(url)) > EPSILON) {
                    converged = false;
                    break;
                }
            }

            if (converged) {
                break;
            }

            pageRankMap = newPageRankMap;
            count--;
        }

        for (String url : pageRankMap.keySet()) {
            Document filter = new Document("url", url);

            // Define the update operation
            Document update = new Document("$set", new Document("popularity", pageRankMap.get(url)));

            // Update the document
            UpdateResult result = collection.updateOne(filter, update);
        }
    }
}



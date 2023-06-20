import com.mongodb.Tag;
import com.mongodb.client.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.jsoup.nodes.Element;

import java.util.*;

public class Mongo {

    public Mongo( Map<String, Map<String, Set<Element>>> index,Map<String, Map<String,Integer>> indexwordscount,Map<String,Map<String,String>> indexStemmedword,Map<String,Integer> indextotword,Map<String,String> indexTitle,Map<String ,Double> URLsScore,Map<String ,Integer> URLstest ) {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://nouraymanh:3tAM0HS52jDjQ5wQ@cluster0.skdggct.mongodb.net");
        MongoDatabase database = mongoClient.getDatabase("search-engine-db");
        MongoCollection<Document> collection = database.getCollection("IndexerWithScoreTest1");
        MongoCollection<Document>   collectionindexer=database.getCollection("incremntal");

       // collectionindexer.createIndex(new Document("key", 1), new IndexOptions());


        Map<String, Map<String, Set<Element>>> data = index;
        Map<String,Integer> scores=new HashMap<>();
        //  Elements elements = doc.select("label, title, p, h1, h2, h3, h4, h5, h6");
        scores.put("title",9);
        scores.put("h1",8);
        scores.put("h2",7);
        scores.put("h3",6);
        scores.put("h4",5);
        scores.put("h5",4);
        scores.put("h6",3);
        scores.put("p",2);
        scores.put("label",1);



/////////////////////////////////inserting in database once///////////////////////////////////////
        Document document=null ;

        int score=0;
        for (String word :data.keySet()) {
           // System.out.println("the score of :"+word);
            boolean documentisfound=false;

            org.bson.Document filter=new org.bson.Document("key",word);
            FindIterable<org.bson.Document> worddoc=collectionindexer.find(filter);
            boolean wordExists = worddoc.iterator().hasNext();





            Map<String, Set<Element>> postingsTags = data.get(word);
            Map<String,Integer> postingscount = indexwordscount.get(word);
            Map<String,String> postingsstemmedword=indexStemmedword.get(word);


            document=new Document("key",word);

            List<Document> docs = new ArrayList<>();
            for (String docId : postingsTags.keySet()) {
                documentisfound=false;


                List<Element> tagInd = new ArrayList<>(postingsTags.get(docId));
                List<Document> tags=new ArrayList<>();
                score=0;
                for (Element tag: tagInd) {
                    tags.add(new Document("tag",tag.tagName()).append("content",tag.text()));

                    String text=tag.text();
                    text=text.replaceAll("[^\\w\\s]", "");
                    String[] words=text.split("[\\s,\\.]+");
                    String stemmedword=postingsstemmedword.get(docId);

                    for (String w:words) {
                        if (stemmedword.equalsIgnoreCase(w)) {
                            score+=scores.get(tag.tagName());
                        }
                    }
                    if(score==0){
                        score+=scores.get(tag.tagName());
                    }


                }

                int count=postingscount.get(docId);
                String stemmedword=postingsstemmedword.get(docId);
                int totwords=indextotword.get(docId);
                String title=indexTitle.get(docId);


                if (wordExists) {
                   // System.out.println("Document found!");
                    org.bson.Document doc=worddoc.first();
                    List<org.bson.Document>docstest = (List<org.bson.Document>) doc.get("docs");
                    for (int i = 0; i<docstest.size(); i++){
                        org.bson.Document insidedocs=docstest.get(i);
                        String docName=insidedocs.getString("docName");
                        if(docName.equals(docId)){
                            documentisfound=true;
                            //System.out.println(word);
                          //  System.out.println(docName +"-->"+docId);
                            break;
                        }
                    }
                    if(documentisfound) continue;
                }


                if (wordExists ){
                   // System.out.println(word+"-->"+docId+"in Exist");
                    Document filterdoc = new Document("key", word);

                    Document update = new Document("$push", new Document("docs", new Document("docName", docId).append("count", count).append("popularity",URLsScore.get(docId)).append("score", score).append("DocTitle", title).append("TotWords", totwords).append("stemmed word", stemmedword).append("tags", tags)));

                    UpdateResult result = collectionindexer.updateOne(filterdoc, update);

                }else if(!wordExists) {
                   // System.out.println(word+"-->"+docId+"in not Exist");

                    docs.add(new Document("docName", docId).append("count", count).append("popularity",URLsScore.get(docId)).append("score", score).append("DocTitle", title).append("TotWords", totwords).append("stemmed word", stemmedword).append("tags", tags));
                }

            }
           // System.out.println("I have done word --> "+word+ "with count = "+);

            if(!wordExists)
                collectionindexer.insertOne(document.append("docs",docs));
        }

        ///////////////////////////////end of inserting in data base///////////////////////////////

        mongoClient.close();
    }
}
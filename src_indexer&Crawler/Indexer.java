import com.mongodb.MongoWriteException;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.UpdateResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mongodb.client.*;

import org.jsoup.Connection;

public class Indexer {

    private static final int TIMEOUT_MS = 1000;
    private static final int MAX_RETRIES = 3;
    private Map<String, Map<String, Set<Element>>> index; // for tags   done

    private Map<String, Map<String, List<Integer>>> indexindcies;  // for index of word    done
    private Map<String, Map<String, Integer>> indexwordscount;   // for word count   done
    private Map<String, Map<String, String>> indexStemmedword;  // for word and its stemmed word    done
    private Map<String, Integer> indextotword;   /// for totwords and documentid
    private Map<String, String> indexTitle;   /// for title and documentid

    public MongoClient mongoClient;
    public MongoDatabase database;
    public MongoCollection<org.bson.Document> collection;
    public MongoCollection<org.bson.Document> collectionindexer;
    Mongo m;
    public Map<String, Double> URLsScore;
    public Map<String, Integer> URLstest = new HashMap<>();
    public ArrayList<Document> AllDocs;
    private static final ArrayList<String> s = new ArrayList<String>();
    public static Map<String, Object> wordMap = new HashMap<>();

    public Indexer() {
        long startTime = System.nanoTime();
        mongoClient = MongoClients.create("mongodb+srv://nouraymanh:3tAM0HS52jDjQ5wQ@cluster0.skdggct.mongodb.net");

        database = mongoClient.getDatabase("search-engine-db");

        collection = database.getCollection("crawler");
        collectionindexer = database.getCollection("incremntal");

        //   URLs=new ArrayList<>();
        URLsScore = new HashMap<>();

        AllDocs = new ArrayList<>();

        getURLs();

        //getDocs();
        this.index = new HashMap<>();
        this.indexwordscount = new HashMap<>();
        this.indexStemmedword = new HashMap<>();
        this.indexindcies = new HashMap<>();
        this.indextotword = new HashMap<>();
        this.indexTitle = new HashMap<>();

        System.out.println("------test-------");
//        try (BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\emadt\\Downloads\\Indexer_Crawler\\Indexer_Crawler\\src\\stopwords.txt"))) {
//            String str;
//            while ((str = in.readLine()) != null) {
//                s.add(str);
//            }
//        } catch (IOException e) {
//            System.out.println("File Read Error");
//        }

        setstopword();
        init();
        long endTime = System.nanoTime();
        long durationInNano = (endTime - startTime);
        double durationInSeconds = (double) durationInNano / 1_000_000_000.0;

        System.out.println("Duration in seconds: " + durationInSeconds);
        stemIndex();
        // printIndex();

        //System.out.println("Duration in seconds: " + durationInSeconds);

        m = new Mongo(index, indexwordscount, indexStemmedword, indextotword, indexTitle, URLsScore, URLstest);
    }

    public void setstopword() {


        List<String> wordList = Arrays.asList(
                "a", "about", "above", "actually", "after", "again", "against", "all", "almost", "also", "although", "always", "am",
                "an", "and", "any", "are", "as", "at", "be", "became", "become", "because", "been", "before", "being", "below",
                "between", "both", "but", "by", "can", "could", "did", "do", "does", "doing", "down", "during", "each", "either",
                "else", "few", "for", "from", "further", "had", "has", "have", "having", "he", "he'd", "he'll", "hence", "he's",
                "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "I", "I'd", "I'll", "I'm",
                "I've", "if", "in", "into", "is", "it", "it's", "its", "itself", "just", "let's", "may", "maybe", "me", "might",
                "mine", "more", "most", "must", "my", "myself", "neither", "nor", "not", "of", "oh", "on", "once", "only", "ok",
                "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "she", "she'd", "she'll",
                "she's", "should", "so", "some", "such", "T", "than", "that", "that's", "the", "their", "theirs", "them",
                "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this",
                "those", "through", "to", "too", "under", "until", "up", "very", "was", "we", "we'd", "we'll", "we're", "we've",
                "were", "what", "what's", "when", "whenever", "when's", "where", "whereas", "wherever", "where's", "whether",
                "which", "while", "who", "whoever", "who's", "whose", "whom", "why", "why's", "will", "with", "within", "would",
                "yes", "yet", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves");


        // Add each word to the map with a null value
        for (String word : wordList) {
            wordMap.put(word, null);
        }


    }

    public boolean checkIncrmental(String word, String docid) { //return true if docid in database
        EnglishStemmer stemmer = new EnglishStemmer();
        stemmer.setCurrent(word);
        String stemmedTerm = "";
        if (stemmer.stem()) {
            stemmedTerm = stemmer.getCurrent();
        }

        org.bson.Document filter = new org.bson.Document("key", stemmedTerm);
        FindIterable<org.bson.Document> worddoc = collectionindexer.find(filter);
        boolean documentExists = worddoc.iterator().hasNext();


        if (documentExists) {
            System.out.println("Document found!");
            org.bson.Document doc = worddoc.first();
            List<org.bson.Document> docs = (List<org.bson.Document>) doc.get("docs");
            for (int i = 0; i < docs.size(); i++) {
                org.bson.Document insidedocs = docs.get(i);
                String docName = insidedocs.getString("docName");
                if (docName.equals(docid)) {
                    return true;
                }
            }

        }
        //System.out.println("Document not found.");
        return false;

    }

    public void indexDocument(String documentId, ArrayList<String> words, Document doc) {
        int occ = 0;
        Set<Element> tags = null;
        List<Integer> indixes = null;

        for (String word : words) {

//        long startTime = System.nanoTime();
//       if(checkIncrmental(word,documentId)) {
//           return;
//       }
//        long endTime = System.nanoTime();
//        long durationInNano = (endTime - startTime);
//        double durationInSeconds = (double) durationInNano / 1_000_000_000.0;
//
//        System.out.println("Duration in seconds: " + durationInSeconds);
//


            Map<String, Set<Element>> postingsListtags = index.getOrDefault(word, new HashMap<>());
            Map<String, Integer> postingsListcount = indexwordscount.getOrDefault(word, new HashMap<>());

            occ = postingsListcount.getOrDefault(documentId, 0); // for total occurance will return 0 in first time
            occ++;
            postingsListcount.put(documentId, occ);
            indexwordscount.put(word, postingsListcount);

            if (word != null) {
                Elements elements = doc.select("label, title, p, h1, h2, h3, h4, h5, h6");
                //int indexindoc= doc.body().text().indexOf(word);

                for (Element elem : elements) {
                    String tagName = elem.tagName();
                    String text = elem.text();
                    text = text.replaceAll("[^\\w\\s]", "");
                    String[] w = text.split("[\\s,\\.]+");
                    for (String x : w) {
                        if (x.equalsIgnoreCase(word)) {
                            tags = postingsListtags.getOrDefault(documentId, new LinkedHashSet<>()); // for tags list
                            tags.add(elem);
                            postingsListtags.put(documentId, tags);  // put tags it to url
                            index.put(word, postingsListtags);
                        }
                    }
                }
            }
        }
        int totwords = words.size();
        indextotword.put(doc.baseUri(), totwords);
        String title = doc.title();
        indexTitle.put(documentId, title);
    }

    public static String removeStopWords(String text) {

        StringBuilder result = new StringBuilder();
        String[] words = text.split("\\s+");
        for (String word : words) {
            if (!wordMap.containsKey(word.toLowerCase())) {
                result.append(word).append(" ");
            }
        }
        return result.toString().trim();
    }

    public void init() {
        int i=0;

        Document doc=null;
        System.out.println(URLsScore.size());
        for (String url : URLsScore.keySet()) {
            if(i%100==0)
                System.out.println("count= "+i++);

            Connection con = Jsoup.connect(url).timeout(TIMEOUT_MS);
            // int retries = 0;
            // while (retries < MAX_RETRIES) {
            try {
                doc = con.get();
                if (con.response().statusCode() == 200) {
                    AllDocs.add(doc);

                }
            } catch (UnknownHostException e) {
                System.out.println("Could not resolve host: " + url);
                continue;
            } catch (IOException e) {
                System.out.println("IOException while fetching " + url + ": " + e.getMessage());
                continue;
                //retries++;
            }


            ArrayList<String> Words = new ArrayList<>();
            String res = Jsoup.parse(doc.toString()).body().text();
            res = res.replaceAll("[^\\w\\s]", "");
            // Converting using String.split() method with whitespace as a delimiter
            Pattern pattern = Pattern.compile("\\w+");

            Matcher match = pattern.matcher(res);

            while (match.find()) {
                String word = match.group();
                word = word.toLowerCase();
                if (!wordMap.containsKey(word.toLowerCase())) {
                    Words.add(word);
                }
            }

            indexDocument(doc.baseUri(), Words, doc);
        }
    }

    public void printIndex() {
        System.out.println("in printindex()"+indexwordscount.size());

        for (String term : indexwordscount.keySet()) {
            System.out.println(term + ":");
            Map<String, Integer> postings = indexwordscount.get(term);

            for (String docId : postings.keySet()) {

                System.out.print("  (" + docId + ": ");
                System.out.println(postings.get(docId));
                System.out.println(" )");

            }
        }

    }

    public void stemIndex() {
        Map<String, Map<String,String>> stemmedIndex = new HashMap<>();
        Map<String, Map<String, List<Integer>>> stemmedindexindcies=new HashMap<>();  // for index of word    done
        Map<String, Map<String,Integer>> stemmedindexwordscount=new HashMap<>();   // for word count   done
        Map<String, Map<String, Set<Element>>>  stemmedindex=new HashMap<>();

        EnglishStemmer stemmer = new EnglishStemmer();
        for (String term : index.keySet()) {
            // Perform stemming on the term
            stemmer.setCurrent(term);
            if (stemmer.stem()) {
                String stemmedTerm = stemmer.getCurrent();
                // Add the stemmed term to the stemmed index
                Map<String, String> postingsstemmed = indexStemmedword.getOrDefault(term, new HashMap<>());
                Map<String, List<Integer>> postingsindcies = indexindcies.getOrDefault(term, new HashMap<>());

                Map<String,Integer> postingscount = indexwordscount.getOrDefault(term, new HashMap<>());
                Map<String, Set<Element>>  postingsindex = index.getOrDefault(term, new HashMap<>());

                for (String docId : postingsindex.keySet()) {
                    postingsstemmed.put(docId,term);
                }

                stemmedIndex.put(stemmedTerm, postingsstemmed);
                stemmedindexindcies.put(stemmedTerm,postingsindcies);
                stemmedindexwordscount.put(stemmedTerm,postingscount);
                stemmedindex.put(stemmedTerm,postingsindex);

            }
        }
        // Replace the original index with the stemmed index
        indexStemmedword = stemmedIndex;
        indexwordscount = stemmedindexwordscount;
        index = stemmedindex;
        indexindcies = stemmedindexindcies;
    }

    public void getURLs() {
        MongoCursor<org.bson.Document> cursor = collection.find().iterator();
        try {
            int i=0;
            while (cursor.hasNext()) {
                org.bson.Document document = cursor.next();
                // Do something with the retrieved document
                URLsScore.put((document.getString("url")), document.getDouble("popularity"));
//                URLstest.put(document.getString("url"),i);
//                i++;
            }
        } finally {
            cursor.close();
        }
    }

    public void getDocs() {
        for (String url:URLsScore.keySet()) {
            Connection con = Jsoup.connect(url).timeout(TIMEOUT_MS);
            // int retries = 0;
            // while (retries < MAX_RETRIES) {
            try {
                Document doc = con.get();
                if (con.response().statusCode() == 200) {
                    AllDocs.add(doc);
                    break;
                }
            } catch (UnknownHostException e) {
                System.out.println("Could not resolve host: " + url);
            } catch (IOException e) {
                System.out.println("IOException while fetching " + url + ": " + e.getMessage());
                //retries++;
            }
        }
    }
    // }
}

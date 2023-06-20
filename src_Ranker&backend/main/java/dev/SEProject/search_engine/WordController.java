package dev.SEProject.search_engine;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import org.tartarus.snowball.ext.EnglishStemmer;

@SpringBootApplication
@RestController
public class WordController {

    public static List<Result> outp =new ArrayList<>();
    private static final ArrayList<String> s = new ArrayList<String>();
    @Autowired
    private WordService wordService;
    @GetMapping
    public ResponseEntity<List<Word>> getAllWords() {
        return new ResponseEntity<List<Word>>(wordService.allWords(), HttpStatus.OK);
    }
    @GetMapping("/{key}")
    public ResponseEntity<Optional<Word>> getSingleWord(@PathVariable String key) {
        return new ResponseEntity<Optional<Word>>(wordService.singleWord(key), HttpStatus.OK);
    }
    @PostMapping("/my-endpoint")
    public void handlePostRequest(@RequestBody String requestBody) {
        requestBody = requestBody.replaceAll("[+\\-\\*\\@\\!\\#\\&\\?\\=]", " ");
        requestBody=requestBody.toLowerCase();
        if (requestBody.contains(" and ") || requestBody.contains(" or ") || requestBody.contains(" not ")) {

            String[] phrases = null;
            phrases = requestBody.split("%22");
            for (int k = 0; k < phrases.length; k++) {

                String S = phrases[k];
                int indxp = S.indexOf("%");
                while (indxp != -1) {
                    S = S.substring(0, indxp) + S.substring(indxp + 3, S.length());
                    indxp = S.indexOf("%");
                }
                S = S.replaceAll("[^\\w\\s]", "");
                S = S.toLowerCase();
                phrases[k] = S;

            }

            Map<String,List<Result>> totalres =new HashMap<>();
            for (int k =1; k<phrases.length-1 ;k++) {

                if (!phrases[k].equals(" and ")&&!phrases[k].equals(" not ") &&!phrases[k].equals(" or ")) {
                    //System.out.println("S: " + phrases[k]);
                    WordService w = new WordService();
                    phrases[k] = w.removeStopWords(phrases[k]);
                    String[] phrasearr = phrases[k].split(" ");
                    String[] sendarr = phrases[k].split(" ");
                    List<WordData> words = new ArrayList<>();
                    for (int j = 0; j < sendarr.length; j++) {
                        EnglishStemmer stemmer = new EnglishStemmer();
                        stemmer.setCurrent(sendarr[j]);
                        if (stemmer.stem()) {
                            sendarr[j] = stemmer.getCurrent();
                        }
                        sendarr[j] = sendarr[j].toLowerCase();
                        ResponseEntity<Optional<Word>> data = getSingleWord(sendarr[j]);
                        WordData wd = new WordData(data.getBody().get().key);
                        for (int i = 0; i < data.getBody().get().docs.size(); i++) {
                            org.bson.Document insidedocs = data.getBody().get().docs.get(i);
                            String docName = (String) insidedocs.get("docName");
                            int count = (int) insidedocs.get("count");
                            int score = (int) insidedocs.get("score");
                            int popularity= (int) insidedocs.get("popularity");
                            int totwords = (int) insidedocs.get("TotWords");
                            String DocTitle = (String) insidedocs.get("DocTitle");
                            List<tag> tags = new ArrayList<tag>();
                            List<Document> tagss = (List<Document>) insidedocs.get("tags");
                            for (Document dd : tagss) {
                                tag t = new tag((String) dd.get("tag"), (String) dd.get("content"));
                                tags.add(t);
                            }
                            String stemmedword = (String) insidedocs.get("stemmed word");
                            DocData d = new DocData(docName, stemmedword, count, tags, DocTitle, totwords, score, popularity);
                            wd.Docs.add(d);
                        }
                        words.add(wd);
                    }
                    List<Result> R = Ranker.phraseRanking(phrasearr, words);

                    totalres.put(phrases[k], R);
                }
            }


            outp =new ArrayList<>();
            for (Result r : totalres.get(phrases[1])) {
                outp.add(r);
            }


            int index=0;
            for(String s: totalres.keySet())
            {
                System.out.println(s+" "+index);
                if(phrases[index*2].equals(" or ") &&index >0 )
                {
                    Map<String,Result> m=new HashMap<>();
                    for (Result r : outp) {
                        m.put(r.docName,r);

                    }
                    for (Result r : totalres.get(phrases[index*2+1])) {
                        m.put(r.docName,r);

                    }
                    outp=new ArrayList<>();
                    for(String o: m.keySet())
                    {
                        outp.add(m.get(o));
                    }

                }
                else if(phrases[index*2].equals(" and ") &&index >0 )
                {
                    Map<String,Result> m=new HashMap<>();
                    List<String> l2 =new ArrayList<>();
                    for(Result n: totalres.get(phrases[index*2+1]))
                    {
                        l2.add(n.docName);
                    }
                    List<Result> outp2 =new ArrayList<>();
                    for (Result r: outp)
                    {
                        for (String l: l2)
                        {
                            if(r.docName.equals(l))
                            {
                                outp2.add(r);
                            }
                        }
                    }
                    outp=new ArrayList<>();
                    outp=outp2;
                }
                else if(phrases[index*2].equals(" not ") &&index >0 )
                {
                    List<String> l2 =new ArrayList<>();
                    for(Result n: totalres.get(phrases[index*2+1]))
                    {
                        l2.add(n.docName);
                    }
                    List<Result> outp2 =new ArrayList<>();
                    for (Result r: outp)
                    {
                        for (String l: l2)
                        {
                            if(r.docName.equals(l))
                            {
                                outp2.add(r);
                            }
                        }
                    }
                    System.out.println("---------outp2----------");
                    for (Result p:outp2)
                    {
                        System.out.println(p.docName);
                    }
                    System.out.println("---------outp2----------");



                    List<Result> outp3 =new ArrayList<>();
                    List<String> l3 =new ArrayList<>();
                    for(Result n:outp2)
                    {
                        l3.add(n.docName);
                    }
                    for (Result r: outp)
                    {
                        if(!l3.contains(r.docName))
                            outp3.add(r);
                    }

                    System.out.println("---------outp3----------");
                    for (Result p:outp3)
                    {
                        System.out.println(p.docName);
                    }
                    System.out.println("---------outp3----------");
                    outp=new ArrayList<>();
                    outp=outp3;



                }

                index++;
            }
            System.out.println("**********");
            for (Result o: outp)
            {
                System.out.println(o.docName);
            }
            System.out.println("**********");




        }

        else {
            System.out.println("in else");
            int indx = requestBody.indexOf("%");
            int firstq = requestBody.indexOf("%22");
            //int nextq = requestBody.substring(firstq+3,requestBody.length()).indexOf("%22");
            //int secq =requestBody.substring(nextq+3,requestBody.length()).indexOf("%22");
            int lastq = requestBody.lastIndexOf("%22");


            String s = "";
            String[] sendarr = null;
            String[] phrasearr = null;


            if (firstq != -1 && lastq != -1) {

                WordService w = new WordService();
                s = requestBody.substring(firstq + 3, lastq);
                String S = w.removeStopWords(s);

                int indxp = S.indexOf("%");
                while (indxp != -1) {
                    S = S.substring(0, indxp) + S.substring(indxp + 3, S.length());
                    indxp = S.indexOf("%");
                }
                S = S.replaceAll("[^\\w\\s]", "");
                S = S.toLowerCase();
                //System.out.println(S);
                phrasearr = S.split(" ");
                sendarr = phrasearr;
            }
            while (indx != -1) {
                requestBody = requestBody.substring(0, indx) + requestBody.substring(indx + 3, requestBody.length());
                indx = requestBody.indexOf("%");
            }


            WordService w = new WordService();
            String res = w.removeStopWords(requestBody);
            res = res.replaceAll("[^\\w\\s]", "");
            res = res.toLowerCase();
            //System.out.println(res);

            for (char c : res.toCharArray()) {
                if (Character.isDigit(c)) {
                    firstq = 0;
                    phrasearr = res.split(" ");
                    System.out.println("in if");
                }
            }
            String[] arr = null;
            arr = res.split(" ");
            List<WordData> words = new ArrayList<>();
            sendarr = arr;
            for (int j = 0; j < sendarr.length; j++) {

                EnglishStemmer stemmer = new EnglishStemmer();
                stemmer.setCurrent(sendarr[j]);
                if (stemmer.stem()) {
                    sendarr[j] = stemmer.getCurrent();
                }
                sendarr[j] = sendarr[j].toLowerCase();

                ResponseEntity<Optional<Word>> data = getSingleWord(sendarr[j]);

                WordData wd = new WordData(data.getBody().get().key);
                for (int i = 0; i < data.getBody().get().docs.size(); i++) {
                    org.bson.Document insidedocs = data.getBody().get().docs.get(i);
                    String docName = (String) insidedocs.get("docName");
                    int count = (int) insidedocs.get("count");
                    int score = (int) insidedocs.get("score");
                    double popularity= (double) insidedocs.get("popularity");
                    int totwords = (int) insidedocs.get("TotWords");
                    String DocTitle = (String) insidedocs.get("DocTitle");
                    List<tag> tags = new ArrayList<tag>();
                    List<Document> tagss = (List<Document>) insidedocs.get("tags");
                    for (Document dd : tagss) {
                        tag t = new tag((String) dd.get("tag"), (String) dd.get("content"));
                        tags.add(t);
                    }
                    String stemmedword = (String) insidedocs.get("stemmed word");

                    DocData d = new DocData(docName, stemmedword, count, tags, DocTitle, totwords, score,popularity);
                    wd.Docs.add(d);
                }
                words.add(wd);
            }


            if (firstq == -1) {
                outp=new ArrayList<>();
                outp = Ranker.wordsRanking(arr, words);
            }
            else {
                outp = new ArrayList<>();
                outp=Ranker.phraseRanking(phrasearr, words);
            }
        }

    }
    @GetMapping("/result")
    public Optional<List<Result>> handleresult(  )
    {
        return Optional.ofNullable(outp);
    }
}

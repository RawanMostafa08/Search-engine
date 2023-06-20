package dev.SEProject.search_engine;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
@RestController
public class Ranker {

    public static List <Result> Res =new ArrayList<>();
    List<WordData> orderedDocs;

    public static List<Result> phraseRanking(String[]words , List<WordData> wordsdata) {
        Res=new ArrayList<>();

        List<DocData> resultdocs = new ArrayList<>();
        Map<String, List<DocData>> x = new HashMap<>();
        for (int k = 0; k < wordsdata.size(); k++) {
            //for each document containing the word ntf = count of the word in doc/size of doc
            //for each word idf= log(no of indexed docs/ no of docs containing the word)
            double alldocscount = 6151;
            double worddocscount = wordsdata.get(k).Docs.size();
            double idf = Math.log(alldocscount / worddocscount);
            wordsdata.get(k).idf = idf;
            for (DocData doc : wordsdata.get(k).Docs) {
                double wordocc = doc.score;
                double docsize = doc.TotWords;
                double ntf = wordocc / docsize;
                double pop=doc.popularity;
                double ntfxidf = ntf * wordsdata.get(k).idf;
                ntfxidf=0.7*ntfxidf+0.3*pop;
                doc.tfidf = ntfxidf;
                resultdocs.add(doc);
                if (x.get(doc.docName) == null) {
                    x.put(doc.docName, new ArrayList<>());
                }
                x.get(doc.docName).add(doc);
            }
        }
        List<String> keys = new ArrayList<>();
        for (String s : x.keySet()) {
            List<DocData> y = x.get(s);
            if (x.get(s).size() != wordsdata.size())
                keys.add(s);

        }
        for (String key : keys) {
            x.remove(key);
        }
        keys = new ArrayList<>();
        for (String s : x.keySet()) {
            List<DocData> y = x.get(s);
            DocData dummy = x.get(s).get(0);
            Result r=new Result();
            int indeces[]=new int[wordsdata.size()];

            int flag = 0;
            for (tag t : dummy.tags) {
                int count = 0;
                int prev = -1;
                String tag2=t.content.replaceAll("[^\\w\\s]", "");
                tag2=tag2.toLowerCase();
                String [] tagarr=tag2.split(" ");
                int k=0;
                for (String w : words) {
                    int j=0;

                    for (String ta : tagarr) {

                        if(ta.equals(w))
                        {
                            if(j>prev)
                            {
                                prev = j;

                                if(indeces[k]==0)
                                {
                                    indeces[k] = j;

                                }
                                count++;
                                break;
                            }
                        }
                        j++;
                    }
                    k++;
                }

                if (count == wordsdata.size()) {
                    flag = 1;
                    r.tags.add(t);
                    r.showntag=t.content;

                    r.docName=dummy.docName;
                    r.DocTitle=dummy.DocTitle;
                    r.Words =new ArrayList<>();
                    r.tfidf=0;
                    r.indeces=indeces;
                    for (WordData ww: wordsdata)
                        r.Words.add(ww.word);
                    for (DocData d : y)
                        r.tfidf=d.tfidf+r.tfidf;
                    break;

                }
                else
                {
                    indeces=new int[wordsdata.size()];
                }

            }
            if (flag==1)
                Res.add(r);
            if (flag == 0) {
                keys.add(s);
            }
        }
        for (String key : keys) {
            x.remove(key);
        }

        System.out.println("-------------------------");
        for (String s : x.keySet()) {
            List<DocData> y = x.get(s);
        }

        int i=0;

        for (Result r : Res)
        {
            System.out.println(r.docName+" "+r.showntag);
            for(Integer m: Res.get(i).indeces)
            {
                System.out.println(m);
            }
            i++;
        }

        Collections.sort(Res, Collections.reverseOrder());
        return Res;

    }

    public static List<Result> wordsRanking(String[] arr, List<WordData> wordsdata) {
        Res=new ArrayList<>();
        //System.out.println(wordsdata.size());
        List<DocData>  resultdocs=new ArrayList<>();
        for(int k=0 ; k<wordsdata.size();k++)
        {
            //for each document containing the word ntf = count of the word in doc/size of doc
            //for each word idf= log(no of indexed docs/ no of docs containing the word)
            double alldocscount = 6151;
            double worddocscount = wordsdata.get(k).Docs.size();
            double idf = Math.log(alldocscount / worddocscount);
            wordsdata.get(k).idf=idf;
            for (DocData doc : wordsdata.get(k).Docs) {
                double wordocc = doc.score;
                double docsize = doc.TotWords;
                double ntf = wordocc / docsize;
                double pop=doc.popularity;
                double ntfxidf=ntf * wordsdata.get(k).idf;
                ntfxidf=0.7*ntfxidf+0.3*pop;
                doc.tfidf=ntfxidf;
                resultdocs.add(doc);
            }
        }

        Map<String,DocData> distinctdocs=new HashMap<>();  //url-->doc
        for (DocData doc:resultdocs) {
            //Result r=new Result();
            if(distinctdocs.get(doc.docName)!=null&&!distinctdocs.get(doc.docName).stemmedWord.equals(doc.stemmedWord))
            {

                if(distinctdocs.get(doc.docName).tfidf<doc.tfidf) {
                    System.out.println(distinctdocs.get(doc.docName).stemmedWord);
                    distinctdocs.put(doc.docName, doc);

                }
            }
            else  if(distinctdocs.get(doc.docName)==null) {
                distinctdocs.put(doc.docName, doc);

            }
        }

        System.out.println("distinct");
        for (Map.Entry<String, DocData> entry : distinctdocs.entrySet()) {
            Result r=new Result();
            r.tags=entry.getValue().tags;
            r.docName=entry.getValue().docName;
            r.DocTitle=entry.getValue().DocTitle;
            r.tfidf=entry.getValue().tfidf;
            r.word=entry.getValue().stemmedWord;
            Res.add(r);
            System.out.println(entry.getKey() + ": " + entry.getValue().stemmedWord +" "+ entry.getValue().tfidf);
        }

        Collections.sort(Res, Collections.reverseOrder());

        for(Result r:Res)
        {
            int indeces[]=new int[1];
            String tag2=r.tags.get(0).content.replaceAll("[^\\w\\s]", "");
            tag2=tag2.toLowerCase();
            String []tagarr=tag2.split(" ");
            r.showntag=r.tags.get(0).content;
            r.Words=new ArrayList<>();
            int k=0;

            int j=0;
            for (String ta : tagarr) {

                if (ta.contains(r.word))
                    indeces[0] = j;
                j++;
            }

            k++;
            r.indeces=indeces;
        }


        int j=0;
        for (Result r : Res)
        {
            System.out.println(r.docName+" "+r.showntag);
            for(Integer m: Res.get(j).indeces)
            {
                System.out.println(m);
            }
            j++;
        }


        return Res;

    }



}






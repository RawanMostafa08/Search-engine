package dev.SEProject.search_engine;

import java.util.ArrayList;
import java.util.List;

public class DocData {
    DocData(String docname,String stemWord, int c,List<tag> t, String dt, int tw, int s,double p)
    {
        docName=docname;
        count=c;
        //url=u;
        tags=t;
        stemmedWord=stemWord;
        TotWords=tw;
        DocTitle=dt;
        score=s;
        popularity=p;


    }
    public String docName;
    public double popularity;
    public String stemmedWord;
    public int count;
    public int score;
    public String DocTitle;
    public  int TotWords;
    // public String url;
    //public double tf;
    public double tfidf;
    public List<tag> tags = new ArrayList<>();

}


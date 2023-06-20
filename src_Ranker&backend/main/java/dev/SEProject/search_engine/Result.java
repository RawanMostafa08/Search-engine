package dev.SEProject.search_engine;

import java.util.ArrayList;
import java.util.List;

public class Result implements Comparable<Result> {
    public String docName;
    public List<String> Words;
    public  String word;
    public String DocTitle;
    public double tfidf;
    public List<tag> tags = new ArrayList<>();
    public String showntag;
    public int []indeces=null;

    @Override
    public int compareTo(Result e) {
        {
            return Double.compare(this.tfidf, e.tfidf);
        }
    }
}


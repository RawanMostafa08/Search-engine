package dev.SEProject.search_engine;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
public class WordData {

    public WordData(String word)
    {
        this.word=word;
    }
//
    public double idf;
    public String word;
    public List<DocData> Docs=new ArrayList<>();
}

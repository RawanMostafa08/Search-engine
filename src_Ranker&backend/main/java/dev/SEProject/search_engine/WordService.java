package dev.SEProject.search_engine;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WordService {
    private static final ArrayList<String> s = new ArrayList<String>();
    @Autowired
    private WordRepository wordRepository;

    public WordService(){
        try (BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\LENOVO\\Desktop\\Indexer_Crawler\\src\\stopwords.txt"))) {
            String str;
            while ((str = in.readLine()) != null) {
                s.add(str);
            }
        } catch (IOException e) {
            System.out.println("File Read Error");
        }
    }
    public List<Word> allWords() {
        return wordRepository.findAll();
    }
    public Optional<Word> singleWord(String key) {
        Optional<Word> w=wordRepository.findWordByKey(key);
        return w;
    }
    public static String removeStopWords(String text) {

        StringBuilder result = new StringBuilder();
        String[] words = text.split("\\s+");
        for (String word : words) {
            if (!s.contains(word.toLowerCase())) {
                result.append(word).append(" ");
            }
        }
        return result.toString().trim();
    }
}

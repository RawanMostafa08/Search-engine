import java.util.ArrayList;
import java.util.List;

public class Word {
    // private final ArrayList<String> tagName=new ArrayList <String>();;
    public String docName;
    public String stemmedWord;
    public List<String> tags=new ArrayList<>();
    public String url;

    public int count;

    public Word(String docName, String stemmedWord,List<String> tags,int count,String url) {

        this.docName=docName;
        this.count=count;
        this.stemmedWord=stemmedWord;
        this.tags=tags;
        this.url=url;
    }


}

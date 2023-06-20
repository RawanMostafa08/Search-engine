package dev.SEProject.search_engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "incremntal")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Word {
    String key;
    List<org.bson.Document>docs = new ArrayList<>();

}


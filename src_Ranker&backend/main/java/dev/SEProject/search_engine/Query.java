package dev.SEProject.search_engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "Suggestions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Query {
    String key;
}
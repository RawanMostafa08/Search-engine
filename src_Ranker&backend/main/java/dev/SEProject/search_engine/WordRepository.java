package dev.SEProject.search_engine;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WordRepository extends MongoRepository<Word, ObjectId> {
    Optional<Word> findWordByKey(String key);
}

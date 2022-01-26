package com.example.encuentro2.repository;

import com.example.encuentro2.model.Restaurante;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RestauranteMongoRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Restaurante save(Restaurante restaurante, String collection){
        return mongoTemplate.save(restaurante, collection);
    }

    public Restaurante findById(String id){
        return mongoTemplate.findById(id, Restaurante.class);
    }

    public void findAndRemove(Query query){
        mongoTemplate.findOne(query, Restaurante.class);
    }

    public Restaurante findOne(Query query) {
        return mongoTemplate.findOne(query, Restaurante.class);
    }
}

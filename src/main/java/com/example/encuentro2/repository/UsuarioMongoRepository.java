package com.example.encuentro2.repository;

import com.example.encuentro2.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UsuarioMongoRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Usuario save(Usuario usuario, String collection){
        return mongoTemplate.save(usuario, collection);
    }

    public Usuario findById(String id){
        return mongoTemplate.findById(id, Usuario.class);
    }

    public void findAndRemove(Query query){
        mongoTemplate.findOne(query, Usuario.class);
    }

    public Usuario findOne(Query query) {
        return mongoTemplate.findOne(query, Usuario.class);
    }
}

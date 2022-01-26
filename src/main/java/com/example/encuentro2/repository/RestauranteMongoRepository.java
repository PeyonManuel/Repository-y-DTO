package com.example.encuentro2.repository;

import com.example.encuentro2.model.Restaurante;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RestauranteRepository extends MongoRepository {

    public Restaurante save(Restaurante restaurante);

    public Restaurante findById(String id);

    public void deleteById(String id);

    public Restaurante findOne(Example example)
}

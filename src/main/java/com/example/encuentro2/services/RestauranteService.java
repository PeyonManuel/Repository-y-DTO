package com.example.encuentro2.services;

import com.example.encuentro2.handle.RecursoNoExistente;
import com.example.encuentro2.model.Restaurante;
import com.example.encuentro2.repository.RestauranteMongoRepository;
import com.example.encuentro2.repository.RestauranteRedisRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;


@Service
@RequiredArgsConstructor
public class RestauranteService {

    @Autowired
    MongoTemplate mongoTemplate;

    private final ObjectMapper mapper;
    @Autowired
    private RestauranteRedisRepository restauranteRedisRepository;
    @Autowired
    private RestauranteMongoRepository restauranteMongoRepository;

    private static final Logger logger = LoggerFactory.getLogger(RestauranteService.class);

    @PostConstruct
    private void PostConstruct() {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
    }

    private String mappearAString(Restaurante restaurante) throws JsonProcessingException {
        String restauranteString = mapper.writeValueAsString(restaurante);
        logger.info("Restaurante mapeado a string: \n" + restauranteString);
        return restauranteString;
    }

    private Map<String, Restaurante> mappearAMap(String restauranteString) throws JsonProcessingException {
        Map<String, Restaurante> restauranteMap = mapper.readValue(restauranteString, Map.class);
        logger.info("Restaurante mapeado a map: \n" + restauranteMap);
        return restauranteMap;
    }

    private Restaurante mappearAClase(String restauranteString) throws JsonProcessingException {
        var restauranteClase = mapper.readValue(restauranteString, Restaurante.class);
        logger.info("Restaurante mapeado a clase: \n" + restauranteClase);
        return restauranteClase;
    }

    public Restaurante crearRestaurante(Restaurante restaurante){
        logger.info("Guardando");
        try {
            String restauranteString = mappearAString(restaurante);
            mappearAClase(restauranteString);
            mappearAMap(restauranteString);
            Restaurante restauranteNuevo =  restauranteMongoRepository.save(restaurante, "restaurantes");
            restauranteRedisRepository.save(mappearAString(restauranteNuevo), restauranteNuevo.getId());
            return restauranteNuevo;
        } catch (JsonProcessingException e) {
            logger.error("Error convirtiendo a string", e);
        }
        return restaurante;
    }

    public Map<String, Restaurante> restauranteAMap(String restaurante) throws JsonProcessingException {
        return mappearAMap(restaurante);
    }

   public  Restaurante getRestauranteById(String id) throws JsonProcessingException, RecursoNoExistente {
        try {
            logger.info("Con redis");
            return mappearAClase(restauranteRedisRepository.findById(id));
        }catch (IllegalArgumentException e){
            Restaurante restaurante = restauranteMongoRepository.findById(id);
            try{
                logger.info("Con mongo");
                restauranteRedisRepository.save(mappearAString(restaurante), restaurante.getId());
                return restaurante;
            }catch(NullPointerException e2){
                throw new RecursoNoExistente("No se ha encontrado un restaurante con ese id");
            }
        }
   }

    public  Restaurante updateRestaurante(Restaurante restaurante) throws JsonProcessingException, RecursoNoExistente {
        logger.info("Actualizando");
        try {
            String restauranteString = mappearAString(restaurante);
            logger.info(restauranteString);
            Restaurante restauranteActualizado = restauranteMongoRepository.save(restaurante, "restaurantes");
            restauranteRedisRepository.save(mappearAString(restauranteActualizado), restauranteActualizado.getId());
            return restauranteActualizado;
        } catch (JsonProcessingException e) {
            logger.error("Error convirtiendo a string", e);
        }
        return restaurante;
    }

    public void deleteByName(String nombre) throws RecursoNoExistente {
        try {
            Restaurante restaurante = restauranteMongoRepository.findOne((new Query(where("nombre").is(nombre))));
            restauranteMongoRepository.findAndRemove((new Query(where("nombre").is(restaurante.getNombre()))));
            restauranteRedisRepository.delete(restaurante.getId());
        }catch(NullPointerException n){
            throw new RecursoNoExistente("No se ha encontrado un restaurante con ese nombre");
        }
    }

}
